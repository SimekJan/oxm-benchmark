package cz.cuni.mff.simekjan.document.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Select products with bellow average price.
 */
public class C_Query9_subquery implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Document> products = db.getCollection("Products");

        List<Document> results = products.aggregate(List.of(

            new Document("$group",
                new Document("_id", null)
                    .append("avgPrice", new Document("$avg", "$unitPrice"))
            ),

            new Document("$lookup",
                new Document("from", "Products")
                    .append("pipeline", List.of(
                        new Document("$match", new Document())
                    ))
                    .append("as", "products")
            ),

            new Document("$unwind", "$products"),

            new Document("$match",
                new Document("$expr",
                    new Document("$lte", List.of("$products.unitPrice", "$avgPrice"))
                )
            ),

            new Document("$project",
                new Document("_id", 0)
                    .append("productName", "$products.productName")
            )

        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-CQ9------------");
        for (Document row : results) {
            System.out.println(row.getString("productName"));
        }
    }
}
