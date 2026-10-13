package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.Map;

/**
 * Order employees from most orders to least (include order count), show only employees having more than one order.
 */
public class C_Query4_group_by_having_sort implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (e:Employee)
                MATCH (e)-[:IS_RESPONSIBLE_FOR]->(o:Order)
                WITH e.firstName AS firstName, e.lastName AS lastName, COUNT(o) AS orderCount
                WHERE COUNT(o) > 1
                ORDER BY orderCount DESC
                RETURN firstName, lastName, orderCount
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        System.out.println("------------Neo4j-CQ4------------");
        for (Map<String, Object> row : results) {
            String firstName = (String) row.get("firstName");
            String lastName = (String) row.get("lastName");
            Long count = (Long) row.get("orderCount");
            System.out.println(firstName + " " + lastName + " - " + count);
        }
    }
}
