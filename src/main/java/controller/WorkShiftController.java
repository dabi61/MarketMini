package controller;

import dao.WorkShiftDAO;
import dao.EmployeeDAO;
import model.WorkShift;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.math.BigDecimal;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.sql.Date;

/**
 * Controller cho quản lý ca làm việc
 * @author macbook
 */
public class WorkShiftController {
    
    private WorkShiftDAO workShiftDAO;
    private EmployeeDAO employeeDAO;
    
    public WorkShiftController() {
        try {
            this.workShiftDAO = new WorkShiftDAO();
            this.employeeDAO = new EmployeeDAO();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khởi tạo: " + e.getMessage());
        }
    }
    
    /**
     * Lấy tất cả ca làm việc
     */
    public List<WorkShift> getAllShifts() {
        try {
            return workShiftDAO.getAllShifts();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải danh sách ca làm: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy ca làm việc theo nhân viên
     */
    public List<WorkShift> getShiftsByEmployee(int employeeId, int days) {
        try {
            return workShiftDAO.getShiftsByEmployee(employeeId, days);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải ca làm của nhân viên: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Lấy ca làm việc đang diễn ra của nhân viên
     */
    public WorkShift getCurrentShift(int employeeId) {
        try {
            return workShiftDAO.getCurrentShift(employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải ca làm hiện tại: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Tạo ca làm việc mới
     */
    public boolean createShift(WorkShift shift) {
        try {
            return workShiftDAO.createShift(shift);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tạo ca làm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Cập nhật ca làm việc
     */
    public boolean updateShift(WorkShift shift) {
        try {
            return workShiftDAO.updateShift(shift);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật ca làm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Xóa ca làm việc
     */
    public boolean deleteShift(int workingSessionId) {
        try {
            return workShiftDAO.deleteShift(workingSessionId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa ca làm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Bắt đầu ca làm việc (Staff check-in)
     */
    public boolean startShift(int employeeId) {
        try {
            return workShiftDAO.startShift(employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi bắt đầu ca làm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Chốt ca làm việc (Staff check-out)
     */
    public boolean closeShift(int employeeId) {
        try {
            return workShiftDAO.closeShift(employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi chốt ca làm: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Thống kê ca làm việc cho Staff
     */
    public Map<String, Object> getEmployeeShiftStats(int employeeId, int days) {
        try {
            return workShiftDAO.getEmployeeShiftStats(employeeId, days);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải thống kê: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Thống kê tổng quan ca làm việc (Admin)
     */
    public Map<String, Object> getShiftOverviewStats(int days) {
        try {
            return workShiftDAO.getShiftOverviewStats(days);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải thống kê tổng quan: " + e.getMessage());
            return new HashMap<>();
        }
    }
    
    /**
     * Lấy ca làm việc theo ngày
     */
    public List<WorkShift> getShiftsByDate(Date date) {
        try {
            return workShiftDAO.getShiftsByDate(date);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải ca làm theo ngày: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Tạo ca làm việc test
     */
    public boolean createTestShift(int employeeId) {
        try {
            return workShiftDAO.createTestShift(employeeId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tạo ca làm test: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Load danh sách ca làm việc vào table
     */
    public void loadShiftsToTable(JTable table) {
        List<WorkShift> shifts = getAllShifts();
        if (shifts != null) {
            updateTable(table, shifts);
        }
    }
    
    /**
     * Cập nhật table với danh sách ca làm việc
     */
    private void updateTable(JTable table, List<WorkShift> shifts) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setRowCount(0);
        
        for (WorkShift shift : shifts) {
            Object[] row = {
                shift.getWorkingSessionId(),
                shift.getEmployeeName(),
                shift.getDate(),
                shift.getLoginTime(),
                shift.getLogoutTime(),
                shift.getWorkingHours(),
                shift.getWorkStatus()
            };
            model.addRow(row);
        }
    }
    
    /**
     * Xử lý thêm ca làm việc
     */
    public boolean handleAddShift(int employeeId, Date date) {
        WorkShift shift = new WorkShift();
        shift.setEmployeeId(employeeId);
        shift.setDate(date);
        shift.setWorkStatus("SCHEDULED");
        
        boolean success = createShift(shift);
        if (success) {
            JOptionPane.showMessageDialog(null, "Thêm ca làm việc thành công!");
        } else {
            JOptionPane.showMessageDialog(null, "Thêm ca làm việc thất bại!");
        }
        return success;
    }
    
    /**
     * Xử lý xóa ca làm việc
     */
    public boolean handleDeleteShift(int workingSessionId) {
        int confirm = JOptionPane.showConfirmDialog(null, 
            "Bạn có chắc chắn muốn xóa ca làm việc này?", 
            "Xác nhận xóa", 
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = deleteShift(workingSessionId);
            if (success) {
                JOptionPane.showMessageDialog(null, "Xóa ca làm việc thành công!");
            } else {
                JOptionPane.showMessageDialog(null, "Xóa ca làm việc thất bại!");
            }
            return success;
        }
        return false;
    }
    
    /**
     * Xử lý bắt đầu ca làm việc
     */
    public boolean handleStartShift(int employeeId) {
        boolean success = startShift(employeeId);
        if (success) {
            JOptionPane.showMessageDialog(null, "Bắt đầu ca làm việc thành công!");
        } else {
            JOptionPane.showMessageDialog(null, "Không thể bắt đầu ca làm việc!");
        }
        return success;
    }
    
    /**
     * Xử lý chốt ca làm việc
     */
    public boolean handleCloseShift(int employeeId) {
        boolean success = closeShift(employeeId);
        if (success) {
            JOptionPane.showMessageDialog(null, "Chốt ca làm việc thành công!");
        } else {
            JOptionPane.showMessageDialog(null, "Không thể chốt ca làm việc!");
        }
        return success;
    }
    
    /**
     * Lấy thống kê ca làm việc của nhân viên
     */
    public Map<String, Object> getEmployeeStats(int employeeId, int days) {
        Map<String, Object> stats = getEmployeeShiftStats(employeeId, days);
        
        // Format dữ liệu để hiển thị
        if (stats.containsKey("total_hours")) {
            BigDecimal totalHours = (BigDecimal) stats.get("total_hours");
            if (totalHours != null) {
                stats.put("total_hours_formatted", totalHours.setScale(2, BigDecimal.ROUND_HALF_UP) + " giờ");
            }
        }
        
        if (stats.containsKey("estimated_earnings")) {
            BigDecimal earnings = (BigDecimal) stats.get("estimated_earnings");
            if (earnings != null) {
                stats.put("estimated_earnings_formatted", earnings.setScale(0, BigDecimal.ROUND_HALF_UP) + " VNĐ");
            }
        }
        
        return stats;
    }
    
    /**
     * Lấy thống kê tổng quan
     */
    public Map<String, Object> getOverviewStats(int days) {
        Map<String, Object> stats = getShiftOverviewStats(days);
        
        // Format dữ liệu để hiển thị
        if (stats.containsKey("total_work_hours")) {
            BigDecimal totalHours = (BigDecimal) stats.get("total_work_hours");
            if (totalHours != null) {
                stats.put("total_work_hours_formatted", totalHours.setScale(2, BigDecimal.ROUND_HALF_UP) + " giờ");
            }
        }
        
        return stats;
    }
    
    /**
     * Lấy ca làm việc theo ID
     */
    public WorkShift getShiftById(int workingSessionId) {
        try {
            return workShiftDAO.getShiftById(workingSessionId);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải chi tiết ca làm: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Đóng kết nối
     */
    public void cleanup() {
        if (workShiftDAO != null) {
            workShiftDAO.closeConnection();
        }
    }
} 