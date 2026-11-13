package cz.cuni.mff.simekjan.application.dataset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Defines persistence operations for Datasets.
 */
public interface DatasetRepository extends JpaRepository<DatasetEntity, UUID> {
}
