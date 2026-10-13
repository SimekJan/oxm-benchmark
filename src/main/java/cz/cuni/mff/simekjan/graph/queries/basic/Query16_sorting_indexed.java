package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Sort products based on product_id
 * (indexed column)
 */
public class Query16_sorting_indexed implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (p:Product)
                RETURN p.productId, p.productName
                ORDER BY p.productId
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q16------------");
        System.out.println("Products number: " + resultList.size());
        // for (Map<String,Object> row : results) {
        //    Long productId = (Long) row.get("p.productId");
        //    String productName = (String) row.get("p.productName");
        //    System.out.println(productId + ": " + productName);
        // }
    }
}
