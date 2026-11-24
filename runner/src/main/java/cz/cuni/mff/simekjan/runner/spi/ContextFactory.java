package cz.cuni.mff.simekjan.runner.spi;

/**
 * Creates contexts for individual query invocations.
 */
public interface ContextFactory extends AutoCloseable {

    /**
     * @return a fresh query context
     */
    QueryContext open();

    /**
     * Releases backend resources.
     */
    @Override
    void close();
}
