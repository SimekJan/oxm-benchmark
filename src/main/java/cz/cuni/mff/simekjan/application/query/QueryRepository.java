package cz.cuni.mff.simekjan.application.query;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Defines persistence operations for queries.
 */
public interface QueryRepository extends JpaRepository<QueryEntity, UUID> {

    List<QueryEntity> findAllByDataset_IdOrderByNameAsc(UUID datasetId);

    Optional<QueryEntity> findByIdAndDataset_Id(UUID id, UUID datasetId);
}
