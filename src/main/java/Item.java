public class Item {
    private int quantity;
    private String name;

    public Item(String name, int quantity){
        setName(name);
        setQuantity(quantity);
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if(quantity >= 0){
            this.quantity = quantity;
        } else {
            throw new RuntimeException("Invalid input");
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void remove(int number){
        if(quantity > number){
            quantity -= number;
        } else {
            quantity = 0;
        }
    }

    public void add(int number){
        quantity += number;
    }

    public void add(){
        quantity++;
    }
}
