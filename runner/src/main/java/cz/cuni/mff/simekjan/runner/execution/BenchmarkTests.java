package cz.cuni.mff.simekjan.runner.execution;

import cz.cuni.mff.simekjan.runner.api.Query;
import cz.cuni.mff.simekjan.runner.backend.BackendRegistry;
import cz.cuni.mff.simekjan.runner.config.ParameterLoader;
import cz.cuni.mff.simekjan.runner.config.RunnerConfig;
import cz.cuni.mff.simekjan.runner.config.RunnerConfigLoader;
import cz.cuni.mff.simekjan.runner.registration.HibernateRegistration;
import cz.cuni.mff.simekjan.runner.registration.HibernateSubmission;
import cz.cuni.mff.simekjan.runner.registration.RegistrationRegistry;
import cz.cuni.mff.simekjan.runner.spi.ContextFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Creates JUnit benchmark cases for the registered queries.
 */
@ExtendWith(BenchmarkParameters.class)
@Execution(ExecutionMode.SAME_THREAD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public final class BenchmarkTests {

    private RegistrationRegistry registry;
    private final BackendRegistry backends = new BackendRegistry();
    private final Map<String, ContextFactory> contextFactories = new LinkedHashMap<>();

    /**
     * Creates one benchmark case for each registered query.
     *
     * @param configFile configuration path supplied by the runner
     * @return benchmark cases
     */
    @TestFactory
    public Stream<DynamicTest> queries(Path configFile) {
        RunnerConfig config = new RunnerConfigLoader().load(configFile);
        registry = new RegistrationRegistry(config.sourceDir());
        return queries(config, registry);
    }

    @AfterAll
    void closeRegistry() throws Exception {
        Exception failure = null;
        for (ContextFactory factory : contextFactories.values()) {
            try {
                factory.close();
            } catch (Exception e) {
                if (failure == null) {
                    failure = e;
                } else {
                    failure.addSuppressed(e);
                }
            }
        }
        try {
            if (registry != null) {
                registry.close();
            }
        } catch (Exception e) {
            if (failure == null) {
                failure = e;
            } else {
                failure.addSuppressed(e);
            }
        }
        if (failure != null) {
            throw failure;
        }
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
            var registration = registry.get(database.backend());
            var registeredQueries = registration.queries();
            registeredQueries.forEach(query ->
                System.out.println("Loaded query for " + database.id() + ": " + query.getClass().getName()));
            var failures = registration instanceof HibernateRegistration hibernate
                ? hibernate.failures() : List.<HibernateSubmission.Failure>of();
            if (registeredQueries.isEmpty() && failures.isEmpty()) {
                throw new IllegalArgumentException("No queries registered for backend: " + database.backend());
            }
            Stream<DynamicTest> failedTests = failures.stream().map(failure -> DynamicTest.dynamicTest(
                database.id() + " / " + failure.className(),
                () -> {
                    throw new AssertionError("Cannot load or instantiate " + failure.className(), failure.cause());
                }));
            return Stream.concat(queries(config, database, registeredQueries, registration.mappedClasses()), failedTests);
        }).toList();
        return tests.stream();
    }

    Stream<DynamicTest> queries(RunnerConfig config, RunnerConfig.Database database,
                                List<? extends Query<?, ?>> queries, List<Class<?>> mappedClasses) {
        return queries.stream()
            .map(query -> DynamicTest.dynamicTest(
                database.id() + " / " + query.getClass().getName(),
                () -> {
                    try {
                        ContextFactory factory = contextFactories.computeIfAbsent(database.id(),
                            id -> backends.get(database.backend())
                                .createContextFactory(database.connection(), mappedClasses));
                        try (var context = factory.open()) {
                            execute(query, config.paramsFile(), context.value());
                        }
                    } catch (Throwable failure) {
                        throw new AssertionError("Query failed: " + query.getClass().getName(), failure);
                    }
                }));
    }

    private <C, P> void execute(Query<C, P> query, Path paramsFile, Object context) {
        PreparedQuery<C, P> prepared = prepare(query, paramsFile);
        prepared.query().run(query.contextType().cast(context), prepared.params());
    }

    <C, P> PreparedQuery<C, P> prepare(Query<C, P> query, Path paramsFile) {
        P params = new ParameterLoader().load(paramsFile, query);
        return new PreparedQuery<>(query, params);
    }

    record PreparedQuery<C, P>(Query<C, P> query, P params) {
    }
}
