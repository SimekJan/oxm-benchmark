package cz.cuni.mff.simekjan.application.table;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.application.column.ColumnService;
import cz.cuni.mff.simekjan.application.dataset.DatasetEntity;
import cz.cuni.mff.simekjan.model.Table;
import cz.cuni.mff.simekjan.model.DatasetSchemaTablesInner;
import cz.cuni.mff.simekjan.model.TableColumnBase;
import cz.cuni.mff.simekjan.model.TableColumnBaseOneOf;
import cz.cuni.mff.simekjan.model.TableColumnBaseOneOf1;
import cz.cuni.mff.simekjan.model.UpdateTableSizeCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TableService {

    private final TableRepository tableRepository;
    private final ColumnService columnService;

    public Table getTable(UUID tableId) {
        TableEntity table = getTableEntity(tableId);
        Table response = new Table(table.getId(), table.getDataset().getId(), table.getName());
        if (table.getNumberOfRows() != null) {
            response.setNumberOfRows(BigDecimal.valueOf(table.getNumberOfRows()));
        }
        response.setColumns(table.getColumns().stream()
            .map(TableService::toTableColumnBase)
            .toList());
        return response;
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

    private static TableColumnBase toTableColumnBase(ColumnEntity column) {
        boolean unused = Boolean.TRUE.equals(column.getUnused());
        if (unused) {
            TableColumnBaseOneOf1 response = new TableColumnBaseOneOf1();
            response.setUnused(true);
            response.setHidden(Boolean.TRUE.equals(column.getHidden()));
            response.setId(column.getId());
            response.setTableId(column.getTable().getId());
            response.setName(column.getName());
            response.setType(TableColumnBaseOneOf1.TypeEnum.fromValue(column.getDatatype()));
            return response;
        }

        TableColumnBaseOneOf response = new TableColumnBaseOneOf();
        response.setUnused(false);
        response.setId(column.getId());
        response.setTableId(column.getTable().getId());
        response.setName(column.getName());
        response.setType(TableColumnBaseOneOf.TypeEnum.fromValue(column.getDatatype()));
        return response;
    }

    public static class TableNotFoundException extends RuntimeException {
        public TableNotFoundException(UUID tableId) {
            super("Table not found: " + tableId);
        }
    }
}
