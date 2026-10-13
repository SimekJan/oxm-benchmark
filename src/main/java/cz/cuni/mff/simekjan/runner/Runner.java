package cz.cuni.mff.simekjan.runner;

import cz.cuni.mff.simekjan.ConfigLoader;

import com.fasterxml.jackson.core.JsonGenerator;
import cz.cuni.mff.simekjan.generator.Generator;
import cz.cuni.mff.simekjan.graph.Neo4jSessionManager;
import cz.cuni.mff.simekjan.relational.HibernateSessionManager;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Main entrypoint for the query execution
 */
@Slf4j
public class Runner {

    private static final int DEFAULT_NUMBER_OF_RUNS = 20;
    private static final String DEFAULT_PRINT_OPTION = "quiet";

    /**
     * Starts the benchmark with provided parameters from config
     *
     * @throws IOException If config file cannot be loaded.
     */
    static void main() throws IOException {

        Map<String, Object> config = ConfigLoader.getConfig();

        List<String> dbsUsed = ((List<?>) config.get("databaseToUse"))
            .stream()
            .map(String.class::cast)
            .toList();

        Map<String, Object> runnerConfig = ConfigLoader.getSection("runner");

        Integer numberOfRuns = (Integer) runnerConfig.getOrDefault("numberOfRuns", DEFAULT_NUMBER_OF_RUNS);
        String printOption = (String) runnerConfig.getOrDefault("printOption", DEFAULT_PRINT_OPTION);

        List<List<QueryResult>> results = new ArrayList<>();

        if (dbsUsed.contains("mongo")) {
            System.out.println("---------------------------------");
            System.out.println("Starting MongoDB queries");
            System.out.println("---------------------------------");

            QueriesRun run = new DocumentQueriesRun();

            // Warm-up
            if (Objects.equals(printOption, "quiet")) {
                runAll(run.getQueries());
            }

            // Measurement
            for (int i = 0; i < numberOfRuns; i++) {
                results.add(runAll(run.getQueries()));
            }

            System.out.println("---------------------------------");
            System.out.println("Results after MongoDB queries: " + (long) results.size());
        }

        if (dbsUsed.contains("mongoEmbedded")) {
            System.out.println("---------------------------------");
            System.out.println("Starting Embedded MongoDB queries");
            System.out.println("---------------------------------");

            QueriesRun run = new EmbeddedDocumentQueriesRun();

            // Warm-up
            if (Objects.equals(printOption, "quiet")) {
                runAll(run.getQueries());
            }

            // Measurement
            for (int i = 0; i < numberOfRuns; i++) {
                results.add(runAll(run.getQueries()));
            }

            System.out.println("---------------------------------");
            System.out.println("Results after Embedded MongoDB queries: " + (long) results.size());
        }

        if (dbsUsed.contains("neo4j")) {
            System.out.println("---------------------------------");
            System.out.println("Starting Neo4j queries");
            System.out.println("---------------------------------");

            QueriesRun run = new GraphQueriesRun();

            // Warm-up
            if (Objects.equals(printOption, "quiet")) {
                runAll(run.getQueries());
            }

            // Measurement
            for (int i = 0; i < numberOfRuns; i++) {
                // For emptying caches
                Neo4jSessionManager.getSession().clear();

                results.add(runAll(run.getQueries()));
            }

            System.out.println("---------------------------------");
            System.out.println("Results after Neo4j queries: " + (long) results.size());
        }

        if (dbsUsed.contains("postgres")) {
            System.out.println("---------------------------------");
            System.out.println("Starting PostgreSQL queries");
            System.out.println("---------------------------------");

            QueriesRun run = new RelationalQueriesRun();

            // Warm-up
            if (Objects.equals(printOption, "quiet")) {
                runAll(run.getQueries());
            }

            // Measurement
            for (int i = 0; i < numberOfRuns; i++) {
                // For emptying caches
                HibernateSessionManager.getSession().clear();

                results.add(runAll(run.getQueries()));
            }

            System.out.println("---------------------------------");
            System.out.println("Results after PostgreSQL queries: " + (long) results.size());
        }

        // Save results
        Path dataDir = ConfigLoader.getDataDir();
        Path dataPath = dataDir.resolve("results.json");
        try (JsonGenerator json = Generator.jsonGenerator(dataPath)) {
            json.writeObject(results);
        }

        System.out.println("---------------------------------");
        System.out.println("Runner executed.");
    }

    private static List<QueryResult> runAll(List<RunnableQuery> queries) {
        List<QueryResult> results = new ArrayList<>();

        for (RunnableQuery query : queries) {
            results.add(query.run());
        }

        return results;
    }
}
