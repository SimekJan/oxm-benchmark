package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Join orders with employees on employee ID (indexed).
 */
public class Query07_join_indexed implements GraphQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {

        String query = """
                MATCH (e:Employee)-[:IS_RESPONSIBLE_FOR]->(o:Order)
                RETURN o.orderId AS order, e.firstName AS firstName, e.lastName AS lastName
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of());

        List<Map<String, Object>> resultList = new ArrayList<>();
        results.forEach(resultList::add);

        System.out.println("------------Neo4j-Q7-------------");
        System.out.println("Found " + resultList.size() + " join results.");
        // for (Map<String, Object> row : results) {
        //    Long orderId = (Long) row.get("order");
        //    String firstName = (String) row.get("firstName");
        //    String lastName = (String) row.get("lastName");
        //    System.out.println("Order " + orderId + " -> " + firstName + " " + lastName);
        // }
    }
}

/*
    TODO: Takto přes vztahy se mi zdá jako indexovaný join, ale možná je jiné než jako v 8 přes neindexované
 */
