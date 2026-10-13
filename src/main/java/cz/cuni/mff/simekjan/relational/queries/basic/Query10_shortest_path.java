package cz.cuni.mff.simekjan.relational.queries.basic;

import cz.cuni.mff.simekjan.query_params.params.Q10_Params;
import cz.cuni.mff.simekjan.relational.queries.RelationalQuery;

import java.util.List;

/**
 * Find the shortest path between two given suppliers
 */
public class Query10_shortest_path implements RelationalQuery<Q10_Params> {

    @Override
    public void run(Q10_Params params) {
        System.out.println("----------PostgreSQL-Q10---------");

        String sql = """
                WITH RECURSIVE path AS (
                    SELECT
                        s.id,
                        s.company_name,
                        s.supplier_id,
                        ARRAY[s.id] AS path_ids,
                        CAST(ARRAY[s.company_name] AS varchar[]) AS path_nodes,
                        0 AS depth
                    FROM suppliers s
                    WHERE s.supplier_id = :fromSupplier
            
                    UNION ALL
            
                    SELECT
                        next.id,
                        next.company_name,
                        next.supplier_id,
                        p.path_ids || next.id,
                        p.path_nodes || next.company_name,
                        p.depth + 1
                    FROM path p
                    JOIN supplier_relationship sr
                        ON sr.supplier_id = p.id
                    JOIN suppliers next
                        ON next.id = sr.supplied_to_id
                    WHERE p.depth < 10
                      AND NOT (next.id = ANY(p.path_ids))
                )
                SELECT array_to_string(path_nodes, ' -> ') AS path
                FROM path
                WHERE supplier_id = :toSupplier
                ORDER BY depth
                LIMIT 1
            """;

        List<?> results = session.createNativeQuery(sql)
            .setParameter("fromSupplier", params.supplierIdFrom())
            .setParameter("toSupplier", params.supplierIdTo())
            .getResultList();

        if (results.isEmpty()) {
            System.out.println("No path found");
        } else {
            String path = (String) results.get(0);

            System.out.println(path);
        }
    }
}
