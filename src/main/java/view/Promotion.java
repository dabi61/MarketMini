/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import controller.PromotionController;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import dao.CustomerDAO;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import dao.EmployeeDAO;
import dao.ProductDAO;
import dao.PromotionDAO;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import model.Employees;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.DBConnection;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.events.DocumentEvent;



/**
 *
 * @author RAVEN
 */
public class Promotion extends javax.swing.JPanel {
    private boolean isEditing = false;
private int editingOrderId = -1; // lưu id đơn hàng đang sửa (nếu cần)
private int editingPromotionId = -1;

    private JComboBox<String> cboEmployeeName;
private Map<String, Integer> employeeNameIdMap = new HashMap<>();
    /**
     * Creates new form Form_2
     */
    public Promotion() {
        initComponents();
        loadTablePromotions();
        disableFields();
        btnXoa.setEnabled(false);
        btnBoQua.setEnabled(false);
        btnLuu.setEnabled(false);
        btnSua.setEnabled(false);
        loadDanhSachSanPham();
    }

private int selectedProductId = -1;
private void loadDanhSachSanPham() {
    DefaultTableModel model = new DefaultTableModel(new Object[]{"ID", "Tên sản phẩm"}, 0);
    try (Connection conn = DBConnection.getConnection()) {
        String sql = "SELECT product_id, product_name FROM products";
        ResultSet rs = conn.createStatement().executeQuery(sql);
        while (rs.next()) {
            model.addRow(new Object[]{rs.getInt(1), rs.getString(2)});
        }
        tblTenSP.setModel(model);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

    private boolean validateForm() {
    // Kiểm tra tên khuyến mãi
    if (txtTenKM.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khuyến mãi!");
        txtTenKM.requestFocus();
        return false;
    }

    // Kiểm tra ngày tạo và ngày hết hạn
    Date ngayTao = dtpNgayTao.getDate();
    Date ngayHH = dtpNgayHH.getDate();

    if (ngayTao == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày tạo!");
        dtpNgayTao.requestFocus();
        return false;
    }

    if (ngayHH == null) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn ngày hết hạn!");
        dtpNgayHH.requestFocus();
        return false;
    }

    if (ngayTao.after(ngayHH)) {
        JOptionPane.showMessageDialog(this, "Ngày tạo không được lớn hơn ngày hết hạn!");
        dtpNgayTao.requestFocus();
        return false;
    }

    return true; // tất cả OK
}
    private void disableFields() {
    txtTenKM.setEnabled(false);
    dtpNgayTao.setEnabled(false);
    dtpNgayHH.setEnabled(false);
    txtGiamGia.setEditable(false);
    txtTenSP.setEditable(false);
}
    
    private void clearFields() {
    txtTenKM.setText("");
    dtpNgayTao.setDate(new Date());
    dtpNgayHH.setDate(new Date());
    txtGiamGia.setText("");
    txtTenSP.setText("");
}

private void enableButtons() {
    btnThem.setEnabled(true);
    btnSua.setEnabled(true);
    btnXoa.setEnabled(true);
    btnLuu.setEnabled(true);
    btnBoQua.setEnabled(true);
    btnTimKiem.setEnabled(true);
}

    
    private void enableFields() {
    txtTenKM.setEnabled(true);
    dtpNgayTao.setEnabled(true);
    dtpNgayHH.setEnabled(true);
    txtGiamGia.setEditable(true);
    txtTenSP.setEditable(true);
}

    public void exportPromotionsToExcel() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Chọn nơi lưu file Excel");

    int userSelection = fileChooser.showSaveDialog(this);
    if (userSelection != JFileChooser.APPROVE_OPTION) return;

    File fileToSave = fileChooser.getSelectedFile();
    String filePath = fileToSave.getAbsolutePath();
    if (!filePath.endsWith(".xlsx")) {
        filePath += ".xlsx";
    }

    try {
        PromotionController controller = new PromotionController();
        controller.exportToExcel(jTable1.getModel(), new File(filePath));

        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(filePath));
        }

        JOptionPane.showMessageDialog(this, "Xuất file Excel thành công:\n" + filePath);
    } catch (IOException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi xuất Excel: " + e.getMessage());
    }   catch (SQLException ex) {
            Logger.getLogger(Promotion.class.getName()).log(Level.SEVERE, null, ex);
        }
}

    


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        txtGiamGia = new javax.swing.JTextField();
        btnBoQua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        dtpNgayTao = new com.toedter.calendar.JDateChooser();
        btnLuu = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnThem = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        btnXuat = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        dtpNgayHH = new com.toedter.calendar.JDateChooser();
        jLabel8 = new javax.swing.JLabel();
        txtTenKM = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtTenSP = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblTenSP = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        txtTK1 = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Tên mã khuyến mãi", "Ngày tạo mã", "Ngày hết hạn", "Tên sản phẩm", "Giảm giá", "Giá gốc", "Giá sau khi giảm"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable1.setToolTipText("");
        jTable1.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jTable1.setGridColor(new java.awt.Color(0, 0, 0));
        jTable1.setInheritsPopupMenu(true);
        jTable1.setRowSelectionAllowed(false);
        jTable1.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        txtGiamGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGiamGiaActionPerformed(evt);
            }
        });

        btnBoQua.setBackground(new java.awt.Color(0, 204, 204));
        btnBoQua.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnBoQua.setForeground(new java.awt.Color(255, 255, 255));
        btnBoQua.setText("Bỏ qua");
        btnBoQua.setBorderPainted(false);
        btnBoQua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoQuaActionPerformed(evt);
            }
        });

        btnXoa.setBackground(new java.awt.Color(204, 51, 0));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(255, 255, 255));
        btnXoa.setText("Xóa");
        btnXoa.setBorderPainted(false);
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        jLabel4.setText("% Giảm giá:");

        btnLuu.setBackground(new java.awt.Color(0, 102, 204));
        btnLuu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuu.setForeground(new java.awt.Color(255, 255, 255));
        btnLuu.setText("Lưu");
        btnLuu.setBorderPainted(false);
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });

        btnSua.setBackground(new java.awt.Color(255, 153, 0));
        btnSua.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSua.setForeground(new java.awt.Color(255, 255, 255));
        btnSua.setText("Sửa");
        btnSua.setBorderPainted(false);
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnThem.setBackground(new java.awt.Color(0, 102, 0));
        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThem.setForeground(new java.awt.Color(255, 255, 255));
        btnThem.setText("Thêm");
        btnThem.setBorderPainted(false);
        btnThem.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        jLabel3.setText("Ngày bắt đầu: ");

        btnXuat.setBackground(new java.awt.Color(51, 153, 0));
        btnXuat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXuat.setForeground(new java.awt.Color(255, 255, 255));
        btnXuat.setText("Xuất Excel");
        btnXuat.setBorderPainted(false);
        btnXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatActionPerformed(evt);
            }
        });

        jLabel7.setText("Ngày hết hạn:");

        jLabel8.setText("Tên khuyến mãi:");

        txtTenKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenKMActionPerformed(evt);
            }
        });

        jLabel1.setText("Tên sản phẩm:");

        txtTenSP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenSPActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(0, 102, 0));

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Quản lý Khuyến mãi");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(404, 404, 404))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(32, Short.MAX_VALUE))
        );

        tblTenSP.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Title 1", "Title 2"
            }
        ));
        tblTenSP.setGridColor(new java.awt.Color(0, 0, 0));
        tblTenSP.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTenSPMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tblTenSP);

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm kiếm tên khuyến mãi"));

        txtTK1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTK1ActionPerformed(evt);
            }
        });

        btnTimKiem.setBackground(new java.awt.Color(0, 102, 204));
        btnTimKiem.setForeground(new java.awt.Color(255, 255, 255));
        btnTimKiem.setText("Tìm kiếm");
        btnTimKiem.setBorderPainted(false);
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(txtTK1, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(txtTK1)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(135, 135, 135)
                .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnBoQua, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(btnXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(68, 68, 68)
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(txtGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel7)
                                        .addGap(30, 30, 30))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTenKM, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dtpNgayTao, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dtpNgayHH, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(txtTenSP, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(txtTenKM, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(jLabel3)
                                        .addGap(32, 32, 32)
                                        .addComponent(jLabel7)
                                        .addGap(25, 25, 25))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                        .addGap(18, 18, 18)
                                        .addComponent(dtpNgayTao, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(dtpNgayHH, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtGiamGia, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(txtTenSP, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLuu, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBoQua, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    private int getOrderIdFromTable(int row) {
    // Lấy giá trị cột 0 (order_id) ở dòng row, ép kiểu về int
    return (int) jTable1.getModel().getValueAt(row, 0);
}


    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:int selectedRow = jTable1.getSelectedRow();
        btnThem.setEnabled(false);
        btnSua.setEnabled(true);
        btnXoa.setEnabled(true);
        btnLuu.setEnabled(false);
        btnBoQua.setEnabled(true);
        int selectedRow = jTable1.getSelectedRow();
    if (selectedRow >= 0) {
        // Lấy dữ liệu từ bảng
        int promotionId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());
        editingPromotionId = promotionId; 
        String promotionName = jTable1.getValueAt(selectedRow, 1).toString();
        String startDateStr = jTable1.getValueAt(selectedRow, 2).toString();
        String endDateStr = jTable1.getValueAt(selectedRow, 3).toString();
        String productName = jTable1.getValueAt(selectedRow, 4).toString();
        String discountStr = jTable1.getValueAt(selectedRow, 5).toString();
        String discountedPriceStr = jTable1.getValueAt(selectedRow, 6).toString();
        String originalPriceStr = jTable1.getValueAt(selectedRow, 7).toString();

        // Hiển thị lên form
        txtTenKM.setText(promotionName);
        dtpNgayTao.setDate(java.sql.Date.valueOf(startDateStr));
        dtpNgayHH.setDate(java.sql.Date.valueOf(endDateStr));
        txtTenSP.setText(productName);        // Bạn cần có thêm txtDiscount
        txtGiamGia.setText(discountStr);         // Bạn cần có thêm txtOriginal

        // Bật chế độ chỉnh sửa
        isEditing = true;

        enableFields();
    }
    btnXoa.setEnabled(true);
    }//GEN-LAST:event_jTable1MouseClicked

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
        enableFields();
        clearFields(); 
        txtTenSP.setEnabled(false);
        btnThem.setEnabled(false);
        btnXoa.setEnabled(false);
        btnSua.setEnabled(false);
        btnLuu.setEnabled(true);
        btnBoQua.setEnabled(true);
        isEditing = false; // đang thêm mới
    editingOrderId = -1;
    }//GEN-LAST:event_btnThemActionPerformed

    private void txtGiamGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtGiamGiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtGiamGiaActionPerformed

    private void txtTK1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTK1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTK1ActionPerformed

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        // TODO add your handling code here:
        if (!validateForm()) return;
         if (txtTenKM.getText().trim().isEmpty() ||
        txtTenSP.getText().trim().isEmpty() ||
        txtGiamGia.getText().trim().isEmpty()) {

        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    String discountStr = txtGiamGia.getText().trim();
    if (!discountStr.matches("\\d+")) {
        JOptionPane.showMessageDialog(this, "Phần trăm giảm giá phải là số nguyên!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        String promotionName = txtTenKM.getText().trim();
        Date startDate = new java.sql.Date(dtpNgayTao.getDate().getTime());
        Date endDate = new java.sql.Date(dtpNgayHH.getDate().getTime());
        String productName = txtTenSP.getText().trim();
        int discount = Integer.parseInt(discountStr);

        PromotionController controller = new PromotionController();
        if (controller.isPromotionNameDuplicate(promotionName) && !isEditing) {
    JOptionPane.showMessageDialog(this, "Tên khuyến mãi đã tồn tại. Vui lòng nhập tên khác!", "Trùng tên", JOptionPane.WARNING_MESSAGE);
    return;
}
        boolean success = controller.savePromotion(
            isEditing,
            editingPromotionId,
            promotionName,
            startDate,
            endDate,
            productName,
            discount
        );

        JOptionPane.showMessageDialog(this,
            success
                ? (isEditing ? "Cập nhật khuyến mãi thành công!" : "Thêm khuyến mãi thành công!")
                : (isEditing ? "Cập nhật khuyến mãi thất bại!" : "Thêm khuyến mãi thất bại!")
        );

        if (success) {
            loadTablePromotions();
            disableFields();
            clearFields();
            btnThem.setEnabled(true);
            btnSua.setEnabled(false);
            btnLuu.setEnabled(false);
            btnXoa.setEnabled(false);
            btnBoQua.setEnabled(false);
            isEditing = false;
            editingPromotionId = -1;
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
    }
    }//GEN-LAST:event_btnLuuActionPerformed

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
        String keyword = txtTK1.getText().trim();

    try {
        PromotionController controller = new PromotionController();
        DefaultTableModel model = controller.searchPromotions(keyword);

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi phù hợp!");
        }

        jTable1.setModel(model);

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tìm kiếm: " + e.getMessage());
    }
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        // TODO add your handling code here:
        btnThem.setEnabled(false);
        btnSua.setEnabled(false);
        btnXoa.setEnabled(false);
        btnBoQua.setEnabled(true);
        btnLuu.setEnabled(true);
        enableFields();
        isEditing = true; // đang sửa
    // Lưu lại ID đơn hàng đang sửa nếu có (ví dụ lấy từ dòng chọn)
    int selectedRow = jTable1.getSelectedRow();
    if (selectedRow != -1) {
        // Giả sử bạn có hàm lấy ID đơn hàng từ bảng
        editingOrderId = getOrderIdFromTable(selectedRow); 
    }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
        if (!isEditing || editingPromotionId == -1) {
        JOptionPane.showMessageDialog(this, "Vui lòng chọn một khuyến mãi để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa khuyến mãi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) {
        return;
    }

    try {
        PromotionController controller = new PromotionController();
        boolean success = controller.deletePromotion(editingPromotionId);

        if (success) {
            JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thành công!");
            loadTablePromotions();
            clearFields();
            disableFields();
            enableButtons();
            isEditing = false;
            editingPromotionId = -1;
        } else {
            JOptionPane.showMessageDialog(this, "Xóa khuyến mãi thất bại!");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
    }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnBoQuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoQuaActionPerformed
        // TODO add your handling code here:
        clearFields();
        loadTablePromotions();
        disableFields();
        btnThem.setEnabled(true);
        btnXoa.setEnabled(false);
        btnSua.setEnabled(false);
        btnLuu.setEnabled(false);
        btnBoQua.setEnabled(false);
    }//GEN-LAST:event_btnBoQuaActionPerformed

    private void btnXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatActionPerformed
        // TODO add your handling code here:
        exportPromotionsToExcel();
    }//GEN-LAST:event_btnXuatActionPerformed

    private void txtTenKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenKMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenKMActionPerformed

    private void txtTenSPActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenSPActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenSPActionPerformed

    private void tblTenSPMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTenSPMouseClicked
        // TODO add your handling code here:
        int row = tblTenSP.getSelectedRow();
    if (row >= 0) {
        String tenSP = tblTenSP.getValueAt(row, 1).toString();
        txtTenSP.setText(tenSP);

        selectedProductId = Integer.parseInt(tblTenSP.getValueAt(row, 0).toString());
        System.out.println("Đã chọn: " + tenSP + " | ID: " + selectedProductId);
    }
    }//GEN-LAST:event_tblTenSPMouseClicked
    private void loadTablePromotions() {
    try {
        PromotionController controller = new PromotionController();
        DefaultTableModel model = controller.getPromotionTableModel();
        jTable1.setModel(model);
    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách khuyến mãi!");
    }
}







    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBoQua;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXuat;
    private com.toedter.calendar.JDateChooser dtpNgayHH;
    private com.toedter.calendar.JDateChooser dtpNgayTao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable tblTenSP;
    private javax.swing.JTextField txtGiamGia;
    private javax.swing.JTextField txtTK1;
    private javax.swing.JTextField txtTenKM;
    private javax.swing.JTextField txtTenSP;
    // End of variables declaration//GEN-END:variables
}
