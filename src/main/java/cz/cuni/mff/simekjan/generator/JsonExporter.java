package cz.cuni.mff.simekjan.generator;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
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
    public GenerateBenchmarkCommand.FormatEnum getFormat() {
        return GenerateBenchmarkCommand.FormatEnum.JSON;
    }

    @Override
    public void export(DatasetEntity dataset, List<GeneratedTable> tables) throws IOException {
        Path outputDirectory = ConfigLoader.getJsonDir().resolve(dataset.getId().toString());
        Files.createDirectories(outputDirectory);

        for (GeneratedTable table : tables) {
            Path outputFile = outputDirectory.resolve(fileName(table.name(), table.id()) + ".json");
            List<Map<String, Object>> records = new ArrayList<>(table.rows().size());
            for (List<Object> row : table.rows()) {
                Map<String, Object> record = new LinkedHashMap<>();
                for (int columnIndex = 0; columnIndex < table.columns().size(); columnIndex++) {
                    Object value = row.get(columnIndex);
                    record.put(
                        table.columns().get(columnIndex),
                        value instanceof LocalDate date ? date.toString() : value
                    );
                }
                records.add(record);
            }
            objectMapper.writeValue(outputFile.toFile(), records);
        }
    }

    private static String fileName(String name, UUID id) {
        return name.replaceAll("[^A-Za-z0-9._-]", "_") + "-" + id;
    }
}
