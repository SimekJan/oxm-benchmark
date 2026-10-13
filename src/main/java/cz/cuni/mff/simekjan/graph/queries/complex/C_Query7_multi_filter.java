package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ7_Params;

import java.util.Map;

/*
 * Select only employees with firstname starting with R, lastname starting with M,
 * city 'Reykjavik' birthdate before 2000 and date of hire after 2014.
 */
public class C_Query7_multi_filter implements GraphQuery<CQ7_Params> {

    @Override
    public void run(CQ7_Params params) {

        String query = """
                MATCH (e:Employee)
                WHERE e.firstName STARTS WITH $startingFirstName
                    AND e.lastName STARTS WITH $startingLastName
                    AND e.city = $city
                    AND e.birthDate < $maxBirthDate
                    AND e.hireDate > $minHireDate
                RETURN e.firstName AS firstName, e.lastName AS lastName
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of(
            "$startingFirstName", params.firstNameStartingLetter(),
            "$startingLastName", params.lastNameStartingLetter(),
            "$city", params.cityName(),
            "$maxBirthDate", params.birthDate().toString(),
            "$minHireDate", params.hireDate().toString()
        ));

        System.out.println("------------Neo4j-CQ7------------");
        for (Map<String, Object> row : results) {
            String firstName = (String) row.get("firstName");
            String lastName = (String) row.get("lastName");
            System.out.println(firstName + " " + lastName);
        }
    }
}
