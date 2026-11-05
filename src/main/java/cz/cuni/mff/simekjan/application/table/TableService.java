package cz.cuni.mff.simekjan.application.table;

import cz.cuni.mff.simekjan.model.Table;
import cz.cuni.mff.simekjan.model.UpdateTableSizeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TableService {

    private final TableRepository tableRepository;

    public Table getTable(UUID tableId) {
        return null;
    }

    @Transactional
    public void updateTableSize(UUID tableId, UpdateTableSizeCommand command) {
    }
}
