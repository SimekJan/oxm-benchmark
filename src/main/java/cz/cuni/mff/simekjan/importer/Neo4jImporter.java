package cz.cuni.mff.simekjan.importer;

import cz.cuni.mff.simekjan.graph.Neo4jSessionManager;
import cz.cuni.mff.simekjan.graph.northwind.*;
import cz.cuni.mff.simekjan.graph.northwind.*;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.neo4j.ogm.session.Session;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Neo4jImporter {

    private static final int BATCH_SIZE = 2_000;

    private static Session session;

    public static void run() throws IOException {

        session = Neo4jSessionManager.getSession();

        loadSuppliers();
        linkSuppliers();

        loadCustomers();

        loadEmployees();
        linkEmployees();

        loadProducts();
        linkProductsWithSuppliers();

        loadOrders();
        linkOrdersWithCustomers();
        linkOrdersWithEmployees();
        linkOrdersWithProducts();
    }

    private static void loadSuppliers() throws IOException {

        CSVParser parser = CsvParser.getParser("suppliers.csv");
        List<Supplier> batch = new ArrayList<>();

        for (CSVRecord record : parser) {

            Supplier supplier = new Supplier(
                Long.parseLong(record.get("supplier_id")),
                record.get("company_name"),
                record.get("city")
            );

            batch.add(supplier);

            if (batch.size() >= BATCH_SIZE) {
                session.save(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            session.save(batch);
        }
    }

    private static void linkSuppliers() throws IOException {

        CSVParser parser = CsvParser.getParser("supplier_relationships.csv");

        List<Map<String, Long>> batch = new ArrayList<>(BATCH_SIZE);

        for (CSVRecord record : parser) {

            batch.add(
                Map.of(
                    "supplierId", Long.parseLong(record.get("supplier_id")),
                    "suppliedToId", Long.parseLong(record.get("supplied_to_id"))
                )
            );

            if (batch.size() == BATCH_SIZE) {
                saveSupplierRelationships(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            saveSupplierRelationships(batch);
        }
    }

    private static void saveSupplierRelationships(
        List<Map<String, Long>> relationships) {

        String query = """
                UNWIND $relationships AS rel
            
                MATCH (supplier:Supplier {
                    supplierId: rel.supplierId
                })
            
                MATCH (suppliedTo:Supplier {
                    supplierId: rel.suppliedToId
                })
            
                MERGE (supplier)-[:SUPPLIES_TO]->(suppliedTo)
            """;

        session.query(
            query,
            Map.of("relationships", relationships)
        );
    }

    private static void loadCustomers() throws IOException {

        CSVParser parser = CsvParser.getParser("customers.csv");
        List<Customer> batch = new ArrayList<>();

        for (CSVRecord record : parser) {
            Customer customer = new Customer(
                Long.valueOf(record.get("customer_id")),
                record.get("company_name"),
                record.get("city")
            );

            batch.add(customer);

            if (batch.size() >= BATCH_SIZE) {
                session.save(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            session.save(batch);
        }
    }

    private static void loadEmployees() throws IOException {

        CSVParser parser = CsvParser.getParser("employees.csv");
        List<Employee> batch = new ArrayList<>();

        for (CSVRecord record : parser) {
            Employee employee = new Employee(
                Long.valueOf(record.get("employee_id")),
                record.get("first_name"),
                record.get("last_name"),
                LocalDate.parse(record.get("birth_date")),
                LocalDate.parse(record.get("hire_date")),
                record.get("city")
            );

            batch.add(employee);

            if (batch.size() >= BATCH_SIZE) {
                session.save(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            session.save(batch);
        }
    }

    private static void linkEmployees() throws IOException {

        CSVParser parser = CsvParser.getParser("employees.csv");

        List<Map<String, Long>> batch = new ArrayList<>(BATCH_SIZE);

        for (CSVRecord record : parser) {

            String reportsTo = record.get("reports_to");

            // CEO / top-level employee
            if (reportsTo == null || reportsTo.isBlank()) {
                continue;
            }

            batch.add(Map.of(
                "employeeId", Long.parseLong(record.get("employee_id")),
                "reportsToId", Long.parseLong(reportsTo)
            ));

            if (batch.size() == BATCH_SIZE) {
                saveEmployeeRelationships(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            saveEmployeeRelationships(batch);
        }
    }

    private static void saveEmployeeRelationships(
        List<Map<String, Long>> relationships) {

        String query = """
                UNWIND $relationships AS rel
            
                MATCH (employee:Employee {
                    employeeId: rel.employeeId
                })
            
                MATCH (manager:Employee {
                    employeeId: rel.reportsToId
                })
            
                MERGE (employee)-[:REPORTS_TO]->(manager)
            """;

        session.query(
            query,
            Map.of("relationships", relationships)
        );
    }

    private static void loadProducts() throws IOException {

        CSVParser parser = CsvParser.getParser("products.csv");
        List<Product> batch = new ArrayList<>();

        for (CSVRecord record : parser) {
            Product product = new Product(
                Long.valueOf(record.get("product_id")),
                record.get("product_name"),
                Double.valueOf(record.get("unit_price")),
                record.get("category")
            );

            batch.add(product);

            if (batch.size() >= BATCH_SIZE) {
                session.save(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            session.save(batch);
        }
    }

    private static void linkProductsWithSuppliers() throws IOException {

        CSVParser parser = CsvParser.getParser("products.csv");

        List<Map<String, Long>> batch = new ArrayList<>(BATCH_SIZE);

        for (CSVRecord record : parser) {

            batch.add(Map.of(
                "productId", Long.parseLong(record.get("product_id")),
                "supplierId", Long.parseLong(record.get("supplier_id"))
            ));

            if (batch.size() == BATCH_SIZE) {
                saveProductSupplierRelationships(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            saveProductSupplierRelationships(batch);
        }
    }

    private static void saveProductSupplierRelationships(
        List<Map<String, Long>> relationships) {

        String query = """
                UNWIND $relationships AS rel
            
                MATCH (product:Product {
                    productId: rel.productId
                })
            
                MATCH (supplier:Supplier {
                    supplierId: rel.supplierId
                })
            
                MERGE (product)-[:IS_PRODUCED_BY]->(supplier)
            """;

        session.query(
            query,
            Map.of("relationships", relationships)
        );
    }

    private static void loadOrders() throws IOException {

        CSVParser parser = CsvParser.getParser("orders.csv");
        List<Order> batch = new ArrayList<>();

        for (CSVRecord record : parser) {

            Order order = new Order(
                Long.parseLong(record.get("order_id")),
                LocalDate.parse(record.get("order_date"))
            );

            batch.add(order);

            if (batch.size() >= BATCH_SIZE) {
                session.save(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            session.save(batch);
        }
    }


    private static void linkOrdersWithCustomers() throws IOException {

        CSVParser parser = CsvParser.getParser("orders.csv");

        List<Map<String, Long>> batch = new ArrayList<>(BATCH_SIZE);

        for (CSVRecord record : parser) {

            batch.add(Map.of(
                "orderId", Long.parseLong(record.get("order_id")),
                "customerId", Long.parseLong(record.get("customer_id"))
            ));

            if (batch.size() == BATCH_SIZE) {
                saveOrderCustomerRelationship(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            saveOrderCustomerRelationship(batch);
        }
    }

    private static void saveOrderCustomerRelationship(
        List<Map<String, Long>> relationships) {

        String query = """
                UNWIND $relationships AS rel
            
                MATCH (order:Order {
                    orderId: rel.orderId
                })
            
                MATCH (customer:Customer {
                    customerId: rel.customerId
                })
            
                MERGE (order)-[:IS_CUSTOMERS_ORDER]->(customer)
            """;

        session.query(
            query,
            Map.of("relationships", relationships)
        );
    }

    private static void linkOrdersWithEmployees() throws IOException {

        CSVParser parser = CsvParser.getParser("orders.csv");

        List<Map<String, Long>> batch = new ArrayList<>(BATCH_SIZE);

        for (CSVRecord record : parser) {

            batch.add(Map.of(
                "orderId", Long.parseLong(record.get("order_id")),
                "employeeId", Long.parseLong(record.get("employee_id"))
            ));

            if (batch.size() == BATCH_SIZE) {
                saveOrderEmployeeRelationship(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            saveOrderEmployeeRelationship(batch);
        }
    }

    private static void saveOrderEmployeeRelationship(
        List<Map<String, Long>> relationships) {

        String query = """
                UNWIND $relationships AS rel
            
                MATCH (order:Order {
                    orderId: rel.orderId
                })
            
                MATCH (employee:Employee {
                    employeeId: rel.employeeId
                })
            
                MERGE (employee)-[:IS_RESPONSIBLE_FOR]->(order)
            """;

        session.query(
            query,
            Map.of("relationships", relationships)
        );
    }

    private static void linkOrdersWithProducts() throws IOException {

        CSVParser parser = CsvParser.getParser("order_products.csv");

        List<Map<String, Long>> batch = new ArrayList<>(BATCH_SIZE);

        for (CSVRecord record : parser) {

            batch.add(Map.of(
                "orderId", Long.parseLong(record.get("order_id")),
                "productId", Long.parseLong(record.get("product_id"))
            ));

            if (batch.size() == BATCH_SIZE) {
                saveOrderProductRelationships(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            saveOrderProductRelationships(batch);
        }
    }

    private static void saveOrderProductRelationships(
        List<Map<String, Long>> relationships) {

        String query = """
                UNWIND $relationships AS rel
            
                MATCH (order:Order {
                    orderId: rel.orderId
                })
            
                MATCH (product:Product {
                    productId: rel.productId
                })
            
                MERGE (order)-[:INCLUDES]->(product)
            """;

        session.query(
            query,
            Map.of("relationships", relationships)
        );
    }
}
