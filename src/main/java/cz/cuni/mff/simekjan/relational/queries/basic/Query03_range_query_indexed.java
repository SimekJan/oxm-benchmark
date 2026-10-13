package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.Q3_Params;
import cz.cuni.mff.simekjan.relational.northwind.Employee;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Get all users with employee_id between 5 and 10 (Range query).
 */
public class Query03_range_query_indexed implements RelationalQuery<Q3_Params> {

    @Override
    public void run(Q3_Params params) {
        System.out.println("----------PostgreSQL-Q3----------");

        String hql = "FROM Employees e " +
            "WHERE e.employeeId BETWEEN :id_min AND :id_max";

        List<Employee> empls = session.createQuery(hql, Employee.class)
            .setParameter("id_min", params.employeeIdFrom())
            .setParameter("id_max", params.employeeIdTo())
            .getResultList();

        System.out.println("Found " + empls.size() + " employees.");
        // for (Employee e : empls) {
        //    System.out.println(e.getEmployeeId() + ": " + e.getFirstName() + " " + e.getLastName());
        // }
    }
}
