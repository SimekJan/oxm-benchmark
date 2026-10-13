package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Find all cities in Customers and Suppliers (in either)
 */
public class Query12_union implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (c:Customer)
                RETURN c.city AS city
                UNION
                MATCH (s:Supplier)
                RETURN s.city AS city
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q12------------");
        System.out.println("Found " + resultList.size() + " cities in total.");
        // for (Map<String,Object> row : results) {
        //    String city = (String) row.get("city");
        //    System.out.println(city);
        // }
    }
}
