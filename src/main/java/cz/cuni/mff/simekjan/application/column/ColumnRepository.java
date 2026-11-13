package cz.cuni.mff.simekjan.application.column;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * Defines persistence operations for dataset columns.
 */
public interface ColumnRepository extends JpaRepository<ColumnEntity, UUID> {
}
