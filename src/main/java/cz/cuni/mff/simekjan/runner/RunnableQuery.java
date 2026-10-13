package cz.cuni.mff.simekjan.runner;

/**
 * Common interface for exposing run method for all Queries
 */
public interface RunnableQuery {

    /**
     * Run the described query
     */
    QueryResult run();
}
