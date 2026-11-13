package cz.cuni.mff.simekjan.application.column;

import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInnerColumnsInner;
import cz.cuni.mff.simekjan.model.PatchColumnVisibilityCommand;
import cz.cuni.mff.simekjan.model.TableColumn;
import cz.cuni.mff.simekjan.model.UpdateTableColumnRelationshipsCommand;
import cz.cuni.mff.simekjan.model.UpdateTableColumnValuesCommand;

import java.util.UUID;

/**
 * Defines business logic methods to operate with dataset columns.
 */
public interface ColumnService {

    void createColumn(TableEntity table, DatasetSchemaTablesInnerColumnsInner columnSchema);

    void changeUnusedColumnVisibility(UUID columnId, PatchColumnVisibilityCommand command);

    TableColumn getColumn(UUID columnId);

    void updateColumnRelationships(UUID columnId, UpdateTableColumnRelationshipsCommand command);

    void updateColumnValues(UUID columnId, UpdateTableColumnValuesCommand command);

    class ColumnNotFoundException extends RuntimeException {
        public ColumnNotFoundException(UUID columnId) {
            super("Column not found: " + columnId);
        }
    }
}
