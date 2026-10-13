package cz.cuni.mff.simekjan.relational.queries_legacy.schema;

import cz.cuni.mff.simekjan.relational.queries_legacy.Query;
import org.hibernate.Session;

import java.util.List;

public class S_Query1_list_tables implements Query {
    @Override
    public void perform(Session session) {
        String sql = "SELECT table_name " +
            "FROM information_schema.tables " +
            "WHERE table_schema = 'public' " +
            "AND table_type = 'BASE TABLE'";

        List<?> results = session.createNativeQuery(sql).getResultList();

        for (Object lineObj : results) {
            String table_name = (String) lineObj;
            System.out.println(table_name);
        }
    }
}
