import java.util.List;

public class OrderService {
    private OrderRepo orderRepo = new OrderRepo();

    public void createOrder(String customerName, String productName, int quantity) {
        orderRepo.createOrder(customerName, productName, quantity);
    }

}
