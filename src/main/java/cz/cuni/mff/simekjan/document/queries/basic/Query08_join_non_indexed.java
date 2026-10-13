package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Employee;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.*;

/**
 * Join customers and employees on city (non-indexed)
 */
public class Query08_join_non_indexed implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        MongoCollection<Employee> employees = db.getCollection("Employees", Employee.class);

        List<Document> result = employees.aggregate(List.of(
            lookup("Customers",       // from collection
                "city",                 // local field
                "city",                 // foreign field
                "customer"),            // output array field
            unwind("$customer"),   // INNER JOIN behavior
            project(fields(
                include("firstName", "lastName", "city"),
                computed("companyName", "$customer.companyName")
            ))
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q8-------------");
        System.out.println("Employee - Customer join results: " + result.size());
        // for (Document doc : result) {
        //    System.out.println(doc.get("firstName") + " " + doc.get("lastName") + ": " +
        //            doc.get("companyName") + " in " + doc.get("city"));
        // }
    }
}
