package cz.cuni.mff.simekjan.application.table;

import cz.cuni.mff.simekjan.application.column.ColumnService;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.Table;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInner;
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
    private final ColumnService columnService;

    public Table getTable(UUID tableId) {
        return null;
    }

    @Transactional
    public TableEntity createTable(DatasetEntity dataset, DatasetSchemaTablesInner tableSchema) {
        TableEntity table = new TableEntity();
        table.setId(UUID.randomUUID());
        table.setName(tableSchema.getName());
        table.setDataset(dataset);

        TableEntity savedTable = tableRepository.save(table);
        dataset.getTables().add(savedTable);
        tableSchema.getColumns().forEach(columnSchema -> columnService.createColumn(savedTable, columnSchema));
        return savedTable;
    }

    @Transactional
    public void updateTableSize(UUID tableId, UpdateTableSizeCommand command) {
    }
}
