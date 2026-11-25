package cz.cuni.mff.simekjan.runner.config;

import com.fasterxml.jackson.core.StreamReadFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import cz.cuni.mff.simekjan.runner.api.NoParams;
import cz.cuni.mff.simekjan.runner.api.Query;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loads parameters for a query.
 */
public final class ParameterLoader {

    private final ObjectMapper mapper = YAMLMapper.builder(
        YAMLFactory.builder().enable(StreamReadFeature.STRICT_DUPLICATE_DETECTION).build())
        .addModule(new JavaTimeModule())
        .enable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
        .enable(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES)
        .enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
        .enable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)
        .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
        .disable(MapperFeature.ALLOW_COERCION_OF_SCALARS)
        .build();

    /**
     * Binds parameters to the query's declared parameter type.
     *
     * @param paramsFile path to the parameter YAML
     * @param query      query whose parameters are loaded
     * @param <P>        query parameter type
     * @return bound parameters
     * @throws IllegalArgumentException if the file or parameters are invalid
     */
    public <P> P load(Path paramsFile, Query<?, P> query) {
        Class<P> parameterType = query.parameterType();
        if (parameterType == NoParams.class) {
            return parameterType.cast(new NoParams());
        }

        String queryId = query.id();
        try (var input = Files.newInputStream(paramsFile)) {
            JsonNode root = mapper.readTree(input);
            if (root == null || !root.isObject()) {
                throw new IllegalArgumentException("Parameter file must contain a mapping of query IDs: " + paramsFile);
            }
            JsonNode parameters = root.get(queryId);
            if (parameters == null) {
                throw new IllegalArgumentException("Missing parameters for query '" + queryId + "' in " + paramsFile);
            }
            if (!parameters.isObject()) {
                throw new IllegalArgumentException("Parameters for query '" + queryId + "' must be a mapping in " + paramsFile);
            }
            return mapper.treeToValue(parameters, parameterType);
        } catch (IOException e) {
            throw new IllegalArgumentException("Cannot load parameters for query '" + queryId + "' from " + paramsFile, e);
        }
    }
}
