package cz.cuni.mff.simekjan.application.table;

import cz.cuni.mff.simekjan.api.TableApi;
import cz.cuni.mff.simekjan.model.Table;
import cz.cuni.mff.simekjan.model.UpdateTableSizeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
public class TableController implements TableApi {

    private final TableService tableService;

    @Override
    public ResponseEntity<Table> getTable(UUID tableId) {
        return null;
    }

    @Override
    public ResponseEntity<Void> updateTableSize(UUID tableId, UpdateTableSizeCommand updateTableSizeCommand) {
        return null;
    }
}
