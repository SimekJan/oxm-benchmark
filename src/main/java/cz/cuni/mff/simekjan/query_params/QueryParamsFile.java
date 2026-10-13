package cz.cuni.mff.simekjan.query_params;

import cz.cuni.mff.simekjan.ConfigLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Handles writing and reading of chosen parameters
 * to use in queries into yaml file in dataDir
 */
public class QueryParamsFile {

    public static final String RUN_CONFIG_FILENAME = "query-params.yaml";

    private final Yaml yaml;

    public QueryParamsFile() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        options.setPrettyFlow(true);

        this.yaml = new Yaml(options);
    }

    public void write(QueryParams config) throws IOException {

        Path dataDir = ConfigLoader.getDataDir();
        Path path = dataDir.resolve(RUN_CONFIG_FILENAME);

        try (Writer writer = Files.newBufferedWriter(path)) {
            yaml.dump(config, writer);
        }
    }

    public QueryParams read() throws IOException {

        Path dataDir = ConfigLoader.getDataDir();
        Path path = dataDir.resolve(RUN_CONFIG_FILENAME);

        try (InputStream input = Files.newInputStream(path)) {
            return yaml.loadAs(input, QueryParams.class);
        }
    }
}
