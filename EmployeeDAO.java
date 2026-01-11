import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {


    public List<Employee> getAllEmployees() {
        List<Employee> employees = new ArrayList<>();
        String sql = "SELECT * FROM employee ORDER BY employee_id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getString("employee_id"));
                emp.setName(rs.getString("name")); // ใช้คอลัมน์ name ตามโครงสร้างเดิม
                emp.setUsername(rs.getString("username"));
                emp.setPassword(rs.getString("password"));
                emp.setRole(rs.getString("role"));
                employees.add(emp);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error loading employees: " + e.getMessage());
        }
        return employees;
    }

    public List<Employee> searchEmployees(String keyword) {
        List<Employee> employees = new ArrayList<>();
        // แก้ไข SQL: ตัดเงื่อนไขนามสกุล/เบอร์โทร และลบเครื่องหมายคอมม่าที่เกินออก
        String sql = "SELECT * FROM employee WHERE " +
                "employee_id LIKE ? OR " +
                "name LIKE ? OR " +
                "username LIKE ? " +
                "ORDER BY employee_id";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setString(2, searchPattern);
            pstmt.setString(3, searchPattern);

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getString("employee_id"));
                emp.setName(rs.getString("name"));
                emp.setUsername(rs.getString("username"));
                emp.setPassword(rs.getString("password"));
                emp.setRole(rs.getString("role"));
                employees.add(emp);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error searching employees: " + e.getMessage());
        }
        return employees;
    }


    public Employee getEmployeeById(String employeeId) {
        String sql = "SELECT * FROM employee WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getString("employee_id"));
                emp.setName(rs.getString("name"));
                emp.setUsername(rs.getString("username"));
                emp.setPassword(rs.getString("password"));
                emp.setRole(rs.getString("role"));
                return emp;
            }

        } catch (SQLException e) {
            System.err.println("❌ Error getting employee: " + e.getMessage());
        }
        return null;
    }


    public boolean addEmployee(Employee employee) {

        String sql = "INSERT INTO employee (employee_id, name, username, password, role) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employee.getEmployeeId());
            pstmt.setString(2, employee.getName());
            pstmt.setString(3, employee.getUsername());
            pstmt.setString(4, employee.getPassword());
            pstmt.setString(5, employee.getRole());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error adding employee: " + e.getMessage());
            return false;
        }
    }


    public boolean updateEmployee(Employee employee) {
        // ปรับ SQL: ตัดการอัปเดต LastName และ PhoneNumber ออก
        String sql = "UPDATE employee SET " +
                "name = ?, " +
                "username = ?, " +
                "password = ?, " +
                "role = ? " +
                "WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employee.getName());
            pstmt.setString(2, employee.getUsername());
            pstmt.setString(3, employee.getPassword());
            pstmt.setString(4, employee.getRole());
            pstmt.setString(5, employee.getEmployeeId());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error updating employee: " + e.getMessage());
            return false;
        }
    }


    public boolean deleteEmployee(String employeeId) {
        String sql = "DELETE FROM employee WHERE employee_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, employeeId);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("❌ Error deleting employee: " + e.getMessage());
            return false;
        }
    }


    public String generateNextEmployeeId() {
        String sql = "SELECT employee_id FROM employee ORDER BY employee_id DESC LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                String lastId = rs.getString("employee_id");
                // คาดหวังรูปแบบ EMP-001
                if (lastId != null && lastId.startsWith("EMP-")) {
                    int number = Integer.parseInt(lastId.substring(4));
                    return String.format("EMP-%03d", number + 1);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Error generating employee ID: " + e.getMessage());
        }
        return "EMP-001";
    }


    public int getTotalEmployees() {
        String sql = "SELECT COUNT(*) as total FROM employee";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error counting employees: " + e.getMessage());
        }
        return 0;
    }
}