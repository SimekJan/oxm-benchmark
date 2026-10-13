package cz.cuni.mff.simekjan.document.queries.basic;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.northwind.Customer;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Aggregates.group;

/**
 * Distinct customer cities
 */
public class Query17_distinct implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Customer> customers = db.getCollection("Customers", Customer.class);

        List<Document> result = customers.aggregate(List.of(
            group("$city")
        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-Q17------------");
        System.out.println("Distinct cities: " + result.size());
    }
}
