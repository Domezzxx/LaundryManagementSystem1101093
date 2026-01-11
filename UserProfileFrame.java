import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.sql.*;

public class UserProfileFrame extends JFrame {

    private String currentUsername = "";
    private String currentCustomerName = "";
    private String currentEmail = "";

    // Database Connection Settings
    private final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "DomeDome55&55";

    public UserProfileFrame(String username) {
        this.currentUsername = username;

        // โหลดข้อมูลจาก DB ก่อนสร้างหน้าจอ
        loadUserData();

        initComponents();
    }

    private void initComponents() {
        setTitle("โปรไฟล์ผู้ใช้ - ปลายฟ้า LAUNDRY");
        setSize(1920, 1080);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // สำคัญมาก: ต้องใช้ DISPOSE เพื่อให้ HomeFrame ทำงานต่อ
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- Header (สีฟ้าด้านบน) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 204, 204)); // สีธีมหลัก
        headerPanel.setPreferredSize(new Dimension(1000, 80));

        // ✅ 1. สร้าง Panel ย่อยสำหรับฝั่งซ้าย (ปุ่มย้อนกลับ + หัวข้อ)
        JPanel leftHeaderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 20));
        leftHeaderPanel.setOpaque(false); // ให้พื้นหลังใส เพื่อเห็นสีฟ้า

        // ✅ 2. สร้างปุ่มย้อนกลับ
        JButton backButton = new JButton("⬅ ย้อนกลับ");
        backButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        backButton.setForeground(Color.WHITE);
        backButton.setBackground(new Color(0, 204, 204));
        backButton.setBorder(null); // ไม่มีขอบ
        backButton.setFocusPainted(false); // ไม่มีกรอบเวลาคลิก
        backButton.setContentAreaFilled(false); // พื้นหลังใส
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // ✅ 3. Action ของปุ่มย้อนกลับ: ปิดหน้านี้ (HomeFrame จะเด้งกลับมาเองเพราะ WindowListener)
        backButton.addActionListener(e -> this.dispose());

        JLabel titleLabel = new JLabel("ข้อมูลส่วนตัว"); // เอา space ข้างหน้าออก เพราะเราใช้ gap ของ FlowLayout แล้ว
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        // นำปุ่มและหัวข้อใส่ใน Panel ย่อย
        leftHeaderPanel.add(backButton);
        leftHeaderPanel.add(titleLabel);

        // นำ Panel ย่อยใส่ใน Header หลัก
        headerPanel.add(leftHeaderPanel, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // --- Main Content (พื้นหลังสีเทาอ่อน) ---
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(240, 245, 250));
        mainPanel.setBorder(new EmptyBorder(40, 0, 0, 0));

        // 1. Profile Card Section
        JPanel profileCard = createProfileCard();
        profileCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(profileCard);

        mainPanel.add(Box.createRigidArea(new Dimension(0, 40))); // เว้นระยะ

        // 2. Menu Section
        JPanel menuPanel = createMenuPanel();
        menuPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(menuPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createProfileCard() {
        // ใช้ GridBagLayout เพื่อการจัดกึ่งกลางที่แม่นยำที่สุด
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(600, 220));
        card.setMaximumSize(new Dimension(600, 220));

        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 1.0;

        // --- รูปโปรไฟล์ ---
        JLabel avatarLabel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(100, 149, 237));
                g2.fillOval(0, 0, getWidth(), getHeight());

                g2.setColor(Color.WHITE);
                g2.fillOval(25, 20, 30, 30);
                g2.fillArc(15, 55, 50, 40, 0, 180);
            }
        };
        avatarLabel.setPreferredSize(new Dimension(80, 80));

        gbc.insets = new Insets(0, 0, 15, 0);
        card.add(avatarLabel, gbc);

        // --- ชื่อ ---
        JLabel nameLabel = new JLabel(currentCustomerName);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        nameLabel.setForeground(new Color(60, 60, 60));
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(nameLabel, gbc);

        // --- อีเมล ---
        JLabel emailLabel = new JLabel("<html><center><u>" + currentEmail + "</u></center></html>");
        emailLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        emailLabel.setForeground(Color.GRAY);
        emailLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        emailLabel.setHorizontalAlignment(SwingConstants.CENTER);

        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(emailLabel, gbc);

        return card;
    }

    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(600, 300));

        // สร้างเมนู
        panel.add(createMenuItem("📝", "ข้อกำหนดและเงื่อนไข", e -> {
            new TermsAndConditionsFrame().setVisible(true);
        }));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        panel.add(createMenuItem("🔑", "เปลี่ยนรหัสผ่าน", e -> openChangePasswordDialog()));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));



        return panel;
    }

    private JPanel createMenuItem(String icon, String text, java.awt.event.ActionListener action) {
        JPanel item = new JPanel(new BorderLayout());
        item.setBackground(Color.WHITE);
        item.setMaximumSize(new Dimension(600, 60));
        item.setPreferredSize(new Dimension(600, 60));
        item.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 24));

        JLabel textLabel = new JLabel("  " + text);
        textLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        textLabel.setForeground(new Color(60, 60, 60));

        item.add(iconLabel, BorderLayout.WEST);
        item.add(textLabel, BorderLayout.CENTER);

        item.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                item.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(MouseEvent e) {
                item.setBackground(Color.WHITE);
            }
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return item;
    }

    private void openChangePasswordDialog() {
        ChangePasswordDialog dialog = new ChangePasswordDialog(this, currentUsername);
        dialog.setVisible(true);
    }

    private void loadUserData() {
        String sql = "SELECT name, email FROM customer WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentUsername);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                this.currentCustomerName = rs.getString("name");
                this.currentEmail = rs.getString("email");
            } else {
                this.currentCustomerName = "Unknown User";
                this.currentEmail = "-";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error Database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new UserProfileFrame("UMLZ").setVisible(true));
    }
}