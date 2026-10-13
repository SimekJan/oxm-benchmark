package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.Map;

/**
 * Select products with bellow average price.
 */
public class C_Query9_subquery implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (p:Product)
                WITH AVG(p.unitPrice) AS avgPrice
                MATCH (p:Product)
                WHERE p.unitPrice <= avgPrice
                RETURN p.productName
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        System.out.println("------------Neo4j-CQ9------------");
        for (Map<String, Object> row : results) {
            String customerName = (String) row.get("p.productName");
            System.out.println(customerName);
        }
    }
}
