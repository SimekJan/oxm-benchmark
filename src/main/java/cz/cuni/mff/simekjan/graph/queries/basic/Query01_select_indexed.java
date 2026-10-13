package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.northwind.Employee;
import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.Q1_Params;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;

import java.util.Collection;

/**
 * Get employee with specific id. That is filter by indexed column.
 */
public class Query01_select_indexed implements GraphQuery<Q1_Params> {

    @Override
    public void run(Q1_Params params) {

        Filter filter = new Filter("employeeId", ComparisonOperator.EQUALS, params.employeeId());

        Collection<Employee> result = session.loadAll(Employee.class, filter);
        Employee employee = result.stream().toList().getFirst();

        System.out.println("------------Neo4j-Q1-------------");
        System.out.println("Found employee: " + employee.getFirstName() + " " + employee.getLastName());
    }
}
