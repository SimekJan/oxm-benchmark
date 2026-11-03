package cz.cuni.mff.simekjan.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.simekjan.model.CreateDatasetFromSchemaCommand;
import cz.cuni.mff.simekjan.model.DatasetRuntimeStatus;
import cz.cuni.mff.simekjan.model.DatasetSchema;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Service
public class DatasetBuildService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasetBuildService.class);
    private static final int STARTUP_TIMEOUT_SECONDS = 30;

    private final ObjectMapper objectMapper;
    private final DatasetSourceGenerator sourceGenerator = new DatasetSourceGenerator();
    private final Path dataDirectory;
    private final Executor buildExecutor;
    private final Map<UUID, DatasetRuntimeStatus> statuses = new ConcurrentHashMap<>();
    private final Map<UUID, Process> processes = new ConcurrentHashMap<>();

    public DatasetBuildService(
        ObjectMapper objectMapper,
        @Value("${executor.data-dir:./data/executor}") String dataDirectory,
        @Qualifier("datasetBuildExecutor") Executor buildExecutor
    ) {
        this.objectMapper = objectMapper;
        this.dataDirectory = Path.of(dataDirectory).toAbsolutePath().normalize();
        this.buildExecutor = buildExecutor;
    }

    @PostConstruct
    public void recoverDatasets() {
        try {
            Files.createDirectories(dataDirectory);
            try (var directories = Files.list(dataDirectory)) {
                directories.filter(Files::isDirectory).forEach(this::recoverDataset);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Could not recover executor datasets", exception);
        }
    }

    public DatasetRuntimeStatus submit(UUID datasetId, CreateDatasetFromSchemaCommand command) {
        if (command == null || command.getSchema() == null) {
            throw new IllegalArgumentException("Dataset schema is required");
        }
        DatasetSourceGenerator.validateSchema(command.getSchema());
        Path directory = datasetDirectory(datasetId);
        if (Files.exists(directory.resolve("request.json"))) {
            DatasetRuntimeStatus existing = getStatus(datasetId);
            if (existing != null) {
                return existing;
            }
            throw new IllegalStateException("Dataset runtime already exists without a readable status");
        }

        try {
            Files.createDirectories(directory);
            objectMapper.writeValue(directory.resolve("request.json").toFile(), command);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not persist dataset build request", exception);
        }

        DatasetRuntimeStatus status = newStatus(datasetId, "BUILDING", "Build queued");
        saveStatus(status);
        buildExecutor.execute(() -> buildAndStart(datasetId, command));
        return status;
    }

    public DatasetRuntimeStatus getStatus(UUID datasetId) {
        DatasetRuntimeStatus status = statuses.get(datasetId);
        if (status == null) {
            Path statusPath = datasetDirectory(datasetId).resolve("status.json");
            if (!Files.isRegularFile(statusPath)) {
                return null;
            }
            try {
                status = objectMapper.readValue(statusPath.toFile(), DatasetRuntimeStatus.class);
                statuses.put(datasetId, status);
            } catch (IOException exception) {
                throw new IllegalStateException("Could not read dataset runtime status", exception);
            }
        }

        Process process = processes.get(datasetId);
        if (status.getStatus() == DatasetRuntimeStatus.StatusEnum.READY && process != null && !process.isAlive()) {
            status = newStatus(datasetId, "FAILED", "Generated application process exited");
            saveStatus(status);
        }
        return status;
    }

    @PreDestroy
    public void stopDatasetProcesses() {
        processes.values().forEach(process -> {
            process.destroy();
            try {
                if (!process.waitFor(5, TimeUnit.SECONDS)) {
                    process.destroyForcibly();
                }
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                process.destroyForcibly();
            }
        });
    }

    private void recoverDataset(Path directory) {
        Path requestPath = directory.resolve("request.json");
        if (!Files.isRegularFile(requestPath)) {
            return;
        }
        try {
            UUID datasetId = UUID.fromString(directory.getFileName().toString());
            CreateDatasetFromSchemaCommand command =
                objectMapper.readValue(requestPath.toFile(), CreateDatasetFromSchemaCommand.class);
            DatasetRuntimeStatus status = newStatus(datasetId, "BUILDING", "Restarting generated application");
            saveStatus(status);
            buildExecutor.execute(() -> buildAndStart(datasetId, command));
        } catch (IllegalArgumentException | IOException exception) {
            LOGGER.error("Could not restore executor dataset from {}", requestPath, exception);
        }
    }

    private void buildAndStart(UUID datasetId, CreateDatasetFromSchemaCommand command) {
        Path datasetDirectory = datasetDirectory(datasetId);
        try {
            Path sourceDirectory = datasetDirectory.resolve("generated-sources");
            Path classesDirectory = datasetDirectory.resolve("classes");
            Files.createDirectories(sourceDirectory);
            clearDirectory(classesDirectory);
            Files.createDirectories(classesDirectory);

            List<String> generatedClasses =
                sourceGenerator.generate(datasetId, command.getSchema(), sourceDirectory);
            compile(sourceDirectory, classesDirectory);
            Path schemaPath = datasetDirectory.resolve("schema.json");
            objectMapper.writeValue(schemaPath.toFile(), command.getSchema());
            int port = reservePort();
            Path logPath = datasetDirectory.resolve("runtime.log");
            String classpath = System.getProperty("java.class.path")
                + java.io.File.pathSeparator + classesDirectory;
            Path javaPath = Path.of(System.getProperty("java.home"), "bin", "java");
            Process process = new ProcessBuilder(
                javaPath.toString(),
                "-cp", classpath,
                DatasetRuntimeMain.class.getName(),
                "--port", Integer.toString(port),
                "--schema", schemaPath.toString(),
                "--classes", String.join(",", generatedClasses)
            ).redirectErrorStream(true)
                .redirectOutput(logPath.toFile())
                .start();
            processes.put(datasetId, process);

            waitForReady(process, port, logPath);
            DatasetRuntimeStatus status = newStatus(datasetId, "READY", "Generated application is ready");
            status.setMessage(status.getMessage() + " (port " + port + ")");
            saveStatus(status);
        } catch (IOException | RuntimeException exception) {
            LOGGER.error("Failed to build or start generated application for dataset {}", datasetId, exception);
            Process process = processes.remove(datasetId);
            if (process != null) {
                process.destroyForcibly();
            }
            saveStatus(newStatus(datasetId, "FAILED", exception.getMessage()));
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            Process process = processes.remove(datasetId);
            if (process != null) {
                process.destroyForcibly();
            }
            saveStatus(newStatus(datasetId, "FAILED", "Build or startup was interrupted"));
        }
    }

    private static void compile(Path sourceDirectory, Path classesDirectory) throws IOException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("A JDK is required to compile generated dataset classes");
        }

        List<Path> sourcePaths;
        try (var paths = Files.walk(sourceDirectory)) {
            sourcePaths = paths.filter(path -> path.toString().endsWith(".java")).toList();
        }
        if (sourcePaths.isEmpty()) {
            throw new IllegalStateException("No Java entity classes were generated");
        }

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(
            diagnostics, null, StandardCharsets.UTF_8
        )) {
            Iterable<? extends JavaFileObject> sourceFiles =
                fileManager.getJavaFileObjectsFromPaths(sourcePaths);
            List<String> options = List.of(
                "-classpath", System.getProperty("java.class.path"),
                "-d", classesDirectory.toString()
            );
            boolean compiled = compiler.getTask(null, fileManager, diagnostics, options, null, sourceFiles).call();
            if (!compiled) {
                String errors = diagnostics.getDiagnostics().stream()
                    .filter(diagnostic -> diagnostic.getKind() == Diagnostic.Kind.ERROR)
                    .map(DatasetBuildService::formatDiagnostic)
                    .reduce((first, second) -> first + System.lineSeparator() + second)
                    .orElse("Unknown Java compiler error");
                throw new IllegalArgumentException("Generated entity compilation failed: " + errors);
            }
        }
    }

    private static String formatDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
        return diagnostic.getSource() + ":" + diagnostic.getLineNumber() + ": " + diagnostic.getMessage(null);
    }

    private static int reservePort() throws IOException {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private static void waitForReady(Process process, int port, Path logPath) throws IOException, InterruptedException {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(STARTUP_TIMEOUT_SECONDS);
        URI healthUri = URI.create("http://127.0.0.1:" + port + "/health");
        while (System.nanoTime() < deadline) {
            if (!process.isAlive()) {
                throw new IllegalStateException("Generated application exited; see " + logPath);
            }
            try {
                HttpURLConnection connection = (HttpURLConnection) healthUri.toURL().openConnection();
                connection.setConnectTimeout(500);
                connection.setReadTimeout(500);
                if (connection.getResponseCode() == 200) {
                    connection.disconnect();
                    return;
                }
                connection.disconnect();
            } catch (IOException ignored) {
                Thread.sleep(100);
            }
        }
        throw new IllegalStateException("Generated application did not become ready; see " + logPath);
    }

    private Path datasetDirectory(UUID datasetId) {
        Path directory = dataDirectory.resolve(datasetId.toString()).normalize();
        if (!directory.startsWith(dataDirectory)) {
            throw new IllegalArgumentException("Invalid dataset identifier");
        }
        return directory;
    }

    private void saveStatus(DatasetRuntimeStatus status) {
        statuses.put(status.getDatasetId(), status);
        try {
            Path directory = datasetDirectory(status.getDatasetId());
            Files.createDirectories(directory);
            objectMapper.writeValue(directory.resolve("status.json").toFile(), status);
        } catch (IOException exception) {
            LOGGER.error("Could not persist runtime status for dataset {}", status.getDatasetId(), exception);
        }
    }

    private static DatasetRuntimeStatus newStatus(UUID datasetId, String status, String message) {
        DatasetRuntimeStatus result = new DatasetRuntimeStatus();
        result.setDatasetId(datasetId);
        result.setStatus(DatasetRuntimeStatus.StatusEnum.fromValue(status));
        result.setMessage(message == null ? "Unspecified executor failure" : message);
        return result;
    }

    private static void clearDirectory(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return;
        }
        try (var paths = Files.walk(directory)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }
}
