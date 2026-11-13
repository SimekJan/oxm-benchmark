package cz.cuni.mff.simekjan.application.table;

import cz.cuni.mff.simekjan.api.TableApi;
import cz.cuni.mff.simekjan.model.Table;
import cz.cuni.mff.simekjan.model.UpdateTableSizeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Implements API endpoints for working with dataset tables.
 */
@RequiredArgsConstructor
@RestController
public class TableController implements TableApi {

    private final TableService tableService;

    @Override
    public ResponseEntity<Table> getTable(UUID tableId) {
        try {
            return ResponseEntity.ok(tableService.getTable(tableId));
        } catch (TableService.TableNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> updateTableSize(UUID tableId, UpdateTableSizeCommand updateTableSizeCommand) {
        try {
            tableService.updateTableSize(tableId, updateTableSizeCommand);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (TableService.TableNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}
