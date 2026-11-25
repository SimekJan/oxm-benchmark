package cz.cuni.mff.simekjan.example.northwind.hibernate.queries;

import cz.cuni.mff.simekjan.example.northwind.hibernate.entities.Employee;
import cz.cuni.mff.simekjan.example.northwind.params.Q3_Params;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/**
 * Get all users with employee_id between 5 and 10 (Range query).
 */
public class Query03_range_query_indexed implements HibernateQuery<Q3_Params> {

    @Override
    public String id() {
        return "Q3";
    }

    @Override
    public Class<Q3_Params> parameterType() {
        return Q3_Params.class;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public Object run(Session session, Q3_Params params) {

        String hql = "FROM Employees e " +
            "WHERE e.employeeId BETWEEN :id_min AND :id_max";

        return session.createQuery(hql, Employee.class)
            .setParameter("id_min", params.employeeIdFrom())
            .setParameter("id_max", params.employeeIdTo())
            .getResultList();
    }
}
