package cz.cuni.mff.simekjan.application.table;

import cz.cuni.mff.simekjan.application.column.ColumnService;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.application.table.mapper.TableMapper;
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
public class TableServiceImpl implements TableService {

    private final TableRepository tableRepository;
    private final ColumnService columnService;
    private final TableMapper tableMapper;

    public Table getTable(UUID tableId) {
        TableEntity table = getTableEntity(tableId);
        return tableMapper.toTable(table);
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
        if (command == null || command.getSize() == null || command.getSize() < 0) {
            throw new IllegalArgumentException("Table size must be a non-negative integer");
        }
        TableEntity table = getTableEntity(tableId);
        table.setNumberOfRows(command.getSize());
        tableRepository.save(table);
    }

    private TableEntity getTableEntity(UUID tableId) {
        return tableRepository.findById(tableId)
            .orElseThrow(() -> new TableNotFoundException(tableId));
    }

}
