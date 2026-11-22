package cz.cuni.mff.simekjan.generator.exporter;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.generator.GeneratedTable;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;

import java.io.IOException;
import java.util.List;

/**
 * Defines serialization methods for generated dataset records.
 */
public interface DataExporter {

    GenerateBenchmarkCommand.FormatsEnum getFormat();

    /**
     * Exports the generated dataset records to a file.
     *
     * @param data Prepared data to export
     * @throws IOException if an I/O error occurs
     */
    void write(GeneratedTable data) throws IOException;
}
