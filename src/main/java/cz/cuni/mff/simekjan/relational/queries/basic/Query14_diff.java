package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Find all Customers without an order (all customers - (diff) customer_ids in orders)
 * This is not a best example for set operation, there are better alternatives for this task
 * like NOT EXIST / LEFT JOIN
 */
public class Query14_diff implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q14---------");

        String hql = """
                SELECT c.companyName
                FROM Customers c
                WHERE NOT EXISTS (
                    SELECT 1
                    FROM Orders o
                    WHERE o.customer.id = c.id
                )
            """;

        List<String> results = session.createQuery(hql, String.class).getResultList();

        System.out.println("Found " + results.size() + " customers without an order.");
        // for (String companyName : results) {
        //    System.out.println(companyName);
        // }
    }
}
