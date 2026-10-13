package cz.cuni.mff.simekjan.relational.queries.complex;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Order customers from most orders to least (include order count).
 */
public class C_Query3_group_by_sort_join implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-CQ3---------");

        String hql = "SELECT c.customerId, c.companyName, COUNT(o.orderId) " +
            "FROM Customers c " +
            "LEFT JOIN c.orders o " +
            "GROUP BY c.customerId, c.companyName " +
            "ORDER BY COUNT(o.orderId) DESC";

        List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();

        for (Object[] row : results) {
            Long customerId = (Long) row[0];
            String companyName = (String) row[1];
            Long totalOrders = (Long) row[2];

            System.out.println(customerId + " - " + companyName + " - " + totalOrders);
        }
    }
}
