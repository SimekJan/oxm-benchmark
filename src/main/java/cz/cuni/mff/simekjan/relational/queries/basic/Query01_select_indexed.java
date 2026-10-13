package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.Q1_Params;
import cz.cuni.mff.simekjan.relational.northwind.Employee;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

/**
 * Get employee with specific id. That is filter by indexed column.
 */
public class Query01_select_indexed implements RelationalQuery<Q1_Params> {

    @Override
    public void run(Q1_Params params) {
        System.out.println("----------PostgreSQL-Q1----------");

        String hql = "FROM Employees e " +
            "WHERE e.employeeId = :id";

        Employee e = session.createQuery(hql, Employee.class).setParameter("id", params.employeeId()).uniqueResult();

        System.out.println("Found employee: " + e.getFirstName() + " " + e.getLastName());
    }
}
