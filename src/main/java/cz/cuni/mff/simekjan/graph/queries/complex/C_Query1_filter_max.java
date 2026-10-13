package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ1_Params;

import java.util.Map;

/**
 * Max price of products with name starting with given letter per category.
 */
public class C_Query1_filter_max implements GraphQuery<CQ1_Params> {

    @Override
    public void run(CQ1_Params params) {
        String query = """
                MATCH (p:Product)
                WHERE p.productName STARTS WITH $startingLetter
                RETURN p.category AS category, max(p.unitPrice) AS maxUnitPrice
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of(
            "$startingLetter", params.productNameStartingLetter()
        ));

        for (Map<String, Object> row : results) {
            String category = (String) row.get("category");
            Double maxUnitPrice = (Double) row.get("maxUnitPrice");
            System.out.println(category + ": " + maxUnitPrice);
        }
    }
}
