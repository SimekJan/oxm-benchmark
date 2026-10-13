package cz.cuni.mff.simekjan.relational.northwind.helper;

import cz.cuni.mff.simekjan.generator.DataProvider;
import cz.cuni.mff.simekjan.relational.HibernateSessionManager;
import cz.cuni.mff.simekjan.relational.northwind.*;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.*;

public class GenerateData {

    private static final int SUPPLIER_COUNT = 1_000;
    private static final int EMPLOYEE_COUNT = 2_000;
    private static final int PRODUCT_COUNT = 5_000;
    private static final int CUSTOMER_COUNT = 10_000;
    private static final int ORDER_COUNT = 20_000;

    public static void main(String[] args) {

        Session session = HibernateSessionManager.getSession();
        Transaction tx = session.beginTransaction();

        DataProvider gen = new DataProvider(1234567890L);

        List<Supplier> suppliers = generateSuppliers(gen);
        List<Product> products = generateProducts(gen, suppliers);
        List<Employee> employees = generateEmployees(gen);
        List<Customer> customers = generateCustomers(gen);
        List<Order> orders = generateOrders(gen);

        linkSuppliers(suppliers, gen);
        linkEmployeeHierarchy(employees, gen);
        linkOrders(customers, employees, products, orders, gen);

        // ---------- PERSIST ----------
        suppliers.forEach(session::persist);
        products.forEach(session::persist);
        employees.forEach(session::persist);
        customers.forEach(session::persist);
        orders.forEach(session::persist);

        tx.commit();
        session.close();
    }

    private static List<Supplier> generateSuppliers(DataProvider gen) {

        List<Supplier> list = new ArrayList<>();

        for (long i = 1; i <= SUPPLIER_COUNT; i++) {
            list.add(new Supplier(i, gen.nextCompanyName(), gen.nextCity()));
        }

        return list;
    }

    private static void linkSuppliers(List<Supplier> suppliers,
                                      DataProvider gen) {

        for (Supplier s : suppliers) {

            int links = gen.nextInt(1, 5);

            for (int i = 0; i < links; i++) {
                Supplier target = suppliers.get(gen.nextInt(0, suppliers.size()));
                if (target != s) {
                    s.addSuppliesTo(target);
                }
            }
        }
    }

    private static List<Product> generateProducts(
        DataProvider gen,
        List<Supplier> suppliers) {

        List<Product> list = new ArrayList<>();

        for (long i = 1; i <= PRODUCT_COUNT; i++) {

            Product p = new Product(
                i,
                gen.nextProductName(),
                gen.nextPrice().doubleValue(),
                gen.nextCategory()
            );

            Supplier s = suppliers.get(gen.nextInt(0, suppliers.size()));
            s.addProduct(p);

            list.add(p);
        }

        return list;
    }

    private static List<Employee> generateEmployees(DataProvider gen) {

        List<Employee> list = new ArrayList<>();

        for (long i = 1; i <= EMPLOYEE_COUNT; i++) {

            list.add(new Employee(
                i,
                gen.nextFirstName(),
                gen.nextLastName(),
                gen.nextBirthDate(),
                gen.nextHireDate(),
                gen.nextCity()
            ));
        }

        return list;
    }

    private static void linkEmployeeHierarchy(List<Employee> employees,
                                              DataProvider gen) {

        Queue<Employee> managers = new LinkedList<>();
        managers.add(employees.get(0)); // CEO

        int i = 1;

        while (i < employees.size()) {

            Employee manager = managers.poll();
            int reports = gen.nextInt(2, 7);

            for (int r = 0; r < reports && i < employees.size(); r++) {

                Employee e = employees.get(i++);
                manager.addSubordinate(e);

                managers.add(e);
            }
        }
    }

    private static List<Customer> generateCustomers(DataProvider gen) {

        List<Customer> list = new ArrayList<>();

        for (long i = 1; i <= CUSTOMER_COUNT; i++) {
            list.add(new Customer(i, gen.nextCompanyName(), gen.nextCity()));
        }

        return list;
    }

    private static List<Order> generateOrders(DataProvider gen) {

        List<Order> list = new ArrayList<>();

        for (long i = 1; i <= ORDER_COUNT; i++) {
            list.add(new Order(i, gen.nextOrderDate()));
        }

        return list;
    }

    private static void linkOrders(List<Customer> customers,
                                   List<Employee> employees,
                                   List<Product> products,
                                   List<Order> orders,
                                   DataProvider gen) {

        for (Order o : orders) {

            Customer c = customers.get(gen.nextInt(0, customers.size()));
            Employee e = employees.get(gen.nextInt(0, employees.size()));

            c.addOrder(o);
            e.addOrder(o);

            int productCount = gen.nextInt(0, 4);

            for (int i = 0; i < productCount; i++) {
                Product p = products.get(gen.nextInt(0, products.size()));
                o.addProduct(p);
            }
        }
    }
}