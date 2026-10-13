package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Get all suppliers and their supplier count (even if 0)
 */
public class Query11_optional_traversal implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (s:Supplier)
                OPTIONAL MATCH (s)-[:SUPPLIES_TO]->(sub:Supplier)
                RETURN s, s.companyName AS supplier, count(sub) AS suppliedCount
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q11------------");
        System.out.println("Found " + resultList.size() + " suppliers");
        // for (Map<String,Object> row : results) {
        //    String supplier = (String) row.get("supplier");
        //    Long count = (Long) row.get("suppliedCount");
        //    System.out.println(supplier + ": " + count);
        // }
    }
}
