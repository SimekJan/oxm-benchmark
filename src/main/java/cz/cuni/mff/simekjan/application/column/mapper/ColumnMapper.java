package cz.cuni.mff.simekjan.application.column.mapper;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.model.TableColumn;
import cz.cuni.mff.simekjan.model.TableColumnBase;
import cz.cuni.mff.simekjan.model.TableColumnBaseOneOf;
import cz.cuni.mff.simekjan.model.TableColumnBaseOneOf1;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Defines mappings between column entities and API models.
 */
@Mapper(componentModel = "spring")
public interface ColumnMapper {

    @Mapping(target = "tableId", source = "table.id")
    TableColumn toTableColumn(ColumnEntity column);

    @Mapping(target = "tableId", source = "table.id")
    TableColumnBaseOneOf1 toUnusedTableColumnBase(ColumnEntity column);

    @Mapping(target = "tableId", source = "table.id")
    TableColumnBaseOneOf toUsedTableColumnBase(ColumnEntity column);

    default TableColumnBase toTableColumnBase(ColumnEntity column) {
        return column.isUnused()
            ? toUnusedTableColumnBase(column)
            : toUsedTableColumnBase(column);
    }

}
