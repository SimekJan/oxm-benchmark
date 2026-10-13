package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Find the most expensive product per category (maximum).
 */
public class Query06_max implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (p:Product)
                RETURN p.category AS category, max(p.unitPrice) AS maxUnitPrice
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q6-------------");
        System.out.println("Prices measured in " + resultList.size() + " categories.");
        // for (Map<String, Object> row : results) {
        //    String category = (String) row.get("category");
        //    Double maxUnitPrice = (Double) row.get("maxUnitPrice");
        //    System.out.println(category + ": " + maxUnitPrice);
        //}
    }
}
