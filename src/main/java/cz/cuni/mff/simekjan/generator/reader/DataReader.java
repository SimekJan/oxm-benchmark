package cz.cuni.mff.simekjan.generator.reader;

import cz.cuni.mff.simekjan.generator.GeneratedTable;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Reads serialized generated dataset records.
 */
public interface DataReader {

    GenerateBenchmarkCommand.FormatsEnum getFormat();

    /**
     * Reads values from one column in a range of data rows.
     *
     * @param name table name used to locate the exported file
     * @param columnName name of the column to read
     * @param startRow zero-based index of the first data row (excluding the header)
     * @param rowCount maximum number of values to return
     * @return column values in file order; fewer than rowCount if the end of the file is reached
     * @throws IllegalArgumentException if the range is negative or the column does not exist
     * @throws IOException if the file cannot be read or is invalid
     */
    List<String> readColumn(String name, String columnName, long startRow, int rowCount) throws IOException;
}
