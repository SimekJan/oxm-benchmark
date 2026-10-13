package cz.cuni.mff.simekjan.graph.queries.basic;

import cz.cuni.mff.simekjan.graph.northwind.Product;
import cz.cuni.mff.simekjan.graph.queries.GraphQuery;
import cz.cuni.mff.simekjan.query_params.params.Q4_Params;
import org.neo4j.ogm.cypher.BooleanOperator;
import org.neo4j.ogm.cypher.ComparisonOperator;
import org.neo4j.ogm.cypher.Filter;
import org.neo4j.ogm.cypher.Filters;

import java.util.Collection;

/**
 * Get all products in price range 9.5 - 12.5 (Range query).
 */
public class Query04_range_query_non_indexed implements GraphQuery<Q4_Params> {

    @Override
    public void run(Q4_Params params) {

        Filter minLastName = new Filter("unitPrice", ComparisonOperator.GREATER_THAN_EQUAL, params.unitPriceFrom());
        Filter maxLastName = new Filter("unitPrice", ComparisonOperator.LESS_THAN, params.unitPriceTo());
        minLastName.setBooleanOperator(BooleanOperator.AND);

        Filters range = maxLastName.and(minLastName);

        Collection<Product> result = session.loadAll(Product.class, range);

        System.out.println("------------Neo4j-Q4-------------");
        System.out.println("Found " + result.size() + " products in range.");
        // for (Product p : result) {
        //    System.out.println(p.getProductName() + ": " + p.getUnitPrice());
        // }
    }
}
