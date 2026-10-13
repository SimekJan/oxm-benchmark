package cz.cuni.mff.simekjan.relational.queries.complex;

import cz.cuni.mff.simekjan.query_params.params.CQ6_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Count products with unitPrice between 10 and 15 per category
 */
public class C_Query6_range_count implements RelationalQuery<CQ6_Params> {

    @Override
    public void run(CQ6_Params params) {
        System.out.println("----------PostgreSQL-CQ6---------");

        String hql = "SELECT p.category, COUNT(p.unitPrice) " +
            "FROM Products p " +
            "WHERE p.unitPrice BETWEEN :price_min AND :price_max " +
            "GROUP BY p.category";

        List<Object[]> results = session.createQuery(hql, Object[].class)
            .setParameter("price_min", params.unitPriceFrom())
            .setParameter("price_max", params.unitPriceTo())
            .getResultList();

        for (Object[] row : results) {
            String category = (String) row[0];
            Long count = (Long) row[1];
            System.out.println(category + " - " + count);
        }
    }
}
