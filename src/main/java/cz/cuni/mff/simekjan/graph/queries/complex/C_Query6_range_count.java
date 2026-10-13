package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ6_Params;

import java.util.Map;

/**
 * Count products with unitPrice between 10 and 15 per category
 */
public class C_Query6_range_count implements GraphQuery<CQ6_Params> {

    @Override
    public void run(CQ6_Params params) {

        String query = """
                MATCH (p:Product)
                WHERE p.unitPrice >= $min AND p.unitPrice <= $max
                RETURN p.category AS category, COUNT(p) AS productCount
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of(
            "$min", params.unitPriceFrom(),
            "$max", params.unitPriceTo()
        ));

        System.out.println("------------Neo4j-CQ6------------");
        for (Map<String, Object> row : results) {
            String category = (String) row.get("category");
            Long count = (Long) row.get("productCount");
            System.out.println(category + ": " + count);
        }
    }
}
