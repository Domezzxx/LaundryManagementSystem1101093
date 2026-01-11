import java.sql.*;
import java.util.*;
import java.util.Date;

public class OrderManager {
    private static OrderManager instance;
    private List<StatusChangeListener> listeners = new ArrayList<>();


    private static final String PICKUP_DATE_COLUMN = "booking_date";


    private OrderManager() {}

    public static OrderManager getInstance() {
        if (instance == null) {
            instance = new OrderManager();
        }
        return instance;
    }


    public interface StatusChangeListener {
        void onStatusChanged();
    }

    public void addStatusChangeListener(StatusChangeListener listener) {
        listeners.add(listener);
    }

    public void removeStatusChangeListener(StatusChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyStatusChange() {
        for (StatusChangeListener listener : listeners) {
            listener.onStatusChanged();
        }
    }


    // ORDER MANAGEMENT

    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT lo.*, c.name as customer_name, c.phone as customer_phone " +
                "FROM laundryorder lo " +
                "LEFT JOIN customer c ON lo.customer_id = c.customer_id " +
                "ORDER BY lo.created_at DESC";

        System.out.println("🔍 Executing query: " + query);

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int count = 0;
            while (rs.next()) {
                count++;
                Order order = extractOrderFromResultSet(rs);
                orders.add(order);
                System.out.println("  ✓ Loaded Order #" + count + ": " + order.getOrderId());
            }

            System.out.println("📦 Total orders loaded: " + count);

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการดึงข้อมูลคำสั่งซื้อ: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }


    public List<Order> searchByOrderId(String orderId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT lo.*, c.name as customer_name, c.phone as customer_phone " +
                "FROM laundryorder lo " +
                "LEFT JOIN customer c ON lo.customer_id = c.customer_id " +
                "WHERE lo.order_id = ? " +
                "ORDER BY lo.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }


    public List<Order> searchByPhone(String phone) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT lo.*, c.name as customer_name, c.phone as customer_phone " +
                "FROM laundryorder lo " +
                "LEFT JOIN customer c ON lo.customer_id = c.customer_id " +
                "WHERE c.phone LIKE ? " +
                "ORDER BY lo.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, "%" + phone + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }


    public List<Order> searchOrders(String searchText) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT lo.*, c.name as customer_name, c.phone as customer_phone " +
                "FROM laundryorder lo " +
                "LEFT JOIN customer c ON lo.customer_id = c.customer_id " +
                "WHERE CAST(lo.order_id AS CHAR) LIKE ? OR c.name LIKE ? " +
                "ORDER BY lo.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            String pattern = "%" + searchText + "%";
            pstmt.setString(1, pattern);
            pstmt.setString(2, pattern);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                orders.add(extractOrderFromResultSet(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public Order getOrderById(String orderId) {
        String query = "SELECT lo.*, c.name as customer_name, c.phone as customer_phone " +
                "FROM laundryorder lo " +
                "LEFT JOIN customer c ON lo.customer_id = c.customer_id " +
                "WHERE lo.order_id = ?";
        Order order = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                order = extractOrderFromResultSet(rs);
            }

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการดึงข้อมูลคำสั่งซื้อตาม ID: " + e.getMessage());
            e.printStackTrace();
        }

        return order;
    }


    public List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT lo.*, c.name as customer_name, c.phone as customer_phone " +
                "FROM laundryorder lo " +
                "LEFT JOIN customer c ON lo.customer_id = c.customer_id " +
                "WHERE lo.status = ? " +
                "ORDER BY lo.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                orders.add(order);
            }

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการดึงข้อมูลคำสั่งซื้อตามสถานะ: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }


    public List<Order> getOrdersByCustomerId(int customerId) {
        List<Order> orders = new ArrayList<>();
        String query = "SELECT lo.*, c.name as customer_name, c.phone as customer_phone " +
                "FROM laundryorder lo " +
                "LEFT JOIN customer c ON lo.customer_id = c.customer_id " +
                "WHERE lo.customer_id = ? " +
                "ORDER BY lo.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Order order = extractOrderFromResultSet(rs);
                orders.add(order);
            }

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการดึงข้อมูลคำสั่งซื้อตามรหัสลูกค้า: " + e.getMessage());
            e.printStackTrace();
        }

        return orders;
    }


    private Order extractOrderFromResultSet(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getString("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));


        try {
            order.setCustomerName(rs.getString("customer_name"));
        } catch (SQLException e) {
            order.setCustomerName("N/A");
        }

        try {
            order.setCustomerPhone(rs.getString("customer_phone"));
        } catch (SQLException e) {
            order.setCustomerPhone("N/A");
        }

        order.setStatus(rs.getString("status"));


        try {
            order.setOrderDate(rs.getTimestamp("created_at"));
        } catch (SQLException e) {
            order.setOrderDate(new Date());
        }


        try {

            Timestamp pickupTimestamp = rs.getTimestamp("booking_date");
            if (pickupTimestamp != null) {
                order.setPickupDate(pickupTimestamp);
            } else {

                java.sql.Date pickupDate = rs.getDate("booking_date");
                order.setPickupDate(pickupDate);
            }
        } catch (SQLException e) {
            order.setPickupDate(null);
        }

        order.setTotalPrice(rs.getDouble("total_price"));
        return order;
    }


    // STATUS HISTORY MANAGEMENT
    public Map<String, String> getStatusHistory(String orderId) {
        Map<String, String> statusTimes = new LinkedHashMap<>();
        String query = "SELECT status, " +
                "DATE_FORMAT(changed_date, '%d %M %y %H:%i น.') as display_time " +
                "FROM order_status_history " +
                "WHERE order_id = ? " +
                "ORDER BY changed_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String status = rs.getString("status");
                String displayTime = rs.getString("display_time");
                statusTimes.put(status, displayTime);
            }

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการดึงประวัติสถานะ: " + e.getMessage());
            e.printStackTrace();
        }

        return statusTimes;
    }


    public boolean addStatusHistory(String orderId, String status, String changedBy) {
        String query = "INSERT INTO order_status_history (order_id, status, changed_by) " +
                "VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, orderId);
            pstmt.setString(2, status);
            pstmt.setString(3, changedBy);
            int rows = pstmt.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการเพิ่มประวัติสถานะ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public List<StatusHistory> getDetailedStatusHistory(String orderId) {
        List<StatusHistory> history = new ArrayList<>();
        String query = "SELECT * FROM order_status_history " +
                "WHERE order_id = ? " +
                "ORDER BY changed_date ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                StatusHistory sh = new StatusHistory();
                sh.setHistoryId(rs.getInt("history_id"));
                sh.setOrderId(rs.getString("order_id"));
                sh.setStatus(rs.getString("status"));
                sh.setChangedDate(rs.getTimestamp("changed_date"));
                sh.setChangedBy(rs.getString("changed_by"));
                sh.setRemarks(rs.getString("remarks"));
                history.add(sh);
            }

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการดึงรายละเอียดประวัติสถานะ: " + e.getMessage());
            e.printStackTrace();
        }

        return history;
    }


    // CREATE & UPDATE OPERATIONS
    public boolean createOrder(Order order) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // เริ่ม transaction

           
            String insertOrderQuery = "INSERT INTO laundryorder " +
                    "(order_id, customer_id, status, booking_date, total_price) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertOrderQuery)) {
                pstmt.setString(1, order.getOrderId());
                pstmt.setInt(2, order.getCustomerId());
                pstmt.setString(3, order.getStatus());

               
                if (order.getPickupDate() != null) {
                    pstmt.setDate(4, new java.sql.Date(order.getPickupDate().getTime()));
                } else {
                    pstmt.setNull(4, Types.DATE);
                }

                pstmt.setDouble(5, order.getTotalPrice());
                pstmt.executeUpdate();
            }


            String insertHistoryQuery = "INSERT INTO order_status_history " +
                    "(order_id, status, changed_by) " +
                    "VALUES (?, ?, ?)";

            try (PreparedStatement pstmt = conn.prepareStatement(insertHistoryQuery)) {
                pstmt.setString(1, order.getOrderId());
                pstmt.setString(2, order.getStatus());
                pstmt.setString(3, "ระบบ");
                pstmt.executeUpdate();
            }

            conn.commit();
            notifyStatusChange();

            System.out.println("✓ สร้าง Order สำเร็จ: " + order.getOrderId());
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback(); // ยกเลิกถ้าเกิดข้อผิดพลาด
                    System.err.println("✗ ยกเลิกการทำรายการ");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("❌ เกิดข้อผิดพลาดในการสร้างคำสั่งซื้อ: " + e.getMessage());
            e.printStackTrace();
            return false;
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
    }

    public boolean updateOrderStatus(String orderId, String newStatus, String changedBy, String remarks) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            java.sql.Timestamp newTimestamp = null;
            boolean updateDate = false;


            if ("กำลังดำเนินการ".equals(newStatus)) {
               
                newTimestamp = null; 
                updateDate = true;
            } else if ("เสร็จสิ้น".equals(newStatus)) {
                newTimestamp = new java.sql.Timestamp(System.currentTimeMillis());
                updateDate = true;
            } else if ("รอดำเนินการ".equals(newStatus)) {
                newTimestamp = null; 
                updateDate = true;
            }

            StringBuilder sql = new StringBuilder("UPDATE laundryorder SET status = ?");
            if (updateDate) {
                sql.append(", ").append(PICKUP_DATE_COLUMN).append(" = ?");
            }
            sql.append(" WHERE order_id = ?");

            try (PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
                int index = 1;
                pstmt.setString(index++, newStatus);

                if (updateDate) {

                    pstmt.setTimestamp(index++, newTimestamp);
                }
                pstmt.setString(index++, orderId);

                int rows = pstmt.executeUpdate();
                if (rows == 0) throw new SQLException("ไม่พบ Order ID: " + orderId);
            }

            String insertHistory = "INSERT INTO order_status_history (order_id, status, changed_by, remarks, changed_date) VALUES (?, ?, ?, ?, NOW())";
            try (PreparedStatement pstmt = conn.prepareStatement(insertHistory)) {
                pstmt.setString(1, orderId);
                pstmt.setString(2, newStatus);
                pstmt.setString(3, changedBy);
                pstmt.setString(4, remarks);
                pstmt.executeUpdate();
            }

            conn.commit();
            notifyStatusChange();
            System.out.println("✓ Update สำเร็จ: " + orderId + " -> " + newStatus);
            return true;

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }
    private String buildUpdateQuery(String newStatus) {

        StringBuilder query = new StringBuilder("UPDATE laundryorder SET status = ?");

        String status = (newStatus == null) ? "" : newStatus.trim();


        switch (status) {
            case "รอดำเนินการ":
                query.append(", booking_date = NULL");
                break;
            case "กำลังดำเนินการ":
                query.append(", booking_date = DATE_ADD(NOW(), INTERVAL 3 DAY)");
                break;
            case "เสร็จสิ้น":
                query.append(", booking_date = NOW()");
                break;
            default:

                break;
        }


        query.append(" WHERE order_id = ?");

        return query.toString();
    }

    public boolean cancelOrder(String orderId, String canceledBy) {
        return updateOrderStatus(orderId, "ยกเลิก", canceledBy, "ยกเลิกโดยผู้ใช้");
    }


    public boolean cancelOrder(String orderId, String canceledBy, String reason) {
        return updateOrderStatus(orderId, "ยกเลิก", canceledBy, reason);
    }


    public boolean updateOrder(Order order) {
        String query = "UPDATE laundryorder " +
                "SET customer_id = ?, " + PICKUP_DATE_COLUMN + " = ?, total_price = ? " +
                "WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, order.getCustomerId());


            if (order.getPickupDate() != null) {
                pstmt.setTimestamp(2, new java.sql.Timestamp(order.getPickupDate().getTime()));
            } else {
                pstmt.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            }

            pstmt.setDouble(3, order.getTotalPrice());
            pstmt.setString(4, order.getOrderId());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                notifyStatusChange();
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteOrder(String orderId) {
        String query = "DELETE FROM laundryorder WHERE order_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, orderId);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✓ ลบ Order สำเร็จ: " + orderId);
                notifyStatusChange();
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการลบคำสั่งซื้อ: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // UTILITY METHODS
    public int countOrdersByStatus(String status) {
        String query = "SELECT COUNT(*) as count FROM laundryorder WHERE status = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการนับจำนวนคำสั่งซื้อ: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }


    public int countAllOrders() {
        String query = "SELECT COUNT(*) as count FROM laundryorder";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                return rs.getInt("count");
            }

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการนับจำนวนคำสั่งซื้อทั้งหมด: " + e.getMessage());
            e.printStackTrace();
        }

        return 0;
    }


    public String generateOrderId() {
        String prefix = "ORD-";
        String query = "SELECT MAX(CAST(SUBSTRING(order_id, 5) AS UNSIGNED)) as max_num " +
                "FROM laundryorder " +
                "WHERE order_id LIKE 'ORD-%'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            if (rs.next()) {
                int maxNum = rs.getInt("max_num");
                return prefix + String.format("%03d", maxNum + 1);
            }

        } catch (SQLException e) {
            System.err.println("❌ เกิดข้อผิดพลาดในการสร้างหมายเลขคำสั่งซื้อ: " + e.getMessage());
            e.printStackTrace();
        }

        // ถ้าเกิดข้อผิดพลาดหรือยังไม่มี Order ใช้ timestamp
        return prefix + System.currentTimeMillis();
    }


    // INNER CLASS: StatusHistory


    public static class StatusHistory {
        private int historyId;
        private String orderId;
        private String status;
        private Date changedDate;
        private String changedBy;
        private String remarks;


        public int getHistoryId() { return historyId; }
        public void setHistoryId(int historyId) { this.historyId = historyId; }

        public String getOrderId() { return orderId; }
        public void setOrderId(String orderId) { this.orderId = orderId; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public Date getChangedDate() { return changedDate; }
        public void setChangedDate(Date changedDate) { this.changedDate = changedDate; }

        public String getChangedBy() { return changedBy; }
        public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

}
