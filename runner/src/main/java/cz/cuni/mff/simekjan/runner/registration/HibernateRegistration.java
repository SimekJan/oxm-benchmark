package cz.cuni.mff.simekjan.runner.registration;

import cz.cuni.mff.simekjan.example.northwind.hibernate.entities.*;
import cz.cuni.mff.simekjan.example.northwind.hibernate.queries.*;
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
        return List.of(Customer.class, Employee.class, Order.class, Product.class, Supplier.class);
    }

    /**
     * @return registered Hibernate query classes
     */
    public static List<Class<? extends HibernateQuery<?>>> queries() {
        return List.of(Query01_select_indexed.class, Query02_select_non_indexed.class,
            Query03_range_query_indexed.class, Query04_range_query_non_indexed.class,
            Query07_join_indexed.class, Query08_join_non_indexed.class);
    }
}
