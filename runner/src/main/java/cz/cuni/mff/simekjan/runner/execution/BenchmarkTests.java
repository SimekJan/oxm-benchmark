package cz.cuni.mff.simekjan.runner.execution;

import cz.cuni.mff.simekjan.runner.api.Query;
import cz.cuni.mff.simekjan.runner.config.ParameterLoader;
import cz.cuni.mff.simekjan.runner.config.RunnerConfig;
import cz.cuni.mff.simekjan.runner.config.RunnerConfigLoader;
import cz.cuni.mff.simekjan.runner.registration.RegistrationRegistry;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

/**
 * Creates JUnit benchmark cases for the registered queries.
 */
@ExtendWith(BenchmarkParameters.class)
@Execution(ExecutionMode.SAME_THREAD)
public final class BenchmarkTests {

    /**
     * Creates one benchmark case for each registered query.
     *
     * @param configFile configuration path supplied by the runner
     * @return benchmark cases
     */
    @TestFactory
    public Stream<DynamicTest> queries(Path configFile) {
        RunnerConfig config = new RunnerConfigLoader().load(configFile);
        return queries(config, new RegistrationRegistry());
    }

    Stream<DynamicTest> queries(RunnerConfig config, RegistrationRegistry registry) {
        if (config.databases().isEmpty()) {
            throw new IllegalArgumentException("No runner database entries configured");
        }
        var ids = new HashSet<String>();
        List<DynamicTest> tests = config.databases().stream().flatMap(database -> {
            if (!ids.add(database.id())) {
                throw new IllegalArgumentException("Duplicate runner database entry: " + database.id());
            }
            var registeredQueries = registry.get(database.backend()).queries();
            if (registeredQueries.isEmpty()) {
                throw new IllegalArgumentException("No queries registered for backend: " + database.backend());
            }
            return queries(config, database, registeredQueries);
        }).toList();
        return tests.stream();
    }

    Stream<DynamicTest> queries(RunnerConfig config, RunnerConfig.Database database,
                                List<? extends Class<? extends Query<?, ?>>> queries) {
        return queries.stream()
            .map(query -> DynamicTest.dynamicTest(
                database.id() + " / " + query.getName(),
                () -> execute(database, query, config.paramsFile())));
    }

    private void execute(RunnerConfig.Database database, Class<? extends Query<?, ?>> queryClass, Path paramsFile) {
        Query<?, ?> query;
        try {
            query = queryClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException("Cannot instantiate query " + queryClass.getName(), e);
        }
        PreparedQuery<?, ?> prepared = prepare(query, paramsFile);
        throw new UnsupportedOperationException(
            "Benchmark execution is not implemented for query '" + prepared.query().id()
                + "' on database entry '" + database.id() + "'"
        );
    }

    <C, P> PreparedQuery<C, P> prepare(Query<C, P> query, Path paramsFile) {
        P params = new ParameterLoader().load(paramsFile, query);
        return new PreparedQuery<>(query, params);
    }

    record PreparedQuery<C, P>(Query<C, P> query, P params) {
    }
}
