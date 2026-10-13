package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Employee;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.Q1_Params;

import static com.mongodb.client.model.Filters.eq;

/**
 * Get employee with specific id. That is filter by indexed column.
 */
public class Query01_select_indexed implements DocumentQuery<Q1_Params> {

    @Override
    public void run(Q1_Params params) {

        MongoCollection<Employee> employees = db.getCollection("Employees", Employee.class);

        Employee found = employees.find(eq("employeeId", params.employeeId())).first();
        assert found != null;

        System.out.println("------------Mongo-Q1-------------");
        System.out.println(found.getFirstName() + " " + found.getLastName());
    }
}
