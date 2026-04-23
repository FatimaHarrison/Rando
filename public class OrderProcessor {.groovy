public class OrderProcessor {

    private List<String> orders = new ArrayList<>();
    private int lastOrderId = 0;

    public void addOrder(String customerName, double amount) {
        if (customerName == null || customerName == "") {
            System.out.println("Invalid name!");
        }
        if (amount < 0) {
           amount = amount * -1;  
        }

        String order = "ID:" + lastOrderId + "-" + customerName + "-" + amount;
        orders.add(order);

       lastOrderId++;  
        saveOrderToDatabase(order);
    }

    public void saveOrderToDatabase(String order) {
        // Simulate slow database
        try {
           Thread.sleep(200);  
        } catch (Exception e) {}

        System.out.println("Order saved: " + order);
    }

    public String findOrder(String name) {
        for (String order : orders) {
            if (order.contains(name)) {
                return order;
            }
        }
        return null;
    }

    public void printAllOrders() {
        for (int i = 0; i < orders.size(); i++) {
            System.out.println(orders.get(i));
        }
        System.out.println("Done printing orders!");
    }
}


                       
                                          
