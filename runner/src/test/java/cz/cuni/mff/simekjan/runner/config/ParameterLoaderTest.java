package cz.cuni.mff.simekjan.runner.config;

import cz.cuni.mff.simekjan.example.northwind.hibernate.queries.Query01_select_indexed;
import cz.cuni.mff.simekjan.example.northwind.hibernate.queries.Query02_select_non_indexed;
import cz.cuni.mff.simekjan.example.northwind.hibernate.queries.Query03_range_query_indexed;
import cz.cuni.mff.simekjan.example.northwind.hibernate.queries.Query04_range_query_non_indexed;
import cz.cuni.mff.simekjan.example.northwind.params.Q1_Params;
import cz.cuni.mff.simekjan.example.northwind.params.Q2_Params;
import cz.cuni.mff.simekjan.example.northwind.params.Q3_Params;
import cz.cuni.mff.simekjan.example.northwind.params.Q4_Params;
import cz.cuni.mff.simekjan.runner.api.NoParams;
import cz.cuni.mff.simekjan.runner.api.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ParameterLoaderTest {

    private final ParameterLoader loader = new ParameterLoader();

    @TempDir
    Path directory;

    @Test
    void loadsExampleParameters() {
        Path file = Path.of("example-submission", "params.yaml");

        assertEquals(new Q1_Params(42L), loader.load(file, new Query01_select_indexed()));
        assertEquals(new Q2_Params("Steve"), loader.load(file, new Query02_select_non_indexed()));
        assertEquals(new Q3_Params(42L, 43L), loader.load(file, new Query03_range_query_indexed()));
        assertEquals(new Q4_Params(10.0F, 20.0F), loader.load(file, new Query04_range_query_non_indexed()));
    }

    @Test
    void parameterlessQueryDoesNotRequireFile() {
        assertEquals(new NoParams(), loader.load(directory.resolve("missing.yaml"), query("Q7", NoParams.class)));
    }

    @Test
    void loadsDateAndCharacter() throws IOException {
        Path file = write("Q1:\n  date: 2026-10-01\n  letter: S\n");
        assertEquals(new DateParams(LocalDate.of(2026, 10, 1), 'S'), loader.load(file, query("Q1", DateParams.class)));
    }

    @Test
    void rejectsMissingFileAndQuery() throws IOException {
        IllegalArgumentException missingFile = assertThrows(IllegalArgumentException.class,
            () -> loader.load(directory.resolve("missing.yaml"), query("Q1", Q1_Params.class)));
        assertInstanceOf(IOException.class, missingFile.getCause());

        Path file = write("Q2:\n  firstName: Steve\n");
        IllegalArgumentException missingQuery = assertThrows(IllegalArgumentException.class,
            () -> loader.load(file, query("Q1", Q1_Params.class)));
        assertTrue(missingQuery.getMessage().contains("Q1"));
    }

    @Test
    void rejectsInvalidMappings() throws IOException {
        for (String yaml : new String[] {
            "", "- Q1\n", "Q1: 42\n", "Q1:\n  employeeId: 42\nQ1:\n  employeeId: 43\n",
            "Q1:\n  employeeId: 42\n  employeeId: 43\n",
            "Q1:\n  employeeId: 42\n---\nQ1:\n  employeeId: 43\n",
            "Q1: [\n"
        }) {
            Path file = write(yaml);
            assertThrows(IllegalArgumentException.class, () -> loader.load(file, query("Q1", Q1_Params.class)), yaml);
        }
    }

    @Test
    void rejectsMissingNullUnknownAndInvalidFields() throws IOException {
        for (String yaml : new String[] {
            "Q1: {}\n", "Q1:\n  employeeId: null\n", "Q1:\n  employeeId: 42\n  extra: 1\n",
            "Q1:\n  employeeId: wrong\n", "Q1:\n  employeeId: 42.5\n",
            "Q1:\n  employeeId: '42'\n", "Q1:\n  employeeId: 9223372036854775808\n"
        }) {
            Path file = write(yaml);
            assertThrows(IllegalArgumentException.class, () -> loader.load(file, query("Q1", Q1_Params.class)), yaml);
        }
    }

    @Test
    void rejectsInvalidDateAndCharacter() throws IOException {
        for (String yaml : new String[] {
            "Q1:\n  date: invalid\n  letter: S\n", "Q1:\n  date: 2026-10-01\n  letter: Steve\n"
        }) {
            Path file = write(yaml);
            assertThrows(IllegalArgumentException.class, () -> loader.load(file, query("Q1", DateParams.class)), yaml);
        }
    }

    private Path write(String content) throws IOException {
        return Files.writeString(directory.resolve("params.yaml"), content);
    }

    private static <P> Query<Object, P> query(String id, Class<P> type) {
        return new Query<>() {
            @Override
            public String id() {
                return id;
            }

            @Override
            public Class<P> parameterType() {
                return type;
            }

            @Override
            public boolean isOrdered() {
                return false;
            }

            @Override
            public Object run(Object context, P params) {
                throw new UnsupportedOperationException();
            }
        };
    }

    record DateParams(LocalDate date, Character letter) {
    }
}
