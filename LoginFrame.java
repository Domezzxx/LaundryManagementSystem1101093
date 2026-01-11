import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;


    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "DomeDome55&55";

    public LoginFrame() {
        setTitle("ปลายฟ้า LAUNDRY - เข้าสู่ระบบ");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);


        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;


        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(new Color(240, 240, 240));

        GridBagConstraints contentGbc = new GridBagConstraints();
        contentGbc.fill = GridBagConstraints.BOTH;
        contentGbc.gridy = 0;
        contentGbc.weighty = 1.0;


        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(new Color(240, 240, 240));


        JPanel leftContent = new JPanel();
        leftContent.setLayout(null);
        leftContent.setPreferredSize(new Dimension(450, 500));
        leftContent.setBackground(new Color(240, 240, 240));


        JLabel titleLabel = new JLabel("ปลายฟ้า LAUNDRY", SwingConstants.CENTER);
        titleLabel.setBounds(0, 0, 450, 80);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 42));
        titleLabel.setForeground(new Color(0, 150, 200));
        leftContent.add(titleLabel);


        JPanel usernamePanel = new JPanel();
        usernamePanel.setBounds(85, 120, 280, 55);
        usernamePanel.setLayout(new BorderLayout());
        usernamePanel.setBackground(Color.WHITE);
        usernamePanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel userIcon = new JLabel("📧");
        userIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        userIcon.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
        usernamePanel.add(userIcon, BorderLayout.WEST);

        usernameField = new JTextField("ชื่อผู้ใช้");
        usernameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        usernameField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        usernameField.setForeground(Color.GRAY);
        usernameField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("ชื่อผู้ใช้")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("ชื่อผู้ใช้");
                    usernameField.setForeground(Color.GRAY);
                }
            }
        });
        usernamePanel.add(usernameField, BorderLayout.CENTER);
        leftContent.add(usernamePanel);


        JPanel passwordPanel = new JPanel();
        passwordPanel.setBounds(85, 200, 280, 55);
        passwordPanel.setLayout(new BorderLayout());
        passwordPanel.setBackground(Color.WHITE);
        passwordPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));

        JLabel passIcon = new JLabel("🔒");
        passIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        passIcon.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 10));
        passwordPanel.add(passIcon, BorderLayout.WEST);

        passwordField = new JPasswordField();
        passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 18));
        passwordField.setEchoChar((char) 0);
        passwordField.setText("รหัสผ่าน");
        passwordField.setForeground(Color.GRAY);
        passwordField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                if (new String(passwordField.getPassword()).equals("รหัสผ่าน")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.BLACK);
                }
            }

            public void focusLost(FocusEvent e) {
                if (new String(passwordField.getPassword()).isEmpty()) {
                    passwordField.setEchoChar((char) 0);
                    passwordField.setText("รหัสผ่าน");
                    passwordField.setForeground(Color.GRAY);
                }
            }
        });
        passwordPanel.add(passwordField, BorderLayout.CENTER);
        leftContent.add(passwordPanel);

        JLabel forgotPasswordLabel = new JLabel("ลืมรหัสผ่าน");
        forgotPasswordLabel.setBounds(255, 265, 110, 30);
        forgotPasswordLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
        forgotPasswordLabel.setForeground(new Color(0, 180, 200));
        forgotPasswordLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotPasswordLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        forgotPasswordLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                dispose();
                new ResetPasswordGUI().setVisible(true);

            }

            public void mouseEntered(MouseEvent e) {
                forgotPasswordLabel.setForeground(new Color(0, 120, 160));
            }

            public void mouseExited(MouseEvent e) {
                forgotPasswordLabel.setForeground(new Color(0, 180, 200));
            }
        });
        leftContent.add(forgotPasswordLabel);


        loginButton = new JButton("เข้าสู่ระบบ");
        loginButton.setBounds(85, 310, 280, 50);
        loginButton.setBackground(new Color(0, 180, 200));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 20));
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());
        leftContent.add(loginButton);

        registerButton = new JButton("สมัครใช้งาน");
        registerButton.setBounds(85, 375, 280, 45);
        registerButton.setBackground(Color.WHITE);
        registerButton.setForeground(new Color(0, 180, 200));
        registerButton.setFont(new Font("Tahoma", Font.PLAIN, 18));
        registerButton.setBorder(BorderFactory.createLineBorder(new Color(0, 180, 200), 2));
        registerButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> openRegisterPage());
        leftContent.add(registerButton);

        GridBagConstraints leftGbc = new GridBagConstraints();
        leftPanel.add(leftContent, leftGbc);


        JPanel welcomePanel = new JPanel(new GridBagLayout());
        welcomePanel.setBackground(new Color(0, 200, 220));


        JPanel welcomeContent = new JPanel();
        welcomeContent.setLayout(null);
        welcomeContent.setPreferredSize(new Dimension(600, 600));
        welcomeContent.setBackground(new Color(0, 200, 220));

        JLabel welcomeTitle = new JLabel("ยินดีต้อนรับ");
        welcomeTitle.setBounds(0, 0, 500, 60);
        welcomeTitle.setFont(new Font("Tahoma", Font.BOLD, 42));
        welcomeTitle.setForeground(Color.WHITE);
        welcomeTitle.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeContent.add(welcomeTitle);

        JTextArea welcomeText = new JTextArea(
                "ปลายฟ้า Laundry บริการซักอบรีดคุณภาพ \n" +
                        "ที่ช่วยลดภาระงานบ้านให้คุณได้พักมากขึ้น \n\n" +
                        "เราใส่ใจทุกขั้นตอนตั้งแต่คัดแยกผ้า \n" +
                        "ซักด้วยน้ำยาที่เหมาะสม อบและพับอย่างประณีต \n\n" +
                        "เพื่อให้เสื้อผ้าของคุณสะอาด นุ่ม หอม \n" +
                        "พร้อมใช้งานทุกวัน"
        );
        welcomeText.setBounds(50, 100, 400, 200);
        welcomeText.setFont(new Font("Tahoma", Font.PLAIN, 16));
        welcomeText.setForeground(Color.WHITE);
        welcomeText.setBackground(new Color(0, 200, 220));
        welcomeText.setLineWrap(true);
        welcomeText.setWrapStyleWord(true);
        welcomeText.setEditable(false);
        welcomeContent.add(welcomeText);

        GridBagConstraints welcomeGbc = new GridBagConstraints();
        welcomePanel.add(welcomeContent, welcomeGbc);


        contentGbc.gridx = 0;
        contentGbc.weightx = 0.9; // 70% width
        contentPanel.add(leftPanel, contentGbc);

        contentGbc.gridx = 1;
        contentGbc.weightx = 0.1; // 30% width
        contentPanel.add(welcomePanel, contentGbc);


        mainPanel.add(contentPanel, gbc);

        add(mainPanel);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || username.equals("ชื่อผู้ใช้")) {
            JOptionPane.showMessageDialog(this,
                    "กรุณากรอกชื่อผู้ใช้",
                    "ข้อมูลไม่ครบถ้วน",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (password.isEmpty() || password.equals("รหัสผ่าน")) {
            JOptionPane.showMessageDialog(this,
                    "กรุณากรอกรหัสผ่าน",
                    "ข้อมูลไม่ครบถ้วน",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        AuthResult authResult = authenticateUser(username, password);

        if (authResult != null) {
            if (authResult.userType.equals("customer")) {
                JOptionPane.showMessageDialog(this,
                        "เข้าสู่ระบบสำเร็จ!\nยินดีต้อนรับคุณลูกค้า",
                        "สำเร็จ",
                        JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                // ✅ ส่งทั้ง userId และ username ไปยัง HomeFrame
                new HomeFrame(authResult.userId, authResult.username).setVisible(true);
            } else if (authResult.userType.equals("employee")) {
                JOptionPane.showMessageDialog(this,
                        "เข้าสู่ระบบสำเร็จ!\nยินดีต้อนรับพนักงาน",
                        "สำเร็จ",
                        JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new ServiceManagementFrame().setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง",
                    "เข้าสู่ระบบไม่สำเร็จ",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private class AuthResult {
        String userType;
        int userId;
        String username;

        AuthResult(String userType, int userId, String username) {
            this.userType = userType;
            this.userId = userId;
            this.username = username;
        }
    }

    private AuthResult authenticateUser(String username, String password) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            Class.forName("com.mysql.cj.jdbc.Driver");


            String customerQuery = "SELECT customer_id, username FROM customer WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(customerQuery)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        // ส่ง username กลับไป
                        return new AuthResult("customer", rs.getInt("customer_id"), rs.getString("username"));
                    }
                }
            }


            String employeeQuery = "SELECT employee_id FROM employee WHERE username = ? AND password = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(employeeQuery)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return new AuthResult("employee", rs.getInt("employee_id"), username);
                    }
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "เกิดข้อผิดพลาด:\n" + e.getMessage(),
                    "ข้อผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
        }
        return null;
    }

    private void openRegisterPage() {
        this.dispose();
        new LaundryRegister().setVisible(true);
    }

    public static void setUIFont(Font f) {
        java.util.Enumeration keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource) {
                UIManager.put(key, new javax.swing.plaf.FontUIResource(f));
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }


            Font thaiFont = new Font("Tahoma", Font.PLAIN, 14);


            java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);

                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, new javax.swing.plaf.FontUIResource(thaiFont));
                }
            }


            UIManager.put("OptionPane.messageFont", thaiFont);
            UIManager.put("OptionPane.buttonFont", thaiFont);
            UIManager.put("OptionPane.okButtonText", "ตกลง");
            UIManager.put("OptionPane.yesButtonText", "ใช่");
            UIManager.put("OptionPane.noButtonText", "ไม่");
            UIManager.put("OptionPane.cancelButtonText", "ยกเลิก");


            UIManager.put("FileChooser.lookInLabelText", "มองหาใน:");
            UIManager.put("FileChooser.saveInLabelText", "บันทึกใน:");
            UIManager.put("FileChooser.openDialogTitleText", "เปิดไฟล์");
            UIManager.put("FileChooser.saveDialogTitleText", "บันทึกไฟล์");
            UIManager.put("FileChooser.fileNameLabelText", "ชื่อไฟล์:");
            UIManager.put("FileChooser.filesOfTypeLabelText", "ชนิดของไฟล์:");
            UIManager.put("FileChooser.upFolderToolTipText", "ขึ้นไป 1 ระดับ");
            UIManager.put("FileChooser.newFolderToolTipText", "สร้างโฟลเดอร์ใหม่");
            UIManager.put("FileChooser.listViewButtonToolTipText", "รายการ");
            UIManager.put("FileChooser.detailsViewButtonToolTipText", "รายละเอียด");
            new LoginFrame().setVisible(true);
        });
    }
}