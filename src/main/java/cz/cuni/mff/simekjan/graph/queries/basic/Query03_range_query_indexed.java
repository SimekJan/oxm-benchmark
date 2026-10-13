package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.northwind.Employee;
import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.Q3_Params;
import org.neo4j.ogm.cypher.BooleanOperator;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;

/**
 * Get all users with employee_id between 5 and 10 (Range query).
 */
public class Query03_range_query_indexed implements GraphQuery<Q3_Params> {

    @Override
    public void run(Q3_Params params) {

        Filter minLastName = new Filter("employeeId", ComparisonOperator.GREATER_THAN_EQUAL, params.employeeIdFrom());
        Filter maxLastName = new Filter("employeeId", ComparisonOperator.LESS_THAN, params.employeeIdTo());
        minLastName.setBooleanOperator(BooleanOperator.AND);

        Filters range = maxLastName.and(minLastName);

        Collection<Employee> result = session.loadAll(Employee.class, range);

        System.out.println("------------Neo4j-Q3-------------");
        System.out.println("Found " + result.size() + " employees in range.");
        // for (Employee e : result) {
        //    System.out.println(e.getFirstName() + " " + e.getLastName());
        // }
    }
}
