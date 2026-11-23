package cz.cuni.mff.simekjan.generator.reader;

import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.generator.GeneratedTable;
import cz.cuni.mff.simekjan.generator.exporter.CsvExporter;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand.FormatsEnum;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.MockedStatic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

class CsvReaderTest {

    @TempDir
    Path csvDirectory;

    @Test
    void validatesRangeAndColumn() throws Exception {
        try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
            config.when(ConfigLoader::getCsvDir).thenReturn(csvDirectory);
            Files.writeString(csvDirectory.resolve("people.csv"), "id,name\r\n1,Alice\r\n");
            CsvReader reader = new CsvReader();
            assertThrows(IllegalArgumentException.class, () -> reader.readColumn("people", "name", -1, 1));
            assertThrows(IllegalArgumentException.class, () -> reader.readColumn("people", "name", 0, -1));
            assertThrows(IllegalArgumentException.class, () -> reader.readColumn("people", "unknown", 0, 0));
        }
    }

    @Test
    void validatesRecordsTraversedButDoesNotReadPastRange() throws Exception {
        try (MockedStatic<ConfigLoader> config = mockStatic(ConfigLoader.class)) {
            config.when(ConfigLoader::getCsvDir).thenReturn(csvDirectory);
            Path file = csvDirectory.resolve("people.csv");
            CsvReader reader = new CsvReader();
            Files.writeString(file, "id,name\r\n1,Alice\r\n2,\"unfinished");
            assertEquals(List.of("Alice"), reader.readColumn("people", "name", 0, 1));
            assertThrows(IOException.class, () -> reader.readColumn("people", "name", 1, 1));
            Files.writeString(file, "id,name\r\n1\r\n2,Bob\r\n");
            assertThrows(IOException.class, () -> reader.readColumn("people", "name", 1, 1));
            Files.writeString(file, "");
            assertThrows(IOException.class, () -> reader.readColumn("people", "name", 0, 1));
        }
    }
}
