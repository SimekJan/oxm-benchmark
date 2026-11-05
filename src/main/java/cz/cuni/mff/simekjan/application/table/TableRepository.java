package cz.cuni.mff.simekjan.application.table;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TableRepository extends JpaRepository<TableEntity, UUID> {
}
