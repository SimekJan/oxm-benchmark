package cz.cuni.mff.simekjan.application.relationship;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Defines persistence operations for dataset relationships.
 */
public interface RelationshipRepository extends JpaRepository<RelationshipEntity, UUID> {

    /**
     * Finds relationships with the given source or target column.
     *
     * @param sourceColumnId source column ID to match
     * @param targetColumnId target column ID to match
     */
    List<RelationshipEntity> findBySourceColumnIdOrTargetColumnId(UUID sourceColumnId, UUID targetColumnId);
}
