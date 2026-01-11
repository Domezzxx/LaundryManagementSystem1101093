import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.sql.*;
import java.time.LocalDate;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.*;
import org.jfree.chart.labels.StandardPieSectionLabelGenerator;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class LaundryDashboard extends JFrame {
    private Connection conn;
    private JComboBox<String> viewTypeCombo;
    private JComboBox<String> monthCombo;
    private JPanel statsPanel;
    private JPanel chartArea;
    private JPanel chartPanel;

    public LaundryDashboard() {
        setTitle("Dashboard - ระบบซักรีด");
        setSize(1920,1080);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(0, 0));
        getContentPane().setBackground(new Color(245, 245, 245));


        connectDatabase();


        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);


        JPanel mainContent = new JPanel(new BorderLayout(0, 0));
        mainContent.setBackground(new Color(245, 245, 245));


        createTopPanel(mainContent);


        JPanel centerArea = new JPanel(new BorderLayout(0, 0));
        centerArea.setBackground(new Color(245, 245, 245));


        createStatsCards(centerArea);


        chartArea = new JPanel(new GridLayout(1, 2, 15, 0));
        chartArea.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));
        chartArea.setBackground(new Color(245, 245, 245));


        this.chartPanel = chartArea;

        centerArea.add(chartArea, BorderLayout.CENTER);
        mainContent.add(centerArea, BorderLayout.CENTER);


        add(mainContent, BorderLayout.CENTER);


        refreshDashboard();
    }

    private void connectDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/laundry_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&useUnicode=true";
            String user = "root";
            String password = "DomeDome55&55";
            conn = DriverManager.getConnection(url, user, password);


            Statement stmt = conn.createStatement();
            stmt.execute("SET SESSION sql_mode = ''");
            stmt.close();

            System.out.println("เชื่อมต่อฐานข้อมูลสำเร็จ!");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(this,
                    "ไม่พบ MySQL Driver: " + e.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "ไม่สามารถเชื่อมต่อฐานข้อมูล: " + e.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }


    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(0, 204, 204)); // สี Turquoise
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

    private ImageIcon createCircleIcon() {
        int size = 60;
        java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(size, size, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(255, 255, 255, 80));
        g2.fillOval(0, 0, size, size);
        g2.dispose();
        return new ImageIcon(img);
    }


    private void createTopPanel(JPanel container) {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 15));
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));

        JLabel lblView = new JLabel("แสดงข้อมูล:");
        lblView.setFont(new Font("Tahoma", Font.BOLD, 14));

        viewTypeCombo = new JComboBox<>(new String[]{"รายวัน", "รายสัปดาห์", "รายเดือน", "วันนี้"});
        viewTypeCombo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        viewTypeCombo.setPreferredSize(new Dimension(130, 30));

        JLabel lblMonth = new JLabel("เดือน:");
        lblMonth.setFont(new Font("Tahoma", Font.BOLD, 14));
        monthCombo = new JComboBox<>(getMonths());
        monthCombo.setSelectedItem(getCurrentMonth());
        monthCombo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        monthCombo.setPreferredSize(new Dimension(130, 30));

        JButton btnRefresh = new JButton("ดูรายงาน");
        btnRefresh.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnRefresh.setPreferredSize(new Dimension(100, 30));
        btnRefresh.setBackground(new Color(255, 215, 0));
        btnRefresh.setForeground(Color.BLACK);
        btnRefresh.addActionListener(e -> refreshDashboard());

        topPanel.add(lblView);
        topPanel.add(viewTypeCombo);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(lblMonth);
        topPanel.add(monthCombo);
        topPanel.add(Box.createHorizontalStrut(20));
        topPanel.add(btnRefresh);

        container.add(topPanel, BorderLayout.NORTH);
        JButton btnExport = new JButton("Export Excel");
        btnExport.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnExport.setPreferredSize(new Dimension(120, 30));
        btnExport.setBackground(new Color(215, 255, 0));
        btnExport.setForeground(Color.BLACK);
        btnExport.addActionListener(e -> exportToExcel()); // เรียกใช้เมธอด Export

        topPanel.add(btnExport);
    }

    private void exportToExcel() {
        String selectedMonth = (String) monthCombo.getSelectedItem();
        int monthNum = getMonthNumber(selectedMonth);
        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        if (year > 2500) year -= 543;

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("บันทึกข้อมูลรายได้ (แบบละเอียด)");
        fileChooser.setSelectedFile(new java.io.File("Revenue_Detail_Report_" + selectedMonth + "_" + (year + 543) + ".xlsx"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".xlsx")) {
                filePath += ".xlsx";
                fileToSave = new File(filePath);
            }

            if (fileToSave.exists()) {
                try (FileOutputStream fos = new FileOutputStream(fileToSave, true);
                     java.nio.channels.FileLock lock = fos.getChannel().tryLock()) {
                    if (lock == null) {
                        JOptionPane.showMessageDialog(this, "กรุณาปิดไฟล์ Excel เดิมก่อนทำการ Export ใหม่", "ไฟล์ถูกใช้งานอยู่", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "ไฟล์กำลังถูกโปรแกรมอื่นใช้งานอยู่ กรุณาปิดไฟล์ก่อนครับ", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            try (Connection exportConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/laundry_db?useSSL=false&characterEncoding=UTF-8", "root", "DomeDome55&55");
                 Workbook workbook = new XSSFWorkbook()) {

                Sheet sheet = workbook.createSheet("รายละเอียดรายได้ " + selectedMonth);


                String[] columns = {"วันที่รับบริการ", "หมายเลขออเดอร์", "ชื่อลูกค้า", "รายละเอียดสินค้า/บริการ", "สถานะ", "ยอดรวมออเดอร์ (บาท)"};
                Row headerRow = sheet.createRow(0);
                CellStyle headerStyle = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }


                String sql = "SELECT lo.booking_date, lo.order_id, lo.customer_name, lo.status, lo.total_price, od.order_details " +
                        "FROM laundryorder lo " +
                        "JOIN order_detail od ON lo.order_id = od.order_id " +
                        "WHERE MONTH(lo.booking_date) = ? AND YEAR(lo.booking_date) = ? AND lo.status != 'ยกเลิก' " +
                        "ORDER BY lo.booking_date, lo.order_id";

                try (PreparedStatement pstmt = exportConn.prepareStatement(sql)) {
                    pstmt.setInt(1, monthNum);
                    pstmt.setInt(2, year);
                    ResultSet rs = pstmt.executeQuery();

                    int rowNum = 1;
                    double grandTotal = 0;
                    int previousOrderId = -1; // ตัวแปรเช็คออเดอร์ซ้ำ

                    while (rs.next()) {
                        Row row = sheet.createRow(rowNum++);
                        int currentOrderId = rs.getInt("order_id");
                        double orderPrice = rs.getDouble("total_price");

                        row.createCell(0).setCellValue(rs.getTimestamp("booking_date").toString());
                        row.createCell(1).setCellValue(currentOrderId);
                        row.createCell(2).setCellValue(rs.getString("customer_name"));
                        row.createCell(3).setCellValue(rs.getString("order_details")); // ข้อมูลจากตารางลูก
                        row.createCell(4).setCellValue(rs.getString("status"));


                        if (currentOrderId != previousOrderId) {
                            row.createCell(5).setCellValue(orderPrice);
                            grandTotal += orderPrice;
                            previousOrderId = currentOrderId;
                        } else {
                            row.createCell(5).setCellValue("-");
                        }
                    }


                    Row totalRow = sheet.createRow(rowNum + 1);
                    totalRow.createCell(4).setCellValue("ยอดรวมสุทธิเดือนนี้:");
                    Cell totalValCell = totalRow.createCell(5);
                    totalValCell.setCellValue(grandTotal);


                    CellStyle totalStyle = workbook.createCellStyle();
                    org.apache.poi.ss.usermodel.Font totalFont = workbook.createFont();
                    totalFont.setBold(true);
                    totalFont.setColor(IndexedColors.RED.getIndex());
                    totalStyle.setFont(totalFont);
                    totalRow.getCell(4).setCellStyle(totalStyle);
                    totalValCell.setCellStyle(totalStyle);
                }

                for (int i = 0; i < columns.length; i++) sheet.autoSizeColumn(i);

                try (FileOutputStream fileOut = new FileOutputStream(fileToSave)) {
                    workbook.write(fileOut);
                    fileOut.flush();
                }

                JOptionPane.showMessageDialog(this, "ส่งออกข้อมูลแบบละเอียดสำเร็จแล้วที่:\n" + filePath);

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาด: " + ex.getMessage());
            }
        }
    }

    private void createStatsCards(JPanel container) {
        statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        statsPanel.setBackground(new Color(245, 245, 245));
        container.add(statsPanel, BorderLayout.NORTH); // เปลี่ยนเป็น NORTH ของพื้นที่กลาง
    }


    private void refreshDashboard() {
        // ล้าง Component เดิมออกให้หมด
        if (statsPanel != null) statsPanel.removeAll();
        if (chartArea != null) chartArea.removeAll();

        String viewType = (String) viewTypeCombo.getSelectedItem();
        String selectedMonth = (String) monthCombo.getSelectedItem();

        // ดึงข้อมูลใหม่
        updateStatsCards(viewType, selectedMonth);
        chartArea.add(createRevenueBarChart(viewType, selectedMonth));
        chartArea.add(createServiceTypePieChart(viewType, selectedMonth));


        statsPanel.revalidate();
        statsPanel.repaint();
        chartArea.revalidate();
        chartArea.repaint();
    }

    private void updateStatsCards(String viewType, String month) {
        if (conn == null) return;

        int monthNum = getMonthNumber(month);
        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        if (year > 2500) year -= 543;

        String dateCondition;
        if ("วันนี้".equals(viewType)) {

            dateCondition = "DATE(booking_date) = CURDATE() AND MONTH(booking_date) = " + monthNum + " AND YEAR(booking_date) = " + year;
        } else {
            dateCondition = "MONTH(booking_date) = " + monthNum + " AND YEAR(booking_date) = " + year;
        }


        String totalSql = "SELECT COUNT(*) FROM laundryorder WHERE " + dateCondition + " AND status != 'ยกเลิก'";
        String revenueSql = "SELECT SUM(total_price) FROM laundryorder WHERE " + dateCondition + " AND status != 'ยกเลิก'";
        String cancelledSql = "SELECT COUNT(*) FROM laundryorder WHERE status = 'ยกเลิก' AND " + dateCondition;

        int totalOrders = getCountWithCondition(totalSql);
        double totalRevenue = getDoubleWithCondition(revenueSql);
        int cancelledOrders = getCountWithCondition(cancelledSql);

        statsPanel.removeAll();
        statsPanel.add(createStatCard("ออเดอร์ทั้งหมด", String.valueOf(totalOrders), new Color(144, 238, 144), "📋"));
        statsPanel.add(createStatCard("รายการยกเลิก", String.valueOf(cancelledOrders), new Color(255, 160, 160), "❌"));
        statsPanel.add(createStatCard("ยอดสุทธิ (บาท)", String.format("%,.2f", totalRevenue), new Color(255, 215, 0), "💰"));
        statsPanel.revalidate();
        statsPanel.repaint();
    }

    // เมธอดช่วยนับจำนวนแบบมีเงื่อนไข
    private int getCountWithCondition(String sql) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
        return 0;
    }

    // เมธอดช่วยดึงค่าทศนิยม (สำหรับ SUM รายได้)
    private double getDoubleWithCondition(String sql) {
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            System.err.println("Sum Error: " + e.getMessage());
        }
        return 0.0;
    }

    private JPanel createStatCard(String title, String value, Color bgColor, String icon) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));


        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));

        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Tahoma", Font.BOLD, 56));
        lblValue.setForeground(new Color(60, 60, 60));

        centerPanel.add(lblIcon, BorderLayout.NORTH);
        centerPanel.add(lblValue, BorderLayout.CENTER);


        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Tahoma", Font.PLAIN, 18));
        lblTitle.setForeground(new Color(100, 100, 100));


        JPanel colorBar = new JPanel();
        colorBar.setBackground(bgColor);
        colorBar.setPreferredSize(new Dimension(0, 5));

        card.add(colorBar, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(lblTitle, BorderLayout.SOUTH);

        return card;
    }

    private JPanel createServiceTypePieChart(String viewType, String month) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        int monthNum = getMonthNumber(month);
        int year = LocalDate.now().getYear();

        try {
            String dateCondition = "";
            if ("วันนี้".equals(viewType)) {
                // แก้ไขเงื่อนไขให้ตรงกัน
                dateCondition = "DATE(lo.booking_date) = CURDATE() AND MONTH(lo.booking_date) = " + monthNum + " AND YEAR(lo.booking_date) = " + year;
            } else {
                dateCondition = "YEAR(lo.booking_date) = " + year + " AND MONTH(lo.booking_date) = " + monthNum;
            }

            String sql = "SELECT " +
                    "SUM(CASE WHEN od.order_details LIKE '%ซักแห้ง%' THEN 1 ELSE 0 END) as dry_clean, " +
                    "SUM(CASE WHEN od.order_details LIKE '%ซักและรีด%' THEN 1 ELSE 0 END) as wash_iron, " +
                    "SUM(CASE WHEN (od.order_details LIKE '%ซัก%' OR od.order_details LIKE '%ซักผ้า%') " +
                    "AND od.order_details NOT LIKE '%ซักแห้ง%' " +
                    "AND od.order_details NOT LIKE '%ซักและรีด%' THEN 1 ELSE 0 END) as wash_only " +
                    "FROM order_detail od " +
                    "JOIN laundryorder lo ON od.order_id = lo.order_id " +
                    "WHERE " + dateCondition;

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int wash = rs.getInt("wash_only");
                int dry = rs.getInt("dry_clean");
                int iron = rs.getInt("wash_iron");

                // ตรวจสอบข้อมูลก่อนใส่ dataset
                if (wash > 0) dataset.setValue("ซัก", wash);
                if (dry > 0) dataset.setValue("ซักแห้ง", dry);
                if (iron > 0) dataset.setValue("ซักและรีด", iron);
            }

            // ถ้าไม่มีข้อมูลจริงในเดือนที่เลือก ให้ใส่ Label ว่าไม่มีข้อมูล
            if (dataset.getItemCount() == 0) {
                dataset.setValue("ไม่มีข้อมูลในเดือนนี้", 0);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JFreeChart pieChart = ChartFactory.createPieChart("สัดส่วนบริการ (" + viewType + ")", dataset, true, true, false);
        PiePlot plot = (PiePlot) pieChart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setLabelGenerator(new StandardPieSectionLabelGenerator("{0}: {1} ({2})"));

        plot.setSectionPaint("ซัก", new Color(100, 149, 237));
        plot.setSectionPaint("ซักแห้ง", new Color(144, 238, 144));
        plot.setSectionPaint("ซักและรีด", new Color(255, 127, 80));

        ChartPanel panel = new ChartPanel(pieChart);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        return panel;
    }

    private JPanel createRevenueBarChart(String viewType, String month) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] monthNames = {"ม.ค.", "ก.พ.", "มี.ค.", "เม.ย.", "พ.ค.", "มิ.ย.",
                "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.", "ธ.ค."};

        try {
            String sql = buildRevenueQuery(viewType, month);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                double revenue = rs.getDouble("revenue");
                int periodNum = rs.getInt("period_num");

                String periodLabel = "";
                if (viewType.equals("รายเดือน")) {
                    periodLabel = monthNames[periodNum - 1];
                } else if (viewType.equals("รายสัปดาห์")) {
                    periodLabel = "สัปดาห์ " + periodNum;
                } else if (viewType.equals("รายวัน")) {
                    periodLabel = "วันที่ " + periodNum;
                } else {
                    periodLabel = String.format("%02d:00 น.", periodNum);
                }
                dataset.addValue(revenue, "รายได้", periodLabel);
            }

            // *** สำคัญ: ถ้าไม่มีข้อมูล ให้ใส่ค่า 0 เพื่อล้างกราฟเก่า ***
            if (!hasData) {
                dataset.addValue(0, "รายได้", "ไม่มีข้อมูล");
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "ไม่สามารถโหลดข้อมูลกราฟ: " + e.getMessage(),
                    "ข้อผิดพลาด", JOptionPane.ERROR_MESSAGE);
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "กราฟรายได้ (" + viewType + ")",
                "ช่วงเวลา",
                "รายได้ (บาท)",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false
        );


        barChart.setBackgroundPaint(Color.WHITE);
        barChart.getTitle().setFont(new Font("Tahoma", Font.BOLD, 18));

        CategoryPlot plot = barChart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(new Color(200, 200, 200));


        org.jfree.chart.renderer.category.BarRenderer renderer =
                (org.jfree.chart.renderer.category.BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(255, 193, 7));
        renderer.setMaximumBarWidth(0.08); // ทำให้แท่งเรียวลง
        renderer.setItemMargin(0.1); // ระยะห่างระหว่างแท่ง
        renderer.setDrawBarOutline(true);
        renderer.setSeriesOutlinePaint(0, new Color(230, 170, 0));
        renderer.setSeriesOutlineStroke(0, new BasicStroke(1.5f));


        plot.getDomainAxis().setLabelFont(new Font("Tahoma", Font.PLAIN, 14));
        plot.getDomainAxis().setTickLabelFont(new Font("Tahoma", Font.PLAIN, 11));
        plot.getDomainAxis().setCategoryMargin(0.1);


        org.jfree.chart.axis.NumberAxis rangeAxis = (org.jfree.chart.axis.NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabelFont(new Font("Tahoma", Font.PLAIN, 14));
        rangeAxis.setTickLabelFont(new Font("Tahoma", Font.PLAIN, 12));


        rangeAxis.setStandardTickUnits(org.jfree.chart.axis.NumberAxis.createIntegerTickUnits());


        boolean isAllZero = true;
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            Number val = dataset.getValue(0, i); // แถว 0 (รายได้)
            if (val != null && val.doubleValue() > 0) {
                isAllZero = false;
                break;
            }
        }


        if (isAllZero) {
            rangeAxis.setRange(0, 3500);
        } else {
            rangeAxis.setAutoRange(true); // ถ้ามีข้อมูลให้ปรับออโต้
        }

        ChartPanel panel = new ChartPanel(barChart);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        panel.setPreferredSize(new Dimension(1000, 400));

        return panel;
    }

    private String buildRevenueQuery(String viewType, String month) {
        int year = LocalDate.now().getYear();
        if (year > 2500) year -= 543;
        int monthNum = getMonthNumber(month);
        String baseSql = "SELECT %s as period_num, SUM(total_price) as revenue FROM laundryorder ";

        if (viewType.equals("วันนี้")) {

            return String.format(baseSql, "HOUR(booking_date)") +
                    "WHERE DATE(booking_date) = CURDATE() AND MONTH(booking_date) = " + monthNum +
                    " AND YEAR(booking_date) = " + year + " AND status != 'ยกเลิก' GROUP BY period_num";
        } else if (viewType.equals("รายเดือน")) {
            return String.format(baseSql, "MONTH(booking_date)") +
                    "WHERE YEAR(booking_date) = " + year +
                    " AND MONTH(booking_date) = " + monthNum +
                    " AND status != 'ยกเลิก' GROUP BY period_num";
        } else {
            String field = viewType.equals("รายวัน") ? "DAY(booking_date)" : "FLOOR((DAY(booking_date)-1)/7)+1";
            return String.format(baseSql, field) + "WHERE YEAR(booking_date) = " + year +
                    " AND MONTH(booking_date) = " + monthNum + " AND status != 'ยกเลิก' GROUP BY period_num ORDER BY period_num";
        }
    }


    private String[] getMonths() {
        return new String[]{"มกราคม", "กุมภาพันธ์", "มีนาคม", "เมษายน", "พฤษภาคม",
                "มิถุนายน", "กรกฎาคม", "สิงหาคม", "กันยายน", "ตุลาคม", "พฤศจิกายน", "ธันวาคม"};
    }

    private String getCurrentMonth() {
        String[] months = getMonths();
        return months[LocalDate.now().getMonthValue() - 1];
    }

    private int getMonthNumber(String month) {
        String[] months = getMonths();
        for (int i = 0; i < months.length; i++) {
            if (months[i].equals(month)) {
                return i + 1;
            }
        }
        return 1;
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

            // ตั้งค่าภาษาไทยให้กับ Dialog ต่างๆ
            UIManager.put("OptionPane.messageFont", thaiFont);
            UIManager.put("OptionPane.buttonFont", thaiFont);
            UIManager.put("OptionPane.okButtonText", "ตกลง");
            UIManager.put("OptionPane.yesButtonText", "ใช่");
            UIManager.put("OptionPane.noButtonText", "ไม่");
            UIManager.put("OptionPane.cancelButtonText", "ยกเลิก");

            //  ตั้งค่าภาษาไทยให้กับ FileChooser
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
            LaundryDashboard dashboard = new LaundryDashboard();
            dashboard.setLocationRelativeTo(null);
            dashboard.setVisible(true);
        });
    }
}
