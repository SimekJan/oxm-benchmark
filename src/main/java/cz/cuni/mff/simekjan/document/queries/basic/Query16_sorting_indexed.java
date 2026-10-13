package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import cz.cuni.mff.simekjan.document.northwind.Product;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.sort;

/**
 * Sort products based on productId
 * (indexed column)
 */
public class Query16_sorting_indexed implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        MongoCollection<Product> products = db.getCollection("Products", Product.class);

        List<Product> results = products.aggregate(List.of(
            sort(Sorts.ascending("productId"))
        ), Product.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q16------------");
        System.out.println("Sorted products (indexed): " + results.size());
        // for (Product p: results) {
        //    System.out.println(p.getProductId() + ": " + p.getProductName());
        // }
    }
}
