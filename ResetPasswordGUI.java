// ResetPasswordGUI.java - ใช้ Token แทนการส่งอีเมล
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ResetPasswordGUI extends JFrame {
    private JTextField emailField;
    private JTextField tokenField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;
    private JButton generateTokenButton;
    private JButton resetButton;
    private JLabel tokenDisplayLabel;
    private JPanel tokenPanel;
    private JPanel resetPanel;
    private JPanel cardPanel; // เพิ่มตัวแปรนี้

    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "DomeDome55&55";

    private String generatedToken = null;
    private String userEmail = null;

    public ResetPasswordGUI() {
        setTitle("ลืมรหัสผ่าน - ปลายฟ้า Laundry");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);


        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);


        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(Color.WHITE);
        logoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        JLabel logoLabel = new JLabel("ปลายฟ้า LAUNDRY");
        logoLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        logoLabel.setForeground(new Color(0, 174, 239));
        logoPanel.add(logoLabel);


        cardPanel = new JPanel(new CardLayout());
        cardPanel.setBackground(Color.WHITE);


        tokenPanel = createTokenPanel();


        resetPanel = createResetPanel();

        cardPanel.add(tokenPanel, "TOKEN");
        cardPanel.add(resetPanel, "RESET");

        mainPanel.add(logoPanel, BorderLayout.NORTH);
        mainPanel.add(cardPanel, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createTokenPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));


        JLabel instructionLabel = new JLabel("กรอกอีเมลเพื่อรับรหัส Token");
        instructionLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        JLabel emailLabel = new JLabel("อีเมล ของคุณ");
        emailLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);


        emailField = new JTextField();
        emailField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        emailField.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));


        generateTokenButton = new JButton("สร้าง Token");
        generateTokenButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        generateTokenButton.setBackground(new Color(0, 174, 239));
        generateTokenButton.setForeground(Color.WHITE);
        generateTokenButton.setFocusPainted(false);
        generateTokenButton.setBorderPainted(false);
        generateTokenButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        generateTokenButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        generateTokenButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        tokenDisplayLabel = new JLabel(" ");
        tokenDisplayLabel.setFont(new Font("Monospaced", Font.BOLD, 32));
        tokenDisplayLabel.setForeground(new Color(0, 174, 239));
        tokenDisplayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel tokenDisplayPanel = new JPanel();
        tokenDisplayPanel.setBackground(new Color(240, 248, 255));
        tokenDisplayPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 174, 239), 2),
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        tokenDisplayPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        tokenDisplayPanel.setMinimumSize(new Dimension(0, 80));
        tokenDisplayPanel.setPreferredSize(new Dimension(Integer.MAX_VALUE, 80));
        tokenDisplayPanel.add(tokenDisplayLabel);
        tokenDisplayPanel.setOpaque(false);
        tokenDisplayPanel.setBorder(null);

        panel.add(instructionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(emailLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(emailField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(generateTokenButton);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(tokenDisplayPanel);


        generateTokenButton.addActionListener(e -> handleGenerateToken(tokenDisplayPanel));

        return panel;
    }

    private JPanel createResetPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 40, 20, 40));

        JLabel tokenLabel = new JLabel("รหัส Token (6 หลัก)");
        tokenLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        tokenLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        tokenField = new JTextField();
        tokenField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        tokenField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        tokenField.setAlignmentX(Component.LEFT_ALIGNMENT);
        tokenField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));


        JLabel newPasswordLabel = new JLabel("รหัสผ่านใหม่");
        newPasswordLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        newPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        newPasswordField = new JPasswordField();
        newPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        newPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        newPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        newPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));


        JLabel confirmPasswordLabel = new JLabel("ยืนยันรหัสผ่านใหม่");
        confirmPasswordLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        confirmPasswordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        confirmPasswordField = new JPasswordField();
        confirmPasswordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        confirmPasswordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        confirmPasswordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));


        resetButton = new JButton("รีเซ็ตรหัสผ่าน");
        resetButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        resetButton.setBackground(new Color(0, 174, 239));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        resetButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        resetButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        resetButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        JButton backButton = new JButton("← กลับ");
        backButton.setFont(new Font("Tahoma", Font.PLAIN, 12));
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(0, 174, 239));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(true);
        backButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        panel.add(tokenLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(tokenField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(newPasswordLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(newPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(confirmPasswordLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 8)));
        panel.add(confirmPasswordField);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(resetButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(backButton);


        resetButton.addActionListener(e -> handleResetPassword());
        backButton.addActionListener(e -> switchToPanel("TOKEN"));

        return panel;
    }

    private void handleGenerateToken(JPanel tokenDisplayPanel) {
        String email = emailField.getText().trim();

        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "กรุณากรอกอีเมล",
                    "ข้อมูลไม่ครบถ้วน",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        generateTokenButton.setEnabled(false);
        generateTokenButton.setText("กำลังตรวจสอบ...");

        new Thread(() -> {
            try {
                if (checkEmailExists(email)) {
                    generatedToken = generateToken();
                    userEmail = email;

                    // บันทึก token ลงฐานข้อมูล (optional - สำหรับเก็บประวัติ)
                    saveTokenToDatabase(email, generatedToken);

                    SwingUtilities.invokeLater(() -> {
                        tokenDisplayLabel.setText(generatedToken);
                        tokenDisplayLabel.setVisible(true);
                        tokenDisplayPanel.setVisible(true);

                        JOptionPane.showMessageDialog(ResetPasswordGUI.this,
                                "Token ถูกสร้างเรียบร้อยแล้ว!\nกรุณาจดจำหรือคัดลอก Token นี้\n\nToken: " + generatedToken + "\n\nToken นี้ใช้ได้ครั้งเดียวเท่านั้น",
                                "Token สำเร็จ",
                                JOptionPane.INFORMATION_MESSAGE);

                        // เปลี่ยนปุ่มให้ไปหน้าถัดไป
                        generateTokenButton.setText("ไปหน้ารีเซ็ตรหัสผ่าน →");
                        generateTokenButton.removeActionListener(generateTokenButton.getActionListeners()[0]);
                        generateTokenButton.addActionListener(e -> switchToPanel("RESET"));
                    });
                } else {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(ResetPasswordGUI.this,
                                "ไม่พบอีเมลนี้ในระบบ",
                                "ข้อผิดพลาด",
                                JOptionPane.ERROR_MESSAGE);
                    });
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(ResetPasswordGUI.this,
                            "เกิดข้อผิดพลาด: " + ex.getMessage(),
                            "ข้อผิดพลาด",
                            JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    generateTokenButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void handleResetPassword() {
        String token = tokenField.getText().trim();
        String newPassword = new String(newPasswordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (token.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "กรุณากรอกข้อมูลให้ครบถ้วน",
                    "ข้อมูลไม่ครบถ้วน",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this,
                    "รหัสผ่านไม่ตรงกัน",
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!token.equals(generatedToken)) {
            JOptionPane.showMessageDialog(this,
                    "Token ไม่ถูกต้อง",
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        resetButton.setEnabled(false);
        resetButton.setText("กำลังรีเซ็ต...");

        new Thread(() -> {
            try {
                updatePassword(userEmail, newPassword);

                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(ResetPasswordGUI.this,
                            "รีเซ็ตรหัสผ่านสำเร็จ!\nกรุณาเข้าสู่ระบบด้วยรหัสผ่านใหม่",
                            "สำเร็จ",
                            JOptionPane.INFORMATION_MESSAGE);

                    // ล้างข้อมูลและกลับไปหน้าแรก
                    ResetPasswordGUI.this.dispose();  // ปิดหน้า Reset Password
                    new LoginFrame().setVisible(true); // เปิดหน้า Login
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(ResetPasswordGUI.this,
                            "เกิดข้อผิดพลาด: " + ex.getMessage(),
                            "ข้อผิดพลาด",
                            JOptionPane.ERROR_MESSAGE);
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    resetButton.setEnabled(true);
                    resetButton.setText("รีเซ็ตรหัสผ่าน");
                });
            }
        }).start();
    }


    private void switchToPanel(String panelName) {
        CardLayout cl = (CardLayout) cardPanel.getLayout();
        cl.show(cardPanel, panelName);
    }

    private void resetForm() {
        emailField.setText("");
        tokenField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
        tokenDisplayLabel.setVisible(false);
        tokenDisplayLabel.getParent().setVisible(false);
        generatedToken = null;
        userEmail = null;
        generateTokenButton.setText("สร้าง Token");
    }

    private boolean checkEmailExists(String email) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT email FROM customer WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                try (ResultSet rs = pstmt.executeQuery()) {
                    return rs.next();
                }
            }
        }
    }

    private String generateToken() {
        Random random = new Random();
        int token = 100000 + random.nextInt(900000); // สร้างเลข 6 หลัก
        return String.valueOf(token);
    }

    private void saveTokenToDatabase(String email, String token) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String createTable = "CREATE TABLE IF NOT EXISTS reset_tokens (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "email VARCHAR(255) NOT NULL, " +
                    "token VARCHAR(10) NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "used BOOLEAN DEFAULT FALSE)";

            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTable);
            }


            String query = "INSERT INTO reset_tokens (email, token) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, email);
                pstmt.setString(2, token);
                pstmt.executeUpdate();
            }
        }
    }

    private void updatePassword(String email, String newPassword) throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE customer SET password = ? WHERE email = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, newPassword); // ควรเข้ารหัส password ด้วย hash
                pstmt.setString(2, email);
                pstmt.executeUpdate();
            }


            String updateToken = "UPDATE reset_tokens SET used = TRUE WHERE email = ? AND token = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateToken)) {
                pstmt.setString(1, email);
                pstmt.setString(2, generatedToken);
                pstmt.executeUpdate();
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new ResetPasswordGUI().setVisible(true);
        });
    }
}