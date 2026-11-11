package cz.cuni.mff.simekjan.application.table.mapper;

import cz.cuni.mff.simekjan.application.column.mapper.ColumnMapper;
import cz.cuni.mff.simekjan.application.table.TableEntity;
import cz.cuni.mff.simekjan.model.Table;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ColumnMapper.class)
public interface TableMapper {

    @Mapping(target = "datasetId", source = "dataset.id")
    Table toTable(TableEntity table);
}
