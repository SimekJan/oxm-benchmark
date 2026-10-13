package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.QX_No_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Count the number of employees per city.
 */
public class Query05_count implements RelationalQuery<QX_No_Params> {

    @Override
    public void run(QX_No_Params params) {
        System.out.println("----------PostgreSQL-Q5----------");

        String hql = "SELECT e.city, COUNT(e) " +
            "FROM Employees e " +
            "GROUP BY e.city";

        List<Object[]> results = session.createQuery(hql, Object[].class).getResultList();

        System.out.println("Found " + results.size() + " employees.");
        // for (Object[] line: results) {
        //    System.out.println(line[0] + ": " + line[1]);
        // }
    }
}

/*
 *       TODO: Co čistě COUNT (a MAX) bez GROUP BY ?
 *
 *       GROUPing bude potenciálně stát další operace.
 *       COUNT samotný může mít taky rozdíly v implementaci: projít prvky / číst z metadat.
 *
 *       Ostatní články ale mají pouze verze s GROUP BY
 * */
