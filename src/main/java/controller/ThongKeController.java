/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ThongKeDAO;
import dao.WorkShiftDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Session;
import view.ThongKeView;
import com.raven.chart.ModelChartLine;
import com.raven.chart.ModelChartPie;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author macbook
 */
public class ThongKeController implements ActionListener {
    private ThongKeView thongKeView;
    private ThongKeDAO thongKeDAO;
    private WorkShiftDAO workShiftDAO;
    private int currentUserRole;
    private int currentEmployeeId;
    private DecimalFormat formatter = new DecimalFormat("#,###");
    
    public ThongKeController(ThongKeView thongKeView) {
        this.thongKeView = thongKeView;
        this.currentUserRole = Session.getInstance().getRole();
        this.currentEmployeeId = Session.getInstance().getEmployeeId();
        
        try {
            this.thongKeDAO = new ThongKeDAO();
            this.workShiftDAO = new WorkShiftDAO();
            
            // Cập nhật buttons theo role
            thongKeView.updateButtonsForRole(currentUserRole);
            
            loadStatisticsBasedOnRole();
        } catch (SQLException ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        
        try {
            switch (command) {
                case "START_SHIFT":
                    handleStartShift();
                    break;
                case "END_SHIFT":
                    handleEndShift();
                    break;
                case "DETAIL_REPORT":
                    openDetailReportForm();
                    break;
                case "SHIFT_MANAGEMENT":
                    openShiftManagementForm();
                    break;
                case "INVENTORY_DETAIL":
                    openInventoryDetailForm();
                    break;
                case "SALES_DETAIL":
                    openSalesDetailForm();
                    break;
                case "PERSONAL_STATS":
                    openPersonalStatsForm();
                    break;
                case "REFRESH":
                    loadStatisticsBasedOnRole();
                    break;
                default:
                    loadStatisticsBasedOnRole();
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi xử lý: " + ex.getMessage());
        } catch (Exception ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi hệ thống: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý bắt đầu ca cho nhân viên
     */
    private void handleStartShift() throws SQLException {
        System.out.println("DEBUG: handleStartShift called for employee " + currentEmployeeId);
        
        if (currentUserRole != 2) { // Chỉ staff mới được bắt đầu ca
            JOptionPane.showMessageDialog(null, "Chỉ nhân viên mới có thể bắt đầu ca!");
            return;
        }
        
        // CRITICAL: Kiểm tra có ca IN_PROGRESS không trước tiên
        model.WorkShift inProgressShift = workShiftDAO.getCurrentShift(currentEmployeeId);
        if (inProgressShift != null) {
            JOptionPane.showMessageDialog(null, 
                "Bạn đang có ca làm việc đang diễn ra (ID: " + inProgressShift.getShiftId() + ")!\n" +
                "Vui lòng kết thúc ca hiện tại trước khi bắt đầu ca mới.", 
                "Lỗi - Ca đang diễn ra", 
                JOptionPane.WARNING_MESSAGE);
            return; // Exit immediately
        }
        
        // Debug: Kiểm tra bất kỳ ca nào hôm nay
        model.WorkShift anyShift = workShiftDAO.getAnyShiftToday(currentEmployeeId);
        System.out.println("Any shift today: " + (anyShift != null ? anyShift.getShiftId() + " - " + anyShift.getStatus() : "null"));
        
        // Kiểm tra ca có thể bắt đầu không
        model.WorkShift currentShift = workShiftDAO.getScheduledShift(currentEmployeeId);
        System.out.println("Scheduled shift: " + (currentShift != null ? currentShift.getShiftId() + " - " + currentShift.getStatus() : "null"));
        
        // Kiểm tra nếu đã có ca COMPLETED hôm nay (chỉ khi không có ca IN_PROGRESS)
        model.WorkShift completedShift = workShiftDAO.getAnyShiftToday(currentEmployeeId);
        if (completedShift != null && "COMPLETED".equals(completedShift.getStatus().name())) {
            System.out.println("Found completed shift, creating new shift for additional work");
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Bạn đã hoàn thành ca làm việc hôm nay.\n" +
                "Bắt đầu ca bổ sung sẽ tính làm thêm giờ.\n" +
                "Bạn có muốn tiếp tục?", 
                "Xác nhận ca bổ sung", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = workShiftDAO.startUnscheduledShift(currentEmployeeId);
                if (success) {
                    JOptionPane.showMessageDialog(null, 
                        "Đã bắt đầu ca bổ sung!\nThời gian làm sẽ được tính làm thêm giờ.", 
                        "Thành Công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadStatisticsBasedOnRole();
                }
            }
            return; // Exit early after handling completed shift case
        }
        
        if (currentShift == null) {
            // Không có ca trong lịch - cho phép làm ngoài lịch với 75% lương
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Bạn không có ca trong lịch làm hôm nay.\n" +
                "Bắt đầu ca ngoài lịch sẽ chỉ tính 75% lương.\n" +
                "Bạn có muốn tiếp tục?", 
                "Xác nhận bắt đầu ca ngoài lịch", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = workShiftDAO.startUnscheduledShift(currentEmployeeId);
                if (success) {
                    JOptionPane.showMessageDialog(null, 
                        "Đã bắt đầu ca ngoài lịch!\nLương sẽ được tính 75%.", 
                        "Thành Công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadStatisticsBasedOnRole();
                }
            }
        } else if (currentShift.canCheckIn()) {
            // Có ca trong lịch - check in bình thường
            boolean success = workShiftDAO.checkInShift(currentEmployeeId);
            
            if (success) {
                JOptionPane.showMessageDialog(null, 
                    "Đã bắt đầu ca làm việc thành công!", 
                    "Thành Công", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadStatisticsBasedOnRole();
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Lỗi khi bắt đầu ca. Vui lòng thử lại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(null, 
                "Không thể bắt đầu ca làm việc!\nCa đã được bắt đầu hoặc đã kết thúc.", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Xử lý kết thúc ca cho nhân viên
     */
    private void handleEndShift() throws SQLException {
        if (currentUserRole != 2) { // Chỉ staff mới được kết thúc ca
            JOptionPane.showMessageDialog(null, "Chỉ nhân viên mới có thể kết thúc ca!");
            return;
        }
        
        // Kiểm tra có ca đang làm không
        model.WorkShift currentShift = workShiftDAO.getCurrentShift(currentEmployeeId);
        if (currentShift == null || !currentShift.canCheckOut()) {
            JOptionPane.showMessageDialog(null, "Bạn không có ca làm việc nào đang diễn ra!");
            return;
        }
        
        // Hỏi ghi chú khi kết thúc ca
        String notes = JOptionPane.showInputDialog(null, 
            "Nhập ghi chú cho ca làm việc (tùy chọn):", 
            "Kết Thúc Ca Làm Việc", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (notes == null) return; // User cancelled
        
        // Thực hiện kết thúc ca với penalty system
        boolean success = workShiftDAO.checkOutShift(currentEmployeeId, notes);
        
        if (success) {
            JOptionPane.showMessageDialog(null, 
                "Kết thúc ca thành công!\nCảm ơn bạn đã hoàn thành ca làm việc.", 
                "Thành Công", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh lại thống kê
            loadStatisticsBasedOnRole();
        } else {
            JOptionPane.showMessageDialog(null, 
                "Lỗi khi kết thúc ca. Vui lòng thử lại!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Load thống kê dựa trên role của user
     */
    private void loadStatisticsBasedOnRole() throws SQLException {
        if (currentUserRole == 1) { // Admin
            loadAdminStatistics();
        } else { // Staff
            loadStaffStatistics();
        }
    }
    
    /**
     * Load thống kê cho Admin - Toàn quyền
     */
    private void loadAdminStatistics() throws SQLException {
        LocalDate now = LocalDate.now();
        int currentMonth = now.getMonthValue();
        int currentYear = now.getYear();
        
        // 1. Thống kê doanh thu tháng hiện tại
        Map<String, Object> monthlyRevenue = thongKeDAO.getMonthlyRevenue(currentMonth, currentYear);
        
        // 2. Thống kê lợi nhuận
        Map<String, Object> profitAnalysis = thongKeDAO.getProfitAnalysis(currentMonth, currentYear);
        
        // 3. Hiệu suất nhân viên (bán hàng)
        List<Map<String, Object>> employeePerformance = thongKeDAO.getEmployeePerformance(currentMonth, currentYear);
        
        // 4. Tồn kho
        List<Map<String, Object>> inventoryStatus = thongKeDAO.getInventoryStatus();
        
        // 5. Thống kê khách hàng
        Map<String, Object> customerStats = thongKeDAO.getCustomerStats();
        
        // 6. Thống kê ca làm tổng quan
        Map<String, Object> shiftOverview = thongKeDAO.getShiftOverview(currentMonth, currentYear);
        
        // 7. Hiệu suất ca làm theo nhân viên
        List<Map<String, Object>> shiftPerformance = thongKeDAO.getShiftPerformanceByEmployee(currentMonth, currentYear);
        
        // Cập nhật giao diện
        updateAdminUI(monthlyRevenue, profitAnalysis, employeePerformance, inventoryStatus, 
                     customerStats, shiftOverview, shiftPerformance);
        
        // Cập nhật biểu đồ cho Admin
        updateAdminCharts(shiftPerformance, inventoryStatus);
    }
    
    /**
     * Load thống kê cho Staff - Quyền hạn chế
     */
    private void loadStaffStatistics() throws SQLException {
        System.out.println("DEBUG: loadStaffStatistics for employee " + currentEmployeeId);
        
        // Check existing shifts for debugging
        try {
            model.WorkShift existingShift = workShiftDAO.getAnyShiftToday(currentEmployeeId);
            System.out.println("Existing shift today: " + (existingShift != null ? 
                existingShift.getShiftId() + " - " + existingShift.getStatus() : "none"));
        } catch (SQLException e) {
            System.err.println("Error checking shift: " + e.getMessage());
        }
        
        // 1. Thống kê hôm nay
        Map<String, Object> todayStats = thongKeDAO.getTodayStats();
        
        // 2. Sản phẩm bán chạy (top 10)
        List<Map<String, Object>> topProducts = thongKeDAO.getTopSellingProducts(10);
        
        // 3. Doanh thu 7 ngày gần nhất
        List<Map<String, Object>> recentDays = thongKeDAO.getRecentDaysRevenue();
        
        // 4. Thống kê khách hàng
        Map<String, Object> customerStats = thongKeDAO.getCustomerStats();
        
        // 5. Thống kê ca làm của nhân viên
        Map<String, Object> shiftSummary = thongKeDAO.getEmployeeShiftSummary(currentEmployeeId);
        System.out.println("DEBUG: shiftSummary = " + shiftSummary);
        
        // 6. Lương dự kiến
        Map<String, Object> earningsEstimate = thongKeDAO.getEmployeeEarningsEstimate(currentEmployeeId);
        System.out.println("DEBUG: earningsEstimate = " + earningsEstimate);
        
        // 7. Ca làm gần đây
        List<Map<String, Object>> recentShifts = thongKeDAO.getRecentShifts(currentEmployeeId);
        
        // Cập nhật giao diện
        updateStaffUI(todayStats, topProducts, recentDays, customerStats, 
                     shiftSummary, earningsEstimate, recentShifts);
        
        // Cập nhật biểu đồ cho Staff
        updateStaffCharts(topProducts, recentDays);
    }
    
    /**
     * Cập nhật giao diện cho Admin
     */
    private void updateAdminUI(Map<String, Object> monthlyRevenue, 
                               Map<String, Object> profitAnalysis,
                               List<Map<String, Object>> employeePerformance,
                               List<Map<String, Object>> inventoryStatus,
                               Map<String, Object> customerStats,
                               Map<String, Object> shiftOverview,
                               List<Map<String, Object>> shiftPerformance) {
        
        // Cập nhật bảng hiệu suất ca làm thay vì bán hàng - Dùng Icon cho column đầu tiên
        String[] adminColumns = {"Ảnh", "Mã NV", "Họ tên", "Chức vụ", "Ca làm", "Giờ làm", "Lương (VNĐ)", "Chuyên cần"};
        DefaultTableModel adminModel = new DefaultTableModel(adminColumns, 0);
        
        // Icon mặc định cho nhân viên
        javax.swing.ImageIcon defaultIcon;
        try {
            defaultIcon = new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/1.png"));
        } catch (Exception e) {
            // Tạo icon trống nếu không load được
            defaultIcon = new javax.swing.ImageIcon();
        }
        
        for (Map<String, Object> emp : shiftPerformance) {
            int totalShifts = (Integer) emp.getOrDefault("total_shifts", 0);
            int completedShifts = (Integer) emp.getOrDefault("completed_shifts", 0);
            double attendanceRate = (Double) emp.getOrDefault("attendance_rate", 0.0);
            double totalHours = (Double) emp.getOrDefault("total_hours", 0.0);
            double totalEarnings = (Double) emp.getOrDefault("total_earnings", 0.0);
            
            Object[] row = {
                defaultIcon, // Column đầu tiên phải là Icon
                emp.get("employee_id"),
                emp.get("full_name"),
                emp.get("role"),
                String.format("%d/%d", completedShifts, totalShifts),
                String.format("%.1f", totalHours),
                formatter.format((long) totalEarnings),
                String.format("%.1f%%", attendanceRate)
            };
            adminModel.addRow(row);
        }
        
        thongKeView.getTable1().setModel(adminModel);
        thongKeView.getTable1().getColumnModel().getColumn(0).setPreferredWidth(50);
        thongKeView.getTable1().getColumnModel().getColumn(6).setPreferredWidth(120);
        thongKeView.getTable1().getColumnModel().getColumn(7).setPreferredWidth(80);
        
        // Cập nhật label thông tin với thiết kế đẹp hơn
        int totalShifts = (Integer) shiftOverview.getOrDefault("total_shifts", 0);
        int activeEmployees = (Integer) shiftOverview.getOrDefault("active_employees", 0);
        int completedShifts = (Integer) shiftOverview.getOrDefault("completed_shifts", 0);
        int ongoingShifts = (Integer) shiftOverview.getOrDefault("ongoing_shifts", 0);
        double totalHours = (Double) shiftOverview.getOrDefault("total_hours", 0.0);
        
        long revenue = (Long) monthlyRevenue.getOrDefault("total_revenue", 0L);
        long profit = (Long) profitAnalysis.getOrDefault("profit", 0L);
        int totalOrders = (Integer) monthlyRevenue.getOrDefault("total_orders", 0);
        int totalCustomers = (Integer) customerStats.getOrDefault("total_customers", 0);
        
        String adminInfo = String.format(
            "<html><div style='font-family: Arial; font-size: 12px;'>" +
            "<b style='color: #2E7D32; font-size: 14px;'>📊 DASHBOARD ADMIN - THÁNG %d/%d</b><br/><br/>" +
            
            "<table cellpadding='2' cellspacing='0'>" +
            "<tr><td><b style='color: #1976D2;'>💰 KINH DOANH</b></td><td width='20'></td><td><b style='color: #D32F2F;'>👥 NHÂN SỰ</b></td></tr>" +
            "<tr><td>• Doanh thu: <b>%s</b> VNĐ</td><td></td><td>• Tổng ca làm: <b>%d</b> ca</td></tr>" +
            "<tr><td>• Lợi nhuận: <b style='color: %s;'>%s</b> VNĐ</td><td></td><td>• Nhân viên hoạt động: <b>%d</b></td></tr>" +
            "<tr><td>• Đơn hàng: <b>%s</b> đơn</td><td></td><td>• Ca hoàn thành: <b>%d</b>/%d</td></tr>" +
            "<tr><td>• Khách hàng: <b>%s</b></td><td></td><td>• Ca đang diễn ra: <b style='color: green;'>%d</b></td></tr>" +
            "<tr><td colspan='3'><hr style='border: 1px solid #E0E0E0; margin: 5px 0;'></td></tr>" +
            "<tr><td colspan='3'>⏰ <b>Tổng giờ làm:</b> <b style='color: #FF6F00;'>%.1f</b> giờ | " +
            "📈 <b>Hiệu suất ca làm ↓</b></td></tr>" +
            "</table>" +
            "</div></html>",
            
            LocalDate.now().getMonthValue(), LocalDate.now().getYear(),
            formatter.format(revenue),
            totalShifts,
            profit >= 0 ? "green" : "red", formatter.format(profit),
            activeEmployees,
            formatter.format(totalOrders),
            completedShifts, totalShifts,
            formatter.format(totalCustomers),
            ongoingShifts,
            totalHours
        );
        
        thongKeView.getJLabel1().setText(adminInfo);
    }
    
    /**
     * Cập nhật giao diện cho Staff
     */
    private void updateStaffUI(Map<String, Object> todayStats,
                               List<Map<String, Object>> topProducts,
                               List<Map<String, Object>> recentDays,
                               Map<String, Object> customerStats,
                               Map<String, Object> shiftSummary,
                               Map<String, Object> earningsEstimate,
                               List<Map<String, Object>> recentShifts) {
        
        // Cập nhật bảng ca làm gần đây thay vì sản phẩm bán chạy
        String[] staffColumns = {"Ảnh", "Ngày", "Ca làm", "Giờ vào", "Giờ ra", "Trạng thái"};
        DefaultTableModel staffModel = new DefaultTableModel(staffColumns, 0);
        
        // Icon mặc định cho ca làm
        javax.swing.ImageIcon shiftIcon;
        try {
            shiftIcon = new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/3.png"));
        } catch (Exception e) {
            // Tạo icon trống nếu không load được
            shiftIcon = new javax.swing.ImageIcon();
        }
        
        for (Map<String, Object> shift : recentShifts) {
            String shiftType = (String) shift.get("shift_type");
            String displayShiftType = "";
            switch (shiftType) {
                case "SANG": displayShiftType = "Ca Sáng"; break;
                case "CHIEU": displayShiftType = "Ca Chiều"; break;
                case "TOI": displayShiftType = "Ca Tối"; break;
                case "FULL": displayShiftType = "Ca Nguyên"; break;
                default: displayShiftType = shiftType;
            }
            
            String status = (String) shift.get("status");
            String displayStatus = "";
            switch (status) {
                case "COMPLETED": displayStatus = "Hoàn thành"; break;
                case "IN_PROGRESS": displayStatus = "Đang làm"; break;
                case "SCHEDULED": displayStatus = "Đã lên lịch"; break;
                case "ABSENT": displayStatus = "Vắng mặt"; break;
                default: displayStatus = status;
            }
            
            Object[] row = {
                shiftIcon, // Column đầu tiên phải là Icon
                shift.get("shift_date"),
                displayShiftType,
                shift.get("start_time"),
                shift.get("end_time") != null ? shift.get("end_time") : "Chưa kết thúc",
                displayStatus
            };
            staffModel.addRow(row);
        }
        
        thongKeView.getTable1().setModel(staffModel);
        thongKeView.getTable1().getColumnModel().getColumn(0).setPreferredWidth(50);
        thongKeView.getTable1().getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Cập nhật label thông tin với ca làm và lương
        String todayShiftType = (String) shiftSummary.getOrDefault("today_shift_type", "Không có ca");
        String todayStatus = (String) shiftSummary.getOrDefault("today_status", "NO_SHIFT");
        
        int monthlyShifts = (Integer) shiftSummary.getOrDefault("monthly_shifts", 0);
        int completedShifts = (Integer) shiftSummary.getOrDefault("completed_shifts", 0);
        
        Object totalHoursObj = earningsEstimate.getOrDefault("total_hours", 0.0);
        double totalHours = totalHoursObj instanceof Number ? ((Number) totalHoursObj).doubleValue() : 0.0;
        
        Object totalEarningsObj = earningsEstimate.getOrDefault("total_earnings", 0.0);
        double totalEarnings = totalEarningsObj instanceof Number ? ((Number) totalEarningsObj).doubleValue() : 0.0;
        
        String staffInfo = String.format(
            "<html><b>THỐNG KÊ NHÂN VIÊN - %s</b><br/>" +
            "🎯 <b>CA LÀM HÔM NAY:</b> %s<br/>" +
            "📊 <b>CA TRONG THÁNG:</b> %d ca (%d hoàn thành)<br/>" +
            "⏰ <b>TỔNG GIỜ LÀM:</b> %.1f giờ<br/>" +
            "💰 <b>LƯƠNG DỰ KIẾN:</b> %s VNĐ<br/>" +
            "👥 <b>KHÁCH HÀNG:</b> %s | Ca làm 7 ngày ↓" +
            "%s</html>",
            Session.getInstance().getFullName(),
            todayShiftType,
            monthlyShifts,
            completedShifts,
            totalHours,
            formatter.format((long) totalEarnings),
            formatter.format((Integer) customerStats.getOrDefault("total_customers", 0)),
            "IN_PROGRESS".equals(todayStatus) ? 
                "<br/><font color='green'>✓ Đang trong ca làm việc</font>" : ""
        );
        
        thongKeView.getJLabel1().setText(staffInfo);
        
        // Debug shift status
        System.out.println("DEBUG ThongKeController - Employee ID: " + currentEmployeeId);
        System.out.println("Today shift type: " + todayShiftType);
        System.out.println("Today status: " + todayStatus);
        
        // Kiểm tra có ca nào đang IN_PROGRESS không (ưu tiên cao nhất)
        String actualCurrentStatus = todayStatus;
        try {
            model.WorkShift inProgressShift = workShiftDAO.getCurrentShift(currentEmployeeId);
            if (inProgressShift != null) {
                actualCurrentStatus = "IN_PROGRESS";
                System.out.println("Found IN_PROGRESS shift: " + inProgressShift.getShiftId() + ", overriding status");
            }
        } catch (SQLException e) {
            System.err.println("Error checking in-progress shift: " + e.getMessage());
        }
        
        System.out.println("Actual current status: " + actualCurrentStatus);
        
        // Hiển thị/ẩn nút ca làm việc - enhanced logic
        boolean canStartShift = "SCHEDULED".equals(actualCurrentStatus) || "NO_SHIFT".equals(actualCurrentStatus);
        boolean canEndShift = "IN_PROGRESS".equals(actualCurrentStatus);
        
        // Special case: Nếu ca đã COMPLETED và KHÔNG có ca IN_PROGRESS, cho phép tạo ca mới
        if ("COMPLETED".equals(actualCurrentStatus)) {
            canStartShift = true; // Cho phép bắt đầu ca mới
            System.out.println("Shift already completed, allowing new shift creation");
        }
        
        System.out.println("Can start shift: " + canStartShift);
        System.out.println("Can end shift: " + canEndShift);
        
        thongKeView.updateShiftButtons(canStartShift, canEndShift);
    }
    
    /**
     * Cập nhật biểu đồ cho Admin
     */
    private void updateAdminCharts(List<Map<String, Object>> shiftPerformance,
                                   List<Map<String, Object>> inventoryStatus) {
        
        // Biểu đồ tròn - Lương theo nhân viên (dựa trên ca làm)
        List<ModelChartPie> pieData = new ArrayList<>();
        Color[] colors = {
            new Color(4, 174, 243), new Color(215, 39, 250), new Color(44, 88, 236),
            new Color(21, 202, 87), new Color(127, 63, 255), new Color(238, 167, 35),
            new Color(245, 79, 99)
        };
        
        int colorIndex = 0;
        for (Map<String, Object> emp : shiftPerformance.subList(0, Math.min(7, shiftPerformance.size()))) {
            String label = (String) emp.get("full_name");
            Double earnings = (Double) emp.getOrDefault("total_earnings", 0.0);
            if (earnings > 0) {
                pieData.add(new ModelChartPie(label, earnings, colors[colorIndex % colors.length]));
                colorIndex++;
            }
        }
        thongKeView.getChartPie().setModel(pieData);
        
        // Biểu đồ line - Chuyên cần nhân viên (attendance rate)
        List<ModelChartLine> lineData = new ArrayList<>();
        for (int i = 0; i < Math.min(7, shiftPerformance.size()); i++) {
            Map<String, Object> emp = shiftPerformance.get(i);
            String empName = ((String) emp.get("full_name"));
            // Lấy tên ngắn gọn
            String shortName = empName.contains(" ") ? 
                empName.substring(empName.lastIndexOf(" ") + 1) : empName;
            if (shortName.length() > 8) {
                shortName = shortName.substring(0, 8);
            }
            
            Double attendanceRate = (Double) emp.getOrDefault("attendance_rate", 0.0);
            lineData.add(new ModelChartLine(shortName, attendanceRate));
        }
        thongKeView.getChartLine1().setModel(lineData);
    }
    
    /**
     * Cập nhật biểu đồ cho Staff
     */
    private void updateStaffCharts(List<Map<String, Object>> topProducts,
                                   List<Map<String, Object>> recentDays) {
        
        // Biểu đồ tròn - Top sản phẩm bán chạy
        List<ModelChartPie> pieData = new ArrayList<>();
        Color[] colors = {
            new Color(21, 202, 87), new Color(4, 174, 243), new Color(215, 39, 250),
            new Color(44, 88, 236), new Color(127, 63, 255), new Color(238, 167, 35),
            new Color(245, 79, 99)
        };
        
        if (topProducts != null && !topProducts.isEmpty()) {
            int colorIndex = 0;
            int maxItems = Math.min(5, topProducts.size());
            for (int i = 0; i < maxItems; i++) {
                Map<String, Object> product = topProducts.get(i);
                if (product != null) {
                    String productName = (String) product.get("product_name");
                    if (productName == null || productName.trim().isEmpty()) {
                        productName = "Sản phẩm " + (i + 1);
                    }
                    String label = productName.substring(0, Math.min(12, productName.length()));
                    Integer sold = (Integer) product.getOrDefault("total_sold", 0);
                    if (sold > 0) {
                        pieData.add(new ModelChartPie(label, sold, colors[colorIndex % colors.length]));
                        colorIndex++;
                    }
                }
            }
        }
        
        // Nếu không có dữ liệu, thêm một item mặc định
        if (pieData.isEmpty()) {
            pieData.add(new ModelChartPie("Chưa có dữ liệu", 1, colors[0]));
        }
        thongKeView.getChartPie().setModel(pieData);
        
        // Biểu đồ line - Doanh thu 7 ngày gần nhất
        List<ModelChartLine> lineData = new ArrayList<>();
        if (recentDays != null && !recentDays.isEmpty()) {
            for (Map<String, Object> day : recentDays) {
                if (day != null && day.get("date") != null) {
                    String dateStr = day.get("date").toString();
                    String date = dateStr.length() > 5 ? dateStr.substring(5) : dateStr; // MM-dd
                    Long revenue = (Long) day.getOrDefault("daily_revenue", 0L);
                    lineData.add(new ModelChartLine(date, revenue.doubleValue() / 1000)); // Chia 1000 cho dễ đọc
                }
            }
        }
        
        // Nếu không có dữ liệu, thêm dữ liệu mặc định
        if (lineData.isEmpty()) {
            lineData.add(new ModelChartLine("Hôm nay", 0));
        }
        thongKeView.getChartLine1().setModel(lineData);
    }
    
    /**
     * Refresh dữ liệu thống kê
     */
    public void refreshStatistics() {
        try {
            loadStatisticsBasedOnRole();
        } catch (SQLException ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi tải lại dữ liệu: " + ex.getMessage());
        }
    }
    
    /**
     * Mở form báo cáo chi tiết
     */
    private void openDetailReportForm() {
        try {
            view.DetailReportForm detailReportForm = new view.DetailReportForm(currentUserRole, currentEmployeeId);
            detailReportForm.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi mở báo cáo chi tiết: " + ex.getMessage());
        }
    }
    
    /**
     * Mở form quản lý ca làm (chỉ admin)
     */
    private void openShiftManagementForm() {
        if (currentUserRole != 1) {
            JOptionPane.showMessageDialog(null, "Chỉ admin mới có quyền quản lý ca làm!");
            return;
        }
        
        try {
            view.WorkShiftForm workShiftForm = new view.WorkShiftForm();
            workShiftForm.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi mở quản lý ca làm: " + ex.getMessage());
        }
    }
    
    /**
     * Mở form chi tiết kho
     */
    private void openInventoryDetailForm() {
        try {
            view.InventoryDetailForm inventoryForm = new view.InventoryDetailForm(currentUserRole);
            inventoryForm.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi mở chi tiết kho: " + ex.getMessage());
        }
    }
    
    /**
     * Mở form chi tiết bán hàng
     */
    private void openSalesDetailForm() {
        try {
            view.SalesDetailForm salesForm = new view.SalesDetailForm(currentUserRole, currentEmployeeId);
            salesForm.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi mở chi tiết bán hàng: " + ex.getMessage());
        }
    }
    
    /**
     * Mở form thống kê cá nhân (chỉ staff)
     */
    private void openPersonalStatsForm() {
        if (currentUserRole != 2) {
            JOptionPane.showMessageDialog(null, "Chức năng này chỉ dành cho nhân viên!");
            return;
        }
        
        try {
            view.PersonalStatsForm personalForm = new view.PersonalStatsForm(currentEmployeeId);
            personalForm.setVisible(true);
        } catch (Exception ex) {
            Logger.getLogger(ThongKeController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "Lỗi mở thống kê cá nhân: " + ex.getMessage());
        }
    }
    
    /**
     * Giải phóng tài nguyên
     */
    public void cleanup() {
        if (thongKeDAO != null) {
            thongKeDAO.closeConnection();
        }
        if (workShiftDAO != null) {
            workShiftDAO.closeConnection();
        }
    }
}
