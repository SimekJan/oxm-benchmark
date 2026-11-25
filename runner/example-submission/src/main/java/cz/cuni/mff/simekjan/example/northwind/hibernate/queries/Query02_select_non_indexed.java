package cz.cuni.mff.simekjan.example.northwind.hibernate.queries;

import cz.cuni.mff.simekjan.example.northwind.hibernate.entities.Employee;
import cz.cuni.mff.simekjan.example.northwind.params.Q2_Params;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/**
 * Get employee by first_name. That is non-indexed column.
 */
public class Query02_select_non_indexed implements HibernateQuery<Q2_Params> {

    @Override
    public String id() {
        return "Q2";
    }

    @Override
    public Class<Q2_Params> parameterType() {
        return Q2_Params.class;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public Object run(Session session, Q2_Params params) {

        String hql = "FROM Employees e " +
            "WHERE e.firstName = :first_name";

        return session.createQuery(hql, Employee.class).setParameter("first_name", params.firstName()).getResultList();
    }
}
