package cz.cuni.mff.simekjan.relational.queries.complex;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Select products with bellow average price.
 */
public class C_Query9_subquery implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-CQ9---------");

        String hql = "SELECT p.productName " +
            "FROM Products p " +
            "WHERE p.unitPrice <= (" +
            "   SELECT AVG(p2.unitPrice) " +
            "   FROM Products p2 " +
            ")";

        List<String> result = session.createQuery(hql, String.class).getResultList();

        for (String productName : result) {
            System.out.println(productName);
        }

    }
}
