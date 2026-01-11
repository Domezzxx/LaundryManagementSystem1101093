public class Employee {
    private String employeeId;
    private String name; // เปลี่ยนจาก firstName เป็น name
    private String username;
    private String password;
    private String role;


    public Employee() {
    }


    public Employee(String employeeId, String name, String username,
                    String password, String role) {
        this.employeeId = employeeId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }


    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "employeeId='" + employeeId + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}