import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date; // ✅ เพิ่ม import Date
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OrderEditDialog extends JDialog {

    private Order order;
    private OrderManager orderManager;
    private OrderDataAccessObject orderDAO;
    private ServiceManagementFrame parentFrame;


    private JTextField txtOrderId;
    private JTextField txtCustomerName;
    private JTextField txtPhone;
    private JComboBox<String> cmbStatus;
    private JTextField txtPickupDate;
    private JTextField txtBookingTime;
    private JTextField txtTotalPrice;

    private JTable itemsTable;
    private DefaultTableModel tableModel;

    private JButton btnSave;
    private JButton btnCancel;
    private JButton btnDelete;

    private boolean isInitializing = false;
    private boolean isSystemUpdating = false;


    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");


    private final Font HEADER_FONT = new Font("Tahoma", Font.BOLD, 16);
    private final Font CONTENT_FONT = new Font("Tahoma", Font.PLAIN, 16);
    private final Font CONTENT_FONT_BOLD = new Font("Tahoma", Font.BOLD, 16);

    public OrderEditDialog(ServiceManagementFrame parent, Order order, OrderManager orderManager) {
        super(parent, "แก้ไขรายละเอียดคำสั่งซื้อ", true);
        this.parentFrame = parent;
        this.order = order;
        this.orderManager = orderManager;
        this.orderDAO = new OrderDataAccessObject();

        initComponents();
        loadOrderData();
        loadTableFromDB();
    }

    private void initComponents() {
        setSize(1000, 750);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(Color.WHITE);


        JPanel topPanel = new JPanel(new GridBagLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1), " ข้อมูลทั่วไป ", 0, 0, HEADER_FONT, Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        txtOrderId = createTextField(false);
        cmbStatus = createStatusComboBox();
        txtCustomerName = createTextField(false);
        txtPickupDate = createTextField(false); // Read-only เพราะจะออโต้ตามสถานะ
        txtPhone = createTextField(false);
        txtBookingTime = createTextField(false);


        cmbStatus.addActionListener(e -> {
            if (isInitializing) return;

            String status = (String) cmbStatus.getSelectedItem();
            if ("เสร็จสิ้น".equals(status)) {

                txtPickupDate.setText(dateFormat.format(new Date()));
            } else {

                txtPickupDate.setText("-");
            }
        });


        addFormRow(topPanel, gbc, 0, 0, "รหัสคำสั่งซื้อ:", txtOrderId);
        addFormRow(topPanel, gbc, 0, 2, "สถานะงาน:", cmbStatus);

        addFormRow(topPanel, gbc, 1, 0, "ชื่อลูกค้า:", txtCustomerName);
        addFormRow(topPanel, gbc, 1, 2, "วันที่รับผ้า:", txtPickupDate);

        addFormRow(topPanel, gbc, 2, 0, "เบอร์โทรศัพท์:", txtPhone);
        addFormRow(topPanel, gbc, 2, 2, "รอบเวลาจอง:", txtBookingTime);


        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1), " รายการบริการ ", 0, 0, HEADER_FONT, Color.BLACK));

        String[] columnNames = {"รายละเอียดบริการ", "ราคา (บาท)"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return true; }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 1) ? Double.class : String.class;
            }
        };

        itemsTable = new JTable(tableModel);
        itemsTable.setFont(CONTENT_FONT);
        itemsTable.setRowHeight(45);


        JTableHeader header = itemsTable.getTableHeader();
        header.setPreferredSize(new Dimension(0, 45));
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                setFont(new Font("Tahoma", Font.BOLD, 16));
                setBackground(new Color(230, 230, 230));
                setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.GRAY));
                return c;
            }
        });


        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(700);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        itemsTable.getColumnModel().getColumn(1).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setFont(new Font("Tahoma", Font.BOLD, 18));
                if (isSelected) {
                    c.setForeground(Color.BLACK);
                    c.setBackground(new Color(255, 255, 204));
                } else {
                    c.setForeground(new Color(0, 153, 0));
                    c.setBackground(Color.WHITE);
                }
                setHorizontalAlignment(JLabel.CENTER);
                return c;
            }
        });

        tableModel.addTableModelListener(e -> {
            if (isInitializing || isSystemUpdating) return;
            if (e.getType() == TableModelEvent.UPDATE) {
                int row = e.getFirstRow();
                int col = e.getColumn();
                if (col == 0) calculatePriceRow(row);
                else if (col == 1) calculateGrandTotal();
            }
        });

        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        tablePanel.add(scrollPane, BorderLayout.CENTER);


        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(new EmptyBorder(15, 0, 0, 0));

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pricePanel.setBackground(Color.WHITE);
        JLabel lblTotal = new JLabel("ราคารวมสุทธิ (บาท): ");
        lblTotal.setFont(new Font("Tahoma", Font.BOLD, 22));

        txtTotalPrice = createTextField(false);
        txtTotalPrice.setFont(new Font("Tahoma", Font.BOLD, 36));
        txtTotalPrice.setHorizontalAlignment(JTextField.RIGHT);
        txtTotalPrice.setForeground(new Color(0, 153, 0));
        txtTotalPrice.setPreferredSize(new Dimension(280, 60));

        pricePanel.add(lblTotal);
        pricePanel.add(txtTotalPrice);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        buttonPanel.setBackground(Color.WHITE);

        btnSave = createButton("บันทึกการเปลี่ยนแปลง", new Color(144, 238, 144));
        btnDelete = createButton("ลบออเดอร์นี้", new Color(255, 182, 193));
        btnCancel = createButton("ยกเลิก", new Color(230, 230, 230));

        buttonPanel.add(btnSave);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnCancel);

        bottomPanel.add(pricePanel, BorderLayout.NORTH);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(tablePanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        btnSave.addActionListener(this::onSave);
        btnCancel.addActionListener(e -> dispose());
        btnDelete.addActionListener(this::onDelete);
    }

    private void loadOrderData() {
        isInitializing = true; // เริ่มโหลดข้อมูล (ป้องกัน Listener ทำงานผิดจังหวะ)

        if (order != null) {
            txtOrderId.setText(order.getOrderId());
            txtCustomerName.setText(order.getCustomerName());
            txtPhone.setText(order.getCustomerPhone());
            cmbStatus.setSelectedItem(order.getStatus());

            if ("เสร็จสิ้น".equals(order.getStatus()) && order.getPickupDate() != null) {
                txtPickupDate.setText(dateFormat.format(order.getPickupDate()));
            } else {
                txtPickupDate.setText("-");
            }

            txtBookingTime.setText(getBookingTimeFromDB(order.getOrderId()));
        }

        isInitializing = false; // โหลดเสร็จแล้ว
    }



    private void loadTableFromDB() {
        isInitializing = true;
        tableModel.setRowCount(0);
        List<String> lines = orderDAO.getOrderDetailsByOrderId(order.getOrderId());
        if (lines != null && !lines.isEmpty()) {
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                String[] subLines = line.split("\n");
                for (String subLine : subLines) {
                    tableModel.addRow(new Object[]{subLine, 0.0});
                    calculatePriceRowInternal(tableModel.getRowCount() - 1);
                }
            }
        } else {
            tableModel.addRow(new Object[]{"", 0.0});
        }
        txtTotalPrice.setText(currencyFormat.format(order.getTotalPrice()));
        isInitializing = false;
    }

    private String getBookingTimeFromDB(String orderId) {
        String query = "SELECT booking_time FROM laundryorder WHERE order_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) return rs.getString(1);
        } catch (Exception e) {
            System.err.println("Error fetching booking time: " + e.getMessage());
        }
        return "-";
    }

    private void calculatePriceRow(int row) {
        SwingUtilities.invokeLater(() -> {
            isSystemUpdating = true;
            calculatePriceRowInternal(row);
            isSystemUpdating = false;
            calculateGrandTotal();
        });
    }

    private void calculatePriceRowInternal(int row) {
        try {
            String text = (String) tableModel.getValueAt(row, 0);
            if (text == null) text = "";
            String[] items = text.split(",");
            double totalRow = 0.0;
            for (String item : items) {
                item = item.trim();
                if (item.isEmpty()) continue;
                double itemPrice = 0.0;
                String cleanItem = item.replace(",", "");
                Pattern pEqual = Pattern.compile("=\\s*[฿B]?\\s*(\\d+(\\.\\d+)?)");
                Matcher mEqual = pEqual.matcher(cleanItem);

                if (mEqual.find()) {
                    itemPrice = Double.parseDouble(mEqual.group(1));
                } else {
                    if (cleanItem.toLowerCase().contains("x")) {
                        double quantity = 1.0;
                        double pricePerUnit = 0.0;
                        Pattern qtyPattern = Pattern.compile("[xX]\\s*(\\d+(\\.\\d+)?)");
                        Matcher qtyMatcher = qtyPattern.matcher(cleanItem);
                        if (qtyMatcher.find()) {
                            quantity = Double.parseDouble(qtyMatcher.group(1));
                            cleanItem = cleanItem.replace(qtyMatcher.group(0), "");
                        }
                        List<Double> numbers = extractNumbers(cleanItem);
                        if (!numbers.isEmpty()) pricePerUnit = numbers.get(numbers.size() - 1);
                        itemPrice = quantity * pricePerUnit;
                    } else {
                        List<Double> numbers = extractNumbers(cleanItem);
                        if (!numbers.isEmpty()) itemPrice = numbers.get(numbers.size() - 1);
                    }
                }
                totalRow += itemPrice;
            }
            tableModel.setValueAt(totalRow, row, 1);
        } catch (Exception e) {
            tableModel.setValueAt(0.0, row, 1);
        }
    }

    private List<Double> extractNumbers(String text) {
        List<Double> numbers = new ArrayList<>();
        Pattern p = Pattern.compile("(\\d+(\\.\\d+)?)");
        Matcher m = p.matcher(text);
        while (m.find()) {
            try { numbers.add(Double.parseDouble(m.group(1))); } catch (Exception ignored) {}
        }
        return numbers;
    }

    private void calculateGrandTotal() {
        double grandTotal = 0.0;
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            try {
                Object val = tableModel.getValueAt(i, 1);
                double rowVal = 0.0;
                if (val instanceof Number) rowVal = ((Number) val).doubleValue();
                else if (val instanceof String && !((String) val).isEmpty()) rowVal = Double.parseDouble(((String) val).replace(",", ""));
                grandTotal += rowVal;
            } catch (Exception e) { }
        }
        txtTotalPrice.setText(currencyFormat.format(grandTotal));
    }

    private void onSave(ActionEvent e) {
        try {
            Number number = currencyFormat.parse(txtTotalPrice.getText());
            double newTotalPrice = number.doubleValue();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String line = (String) tableModel.getValueAt(i, 0);
                if (line != null && !line.trim().isEmpty()) {
                    if (sb.length() > 0) sb.append("\n");
                    sb.append(line.trim());
                }
            }

            boolean success = orderDAO.updateOrderDetailsAndPrice(order.getOrderId(), sb.toString(), newTotalPrice);


            String status = (String) cmbStatus.getSelectedItem();
            orderManager.updateOrderStatus(order.getOrderId(), status, "Admin", "แก้ไข");


            if (success) {
                JOptionPane.showMessageDialog(this, "บันทึกเรียบร้อย");
                if (parentFrame != null) parentFrame.onStatusChanged();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "บันทึกไม่สำเร็จ", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    private void onDelete(ActionEvent e) {
        if (JOptionPane.showConfirmDialog(this, "ยืนยันการลบ?", "ยืนยัน", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            if (orderManager.deleteOrder(order.getOrderId())) {
                JOptionPane.showMessageDialog(this, "ลบข้อมูลเรียบร้อย");
                if (parentFrame != null) parentFrame.onStatusChanged();
                dispose();
            }
        }
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, int row, int col, String labelText, JComponent component) {
        gbc.gridy = row;
        gbc.gridx = col;
        gbc.weightx = 0.1;
        JLabel label = new JLabel(labelText);
        label.setFont(HEADER_FONT);
        panel.add(label, gbc);

        gbc.gridx = col + 1;
        gbc.weightx = 0.4;
        component.setPreferredSize(new Dimension(220, 40));
        panel.add(component, gbc);
    }

    private JTextField createTextField(boolean editable) {
        JTextField field = new JTextField();
        field.setFont(editable ? CONTENT_FONT : CONTENT_FONT_BOLD);
        field.setEditable(editable);
        field.setBackground(editable ? Color.WHITE : new Color(245, 245, 245));
        field.setBorder(new CompoundBorder(new LineBorder(Color.GRAY, 1), new EmptyBorder(5, 8, 5, 8)));
        return field;
    }

    private JComboBox<String> createStatusComboBox() {
        JComboBox<String> cmb = new JComboBox<>(new String[]{"รอดำเนินการ", "กำลังดำเนินการ", "เสร็จสิ้น", "ยกเลิก"});
        cmb.setFont(CONTENT_FONT);
        cmb.setBackground(Color.WHITE);
        ((JLabel)cmb.getRenderer()).setBorder(new EmptyBorder(0,5,0,0));
        return cmb;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Tahoma", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(220, 50));
        btn.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        return btn;
    }
}