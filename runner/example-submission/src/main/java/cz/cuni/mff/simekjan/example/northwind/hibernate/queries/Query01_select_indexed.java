package cz.cuni.mff.simekjan.example.northwind.hibernate.queries;

import cz.cuni.mff.simekjan.example.northwind.hibernate.entities.Employee;
import cz.cuni.mff.simekjan.example.northwind.params.Q1_Params;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/**
 * Get employee with specific id. That is filter by indexed column.
 */
public class Query01_select_indexed implements HibernateQuery<Q1_Params> {

    @Override
    public String id() {
        return "Q1";
    }

    @Override
    public Class<Q1_Params> parameterType() {
        return Q1_Params.class;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public Object run(Session context, Q1_Params params) {
        String hql = "FROM Employees e " +
            "WHERE e.employeeId = :id";

        return context.createQuery(hql, Employee.class).setParameter("id", params.employeeId()).uniqueResult();
    }
}
