/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.Salary;

import view.*;
import com.toedter.calendar.JDateChooser;
import controller.DisplayController;
import controller.SalaryController;
import dao.DisplayDAO;
import dao.SalaryDAO;
import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.DBConnection;
import model.Products;
import model.Salary;
import view.Salary.SuaLuongForm;

/**
 *
 * @author Admin
 */
public class SalaryForm extends javax.swing.JPanel {

    private SalaryController salaryController;
    private SalaryDAO salaryDAO;

    public SalaryForm() {
        initComponents();
        initializeController();
        setupEventListeners();
        loadInitialData();
        
        // Thêm listener cho khi form được hiển thị
        this.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                System.out.println("SalaryForm được hiển thị - reload data");
                if (salaryController != null) {
                    reloadData();
                }
            }
        });
    }

    /**
     * Khởi tạo controller và DAO
     */
    private void initializeController() {
        try {
            System.out.println("Đang khởi tạo SalaryDAO...");
            salaryDAO = new SalaryDAO();
            System.out.println("SalaryDAO khởi tạo thành công!");
            
            System.out.println("Đang khởi tạo SalaryController...");
            salaryController = new SalaryController(salaryDAO, this);
            System.out.println("SalaryController khởi tạo thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khởi tạo: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo: " + e.getMessage());
        }
    }

    /**
     * Thiết lập các event listener
     */
    private void setupEventListeners() {
        bt_add.addActionListener(salaryController);
        bt_delete.addActionListener(salaryController);
        bt_export_excel.addActionListener(salaryController);
        
        // Thêm nút xuất Excel theo nhân viên được chọn
        bt_export_selected.addActionListener(salaryController);
        
        // Double-click để mở form chỉnh sửa
        tb_list_salary.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) { // Thay đổi từ 1 sang 2 để double-click
                    handleEditSalary();
                }
            }
        });
        
    }

    /**
     * Reload dữ liệu - có thể gọi từ bên ngoài
     */
    public void reloadData() {
        loadInitialData();
    }
    
    /**
     * Load dữ liệu ban đầu
     */
    private void loadInitialData() {
        try {
            System.out.println("Đang khởi tạo dữ liệu ban đầu...");
            
            // Test kết nối database
            System.out.println("Đang test kết nối database...");
            salaryDAO.testConnection();
            
            // Kiểm tra lỗi chi tiết
            System.out.println("Đang kiểm tra lỗi chi tiết...");
            salaryDAO.checkDetailedErrors();
            
            // Tạo bảng employees nếu chưa tồn tại
            System.out.println("Đang kiểm tra và tạo bảng employees...");
            salaryDAO.createEmployeesTableIfNotExists();
            
            // Tạo bảng salary nếu chưa tồn tại
            System.out.println("Đang kiểm tra và tạo bảng salary...");
            salaryDAO.createSalaryTableIfNotExists();
            
            // Thêm dữ liệu test cho employees nếu cần
            System.out.println("Đang kiểm tra dữ liệu employees...");
            salaryDAO.addTestEmployeeData();
            
            // Kiểm tra cấu trúc bảng
            System.out.println("Đang kiểm tra cấu trúc bảng...");
            salaryDAO.checkTableStructure();
            
            // Kiểm tra xem có dữ liệu không, nếu không thì thêm dữ liệu test
            String checkDataSql = "SELECT COUNT(*) as count FROM salary";
            try (java.sql.PreparedStatement stmt = salaryDAO.getConnection().prepareStatement(checkDataSql);
                 java.sql.ResultSet rs = stmt.executeQuery()) {
                
                if (rs.next() && rs.getInt("count") == 0) {
                    System.out.println("Không có dữ liệu, đang thêm dữ liệu test...");
                    salaryDAO.addTestData();
                }
            }
            
            // Load danh sách nhân viên
            System.out.println("Đang load danh sách nhân viên...");
            salaryController.loadEmployeesToComboBox(cb_name);
            System.out.println("Load danh sách nhân viên thành công!");
            
            // Load danh sách lương
            System.out.println("Đang load danh sách lương...");
            salaryController.loadSalaryTable(tb_list_salary);
            System.out.println("Load danh sách lương thành công!");
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Lỗi khi tải dữ liệu: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu: " + e.getMessage());
        }
    }

    /**
     * Xử lý chỉnh sửa lương - mở form SuaLuongForm
     */
    private void handleEditSalary() {
        int selectedRow = tb_list_salary.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn lương cần chỉnh sửa!");
            return;
        }

        try {
            Object value = tb_list_salary.getValueAt(selectedRow, 0);
            if (value == null) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
                return;
            }
            
            int salaryId = Integer.parseInt(value.toString());
            
            // Sử dụng controller để mở form chỉnh sửa
            boolean success = salaryController.openEditSalaryForm(salaryId, (java.awt.Frame) getTopLevelAncestor());
            
            if (success) {
                // Reload table sau khi chỉnh sửa
                salaryController.loadSalaryTable(tb_list_salary);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
        }
    }

    // Getter methods cho controller
    public javax.swing.JButton getBt_add() {
        return bt_add;
    }

    public javax.swing.JButton getBt_delete() {
        return bt_delete;
    }

    public javax.swing.JComboBox<String> getCb_name() {
        return cb_name;
    }

    public javax.swing.JTextField getEt_hourly_wage() {
        return et_hourly_wage;
    }

    public com.toedter.calendar.JDateChooser getDt_payment() {
        return dt_payment;
    }

    public javax.swing.JTable getTb_list_salary() {
        return tb_list_salary;
    }

    public javax.swing.JButton getBt_export_excel() {
        return bt_export_excel;
    }
    
    public javax.swing.JButton getBt_export_selected() {
        return bt_export_selected;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        cb_name = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        et_hourly_wage = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        dt_payment = new com.toedter.calendar.JDateChooser();
        bt_add = new javax.swing.JButton();
        bt_delete = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tb_list_salary = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        bt_export_excel = new javax.swing.JButton();
        bt_export_selected = new javax.swing.JButton();

        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentShown(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(0, 102, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("QUẢN LÝ LƯƠNG");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(330, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(355, 355, 355))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(44, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(40, 40, 40))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        jLabel3.setText("Thông tin ");

        jLabel4.setText("Tên nhân viên:");

        cb_name.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText("Lương cơ bản");

        jLabel6.setText("Ngày nhận lương");

        bt_add.setBackground(new java.awt.Color(0, 102, 0));
        bt_add.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bt_add.setForeground(new java.awt.Color(255, 255, 255));
        bt_add.setText("Thêm");
        bt_add.setBorderPainted(false);

        bt_delete.setBackground(new java.awt.Color(204, 51, 0));
        bt_delete.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bt_delete.setForeground(new java.awt.Color(255, 255, 255));
        bt_delete.setText("Xóa");
        bt_delete.setBorderPainted(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dt_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
                                .addComponent(cb_name, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(et_hourly_wage, javax.swing.GroupLayout.Alignment.LEADING))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(bt_add, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(bt_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 7, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cb_name, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(et_hourly_wage, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dt_payment, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bt_add, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 233, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        tb_list_salary.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(tb_list_salary);

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel2.setText("Danh sách lương");

        bt_export_excel.setBackground(new java.awt.Color(51, 153, 0));
        bt_export_excel.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bt_export_excel.setForeground(new java.awt.Color(255, 255, 255));
        bt_export_excel.setText("Xuất Excel");
        bt_export_excel.setBorderPainted(false);

        bt_export_selected.setBackground(new java.awt.Color(255, 140, 0));
        bt_export_selected.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        bt_export_selected.setForeground(new java.awt.Color(255, 255, 255));
        bt_export_selected.setText("Xuất Excel NV");
        bt_export_selected.setBorderPainted(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_export_excel, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_export_selected, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(bt_export_excel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bt_export_selected, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentShown

    }//GEN-LAST:event_jPanel1ComponentShown


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_add;
    private javax.swing.JButton bt_delete;
    private javax.swing.JButton bt_export_excel;
    private javax.swing.JButton bt_export_selected;
    private javax.swing.JComboBox<String> cb_name;
    private com.toedter.calendar.JDateChooser dt_payment;
    private javax.swing.JTextField et_hourly_wage;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable tb_list_salary;
    // End of variables declaration//GEN-END:variables

}
