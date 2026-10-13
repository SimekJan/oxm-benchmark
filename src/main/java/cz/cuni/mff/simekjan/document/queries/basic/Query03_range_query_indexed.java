package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Employee;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.Q3_Params;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

/**
 * Get all employees with employeeId (Range query).
 */
public class Query03_range_query_indexed implements DocumentQuery<Q3_Params> {

    @Override
    public void run(Q3_Params params) {
        MongoCollection<Employee> employees = db.getCollection("Employees", Employee.class);

        List<Employee> res = employees.find(and(
            gte("employeeId", params.employeeIdFrom()),
            lte("employeeId", params.employeeIdTo())
        )).into(new ArrayList<>());

        System.out.println("------------Mongo-Q3-------------");
        System.out.println("Number of employees found (ranged, indexed): " + res.size());
    }
}
