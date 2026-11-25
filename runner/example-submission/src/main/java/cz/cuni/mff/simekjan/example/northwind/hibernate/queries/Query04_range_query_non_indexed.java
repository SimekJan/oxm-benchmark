package cz.cuni.mff.simekjan.example.northwind.hibernate.queries;

import cz.cuni.mff.simekjan.example.northwind.hibernate.entities.Product;
import cz.cuni.mff.simekjan.example.northwind.params.Q4_Params;
import cz.cuni.mff.simekjan.runner.backend.hibernate.HibernateQuery;
import org.hibernate.Session;

/**
 * Get all products in price range.
 */
public class Query04_range_query_non_indexed implements HibernateQuery<Q4_Params> {

    @Override
    public String id() {
        return "Q4";
    }

    @Override
    public Class<Q4_Params> parameterType() {
        return Q4_Params.class;
    }

    @Override
    public boolean isOrdered() {
        return false;
    }

    @Override
    public Object run(Session session, Q4_Params params) {

        String hql = "FROM Products p " +
            "WHERE p.unitPrice BETWEEN :price_from AND :price_to";

        return session.createQuery(hql, Product.class)
            .setParameter("price_from", params.unitPriceFrom().doubleValue())
            .setParameter("price_to", params.unitPriceTo().doubleValue())
            .getResultList();
    }
}
