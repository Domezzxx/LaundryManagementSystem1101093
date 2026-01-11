import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class LaundryServiceFrame extends JFrame {
    private JPanel summaryPanel;
    private Map<String, BookingFrame.OrderItem> orderItems = new HashMap<>();
    private JLabel totalLabel;
    private int totalAmount = 0;
    private String customerName;
    private int customerId;
    private JLabel lblDateTime;
    private Timer timer;


    private HomeFrame homeFrame;

    private static final int DEFAULT_PRICE = 40;
    private static final int JEAN_PRICE = 60;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";
    private String username;


    public LaundryServiceFrame(int customerId, HomeFrame homeFrame) {
        this.customerId = customerId;
        this.homeFrame = homeFrame; // เก็บ Reference
        loadCustomerName();
        initComponents();


        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (homeFrame != null) {
                    homeFrame.setVisible(true); // แสดง HomeFrame
                }
                dispose(); // ปิดตัวเอง
            }
        });
    }


    public LaundryServiceFrame(int customerId) {
        this(customerId, null);
    }


    public LaundryServiceFrame() {
        this(1, null);
        this.customerName = "ผู้ใช้ทั่วไป";
    }

    private void loadCustomerName() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ไม่พบ MySQL JDBC Driver!");
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT name FROM customer WHERE customer_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, customerId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        customerName = rs.getString("name");
                    } else {
                        customerName = "ผู้ใช้ ID:" + customerId;
                    }
                }
            }
        } catch (SQLException e) {
            customerName = "ผู้ใช้ (DB Error)";
        }
    }

    private void initComponents() {
        setTitle("รายการซักผ้า");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());



        JPanel topBar = createTopBar();
        JPanel mainPanel = new JPanel(new BorderLayout(20, 0));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel leftPanelContent = createLeftPanelFromDB(); // Panel ที่มีรายการทั้งหมด
        JPanel rightPanel = createRightPanel();


        JScrollPane leftScrollPane = new JScrollPane(leftPanelContent);
        leftScrollPane.setBorder(null); // ไม่แสดงเส้นขอบ
        leftScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        leftScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER); // ปิด Scroll แนวนอน


        mainPanel.add(leftScrollPane, BorderLayout.CENTER);
        mainPanel.add(rightPanel, BorderLayout.EAST);

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
                homeFrame.setVisible(true); // แสดง HomeFrame
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
                    LaundryServiceFrame.this.setVisible(true); // กลับมาแสดงหน้า Home
                }
            });


            profileFrame.setVisible(true);
        });

        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(LaundryServiceFrame.this,
                    "คุณต้องการออกจากระบบหรือไม่?",
                    "ยืนยันการออกจากระบบ",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                LaundryServiceFrame.this.dispose();
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

    private JPanel createLeftPanelFromDB() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("รายการซักผ้า");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel);


        String sql = "SELECT * FROM products WHERE visible = 1 ORDER BY created_at DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int price = rs.getInt("price"); // ดึงเป็น int
                String imagePath = rs.getString("image_path"); // ดึง path รูปภาพ

                panel.add(createServiceItem(name, imagePath, price));
                panel.add(Box.createVerticalStrut(15));
            }

        } catch (SQLException e) {
            e.printStackTrace();
            panel.add(new JLabel("ไม่สามารถโหลดข้อมูลสินค้าได้: " + e.getMessage()));
        }

        panel.add(Box.createVerticalGlue());
        return panel;
    }


    private String getProductIcon(String name) {
        if (name.contains("เสื้อ")) return "👕";
        if (name.contains("กางเกง")) return "👖";
        return "🛏️";
    }



    private JPanel createServiceItem(String itemName, String imagePath, int price) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 245, 245));
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 15, 15));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        panel.setLayout(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setMaximumSize(new Dimension(700, 100));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JPanel leftInfo = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftInfo.setOpaque(false);

        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setPreferredSize(new Dimension(60, 60));
        iconPanel.setBackground(new Color(240, 248, 255));
        iconPanel.setBorder(BorderFactory.createLineBorder(new Color(0, 204, 204), 2));
        JLabel iconLabel = new JLabel();


        ImageIcon icon = new ImageIcon(imagePath);


        if (icon.getImageLoadStatus() == MediaTracker.COMPLETE && icon.getIconWidth() > 0) {

            Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        } else {

            iconLabel.setText(getProductIcon(itemName));
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        }


        JPanel namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.setOpaque(false);

        JLabel nameLabel = new JLabel(itemName);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 18));

        JLabel priceLabel = new JLabel(price + " บาท");
        priceLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        priceLabel.setForeground(Color.GRAY);

        namePanel.add(nameLabel);
        namePanel.add(priceLabel);

        leftInfo.add(iconLabel);
        leftInfo.add(namePanel);

        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightControls.setOpaque(false);

        String[] serviceTypes = {"ซัก", "ซักแห้ง", "ซักและรีด"};
        JComboBox<String> serviceCombo = new JComboBox<>(serviceTypes);
        serviceCombo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        serviceCombo.setPreferredSize(new Dimension(120, 35));

        JButton minusBtn = createRoundButton("-");
        JLabel quantityLabel = new JLabel("0");
        quantityLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        quantityLabel.setPreferredSize(new Dimension(30, 30));
        quantityLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JButton plusBtn = createRoundButton("+");

        JButton addBtn = new JButton("เพิ่ม");
        addBtn.setBackground(new Color(0, 204, 204));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFont(new Font("Tahoma", Font.BOLD, 14));
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        addBtn.setPreferredSize(new Dimension(80, 35));

        plusBtn.addActionListener(e -> {
            int current = Integer.parseInt(quantityLabel.getText());
            quantityLabel.setText(String.valueOf(current + 1));
        });

        minusBtn.addActionListener(e -> {
            int current = Integer.parseInt(quantityLabel.getText());
            if (current > 0) {
                quantityLabel.setText(String.valueOf(current - 1));
            }
        });

        addBtn.addActionListener(e -> {
            int quantity = Integer.parseInt(quantityLabel.getText());
            if (quantity > 0) {
                String serviceType = (String) serviceCombo.getSelectedItem();
                addToOrder(itemName, serviceType, quantity, price);
                quantityLabel.setText("0");
                JOptionPane.showMessageDialog(this,
                        "เพิ่ม " + itemName + " (" + serviceType + ") จำนวน " + quantity + " ชิ้นแล้ว",
                        "สำเร็จ",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "กรุณาเลือกจำนวนมากกว่า 0",
                        "แจ้งเตือน",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        rightControls.add(serviceCombo);
        rightControls.add(minusBtn);
        rightControls.add(quantityLabel);
        rightControls.add(plusBtn);
        rightControls.add(addBtn);

        panel.add(leftInfo, BorderLayout.WEST);
        panel.add(rightControls, BorderLayout.EAST);

        return panel;
    }

    private JButton createRoundButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(35, 35));
        btn.setFont(new Font("Tahoma", Font.BOLD, 20));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);

        btn.setBackground(new Color(230, 230, 230));
        btn.setForeground(Color.BLACK);
        btn.setFocusPainted(false);


        btn.setBorderPainted(false);

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(200, 200, 200));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(230, 230, 230));
            }
        });

        return btn;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(350, 600));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel titleLabel = new JLabel("สรุปรายการ");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));

        summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(summaryPanel);
        scrollPane.setBorder(null);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(250, 250, 250));
        bottomPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        totalLabel = new JLabel("ยอดรวม: 0 บาท");
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        totalLabel.setForeground(new Color(0, 150, 150));

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        buttonPanel.setBackground(new Color(250, 250, 250));
        buttonPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JButton viewCartBtn = new JButton("ดูตะกร้าสินค้า");
        viewCartBtn.setBackground(new Color(0, 204, 204));
        viewCartBtn.setForeground(Color.WHITE);
        viewCartBtn.setFont(new Font("Tahoma", Font.BOLD, 15));
        viewCartBtn.setFocusPainted(false);
        viewCartBtn.setBorderPainted(false);
        viewCartBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewCartBtn.addActionListener(e -> openBookingFrame());

        JButton clearBtn = new JButton("ล้างรายการ");
        clearBtn.setBackground(new Color(231, 76, 60));
        clearBtn.setForeground(Color.WHITE);
        clearBtn.setFont(new Font("Tahoma", Font.BOLD, 15));
        clearBtn.setFocusPainted(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearBtn.addActionListener(e -> clearAllOrders());

        buttonPanel.add(viewCartBtn);
        buttonPanel.add(clearBtn);

        bottomPanel.add(totalLabel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void openBookingFrame() {
        if (orderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "ไม่มีรายการในตะกร้า\nกรุณาเพิ่มรายการก่อน",
                    "แจ้งเตือน",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<BookingFrame.OrderItem> bookingItems = new ArrayList<>();
        for (BookingFrame.OrderItem item : orderItems.values()) {
            bookingItems.add(new BookingFrame.OrderItem(
                    item.itemName,
                    item.serviceType,
                    item.quantity,
                    item.pricePerItem
            ));
        }

        try {

            BookingFrame bookingFrame = new BookingFrame(
                    bookingItems,
                    totalAmount,
                    customerName,
                    customerId,
                    this.homeFrame,
                    this
            );

            bookingFrame.setVisible(true);
            this.dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "เกิดข้อผิดพลาดในการเปิดหน้า Booking Frame: " + e.getMessage() +
                            "\nตรวจสอบว่า BookingFrame.java และ Constructor ถูกต้อง",
                    "ข้อผิดพลาดการเชื่อมต่อ",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void addToOrder(String itemName, String serviceType, int quantity, int pricePerItem) {

        BookingFrame.OrderItem item = new BookingFrame.OrderItem(itemName, serviceType, quantity, pricePerItem);
        String key = item.getKey();

        if (orderItems.containsKey(key)) {
            BookingFrame.OrderItem existing = orderItems.get(key);
            existing.quantity += quantity;
        } else {
            orderItems.put(key, item);
        }

        updateSummary();
    }

    private void updateSummary() {
        summaryPanel.removeAll();
        totalAmount = 0;

        for (BookingFrame.OrderItem item : orderItems.values()) {
            summaryPanel.add(createSummaryItem(item));
            summaryPanel.add(Box.createVerticalStrut(10));
            totalAmount += item.getTotalPrice();
        }

        totalLabel.setText("ยอดรวม: " + String.format("%,d", totalAmount) + " บาท");
        summaryPanel.revalidate();
        summaryPanel.repaint();
    }

    private JPanel createSummaryItem(BookingFrame.OrderItem item) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(new Color(240, 248, 255));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setMaximumSize(new Dimension(300, 100));

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(item.itemName);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

        JLabel serviceLabel = new JLabel(item.serviceType);
        serviceLabel.setFont(new Font("Tahoma", Font.PLAIN, 13));
        serviceLabel.setForeground(Color.GRAY);

        infoPanel.add(nameLabel);
        infoPanel.add(serviceLabel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setOpaque(false);

        JLabel quantityLabel = new JLabel("จำนวน " + item.quantity + " ชิ้น");
        quantityLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        quantityLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel priceLabel = new JLabel(String.format("%,d", item.getTotalPrice()) + " บาท");
        priceLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        priceLabel.setForeground(new Color(0, 150, 150));
        priceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        rightPanel.add(quantityLabel);
        rightPanel.add(priceLabel);

        panel.add(infoPanel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private void clearAllOrders() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "ต้องการล้างรายการทั้งหมดใช่หรือไม่?",
                "ยืนยัน",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            orderItems.clear();
            updateSummary();
        }
    }

    private Image createClockIcon() {
        int size = 16;
        Image img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(Color.WHITE);
        g2.fillOval(0, 0, size, size);
        g2.setColor(new Color(0, 204, 204));
        g2.setStroke(new BasicStroke(2));
        g2.drawLine(8, 8, 8, 4);
        g2.drawLine(8, 8, 11, 8);

        g2.dispose();
        return img;
    }


    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {

            LaundryServiceFrame frame = new LaundryServiceFrame(1, null);
            frame.setVisible(true);
        });
    }

}
