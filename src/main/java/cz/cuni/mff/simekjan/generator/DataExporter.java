package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;

import java.io.IOException;
import java.util.List;

/**
 * Defines serialization methods for generated dataset records.
 */
public interface DataExporter {

    GenerateBenchmarkCommand.FormatEnum getFormat();

    void export(DatasetEntity dataset, List<GeneratedTable> tables) throws IOException;
}
