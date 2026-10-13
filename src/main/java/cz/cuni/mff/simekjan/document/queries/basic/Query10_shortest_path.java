package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Sorts;
import cz.cuni.mff.simekjan.document.northwind.Supplier;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.Q10_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Projections.*;

/**
 * Find the shortest path between two given suppliers
 */
public class Query10_shortest_path implements DocumentQuery<Q10_Params> {

    @Override
    public void run(Q10_Params params) {
        MongoCollection<Supplier> suppliers = db.getCollection("Suppliers", Supplier.class);

        List<Document> result = suppliers.aggregate(List.of(
            match(eq("supplierId", params.supplierIdFrom())),
            new Document("$graphLookup",
                new Document("from", "Suppliers")
                    .append("startWith", "$suppliedBy")
                    .append("connectFromField", "suppliedBy")
                    .append("connectToField", "_id")
                    .append("as", "reachable")
                    .append("depthField", "depth")
                    .append("maxDepth", 10) // safety limit
            ),
            unwind("$reachable"),
            match(eq("reachable.supplierId", params.supplierIdTo())),
            sort(Sorts.ascending("reachable.depth")),
            limit(1),
            project(fields(
                include("companyName"),
                computed("target", "$reachable.companyName"),
                computed("depth", "$reachable.depth")
            ))
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q10------------");
        for (Document doc : result) {
            System.out.println("Shortest path: " + doc.toJson() + " -- Depth starts from 0.");
        }
    }
}

/*
    TODO: neexistuje přímo nejkratší cesta
            tohle bere všechny, a poté vyfiltruje tu, co chceme
            navíc neukládá cestu, ale jen hloubku
*/
