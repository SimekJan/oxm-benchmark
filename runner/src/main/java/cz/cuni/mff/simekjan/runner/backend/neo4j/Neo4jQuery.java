package cz.cuni.mff.simekjan.runner.backend.neo4j;

import cz.cuni.mff.simekjan.runner.api.Query;
import org.springframework.data.neo4j.core.Neo4jClient;

/**
 * Query using a Spring Data Neo4j client.
 *
 * @param <P> query parameter type
 */
public interface Neo4jQuery<P> extends Query<Neo4jClient, P> {

    @Override
    default Class<Neo4jClient> contextType() {
        return Neo4jClient.class;
    }
}
