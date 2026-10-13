package cz.cuni.mff.simekjan.graph;

import cz.cuni.mff.simekjan.ConfigLoader;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.ogm.config.Configuration;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;

import java.io.IOException;
import java.util.Map;

/**
 * Provides connection to chosen Neo4j instance from config
 * to all parts of program
 */
@Slf4j
public class Neo4jSessionManager {

    private static final String DEFAULT_NEO4J_CONNECTION = "bolt://localhost:7687";
    private static final String DEFAULT_NEO4J_USERNAME = "neo4j";
    private static final String DEFAULT_NEO4J_PASSWORD = "oxm_password";
    private static final String DOMAIN_PACKAGE = "cs.mff.uk.simek.graph.northwind";

    private static Session session;

    public static Session getSession() {

        if (session != null) {
            return session;
        }

        String connection = DEFAULT_NEO4J_CONNECTION;
        String username = DEFAULT_NEO4J_USERNAME;
        String password = DEFAULT_NEO4J_PASSWORD;

        try {
            Map<String, Object> config = ConfigLoader.getSection(
                ConfigLoader.IMPORTER_SECTION,
                ConfigLoader.NEO4J_IMPORTER_SECTION);

            connection = (String) config.get("connection");
            username = (String) config.get("username");
            password = (String) config.get("password");
        } catch (IOException e) {
            log.info("Cannot load Neo4j config, using default connection and authentication.");
        }

        Configuration configuration = new Configuration.Builder()
            .uri(connection)
            .credentials(username, password)
            .build();

        SessionFactory sessionFactory = new SessionFactory(configuration, DOMAIN_PACKAGE);
        session = sessionFactory.openSession();

        return session;
    }
}