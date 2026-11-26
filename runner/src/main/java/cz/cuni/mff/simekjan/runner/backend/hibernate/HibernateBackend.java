package cz.cuni.mff.simekjan.runner.backend.hibernate;

import cz.cuni.mff.simekjan.runner.spi.Backend;
import cz.cuni.mff.simekjan.runner.spi.ContextFactory;
import cz.cuni.mff.simekjan.runner.spi.QueryContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.BootstrapServiceRegistryBuilder;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.JdbcSettings;

import java.util.List;
import java.util.Map;

/**
 * Hibernate backend implementation.
 */
public class HibernateBackend implements Backend {

    /**
     * {@inheritDoc}
     */
    @Override
    public String id() {
        return "hibernate";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ContextFactory createContextFactory(Map<String, String> connection, List<Class<?>> mappedClasses) {
        var bootstrap = new BootstrapServiceRegistryBuilder();
        mappedClasses.stream().map(Class::getClassLoader).distinct().forEach(bootstrap::applyClassLoader);
        var builder = new StandardServiceRegistryBuilder(bootstrap.build())
            .applySetting(JdbcSettings.JAKARTA_JDBC_URL, connection.get("connection"))
            .applySetting("hibernate.hbm2ddl.auto", "none");
        if (connection.containsKey("username")) {
            builder.applySetting(JdbcSettings.JAKARTA_JDBC_USER, connection.get("username"));
        }
        if (connection.containsKey("password")) {
            builder.applySetting(JdbcSettings.JAKARTA_JDBC_PASSWORD, connection.get("password"));
        }
        var registry = builder.build();
        SessionFactory sessionFactory;
        try {
            var metadata = new MetadataSources(registry);
            mappedClasses.forEach(metadata::addAnnotatedClass);
            sessionFactory = metadata.buildMetadata().buildSessionFactory();
        } catch (RuntimeException | Error failure) {
            try {
                StandardServiceRegistryBuilder.destroy(registry);
            } catch (RuntimeException | Error cleanup) {
                failure.addSuppressed(cleanup);
            }
            throw failure;
        }

        return new ContextFactory() {
            @Override
            public QueryContext open() {
                Session session = sessionFactory.openSession();
                return new QueryContext() {
                    @Override
                    public Session value() {
                        return session;
                    }

                    @Override
                    public void close() {
                        session.close();
                    }
                };
            }

            @Override
            public void close() {
                sessionFactory.close();
            }
        };
    }
}
