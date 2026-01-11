import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * หน้าแสดงประวัติการใช้บริการของลูกค้า
 * เพิ่มปุ่มกลับ, แสดงชื่อผู้ใช้ (พร้อม Dropdown), และแสดงวันเวลาแบบ real-time (พร้อมวินาที)
 */
public class OrderHistoryPanel extends JPanel {


    private LaundryOrderService orderService;
    private int currentCustomerId;
    private String customerName;
    private JPanel ordersContainer;
    private JLabel statusLabel;
    private JLabel dateTimeLabel;
    private Timer clockTimer;
    private ActionListener backActionListener;

    private static final Font THAI_FONT_TITLE = new Font("Tahoma", Font.BOLD, 24);
    private static final Font THAI_FONT_NORMAL = new Font("Tahoma", Font.PLAIN, 16);
    private static final Font THAI_FONT_BOLD = new Font("Tahoma", Font.BOLD, 18);
    private static final Font THAI_FONT_SMALL = new Font("Tahoma", Font.PLAIN, 14);
    private static final Font THAI_FONT_HEADER = new Font("Tahoma", Font.PLAIN, 13);

    public OrderHistoryPanel(int customerId) {
        this(customerId, null, null);
    }

    public OrderHistoryPanel(int customerId, String customerName, ActionListener backListener) {
        this.currentCustomerId = customerId;
        this.customerName = customerName;
        this.backActionListener = backListener;


        this.orderService = new LaundryOrderService();

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);


        JPanel topBar = createTopBar();
        add(topBar, BorderLayout.NORTH);


        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);


        JPanel logoSection = createLogoSection();
        mainPanel.add(logoSection, BorderLayout.NORTH);


        JPanel ordersSection = createOrdersSection();
        mainPanel.add(ordersSection, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        loadOrders();

    }


    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(0, 204, 204));
        topBar.setPreferredSize(new Dimension(1920, 45));
        topBar.setBorder(new EmptyBorder(8, 15, 8, 15));


        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        JLabel phoneIcon = new JLabel("📞");
        phoneIcon.setForeground(Color.WHITE);
        JLabel phoneLabel = new JLabel("01-234-5678");
        phoneLabel.setFont(THAI_FONT_HEADER);
        phoneLabel.setForeground(Color.WHITE);
        leftPanel.add(phoneIcon);
        leftPanel.add(phoneLabel);


        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        centerPanel.setOpaque(false);
        JLabel shopNameLabel = new JLabel("@Laundry Clean & Fresh");
        shopNameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
        shopNameLabel.setForeground(Color.WHITE);
        centerPanel.add(shopNameLabel);


        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);


        rightPanel.add(Box.createHorizontalStrut(5));



        String displayName = "ลูกค้า"; // Default
        if (customerName != null && !customerName.trim().isEmpty()) {
            displayName = customerName;
        } else {

            if (orderService != null) {
                try {
                    List<LaundryOrder> orders = orderService.getCustomerOrderHistory(currentCustomerId);
                    if (!orders.isEmpty() && orders.get(0).getCustomerName() != null) {
                        displayName = orders.get(0).getCustomerName();
                    }
                } catch (Exception e) {

                }
            }
        }


        JButton userButton = new JButton(displayName + " ▼");
        userButton.setBackground(Color.WHITE);
        userButton.setForeground(new Color(0, 150, 150));
        userButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        userButton.setFocusPainted(false);
        userButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 5)); // ปรับขอบเล็กน้อย
        userButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userButton.setPreferredSize(new Dimension(130, 30));


        JPopupMenu userMenu = new JPopupMenu();


        JMenuItem profileItem = new JMenuItem("โปรไฟล์");
        profileItem.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "ฟังก์ชัน 'โปรไฟล์' ยังไม่ได้เชื่อมต่อ", "แจ้งเตือน", JOptionPane.INFORMATION_MESSAGE);
        });

        JMenuItem logoutItem = new JMenuItem("ออกจากระบบ");
        logoutItem.addActionListener(e -> {

            JOptionPane.showMessageDialog(this, "ฟังก์ชัน 'ออกจากระบบ' ยังไม่ได้เชื่อมต่อ", "แจ้งเตือน", JOptionPane.INFORMATION_MESSAGE);

        });

        userMenu.add(profileItem);
        userMenu.add(new JSeparator());
        userMenu.add(logoutItem);


        userButton.addActionListener(e -> {
            userMenu.show(userButton, 0, userButton.getHeight());
        });

        rightPanel.add(userButton);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(centerPanel, BorderLayout.CENTER);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }


    private JPanel createLogoSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(new EmptyBorder(15, 25, 0, 25));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);


        JButton backButton = new JButton("← ย้อนกลับ");
        backButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        backButton.setForeground(new Color(0, 150, 200));
        backButton.setBackground(Color.WHITE);
        backButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 150, 200), 2),
                new EmptyBorder(5, 12, 5, 12)
        ));
        backButton.setFocusPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));


        if (backActionListener != null) {
            backButton.addActionListener(backActionListener);
        } else {
            backButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(
                        this,
                        "กรุณาเรียกใช้จาก HomeFrame เพื่อใช้ฟังก์ชันย้อนกลับ",
                        "ข้อมูล",
                        JOptionPane.INFORMATION_MESSAGE
                );
            });
        }


        backButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                backButton.setBackground(new Color(230, 245, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                backButton.setBackground(Color.WHITE);
            }
        });

        leftPanel.add(backButton);
        leftPanel.add(Box.createHorizontalStrut(20));


        JLabel logoIcon = new JLabel("🧺");
        logoIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        JLabel shopLabel = new JLabel("ปลายฟ้า LAUNDRY");
        shopLabel.setFont(new Font("Tahoma", Font.BOLD, 20));
        shopLabel.setForeground(new Color(0, 100, 150));

        leftPanel.add(logoIcon);
        leftPanel.add(Box.createHorizontalStrut(10));
        leftPanel.add(shopLabel);

        section.add(leftPanel, BorderLayout.WEST);

        return section;
    }


    private JPanel createOrdersSection() {
        JPanel section = new JPanel(new BorderLayout());
        section.setBackground(Color.WHITE);
        section.setBorder(new EmptyBorder(10, 25, 15, 25));


        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(10, 0, 15, 0));

        JLabel titleLabel = new JLabel("ประวัติการบริการ");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
        titleLabel.setForeground(Color.BLACK);

        statusLabel = new JLabel("");
        statusLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statusLabel, BorderLayout.EAST);

        section.add(headerPanel, BorderLayout.NORTH);


        ordersContainer = new JPanel();
        ordersContainer.setLayout(new BoxLayout(ordersContainer, BoxLayout.Y_AXIS));
        ordersContainer.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(ordersContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        section.add(scrollPane, BorderLayout.CENTER);

        return section;
    }


    private void loadOrders() {
        ordersContainer.removeAll();

        try {

            List<LaundryOrder> orders = orderService.getCustomerOrderHistory(currentCustomerId);

            statusLabel.setText("ทั้งหมด " + orders.size() + " รายการ");

            if (orders.isEmpty()) {
                JPanel emptyPanel = new JPanel();
                emptyPanel.setBackground(Color.WHITE);
                emptyPanel.setLayout(new BoxLayout(emptyPanel, BoxLayout.Y_AXIS));
                emptyPanel.setBorder(new EmptyBorder(80, 0, 80, 0));

                JLabel emptyIcon = new JLabel("📋");
                emptyIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 64));
                emptyIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

                JLabel emptyLabel = new JLabel("ไม่มีประวัติการใช้บริการ");
                emptyLabel.setFont(new Font("Tahoma", Font.BOLD, 18));
                emptyLabel.setForeground(Color.GRAY);
                emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                emptyPanel.add(emptyIcon);
                emptyPanel.add(Box.createVerticalStrut(15));
                emptyPanel.add(emptyLabel);

                ordersContainer.add(emptyPanel);
            } else {
                for (LaundryOrder order : orders) {
                    JPanel orderCard = createOrderCard(order);
                    ordersContainer.add(orderCard);
                    ordersContainer.add(Box.createVerticalStrut(12));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JPanel errorPanel = new JPanel();
            errorPanel.setBackground(new Color(255, 240, 240));
            errorPanel.setBorder(new EmptyBorder(30, 20, 30, 20));

            JLabel errorLabel = new JLabel("⚠ เกิดข้อผิดพลาดในการดึงข้อมูล: " + e.getMessage());
            errorLabel.setFont(THAI_FONT_NORMAL);
            errorLabel.setForeground(new Color(200, 0, 0));

            errorPanel.add(errorLabel);
            ordersContainer.add(errorPanel);
        }

        ordersContainer.revalidate();
        ordersContainer.repaint();
    }

    /**
     * สร้างการ์ดแสดงข้อมูล Order
     */
    private JPanel createOrderCard(LaundryOrder order) {
        JPanel card = new JPanel(new BorderLayout(15, 0));
        card.setBackground(new Color(245, 248, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 220, 240), 1),
                new EmptyBorder(18, 22, 18, 22)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));


        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);
        leftPanel.setPreferredSize(new Dimension(180, 100));


        String customerDisplayName = order.getCustomerName() != null ?
                order.getCustomerName() : "ไม่ระบุชื่อ";

        JLabel customerLabel = new JLabel(customerDisplayName);
        customerLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        customerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        leftPanel.add(customerLabel);
        leftPanel.add(Box.createVerticalStrut(20));


        JPanel centerPanel = createCenterPanel(order);


        JPanel rightPanel = createRightPanel(order);

        card.add(leftPanel, BorderLayout.WEST);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);

        return card;
    }


    private JPanel createCenterPanel(LaundryOrder order) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);


        JPanel dateTimePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dateTimePanel.setOpaque(false);
        dateTimePanel.setAlignmentX(Component.LEFT_ALIGNMENT);


        java.time.LocalDate dateToDisplay = order.getBookingDate();
        java.time.LocalTime timeToDisplay = order.getBookingTime();

        String dateSuffix = ""; // สำหรับระบุว่าวันที่มาจาก CreatedAt


        if (dateToDisplay == null && order.getCreatedAt() != null) {
            LocalDateTime created = order.getCreatedAt();
            dateToDisplay = created.toLocalDate();
            timeToDisplay = created.toLocalTime();
            dateSuffix = " (สั่งซื้อ)";
        }

        String dateTimeText = "ไม่ระบุวันที่";


        if (dateToDisplay != null) {
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            dateTimeText = dateToDisplay.format(dateFormatter);

            if (timeToDisplay != null) {
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                dateTimeText += "   " + timeToDisplay.format(timeFormatter) + " น." + dateSuffix;
            } else {
                dateTimeText += dateSuffix;
            }
        }


        JLabel dateTimeLabel = new JLabel(dateTimeText);
        dateTimeLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));

        dateTimePanel.add(dateTimeLabel);


        String statusText = order.getStatus() != null ? order.getStatus() : "ไม่ระบุ";
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        statusPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel statusLabel = new JLabel(statusText);
        statusLabel.setFont(new Font("Tahoma", Font.BOLD, 12));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setOpaque(true);
        statusLabel.setBackground(getStatusColor(statusText));
        statusLabel.setBorder(new EmptyBorder(3, 10, 3, 10));
        statusPanel.add(statusLabel);



        panel.add(dateTimePanel);
        panel.add(Box.createVerticalStrut(8));
        panel.add(statusPanel);

        return panel;
    }


    private JPanel createRightPanel(LaundryOrder order) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(250, 100));


        JLabel itemsHeader = new JLabel("รายการซัก");
        itemsHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
        itemsHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(itemsHeader);
        panel.add(Box.createVerticalStrut(5));


        if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {

            for (OrderDetail detail : order.getOrderDetails()) {
                String detailText = detail.getOrderDetails();
                if (detailText != null && !detailText.trim().isEmpty()) {

                    String[] lines = detailText.split("\\r?\\n");
                    int displayCount = 0;

                    for (String line : lines) {
                        line = line.trim();
                        if (!line.isEmpty() && displayCount < 2) {

                            if (line.length() > 35) {
                                line = line.substring(0, 35) + "...";
                            }

                            JLabel itemLabel = new JLabel("• " + line);
                            itemLabel.setFont(new Font("Tahoma", Font.PLAIN, 12));
                            itemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                            panel.add(itemLabel);
                            displayCount++;
                        }
                    }


                    if (lines.length > 2) {
                        JLabel moreLabel = new JLabel("... และอื่นๆ");
                        moreLabel.setFont(new Font("Tahoma", Font.ITALIC, 11));
                        moreLabel.setForeground(Color.GRAY);
                        moreLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                        panel.add(moreLabel);
                    }

                    break;
                }
            }
        } else {
            JLabel noItemLabel = new JLabel("ไม่มีรายละเอียด");
            noItemLabel.setFont(new Font("Tahoma", Font.ITALIC, 12));
            noItemLabel.setForeground(Color.GRAY);
            noItemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(noItemLabel);
        }

        panel.add(Box.createVerticalStrut(10));


        JLabel priceHeader = new JLabel("รวมทั้งหมด");
        priceHeader.setFont(new Font("Tahoma", Font.BOLD, 14));
        priceHeader.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(priceHeader);

        String priceText = order.getTotalPrice() != null ?
                String.format("%,d บาท", order.getTotalPrice().intValue()) : "0 บาท";
        JLabel priceValue = new JLabel(priceText);
        priceValue.setFont(new Font("Tahoma", Font.BOLD, 18));
        priceValue.setForeground(new Color(0, 150, 0));
        priceValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(priceValue);

        return panel;
    }


    private Color getStatusColor(String status) {
        if (status == null) return Color.GRAY;

        switch (status.trim()) {
            case "เสร็จสิ้น":
                return new Color(76, 175, 80);
            case "รอดำเนินการ":
                return new Color(255, 193, 7);
            case "กำลังดำเนินการ":
                return new Color(33, 150, 243);
            case "ยกเลิก":
                return new Color(244, 67, 54);
            case "paid":
                return new Color(0, 150, 136); // สีเขียวอมฟ้า
            default:
                return Color.GRAY;
        }
    }


    public void refreshOrders() {
        loadOrders();
    }


    public void setCustomerId(int customerId) {
        this.currentCustomerId = customerId;
        loadOrders();
    }


    public void setCustomerName(String name) {
        this.customerName = name;
    }
}