package cz.cuni.mff.simekjan.relational.queries.complex;

import cz.cuni.mff.simekjan.query_params.params.CQ1_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Max price of products with name starting with "S" per category.
 */
public class C_Query1_filter_max implements RelationalQuery<CQ1_Params> {

    @Override
    public void run(CQ1_Params params) {
        System.out.println("----------PostgreSQL-CQ1---------");

        String hql = "SELECT p.category, MAX(p.unitPrice) " +
            "FROM Products p " +
            "WHERE p.productName LIKE CONCAT(:startingLetter, '%') " +
            "GROUP BY p.category";

        List<Object[]> results = session.createQuery(hql, Object[].class)
            .setParameter("startingLetter", params.productNameStartingLetter())
            .getResultList();

        for (Object[] row : results) {
            String category = (String) row[0];
            Double maxPrice = (Double) row[1];

            System.out.println(category + " - " + maxPrice);
        }
    }
}
