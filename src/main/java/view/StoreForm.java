/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import com.toedter.calendar.JDateChooser;
import controller.ImportsController;
import dao.ImportsDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.DBConnection;
import model.Imports;

/**
 *
 * @author RAVEN
 */
public class StoreForm extends javax.swing.JPanel {

    ImportsController importsController;
    Connection con;
    Map< String, Integer> categoryMap = new HashMap<>();
    Map< String, Integer> supplierMap = new HashMap<>();
    Map< String, Integer> employeeMap = new HashMap<>();
    private String status;

    /**
     * Creates new form Form_2
     */
    public StoreForm() {
        initComponents();
        initController();
    }

    private void initController() {
        try {
            con = DBConnection.getConnection(); // Khởi tạo kết nối database
            if (con == null) {
                throw new SQLDataException("Failed to establish database connection");
            }
            ImportsDAO importsDAO = new ImportsDAO(con); // Khởi tạo ImportsDAO
            importsController = new ImportsController(importsDAO, this); // Khởi tạo ImportsController
        } catch (SQLDataException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo controller: " + e.getMessage());
        }
    }

    private void loadCategory() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT category_id, category_name FROM category"); ResultSet rs = ps.executeQuery()) {

            cboLoaiHangNhap.removeAllItems();
            cboLoaiHangNhap.addItem("--Chọn loại hàng");

            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                int categoryId = rs.getInt("category_id"); // Lấy ID dưới dạng int
                cboLoaiHangNhap.addItem(categoryName);
                categoryMap.put(categoryName, categoryId); // Lưu vào map với value là Integer
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách loại hàng: " + e.getMessage());
        }
    }

    private void loadSupplier() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT supplier_id, supplier_name FROM suppliers"); ResultSet rs = ps.executeQuery()) {

            cboNCCNhap.removeAllItems();
            cboNCCNhap.addItem("--Chọn nhà cung cấp");

            while (rs.next()) {
                String supplierName = rs.getString("supplier_name");
                int supplierId = rs.getInt("supplier_id");
                cboNCCNhap.addItem(supplierName);
                supplierMap.put(supplierName, supplierId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhà cung cấp: " + e.getMessage());
        }
    }
    
    private void loadEmployee() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT employee_id, employee_name FROM employees"); ResultSet rs = ps.executeQuery()){

            cboNhanVienNhap.removeAllItems();
            cboNhanVienNhap.addItem("--Chọn nhân viên nhập");

            while (rs.next()) {
                String employeeName = rs.getString("employee_name");
                int employeeId = rs.getInt("employee_id");
                cboNhanVienNhap.addItem(employeeName);
                employeeMap.put(employeeName, employeeId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhân viên: " + e.getMessage());
        }
    }
    

    public String getCategoryComboBoxSelection() {
        return cboLoaiHangNhap.getSelectedItem() != null ? cboLoaiHangNhap.getSelectedItem().toString() : null;
    }

    public String getSupplierComboBoxSelection() {
        return cboNCCNhap.getSelectedItem() != null ? cboNCCNhap.getSelectedItem().toString() : null;
    }
    
    public String getEmployeeComboBoxSelection() {
        return cboNhanVienNhap.getSelectedItem() != null ? cboNhanVienNhap.getSelectedItem().toString() : null;
    }

//    private void loadEmployee() {
//        try {
//            con = DBConnection.getConnection();
//
//            String sql = "SELECT * From employees  ";
//            PreparedStatement ps = con.prepareStatement(sql);
//
//            ResultSet rs = ps.executeQuery();
//            cboNhanVienNhap.addItem("--Chọn nhân viên nhập");
//            employeeMap.put("--Chọn nhân viên nhập", "");
//
//            while (rs.next()) {
//                cboNhanVienNhap.addItem(rs.getString("full_name"));
//                employeeMap.put(rs.getString("full_name"), rs.getString("employee_id"));
//            }
//            con.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void loadDuLieu() {
        try {
            setMacDinh();
            tblViewNhapHang.removeAll();
            con = DBConnection.getConnection();
            //String sql = "SELECT * FROM products";
            String sql = "SELECT p.product_name, i.quantity, i.import_price, p.unit, i.import_date, "
                    + "s.supplier_name, c.category_name, e.employee_name "
                    + "FROM products p "
                    + "LEFT JOIN category c ON p.category_id = c.category_id "
                    + "LEFT JOIN imports i ON p.product_id = i.product_id "
                    + "LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id "
                    + "LEFT JOIN employees e ON i.employee_id = e.employee_id";
            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            String[] td = {"Tên sản phẩm", "Số lượng", "Giá nhập", "Đơn vị", "Ngày nhập", "Nhà cung cấp", "Loại hàng", "Nhân viên"};
            DefaultTableModel tb = new DefaultTableModel(td, 0);
            while (rs.next()) {
                Vector vt = new Vector();
                vt.add(rs.getString("product_name"));
                vt.add(rs.getString("quantity"));
                vt.add(rs.getString("import_price"));
                vt.add(rs.getString("unit"));
                vt.add(rs.getString("import_date"));
                vt.add(rs.getString("supplier_name"));
                vt.add(rs.getString("category_name"));
                vt.add(rs.getString("employee_name"));
                tb.addRow(vt);
            }
            tblViewNhapHang.setModel(tb);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void enty() {
        txtTenSPNhap.setText("");
        txtSoLuongNhap.setText("");
        txtGiaNhap.setText("");
        txtDonViNhap.setText("");
        //jDateNgayNhap.cleanup();
        cboLoaiHangNhap.setSelectedIndex(0);
        cboNCCNhap.setSelectedIndex(0);
        cboNhanVienNhap.setSelectedIndex(0);
    }

    private void setMacDinh() {
        txtTenSPNhap.setEnabled(false);
        txtSoLuongNhap.setEnabled(false);
        txtGiaNhap.setEnabled(false);
        txtDonViNhap.setEnabled(false);
        jDateNgayNhap.setEnabled(false);
        cboLoaiHangNhap.setEnabled(false);
        cboNCCNhap.setEnabled(false);
        cboNhanVienNhap.setEnabled(false);
        btnLuuNhap.setEnabled(false);
        btnBoQuaNhap.setEnabled(false);
        btnSuaNhap.setEnabled(true);
        btnXoaNhap.setEnabled(true);
        btnThemNhap.setEnabled(true);
    }

    private void setThem() {
        txtTenSPNhap.setEnabled(true);
        txtSoLuongNhap.setEnabled(true);
        txtGiaNhap.setEnabled(true);
        txtDonViNhap.setEnabled(true);
        jDateNgayNhap.setEnabled(true);
        cboLoaiHangNhap.setEnabled(true);
        cboNCCNhap.setEnabled(true);
        cboNhanVienNhap.setEnabled(true);
        btnLuuNhap.setEnabled(true);
        btnBoQuaNhap.setEnabled(true);
        btnXoaNhap.setEnabled(false);
        btnSuaNhap.setEnabled(false);
    }

    private void setSua() {
        txtTenSPNhap.setEnabled(true);
        txtSoLuongNhap.setEnabled(true);
        txtGiaNhap.setEnabled(true);
        txtDonViNhap.setEnabled(true);
        jDateNgayNhap.setEnabled(true);
        cboLoaiHangNhap.setEnabled(true);
        cboNCCNhap.setEnabled(true);
        cboNhanVienNhap.setEnabled(true);
        btnLuuNhap.setEnabled(true);
        btnBoQuaNhap.setEnabled(true);
        btnXoaNhap.setEnabled(false);
        btnThemNhap.setEnabled(false);
    }

    public Connection getCon() {

        return con;
    }

    public Map<String, Integer> getCategoryMap() {
        return categoryMap;
    }

    public Map<String, Integer> getSupplierMap() {
        return supplierMap;
    }

    public Map<String, Integer> getEmployeeMap() {
        return employeeMap;
    }

    public String getStatus() {
        return status;
    }

    public JButton getBtnBoQuaNhap() {
        return btnBoQuaNhap;
    }

    public JButton getBtnLuuNhap() {
        return btnLuuNhap;
    }

    public JButton getBtnSuaNhap() {
        return btnSuaNhap;
    }

    public JButton getBtnThemNhap() {
        return btnThemNhap;
    }

    public JButton getBtnTim() {
        return btnTim;
    }

    public JButton getBtnXoaNhap() {
        return btnXoaNhap;
    }

    public JComboBox<String> getCboCategory() {
        return cboCategory;
    }

    public JComboBox<String> getCboLoaiHangNhap() {
        return cboLoaiHangNhap;
    }

    public JComboBox<String> getCboNCCNhap() {
        return cboNCCNhap;
    }

    public JComboBox<String> getCboNhanVienNhap() {
        return cboNhanVienNhap;
    }

    public JDateChooser getjDateNgayNhap() {
        return jDateNgayNhap;
    }

    public JTable getTblViewNhapHang() {
        return tblViewNhapHang;
    }

    public JTextField getTxtDonViNhap() {
        return txtDonViNhap;
    }

    public JTextField getTxtGiaNhap() {
        return txtGiaNhap;
    }

    public JTextField getTxtSoLuongNhap() {
        return txtSoLuongNhap;
    }

    public JTextField getTxtTenSPNhap() {
        return txtTenSPNhap;
    }

    public void setTblViewNhapHang(JTable tblViewNhapHang) {
        this.tblViewNhapHang = tblViewNhapHang;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        cboCategory = new javax.swing.JComboBox<>();
        btnTim = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblViewNhapHang = new javax.swing.JTable();
        txtTenSPNhap = new javax.swing.JTextField();
        txtSoLuongNhap = new javax.swing.JTextField();
        txtDonViNhap = new javax.swing.JTextField();
        cboNhanVienNhap = new javax.swing.JComboBox<>();
        cboNCCNhap = new javax.swing.JComboBox<>();
        btnThemNhap = new javax.swing.JButton();
        btnSuaNhap = new javax.swing.JButton();
        btnXoaNhap = new javax.swing.JButton();
        btnLuuNhap = new javax.swing.JButton();
        jDateNgayNhap = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txtGiaNhap = new javax.swing.JTextField();
        btnBoQuaNhap = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        cboLoaiHangNhap = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(250, 250, 250));

        cboCategory.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        btnTim.setText("Tìm");
        btnTim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTim)
                .addGap(20, 20, 20))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(288, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTim))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17))
        );

        jTabbedPane1.addTab("Kho hàng", jPanel2);

        jPanel3.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel3ComponentShown(evt);
            }
        });

        jLabel1.setText("Tên sản phẩm");

        jLabel3.setText("Nhà cung cấp");

        jLabel4.setText("Số lượng");

        jLabel5.setText("Giá nhập");

        jLabel6.setText("Ngày nhập");

        jLabel7.setText("Nhân viên nhập");

        tblViewNhapHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(tblViewNhapHang);

        txtSoLuongNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtSoLuongNhapActionPerformed(evt);
            }
        });

        btnThemNhap.setText("Thêm");
        btnThemNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemNhapActionPerformed(evt);
            }
        });

        btnSuaNhap.setText("Sửa");
        btnSuaNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaNhapActionPerformed(evt);
            }
        });

        btnXoaNhap.setText("Xóa");

        btnLuuNhap.setText("Lưu");
        btnLuuNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuNhapActionPerformed(evt);
            }
        });

        jLabel8.setText("Đơn vị");

        txtGiaNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGiaNhapActionPerformed(evt);
            }
        });

        btnBoQuaNhap.setText("Bỏ qua");
        btnBoQuaNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoQuaNhapActionPerformed(evt);
            }
        });

        jLabel9.setText("Loại hàng");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 713, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnThemNhap)
                                .addGap(29, 29, 29)
                                .addComponent(btnSuaNhap)
                                .addGap(29, 29, 29)
                                .addComponent(btnXoaNhap))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(jLabel8)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(txtDonViNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel5)
                                            .addGap(40, 40, 40))
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel1)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                            .addComponent(jLabel4)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(txtSoLuongNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtTenSPNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addComponent(btnLuuNhap)
                                .addGap(28, 28, 28)
                                .addComponent(btnBoQuaNhap)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(41, 41, 41)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cboLoaiHangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
                                        .addComponent(cboNhanVienNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel6))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(jDateNgayNhap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(cboNCCNhap, 0, 222, Short.MAX_VALUE))))))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtTenSPNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addGap(22, 22, 22)
                        .addComponent(jLabel4))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel6)
                            .addComponent(jDateNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(cboNCCNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSoLuongNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(23, 23, 23)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel9)
                                .addComponent(cboLoaiHangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel7)
                        .addComponent(cboNhanVienNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel8)
                        .addComponent(txtDonViNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThemNhap)
                    .addComponent(btnSuaNhap)
                    .addComponent(btnXoaNhap)
                    .addComponent(btnLuuNhap)
                    .addComponent(btnBoQuaNhap))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 214, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        jTabbedPane1.addTab("Nhập hàng", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 746, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 476, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel2.setText("QUẢN LÝ KHO HÀNG");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
            .addGroup(layout.createSequentialGroup()
                .addGap(331, 331, 331)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnTimActionPerformed

    private void btnSuaNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaNhapActionPerformed
        status = "sua";
        setSua();
    }//GEN-LAST:event_btnSuaNhapActionPerformed

    private void txtSoLuongNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtSoLuongNhapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSoLuongNhapActionPerformed

    private void txtGiaNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGiaNhapActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGiaNhapActionPerformed

    private void jPanel3ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel3ComponentShown
        loadCategory();
        loadSupplier();
        loadEmployee();
        loadDuLieu();
    }//GEN-LAST:event_jPanel3ComponentShown

    private void btnThemNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemNhapActionPerformed
        status = "them";
        setThem();
    }//GEN-LAST:event_btnThemNhapActionPerformed

    private void btnBoQuaNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoQuaNhapActionPerformed
        setMacDinh();
        enty();
    }//GEN-LAST:event_btnBoQuaNhapActionPerformed

    private void btnLuuNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuNhapActionPerformed
        try {
            if (status == "them") {
                importsController.addImport();
                loadDuLieu();
            } else if (status == "sua") {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnLuuNhapActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBoQuaNhap;
    private javax.swing.JButton btnLuuNhap;
    private javax.swing.JButton btnSuaNhap;
    private javax.swing.JButton btnThemNhap;
    private javax.swing.JButton btnTim;
    private javax.swing.JButton btnXoaNhap;
    private javax.swing.JComboBox<String> cboCategory;
    private javax.swing.JComboBox<String> cboLoaiHangNhap;
    private javax.swing.JComboBox<String> cboNCCNhap;
    private javax.swing.JComboBox<String> cboNhanVienNhap;
    private com.toedter.calendar.JDateChooser jDateNgayNhap;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblViewNhapHang;
    private javax.swing.JTextField txtDonViNhap;
    private javax.swing.JTextField txtGiaNhap;
    private javax.swing.JTextField txtSoLuongNhap;
    private javax.swing.JTextField txtTenSPNhap;
    // End of variables declaration//GEN-END:variables

}
