package cz.cuni.mff.simekjan.document.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ1_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Max price of products with name starting with "S" per category.
 */
public class C_Query1_filter_max implements DocumentQuery<CQ1_Params> {

    @Override
    public void run(CQ1_Params params) {

        MongoCollection<Document> products = db.getCollection("Products");

        List<Document> results = products.aggregate(List.of(
            new Document("$match",
                new Document("productName",
                    new Document("$regex", "^" + params.productNameStartingLetter())
                )
            ),

            new Document("$group",
                new Document("_id", "$category")
                    .append("maxUnitPrice",
                        new Document("$max", "$unitPrice")
                    )
            )
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-CQ1------------");
        for (Document row : results) {
            String category = row.getString("_id");
            Double maxUnitPrice = row.getDouble("maxUnitPrice");

            System.out.println(category + ": " + maxUnitPrice);
        }
    }
}
