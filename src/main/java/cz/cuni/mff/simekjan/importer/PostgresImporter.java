package cz.cuni.mff.simekjan.importer;

import cz.cuni.mff.simekjan.relational.northwind.*;
import cz.cuni.mff.simekjan.relational.HibernateSessionManager;
import cz.cuni.mff.simekjan.relational.northwind.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Slf4j
public class PostgresImporter {

    private static final int BATCH_SIZE = 2_000;

    private static Session session;

    public static void run() throws IOException {

        session = HibernateSessionManager.getSession();

        Transaction transaction = session.beginTransaction();

        try {
            loadSuppliers();
            linkSuppliers();

            loadCustomers();

            loadEmployees();
            linkEmployees();

            loadProducts();
            linkProductsWithSuppliers();

            loadOrders();
            linkOrdersWithCustomersAndEmployees();
            linkOrdersWithProducts();

            transaction.commit();

        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            session.close();
        }
    }

    private static void loadSuppliers() throws IOException {

        CSVParser parser = CsvParser.getParser("suppliers.csv");

        int count = 0;

        for (CSVRecord record : parser) {

            Supplier supplier = new Supplier(
                Long.parseLong(record.get("supplier_id")),
                record.get("company_name"),
                record.get("city")
            );

            session.persist(supplier);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void linkSuppliers() throws IOException {

        CSVParser parser = CsvParser.getParser("supplier_relationships.csv");

        int count = 0;

        for (CSVRecord record : parser) {

            Long supplierId = Long.parseLong(record.get("supplier_id"));
            Long suppliedToId = Long.parseLong(record.get("supplied_to_id"));

            List<Supplier> suppliers = session.createQuery("""
                    from Suppliers s
                    where s.supplierId in (:supplierId, :suppliedToId)
                    """, Supplier.class)
                .setParameter("supplierId", supplierId)
                .setParameter("suppliedToId", suppliedToId)
                .getResultList();

            if (suppliers.size() != 2) {
                throw new IllegalStateException(
                    "Supplier not found: " +
                        supplierId + " or " + suppliedToId
                );
            }

            Supplier supplier = suppliers.stream()
                .filter(s -> s.getSupplierId().equals(supplierId))
                .findFirst()
                .orElseThrow();

            Supplier suppliedTo = suppliers.stream()
                .filter(s -> s.getSupplierId().equals(suppliedToId))
                .findFirst()
                .orElseThrow();

            supplier.addSuppliesTo(suppliedTo);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void loadCustomers() throws IOException {

        CSVParser parser = CsvParser.getParser("customers.csv");

        int count = 0;

        for (CSVRecord record : parser) {

            Customer customer = new Customer(
                Long.valueOf(record.get("customer_id")),
                record.get("company_name"),
                record.get("city")
            );

            session.persist(customer);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void loadEmployees() throws IOException {

        CSVParser parser = CsvParser.getParser("employees.csv");

        int count = 0;

        for (CSVRecord record : parser) {
            Employee employee = new Employee(
                Long.valueOf(record.get("employee_id")),
                record.get("first_name"),
                record.get("last_name"),
                LocalDate.parse(record.get("birth_date")),
                LocalDate.parse(record.get("hire_date")),
                record.get("city")
            );

            session.persist(employee);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void linkEmployees() throws IOException {

        CSVParser parser = CsvParser.getParser("employees.csv");

        int count = 0;

        for (CSVRecord record : parser) {

            Long employeeId = Long.parseLong(record.get("employee_id"));

            String reportsToTest = record.get("reports_to");

            // CEO / top-level employee
            if (reportsToTest == null || reportsToTest.isBlank()) {
                continue;
            }

            Long reportsToId = Long.parseLong(record.get("reports_to"));

            List<Employee> employees = session.createQuery("""
                        from Employees e
                        where e.employeeId in (:employeeId, :reportsToId)
                    """, Employee.class)
                .setParameter("employeeId", employeeId)
                .setParameter("reportsToId", reportsToId)
                .getResultList();

            if (employees.size() != 2) {
                throw new IllegalStateException(
                    "Employees not found: " + employeeId + " or " + reportsToId
                );
            }

            Employee employee = employees.stream()
                .filter(e -> e.getEmployeeId().equals(employeeId))
                .findFirst()
                .orElseThrow();

            Employee reportsTo = employees.stream()
                .filter(e -> e.getEmployeeId().equals(reportsToId))
                .findFirst()
                .orElseThrow();

            reportsTo.addSubordinate(employee);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void loadProducts() throws IOException {

        CSVParser parser = CsvParser.getParser("products.csv");

        int count = 0;

        for (CSVRecord record : parser) {
            Product product = new Product(
                Long.valueOf(record.get("product_id")),
                record.get("product_name"),
                Double.valueOf(record.get("unit_price")),
                record.get("category")
            );

            session.persist(product);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void linkProductsWithSuppliers() throws IOException {

        CSVParser parser = CsvParser.getParser("products.csv");

        int count = 0;

        for (CSVRecord record : parser) {

            Long supplierId = Long.parseLong(record.get("supplier_id"));
            Long productId = Long.parseLong(record.get("product_id"));

            Supplier supplier = session.createQuery("""
                        from Suppliers s
                        where s.supplierId = :supplierId
                    """, Supplier.class)
                .setParameter("supplierId", supplierId)
                .getSingleResult();

            Product product = session.createQuery("""
                        from Products p
                        where p.productId = :productId
                    """, Product.class)
                .setParameter("productId", productId)
                .getSingleResult();

            supplier.addProduct(product);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void loadOrders() throws IOException {

        CSVParser parser = CsvParser.getParser("orders.csv");

        int count = 0;

        for (CSVRecord record : parser) {

            Order order = new Order(
                Long.parseLong(record.get("order_id")),
                LocalDate.parse(record.get("order_date"))
            );

            session.persist(order);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void linkOrdersWithCustomersAndEmployees() throws IOException {

        CSVParser parser = CsvParser.getParser("orders.csv");

        int count = 0;

        for (CSVRecord record : parser) {

            Long orderId = Long.parseLong(record.get("order_id"));
            Long employeeId = Long.parseLong(record.get("employee_id"));
            Long customerId = Long.parseLong(record.get("customer_id"));

            Order order = session.createQuery("""
                        from Orders o
                        where o.orderId = :orderId
                    """, Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult();

            Employee employee = session.createQuery("""
                        from Employees e
                        where e.employeeId = :employeeId
                    """, Employee.class)
                .setParameter("employeeId", employeeId)
                .getSingleResult();

            Customer customer = session.createQuery("""
                        from Customers c
                        where c.customerId = :customerId
                    """, Customer.class)
                .setParameter("customerId", customerId)
                .getSingleResult();

            customer.addOrder(order);
            employee.addOrder(order);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }

    private static void linkOrdersWithProducts() throws IOException {

        CSVParser parser = CsvParser.getParser("order_products.csv");

        int count = 0;

        for (CSVRecord record : parser) {

            Long orderId = Long.parseLong(record.get("order_id"));
            Long productId = Long.parseLong(record.get("product_id"));

            Order order = session.createQuery("""
                        from Orders o
                        where o.orderId = :orderId
                    """, Order.class)
                .setParameter("orderId", orderId)
                .getSingleResult();

            Product product = session.createQuery("""
                        from Products p
                        where p.productId = :productId
                    """, Product.class)
                .setParameter("productId", productId)
                .getSingleResult();

            order.addProduct(product);

            if (++count % BATCH_SIZE == 0) {
                session.flush();
                session.clear();
            }
        }

        session.flush();
        session.clear();
    }
}
