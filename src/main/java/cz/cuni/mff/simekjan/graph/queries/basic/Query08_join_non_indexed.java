package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Join customers and employees on city (non-indexed)
 */
public class Query08_join_non_indexed implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (c:Customer)
                MATCH (e:Employee)
                WHERE c.city = e.city
                RETURN c.companyName AS customer,
                       e.firstName AS name,
                       c.city AS city
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q8-------------");
        System.out.println("Found " + resultList.size() + " join results (non-indexed).");
        // for (Map<String, Object> row : results) {
        //    String customer = (String) row.get("customer");
        //    String name = (String) row.get("name");
        //    String city = (String) row.get("city");
        //    System.out.println(customer + " - " + city + " - " + name);
        // }
    }
}
