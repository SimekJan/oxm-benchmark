package cz.cuni.mff.simekjan.runner;

import java.util.function.Consumer;

/**
 * Implementation of common class for all Queries
 * with fixed method and parameters
 *
 * @param <P> Parameters record of given Query
 */
public class BoundedQuery<P> implements RunnableQuery {

    private final P params;

    private final Consumer<P> operation;

    public BoundedQuery(P params, Consumer<P> operation) {
        this.params = params;
        this.operation = operation;
    }

    /**
     * Run the described query
     */
    @Override
    public QueryResult run() {
        long start = System.nanoTime();
        operation.accept(params);
        long end = System.nanoTime();

        return new QueryResult(end - start);
    }
}
