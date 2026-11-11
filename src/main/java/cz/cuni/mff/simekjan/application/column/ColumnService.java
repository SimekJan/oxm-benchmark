package cz.cuni.mff.simekjan.application.column;

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

    @Transactional
    public ColumnEntity createColumn(TableEntity table, DatasetSchemaTablesInnerColumnsInner columnSchema) {
        ColumnEntity column = new ColumnEntity();
        column.setId(UUID.randomUUID());
        column.setName(columnSchema.getName());
        column.setDatatype(columnSchema.getDatatype().getValue());
        column.setIndexed(columnSchema.getIndexed());
        column.setPartOfKey(columnSchema.getIsKey());
        column.setOptional(columnSchema.getOptional());
        column.setTable(table);

        ColumnEntity savedColumn = columnRepository.save(column);
        table.getColumns().add(savedColumn);
        return savedColumn;
    }

    @Transactional
    public void changeUnusedColumnVisibility(UUID columnId, PatchColumnVisibilityCommand command) {
    }

    public TableColumn getColumn(UUID columnId) {
        return null;
    }

    @Transactional
    public void updateColumnRelationships(UUID columnId, UpdateTableColumnRelationshipsCommand command) {
    }

    @Transactional
    public void updateColumnValues(UUID columnId, UpdateTableColumnValuesCommand command) {
    }
}
