package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Products based on unit price
 * (not indexed column)
 */
public class Query15_sorting implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q15---------");

        String hql = "SELECT p.productName, p.unitPrice " +
            "FROM Products p " +
            "ORDER BY p.unitPrice";

        List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();

        System.out.println("Sorted " + results.size() + " products.");
        // for (Object[] row : results) {
        //    String productName = (String) row[0];
        //    Double unitPrice = (Double) row[1];
        //    System.out.println(productName + " - " + unitPrice);
        // }
    }
}
