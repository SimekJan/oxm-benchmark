package cz.cuni.mff.simekjan.graph.queries.complex;

import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.CQ5_Params;

import java.util.Map;

/**
 * Join suppliers which are from cities starting wit 'P' with their "Packaging Materials" products.
 */
public class C_Query5_filter_join implements GraphQuery<CQ5_Params> {

    @Override
    public void run(CQ5_Params params) {

        String query = """
                MATCH (p:Product)-[:IS_PRODUCED_BY]->(s:Supplier)
                WHERE s.city STARTS WITH $startingLetter AND p.category = $category
                RETURN p.productName AS product, s.companyName AS company
            """;

        Iterable<Map<String, Object>> results = session.query(query, Map.of(
            "$startingLetter", params.supplierCityStartingLetter(),
            "$category", params.productCategory()
        ));

        System.out.println("------------Neo4j-CQ5------------");
        for (Map<String, Object> row : results) {
            String product = (String) row.get("product");
            String company = (String) row.get("company");
            System.out.println(company + " - " + product);
        }
    }
}
