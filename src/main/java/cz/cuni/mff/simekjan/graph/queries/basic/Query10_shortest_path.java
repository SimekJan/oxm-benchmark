package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.Q10_Params;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Find the shortest path between two given suppliers
 */
public class Query10_shortest_path implements GraphQuery<Q10_Params> {

    @Override
    public void run(Q10_Params params) {

        String query = """
                MATCH (s1:Supplier {supplierId: $from}),
                      (s2:Supplier {supplierId: $to}),
                      p = shortestPath((s1)-[:SUPPLIES_TO*1..10]->(s2))
                RETURN [n IN nodes(p) | n.companyName] AS pathNodes
            """;

        Map<String, Object> queryParams = Map.of(
            "from", params.supplierIdFrom(),
            "to", params.supplierIdTo()
        );

        Iterable<Map<String, Object>> results = session.query(query, queryParams);

        System.out.println("------------Neo4j-Q10------------");
        for (Map<String, Object> row : results) {
            String[] pathArray = (String[]) row.get("pathNodes");
            List<String> path = Arrays.asList(pathArray);
            System.out.println(path);
        }
    }
}
