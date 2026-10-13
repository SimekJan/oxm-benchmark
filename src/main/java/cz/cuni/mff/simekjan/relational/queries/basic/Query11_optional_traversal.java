package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Get all suppliers and their supplier count (even if 0)
 */
public class Query11_optional_traversal implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q11---------");

        String sql = """
                SELECT
                    s.id AS supplier_id,
                    s.company_name AS supplier,
                    COUNT(sr.supplied_to_id) AS supplied_count
                FROM suppliers s
                LEFT JOIN supplier_relationship sr
                    ON sr.supplier_id = s.id
                GROUP BY s.id, s.company_name;
            """;

        List<Object[]> results = session.createNativeQuery(sql).getResultList();

        System.out.println("Found " + results.size() + " suppliers.");
        // for (Object[] row : results) {
        //    String supplier = (String) row[0];
        //    Number count = (Number) row[1];

        //    System.out.println(supplier + ": " + count.longValue());
        // }
    }
}
