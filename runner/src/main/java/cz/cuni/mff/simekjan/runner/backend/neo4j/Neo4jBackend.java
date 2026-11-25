package cz.cuni.mff.simekjan.runner.backend.neo4j;

import cz.cuni.mff.simekjan.runner.spi.Backend;
import cz.cuni.mff.simekjan.runner.spi.ContextFactory;

import java.util.Map;

/**
 * Spring Data Neo4j backend implementation.
 */
public class Neo4jBackend implements Backend {

    /**
     * {@inheritDoc}
     */
    @Override
    public String id() {
        return "springdata-neo4j";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextFactory createContextFactory(Map<String, String> connection) {
        throw new UnsupportedOperationException("Neo4j backend is not implemented");
    }
}
