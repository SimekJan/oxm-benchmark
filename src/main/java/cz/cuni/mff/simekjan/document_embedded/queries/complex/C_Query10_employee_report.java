package cz.cuni.mff.simekjan.document_embedded.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document_embedded.queries.EmbeddedDocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ10_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Create a complex real-world-like report including employee info
 */
public class C_Query10_employee_report implements EmbeddedDocumentQuery<CQ10_Params> {

    @Override
    public void run(CQ10_Params params) {

        System.out.println("----------E-Mongo-CQ10-----------");

        MongoCollection<Document> employees = db.getCollection("Employees");

        List<Document> results = employees.aggregate(List.of(

            new Document("$lookup",
                new Document("from", "Orders")
                    .append("localField", "_id")
                    .append("foreignField", "employee")
                    .append("as", "orders")
            ),

            new Document("$addFields",
                new Document("orderCount",
                    new Document("$size", "$orders")
                ).append("totalPrice",
                    new Document("$sum",
                        new Document("$map",
                            new Document("input", "$orders")
                                .append("as", "order")
                                .append("in",
                                    new Document("$sum", "$$order.products.unitPrice")
                                )
                        )
                    )
                )
            ),

            new Document("$lookup",
                new Document("from", "Employees")
                    .append("localField", "_id")
                    .append("foreignField", "reportsTo")
                    .append("as", "subordinates")
            ),

            new Document("$addFields",
                new Document("subordinateCount",
                    new Document("$size", "$subordinates")
                ).append("yearsWorked",
                    new Document("$dateDiff",
                        new Document("startDate", "$hireDate")
                            .append("endDate", "$$NOW")
                            .append("unit", "year")
                    )
                ).append("age",
                    new Document("$dateDiff",
                        new Document("startDate", "$birthDate")
                            .append("endDate", "$$NOW")
                            .append("unit", "year")
                    )
                )
            ),

            new Document("$project",
                new Document("_id", 0)
                    .append("employeeId", "$employeeId")
                    .append("firstName", 1)
                    .append("lastName", 1)
                    .append("orderCount", 1)
                    .append("totalPrice", 1)
                    .append("subordinateCount", 1)
                    .append("yearsWorked", 1)
                    .append("age", 1)
            )

        ), Document.class).into(new ArrayList<>());

        System.out.println("Report included " + results.size() + " lines.");
        /*
            for (Document row : results) {
                System.out.println("Employee: " +
                    row.get("employeeId") + " " +
                    row.getString("firstName") + " " +
                    row.getString("lastName")
                );

                System.out.println("Manages " + row.get("orderCount") +
                    " orders, with total price of " + row.get("totalPrice"));

                System.out.println("Supervises " + row.get("subordinateCount") + " employee(s)");
                System.out.println("Works for " + row.get("yearsWorked") + " years. Aged: " + row.get("age"));
                System.out.println("-----------------------------------------------------------------------------------");
            }
        */
    }
}
