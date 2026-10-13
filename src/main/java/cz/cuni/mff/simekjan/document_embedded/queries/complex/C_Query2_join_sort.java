package cz.cuni.mff.simekjan.document_embedded.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document_embedded.queries.EmbeddedDocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Join employees with orders and order by employee name.
 */
public class C_Query2_join_sort implements EmbeddedDocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        System.out.println("----------E-Mongo-CQ2------------");

        MongoCollection<Document> employees = db.getCollection("Employees");

        List<Document> results = employees.aggregate(List.of(

            new Document("$unwind", "$orders"),

            new Document("$project",
                new Document("_id", 0)
                    .append("order", "$orders.orderId")
                    .append("firstName", "$firstName")
                    .append("lastName", "$lastName")
            ),

            new Document("$sort",
                new Document("lastName", 1)
                    .append("firstName", 1)
            )

        ), Document.class).into(new ArrayList<>());

        System.out.println("Joined orders and employees result size: " + results.size());

        /*
            for (Document row : results) {
                Long orderId = row.getLong("order");
                String firstName = row.getString("firstName");
                String lastName = row.getString("lastName");

                System.out.println("Order " + orderId + " -> " + firstName + " " + lastName);
            }
         */
    }
}
