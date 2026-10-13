package cz.cuni.mff.simekjan.document_embedded.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document_embedded.queries.EmbeddedDocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Order employees from most orders to least (include order count), show only employees having more than one order.
 */
public class C_Query4_group_by_having_sort implements EmbeddedDocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        System.out.println("----------E-Mongo-CQ4------------");

        MongoCollection<Document> employees = db.getCollection("Employees");

        List<Document> results = employees.aggregate(List.of(

            new Document("$addFields",
                new Document("orderCount",
                    new Document("$size", "$orders")
                )
            ),

            new Document("$match",
                new Document("orderCount",
                    new Document("$gt", 1)
                )
            ),

            new Document("$project",
                new Document("_id", 0)
                    .append("firstName", 1)
                    .append("lastName", 1)
                    .append("orderCount", 1)
            ),

            new Document("$sort",
                new Document("orderCount", -1)
            )
        ), Document.class).into(new ArrayList<>());

        System.out.println("Ordered employees with more than one order: " + results.size() + ".");
        /*
            for (Document row : results) {
                String firstName = row.getString("firstName");
                String lastName = row.getString("lastName");
                Integer count = row.getInteger("orderCount");

                System.out.println(firstName + " " + lastName + " - " + count);
            }
         */
    }
}
