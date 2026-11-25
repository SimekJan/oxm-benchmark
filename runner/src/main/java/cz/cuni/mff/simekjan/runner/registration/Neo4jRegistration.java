package cz.cuni.mff.simekjan.runner.registration;

import cz.cuni.mff.simekjan.runner.backend.neo4j.Neo4jQuery;

import java.util.List;

/**
 * Placeholder for Spring Data Neo4j submissions.
 */
public final class Neo4jRegistration implements QueryRegistration {

    @Override
    public String backendId() {
        return "springdata-neo4j";
    }

    @Override
    public List<Class<? extends Neo4jQuery<?>>> queries() {
        throw new UnsupportedOperationException("Spring Data Neo4j query registration is not implemented");
    }
}
