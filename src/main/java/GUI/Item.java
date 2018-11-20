package GUI;

/**
 * Item is object used in the Shopping list as an list item.
 * @author Hanna Haataja, hanna.haataja@cs.tamk.fi
 * @version 1.0, 11/20/2018
 * @since 1.0
 */
public class Item {
    private int quantity;
    private String name;

    /**
     * Constructor that sets name of the item and quantity.
     * @param name the name of the item.
     * @param quantity The quantity to bee bought from the shop.
     */
    public Item(String name, int quantity){
        setName(name);
        setQuantity(quantity);
    }

    /**
     * Gets the quantity of the object.
     * @return int quantity.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the object.
     * @param quantity int quantity that is set, must be zero or greater.
     */
    public void setQuantity(int quantity) {
        if(quantity >= 0){
            this.quantity = quantity;
        } else {
            throw new RuntimeException("Invalid input");
        }
    }

    /**
     * Gets the name of the item.
     * @return Name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the item.
     * @param name Name of the item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Subtracts quantity by given number.
     * If given number greater than quantity, quantity is set to zero.
     * @param number Number that is subtracted from quantity.
     */
    public void remove(int number){
        if(quantity > number){
            quantity -= number;
        } else {
            quantity = 0;
        }
    }

    /**
     * Adds given number to quantity.
     * @param number Number that is added to quantity.
     */
    public void add(int number){
        quantity += number;
    }

    /**
     * Adds one to quantity.
     */
    public void add(){
        quantity++;
    }
}
