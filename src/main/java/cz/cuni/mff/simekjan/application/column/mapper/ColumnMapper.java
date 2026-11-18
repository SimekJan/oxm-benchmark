package cz.cuni.mff.simekjan.application.column.mapper;

import cz.cuni.mff.simekjan.application.column.ColumnEntity;
import cz.cuni.mff.simekjan.model.TableColumn;
import cz.cuni.mff.simekjan.model.TableColumnBase;
import cz.cuni.mff.simekjan.model.TableColumnBaseOneOf;
import cz.cuni.mff.simekjan.model.TableColumnBaseOneOf1;
import cz.cuni.mff.simekjan.model.TableColumnValuesInfo;
import cz.cuni.mff.simekjan.model.ValueDistributionInner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;

/**
 * Defines mappings between column entities and API models.
 */
@Mapper(componentModel = "spring")
public interface ColumnMapper {

    @Mapping(target = "tableId", source = "table.id")
    @Mapping(target = "valuesInfo", expression = "java(toValuesInfo(column))")
    TableColumn toTableColumn(ColumnEntity column);

    @Mapping(target = "tableId", source = "table.id")
    TableColumnBaseOneOf1 toUnusedTableColumnBase(ColumnEntity column);

    @Mapping(target = "tableId", source = "table.id")
    TableColumnBaseOneOf toUsedTableColumnBase(ColumnEntity column);

    default TableColumnValuesInfo toValuesInfo(ColumnEntity column) {
        TableColumnValuesInfo valuesInfo = new TableColumnValuesInfo();
        valuesInfo.setNumberOfValues(column.getTable().getNumberOfRows());
        valuesInfo.setNumberOfUniqueValues(column.getNumberOfUniqueValues());
        valuesInfo.setIndexed(column.isIndexed());
        valuesInfo.setUniqueValues(column.isUniqueValues());
        valuesInfo.setValueDistribution(new ArrayList<>());
        column.getValueDistribution().forEach((value, count) ->
            valuesInfo.addValueDistributionItem(new ValueDistributionInner().value(value).count(count))
        );
        return valuesInfo;
    }

    default TableColumnBase toTableColumnBase(ColumnEntity column) {
        if (column == null) {
            return null;
        }
        return column.isUnused()
            ? toUnusedTableColumnBase(column)
            : toUsedTableColumnBase(column);
    }

}
