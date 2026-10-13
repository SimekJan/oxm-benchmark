package cz.cuni.mff.simekjan.relational.northwind.helper;

import cz.cuni.mff.simekjan.relational.HibernateSessionManager;
import cz.cuni.mff.simekjan.relational.northwind.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.util.Collection;

public class PopulateWithDummyData {

    public static void main(String[] args) {

        Session session = HibernateSessionManager.getSession();
        Transaction tx = session.beginTransaction();

        // ---------- SUPPLIERS ----------

        Supplier alpha = new Supplier(1L, "Alpha", "Prague");
        Supplier beta = new Supplier(2L, "Beta", "Brno");
        Supplier gamma = new Supplier(3L, "Gamma", "Ostrava");
        Supplier delta = new Supplier(4L, "Delta", "Plzen");
        Supplier epsilon = new Supplier(5L, "Epsilon", "Liberec");

        alpha.addSuppliesTo(beta);
        alpha.addSuppliesTo(gamma);

        beta.addSuppliesTo(delta);
        beta.addSuppliesTo(gamma);
        beta.addSuppliesTo(epsilon);

        gamma.addSuppliesTo(epsilon);
        gamma.addSuppliesTo(delta);

        delta.addSuppliesTo(gamma);


        // ---------- PRODUCTS ----------

        Product p1 = new Product(1L, "S-Bolt", 10D, "cat1");
        Product p2 = new Product(2L, "Nut", 15D, "cat2");
        Product p3 = new Product(3L, "Screw", 9D, "cat1");
        Product p4 = new Product(4L, "Steel Plate", 12D, "cat2");
        Product p5 = new Product(5L, "Gear", 12D, "cat2");
        Product p6 = new Product(6L, "Valve", 16D, "cat3");

        alpha.addProduct(p1);
        alpha.addProduct(p2);

        beta.addProduct(p3);
        gamma.addProduct(p4);
        delta.addProduct(p5);
        epsilon.addProduct(p6);


        // ---------- EMPLOYEES ----------

        Employee ceo = new Employee(1L, "Alice", "CEO", LocalDate.of(1985, 1, 1), LocalDate.of(2020, 3, 15), "Karlovy Vary");
        Employee manager1 = new Employee(2L, "Bob", "Manager", LocalDate.of(1990, 1, 1), LocalDate.of(2020, 4, 15), "Prague");
        Employee manager2 = new Employee(3L, "Carol", "Manager", LocalDate.of(1992, 1, 1), LocalDate.of(2018, 3, 4), "Brno");
        Employee worker1 = new Employee(4L, "David", "Worker", LocalDate.of(1994, 1, 1), LocalDate.of(2005, 7, 28), "Liberec");
        Employee worker2 = new Employee(5L, "Eva", "Worker", LocalDate.of(1995, 1, 1), LocalDate.of(2008, 4, 26), "Ostrava");
        Employee worker3 = new Employee(6L, "Steve", "Worker", LocalDate.of(2001, 1, 1), LocalDate.of(2024, 1, 1), "Liberec");

        ceo.addSubordinate(manager1);
        ceo.addSubordinate(manager2);

        manager1.addSubordinate(worker1);
        manager2.addSubordinate(worker2);
        manager2.addSubordinate(worker3);


        // ---------- CUSTOMERS ----------

        Customer c1 = new Customer(1L, "A", "Karlovy Vary");
        Customer c2 = new Customer(2L, "B", "Prague");
        Customer c3 = new Customer(3L, "C", "Olomouc");
        Customer c4 = new Customer(4L, "D", "Ostrava");
        Customer c5 = new Customer(5L, "E", "Brno");
        Customer c6 = new Customer(6L, "F", "Prague");

        // ---------- ORDERS ----------

        Order o1 = new Order(1L, LocalDate.of(2025, 1, 1));
        Order o2 = new Order(2L, LocalDate.of(2025, 10, 11));
        Order o3 = new Order(3L, LocalDate.of(2025, 8, 21));
        Order o4 = new Order(4L, LocalDate.of(2025, 9, 15));
        Order o5 = new Order(5L, LocalDate.of(2025, 7, 4));
        Order o6 = new Order(6L, LocalDate.of(2025, 12, 9));

        c1.addOrder(o1);
        c2.addOrder(o2);
        c3.addOrder(o3);
        c4.addOrder(o4);
        c5.addOrder(o5);
        c5.addOrder(o6);

        manager1.addOrder(o1);
        manager1.addOrder(o2);

        manager2.addOrder(o3);

        worker1.addOrder(o4);
        worker1.addOrder(o6);

        worker2.addOrder(o5);

        o1.addProduct(p1);
        o1.addProduct(p3);

        o2.addProduct(p2);
        o2.addProduct(p4);

        o3.addProduct(p5);

        o4.addProduct(p6);
        o4.addProduct(p1);

        o5.addProduct(p2);
        o5.addProduct(p3);


        // ---------- PERSIST ----------

        session.persist(ceo);
        session.persist(manager1);
        session.persist(manager2);
        session.persist(worker1);
        session.persist(worker2);
        session.persist(worker3);

        session.persist(alpha);
        session.persist(beta);
        session.persist(gamma);
        session.persist(delta);
        session.persist(epsilon);

        session.persist(p1);
        session.persist(p2);
        session.persist(p3);
        session.persist(p4);
        session.persist(p5);
        session.persist(p6);

        session.persist(c1);
        session.persist(c2);
        session.persist(c3);
        session.persist(c4);
        session.persist(c5);
        session.persist(c6);

        session.persist(o1);
        session.persist(o2);
        session.persist(o3);
        session.persist(o4);
        session.persist(o5);
        session.persist(o6);

        tx.commit();

        // ---------- VERIFY ----------

        Collection<Supplier> suppliers = session.createQuery("from Suppliers", Supplier.class).list();
        suppliers.forEach(System.out::println);

        Collection<Employee> employees = session.createQuery("from Employees", Employee.class).list();
        employees.forEach(System.out::println);

        Collection<Order> orders = session.createQuery("from Orders", Order.class).list();
        orders.forEach(System.out::println);

        Collection<Product> products = session.createQuery("from Products", Product.class).list();
        products.forEach(System.out::println);

        Collection<Customer> customers = session.createQuery("from Customers", Customer.class).list();
        customers.forEach(System.out::println);

        session.close();

    }
}