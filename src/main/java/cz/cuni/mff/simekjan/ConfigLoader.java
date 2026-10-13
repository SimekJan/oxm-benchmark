package cz.cuni.mff.simekjan;

import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ConfigLoader {

    public static final String GENERATOR_SECTION = "generator";
    public static final String IMPORTER_SECTION = "importer";
    public static final String MONGO_IMPORTER_SECTION = "mongo";
    public static final String NEO4J_IMPORTER_SECTION = "neo4j";
    public static final String POSTGRES_IMPORTER_SECTION = "postgres";

    /**
     * Load whole app config.
     *
     * @return Map of key-value pairs of whole app config.
     */
    public static Map<String, Object> getConfig() throws IOException {
        String configFile = System.getenv().getOrDefault(
            "CONFIG_FILE",
            "oxm_generator.yaml"
        );

        Yaml yaml = new Yaml();

        try (InputStream input = Files.newInputStream(Path.of(configFile))) {
            return yaml.load(input);
        }
    }

    /**
     * Loads nested config based on given structured path.
     *
     * @param path List of in order nested subsections of config yaml.
     * @return Map of key-value pairs from requested config subsection.
     */
    public static Map<String, Object> getSection(String... path) throws IOException {
        Map<String, Object> current = getConfig();

        for (String key : path) {
            Object value = current.get(key);

            if (value == null) {
                throw new IllegalArgumentException(
                    "Missing configuration section: " + String.join(".", path)
                );
            }

            if (!(value instanceof Map<?, ?>)) {
                throw new IllegalArgumentException(
                    "Configuration section '" + key + "' must be a map"
                );
            }

            current = (Map<String, Object>) value;
        }

        return current;
    }

    private static final String OUTPUT_DIR = "data";
    private static final String CSV_DIR = "csv";
    private static final String JSON_DIR = "json";

    private static final Path defaultOutputDir = Path.of(OUTPUT_DIR);

    public static Path getDataDir() throws IOException {
        Map<String, Object> config = ConfigLoader.getSection(ConfigLoader.GENERATOR_SECTION);

        String configOutputDir = (String) config.get("outputDir");
        return (configOutputDir != null) ?
            Path.of(configOutputDir) : defaultOutputDir;
    }

    /**
     * Returns chosen path for saved CSV data according to config.
     *
     * @return CSV data Path.
     */
    public static Path getCsvDir() throws IOException {
        Path csvDir = defaultOutputDir.resolve(CSV_DIR);

        Map<String, Object> config = ConfigLoader.getSection(ConfigLoader.GENERATOR_SECTION);

        String configOutputDir = (String) config.get("outputDir");
        if (configOutputDir != null) {
            Path outputDir = Path.of(configOutputDir);
            csvDir = outputDir.resolve(CSV_DIR);
        }

        return csvDir;
    }

    /**
     * Returns chosen path for saved JSON data according to config.
     *
     * @return JSON data Path.
     */
    public static Path getJsonDir() throws IOException {
        Path jsonDir = defaultOutputDir.resolve(JSON_DIR);

        Map<String, Object> config = ConfigLoader.getSection(ConfigLoader.GENERATOR_SECTION);

        String configOutputDir = (String) config.get("outputDir");
        if (configOutputDir != null) {
            Path outputDir = Path.of(configOutputDir);
            jsonDir = outputDir.resolve(JSON_DIR);
        }

        return jsonDir;
    }
}
