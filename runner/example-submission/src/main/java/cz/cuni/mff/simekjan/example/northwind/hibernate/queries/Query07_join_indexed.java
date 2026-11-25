package cz.cuni.mff.simekjan.example.northwind.hibernate.queries;

import cz.cuni.mff.simekjan.example.northwind.params.QX_No_Params;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/**
 * Join orders with employees on employee ID (indexed).
 */
public class Query07_join_indexed implements HibernateQuery<QX_No_Params> {

    @Override
    public String id() {
        return "Q7";
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

        String hql = "SELECT o.orderId, e.firstName, e.lastName " +
            "FROM Orders o " +
            "       INNER JOIN Employees e " +
            "       ON o.employee = e.id ";

        return session.createQuery(hql, Object[].class).getResultList();
    }
}
