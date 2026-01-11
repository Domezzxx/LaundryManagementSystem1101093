import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.Timer;
import java.text.SimpleDateFormat;

public class PaymentFrame extends JFrame {

    private HomeFrame homeFrame;

    private List<BookingFrame.OrderItem> orderItems;
    private int totalAmount;
    private String bookingDate; // รูปแบบ: "14/12/2025"
    private String bookingTime; // รูปแบบ: "08:00"
    private JCheckBox termsCheckbox;
    private String customerName;
    private int customerId;

    private JLabel realTimeLabel;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "DomeDome55&55";
    private String username;

    public PaymentFrame(List<BookingFrame.OrderItem> items, int total, String date, String time, String customerName, int customerId, HomeFrame homeFrame) {
        this.orderItems = items;
        this.totalAmount = total;
        this.bookingDate = date;
        this.bookingTime = time;
        this.customerName = customerName;
        this.customerId = customerId;
        this.homeFrame = homeFrame;

        initComponents();


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (homeFrame != null) {
                    homeFrame.setVisible(true);
                }
                dispose();
            }
        });
    }

    public PaymentFrame(List<BookingFrame.OrderItem> items, int total, String date, String time, String customerName, int customerId) {
        this(items, total, date, time, customerName, customerId, null);
    }

    private void initComponents() {
        setTitle("ชำระเงิน");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = createTopBar();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 30);
        gbc.weightx = 0.6;
        mainPanel.add(createLeftPanel(), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 30, 0, 0);
        gbc.weightx = 0.4;
        mainPanel.add(createRightPanel(), gbc);

        add(topBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(0, 204, 204));
        topBar.setPreferredSize(new Dimension(1200, 50));
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(new EmptyBorder(8, 20, 8, 20));


        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);


        JButton backButton = new JButton("← กลับ");
        backButton.setBackground(new Color(0, 180, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(90, 30));


        backButton.addActionListener(e -> {
            this.dispose();
            if (homeFrame != null) {
                homeFrame.setVisible(true);
            }
        });

        JLabel phoneLabel = new JLabel("📞 01-234-5678");
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        JLabel lineLabel = new JLabel("💬 @Laundry Clean & Fresh");
        lineLabel.setForeground(Color.WHITE);
        lineLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        leftPanel.add(backButton);
        leftPanel.add(phoneLabel);
        leftPanel.add(lineLabel);


        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);


        JButton userButton = new JButton(customerName + " ▼");
        userButton.setBackground(Color.WHITE);
        userButton.setForeground(new Color(0, 204, 204));
        userButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        userButton.setFocusPainted(false);
        userButton.setBorderPainted(false);
        userButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userButton.setPreferredSize(new Dimension(180, 30));

        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("โปรไฟล์");
        JMenuItem logoutItem = new JMenuItem("ออกจากระบบ");

        profileItem.setFont(new Font("Tahoma", Font.PLAIN, 12));
        logoutItem.setFont(new Font("Tahoma", Font.PLAIN, 12));


        profileItem.addActionListener(e -> {

            this.setVisible(false);


            UserProfileFrame profileFrame = new UserProfileFrame(this.username);


            profileFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    PaymentFrame.this.setVisible(true); // กลับมาแสดงหน้า Home
                }
            });


            profileFrame.setVisible(true);
        });

        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(PaymentFrame.this,
                    "คุณต้องการออกจากระบบหรือไม่?",
                    "ยืนยันการออกจากระบบ",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                PaymentFrame.this.dispose();
                new LoginFrame().setVisible(true);
            }
        });

        userMenu.add(profileItem);
        userMenu.addSeparator();
        userMenu.add(logoutItem);

        userButton.addActionListener(e -> userMenu.show(userButton, 0, userButton.getHeight()));


        rightPanel.add(userButton);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("สรุปคำสั่งซื้อ");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));

        JPanel totalTopPanel = new JPanel(new BorderLayout());
        totalTopPanel.setBackground(Color.WHITE);
        totalTopPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        totalTopPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel totalTopLabel = new JLabel("฿ " + String.format("%,d", totalAmount));
        totalTopLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        totalTopLabel.setForeground(new Color(0, 150, 150));
        totalTopPanel.add(totalTopLabel, BorderLayout.WEST);
        contentPanel.add(totalTopPanel);
        contentPanel.add(Box.createVerticalStrut(20));

        termsCheckbox = new JCheckBox("<html>ข้าพเจ้ายืนยันว่าพอใจที่จะใช้งานและส่งมอบเครื่องนอนและเสื้อผ้าให้แก่บริการซักรีดและอนุญาตให้สามารถทำการบันทึกข้อมูล</html>");
        termsCheckbox.setFont(new Font("Tahoma", Font.PLAIN, 12));
        termsCheckbox.setBackground(Color.WHITE);
        termsCheckbox.setAlignmentX(Component.LEFT_ALIGNMENT);
        termsCheckbox.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        contentPanel.add(termsCheckbox);
        contentPanel.add(Box.createVerticalStrut(30));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(separator);
        contentPanel.add(Box.createVerticalStrut(20));

        JLabel detailsTitle = new JLabel("รายการสั่งซัก");
        detailsTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        detailsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(detailsTitle);
        contentPanel.add(Box.createVerticalStrut(15));

        JPanel itemsListPanel = new JPanel();
        itemsListPanel.setLayout(new BoxLayout(itemsListPanel, BoxLayout.Y_AXIS));
        itemsListPanel.setBackground(Color.WHITE);
        itemsListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (BookingFrame.OrderItem item : orderItems) {
            itemsListPanel.add(createOrderItemPanel(item));
            itemsListPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(itemsListPanel);
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        contentPanel.add(scrollPane);
        contentPanel.add(Box.createVerticalGlue());

        panel.add(contentPanel);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        return panel;
    }

    private JPanel createOrderItemPanel(BookingFrame.OrderItem item) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        JLabel nameLabel = new JLabel(item.itemName);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        JLabel serviceLabel = new JLabel(item.serviceType);
        serviceLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
        serviceLabel.setForeground(Color.GRAY);
        leftPanel.add(nameLabel);
        leftPanel.add(serviceLabel);

        JLabel qtyLabel = new JLabel("จำนวน " + item.quantity + " ชิ้น");
        qtyLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        qtyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel priceLabel = new JLabel("฿" + String.format("%,d", item.getTotalPrice()));
        priceLabel.setFont(new Font("Tahoma", Font.BOLD, 15));

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(qtyLabel, BorderLayout.CENTER);
        panel.add(priceLabel, BorderLayout.EAST);
        return panel;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel titleLabel = new JLabel("ดำเนินการชำระเงิน");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));

        JButton completePaymentButton = createSinglePaymentButton("✅ ชำระเงิน");
        panel.add(completePaymentButton);
        panel.add(Box.createVerticalStrut(30));

        JLabel totalHint = new JLabel("ยอดที่ต้องชำระ: ฿" + String.format("%,d", totalAmount), SwingConstants.CENTER);
        totalHint.setFont(new Font("Tahoma", Font.BOLD, 18));
        totalHint.setForeground(new Color(20, 100, 20));
        totalHint.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(totalHint);

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    private JButton createSinglePaymentButton(String buttonText) {
        JButton button = new JButton(buttonText);
        button.setFont(new Font("Tahoma", Font.BOLD, 16));
        button.setBackground(new Color(0, 204, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);

        button.addActionListener(e -> processPayment("ชำระเงินสด"));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 180, 180));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(new Color(0, 204, 204));
            }
        });
        return button;
    }

    private void processPayment(String paymentMethod) {
        if (!termsCheckbox.isSelected()) {
            JOptionPane.showMessageDialog(this,
                    "กรุณายอมรับข้อตกลงก่อนดำเนินการชำระเงิน",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (orderItems == null || orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "ไม่มีรายการสั่งซื้อ",
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }


        String sqlDate = "";
        try {
            String[] dateParts = bookingDate.split("/");
            sqlDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "รูปแบบวันที่ไม่ถูกต้อง", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean slotBooked = saveBookingAndUpdateSlot(sqlDate, bookingTime);

        if (!slotBooked) {
            return;
        }

        int orderId = saveOrderToDatabase(paymentMethod, sqlDate);

        if (orderId > 0) {
            JOptionPane.showMessageDialog(this,
                    "ชำระเงินสำเร็จ!\n" +
                            "หมายเลขคำสั่งซื้อ: " + orderId + "\n" +
                            "ลูกค้า: " + customerName + "\n" +
                            "ยอดชำระ: ฿" + String.format("%,d", totalAmount),
                    "ชำระเงินสำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE);

            if (homeFrame != null) {
                homeFrame.setVisible(true);
            }
            dispose();

        } else {
            cancelBookingSlot(sqlDate, bookingTime);
            JOptionPane.showMessageDialog(this,
                    "เกิดข้อผิดพลาดในการบันทึกข้อมูล\nกรุณาลองใหม่อีกครั้ง",
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean saveBookingAndUpdateSlot(String bookingDate, String timeSlot) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);

            String checkQuery = "SELECT current_bookings FROM booking_slots " +
                    "WHERE booking_date = ? AND time_slot = ? FOR UPDATE";

            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, bookingDate);
            checkStmt.setString(2, timeSlot);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                int currentBookings = rs.getInt("current_bookings");

                if (currentBookings >= 8) {
                    conn.rollback();
                    JOptionPane.showMessageDialog(this,
                            "ขออภัย ช่วงเวลานี้เต็มแล้ว (8/8)",
                            "ไม่สามารถจองได้",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }

                String updateQuery = "UPDATE booking_slots SET current_bookings = current_bookings + 1 " +
                        "WHERE booking_date = ? AND time_slot = ?";
                PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                updateStmt.setString(1, bookingDate);
                updateStmt.setString(2, timeSlot);
                updateStmt.executeUpdate();

            } else {
                String insertQuery = "INSERT INTO booking_slots (booking_date, time_slot, current_bookings, max_bookings) " +
                        "VALUES (?, ?, 1, 8)";
                PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
                insertStmt.setString(1, bookingDate);
                insertStmt.setString(2, timeSlot);
                insertStmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "เกิดข้อผิดพลาดในการจองช่วงเวลา: " + e.getMessage(),
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
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

    private void cancelBookingSlot(String bookingDate, String timeSlot) {
        String updateQuery = "UPDATE booking_slots SET current_bookings = current_bookings - 1 " +
                "WHERE booking_date = ? AND time_slot = ? AND current_bookings > 0";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {

            pstmt.setString(1, bookingDate);
            pstmt.setString(2, timeSlot);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int saveOrderToDatabase(String paymentMethod, String sqlDate) {
        int orderId = -1;
        int paymentId = -1;
        Connection conn = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "ไม่พบ MySQL Driver: " + e.getMessage(),
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
            return -1;
        }

        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            conn.setAutoCommit(false);


            String sqlPayment = "INSERT INTO payment (amount, payment_method, payment_date, payment_status) VALUES (?, ?, NOW(), ?)";
            try (PreparedStatement pstmtPayment = conn.prepareStatement(sqlPayment, Statement.RETURN_GENERATED_KEYS)) {
                pstmtPayment.setDouble(1, totalAmount);
                pstmtPayment.setString(2, paymentMethod);
                pstmtPayment.setString(3, "ชำระเงินแล้ว");
                pstmtPayment.executeUpdate();

                try (ResultSet rsPayment = pstmtPayment.getGeneratedKeys()) {
                    if (rsPayment.next()) {
                        paymentId = rsPayment.getInt(1);
                    } else {
                        throw new SQLException("ไม่สามารถดึง Payment ID ได้");
                    }
                }
            }


            Timestamp bookingTimestamp = null;
            try {
                String cleanTime = bookingTime.trim();

                if (cleanTime.contains("AM") || cleanTime.contains("PM")) {
                    SimpleDateFormat format12 = new SimpleDateFormat("hh:mm a", java.util.Locale.US);
                    SimpleDateFormat format24 = new SimpleDateFormat("HH:mm");
                    java.util.Date time12 = format12.parse(cleanTime);
                    cleanTime = format24.format(time12);
                }

                if (!cleanTime.contains(":")) {
                    cleanTime = cleanTime + ":00";
                } else if (cleanTime.split(":").length == 2) {
                    cleanTime = cleanTime + ":00";
                }

                String bookingDateTimeStr = sqlDate + " " + cleanTime;
                bookingTimestamp = Timestamp.valueOf(bookingDateTimeStr);

            } catch (Exception e) {
                System.err.println("Error timestamp conversion: " + e.getMessage());
                bookingTimestamp = new Timestamp(System.currentTimeMillis());
            }

            String sqlOrder = "INSERT INTO laundryorder (customer_id, customer_name, booking_date, booking_time, total_price, payment_id, status, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement pstmtOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                pstmtOrder.setInt(1, customerId);
                pstmtOrder.setString(2, customerName);
                pstmtOrder.setString(3, sqlDate);
                pstmtOrder.setString(4, bookingTime);
                pstmtOrder.setDouble(5, totalAmount);
                pstmtOrder.setInt(6, paymentId);
                pstmtOrder.setString(7, "ชำระเงินแล้ว");
                pstmtOrder.setTimestamp(8, bookingTimestamp);

                pstmtOrder.executeUpdate();

                try (ResultSet rsOrder = pstmtOrder.getGeneratedKeys()) {
                    if (rsOrder.next()) {
                        orderId = rsOrder.getInt(1);
                    } else {
                        throw new SQLException("ไม่สามารถดึง Order ID ได้");
                    }
                }
            }


            StringBuilder orderDetailsBuilder = new StringBuilder();
            int totalItems = 0;
            for (int i = 0; i < orderItems.size(); i++) {
                BookingFrame.OrderItem item = orderItems.get(i);
                orderDetailsBuilder.append(item.itemName)
                        .append(" (").append(item.serviceType).append(") ")
                        .append("x").append(item.quantity)
                        .append(" = ฿").append(item.getTotalPrice());
                if (i < orderItems.size() - 1) {
                    orderDetailsBuilder.append(", ");
                }
                totalItems += item.quantity;
            }
            String orderDetails = orderDetailsBuilder.toString();

            String sqlDetail = "INSERT INTO order_detail (order_id, order_details, total_items, total_price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmtDetail = conn.prepareStatement(sqlDetail)) {
                pstmtDetail.setInt(1, orderId);
                pstmtDetail.setString(2, orderDetails);
                pstmtDetail.setInt(3, totalItems);
                pstmtDetail.setDouble(4, totalAmount);
                pstmtDetail.executeUpdate();
            }

            conn.commit(); // ยืนยันการบันทึกทั้งหมด
            System.out.println("Transaction committed successfully!");

        } catch (SQLException e) {
            orderId = -1;
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Database Error: " + e.getMessage(),
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException closeEx) {
                    closeEx.printStackTrace();
                }
            }
        }
        return orderId;
    }
}