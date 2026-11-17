package cz.cuni.mff.simekjan.application.query.mapper;

import cz.cuni.mff.simekjan.application.column.mapper.ColumnMapper;
import cz.cuni.mff.simekjan.application.query.QueryEntity;
import cz.cuni.mff.simekjan.model.QueryDetail;
import cz.cuni.mff.simekjan.model.QueryInfo;
import org.mapstruct.Mapper;

/**
 * Defines mappings between query entities and API models.
 */
@Mapper(componentModel = "spring", uses = ColumnMapper.class)
public interface QueryMapper {

    QueryDetail toQueryDetail(QueryEntity query);

    QueryInfo toQueryInfo(QueryEntity query);
}
