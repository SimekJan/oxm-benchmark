package cz.cuni.mff.simekjan.runner.config;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Loads the standalone runner configuration.
 */
public final class RunnerConfigLoader {
    
    private static final Pattern ENVIRONMENT_VARIABLE = Pattern.compile("\\$\\{([A-Za-z_][A-Za-z0-9_]*)}");
    private static final String DEFAULT_SOURCE_DIR = "runner/example-submission/src/main/java";
    private final ObjectMapper mapper = YAMLMapper.builder(
            YAMLFactory.builder().enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build())
        .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
        .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
        .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
        .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
        .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
        .build();
    private final Map<String, String> environment;

    public RunnerConfigLoader() {
        this(System.getenv());
    }

    RunnerConfigLoader(Map<String, String> environment) {
        this.environment = Map.copyOf(environment);
    }

    /**
     * Reads and validates the runner settings without connecting to a database.
     *
     * @param configFile configuration file path
     * @return runner configuration
     * @throws IllegalArgumentException if the configuration cannot be read or is invalid
     */
    public RunnerConfig load(Path configFile) {
        ConfigFile file;
        try (var input = Files.newInputStream(configFile)) {
            JsonNode root = mapper.readTree(input);
            if (root != null && root.path("runner") instanceof ObjectNode runner && !runner.has("sourceDir")) {
                runner.put("sourceDir", DEFAULT_SOURCE_DIR);
            }
            file = mapper.treeToValue(root, ConfigFile.class);
        } catch (com.fasterxml.jackson.core.JsonProcessingException e) {
            // Jackson's error messages can include connection values and credentials.
            throw new IllegalArgumentException("Invalid runner configuration: " + configFile);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot read runner configuration: " + configFile, e);
        }
        if (file == null || file.runner() == null) {
            throw new IllegalArgumentException("Runner configuration is missing");
        }
        RunnerConfig config = file.runner();
        if (config.numberOfRuns() <= 0 || config.timeoutMs() <= 0) {
            throw new IllegalArgumentException("numberOfRuns and timeoutMs must be positive");
        }
        if (config.paramsFile().toString().isBlank() || config.sourceDir().toString().isBlank()
            || config.outputDir().toString().isBlank()) {
            throw new IllegalArgumentException("paramsFile, sourceDir and outputDir must not be blank");
        }
        if (config.databases().isEmpty()) {
            throw new IllegalArgumentException("runner.databases must not be empty");
        }
        if (config.queries().stream().anyMatch(id -> id == null || id.isBlank())
            || new HashSet<>(config.queries()).size() != config.queries().size()) {
            throw new IllegalArgumentException("runner.queries must contain unique, non-empty IDs");
        }

        var ids = new HashSet<String>();
        List<RunnerConfig.Database> databases = config.databases().stream().map(database -> {
            if (database == null || database.id().isBlank() || database.database().isBlank()
                || database.backend().isBlank()) {
                throw new IllegalArgumentException("Database entries must have a non-empty id, database and backend");
            }
            if (!ids.add(database.id())) {
                throw new IllegalArgumentException("Duplicate runner database entry id: " + database.id());
            }
            Map<String, String> connection = new LinkedHashMap<>();
            database.connection().forEach((key, value) -> {
                if (key.isBlank() || value == null || value.isBlank()) {
                    throw new IllegalArgumentException("Connection fields must have non-empty names and values");
                }
                String resolved = expandEnvironment(value);
                if (resolved.isBlank()) {
                    throw new IllegalArgumentException("Connection field " + key + " must not be blank");
                }
                connection.put(key, resolved);
            });
            if (!connection.containsKey("connection") || connection.get("connection").isBlank()) {
                throw new IllegalArgumentException("Database entry " + database.id() + " needs a connection address");
            }
            return new RunnerConfig.Database(database.id(), database.database(), database.backend(),
                Map.copyOf(connection));
        }).toList();

        return new RunnerConfig(config.numberOfRuns(), config.warmup(), List.copyOf(config.queries()),
            config.paramsFile(), config.sourceDir(), config.timeoutMs(), config.outputDir(), databases);
    }

    private String expandEnvironment(String value) {
        Matcher matcher = ENVIRONMENT_VARIABLE.matcher(value);
        StringBuilder result = new StringBuilder();
        int previousEnd = 0;
        while (matcher.find()) {
            if (value.substring(previousEnd, matcher.start()).contains("${")) {
                throw new IllegalArgumentException("Invalid environment variable reference in connection");
            }
            String variable = matcher.group(1);
            String replacement = environment.get(variable);
            if (replacement == null) {
                throw new IllegalArgumentException("Missing environment variable: " + variable);
            }
            matcher.appendReplacement(result, Matcher.quoteReplacement(replacement));
            previousEnd = matcher.end();
        }
        if (value.substring(previousEnd).contains("${")) {
            throw new IllegalArgumentException("Invalid environment variable reference in connection");
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private record ConfigFile(RunnerConfig runner) {
    }
}
