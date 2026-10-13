package cz.cuni.mff.simekjan.document_embedded.queries.basic;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Aggregates;
import cz.cuni.mff.simekjan.document_embedded.northwind.Employee;
import cz.cuni.mff.simekjan.document_embedded.queries.EmbeddedDocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Join orders with employees on employee ID
 */
public class Query07_join_indexed implements EmbeddedDocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        System.out.println("----------E-Mongo-Q7-------------");

        MongoCollection<Employee> employees = db.getCollection("Employees", Employee.class);

        List<Document> results = employees.aggregate(List.of(
            Aggregates.unwind("$orders")
        ), Document.class).into(new ArrayList<>());

        System.out.println("Orders-employees join result size: " + results.size());

        /*
            for (Document d : results) {
                String firstName = d.getString("firstName");
                String lastName = d.getString("lastName");
                Document order = (Document) d.get("orders");
                Date orderDate = order.getDate("orderDate");

                System.out.println(firstName + " " + lastName + ": " + orderDate);
            }
         */
    }
}
