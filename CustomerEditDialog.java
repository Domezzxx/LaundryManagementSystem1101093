
import javax.swing.*;
import java.awt.*;


public class CustomerEditDialog extends JDialog {

    private Customer customer;
    private CustomerDAO customerDAO;

    private JTextField idField;
    private JTextField nameField;
    private JTextField usernameField;
    private JTextField phoneField;
    private JPasswordField passwordField;
    private JTextField emailField;

    private JButton saveButton;
    private JButton deleteButton;
    private JButton cancelButton;

    public CustomerEditDialog(JFrame parent, Customer customer, CustomerDAO customerDAO) {
        super(parent, "แก้ไขข้อมูลลูกค้า", true);
        this.customer = customer;
        this.customerDAO = customerDAO;

        initComponents();
        loadCustomerData();
    }

    private void initComponents() {
        setSize(500, 450);
        setLocationRelativeTo(getParent());
        setResizable(false);


        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }


    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Tahoma", Font.BOLD, 14);
        Font fieldFont = new Font("Tahoma", Font.PLAIN, 14);


        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = new JLabel("รหัสลูกค้า:");
        idLabel.setFont(labelFont);
        panel.add(idLabel, gbc);

        gbc.gridx = 1;
        idField = new JTextField(20);
        idField.setFont(fieldFont);
        idField.setEditable(false);
        idField.setBackground(new Color(240, 240, 240));
        panel.add(idField, gbc);

        // Customer Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel nameLabel = new JLabel("ชื่อ-นามสกุล:");
        nameLabel.setFont(labelFont);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        nameField.setFont(fieldFont);
        panel.add(nameField, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel usernameLabel = new JLabel("ชื่อบัญชี:");
        usernameLabel.setFont(labelFont);
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        usernameField = new JTextField(20);
        usernameField.setFont(fieldFont);
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel passwordLabel = new JLabel("รหัสผ่าน:");
        passwordLabel.setFont(labelFont);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        panel.add(passwordField, gbc);

        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel phoneLabel = new JLabel("เบอร์โทรศัพท์:");
        phoneLabel.setFont(labelFont);
        panel.add(phoneLabel, gbc);

        gbc.gridx = 1;
        phoneField = new JTextField(20);
        phoneField.setFont(fieldFont);
        panel.add(phoneField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(labelFont);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        panel.add(emailField, gbc);

        return panel;
    }


    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        // Save Button
        saveButton = new JButton("บันทึก");
        saveButton.setBackground(new Color(76, 175, 80));
        saveButton.setForeground(Color.BLACK);
        saveButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setFocusPainted(false);
        saveButton.addActionListener(e -> saveCustomer());

        // Delete Button
        deleteButton = new JButton("ลบ");
        deleteButton.setBackground(new Color(244, 67, 54));
        deleteButton.setForeground(Color.BLACK);
        deleteButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> deleteCustomer());

        // Cancel Button
        cancelButton = new JButton("ยกเลิก");
        cancelButton.setBackground(new Color(158, 158, 158));
        cancelButton.setForeground(Color.BLACK);
        cancelButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(deleteButton);
        panel.add(cancelButton);

        return panel;
    }


    private void loadCustomerData() {
        idField.setText(customer.getCustomerId());
        nameField.setText(customer.getCustomerName());
        usernameField.setText(customer.getUsername());
        passwordField.setText(customer.getPassword());
        phoneField.setText(customer.getPhone());
        emailField.setText(customer.getEmail());
    }

    private void saveCustomer() {
        // ตรวจสอบข้อมูล
        if (!validateInput()) {
            return;
        }

        // อัปเดตข้อมูล
        customer.setCustomerName(nameField.getText().trim());
        customer.setUsername(usernameField.getText().trim());
        customer.setPassword(new String(passwordField.getPassword()).trim());
        customer.setPhone(phoneField.getText().trim());
        customer.setEmail(emailField.getText().trim());

        // บันทึกลงฐานข้อมูล
        boolean success = customerDAO.updateCustomer(customer);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "บันทึกข้อมูลสำเร็จ",
                    "สำเร็จ",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "บันทึกข้อมูลไม่สำเร็จ",
                    "ผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void deleteCustomer() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "คุณต้องการลบลูกค้า " + customer.getCustomerName() + " ใช่หรือไม่?",
                "ยืนยันการลบ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = customerDAO.deleteCustomer(customer.getCustomerId());

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "ลบข้อมูลสำเร็จ",
                        "สำเร็จ",
                        JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "ลบข้อมูลไม่สำเร็จ",
                        "ผิดพลาด",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private boolean validateInput() {
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "กรุณากรอกชื่อ-นามสกุล",
                    "ข้อมูลไม่ครบ",
                    JOptionPane.WARNING_MESSAGE);
            nameField.requestFocus();
            return false;
        }

        if (usernameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "กรุณากรอกชื่อบัญชี",
                    "ข้อมูลไม่ครบ",
                    JOptionPane.WARNING_MESSAGE);
            usernameField.requestFocus();
            return false;
        }

        if (new String(passwordField.getPassword()).trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "กรุณากรอกรหัสผ่าน",
                    "ข้อมูลไม่ครบ",
                    JOptionPane.WARNING_MESSAGE);
            passwordField.requestFocus();
            return false;
        }

        if (emailField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "กรุณากรอกอีเมล",
                    "ข้อมูลไม่ครบ",
                    JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        // ตรวจสอบรูปแบบอีเมล
        String email = emailField.getText().trim();
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            JOptionPane.showMessageDialog(this,
                    "รูปแบบอีเมลไม่ถูกต้อง",
                    "ข้อมูลไม่ถูกต้อง",
                    JOptionPane.WARNING_MESSAGE);
            emailField.requestFocus();
            return false;
        }

        return true;
    }
}