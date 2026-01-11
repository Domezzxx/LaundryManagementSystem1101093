import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.text.DecimalFormat;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ServiceManagementFrame extends JFrame implements OrderManager.StatusChangeListener {
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private JLabel lblWaitingCount, lblProcessingCount, lblCompletedCount, lblCancelledCount;
    private OrderManager orderManager;
    private JTextField searchField;

    private JComboBox<String> sortComboBox;
    private OrderDataAccessObject orderDAO;

    public ServiceManagementFrame() {
        orderManager = OrderManager.getInstance();
        orderManager.addStatusChangeListener(this);
        this.orderDAO = new OrderDataAccessObject();

        initComponents();
        loadOrderData();
    }

    private void initComponents() {
        setTitle("จัดการบริการ - ปลายฟ้า LAUNDRY");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = createSidebar();
        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel searchPanel = createSearchPanel();
        JPanel statusPanel = createStatusPanel();
        JPanel tablePanel = createTablePanel();

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(statusPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

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
        logoutBtn.setContentAreaFilled(false);
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


    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);


        JLabel searchIcon = new JLabel("🔍");
        searchIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        searchIcon.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        searchField = new JTextField();
        searchField.setPreferredSize(new Dimension(400, 35)); // ปรับขนาดลงนิดหน่อยเพื่อเผื่อที่ให้ปุ่ม Sort
        searchField.setFont(new Font("Tahoma", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                refreshTableData();
            }
        });

        panel.add(searchIcon);
        panel.add(searchField);


        panel.add(Box.createHorizontalStrut(20)); 

        JLabel lblSort = new JLabel("เรียงตาม: ");
        lblSort.setFont(new Font("Tahoma", Font.BOLD, 14));
        panel.add(lblSort);

        String[] sortOptions = {"ล่าสุด", "ราคา: น้อย - มาก", "ราคา: มาก - น้อย"};
        sortComboBox = new JComboBox<>(sortOptions);
        sortComboBox.setFont(new Font("Tahoma", Font.PLAIN, 14));
        sortComboBox.setBackground(Color.WHITE);
        sortComboBox.setPreferredSize(new Dimension(180, 35));


        sortComboBox.addActionListener(e -> {
            refreshTableData();
        });

        panel.add(sortComboBox);

        return panel;
    }

    private JPanel createStatusPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                "สถานะทั้งหมด",
                0, 0,
                new Font("Tahoma", Font.BOLD, 16)
        ));

        JPanel statusGrid = new JPanel(new GridLayout(1, 4, 15, 0));
        statusGrid.setBackground(Color.WHITE);
        statusGrid.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        lblWaitingCount = new JLabel("0");
        lblProcessingCount = new JLabel("0");
        lblCompletedCount = new JLabel("0");
        lblCancelledCount = new JLabel("0");

        statusGrid.add(createStatusCard("รอดำเนินการ", lblWaitingCount, new Color(255, 215, 0)));
        statusGrid.add(createStatusCard("กำลังดำเนินการ", lblProcessingCount, new Color(0, 206, 209)));
        statusGrid.add(createStatusCard("เสร็จสิ้น", lblCompletedCount, new Color(144, 238, 144)));
        statusGrid.add(createStatusCard("ยกเลิก", lblCancelledCount, new Color(255, 107, 107)));

        panel.add(statusGrid, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createStatusCard(String title, JLabel countLabel, Color color) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        countLabel.setFont(new Font("Tahoma", Font.BOLD, 32));
        countLabel.setForeground(new Color(40, 40, 40));
        countLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(countLabel, BorderLayout.CENTER);

        return card;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        String[] columns = {"หมายเลข", "ชื่อลูกค้า", "เบอร์โทร", "วันสั่ง", "วันรับผ้า", "สถานะ", "ราคา", ""};


        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; 
            }
        };

        orderTable = new JTable(tableModel);
        orderTable.setFont(new Font("Tahoma", Font.PLAIN, 14));
        orderTable.setRowHeight(45);
        orderTable.setGridColor(new Color(0, 230, 230));

        JTableHeader header = orderTable.getTableHeader();
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

        orderTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(130);
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(80);
        orderTable.getColumnModel().getColumn(7).setPreferredWidth(100);

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < orderTable.getColumnCount(); i++) {
            if (i != 5 && i != 7) {
                orderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
            }
        }

        orderTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setOpaque(true);
                label.setHorizontalAlignment(CENTER);
                label.setFont(new Font("Tahoma", Font.BOLD, 13));

                String status = (String) value;
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                } else {
                    switch (status) {
                        case "รอดำเนินการ":
                            label.setBackground(new Color(255, 193, 7));
                            label.setForeground(Color.BLACK);
                            break;
                        case "กำลังดำเนินการ":
                            label.setBackground(new Color(23, 162, 184));
                            label.setForeground(Color.WHITE);
                            break;
                        case "เสร็จสิ้น":
                            label.setBackground(new Color(40, 167, 69));
                            label.setForeground(Color.WHITE);
                            break;
                        case "ยกเลิก":
                            label.setBackground(new Color(220, 53, 69));
                            label.setForeground(Color.WHITE);
                            break;
                        default:
                            label.setBackground(Color.WHITE);
                            label.setForeground(Color.BLACK);
                    }
                }
                return label;
            }
        });

        orderTable.getColumnModel().getColumn(7).setCellRenderer(new ButtonRenderer());
        orderTable.getColumnModel().getColumn(7).setCellEditor(new ButtonEditor(new JCheckBox()));

        orderTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int row = orderTable.getSelectedRow();
                    if (row != -1) {
                        String orderId = (String) tableModel.getValueAt(row, 0);
                        openOrderEditDialog(orderId);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private void refreshTableData() {
        String searchText = (searchField != null) ? searchField.getText().trim() : "";
        List<Order> orders;

       
        if (searchText.isEmpty()) {
            orders = orderManager.getAllOrders();
        } else {

            if (searchText.matches("^[0-9]{1,2}$|^100$")) {

                orders = orderManager.searchByOrderId(searchText);
            }

            else if (searchText.matches("^[0-9]{9,10}$")) {

                orders = orderManager.searchByPhone(searchText);
            }

            else {
                orders = orderManager.searchOrders(searchText);
            }
        }


        if (sortComboBox != null) {
            int selectedIndex = sortComboBox.getSelectedIndex();
            if (selectedIndex == 1) orders.sort(Comparator.comparingDouble(Order::getTotalPrice));
            else if (selectedIndex == 2) orders.sort(Comparator.comparingDouble(Order::getTotalPrice).reversed());
        }


        tableModel.setRowCount(0);
        for (Order order : orders) {
            String pickupDateDisplay = "-";
            if ("เสร็จสิ้น".equals(order.getStatus())) {
                String dbDate = order.getPickupDateFormatted();
                if (dbDate != null && !dbDate.trim().isEmpty() && !"N/A".equals(dbDate)) {
                    pickupDateDisplay = dbDate;
                }
            }

            tableModel.addRow(new Object[]{
                    order.getOrderId(),
                    order.getCustomerName(),
                    order.getCustomerPhone(),
                    order.getOrderDateFormatted(),
                    pickupDateDisplay,
                    order.getStatus(),
                    order.getTotalPriceFormatted(),
                    "แก้ไข / รายละเอียด"
            });
        }
        updateStatusCounts();
    }

    public void loadOrderData() {
        refreshTableData(); // เรียกใช้ Method กลางแทน
    }

    private void filterOrders(String searchText) {
        refreshTableData(); // เรียกใช้ Method กลางแทน
    }

    private String getPickupDateDisplay(Order order) {
        String pickupDate = order.getPickupDateFormatted();

        if (pickupDate == null || pickupDate.trim().isEmpty() || "N/A".equals(pickupDate)) {
            return "-";
        }
        return pickupDate;
    }

    private void updateStatusCounts() {
        int waitingCount = orderManager.countOrdersByStatus("รอดำเนินการ");
        int processingCount = orderManager.countOrdersByStatus("กำลังดำเนินการ");
        int completedCount = orderManager.countOrdersByStatus("เสร็จสิ้น");
        int cancelledCount = orderManager.countOrdersByStatus("ยกเลิก");

        lblWaitingCount.setText(String.valueOf(waitingCount));
        lblProcessingCount.setText(String.valueOf(processingCount));
        lblCompletedCount.setText(String.valueOf(completedCount));
        lblCancelledCount.setText(String.valueOf(cancelledCount));
    }

    @Override
    public void onStatusChanged() {
        SwingUtilities.invokeLater(() -> {
            refreshTableData();
        });
    }

    private ImageIcon createLogoIcon() {
        int size = 60;
        Image img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(new Color(0, 180, 220));
        g2.fillOval(0, 0, size, size);

        g2.setColor(Color.WHITE);
        g2.setStroke(new BasicStroke(3));
        g2.drawOval(2, 2, size - 4, size - 4);

        g2.setColor(Color.WHITE);
        g2.fillRoundRect(15, 18, 30, 28, 5, 5);
        g2.setColor(new Color(0, 180, 220));
        g2.fillOval(20, 24, 20, 20);

        g2.dispose();
        return new ImageIcon(img);
    }

    private void openOrderEditDialog(String orderId) {
        Order order = orderManager.getOrderById(orderId);
        if (order != null) {
            OrderEditDialog dialog = new OrderEditDialog(ServiceManagementFrame.this, order, orderManager);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "ไม่พบข้อมูลคำสั่งซื้อ", "ผิดพลาด", JOptionPane.ERROR_MESSAGE);
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
            setText((value == null) ? "แก้ไข" : value.toString());
            return this;
        }
    }

    class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean clicked;
        private int row;

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


                int selectedRow = orderTable.getSelectedRow();
                if (selectedRow != -1) {
                    String orderId = (String) tableModel.getValueAt(selectedRow, 0);
                    openOrderEditDialog(orderId);
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                     boolean isSelected, int row, int column) {
            this.row = row;
            label = (value == null) ? "แก้ไข" : value.toString();
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

            ServiceManagementFrame frame = new ServiceManagementFrame();
            frame.setVisible(true);
        });
    }

}
