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
 * DAO cho quản lý ca làm việc
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
                    "FROM workshifts ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "ORDER BY ws.shift_date DESC, ws.start_time";
        
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
                    "FROM workshifts ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.employee_id = ? " +
                    "AND ws.shift_date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                    "ORDER BY ws.shift_date DESC";
        
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
     * Lấy ca làm đang diễn ra của nhân viên (có thể có nhiều ca IN_PROGRESS)
     */
    public WorkShift getCurrentShift(int employeeId) throws SQLException {
        System.out.println("DEBUG: getCurrentShift for employee " + employeeId);
        
        String sql = "SELECT ws.*, e.full_name, s.hourly_wage " +
                    "FROM workshifts ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.employee_id = ? " +
                    "AND ws.shift_date = CURRENT_DATE " +
                    "AND ws.status = 'IN_PROGRESS' " +
                    "ORDER BY ws.created_at DESC " + // Get most recent in-progress shift
                    "LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    WorkShift shift = mapResultSetToWorkShift(rs);
                    System.out.println("Found IN_PROGRESS shift: " + shift.getShiftId());
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
        String sql = "INSERT INTO workshifts (employee_id, shift_date, shift_type, start_time, " +
                    "planned_hours, break_minutes, status, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, shift.getEmployeeId());
            ps.setDate(2, shift.getShiftDate());
            ps.setString(3, shift.getShiftType().name());
            ps.setTime(4, shift.getStartTime());
            ps.setBigDecimal(5, shift.getPlannedHours());
            ps.setInt(6, shift.getBreakMinutes());
            ps.setString(7, shift.getStatus().name());
            ps.setString(8, shift.getNotes());
            
            int result = ps.executeUpdate();
            
            if (result > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        shift.setShiftId(generatedKeys.getInt(1));
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
        String sql = "UPDATE workshifts SET shift_date = ?, shift_type = ?, start_time = ?, " +
                    "end_time = ?, planned_hours = ?, actual_hours = ?, break_minutes = ?, " +
                    "overtime_hours = ?, status = ?, notes = ?, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE shift_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDate(1, shift.getShiftDate());
            ps.setString(2, shift.getShiftType().name());
            ps.setTime(3, shift.getStartTime());
            ps.setTime(4, shift.getEndTime());
            ps.setBigDecimal(5, shift.getPlannedHours());
            ps.setBigDecimal(6, shift.getActualHours());
            ps.setInt(7, shift.getBreakMinutes());
            ps.setBigDecimal(8, shift.getOvertimeHours());
            ps.setString(9, shift.getStatus().name());
            ps.setString(10, shift.getNotes());
            ps.setInt(11, shift.getShiftId());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Xóa ca làm
     */
    public boolean deleteShift(int shiftId) throws SQLException {
        String sql = "DELETE FROM workshifts WHERE shift_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, shiftId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Chốt ca làm (Staff sử dụng)
     */
    public boolean closeShift(int employeeId, String notes) throws SQLException {
        // Tìm ca làm đang diễn ra
        String findSql = "SELECT shift_id, start_time, planned_hours FROM workshifts " +
                        "WHERE employee_id = ? AND shift_date = CURRENT_DATE " +
                        "AND status = 'IN_PROGRESS' LIMIT 1";
        
        try (PreparedStatement findPs = conn.prepareStatement(findSql)) {
            findPs.setInt(1, employeeId);
            
            try (ResultSet rs = findPs.executeQuery()) {
                if (!rs.next()) {
                    return false; // Không có ca đang làm
                }
                
                int shiftId = rs.getInt("shift_id");
                Time startTime = rs.getTime("start_time");
                BigDecimal plannedHours = rs.getBigDecimal("planned_hours");
                
                // Tính toán giờ làm thực tế
                Time currentTime = new Time(System.currentTimeMillis());
                long workTimeMs = currentTime.getTime() - startTime.getTime();
                BigDecimal actualHours = BigDecimal.valueOf(workTimeMs / (1000.0 * 60.0 * 60.0));
                
                // Tính overtime (nếu có)
                BigDecimal overtimeHours = actualHours.compareTo(plannedHours) > 0 ? 
                    actualHours.subtract(plannedHours) : BigDecimal.ZERO;
                
                // Cập nhật ca làm
                String updateSql = "UPDATE workshifts SET " +
                                  "end_time = CURRENT_TIME, " +
                                  "actual_hours = ?, " +
                                  "overtime_hours = ?, " +
                                  "status = 'COMPLETED', " +
                                  "notes = ?, " +
                                  "updated_at = CURRENT_TIMESTAMP " +
                                  "WHERE shift_id = ?";
                
                try (PreparedStatement updatePs = conn.prepareStatement(updateSql)) {
                    updatePs.setBigDecimal(1, actualHours);
                    updatePs.setBigDecimal(2, overtimeHours);
                    updatePs.setString(3, notes);
                    updatePs.setInt(4, shiftId);
                    
                    return updatePs.executeUpdate() > 0;
                }
            }
        }
    }
    
    /**
     * Bắt đầu ca làm (Staff check-in)
     */
    public boolean startShift(int employeeId) throws SQLException {
        String sql = "UPDATE workshifts SET status = 'IN_PROGRESS', " +
                    "start_time = CURRENT_TIME, updated_at = CURRENT_TIMESTAMP " +
                    "WHERE employee_id = ? AND shift_date = CURRENT_DATE " +
                    "AND status = 'SCHEDULED' LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Thống kê ca làm cho Staff
     */
    public Map<String, Object> getEmployeeShiftStats(int employeeId, int days) throws SQLException {
        Map<String, Object> stats = new HashMap<>();
        
        // Tổng số ca làm
        String countSql = "SELECT COUNT(*) as total_shifts, " +
                         "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                         "SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts " +
                         "FROM workshifts " +
                         "WHERE employee_id = ? " +
                         "AND shift_date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)";
        
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
        String earningSql = "SELECT SUM(COALESCE(actual_hours, planned_hours)) as total_hours, " +
                           "SUM(overtime_hours) as total_overtime " +
                           "FROM workshifts " +
                           "WHERE employee_id = ? " +
                           "AND shift_date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY) " +
                           "AND status = 'COMPLETED'";
        
        try (PreparedStatement ps = conn.prepareStatement(earningSql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, days);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    stats.put("total_hours", rs.getBigDecimal("total_hours"));
                    stats.put("total_overtime", rs.getBigDecimal("total_overtime"));
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
                    BigDecimal overtimeHours = (BigDecimal) stats.getOrDefault("total_overtime", BigDecimal.ZERO);
                    
                    BigDecimal regularEarnings = totalHours.multiply(hourlyWage);
                    BigDecimal overtimeEarnings = overtimeHours.multiply(hourlyWage).multiply(BigDecimal.valueOf(1.5));
                    BigDecimal totalEarnings = regularEarnings.add(overtimeEarnings);
                    
                    stats.put("hourly_wage", hourlyWage);
                    stats.put("estimated_earnings", totalEarnings);
                    stats.put("overtime_earnings", overtimeEarnings);
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
                    "SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_shifts, " +
                    "SUM(CASE WHEN status = 'ABSENT' THEN 1 ELSE 0 END) as absent_shifts, " +
                    "SUM(CASE WHEN status = 'IN_PROGRESS' THEN 1 ELSE 0 END) as ongoing_shifts, " +
                    "SUM(COALESCE(actual_hours, planned_hours)) as total_work_hours, " +
                    "SUM(overtime_hours) as total_overtime_hours " +
                    "FROM workshifts " +
                    "WHERE shift_date >= DATE_SUB(CURRENT_DATE, INTERVAL ? DAY)";
        
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
                    stats.put("total_overtime_hours", rs.getBigDecimal("total_overtime_hours"));
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
                    "FROM workshifts ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.shift_date = ? " +
                    "ORDER BY ws.start_time";
        
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
     * Helper method: Map ResultSet to WorkShift
     */
    private WorkShift mapResultSetToWorkShift(ResultSet rs) throws SQLException {
        WorkShift shift = new WorkShift();
        
        shift.setShiftId(rs.getInt("shift_id"));
        shift.setEmployeeId(rs.getInt("employee_id"));
        shift.setEmployeeName(rs.getString("full_name"));
        shift.setShiftDate(rs.getDate("shift_date"));
        
        // Enum parsing
        String shiftTypeStr = rs.getString("shift_type");
        if (shiftTypeStr != null) {
            shift.setShiftType(WorkShift.ShiftType.valueOf(shiftTypeStr));
        }
        
        String statusStr = rs.getString("status");
        if (statusStr != null) {
            shift.setStatus(WorkShift.ShiftStatus.valueOf(statusStr));
        }
        
        shift.setStartTime(rs.getTime("start_time"));
        shift.setEndTime(rs.getTime("end_time"));
        shift.setPlannedHours(rs.getBigDecimal("planned_hours"));
        shift.setActualHours(rs.getBigDecimal("actual_hours"));
        shift.setBreakMinutes(rs.getInt("break_minutes"));
        shift.setOvertimeHours(rs.getBigDecimal("overtime_hours"));
        shift.setNotes(rs.getString("notes"));
        shift.setCreatedAt(rs.getTimestamp("created_at"));
        shift.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        // Enhanced penalty system fields
        shift.setCheckInTime(rs.getTime("check_in_time"));
        shift.setCheckOutTime(rs.getTime("check_out_time"));
        shift.setLateMinutes(rs.getInt("late_minutes"));
        shift.setEarlyLeaveMinutes(rs.getInt("early_leave_minutes"));
        shift.setScheduled(rs.getBoolean("is_scheduled"));
        shift.setPenaltyAmount(rs.getBigDecimal("penalty_amount"));
        shift.setPenaltyReason(rs.getString("penalty_reason"));
        shift.setSalaryAdjustmentPercent(rs.getBigDecimal("salary_adjustment_percent"));
        shift.setAdjustmentReason(rs.getString("adjustment_reason"));
        shift.setAutoCheckoutPenalty(rs.getBoolean("auto_checkout_penalty"));
        
        // Hourly wage if available - tính lương dựa trên thời gian thực tế
        BigDecimal hourlyWage = rs.getBigDecimal("hourly_wage");
        if (hourlyWage != null) {
            shift.setHourlyWage(hourlyWage);
            // Tính lương dựa trên actual_hours thay vì planned_hours
            shift.calculateEarnings();
        }
        
        return shift;
    }
    
    /**
     * Lấy ca đã lên lịch của nhân viên hôm nay
     */
    public WorkShift getScheduledShift(int employeeId) throws SQLException {
        System.out.println("DEBUG: getScheduledShift for employee " + employeeId);
        
        // Debug: Kiểm tra tất cả ca làm việc của nhân viên hôm nay
        String debugSql = "SELECT shift_id, shift_date, status, is_scheduled, start_time, end_time " +
                         "FROM workshifts " +
                         "WHERE employee_id = ? AND shift_date = CURRENT_DATE";
        
        try (PreparedStatement debugPs = conn.prepareStatement(debugSql)) {
            debugPs.setInt(1, employeeId);
            try (ResultSet debugRs = debugPs.executeQuery()) {
                System.out.println("All shifts for employee " + employeeId + " today:");
                while (debugRs.next()) {
                    System.out.println("- Shift ID: " + debugRs.getInt("shift_id") + 
                                     ", Date: " + debugRs.getDate("shift_date") +
                                     ", Status: " + debugRs.getString("status") +
                                     ", Scheduled: " + debugRs.getBoolean("is_scheduled") +
                                     ", Start: " + debugRs.getTime("start_time") +
                                     ", End: " + debugRs.getTime("end_time"));
                }
            }
        }
        
        String sql = "SELECT ws.*, e.full_name, s.hourly_wage " +
                    "FROM workshifts ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.employee_id = ? " +
                    "AND ws.shift_date = CURRENT_DATE " +
                    "AND ws.status = 'SCHEDULED' " +
                    "AND ws.is_scheduled = TRUE " +
                    "LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Found scheduled shift: " + rs.getInt("shift_id"));
                    return mapResultSetToWorkShift(rs);
                } else {
                    System.out.println("No scheduled shift found for employee " + employeeId);
                }
            }
        }
        return null;
    }
    
    /**
     * Bắt đầu ca ngoài lịch (75% lương)
     */
    public boolean startUnscheduledShift(int employeeId) throws SQLException {
        String sql = "INSERT INTO workshifts (employee_id, shift_date, shift_type, start_time, " +
                    "check_in_time, planned_hours, break_minutes, status, is_scheduled, " +
                    "salary_adjustment_percent, adjustment_reason, notes) " +
                    "VALUES (?, CURRENT_DATE, 'FULL', CURRENT_TIME, CURRENT_TIME, " +
                    "8.0, 60, 'IN_PROGRESS', FALSE, 75.0, " +
                    "'Ca ngoài lịch - chỉ tính 75% lương', 'Ca làm ngoài lịch')";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Check in ca theo lịch
     */
    public boolean checkInShift(int employeeId) throws SQLException {
        // Tìm ca đã lên lịch
        WorkShift scheduledShift = getScheduledShift(employeeId);
        if (scheduledShift == null) {
            return false;
        }
        
        Time currentTime = new Time(System.currentTimeMillis());
        
        // Tính số phút muộn
        int lateMinutes = 0;
        if (currentTime.after(scheduledShift.getStartTime())) {
            long diffMs = currentTime.getTime() - scheduledShift.getStartTime().getTime();
            lateMinutes = (int) (diffMs / (1000 * 60));
        }
        
        // Áp dụng penalty cho đến muộn
        BigDecimal salaryAdjustment = BigDecimal.valueOf(100.0);
        String adjustmentReason = null;
        
        if (lateMinutes > 0) {
            // Giảm 25% cho mỗi giờ muộn (làm tròn lên)
            int lateHours = (int) Math.ceil(lateMinutes / 60.0);
            BigDecimal penaltyPercent = BigDecimal.valueOf(25 * lateHours);
            salaryAdjustment = salaryAdjustment.subtract(penaltyPercent);
            
            if (salaryAdjustment.compareTo(BigDecimal.ZERO) < 0) {
                salaryAdjustment = BigDecimal.ZERO;
            }
            
            adjustmentReason = "Đến muộn " + lateMinutes + " phút - giảm " + penaltyPercent + "% lương";
        }
        
        // Cập nhật ca làm
        String sql = "UPDATE workshifts SET " +
                    "status = 'IN_PROGRESS', " +
                    "check_in_time = CURRENT_TIME, " +
                    "late_minutes = ?, " +
                    "salary_adjustment_percent = ?, " +
                    "adjustment_reason = ?, " +
                    "updated_at = CURRENT_TIMESTAMP " +
                    "WHERE shift_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, lateMinutes);
            ps.setBigDecimal(2, salaryAdjustment);
            ps.setString(3, adjustmentReason);
            ps.setInt(4, scheduledShift.getShiftId());
            
            return ps.executeUpdate() > 0;
        }
    }
    
    /**
     * Check out ca với penalty system
     */
    public boolean checkOutShift(int employeeId, String notes) throws SQLException {
        // Tìm ca đang làm
        WorkShift currentShift = getCurrentShift(employeeId);
        if (currentShift == null) {
            return false;
        }
        
        Time currentTime = new Time(System.currentTimeMillis());
        Time checkInTime = currentShift.getCheckInTime() != null ? 
            currentShift.getCheckInTime() : currentShift.getStartTime();
        
        // Debug logging
        System.out.println("Debug checkOutShift - Employee: " + employeeId);
        System.out.println("Current time: " + currentTime);
        System.out.println("Check in time: " + checkInTime);
        System.out.println("Shift date: " + currentShift.getShiftDate());
        
        // Validation: Đảm bảo checkInTime hợp lệ
        if (checkInTime == null) {
            throw new SQLException("Không thể xác định thời gian bắt đầu ca làm việc");
        }
        
        // Tính toán thời gian làm việc chính xác bằng cách sử dụng LocalTime
        java.time.LocalTime checkInLocalTime = checkInTime.toLocalTime();
        java.time.LocalTime currentLocalTime = currentTime.toLocalTime();
        
        System.out.println("Check-in LocalTime: " + checkInLocalTime);
        System.out.println("Current LocalTime: " + currentLocalTime);
        
        // Tính duration between hai LocalTime
        java.time.Duration duration = java.time.Duration.between(checkInLocalTime, currentLocalTime);
        
        // Nếu âm (qua ngày), cộng 24h
        if (duration.isNegative()) {
            duration = duration.plusDays(1);
        }
        
        long workTimeMs = duration.toMillis();
        System.out.println("Duration: " + duration);
        System.out.println("Work time (ms): " + workTimeMs);
        
        // Validation: Giới hạn hợp lý (tối đa 16 giờ)
        long maxWorkTimeMs = 16 * 60 * 60 * 1000; // 16 giờ
        if (workTimeMs > maxWorkTimeMs) {
            System.out.println("Work time > 16h, capping at 16h");
            workTimeMs = maxWorkTimeMs;
        }
        
        // Tối thiểu 1 phút (tránh 0)
        if (workTimeMs < 60 * 1000) {
            workTimeMs = 60 * 1000; // 1 phút tối thiểu
        }
        
        BigDecimal actualHours = BigDecimal.valueOf(workTimeMs / (1000.0 * 60.0 * 60.0));
        
        System.out.println("Final work time (ms): " + workTimeMs);
        System.out.println("Work time (minutes): " + (workTimeMs / (1000.0 * 60.0)));
        
        System.out.println("Calculated actual hours: " + actualHours);
        
        // Scale to 2 decimal places để match database precision
        actualHours = actualHours.setScale(2, BigDecimal.ROUND_HALF_UP);
        System.out.println("Final actual hours: " + actualHours);
        
        // Tính overtime dựa trên actual vs planned hours
        BigDecimal plannedHours = currentShift.getPlannedHours() != null ? 
            currentShift.getPlannedHours() : BigDecimal.valueOf(8.0);
        BigDecimal overtimeHours = actualHours.compareTo(plannedHours) > 0 ? 
            actualHours.subtract(plannedHours) : BigDecimal.ZERO;
        
        // Validation: giới hạn overtime tối đa 12 giờ
        if (overtimeHours.compareTo(BigDecimal.valueOf(12.0)) > 0) {
            overtimeHours = BigDecimal.valueOf(12.0);
        }
        
        // Scale to 2 decimal places
        overtimeHours = overtimeHours.setScale(2, BigDecimal.ROUND_HALF_UP);
        System.out.println("Planned hours: " + plannedHours + ", Overtime hours: " + overtimeHours);
        
        // Set data vào shift object để tính lương chính xác
        currentShift.setActualHours(actualHours);
        currentShift.setOvertimeHours(overtimeHours);
        
        // Tính lương dựa trên thời gian thực tế
        if (currentShift.getHourlyWage() != null) {
            currentShift.calculateEarnings();
            System.out.println("Calculated earnings for shift: " + currentShift.getFinalEarnings());
        }
        
        // Tính end time dự kiến dựa trên start time + planned hours
        Time expectedEndTime = currentShift.getEndTime();
        if (expectedEndTime == null) {
            long plannedMs = plannedHours.longValue() * 60 * 60 * 1000;
            long breakMs = currentShift.getBreakMinutes() * 60 * 1000;
            long expectedEndMs = checkInTime.getTime() + plannedMs + breakMs;
            expectedEndTime = new Time(expectedEndMs);
        }
        
        // Kiểm tra về sớm
        int earlyLeaveMinutes = 0;
        if (currentTime.before(expectedEndTime)) {
            long earlyMs = expectedEndTime.getTime() - currentTime.getTime();
            earlyLeaveMinutes = (int) (earlyMs / (1000 * 60));
        }
        
        // Kiểm tra checkout muộn (sau 30 phút kết ca)
        boolean lateCheckout = false;
        BigDecimal lateCheckoutPenalty = BigDecimal.ZERO;
        
        long lateCheckoutMs = currentTime.getTime() - expectedEndTime.getTime() - (30 * 60 * 1000);
        if (lateCheckoutMs > 0) {
            lateCheckout = true;
            lateCheckoutPenalty = BigDecimal.valueOf(200000); // Phạt 200k
        }
        
        // Tính penalty cho về sớm (25% mỗi giờ, tương tự đến muộn)
        BigDecimal currentSalaryAdjustment = currentShift.getSalaryAdjustmentPercent() != null ? 
            currentShift.getSalaryAdjustmentPercent() : BigDecimal.valueOf(100.0);
        
        String adjustmentReason = currentShift.getAdjustmentReason();
        if (earlyLeaveMinutes > 0) {
            int earlyHours = (int) Math.ceil(earlyLeaveMinutes / 60.0);
            BigDecimal earlyPenaltyPercent = BigDecimal.valueOf(25 * earlyHours);
            currentSalaryAdjustment = currentSalaryAdjustment.subtract(earlyPenaltyPercent);
            
            if (currentSalaryAdjustment.compareTo(BigDecimal.ZERO) < 0) {
                currentSalaryAdjustment = BigDecimal.ZERO;
            }
            
            String earlyPenaltyReason = "Về sớm " + earlyLeaveMinutes + " phút - giảm " + earlyPenaltyPercent + "% lương";
            adjustmentReason = adjustmentReason == null ? earlyPenaltyReason : 
                adjustmentReason + "; " + earlyPenaltyReason;
        }
        
        // Scale salary adjustment to 2 decimal places
        currentSalaryAdjustment = currentSalaryAdjustment.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        // Scale penalty amount to 2 decimal places
        lateCheckoutPenalty = lateCheckoutPenalty.setScale(2, BigDecimal.ROUND_HALF_UP);
        
        System.out.println("Salary adjustment: " + currentSalaryAdjustment + "%, Penalty amount: " + lateCheckoutPenalty);
        
        // Tổng hợp penalty reason
        String penaltyReason = currentShift.getPenaltyReason();
        if (lateCheckout) {
            String lateCheckoutReason = "Không chốt ca đúng giờ - phạt 200,000 VNĐ";
            penaltyReason = penaltyReason == null ? lateCheckoutReason : 
                penaltyReason + "; " + lateCheckoutReason;
        }
        
        // Cập nhật ca làm với thông tin chính xác theo thời gian thực tế
        String sql = "UPDATE workshifts SET " +
                    "end_time = CURRENT_TIME, " +
                    "check_out_time = CURRENT_TIME, " +
                    "actual_hours = ?, " +
                    "overtime_hours = ?, " +
                    "early_leave_minutes = ?, " +
                    "penalty_amount = COALESCE(penalty_amount, 0) + ?, " +
                    "penalty_reason = ?, " +
                    "salary_adjustment_percent = ?, " +
                    "adjustment_reason = ?, " +
                    "auto_checkout_penalty = ?, " +
                    "status = 'COMPLETED', " +
                    "notes = ?, " +
                    "updated_at = CURRENT_TIMESTAMP " +
                    "WHERE shift_id = ?";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            System.out.println("=== THÔNG TIN TÍNH LƯƠNG CA LÀM VIỆC ===");
            System.out.println("Shift ID: " + currentShift.getShiftId());
            System.out.println("Thời gian thực tế làm việc: " + actualHours + " giờ");
            System.out.println("Thời gian overtime: " + overtimeHours + " giờ");
            System.out.println("Điều chỉnh lương: " + currentSalaryAdjustment + "%");
            System.out.println("Penalty: " + lateCheckoutPenalty + " VNĐ");
            if (currentShift.getFinalEarnings() != null) {
                System.out.println("Lương cuối cùng: " + currentShift.getFinalEarnings() + " VNĐ");
            }
            System.out.println("========================================");
            
            ps.setBigDecimal(1, actualHours);
            ps.setBigDecimal(2, overtimeHours);
            ps.setInt(3, earlyLeaveMinutes);
            ps.setBigDecimal(4, lateCheckoutPenalty);
            ps.setString(5, penaltyReason);
            ps.setBigDecimal(6, currentSalaryAdjustment);
            ps.setString(7, adjustmentReason);
            ps.setBoolean(8, lateCheckout);
            ps.setString(9, notes);
            ps.setInt(10, currentShift.getShiftId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("SQL Error in checkOutShift: " + e.getMessage());
            System.err.println("Actual hours: " + actualHours);
            System.err.println("Overtime hours: " + overtimeHours);
            System.err.println("Salary adjustment: " + currentSalaryAdjustment);
            System.err.println("Penalty amount: " + lateCheckoutPenalty);
            throw e;
        }
    }
    
    /**
     * Debug method: Lấy bất kỳ ca nào của nhân viên hôm nay
     */
    public WorkShift getAnyShiftToday(int employeeId) throws SQLException {
        String sql = "SELECT ws.*, e.full_name, s.hourly_wage " +
                    "FROM workshifts ws " +
                    "JOIN employees e ON ws.employee_id = e.employee_id " +
                    "LEFT JOIN salary s ON ws.employee_id = s.employee_id " +
                    "WHERE ws.employee_id = ? " +
                    "AND ws.shift_date = CURRENT_DATE " +
                    "ORDER BY ws.created_at DESC " +
                    "LIMIT 1";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToWorkShift(rs);
                }
            }
        }
        return null;
    }
    
    /**
     * Debug method: Tạo ca làm việc test cho nhân viên
     */
    public boolean createTestShift(int employeeId, String startTime, String endTime, String shiftType) throws SQLException {
        String sql = "INSERT INTO workshifts (employee_id, shift_date, shift_type, start_time, end_time, " +
                    "planned_hours, break_minutes, status, is_scheduled, salary_adjustment_percent) " +
                    "VALUES (?, CURRENT_DATE, ?, ?, ?, 4.0, 60, 'SCHEDULED', TRUE, 100.0)";
        
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setString(2, shiftType);
            ps.setTime(3, Time.valueOf(startTime + ":00"));
            ps.setTime(4, Time.valueOf(endTime + ":00"));
            
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