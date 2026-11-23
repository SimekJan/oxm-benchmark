package cz.cuni.mff.simekjan.application.relationship;

import cz.cuni.mff.simekjan.model.RelationshipDetail;
import cz.cuni.mff.simekjan.model.RelationshipInfo;
import cz.cuni.mff.simekjan.model.UpdateRelationshipCommand;

import java.util.List;
import java.util.UUID;

/**
 * Defines business logic for dataset relationships.
 */
public interface RelationshipService {

    /**
     * Gets the details of a relationship by ID.
     *
     * @param relationshipId relationship ID
     */
    RelationshipDetail getRelationship(UUID relationshipId);

    /**
     * Gets brief information about relationships involving a column.
     *
     * @param columnId source or target column ID
     */
    List<RelationshipInfo> getRelationshipsForColumn(UUID columnId);

    /**
     * Replaces the distribution settings of a relationship.
     *
     * @param relationshipId relationship ID
     * @param command replacement distribution settings
     */
    void updateRelationship(UUID relationshipId, UpdateRelationshipCommand command);

    /**
     * Indicates that a relationship does not exist.
     */
    class RelationshipNotFoundException extends RuntimeException {
        /**
         * Creates an exception for the missing relationship.
         *
         * @param relationshipId missing relationship ID
         */
        public RelationshipNotFoundException(UUID relationshipId) {
            super("Relationship not found: " + relationshipId);
        }
    }
}
