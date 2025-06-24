package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DBConnection;

/**
 * DAO cho các chức năng thống kê
 * @author macbook
 */
public class ThongKeDAO {
    private Connection conn;
    
    public ThongKeDAO() throws SQLException {
        this.conn = DBConnection.getConnection();
    }
    
    // ========== THỐNG KÊ CHO ADMIN ==========
    
    /**
     * Thống kê doanh thu theo tháng (Admin)
     */
    public Map<String, Object> getMonthlyRevenue(int month, int year) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT COUNT(*) as total_orders, " +
                    "SUM(final_amount) as total_revenue, " +
                    "AVG(final_amount) as avg_order_value " +
                    "FROM orders " +
                    "WHERE MONTH(order_date) = ? AND YEAR(order_date) = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("total_orders", rs.getInt("total_orders"));
                result.put("total_revenue", rs.getLong("total_revenue"));
                result.put("avg_order_value", rs.getDouble("avg_order_value"));
            }
        }
        return result;
    }
    
    /**
     * Thống kê lợi nhuận (Admin)
     */
    public Map<String, Object> getProfitAnalysis(int month, int year) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        // Doanh thu
        String revenueSql = "SELECT SUM(final_amount) as revenue FROM orders " +
                           "WHERE MONTH(order_date) = ? AND YEAR(order_date) = ?";
        
        // Chi phí nhập hàng
        String costSql = "SELECT SUM(i.quantity * i.import_price) as import_cost " +
                        "FROM imports i " +
                        "WHERE MONTH(i.import_date) = ? AND YEAR(i.import_date) = ?";
        
        // Chi phí lương
        String salarySql = "SELECT SUM(s.total_hours * s.hourly_wage + s.bonus) as salary_cost " +
                          "FROM salary s " +
                          "WHERE MONTH(s.created_date) = ? AND YEAR(s.created_date) = ?";
        
        long revenue = 0, importCost = 0, salaryCost = 0;
        
        // Lấy doanh thu
        try (PreparedStatement ps = conn.prepareStatement(revenueSql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) revenue = rs.getLong("revenue");
        }
        
        // Lấy chi phí nhập hàng
        try (PreparedStatement ps = conn.prepareStatement(costSql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) importCost = rs.getLong("import_cost");
        }
        
        // Lấy chi phí lương
        try (PreparedStatement ps = conn.prepareStatement(salarySql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) salaryCost = rs.getLong("salary_cost");
        }
        
        result.put("revenue", revenue);
        result.put("import_cost", importCost);
        result.put("salary_cost", salaryCost);
        result.put("profit", revenue - importCost - salaryCost);
        
        return result;
    }
    
    /**
     * Thống kê nhân viên (Admin)
     */
    public List<Map<String, Object>> getEmployeePerformance(int month, int year) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT e.employee_id, e.full_name, e.role, " +
                    "COUNT(o.order_id) as total_orders, " +
                    "SUM(o.final_amount) as total_sales " +
                    "FROM employees e " +
                    "LEFT JOIN orders o ON e.employee_id = o.employee_id " +
                    "AND MONTH(o.order_date) = ? AND YEAR(o.order_date) = ? " +
                    "GROUP BY e.employee_id, e.full_name, e.role " +
                    "ORDER BY total_sales DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> emp = new HashMap<>();
                emp.put("employee_id", rs.getInt("employee_id"));
                emp.put("full_name", rs.getString("full_name"));
                emp.put("role", rs.getInt("role") == 1 ? "Admin" : "Nhân viên");
                emp.put("total_orders", rs.getInt("total_orders"));
                emp.put("total_sales", rs.getLong("total_sales"));
                result.add(emp);
            }
        }
        return result;
    }
    
    /**
     * Thống kê tồn kho (Admin)
     */
    public List<Map<String, Object>> getInventoryStatus() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, c.category_name, " +
                    "p.stock_quantity, p.price, " +
                    "(p.stock_quantity * p.price) as inventory_value " +
                    "FROM products p " +
                    "JOIN category c ON p.category_id = c.category_id " +
                    "ORDER BY p.stock_quantity ASC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("product_id", rs.getInt("product_id"));
                product.put("product_name", rs.getString("product_name"));
                product.put("category_name", rs.getString("category_name"));
                product.put("stock_quantity", rs.getInt("stock_quantity"));
                product.put("price", rs.getInt("price"));
                product.put("inventory_value", rs.getLong("inventory_value"));
                result.add(product);
            }
        }
        return result;
    }
    
    // ========== THỐNG KÊ CHO STAFF ==========
    
    /**
     * Thống kê doanh thu hôm nay (Staff)
     */
    public Map<String, Object> getTodayStats() throws SQLException {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT COUNT(*) as today_orders, " +
                    "SUM(final_amount) as today_revenue " +
                    "FROM orders " +
                    "WHERE DATE(order_date) = CURDATE()";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("today_orders", rs.getInt("today_orders"));
                result.put("today_revenue", rs.getLong("today_revenue"));
            }
        }
        return result;
    }
    
    /**
     * Sản phẩm bán chạy nhất (Staff)
     */
    public List<Map<String, Object>> getTopSellingProducts(int limit) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT p.product_id, p.product_name, " +
                    "SUM(od.quantity) as total_sold, " +
                    "SUM(od.quantity * od.unit_price) as total_revenue " +
                    "FROM products p " +
                    "JOIN orderdetails od ON p.product_id = od.product_id " +
                    "JOIN orders o ON od.order_id = o.order_id " +
                    "WHERE DATE(o.order_date) >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                    "GROUP BY p.product_id, p.product_name " +
                    "ORDER BY total_sold DESC " +
                    "LIMIT ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("product_id", rs.getInt("product_id"));
                product.put("product_name", rs.getString("product_name"));
                product.put("total_sold", rs.getInt("total_sold"));
                product.put("total_revenue", rs.getLong("total_revenue"));
                result.add(product);
            }
        }
        return result;
    }
    
    /**
     * Thống kê doanh thu 7 ngày gần nhất (Staff)
     */
    public List<Map<String, Object>> getRecentDaysRevenue() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        String sql = "SELECT DATE(order_date) as date, " +
                    "COUNT(*) as orders_count, " +
                    "SUM(final_amount) as daily_revenue " +
                    "FROM orders " +
                    "WHERE DATE(order_date) >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                    "GROUP BY DATE(order_date) " +
                    "ORDER BY date DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> daily = new HashMap<>();
                daily.put("date", rs.getDate("date"));
                daily.put("orders_count", rs.getInt("orders_count"));
                daily.put("daily_revenue", rs.getLong("daily_revenue"));
                result.add(daily);
            }
        }
        return result;
    }
    
    /**
     * Thống kê khách hàng (Cả Admin và Staff)
     */
    public Map<String, Object> getCustomerStats() throws SQLException {
        Map<String, Object> result = new HashMap<>();
        String sql = "SELECT COUNT(*) as total_customers, " +
                    "SUM(CASE WHEN points > 0 THEN 1 ELSE 0 END) as customers_with_points, " +
                    "SUM(points) as total_points " +
                    "FROM customers";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("total_customers", rs.getInt("total_customers"));
                result.put("customers_with_points", rs.getInt("customers_with_points"));
                result.put("total_points", rs.getLong("total_points"));
            }
        }
        return result;
    }
    
    // ========== THỐNG KÊ CA LÀM VIỆC ==========
    
    /**
     * Thống kê ca làm của nhân viên hiện tại (Staff)
     */
    public Map<String, Object> getEmployeeShiftSummary(int employeeId) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        // Ca làm trong tháng
        String monthlySql = "SELECT " +
                           "COUNT(*) as monthly_shifts, " +
                           "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                           "SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts, " +
                           "SUM(COALESCE(actual_hours, planned_hours)) as total_hours, " +
                           "SUM(overtime_hours) as overtime_hours " +
                           "FROM workshifts " +
                           "WHERE employee_id = ? " +
                           "AND MONTH(shift_date) = MONTH(CURRENT_DATE) " +
                           "AND YEAR(shift_date) = YEAR(CURRENT_DATE)";
        
        try (PreparedStatement ps = conn.prepareStatement(monthlySql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("monthly_shifts", rs.getInt("monthly_shifts"));
                result.put("completed_shifts", rs.getInt("completed_shifts"));
                result.put("absent_shifts", rs.getInt("absent_shifts"));
                result.put("total_hours", rs.getBigDecimal("total_hours"));
                result.put("overtime_hours", rs.getBigDecimal("overtime_hours"));
            }
        }
        
        // Ca làm hôm nay
        String todaySql = "SELECT * FROM workshifts " +
                         "WHERE employee_id = ? AND shift_date = CURRENT_DATE " +
                         "ORDER BY start_time LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(todaySql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("today_shift_type", rs.getString("shift_type"));
                result.put("today_start_time", rs.getTime("start_time"));
                result.put("today_end_time", rs.getTime("end_time"));
                result.put("today_status", rs.getString("status"));
                result.put("today_shift_id", rs.getInt("shift_id"));
            } else {
                result.put("today_shift_type", "Không có ca");
                result.put("today_status", "NO_SHIFT");
            }
        }
        
        return result;
    }
    
    /**
     * Lương dự kiến của nhân viên (Staff)
     */
    public Map<String, Object> getEmployeeEarningsEstimate(int employeeId) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SELECT " +
                    "s.hourly_wage, " +
                    "SUM(COALESCE(ws.actual_hours, ws.planned_hours)) as total_hours, " +
                    "SUM(ws.overtime_hours) as overtime_hours " +
                    "FROM salary s " +
                    "LEFT JOIN workshifts ws ON s.employee_id = ws.employee_id " +
                    "AND MONTH(ws.shift_date) = MONTH(CURRENT_DATE) " +
                    "AND YEAR(ws.shift_date) = YEAR(CURRENT_DATE) " +
                    "AND ws.status = 'COMPLETED' " +
                    "WHERE s.employee_id = ? " +
                    "GROUP BY s.employee_id, s.hourly_wage";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double hourlyWage = rs.getDouble("hourly_wage");
                double totalHours = rs.getDouble("total_hours");
                double overtimeHours = rs.getDouble("overtime_hours");
                
                double regularEarnings = totalHours * hourlyWage;
                double overtimeEarnings = overtimeHours * hourlyWage * 1.5;
                double totalEarnings = regularEarnings + overtimeEarnings;
                
                result.put("hourly_wage", hourlyWage);
                result.put("total_hours", totalHours);
                result.put("overtime_hours", overtimeHours);
                result.put("regular_earnings", regularEarnings);
                result.put("overtime_earnings", overtimeEarnings);
                result.put("total_earnings", totalEarnings);
            }
        }
        
        return result;
    }
    
    /**
     * Thống kê tổng quan ca làm (Admin)
     */
    public Map<String, Object> getShiftOverview(int month, int year) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(*) as total_shifts, " +
                    "COUNT(DISTINCT employee_id) as active_employees, " +
                    "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                    "SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts, " +
                    "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as ongoing_shifts, " +
                    "SUM(COALESCE(actual_hours, planned_hours)) as total_hours, " +
                    "SUM(overtime_hours) as overtime_hours, " +
                    "AVG(COALESCE(actual_hours, planned_hours)) as avg_hours_per_shift " +
                    "FROM workshifts " +
                    "WHERE MONTH(shift_date) = ? AND YEAR(shift_date) = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("total_shifts", rs.getInt("total_shifts"));
                result.put("active_employees", rs.getInt("active_employees"));
                result.put("completed_shifts", rs.getInt("completed_shifts"));
                result.put("absent_shifts", rs.getInt("absent_shifts"));
                result.put("ongoing_shifts", rs.getInt("ongoing_shifts"));
                result.put("total_hours", rs.getDouble("total_hours"));
                result.put("overtime_hours", rs.getDouble("overtime_hours"));
                result.put("avg_hours_per_shift", rs.getDouble("avg_hours_per_shift"));
            }
        }
        
        return result;
    }
    
    /**
     * Thống kê hiệu suất ca làm theo nhân viên (Admin)
     */
    public List<Map<String, Object>> getShiftPerformanceByEmployee(int month, int year) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String sql = "SELECT " +
                    "e.employee_id, e.full_name, e.role, " +
                    "COUNT(ws.shift_id) as total_shifts, " +
                    "SUM(CASE WHEN ws.status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                    "SUM(CASE WHEN ws.status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts, " +
                    "SUM(COALESCE(ws.actual_hours, ws.planned_hours)) as total_hours, " +
                    "SUM(ws.overtime_hours) as overtime_hours, " +
                    "s.hourly_wage, " +
                    "SUM((COALESCE(ws.actual_hours, ws.planned_hours) * s.hourly_wage) + " +
                    "    (ws.overtime_hours * s.hourly_wage * 1.5)) as total_earnings " +
                    "FROM employees e " +
                    "LEFT JOIN workshifts ws ON e.employee_id = ws.employee_id " +
                    "AND MONTH(ws.shift_date) = ? AND YEAR(ws.shift_date) = ? " +
                    "LEFT JOIN salary s ON e.employee_id = s.employee_id " +
                    "WHERE e.role IN (1, 2) " +
                    "GROUP BY e.employee_id, e.full_name, e.role, s.hourly_wage " +
                    "ORDER BY total_earnings DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, month);
            ps.setInt(2, year);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> emp = new HashMap<>();
                emp.put("employee_id", rs.getInt("employee_id"));
                emp.put("full_name", rs.getString("full_name"));
                emp.put("role", rs.getInt("role") == 1 ? "Admin" : "Nhân viên");
                emp.put("total_shifts", rs.getInt("total_shifts"));
                emp.put("completed_shifts", rs.getInt("completed_shifts"));
                emp.put("absent_shifts", rs.getInt("absent_shifts"));
                emp.put("total_hours", rs.getDouble("total_hours"));
                emp.put("overtime_hours", rs.getDouble("overtime_hours"));
                emp.put("hourly_wage", rs.getDouble("hourly_wage"));
                emp.put("total_earnings", rs.getDouble("total_earnings"));
                
                // Tính attendance rate
                int totalShifts = rs.getInt("total_shifts");
                int completedShifts = rs.getInt("completed_shifts");
                double attendanceRate = totalShifts > 0 ? (completedShifts * 100.0 / totalShifts) : 0;
                emp.put("attendance_rate", attendanceRate);
                
                result.add(emp);
            }
        }
        
        return result;
    }
    
    /**
     * Thống kê ca làm 7 ngày gần nhất (Staff)
     */
    public List<Map<String, Object>> getRecentShifts(int employeeId) throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String sql = "SELECT " +
                    "shift_date, shift_type, start_time, end_time, " +
                    "COALESCE(actual_hours, planned_hours) as hours_worked, " +
                    "overtime_hours, status " +
                    "FROM workshifts " +
                    "WHERE employee_id = ? " +
                    "AND shift_date >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY) " +
                    "ORDER BY shift_date DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> shift = new HashMap<>();
                shift.put("shift_date", rs.getDate("shift_date"));
                shift.put("shift_type", rs.getString("shift_type"));
                shift.put("start_time", rs.getTime("start_time"));
                shift.put("end_time", rs.getTime("end_time"));
                shift.put("hours_worked", rs.getDouble("hours_worked"));
                shift.put("overtime_hours", rs.getDouble("overtime_hours"));
                shift.put("status", rs.getString("status"));
                result.add(shift);
            }
        }
        
        return result;
    }

    public void closeConnection() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 