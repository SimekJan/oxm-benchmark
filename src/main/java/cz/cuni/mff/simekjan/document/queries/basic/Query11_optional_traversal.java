package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Supplier;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Get all suppliers and their supplier count (even if 0)
 */
public class Query11_optional_traversal implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        MongoCollection<Supplier> suppliers = db.getCollection("Suppliers", Supplier.class);

        List<Document> result = suppliers.aggregate(List.of(
            new Document("$lookup",
                new Document("from", "Suppliers")
                    .append("localField", "suppliedBy")
                    .append("foreignField", "_id")
                    .append("as", "suppliers")
            ),

            new Document("$addFields",
                new Document("supplierCount",
                    new Document("$size", "$suppliers")
                )
            ),

            new Document("$project",
                new Document("companyName", 1)
                    .append("supplierCount", 1)
            )
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q11------------");
        System.out.println("Supplier counts: " + result.size());
        // for (Document r : result) {
        //    System.out.println( r.get("companyName") + ": " + r.get("supplierCount"));
        // }
    }
}
