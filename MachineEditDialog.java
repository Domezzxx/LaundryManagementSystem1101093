import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class MachineEditDialog extends JDialog {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    private JTextField txtMachineId;
    private JComboBox<String> cmbMachineType;
    private JComboBox<String> cmbMachineStatus;
    private JButton btnSave, btnDelete, btnCancel;

    private String machineId;
    private boolean isEditMode;
    private WashingManagement parentFrame;

    // Constructor for Add Mode
    public MachineEditDialog(WashingManagement parent) {
        this(parent, null, null, null);
        this.isEditMode = false;
        setTitle("เพิ่มข้อมูลเครื่องซักผ้า");
    }


    public MachineEditDialog(WashingManagement parent, String machineId, String machineType, String machineStatus) {
        super(parent, "แก้ไขข้อมูลเครื่องซักผ้า", true);
        this.parentFrame = parent;
        this.machineId = machineId;
        this.isEditMode = (machineId != null);

        initComponents();

        if (isEditMode) {
            loadMachineData(machineId, machineType, machineStatus);
            txtMachineId.setEnabled(false);
            btnDelete.setVisible(true);
        } else {
            btnDelete.setVisible(false);
        }

        setSize(600, 450);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initComponents() {
        setLayout(new BorderLayout(20, 20));
        getContentPane().setBackground(Color.WHITE);


        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(0, 204, 204));
        headerPanel.setPreferredSize(new Dimension(600, 80));
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 30, 20));

        JLabel iconLabel = new JLabel(createMachineIcon());
        JLabel titleLabel = new JLabel(isEditMode ? "แก้ไขข้อมูลเครื่อง" : "เพิ่มเครื่องใหม่");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(iconLabel);
        headerPanel.add(titleLabel);


        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);


        JLabel lblMachineId = new JLabel("หมายเลขเครื่อง:");
        lblMachineId.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(lblMachineId, gbc);

        txtMachineId = new JTextField();
        txtMachineId.setFont(new Font("Tahoma", Font.PLAIN, 14));
        txtMachineId.setPreferredSize(new Dimension(250, 35));
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(txtMachineId, gbc);


        JLabel lblMachineType = new JLabel("ประเภท:");
        lblMachineType.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(lblMachineType, gbc);

        String[] types = {"เครื่องซักผ้า", "เครื่องอบผ้า", "เตารีด"};
        cmbMachineType = new JComboBox<>(types);
        cmbMachineType.setFont(new Font("Tahoma", Font.PLAIN, 14));
        cmbMachineType.setPreferredSize(new Dimension(250, 35));
        cmbMachineType.setBackground(Color.WHITE);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(cmbMachineType, gbc);


        JLabel lblMachineStatus = new JLabel("สถานะของเครื่อง:");
        lblMachineStatus.setFont(new Font("Tahoma", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        formPanel.add(lblMachineStatus, gbc);

        String[] statuses = {"พร้อมใช้งาน", "กำลังทำงาน", "ไม่พร้อมใช้งาน"};
        cmbMachineStatus = new JComboBox<>(statuses);
        cmbMachineStatus.setFont(new Font("Tahoma", Font.PLAIN, 14));
        cmbMachineStatus.setPreferredSize(new Dimension(250, 35));
        cmbMachineStatus.setBackground(Color.WHITE);


        cmbMachineStatus.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);

                if (!isSelected && value != null) {
                    String status = value.toString();
                    switch (status) {
                        case "พร้อมใช้งาน":
                            label.setBackground(new Color(76, 175, 80));
                            label.setForeground(Color.WHITE);
                            break;
                        case "เสร็จสิ้น":
                            label.setBackground(new Color(33, 150, 243));
                            label.setForeground(Color.WHITE);
                            break;
                        case "กำลังทำงาน":
                            label.setBackground(new Color(255, 193, 7));
                            label.setForeground(Color.BLACK);
                            break;
                        case "ไม่ว่าง":
                            label.setBackground(new Color(255, 152, 0));
                            label.setForeground(Color.WHITE);
                            break;
                        case "ไม่พร้อมใช้งาน":
                            label.setBackground(new Color(244, 67, 54));
                            label.setForeground(Color.WHITE);
                            break;
                    }
                    label.setOpaque(true);
                }
                return label;
            }
        });

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        formPanel.add(cmbMachineStatus, gbc);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 20));
        buttonPanel.setBackground(Color.WHITE);

        btnSave = createStyledButton("บันทึก", new Color(40, 167, 69), Color.WHITE);
        btnSave.addActionListener(e -> saveMachine());

        btnCancel = createStyledButton("ยกเลิก", new Color(108, 117, 125), Color.WHITE);
        btnCancel.addActionListener(e -> dispose());

        btnDelete = createStyledButton("ลบ", new Color(220, 53, 69), Color.WHITE);
        btnDelete.addActionListener(e -> deleteMachine());

        buttonPanel.add(btnSave);
        if (isEditMode) {
            buttonPanel.add(btnDelete);
        }
        buttonPanel.add(btnCancel);
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMachineData(String id, String type, String status) {
        txtMachineId.setText(id);
        cmbMachineType.setSelectedItem(type);


        String displayStatus = mapDatabaseStatusToDisplay(status);
        cmbMachineStatus.setSelectedItem(displayStatus);
    }

    private String mapDatabaseStatusToDisplay(String dbStatus) {
        if (dbStatus == null) return "พร้อมใช้งาน";

        switch (dbStatus.toLowerCase()) {
            case "active":
                return "พร้อมใช้งาน";
            case "maintenance":
                return "กำลังทำงาน";
            case "inactive":
                return "ไม่พร้อมใช้งาน";
            default:
                return dbStatus;
        }
    }

    private String mapDisplayStatusToDatabase(String displayStatus) {
        switch (displayStatus) {
            case "พร้อมใช้งาน":
                return "active";
            case "กำลังทำงาน":
            case "ไม่ว่าง":
                return "maintenance";
            case "ไม่พร้อมใช้งาน":
            case "เสร็จสิ้น":
                return "inactive";
            default:
                return displayStatus;
        }
    }

    private void saveMachine() {
        String id = txtMachineId.getText().trim();
        String type = (String) cmbMachineType.getSelectedItem();
        String displayStatus = (String) cmbMachineStatus.getSelectedItem();
        String dbStatus = mapDisplayStatusToDatabase(displayStatus);

        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "กรุณากรอกหมายเลขเครื่อง",
                    "ข้อมูลไม่ครบ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql;

            if (isEditMode) {
                // Update existing machine
                sql = "UPDATE washing_machines SET machine_type = ?, machine_status = ? WHERE machine_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, type);
                stmt.setString(2, dbStatus);
                stmt.setString(3, id);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "แก้ไขข้อมูลสำเร็จ",
                            "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.loadTableData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "ไม่พบข้อมูลเครื่อง",
                            "ผิดพลาด", JOptionPane.ERROR_MESSAGE);
                }
            } else {

                sql = "INSERT INTO washing_machines (machine_id, machine_type, machine_status, created_at) VALUES (?, ?, ?, NOW())";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, id);
                stmt.setString(2, type);
                stmt.setString(3, dbStatus);

                stmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "เพิ่มเครื่องใหม่สำเร็จ",
                        "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                parentFrame.loadTableData();
                dispose();
            }

        } catch (SQLIntegrityConstraintViolationException e) {
            JOptionPane.showMessageDialog(this, "หมายเลขเครื่องนี้มีอยู่แล้ว",
                    "ข้อมูลซ้ำ", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMachine() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "คุณต้องการลบเครื่อง " + machineId + " หรือไม่?",
                "ยืนยันการลบ",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
                String sql = "DELETE FROM washing_machines WHERE machine_id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, machineId);

                int rowsDeleted = stmt.executeUpdate();

                if (rowsDeleted > 0) {
                    JOptionPane.showMessageDialog(this, "ลบข้อมูลสำเร็จ",
                            "สำเร็จ", JOptionPane.INFORMATION_MESSAGE);
                    parentFrame.loadTableData();
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "ไม่พบข้อมูลเครื่อง",
                            "ผิดพลาด", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + e.getMessage(),
                        "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private JButton createStyledButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Tahoma", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(120, 40));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });

        return button;
    }

    private ImageIcon createMachineIcon() {
        int size = 50;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(
                size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(5, 5, 40, 40, 8, 8);
        g2.setColor(new Color(0, 150, 150));
        g2.setStroke(new BasicStroke(2));
        g2.drawRoundRect(5, 5, 40, 40, 8, 8);

        // Draw door
        g2.setColor(new Color(100, 200, 250));
        g2.fillOval(13, 13, 24, 24);
        g2.setColor(new Color(0, 150, 150));
        g2.drawOval(13, 13, 24, 24);

        g2.dispose();
        return new ImageIcon(img);
    }

}
