package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Supplier;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Find all direct and indirect connections between suppliers (max depth = 2).
 */
public class Query09_neighbours implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Supplier> suppliers = db.getCollection("Suppliers", Supplier.class);

        List<Document> result = suppliers.aggregate(List.of(
            new Document("$graphLookup",
                new Document("from", "Suppliers")
                    .append("startWith", "$_id")
                    .append("connectFromField", "_id")
                    .append("connectToField", "suppliedBy")
                    .append("as", "path")
                    .append("maxDepth", 2)
                    .append("depthField", "depth")
            ),
            new Document("$unwind", "$path"),
            new Document("$project",
                new Document("fromSupplier", "$companyName")
                    .append("toSupplier", "$path.companyName")
                    .append("depth", "$path.depth")
            )
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q9-------------");
        System.out.println("Connections of depth max 2: " + result.size());
        // for (Document d: result) {
        //    System.out.println(d.get("fromSupplier") + " to " + d.get("toSupplier") + " with depth: " + d.get("depth"));
        // }
    }
}
