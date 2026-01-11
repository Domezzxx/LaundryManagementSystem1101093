import java.util.List;


public class LaundryOrderService {
    private OrderDataAccessObject orderDAO;


    public LaundryOrderService() {
        this.orderDAO = new OrderDataAccessObject();
    }


    public LaundryOrderService(OrderDataAccessObject orderDAO) {
        this.orderDAO = orderDAO;
    }


    public List<LaundryOrder> getCustomerOrderHistory(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID ต้องมากกว่า 0");
        }
        return orderDAO.getOrdersByCustomerId(customerId);
    }


    public LaundryOrder getOrderDetails(int orderId) {
        if (orderId <= 0) {
            throw new IllegalArgumentException("Order ID ต้องมากกว่า 0");
        }
        return orderDAO.getOrderById(orderId);
    }


    public List<LaundryOrder> getOrdersByStatus(int customerId, String status) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID ต้องมากกว่า 0");
        }
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("Status ไม่สามารถว่างได้");
        }
        return orderDAO.getOrdersByCustomerIdAndStatus(customerId, status);
    }


    public int getTotalOrderCount(int customerId) {
        if (customerId <= 0) {
            throw new IllegalArgumentException("Customer ID ต้องมากกว่า 0");
        }
        return orderDAO.countOrdersByCustomerId(customerId);
    }


    public boolean hasOrderHistory(int customerId) {
        return getTotalOrderCount(customerId) > 0;
    }


    public List<LaundryOrder> getCompletedOrders(int customerId) {
        return getOrdersByStatus(customerId, "เสร็จสิ้น");
    }


    public List<LaundryOrder> getActiveOrders(int customerId) {
        List<LaundryOrder> activeOrders = new java.util.ArrayList<>();
        activeOrders.addAll(getOrdersByStatus(customerId, "กำลังดำเนินการ"));
        activeOrders.addAll(getOrdersByStatus(customerId, "รอดำเนินการ"));

        activeOrders.sort((o1, o2) -> o2.getCreatedAt().compareTo(o1.getCreatedAt()));

        return activeOrders;
    }
}