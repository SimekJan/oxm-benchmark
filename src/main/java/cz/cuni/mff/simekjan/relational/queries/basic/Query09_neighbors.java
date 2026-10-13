package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Find all direct and indirect connections between suppliers (max depth = 2).
 */
public class Query09_neighbors implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q9----------");

        String hql1 = """
            SELECT DISTINCT s, o1
            FROM Suppliers s
            JOIN s.suppliesTo o1
            WHERE s.id <> o1.id
            """;

        List<Object[]> results1 = session.createQuery(hql1).getResultList();

        System.out.println("Found " + results1.size() + " connections with depth 1.");
        // for (Object[] row : results1) {
        //    Supplier from = (Supplier) row[0];
        //    Supplier to = (Supplier) row[1];

        //    System.out.println(from.getCompanyName() + " -> " + to.getCompanyName() + " with depth 1");
        // }

        String hql2 = """
                SELECT DISTINCT s, o2
                FROM Suppliers s
                JOIN s.suppliesTo o1
                JOIN o1.suppliesTo o2
                WHERE s.id <> o2.id
            """;

        List<Object[]> results2 = session.createQuery(hql2).getResultList();

        System.out.println("Found " + results2.size() + " connections with depth 2.");
        // for (Object[] row : results2) {
        //    Supplier from = (Supplier) row[0];
        //    Supplier to = (Supplier) row[1];

        //    System.out.println(from.getCompanyName() + " -> " + to.getCompanyName() + " with depth 2");
        // }
    }
}

/*
    TODO: Takhle nebo native SQL s WITH RECOURSIVE, oboje nic moc
 */
