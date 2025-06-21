package view;

import com.toedter.calendar.JDateChooser;
import controller.ImportsController;
import dao.ImportsDAO;
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
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import model.DBConnection;
import model.Imports;
import model.Products;

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

    private void loadCategoryNhap() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT category_id, category_name FROM category"); ResultSet rs = ps.executeQuery()) {

            cboLoaiHangNhap.removeAllItems();
            cboLoaiHangNhap.addItem("--Chọn loại hàng");

            cboLoaiHangNhapTim.removeAllItems();
            cboLoaiHangNhapTim.addItem("--Chọn loại hàng");

            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                int categoryId = rs.getInt("category_id"); // Lấy ID dưới dạng int
                cboLoaiHangNhap.addItem(categoryName);
                categoryMap.put(categoryName, categoryId); // Lưu vào map với value là Integer

                cboLoaiHangNhapTim.addItem(categoryName);
                categoryMap.put(categoryName, categoryId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách loại hàng: " + e.getMessage());
        }
    }

    private void loadCategoryKho() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT category_id, category_name FROM category"); ResultSet rs = ps.executeQuery()) {

            cboLoaiHangKho.removeAllItems();
            cboLoaiHangKho.addItem("--Chọn loại hàng");

            cboLoaiHangKhoTim.removeAllItems();
            cboLoaiHangKhoTim.addItem("--Chọn loại hàng");

            while (rs.next()) {
                String categoryName = rs.getString("category_name");
                int categoryId = rs.getInt("category_id"); // Lấy ID dưới dạng int
                cboLoaiHangKho.addItem(categoryName);
                categoryMap.put(categoryName, categoryId); // Lưu vào map với value là Integer

                cboLoaiHangKhoTim.addItem(categoryName);
                categoryMap.put(categoryName, categoryId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách loại hàng: " + e.getMessage());
        }
    }

    private void loadSupplierNhap() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT supplier_id, supplier_name FROM suppliers"); ResultSet rs = ps.executeQuery()) {

            cboNCCNhap.removeAllItems();
            cboNCCNhap.addItem("--Chọn nhà cung cấp");

            cboNCCNhapTim.removeAllItems();
            cboNCCNhapTim.addItem("--Chọn nhà cung cấp");

            while (rs.next()) {
                String supplierName = rs.getString("supplier_name");
                int supplierId = rs.getInt("supplier_id");
                cboNCCNhap.addItem(supplierName);
                supplierMap.put(supplierName, supplierId);

                cboNCCNhapTim.addItem(supplierName);
                supplierMap.put(supplierName, supplierId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhà cung cấp: " + e.getMessage());
        }
    }

    private void loadSupplierKho() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT supplier_id, supplier_name FROM suppliers"); ResultSet rs = ps.executeQuery()) {

            cboNCCKhoTim.removeAllItems();
            cboNCCKhoTim.addItem("--Chọn nhà cung cấp");

            while (rs.next()) {
                String supplierName = rs.getString("supplier_name");
                int supplierId = rs.getInt("supplier_id");
                cboNCCKhoTim.addItem(supplierName);
                supplierMap.put(supplierName, supplierId);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhà cung cấp: " + e.getMessage());
        }
    }

    private void loadEmployeeNhap() {
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement("SELECT employee_id, full_name FROM employees"); ResultSet rs = ps.executeQuery()) {

            cboNhanVienNhap.removeAllItems();
            cboNhanVienNhap.addItem("--Chọn nhân viên nhập");

            while (rs.next()) {
//                String employeeName = rs.getString("employee_name");
                String full_name = rs.getString("full_name");
                int employeeId = rs.getInt("employee_id");
                cboNhanVienNhap.addItem(full_name);
                employeeMap.put(full_name, employeeId);
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

    public String getCategoryStoreComboBox() {
        return cboLoaiHangKho.getSelectedItem() != null ? cboLoaiHangKho.getSelectedItem().toString() : null;
    }

    public String getCategorySearchComboBox() {
        return cboLoaiHangKhoTim.getSelectedItem() != null ? cboLoaiHangKhoTim.getSelectedItem().toString() : null;
    }

    public String getSupplierSearchComboBox() {
        return cboNCCKhoTim.getSelectedItem() != null ? cboNCCKhoTim.getSelectedItem().toString() : null;
    }

    public String getCboLoaiHangNhapTim() {
        return cboLoaiHangNhapTim.getSelectedItem() != null ? cboLoaiHangNhapTim.getSelectedItem().toString() : null;
    }

    public String getCboNCCNhapTim() {
        return cboNCCNhapTim.getSelectedItem() != null ? cboNCCNhapTim.getSelectedItem().toString() : null;
    }

    public JDateChooser getjDateTimDen() {
        return jDateTimDen;
    }

    public JDateChooser getjDateTimTu() {
        return jDateTimTu;
    }

    private void loadDuLieuNhap() {
        try {
            setMacDinh();
            tblViewNhapHang.removeAll();
            con = DBConnection.getConnection();
            //String sql = "SELECT * FROM products";
            String sql = "SELECT i.import_id, p.product_name, i.quantity, i.import_price, p.unit, i.import_date, "
                    + "s.supplier_name, c.category_name, e.full_name "
                    + "FROM products p "
                    + "LEFT JOIN category c ON p.category_id = c.category_id "
                    + "LEFT JOIN imports i ON p.product_id = i.product_id "
                    + "LEFT JOIN suppliers s ON i.supplier_id = s.supplier_id "
                    + "LEFT JOIN employees e ON i.employee_id = e.employee_id "
                    + "WHERE i.import_id IS NOT NULL";
            PreparedStatement ps = con.prepareStatement(sql);

            ResultSet rs = ps.executeQuery();

            String[] td = {"Mã nhập", "Tên sản phẩm", "Số lượng", "Giá nhập", "Đơn vị", "Ngày nhập", "Nhà cung cấp", "Loại hàng", "Nhân viên"};
            DefaultTableModel tb = new DefaultTableModel(td, 0);
            while (rs.next()) {
                Vector vt = new Vector();
                vt.add(rs.getString("import_id"));
                vt.add(rs.getString("product_name"));
                vt.add(rs.getString("quantity"));
                vt.add(rs.getString("import_price"));
                vt.add(rs.getString("unit"));
                vt.add(rs.getString("import_date"));
                vt.add(rs.getString("supplier_name"));
                vt.add(rs.getString("category_name"));
                vt.add(rs.getString("full_name"));
                tb.addRow(vt);
            }
            tblViewNhapHang.setModel(tb);
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDuLieuNhap1(List<Imports> results) {
        try {
            setMacDinhKho(); // Nếu bạn có dùng để reset các trường

            // Đặt tiêu đề cột
            String[] columnNames = {"Mã nhập", "Tên sản phẩm", "Số lượng", "Giá nhập", "Đơn vị", "Ngày nhập", "Nhà cung cấp", "Loại hàng", "Nhân viên"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Duyệt danh sách sản phẩm và thêm vào model
            for (Imports imports : results) {
                Vector<Object> row = new Vector<>();
                row.add(imports.getImport_id());
                row.add(imports.getProduct_name());
                row.add(imports.getQuantity());
                row.add(imports.getImport_price());
                row.add(imports.getUnit());
                row.add(imports.getImport_date());
                row.add(imports.getSupplier_name());
                row.add(imports.getCategory_name());
                row.add(imports.getEmployee_name());
                tableModel.addRow(row);
            }

            // Gán model vào bảng
            tblViewNhapHang.setModel(tableModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDuLieuKho() {
        try {
            setMacDinhKho();
            tblViewKhoHang.removeAll();
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
            tblViewKhoHang.setModel(tb);
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadDuLieuKho1(List<Products> results) {
        try {
            setMacDinhKho(); // Nếu bạn có dùng để reset các trường

            // Đặt tiêu đề cột
            String[] columnNames = {"Mã sản phẩm", "Tên sản phẩm", "Tên loại hàng", "Giá", "Số lượng tồn kho", "Đơn vị"};
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);

            // Duyệt danh sách sản phẩm và thêm vào model
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
            tblViewKhoHang.setModel(tableModel);
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
        jDateNgayNhap.setDate(null);
        cboLoaiHangNhap.setSelectedIndex(0);
        cboNCCNhap.setSelectedIndex(0);
        cboNhanVienNhap.setSelectedIndex(0);
    }

    private void entyKho() {
        txtTenSPKho.setText("");
        cboLoaiHangKho.setSelectedIndex(0);
        txtGiaKho.setText("");
        txtTonKho.setText("");
        txtDonViKho.setText("");
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

    private void setMacDinhKho() {
        txtTenSPKho.setEnabled(false);
        txtGiaKho.setEnabled(false);
        txtTonKho.setEnabled(false);
        txtDonViKho.setEnabled(false);
        cboLoaiHangKho.setEnabled(false);
        btnLuuKho.setEnabled(false);
        btnBoQuaKho.setEnabled(false);
        btnSuaKho.setEnabled(true);
        btnXoaKho.setEnabled(true);
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
        enty();
    }

    private void setSua() {
        txtTenSPNhap.setEnabled(false);
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
        //enty();
    }

    private void setSuaKho() {
        txtTenSPKho.setEnabled(true);
        txtGiaKho.setEnabled(true);
        txtTonKho.setEnabled(false);
        txtDonViKho.setEnabled(true);
        cboLoaiHangKho.setEnabled(true);
        btnLuuKho.setEnabled(true);
        btnBoQuaKho.setEnabled(true);
        btnXoaKho.setEnabled(false);
        btnSuaKho.setEnabled(false);
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
        return btnTimKho;
    }

    public JButton getBtnXoaNhap() {
        return btnXoaNhap;
    }

    public JComboBox<String> getCboCategory() {
        return cboLoaiHangKhoTim;
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

    public JTable getTblViewKhoHang() {
        return tblViewKhoHang;
    }

    public void setTblViewKhoHang(JTable tblViewKhoHang) {
        this.tblViewKhoHang = tblViewKhoHang;
    }

    public JComboBox<String> getCboLoaiHangKho() {
        return cboLoaiHangKho;
    }

    public JComboBox<String> getCboLoaiHangKhoTim() {
        return cboLoaiHangKhoTim;
    }

    public JComboBox<String> getCboNCCKhoTim() {
        return cboNCCKhoTim;
    }

    public JTextField getTxtDonViKho() {
        return txtDonViKho;
    }

    public JTextField getTxtGiaKho() {
        return txtGiaKho;
    }

    public JTextField getTxtTenSPKho() {
        return txtTenSPKho;
    }

    public JTextField getTxtTenSPTimKho() {
        return txtTenSPTimKho;
    }

    public JTextField getTxtTonKho() {
        return txtTonKho;
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        cboLoaiHangKhoTim = new javax.swing.JComboBox<>();
        btnTimKho = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblViewKhoHang = new javax.swing.JTable();
        cboNCCKhoTim = new javax.swing.JComboBox<>();
        txtTenSPTimKho = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        txtTenSPKho = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtGiaKho = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTonKho = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtDonViKho = new javax.swing.JTextField();
        cboLoaiHangKho = new javax.swing.JComboBox<>();
        btnSuaKho = new javax.swing.JButton();
        btnXoaKho = new javax.swing.JButton();
        btnLuuKho = new javax.swing.JButton();
        btnBoQuaKho = new javax.swing.JButton();
        btnXuatExcel = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
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
        btnNhapExcel = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jDateTimTu = new com.toedter.calendar.JDateChooser();
        cboLoaiHangNhapTim = new javax.swing.JComboBox<>();
        cboNCCNhapTim = new javax.swing.JComboBox<>();
        btnTimNhap = new javax.swing.JButton();
        jDateTimDen = new com.toedter.calendar.JDateChooser();
        btnXuatExcelNhap = new javax.swing.JButton();

        setBackground(new java.awt.Color(250, 250, 250));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jTabbedPane1.setBackground(new java.awt.Color(204, 255, 204));

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                jPanel2ComponentShown(evt);
            }
        });

        btnTimKho.setText("Tìm");
        btnTimKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKhoActionPerformed(evt);
            }
        });

        tblViewKhoHang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblViewKhoHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblViewKhoHangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblViewKhoHang);

        txtTenSPTimKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenSPTimKhoActionPerformed(evt);
            }
        });

        jLabel10.setText("Tên sản phẩm");

        jLabel11.setText("Tên sản phẩm");

        txtTenSPKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenSPKhoActionPerformed(evt);
            }
        });

        jLabel12.setText("Tên loại hàng");

        txtGiaKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGiaKhoActionPerformed(evt);
            }
        });

        jLabel13.setText("Giá");

        txtTonKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTonKhoActionPerformed(evt);
            }
        });

        jLabel14.setText("Tồn kho");

        jLabel15.setText("Đơn vị");

        txtDonViKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDonViKhoActionPerformed(evt);
            }
        });

        btnSuaKho.setText("Sửa");
        btnSuaKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaKhoActionPerformed(evt);
            }
        });

        btnXoaKho.setText("Xóa");
        btnXoaKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaKhoActionPerformed(evt);
            }
        });

        btnLuuKho.setText("Lưu");
        btnLuuKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuKhoActionPerformed(evt);
            }
        });

        btnBoQuaKho.setText("Bỏ qua");
        btnBoQuaKho.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoQuaKhoActionPerformed(evt);
            }
        });

        btnXuatExcel.setText("Xuất excel");

        jLabel16.setText("Tìm kiếm");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addGap(18, 18, 18)
                        .addComponent(cboLoaiHangKhoTim, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cboNCCKhoTim, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(txtTenSPTimKho, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(26, 26, 26)
                        .addComponent(btnTimKho)
                        .addGap(0, 267, Short.MAX_VALUE))
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(btnSuaKho)
                                .addGap(33, 33, 33)
                                .addComponent(btnXoaKho)
                                .addGap(30, 30, 30)
                                .addComponent(btnLuuKho)
                                .addGap(32, 32, 32)
                                .addComponent(btnBoQuaKho)
                                .addGap(32, 32, 32)
                                .addComponent(btnXuatExcel))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(txtDonViKho, javax.swing.GroupLayout.DEFAULT_SIZE, 214, Short.MAX_VALUE)
                                    .addComponent(txtGiaKho, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTenSPKho, javax.swing.GroupLayout.Alignment.LEADING))
                                .addGap(51, 51, 51)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel12)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jLabel14)
                                        .addGap(42, 42, 42)))
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtTonKho)
                                    .addComponent(cboLoaiHangKho, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtTenSPTimKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10)
                    .addComponent(cboNCCKhoTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cboLoaiHangKhoTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16)
                    .addComponent(btnTimKho))
                .addGap(59, 59, 59)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTenSPKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtGiaKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel13)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel12)
                            .addComponent(cboLoaiHangKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTonKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtDonViKho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSuaKho)
                    .addComponent(btnXoaKho)
                    .addComponent(btnLuuKho)
                    .addComponent(btnBoQuaKho)
                    .addComponent(btnXuatExcel))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 269, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Kho hàng", jPanel2);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
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
        tblViewNhapHang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblViewNhapHangMouseClicked(evt);
            }
        });
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
        btnXoaNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaNhapActionPerformed(evt);
            }
        });

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

        btnNhapExcel.setText("Nhập excel");
        btnNhapExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNhapExcelActionPerformed(evt);
            }
        });

        jLabel2.setText("Tìm kiếm");

        jLabel17.setText("Từ");

        jLabel18.setText("Đến");

        btnTimNhap.setText("Tìm");
        btnTimNhap.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimNhapActionPerformed(evt);
            }
        });

        btnXuatExcelNhap.setText("Xuất excel");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(87, 87, 87)
                                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jDateTimTu, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(29, 29, 29)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateTimDen, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnTimNhap)
                        .addGap(22, 22, 22))
                    .addComponent(jScrollPane2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addComponent(jLabel8)
                                        .addGap(53, 53, 53)))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtSoLuongNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtTenSPNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtDonViNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(btnThemNhap)
                                .addGap(29, 29, 29)
                                .addComponent(btnSuaNhap)
                                .addGap(29, 29, 29)
                                .addComponent(btnXoaNhap)
                                .addGap(28, 28, 28)
                                .addComponent(btnLuuNhap)))
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(47, 47, 47)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel6))
                                        .addGap(22, 22, 22))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel9))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jDateNgayNhap, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(cboNCCNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cboLoaiHangNhap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cboNhanVienNhap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(btnBoQuaNhap)
                                .addGap(28, 28, 28)
                                .addComponent(btnNhapExcel)
                                .addGap(29, 29, 29)
                                .addComponent(btnXuatExcelNhap))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(167, 167, 167)
                                .addComponent(cboLoaiHangNhapTim, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(27, 27, 27)
                                .addComponent(cboNCCNhapTim, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(0, 121, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel17))
                    .addComponent(jLabel18)
                    .addComponent(jDateTimTu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cboLoaiHangNhapTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cboNCCNhapTim, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnTimNhap))
                    .addComponent(jDateTimDen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(txtTenSPNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel1))
                                .addGap(22, 22, 22)
                                .addComponent(jLabel4))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(42, 42, 42)
                                .addComponent(txtSoLuongNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(23, 23, 23)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel5)
                                    .addComponent(txtGiaNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(txtDonViNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(jDateNgayNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cboNCCNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3))
                                .addGap(23, 23, 23)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(cboLoaiHangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel9))))
                        .addGap(18, 18, 18)
                        .addComponent(cboNhanVienNhap, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThemNhap)
                    .addComponent(btnSuaNhap)
                    .addComponent(btnXoaNhap)
                    .addComponent(btnLuuNhap)
                    .addComponent(btnBoQuaNhap)
                    .addComponent(btnNhapExcel)
                    .addComponent(btnXuatExcelNhap))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Nhập hàng", jPanel3);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1007, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(15, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnTimKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKhoActionPerformed
        importsController.searchProduct();
    }//GEN-LAST:event_btnTimKhoActionPerformed

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
        loadCategoryNhap();
        loadSupplierNhap();
        loadEmployeeNhap();
        loadDuLieuNhap();
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
                loadDuLieuNhap();
            } else if (status == "sua") {
                importsController.updateImport();
                enty();
                loadDuLieuNhap();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnLuuNhapActionPerformed

    private void tblViewNhapHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblViewNhapHangMouseClicked
        try {
            int i = tblViewNhapHang.getSelectedRow();
            DefaultTableModel tb = (DefaultTableModel) tblViewNhapHang.getModel();
            txtTenSPNhap.setText(tb.getValueAt(i, 1).toString());
            txtSoLuongNhap.setText(tb.getValueAt(i, 2).toString());
            txtGiaNhap.setText(tb.getValueAt(i, 3).toString());
            txtDonViNhap.setText(tb.getValueAt(i, 4).toString());

            String ngayNhapStr = tb.getValueAt(i, 5).toString();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate localDate = LocalDate.parse(ngayNhapStr, formatter);
            Date date = java.sql.Date.valueOf(localDate); // chuyển sang java.util.Date
            jDateNgayNhap.setDate(date);

            String tenNCC = tb.getValueAt(i, 6).toString();
            supplierMap.forEach((supplier_name, supplier_id)
                    -> {
                if (supplier_name.equals(tenNCC)) {
                    cboNCCNhap.setSelectedItem(supplier_name);
                }
            });

            String tenLoaiHangNhap = tb.getValueAt(i, 7).toString();
            categoryMap.forEach((category_name, category_id)
                    -> {
                if (category_name.equals(tenLoaiHangNhap)) {
                    cboLoaiHangNhap.setSelectedItem(category_name);
                }
            });

            String tenNhanVienNhap = tb.getValueAt(i, 8).toString();
            employeeMap.forEach((full_name, employee_id)
                    -> {
                if (full_name.equals(tenNhanVienNhap)) {
                    cboNhanVienNhap.setSelectedItem(full_name);
                }
            });
        } catch (Exception e) {
            e.getStackTrace();
        }
    }//GEN-LAST:event_tblViewNhapHangMouseClicked

    private void btnXoaNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaNhapActionPerformed
        importsController.deleteImport();
        loadDuLieuNhap();
        enty();
    }//GEN-LAST:event_btnXoaNhapActionPerformed

    private void txtTenSPTimKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenSPTimKhoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenSPTimKhoActionPerformed

    private void jPanel2ComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_jPanel2ComponentShown
        loadDuLieuKho();
        loadCategoryKho();
        loadSupplierKho();
    }//GEN-LAST:event_jPanel2ComponentShown

    private void txtTenSPKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenSPKhoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenSPKhoActionPerformed

    private void txtGiaKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGiaKhoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGiaKhoActionPerformed

    private void txtTonKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTonKhoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTonKhoActionPerformed

    private void txtDonViKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDonViKhoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDonViKhoActionPerformed

    private void tblViewKhoHangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblViewKhoHangMouseClicked
        try {
            int i = tblViewKhoHang.getSelectedRow();
            DefaultTableModel tb = (DefaultTableModel) tblViewKhoHang.getModel();
            txtTenSPKho.setText(tb.getValueAt(i, 1).toString());
            String tenLoaiHangKho = tb.getValueAt(i, 2).toString();
            categoryMap.forEach((category_name, category_id)
                    -> {
                if (category_name.equals(tenLoaiHangKho)) {
                    cboLoaiHangKho.setSelectedItem(category_name);
                }
            });
            txtGiaKho.setText(tb.getValueAt(i, 3).toString());
            txtTonKho.setText(tb.getValueAt(i, 4).toString());
            txtDonViKho.setText(tb.getValueAt(i, 5).toString());
        } catch (Exception e) {
            e.getStackTrace();
        }
    }//GEN-LAST:event_tblViewKhoHangMouseClicked

    private void btnBoQuaKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoQuaKhoActionPerformed
        setMacDinhKho();
        entyKho();
    }//GEN-LAST:event_btnBoQuaKhoActionPerformed

    private void btnSuaKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaKhoActionPerformed
        status = "suaKho";
        setSuaKho();
    }//GEN-LAST:event_btnSuaKhoActionPerformed

    private void btnXoaKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaKhoActionPerformed
        importsController.deleteProduct();
        loadDuLieuKho();
        entyKho();
    }//GEN-LAST:event_btnXoaKhoActionPerformed

    private void btnLuuKhoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuKhoActionPerformed
        try {
            if (status == "suaKho") {
                importsController.updateProduct();
                loadDuLieuKho();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_btnLuuKhoActionPerformed

    private void btnTimNhapActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimNhapActionPerformed
        importsController.searchImport();
    }//GEN-LAST:event_btnTimNhapActionPerformed

    private void btnNhapExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhapExcelActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Chọn file Excel để nhập");

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();

            // Kiểm tra phần mở rộng
            if (!selectedFile.getName().toLowerCase().endsWith(".xlsx")) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn file Excel định dạng .xlsx", "Định dạng không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Gọi controller xử lý nhập Excel
            try {
                importsController.importExcelToDatabase(selectedFile);
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Lỗi khi nhập Excel: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnNhapExcelActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBoQuaKho;
    private javax.swing.JButton btnBoQuaNhap;
    private javax.swing.JButton btnLuuKho;
    private javax.swing.JButton btnLuuNhap;
    private javax.swing.JButton btnNhapExcel;
    private javax.swing.JButton btnSuaKho;
    private javax.swing.JButton btnSuaNhap;
    private javax.swing.JButton btnThemNhap;
    private javax.swing.JButton btnTimKho;
    private javax.swing.JButton btnTimNhap;
    private javax.swing.JButton btnXoaKho;
    private javax.swing.JButton btnXoaNhap;
    private javax.swing.JButton btnXuatExcel;
    private javax.swing.JButton btnXuatExcelNhap;
    private javax.swing.JComboBox<String> cboLoaiHangKho;
    private javax.swing.JComboBox<String> cboLoaiHangKhoTim;
    private javax.swing.JComboBox<String> cboLoaiHangNhap;
    private javax.swing.JComboBox<String> cboLoaiHangNhapTim;
    private javax.swing.JComboBox<String> cboNCCKhoTim;
    private javax.swing.JComboBox<String> cboNCCNhap;
    private javax.swing.JComboBox<String> cboNCCNhapTim;
    private javax.swing.JComboBox<String> cboNhanVienNhap;
    private com.toedter.calendar.JDateChooser jDateNgayNhap;
    private com.toedter.calendar.JDateChooser jDateTimDen;
    private com.toedter.calendar.JDateChooser jDateTimTu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
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
    private javax.swing.JTable tblViewKhoHang;
    private javax.swing.JTable tblViewNhapHang;
    private javax.swing.JTextField txtDonViKho;
    private javax.swing.JTextField txtDonViNhap;
    private javax.swing.JTextField txtGiaKho;
    private javax.swing.JTextField txtGiaNhap;
    private javax.swing.JTextField txtSoLuongNhap;
    private javax.swing.JTextField txtTenSPKho;
    private javax.swing.JTextField txtTenSPNhap;
    private javax.swing.JTextField txtTenSPTimKho;
    private javax.swing.JTextField txtTonKho;
    // End of variables declaration//GEN-END:variables

}
