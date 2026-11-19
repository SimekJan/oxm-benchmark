package cz.cuni.mff.simekjan.generator.exporter;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.generator.GeneratedTable;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implements JSON serialization for generated dataset records.
 */
@Component
public class JsonExporter implements DataExporter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public GenerateBenchmarkCommand.FormatsEnum getFormat() {
        return GenerateBenchmarkCommand.FormatsEnum.JSON;
    }

    @Override
    public void write(String tableName, GeneratedTable data) throws IOException {

        // TODO: handle if the file already exists, we are writing by batches

        Path outputDirectory = ConfigLoader.getJsonDir();
        Files.createDirectories(outputDirectory);

        Path outputFile = outputDirectory.resolve(tableName + ".json");
        List<Map<String, Object>> records = new ArrayList<>(data.rows().size());
        for (List<Object> row : data.rows()) {
            Map<String, Object> record = new LinkedHashMap<>();
            for (int columnIndex = 0; columnIndex < data.columns().size(); columnIndex++) {
                Object value = row.get(columnIndex);
                record.put(
                    data.columns().get(columnIndex),
                    value instanceof LocalDate date ? date.toString() : value
                );
            }
            records.add(record);
        }
        objectMapper.writeValue(outputFile.toFile(), records);
    }
}
