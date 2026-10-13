package cz.cuni.mff.simekjan.relational.queries.complex;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Join employees with orders and order by employee name.
 */
public class C_Query2_join_sort implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-CQ2---------");

        String hql = "SELECT e.firstName, e.lastName, o.orderId " +
            "FROM Employees e " +
            "JOIN e.orders o " +
            "ORDER BY e.lastName, e.firstName";

        List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();

        for (Object[] row : results) {
            String customerId = (String) row[0];
            String companyName = (String) row[1];
            Long totalOrders = (Long) row[2];

            System.out.println(customerId + " " + companyName + " -> " + totalOrders);
        }
    }
}
