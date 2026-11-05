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

@RequiredArgsConstructor
@RestController
public class ColumnController implements ColumnApi {

    private final ColumnService columnService;

    @Override
    public ResponseEntity<Void> changeUnusedColumnVisibility(
        UUID columnId,
        PatchColumnVisibilityCommand patchColumnVisibilityCommand
    ) {
        return null;
    }

    @Override
    public ResponseEntity<TableColumn> getColumn(UUID columnId) {
        return null;
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
