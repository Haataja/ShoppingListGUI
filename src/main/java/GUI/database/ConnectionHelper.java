package GUI.database;

import GUI.ShoppingList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ConnectionHelper {
    private Session session;
    private SessionFactory sessionFactory;

    public void connect(){
        sessionFactory = buildSessionFactory();
        session = sessionFactory.openSession();
    }

    public void close(){
        session.close();
        sessionFactory.close();
    }

    public void writeToDatabase(List<ShoppingList> itemList){
        for(ShoppingList item: itemList){
            //System.out.println("ITEM: " + item.getName()+ " " +item.getQuantity());
            session.save(item);
        }
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private static SessionFactory buildSessionFactory(){
        try{
            return new Configuration().configure().addAnnotatedClass(ShoppingList.class).buildSessionFactory();
        } catch (Error e){
            e.printStackTrace();
        }
        return null;
    }
}
