package view;

import dao.ThongKeDAO;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Form báo cáo chi tiết với phân quyền Admin/Staff
 */
public class DetailReportForm extends JFrame {
    
    private int userRole;
    private int employeeId;
    private ThongKeDAO thongKeDAO;
    
    // Components
    private JTabbedPane tabbedPane;
    private JPanel revenuePanel;
    private JPanel employeePanel;
    private JPanel inventoryPanel;
    private JPanel personalPanel;
    
    public DetailReportForm(int userRole, int employeeId) {
        this.userRole = userRole;
        this.employeeId = employeeId;
        
        try {
            this.thongKeDAO = new ThongKeDAO();
            initComponents();
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(DetailReportForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + ex.getMessage());
        }
    }
    
    private void initComponents() {
        setTitle("📊 Báo Cáo Chi Tiết - " + (userRole == 1 ? "Admin" : "Nhân Viên"));
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Setup tabs based on role
        if (userRole == 1) { // Admin tabs
            setupAdminTabs();
        } else { // Staff tabs
            setupStaffTabs();
        }
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add close button
        JPanel buttonPanel = new JPanel();
        JButton closeButton = new JButton("Đóng");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void setupAdminTabs() {
        // Tab 1: Doanh thu chi tiết
        revenuePanel = createScrollablePanel();
        tabbedPane.addTab("💰 Doanh Thu", revenuePanel);
        
        // Tab 2: Hiệu suất nhân viên
        employeePanel = createScrollablePanel();
        tabbedPane.addTab("👥 Nhân Viên", employeePanel);
        
        // Tab 3: Tồn kho chi tiết
        inventoryPanel = createScrollablePanel();
        tabbedPane.addTab("📦 Tồn Kho", inventoryPanel);
    }
    
    private void setupStaffTabs() {
        // Tab 1: Doanh thu (view-only)
        revenuePanel = createScrollablePanel();
        tabbedPane.addTab("💰 Doanh Thu", revenuePanel);
        
        // Tab 2: Thống kê cá nhân
        personalPanel = createScrollablePanel();
        tabbedPane.addTab("👤 Cá Nhân", personalPanel);
    }
    
    private JPanel createScrollablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        return panel;
    }
    
    private void loadData() {
        try {
            if (userRole == 1) {
                loadAdminData();
            } else {
                loadStaffData();
            }
        } catch (SQLException ex) {
            Logger.getLogger(DetailReportForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private void loadAdminData() throws SQLException {
        LocalDate now = LocalDate.now();
        
        // Load revenue data
        loadRevenueData();
        
        // Load employee performance
        loadEmployeePerformanceData();
        
        // Load inventory data
        loadInventoryData();
    }
    
    private void loadStaffData() throws SQLException {
        // Load limited revenue data
        loadRevenueData();
        
        // Load personal stats
        loadPersonalStatsData();
    }
    
    private void loadRevenueData() throws SQLException {
        JPanel content = new JPanel(new BorderLayout());
        
        // Title
        JLabel title = new JLabel("📊 BÁO CÁO DOANH THU CHI TIẾT", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        content.add(title, BorderLayout.NORTH);
        
        // Revenue table - Xóa cột "Số Đơn" và "Thực Thu"
        String[] columns = {"Ngày", "Doanh Thu (VNĐ)", "Giảm Giá (VNĐ)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Get recent revenue data
        List<Map<String, Object>> recentRevenue = thongKeDAO.getRecentDaysRevenue();
        for (Map<String, Object> day : recentRevenue) {
            model.addRow(new Object[]{
                day.get("date"),
                String.format("%,d", (Long) day.getOrDefault("daily_revenue", 0L)),
                String.format("%,d", (Long) day.getOrDefault("total_discount", 0L))
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        
        revenuePanel.removeAll();
        revenuePanel.add(content);
        revenuePanel.revalidate();
    }
    
    private void loadEmployeePerformanceData() throws SQLException {
        JPanel content = new JPanel(new BorderLayout());
        
        // Title
        JLabel title = new JLabel("👥 HIỆU SUẤT NHÂN VIÊN", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        content.add(title, BorderLayout.NORTH);
        
        // Employee performance table
        String[] columns = {"Mã NV", "Họ Tên", "Số Đơn", "Doanh Thu", "Ca Làm", "Giờ Làm", "Lương", "Chuyên Cần"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        LocalDate now = LocalDate.now();
        List<Map<String, Object>> empPerformance = thongKeDAO.getEmployeePerformance(now.getMonthValue(), now.getYear());
        List<Map<String, Object>> shiftPerformance = thongKeDAO.getShiftPerformanceByEmployee(now.getMonthValue(), now.getYear());
        
        // Combine data
        for (Map<String, Object> emp : empPerformance) {
            // Find corresponding shift data
            Map<String, Object> shiftData = shiftPerformance.stream()
                .filter(s -> s.get("employee_id").equals(emp.get("employee_id")))
                .findFirst().orElse(Map.of());
                
            model.addRow(new Object[]{
                emp.get("employee_id"),
                emp.get("full_name"),
                emp.getOrDefault("total_orders", 0),
                String.format("%,d", (Long) emp.getOrDefault("total_sales", 0L)),
                String.format("%d/%d", 
                    (Integer) shiftData.getOrDefault("completed_shifts", 0),
                    (Integer) shiftData.getOrDefault("total_shifts", 0)),
                String.format("%.1f", (Double) shiftData.getOrDefault("total_hours", 0.0)),
                String.format("%,d", ((Double) shiftData.getOrDefault("total_earnings", 0.0)).longValue()),
                String.format("%.1f%%", (Double) shiftData.getOrDefault("attendance_rate", 0.0))
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        
        employeePanel.removeAll();
        employeePanel.add(content);
        employeePanel.revalidate();
    }
    
    private void loadInventoryData() throws SQLException {
        JPanel content = new JPanel(new BorderLayout());
        
        // Title
        JLabel title = new JLabel("📦 CHI TIẾT TỒN KHO", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        content.add(title, BorderLayout.NORTH);
        
        // Inventory table
        String[] columns = {"Mã SP", "Tên Sản Phẩm", "Danh Mục", "Tồn Kho", "Giá Bán", "Giá Trị Tồn", "Trạng Thái"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<Map<String, Object>> inventory = thongKeDAO.getInventoryStatus();
        for (Map<String, Object> item : inventory) {
            model.addRow(new Object[]{
                item.getOrDefault("product_id", ""),
                item.getOrDefault("product_name", ""),
                item.getOrDefault("category_name", ""),
                item.getOrDefault("stock_quantity", 0),
                String.format("%,d", ((Number) item.getOrDefault("price", 0)).intValue()),
                String.format("%,d", ((Number) item.getOrDefault("inventory_value", 0L)).longValue()),
                item.getOrDefault("stock_status", "Không xác định")
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        
        // Color rows based on stock status
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                try {
                    String status = (String) table.getValueAt(row, 6); // Status column
                    if (!isSelected && status != null) {
                        switch (status) {
                            case "Hết hàng":
                                setBackground(new Color(255, 235, 238));
                                break;
                            case "Sắp hết":
                                setBackground(new Color(255, 243, 224));
                                break;
                            case "Ít":
                                setBackground(new Color(255, 248, 225));
                                break;
                            default:
                                setBackground(Color.WHITE);
                        }
                    } else if (!isSelected) {
                        setBackground(Color.WHITE);
                    }
                } catch (Exception e) {
                    setBackground(Color.WHITE);
                }
                return this;
            }
        });
        
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        
        inventoryPanel.removeAll();
        inventoryPanel.add(content);
        inventoryPanel.revalidate();
    }
    
    private void loadPersonalStatsData() throws SQLException {
        JPanel content = new JPanel(new BorderLayout());
        
        // Title
        JLabel title = new JLabel("👤 THỐNG KÊ CÁ NHÂN", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        content.add(title, BorderLayout.NORTH);
        
        // Personal stats info
        Map<String, Object> shiftSummary = thongKeDAO.getEmployeeShiftSummary(employeeId);
        Map<String, Object> earnings = thongKeDAO.getEmployeeEarningsEstimate(employeeId);
        
        JPanel infoPanel = new JPanel(new GridLayout(6, 2, 10, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        infoPanel.add(new JLabel("Ca làm tháng này:"));
        infoPanel.add(new JLabel(String.valueOf(shiftSummary.getOrDefault("monthly_shifts", 0))));
        
        infoPanel.add(new JLabel("Ca hoàn thành:"));
        infoPanel.add(new JLabel(String.valueOf(shiftSummary.getOrDefault("completed_shifts", 0))));
        
        infoPanel.add(new JLabel("Tổng giờ làm:"));
        infoPanel.add(new JLabel(String.format("%.1f giờ", 
            ((Number) earnings.getOrDefault("total_hours", 0)).doubleValue())));
        
        infoPanel.add(new JLabel("Lương dự kiến:"));
        infoPanel.add(new JLabel(String.format("%,d VNĐ", 
            (long) ((Number) earnings.getOrDefault("total_earnings", 0)).doubleValue())));
        
        infoPanel.add(new JLabel("Tỷ lệ chuyên cần:"));
        int totalShifts = (Integer) shiftSummary.getOrDefault("monthly_shifts", 1);
        int completedShifts = (Integer) shiftSummary.getOrDefault("completed_shifts", 0);
        double attendanceRate = totalShifts > 0 ? (completedShifts * 100.0 / totalShifts) : 0;
        infoPanel.add(new JLabel(String.format("%.1f%%", attendanceRate)));
        
        content.add(infoPanel, BorderLayout.NORTH);
        
        // Recent shifts table - Xóa cột "Ca Làm"
        String[] columns = {"Ngày", "Giờ Vào", "Giờ Ra", "Trạng Thái", "Ghi Chú"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<Map<String, Object>> recentShifts = thongKeDAO.getRecentShifts(employeeId);
        for (Map<String, Object> shift : recentShifts) {
            model.addRow(new Object[]{
                shift.get("shift_date"),
                shift.get("start_time"),
                shift.get("end_time") != null ? shift.get("end_time") : "Chưa kết thúc",
                shift.get("status"),
                shift.getOrDefault("notes", "")
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        content.add(new JScrollPane(table), BorderLayout.CENTER);
        
        personalPanel.removeAll();
        personalPanel.add(content);
        personalPanel.revalidate();
    }
    
    @Override
    public void dispose() {
        if (thongKeDAO != null) {
            thongKeDAO.closeConnection();
        }
        super.dispose();
    }
} 