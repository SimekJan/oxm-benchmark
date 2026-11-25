package cz.cuni.mff.simekjan.runner.backend.hibernate;

import cz.cuni.mff.simekjan.runner.spi.Backend;
import cz.cuni.mff.simekjan.runner.spi.ContextFactory;

import java.util.Map;

/** Hibernate backend implementation. */
public class HibernateBackend implements Backend {

    /** {@inheritDoc} */
    @Override
    public String id() {
        return "hibernate";
    }

    /** {@inheritDoc} */
    @Override
    public ContextFactory createContextFactory(Map<String, String> connection) {
        throw new UnsupportedOperationException("Hibernate backend is not implemented");
    }
}
