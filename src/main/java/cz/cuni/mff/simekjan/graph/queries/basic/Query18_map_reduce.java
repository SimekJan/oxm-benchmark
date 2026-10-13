package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Number of products by supplier
 */
public class Query18_map_reduce implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (s:Supplier)<-[:IS_PRODUCED_BY]-(p:Product)
                RETURN s, s.companyName AS supplier, count(p) AS productCount
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q18------------");
        System.out.println("Found " + resultList.size() + " suppliers");
        /*for (Map<String,Object> row : results) {
            String supplier = (String) row.get("supplier");
            Long count = (Long) row.get("productCount");
            System.out.println(supplier + ": " + count);
        }*/
    }
}

/*
    This is not a true map-reduce
    Included only for comparison
 */
