package cz.cuni.mff.simekjan.runner.execution;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.nio.file.Path;

/**
 * Provides per-launch configuration to the benchmark test factory.
 */
public final class BenchmarkParameters implements ParameterResolver {

    static final String CONFIG_FILE = "oxm.runner.configFile";

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean supportsParameter(ParameterContext parameter, @NonNull ExtensionContext context) {
        return parameter.getParameter().getType() == Path.class;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object resolveParameter(@NonNull ParameterContext parameter, ExtensionContext context) {
        return context.getConfigurationParameter(CONFIG_FILE)
            .map(Path::of)
            .orElseThrow(() -> new ParameterResolutionException("Runner configuration path is missing"));
    }
}
