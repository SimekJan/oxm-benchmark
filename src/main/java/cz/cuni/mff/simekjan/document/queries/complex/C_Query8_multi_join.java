package cz.cuni.mff.simekjan.document.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Join Customers, Employees, Orders, Products and Suppliers.
 */
public class C_Query8_multi_join implements DocumentQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        MongoCollection<Document> suppliers = db.getCollection("Suppliers");

        // Order is optimized by collection sizes and existing connection between tables
        List<Document> results = suppliers.aggregate(List.of(

            new Document("$match",
                new Document("companyName", "BluePeak Industries")
            ),

            new Document("$lookup",
                new Document("from", "Products")
                    .append("localField", "_id")
                    .append("foreignField", "supplier")
                    .append("as", "product")
            ),
            new Document("$unwind", "$product"),

            new Document("$match",
                new Document("product.productName", "Steel Plate")
            ),

            new Document("$lookup",
                new Document("from", "Orders")
                    .append("localField", "product._id")
                    .append("foreignField", "products")
                    .append("as", "order")
            ),
            new Document("$unwind", "$order"),

            // No filter here, no suitable filter found

            new Document("$lookup",
                new Document("from", "Employees")
                    .append("localField", "order.employee")
                    .append("foreignField", "_id")
                    .append("as", "employee")
            ),
            new Document("$unwind", "$employee"),

            new Document("$match",
                new Document("employee.firstName", "Jeffrey")
            ),

            new Document("$lookup",
                new Document("from", "Customers")
                    .append("localField", "order.customer")
                    .append("foreignField", "_id")
                    .append("as", "customer")
            ),
            new Document("$unwind", "$customer"),

            new Document("$match",
                new Document("customer.companyName", "StoneBridge")
            ),

            new Document("$project",
                new Document("_id", 0)
                    .append("customerName", "$customer.companyName")
                    .append("employeeFirstName", "$employee.firstName")
                    .append("orderDate", "$order.orderDate")
                    .append("productName", "$product.productName")
                    .append("supplierName", "$companyName")
            )

        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-CQ8------------");
        for (Document row : results) {
            System.out.println(
                row.getString("customerName") + " - " +
                    row.getString("employeeFirstName") + " - " +
                    row.get("orderDate") + " - " +
                    row.getString("productName") + " - " +
                    row.getString("supplierName")
            );
        }
    }
}
