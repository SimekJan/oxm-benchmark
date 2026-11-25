package cz.cuni.mff.simekjan.runner.registration;

import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import jakarta.persistence.Entity;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Modifier;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Compiled submission and its class loader, owned by a benchmark launch.
 */
public final class HibernateSubmission implements AutoCloseable {

    private final Path output;
    private final URLClassLoader loader;
    private final List<Class<?>> queryClasses;
    private final List<Class<?>> entities;
    private final List<Failure> failures;

    private HibernateSubmission(Path output, URLClassLoader loader, List<Class<?>> queryClasses,
                                List<Class<?>> entities, List<Failure> failures) {
        this.output = output;
        this.loader = loader;
        this.queryClasses = List.copyOf(queryClasses);
        this.entities = List.copyOf(entities);
        this.failures = List.copyOf(failures);
    }

    public static HibernateSubmission load(Path sourceDir) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("A JDK with the Java compiler is required to compile submissions");
        }
        if (!Files.isDirectory(sourceDir)) {
            throw new IllegalArgumentException("Submission source directory does not exist: " + sourceDir);
        }
        try {
            List<Path> sources;
            try (var paths = Files.walk(sourceDir)) {
                sources = paths.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .sorted().toList();
            }
            if (sources.isEmpty()) {
                throw new IllegalArgumentException("No Java sources in submission: " + sourceDir);
            }
            for (Path source : sources) {
                Path relative = sourceDir.relativize(source);
                if (relative.getNameCount() < 2 || !relative.getName(0).toString().equals("queries")) {
                    throw new IllegalArgumentException("Submission source must be under queries/: " + source);
                }
            }
            Path output = Files.createTempDirectory("oxm-hibernate-submission-");
            try {
                compile(compiler, sources, output);
                URLClassLoader loader = new URLClassLoader(
                    new java.net.URL[]{output.toUri().toURL()}, HibernateQuery.class.getClassLoader());
                try {
                    return discover(sourceDir, sources, output, loader);
                } catch (Throwable failure) {
                    try {
                        loader.close();
                    } catch (IOException cleanup) {
                        failure.addSuppressed(cleanup);
                    }
                    throw failure;
                }
            } catch (Throwable failure) {
                try {
                    deleteOutput(output);
                } catch (IOException cleanup) {
                    failure.addSuppressed(cleanup);
                }
                throw failure;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot compile submission from " + sourceDir, e);
        }
    }

    private static void compile(JavaCompiler compiler, List<Path> sources, Path output) throws IOException {
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        String classpath = System.getProperty("java.class.path");
        if (classpath == null || classpath.isBlank()) {
            throw new IllegalStateException("The application Java classpath is empty");
        }
        try (var files = compiler.getStandardFileManager(diagnostics, Locale.ROOT, StandardCharsets.UTF_8)) {
            var units = files.getJavaFileObjectsFromPaths(sources);
            List<String> options = List.of("-classpath", classpath, "-processorpath", classpath,
                "-processor", "lombok.launch.AnnotationProcessorHider$AnnotationProcessor",
                "-d", output.toString(), "-encoding", "UTF-8");
            boolean compiled = Boolean.TRUE.equals(compiler.getTask(null, files, diagnostics, options, null, units).call());
            if (!compiled) {
                StringBuilder message = new StringBuilder("Submission compilation failed:");
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    message.append(System.lineSeparator()).append(formatDiagnostic(diagnostic));
                }
                throw new IllegalArgumentException(message.toString());
            }
            diagnostics.getDiagnostics().forEach(diagnostic -> System.err.println(formatDiagnostic(diagnostic)));
        }
    }

    private static String formatDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
        return (diagnostic.getSource() == null ? "<compiler>" : diagnostic.getSource().getName())
            + ":" + diagnostic.getLineNumber() + ": " + diagnostic.getKind() + ": "
            + diagnostic.getMessage(Locale.ROOT);
    }

    private static HibernateSubmission discover(Path sourceDir, List<Path> sources, Path output,
                                                URLClassLoader loader) {
        List<Class<?>> queryClasses = new ArrayList<>();
        List<Class<?>> entities = new ArrayList<>();
        List<Failure> failures = new ArrayList<>();
        for (Path source : sources) {
            String relative = sourceDir.relativize(source).toString();
            String className = relative.substring(0, relative.length() - ".java".length())
                .replace(source.getFileSystem().getSeparator(), ".");
            if (className.endsWith(".package-info")) {
                continue;
            }
            try {
                Class<?> candidate = Class.forName(className, false, loader);
                if (candidate.getClassLoader() != loader) {
                    throw new IllegalStateException("Submission class shadows an application class: " + className);
                }
                if (candidate.isAnnotationPresent(Entity.class)) {
                    entities.add(candidate);
                }
                if (HibernateQuery.class.isAssignableFrom(candidate)
                    && !candidate.isInterface() && !Modifier.isAbstract(candidate.getModifiers())) {
                    queryClasses.add(candidate);
                }
            } catch (Throwable error) {
                failures.add(new Failure(className, error));
            }
        }
        return new HibernateSubmission(output, loader, queryClasses, entities, failures);
    }

    public Batch newBatch() {
        List<HibernateQuery<?>> queries = new ArrayList<>();
        List<Failure> batchFailures = new ArrayList<>(failures);
        for (Class<?> candidate : queryClasses) {
            try {
                queries.add(HibernateQuery.class.cast(candidate.getConstructor().newInstance()));
            } catch (Throwable error) {
                batchFailures.add(new Failure(candidate.getName(), error));
            }
        }
        return new Batch(List.copyOf(queries), List.copyOf(batchFailures));
    }

    public List<Class<?>> entities() {
        return entities;
    }

    @Override
    public void close() {
        IOException error = null;
        try {
            loader.close();
        } catch (IOException e) {
            error = e;
        }
        try {
            deleteOutput(output);
        } catch (IOException e) {
            if (error == null) {
                error = e;
            } else {
                error.addSuppressed(e);
            }
        }
        if (error != null) {
            throw new UncheckedIOException("Cannot clean up Hibernate submission", error);
        }
    }

    private static void deleteOutput(Path output) throws IOException {
        try (var paths = Files.walk(output)) {
            for (Path path : paths.sorted(Comparator.reverseOrder()).toList()) {
                Files.delete(path);
            }
        }
    }

    public record Failure(String className, Throwable cause) {
    }

    public record Batch(List<HibernateQuery<?>> queries, List<Failure> failures) {
    }
}
