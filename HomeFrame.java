import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.net.URL;
import java.sql.*;
import java.awt.image.BufferedImage;

public class HomeFrame extends JFrame {


    private String customerName;
    private int customerId;
    private String username;


    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "DomeDome55&55";

    public HomeFrame(int customerId, String username) {
        this.customerId = customerId;
        this.username = username;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("ไม่พบ MySQL JDBC Driver!");
        }

        loadCustomerName();
        initComponents();

    }

    public HomeFrame(int customerId) {
        this(customerId, "");
    }

    private void loadCustomerName() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT name FROM customer WHERE customer_id = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, customerId);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        customerName = rs.getString("name");
                    } else {
                        customerName = "ผู้ใช้";
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            customerName = "ผู้ใช้";
            JOptionPane.showMessageDialog(this,
                    "ไม่สามารถโหลดข้อมูลผู้ใช้ได้: " + e.getMessage(),
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initComponents() {
        setTitle("ปลายฟ้า LAUNDRY");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = createTopBar();
        JPanel headerPanel = createHeaderPanel();

        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(230, 235, 245));
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 25, 15, 25);
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;

        // แถวที่ 1 - ✅ แก้ไขชื่อไฟล์ให้ตรงกับที่มีในโฟลเดอร์
        String[] row1Labels = {"ซัก", "ซักรีด", "ซักแห้ง"};
        // ✅ ชื่อไฟล์รูป (ต้องวางไว้ที่เดียวกับ Ellipse_100.png)
        String[] row1Icons = {"icon_wash", "icon_iron", "icon_dryclean"};

        for (int i = 0; i < 3; i++) {
            gbc.gridx = i;
            gbc.gridy = 0;
            mainPanel.add(createServiceCard(row1Labels[i], row1Icons[i]), gbc);
        }

        // แถวที่ 2 - ✅ แก้ไขชื่อไฟล์ให้ตรงกับที่มีในโฟลเดอร์
        JPanel row2Panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 50, 0));
        row2Panel.setOpaque(false);
        row2Panel.add(createServiceCard("ประวัติการใช้งาน", "icon_history"));
        row2Panel.add(createServiceCard("เช็คสถานะ", "icon_status"));

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(30, 0, 0, 0);
        mainPanel.add(row2Panel, gbc);

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

        JLabel phoneLabel = new JLabel("📞 01-234-5678");
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        JLabel lineLabel = new JLabel("💬 @Laundry Clean & Fresh");
        lineLabel.setForeground(Color.WHITE);
        lineLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

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
                    HomeFrame.this.setVisible(true);
                }
            });
            profileFrame.setVisible(true);
        });

        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(HomeFrame.this,
                    "คุณต้องการออกจากระบบหรือไม่?",
                    "ยืนยันการออกจากระบบ",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                HomeFrame.this.dispose();
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

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        headerPanel.setBackground(new Color(230, 235, 245));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JLabel logoLabel = new JLabel(createLogoIcon());

        JLabel titleLabel = new JLabel("ปลายฟ้า LAUNDRY");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        titleLabel.setForeground(new Color(50, 50, 50));

        headerPanel.add(logoLabel);
        headerPanel.add(titleLabel);

        return headerPanel;
    }

    private JPanel createServiceCard(String title, String iconFileName) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 20, 20));
                g2.dispose();
                super.paintComponent(g);
            }
        };

        card.setLayout(new BorderLayout(0, 10));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(25, 10, 20, 10));
        card.setPreferredSize(new Dimension(220, 190));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(createServiceIcon(iconFileName));
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(iconLabel, BorderLayout.CENTER);
        card.add(titleLabel, BorderLayout.SOUTH);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(240, 245, 255));
                card.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
                card.repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (title.equals("ซัก") || title.equals("ซักรีด") || title.equals("ซักแห้ง")) {
                    openLaundryServiceFrame();
                } else if (title.equals("เช็คสถานะ")) {
                    openStatusCheckFrame();
                } else if (title.equals("ประวัติการใช้งาน")) {
                    openOrderHistoryFrame();
                } else {
                    JOptionPane.showMessageDialog(HomeFrame.this,
                            "เปิดหน้า: " + title,
                            "แจ้งเตือน",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        return card;
    }

    private void openLaundryServiceFrame() {
        this.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            LaundryServiceFrame serviceFrame = new LaundryServiceFrame(customerId, this);
            serviceFrame.setVisible(true);
        });
    }

    private void openStatusCheckFrame() {
        this.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            StatusCheckFrame statusFrame = new StatusCheckFrame(customerId, this.username);
            statusFrame.setVisible(true);

            statusFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    // ถ้าต้องการให้กลับมาแสดงหน้า Home
                }
            });
        });
    }

    private void openOrderHistoryFrame() {
        this.setVisible(false);

        SwingUtilities.invokeLater(() -> {
            JFrame historyFrame = new JFrame("ปลายฟ้า LAUNDRY - ประวัติการใช้บริการ");
            historyFrame.setSize(1920, 1080);
            historyFrame.setLocationRelativeTo(null);
            historyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

            ActionListener backListener = e -> historyFrame.dispose();

            OrderHistoryPanel historyPanel = new OrderHistoryPanel(customerId, customerName, backListener);
            historyFrame.add(historyPanel);

            historyFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    HomeFrame.this.setVisible(true);
                }
            });

            historyFrame.setVisible(true);
        });
    }



    private ImageIcon createServiceIcon(String iconName) {
        int size = 100;
        try {

            URL imgURL = getClass().getResource("/" + iconName + ".png");

            if (imgURL != null) {
                return new ImageIcon(new ImageIcon(imgURL).getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {

        }


        String serviceName = "";
        return createDefaultIconFallback(serviceName, size);
    }



    private ImageIcon createDefaultIconFallback(String serviceName, int size) {
        Image img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(200, 200, 200));
        g2.fillOval(10, 10, size - 20, size - 20);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 40));
        g2.drawString("?", size/2 - 10, size/2 + 15);

        g2.dispose();
        return new ImageIcon(img);
    }

    private ImageIcon createLogoIcon() {
        int size = 70;
        try {

            URL imgURL = getClass().getResource("/resources/Ellipse_100.png");
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image scaledImg = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImg);
            } else {
                return createFallbackLogo(size);
            }
        } catch (Exception e) {
            return createFallbackLogo(size);
        }
    }

    private ImageIcon createFallbackLogo(int size) {
        Image img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 180, 220));
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return new ImageIcon(img);
    }



    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            HomeFrame frame = new HomeFrame(1, "testuser");
            frame.setVisible(true);
        });
    }
}