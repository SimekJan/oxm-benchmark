package cz.cuni.mff.simekjan.runner.config;

import cz.cuni.mff.simekjan.runner.api.Query;

import java.nio.file.Path;

/**
 * Loads parameters for a query.
 */
public final class ParameterLoader {

    /**
     * Binds parameters to the query's declared parameter type.
     *
     * @param paramsFile path to the parameter YAML
     * @param query      query whose parameters are loaded
     * @param <P>        query parameter type
     * @return bound parameters
     * @throws UnsupportedOperationException until parameter loading is implemented
     */
    public <P> P load(Path paramsFile, Query<?, P> query) {
        throw new UnsupportedOperationException("Query parameter loading is not implemented");
    }
}
