/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

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
import dao.OrderDAO;
import dao.ProductDAO;
import dao.PromotionDAO;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import model.Employees;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import model.ButtonEditor;
import model.ButtonRenderer;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File fileToSave = fileChooser.getSelectedFile();
        String filePath = fileToSave.getAbsolutePath();
        if (!filePath.endsWith(".xlsx")) {
            filePath += ".xlsx";
        }

        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Danh sách khuyến mãi");
            TableModel model = jTable1.getModel();

            // Tạo kiểu cho header
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);

            // Ghi header
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount(); col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
                cell.setCellStyle(headerStyle);
            }

            // Ghi dữ liệu
            for (int row = 0; row < model.getRowCount(); row++) {
                Row excelRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Cell cell = excelRow.createCell(col);
                    Object value = model.getValueAt(row, col);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int col = 0; col < model.getColumnCount(); col++) {
                sheet.autoSizeColumn(col);
            }

            // Ghi ra file
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            // Mở file nếu có thể
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(filePath));
            }

            JOptionPane.showMessageDialog(this, "Xuất file Excel thành công:\n" + filePath);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Lỗi khi xuất Excel: " + e.getMessage());
        }
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
        btnTimKiem = new javax.swing.JButton();
        txtGiamGia = new javax.swing.JTextField();
        btnBoQua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        txtTK1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
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
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btnTimKiem.setText("Tìm kiếm");
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
            }
        });
        jPanel1.add(btnTimKiem, new org.netbeans.lib.awtextra.AbsoluteConstraints(780, 80, 100, -1));

        txtGiamGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtGiamGiaActionPerformed(evt);
            }
        });
        jPanel1.add(txtGiamGia, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 160, 179, -1));

        btnBoQua.setText("Bỏ qua");
        btnBoQua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoQuaActionPerformed(evt);
            }
        });
        jPanel1.add(btnBoQua, new org.netbeans.lib.awtextra.AbsoluteConstraints(670, 270, -1, -1));

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });
        jPanel1.add(btnXoa, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 270, -1, -1));

        txtTK1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTK1ActionPerformed(evt);
            }
        });
        jPanel1.add(txtTK1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 80, 140, -1));

        jLabel6.setText("Tìm kiếm");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 80, -1, -1));

        jLabel4.setText("% Giảm giá");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 160, 112, 19));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 153, 0));
        jLabel2.setText("Quản lý Khuyến mãi");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, -1, -1));
        jPanel1.add(dtpNgayTao, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 160, 172, -1));

        btnLuu.setText("Lưu");
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });
        jPanel1.add(btnLuu, new org.netbeans.lib.awtextra.AbsoluteConstraints(430, 270, -1, -1));

        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });
        jPanel1.add(btnSua, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 270, -1, -1));

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });
        jPanel1.add(btnThem, new org.netbeans.lib.awtextra.AbsoluteConstraints(130, 270, -1, -1));

        jLabel3.setText("Ngày tạo đơn: ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 160, -1, -1));

        btnXuat.setText("Xuất Excel");
        btnXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatActionPerformed(evt);
            }
        });
        jPanel1.add(btnXuat, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 30, -1, -1));

        jLabel7.setText("Ngày hết hạn:");
        jPanel1.add(jLabel7, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 210, -1, -1));
        jPanel1.add(dtpNgayHH, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 200, 170, -1));

        jLabel8.setText("Tên mã khuyến mãi");
        jPanel1.add(jLabel8, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 120, -1, -1));

        txtTenKM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTenKMActionPerformed(evt);
            }
        });
        jPanel1.add(txtTenKM, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 120, 150, -1));

        jLabel1.setText("Tên sản phẩm:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 120, -1, -1));
        jPanel1.add(txtTenSP, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 120, 180, -1));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jScrollPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE)
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
         if (txtTenKM.getText().trim().isEmpty() ||
        txtTenSP.getText().trim().isEmpty() ||
        txtGiamGia.getText().trim().isEmpty()) {

        JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    // Kiểm tra discount là số nguyên
    String discountStr = txtGiamGia.getText().trim();
    if (!discountStr.matches("\\d+")) {
        JOptionPane.showMessageDialog(this, "Phần trăm giảm giá phải là số nguyên!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        String promotionName = txtTenKM.getText().trim();
        Date startDate = new java.sql.Date(dtpNgayTao.getDate().getTime());
        Date endDate = new java.sql.Date(dtpNgayHH.getDate().getTime());
        int discount = Integer.parseInt(discountStr);

        String productName = txtTenSP.getText().trim();
        ProductDAO productDAO = new ProductDAO();
        int productId = productDAO.getProductIdByName(productName);
        int originalPrice = productDAO.getPriceByProductId(productId);

        // Tính giá sau giảm
        int discountedPrice = originalPrice * (100 - discount) / 100;

        // Tạo đối tượng Promotion
        model.Promotion promotion = new model.Promotion();

        promotion.setPromotionName(promotionName);
        promotion.setStartDate(startDate);
        promotion.setEndDate(endDate);
        promotion.setProductId(productId);
        promotion.setDiscount(discount);
        promotion.setOriginalPrice(originalPrice);
        promotion.setDiscountedPrice(discountedPrice);

        PromotionDAO promotionDAO = new PromotionDAO();
        boolean success;

        if (isEditing) {
            promotion.setPromotionId(editingPromotionId);
            success = promotionDAO.update(promotion);
            JOptionPane.showMessageDialog(this,
                success ? "Cập nhật khuyến mãi thành công!" : "Cập nhật khuyến mãi thất bại!");
        } else {
            success = promotionDAO.insert(promotion);
            JOptionPane.showMessageDialog(this,
                success ? "Thêm khuyến mãi thành công!" : "Thêm khuyến mãi thất bại!");
        }

        if (success) {
            loadTablePromotions();
            disableFields();
            clearFields();
            enableButtons();
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
        PromotionDAO dao = new PromotionDAO();
        List<Object[]> results;

        if (keyword.isEmpty()) {
            // Nếu không nhập gì → hiển thị lại toàn bộ danh sách
            results = dao.getAllPromotionsWithProductNames();
        } else {
            // Tìm theo tên khuyến mãi
            results = dao.searchByPromotionName(keyword);

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy khuyến mãi phù hợp!");
            }
        }

        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        model.setColumnIdentifiers(new String[]{
            "promotion_id", "Tên khuyến mãi", "Ngày bắt đầu", "Ngày kết thúc",
            "Tên sản phẩm", "Giảm giá (%)", "Giá sau giảm", "Giá gốc"
        });

        for (Object[] row : results) {
            model.addRow(row);
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
        PromotionDAO dao = new PromotionDAO();
        boolean success = dao.delete(editingPromotionId);

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
        btnXoa.setEnabled(true);
        btnSua.setEnabled(true);
        btnLuu.setEnabled(true);
        btnBoQua.setEnabled(true);
    }//GEN-LAST:event_btnBoQuaActionPerformed

    private void btnXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatActionPerformed
        // TODO add your handling code here:
        exportPromotionsToExcel();
    }//GEN-LAST:event_btnXuatActionPerformed

    private void txtTenKMActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTenKMActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTenKMActionPerformed
    private void loadTablePromotions() {
    try {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Không cho sửa dữ liệu trong bảng
            }
        };

        model.setColumnIdentifiers(new String[] {
            "Promotion ID", "Tên khuyến mãi", "Ngày tạo mã", "Ngày hết hạn",
            "Tên sản phẩm", "Giảm (%)", "Giá gốc", "Giá sau giảm"
        });

        PromotionDAO dao = new PromotionDAO();
        List<Object[]> list = dao.getAllPromotionsWithProductNames();

        for (Object[] row : list) {
            model.addRow(row);
        }

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
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtGiamGia;
    private javax.swing.JTextField txtTK1;
    private javax.swing.JTextField txtTenKM;
    private javax.swing.JTextField txtTenSP;
    // End of variables declaration//GEN-END:variables
}
