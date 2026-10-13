package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.Map;

/**
 * Join employees with orders and order by employee name.
 */
public class C_Query2_join_sort implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (e:Employee)-[:IS_RESPONSIBLE_FOR]->(o:Order)
                RETURN o.orderId AS order, e.firstName AS firstName, e.lastName AS lastName
                ORDER BY e.lastName, e.firstName
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        System.out.println("------------Neo4j-CQ2------------");
        for (Map<String, Object> row : results) {
            Long orderId = (Long) row.get("order");
            String firstName = (String) row.get("firstName");
            String lastName = (String) row.get("lastName");
            System.out.println("Order " + orderId + " -> " + firstName + " " + lastName);
        }
    }
}
