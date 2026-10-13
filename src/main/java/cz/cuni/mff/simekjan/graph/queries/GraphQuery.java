package cz.cuni.mff.simekjan.graph.queries;

import cz.cuni.mff.simekjan.graph.Neo4jSessionManager;
import cz.cuni.mff.simekjan.runner.Query;
import org.neo4j.ogm.session.Session;

public interface GraphQuery<P> extends Query<P> {

    Session session = Neo4jSessionManager.getSession();
}
