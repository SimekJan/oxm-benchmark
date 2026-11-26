package cz.cuni.mff.simekjan.runner.spi;

import java.util.List;
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
     * @param connection resolved backend connection settings
     * @param mappedClasses classes discovered in the submission for this backend
     * @return context factory
     */
    ContextFactory createContextFactory(Map<String, String> connection, List<Class<?>> mappedClasses);
}
