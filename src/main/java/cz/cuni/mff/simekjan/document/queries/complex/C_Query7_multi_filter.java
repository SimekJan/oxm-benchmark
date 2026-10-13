package cz.cuni.mff.simekjan.document.queries.complex;

import com.mongodb.client.MongoCollection;
import cz.cuni.mff.simekjan.document.queries.DocumentQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ7_Params;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

/**
 * Select only employees with firstname starting with a given letter,
 * lastname starting with a given letter,
 * given city, birthdate before given date and date of hire after given date.
 */
public class C_Query7_multi_filter implements DocumentQuery<CQ7_Params> {

    @Override
    public void run(CQ7_Params params) {

        MongoCollection<Document> employees = db.getCollection("Employees");

        List<Document> results = employees.aggregate(List.of(

            // order can be optimized internally by Mongo, so it does not matter much
            new Document("$match",
                new Document("firstName",
                    new Document("$regex", "^" + params.firstNameStartingLetter())
                )
                    .append("lastName",
                        new Document("$regex", "^" + params.lastNameStartingLetter())
                    )
                    .append("city", params.cityName())
                    .append("birthDate",
                        new Document("$lt", params.birthDate())
                    )
                    .append("hireDate",
                        new Document("$gt", params.hireDate())
                    )
            ),

            new Document("$project",
                new Document("_id", 0)
                    .append("firstName", 1)
                    .append("lastName", 1)
            )

        ), Document.class).into(new ArrayList<>());

        System.out.println("------------Mongo-CQ7------------");
        for (Document row : results) {
            String firstName = row.getString("firstName");
            String lastName = row.getString("lastName");

            System.out.println(firstName + " " + lastName);
        }
    }
}
