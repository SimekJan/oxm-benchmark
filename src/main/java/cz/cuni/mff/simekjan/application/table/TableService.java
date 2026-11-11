package cz.cuni.mff.simekjan.application.table;

import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInner;
import cz.cuni.mff.simekjan.model.Table;
import cz.cuni.mff.simekjan.model.UpdateTableSizeCommand;

import java.util.UUID;

public interface TableService {

    Table getTable(UUID tableId);

    TableEntity createTable(DatasetEntity dataset, DatasetSchemaTablesInner tableSchema);

    void updateTableSize(UUID tableId, UpdateTableSizeCommand command);

    class TableNotFoundException extends RuntimeException {
        public TableNotFoundException(UUID tableId) {
            super("Table not found: " + tableId);
        }
    }
}
