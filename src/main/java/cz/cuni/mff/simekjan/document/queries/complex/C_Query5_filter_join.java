package cz.cuni.mff.simekjan.document.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ5_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Join suppliers which are from cities starting wit 'P' with their "Packaging Materials" products.
 */
public class C_Query5_filter_join implements DocumentQuery<CQ5_Params> {

    @Override
    public void run(CQ5_Params params) {

        MongoCollection<Document> suppliers = db.getCollection("Suppliers");

        List<Document> results = suppliers.aggregate(List.of(

            new Document("$match",
                new Document("city",
                    new Document("$regex", "^" + params.supplierCityStartingLetter())
                )
            ),

            new Document("$lookup",
                new Document("from", "Products")
                    .append("localField", "_id")
                    .append("foreignField", "supplier")
                    .append("as", "products")
            ),

            new Document("$unwind", "$products"),

            new Document("$match",
                new Document("products.category", params.productCategory())
            ),

            new Document("$project",
                new Document("_id", 0)
                    .append("product", "$products.productName")
                    .append("company", "$companyName")
            )
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-CQ5------------");
        for (Document row : results) {
            String product = row.getString("product");
            String company = row.getString("company");

            System.out.println(company + " - " + product);
        }
    }
}
