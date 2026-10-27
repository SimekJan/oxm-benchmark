package cz.cuni.mff.simekjan.application.dataset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface QueryRepository extends JpaRepository<QueryEntity, UUID> {

    List<QueryEntity> findAllByDataset_IdOrderByNameAsc(String datasetId);
}
