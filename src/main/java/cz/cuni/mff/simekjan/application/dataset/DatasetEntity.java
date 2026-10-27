package cz.cuni.mff.simekjan.application.dataset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Getter
@Entity
@Table(name = "datasets")
public class DatasetEntity {

    @Id
    @Column(nullable = false, length = 100)
    private String id;

    @Column(nullable = false)
    private String name;

    private String description;

    protected DatasetEntity() {
    }

}
