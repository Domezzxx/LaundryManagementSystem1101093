import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Collections; 


public class Order {

    // Properties

    private String orderId;
    private int customerId;
    private String customerName;
    private String customerPhone;
    private String status;
    private Date orderDate;
    private Date pickupDate;
    private double totalPrice;
    private String remarks;


    
    //
    public static final String STATUS_PENDING = "รอดำเนินการ";
    public static final String STATUS_PROCESSING = "กำลังดำเนินการ";
    public static final String STATUS_COMPLETED = "เสร็จสิ้น";
    public static final String STATUS_CANCELLED = "ยกเลิก";


    // Constructors



    public Order() {
        this.status = STATUS_PENDING; // ค่าเริ่มต้น
        this.orderDate = new Date();
    }

    public Order(String orderId, int customerId, String status, Date orderDate, Date pickupDate, double totalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.orderDate = orderDate;
        this.pickupDate = pickupDate;
        this.totalPrice = totalPrice;
    }


    public Order(String orderId, int customerId, String customerName, String customerPhone,
                 String status, Date orderDate, Date pickupDate, double totalPrice, String remarks) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.status = status;
        this.orderDate = orderDate;
        this.pickupDate = pickupDate;
        this.totalPrice = totalPrice;
        this.remarks = remarks;
    }





    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
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

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getPickupDate() {
        return pickupDate;
    }

    public void setPickupDate(Date pickupDate) {
        this.pickupDate = pickupDate;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }




    // Utility Methods



    public boolean isStatus(String status) {
        return this.status != null && this.status.equals(status);
    }


    public boolean isPending() {
        return isStatus(STATUS_PENDING);
    }


    public boolean isProcessing() {
        return isStatus(STATUS_PROCESSING);
    }


    public boolean isCompleted() {
        return isStatus(STATUS_COMPLETED);
    }


    public boolean isCancelled() {
        return isStatus(STATUS_CANCELLED);
    }


    public boolean isEditable() {
        return !isCancelled();
    }


    public boolean isCancellable() {
        return !isCompleted() && !isCancelled();
    }


    public int getDaysUntilPickup() {
        if (pickupDate == null) return -1;

        long diff = pickupDate.getTime() - new Date().getTime();
        return (int) (diff / (1000 * 60 * 60 * 24));
    }

    public boolean isOverdue() {
        return getDaysUntilPickup() < 0 && !isCompleted() && !isCancelled();
    }


    public String getOrderDateFormatted() {
        if (orderDate == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        return sdf.format(orderDate);
    }


    public String getOrderDateTimeFormatted() {
        if (orderDate == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(orderDate);
    }


    public String getPickupDateFormatted() {
        if (pickupDate == null) return "";


        SimpleDateFormat sdf;
        if (pickupDate instanceof java.sql.Timestamp) {

            sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        } else {

            sdf = new SimpleDateFormat("dd/MM/yyyy");
        }

        return sdf.format(pickupDate);
    }


    public String getTotalPriceFormatted() {
        return String.format("%.2f บาท", totalPrice);
    }


    public String getStatusWithIcon() {
        switch (status) {
            case STATUS_PENDING:
                return "⏳ " + status;
            case STATUS_PROCESSING:
                return "🔄 " + status;
            case STATUS_COMPLETED:
                return "✅ " + status;
            case STATUS_CANCELLED:
                return "❌ " + status;
            default:
                return status;
        }
    }


    public boolean isValid() {
        if (orderId == null || orderId.trim().isEmpty()) return false;
        if (customerId <= 0) return false;
        if (status == null || status.trim().isEmpty()) return false;
        if (orderDate == null) return false;
        if (pickupDate == null) return false;
        if (totalPrice < 0) return false;
        return true;
    }


    public Order clone() {
        return new Order(
                this.orderId,
                this.customerId,
                this.customerName,
                this.customerPhone,
                this.status,
                this.orderDate != null ? new Date(this.orderDate.getTime()) : null,
                this.pickupDate != null ? new Date(this.pickupDate.getTime()) : null,
                this.totalPrice,
                this.remarks
        );
    }


    // Override Methods

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", status='" + status + '\'' +
                ", orderDate=" + getOrderDateFormatted() +
                ", pickupDate=" + getPickupDateFormatted() +
                ", totalPrice=" + getTotalPriceFormatted() +
                '}';
    }


    public String toDetailedString() {
      
        return toDetailedString(Collections.emptyList());
    }


    public String toDetailedString(List<String> orderDetails) {
        StringBuilder sb = new StringBuilder();
        sb.append("╔════════════════════════════════════════╗\n");
        sb.append("║           รายละเอียดคำสั่ง             ║\n");
        sb.append("╠════════════════════════════════════════╣\n");
        sb.append(String.format("║ คำสั่ง:      %-25s ║\n", orderId));
        sb.append(String.format("║ ลูกค้า:      %-25s ║\n", customerName != null ? customerName : "N/A"));
        sb.append(String.format("║ เบอร์:       %-25s ║\n", customerPhone != null ? customerPhone : "N/A"));
        sb.append(String.format("║ สถานะ:       %-25s ║\n", getStatusWithIcon()));
        sb.append(String.format("║ วันสั่ง:     %-25s ║\n", getOrderDateTimeFormatted()));
        sb.append(String.format("║ วันรับผ้า:   %-25s ║\n", getPickupDateFormatted()));

       
        sb.append("╠════════════════════════════════════════╣\n");
        sb.append("║           รายการซักผ้า                ║\n");
        sb.append("╠════════════════════════════════════════╣\n");

        if (orderDetails == null || orderDetails.isEmpty()) {
            sb.append(String.format("║ %-38s ║\n", "ไม่มีรายการซักผ้า"));
        } else {

            for (String detail : orderDetails) {

                String displayDetail = detail.length() > 38 ? detail.substring(0, 35) + "..." : detail;
                sb.append(String.format("║ * %-36s ║\n", displayDetail));
            }
        }

        sb.append("╠════════════════════════════════════════╣\n");

        sb.append(String.format("║ ราคารวม:    %-25s ║\n", getTotalPriceFormatted()));

        if (remarks != null && !remarks.trim().isEmpty()) {
            sb.append(String.format("║ หมายเหตุ:   %-25s ║\n", remarks));
        }

        sb.append("╚════════════════════════════════════════╝");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Order order = (Order) obj;
        return orderId != null && orderId.equals(order.orderId);
    }

    @Override
    public int hashCode() {
        return orderId != null ? orderId.hashCode() : 0;
    }


    // Static Helper Methods (ไม่มีการเปลี่ยนแปลง)



    public static boolean isValidStatus(String status) {
        return STATUS_PENDING.equals(status) ||
                STATUS_PROCESSING.equals(status) ||
                STATUS_COMPLETED.equals(status) ||
                STATUS_CANCELLED.equals(status);
    }


    public static String[] getAllStatuses() {
        return new String[]{
                STATUS_PENDING,
                STATUS_PROCESSING,
                STATUS_COMPLETED,
                STATUS_CANCELLED
        };
    }


    public static int compareByOrderDate(Order o1, Order o2) {
        if (o1.orderDate == null && o2.orderDate == null) return 0;
        if (o1.orderDate == null) return 1;
        if (o2.orderDate == null) return -1;
        return o2.orderDate.compareTo(o1.orderDate); // ใหม่กว่าอยู่ก่อน
    }


    public static int compareByPickupDate(Order o1, Order o2) {
        if (o1.pickupDate == null && o2.pickupDate == null) return 0;
        if (o1.pickupDate == null) return 1;
        if (o2.pickupDate == null) return -1;
        return o1.pickupDate.compareTo(o2.pickupDate);
    }


    public static int compareByPrice(Order o1, Order o2) {
        return Double.compare(o2.totalPrice, o1.totalPrice); // แพงกว่าอยู่ก่อน
    }

}
