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

public class ThongKeView extends javax.swing.JPanel {
    
    private ThongKeController thongKeController;

    public ThongKeView() {
        initComponents();
        // Thay thế initData() cũ bằng controller mới
        initController();
    }
    
    /**
     * Khởi tạo controller thống kê với role-based access
     */
    private void initController() {
        thongKeController = new ThongKeController(this);
    }

    private void initData() {
        //  Test Data table
        DefaultTableModel model = (DefaultTableModel) table1.getModel();
        Random r = new Random();
        for (int i = 0; i < 20; i++) {
            String status;
            int ran = r.nextInt(3);
            if (ran == 0) {
                status = "Pending";
            } else if (ran == 1) {
                status = "Approved";
            } else {
                status = "Cancel";
            }
            model.addRow(new ModelStaff(new ImageIcon(getClass().getResource("/com/raven/icon/staff.jpg")), "Mr Raven", "Male", "raven_programming@gmail.com", status).toDataTable());
        }
        table1.fixTable(jScrollPane1);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        table1 = new com.raven.swing.Table();
        buttonPanel = new javax.swing.JPanel();
        btnStartShift = new com.raven.swing.Button();
        btnEndShift = new com.raven.swing.Button();
        btnDetailReport = new com.raven.swing.Button();
        btnShiftManagement = new com.raven.swing.Button();
        btnInventoryDetail = new com.raven.swing.Button();
        btnSalesDetail = new com.raven.swing.Button();
        btnPersonalStats = new com.raven.swing.Button();

        setBackground(new java.awt.Color(250, 250, 250));

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
        btnStartShift.setVisible(false); // Ẩn mặc định

        btnEndShift.setBackground(new java.awt.Color(244, 67, 54));
        btnEndShift.setForeground(new java.awt.Color(255, 255, 255));
        btnEndShift.setText("🔴 Kết Thúc Ca");
        btnEndShift.setFont(new java.awt.Font("sansserif", 1, 12));
        btnEndShift.addActionListener(this::btnEndShiftActionPerformed);
        btnEndShift.setVisible(false); // Ẩn mặc định

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

        table1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Profile", "Name", "Gender", "Email", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane1.setViewportView(table1);
        if (table1.getColumnModel().getColumnCount() > 0) {
            table1.getColumnModel().getColumn(0).setPreferredWidth(50);
            table1.getColumnModel().getColumn(4).setPreferredWidth(50);
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(chartLine1, javax.swing.GroupLayout.DEFAULT_SIZE, 553, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(chartPie, javax.swing.GroupLayout.DEFAULT_SIZE, 529, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addComponent(buttonPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chartLine1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chartPie, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(buttonPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    // Button action listeners - delegate to controller
    private void btnStartShiftActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "START_SHIFT"));
        }
    }

    private void btnEndShiftActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "END_SHIFT"));
        }
    }

    private void btnDetailReportActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "DETAIL_REPORT"));
        }
    }

    private void btnShiftManagementActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SHIFT_MANAGEMENT"));
        }
    }

    private void btnInventoryDetailActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "INVENTORY_DETAIL"));
        }
    }

    private void btnSalesDetailActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "SALES_DETAIL"));
        }
    }

    private void btnPersonalStatsActionPerformed(ActionEvent evt) {
        if (thongKeController != null) {
            thongKeController.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "PERSONAL_STATS"));
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private com.raven.chart.ChartLine chartLine1;
    private com.raven.chart.ChartPie chartPie;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private com.raven.swing.Table table1;
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
    public com.raven.swing.Table getTable1() {
        return table1;
    }
    
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
        if (btnStartShift != null) {
            btnStartShift.setVisible(canStartShift);
        }
        if (btnEndShift != null) {
            btnEndShift.setVisible(canEndShift);
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
    
    /**
     * Cập nhật hiển thị buttons theo role
     */
    public void updateButtonsForRole(int userRole) {
        if (userRole == 1) { // Admin
            btnDetailReport.setVisible(true);
            btnShiftManagement.setVisible(true);
            btnInventoryDetail.setVisible(true);
            btnSalesDetail.setVisible(true);
            btnPersonalStats.setVisible(false);
            btnStartShift.setVisible(false);
            btnEndShift.setVisible(false);
        } else { // Staff
            btnDetailReport.setVisible(true);
            btnShiftManagement.setVisible(false);
            btnInventoryDetail.setVisible(true);
            btnSalesDetail.setVisible(true);
            btnPersonalStats.setVisible(true);
            // btnStartShift và btnEndShift visibility will be handled by updateShiftButtons()
        }
        buttonPanel.revalidate();
        buttonPanel.repaint();
    }
}
