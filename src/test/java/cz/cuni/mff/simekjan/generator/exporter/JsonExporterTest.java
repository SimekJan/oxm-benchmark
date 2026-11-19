package cz.cuni.mff.simekjan.generator.exporter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.generator.GeneratedTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

class JsonExporterTest {
    @TempDir
    Path outputDirectory;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void appendsBatchesToOneArray() throws Exception {
        Path jsonDirectory = outputDirectory.resolve("json");
        try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
            config.when(ConfigLoader::getJsonDir).thenReturn(jsonDirectory);
            JsonExporter exporter = new JsonExporter();
            exporter.write("people", table(List.of(List.of(1, "Alice", LocalDate.of(2025, 1, 2)))));
            exporter.write("people", table(List.of(List.of(2, "Bob", LocalDate.of(2025, 2, 3)))));
            exporter.write("people", table(List.of()));
        }

        JsonNode actual = objectMapper.readTree(jsonDirectory.resolve("people.json").toFile());
        assertEquals(objectMapper.readTree("""
            [{"id":1,"name":"Alice","date":"2025-01-02"},
             {"id":2,"name":"Bob","date":"2025-02-03"}]
            """), actual);
    }

    @Test
    void appendsToExistingArray() throws Exception {
        Path file = outputDirectory.resolve("people.json");
        Files.writeString(file, " [ {\"nested\": [1, {\"value\": true}]} ] \n");
        try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
            config.when(ConfigLoader::getJsonDir).thenReturn(outputDirectory);
            new JsonExporter().write("people", table(List.of(List.of(2, "Bob", LocalDate.of(2025, 2, 3)))));
        }

        assertEquals(objectMapper.readTree("""
            [{"nested":[1,{"value":true}]},{"id":2,"name":"Bob","date":"2025-02-03"}]
            """), objectMapper.readTree(file.toFile()));
    }

    @Test
    void writesToEmptyFileAndEmptyArray() throws Exception {
        Path file = Files.createFile(outputDirectory.resolve("people.json"));
        try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
            config.when(ConfigLoader::getJsonDir).thenReturn(outputDirectory);
            JsonExporter exporter = new JsonExporter();
            exporter.write("people", table(List.of()));
            exporter.write("people", table(List.of(List.of(1, "Alice", LocalDate.of(2025, 1, 2)))));
        }

        assertEquals(objectMapper.readTree("""
            [{"id":1,"name":"Alice","date":"2025-01-02"}]
            """), objectMapper.readTree(file.toFile()));
    }

    @Test
    void rejectsInvalidExistingJsonWithoutChangingIt() throws Exception {
        Path file = outputDirectory.resolve("people.json");
        for (String invalid : List.of("{\"id\":1}", "[{\"id\":1}", "[] true")) {
            Files.writeString(file, invalid);
            try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
                config.when(ConfigLoader::getJsonDir).thenReturn(outputDirectory);
                assertThrows(java.io.IOException.class,
                    () -> new JsonExporter().write("people", table(List.of())));
            }
            assertEquals(invalid, Files.readString(file));
        }
    }

    private GeneratedTable table(List<List<Object>> rows) {
        return new GeneratedTable(UUID.randomUUID(), "people", List.of("id", "name", "date"), rows);
    }
}
