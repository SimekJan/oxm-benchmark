package cz.cuni.mff.simekjan.runner.spi;

import java.util.Map;

/**
 * Database backend bundled with the runner.
 */
public interface Backend {

    /**
     * @return backend identifier used in configuration
     */
    String id();

    /**
     * Creates a factory for query contexts.
     *
     * @param options resolved backend connection and configuration options
     * @return context factory
     */
    ContextFactory createContextFactory(Map<String, Object> options);
}
