package cz.cuni.mff.simekjan.relational.queries;

import cz.cuni.mff.simekjan.relational.HibernateSessionManager;
import cz.cuni.mff.simekjan.runner.Query;
import org.hibernate.Session;

public interface RelationalQuery<P> extends Query<P> {

    Session session = HibernateSessionManager.getSession();
}
