/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view;

import controller.SalesController;
import dao.SalesDAO;
import java.awt.BorderLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import model.Customers;
import model.DBConnection;
import model.Products;

/**
 *
 * @author Admin
 */
public class SalesForm extends javax.swing.JPanel {

    // Phần thông tin
    private JPopupMenu popupMenu;
    private JTable tblSuggestions;
    private DefaultTableModel tblModel;

    SalesController salesController;
    Connection con;

    public SalesForm() {
        initComponents();
        initController();
        loadDuLieuProduct();
        initCustomerSuggestionPopup();
        initTableDonHang();
    }

    private void initController() {
        try {
            con = DBConnection.getConnection(); // Khởi tạo kết nối database
            if (con == null) {
                throw new SQLDataException("Failed to establish database connection");
            }
            SalesDAO salesDAO = new SalesDAO(con);
            salesController = new SalesController(salesDAO, this);
        } catch (SQLDataException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khởi tạo controller: " + e.getMessage());
        }
    }

    private void initCustomerSuggestionPopup() {
        // Khởi tạo bảng popup gợi ý
        popupMenu = new JPopupMenu();
        tblModel = new DefaultTableModel(new Object[]{"ID", "Tên", "SĐT"}, 0);
        tblSuggestions = new JTable(tblModel);
        popupMenu.setLayout(new BorderLayout());
        popupMenu.add(new JScrollPane(tblSuggestions), BorderLayout.CENTER);

        // Đặt kích thước sau khi form hiển thị xong
        SwingUtilities.invokeLater(() -> {
            popupMenu.setPopupSize(txtTimKiemKhachHang.getWidth(), 150);
        });
        // Thêm DocumentListener cho txtTimKiemKhachHang
        txtTimKiemKhachHang.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                salesController.searchCustomers(txtTimKiemKhachHang.getText());
                //txtTimKiemKhachHang.requestFocusInWindow(); // Giữ focus
                SwingUtilities.invokeLater(() -> txtTimKiemKhachHang.requestFocusInWindow());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                salesController.searchCustomers(txtTimKiemKhachHang.getText());
                //txtTimKiemKhachHang.requestFocusInWindow(); // Giữ focus
                SwingUtilities.invokeLater(() -> txtTimKiemKhachHang.requestFocusInWindow());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                salesController.searchCustomers(txtTimKiemKhachHang.getText());
                //txtTimKiemKhachHang.requestFocusInWindow(); // Giữ focus
                SwingUtilities.invokeLater(() -> txtTimKiemKhachHang.requestFocusInWindow());
            }
        });
        // Xử lý sự kiện khi chọn một gợi ý từ bảng
        tblSuggestions.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = tblSuggestions.getSelectedRow();
                if (row >= 0) {
                    String customerId = tblModel.getValueAt(row, 0).toString();
                    String customerName = tblModel.getValueAt(row, 1).toString();
                    String customerPhone = tblModel.getValueAt(row, 2).toString();

                    // Điền thông tin vào các trường
                    txtTimKiemKhachHang.setText(customerName);
                    loadDuLieuPoint(customerId);
                    // Ẩn popup menu sau khi chọn
                    popupMenu.setVisible(false);
                }
            }
        });

        // Đảm bảo txtTimKiemKhachHang giữ focus khi popup hiển thị
        popupMenu.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
                SwingUtilities.invokeLater(() -> txtTimKiemKhachHang.requestFocusInWindow());
            }

            @Override
            public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {
                SwingUtilities.invokeLater(() -> txtTimKiemKhachHang.requestFocusInWindow());
            }

            @Override
            public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {
                SwingUtilities.invokeLater(() -> txtTimKiemKhachHang.requestFocusInWindow());
            }
        });
    }

    private void initTableDonHang() {
        DefaultTableModel modelDonHang = new DefaultTableModel(
                new Object[]{"Tên sản phẩm", "Giá bán", "Số lượng"}, 0
        );
        tblDonHangView.setModel(modelDonHang);
    }

    public void loadDuLieuPoint(String customerId) {
        try {
            txtPoint.removeAll();
            con = DBConnection.getConnection();
            String sql = "SELECT points FROM customers WHERE customer_id = ? ";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, customerId);
            ResultSet rs = ps.executeQuery();

            int point = 0;
            if (rs.next()) {
                point = rs.getInt("points");
            }
            txtPoint.setText(String.valueOf(point));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void loadDuLieuProduct() {
        try {
            tblViewProduct.removeAll();
            con = DBConnection.getConnection();
            String sql = "SELECT p.product_id, p.product_name, c.category_name, p.price, p.stock_quantity, p.unit "
                    + "FROM products p "
                    + "LEFT JOIN category c ON p.category_id = c.category_id ";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            String[] td = {"Mã sản phẩm", "Tên sản phẩm", "Tên loại hàng", "Giá", "Số lượng tồn kho", "Đơn vị"};
            DefaultTableModel tb = new DefaultTableModel(td, 0);
            while (rs.next()) {
                Vector vt = new Vector();
                vt.add(rs.getString("product_id"));
                vt.add(rs.getString("product_name"));
                vt.add(rs.getString("category_name"));
                vt.add(rs.getString("price"));
                vt.add(rs.getString("stock_quantity"));
                vt.add(rs.getString("unit"));
                tb.addRow(vt);
            }
            tblViewProduct.setModel(tb);
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDuLieuProduct1(List<Products> results) {
        try {
            // Đặt tiêu đề cột
            String[] columnNames = {"Mã sản phẩm", "Tên sản phẩm", "Tên loại hàng", "Giá", "Số lượng tồn kho", "Đơn vị"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Duyệt danh sách ssản phẩm và thêm vào model
            for (Products product : results) {
                Vector<Object> row = new Vector<>();
                row.add(product.getProduct_id());
                row.add(product.getProduct_name());
                row.add(product.getCategoryName());
                row.add(product.getPrice());
                row.add(product.getStock_quantity());
                row.add(product.getUnit());
                tableModel.addRow(row);
            }

            // Gán model vào bảng
            tblViewProduct.setModel(tableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Phần hóa đơn
    private int tinhTongTienHang() {
        DefaultTableModel modelDonHang = (DefaultTableModel) tblDonHangView.getModel();
        int tongTien = 0;
        for (int i = 0; i < modelDonHang.getRowCount(); i++) {
            int gia = Integer.parseInt(modelDonHang.getValueAt(i, 1).toString());
            int soLuong = Integer.parseInt(modelDonHang.getValueAt(i, 2).toString());
            tongTien += gia * soLuong;
        }
        txtTongTienHang.setText(String.valueOf(tongTien));
        return tongTien;
    }

    private int tinhGiamGia() {
        if (chkDungPoint.isSelected()) {
            try {
                int diemPoint = Integer.parseInt(txtPoint.getText());
                txtGiamGia.setText(String.valueOf(diemPoint)); // hiển thị giảm giá = point
                return diemPoint;
            } catch (NumberFormatException e) {
                txtGiamGia.setText("0"); // nếu nhập sai thì hiện 0
                return 0;
            }
        } else {
            txtGiamGia.setText("0"); // nếu không chọn dùng point
            return 0;
        }
    }

    private void tinhKhachCanTra() {
        int tongTien = tinhTongTienHang();
        int giamGia = tinhGiamGia();
        int khachCanTra = tongTien - giamGia;
        txtKhachCanTra.setText(String.valueOf(khachCanTra));
    }

    public void loadTienThua() {
        try {
            // Lấy tiền khách đưa từ ô nhập
            int tienKhachTra = Integer.parseInt(txtKhachTra.getText());
            // Lấy tiền khách cần phải trả
            int tienKhachCanTra = Integer.parseInt(txtKhachCanTra.getText());

            // Tính tiền thừa
            int tienThua = tienKhachTra - tienKhachCanTra;

            // Hiển thị kết quả
            txtTienThua.setText(String.valueOf(tienThua));
        } catch (Exception e) {
            txtTienThua.setText("0");
        }
    }

    public JTextField getTxtThemSdtKhach() {
        return txtThemSdtKhach;
    }

    public JTextField getTxtThemTenKhach() {
        return txtThemTenKhach;
    }

    public JTextField getTxtTimKiemKhachHang() {
        return txtTimKiemKhachHang;
    }

    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public JTable getTblSuggestions() {
        return tblSuggestions;
    }

    public JTextField getTxtTimSP() {
        return txtTimSP;
    }

    public JTextField getTxtKhachTra() {
        return txtKhachTra;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtTimSP = new javax.swing.JTextField();
        btnTimSP = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDonHangView = new javax.swing.JTable();
        btnThemSP = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblViewProduct = new javax.swing.JTable();
        btnXoaSP = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        spnSoLuong = new javax.swing.JSpinner();
        jPanel2 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        txtPoint = new javax.swing.JTextField();
        chkDungPoint = new javax.swing.JCheckBox();
        jLabel9 = new javax.swing.JLabel();
        txtTimKiemKhachHang = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtThemTenKhach = new javax.swing.JTextField();
        txtThemSdtKhach = new javax.swing.JTextField();
        btnThemKhachHang = new javax.swing.JButton();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtTongTienHang = new javax.swing.JTextField();
        txtKhachCanTra = new javax.swing.JTextField();
        txtGiamGia = new javax.swing.JTextField();
        txtKhachTra = new javax.swing.JTextField();
        txtTienThua = new javax.swing.JTextField();
        btnThanhToan = new javax.swing.JButton();
        btnInHoaDon = new javax.swing.JButton();
        btnTienThuaTraKhach = new javax.swing.JButton();

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));
        jPanel1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel1ComponentShown(evt);
            }
        });

        btnTimSP.setText("Tìm");
        btnTimSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimSPActionPerformed(evt);
            }
        });

        jTabbedPane1.setBackground(new java.awt.Color(204, 255, 204));
        jTabbedPane1.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jTabbedPane1ComponentShown(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        tblDonHangView.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblDonHangView.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                tblDonHangViewComponentShown(evt);
            }
        });
        jScrollPane2.setViewportView(tblDonHangView);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 741, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Đơn hàng", jPanel3);

        btnThemSP.setText("Thêm sản phẩm");
        btnThemSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemSPActionPerformed(evt);
            }
        });

        tblViewProduct.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(tblViewProduct);

        btnXoaSP.setText("Xóa sản phẩm");
        btnXoaSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaSPActionPerformed(evt);
            }
        });

        jLabel10.setText("Số lượng");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spnSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnThemSP, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnXoaSP, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jTabbedPane1)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(txtTimSP, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(btnTimSP))
                        .addComponent(jScrollPane1)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTimSP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimSP))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(13, 13, 13)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThemSP)
                    .addComponent(btnXoaSP)
                    .addComponent(jLabel10)
                    .addComponent(spnSoLuong, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(17, 17, 17))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204)));

        jTabbedPane2.setBackground(new java.awt.Color(204, 255, 204));

        jPanel4.setBackground(new java.awt.Color(255, 255, 255));

        jLabel8.setText("Tìm khách hàng");

        chkDungPoint.setText("Dùng điểm point");
        chkDungPoint.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chkDungPointActionPerformed(evt);
            }
        });

        jLabel9.setText("Point");

        txtTimKiemKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTimKiemKhachHangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPoint)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addComponent(chkDungPoint)
                            .addComponent(jLabel9))
                        .addGap(0, 140, Short.MAX_VALUE))
                    .addComponent(txtTimKiemKhachHang))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(txtTimKiemKhachHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtPoint, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chkDungPoint)
                .addGap(25, 25, 25))
        );

        jTabbedPane2.addTab("Thông tin", jPanel4);

        jPanel6.setBackground(new java.awt.Color(255, 255, 255));

        jLabel6.setText("Tên khách");

        jLabel7.setText("Số điện thoại");

        btnThemKhachHang.setText("Thêm khách hàng");
        btnThemKhachHang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemKhachHangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(txtThemTenKhach))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(26, 26, 26)
                        .addComponent(txtThemSdtKhach, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnThemKhachHang)
                .addGap(65, 65, 65))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(txtThemTenKhach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(txtThemSdtKhach, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addComponent(btnThemKhachHang)
                .addGap(28, 28, 28))
        );

        jTabbedPane2.addTab("Mở rộng", jPanel6);

        jTabbedPane3.setBackground(new java.awt.Color(204, 255, 204));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        jLabel1.setText("Tổng tiền hàng");

        jLabel2.setText("Giảm giá");

        jLabel3.setText("Khách trả");

        jLabel4.setText("Khách cần trả");

        btnThanhToan.setText("Thanh toán");

        btnInHoaDon.setText("In");

        btnTienThuaTraKhach.setText("Tiền thừa");
        btnTienThuaTraKhach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTienThuaTraKhachActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(37, 37, 37)
                        .addComponent(txtTongTienHang))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(46, 46, 46)
                        .addComponent(txtKhachCanTra, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(71, 71, 71)
                        .addComponent(txtGiamGia, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(btnTienThuaTraKhach))
                        .addGap(38, 38, 38)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTienThua)
                            .addComponent(txtKhachTra, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnThanhToan)
                .addGap(34, 34, 34))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel5Layout.createSequentialGroup()
                    .addGap(43, 43, 43)
                    .addComponent(btnInHoaDon)
                    .addContainerGap(150, Short.MAX_VALUE)))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtTongTienHang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(txtGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtKhachCanTra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtKhachTra, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTienThua, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTienThuaTraKhach))
                .addGap(45, 45, 45)
                .addComponent(btnThanhToan)
                .addContainerGap(57, Short.MAX_VALUE))
            .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                    .addContainerGap(239, Short.MAX_VALUE)
                    .addComponent(btnInHoaDon)
                    .addGap(57, 57, 57)))
        );

        jTabbedPane3.addTab("Hóa đơn", jPanel5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jTabbedPane3)
                    .addComponent(jTabbedPane2))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 354, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel1ComponentShown
        loadDuLieuProduct();
    }//GEN-LAST:event_jPanel1ComponentShown

    private void tblDonHangViewComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_tblDonHangViewComponentShown

    }//GEN-LAST:event_tblDonHangViewComponentShown

    private void jTabbedPane1ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jTabbedPane1ComponentShown
        //loadDuLieuProduct();
    }//GEN-LAST:event_jTabbedPane1ComponentShown

    private void btnThemKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemKhachHangActionPerformed
        salesController.addCustomer();
        txtThemSdtKhach.setText("");
        txtThemTenKhach.setText("");
    }//GEN-LAST:event_btnThemKhachHangActionPerformed

    private void txtTimKiemKhachHangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTimKiemKhachHangActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTimKiemKhachHangActionPerformed

    private void btnTimSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimSPActionPerformed
        salesController.searchProduct();
    }//GEN-LAST:event_btnTimSPActionPerformed

    private void btnThemSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemSPActionPerformed
        int selectedRow = tblViewProduct.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm để thêm!");
            return;
        }

        int soLuongThem = (int) spnSoLuong.getValue();
        if (soLuongThem <= 0) {
            JOptionPane.showMessageDialog(this, "Số lượng phải lớn hơn 0!");
            return;
        }

        DefaultTableModel modelSanPham = (DefaultTableModel) tblViewProduct.getModel();
        DefaultTableModel modelDonHang = (DefaultTableModel) tblDonHangView.getModel();

        String tenSanPham = (String) modelSanPham.getValueAt(selectedRow, 1); // cột 1: tên sản phẩm
        String giaBanStr = (String) modelSanPham.getValueAt(selectedRow, 3); // cột 3: giá bán
        // Tăng thành giá bán
        int giaBanGoc = Integer.parseInt(giaBanStr);
        int giaBanTang = (int) (giaBanGoc * 1.2); // tăng 20%

        // Kiểm tra nếu sản phẩm đã có trong đơn hàng thì cộng dồn số lượng
        boolean daCo = false;
        for (int i = 0; i < modelDonHang.getRowCount(); i++) {
            String tenTrongDonHang = (String) modelDonHang.getValueAt(i, 0);
            if (tenSanPham.equals(tenTrongDonHang)) {
                int soLuongCu = (int) modelDonHang.getValueAt(i, 2);
                modelDonHang.setValueAt(soLuongCu + soLuongThem, i, 2);
                daCo = true;
                break;
            }
        }
        // Nếu chưa có thì thêm dòng mới
        if (!daCo) {
            modelDonHang.addRow(new Object[]{
                tenSanPham,
                giaBanTang,
                soLuongThem
            });
        }

        // Reset spinner về 0
        spnSoLuong.setValue(0);
        tinhTongTienHang();
        tinhGiamGia();
        tinhKhachCanTra();
    }//GEN-LAST:event_btnThemSPActionPerformed

    private void btnXoaSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaSPActionPerformed
        int selectedRow = tblDonHangView.getSelectedRow();

        if (selectedRow == -1) {
            // Không có dòng nào được chọn
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một sản phẩm để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Nếu có dòng được chọn, xóa dòng đó
        DefaultTableModel model = (DefaultTableModel) tblDonHangView.getModel();
        model.removeRow(selectedRow);
        tinhTongTienHang();
        tinhGiamGia();
        tinhKhachCanTra();
    }//GEN-LAST:event_btnXoaSPActionPerformed

    private void chkDungPointActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chkDungPointActionPerformed
        tinhGiamGia();
        tinhKhachCanTra();
        loadTienThua();
    }//GEN-LAST:event_chkDungPointActionPerformed

    private void btnTienThuaTraKhachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTienThuaTraKhachActionPerformed
        loadTienThua();
    }//GEN-LAST:event_btnTienThuaTraKhachActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnInHoaDon;
    private javax.swing.JButton btnThanhToan;
    private javax.swing.JButton btnThemKhachHang;
    private javax.swing.JButton btnThemSP;
    private javax.swing.JButton btnTienThuaTraKhach;
    private javax.swing.JButton btnTimSP;
    private javax.swing.JButton btnXoaSP;
    private javax.swing.JCheckBox chkDungPoint;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JSpinner spnSoLuong;
    private javax.swing.JTable tblDonHangView;
    private javax.swing.JTable tblViewProduct;
    private javax.swing.JTextField txtGiamGia;
    private javax.swing.JTextField txtKhachCanTra;
    private javax.swing.JTextField txtKhachTra;
    private javax.swing.JTextField txtPoint;
    private javax.swing.JTextField txtThemSdtKhach;
    private javax.swing.JTextField txtThemTenKhach;
    private javax.swing.JTextField txtTienThua;
    private javax.swing.JTextField txtTimKiemKhachHang;
    private javax.swing.JTextField txtTimSP;
    private javax.swing.JTextField txtTongTienHang;
    // End of variables declaration//GEN-END:variables
}
