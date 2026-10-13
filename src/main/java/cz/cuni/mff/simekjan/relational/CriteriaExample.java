package cz.cuni.mff.simekjan.relational;

import javax.persistence.criteria.*;
import java.util.List;

import cz.cuni.mff.simekjan.relational.northwind.Customer;
import cz.cuni.mff.simekjan.relational.northwind.Order;

import org.hibernate.cfg.Configuration;

import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityManager;

public class CriteriaExample {
    public static void main(String[] args) {

        Configuration cfg = new Configuration();
        cfg.configure();
        EntityManagerFactory emf = cfg.buildSessionFactory().unwrap(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);

        Root<Customer> customer = query.from(Customer.class);
        Join<Customer, Order> orders = customer.join("orders", JoinType.LEFT);

        query.multiselect(
                customer.get("customerId"),
                customer.get("companyName"),
                cb.count(orders.get("orderId"))
            )
            .groupBy(customer.get("customerId"), customer.get("companyName"))
            .orderBy(cb.desc(cb.count(orders.get("orderId"))));

        List<Object[]> results = em.createQuery(query).getResultList();

        for (Object[] row : results) {
            System.out.println(row[0] + " - " + row[1] + ": " + row[2] + " orders");
        }

        em.close();
        emf.close();
    }
}
