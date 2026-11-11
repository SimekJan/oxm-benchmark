package cz.cuni.mff.simekjan.application.column;

import cz.cuni.mff.simekjan.application.column.mapper.ColumnMapper;
import cz.cuni.mff.simekjan.application.operation.OperationService;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInnerColumnsInner;
import cz.cuni.mff.simekjan.model.PatchColumnVisibilityCommand;
import cz.cuni.mff.simekjan.model.TableColumn;
import cz.cuni.mff.simekjan.model.UpdateTableColumnRelationshipsCommand;
import cz.cuni.mff.simekjan.model.UpdateTableColumnValuesCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ColumnService {

    private final ColumnRepository columnRepository;
    private final ColumnMapper columnMapper;
    private final OperationService operationService;

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
        return response;
    }

    @Transactional
    public void updateColumnRelationships(UUID columnId, UpdateTableColumnRelationshipsCommand command) {
    }

    @Transactional
    public void updateColumnValues(UUID columnId, UpdateTableColumnValuesCommand command) {
    }

    private ColumnEntity getColumnEntity(UUID columnId) {
        return columnRepository.findById(columnId)
            .orElseThrow(() -> new ColumnNotFoundException(columnId));
    }

    public static class ColumnNotFoundException extends RuntimeException {
        public ColumnNotFoundException(UUID columnId) {
            super("Column not found: " + columnId);
        }
    }
}
