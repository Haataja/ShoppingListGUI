package GUI.database;

import GUI.ShoppingList;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

/**
 * ConnectionHelper is class that holds all thing related to H2 connection and it uses Hibernate.
 *
 * @author Hanna Haataja, hanna.haataja@tuni.fi
 * @version 3.0, 12/15/2018
 * @since 3.0
 */
public class ConnectionHelper {
    private Session session;
    private SessionFactory sessionFactory;

    /**
     * Connects to the H2 database.
     * @throws Exception Throws exception if session is null, after opening it.
     */
    public void connect() throws Exception{
        sessionFactory = buildSessionFactory();
        session = sessionFactory.openSession();
        if(session == null){
            throw new Exception("Connection error");
        }
    }

    /**
     * Closes session and sessionFactory if they are opened successfully.
     */
    public void close(){
        if(session != null){
            session.close();
            sessionFactory.close();
        }
    }

    /**
     * Adds items to the H2 database.
     * @param itemList Items to be added.
     */
    public void writeToDatabase(List<ShoppingList> itemList){
        for(ShoppingList item: itemList){
            //System.out.println("ITEM: " + item.getName()+ " " +item.getQuantity());
            session.save(item);
        }
    }

    /**
     * Collects from the H2 database and adds to the table.
     * @param data Items that are shown in the table.
     */
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

    /**
     * Gets the session.
     * @return Session aka. Connection.
     */
    public Session getSession() {
        return session;
    }


    private static SessionFactory buildSessionFactory(){
        return new Configuration().configure().addAnnotatedClass(ShoppingList.class).buildSessionFactory();
    }
}
