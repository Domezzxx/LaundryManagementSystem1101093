import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StaffManagementFrame extends JFrame {
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel lblTotalCount;
    private EmployeeDAO employeeDAO;

    public StaffManagementFrame() {
        this.employeeDAO = new EmployeeDAO();
        initComponents();
        loadEmployeeData();
    }

    private void initComponents() {
        setTitle("จัดการข้อมูลพนักงาน - ปลายฟ้า LAUNDRY");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        JPanel sidebar = createSidebar();


        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));


        JPanel headerPanel = createHeaderPanel();


        JPanel searchPanel = createSearchPanel();


        JPanel tablePanel = createTablePanel();


        JPanel footerPanel = createFooterPanel();

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(searchPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0, 204, 204));
        sidebar.setPreferredSize(new Dimension(110, 700));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel logo = new JLabel(createLogoIcon());
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(logo);
        sidebar.add(Box.createRigidArea(new Dimension(0, 30)));

        String[] icons = {"🏠", "📦", "👥", "👷", "📊","⚙️"};
        String[] tooltips = {"หน้าหลัก", "บริการ", "ลูกค้า", "พนักงาน", "แดชบอร์ด","จัดการเครื่อง"};

        for (int i = 0; i < icons.length; i++) {
            final int index = i;
            JButton btn = new JButton(icons[i]);
            btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
            btn.setBackground(new Color(0, 204, 204));
            btn.setForeground(Color.WHITE);
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true); // เพิ่มเพื่อให้สีพื้นหลังแสดงผลถูกต้อง
            btn.setContentAreaFilled(false); // ป้องกันปุ่มเปลี่ยนสีเทาเวลาคลิก
            btn.setMaximumSize(new Dimension(80, 60));
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.setToolTipText(tooltips[i]);

            btn.addActionListener(e -> {
                JFrame nextFrame = null;
                switch (index) {
                    case 0: nextFrame = new ServiceManagementFrame(); break;
                    case 1: nextFrame = new ProductManagement(); break;
                    case 2: nextFrame = new CustomerManagementFrame(); break;
                    case 3: nextFrame = new StaffManagementFrame(); break;
                    case 4: nextFrame = new LaundryDashboard(); break;
                    case 5: nextFrame = new WashingManagement(); break;
                    case 6:
                        return;
                }

                if (nextFrame != null) {
                    nextFrame.setVisible(true);
                    this.dispose();
                }
            });

            sidebar.add(btn);
            sidebar.add(Box.createRigidArea(new Dimension(0, 15)));
        }
        sidebar.add(Box.createVerticalGlue());


        JButton logoutBtn = new JButton("🚪");
        logoutBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        logoutBtn.setBackground(new Color(0, 204, 204)); // ✅ ใช้สีเดียวกับ Sidebar
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorderPainted(false);
        logoutBtn.setOpaque(true);
        logoutBtn.setContentAreaFilled(false); // ✅ ไม่แสดงพื้นหลัง
        logoutBtn.setMaximumSize(new Dimension(80, 60));
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutBtn.setToolTipText("ออกจากระบบ");

        logoutBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "คุณต้องการออกจากระบบหรือไม่?",
                    "ยืนยันการออกจากระบบ",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {

                this.dispose();


                SwingUtilities.invokeLater(() -> {
                    new LoginFrame().setVisible(true);
                });
            }
        });

        sidebar.add(logoutBtn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 20)));

        return sidebar;
    }


    private JButton createSidebarButton(String icon) {
        JButton btn = new JButton(icon);
        btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        btn.setBackground(new Color(0, 204, 204));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(80, 60));
        btn.setMaximumSize(new Dimension(80, 60));
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 180, 180));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(0, 204, 204));
            }
        });

        return btn;
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("จัดการข้อมูลพนักงาน");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(new Color(0, 204, 204));

        lblTotalCount = new JLabel("พนักงานทั้งหมด: 0 คน");
        lblTotalCount.setFont(new Font("Tahoma", Font.PLAIN, 14));
        lblTotalCount.setForeground(new Color(100, 100, 100));

        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(lblTotalCount, BorderLayout.EAST);

        return panel;
    }
    private JComboBox<String> sortComboBox;
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(400, 35));
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterEmployees(searchField.getText());
            }
        });

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));


        String[] sortOptions = {"หมายเลข", "ตำแหน่ง (สูง-ต่ำ)"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
        sortComboBox.addActionListener(e -> filterEmployees(searchField.getText()));

        panel.add(searchIcon);
        panel.add(searchField);
        panel.add(Box.createRigidArea(new Dimension(10, 0)));
        panel.add(new JLabel("เรียง: "));
        panel.add(sortComboBox);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        String[] columns = {"หมายเลข", "ชื่อ", "ชื่อผู้ใช้", "รหัสผ่าน", "ตำแหน่ง", ""};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };

        employeeTable = new JTable(tableModel);
        employeeTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
        employeeTable.setRowHeight(40);


        employeeTable.getTableHeader().setFont(new Font("Tahoma", Font.BOLD, 14));
        employeeTable.getTableHeader().setReorderingAllowed(false);
        employeeTable.getTableHeader().setPreferredSize(new Dimension(0, 40));
        employeeTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setBackground(new Color(0, 204, 204));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Tahoma", Font.BOLD, 14));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true);

                return label;
            }
        });


        employeeTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        employeeTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        employeeTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        employeeTable.getColumnModel().getColumn(4).setPreferredWidth(150);
        employeeTable.getColumnModel().getColumn(5).setPreferredWidth(130);


        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 5; i++) {
            employeeTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        employeeTable.getColumnModel().getColumn(5).setCellRenderer(new IconButtonRenderer());
        employeeTable.getColumnModel().getColumn(5).setCellEditor(new IconButtonEditor(new JCheckBox()));

        JScrollPane scrollPane = new JScrollPane(employeeTable);


        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);

        JButton btnAdd = new JButton("เพิ่มพนักงาน");
        btnAdd.setFont(new Font("Tahoma", Font.BOLD, 14));
        styleButton(btnAdd, new Color(0, 204, 204), Color.BLACK);
        btnAdd.addActionListener(e -> openAddEmployeeDialog());

        panel.add(btnAdd);
        return panel;
    }

    private void loadEmployeeData() {
        tableModel.setRowCount(0);
        List<Employee> employees = employeeDAO.getAllEmployees();

        for (Employee emp : employees) {

            tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getName(),
                    emp.getUsername(),
                    "********",
                    emp.getRole(),
                    "แก้ไข/รายละเอียด"
            });
        }
        lblTotalCount.setText("พนักงานทั้งหมด: " + employees.size() + " คน");
    }
    private void filterEmployees(String searchText) {
        tableModel.setRowCount(0);
        List<Employee> employees;

        String query = searchText.trim();

        if (query.isEmpty()) {
            employees = employeeDAO.getAllEmployees();
        } else if (query.matches("\\d+")) {
            Employee emp = employeeDAO.getEmployeeById(query);
            employees = new java.util.ArrayList<>();
            if (emp != null) employees.add(emp);
        } else {
            employees = employeeDAO.searchEmployees(query);
        }

        if (sortComboBox.getSelectedIndex() == 1) {
            employees.sort((e1, e2) -> Integer.compare(getRolePriority(e2.getRole()), getRolePriority(e1.getRole())));
        } else {
            employees.sort((e1, e2) -> e1.getEmployeeId().compareTo(e2.getEmployeeId()));
        }

        for (Employee emp : employees) {

            tableModel.addRow(new Object[]{
                    emp.getEmployeeId(),
                    emp.getName(),
                    emp.getUsername(),
                    "********",
                    emp.getRole(),
                    "แก้ไข/รายละเอียด"
            });
        }
        lblTotalCount.setText("พนักงานทั้งหมด: " + employees.size() + " คน");
    }

    private int getRolePriority(String role) {
        switch (role) {
            case "ผู้จัดการ": return 5;
            case "หัวหน้างาน": return 4;
            case "พนักงานแพ็ค": return 3;
            case "พนักงานรีด": return 2;
            case "พนักงานซักผ้า": return 1;
            default: return 0;
        }
    }
    private void openAddEmployeeDialog() {
        JDialog dialog = new JDialog(this, "เพิ่มพนักงานใหม่", true);
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField txtName = new JTextField(20);
        JTextField txtUsername = new JTextField(20);
        JPasswordField txtPassword = new JPasswordField(20);
        String[] roles = {"พนักงานซักผ้า", "พนักงานรีด", "พนักงานแพ็ค", "หัวหน้างาน", "ผู้จัดการ"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);

        addFormField(panel, gbc, 0, "ชื่อ:", txtName);
        addFormField(panel, gbc, 1, "ชื่อผู้ใช้:", txtUsername);
        addFormField(panel, gbc, 2, "รหัสผ่าน:", txtPassword);
        addFormField(panel, gbc, 3, "หน้าที่:", cmbRole);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("บันทึก");
        JButton btnCancel = new JButton("ยกเลิก");

        styleButton(btnSave, new Color(50, 205, 50), Color.BLACK);
        styleButton(btnCancel, new Color(220, 220, 220), Color.BLACK);

        btnSave.addActionListener(e -> {
            if (validateEmployeeInput(txtName, txtUsername, txtPassword)) {
                Employee newEmp = new Employee();
                newEmp.setName(txtName.getText().trim());
                newEmp.setUsername(txtUsername.getText().trim());
                newEmp.setPassword(new String(txtPassword.getPassword()));
                newEmp.setRole((String) cmbRole.getSelectedItem());

                if (employeeDAO.addEmployee(newEmp)) {
                    JOptionPane.showMessageDialog(dialog, "เพิ่มพนักงานเรียบร้อยแล้ว", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadEmployeeData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "เกิดข้อผิดพลาดในการเพิ่มพนักงาน", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openEditEmployeeDialog(Employee employee) {
        JDialog dialog = new JDialog(this, "แก้ไขข้อมูลพนักงาน", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        JTextField txtId = new JTextField(employee.getEmployeeId());
        txtId.setEditable(false);
        txtId.setBackground(new Color(240, 240, 240));

        JTextField txtName = new JTextField(employee.getName(), 20);
        JTextField txtUsername = new JTextField(employee.getUsername(), 20);
        JPasswordField txtPassword = new JPasswordField(employee.getPassword(), 20);
        String[] roles = {"พนักงานซักผ้า", "พนักงานรีด", "พนักงานแพ็ค", "หัวหน้างาน", "ผู้จัดการ"};
        JComboBox<String> cmbRole = new JComboBox<>(roles);
        cmbRole.setSelectedItem(employee.getRole());

        addFormField(panel, gbc, 0, "หมายเลข:", txtId);
        addFormField(panel, gbc, 1, "ชื่อ:", txtName);
        addFormField(panel, gbc, 2, "ชื่อผู้ใช้:", txtUsername);
        addFormField(panel, gbc, 3, "รหัสผ่าน:", txtPassword);
        addFormField(panel, gbc, 4, "หน้าที่:", cmbRole);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnSave = new JButton("บันทึก");
        JButton btnDelete = new JButton("ลบ");
        JButton btnCancel = new JButton("ยกเลิก");

        styleButton(btnSave, new Color(50, 205, 50), Color.BLACK);
        styleButton(btnDelete, new Color(255, 107, 107), Color.BLACK);
        styleButton(btnCancel, new Color(220, 220, 220), Color.BLACK);

        btnSave.addActionListener(e -> {
            if (validateEmployeeInput(txtName, txtUsername, txtPassword)) {
                employee.setName(txtName.getText().trim());
                employee.setUsername(txtUsername.getText().trim());
                employee.setPassword(new String(txtPassword.getPassword()));
                employee.setRole((String) cmbRole.getSelectedItem());

                if (employeeDAO.updateEmployee(employee)) {
                    JOptionPane.showMessageDialog(dialog, "แก้ไขข้อมูลเรียบร้อยแล้ว", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadEmployeeData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "เกิดข้อผิดพลาดในการแก้ไขข้อมูล", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnDelete.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "ต้องการลบพนักงานคนนี้หรือไม่?", "ยืนยันการลบ", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                if (employeeDAO.deleteEmployee(employee.getEmployeeId())) {
                    JOptionPane.showMessageDialog(dialog, "ลบข้อมูลเรียบร้อยแล้ว", "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadEmployeeData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "เกิดข้อผิดพลาดในการลบข้อมูล", "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());
        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnCancel);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        field.setFont(new Font("Tahoma", Font.PLAIN, 13));
        panel.add(field, gbc);
    }

    private boolean validateEmployeeInput(JTextField txtName, JTextField txtUsername, JPasswordField txtPassword) {
        if (txtName.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อ", "ข้อมูลไม่ครบ", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtUsername.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "กรุณากรอกชื่อผู้ใช้", "ข้อมูลไม่ครบ", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtPassword.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "กรุณากรอกรหัสผ่าน", "ข้อมูลไม่ครบ", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Tahoma", Font.PLAIN, 12));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
    }

    private ImageIcon createLogoIcon() {
        int size = 60;
        Image img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 180, 220));
        g2.fillOval(0, 0, size, size);
        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(2, 2, size - 4, size - 4);
        g2.dispose();
        return new ImageIcon(img);
    }

    class IconButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public IconButtonRenderer() {
            setOpaque(true);
            setFont(new Font("Tahoma", Font.BOLD, 12));
            setFocusPainted(false);

            setBorderPainted(false);
            setBackground(new Color(0, 123, 255));
            setForeground(Color.WHITE); // ตัวอักษรสีขาว
            setBorder(null);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value != null ? value.toString() : "แก้ไข");
            return this;
        }
    }

    class IconButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;

        public IconButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);


            button.setFont(new Font("Tahoma", Font.BOLD, 12));
            button.setFocusPainted(false);


            button.setBorderPainted(false);
            button.setBackground(new Color(0, 123, 255));
            button.setForeground(Color.WHITE);
            button.setBorder(null); // นำขอบออก


            button.addActionListener(e -> {
                fireEditingStopped();

                int viewRow = employeeTable.getSelectedRow();
                if (viewRow != -1) {

                    Object value = tableModel.getValueAt(viewRow, 0);

                    if (value != null) {
                        String employeeId = value.toString();


                        Employee emp = employeeDAO.getEmployeeById(employeeId);

                        if (emp != null) {
                            openEditEmployeeDialog(emp); // เปิดหน้าต่างแก้ไข
                        } else {
                            JOptionPane.showMessageDialog(null, "ไม่พบข้อมูลพนักงานรหัส: " + employeeId);
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            return label;
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
            new StaffManagementFrame().setVisible(true);
        });
    }
}