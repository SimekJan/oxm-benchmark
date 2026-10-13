package cz.cuni.mff.simekjan.relational.queries.complex;

import cz.cuni.mff.simekjan.query_params.params.CQ5_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Join suppliers which are from cities starting wit 'P' with their "Packaging Materials" products.
 */
public class C_Query5_filter_join implements RelationalQuery<CQ5_Params> {

    @Override
    public void run(CQ5_Params params) {
        System.out.println("----------PostgreSQL-CQ5---------");

        String hql = "SELECT s.companyName, p.productName " +
            "FROM Suppliers s " +
            "JOIN s.products p " +
            "WHERE s.city LIKE CONCAT(:startingLetter, '%') AND p.category = :category";

        List<Object[]> results = session.createQuery(hql, Object[].class)
            .setParameter("startingLetter", params.supplierCityStartingLetter())
            .setParameter("category", params.productCategory())
            .getResultList();

        for (Object[] row : results) {
            String companyName = (String) row[0];
            String productName = (String) row[1];

            System.out.println(companyName + " - " + productName);
        }
    }
}
