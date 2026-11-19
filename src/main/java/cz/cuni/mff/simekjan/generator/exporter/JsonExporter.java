package cz.cuni.mff.simekjan.generator.exporter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.generator.GeneratedTable;
import cz.cuni.mff.simekjan.model.GenerateBenchmarkCommand;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

        Path outputDirectory = ConfigLoader.getJsonDir();
        Files.createDirectories(outputDirectory);

        Path outputFile = outputDirectory.resolve(tableName + ".json");
        Path temporaryFile = Files.createTempFile(outputDirectory, ".json-export-", ".tmp");
        try {
            try (
                var writer = Files.newBufferedWriter(temporaryFile);
                JsonGenerator generator = objectMapper.getFactory().createGenerator(writer)
            ) {
                generator.writeStartArray();
                if (Files.exists(outputFile) && Files.size(outputFile) > 0) {
                    try (JsonParser parser = objectMapper.getFactory().createParser(outputFile.toFile())) {
                        if (parser.nextToken() != JsonToken.START_ARRAY) {
                            throw new IOException("Expected a JSON array in " + outputFile);
                        }
                        JsonToken token;
                        while ((token = parser.nextToken()) != JsonToken.END_ARRAY) {
                            if (token == null) {
                                throw new IOException("Incomplete JSON array in " + outputFile);
                            }
                            generator.copyCurrentStructure(parser);
                        }
                        if (parser.nextToken() != null) {
                            throw new IOException("Unexpected content after JSON array in " + outputFile);
                        }
                    }
                }
                for (List<Object> row : data.rows()) {
                    Map<String, Object> record = new LinkedHashMap<>();
                    for (int columnIndex = 0; columnIndex < data.columns().size(); columnIndex++) {
                        Object value = row.get(columnIndex);
                        record.put(
                            data.columns().get(columnIndex),
                            value instanceof LocalDate date ? date.toString() : value
                        );
                    }
                    objectMapper.writeValue(generator, record);
                }
                generator.writeEndArray();
            }
            try {
                Files.move(temporaryFile, outputFile, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException e) {
                Files.move(temporaryFile, outputFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } finally {
            Files.deleteIfExists(temporaryFile);
        }
    }
}
