import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.util.Map;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class StatusCheckFrame extends JFrame implements OrderManager.StatusChangeListener {
    private OrderManager orderManager;
    private JPanel timelinePanel;
    private Order currentOrder;
    private JLabel orderIdLabel;
    private JLabel statusLabel;
    private int customerId;
    private String customerName;


    private String currentUsername;

    private JButton userButton;
    private JLabel timeLabel;
    private Timer timer;


    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private String username;


    public StatusCheckFrame(int customerId, String username) {
        this.customerId = customerId;
        this.currentUsername = username; 
        this.customerName = getCustomerName(customerId);

        orderManager = OrderManager.getInstance();
        orderManager.addStatusChangeListener(this);
        initComponents();


        startClock();


        try {
            List<Order> orders = orderManager.getOrdersByCustomerId(customerId);
            System.out.println("📊 จำนวน Orders ของลูกค้า: " + orders.size());

            if (!orders.isEmpty()) {
                currentOrder = orders.get(0);
                System.out.println("✓ โหลด Order: " + currentOrder.getOrderId());
                updateDisplay();
            } else {
                System.out.println("⚠️ ไม่มี Order ในระบบ");
                JOptionPane.showMessageDialog(this,
                        "ยังไม่มีคำสั่งซักรีดในระบบ",
                        "แจ้งเตือน",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            System.err.println("❌ Error loading orders: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "เกิดข้อผิดพลาดในการโหลดข้อมูล: " + e.getMessage(),
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public StatusCheckFrame(int customerId) {
        this(customerId, "");
    }

    private void startClock() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        timer = new Timer(1000, e -> {
            if (timeLabel != null) {
                timeLabel.setText("🕐 " + sdf.format(new Date()));
            }
        });
        timer.start();
    }

    private String getCustomerName(int customerId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            String query = "SELECT name FROM customer WHERE customer_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, customerId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("name");
            }

            return "ผู้ใช้";

        } catch (Exception e) {
            e.printStackTrace();
            return "ผู้ใช้";
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initComponents() {
        setTitle("ปลายฟ้า LAUNDRY - เช็คสถานะ");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = createTopBar();
        JPanel headerPanel = createHeaderPanel();

        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBackground(new Color(230, 235, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 40, 40));

        JPanel orderCard = createOrderCard();

        timelinePanel = new JPanel();
        timelinePanel.setLayout(new BoxLayout(timelinePanel, BoxLayout.Y_AXIS));
        timelinePanel.setBackground(Color.WHITE);
        timelinePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton manageButton = new JButton("ยกเลิกบริการ");
        manageButton.setPreferredSize(new Dimension(140, 35));
        manageButton.setFont(new Font("Tahoma", Font.PLAIN, 13));
        manageButton.setBackground(Color.WHITE);
        manageButton.setForeground(Color.RED);
        manageButton.setBorder(BorderFactory.createLineBorder(Color.RED));
        manageButton.setFocusPainted(false);
        manageButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        manageButton.addActionListener(e -> {
            if (currentOrder != null) {
                cancelCurrentOrder();
            }
        });
        buttonPanel.add(manageButton);

        JPanel contentPanel = new JPanel(new BorderLayout(0, 15));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.add(timelinePanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(orderCard, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(topBar, BorderLayout.NORTH);
        topPanel.add(headerPanel, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);
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

        backButton.addActionListener(e -> goBackToHome());


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
                    StatusCheckFrame.this.setVisible(true);
                }
            });

            profileFrame.setVisible(true);
        });

        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(StatusCheckFrame.this,
                    "คุณต้องการออกจากระบบหรือไม่?",
                    "ยืนยันการออกจากระบบ",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                StatusCheckFrame.this.dispose();
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

    private void goBackToHome() {
        this.dispose();
        SwingUtilities.invokeLater(() -> {

            new HomeFrame(customerId, currentUsername).setVisible(true);
        });
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        headerPanel.setBackground(new Color(230, 235, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 30, 5, 30));

        JLabel logoLabel = new JLabel(createLogoIcon());
        JLabel titleLabel = new JLabel("ปลายฟ้า LAUNDRY");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 26));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(logoLabel);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createOrderCard() {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        JLabel iconLabel = new JLabel(createWashingIcon());

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(Color.WHITE);

        orderIdLabel = new JLabel("คิว # ABCDE1234");
        orderIdLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        orderIdLabel.setForeground(new Color(50, 50, 50));

        statusLabel = new JLabel("กำลังดำเนินการ");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(255, 140, 0));

        textPanel.add(orderIdLabel);
        textPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        textPanel.add(statusLabel);

        card.add(iconLabel, BorderLayout.WEST);
        card.add(textPanel, BorderLayout.CENTER);

        return card;
    }

    private void updateDisplay() {
        if (currentOrder == null) return;

        orderIdLabel.setText("คิว # " + currentOrder.getOrderId());
        statusLabel.setText(currentOrder.getStatus());

        switch (currentOrder.getStatus()) {
            case "รอดำเนินการ":
                statusLabel.setForeground(new Color(255, 215, 0));
                break;
            case "กำลังดำเนินการ":
                statusLabel.setForeground(new Color(255, 140, 0));
                break;
            case "เสร็จสิ้น":
                statusLabel.setForeground(new Color(34, 139, 34));
                break;
            case "ยกเลิก":
                statusLabel.setForeground(Color.RED);
                break;
        }

        updateTimeline();
    }

    private void updateTimeline() {
        timelinePanel.removeAll();

        if (currentOrder == null) {
            timelinePanel.revalidate();
            timelinePanel.repaint();
            return;
        }

        Map<String, String> statusTimes = orderManager.getStatusHistory(currentOrder.getOrderId());
        String currentStatus = currentOrder.getStatus();

        timelinePanel.add(createTimelineItem("รอดำเนินการ",
                currentStatus.equals("รอดำเนินการ"),
                new Color(34, 139, 34),
                statusTimes.get("รอดำเนินการ")));
        timelinePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        timelinePanel.add(createTimelineItem("กำลังดำเนินการ",
                currentStatus.equals("กำลังดำเนินการ"),
                new Color(255, 140, 0),
                statusTimes.get("กำลังดำเนินการ")));
        timelinePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        timelinePanel.add(createTimelineItem("เสร็จสิ้น",
                currentStatus.equals("เสร็จสิ้น"),
                new Color(128, 128, 128),
                statusTimes.get("เสร็จสิ้น")));
        timelinePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        timelinePanel.add(createTimelineItem("ยกเลิก",
                currentStatus.equals("ยกเลิก"),
                new Color(128, 128, 128),
                statusTimes.get("ยกเลิก")));

        timelinePanel.revalidate();
        timelinePanel.repaint();
    }

    private JPanel createTimelineItem(String status, boolean isActive, Color color, String timestamp) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(600, 40));

        JLabel iconLabel = new JLabel(isActive ? "✓" : "○");
        iconLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        iconLabel.setPreferredSize(new Dimension(35, 35));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (isActive) {
            iconLabel.setOpaque(true);
            iconLabel.setBackground(color);
            iconLabel.setForeground(Color.WHITE);
            iconLabel.setBorder(BorderFactory.createLineBorder(color, 2));
        } else {
            iconLabel.setForeground(new Color(180, 180, 180));
            iconLabel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        }

        JLabel statusLabelItem = new JLabel(status);
        statusLabelItem.setFont(new Font("Tahoma", isActive ? Font.BOLD : Font.PLAIN, 14));
        statusLabelItem.setForeground(isActive ? Color.BLACK : new Color(150, 150, 150));

        JLabel timeLabel = new JLabel();
        if (timestamp != null && !timestamp.isEmpty()) {
            timeLabel.setText(timestamp);
            timeLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
            timeLabel.setForeground(new Color(120, 120, 120));
        }

        panel.add(iconLabel);
        panel.add(statusLabelItem);
        if (timestamp != null && !timestamp.isEmpty()) {
            panel.add(timeLabel);
        }

        return panel;
    }

    private void cancelCurrentOrder() {
        if (currentOrder == null) return;


        if (currentOrder.getStatus().equals("เสร็จสิ้น")) {
            JOptionPane.showMessageDialog(this,
                    "ไม่สามารถยกเลิกคำสั่งที่เสร็จสิ้นแล้ว",
                    "ไม่สามารถยกเลิกได้",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentOrder.getStatus().equals("ยกเลิก")) {
            JOptionPane.showMessageDialog(this,
                    "คำสั่งนี้ถูกยกเลิกแล้ว",
                    "ยกเลิกแล้ว",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }


        int confirm = JOptionPane.showConfirmDialog(this,
                "ต้องการยกเลิกคำสั่ง # " + currentOrder.getOrderId() + " หรือไม่?\n" +
                        "การยกเลิกจะไม่สามารถย้อนกลับได้",
                "ยืนยันการยกเลิก",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {

            boolean success = orderManager.updateOrderStatus(
                    currentOrder.getOrderId(),
                    "ยกเลิก",
                    customerName,
                    "ยกเลิกโดยลูกค้า"
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "ยกเลิกคำสั่งเรียบร้อยแล้ว",
                        "สำเร็จ",
                        JOptionPane.INFORMATION_MESSAGE);


                currentOrder = orderManager.getOrderById(currentOrder.getOrderId());
                updateDisplay();
            } else {
                JOptionPane.showMessageDialog(this,
                        "เกิดข้อผิดพลาดในการยกเลิกคำสั่ง",
                        "ข้อผิดพลาด",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    @Override
    public void onStatusChanged() {
        if (currentOrder != null) {
            currentOrder = orderManager.getOrderById(currentOrder.getOrderId());
            updateDisplay();
        }
    }

    private ImageIcon createLogoIcon() {
        int size = 55;
        Image img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 180, 220));
        g2.fillOval(0, 0, size, size);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(2));
        g2.drawOval(2, 2, size - 4, size - 4);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(13, 16, 30, 25, 4, 4);
        g2.setColor(new Color(0, 180, 220));
        g2.fillOval(18, 21, 20, 20);

        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createWashingIcon() {
        int size = 60;
        Image img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(200, 220, 240));
        g2.fillRoundRect(5, 5, 50, 50, 8, 8);

        g2.setColor(new Color(100, 180, 220));
        g2.fillOval(15, 15, 30, 30);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(17, 17, 26, 26);

        g2.dispose();
        return new ImageIcon(img);
    }

    @Override
    public void dispose() {
        if (timer != null) {
            timer.stop();
        }
        orderManager.removeStatusChangeListener(this);
        super.dispose();
    }

}
