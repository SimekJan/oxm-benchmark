package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Employee;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Accumulators.sum;
import static com.mongodb.client.model.Aggregates.group;

/**
 * Count the number of employees per city.
 */
public class Query05_count implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Employee> employees = db.getCollection("Employees", Employee.class);

        List<Document> result = employees.aggregate(List.of(
            group("$city", sum("count", 1))
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q5-------------");
        System.out.println("Number of cities: " + result.size());
        // for (Document doc : result) {
        //    System.out.println(doc.get("_id") + ": " + doc.getInteger("count"));
        // }
    }
}
