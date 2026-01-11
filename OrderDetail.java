import java.math.BigDecimal;


public class OrderDetail {
    private int detailId;
    private int orderId;
    private String orderDetails;
    private int totalItems;
    private BigDecimal totalPrice;

    public OrderDetail() {
    }

    public OrderDetail(int detailId, int orderId, String orderDetails,
                       int totalItems, BigDecimal totalPrice) {
        this.detailId = detailId;
        this.orderId = orderId;
        this.orderDetails = orderDetails;
        this.totalItems = totalItems;
        this.totalPrice = totalPrice;
    }

    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "OrderDetail{" +
                "detailId=" + detailId +
                ", orderId=" + orderId +
                ", orderDetails='" + orderDetails + '\'' +
                ", totalItems=" + totalItems +
                ", totalPrice=" + totalPrice +
                '}';
    }
}