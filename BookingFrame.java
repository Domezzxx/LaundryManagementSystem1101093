import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BookingFrame extends JFrame {

    private String username;

    public static class OrderItem {
        String itemName;
        String serviceType;
        int quantity;
        int pricePerItem;

        public OrderItem(String itemName, String serviceType, int quantity, int pricePerItem) {
            this.itemName = itemName;
            this.serviceType = serviceType;
            this.quantity = quantity;
            this.pricePerItem = pricePerItem;
        }

        public int getTotalPrice() {
            return quantity * pricePerItem;
        }

        public String getKey() {
            return itemName + "_" + serviceType;
        }
    }

    private List<OrderItem> orderItems;
    private int totalAmount;
    private JPanel itemsPanel;
    private JLabel totalLabel;
    private LocalDate selectedDate;

    private String selectedTimeSlot = null;


    private final String[] TIME_SLOTS = {
            "09:00", "10:00", "11:00", "13:00", "14:00", "15:00", "16:00", "17:00"
    };

    private String customerName;
    private int customerId;

    private JLabel monthYearLabel;
    private JPanel calendarGridPanel;
    private JPanel timeSlotsPanel;

    private JScrollPane timeSlotsScrollPane;

    private HomeFrame homeFrame;
    private LaundryServiceFrame laundryServiceFrame;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "";

    public BookingFrame(List<OrderItem> items, int total, String customerName, int customerId,
                        HomeFrame homeFrame, LaundryServiceFrame laundryServiceFrame) {
        this.orderItems = items != null ? items : new ArrayList<>();
        this.totalAmount = total;
        this.customerName = customerName;
        this.customerId = customerId;
        this.selectedDate = LocalDate.now();
        this.homeFrame = homeFrame;
        this.laundryServiceFrame = laundryServiceFrame;

        if (this.orderItems.isEmpty() && total > 0) {
            this.totalAmount = 0;
        }

        initComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (laundryServiceFrame != null) {
                    laundryServiceFrame.setVisible(true);
                } else if (homeFrame != null) {
                    homeFrame.setVisible(true);
                }
                dispose();
            }
        });
    }

    public BookingFrame(List<OrderItem> items, int total, String customerName, int customerId) {
        this(items, total, customerName, customerId, null, null);
    }

    private void initComponents() {
        setTitle("เลือกวันและเวลารับ-ส่งผ้า");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel topBar = createTopBar();

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 0, 20);
        mainPanel.add(createLeftPanel(), gbc);

        gbc.gridx = 1;
        gbc.insets = new Insets(0, 20, 0, 0);
        mainPanel.add(createRightPanel(), gbc);

        add(topBar, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        refreshCalendar(calendarGridPanel);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(0, 204, 204));
        topBar.setPreferredSize(new Dimension(1200, 50));
        topBar.setLayout(new BorderLayout());
        topBar.setBorder(new EmptyBorder(8, 20, 8, 20));

        // Left side - Back button and contact info
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setOpaque(false);

        // Back button
        JButton backButton = new JButton("← กลับ");
        backButton.setBackground(new Color(0, 180, 180));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.setPreferredSize(new Dimension(90, 30));


        backButton.addActionListener(e -> {
            this.dispose();

            new LaundryServiceFrame().setVisible(true);
        });

        JLabel phoneLabel = new JLabel("📞 01-234-5678");
        phoneLabel.setForeground(Color.WHITE);
        phoneLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        JLabel lineLabel = new JLabel("💬 @Laundry Clean & Fresh");
        lineLabel.setForeground(Color.WHITE);
        lineLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

        leftPanel.add(backButton);
        leftPanel.add(phoneLabel);
        leftPanel.add(lineLabel);

        // Right side
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);


        JButton userButton = new JButton(customerName + " ▼");
        userButton.setBackground(Color.WHITE);
        userButton.setForeground(new Color(0, 204, 204));
        userButton.setFont(new Font("Tahoma", Font.BOLD, 12));
        userButton.setFocusPainted(false);
        userButton.setBorderPainted(false);
        userButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        userButton.setPreferredSize(new Dimension(180, 30));

        JPopupMenu userMenu = new JPopupMenu();
        JMenuItem profileItem = new JMenuItem("โปรไฟล์");
        JMenuItem logoutItem = new JMenuItem("ออกจากระบบ");

        profileItem.setFont(new Font("Tahoma", Font.PLAIN, 12));
        logoutItem.setFont(new Font("Tahoma", Font.PLAIN, 12));


        profileItem.addActionListener(e -> {

            this.setVisible(false);


            UserProfileFrame profileFrame = new UserProfileFrame(this.username);


            profileFrame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    BookingFrame.this.setVisible(true); // กลับมาแสดงหน้า Home
                }
            });

            profileFrame.setVisible(true);
        });

        logoutItem.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(BookingFrame.this,
                    "คุณต้องการออกจากระบบหรือไม่?",
                    "ยืนยันการออกจากระบบ",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                BookingFrame.this.dispose();
                new LoginFrame().setVisible(true);
            }
        });

        userMenu.add(profileItem);
        userMenu.addSeparator();
        userMenu.add(logoutItem);

        userButton.addActionListener(e -> userMenu.show(userButton, 0, userButton.getHeight()));


        rightPanel.add(userButton);

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createLeftPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("รายการตะกร้าสินค้า");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        panel.add(titleLabel);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBackground(Color.WHITE);

        for (OrderItem item : orderItems) {
            itemsPanel.add(createOrderItemPanel(item));
            itemsPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        scrollPane.setBorder(null);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(450, 400));
        scrollPane.setPreferredSize(new Dimension(450, 400));

        panel.add(scrollPane);
        return panel;
    }

    private JPanel createOrderItemPanel(OrderItem item) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(450, 80));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setOpaque(false);

        JLabel nameLabel = new JLabel(item.itemName);
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

        String[] serviceTypes = {"ซัก", "ซักแห้ง", "ซักและรีด"};
        JComboBox<String> serviceCombo = new JComboBox<>(serviceTypes);
        serviceCombo.setSelectedItem(item.serviceType);
        serviceCombo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        serviceCombo.setMaximumSize(new Dimension(120, 30));
        serviceCombo.setAlignmentX(Component.LEFT_ALIGNMENT);

        serviceCombo.addActionListener(e -> {
            item.serviceType = (String) serviceCombo.getSelectedItem();
            updateTotal();
        });

        leftPanel.add(nameLabel);
        leftPanel.add(Box.createVerticalStrut(5));
        leftPanel.add(serviceCombo);

        JPanel centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        centerPanel.setOpaque(false);

        JButton minusBtn = createSmallButton("-");
        JLabel qtyLabel = new JLabel(String.valueOf(item.quantity));
        qtyLabel.setFont(new Font("Tahoma", Font.BOLD, 15));
        qtyLabel.setPreferredSize(new Dimension(30, 30));
        qtyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        JButton plusBtn = createSmallButton("+");

        minusBtn.addActionListener(e -> {
            if (item.quantity > 1) {
                item.quantity--;
                qtyLabel.setText(String.valueOf(item.quantity));
                updateTotal();
            } else if (item.quantity == 1) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "ต้องการลบรายการ " + item.itemName + " ออกจากตะกร้าหรือไม่?",
                        "ยืนยันการลบ",
                        JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    orderItems.remove(item);
                    updateTotal();
                    refreshLeftPanel();
                }
            }
        });

        plusBtn.addActionListener(e -> {
            item.quantity++;
            qtyLabel.setText(String.valueOf(item.quantity));
            updateTotal();
        });

        JLabel priceLabel = new JLabel("฿" + String.format("%,d", item.getTotalPrice()));
        priceLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

        qtyLabel.addPropertyChangeListener("text", evt -> {
            priceLabel.setText("฿" + String.format("%,d", item.getTotalPrice()));
        });

        serviceCombo.addActionListener(e -> {
            priceLabel.setText("฿" + String.format("%,d", item.getTotalPrice()));
        });

        centerPanel.add(minusBtn);
        centerPanel.add(qtyLabel);
        centerPanel.add(plusBtn);

        panel.add(leftPanel, BorderLayout.WEST);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(priceLabel, BorderLayout.EAST);

        return panel;
    }

    private void refreshLeftPanel() {
        itemsPanel.removeAll();
        for (OrderItem item : orderItems) {
            itemsPanel.add(createOrderItemPanel(item));
            itemsPanel.add(Box.createVerticalStrut(10));
        }
        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private JButton createSmallButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(30, 30));
        btn.setFont(new Font("Tahoma", Font.BOLD, 20));
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setHorizontalAlignment(SwingConstants.CENTER);
        btn.setVerticalAlignment(SwingConstants.CENTER);
        btn.setBackground(new Color(240, 240, 240));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JPanel createRightPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(250, 250, 250));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel dateTitle = new JLabel("วันที่ต้องการจอง");
        dateTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        dateTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel fixedCalendarWrapper = new JPanel(new BorderLayout());
        fixedCalendarWrapper.setOpaque(false);
        fixedCalendarWrapper.setPreferredSize(new Dimension(420, 250));
        fixedCalendarWrapper.setMaximumSize(new Dimension(420, 250));
        fixedCalendarWrapper.add(createCalendarPanel(), BorderLayout.CENTER);
        fixedCalendarWrapper.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(dateTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(fixedCalendarWrapper);
        panel.add(Box.createVerticalStrut(30));

        JLabel timeTitle = new JLabel("เวลาที่ต้องการจอง");
        timeTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        timeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        timeSlotsPanel = createTimePickerPanel();
        timeSlotsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(timeTitle);
        panel.add(Box.createVerticalStrut(15));
        panel.add(timeSlotsPanel);
        panel.add(Box.createVerticalStrut(30));

        JPanel bottomPanel = createBottomPanel();
        bottomPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(bottomPanel);

        return panel;
    }

    private JPanel createCalendarPanel() {
        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(Color.WHITE);
        container.setBorder(new EmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        headerPanel.setOpaque(false);
        monthYearLabel = new JLabel();
        monthYearLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        headerPanel.add(monthYearLabel);
        container.add(headerPanel);

        container.add(Box.createVerticalStrut(10));

        calendarGridPanel = new JPanel(new GridLayout(0, 7, 5, 5));
        calendarGridPanel.setOpaque(false);

        Dimension fixedGridSize = new Dimension(390, 240);
        calendarGridPanel.setPreferredSize(fixedGridSize);
        calendarGridPanel.setMinimumSize(fixedGridSize);
        calendarGridPanel.setMaximumSize(fixedGridSize);

        String[] days = {"อา", "จ", "อ", "พ", "พฤ", "ศ", "ส"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Tahoma", Font.BOLD, 13));
            label.setForeground(Color.GRAY);
            calendarGridPanel.add(label);
        }

        container.add(calendarGridPanel);
        return container;
    }

    private JButton createDayButton(LocalDate date, boolean isSelectable, boolean isPastDate, JPanel parentPanel) {
        JButton btn = new JButton(String.valueOf(date.getDayOfMonth()));
        btn.setFont(new Font("Tahoma", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setPreferredSize(new Dimension(45, 30));

        LocalDate today = LocalDate.now();

        btn.setBackground(Color.WHITE);
        btn.setForeground(Color.BLACK);

        if (isPastDate) {
            btn.setEnabled(false);
            btn.setForeground(new Color(160, 160, 160));
            btn.setBorder(null);
        } else if (!isSelectable) {
            btn.setEnabled(false);
            btn.setForeground(new Color(200, 200, 200));
            btn.setBorder(null);
        } else {
            if (date.equals(selectedDate)) {
                btn.setBackground(new Color(0, 204, 204));
                btn.setForeground(Color.WHITE);
            }

            if (date.isEqual(today)) {
                btn.setBorder(BorderFactory.createLineBorder(new Color(0, 150, 150), 2));
            } else if (!date.equals(selectedDate)) {
                btn.setBorder(null);
            }

            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                selectedDate = date;
                selectedTimeSlot = null;
                refreshCalendar(parentPanel);
                updateTimeSlotButtons();
            });
        }

        return btn;
    }

    private void refreshCalendar(JPanel calendarGridPanel) {
        calendarGridPanel.removeAll();
        monthYearLabel.setText(selectedDate.format(DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("th", "TH"))));

        String[] days = {"อา", "จ", "อ", "พ", "พฤ", "ศ", "ส"};
        for (String day : days) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(new Font("Tahoma", Font.BOLD, 13));
            label.setForeground(Color.GRAY);
            calendarGridPanel.add(label);
        }

        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = selectedDate.withDayOfMonth(1);
        LocalDate bookingLimit = today.plusDays(15);

        int startDayIndex = firstDayOfMonth.getDayOfWeek().getValue() % 7;
        if (firstDayOfMonth.getDayOfWeek() == DayOfWeek.SUNDAY) {
            startDayIndex = 0;
        }

        for (int i = 0; i < startDayIndex; i++) {
            JLabel emptyLabel = new JLabel("");
            calendarGridPanel.add(emptyLabel);
        }

        for (int i = 1; i <= selectedDate.lengthOfMonth(); i++) {
            LocalDate date = selectedDate.withDayOfMonth(i);
            boolean isPastDate = date.isBefore(today);
            boolean isSelectable = !date.isBefore(today) && date.isBefore(bookingLimit);
            JButton dayBtn = createDayButton(date, isSelectable, isPastDate, calendarGridPanel);
            calendarGridPanel.add(dayBtn);
        }

        calendarGridPanel.revalidate();
        calendarGridPanel.repaint();
    }


    private int getCurrentBookingsCount(String timeSlot, LocalDate date) {
        String query = "SELECT COALESCE(current_bookings, 0) as count FROM booking_slots WHERE booking_date = ? AND time_slot = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setDate(1, Date.valueOf(date));
            pstmt.setString(2, timeSlot);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // หากไม่พบข้อมูลในตาราง แสดงว่ามีการจอง 0 คน
    }


    private boolean isTimeSlotAvailable(String timeSlot, LocalDate date) {
        int current = getCurrentBookingsCount(timeSlot, date);
        return current < 8;
    }

    private JPanel createTimePickerPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setMaximumSize(new Dimension(420, 450));
        panel.setPreferredSize(new Dimension(420, 450));
        panel.setBorder(new EmptyBorder(20, 15, 20, 15));

        JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        gridPanel.setBackground(Color.WHITE);

        for (String timeSlot : TIME_SLOTS) {
            JButton slotButton = createTimeSlotButton(timeSlot);
            gridPanel.add(slotButton);
        }

        timeSlotsScrollPane = new JScrollPane(gridPanel);
        timeSlotsScrollPane.setBorder(null);
        timeSlotsScrollPane.setBackground(Color.WHITE);
        timeSlotsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        panel.add(timeSlotsScrollPane);
        return panel;
    }

    private JButton createTimeSlotButton(String timeSlot) {
        int count = getCurrentBookingsCount(timeSlot, selectedDate);
        String btnText = "<html><center>" + timeSlot + "<br/>(" + count + "/8)</center></html>";

        JButton slotButton = new JButton(btnText);
        slotButton.setFont(new Font("Tahoma", Font.BOLD, 14));
        slotButton.setFocusPainted(false);
        slotButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        slotButton.setPreferredSize(new Dimension(180, 50));

        boolean isFull = count >= 8;

        if (isFull) {
            slotButton.setBackground(new Color(200, 200, 200));
            slotButton.setForeground(new Color(100, 100, 100));
            slotButton.setEnabled(false);
            slotButton.setBorder(BorderFactory.createLineBorder(new Color(150, 150, 150), 1));
        } else {
            if (timeSlot.equals(selectedTimeSlot)) {
                slotButton.setBackground(new Color(0, 204, 204));
                slotButton.setForeground(Color.WHITE);
                slotButton.setBorderPainted(false);
            } else {
                slotButton.setBackground(Color.WHITE);
                slotButton.setForeground(Color.BLACK);
                slotButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
            }

            slotButton.addActionListener(e -> {
                selectedTimeSlot = timeSlot;
                updateTimeSlotButtons();
            });
        }

        return slotButton;
    }

    private void updateTimeSlotButtons() {
        if (timeSlotsScrollPane == null || timeSlotsScrollPane.getViewport().getView() == null) {
            refreshTimeSlots();
            return;
        }

        JPanel gridPanel = (JPanel) timeSlotsScrollPane.getViewport().getView();
        gridPanel.removeAll();

        for (String timeSlot : TIME_SLOTS) {
            JButton slotButton = createTimeSlotButton(timeSlot);
            gridPanel.add(slotButton);
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void refreshTimeSlots() {
        if (timeSlotsScrollPane == null) {
            timeSlotsPanel.removeAll();

            JPanel gridPanel = new JPanel(new GridLayout(0, 2, 10, 10));
            gridPanel.setBackground(Color.WHITE);

            for (String timeSlot : TIME_SLOTS) {
                JButton slotButton = createTimeSlotButton(timeSlot);
                gridPanel.add(slotButton);
            }

            timeSlotsScrollPane = new JScrollPane(gridPanel);
            timeSlotsScrollPane.setBorder(null);
            timeSlotsScrollPane.setBackground(Color.WHITE);
            timeSlotsScrollPane.getVerticalScrollBar().setUnitIncrement(16);

            timeSlotsPanel.add(timeSlotsScrollPane);
            timeSlotsPanel.revalidate();
            timeSlotsPanel.repaint();
        } else {
            updateTimeSlotButtons();
        }
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(new Color(250, 250, 250));
        panel.setMaximumSize(new Dimension(420, 100));

        JPanel totalPanel = new JPanel(new BorderLayout());
        totalPanel.setBackground(new Color(250, 250, 250));

        JLabel totalTextLabel = new JLabel("รวมทั้งหมด");
        totalTextLabel.setFont(new Font("Tahoma", Font.BOLD, 16));

        totalLabel = new JLabel("฿" + String.format("%,d", totalAmount));
        totalLabel.setFont(new Font("Tahoma", Font.BOLD, 24));
        totalLabel.setForeground(new Color(0, 150, 150));

        totalPanel.add(totalTextLabel, BorderLayout.WEST);
        totalPanel.add(totalLabel, BorderLayout.EAST);

        JButton submitBtn = new JButton("ยืนยันและชำระเงิน");
        submitBtn.setBackground(new Color(0, 204, 204));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFont(new Font("Tahoma", Font.BOLD, 18));
        submitBtn.setFocusPainted(false);
        submitBtn.setBorderPainted(false);
        submitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitBtn.setPreferredSize(new Dimension(420, 50));

        submitBtn.addActionListener(e -> {
            if (orderItems.isEmpty()) {
                JOptionPane.showMessageDialog(this, "กรุณาเพิ่มรายการสั่งซักก่อนดำเนินการต่อ", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedTimeSlot == null) {
                JOptionPane.showMessageDialog(this, "กรุณาเลือกช่วงเวลาที่ต้องการจอง", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (selectedDate.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "ไม่สามารถจองวันที่ผ่านมาแล้วได้ กรุณาเลือกวันที่ใหม่", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // ตรวจสอบจำนวนจองล่าสุดอีกครั้งก่อนไปหน้าถัดไป
            if (!isTimeSlotAvailable(selectedTimeSlot, selectedDate)) {
                JOptionPane.showMessageDialog(this, "ขออภัย ช่วงเวลานี้เต็มแล้ว (8/8) กรุณาเลือกช่วงเวลาอื่น", "แจ้งเตือน", JOptionPane.WARNING_MESSAGE);
                updateTimeSlotButtons();
                return;
            }

            String dateStr = selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            String timeStr = selectedTimeSlot;

            try {
                PaymentFrame paymentFrame = new PaymentFrame(orderItems, totalAmount, dateStr, timeStr, customerName, customerId, this.homeFrame);
                paymentFrame.setVisible(true);
                this.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + ex.getMessage(), "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        panel.add(totalPanel, BorderLayout.NORTH);
        panel.add(submitBtn, BorderLayout.SOUTH);

        return panel;
    }

    private void updateTotal() {
        totalAmount = 0;
        for (OrderItem item : orderItems) {
            totalAmount += item.getTotalPrice();
        }
        totalLabel.setText("฿" + String.format("%,d", totalAmount));
    }

}
