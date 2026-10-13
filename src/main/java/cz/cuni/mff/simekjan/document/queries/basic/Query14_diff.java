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
 * Find all Customers without an order (all customers - (diff) customer_ids in orders)
 */
public class Query14_diff implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Customer> customers = db.getCollection("Customers", Customer.class);

        List<Document> result = customers.aggregate(List.of(
            lookup("Orders", "_id", "customer", "orders"),
            match(new Document("orders.0", new Document("$exists", false))),
            project(fields(include("companyName")))
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q14------------");
        System.out.println("Customers without order: " + result.size());
        // for (Document doc: result) {
        //    System.out.println(doc.get("companyName"));
        // }
    }
}
