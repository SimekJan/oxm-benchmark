package cz.cuni.mff.simekjan.relational.queries_legacy.basic_queries;

import cz.cuni.mff.simekjan.relational.queries_legacy.Query;
import org.hibernate.Session;

import java.util.List;

/**
 * Filtering using regex
 */
public class Query1 implements Query {

    @Override
    public void perform(Session session) {
        String hql = "SELECT p.productName, p.unitsInStock, p.unitPrice " +
            "FROM Products p " +
            "WHERE p.productName LIKE 'S%' " +
            "AND p.unitsInStock <= 50 " +
            "AND p.unitPrice >= 15.0";

        List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();

        for (Object[] row : results) {
            String productName = (String) row[0];
            Short unitsInStock = (Short) row[1];
            Float unitPrice = (Float) row[2];
            System.out.println(productName + " - " + unitsInStock + " - " + unitPrice);
        }
    }
}
