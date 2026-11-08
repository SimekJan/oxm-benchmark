package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.model.DatasetInfo;
import cz.cuni.mff.simekjan.model.DatasetRuntimeStatus;
import cz.cuni.mff.simekjan.model.QueryInfo;
import cz.cuni.mff.simekjan.model.CreateDatasetFromDatabaseCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetFromFileCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetFromSchemaCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetWithoutSchemaCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DatasetService {

    private final DatasetRepository datasetRepository;
    private final QueryRepository queryRepository;

    public List<DatasetInfo> getDatasets() {
        return null;
    }

    @Transactional(noRollbackFor = ExecutorUnavailableException.class)
    public UUID createDatasetFromSchema(CreateDatasetFromSchemaCommand command) {
        validateSchema(command);
        UUID datasetId = UUID.randomUUID();

        DatasetEntity dataset = new DatasetEntity();
        dataset.setId(datasetId);
        dataset.setName(command.getName());
        dataset.setDescription(command.getDescription());
        dataset.setRuntimeStatus("BUILDING");

        for (var tableSchema : command.getSchema().getTables()) {
            TableEntity table = new TableEntity();
            table.setId(UUID.randomUUID());
            table.setName(tableSchema.getName());
            table.setDataset(dataset);

            for (var columnSchema : tableSchema.getColumns()) {
                ColumnEntity column = new ColumnEntity();
                column.setId(UUID.randomUUID());
                column.setName(columnSchema.getName());
                column.setDatatype(columnSchema.getDatatype().getValue());
                column.setIndexed(columnSchema.getIndexed());
                column.setPartOfKey(columnSchema.getIsKey());
                column.setOptional(columnSchema.getOptional());
                column.setTable(table);
                table.getColumns().add(column);
            }

            dataset.getTables().add(table);
        }

        datasetRepository.saveAndFlush(dataset);
        return datasetId;
    }

    public UUID createDatasetWithoutSchema(CreateDatasetWithoutSchemaCommand command) {
        throw new DatasetCreationNotSupportedException("none");
    }

    public UUID createDatasetFromDatabase(CreateDatasetFromDatabaseCommand command) {
        throw new DatasetCreationNotSupportedException("database");
    }

    public UUID createDatasetFromFile(CreateDatasetFromFileCommand command) {
        throw new DatasetCreationNotSupportedException("file");
    }

    @Transactional
    public DatasetRuntimeStatus getRuntimeStatus(UUID datasetId) {
        return null;
    }

    public List<QueryInfo> getQueries(UUID datasetId) {
        return queryRepository.findAllByDataset_IdOrderByNameAsc(datasetId)
            .stream()
            .map(query -> {
                QueryInfo info = new QueryInfo(query.getId(), query.getName());
                info.setDescription(query.getDescription());
                return info;
            })
            .toList();
    }

    private static void validateSchema(CreateDatasetFromSchemaCommand command) {
        if (command == null || command.getSchema() == null) {
            throw new IllegalArgumentException("Dataset schema is required");
        }
        if (command.getName() == null || command.getName().isBlank()) {
            throw new IllegalArgumentException("Dataset name must not be blank");
        }
        if (command.getSchema().getTables() == null || command.getSchema().getTables().isEmpty()) {
            throw new IllegalArgumentException("Dataset schema must contain at least one table");
        }
    }

    public static class DatasetNotFoundException extends RuntimeException {
        public DatasetNotFoundException(UUID datasetId) {
            super("Dataset not found: " + datasetId);
        }
    }

    public static class ExecutorUnavailableException extends RuntimeException {
        public ExecutorUnavailableException(Throwable cause) {
            super("Dataset executor is unavailable", cause);
        }
    }

    public static class DatasetCreationNotSupportedException extends RuntimeException {
        public DatasetCreationNotSupportedException(String creationType) {
            super("Dataset creation type is not implemented: " + creationType);
        }
    }
}
