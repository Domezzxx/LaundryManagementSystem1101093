import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.math.BigDecimal;

public class OrderDataAccessObject {

    // *** สร้าง Formatter สำหรับการ Parse เวลา 12-ชั่วโมง (AM/PM) ที่ต้องใช้ Locale ***
    private static final DateTimeFormatter TIME_12H_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_H_MM_A_FORMATTER = DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter TIME_H_MM_SS_A_FORMATTER = DateTimeFormatter.ofPattern("h:mm:ss a", Locale.ENGLISH);



    public List<String> getOrderDetailsByOrderId(String orderId) {
        String sql = "SELECT order_details FROM order_detail WHERE order_id = ?";
        List<String> details = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String rawDetails = rs.getString("order_details");
                    if (rawDetails != null && !rawDetails.trim().isEmpty()) {
                        String[] items = rawDetails.split("\\r?\\n");
                        for (String item : items) {
                            if (!item.trim().isEmpty()) {
                                details.add(item.trim());
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error retrieving order details for ID " + orderId + ": " + e.getMessage());
        }
        return details;
    }


    public boolean updateOrderDetailsAndPrice(String orderId, String newDetailsRaw, double newTotalPrice) {
        Connection conn = null;
        boolean success = false;

        String sqlDetail = "UPDATE order_detail SET order_details = ?, total_price = ? WHERE order_id = ?";
        String sqlOrder = "UPDATE laundryorder SET total_price = ? WHERE order_id = ?";

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                pstmtDetail.setString(1, newDetailsRaw);
                pstmtDetail.setDouble(2, newTotalPrice);
                pstmtDetail.setString(3, orderId);
                pstmtDetail.executeUpdate();
            }

            try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder)) {
                pstmtOrder.setDouble(1, newTotalPrice);
                pstmtOrder.setString(2, orderId);
                pstmtOrder.executeUpdate();
            }

            conn.commit();
            success = true;
            System.out.println("✓ อัปเดตราคาและรายการสำเร็จสำหรับ Order ID: " + orderId);

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                    System.err.println("✗ Rollback Transaction");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Database Error updating order details and price for ID " + orderId + ": " + e.getMessage());
            success = false;

        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return success;
    }


    private LocalDate parseBookingDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }

        dateString = dateString.trim();

        try {

            LocalDateTime dateTime = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return dateTime.toLocalDate();
        } catch (DateTimeParseException e) {

        }

        DateTimeFormatter[] formatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),  // 2025-12-10 (ISO format)
                DateTimeFormatter.ofPattern("MM/dd/yyyy"),  // 10/12/2025
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),  // 12/10/2025
                DateTimeFormatter.ofPattern("M/d/yyyy"),    // 1/5/2025
                DateTimeFormatter.ofPattern("d/M/yyyy"),    // 5/1/2025
                DateTimeFormatter.ofPattern("yyyy-M-d"),    // 2025-1-5
                DateTimeFormatter.ofPattern("yyyy/MM/dd"),  // 2025/12/10
                DateTimeFormatter.ofPattern("dd-MM-yyyy")   // 10-12-2025
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalDate.parse(dateString, formatter);
            } catch (DateTimeParseException e) {
                continue;
            }
        }

        System.err.println("Warning: Could not parse date: " + dateString);
        return null;
    }


    private LocalTime parseBookingTime(String timeString) {
        if (timeString == null || timeString.trim().isEmpty()) {
            return null;
        }

        timeString = timeString.trim();


        if (timeString.contains("-")) {

            String[] parts = timeString.split("-");
            if (parts.length > 0) {
                timeString = parts[0].trim();

            }
        }


        timeString = timeString.replaceAll("\\s+", " ");


        if (timeString.toLowerCase().endsWith(" am")) {
            timeString = timeString.substring(0, timeString.length() - 3).trim() + " AM";
        } else if (timeString.toLowerCase().endsWith(" pm")) {
            timeString = timeString.substring(0, timeString.length() - 3).trim() + " PM";
        }

        DateTimeFormatter[] formatters = {

                TIME_12H_FORMATTER,
                TIME_H_MM_A_FORMATTER,
                TIME_H_MM_SS_A_FORMATTER,


                DateTimeFormatter.ofPattern("HH:mm:ss"),
                DateTimeFormatter.ofPattern("HH:mm"),
                DateTimeFormatter.ofPattern("H:mm"),
        };

        for (DateTimeFormatter formatter : formatters) {
            try {
                return LocalTime.parse(timeString, formatter);
            } catch (DateTimeParseException e) {
                continue;
            }
        }


        System.err.println("Warning: Could not parse time: " + timeString);
        return null;
    }


    public List<LaundryOrder> getOrdersByCustomerId(int customerId) {
        List<LaundryOrder> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.customer_id, o.customer_name, o.booking_date, " +
                "o.booking_time, o.total_price, o.payment_id, o.status, o.created_at " +
                "FROM laundryorder o " +
                "WHERE o.customer_id = ? " +
                "ORDER BY o.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LaundryOrder order = new LaundryOrder();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setCustomerName(rs.getString("customer_name"));


                    String bookingDateStr = rs.getString("booking_date");
                    if (bookingDateStr != null) {
                        LocalDate bookingDate = parseBookingDate(bookingDateStr);
                        order.setBookingDate(bookingDate);
                    }

                    String bookingTimeStr = rs.getString("booking_time");
                    if (bookingTimeStr != null) {
                        LocalTime bookingTime = parseBookingTime(bookingTimeStr);
                        order.setBookingTime(bookingTime);
                    }

                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setPaymentId(rs.getInt("payment_id"));
                    order.setStatus(rs.getString("status"));

                    java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        order.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    order.setOrderDetails(getOrderDetailsObjectsByOrderId(order.getOrderId()));

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error retrieving orders for customer ID " + customerId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }


    public List<OrderDetail> getOrderDetailsObjectsByOrderId(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        String sql = "SELECT detail_id, order_id, order_details, total_items, total_price " +
                "FROM order_detail " +
                "WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    OrderDetail detail = new OrderDetail();
                    detail.setDetailId(rs.getInt("detail_id"));
                    detail.setOrderId(rs.getInt("order_id"));
                    detail.setOrderDetails(rs.getString("order_details"));
                    detail.setTotalItems(rs.getInt("total_items"));
                    detail.setTotalPrice(rs.getBigDecimal("total_price"));

                    details.add(detail);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error retrieving order details for order ID " + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return details;
    }


    public LaundryOrder getOrderById(int orderId) {
        String sql = "SELECT order_id, customer_id, customer_name, booking_date, " +
                "booking_time, total_price, payment_id, status, created_at " +
                "FROM laundryorder " +
                "WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, orderId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    LaundryOrder order = new LaundryOrder();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setCustomerName(rs.getString("customer_name"));

                    // ใช้ getString แล้ว parse เอง ด้วยเมธอดที่ถูกแก้ไขแล้ว
                    String bookingDateStr = rs.getString("booking_date");
                    if (bookingDateStr != null) {
                        LocalDate bookingDate = parseBookingDate(bookingDateStr);
                        order.setBookingDate(bookingDate);
                    }

                    String bookingTimeStr = rs.getString("booking_time");
                    if (bookingTimeStr != null) {
                        LocalTime bookingTime = parseBookingTime(bookingTimeStr);
                        order.setBookingTime(bookingTime);
                    }

                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setPaymentId(rs.getInt("payment_id"));
                    order.setStatus(rs.getString("status"));

                    java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        order.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    order.setOrderDetails(getOrderDetailsObjectsByOrderId(order.getOrderId()));

                    return order;
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error retrieving order by ID " + orderId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    public int countOrdersByCustomerId(int customerId) {
        String sql = "SELECT COUNT(*) as total FROM laundryorder WHERE customer_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error counting orders for customer ID " + customerId + ": " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }


    public List<LaundryOrder> getOrdersByCustomerIdAndStatus(int customerId, String status) {
        List<LaundryOrder> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.customer_id, o.customer_name, o.booking_date, " +
                "o.booking_time, o.total_price, o.payment_id, o.status, o.created_at " +
                "FROM laundryorder o " +
                "WHERE o.customer_id = ? AND o.status = ? " +
                "ORDER BY o.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, customerId);
            pstmt.setString(2, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LaundryOrder order = new LaundryOrder();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomerId(rs.getInt("customer_id"));
                    order.setCustomerName(rs.getString("customer_name"));

                    // ใช้ getString แล้ว parse เอง ด้วยเมธอดที่ถูกแก้ไขแล้ว
                    String bookingDateStr = rs.getString("booking_date");
                    if (bookingDateStr != null) {
                        LocalDate bookingDate = parseBookingDate(bookingDateStr);
                        order.setBookingDate(bookingDate);
                    }

                    String bookingTimeStr = rs.getString("booking_time");
                    if (bookingTimeStr != null) {
                        LocalTime bookingTime = parseBookingTime(bookingTimeStr);
                        order.setBookingTime(bookingTime);
                    }

                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setPaymentId(rs.getInt("payment_id"));
                    order.setStatus(rs.getString("status"));

                    java.sql.Timestamp createdAt = rs.getTimestamp("created_at");
                    if (createdAt != null) {
                        order.setCreatedAt(createdAt.toLocalDateTime());
                    }

                    order.setOrderDetails(getOrderDetailsObjectsByOrderId(order.getOrderId()));

                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            System.err.println("Database Error retrieving orders: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }
}