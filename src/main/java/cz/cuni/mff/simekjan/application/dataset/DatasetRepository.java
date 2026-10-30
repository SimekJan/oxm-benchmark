package cz.cuni.mff.simekjan.application.dataset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DatasetRepository extends JpaRepository<DatasetEntity, UUID> {

    List<DatasetEntity> findAllByOrderByNameAsc();
}
