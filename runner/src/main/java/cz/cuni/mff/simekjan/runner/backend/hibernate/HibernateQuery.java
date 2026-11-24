package cz.cuni.mff.simekjan.runner.backend.hibernate;

import cz.cuni.mff.simekjan.runner.api.Query;
import org.hibernate.Session;

/**
 * Query using a Hibernate session.
 *
 * @param <P> query parameter type
 */
public interface HibernateQuery<P> extends Query<Session, P> {
}
