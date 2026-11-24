package cz.cuni.mff.simekjan.runner.execution;

import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import cz.cuni.mff.simekjan.runner.registration.HibernateRegistration;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Creates JUnit benchmark cases for the registered queries.
 */
@ExtendWith(BenchmarkParameters.class)
public final class BenchmarkTests {

    /**
     * Creates one benchmark case for each registered query.
     *
     * @param configFile configuration path supplied by the runner
     * @return benchmark cases
     */
    @TestFactory
    public Stream<DynamicTest> queries(Path configFile) {
        return HibernateRegistration.queries().stream()
            .map(query -> DynamicTest.dynamicTest(query.getName(), () -> execute(query, configFile)));
    }

    private void execute(Class<? extends HibernateQuery<?>> query, Path configFile) {
        throw new UnsupportedOperationException(
            "Benchmark execution and configuration loading are not implemented for "
                + query.getName() + " (configuration: " + configFile + ")"
        );
    }
}
