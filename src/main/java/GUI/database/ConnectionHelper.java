package GUI.database;

import GUI.ShoppingList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ConnectionHelper {
    private Session session;
    private SessionFactory sessionFactory;

    public void connect() throws Exception{
        sessionFactory = buildSessionFactory();
        session = sessionFactory.openSession();
        if(session == null){
            throw new Exception("Connection error");
        }
    }

    public void close(){
        if(session != null){
            session.close();
            sessionFactory.close();
        }
    }

    public void writeToDatabase(List<ShoppingList> itemList){
        for(ShoppingList item: itemList){
            //System.out.println("ITEM: " + item.getName()+ " " +item.getQuantity());
            session.save(item);
        }
    }

    public void readFromDatabase(List<ShoppingList> data){
        boolean keepGoing= true;
        int i = 1;
        while (keepGoing){
            if(session.get(ShoppingList.class,i) != null){
                data.add(session.get(ShoppingList.class,i));
            } else {
                keepGoing = false;
            }
            i++;
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
        return new Configuration().configure().addAnnotatedClass(ShoppingList.class).buildSessionFactory();
    }
}
