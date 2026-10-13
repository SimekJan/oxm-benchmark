package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Sort products based on product_id
 * (indexed column)
 */
public class Query16_sorting_indexed implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q16---------");

        String hql = "SELECT p.productId, p.productName " +
            "FROM Products p " +
            "ORDER BY p.productId";

        List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();

        System.out.println("Sorted " + results.size() + " products.");
        // for (Object[] row : results) {
        //    Long productId = (Long) row[0];
        //    String productName = (String) row[1];
        //    System.out.println(productId + ": " + productName);
        // }
    }
}
