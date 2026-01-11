import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;

class Product {
    private int id;             
    private String name;
    private int price;
    private boolean visible;
    private String imagePath;

    public Product(int id, String name, int price, boolean visible, String imagePath) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.visible = visible;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}

public class ProductManagement extends JFrame {
    private ArrayList<Product> products;
    private JPanel productPanel;
    private ServiceFrame serviceFrame;


    private final String DB_URL = "jdbc:mysql://localhost:3306/laundry_db";
    private final String DB_USER = "root"; 
    private final String DB_PASS = ""; 

    public ProductManagement() {
        setTitle("จัดการข้อมูลบริการสินค้า - ปลายฟ้า LAUNDRY");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());


        new File("product_images").mkdir();

        products = new ArrayList<>();
        loadProductsFromDB();

        serviceFrame = new ServiceFrame();


        JPanel sidebar = createSidebar();


        JPanel mainPanel = new JPanel(new BorderLayout(0, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = createProductList();
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = createBottomPanel();
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(sidebar, BorderLayout.WEST);
        add(mainPanel, BorderLayout.CENTER);

        updateServiceFrame();
    }


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private void loadProductsFromDB() {
        products.clear();
        String sql = "SELECT * FROM products ORDER BY created_at DESC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                products.add(new Product(
                        rs.getInt("product_id"),
                        rs.getString("name"),
                        rs.getInt("price"),
                        rs.getBoolean("visible"),
                        rs.getString("image_path")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "ไม่สามารถเชื่อมต่อฐานข้อมูลได้: " + e.getMessage());
        }
    }

    private boolean addProductToDB(String name, int price, File imageFile) {
        String insertSql = "INSERT INTO products (name, price, visible, image_path) VALUES (?, ?, ?, ?)";
        String savedPath = null;


        if (imageFile != null) {
            try {

                String fileName = System.currentTimeMillis() + "_" + imageFile.getName();
                File destFile = new File("product_images/" + fileName);
                Files.copy(imageFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                savedPath = destFile.getPath(); // เก็บ path นี้ลง DB
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setBoolean(3, true);
            pstmt.setString(4, savedPath);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateProductVisibilityDB(int id, boolean visible) {
        String sql = "UPDATE products SET visible = ? WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, visible);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateProductDetailsDB(int id, String name, int price) {
        String sql = "UPDATE products SET name = ?, price = ? WHERE product_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setInt(2, price);
            pstmt.setInt(3, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean deleteProductFromDB(int productId) {
        String sql = "DELETE FROM products WHERE product_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, productId);
            return pstmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    class ModernToggleButton extends JToggleButton {
        public ModernToggleButton(boolean selected) {
            setSelected(selected);
            setPreferredSize(new Dimension(65, 30));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(230, 230, 230));
            g2.fillRoundRect(0, 5, 60, 22, 22, 22);

            if (isSelected()) {
                g2.setColor(new Color(135, 206, 250));
                g2.fillOval(32, 1, 28, 28);
            } else {
                g2.setColor(new Color(40, 40, 40));
                g2.fillOval(0, 1, 28, 28);
            }
            g2.dispose();
        }
    }

    class AddProductDialog extends JDialog {
        private JLabel imgLabel;
        private File selectedImageFile = null;

        public AddProductDialog(JFrame parent) {
            super(parent, "เพิ่มสินค้า", true);
            setSize(850, 500);
            setLocationRelativeTo(parent);
            setResizable(false);

            JPanel container = new JPanel(null);
            container.setBackground(new Color(242, 243, 245));
            setContentPane(container);


            JPanel imgPlaceholder = new JPanel(new BorderLayout());
            imgPlaceholder.setBounds(40, 40, 130, 130);
            imgPlaceholder.setBackground(new Color(224, 226, 230));
            imgPlaceholder.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            imgLabel = new JLabel("📦", SwingConstants.CENTER);
            imgLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
            imgPlaceholder.add(imgLabel, BorderLayout.CENTER);
            container.add(imgPlaceholder);


            JButton btnAddImg = new JButton("📷+ เพิ่มรูปภาพ");
            btnAddImg.setBounds(180, 50, 150, 30);
            btnAddImg.setFont(new Font("Tahoma", Font.BOLD, 15));
            btnAddImg.setForeground(new Color(65, 105, 225));
            btnAddImg.setContentAreaFilled(false);
            btnAddImg.setBorderPainted(false);
            btnAddImg.setCursor(new Cursor(Cursor.HAND_CURSOR));

            btnAddImg.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new FileNameExtensionFilter("Images", "jpg", "png", "gif", "jpeg"));
                if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                    selectedImageFile = chooser.getSelectedFile();
                    ImageIcon icon = new ImageIcon(selectedImageFile.getAbsolutePath());
                    Image img = icon.getImage().getScaledInstance(130, 130, Image.SCALE_SMOOTH);
                    imgLabel.setIcon(new ImageIcon(img));
                    imgLabel.setText("");
                }
            });
            container.add(btnAddImg);


            JLabel lblNameTitle = new JLabel("ชื่อสินค้า");
            lblNameTitle.setBounds(190, 100, 100, 20);
            lblNameTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
            lblNameTitle.setForeground(Color.GRAY);
            container.add(lblNameTitle);

            JTextField txtName = new JTextField();
            txtName.setBounds(190, 130, 400, 30);
            txtName.setFont(new Font("Tahoma", Font.PLAIN, 15));
            txtName.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
            txtName.setBackground(new Color(242, 243, 245));
            container.add(txtName);


            JPanel priceCard = new JPanel(null);
            priceCard.setBounds(185, 190, 380, 180);
            priceCard.setBackground(Color.WHITE);
            priceCard.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

            JLabel lblPriceSection = new JLabel("ราคา");
            lblPriceSection.setBounds(20, 15, 120, 25);
            lblPriceSection.setFont(new Font("Tahoma", Font.BOLD, 16));
            priceCard.add(lblPriceSection);

            JLabel lblWash = new JLabel("ซัก");
            lblWash.setBounds(20, 55, 80, 25);
            priceCard.add(lblWash);

            JTextField txtWash = new JTextField("40");
            txtWash.setBounds(160, 55, 180, 25);
            txtWash.setHorizontalAlignment(JTextField.RIGHT);
            txtWash.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            priceCard.add(txtWash);

            JLabel lblDry = new JLabel("ซักแห้ง");
            lblDry.setBounds(20, 90, 80, 25);
            priceCard.add(lblDry);

            JTextField txtDry = new JTextField("40");
            txtDry.setBounds(160, 90, 180, 25);
            txtDry.setHorizontalAlignment(JTextField.RIGHT);
            txtDry.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            priceCard.add(txtDry);

            JLabel lblIron = new JLabel("ซักรีด");
            lblIron.setBounds(20, 125, 80, 25);
            priceCard.add(lblIron);

            JTextField txtIron = new JTextField("40");
            txtIron.setBounds(160, 125, 180, 25);
            txtIron.setHorizontalAlignment(JTextField.RIGHT);
            txtIron.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
            priceCard.add(txtIron);

            container.add(priceCard);


            JButton btnSubmit = new JButton("เพิ่มสินค้า");
            btnSubmit.setBounds(650, 390, 160, 45);
            btnSubmit.setBackground(new Color(45, 80, 185));
            btnSubmit.setForeground(Color.BLACK);
            btnSubmit.setFont(new Font("Tahoma", Font.BOLD, 16));
            btnSubmit.addActionListener(e -> {
                if (!txtName.getText().isEmpty()) {
                    int price;
                    try {
                        price = Integer.parseInt(txtWash.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "กรุณากรอกราคาเป็นตัวเลข");
                        return;
                    }

                    if (addProductToDB(txtName.getText(), price, selectedImageFile)) {
                        JOptionPane.showMessageDialog(this, "เพิ่มข้อมูลเรียบร้อย");
                        loadProductsFromDB();
                        refreshProductList();
                        updateServiceFrame();
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(this, "เกิดข้อผิดพลาดในการบันทึก");
                    }
                }
            });
            container.add(btnSubmit);
        }
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

    private ImageIcon createLogoIcon() {
        int size = 60;
        BufferedImage img = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = (Graphics2D) img.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(new Color(0, 180, 220));
        g2.fillOval(0, 0, size, size);
        g2.setColor(Color.WHITE);
        g2.drawOval(2, 2, size - 4, size - 4);
        g2.dispose();
        return new ImageIcon(img);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        JLabel title = new JLabel("จัดการข้อมูลบริการสินค้า");
        title.setFont(new Font("Tahoma", Font.BOLD, 24));
        header.add(title, BorderLayout.WEST);
        return header;
    }

    private void refreshProductList() {
        productPanel.removeAll();
        for (Product product : products) {
            productPanel.add(createProductItem(product));
            productPanel.add(Box.createVerticalStrut(10));
        }
        productPanel.revalidate();
        productPanel.repaint();
    }

    private JScrollPane createProductList() {
        productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(Color.WHITE);

        refreshProductList();

        JScrollPane scrollPane = new JScrollPane(productPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        return scrollPane;
    }

    private JPanel createProductItem(Product product) {
        JPanel item = new JPanel(new BorderLayout(15, 10));
        item.setBackground(Color.WHITE);
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
                new EmptyBorder(15, 20, 15, 20)
        ));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(Color.WHITE);

        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setPreferredSize(new Dimension(65, 65));
        iconPanel.setBackground(new Color(240, 248, 255));
        iconPanel.setBorder(new LineBorder(new Color(0, 204, 204), 2, true));

        JLabel iconView = new JLabel();
        if (product.getImagePath() != null && !product.getImagePath().isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(product.getImagePath());
                if (originalIcon.getIconWidth() > 0) {
                    Image img = originalIcon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                    iconView.setIcon(new ImageIcon(img));
                } else {
                    iconView.setText(getProductIcon(product.getName()));
                    iconView.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
                }
            } catch (Exception e) {
                iconView.setText("📦");
            }
        } else {
            iconView.setText(getProductIcon(product.getName()));
            iconView.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));
        }
        iconView.setHorizontalAlignment(SwingConstants.CENTER);
        iconPanel.add(iconView);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(Color.WHITE);
        JLabel nameLabel = new JLabel(product.getName());
        nameLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        JLabel priceLabel = new JLabel(product.getPrice() + " บาท");
        priceLabel.setForeground(new Color(0, 180, 180));
        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);

        leftPanel.add(iconPanel);
        leftPanel.add(infoPanel);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        rightPanel.setBackground(Color.WHITE);

        JLabel statusLabel = new JLabel(product.isVisible() ? "แสดง" : "ซ่อน");
        ProductManagement.ModernToggleButton toggleBtn = new ProductManagement.ModernToggleButton(product.isVisible());
        toggleBtn.addActionListener(e -> {
            boolean newState = toggleBtn.isSelected();
            product.setVisible(newState);
            updateProductVisibilityDB(product.getId(), newState); // อัปเดต DB
            statusLabel.setText(newState ? "แสดง" : "ซ่อน");
            updateServiceFrame();
        });


        JButton editBtn = new JButton("✏️");
        editBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        editBtn.setContentAreaFilled(false);
        editBtn.setBorderPainted(false);
        editBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        editBtn.addActionListener(e -> editProductDialog(product, nameLabel, priceLabel));


        JButton deleteBtn = new JButton("🗑️");
        deleteBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        deleteBtn.setContentAreaFilled(false);
        deleteBtn.setBorderPainted(false);
        deleteBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "คุณต้องการลบสินค้า \"" + product.getName() + "\" หรือไม่?",
                    "ยืนยันการลบ",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                if (deleteProductFromDB(product.getId())) {
                    products.remove(product);   // ลบจาก ArrayList
                    refreshProductList();       // รีเฟรช UI
                    updateServiceFrame();       // อัปเดตหน้าบริการ
                    JOptionPane.showMessageDialog(this, "ลบข้อมูลเรียบร้อย");
                } else {
                    JOptionPane.showMessageDialog(this, "ไม่สามารถลบข้อมูลได้");
                }
            }
        });

        rightPanel.add(statusLabel);
        rightPanel.add(toggleBtn);
        rightPanel.add(editBtn);
        rightPanel.add(deleteBtn);

        item.add(leftPanel, BorderLayout.WEST);
        item.add(rightPanel, BorderLayout.EAST);
        return item;
    }

    private void editProductDialog(Product product, JLabel nameLabel, JLabel priceLabel) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        JTextField nameField = new JTextField(product.getName());
        JTextField priceField = new JTextField(String.valueOf(product.getPrice()));
        panel.add(new JLabel("ชื่อสินค้า:"));
        panel.add(nameField);
        panel.add(new JLabel("ราคา:"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "แก้ไขสินค้า", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String newName = nameField.getText();
                int newPrice = Integer.parseInt(priceField.getText());


                updateProductDetailsDB(product.getId(), newName, newPrice);


                product.setName(newName);
                product.setPrice(newPrice);
                nameLabel.setText(newName);
                priceLabel.setText(newPrice + " บาท");
                updateServiceFrame();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "กรุณากรอกราคาเป็นตัวเลข");
            }
        }
    }

    private String getProductIcon(String name) {
        if (name.contains("เสื้อ")) return "👕";
        if (name.contains("กางเกง")) return "👖";
        return "🛏️";
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panel.setBackground(Color.WHITE);
        JButton btnAdd = new JButton("+ เพิ่มสินค้า");
        btnAdd.setBackground(new Color(0, 204, 204));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFont(new Font("Tahoma", Font.BOLD, 14));
        btnAdd.setPreferredSize(new Dimension(180, 45));
        btnAdd.setBorderPainted(false);
        btnAdd.addActionListener(e -> new AddProductDialog(this).setVisible(true));
        panel.add(btnAdd);
        return panel;
    }

    private void updateServiceFrame() {
        if (serviceFrame != null) serviceFrame.updateProducts(products);
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

            //  ตั้งค่าภาษาไทยให้กับ Dialog ต่างๆ
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
            new ProductManagement().setVisible(true);
        });
    }
}


class ServiceFrame extends JFrame {
    private JPanel productPanel;
    public ServiceFrame() {
        setTitle("รายการบริการ");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel title = new JLabel("รายการบริการที่เปิดใช้งาน", SwingConstants.CENTER);
        title.setFont(new Font("Tahoma", Font.BOLD, 20));
        title.setForeground(new Color(0, 180, 180));
        title.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        productPanel = new JPanel();
        productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
        productPanel.setBackground(Color.WHITE);
        productPanel.setBorder(new EmptyBorder(10, 20, 10, 20));
        add(new JScrollPane(productPanel), BorderLayout.CENTER);
    }

    public void updateProducts(ArrayList<Product> products) {
        productPanel.removeAll();
        for (Product product : products) {
            if (product.isVisible()) {
                JPanel item = new JPanel(new BorderLayout());
                item.setBackground(new Color(240, 252, 255));
                item.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0, 204, 204), 2, true), new EmptyBorder(15, 20, 15, 20)));
                item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

                JLabel nameLabel = new JLabel(product.getName());
                nameLabel.setFont(new Font("Tahoma", Font.BOLD, 14));
                item.add(nameLabel, BorderLayout.WEST);

                JLabel price = new JLabel(product.getPrice() + " บาท");
                price.setFont(new Font("Tahoma", Font.BOLD, 14));
                price.setForeground(new Color(0, 180, 180));
                item.add(price, BorderLayout.EAST);

                productPanel.add(item);
                productPanel.add(Box.createVerticalStrut(10));
            }
        }
        productPanel.revalidate();
        productPanel.repaint();
    }

}
