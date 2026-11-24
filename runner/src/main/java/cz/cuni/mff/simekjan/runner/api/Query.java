package cz.cuni.mff.simekjan.runner.api;

/**
 * A query implemented by a submission.
 *
 * @param <C> backend context type
 * @param <P> query parameter type
 */
public interface Query<C, P> {
    /** @return identifier shared by equivalent queries across backends */
    String id();

    /** @return type used to bind query parameters */
    Class<P> parameterType();

    /** @return whether result ordering matters */
    boolean isOrdered();

    /**
     * Executes the query.
     *
     * @param context backend context owned by the runner
     * @param params parameters for the query
     * @return query result
     */
    Object run(C context, P params);
}
