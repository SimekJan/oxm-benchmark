package cz.cuni.mff.simekjan.generator;

import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
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
    public GenerateBenchmarkCommand.FormatEnum getFormat() {
        return GenerateBenchmarkCommand.FormatEnum.CSV;
    }

    @Override
    public void export(DatasetEntity dataset, List<GeneratedTable> tables) throws IOException {
        Path outputDirectory = ConfigLoader.getCsvDir().resolve(dataset.getId().toString());
        Files.createDirectories(outputDirectory);

        for (GeneratedTable table : tables) {
            Path outputFile = outputDirectory.resolve(fileName(table.name(), table.id()) + ".csv");
            try (
                BufferedWriter writer = Files.newBufferedWriter(outputFile);
                CSVPrinter csv = new CSVPrinter(
                    writer,
                    CSVFormat.DEFAULT.builder().setHeader(table.columns().toArray(String[]::new)).get()
                )
            ) {
                for (List<Object> row : table.rows()) {
                    csv.printRecord(row);
                }
            }
        }
    }

    private static String fileName(String name, UUID id) {
        return name.replaceAll("[^A-Za-z0-9._-]", "_") + "-" + id;
    }
}
