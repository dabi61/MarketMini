package view;

import dao.ThongKeDAO;
import model.Session;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Form thống kê cá nhân cho staff
 */
public class PersonalStatsForm extends JFrame {
    
    private int employeeId;
    private ThongKeDAO thongKeDAO;
    
    public PersonalStatsForm(int employeeId) {
        this.employeeId = employeeId;
        
        try {
            this.thongKeDAO = new ThongKeDAO();
            initComponents();
            loadData();
        } catch (SQLException ex) {
            Logger.getLogger(PersonalStatsForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi kết nối database: " + ex.getMessage());
        }
    }
    
    private void initComponents() {
        setTitle("👤 Thống Kê Cá Nhân - " + Session.getInstance().getFullName());
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    
    private void loadData() {
        try {
            JTabbedPane tabbedPane = new JTabbedPane();
            
            // Tab 1: Tổng quan cá nhân
            JPanel overviewPanel = createOverviewPanel();
            tabbedPane.addTab("📊 Tổng Quan", overviewPanel);
            
            // Tab 2: Lịch sử ca làm
            JPanel shiftHistoryPanel = createShiftHistoryPanel();
            tabbedPane.addTab("⏰ Lịch Sử Ca Làm", shiftHistoryPanel);
            
            // Tab 3: Thống kê lương
            JPanel salaryPanel = createSalaryPanel();
            tabbedPane.addTab("💰 Thống Kê Lương", salaryPanel);
            
            setLayout(new BorderLayout());
            add(tabbedPane, BorderLayout.CENTER);
            
            // Close button
            JPanel buttonPanel = new JPanel();
            JButton closeButton = new JButton("Đóng");
            closeButton.addActionListener(e -> dispose());
            buttonPanel.add(closeButton);
            add(buttonPanel, BorderLayout.SOUTH);
            
        } catch (SQLException ex) {
            Logger.getLogger(PersonalStatsForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(this, "Lỗi tải dữ liệu: " + ex.getMessage());
        }
    }
    
    private JPanel createOverviewPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("📊 TỔNG QUAN CÁ NHÂN", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Get personal stats
        Map<String, Object> shiftSummary = thongKeDAO.getEmployeeShiftSummary(employeeId);
        Map<String, Object> earnings = thongKeDAO.getEmployeeEarningsEstimate(employeeId);
        
        // Create info panel
        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 20, 10));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        
        // Employee info
        infoPanel.add(createInfoLabel("👤 Nhân viên:", Session.getInstance().getFullName()));
        infoPanel.add(createInfoLabel("🆔 Mã nhân viên:", String.valueOf(employeeId)));
        
        // Shift stats
        int monthlyShifts = (Integer) shiftSummary.getOrDefault("monthly_shifts", 0);
        int completedShifts = (Integer) shiftSummary.getOrDefault("completed_shifts", 0);
        double attendanceRate = monthlyShifts > 0 ? (completedShifts * 100.0 / monthlyShifts) : 0;
        
        infoPanel.add(createInfoLabel("📅 Ca làm tháng này:", String.valueOf(monthlyShifts)));
        infoPanel.add(createInfoLabel("✅ Ca hoàn thành:", String.valueOf(completedShifts)));
        infoPanel.add(createInfoLabel("📈 Tỷ lệ chuyên cần:", String.format("%.1f%%", attendanceRate)));
        
        // Working hours and earnings
        double totalHours = ((Number) earnings.getOrDefault("total_hours", 0)).doubleValue();
        double totalEarnings = ((Number) earnings.getOrDefault("total_earnings", 0)).doubleValue();
        
        infoPanel.add(createInfoLabel("⏰ Tổng giờ làm:", String.format("%.1f giờ", totalHours)));
        infoPanel.add(createInfoLabel("💰 Lương dự kiến:", String.format("%,d VNĐ", (long) totalEarnings)));
        
        // Today's shift status
        String todayShiftType = (String) shiftSummary.getOrDefault("today_shift_type", "Không có ca");
        String todayStatus = (String) shiftSummary.getOrDefault("today_status", "NO_SHIFT");
        String statusText = "";
        switch (todayStatus) {
            case "SCHEDULED": statusText = "Đã lên lịch"; break;
            case "IN_PROGRESS": statusText = "Đang làm"; break;
            case "COMPLETED": statusText = "Đã hoàn thành"; break;
            case "ABSENT": statusText = "Vắng mặt"; break;
            default: statusText = "Không có ca";
        }
        
        infoPanel.add(createInfoLabel("🎯 Ca hôm nay:", todayShiftType));
        infoPanel.add(createInfoLabel("📊 Trạng thái:", statusText));
        
        panel.add(infoPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JLabel createInfoLabel(String label, String value) {
        JLabel infoLabel = new JLabel("<html><b>" + label + "</b><br/>" + value + "</html>");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        infoLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return infoLabel;
    }
    
    private JPanel createShiftHistoryPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("⏰ LỊCH SỬ CA LÀM VIỆC", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        String[] columns = {
            "Ngày", "Ca Làm", "Giờ Vào", "Giờ Ra", 
            "Giờ Dự Kiến", "Giờ Thực Tế", "Overtime", "Trạng Thái", "Ghi Chú"
        };
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<Map<String, Object>> recentShifts = thongKeDAO.getRecentShifts(employeeId);
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
            
            model.addRow(new Object[]{
                shift.get("shift_date"),
                displayShiftType,
                shift.get("start_time"),
                shift.get("end_time") != null ? shift.get("end_time") : "Chưa kết thúc",
                String.format("%.1f", (Double) shift.getOrDefault("planned_hours", 0.0)),
                shift.get("actual_hours") != null ? 
                    String.format("%.1f", (Double) shift.get("actual_hours")) : "N/A",
                String.format("%.1f", (Double) shift.getOrDefault("overtime_hours", 0.0)),
                displayStatus,
                shift.getOrDefault("notes", "")
            });
        }
        
        JTable table = new JTable(model);
        table.setRowHeight(25);
        
        // Color rows based on status
        table.setDefaultRenderer(Object.class, new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                String status = (String) table.getValueAt(row, 7); // Status column
                if (!isSelected) {
                    switch (status) {
                        case "Vắng mặt":
                            setBackground(new java.awt.Color(255, 235, 238));
                            break;
                        case "Đang làm":
                            setBackground(new java.awt.Color(232, 245, 233));
                            break;
                        case "Hoàn thành":
                            setBackground(new java.awt.Color(237, 247, 237));
                            break;
                        default:
                            setBackground(java.awt.Color.WHITE);
                    }
                }
                return this;
            }
        });
        
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSalaryPanel() throws SQLException {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("💰 THỐNG KÊ LƯƠNG & THU NHẬP", JLabel.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        // Salary breakdown
        Map<String, Object> earnings = thongKeDAO.getEmployeeEarningsEstimate(employeeId);
        
        JPanel salaryInfoPanel = new JPanel(new GridLayout(0, 2, 20, 10));
        salaryInfoPanel.setBorder(BorderFactory.createTitledBorder("Thông Tin Lương"));
        
        double totalHours = ((Number) earnings.getOrDefault("total_hours", 0)).doubleValue();
        double totalEarnings = ((Number) earnings.getOrDefault("total_earnings", 0)).doubleValue();
        double overtimeHours = ((Number) earnings.getOrDefault("overtime_hours", 0)).doubleValue();
        double overtimeEarnings = ((Number) earnings.getOrDefault("overtime_earnings", 0)).doubleValue();
        double regularEarnings = totalEarnings - overtimeEarnings;
        
        salaryInfoPanel.add(createInfoLabel("⏰ Tổng giờ làm:", String.format("%.1f giờ", totalHours)));
        salaryInfoPanel.add(createInfoLabel("⌚ Giờ overtime:", String.format("%.1f giờ", overtimeHours)));
        salaryInfoPanel.add(createInfoLabel("💰 Lương cơ bản:", String.format("%,d VNĐ", (long) regularEarnings)));
        salaryInfoPanel.add(createInfoLabel("💵 Lương overtime:", String.format("%,d VNĐ", (long) overtimeEarnings)));
        salaryInfoPanel.add(createInfoLabel("💎 Tổng lương dự kiến:", String.format("%,d VNĐ", (long) totalEarnings)));
        salaryInfoPanel.add(createInfoLabel("📊 Lương/giờ TB:", 
            totalHours > 0 ? String.format("%,d VNĐ/giờ", (long)(totalEarnings / totalHours)) : "N/A"));
        
        panel.add(salaryInfoPanel, BorderLayout.NORTH);
        
        // Recent shifts with earnings
        JPanel shiftsPanel = new JPanel(new BorderLayout());
        shiftsPanel.setBorder(BorderFactory.createTitledBorder("Ca Làm & Thu Nhập Gần Đây"));
        
        String[] columns = {"Ngày", "Ca Làm", "Giờ Làm", "Overtime", "Thu Nhập", "Ghi Chú"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        List<Map<String, Object>> recentShifts = thongKeDAO.getRecentShifts(employeeId);
        for (Map<String, Object> shift : recentShifts) {
            if ("COMPLETED".equals(shift.get("status"))) {
                double actualHours = (Double) shift.getOrDefault("actual_hours", 0.0);
                double overtime = (Double) shift.getOrDefault("overtime_hours", 0.0);
                // Assume hourly wage of 18000 VND (should get from database)
                double shiftEarnings = (actualHours * 18000) + (overtime * 18000 * 1.5);
                
                model.addRow(new Object[]{
                    shift.get("shift_date"),
                    shift.get("shift_type"),
                    String.format("%.1f", actualHours),
                    String.format("%.1f", overtime),
                    String.format("%,d VNĐ", (long) shiftEarnings),
                    shift.getOrDefault("notes", "")
                });
            }
        }
        
        JTable shiftsTable = new JTable(model);
        shiftsTable.setRowHeight(25);
        shiftsPanel.add(new JScrollPane(shiftsTable), BorderLayout.CENTER);
        
        panel.add(shiftsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    @Override
    public void dispose() {
        if (thongKeDAO != null) {
            thongKeDAO.closeConnection();
        }
        super.dispose();
    }
} 