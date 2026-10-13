package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MapReduceIterable;
import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Product;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Number of products by supplier (one order or more)
 */
public class Query18_map_reduce implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Product> products = db.getCollection("Products", Product.class);

        String mapFunction = "function() { emit(this.supplier, 1); }";
        String reduceFunction = "function(key, values) { return Array.sum(values); }";

        @Deprecated
        MapReduceIterable<Document> result = products.mapReduce(mapFunction, reduceFunction, Document.class);

        System.out.println("------------Mongo-Q18------------");
        List<Document> resultList = result.into(new ArrayList<>());
        System.out.println("Map-reduce number of rows: " + resultList.size());
        /*for (Document d: resultList) {
            System.out.println(d.toJson());
        }*/
    }
}

/*
    map-reduce is deprecated in MongoDB
    documentation advises to use aggregation instead
 */