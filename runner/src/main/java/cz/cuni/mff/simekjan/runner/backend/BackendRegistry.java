package cz.cuni.mff.simekjan.runner.backend;

import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateBackend;
import cz.cuni.mff.simekjan.runner.backend.mongodb.MongoBackend;
import cz.cuni.mff.simekjan.runner.backend.neo4j.Neo4jBackend;
import cz.cuni.mff.simekjan.runner.spi.Backend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Resolves the backend for a configured database entry.
 */
public final class BackendRegistry {

    private final Map<String, Backend> backends;

    public BackendRegistry() {
        Map<String, Backend> byId = new HashMap<>();
        List<Backend> backendList = List.of(new HibernateBackend(), new MongoBackend(), new Neo4jBackend());
        for (Backend backend : backendList) {
            if (byId.putIfAbsent(backend.id(), backend) != null) {
                throw new IllegalArgumentException("Duplicate backend: " + backend.id());
            }
        }
        backends = Map.copyOf(byId);
    }

    public Backend get(String backendId) {
        Backend backend = backends.get(backendId);
        if (backend == null) {
            throw new IllegalArgumentException("Unknown backend: " + backendId);
        }
        return backend;
    }
}
