package queries.northwind.hibernate.queries;

import cz.cuni.mff.simekjan.runner.api.NoParams;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/**
 * Join customers and employees on city (non-indexed)
 */
public class Query08_join_non_indexed implements HibernateQuery<NoParams> {

    @Override
    public String id() {
        return "Q8";
    }

    @Override
    public Class<NoParams> parameterType() {
        return NoParams.class;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public Object run(Session session, NoParams params) {

        String hql = "SELECT c.companyName, c.city, e.firstName, e.lastName " +
            "FROM Customers c " +
            "       INNER JOIN Employees e " +
            "       ON c.city = e.city ";

        return session.createQuery(hql, Object[].class).getResultList();
    }
}
