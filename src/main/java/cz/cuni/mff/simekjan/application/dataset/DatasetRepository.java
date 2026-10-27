package cz.cuni.mff.simekjan.application.dataset;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DatasetRepository extends JpaRepository<DatasetEntity, String> {

    List<DatasetEntity> findAllByOrderByNameAsc();
}
