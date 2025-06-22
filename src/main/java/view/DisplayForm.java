/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import com.toedter.calendar.JDateChooser;
import controller.DisplayController;
import dao.DisplayDAO;
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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.DBConnection;
import model.Products;

/**
 *
 * @author Admin
 */
public class DisplayForm extends javax.swing.JPanel {

    DisplayController displayController;
    Connection con;
    Map< String, Integer> categoryMap = new HashMap<>();
    Map< String, Integer> supplierMap = new HashMap<>();
    private String status;

    public DisplayForm() {
        initComponents();
        initController();
        loadCategoryTim();
        loadSupplierTim();
        loadDuLieuKho();
        loadDuLieuDisplay();
        setMacDinh();
    }

    private void enty() {
        txtMaSP.setText("");
        txtTenSP.setText("");
        txtDay.setText("");
        txtTang.setText("");
        jDateNgayBatDau.setDate(null);
        jDateNgayKetThuc.setDate(null);
    }

    public void setMacDinh() {
        txtMaSP.setEnabled(false);
        txtTenSP.setEnabled(false);
        txtDay.setEnabled(false);
        txtTang.setEnabled(false);
        jDateNgayBatDau.setEnabled(false);
        jDateNgayKetThuc.setEnabled(false);
        btnLuu.setEnabled(false);
        btnBoQua.setEnabled(false);
        btnThem.setEnabled(true);
        btnSua.setEnabled(true);
        btnXoa.setEnabled(true);
        tblProductSearch.setEnabled(false);
    }

    private void setThem() {
        txtMaSP.setEnabled(false);
        txtTenSP.setEnabled(false);
        txtDay.setEnabled(true);
        txtTang.setEnabled(true);
        jDateNgayBatDau.setEnabled(true);
        jDateNgayKetThuc.setEnabled(true);
        btnLuu.setEnabled(true);
        btnBoQua.setEnabled(true);
        btnThem.setEnabled(false);
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        tblProductSearch.setEnabled(true);
    }

    private void setSua() {
        txtMaSP.setEnabled(false);
        txtTenSP.setEnabled(false);
        txtDay.setEnabled(true);
        txtTang.setEnabled(true);
        jDateNgayBatDau.setEnabled(true);
        jDateNgayKetThuc.setEnabled(true);
        btnLuu.setEnabled(true);
        btnBoQua.setEnabled(true);
        btnThem.setEnabled(false);
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        tblProductSearch.setEnabled(false);
    }

    private void initController() {
        try {
            con = DBConnection.getConnection(); // Khởi tạo kết nối database
            if (con == null) {
                throw new SQLDataException("Failed to establish database connection");
            }
            DisplayDAO displayDAO = new DisplayDAO(con);
            displayController = new DisplayController(displayDAO, this);
        } catch (SQLDataException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo controller: " + e.getMessage());
        }
    }

    public void loadDuLieuDisplay() {
        try {
            //setMacDinhKho();
            tblDisplayView.removeAll();
            con = DBConnection.getConnection();
            String sql = "SELECT pd.display_id, pd.product_id, p.product_name, pd.row, pd.floor, pd.start_date, pd.end_date "
                    + "FROM productdisplay pd "
                    + "LEFT JOIN products p ON pd.product_id = p.product_id ";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String[] td = {"Mã Trưng bày", "Mã sản phẩm", "Tên sản phẩm", "Dãy", "Tầng", "Từ Ngày", "Đến ngày"};
            DefaultTableModel tb = new DefaultTableModel(td, 0);
            while (rs.next()) {
                Vector vt = new Vector();
                vt.add(rs.getString("display_id"));
                vt.add(rs.getString("product_id"));
                vt.add(rs.getString("product_name"));
                vt.add(rs.getString("row"));
                vt.add(rs.getString("floor"));
                vt.add(rs.getString("start_date"));
                vt.add(rs.getString("end_date"));
                tb.addRow(vt);
            }
            tblDisplayView.setModel(tb);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCategoryTim() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT category_id, category_name FROM category"); ResultSet rs = ps.executeQuery()) {

            cboLoaiHangTim.removeAllItems();
            cboLoaiHangTim.addItem("--Chọn loại hàng");

            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                int categoryId = rs.getInt("category_id"); // Lấy ID dưới dạng int
                cboLoaiHangTim.addItem(categoryName);
                categoryMap.put(categoryName, categoryId); // Lưu vào map với value là Integer

            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách loại hàng: " + e.getMessage());
        }
    }

    private void loadSupplierTim() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT supplier_id, supplier_name FROM suppliers"); ResultSet rs = ps.executeQuery()) {

            cboNCCTim.removeAllItems();
            cboNCCTim.addItem("--Chọn nhà cung cấp");

            while (rs.next()) {
                String supplierName = rs.getString("supplier_name");
                int supplierId = rs.getInt("supplier_id");
                cboNCCTim.addItem(supplierName);
                supplierMap.put(supplierName, supplierId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhà cung cấp: " + e.getMessage());
        }
    }

    public void loadDuLieuKho() {
        try {
            //setMacDinhKho();
            tblProductSearch.removeAll();
            con = DBConnection.getConnection();
            String sql = "SELECT product_id, product_name FROM products";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String[] td = {"Mã sản phẩm", "Tên sản phẩm"};
            DefaultTableModel tb = new DefaultTableModel(td, 0);
            while (rs.next()) {
                Vector vt = new Vector();
                vt.add(rs.getString("product_id"));
                vt.add(rs.getString("product_name"));
                tb.addRow(vt);
            }
            tblProductSearch.setModel(tb);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDuLieuKho1(List<Products> results) {
        try {
            //setMacDinhKho(); // Nếu bạn có dùng để reset các trường
            // Đặt tiêu đề cột
            String[] columnNames = {"Mã sản phẩm", "Tên sản phẩm"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Duyệt danh sách sản phẩm và thêm vào model
            for (Products product : results) {
                Vector<Object> row = new Vector<>();
                row.add(product.getProduct_id());
                row.add(product.getProduct_name());

                tableModel.addRow(row);
            }

            // Gán model vào bảng
            tblProductSearch.setModel(tableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSupplierSearchComboBox() {
        return cboNCCTim.getSelectedItem() != null ? cboNCCTim.getSelectedItem().toString() : null;
    }

    public String getCategorySearchComboBox() {
        return cboLoaiHangTim.getSelectedItem() != null ? cboLoaiHangTim.getSelectedItem().toString() : null;
    }

    public JTable getTblDisplayView() {
        return tblDisplayView;
    }

    public JTable getTblProductSearch() {
        return tblProductSearch;
    }

    public JTextField getTxtTenSPTim() {
        return txtTenSPTim;
    }

    public Map<String, Integer> getCategoryMap() {
        return categoryMap;
    }

    public Map<String, Integer> getSupplierMap() {
        return supplierMap;
    }

    public JDateChooser getjDateNgayBatDau() {
        return jDateNgayBatDau;
    }

    public JDateChooser getjDateNgayKetThuc() {
        return jDateNgayKetThuc;
    }

    public JTextField getTxtDay() {
        return txtDay;
    }

    public JTextField getTxtTang() {
        return txtTang;
    }

    public JTextField getTxtTenSP() {
        return txtMaSP;
    }

    public JTextField getTxtMaSP() {
        return txtMaSP;
    }
    
    

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblDisplayView = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtMaSP = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtDay = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtTang = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jDateNgayBatDau = new com.toedter.calendar.JDateChooser();
        jDateNgayKetThuc = new com.toedter.calendar.JDateChooser();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnLuu = new javax.swing.JButton();
        btnBoQua = new javax.swing.JButton();
        btnXuatExcel = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        cboLoaiHangTim = new javax.swing.JComboBox<>();
        txtTenSPTim = new javax.swing.JTextField();
        cboNCCTim = new javax.swing.JComboBox<>();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductSearch = new javax.swing.JTable();
        btnTimSP = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        txtTenSP = new javax.swing.JTextField();

        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentShown(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(51, 153, 0));
        jLabel1.setText("Quản lý trưng bày sản phẩm");
        jLabel1.setToolTipText("");

        tblDisplayView.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblDisplayView.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblDisplayViewMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblDisplayView);

        jLabel2.setText("Mã sản phẩm");

        jLabel3.setText("Dãy");

        jLabel4.setText("Tầng");

        jLabel5.setText("Ngày bắt đầu");

        jLabel6.setText("Ngày kết thúc");

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnLuu.setText("Lưu");
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });

        btnBoQua.setText("Bỏ qua");
        btnBoQua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoQuaActionPerformed(evt);
            }
        });

        btnXuatExcel.setText("Xuất excel");

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm kiếm sản phẩm"));

        jLabel7.setText("Tên sản phẩm");

        tblProductSearch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblProductSearch.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductSearchMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblProductSearch);

        btnTimSP.setText("Tìm");
        btnTimSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimSPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(cboLoaiHangTim, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cboNCCTim, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTenSPTim)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnTimSP)
                .addGap(124, 124, 124))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtTenSPTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cboNCCTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLoaiHangTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnTimSP)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel8.setText("Tên sản phẩm");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(262, 262, 262))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(txtTenSP, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(15, 15, 15))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel2)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtMaSP, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(btnThem)
                                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(18, 18, 18)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtDay, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(btnSua)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnXoa)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnLuu)))))))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnBoQua)
                                .addGap(18, 18, 18)
                                .addComponent(btnXuatExcel))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jDateNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtTang)
                                    .addComponent(jDateNgayBatDau, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jScrollPane1))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(txtMaSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel8)
                                        .addComponent(txtTenSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel3)
                                    .addComponent(txtDay, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(btnThem)
                                    .addComponent(btnSua)
                                    .addComponent(btnXoa)
                                    .addComponent(btnLuu)
                                    .addComponent(btnBoQua)
                                    .addComponent(btnXuatExcel)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jDateNgayBatDau, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jDateNgayKetThuc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

    private void btnTimSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimSPActionPerformed
        displayController.searchProduct();
    }//GEN-LAST:event_btnTimSPActionPerformed

    private void tblProductSearchMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductSearchMouseClicked
        try {
            int i = tblProductSearch.getSelectedRow();
            DefaultTableModel tb = (DefaultTableModel) tblProductSearch.getModel();
            txtTenSP.setText(tb.getValueAt(i, 1).toString());
            txtMaSP.setText(tb.getValueAt(i, 0).toString());
        } catch (Exception e) {
            e.getStackTrace();
        }
    }//GEN-LAST:event_tblProductSearchMouseClicked

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        status = "them";
        setThem();
        enty();
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnBoQuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoQuaActionPerformed
        setMacDinh();
        enty();
    }//GEN-LAST:event_btnBoQuaActionPerformed

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        try {
            if (status == "them") {
                displayController.addDisplay();
                loadDuLieuDisplay();

            } else if (status == "sua") {
                displayController.updateDisplay();
                //enty();
                loadDuLieuDisplay();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnLuuActionPerformed

    private void tblDisplayViewMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblDisplayViewMouseClicked
        try {
            int i = tblDisplayView.getSelectedRow();
            DefaultTableModel tb = (DefaultTableModel) tblDisplayView.getModel();
            txtMaSP.setText(tb.getValueAt(i, 1).toString());
            txtTenSP.setText(tb.getValueAt(i, 2).toString());
            txtDay.setText(tb.getValueAt(i, 3).toString());
            txtTang.setText(tb.getValueAt(i, 4).toString());

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            // Lấy và chuyển đổi ngày bắt đầu
            String tuNgayStr = tb.getValueAt(i, 5).toString();
            LocalDate tuNgay = LocalDate.parse(tuNgayStr, formatter);
            Date dateTuNgay = java.sql.Date.valueOf(tuNgay);
            jDateNgayBatDau.setDate(dateTuNgay);

            // Lấy và chuyển đổi ngày kết thúc
            String denNgayStr = tb.getValueAt(i, 6).toString();
            LocalDate denNgay = LocalDate.parse(denNgayStr, formatter);
            Date dateDenNgay = java.sql.Date.valueOf(denNgay);
            jDateNgayKetThuc.setDate(dateDenNgay);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }//GEN-LAST:event_tblDisplayViewMouseClicked

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        displayController.deleteDisplay();
        loadDuLieuDisplay();
        enty();
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        status = "sua";
        setSua();
    }//GEN-LAST:event_btnSuaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBoQua;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimSP;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXuatExcel;
    private javax.swing.JComboBox<String> cboLoaiHangTim;
    private javax.swing.JComboBox<String> cboNCCTim;
    private com.toedter.calendar.JDateChooser jDateNgayBatDau;
    private com.toedter.calendar.JDateChooser jDateNgayKetThuc;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDisplayView;
    private javax.swing.JTable tblProductSearch;
    private javax.swing.JTextField txtDay;
    private javax.swing.JTextField txtMaSP;
    private javax.swing.JTextField txtTang;
    private javax.swing.JTextField txtTenSP;
    private javax.swing.JTextField txtTenSPTim;
    // End of variables declaration//GEN-END:variables

}
