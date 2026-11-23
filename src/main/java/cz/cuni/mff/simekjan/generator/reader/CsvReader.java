package cz.cuni.mff.simekjan.generator.reader;

import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Reads CSV exports. Values are returned as strings because CSV does not retain types.
 */
@Component
public class CsvReader implements DataReader {

    private static final CSVFormat CSV_WITH_HEADER = CSVFormat.DEFAULT.builder()
        .setHeader()
        .setSkipHeaderRecord(true)
        .get();

    @Override
    public GenerateBenchmarkCommand.FormatsEnum getFormat() {
        return GenerateBenchmarkCommand.FormatsEnum.CSV;
    }

    @Override
    public List<String> readColumn(String name, String columnName, long startRow, int rowCount) throws IOException {
        if (startRow < 0 || rowCount < 0) {
            throw new IllegalArgumentException("Row start and count must be non-negative");
        }

        Path inputFile = ConfigLoader.getCsvDir().resolve(name + ".csv");
        try (
            var input = Files.newBufferedReader(inputFile);
            CSVParser csv = CSV_WITH_HEADER.parse(input)
        ) {
            List<String> columns = readColumns(csv, inputFile);
            int columnIndex = columns.indexOf(columnName);
            if (columnIndex < 0) {
                throw new IllegalArgumentException("Column '" + columnName + "' not found in " + inputFile);
            }

            List<String> values = new ArrayList<>();
            if (rowCount == 0) {
                return values;
            }
            Iterator<CSVRecord> records = csv.iterator();
            long rowIndex = 0;
            while (values.size() < rowCount && records.hasNext()) {
                CSVRecord record = records.next();
                validateRecord(record, columns.size(), inputFile);
                if (rowIndex >= startRow) {
                    values.add(record.get(columnIndex));
                }
                rowIndex++;
            }
            return values;
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    private static List<String> readColumns(CSVParser csv, Path inputFile) throws IOException {
        List<String> columns = csv.getHeaderNames();
        if (columns.isEmpty()) {
            throw new IOException("Missing CSV header in " + inputFile);
        }
        return columns;
    }

    private static void validateRecord(CSVRecord record, int columnCount, Path inputFile) throws IOException {
        if (record.size() != columnCount) {
            throw new IOException("Invalid CSV record " + record.getRecordNumber() + " in " + inputFile);
        }
    }
}
