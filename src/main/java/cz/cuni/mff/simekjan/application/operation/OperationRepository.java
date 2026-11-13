package cz.cuni.mff.simekjan.application.operation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Defines persistence operations for query operations.
 */
public interface OperationRepository extends JpaRepository<OperationEntity, UUID> {

    List<OperationEntity> findAllByQuery_IdAndParentOperationIsNull(UUID queryId);

    List<OperationEntity> findAllByColumn_IdOrColumn1_IdOrColumn2_Id(
        UUID columnId,
        UUID column1Id,
        UUID column2Id
    );
}
