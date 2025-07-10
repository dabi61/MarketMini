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
     * Thống kê 7 ngày gần nhất (Staff) - Alias cho getRecentDaysRevenue
     */
    public List<Map<String, Object>> getRecentDaysStats(int days) throws SQLException {
        return getRecentDaysRevenue();
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
                           "SUM(CASE WHEN work_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                           "SUM(CASE WHEN work_status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts, " +
                           "SUM(COALESCE(working_hours, 0)) as total_hours " +
                           "FROM workingsession " +
                           "WHERE employee_id = ? " +
                           "AND MONTH(date) = MONTH(CURRENT_DATE) " +
                           "AND YEAR(date) = YEAR(CURRENT_DATE)";
        
        try (PreparedStatement ps = conn.prepareStatement(monthlySql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("monthly_shifts", rs.getInt("monthly_shifts"));
                result.put("completed_shifts", rs.getInt("completed_shifts"));
                result.put("absent_shifts", rs.getInt("absent_shifts"));
                result.put("total_hours", rs.getBigDecimal("total_hours"));
            }
        }
        
        // Ca làm hôm nay
        String todaySql = "SELECT * FROM workingsession " +
                         "WHERE employee_id = ? AND date = CURRENT_DATE " +
                         "ORDER BY login_time LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(todaySql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("today_start_time", rs.getTimestamp("login_time"));
                result.put("today_end_time", rs.getTimestamp("logout_time"));
                result.put("today_status", rs.getString("work_status"));
                result.put("today_shift_id", rs.getInt("working_session_id"));
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
                    "SUM(COALESCE(ws.working_hours, 0)) as total_hours " +
                    "FROM salary s " +
                    "LEFT JOIN workingsession ws ON s.employee_id = ws.employee_id " +
                    "AND MONTH(ws.date) = MONTH(CURRENT_DATE) " +
                    "AND YEAR(ws.date) = YEAR(CURRENT_DATE) " +
                    "AND ws.work_status = 'COMPLETED' " +
                    "WHERE s.employee_id = ? " +
                    "GROUP BY s.employee_id, s.hourly_wage";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                double hourlyWage = rs.getDouble("hourly_wage");
                double totalHours = rs.getDouble("total_hours");
                
                double regularEarnings = totalHours * hourlyWage;
                double totalEarnings = regularEarnings;
                
                result.put("hourly_wage", hourlyWage);
                result.put("total_hours", totalHours);
                result.put("regular_earnings", regularEarnings);
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
                    "SUM(CASE WHEN work_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                    "SUM(CASE WHEN work_status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts, " +
                    "SUM(CASE WHEN work_status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as ongoing_shifts, " +
                    "SUM(COALESCE(working_hours, 0)) as total_hours, " +
                    "AVG(COALESCE(working_hours, 0)) as avg_hours_per_shift " +
                    "FROM workingsession " +
                    "WHERE MONTH(date) = ? AND YEAR(date) = ?";
        
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
                    "COUNT(ws.working_session_id) as total_shifts, " +
                    "SUM(CASE WHEN ws.work_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                    "SUM(CASE WHEN ws.work_status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts, " +
                    "SUM(COALESCE(ws.working_hours, 0)) as total_hours, " +
                    "s.hourly_wage, " +
                    "SUM(COALESCE(ws.working_hours, 0) * s.hourly_wage) as total_earnings " +
                    "FROM employees e " +
                    "LEFT JOIN workingsession ws ON e.employee_id = ws.employee_id " +
                    "AND MONTH(ws.date) = ? AND YEAR(ws.date) = ? " +
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
                    "date, login_time, logout_time, " +
                    "COALESCE(working_hours, 0) as hours_worked, " +
                    "work_status " +
                    "FROM workingsession " +
                    "WHERE employee_id = ? " +
                    "AND date >= DATE_SUB(CURRENT_DATE, INTERVAL 7 DAY) " +
                    "ORDER BY date DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> shift = new HashMap<>();
                shift.put("shift_date", rs.getDate("date"));
                shift.put("start_time", rs.getTimestamp("login_time"));
                shift.put("end_time", rs.getTimestamp("logout_time"));
                shift.put("hours_worked", rs.getDouble("hours_worked"));
                shift.put("status", rs.getString("work_status"));
                result.add(shift);
            }
        }
        
        return result;
    }
    
    /**
     * Thống kê ca làm của nhân viên theo số ngày (Staff)
     */
    public Map<String, Object> getEmployeeShiftStats(int employeeId, int days) throws SQLException {
        Map<String, Object> result = new HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(*) as total_shifts, " +
                    "SUM(CASE WHEN work_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                    "SUM(COALESCE(working_hours, 0)) as total_hours, " +
                    "AVG(COALESCE(working_hours, 0)) as avg_hours " +
                    "FROM workingsession " +
                    "WHERE employee_id = ? " +
                    "AND date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, days);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                result.put("total_shifts", rs.getInt("total_shifts"));
                result.put("completed_shifts", rs.getInt("completed_shifts"));
                result.put("total_hours", rs.getDouble("total_hours"));
                result.put("avg_hours", rs.getDouble("avg_hours"));
                
                // Tính ước tính thu nhập
                double totalHours = rs.getDouble("total_hours");
                double estimatedEarnings = totalHours * 30000; // Giả sử lương cơ bản 30k/giờ
                result.put("estimated_earnings", estimatedEarnings);
            }
        }
        
        return result;
    }

    /**
     * Dữ liệu cho biểu đồ line chart - Doanh thu theo ngày trong tuần
     */
    public List<Map<String, Object>> getWeeklyRevenueData() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String sql = "SELECT " +
                    "DAYNAME(order_date) as day_name, " +
                    "DAYOFWEEK(order_date) as day_order, " +
                    "COUNT(*) as orders_count, " +
                    "SUM(final_amount) as daily_revenue " +
                    "FROM orders " +
                    "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                    "GROUP BY DAYNAME(order_date), DAYOFWEEK(order_date) " +
                    "ORDER BY day_order";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> day = new HashMap<>();
                day.put("day_name", rs.getString("day_name"));
                day.put("day_order", rs.getInt("day_order"));
                day.put("orders_count", rs.getInt("orders_count"));
                day.put("daily_revenue", rs.getLong("daily_revenue"));
                result.add(day);
            }
        }
        
        return result;
    }
    
    /**
     * Dữ liệu cho biểu đồ pie chart - Phân bố doanh thu theo danh mục
     */
    public List<Map<String, Object>> getCategoryRevenueData() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String sql = "SELECT " +
                    "c.category_name, " +
                    "COUNT(od.order_id) as orders_count, " +
                    "SUM(od.quantity * od.unit_price) as category_revenue " +
                    "FROM category c " +
                    "JOIN products p ON c.category_id = p.category_id " +
                    "JOIN orderdetails od ON p.product_id = od.product_id " +
                    "JOIN orders o ON od.order_id = o.order_id " +
                    "WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                    "GROUP BY c.category_id, c.category_name " +
                    "ORDER BY category_revenue DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> category = new HashMap<>();
                category.put("category_name", rs.getString("category_name"));
                category.put("orders_count", rs.getInt("orders_count"));
                category.put("category_revenue", rs.getLong("category_revenue"));
                result.add(category);
            }
        }
        
        return result;
    }
    
    /**
     * Dữ liệu cho biểu đồ line chart - Ca làm việc theo ngày trong tuần
     */
    public List<Map<String, Object>> getWeeklyShiftData() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String sql = "SELECT " +
                    "DAYNAME(date) as day_name, " +
                    "DAYOFWEEK(date) as day_order, " +
                    "COUNT(*) as shifts_count, " +
                    "SUM(COALESCE(working_hours, 0)) as total_hours " +
                    "FROM workingsession " +
                    "WHERE date >= DATE_SUB(CURDATE(), INTERVAL 7 DAY) " +
                    "AND work_status = 'COMPLETED' " +
                    "GROUP BY DAYNAME(date), DAYOFWEEK(date) " +
                    "ORDER BY day_order";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> day = new HashMap<>();
                day.put("day_name", rs.getString("day_name"));
                day.put("day_order", rs.getInt("day_order"));
                day.put("shifts_count", rs.getInt("shifts_count"));
                day.put("total_hours", rs.getDouble("total_hours"));
                result.add(day);
            }
        }
        
        return result;
    }
    
    /**
     * Dữ liệu cho biểu đồ line chart - Doanh thu theo tháng (12 tháng gần nhất)
     */
    public List<Map<String, Object>> getMonthlyRevenueTrend() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String sql = "SELECT " +
                    "YEAR(order_date) as year, " +
                    "MONTH(order_date) as month, " +
                    "MONTHNAME(order_date) as month_name, " +
                    "COUNT(*) as orders_count, " +
                    "SUM(final_amount) as monthly_revenue " +
                    "FROM orders " +
                    "WHERE order_date >= DATE_SUB(CURDATE(), INTERVAL 12 MONTH) " +
                    "GROUP BY YEAR(order_date), MONTH(order_date), MONTHNAME(order_date) " +
                    "ORDER BY year, month";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> month = new HashMap<>();
                month.put("year", rs.getInt("year"));
                month.put("month", rs.getInt("month"));
                month.put("month_name", rs.getString("month_name"));
                month.put("orders_count", rs.getInt("orders_count"));
                month.put("monthly_revenue", rs.getLong("monthly_revenue"));
                result.add(month);
            }
        }
        
        return result;
    }
    
    /**
     * Dữ liệu cho biểu đồ pie chart - Top 5 sản phẩm bán chạy
     */
    public List<Map<String, Object>> getTopProductsPieData() throws SQLException {
        List<Map<String, Object>> result = new ArrayList<>();
        
        String sql = "SELECT " +
                    "p.product_name, " +
                    "SUM(od.quantity) as total_sold, " +
                    "SUM(od.quantity * od.unit_price) as total_revenue " +
                    "FROM products p " +
                    "JOIN orderdetails od ON p.product_id = od.product_id " +
                    "JOIN orders o ON od.order_id = o.order_id " +
                    "WHERE o.order_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY) " +
                    "GROUP BY p.product_id, p.product_name " +
                    "ORDER BY total_sold DESC " +
                    "LIMIT 5";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> product = new HashMap<>();
                product.put("product_name", rs.getString("product_name"));
                product.put("total_sold", rs.getInt("total_sold"));
                product.put("total_revenue", rs.getLong("total_revenue"));
                result.add(product);
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