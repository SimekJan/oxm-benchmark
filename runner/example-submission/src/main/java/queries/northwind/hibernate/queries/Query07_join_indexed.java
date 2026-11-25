package queries.northwind.hibernate.queries;

import cz.cuni.mff.simekjan.runner.api.NoParams;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/**
 * Join orders with employees on employee ID (indexed).
 */
public class Query07_join_indexed implements HibernateQuery<NoParams> {

    @Override
    public String id() {
        return "Q7";
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

        String hql = "SELECT o.orderId, e.firstName, e.lastName " +
            "FROM Orders o " +
            "       INNER JOIN Employees e " +
            "       ON o.employee = e.id ";

        return session.createQuery(hql, Object[].class).getResultList();
    }
}
