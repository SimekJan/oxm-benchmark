package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Customer;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.*;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * Find all cities in both Customers and Suppliers
 */
public class Query13_intersect implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        MongoCollection<Customer> customers = db.getCollection("Customers", Customer.class);

        List<Document> result = customers.aggregate(List.of(
            project(fields(include("city"))),
            lookup("Suppliers", "city", "city", "match"),
            match(new Document("match.0", new Document("$exists", true))),
            group("$city")
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q13------------");
        System.out.println("Cities in both: " + result.size());
    }
}
