package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Product;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.Q4_Params;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.*;

/**
 * Get all products in price range (Range query).
 */
public class Query04_range_query_non_indexed implements DocumentQuery<Q4_Params> {

    @Override
    public void run(Q4_Params params) {
        MongoCollection<Product> products = db.getCollection("Products", Product.class);

        List<Product> res = products.find(and(
            gte("unitPrice", BigDecimal.valueOf(params.unitPriceFrom())),
            lte("unitPrice", BigDecimal.valueOf(params.unitPriceTo()))
        )).into(new ArrayList<>());

        System.out.println("------------Mongo-Q4-------------");
        System.out.println("Number of products found (ranged): " + res.size());
        // for (Product p: res) {
        //    System.out.println(p.getProductName() + ": " + p.getUnitPrice());
        // }
    }
}
