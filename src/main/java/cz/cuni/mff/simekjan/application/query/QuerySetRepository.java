package cz.cuni.mff.simekjan.application.query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Defines persistence operations for query sets.
 */
public interface QuerySetRepository extends JpaRepository<QuerySetEntity, UUID> {

    List<QuerySetEntity> findAllByDataset_IdOrderByNameAsc(UUID datasetId);
}
