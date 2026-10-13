package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Products based on unit price
 * (not indexed column)
 */
public class Query15_sorting implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (p:Product)
                RETURN p.productName AS product, p.unitPrice AS price
                ORDER BY price
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q15------------");
        System.out.println("Products number: " + resultList.size());
        // for (Map<String,Object> row : results) {
        //    String product = (String) row.get("product");
        //    Double price = (Double) row.get("price");
        //    System.out.println(product + ": " + price);
        // }
    }
}
