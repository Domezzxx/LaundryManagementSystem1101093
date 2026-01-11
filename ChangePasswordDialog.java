import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class ChangePasswordDialog extends JDialog {

    private String username;
    private JPasswordField txtOldPass, txtNewPass, txtConfirmPass;


    private final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private final String DB_USER = "root";
    private final String DB_PASS = "";

    public ChangePasswordDialog(JFrame parent, String username) {
        super(parent, "เปลี่ยนรหัสผ่าน", true);
        this.username = username;

        initComponents();
    }

    private void initComponents() {
        setSize(500, 550);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(240, 245, 250)); // พื้นหลังเทาอ่อน


        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(500, 150));
        topPanel.setLayout(new GridBagLayout());

        JLabel avatar = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(65, 105, 225)); // Royal Blue
                g2.fillOval(0, 0, 100, 100);

                // รูปคนแบบง่ายๆ
                g2.setColor(Color.WHITE);
                g2.fillOval(30, 25, 40, 40);
                g2.fillArc(20, 70, 60, 50, 0, 180);
            }
        };
        avatar.setPreferredSize(new Dimension(100, 100));
        topPanel.add(avatar);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new EmptyBorder(30, 40, 30, 40));
        formPanel.setLayout(new GridLayout(7, 1, 0, 10)); // Grid แนวตั้ง

        JLabel lblTitle = new JLabel("เปลี่ยนรหัสผ่าน", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 20));

        formPanel.add(lblTitle);
        formPanel.add(createLabel("รหัสผ่านเดิม"));
        txtOldPass = createPasswordField();
        formPanel.add(txtOldPass);

        formPanel.add(createLabel("รหัสผ่านใหม่"));
        txtNewPass = createPasswordField();
        formPanel.add(txtNewPass);

        formPanel.add(createLabel("ยืนยันรหัสผ่านใหม่"));
        txtConfirmPass = createPasswordField();
        formPanel.add(txtConfirmPass);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.setBorder(new EmptyBorder(10, 0, 20, 0));

        JButton btnSave = new JButton("ยืนยัน");
        btnSave.setFont(new Font("Tahoma", Font.BOLD, 16));
        btnSave.setBackground(new Color(0, 204, 204));
        btnSave.setForeground(Color.WHITE);
        btnSave.setPreferredSize(new Dimension(200, 45));
        btnSave.setFocusPainted(false);
        btnSave.setBorderPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnSave.addActionListener(e -> updatePassword());

        btnPanel.add(btnSave);


        add(topPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        label.setForeground(new Color(80, 80, 80));
        return label;
    }

    private JPasswordField createPasswordField() {
        JPasswordField pf = new JPasswordField();
        pf.setFont(new Font("Tahoma", Font.PLAIN, 14));
        pf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return pf;
    }

    private void updatePassword() {
        String oldP = new String(txtOldPass.getPassword());
        String newP = new String(txtNewPass.getPassword());
        String confP = new String(txtConfirmPass.getPassword());

        if (oldP.isEmpty() || newP.isEmpty() || confP.isEmpty()) {
            JOptionPane.showMessageDialog(this, "กรุณากรอกข้อมูลให้ครบถ้วน", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newP.equals(confP)) {
            JOptionPane.showMessageDialog(this, "รหัสผ่านใหม่ไม่ตรงกัน", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {
            // 1. ตรวจสอบรหัสผ่านเดิม
            String checkSql = "SELECT password FROM customer WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                String currentDbPass = rs.getString("password");
                if (!currentDbPass.equals(oldP)) {
                    JOptionPane.showMessageDialog(this, "รหัสผ่านเดิมไม่ถูกต้อง", "ผิดพลาด", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }


            String updateSql = "UPDATE customer SET password = ? WHERE username = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateSql);
            updateStmt.setString(1, newP);
            updateStmt.setString(2, username);

            int rows = updateStmt.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "เปลี่ยนรหัสผ่านสำเร็จ!", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "ไม่สามารถเปลี่ยนรหัสผ่านได้", "ผิดพลาด", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + e.getMessage());
        }
    }

}
