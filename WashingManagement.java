import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WashingManagement extends JFrame {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private List<Machine> machines;
    private JPanel contentPanel;
    private JPanel summaryPanel;
    private JTable machineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel lblActiveCount, lblMaintenanceCount, lblInactiveCount;
    private JComboBox<String> filterComboBox;

    public WashingManagement() {
        setTitle("สถานะการทำงาน");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(0, 0));


        loadMachinesFromDatabase();


        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);


        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout(20, 20));
        mainContent.setBackground(Color.WHITE);
        mainContent.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));


        JLabel titleLabel = new JLabel("สถานะการทำงาน", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 28));
        mainContent.add(titleLabel, BorderLayout.NORTH);


        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(0, 20));
        contentPanel.setBackground(Color.WHITE);


        JPanel searchPanel = createSearchPanel();
        contentPanel.add(searchPanel, BorderLayout.NORTH);


        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Color.WHITE);


        summaryPanel = createSummaryPanel();
        centerPanel.add(summaryPanel, BorderLayout.NORTH);


        JPanel tablePanel = createTablePanel();
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        contentPanel.add(centerPanel, BorderLayout.CENTER);


        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        mainContent.add(scrollPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);


        loadTableData();
    }

    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(Color.WHITE);

        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                filterMachines();
            }
        });

        panel.add(searchIcon);
        panel.add(searchField);


        JLabel lblFilter = new JLabel("เรียง:");
        lblFilter.setFont(new Font("Tahoma", Font.BOLD, 14));
        lblFilter.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 5));
        panel.add(lblFilter);

        String[] filterOptions = {"ทั้งหมด", "พร้อมใช้งาน", "กำลังทำงาน", "ไม่พร้อมใช้งาน"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
        filterComboBox.setBackground(Color.WHITE);
        filterComboBox.setPreferredSize(new Dimension(160, 35));

        filterComboBox.addActionListener(e -> {
            filterMachines();
        });

        panel.add(filterComboBox);


        JButton btnAddMachine = new JButton("+ เพิ่มเครื่อง");
        btnAddMachine.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnAddMachine.setBackground(new Color(40, 167, 69));
        btnAddMachine.setForeground(Color.WHITE);
        btnAddMachine.setFocusPainted(false);
        btnAddMachine.setBorderPainted(false);
        btnAddMachine.setPreferredSize(new Dimension(140, 35));
        btnAddMachine.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAddMachine.setOpaque(true);

        btnAddMachine.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnAddMachine.setBackground(new Color(33, 136, 56));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnAddMachine.setBackground(new Color(40, 167, 69));
            }
        });

        btnAddMachine.addActionListener(e -> openAddMachineDialog());

        panel.add(btnAddMachine);

        return panel;
    }

    private void openAddMachineDialog() {
        MachineEditDialog dialog = new MachineEditDialog(this);
        dialog.setVisible(true);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columns = {"หมายเลข", "ประเภท", "สถานะ", ""};

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // แก้ไขได้เฉพาะปุ่ม
            }
        };

        machineTable = new JTable(tableModel);
        machineTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
        machineTable.setRowHeight(45);
        machineTable.setGridColor(new Color(0, 230, 230));

        JTableHeader header = machineTable.getTableHeader();
        header.setFont(new Font("Tahoma", Font.BOLD, 14));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(0, 40));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                label.setBackground(new Color(0, 204, 204));
                label.setForeground(Color.WHITE);
                label.setFont(new Font("Tahoma", Font.BOLD, 14));
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setOpaque(true);
                return label;
            }
        });

        machineTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        machineTable.getColumnModel().getColumn(1).setPreferredWidth(250);
        machineTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        machineTable.getColumnModel().getColumn(3).setPreferredWidth(150);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < machineTable.getColumnCount(); i++) {
            if (i != 2 && i != 3) {
                machineTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }


        machineTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setOpaque(true);
                label.setHorizontalAlignment(CENTER);
                label.setFont(new Font("Tahoma", Font.BOLD, 13));

                String status = (String) value;
                String displayStatus = status;
                Color bgColor = Color.WHITE;
                Color fgColor = Color.BLACK;


                String statusLower = status.toLowerCase();
                if (statusLower.equals("active") || statusLower.equals("พร้อม") || statusLower.equals("พร้อมใช้งาน")) {
                    displayStatus = "พร้อม";
                    bgColor = new Color(76, 175, 80);
                    fgColor = Color.WHITE;
                } else if (statusLower.equals("maintenance") || statusLower.equals("กำลังทำงาน") || statusLower.equals("ไม่ว่าง")) {
                    displayStatus = "กำลังทำงาน";
                    bgColor = new Color(255, 152, 0);
                    fgColor = Color.WHITE;
                } else if (statusLower.equals("inactive") || statusLower.equals("ไม่พร้อม") || statusLower.equals("ไม่พร้อมใช้งาน")) {
                    displayStatus = "ไม่พร้อม";
                    bgColor = new Color(244, 67, 54);
                    fgColor = Color.WHITE;
                }

                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                } else {
                    label.setBackground(bgColor);
                    label.setForeground(fgColor);
                }

                label.setText(displayStatus);
                return label;
            }
        });

        machineTable.getColumnModel().getColumn(3).setCellRenderer(new ButtonRenderer());
        machineTable.getColumnModel().getColumn(3).setCellEditor(new ButtonEditor(new JCheckBox()));

        machineTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = machineTable.getSelectedRow();
                    if (row != -1) {
                        String machineId = (String) tableModel.getValueAt(row, 0);
                        openMachineEditDialog(machineId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(machineTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }


    public void loadTableData() {
        loadMachinesFromDatabase();
        refreshTableDisplay();
    }

    private void refreshTableDisplay() {
        tableModel.setRowCount(0);
        for (Machine machine : machines) {
            // แปลงสถานะเป็นภาษาไทยก่อนแสดง
            String displayStatus = convertStatusToThai(machine.status);

            tableModel.addRow(new Object[]{
                    machine.id,
                    machine.type,
                    displayStatus,
                    "แก้ไข/รายละเอียด"
            });
        }
        updateStatusCounts();
    }

    private String convertStatusToThai(String status) {
        String statusLower = status.toLowerCase();
        if (statusLower.equals("active")) {
            return "พร้อม";
        } else if (statusLower.equals("maintenance")) {
            return "กำลังทำงาน";
        } else if (statusLower.equals("inactive")) {
            return "ไม่พร้อม";
        }
        return status; // คืนค่าเดิมถ้าเป็นภาษาไทยอยู่แล้ว
    }

    private void filterMachines() {
        String searchText = searchField.getText().trim().toLowerCase();
        String selectedFilter = (String) filterComboBox.getSelectedItem();
        tableModel.setRowCount(0);

        for (Machine machine : machines) {

            String displayStatus = convertStatusToThai(machine.status);


            boolean matchSearch = false;

            if (searchText.isEmpty()) {
                matchSearch = true;
            } else {

                if (machine.id.toLowerCase().equals(searchText)) {
                    matchSearch = true;
                }

                else if (machine.type.toLowerCase().equals(searchText)) {
                    matchSearch = true;
                }

                else if (displayStatus.toLowerCase().equals(searchText) ||
                        machine.status.toLowerCase().equals(searchText)) {
                    matchSearch = true;
                }
            }


            boolean matchFilter = true;
            if (!selectedFilter.equals("ทั้งหมด")) {
                String status = machine.status.toLowerCase();
                matchFilter = false;

                switch (selectedFilter) {
                    case "พร้อมใช้งาน":
                        if (status.equals("active") || status.equals("พร้อม") || status.equals("พร้อมใช้งาน")) {
                            matchFilter = true;
                        }
                        break;
                    case "กำลังทำงาน":
                        if (status.equals("maintenance") || status.equals("กำลังทำงาน") || status.equals("ไม่ว่าง")) {
                            matchFilter = true;
                        }
                        break;
                    case "ไม่พร้อมใช้งาน":
                        if (status.equals("inactive") || status.equals("ไม่พร้อม") ||
                                status.equals("ไม่พร้อมใช้งาน") || status.equals("เสร็จสิ้น")) {
                            matchFilter = true;
                        }
                        break;
                }
            }


            if (matchSearch && matchFilter) {
                tableModel.addRow(new Object[]{
                        machine.id,
                        machine.type,
                        displayStatus,
                        "แก้ไข/รายละเอียด"
                });
            }
        }


        updateStatusCounts();
    }

    private void openMachineEditDialog(String machineId) {
        Machine machine = null;
        for (Machine m : machines) {
            if (m.id.equals(machineId)) {
                machine = m;
                break;
            }
        }

        if (machine != null) {
            MachineEditDialog dialog = new MachineEditDialog(this, machine.id, machine.type, machine.status);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                    "ไม่พบข้อมูลเครื่อง",
                    "ผิดพลาด",
                    JOptionPane.ERROR_MESSAGE);
        }
    }


    private void loadMachinesFromDatabase() {
        machines = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM washing_machines ORDER BY machine_id";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Machine machine = new Machine();
                machine.id = rs.getString("machine_id");
                machine.type = rs.getString("machine_type");
                machine.status = rs.getString("machine_status");
                machine.createdAt = rs.getTimestamp("created_at");
                machines.add(machine);
            }

            System.out.println("โหลดข้อมูลเครื่องซักผ้า " + machines.size() + " เครื่อง");

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "ไม่สามารถเชื่อมต่อฐานข้อมูลได้: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 90, 0));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(2000, 150));


        lblActiveCount = new JLabel("0");
        lblMaintenanceCount = new JLabel("0");
        lblInactiveCount = new JLabel("0");

        panel.add(createSummaryCard("พร้อมใช้งาน", lblActiveCount, new Color(76, 175, 80)));
        panel.add(createSummaryCard("กำลังทำงาน", lblMaintenanceCount, new Color(255, 152, 0)));
        panel.add(createSummaryCard("ไม่พร้อมใช้งาน", lblInactiveCount, new Color(244, 67, 54)));

        return panel;
    }

    private void updateStatusCounts() {
        int activeCount = 0;
        int maintenanceCount = 0;
        int inactiveCount = 0;

        for (Machine m : machines) {
            String status = m.status.toLowerCase();

            switch (status) {
                case "active":
                case "พร้อม":
                case "พร้อมใช้งาน":
                    activeCount++;
                    break;
                case "maintenance":
                case "กำลังทำงาน":
                case "ไม่ว่าง":
                    maintenanceCount++;
                    break;
                case "inactive":
                case "ไม่พร้อม":
                case "ไม่พร้อมใช้งาน":
                case "เสร็จสิ้น":
                    inactiveCount++;
                    break;
            }
        }

        lblActiveCount.setText(String.valueOf(activeCount));
        lblMaintenanceCount.setText(String.valueOf(maintenanceCount));
        lblInactiveCount.setText(String.valueOf(inactiveCount));
    }

    private JPanel createSummaryCard(String title, JLabel countLabel, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new RoundedBorder(15, new Color(220, 220, 220)));
        card.setPreferredSize(new Dimension(140, 120));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));

        countLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        countLabel.setForeground(color);
        countLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        countLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 20, 10));

        card.add(titleLabel);
        card.add(countLabel);

        return card;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0, 204, 204));
        sidebar.setPreferredSize(new Dimension(110, 800));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JLabel logo = new JLabel(createCircleIcon());
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
            btn.setOpaque(true);
            btn.setContentAreaFilled(false);
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
        logoutBtn.setBackground(new Color(0, 204, 204));
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

    private ImageIcon createCircleIcon() {
        int size = 60;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 80));
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return new ImageIcon(img);
    }

    class RoundedBorder implements Border {
        private int radius;
        private Color color;

        RoundedBorder(int radius, Color color) {
            this.radius = radius;
            this.color = color;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(2, 2, 2, 2);
        }

        public boolean isBorderOpaque() {
            return false;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
            g2.dispose();
        }
    }


    class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setBackground(new Color(0, 123, 255));
            setForeground(Color.WHITE);
            setFont(new Font("Tahoma", Font.PLAIN, 12));
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            setText((value == null) ? "จัดการ" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(new Color(0, 123, 255));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Tahoma", Font.PLAIN, 12));
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));

            button.addActionListener(e -> {
                fireEditingStopped();
                int selectedRow = machineTable.getSelectedRow();
                if (selectedRow != -1) {
                    String machineId = (String) tableModel.getValueAt(selectedRow, 0);
                    openMachineEditDialog(machineId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            label = (value == null) ? "จัดการ" : value.toString();
            button.setText(label);
            clicked = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            clicked = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            clicked = false;
            return super.stopCellEditing();
        }
    }


    class Machine {
        String id;
        String type;
        String status;
        Timestamp createdAt;
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

            WashingManagement frame = new WashingManagement();
            frame.setVisible(true);
        });
    }

}
