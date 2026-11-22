package cz.cuni.mff.simekjan.generator.exporter;

import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.generator.GeneratedTable;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;

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
    public void write(GeneratedTable data) throws IOException {

        Path outputDirectory = ConfigLoader.getCsvDir();
        Files.createDirectories(outputDirectory);

        Path outputFile = outputDirectory.resolve(data.name() + ".csv");
        boolean writeHeader = !Files.exists(outputFile) || Files.size(outputFile) == 0;
        try (
            BufferedWriter writer = Files.newBufferedWriter(
                outputFile, StandardOpenOption.CREATE, StandardOpenOption.APPEND
            );
            CSVPrinter csv = new CSVPrinter(
                writer,
                CSVFormat.DEFAULT.builder()
                    .setHeader(data.columns().toArray(String[]::new))
                    .setSkipHeaderRecord(!writeHeader)
                    .get()
            )
        ) {
            for (List<Object> row : data.rows()) {
                csv.printRecord(row);
            }
        }
    }
}
