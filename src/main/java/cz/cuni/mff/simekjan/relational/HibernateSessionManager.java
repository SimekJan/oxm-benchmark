package cz.cuni.mff.simekjan.relational;

import cz.cuni.mff.simekjan.ConfigLoader;
import cz.cuni.mff.simekjan.relational.northwind.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;

import java.io.IOException;
import java.util.Map;

@Slf4j
public class HibernateSessionManager {

    private static final String DEFAULT_POSTGRES_CONNECTION =
        "jdbc:postgresql://postgres:5432/oxm_benchmark";
    private static final String DEFAULT_POSTGRES_USERNAME = "postgres";
    private static final String DEFAULT_POSTGRES_PASSWORD = "oxm_password";

    private static Session session;

    public static Session getSession() {

        if (session != null) {
            return session;
        }

        String connection = DEFAULT_POSTGRES_CONNECTION;
        String username = DEFAULT_POSTGRES_USERNAME;
        String password = DEFAULT_POSTGRES_PASSWORD;

        try {
            Map<String, Object> config = ConfigLoader.getSection(
                ConfigLoader.IMPORTER_SECTION,
                ConfigLoader.POSTGRES_IMPORTER_SECTION);

            connection = (String) config.get("connection");
            username = (String) config.get("username");
            password = (String) config.get("password");
        } catch (IOException e) {
            log.info("Cannot load Neo4j config, using default connection and authentication.");
        }

        SessionFactory sessionFactory = new Configuration()
            .setProperty("hibernate.connection.url", connection)
            .setProperty("hibernate.connection.username", username)
            .setProperty("hibernate.connection.password", password)
            .setProperty("hibernate.connection.driver_class", "org.postgresql.Driver")
            .setProperty("hibernate.hbm2ddl.auto", "update")
            // To avoid caching
            .setProperty(AvailableSettings.USE_SECOND_LEVEL_CACHE, "false")
            .setProperty(AvailableSettings.USE_QUERY_CACHE, "false")
            //
            .addAnnotatedClass(Customer.class)
            .addAnnotatedClass(Employee.class)
            .addAnnotatedClass(Order.class)
            .addAnnotatedClass(Product.class)
            .addAnnotatedClass(Supplier.class)
            .buildSessionFactory();

        session = sessionFactory.openSession();

        return session;
    }
}