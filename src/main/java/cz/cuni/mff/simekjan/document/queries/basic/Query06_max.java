package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Product;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Accumulators.max;
import static com.mongodb.client.model.Aggregates.group;

/**
 * Find the most expensive product per category (maximum).
 */
public class Query06_max implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Product> products = db.getCollection("Products", Product.class);

        List<Document> result = products.aggregate(List.of(
            group("$category", max("maxPrice", "$unitPrice"))
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q6-------------");
        System.out.println("Product categories: " + result.size());
        // for (Document doc : result) {
        //    System.out.println(doc.getString("_id") + ": " + doc.getDouble("maxPrice"));
        // }
    }
}
