package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Find all Customers without an order (all customers - (diff) customer_ids in orders)
 */
public class Query14_diff implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (c:Customer)
                WHERE NOT EXISTS {
                    MATCH (:Order)-[:IS_CUSTOMERS_ORDER]->(c)
                }
                RETURN c.companyName AS companyName
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q14------------");
        System.out.println("Found " + resultList.size() + " customers without an order.");
        // for (Map<String,Object> row : results) {
        //    String company = (String) row.get("companyName");
        //    System.out.println(company);
        // }
    }
}
