package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Number of products by supplier
 */
public class Query18_map_reduce implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q18---------");

        String hql = "SELECT s.supplierId, s.companyName, COUNT(*) " +
            "FROM Suppliers s " +
            "       JOIN Products p ON s.supplierId = p.supplier " +
            "GROUP BY s.supplierId, s.companyName";

        List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();

        System.out.println("Counted products for " + results.size() + " suppliers.");
        /*for (Object[] line: results) {
            System.out.println(line[0] + ": " + line[1]);
        }*/
    }
}

/*
    This is not a true map-reduce
    Included only for comparison
 */
