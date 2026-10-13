package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * Find all direct and indirect connections between suppliers (max depth = 2).
 */
public class Query09_neighbors implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (s:Supplier)-[:SUPPLIES_TO*1..2]->(other:Supplier)
                WHERE s <> other
                RETURN s.companyName AS fromSupplier, other.companyName AS toSupplier
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q9-------------");
        System.out.println("Found " + resultList.size() + " connections");
        // for (Map<String, Object> row : results) {
        //    String fromSupplier = (String) row.get("fromSupplier");
        //    String toSupplier = (String) row.get("toSupplier");
        //    System.out.println(fromSupplier + ": " + toSupplier);
        // }
    }
}
