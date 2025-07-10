package view;

import com.raven.chart.ModelChartLine;
import com.raven.chart.ModelChartPie;
import com.raven.model.ModelStaff;
import controller.ThongKeController;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.table.DefaultTableModel;
import java.util.Map;
import java.text.DecimalFormat;

public class ThongKeView extends javax.swing.JPanel {
    
    private ThongKeController thongKeController;
    private DecimalFormat formatter = new DecimalFormat("đ #,##0.00");

    public ThongKeView() {
        initComponents();
        // Thay thế initData() cũ bằng controller mới
        initController();
        // Cập nhật hiển thị buttons theo role trước
        try {
            int userRole = model.Session.getInstance().getRole();
            updateButtonsForRole(userRole);
        } catch (Exception e) {
            System.err.println("Error getting user role: " + e.getMessage());
        }
        // Cập nhật trạng thái nút ca làm việc
        updateShiftButtonStates();
    }
    
    /**
     * Khởi tạo controller thống kê với role-based access
     */
    private void initController() {
        try {
            thongKeController = new ThongKeController(this);
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Lỗi khởi tạo controller: " + e.getMessage(), 
                "Lỗi", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
        }
    }

    private void initData() {
        //  Test data chart pie
        List<ModelChartPie> list1 = new ArrayList<>();
        list1.add(new ModelChartPie("Monday", 10, new Color(4, 174, 243)));
        list1.add(new ModelChartPie("Tuesday", 150, new Color(215, 39, 250)));
        list1.add(new ModelChartPie("Wednesday", 80, new Color(44, 88, 236)));
        list1.add(new ModelChartPie("Thursday", 100, new Color(21, 202, 87)));
        list1.add(new ModelChartPie("Friday", 125, new Color(127, 63, 255)));
        list1.add(new ModelChartPie("Saturday", 80, new Color(238, 167, 35)));
        list1.add(new ModelChartPie("Sunday", 200, new Color(245, 79, 99)));
        chartPie.setModel(list1);
        //  Test data chart line
        List<ModelChartLine> list = new ArrayList<>();
        list.add(new ModelChartLine("Monday", 10));
        list.add(new ModelChartLine("Tuesday", 150));
        list.add(new ModelChartLine("Wednesday", 80));
        list.add(new ModelChartLine("Thursday", 100));
        list.add(new ModelChartLine("Friday", 125));
        list.add(new ModelChartLine("Saturday", 80));
        list.add(new ModelChartLine("Sunday", 200));
        chartLine1.setModel(list);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        chartPie = new com.raven.chart.ChartPie();
        chartLine1 = new com.raven.chart.ChartLine();
        jLabel1 = new javax.swing.JLabel();
        titlePanel = new javax.swing.JPanel();
        titleLabel = new javax.swing.JLabel();
        buttonPanel = new javax.swing.JPanel();
        btnStartShift = new com.raven.swing.Button();
        btnEndShift = new com.raven.swing.Button();
        btnDetailReport = new com.raven.swing.Button();
        btnShiftManagement = new com.raven.swing.Button();
        btnInventoryDetail = new com.raven.swing.Button();
        btnSalesDetail = new com.raven.swing.Button();
        btnPersonalStats = new com.raven.swing.Button();

        setBackground(new java.awt.Color(250, 250, 250));

        // Setup title panel
        titlePanel.setBackground(new java.awt.Color(0, 102, 0));
        titlePanel.setLayout(new java.awt.BorderLayout());
        
        titleLabel.setFont(new java.awt.Font("Segoe UI", 1, 24));
        titleLabel.setForeground(new java.awt.Color(255, 255, 255));
        titleLabel.setText("TRANG THỐNG KÊ");
        titleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        titleLabel.setBorder(javax.swing.BorderFactory.createEmptyBorder(15, 0, 15, 0));
        titlePanel.add(titleLabel, java.awt.BorderLayout.CENTER);

        jLabel1.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(66, 66, 66));
        jLabel1.setText("Thống Kê & Báo Cáo");

        // Setup button panel
        buttonPanel.setBackground(new java.awt.Color(250, 250, 250));
        buttonPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 10, 5));

        // Setup buttons
        btnStartShift.setBackground(new java.awt.Color(67, 160, 71));
        btnStartShift.setForeground(new java.awt.Color(255, 255, 255));
        btnStartShift.setText("🟢 Bắt Đầu Ca");
        btnStartShift.setFont(new java.awt.Font("sansserif", 1, 12));
        btnStartShift.addActionListener(this::btnStartShiftActionPerformed);

        btnEndShift.setBackground(new java.awt.Color(244, 67, 54));
        btnEndShift.setForeground(new java.awt.Color(255, 255, 255));
        btnEndShift.setText("🔴 Kết Thúc Ca");
        btnEndShift.setFont(new java.awt.Font("sansserif", 1, 12));
        btnEndShift.addActionListener(this::btnEndShiftActionPerformed);

        btnDetailReport.setBackground(new java.awt.Color(30, 136, 229));
        btnDetailReport.setForeground(new java.awt.Color(255, 255, 255));
        btnDetailReport.setText("📊 Báo Cáo Chi Tiết");
        btnDetailReport.setFont(new java.awt.Font("sansserif", 1, 12));
        btnDetailReport.addActionListener(this::btnDetailReportActionPerformed);

        btnShiftManagement.setBackground(new java.awt.Color(251, 140, 0));
        btnShiftManagement.setForeground(new java.awt.Color(255, 255, 255));
        btnShiftManagement.setText("⏰ Quản Lý Ca Làm");
        btnShiftManagement.setFont(new java.awt.Font("sansserif", 1, 12));
        btnShiftManagement.addActionListener(this::btnShiftManagementActionPerformed);

        btnInventoryDetail.setBackground(new java.awt.Color(156, 39, 176));
        btnInventoryDetail.setForeground(new java.awt.Color(255, 255, 255));
        btnInventoryDetail.setText("📦 Chi Tiết Kho");
        btnInventoryDetail.setFont(new java.awt.Font("sansserif", 1, 12));
        btnInventoryDetail.addActionListener(this::btnInventoryDetailActionPerformed);

        btnSalesDetail.setBackground(new java.awt.Color(0, 150, 136));
        btnSalesDetail.setForeground(new java.awt.Color(255, 255, 255));
        btnSalesDetail.setText("💰 Chi Tiết Bán Hàng");
        btnSalesDetail.setFont(new java.awt.Font("sansserif", 1, 12));
        btnSalesDetail.addActionListener(this::btnSalesDetailActionPerformed);

        btnPersonalStats.setBackground(new java.awt.Color(63, 81, 181));
        btnPersonalStats.setForeground(new java.awt.Color(255, 255, 255));
        btnPersonalStats.setText("👤 Thống Kê Cá Nhân");
        btnPersonalStats.setFont(new java.awt.Font("sansserif", 1, 12));
        btnPersonalStats.addActionListener(this::btnPersonalStatsActionPerformed);

        // Add buttons to panel
        buttonPanel.add(btnStartShift);
        buttonPanel.add(btnEndShift);
        buttonPanel.add(btnDetailReport);
        buttonPanel.add(btnShiftManagement);
        buttonPanel.add(btnInventoryDetail);
        buttonPanel.add(btnSalesDetail);
        buttonPanel.add(btnPersonalStats);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(titlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(chartLine1, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chartPie, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(titlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartPie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Button action listeners - delegate to controller
    private void btnStartShiftActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(evt);
        }
    }

    private void btnEndShiftActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(evt);
        }
    }

    private void btnDetailReportActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(evt);
        }
    }

    private void btnShiftManagementActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(evt);
        }
    }

    private void btnInventoryDetailActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(evt);
        }
    }

    private void btnSalesDetailActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(evt);
        }
    }

    private void btnPersonalStatsActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(evt);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.chart.ChartLine chartLine1;
    private com.raven.chart.ChartPie chartPie;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel titlePanel;
    private javax.swing.JLabel titleLabel;
    private javax.swing.JPanel buttonPanel;
    private com.raven.swing.Button btnStartShift;
    private com.raven.swing.Button btnEndShift;
    private com.raven.swing.Button btnDetailReport;
    private com.raven.swing.Button btnShiftManagement;
    private com.raven.swing.Button btnInventoryDetail;
    private com.raven.swing.Button btnSalesDetail;
    private com.raven.swing.Button btnPersonalStats;
    // End of variables declaration//GEN-END:variables
    
    // Getter methods for controller access
    public javax.swing.JLabel getJLabel1() {
        return jLabel1;
    }
    
    public com.raven.chart.ChartPie getChartPie() {
        return chartPie;
    }
    
    public com.raven.chart.ChartLine getChartLine1() {
        return chartLine1;
    }
    
    /**
     * Refresh dữ liệu thống kê
     */
    public void refreshData() {
        if (thongKeController != null) {
            thongKeController.refreshStatistics();
            // Cập nhật lại trạng thái nút ca làm việc
            updateShiftButtonStates();
        }
    }
    
    /**
     * Cleanup khi đóng form
     */
    public void cleanup() {
        if (thongKeController != null) {
            thongKeController.cleanup();
        }
    }
    
    /**
     * Cập nhật trạng thái nút ca làm việc
     */
    public void updateShiftButtons(boolean canStartShift, boolean canEndShift) {
        System.out.println("DEBUG: updateShiftButtons - canStartShift=" + canStartShift + ", canEndShift=" + canEndShift);
        
        if (btnStartShift != null) {
            btnStartShift.setVisible(canStartShift);
            System.out.println("DEBUG: btnStartShift.setVisible(" + canStartShift + ")");
        } else {
            System.out.println("DEBUG: btnStartShift is null!");
        }
        
        if (btnEndShift != null) {
            btnEndShift.setVisible(canEndShift);
            System.out.println("DEBUG: btnEndShift.setVisible(" + canEndShift + ")");
        } else {
            System.out.println("DEBUG: btnEndShift is null!");
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
        System.out.println("DEBUG: Button panel revalidated and repainted");
    }
    
    /**
     * Force hiển thị nút ca làm việc cho Staff
     */
    public void forceShowShiftButtonsForStaff() {
        System.out.println("DEBUG: forceShowShiftButtonsForStaff called");
        
        if (btnStartShift != null) {
            btnStartShift.setVisible(true);
            System.out.println("DEBUG: Forced btnStartShift to visible");
        }
        
        if (btnEndShift != null) {
            btnEndShift.setVisible(false); // Mặc định ẩn nút kết thúc
            System.out.println("DEBUG: Forced btnEndShift to invisible");
        }
        
        buttonPanel.revalidate();
        buttonPanel.repaint();
        System.out.println("DEBUG: Button panel revalidated and repainted after force show");
    }
    
    /**
     * Force hiển thị nút kết thúc ca
     */
    public void showEndShiftButton() {
        System.out.println("DEBUG: showEndShiftButton called");
        if (btnEndShift != null) {
            btnEndShift.setVisible(true);
            System.out.println("DEBUG: Forced btnEndShift to visible");
            buttonPanel.revalidate();
            buttonPanel.repaint();
        }
    }
    
    /**
     * Force ẩn nút kết thúc ca
     */
    public void hideEndShiftButton() {
        System.out.println("DEBUG: hideEndShiftButton called");
        if (btnEndShift != null) {
            btnEndShift.setVisible(false);
            System.out.println("DEBUG: Forced btnEndShift to invisible");
            buttonPanel.revalidate();
            buttonPanel.repaint();
        }
    }
    
    /**
     * Force hiển thị nút kết thúc ca ngay lập tức
     */
    public void forceShowEndShiftButton() {
        System.out.println("DEBUG: forceShowEndShiftButton called");
        if (btnEndShift != null) {
            btnEndShift.setVisible(true);
            System.out.println("DEBUG: Forced btnEndShift to visible immediately");
            // Force repaint
            btnEndShift.revalidate();
            btnEndShift.repaint();
            buttonPanel.revalidate();
            buttonPanel.repaint();
            this.revalidate();
            this.repaint();
            System.out.println("DEBUG: All components repainted");
        } else {
            System.out.println("DEBUG: btnEndShift is null!");
        }
    }
    
    /**
     * Cập nhật hiển thị buttons theo role
     */
    public void updateButtonsForRole(int userRole) {
        System.out.println("DEBUG: updateButtonsForRole called with userRole = " + userRole);
        
        if (userRole == 1) { // Admin
            btnDetailReport.setVisible(true);
            btnShiftManagement.setVisible(true);
            btnInventoryDetail.setVisible(true);
            btnSalesDetail.setVisible(true);
            btnPersonalStats.setVisible(false);
            btnStartShift.setVisible(false);
            btnEndShift.setVisible(false);
        } else if (userRole == 2) { // Staff
            btnDetailReport.setVisible(true);
            btnShiftManagement.setVisible(false);
            btnInventoryDetail.setVisible(true);
            btnSalesDetail.setVisible(true);
            btnPersonalStats.setVisible(true);
            // Force hiển thị nút ca làm việc cho Staff
            forceShowShiftButtonsForStaff();
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    
    /**
     * Kiểm tra nút ca làm việc có tồn tại trong buttonPanel không
     */
    private void checkShiftButtonsInPanel() {
        System.out.println("DEBUG: Checking shift buttons in panel...");
        System.out.println("DEBUG: btnStartShift = " + (btnStartShift != null ? "exists" : "null"));
        System.out.println("DEBUG: btnEndShift = " + (btnEndShift != null ? "exists" : "null"));
        
        boolean startShiftInPanel = false;
        boolean endShiftInPanel = false;
        
        for (int i = 0; i < buttonPanel.getComponentCount(); i++) {
            java.awt.Component comp = buttonPanel.getComponent(i);
            if (comp == btnStartShift) {
                startShiftInPanel = true;
                System.out.println("DEBUG: btnStartShift found at index " + i);
            }
            if (comp == btnEndShift) {
                endShiftInPanel = true;
                System.out.println("DEBUG: btnEndShift found at index " + i);
            }
        }
        
        System.out.println("DEBUG: startShiftInPanel = " + startShiftInPanel + ", endShiftInPanel = " + endShiftInPanel);
    }
    
    /**
     * Test hiển thị nút ca làm việc
     */
    public void testShiftButtonVisibility() {
        System.out.println("=== TEST SHIFT BUTTON VISIBILITY ===");
        System.out.println("btnStartShift exists: " + (btnStartShift != null));
        System.out.println("btnEndShift exists: " + (btnEndShift != null));
        
        if (btnStartShift != null) {
            System.out.println("btnStartShift visible: " + btnStartShift.isVisible());
            System.out.println("btnStartShift text: " + btnStartShift.getText());
        }
        
        if (btnEndShift != null) {
            System.out.println("btnEndShift visible: " + btnEndShift.isVisible());
            System.out.println("btnEndShift text: " + btnEndShift.getText());
        }
        
        System.out.println("buttonPanel component count: " + buttonPanel.getComponentCount());
        System.out.println("=== END TEST ===");
    }
    
    /**
     * Cập nhật trạng thái nút ca làm việc dựa trên session hiện tại
     */
    public void updateShiftButtonStates() {
        try {
            int userRole = model.Session.getInstance().getRole();
            int employeeId = model.Session.getInstance().getEmployeeId();
            
            System.out.println("DEBUG: updateShiftButtonStates - userRole=" + userRole + ", employeeId=" + employeeId);
            System.out.println("DEBUG: buttonPanel has " + buttonPanel.getComponentCount() + " components");
            
            // Test hiển thị nút trước khi cập nhật
            testShiftButtonVisibility();
            
            // Kiểm tra nút ca làm việc có trong panel không
            checkShiftButtonsInPanel();
            
            // Debug: kiểm tra từng component trong buttonPanel
            for (int i = 0; i < buttonPanel.getComponentCount(); i++) {
                java.awt.Component comp = buttonPanel.getComponent(i);
                System.out.println("DEBUG: Component " + i + " = " + comp.getClass().getSimpleName() + 
                                 ", visible=" + comp.isVisible() + 
                                 (comp instanceof javax.swing.JButton ? ", text=" + ((javax.swing.JButton)comp).getText() : ""));
            }
            
            if (userRole == 2) { // Staff
                // Đảm bảo nút ca làm việc được hiển thị cho Staff
                if (btnStartShift != null && !btnStartShift.isVisible()) {
                    btnStartShift.setVisible(true);
                    System.out.println("DEBUG: Made btnStartShift visible for Staff");
                }
                
                // Kiểm tra trạng thái ca làm hiện tại
                dao.WorkShiftDAO workShiftDAO = new dao.WorkShiftDAO();
                model.WorkShift currentShift = workShiftDAO.getCurrentShift(employeeId);
                
                boolean canStartShift = (currentShift == null);
                boolean canEndShift = (currentShift != null && "IN_PROGRESS".equals(currentShift.getWorkStatus()));
                
                System.out.println("DEBUG: currentShift=" + (currentShift != null ? currentShift.getWorkingSessionId() : "null") + 
                                 ", canStartShift=" + canStartShift + ", canEndShift=" + canEndShift);
                
                updateShiftButtons(canStartShift, canEndShift);
                workShiftDAO.closeConnection();
                
                // Test hiển thị nút sau khi cập nhật
                testShiftButtonVisibility();
            }
        } catch (Exception e) {
            System.err.println("Error updating shift button states: " + e.getMessage());
            e.printStackTrace();
        }
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
        
        // Cập nhật tiêu đề với thông tin tổng quan
        StringBuilder title = new StringBuilder("Thống Kê & Báo Cáo - Admin");
        if (monthlyRevenue != null) {
            title.append(" | Doanh thu tháng: ").append(formatter.format(monthlyRevenue.get("total_revenue")));
        }
        if (profitAnalysis != null) {
            title.append(" | Lợi nhuận: ").append(formatter.format(profitAnalysis.get("profit")));
        }
        jLabel1.setText(title.toString());
        
        // Cập nhật bảng hiệu suất nhân viên
        updateEmployeePerformanceTable(employeePerformance);
        
        // Cập nhật bảng tồn kho
        updateInventoryTable(inventoryStatus);
        
        // Cập nhật bảng hiệu suất ca làm
        updateShiftPerformanceTable(shiftPerformance);
        
        // Cập nhật thông tin biểu đồ
        updateChartInfo(monthlyRevenue, profitAnalysis, customerStats);
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
        
        // Cập nhật tiêu đề với thông tin cá nhân
        StringBuilder title = new StringBuilder("Thống Kê & Báo Cáo - Staff");
        if (todayStats != null) {
            title.append(" | Hôm nay: ").append(formatter.format(todayStats.get("today_revenue")));
        }
        if (earningsEstimate != null) {
            title.append(" | Ước tính thu nhập: ").append(formatter.format(earningsEstimate.get("total_earnings")));
        }
        jLabel1.setText(title.toString());
        
        // Cập nhật bảng sản phẩm bán chạy
        updateTopProductsTable(topProducts);
        
        // Cập nhật bảng thống kê 7 ngày
        updateRecentDaysTable(recentDays);
        
        // Cập nhật bảng ca làm gần đây
        updateRecentShiftsTable(recentShifts);
        
        // Cập nhật thông tin biểu đồ cá nhân
        updatePersonalChartInfo(todayStats, earningsEstimate);
    }
    
    /**
     * Cập nhật thông tin biểu đồ cho Admin
     */
    private void updateChartInfo(Map<String, Object> monthlyRevenue, 
                                Map<String, Object> profitAnalysis,
                                Map<String, Object> customerStats) {
        
        // Tạo tooltip cho biểu đồ
        StringBuilder lineChartInfo = new StringBuilder();
        lineChartInfo.append("📈 Biểu đồ doanh thu theo ngày trong tuần\n");
        if (monthlyRevenue != null) {
            lineChartInfo.append("Tổng doanh thu: ").append(formatter.format(monthlyRevenue.get("total_revenue"))).append("\n");
        }
        if (profitAnalysis != null) {
            lineChartInfo.append("Lợi nhuận: ").append(formatter.format(profitAnalysis.get("profit")));
        }
        
        StringBuilder pieChartInfo = new StringBuilder();
        pieChartInfo.append("🥧 Phân bố doanh thu theo danh mục\n");
        if (customerStats != null) {
            pieChartInfo.append("Tổng khách hàng: ").append(customerStats.get("total_customers"));
        }
       
        
        // Cập nhật tooltip cho biểu đồ
        if (chartLine1 != null) {
            chartLine1.setToolTipText(lineChartInfo.toString());
        }
        if (chartPie != null) {
            chartPie.setToolTipText(pieChartInfo.toString());
        }
    }
    
    /**
     * Cập nhật thông tin biểu đồ cho Staff
     */
    private void updatePersonalChartInfo(Map<String, Object> todayStats,
                                        Map<String, Object> earningsEstimate) {
        
        // Tạo tooltip cho biểu đồ cá nhân
        StringBuilder lineChartInfo = new StringBuilder();
        lineChartInfo.append("📈 Biểu đồ ca làm việc theo ngày\n");
        if (todayStats != null) {
            lineChartInfo.append("Doanh thu hôm nay: ").append(formatter.format(todayStats.get("today_revenue"))).append("\n");
        }
        
        
        StringBuilder pieChartInfo = new StringBuilder();
        pieChartInfo.append("🥧 Top sản phẩm bán chạy\n");
        if (earningsEstimate != null) {
            pieChartInfo.append("Ước tính thu nhập: ").append(formatter.format(earningsEstimate.get("total_earnings")));
        }
        
        
        // Cập nhật tooltip cho biểu đồ
        if (chartLine1 != null) {
            chartLine1.setToolTipText(lineChartInfo.toString());
        }
        if (chartPie != null) {
            chartPie.setToolTipText(pieChartInfo.toString());
        }
    }

    /**
     * Cập nhật bảng hiệu suất nhân viên
     */
    private void updateEmployeePerformanceTable(List<Map<String, Object>> employeePerformance) {
        // Implementation for employee performance table
        System.out.println("Updating employee performance table with " + (employeePerformance != null ? employeePerformance.size() : 0) + " records");
        
        // TODO: Implement table update logic
        // Có thể thêm bảng hiển thị hiệu suất nhân viên ở đây
    }
    
    /**
     * Cập nhật bảng tồn kho
     */
    private void updateInventoryTable(List<Map<String, Object>> inventoryStatus) {
        // Implementation for inventory table
        System.out.println("Updating inventory table with " + (inventoryStatus != null ? inventoryStatus.size() : 0) + " records");
        
        // TODO: Implement table update logic
        // Có thể thêm bảng hiển thị tồn kho ở đây
    }
    
    /**
     * Cập nhật bảng hiệu suất ca làm
     */
    private void updateShiftPerformanceTable(List<Map<String, Object>> shiftPerformance) {
        // Implementation for shift performance table
        System.out.println("Updating shift performance table with " + (shiftPerformance != null ? shiftPerformance.size() : 0) + " records");
        
        // TODO: Implement table update logic
        // Có thể thêm bảng hiển thị hiệu suất ca làm ở đây
    }
    
    /**
     * Cập nhật bảng sản phẩm bán chạy
     */
    private void updateTopProductsTable(List<Map<String, Object>> topProducts) {
        // Implementation for top products table
        System.out.println("Updating top products table with " + (topProducts != null ? topProducts.size() : 0) + " records");
        
        // TODO: Implement table update logic
        // Có thể thêm bảng hiển thị sản phẩm bán chạy ở đây
    }
    
    /**
     * Cập nhật bảng thống kê 7 ngày
     */
    private void updateRecentDaysTable(List<Map<String, Object>> recentDays) {
        // Implementation for recent days table
        System.out.println("Updating recent days table with " + (recentDays != null ? recentDays.size() : 0) + " records");
        
        // TODO: Implement table update logic
        // Có thể thêm bảng hiển thị thống kê 7 ngày ở đây
    }
    
    /**
     * Cập nhật bảng ca làm gần đây
     */
    private void updateRecentShiftsTable(List<Map<String, Object>> recentShifts) {
        // Implementation for recent shifts table
        System.out.println("Updating recent shifts table with " + (recentShifts != null ? recentShifts.size() : 0) + " records");
        
        // TODO: Implement table update logic
        // Có thể thêm bảng hiển thị ca làm gần đây ở đây
    }
}
