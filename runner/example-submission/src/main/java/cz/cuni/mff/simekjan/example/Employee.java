package cz.cuni.mff.simekjan.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/** Placeholder entity for the Hibernate example submission. */
@Entity
public class Employee {
    @Id
    private Long id;

    /** Creates an entity instance for Hibernate. */
    protected Employee() {
    }
}
