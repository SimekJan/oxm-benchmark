package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Find all cities in Customers and Suppliers (in either)
 */
public class Query12_union implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q12---------");

        String sql = "SELECT s.city " +
            "FROM Suppliers s " +
            "UNION " +
            "SELECT c.city " +
            "FROM Customers c ";

        List<String> results = session.createNativeQuery(sql).getResultList();

        System.out.println("Found " + results.size() + " cities.");
        // for (String city: results) {
        //    System.out.println(city);
        // }
    }
}
