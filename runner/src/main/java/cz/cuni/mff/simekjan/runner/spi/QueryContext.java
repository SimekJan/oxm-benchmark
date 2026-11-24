package cz.cuni.mff.simekjan.runner.spi;

/**
 * A backend context owned by the runner for one query invocation.
 */
public interface QueryContext extends AutoCloseable {

    /**
     * @return backend-specific context passed to the query
     */
    Object value();

    /**
     * Releases the query context.
     */
    @Override
    void close();
}
