package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Count the number of employees per city.
 */
public class Query05_count implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (e:Employee)
                RETURN e.city AS city, count(e) AS cnt
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q5-------------");
        System.out.println("Employee in " + resultList.size() + " cities.");
        // for (Map<String, Object> row : results) {
        //    String city = (String) row.get("city");
        //    Long count = (Long) row.get("cnt");
        //    System.out.println(city + ": " + count);
        // }
    }
}
