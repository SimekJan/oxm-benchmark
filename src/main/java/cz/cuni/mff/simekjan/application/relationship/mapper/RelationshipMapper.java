package cz.cuni.mff.simekjan.application.relationship.mapper;

import cz.cuni.mff.simekjan.application.relationship.RelationshipEntity;
import cz.cuni.mff.simekjan.model.RelationshipDetail;
import cz.cuni.mff.simekjan.model.RelationshipInfo;
import cz.cuni.mff.simekjan.model.ValueDistributionInner;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;

/**
 * Defines mappings between relationship entities and API models.
 */
@Mapper(componentModel = "spring")
public interface RelationshipMapper {

    /**
     * Maps a relationship to brief information.
     *
     * @param relationship relationship entity to map
     */
    @Mapping(target = "sourceColumnId", source = "sourceColumn.id")
    @Mapping(target = "targetColumnId", source = "targetColumn.id")
    RelationshipInfo toRelationshipInfo(RelationshipEntity relationship);

    /**
     * Maps a relationship to its detailed representation.
     *
     * @param relationship relationship entity to map
     */
    @Mapping(target = "sourceColumnId", source = "sourceColumn.id")
    @Mapping(target = "targetColumnId", source = "targetColumn.id")
    RelationshipDetail toRelationshipDetail(RelationshipEntity relationship);

    /**
     * Maps stored distribution entries to API values.
     *
     * @param distribution stored values and counts
     */
    default List<ValueDistributionInner> toValueDistribution(Map<String, Integer> distribution) {
        return distribution.entrySet().stream()
            .map(entry -> new ValueDistributionInner().value(entry.getKey()).count(entry.getValue()))
            .toList();
    }
}
