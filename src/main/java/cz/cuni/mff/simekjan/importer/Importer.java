package cz.cuni.mff.simekjan.importer;

import cz.cuni.mff.simekjan.ConfigLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Main entry point for importing all data from CSV/JSON files to databases
 */
public class Importer {

    /**
     * Imports data from generated files to DBs
     *
     * @throws IOException Cannot read a file
     */
    static void main() throws IOException {

        Map<String, Object> config = ConfigLoader.getConfig();

        List<String> dbsUsed = ((List<?>) config.get("databaseToUse"))
            .stream()
            .map(String.class::cast)
            .toList();

        System.out.println("Importing to:");
        System.out.println(dbsUsed);

        if (dbsUsed.contains("mongo")) {
            MongoImporter.run();
            System.out.println("Data imported to MongoDB.");
        }

        if (dbsUsed.contains("mongoEmbedded")) {
            EmbeddedMongoImporter.run();
            System.out.print("Data imported to MongoDB in embedded schema.");
        }

        if (dbsUsed.contains("neo4j")) {
            Neo4jImporter.run();
            System.out.println("Data imported to Neo4j.");
        }

        if (dbsUsed.contains("postgres")) {
            PostgresImporter.run();
            System.out.println("Data imported to PostgreSQL.");
        }
    }
}
