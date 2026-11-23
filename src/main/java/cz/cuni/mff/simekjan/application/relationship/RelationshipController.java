package cz.cuni.mff.simekjan.application.relationship;

import cz.cuni.mff.simekjan.api.RelationshipApi;
import cz.cuni.mff.simekjan.model.RelationshipDetail;
import cz.cuni.mff.simekjan.model.UpdateRelationshipCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class RelationshipController implements RelationshipApi {
    private final RelationshipService relationshipService;

    @Override
    public ResponseEntity<RelationshipDetail> getRelationship(UUID relationshipId) {
        try {
            return ResponseEntity.ok(relationshipService.getRelationship(relationshipId));
        } catch (RelationshipService.RelationshipNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }

    @Override
    public ResponseEntity<Void> updateRelationship(UUID relationshipId, UpdateRelationshipCommand command) {
        try {
            relationshipService.updateRelationship(relationshipId, command);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.badRequest().build();
        } catch (RelationshipService.RelationshipNotFoundException exception) {
            return ResponseEntity.notFound().build();
        }
    }
}
