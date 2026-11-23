package cz.cuni.mff.simekjan.application.relationship;

import cz.cuni.mff.simekjan.application.relationship.mapper.RelationshipMapper;
import cz.cuni.mff.simekjan.model.RelationshipDetail;
import cz.cuni.mff.simekjan.model.RelationshipInfo;
import cz.cuni.mff.simekjan.model.UpdateRelationshipCommand;
import cz.cuni.mff.simekjan.model.ValueDistributionInner;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implements business logic for dataset relationships.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RelationshipServiceImpl implements RelationshipService {
    private final RelationshipRepository relationshipRepository;
    private final RelationshipMapper relationshipMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public RelationshipDetail getRelationship(UUID relationshipId) {
        return relationshipMapper.toRelationshipDetail(getRelationshipEntity(relationshipId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<RelationshipInfo> getRelationshipsForColumn(UUID columnId) {
        return relationshipRepository.findBySourceColumnIdOrTargetColumnId(columnId, columnId).stream()
            .map(relationshipMapper::toRelationshipInfo)
            .toList();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateRelationship(UUID relationshipId, UpdateRelationshipCommand command) {
        if (command == null || command.getValueDistribution() == null) {
            throw new IllegalArgumentException("Relationship command and value distribution must not be null");
        }
        // TODO: distributions does not work with many-to-many relationships in current design
        Map<String, Integer> distribution = createDistribution(command);
        RelationshipEntity relationship = getRelationshipEntity(relationshipId);
        relationship.setDistribution(command.getDistribution());
        relationship.getValueDistribution().clear();
        relationship.getValueDistribution().putAll(distribution);
    }

    private static @NonNull Map<String, Integer> createDistribution(UpdateRelationshipCommand command) {
        Map<String, Integer> distribution = new HashMap<>();
        for (ValueDistributionInner item : command.getValueDistribution()) {
            if (item == null || item.getValue() == null || item.getCount() == null || item.getCount() < 0) {
                throw new IllegalArgumentException("Distribution entries require a value and a non-negative count");
            }
            if (distribution.putIfAbsent(item.getValue(), item.getCount()) != null) {
                throw new IllegalArgumentException("Distribution values must be unique");
            }
        }
        return distribution;
    }

    private RelationshipEntity getRelationshipEntity(UUID relationshipId) {
        return relationshipRepository.findById(relationshipId)
            .orElseThrow(() -> new RelationshipNotFoundException(relationshipId));
    }
}
