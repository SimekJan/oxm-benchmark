package cz.cuni.mff.simekjan.runner.registration;

import cz.cuni.mff.simekjan.example.Employee;
import cz.cuni.mff.simekjan.example.EmployeeQuery;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;

import java.util.List;

/**
 * Entity and query classes compiled into the Hibernate example.
 */
public final class HibernateRegistration {

    private HibernateRegistration() {
    }

    /**
     * @return registered Hibernate entity classes
     */
    public static List<Class<?>> entities() {
        return List.of(Employee.class);
    }

    /**
     * @return registered Hibernate query classes
     */
    public static List<Class<? extends HibernateQuery<?>>> queries() {
        return List.of(EmployeeQuery.class);
    }
}
