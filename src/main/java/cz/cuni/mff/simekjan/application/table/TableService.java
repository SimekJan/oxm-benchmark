package cz.cuni.mff.simekjan.application.table;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.Table;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInner;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInnerColumnsInner;
import cz.cuni.mff.simekjan.model.UpdateTableSizeCommand;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TableService {

    private final TableRepository tableRepository;
    private final EntityManager entityManager;

    public Table getTable(UUID tableId) {
        return null;
    }

    @Transactional
    public TableEntity createTable(DatasetEntity dataset, DatasetSchemaTablesInner tableSchema) {
        TableEntity table = new TableEntity();
        table.setId(UUID.randomUUID());
        table.setName(tableSchema.getName());
        table.setDataset(dataset);

        for (DatasetSchemaTablesInnerColumnsInner columnSchema : tableSchema.getColumns()) {
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
        entityManager.persist(table);
        return table;
    }

    @Transactional
    public void updateTableSize(UUID tableId, UpdateTableSizeCommand command) {
    }
}
