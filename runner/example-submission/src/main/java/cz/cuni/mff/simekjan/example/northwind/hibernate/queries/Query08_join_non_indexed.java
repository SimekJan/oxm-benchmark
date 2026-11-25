package cz.cuni.mff.simekjan.example.northwind.hibernate.queries;

import cz.cuni.mff.simekjan.example.northwind.params.QX_No_Params;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/**
 * Join customers and employees on city (non-indexed)
 */
public class Query08_join_non_indexed implements HibernateQuery<QX_No_Params> {

    @Override
    public String id() {
        return "Q8";
    }

    @Override
    public Class<QX_No_Params> parameterType() {
        return QX_No_Params.class;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public Object run(Session session, QX_No_Params params) {

        String hql = "SELECT c.companyName, c.city, e.firstName, e.lastName " +
            "FROM Customers c " +
            "       INNER JOIN Employees e " +
            "       ON c.city = e.city ";

        return session.createQuery(hql, Object[].class).getResultList();
    }
}
