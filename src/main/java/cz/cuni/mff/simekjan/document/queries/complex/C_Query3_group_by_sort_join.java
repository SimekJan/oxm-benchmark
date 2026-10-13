package cz.cuni.mff.simekjan.document.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Order customers from most orders to least (include order count).
 */
public class C_Query3_group_by_sort_join implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Document> customers = db.getCollection("Customers");

        List<Document> results = customers.aggregate(List.of(

            new Document("$lookup",
                new Document("from", "Orders")
                    .append("localField", "_id")
                    .append("foreignField", "customer")
                    .append("as", "orders")
            ),

            new Document("$addFields",
                new Document("orderCount",
                    new Document("$size", "$orders")
                )
            ),

            new Document("$project",
                new Document("orders", 0)
            ),

            new Document("$sort",
                new Document("orderCount", -1)
            )

        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-CQ3------------");
        for (Document row : results) {

            String customer = row.getString("companyName");
            Long customerId = row.getLong("customerId");
            Integer count = row.getInteger("orderCount");

            System.out.println(customerId + " - " + customer + " - " + count);
        }
    }
}
