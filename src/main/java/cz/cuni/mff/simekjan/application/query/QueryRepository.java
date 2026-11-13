package cz.cuni.mff.simekjan.application.query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Defines persistence operations for queries.
 */
public interface QueryRepository extends JpaRepository<QueryEntity, UUID> {

    List<QueryEntity> findAllByDataset_IdOrderByNameAsc(UUID datasetId);
}
