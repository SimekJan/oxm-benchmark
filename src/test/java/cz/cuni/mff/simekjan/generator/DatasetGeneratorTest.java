package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.generator.exporter.DataExporter;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand.FormatsEnum;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DatasetGeneratorTest {

    @Test
    void wrapsExportFailureAndStopsGeneration() throws IOException {
        DataExporter csv = mock(DataExporter.class);
        DataExporter json = mock(DataExporter.class);
        when(csv.getFormat()).thenReturn(FormatsEnum.CSV);
        when(json.getFormat()).thenReturn(FormatsEnum.JSON);
        IOException cause = new IOException("disk full");
        doThrow(cause).when(csv).write(any());

        TableEntity table = new TableEntity();
        table.setId(UUID.randomUUID());
        table.setName("people");
        table.setNumberOfRows(1);
        DatasetEntity dataset = new DatasetEntity();
        dataset.getTables().add(table);
        DatasetGenerator generator = new DatasetGenerator(List.of(csv, json));

        UncheckedIOException error = assertThrows(
            UncheckedIOException.class,
            () -> generator.generate(dataset, List.of(FormatsEnum.CSV, FormatsEnum.JSON))
        );

        assertEquals("Failed to export table 'people' as " + FormatsEnum.CSV, error.getMessage());
        assertSame(cause, error.getCause());
        verify(json, never()).write(any());
    }
}
