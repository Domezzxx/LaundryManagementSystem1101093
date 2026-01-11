import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.nio.charset.StandardCharsets;

public class LaundryRegister extends JFrame {
    private JTextField nameField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton registerButton;
    private JButton backButton;


    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db?characterEncoding=UTF-8";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "DomeDome55&55";


    private static class ThaiText {
        static final String TITLE = new String("ซักผ้าไหม LAUNDRY - สมัครสมาชิก".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String REGISTER = new String("สมัครสมาชิก".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String SUBTITLE = new String("กรุณากรอกข้อมูลของคุณ".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String NAME_LABEL = new String("ชื่อ-นามสกุล:".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String NAME_PLACEHOLDER = new String("กรอกชื่อ-นามสกุล".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String PHONE_LABEL = new String("เบอร์โทรศัพท์:".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String PHONE_PLACEHOLDER = new String("กรอกเบอร์โทรศัพท์".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String EMAIL_LABEL = new String("อีเมล:".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String EMAIL_PLACEHOLDER = new String("กรอกอีเมล".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String USERNAME_LABEL = new String("ชื่อผู้ใช้:".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String USERNAME_PLACEHOLDER = new String("กรอกชื่อผู้ใช้".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String PASSWORD_LABEL = new String("รหัสผ่าน:".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String PASSWORD_PLACEHOLDER = new String("กรอกรหัสผ่าน".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String CONFIRM_PASSWORD_LABEL = new String("ยืนยันรหัสผ่าน:".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String CONFIRM_PASSWORD_PLACEHOLDER = new String("กรอกรหัสผ่านอีกครั้ง".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        static final String BACK = new String("ย้อนกลับ".getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
    }

    public LaundryRegister() {
        // Force UTF-8 encoding
        System.setProperty("file.encoding", "UTF-8");

        setTitle(ThaiText.TITLE);
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        getContentPane().setBackground(new Color(240, 240, 240));


        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                centerComponents();
            }
        });


        initializeComponents();
    }

    private void initializeComponents() {

        nameField = new JTextField();

        phoneField = new JTextField();


        emailField = new JTextField();


        usernameField = new JTextField();


        passwordField = new JPasswordField();


        confirmPasswordField = new JPasswordField();


        registerButton = new JButton(ThaiText.REGISTER);
        registerButton.setBackground(new Color(0, 180, 200));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> handleRegister());
        add(registerButton);


        backButton = new JButton(ThaiText.BACK);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(new Color(0, 180, 200));
        backButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        backButton.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 200), 2));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> backToLogin());
        add(backButton);
    }

    private void centerComponents() {

        int windowWidth = getWidth();
        int windowHeight = getHeight();


        int formWidth = 500;
        int fieldHeight = 50;
        int labelHeight = 25;
        int verticalSpacing = 70;


        int centerX = (windowWidth - formWidth) / 2;


        int totalHeight = 80 + 30 + (verticalSpacing * 6) + 45 + 20;
        int startY = Math.max(30, (windowHeight - totalHeight) / 2);


        getContentPane().removeAll();


        JPanel logoPanel = new JPanel();
        logoPanel.setBounds(centerX, startY, formWidth, 80);
        logoPanel.setBackground(new Color(240, 240, 240));
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        JLabel titleLabel = new JLabel(ThaiText.REGISTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        titleLabel.setForeground(new Color(0, 150, 200));
        logoPanel.add(titleLabel);
        add(logoPanel);

        JLabel subtitleLabel = new JLabel(ThaiText.SUBTITLE);
        subtitleLabel.setBounds(centerX, startY + 80, formWidth, 30);
        subtitleLabel.setFont(new Font("Tahoma", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.GRAY);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(subtitleLabel);

        int currentY = startY + 110;


        createLabeledField(ThaiText.NAME_LABEL, nameField, centerX, currentY, formWidth, "👤 ", ThaiText.NAME_PLACEHOLDER);
        currentY += verticalSpacing;


        createLabeledField(ThaiText.PHONE_LABEL, phoneField, centerX, currentY, formWidth, "📱 ", ThaiText.PHONE_PLACEHOLDER);
        currentY += verticalSpacing;


        createLabeledField(ThaiText.EMAIL_LABEL, emailField, centerX, currentY, formWidth, "📧 ", ThaiText.EMAIL_PLACEHOLDER);
        currentY += verticalSpacing;


        createLabeledField(ThaiText.USERNAME_LABEL, usernameField, centerX, currentY, formWidth, "👨 ", ThaiText.USERNAME_PLACEHOLDER);
        currentY += verticalSpacing;


        createPasswordField(ThaiText.PASSWORD_LABEL, passwordField, centerX, currentY, formWidth, "🔒 ", ThaiText.PASSWORD_PLACEHOLDER);
        currentY += verticalSpacing;


        createPasswordField(ThaiText.CONFIRM_PASSWORD_LABEL, confirmPasswordField, centerX, currentY, formWidth, "🔒 ", ThaiText.CONFIRM_PASSWORD_PLACEHOLDER);
        currentY += verticalSpacing;


        int buttonWidth = 200;
        int buttonSpacing = 10;
        int totalButtonWidth = (buttonWidth * 2) + buttonSpacing;
        int buttonStartX = centerX + (formWidth - totalButtonWidth) / 2;

        registerButton.setBounds(buttonStartX, currentY + 10, buttonWidth, 45);
        add(registerButton);

        backButton.setBounds(buttonStartX + buttonWidth + buttonSpacing, currentY + 10, buttonWidth, 45);
        add(backButton);


        revalidate();
        repaint();
    }

    private void createLabeledField(String labelText, JTextField textField, int xPosition, int yPosition, int width, String icon, String placeholder) {

        JLabel label = new JLabel(labelText);
        label.setBounds(xPosition, yPosition - 25, width, 25);
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        add(label);

        JPanel fieldPanel = new JPanel();
        fieldPanel.setBounds(xPosition, yPosition, width, 50);
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel fieldIcon = new JLabel(icon);
        fieldIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        fieldPanel.add(fieldIcon, BorderLayout.WEST);

        textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);

        final String finalPlaceholder = placeholder;
        textField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(finalPlaceholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                if (textField.getText().trim().isEmpty()) {
                    textField.setText(finalPlaceholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });

        fieldPanel.add(textField, BorderLayout.CENTER);
        add(fieldPanel);
    }

    private void createPasswordField(String labelText, JPasswordField passwordField, int xPosition, int yPosition, int width, String icon, String placeholder) {

        JLabel label = new JLabel(labelText);
        label.setBounds(xPosition, yPosition - 25, width, 25);
        label.setFont(new Font("Tahoma", Font.BOLD, 14));
        label.setForeground(new Color(60, 60, 60));
        add(label);


        JPanel fieldPanel = new JPanel();
        fieldPanel.setBounds(xPosition, yPosition, width, 50);
        fieldPanel.setLayout(new BorderLayout());
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel fieldIcon = new JLabel(icon);
        fieldIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        fieldPanel.add(fieldIcon, BorderLayout.WEST);

        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        passwordField.setEchoChar((char) 0);
        passwordField.setText(placeholder);
        passwordField.setForeground(Color.GRAY);

        final String finalPlaceholder = placeholder;
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                String currentText = new String(passwordField.getPassword());
                if (currentText.equals(finalPlaceholder)) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.BLACK);
                }
            }
            public void focusLost(FocusEvent e) {
                String currentText = new String(passwordField.getPassword());
                if (currentText.trim().isEmpty()) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText(finalPlaceholder);
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });

        fieldPanel.add(passwordField, BorderLayout.CENTER);
        add(fieldPanel);
    }

    private void handleRegister() {

        String name = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());


        if (name.isEmpty() || name.equals(ThaiText.NAME_PLACEHOLDER)) {
            showError("กรุณากรอกชื่อ-นามสกุล");
            return;
        }

        if (phone.isEmpty() || phone.equals(ThaiText.PHONE_PLACEHOLDER)) {
            showError("กรุณากรอกเบอร์โทรศัพท์");
            return;
        }

        if (!phone.matches("\\d{10}")) {
            showError("เบอร์โทรศัพท์ต้องเป็นตัวเลข 10 หลัก");
            return;
        }

        if (email.isEmpty() || email.equals(ThaiText.EMAIL_PLACEHOLDER)) {
            showError("กรุณากรอกอีเมล");
            return;
        }

        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            showError("รูปแบบอีเมลไม่ถูกต้อง");
            return;
        }

        if (username.isEmpty() || username.equals(ThaiText.USERNAME_PLACEHOLDER)) {
            showError("กรุณากรอกชื่อผู้ใช้");
            return;
        }

        if (password.isEmpty() || password.equals(ThaiText.PASSWORD_PLACEHOLDER)) {
            showError("กรุณากรอกรหัสผ่าน");
            return;
        }

        if (password.length() < 6) {
            showError("รหัสผ่านต้องมีอย่างน้อย 6 ตัวอักษร");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("รหัสผ่านไม่ตรงกัน");
            return;
        }


        if (registerUser(name, phone, email, username, password)) {
            JOptionPane.showMessageDialog(this,
                    "สมัครสมาชิกสำเร็จ!\nกรุณาเข้าสู่ระบบด้วยชื่อผู้ใช้และรหัสผ่านของคุณ",
                    "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE);
            backToLogin();
        }
    }

    private boolean registerUser(String name, String phone, String email, String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);


            String checkQuery = "SELECT username FROM customer WHERE username = ?";
            pstmt = conn.prepareStatement(checkQuery);
            pstmt.setString(1, username);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                showError("ชื่อผู้ใช้นี้ถูกใช้แล้ว");
                return false;
            }

            rs.close();
            pstmt.close();


            String insertQuery = "INSERT INTO customer (name, phone, email, username, password) VALUES (?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(insertQuery);
            pstmt.setString(1, name);
            pstmt.setString(2, phone);
            pstmt.setString(3, email);
            pstmt.setString(4, username);
            pstmt.setString(5, password);

            int result = pstmt.executeUpdate();
            return result > 0;

        } catch (ClassNotFoundException e) {
            showError("ไม่พบ MySQL JDBC Driver");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            showError("เกิดข้อผิดพลาดในการสมัครสมาชิก:\n" + e.getMessage());
            e.printStackTrace();
            return false;
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

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "ข้อผิดพลาด",
                JOptionPane.ERROR_MESSAGE);
    }

    private void backToLogin() {
        this.dispose();
        new LoginFrame().setVisible(true);
    }

    public static void main(String[] args) {
        // Set UTF-8 as default encoding
        System.setProperty("file.encoding", "UTF-8");

        SwingUtilities.invokeLater(() -> {
            new LaundryRegister().setVisible(true);
        });
    }
}