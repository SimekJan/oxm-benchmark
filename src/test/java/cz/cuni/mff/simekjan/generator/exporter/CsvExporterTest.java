package cz.cuni.mff.simekjan.generator.exporter;

import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.generator.GeneratedTable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class CsvExporterTest {

    @TempDir
    Path outputDirectory;

    @Test
    void appendsBatchesWithOneHeader() throws Exception {
        Path csvDirectory = outputDirectory.resolve("csv");
        try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
            config.when(ConfigLoader::getCsvDir).thenReturn(csvDirectory);
            CsvExporter exporter = new CsvExporter();
            exporter.write("people", table(List.of(List.of(1, "Alice"))));
            exporter.write("people", table(List.of(List.of(2, "Bob, Jr."))));
        }

        assertEquals(
            "id,name\r\n1,Alice\r\n2,\"Bob, Jr.\"\r\n",
            Files.readString(csvDirectory.resolve("people.csv"))
        );
    }

    @Test
    void appendsToExistingFileWithoutWritingAnotherHeader() throws Exception {
        Path file = outputDirectory.resolve("people.csv");
        Files.writeString(file, "id,name\r\n1,Alice\r\n");
        try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
            config.when(ConfigLoader::getCsvDir).thenReturn(outputDirectory);
            new CsvExporter().write("people", table(List.of(List.of(2, "Bob"))));
        }

        assertEquals("id,name\r\n1,Alice\r\n2,Bob\r\n", Files.readString(file));
    }

    @Test
    void writesHeaderToExistingEmptyFile() throws Exception {
        Path file = Files.createFile(outputDirectory.resolve("people.csv"));
        try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
            config.when(ConfigLoader::getCsvDir).thenReturn(outputDirectory);
            new CsvExporter().write("people", table(List.of()));
        }

        assertEquals("id,name\r\n", Files.readString(file));
    }

    private GeneratedTable table(List<List<Object>> rows) {
        return new GeneratedTable(UUID.randomUUID(), "people", List.of("id", "name"), rows);
    }
}
