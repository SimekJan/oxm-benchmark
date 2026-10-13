package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Customer;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Aggregates.project;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * Find all cities in Customers and Suppliers (in either)
 */
public class Query12_union implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        MongoCollection<Customer> customers = db.getCollection("Customers", Customer.class);

        List<Document> result = customers.aggregate(List.of(
            new Document("$unionWith",
                new Document("coll", "Suppliers")
            ),
            project(fields(include("city"))),

            // remove duplicates
            group("$city")
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q12------------");
        System.out.println("Cities in either: " + result.size());
    }
}
