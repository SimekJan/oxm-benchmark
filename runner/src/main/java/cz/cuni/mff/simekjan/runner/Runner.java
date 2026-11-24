package cz.cuni.mff.simekjan.runner;

import cz.cuni.mff.simekjan.runner.execution.BenchmarkExecutor;

import java.nio.file.Path;

/**
 * Entrypoint for the standalone runner.
 */
public final class Runner {

    private Runner() {
    }

    /**
     * Starts the benchmark using the classes compiled into the runner.
     *
     * @param args optional configuration path
     */
    static void main(String[] args) {

        if (args.length > 1) {
            System.err.println("Usage: java -jar oxm-benchmark-runner.jar [oxm_config.yaml]");
            System.exit(1);
        }

        Path configFile = args.length == 1
            ? Path.of(args[0])
            : Path.of(System.getenv().getOrDefault("CONFIG_FILE", "oxm_config.yaml"));

        if (!new BenchmarkExecutor().execute(configFile)) {
            System.exit(1);
        }
    }
}
