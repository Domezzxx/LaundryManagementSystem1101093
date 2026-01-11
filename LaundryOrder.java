import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.List;


public class LaundryOrder {
    private int orderId;
    private int customerId;
    private String customerName;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private BigDecimal totalPrice;
    private int paymentId;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderDetail> orderDetails;

    public LaundryOrder() {
    }

    public LaundryOrder(int orderId, int customerId, String customerName,
                        LocalDate bookingDate, LocalTime bookingTime,
                        BigDecimal totalPrice, String status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    @Override
    public String toString() {
        return "LaundryOrder{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", bookingDate=" + bookingDate +
                ", bookingTime=" + bookingTime +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}