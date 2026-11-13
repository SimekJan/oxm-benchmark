package cz.cuni.mff.simekjan.application.column;

import cz.cuni.mff.simekjan.api.ColumnApi;
import cz.cuni.mff.simekjan.model.PatchColumnVisibilityCommand;
import cz.cuni.mff.simekjan.model.TableColumn;
import cz.cuni.mff.simekjan.model.UpdateTableColumnRelationshipsCommand;
import cz.cuni.mff.simekjan.model.UpdateTableColumnValuesCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Implements API endpoints for working with dataset columns.
 */
@RequiredArgsConstructor
@RestController
public class ColumnController implements ColumnApi {

    private final ColumnService columnService;

    @Override
    public ResponseEntity<Void> changeUnusedColumnVisibility(
        UUID columnId,
        PatchColumnVisibilityCommand patchColumnVisibilityCommand
    ) {
        try {
            columnService.changeUnusedColumnVisibility(columnId, patchColumnVisibilityCommand);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (ColumnService.ColumnNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<TableColumn> getColumn(UUID columnId) {
        try {
            return ResponseEntity.ok(columnService.getColumn(columnId));
        } catch (ColumnService.ColumnNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> updateColumnRelationships(
        UUID columnId,
        UpdateTableColumnRelationshipsCommand updateTableColumnRelationshipsCommand
    ) {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateColumnValues(
        UUID columnId,
        UpdateTableColumnValuesCommand updateTableColumnValuesCommand
    ) {
        return null;
    }
}
