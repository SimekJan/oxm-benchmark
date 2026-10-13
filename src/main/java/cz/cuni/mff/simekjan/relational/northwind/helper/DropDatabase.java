package cz.cuni.mff.simekjan.relational.northwind.helper;

import cz.cuni.mff.simekjan.relational.HibernateSessionManager;
import org.hibernate.Session;

public class DropDatabase {

    public static void main(String[] args) {
        Session session = HibernateSessionManager.getSession();

        session.beginTransaction();

        session.createNativeQuery("DROP SCHEMA public CASCADE").executeUpdate();
        session.createNativeQuery("CREATE SCHEMA public").executeUpdate();

        session.getTransaction().commit();

        session.close();
        System.out.println("Database reset complete");
    }
}
