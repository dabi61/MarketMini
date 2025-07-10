/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.DBConnection;
import model.Salary;

/**
 *
 * @author macbook
 */
public class SalaryDAO {
    private Connection conn;

    public SalaryDAO() {
        try {
            this.conn = DBConnection.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public SalaryDAO(Connection con) {
        this.conn = con;
    }
    
    /**
     * Lấy connection
     * @return Connection
     */
    public Connection getConnection() {
        return this.conn;
    }
    
    /**
     * Thêm lương mới
     * @param salary đối tượng Salary cần thêm
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean addSalary(Salary salary) {
        String sql = "INSERT INTO salary (employee_id, total_hours, hourly_wage, bonus, payment_date, created_date, penalty_deduction) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salary.getEmployee_id());
            stmt.setBigDecimal(2, salary.getTotal_hours());
            stmt.setInt(3, salary.getHourly_wage());
            stmt.setInt(4, salary.getBonus());
            stmt.setDate(5, new java.sql.Date(salary.getPayment_date().getTime()));
            stmt.setDate(6, new java.sql.Date(salary.getCreated_date().getTime()));
            stmt.setInt(7, salary.getPenalty_deduction());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật lương
     * @param salary đối tượng Salary cần cập nhật
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean updateSalary(Salary salary) {
        String sql = "UPDATE salary SET employee_id=?, total_hours=?, hourly_wage=?, bonus=?, payment_date=?, penalty_deduction=? WHERE salary_id=?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salary.getEmployee_id());
            stmt.setBigDecimal(2, salary.getTotal_hours());
            stmt.setInt(3, salary.getHourly_wage());
            stmt.setInt(4, salary.getBonus());
            stmt.setDate(5, new java.sql.Date(salary.getPayment_date().getTime()));
            stmt.setInt(6, salary.getPenalty_deduction());
            stmt.setInt(7, salary.getSalary_id());
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Xóa lương theo ID
     * @param salaryId ID lương cần xóa
     * @return true nếu thành công, false nếu thất bại
     */
    public boolean deleteSalary(int salaryId) {
        String sql = "DELETE FROM salary WHERE salary_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaryId);
            
            int result = stmt.executeUpdate();
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Lấy lương theo ID
     * @param salaryId ID lương cần lấy
     * @return đối tượng Salary hoặc null nếu không tìm thấy
     */
    public Salary getSalaryById(int salaryId) {
        String sql = "SELECT * FROM salary WHERE salary_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, salaryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSalary(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Lấy tất cả lương
     * @return danh sách tất cả lương
     */
    public List<Salary> getAllSalaries() {
        List<Salary> salaries = new ArrayList<>();
        String sql = "SELECT * FROM salary ORDER BY created_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                salaries.add(mapResultSetToSalary(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaries;
    }
    
    /**
     * Lấy lương theo nhân viên
     * @param employeeId ID nhân viên
     * @return danh sách lương của nhân viên
     */
    public List<Salary> getSalariesByEmployee(int employeeId) {
        List<Salary> salaries = new ArrayList<>();
        String sql = "SELECT * FROM salary WHERE employee_id = ? ORDER BY created_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                salaries.add(mapResultSetToSalary(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaries;
    }
    
    /**
     * Load danh sách lương vào table
     * @param table Table để hiển thị
     */
    public void loadSalaryTable(JTable table) {
        // Kiểm tra xem các trường mới có tồn tại không
        String sql = "SELECT s.salary_id, e.full_name, s.total_hours, s.hourly_wage, " +
                    "s.bonus, s.payment_date, s.created_date, s.overtime_pay, " +
                    "s.gross_salary, s.penalty_deduction, s.net_salary " +
                    "FROM salary s " +
                    "JOIN employees e ON s.employee_id = e.employee_id " +
                    "ORDER BY s.created_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            DefaultTableModel model = new DefaultTableModel();
            model.addColumn("ID");
            model.addColumn("Tên nhân viên");
            model.addColumn("Tổng giờ");
            model.addColumn("Lương/giờ");
            model.addColumn("Thưởng");
            model.addColumn("Overtime Pay");
            model.addColumn("Gross Salary");
            model.addColumn("Penalty");
            model.addColumn("Net Salary");
            model.addColumn("Ngày thanh toán");
            model.addColumn("Ngày tạo");
            
            while (rs.next()) {
                Object[] row = {
                    rs.getObject("salary_id") != null ? rs.getInt("salary_id") : 0,
                    rs.getString("full_name") != null ? rs.getString("full_name") : "Không xác định",
                    rs.getObject("total_hours") != null ? rs.getBigDecimal("total_hours") : new java.math.BigDecimal("0"),
                    rs.getObject("hourly_wage") != null ? rs.getInt("hourly_wage") : 0,
                    rs.getObject("bonus") != null ? rs.getInt("bonus") : 0,
                    rs.getObject("overtime_pay") != null ? rs.getBigDecimal("overtime_pay") : new java.math.BigDecimal("0"),
                    rs.getObject("gross_salary") != null ? rs.getBigDecimal("gross_salary") : new java.math.BigDecimal("0"),
                    rs.getObject("penalty_deduction") != null ? rs.getInt("penalty_deduction") : 0,
                    rs.getObject("net_salary") != null ? rs.getBigDecimal("net_salary") : new java.math.BigDecimal("0"),
                    rs.getDate("payment_date") != null ? rs.getDate("payment_date") : new java.sql.Date(System.currentTimeMillis()),
                    rs.getDate("created_date") != null ? rs.getDate("created_date") : new java.sql.Date(System.currentTimeMillis())
                };
                model.addRow(row);
            }
            
            table.setModel(model);
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi load salary table: " + e.getMessage());
        }
    }
    
    /**
     * Lấy danh sách nhân viên cho ComboBox
     * @return Vector chứa thông tin nhân viên
     */
    public java.util.Vector<String> getEmployeeList() {
        java.util.Vector<String> employeeList = new java.util.Vector<>();
        employeeList.add("-- Chọn nhân viên --");
        
        String sql = "SELECT employee_id, full_name FROM employees WHERE role = 2 ORDER BY full_name";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                String employeeInfo = rs.getInt("employee_id") + " - " + rs.getString("full_name");
                employeeList.add(employeeInfo);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return employeeList;
    }
    
    /**
     * Test kết nối database và dữ liệu
     */
    public void testConnection() {
        try {
            System.out.println("Đang test kết nối database...");
            
            // Test query đơn giản
            String sql = "SELECT COUNT(*) as count FROM salary";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Số lượng bản ghi trong bảng salary: " + count);
            }
            
            // Test query với JOIN
            sql = "SELECT COUNT(*) as count FROM salary s JOIN employees e ON s.employee_id = e.employee_id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt("count");
                System.out.println("Số lượng bản ghi sau JOIN: " + count);
            }
            
            System.out.println("Test kết nối thành công!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi test kết nối: " + e.getMessage());
        }
    }
    
    /**
     * Thêm dữ liệu test
     */
    public void addTestData() {
        try {
            System.out.println("Đang thêm dữ liệu test...");
            
            // Kiểm tra xem có nhân viên nào không
            String checkEmployeeSql = "SELECT COUNT(*) as count FROM employees WHERE role = 2";
            PreparedStatement checkStmt = conn.prepareStatement(checkEmployeeSql);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (checkRs.next() && checkRs.getInt("count") > 0) {
                // Lấy employee_id đầu tiên
                String getEmployeeSql = "SELECT employee_id FROM employees WHERE role = 2 LIMIT 1";
                PreparedStatement getStmt = conn.prepareStatement(getEmployeeSql);
                ResultSet getRs = getStmt.executeQuery();
                
                if (getRs.next()) {
                    int employeeId = getRs.getInt("employee_id");
                    
                    // Thêm dữ liệu test
                    String insertSql = "INSERT INTO salary (employee_id, total_hours, hourly_wage, bonus, payment_date, created_date, penalty_deduction) VALUES (?, ?, ?, ?, ?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    insertStmt.setInt(1, employeeId);
                    insertStmt.setBigDecimal(2, new java.math.BigDecimal("160"));
                    insertStmt.setInt(3, 50000);
                    insertStmt.setInt(4, 100000);
                    insertStmt.setDate(5, new java.sql.Date(System.currentTimeMillis()));
                    insertStmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
                    insertStmt.setInt(7, 0); // Penalty deduction
                    
                    int result = insertStmt.executeUpdate();
                    if (result > 0) {
                        System.out.println("Thêm dữ liệu test thành công!");
                    } else {
                        System.out.println("Thêm dữ liệu test thất bại!");
                    }
                }
            } else {
                System.out.println("Không có nhân viên nào trong database!");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi thêm dữ liệu test: " + e.getMessage());
        }
    }
    
    /**
     * Thêm dữ liệu test cho bảng employees
     */
    public void addTestEmployeeData() {
        try {
            System.out.println("Đang thêm dữ liệu test cho bảng employees...");
            
            // Kiểm tra xem có nhân viên nào không
            String checkSql = "SELECT COUNT(*) as count FROM employees WHERE role = 2";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            ResultSet checkRs = checkStmt.executeQuery();
            
            if (checkRs.next() && checkRs.getInt("count") == 0) {
                // Thêm nhân viên test
                String insertSql = "INSERT INTO employees (full_name, email, phone, address, role, created_date) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                insertStmt.setString(1, "Nguyễn Văn A");
                insertStmt.setString(2, "nguyenvana@example.com");
                insertStmt.setString(3, "0123456789");
                insertStmt.setString(4, "Hà Nội");
                insertStmt.setInt(5, 2); // role = 2 cho nhân viên
                insertStmt.setDate(6, new java.sql.Date(System.currentTimeMillis()));
                
                int result = insertStmt.executeUpdate();
                if (result > 0) {
                    System.out.println("Thêm nhân viên test thành công!");
                } else {
                    System.out.println("Thêm nhân viên test thất bại!");
                }
            } else {
                System.out.println("Đã có nhân viên trong database!");
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi thêm dữ liệu test cho employees: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra cấu trúc bảng
     */
    public void checkTableStructure() {
        try {
            System.out.println("Đang kiểm tra cấu trúc bảng...");
            
            // Kiểm tra bảng salary
            String checkSalarySql = "DESCRIBE salary";
            PreparedStatement salaryStmt = conn.prepareStatement(checkSalarySql);
            ResultSet salaryRs = salaryStmt.executeQuery();
            
            System.out.println("Cấu trúc bảng salary:");
            while (salaryRs.next()) {
                String field = salaryRs.getString("Field");
                String type = salaryRs.getString("Type");
                String null_ = salaryRs.getString("Null");
                String key = salaryRs.getString("Key");
                String default_ = salaryRs.getString("Default");
                System.out.println(field + " | " + type + " | " + null_ + " | " + key + " | " + default_);
            }
            
            // Kiểm tra bảng employees
            String checkEmployeesSql = "DESCRIBE employees";
            PreparedStatement employeesStmt = conn.prepareStatement(checkEmployeesSql);
            ResultSet employeesRs = employeesStmt.executeQuery();
            
            System.out.println("Cấu trúc bảng employees:");
            while (employeesRs.next()) {
                String field = employeesRs.getString("Field");
                String type = employeesRs.getString("Type");
                String null_ = employeesRs.getString("Null");
                String key = employeesRs.getString("Key");
                String default_ = employeesRs.getString("Default");
                System.out.println(field + " | " + type + " | " + null_ + " | " + key + " | " + default_);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi kiểm tra cấu trúc bảng: " + e.getMessage());
        }
    }
    
    /**
     * Tạo bảng salary nếu chưa tồn tại
     */
    public void createSalaryTableIfNotExists() {
        try {
            System.out.println("Đang kiểm tra và tạo bảng salary...");
            
            String createTableSql = "CREATE TABLE IF NOT EXISTS salary (" +
                "salary_id INT AUTO_INCREMENT PRIMARY KEY," +
                "employee_id INT NOT NULL," +
                "total_hours DECIMAL(10,2) DEFAULT 0," +
                "hourly_wage INT DEFAULT 0," +
                "bonus INT DEFAULT 0," +
                "payment_date DATE," +
                "created_date DATE," +
                "overtime_rate DECIMAL(5,2) DEFAULT 1.2," +
                "gross_salary INT DEFAULT 0," +
                "penalty_deduction INT DEFAULT 0," +
                "net_salary INT DEFAULT 0," +
                "FOREIGN KEY (employee_id) REFERENCES employees(employee_id)" +
                ")";
            
            PreparedStatement stmt = conn.prepareStatement(createTableSql);
            stmt.executeUpdate();
            System.out.println("Bảng salary đã được tạo hoặc đã tồn tại!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tạo bảng salary: " + e.getMessage());
        }
    }
    
    /**
     * Tạo bảng employees nếu chưa tồn tại
     */
    public void createEmployeesTableIfNotExists() {
        try {
            System.out.println("Đang kiểm tra và tạo bảng employees...");
            
            String createTableSql = "CREATE TABLE IF NOT EXISTS employees (" +
                "employee_id INT AUTO_INCREMENT PRIMARY KEY," +
                "full_name VARCHAR(255) NOT NULL," +
                "email VARCHAR(255)," +
                "phone VARCHAR(20)," +
                "address TEXT," +
                "role INT DEFAULT 2," +
                "created_date DATE," +
                "updated_date DATE" +
                ")";
            
            PreparedStatement stmt = conn.prepareStatement(createTableSql);
            stmt.executeUpdate();
            System.out.println("Bảng employees đã được tạo hoặc đã tồn tại!");
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tạo bảng employees: " + e.getMessage());
        }
    }
    
    /**
     * Kiểm tra lỗi chi tiết
     */
    public void checkDetailedErrors() {
        try {
            System.out.println("Đang kiểm tra lỗi chi tiết...");
            
            // Kiểm tra kết nối
            if (conn == null) {
                System.err.println("Connection là null!");
                return;
            }
            
            if (conn.isClosed()) {
                System.err.println("Connection đã đóng!");
                return;
            }
            
            System.out.println("Connection OK!");
            
            // Kiểm tra các bảng
            String[] tables = {"employees", "salary"};
            for (String table : tables) {
                try {
                    String checkSql = "SELECT COUNT(*) as count FROM " + table;
                    PreparedStatement stmt = conn.prepareStatement(checkSql);
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        System.out.println("Bảng " + table + " có " + rs.getInt("count") + " bản ghi");
                    }
                } catch (SQLException e) {
                    System.err.println("Lỗi khi kiểm tra bảng " + table + ": " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi kiểm tra chi tiết: " + e.getMessage());
        }
    }
    
    /**
     * Lấy dữ liệu lương chi tiết cho xuất Excel
     * @return List<Salary> với đầy đủ thông tin
     */
    public List<Salary> getDetailedSalaryData() {
        List<Salary> salaries = new ArrayList<>();
        String sql = "SELECT s.*, e.full_name " +
                    "FROM salary s " +
                    "JOIN employees e ON s.employee_id = e.employee_id " +
                    "ORDER BY s.created_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Salary salary = mapResultSetToSalary(rs);
                // Thêm tên nhân viên vào salary object nếu cần
                salaries.add(salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaries;
    }
    
    /**
     * Lấy dữ liệu lương theo nhân viên cho xuất Excel
     * @param employeeId ID nhân viên
     * @return List<Salary> với đầy đủ thông tin của nhân viên
     */
    public List<Salary> getSalaryDataByEmployee(int employeeId) {
        List<Salary> salaries = new ArrayList<>();
        String sql = "SELECT s.*, e.full_name " +
                    "FROM salary s " +
                    "JOIN employees e ON s.employee_id = e.employee_id " +
                    "WHERE s.employee_id = ? " +
                    "ORDER BY s.created_date DESC";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Salary salary = mapResultSetToSalary(rs);
                salaries.add(salary);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return salaries;
    }
    
    /**
     * Lấy tên nhân viên theo ID
     * @param employeeId ID nhân viên
     * @return tên nhân viên
     */
    public String getEmployeeNameById(int employeeId) {
        String sql = "SELECT full_name FROM employees WHERE employee_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, employeeId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("full_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Không xác định";
    }
    
    /**
     * Map ResultSet thành đối tượng Salary
     * @param rs ResultSet
     * @return đối tượng Salary
     * @throws SQLException
     */
    private Salary mapResultSetToSalary(ResultSet rs) throws SQLException {
        Salary salary = new Salary();
        
        if (rs.getObject("salary_id") != null) {
            salary.setSalary_id(rs.getInt("salary_id"));
        } else {
            salary.setSalary_id(0);
        }
        
        if (rs.getObject("employee_id") != null) {
            salary.setEmployee_id(rs.getInt("employee_id"));
        } else {
            salary.setEmployee_id(0);
        }
        
        // Xử lý NULL values cho các cột cơ bản
        if (rs.getObject("total_hours") != null) {
            salary.setTotal_hours(rs.getBigDecimal("total_hours"));
        } else {
            salary.setTotal_hours(new java.math.BigDecimal("0"));
        }
        
        if (rs.getObject("hourly_wage") != null) {
            salary.setHourly_wage(rs.getInt("hourly_wage"));
        } else {
            salary.setHourly_wage(0);
        }
        
        if (rs.getObject("bonus") != null) {
            salary.setBonus(rs.getInt("bonus"));
        } else {
            salary.setBonus(0);
        }
        
        if (rs.getObject("payment_date") != null) {
            salary.setPayment_date(rs.getDate("payment_date"));
        } else {
            salary.setPayment_date(new java.sql.Date(System.currentTimeMillis()));
        }
        
        if (rs.getObject("created_date") != null) {
            salary.setCreated_date(rs.getDate("created_date"));
        } else {
            salary.setCreated_date(new java.sql.Date(System.currentTimeMillis()));
        }
        
        // Các trường mới - kiểm tra xem có tồn tại không
        try {
            if (rs.getObject("overtime_pay") != null) {
                salary.setOvertime_rate(rs.getBigDecimal("overtime_pay"));
            } else {
                salary.setOvertime_rate(new java.math.BigDecimal("0"));
            }
        } catch (SQLException e) {
            // Cột không tồn tại, bỏ qua
            salary.setOvertime_rate(new java.math.BigDecimal("0"));
        }
        
        try {
            if (rs.getObject("gross_salary") != null) {
                salary.setGross_salary(rs.getBigDecimal("gross_salary").intValue());
            } else {
                salary.setGross_salary(0);
            }
        } catch (SQLException e) {
            // Cột không tồn tại, bỏ qua
            salary.setGross_salary(0);
        }
        
        try {
            if (rs.getObject("penalty_deduction") != null) {
                salary.setPenalty_deduction(rs.getInt("penalty_deduction"));
            } else {
                salary.setPenalty_deduction(0);
            }
        } catch (SQLException e) {
            // Cột không tồn tại, bỏ qua
            salary.setPenalty_deduction(0);
        }
        
        try {
            if (rs.getObject("net_salary") != null) {
                salary.setNet_salary(rs.getBigDecimal("net_salary").intValue());
            } else {
                salary.setNet_salary(0);
            }
        } catch (SQLException e) {
            // Cột không tồn tại, bỏ qua
            salary.setNet_salary(0);
        }
        
        return salary;
    }
}
