package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipType;
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

        Map<String, TableEntity> tablesByName = new HashMap<>();
        for (var tableSchema : command.getSchema().getTables()) {
            TableEntity table = new TableEntity();
            table.setId(UUID.randomUUID());
            table.setName(tableSchema.getName());
            table.setDataset(dataset);
            tablesByName.put(tableSchema.getName(), table);

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

        for (var relationshipSchema : command.getSchema().getRelationships()) {
            RelationshipEntity relationship = new RelationshipEntity();
            relationship.setId(UUID.randomUUID());
            relationship.setName(relationshipSchema.getName());
            relationship.setRelationshipType(RelationshipType.fromValue(
                relationshipSchema.getRelationshipType().getValue()
            ));
            relationship.setDataset(dataset);
            relationship.setSourceColumn(resolveColumn(
                tablesByName, relationshipSchema.getFromTable(), relationshipSchema.getFromColumn(), "source"
            ));
            relationship.setTargetColumn(resolveColumn(
                tablesByName, relationshipSchema.getToTable(), relationshipSchema.getToColumn(), "target"
            ));
            dataset.getRelationships().add(relationship);
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
        if (command.getSchema().getRelationships() == null) {
            throw new IllegalArgumentException("Dataset schema relationships must not be null");
        }
        Set<String> tableNames = new HashSet<>();
        for (var table : command.getSchema().getTables()) {
            if (table == null || table.getName() == null || table.getName().isBlank()) {
                throw new IllegalArgumentException("Table names must not be blank");
            }
            if (!tableNames.add(table.getName())) {
                throw new IllegalArgumentException("Table names must be unique: " + table.getName());
            }

            Set<String> columnNames = new HashSet<>();
            if (table.getColumns() == null) {
                throw new IllegalArgumentException("Table columns must not be null: " + table.getName());
            }
            for (var column : table.getColumns()) {
                if (column == null || column.getName() == null || column.getName().isBlank()) {
                    throw new IllegalArgumentException("Column names must not be blank in table: " + table.getName());
                }
                if (!columnNames.add(column.getName())) {
                    throw new IllegalArgumentException(
                        "Column names must be unique in table " + table.getName() + ": " + column.getName()
                    );
                }
            }
        }
        for (var relationship : command.getSchema().getRelationships()) {
            if (relationship == null || relationship.getName() == null || relationship.getName().isBlank()) {
                throw new IllegalArgumentException("Relationship names must not be blank");
            }
            if (relationship.getRelationshipType() == null) {
                throw new IllegalArgumentException("Relationship type is required: " + relationship.getName());
            }
            if (relationship.getFromTable() == null || relationship.getFromTable().isBlank()
                || relationship.getFromColumn() == null || relationship.getFromColumn().isBlank()
                || relationship.getToTable() == null || relationship.getToTable().isBlank()
                || relationship.getToColumn() == null || relationship.getToColumn().isBlank()) {
                throw new IllegalArgumentException("Relationship endpoints are required: " + relationship.getName());
            }
        }
    }

    private static ColumnEntity resolveColumn(
        Map<String, TableEntity> tablesByName,
        String tableName,
        String columnName,
        String endpoint
    ) {
        TableEntity table = tablesByName.get(tableName);
        if (table == null) {
            throw new IllegalArgumentException(
                "Relationship " + endpoint + " table not found: " + tableName
            );
        }
        return table.getColumns().stream()
            .filter(column -> column.getName().equals(columnName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException(
                "Relationship " + endpoint + " column not found: " + tableName + "." + columnName
            ));
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
