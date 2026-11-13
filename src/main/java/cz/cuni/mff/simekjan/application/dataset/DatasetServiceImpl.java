package cz.cuni.mff.simekjan.application.dataset;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.dataset.mapper.DatasetMapper;
import cz.cuni.mff.simekjan.application.query.QueryService;
import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.application.relationship.RelationshipType;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.application.table.TableService;
import cz.cuni.mff.simekjan.model.DatasetDetail;
import cz.cuni.mff.simekjan.model.DatasetInfo;
import cz.cuni.mff.simekjan.model.DatasetRuntimeStatus;
import cz.cuni.mff.simekjan.model.CreateDatasetFromDatabaseCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetFromFileCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetFromSchemaCommand;
import cz.cuni.mff.simekjan.model.CreateDatasetWithoutSchemaCommand;
import cz.cuni.mff.simekjan.model.CreateQuerySetCommand;
import cz.cuni.mff.simekjan.model.QuerySetInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Implements business logic for operating with Datasets.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DatasetServiceImpl implements DatasetService {

    private final DatasetRepository datasetRepository;
    private final QueryService queryService;
    private final TableService tableService;
    private final DatasetMapper datasetMapper;

    public List<DatasetInfo> getDatasets() {
        return datasetRepository.findAll().stream()
            .map(dataset -> {
                DatasetInfo info = new DatasetInfo(dataset.getId(), dataset.getName());
                info.setDescription(dataset.getDescription());
                info.setRuntimeStatus(DatasetInfo.RuntimeStatusEnum.fromValue(dataset.getRuntimeStatus()));
                return info;
            })
            .toList();
    }

    public DatasetEntity getDatasetEntity(UUID datasetId) {
        return datasetRepository.findById(datasetId)
            .orElseThrow(() -> new DatasetNotFoundException(datasetId));
    }

    public DatasetDetail getDataset(UUID datasetId) {
        return datasetMapper.toDatasetDetail(getDatasetEntity(datasetId));
    }

    @Transactional(noRollbackFor = ExecutorUnavailableException.class)
    public UUID createDatasetFromSchema(CreateDatasetFromSchemaCommand command) {
        validateSchema(command);
        UUID datasetId = UUID.randomUUID();

        DatasetEntity dataset = new DatasetEntity();
        dataset.setId(datasetId);
        dataset.setName(command.getName());
        dataset.setDescription(command.getDescription());
        dataset.setSchemaReady(true);
        dataset.setRuntimeStatus("BUILDING");

        dataset = datasetRepository.save(dataset);

        Map<String, TableEntity> tablesByName = new HashMap<>();
        for (var tableSchema : command.getSchema().getTables()) {
            TableEntity table = tableService.createTable(dataset, tableSchema);
            tablesByName.put(tableSchema.getName(), table);
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

        return datasetId;
    }

    public UUID createDatasetWithoutSchema(CreateDatasetWithoutSchemaCommand command) {
        // Set schemaReady to false for datasets without schema
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

    public List<QuerySetInfo> getQuerySets(UUID datasetId) {
        return queryService.getQuerySets(datasetId);
    }

    @Transactional
    public UUID createQuerySet(UUID datasetId, CreateQuerySetCommand command) {
        DatasetEntity dataset = getDatasetEntity(datasetId);
        return queryService.createQuerySet(dataset, command);
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

}
