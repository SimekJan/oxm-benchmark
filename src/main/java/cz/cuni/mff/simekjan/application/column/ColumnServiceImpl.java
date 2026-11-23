package cz.cuni.mff.simekjan.application.column;

import cz.cuni.mff.simekjan.application.column.mapper.ColumnMapper;
import cz.cuni.mff.simekjan.application.operation.OperationService;
import cz.cuni.mff.simekjan.application.relationship.RelationshipService;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInnerColumnsInner;
import cz.cuni.mff.simekjan.model.PatchColumnVisibilityCommand;
import cz.cuni.mff.simekjan.model.TableColumn;
import cz.cuni.mff.simekjan.model.UpdateTableColumnValuesCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Implements business logic for operating with dataset columns.
 */
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ColumnServiceImpl implements ColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnMapper columnMapper;
    private final OperationService operationService;
    private final RelationshipService relationshipService;

    @Transactional
    public void createColumn(TableEntity table, DatasetSchemaTablesInnerColumnsInner columnSchema) {
        ColumnEntity column = new ColumnEntity();
        column.setId(UUID.randomUUID());
        column.setName(columnSchema.getName());
        column.setDatatype(columnSchema.getDatatype());
        column.setIndexed(Boolean.TRUE.equals(columnSchema.getIndexed()));
        column.setPartOfKey(Boolean.TRUE.equals(columnSchema.getIsKey()));
        column.setOptional(Boolean.TRUE.equals(columnSchema.getOptional()));
        column.setTable(table);

        ColumnEntity savedColumn = columnRepository.save(column);
        table.getColumns().add(savedColumn);
    }

    @Transactional
    public void changeUnusedColumnVisibility(UUID columnId, PatchColumnVisibilityCommand command) {
        if (command == null || command.getHide() == null) {
            throw new IllegalArgumentException("Column visibility command and hide value are required");
        }

        ColumnEntity column = getColumnEntity(columnId);
        if (!column.isUnused()) {
            throw new IllegalArgumentException("Only unused columns can be hidden or shown");
        }

        column.setHidden(command.getHide());
        columnRepository.save(column);
    }

    public TableColumn getColumn(UUID columnId) {
        ColumnEntity column = getColumnEntity(columnId);
        TableColumn response = columnMapper.toTableColumn(column);
        response.setOperations(operationService.getOperationsForColumn(columnId));
        response.setRelationshipsInfo(relationshipService.getRelationshipsForColumn(columnId));
        return response;
    }

    @Transactional
    public void updateColumnValues(UUID columnId, UpdateTableColumnValuesCommand command) {
        ColumnEntity column = getColumnEntity(columnId);
        if (command.getUniqueValues() != null) {
            column.setUniqueValues(command.getUniqueValues());
        }
        if (command.getIndexed() != null) {
            column.setIndexed(command.getIndexed());
        }
        if (command.getNumberOfUniqueValues() != null) {
            column.setNumberOfUniqueValues(command.getNumberOfUniqueValues());
        }
        if (command.getPartOfKey() != null) {
            column.setPartOfKey(command.getPartOfKey());
        }
        if (command.getOptional() != null) {
            column.setOptional(command.getOptional());
        }
        if (command.getDistribution() != null) {
            column.setDistribution(command.getDistribution());
        }
        if (command.getValueDistribution() != null) {
            command.getValueDistribution().forEach(valueDistribution ->
                column.getValueDistribution().put(valueDistribution.getValue(), valueDistribution.getCount())
            );
        }
    }

    private ColumnEntity getColumnEntity(UUID columnId) {
        return columnRepository.findById(columnId)
            .orElseThrow(() -> new ColumnNotFoundException(columnId));
    }

}
