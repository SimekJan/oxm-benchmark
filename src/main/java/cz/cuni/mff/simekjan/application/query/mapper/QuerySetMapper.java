package cz.cuni.mff.simekjan.application.query.mapper;

import cz.cuni.mff.simekjan.application.query.QuerySetEntity;
import cz.cuni.mff.simekjan.model.QueryInfo;
import cz.cuni.mff.simekjan.model.QuerySetDetail;
import cz.cuni.mff.simekjan.model.QuerySetInfo;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.Comparator;

@Mapper(componentModel = "spring", uses = QueryMapper.class)
public interface QuerySetMapper {

    QuerySetInfo toQuerySetInfo(QuerySetEntity querySet);

    QuerySetDetail toQuerySetDetail(QuerySetEntity querySet);

    @AfterMapping
    default void sortQueries(@MappingTarget QuerySetDetail detail) {
        detail.getQueries().sort(Comparator.comparing(QueryInfo::getName).thenComparing(QueryInfo::getId));
    }
}
