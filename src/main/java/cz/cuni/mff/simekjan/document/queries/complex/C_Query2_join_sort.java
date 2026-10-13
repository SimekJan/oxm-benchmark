package cz.cuni.mff.simekjan.document.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Join employees with orders and order by employee name.
 */
public class C_Query2_join_sort implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Document> employees = db.getCollection("Employees");

        List<Document> results = employees.aggregate(List.of(

            new Document("$lookup",
                new Document("from", "Orders")
                    .append("localField", "_id")
                    .append("foreignField", "employee")
                    .append("as", "orders")
            ),

            new Document("$unwind", "$orders"),

            new Document("$project",
                new Document("_id", 0)
                    .append("order", "$orders.orderId")
                    .append("firstName", "$firstName")
                    .append("lastName", "$lastName")
            ),

            // sort
            new Document("$sort",
                new Document("lastName", 1)
                    .append("firstName", 1)
            )

        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-CQ2------------");
        for (Document row : results) {
            Long orderId = row.getLong("order");
            String firstName = row.getString("firstName");
            String lastName = row.getString("lastName");

            System.out.println("Order " + orderId + " -> " + firstName + " " + lastName);
        }
    }
}
