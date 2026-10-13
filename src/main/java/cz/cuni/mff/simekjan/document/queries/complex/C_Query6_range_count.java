package cz.cuni.mff.simekjan.document.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ6_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Count products with unitPrice between 10 and 15 per category
 */
public class C_Query6_range_count implements DocumentQuery<CQ6_Params> {

    @Override
    public void run(CQ6_Params params) {

        MongoCollection<Document> products = db.getCollection("Products");

        List<Document> results = products.aggregate(List.of(

            new Document("$match",
                new Document("unitPrice",
                    new Document("$gte", params.unitPriceFrom())
                        .append("$lte", params.unitPriceTo())
                )
            ),

            new Document("$group",
                new Document("_id", "$category")
                    .append("productCount",
                        new Document("$sum", 1)
                    )
            ),

            new Document("$project",
                new Document("_id", 0)
                    .append("category", "$_id")
                    .append("productCount", 1)
            )

        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-CQ6------------");
        for (Document row : results) {
            String category = row.getString("category");
            Integer count = row.getInteger("productCount");

            System.out.println(category + ": " + count);
        }
    }
}
