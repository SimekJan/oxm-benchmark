package cz.cuni.mff.simekjan.runner.backend.mongodb;

import cz.cuni.mff.simekjan.runner.spi.Backend;
import cz.cuni.mff.simekjan.runner.spi.ContextFactory;

import java.util.Map;

/**
 * Spring Data MongoDB backend implementation.
 */
public class MongoBackend implements Backend {

    /**
     * {@inheritDoc}
     */
    @Override
    public String id() {
        return "springdata-mongodb";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextFactory createContextFactory(Map<String, String> connection) {
        throw new UnsupportedOperationException("MongoDB backend is not implemented");
    }
}
