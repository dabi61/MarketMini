/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ThongKeDAO;
import dao.WorkShiftDAO;
import model.WorkShift;
import view.ThongKeView;
import view.DetailReportForm;
import view.WorkShiftForm;
import view.InventoryDetailForm;
import view.SalesDetailForm;
import view.PersonalStatsForm;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

/**
 * Controller cho quản lý thống kê
 * @author macbook
 */
public class ThongKeController implements ActionListener {
    
    private ThongKeView thongKeView;
    private ThongKeDAO thongKeDAO;
    private WorkShiftDAO workShiftDAO;
    private int currentUserRole;
    private int currentEmployeeId;
    private DecimalFormat formatter = new DecimalFormat("đ #,###");
    
    public ThongKeController(ThongKeView thongKeView) throws SQLException {
        this.thongKeView = thongKeView;
        this.thongKeDAO = new ThongKeDAO();
        this.workShiftDAO = new WorkShiftDAO();
        
        // Lấy thông tin user từ session
        try {
            this.currentUserRole = model.Session.getInstance().getRole();
            this.currentEmployeeId = model.Session.getInstance().getEmployeeId();
        } catch (Exception e) {
            this.currentUserRole = -1;
            this.currentEmployeeId = -1;
            System.err.println("Lỗi khi lấy thông tin session: " + e.getMessage());
        }
        
        // Load thống kê ban đầu
        try {
            loadStatisticsBasedOnRole();
            // Cập nhật trạng thái nút ca làm việc
            thongKeView.updateShiftButtonStates();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tải thống kê: " + e.getMessage());
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            // Xử lý các sự kiện từ ThongKeView
            String command = e.getActionCommand();
            
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
                default:
                    // Xử lý các sự kiện từ button text
                    if (e.getSource() instanceof com.raven.swing.Button) {
                        com.raven.swing.Button button = (com.raven.swing.Button) e.getSource();
                        String buttonText = button.getText();
                        
                        if (buttonText.contains("Bắt Đầu Ca")) {
                            handleStartShift();
                        } else if (buttonText.contains("Kết Thúc Ca")) {
                            handleEndShift();
                        } else if (buttonText.contains("Báo Cáo Chi Tiết")) {
                            openDetailReportForm();
                        } else if (buttonText.contains("Quản Lý Ca Làm")) {
                            openShiftManagementForm();
                        } else if (buttonText.contains("Chi Tiết Kho")) {
                            openInventoryDetailForm();
                        } else if (buttonText.contains("Chi Tiết Bán Hàng")) {
                            openSalesDetailForm();
                        } else if (buttonText.contains("Thống Kê Cá Nhân")) {
                            openPersonalStatsForm();
                        }
                    }
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi: " + ex.getMessage());
        }
    }
    
    /**
     * Xử lý bắt đầu ca cho nhân viên
     */
    private void handleStartShift() throws SQLException {
        if (currentUserRole != 2) { // Chỉ staff mới được bắt đầu ca
            JOptionPane.showMessageDialog(null, "Chỉ nhân viên mới có thể bắt đầu ca!");
            return;
        }
        
        // CRITICAL: Kiểm tra có ca IN_PROGRESS không trước tiên
        model.WorkShift inProgressShift = workShiftDAO.getCurrentShift(currentEmployeeId);
        if (inProgressShift != null) {
            JOptionPane.showMessageDialog(null, 
                "Bạn đang có ca làm việc đang diễn ra (ID: " + inProgressShift.getWorkingSessionId() + ")!\n" +
                "Vui lòng kết thúc ca hiện tại trước khi bắt đầu ca mới.", 
                "Lỗi - Ca đang diễn ra", 
                JOptionPane.WARNING_MESSAGE);
            return; // Exit immediately
        }
        
        // Kiểm tra nếu đã có ca COMPLETED hôm nay (chỉ khi không có ca IN_PROGRESS)
        List<model.WorkShift> todayShifts = workShiftDAO.getShiftsByEmployee(currentEmployeeId, 1);
        boolean hasCompletedShift = todayShifts.stream()
            .anyMatch(shift -> "COMPLETED".equals(shift.getWorkStatus()));
        
        if (hasCompletedShift) {
            System.out.println("Found completed shift, creating new shift for additional work");
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Bạn đã hoàn thành ca làm việc hôm nay.\n" +
                "Bắt đầu ca bổ sung sẽ tính làm thêm giờ.\n" +
                "Bạn có muốn tiếp tục?", 
                "Xác nhận ca bổ sung", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Tạo ca làm việc mới
                model.WorkShift newShift = new model.WorkShift();
                newShift.setEmployeeId(currentEmployeeId);
                newShift.setDate(java.sql.Date.valueOf(LocalDate.now()));
                newShift.setWorkStatus("SCHEDULED");
                
                if (workShiftDAO.createShift(newShift)) {
                    // Bắt đầu ca ngay lập tức
                    if (workShiftDAO.startShift(currentEmployeeId)) {
                    JOptionPane.showMessageDialog(null, 
                        "Đã bắt đầu ca bổ sung!\nThời gian làm sẽ được tính làm thêm giờ.", 
                        "Thành Công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadStatisticsBasedOnRole();
                        // Cập nhật trạng thái nút
                        thongKeView.updateShiftButtonStates();
                        // Force hiển thị nút kết thúc ca
                        thongKeView.forceShowEndShiftButton();
                    }
                }
            }
            return; // Exit early after handling completed shift case
        }
        
        // Kiểm tra có ca SCHEDULED không
        boolean hasScheduledShift = todayShifts.stream()
            .anyMatch(shift -> "SCHEDULED".equals(shift.getWorkStatus()));
        
        if (!hasScheduledShift) {
            // Không có ca trong lịch - cho phép làm ngoài lịch
            int confirm = JOptionPane.showConfirmDialog(null, 
                "Bạn không có ca trong lịch làm hôm nay.\n" +
                "Bắt đầu ca ngoài lịch.\n" +
                "Bạn có muốn tiếp tục?", 
                "Xác nhận bắt đầu ca ngoài lịch", 
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Tạo ca làm việc mới
                model.WorkShift newShift = new model.WorkShift();
                newShift.setEmployeeId(currentEmployeeId);
                newShift.setDate(java.sql.Date.valueOf(LocalDate.now()));
                newShift.setWorkStatus("SCHEDULED");
                
                if (workShiftDAO.createShift(newShift)) {
                    // Bắt đầu ca ngay lập tức
                    if (workShiftDAO.startShift(currentEmployeeId)) {
                    JOptionPane.showMessageDialog(null, 
                            "Đã bắt đầu ca ngoài lịch!", 
                        "Thành Công", 
                        JOptionPane.INFORMATION_MESSAGE);
                    loadStatisticsBasedOnRole();
                        // Cập nhật trạng thái nút
                        thongKeView.updateShiftButtonStates();
                        // Force hiển thị nút kết thúc ca
                        thongKeView.forceShowEndShiftButton();
                    }
                }
            }
        } else {
            // Có ca trong lịch - check in bình thường
            boolean success = workShiftDAO.startShift(currentEmployeeId);
            
            if (success) {
                JOptionPane.showMessageDialog(null, 
                    "Đã bắt đầu ca làm việc thành công!", 
                    "Thành Công", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadStatisticsBasedOnRole();
                // Cập nhật trạng thái nút
                thongKeView.updateShiftButtonStates();
                // Force hiển thị nút kết thúc ca
                thongKeView.forceShowEndShiftButton();
            } else {
                JOptionPane.showMessageDialog(null, 
                    "Lỗi khi bắt đầu ca. Vui lòng thử lại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
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
        if (currentShift == null || !"IN_PROGRESS".equals(currentShift.getWorkStatus())) {
            JOptionPane.showMessageDialog(null, "Bạn không có ca làm việc nào đang diễn ra!");
            return;
        }
        
        // Thực hiện kết thúc ca
        boolean success = workShiftDAO.closeShift(currentEmployeeId);
        
        if (success) {
            JOptionPane.showMessageDialog(null, 
                "Kết thúc ca thành công!\nCảm ơn bạn đã hoàn thành ca làm việc.", 
                "Thành Công", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh lại thống kê
            loadStatisticsBasedOnRole();
            // Cập nhật trạng thái nút
            thongKeView.updateShiftButtonStates();
            // Force ẩn nút kết thúc ca
            thongKeView.hideEndShiftButton();
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
        
        // 1. Thống kê hôm nay
        Map<String, Object> todayStats = thongKeDAO.getTodayStats();
        
        // 2. Sản phẩm bán chạy (top 10)
        List<Map<String, Object>> topProducts = thongKeDAO.getTopSellingProducts(10);
        
        // 3. Thống kê 7 ngày gần nhất
        List<Map<String, Object>> recentDays = thongKeDAO.getRecentDaysStats(7);
        
        // 4. Thống kê khách hàng
        Map<String, Object> customerStats = thongKeDAO.getCustomerStats();
        
        // 5. Thống kê ca làm của nhân viên
        Map<String, Object> shiftSummary = thongKeDAO.getEmployeeShiftStats(currentEmployeeId, 30);
        
        // 6. Ước tính thu nhập
        Map<String, Object> earningsEstimate = thongKeDAO.getEmployeeEarningsEstimate(currentEmployeeId);
        
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
        
        // Cập nhật các label thống kê
        if (monthlyRevenue != null) {
            thongKeView.getJLabel1().setText("Doanh thu: " + formatter.format(monthlyRevenue.get("total_revenue")));
        }
        
        // Cập nhật bảng hiệu suất nhân viên
        updateEmployeePerformanceTable(employeePerformance);
        
        // Cập nhật bảng tồn kho
        updateInventoryTable(inventoryStatus);
        
        // Cập nhật bảng hiệu suất ca làm
        updateShiftPerformanceTable(shiftPerformance);
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
        
        // Cập nhật thống kê hôm nay
        if (todayStats != null) {
            thongKeView.getJLabel1().setText("Hôm nay: " + formatter.format(todayStats.get("today_revenue")));
        }
        
        // Cập nhật bảng sản phẩm bán chạy
        updateTopProductsTable(topProducts);
        
        // Cập nhật bảng thống kê 7 ngày
        updateRecentDaysTable(recentDays);
        
        // Cập nhật bảng ca làm gần đây
        updateRecentShiftsTable(recentShifts);
    }
    
    /**
     * Cập nhật bảng hiệu suất nhân viên
     */
    private void updateEmployeePerformanceTable(List<Map<String, Object>> employeePerformance) {
        // Implementation for employee performance table
        System.out.println("Updating employee performance table with " + employeePerformance.size() + " records");
    }
    
    /**
     * Cập nhật bảng tồn kho
     */
    private void updateInventoryTable(List<Map<String, Object>> inventoryStatus) {
        // Implementation for inventory table
        System.out.println("Updating inventory table with " + inventoryStatus.size() + " records");
    }
    
    /**
     * Cập nhật bảng hiệu suất ca làm
     */
    private void updateShiftPerformanceTable(List<Map<String, Object>> shiftPerformance) {
        // Implementation for shift performance table
        System.out.println("Updating shift performance table with " + shiftPerformance.size() + " records");
    }
    
    /**
     * Cập nhật bảng sản phẩm bán chạy
     */
    private void updateTopProductsTable(List<Map<String, Object>> topProducts) {
        // Implementation for top products table
        System.out.println("Updating top products table with " + topProducts.size() + " records");
    }
    
    /**
     * Cập nhật bảng thống kê 7 ngày
     */
    private void updateRecentDaysTable(List<Map<String, Object>> recentDays) {
        // Implementation for recent days table
        System.out.println("Updating recent days table with " + recentDays.size() + " records");
    }
    
    /**
     * Cập nhật bảng ca làm gần đây
     */
    private void updateRecentShiftsTable(List<Map<String, Object>> recentShifts) {
        // Implementation for recent shifts table
        System.out.println("Updating recent shifts table with " + recentShifts.size() + " records");
    }
    
    /**
     * Cập nhật biểu đồ cho Admin
     */
    private void updateAdminCharts(List<Map<String, Object>> shiftPerformance,
                                   List<Map<String, Object>> inventoryStatus) {
        try {
            // 1. Biểu đồ line chart - Doanh thu theo ngày trong tuần
            List<Map<String, Object>> weeklyRevenue = thongKeDAO.getWeeklyRevenueData();
            updateLineChartForRevenue(weeklyRevenue);
            
            // 2. Biểu đồ pie chart - Phân bố doanh thu theo danh mục
            List<Map<String, Object>> categoryRevenue = thongKeDAO.getCategoryRevenueData();
            updatePieChartForCategoryRevenue(categoryRevenue);
            
            // 3. Biểu đồ line chart - Doanh thu theo tháng
            List<Map<String, Object>> monthlyTrend = thongKeDAO.getMonthlyRevenueTrend();
            updateLineChartForMonthlyTrend(monthlyTrend);
            

            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi cập nhật biểu đồ Admin: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật biểu đồ cho Staff
     */
    private void updateStaffCharts(List<Map<String, Object>> topProducts,
                                   List<Map<String, Object>> recentDays) {
        try {
            // 1. Biểu đồ line chart - Ca làm việc theo ngày trong tuần
            List<Map<String, Object>> weeklyShifts = thongKeDAO.getWeeklyShiftData();
            updateLineChartForShifts(weeklyShifts);
            
            // 2. Biểu đồ pie chart - Top 5 sản phẩm bán chạy
            List<Map<String, Object>> topProductsPie = thongKeDAO.getTopProductsPieData();
            updatePieChartForTopProducts(topProductsPie);
            
            // 3. Biểu đồ line chart - Doanh thu cá nhân theo ngày
            List<Map<String, Object>> personalRevenue = thongKeDAO.getRecentDaysRevenue();
            updateLineChartForPersonalRevenue(personalRevenue);
            
            // 4. Biểu đồ pie chart - Phân bố doanh thu theo danh mục (cá nhân)
            List<Map<String, Object>> personalCategoryRevenue = thongKeDAO.getCategoryRevenueData();
            updatePieChartForPersonalCategoryRevenue(personalCategoryRevenue);
            
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Lỗi khi cập nhật biểu đồ Staff: " + e.getMessage());
        }
    }
    
    /**
     * Cập nhật line chart cho doanh thu theo ngày
     */
    private void updateLineChartForRevenue(List<Map<String, Object>> weeklyRevenue) {
        List<com.raven.chart.ModelChartLine> chartData = new ArrayList<>();
        
        for (Map<String, Object> day : weeklyRevenue) {
            String dayName = (String) day.get("day_name");
            Long revenue = (Long) day.get("daily_revenue");
            chartData.add(new com.raven.chart.ModelChartLine(dayName, revenue != null ? revenue.intValue() : 0));
        }
        
        if (thongKeView.getChartLine1() != null) {
            thongKeView.getChartLine1().setModel(chartData);
        }
    }
    
    /**
     * Cập nhật pie chart cho phân bố doanh thu theo danh mục
     */
    private void updatePieChartForCategoryRevenue(List<Map<String, Object>> categoryRevenue) {
        List<com.raven.chart.ModelChartPie> chartData = new ArrayList<>();
        java.awt.Color[] colors = {
            new java.awt.Color(4, 174, 243),   // Xanh dương
            new java.awt.Color(215, 39, 250),  // Tím
            new java.awt.Color(44, 88, 236),   // Xanh đậm
            new java.awt.Color(21, 202, 87),   // Xanh lá
            new java.awt.Color(127, 63, 255),  // Tím đậm
            new java.awt.Color(238, 167, 35),  // Cam
            new java.awt.Color(245, 79, 99)    // Đỏ
        };
        
        for (int i = 0; i < categoryRevenue.size(); i++) {
            Map<String, Object> category = categoryRevenue.get(i);
            String categoryName = (String) category.get("category_name");
            Long revenue = (Long) category.get("category_revenue");
            java.awt.Color color = colors[i % colors.length];
            
            chartData.add(new com.raven.chart.ModelChartPie(
                categoryName, 
                revenue != null ? revenue.intValue() : 0, 
                color
            ));
        }
        
        if (thongKeView.getChartPie() != null) {
            thongKeView.getChartPie().setModel(chartData);
        }
    }
    
    /**
     * Cập nhật line chart cho doanh thu theo tháng
     */
    private void updateLineChartForMonthlyTrend(List<Map<String, Object>> monthlyTrend) {
        List<com.raven.chart.ModelChartLine> chartData = new ArrayList<>();
        
        for (Map<String, Object> month : monthlyTrend) {
            String monthName = (String) month.get("month_name");
            Long revenue = (Long) month.get("monthly_revenue");
            chartData.add(new com.raven.chart.ModelChartLine(monthName, revenue != null ? revenue.intValue() : 0));
        }
        
        if (thongKeView.getChartLine1() != null) {
            thongKeView.getChartLine1().setModel(chartData);
        }
    }
    
    /**
     * Cập nhật line chart cho ca làm việc theo ngày
     */
    private void updateLineChartForShifts(List<Map<String, Object>> weeklyShifts) {
        List<com.raven.chart.ModelChartLine> chartData = new ArrayList<>();
        
        for (Map<String, Object> day : weeklyShifts) {
            String dayName = (String) day.get("day_name");
            Double hours = (Double) day.get("total_hours");
            chartData.add(new com.raven.chart.ModelChartLine(dayName, hours != null ? hours.intValue() : 0));
        }
        
        if (thongKeView.getChartLine1() != null) {
            thongKeView.getChartLine1().setModel(chartData);
        }
    }
    
    /**
     * Cập nhật pie chart cho top sản phẩm bán chạy
     */
    private void updatePieChartForTopProducts(List<Map<String, Object>> topProducts) {
        List<com.raven.chart.ModelChartPie> chartData = new ArrayList<>();
        java.awt.Color[] colors = {
            new java.awt.Color(4, 174, 243),   // Xanh dương
            new java.awt.Color(215, 39, 250),  // Tím
            new java.awt.Color(44, 88, 236),   // Xanh đậm
            new java.awt.Color(21, 202, 87),   // Xanh lá
            new java.awt.Color(127, 63, 255)   // Tím đậm
        };
        
        for (int i = 0; i < topProducts.size(); i++) {
            Map<String, Object> product = topProducts.get(i);
            String productName = (String) product.get("product_name");
            Integer sold = (Integer) product.get("total_sold");
            java.awt.Color color = colors[i % colors.length];
            
            chartData.add(new com.raven.chart.ModelChartPie(
                productName, 
                sold != null ? sold : 0, 
                color
            ));
        }
        
        if (thongKeView.getChartPie() != null) {
            thongKeView.getChartPie().setModel(chartData);
        }
    }
    
    /**
     * Cập nhật line chart cho doanh thu cá nhân
     */
    private void updateLineChartForPersonalRevenue(List<Map<String, Object>> personalRevenue) {
        List<com.raven.chart.ModelChartLine> chartData = new ArrayList<>();
        
        for (Map<String, Object> day : personalRevenue) {
            String date = day.get("date").toString();
            Long revenue = (Long) day.get("daily_revenue");
            chartData.add(new com.raven.chart.ModelChartLine(date, revenue != null ? revenue.intValue() : 0));
        }
        
        if (thongKeView.getChartLine1() != null) {
            thongKeView.getChartLine1().setModel(chartData);
        }
    }
    
    /**
     * Cập nhật pie chart cho phân bố doanh thu cá nhân theo danh mục
     */
    private void updatePieChartForPersonalCategoryRevenue(List<Map<String, Object>> categoryRevenue) {
        List<com.raven.chart.ModelChartPie> chartData = new ArrayList<>();
        java.awt.Color[] colors = {
            new java.awt.Color(4, 174, 243),   // Xanh dương
            new java.awt.Color(215, 39, 250),  // Tím
            new java.awt.Color(44, 88, 236),   // Xanh đậm
            new java.awt.Color(21, 202, 87),   // Xanh lá
            new java.awt.Color(127, 63, 255),  // Tím đậm
            new java.awt.Color(238, 167, 35),  // Cam
            new java.awt.Color(245, 79, 99)    // Đỏ
        };
        
        for (int i = 0; i < categoryRevenue.size(); i++) {
            Map<String, Object> category = categoryRevenue.get(i);
            String categoryName = (String) category.get("category_name");
            Long revenue = (Long) category.get("category_revenue");
            java.awt.Color color = colors[i % colors.length];
            
            chartData.add(new com.raven.chart.ModelChartPie(
                categoryName, 
                revenue != null ? revenue.intValue() : 0, 
                color
            ));
        }
        
        if (thongKeView.getChartPie() != null) {
            thongKeView.getChartPie().setModel(chartData);
        }
    }
    
    /**
     * Cập nhật trạng thái nút ca làm việc
     */
    public void updateShiftButtonStates() {
        if (thongKeView != null) {
            thongKeView.updateShiftButtonStates();
        }
    }
    
    /**
     * Refresh thống kê
     */
    public void refreshStatistics() {
        try {
            loadStatisticsBasedOnRole();
            // Cập nhật trạng thái nút ca làm việc
            updateShiftButtonStates();
            JOptionPane.showMessageDialog(null, "Đã làm mới thống kê!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi làm mới thống kê: " + e.getMessage());
        }
    }
    
    /**
     * Mở form báo cáo chi tiết
     */
    private void openDetailReportForm() {
        try {
            view.DetailReportForm detailForm = new view.DetailReportForm(currentUserRole, currentEmployeeId);
            detailForm.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi mở báo cáo chi tiết: " + e.getMessage());
        }
    }
    
    /**
     * Mở form quản lý ca làm việc
     */
    private void openShiftManagementForm() {
        if (currentUserRole == 1) { // Chỉ Admin mới được truy cập
            try {
                WorkShiftForm shiftForm = new WorkShiftForm();
                JFrame frame = new JFrame("Quản lý ca làm việc");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setSize(1200, 800);
                frame.setLocationRelativeTo(null);
                frame.add(shiftForm);
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Lỗi mở quản lý ca làm việc: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Chỉ Admin mới có quyền truy cập quản lý ca làm việc!");
        }
    }
    
    /**
     * Mở form chi tiết tồn kho
     */
    private void openInventoryDetailForm() {
        try {
            view.InventoryDetailForm inventoryForm = new view.InventoryDetailForm(currentUserRole);
            inventoryForm.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi mở chi tiết kho: " + e.getMessage());
        }
    }
    
    /**
     * Mở form chi tiết bán hàng
     */
    private void openSalesDetailForm() {
        try {
            view.SalesDetailForm salesForm = new view.SalesDetailForm(currentUserRole, currentEmployeeId);
            salesForm.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi mở chi tiết bán hàng: " + e.getMessage());
        }
    }
    
    /**
     * Mở form thống kê cá nhân
     */
    private void openPersonalStatsForm() {
        try {
            view.PersonalStatsForm personalForm = new view.PersonalStatsForm(currentEmployeeId);
            personalForm.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi mở thống kê cá nhân: " + e.getMessage());
        }
    }
    
    /**
     * Cleanup resources
     */
    public void cleanup() {
        if (workShiftDAO != null) {
            workShiftDAO.closeConnection();
        }
        if (thongKeDAO != null) {
            thongKeDAO.closeConnection();
        }
    }
}
