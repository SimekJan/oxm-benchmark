package cz.cuni.mff.simekjan.runner.execution;

import cz.cuni.mff.simekjan.example.northwind.hibernate.queries.Query01_select_indexed;
import cz.cuni.mff.simekjan.example.northwind.hibernate.queries.Query07_join_indexed;
import cz.cuni.mff.simekjan.example.northwind.params.Q1_Params;
import cz.cuni.mff.simekjan.runner.api.NoParams;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BenchmarkTestsTest {
    private final BenchmarkTests benchmark = new BenchmarkTests();

    @TempDir
    Path directory;

    @Test
    void preparesTypedQueryAndParameters() throws IOException {
        Path paramsFile = Files.writeString(directory.resolve("params.yaml"), "Q1:\n  employeeId: 42\n");

        BenchmarkTests.PreparedQuery<Session, Q1_Params> prepared =
            benchmark.prepare(new Query01_select_indexed(), paramsFile);

        assertEquals("Q1", prepared.query().id());
        assertEquals(new Q1_Params(42L), prepared.params());
    }

    @Test
    void preparesParameterlessQueryWithoutFile() {
        BenchmarkTests.PreparedQuery<Session, NoParams> prepared =
            benchmark.prepare(new Query07_join_indexed(), directory.resolve("missing.yaml"));

        assertEquals("Q7", prepared.query().id());
        assertEquals(new NoParams(), prepared.params());
    }
}
