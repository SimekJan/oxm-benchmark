package cz.cuni.mff.simekjan.runner.config;

import java.nio.file.Path;

/**
 * Loads runner settings from the shared configuration file.
 */
public final class RunnerConfigLoader {

    /**
     * Loads runner settings.
     *
     * @param configFile path to the configuration file
     * @return runner configuration
     * @throws UnsupportedOperationException until configuration loading is implemented
     */
    public RunnerConfig load(Path configFile) {
        throw new UnsupportedOperationException("Runner configuration loading is not implemented");
    }
}
