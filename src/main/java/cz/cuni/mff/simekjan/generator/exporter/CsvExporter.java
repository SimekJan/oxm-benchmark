package cz.cuni.mff.simekjan.generator.exporter;

import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.generator.GeneratedTable;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;

/**
 * Implements CSV serialization for generated dataset records.
 */
@Component
public class CsvExporter implements DataExporter {

    @Override
    public GenerateBenchmarkCommand.FormatsEnum getFormat() {
        return GenerateBenchmarkCommand.FormatsEnum.CSV;
    }

    @Override
    public void write(String tableName, GeneratedTable data) throws IOException {

        // TODO: handle if the file already exists, we are writing by batches

        Path outputDirectory = ConfigLoader.getCsvDir();
        Files.createDirectories(outputDirectory);

        Path outputFile = outputDirectory.resolve(tableName + ".csv");
        try (
            BufferedWriter writer = Files.newBufferedWriter(outputFile);
            CSVPrinter csv = new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.builder().setHeader(data.columns().toArray(String[]::new)).get()
            )
        ) {
            for (List<Object> row : data.rows()) {
                csv.printRecord(row);
            }
        }
    }
}
