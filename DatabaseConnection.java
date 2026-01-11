import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {

    private static final String HOST = "localhost";
    private static final String PORT = "3306";
    private static final String DATABASE = "laundry_db";
    private static final String URL = "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE;
    private static final String USER = "root"; // เปลี่ยนตามของคุณ
    private static final String PASSWORD = "DomeDome55&55"; // เปลี่ยนตามของคุณ


    private static Connection connection = null;



    public static Connection getConnection() throws SQLException {
        try {

            Class.forName("com.mysql.cj.jdbc.Driver");


            String fullUrl = URL +
                    "?useSSL=false" +                          // ปิด SSL (สำหรับ localhost)
                    "&allowPublicKeyRetrieval=true" +          // อนุญาตการดึง public key
                    "&serverTimezone=Asia/Bangkok" +           // ตั้งค่า timezone
                    "&useUnicode=true" +                       // รองรับ Unicode
                    "&characterEncoding=UTF-8";                // เข้ารหัส UTF-8

            return DriverManager.getConnection(fullUrl, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            throw new SQLException("❌ MySQL JDBC Driver not found! กรุณาเพิ่ม mysql-connector-java.jar", e);
        } catch (SQLException e) {
            throw new SQLException("❌ ไม่สามารถเชื่อมต่อฐานข้อมูล: " + e.getMessage(), e);
        }
    }


    public static Connection getSingletonConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = getConnection();
        }
        return connection;
    }


    // ทดสอบการเชื่อมต่อ


    public static boolean testConnection() {
        System.out.println("\n========================================");
        System.out.println("  ทดสอบการเชื่อมต่อฐานข้อมูล");
        System.out.println("========================================");
        System.out.println("Host:     " + HOST);
        System.out.println("Port:     " + PORT);
        System.out.println("Database: " + DATABASE);
        System.out.println("User:     " + USER);
        System.out.println("----------------------------------------");

        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ เชื่อมต่อฐานข้อมูลสำเร็จ!");
                System.out.println("📊 Database: " + conn.getCatalog());
                System.out.println("🔗 Connection: " + conn.getClass().getName());
                System.out.println("========================================\n");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("❌ ไม่สามารถเชื่อมต่อฐานข้อมูล!");
            System.err.println("📝 Error Message: " + e.getMessage());
            System.err.println("🔍 SQL State: " + e.getSQLState());
            System.err.println("🔢 Error Code: " + e.getErrorCode());
            System.err.println("\n💡 แนะนำการแก้ไข:");
            System.err.println("   1. ตรวจสอบว่า MySQL Server เปิดอยู่หรือไม่");
            System.err.println("   2. ตรวจสอบ username และ password");
            System.err.println("   3. ตรวจสอบว่าฐานข้อมูล '" + DATABASE + "' มีอยู่จริง");
            System.err.println("   4. ตรวจสอบว่าได้เพิ่ม mysql-connector-java.jar แล้ว");
            System.err.println("========================================\n");
            e.printStackTrace();
        }
        return false;
    }


    // ปิด Connection


    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                if (!conn.isClosed()) {
                    conn.close();
                    System.out.println("🔒 ปิด Connection สำเร็จ");
                }
            } catch (SQLException e) {
                System.err.println("❌ เกิดข้อผิดพลาดในการปิด Connection");
                e.printStackTrace();
            }
        }
    }


    // ตรวจสอบว่าฐานข้อมูลมีอยู่หรือไม่


    public static boolean isDatabaseExists() {
        String urlWithoutDB = "jdbc:mysql://" + HOST + ":" + PORT +
                "?useSSL=false&allowPublicKeyRetrieval=true";

        try (Connection conn = DriverManager.getConnection(urlWithoutDB, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            String query = "SELECT SCHEMA_NAME FROM INFORMATION_SCHEMA.SCHEMATA " +
                    "WHERE SCHEMA_NAME = '" + DATABASE + "'";
            var rs = stmt.executeQuery(query);

            return rs.next();

        } catch (SQLException e) {
            System.err.println("❌ ไม่สามารถตรวจสอบฐานข้อมูล: " + e.getMessage());
            return false;
        }
    }


    // สร้างฐานข้อมูลอัตโนมัติ


    public static boolean createDatabaseIfNotExists() {
        if (isDatabaseExists()) {
            System.out.println("✅ ฐานข้อมูล '" + DATABASE + "' มีอยู่แล้ว");
            return true;
        }

        String urlWithoutDB = "jdbc:mysql://" + HOST + ":" + PORT +
                "?useSSL=false&allowPublicKeyRetrieval=true";

        try (Connection conn = DriverManager.getConnection(urlWithoutDB, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createDB = "CREATE DATABASE " + DATABASE +
                    " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.executeUpdate(createDB);

            System.out.println("✅ สร้างฐานข้อมูล '" + DATABASE + "' สำเร็จ!");
            return true;

        } catch (SQLException e) {
            System.err.println("❌ ไม่สามารถสร้างฐานข้อมูล: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // ตรวจสอบตารางในฐานข้อมูล


    public static void showTables() {
        System.out.println("\n========================================");
        System.out.println("  ตารางในฐานข้อมูล " + DATABASE);
        System.out.println("========================================");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            var rs = stmt.executeQuery("SHOW TABLES");
            int count = 0;

            while (rs.next()) {
                count++;
                System.out.println("📋 " + count + ". " + rs.getString(1));
            }

            if (count == 0) {
                System.out.println("⚠️  ไม่มีตารางในฐานข้อมูล");
            } else {
                System.out.println("----------------------------------------");
                System.out.println("✅ พบ " + count + " ตาราง");
            }

            System.out.println("========================================\n");

        } catch (SQLException e) {
            System.err.println("❌ ไม่สามารถดึงรายการตาราง: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // ดูข้อมูลในตาราง

    public static void showTableData(String tableName) {
        System.out.println("\n========================================");
        System.out.println("  ข้อมูลในตาราง: " + tableName);
        System.out.println("========================================");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            var rs = stmt.executeQuery("SELECT * FROM " + tableName + " LIMIT 10");
            var metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // แสดง Header
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println("\n" + "----------------------------------------");

            // แสดงข้อมูล
            int rowCount = 0;
            while (rs.next()) {
                rowCount++;
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }

            if (rowCount == 0) {
                System.out.println("⚠️  ไม่มีข้อมูลในตาราง");
            } else {
                System.out.println("----------------------------------------");
                System.out.println("✅ แสดง " + rowCount + " แถว");
            }

            System.out.println("========================================\n");

        } catch (SQLException e) {
            System.err.println("❌ ไม่สามารถดึงข้อมูล: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Main Method สำหรับทดสอบ

    public static void main(String[] args) {
        // ทดสอบการเชื่อมต่อ
        if (testConnection()) {

            showTables();


            showTableData("laundryorder");
            showTableData("customer");
            showTableData("order_status_history");
        }
    }
}