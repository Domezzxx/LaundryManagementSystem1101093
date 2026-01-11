import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TermsAndConditionsFrame extends JFrame {

    public TermsAndConditionsFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("ข้อกำหนดและเงื่อนไข - ปลายฟ้า LAUNDRY");
        setSize(600, 750); 
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null); 
        setLayout(new BorderLayout());


        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
        headerPanel.setBackground(new Color(0, 204, 204));

        JLabel titleLabel = new JLabel("📝 ข้อกำหนดและเงื่อนไข");
        titleLabel.setFont(new Font("Tahoma", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);

        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);


        JEditorPane editorPane = new JEditorPane();
        editorPane.setContentType("text/html");
        editorPane.setText(getTermsHtml());
        editorPane.setEditable(false);
        editorPane.setBackground(Color.WHITE);
        editorPane.setCaretPosition(0); 


        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); 
        add(scrollPane, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 245, 250));
        footerPanel.setBorder(new EmptyBorder(15, 0, 15, 0));

        JButton closeButton = new JButton("รับทราบ");
        closeButton.setFont(new Font("Tahoma", Font.BOLD, 16));
        closeButton.setBackground(new Color(0, 204, 204));
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setPreferredSize(new Dimension(150, 40));
        closeButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        closeButton.addActionListener(e -> this.dispose()); 
        footerPanel.add(closeButton);
        add(footerPanel, BorderLayout.SOUTH);
    }


    private String getTermsHtml() {
        return "<html>" +
                "<body style='font-family: Tahoma, sans-serif; padding: 20px; background-color: #FFFFFF;'>" +
                "<div style='color: #444444;'>" +

               
                "<h3 style='color: #008B8B;'>1. การยอมรับเงื่อนไข</h3>" +
                "<p>การใช้งานแอปพลิเคชันนี้ถือว่าผู้ใช้ได้อ่านทำความเข้าใจและยอมรับข้อกำหนดและเงื่อนไขทั้งหมดโดยสมบูรณ์" +
                "หากไม่ยอมรับ โปรดหยุดการใช้งานทันที</p>" +
                "<br>" +

                "<h3 style='color: #008B8B;'>2. การให้บริการ</h3>" +
                "<p>แอปพลิเคชันนี้ให้บริการซักอบรีดและพับผ้า โดยผู้ใช้สามารถจองคิว เลือกบริการ" +
                "และตรวจสอบสถานะผ้าของตนผ่านระบบได้</p>" +
                "<br>" +

               
                "<h3 style='color: #008B8B;'>3. การรับ–ส่งผ้า</h3>" +
                "<p>ผู้ใช้ต้องตรวจสอบจำนวนและสภาพผ้าก่อนส่งให้ร้านทุกครั้ง ร้านจะไม่รับผิดชอบต่อของมีค่า" +
                "หรือสิ่งของอื่นที่ลืมไว้ในเสื้อผ้า ระยะเวลาการซัก อบ รีด อาจเปลี่ยนแปลงได้ตามปริมาณงานและสภาพอากาศ</p>" +
                "<br>" +

              
                "<h3 style='color: #008B8B;'>4. ความรับผิดชอบของร้านและผู้ใช้</h3>" +
                "<p>ร้านจะพยายามดูแลผ้าของลูกค้าอย่างดีที่สุด แต่ไม่รับผิดชอบในกรณีผ้าเสียหายจากคุณสมบัติของผ้าเอง" +
                "(เช่น สีตก ผ้ายืดหด หรือผ้าเก่า) ผู้ใช้ต้องให้ข้อมูลการติดต่อที่ถูกต้อง และมาติดต่อรับผ้าภายในระยะเวลาที่กำหนด</p>" +
                "<br>" +

                
                "<h3 style='color: #008B8B;'>5. การชำระเงินและการยกเลิก</h3>" +
                "<p>การชำระค่าบริการต้องทำผ่านช่องทางที่ระบุในแอป กรณียกเลิกคำสั่งซักก่อนร้านเริ่มดำเนินการ" +
                "จะได้รับเงินคืนตามนโยบายของร้าน หากร้านได้เริ่มซักแล้ว จะไม่สามารถขอคืนเงินได้</p>" +
                "<br>" +

       
                "<h3 style='color: #008B8B;'>6. ข้อจำกัดความรับผิดชอบ</h3>" +
                "<p>ผู้พัฒนาแอปไม่รับผิดชอบต่อความเสียหายที่เกิดจากการใช้งานแอป เช่น ระบบล่ม หรือข้อมูลผิดพลาด</p>" +
                "<br>" +
                "<hr>" +
                "<p style='text-align: center; color: #888888; font-size: 10px;'>ปลายฟ้า LAUNDRY Service Terms v1.0</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    public static void main(String[] args) {

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ex) {}
        SwingUtilities.invokeLater(() -> new TermsAndConditionsFrame().setVisible(true));
    }

}
