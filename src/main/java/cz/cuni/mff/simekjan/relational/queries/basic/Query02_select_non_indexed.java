package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.Q2_Params;
import cz.cuni.mff.simekjan.relational.northwind.Employee;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Get employee by first_name. That is non-indexed column.
 */
public class Query02_select_non_indexed implements RelationalQuery<Q2_Params> {

    @Override
    public void run(Q2_Params params) {
        System.out.println("----------PostgreSQL-Q2----------");

        String hql = "FROM Employees e " +
            "WHERE e.firstName = :first_name";

        List<Employee> employees = session.createQuery(hql, Employee.class).setParameter("first_name", params.firstName()).getResultList();

        System.out.println("Found " + employees.size() + " employees with given name.");
        // for (Employee e: employees) {
        //    System.out.println("Found employee: " + e.getFirstName() + " " + e.getLastName());
        // }
    }
}
