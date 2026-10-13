package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.Map;

/**
 * Order customers from most orders to least (include order count).
 */
public class C_Query3_group_by_sort_join implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (c:Customer)
                OPTIONAL MATCH (o:Order)-[:IS_CUSTOMERS_ORDER]->(c)
                RETURN c.companyName AS customer, c.customerId AS customerId, COUNT(o) AS orderCount
                ORDER BY orderCount DESC
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        System.out.println("------------Neo4j-CQ3------------");
        for (Map<String, Object> row : results) {
            String customer = (String) row.get("customer");
            Long customerId = (Long) row.get("customerId");
            Long count = (Long) row.get("orderCount");
            System.out.println(customer + " - " + customerId + " - " + count);
        }
    }
}
