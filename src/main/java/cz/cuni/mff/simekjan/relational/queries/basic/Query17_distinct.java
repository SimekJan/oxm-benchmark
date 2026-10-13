package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Find distinct customer cities
 */
public class Query17_distinct implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q17---------");

        String hql = "SELECT DISTINCT (c.city) " +
            "FROM Customers c ";

        List<String> results = session.createQuery(hql, String.class).getResultList();

        System.out.println("Found " + results.size() + " distinct cities.");
        // for (String country : results) {
        //    System.out.println(country);
        // }
    }
}
