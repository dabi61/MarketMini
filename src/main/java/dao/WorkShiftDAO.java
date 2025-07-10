package dao;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.DBConnection;
import model.WorkShift;

/**
 * DAO cho quản lý ca làm việc dựa trên bảng workingsession
 * @author macbook
 */
public class WorkShiftDAO {
    private Connection conn;
    
    public WorkShiftDAO() throws SQLException {
        this.conn = DBConnection.getConnection();
    }
    
    /**
     * Lấy tất cả ca làm (Admin)
     */
    public List<WorkShift> getAllShifts() throws SQLException {
        List<WorkShift> shifts = new ArrayList<>();
        String sql = "SELECT ws.*, e.full_name, s.hourly_wage " +
                    "FROM workingsession ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "ORDER BY ws.date DESC, ws.login_time DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                WorkShift shift = mapResultSetToWorkShift(rs);
                shifts.add(shift);
            }
        }
        return shifts;
    }
    
    /**
     * Lấy ca làm theo nhân viên (Staff)
     */
    public List<WorkShift> getShiftsByEmployee(int employeeId, int days) throws SQLException {
        List<WorkShift> shifts = new ArrayList<>();
        String sql = "SELECT ws.*, e.full_name, s.hourly_wage " +
                    "FROM workingsession ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.employee_id = ? " +
                    "AND ws.date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                    "ORDER BY ws.date DESC";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, days);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WorkShift shift = mapResultSetToWorkShift(rs);
                    shifts.add(shift);
                }
            }
        }
        return shifts;
    }
    
    /**
     * Lấy ca làm đang diễn ra của nhân viên
     */
    public WorkShift getCurrentShift(int employeeId) throws SQLException {
        System.out.println("DEBUG: getCurrentShift for employee " + employeeId);
        
        String sql = "SELECT ws.*, e.full_name, s.hourly_wage " +
                    "FROM workingsession ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.employee_id = ? " +
                    "AND ws.date = CURRENT_DATE " +
                    "AND ws.work_status = 'IN_PROGRESS' " +
                    "ORDER BY ws.login_time DESC " +
                    "LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WorkShift shift = mapResultSetToWorkShift(rs);
                    System.out.println("Found IN_PROGRESS shift: " + shift.getWorkingSessionId());
                    return shift;
                } else {
                    System.out.println("No IN_PROGRESS shift found");
                }
            }
        }
        return null;
    }
    
    /**
     * Tạo ca làm mới
     */
    public boolean createShift(WorkShift shift) throws SQLException {
        String sql = "INSERT INTO workingsession (employee_id, date, work_status) " +
                    "VALUES (?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, shift.getEmployeeId());
            ps.setDate(2, shift.getDate());
            ps.setString(3, shift.getWorkStatus());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        shift.setWorkingSessionId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    /**
     * Cập nhật ca làm
     */
    public boolean updateShift(WorkShift shift) throws SQLException {
        String sql = "UPDATE workingsession SET login_time = ?, logout_time = ?, " +
                    "working_hours = ?, work_status = ? " +
                    "WHERE working_session_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setTimestamp(1, shift.getLoginTime());
            ps.setTimestamp(2, shift.getLogoutTime());
            ps.setBigDecimal(3, shift.getWorkingHours());
            ps.setString(4, shift.getWorkStatus());
            ps.setInt(5, shift.getWorkingSessionId());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa ca làm
     */
    public boolean deleteShift(int workingSessionId) throws SQLException {
        String sql = "DELETE FROM workingsession WHERE working_session_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workingSessionId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Bắt đầu ca làm (Staff check-in)
     */
    public boolean startShift(int employeeId) throws SQLException {
        String sql = "UPDATE workingsession SET work_status = 'IN_PROGRESS', " +
                    "login_time = CURRENT_TIMESTAMP " +
                    "WHERE employee_id = ? AND date = CURRENT_DATE " +
                    "AND work_status = 'SCHEDULED' LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Chốt ca làm (Staff check-out)
     */
    public boolean closeShift(int employeeId) throws SQLException {
        // Tìm ca làm đang diễn ra
        String findSql = "SELECT working_session_id, login_time FROM workingsession " +
                        "WHERE employee_id = ? AND date = CURRENT_DATE " +
                        "AND work_status = 'IN_PROGRESS' LIMIT 1";
        
        try (PreparedStatement findPs = conn.prepareStatement(findSql)) {
            findPs.setInt(1, employeeId);
            
            try (ResultSet rs = findPs.executeQuery()) {
                if (!rs.next()) {
                    return false; // Không có ca đang làm
                }
                
                int workingSessionId = rs.getInt("working_session_id");
                Timestamp loginTime = rs.getTimestamp("login_time");
                
                // Tính toán giờ làm thực tế
                Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                long workTimeMs = currentTime.getTime() - loginTime.getTime();
                BigDecimal workingHours = BigDecimal.valueOf(workTimeMs / (1000.0 * 60.0 * 60.0));
                
                // Cập nhật ca làm
                String updateSql = "UPDATE workingsession SET " +
                                  "logout_time = CURRENT_TIMESTAMP, " +
                                  "working_hours = ?, " +
                                  "work_status = 'COMPLETED' " +
                                  "WHERE working_session_id = ?";
                
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setBigDecimal(1, workingHours);
                    updatePs.setInt(2, workingSessionId);
                    
                    return updatePs.executeUpdate() > 0;
                }
            }
        }
    }
    
    /**
     * Thống kê ca làm cho Staff
     */
    public Map<String, Object> getEmployeeShiftStats(int employeeId, int days) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        // Tổng số ca làm
        String countSql = "SELECT COUNT(*) as total_shifts, " +
                         "SUM(CASE WHEN work_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                         "SUM(CASE WHEN work_status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts " +
                         "FROM workingsession " +
                         "WHERE employee_id = ? " +
                         "AND date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)";
        
        try (PreparedStatement ps = conn.prepareStatement(countSql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, days);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("total_shifts", rs.getInt("total_shifts"));
                    stats.put("completed_shifts", rs.getInt("completed_shifts"));
                    stats.put("absent_shifts", rs.getInt("absent_shifts"));
                }
            }
        }
        
        // Tổng giờ làm và lương
        String earningSql = "SELECT SUM(COALESCE(working_hours, 0)) as total_hours " +
                           "FROM workingsession " +
                           "WHERE employee_id = ? " +
                           "AND date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                           "AND work_status = 'COMPLETED'";
        
        try (PreparedStatement ps = conn.prepareStatement(earningSql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, days);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("total_hours", rs.getBigDecimal("total_hours"));
                }
            }
        }
        
        // Tính lương dự kiến
        String wageSql = "SELECT hourly_wage FROM salary WHERE employee_id = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(wageSql)) {
            ps.setInt(1, employeeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal hourlyWage = rs.getBigDecimal("hourly_wage");
                    BigDecimal totalHours = (BigDecimal) stats.getOrDefault("total_hours", BigDecimal.ZERO);
                    
                    BigDecimal totalEarnings = totalHours.multiply(hourlyWage);
                    
                    stats.put("hourly_wage", hourlyWage);
                    stats.put("estimated_earnings", totalEarnings);
                }
            }
        }
        
        return stats;
    }
    
    /**
     * Thống kê tổng quan ca làm (Admin)
     */
    public Map<String, Object> getShiftOverviewStats(int days) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        String sql = "SELECT " +
                    "COUNT(*) as total_shifts, " +
                    "COUNT(DISTINCT employee_id) as active_employees, " +
                    "SUM(CASE WHEN work_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                    "SUM(CASE WHEN work_status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts, " +
                    "SUM(CASE WHEN work_status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as ongoing_shifts, " +
                    "SUM(COALESCE(working_hours, 0)) as total_work_hours " +
                    "FROM workingsession " +
                    "WHERE date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, days);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("total_shifts", rs.getInt("total_shifts"));
                    stats.put("active_employees", rs.getInt("active_employees"));
                    stats.put("completed_shifts", rs.getInt("completed_shifts"));
                    stats.put("absent_shifts", rs.getInt("absent_shifts"));
                    stats.put("ongoing_shifts", rs.getInt("ongoing_shifts"));
                    stats.put("total_work_hours", rs.getBigDecimal("total_work_hours"));
                }
            }
        }
        
        return stats;
    }
    
    /**
     * Lấy ca làm theo ngày (Admin)
     */
    public List<WorkShift> getShiftsByDate(Date date) throws SQLException {
        List<WorkShift> shifts = new ArrayList<>();
        String sql = "SELECT ws.*, e.full_name, s.hourly_wage " +
                    "FROM workingsession ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.date = ? " +
                    "ORDER BY ws.login_time";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, date);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    WorkShift shift = mapResultSetToWorkShift(rs);
                    shifts.add(shift);
                }
            }
        }
        return shifts;
    }
    
    /**
     * Lấy ca làm việc theo ID
     */
    public WorkShift getShiftById(int workingSessionId) throws SQLException {
        String sql = "SELECT ws.*, e.full_name, s.hourly_wage " +
                    "FROM workingsession ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.working_session_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, workingSessionId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WorkShift shift = mapResultSetToWorkShift(rs);
                    return shift;
                }
            }
        }
        return null;
    }
    
    /**
     * Helper method: Map ResultSet to WorkShift
     */
    private WorkShift mapResultSetToWorkShift(ResultSet rs) throws SQLException {
        WorkShift shift = new WorkShift();
        
        shift.setWorkingSessionId(rs.getInt("working_session_id"));
        shift.setEmployeeId(rs.getInt("employee_id"));
        shift.setEmployeeName(rs.getString("full_name"));
        shift.setLoginTime(rs.getTimestamp("login_time"));
        shift.setLogoutTime(rs.getTimestamp("logout_time"));
        shift.setWorkingHours(rs.getBigDecimal("working_hours"));
        shift.setDate(rs.getDate("date"));
        shift.setWorkStatus(rs.getString("work_status"));
        
        // Hourly wage if available
        BigDecimal hourlyWage = rs.getBigDecimal("hourly_wage");
        if (hourlyWage != null) {
            shift.setHourlyWage(hourlyWage);
            // Tính lương dựa trên working_hours
            shift.calculateEarnings();
        }
        
        return shift;
    }
    
    /**
     * Tạo ca làm việc test cho nhân viên
     */
    public boolean createTestShift(int employeeId) throws SQLException {
        String sql = "INSERT INTO workingsession (employee_id, date, work_status) " +
                    "VALUES (?, CURRENT_DATE, 'SCHEDULED')";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            
            int result = ps.executeUpdate();
            System.out.println("Created test shift for employee " + employeeId + ": " + (result > 0));
            return result > 0;
        }
    }
    
    /**
     * Đóng kết nối
     */
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