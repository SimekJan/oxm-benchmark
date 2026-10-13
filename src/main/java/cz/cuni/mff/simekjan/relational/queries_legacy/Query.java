package cz.cuni.mff.simekjan.relational.queries_legacy;

import org.hibernate.Session;

public interface Query {
    public void perform(Session session);
}
