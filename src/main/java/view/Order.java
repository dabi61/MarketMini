/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;


import dao.CustomerDAO;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComboBox;
import dao.EmployeeDAO;
import dao.OrderDAO;
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
public class Order extends javax.swing.JPanel {
    private boolean isEditing = false;
private int editingOrderId = -1; // lưu id đơn hàng đang sửa (nếu cần)

    private JComboBox<String> cboEmployeeName;
private Map<String, Integer> employeeNameIdMap = new HashMap<>();
    /**
     * Creates new form Form_2
     */
    public Order() {
        initComponents();
        loadEmployeeNames();
        loadTableOrders();
        disableFields();
        btnXoa.setEnabled(false);
        btnBoQua.setEnabled(false);
        btnLuu.setEnabled(false);
        btnSua.setEnabled(false);
    }
    
    private void disableFields() {
    cboIDNV.setEnabled(false);
    dtpNgayTao.setEnabled(false);
    txtTongGia.setEditable(false);
    txtIDKH.setEditable(false);
}
    
    private void clearFields() {
    cboIDNV.setSelectedIndex(0);
    dtpNgayTao.setDate(new Date());
    txtTongGia.setText("");
    txtIDKH.setText("");
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
    cboIDNV.setEnabled(true);
    dtpNgayTao.setEnabled(true);
    txtTongGia.setEditable(true);
    txtIDKH.setEditable(true);
}

    public void exportOrdersToExcel() {
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
            Sheet sheet = workbook.createSheet("Danh sách đơn hàng");

            TableModel model = jTable1.getModel();

            // Header
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            for (int col = 0; col < model.getColumnCount() - 1; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(model.getColumnName(col));
            }

            // Dữ liệu
            for (int row = 0; row < model.getRowCount(); row++) {
                org.apache.poi.ss.usermodel.Row excelRow = sheet.createRow(row + 1);
                for (int col = 0; col < model.getColumnCount() - 1; col++) {
                    Cell cell = excelRow.createCell(col);
                    Object value = model.getValueAt(row, col);
                    cell.setCellValue(value != null ? value.toString() : "");
                }
            }

            // Ghi file và tự động mở
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            // Tự động mở nếu được hệ điều hành hỗ trợ
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


    
private void loadEmployeeNames() {
    try {
        EmployeeDAO employeeDAO = new EmployeeDAO();
        List<Employees> employeesList = employeeDAO.getAllEmployees();
        cboIDNV.removeAllItems();
        employeeNameIdMap.clear();
        for (Employees emp : employeesList) {
            cboIDNV.addItem(emp.getFull_name());
            employeeNameIdMap.put(emp.getFull_name(), emp.getEmployee_id());
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tải danh sách nhân viên!");
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
        txtTongGia = new javax.swing.JTextField();
        txtIDKH = new javax.swing.JTextField();
        btnBoQua = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        txtTK1 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        cboIDNV = new javax.swing.JComboBox<>();
        dtpNgayTao = new com.toedter.calendar.JDateChooser();
        btnLuu = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnThem = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnCapNhat = new javax.swing.JButton();
        btnXuat = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 255));
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID", "Tên nhân viên", "Ngày tạo đơn", "Tổng tiền", "Số điện thoại"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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

        txtTongGia.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTongGiaActionPerformed(evt);
            }
        });
        jPanel1.add(txtTongGia, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 140, 179, -1));

        txtIDKH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIDKHActionPerformed(evt);
            }
        });
        jPanel1.add(txtIDKH, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 180, 179, -1));

        btnBoQua.setText("Bỏ qua");
        btnBoQua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBoQuaActionPerformed(evt);
            }
        });
        jPanel1.add(btnBoQua, new org.netbeans.lib.awtextra.AbsoluteConstraints(710, 230, -1, -1));

        btnXoa.setText("Xóa");
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });
        jPanel1.add(btnXoa, new org.netbeans.lib.awtextra.AbsoluteConstraints(580, 230, -1, -1));

        txtTK1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTK1ActionPerformed(evt);
            }
        });
        jPanel1.add(txtTK1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 80, 140, -1));

        jLabel6.setText("Tìm kiếm");
        jPanel1.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(570, 80, -1, -1));

        jLabel4.setText("Tổng giá tiền:");
        jPanel1.add(jLabel4, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 140, 112, 19));

        jLabel5.setText("Số điện thoại:");
        jPanel1.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 180, -1, -1));

        jLabel2.setFont(new java.awt.Font("Arial", 1, 36)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(51, 153, 0));
        jLabel2.setText("Quản lý đơn hàng");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(340, 20, -1, -1));

        cboIDNV.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jPanel1.add(cboIDNV, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 140, 172, -1));
        jPanel1.add(dtpNgayTao, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 180, 172, -1));

        btnLuu.setText("Lưu");
        btnLuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuActionPerformed(evt);
            }
        });
        jPanel1.add(btnLuu, new org.netbeans.lib.awtextra.AbsoluteConstraints(460, 230, -1, -1));

        btnSua.setText("Sửa");
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });
        jPanel1.add(btnSua, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 230, -1, -1));

        btnThem.setText("Thêm");
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });
        jPanel1.add(btnThem, new org.netbeans.lib.awtextra.AbsoluteConstraints(190, 230, -1, -1));

        jLabel1.setText("Tên nhân viên:");
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(66, 139, -1, -1));

        jLabel3.setText("Ngày tạo đơn: ");
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(66, 182, -1, -1));

        btnCapNhat.setText("Cập nhật");
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });
        jPanel1.add(btnCapNhat, new org.netbeans.lib.awtextra.AbsoluteConstraints(810, 230, -1, -1));

        btnXuat.setText("Xuất Excel");
        btnXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatActionPerformed(evt);
            }
        });
        jPanel1.add(btnXuat, new org.netbeans.lib.awtextra.AbsoluteConstraints(740, 30, -1, -1));

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
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtIDKHActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIDKHActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIDKHActionPerformed
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
    if (selectedRow != -1) {
        String employeeName = jTable1.getValueAt(selectedRow, 1).toString();
        Object dateObj = jTable1.getValueAt(selectedRow, 2);
        java.util.Date orderDate = null;
        if (dateObj instanceof java.sql.Date) {
            orderDate = new java.util.Date(((java.sql.Date) dateObj).getTime());
        } else if (dateObj instanceof java.util.Date) {
            orderDate = (java.util.Date) dateObj;
        }
        String totalAmount = jTable1.getValueAt(selectedRow, 3).toString();
        String customerName = jTable1.getValueAt(selectedRow, 4).toString();

        // Set tên nhân viên lên combo box
        cboIDNV.setSelectedItem(employeeName);

        // Set ngày lên date picker
        dtpNgayTao.setDate(orderDate);

        // Set tổng tiền
        txtTongGia.setText(totalAmount);

        // Set tên khách hàng
        txtIDKH.setText(customerName);
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

    private void txtTongGiaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTongGiaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTongGiaActionPerformed

    private void txtTK1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTK1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTK1ActionPerformed

    private void btnLuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuActionPerformed
        // TODO add your handling code here:
        if (cboIDNV.getSelectedItem() == null || txtIDKH.getText().trim().isEmpty() || txtTongGia.getText().trim().isEmpty()) {
    JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!", "Thông báo", JOptionPane.WARNING_MESSAGE);
    return;
}
        String input = txtIDKH.getText().trim();
if (!input.matches("\\d+")) {
    JOptionPane.showMessageDialog(this, "Số điện thoại chỉ được chứa chữ số!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
    return; // Dừng xử lý nếu không đúng định dạng
}

        try {
        String employeeName = cboIDNV.getSelectedItem().toString();
        EmployeeDAO empDAO = new EmployeeDAO();
        int employeeId = empDAO.getEmployeeIdByName(employeeName);

        String customerName = txtIDKH.getText();
        CustomerDAO cusDAO = new CustomerDAO();
        int customerId = cusDAO.getCustomerIdByPhone(customerName);

        Date orderDate = new java.sql.Date(dtpNgayTao.getDate().getTime());
        int totalAmount = Integer.parseInt(txtTongGia.getText());

        model.Order order = new model.Order();
        order.setEmployeeId(employeeId);
        order.setCustomerId(customerId);
        order.setOrderDate(orderDate);
        order.setTotalAmount(totalAmount);

        OrderDAO orderDAO = new OrderDAO();
        boolean success;

        if (isEditing) {
            // set id cho order để update
            order.setOrderId(editingOrderId);
            success = orderDAO.update(order);
            if (success) {
                JOptionPane.showMessageDialog(this, "Cập nhật đơn hàng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật đơn hàng thất bại!");
            }
        } else {
            success = orderDAO.insert(order);
            if (success) {
                JOptionPane.showMessageDialog(this, "Thêm đơn hàng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Thêm đơn hàng thất bại!");
            }
        }

        if (success) {
            loadTableOrders();
            disableFields();
            clearFields();
            enableButtons();
            isEditing = false;
            editingOrderId = -1;
        }

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
    }
    }//GEN-LAST:event_btnLuuActionPerformed

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        // TODO add your handling code here:
        String phone = txtTK1.getText().trim();
    if (phone.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại để tìm kiếm!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    try {
        OrderDAO orderDAO = new OrderDAO();
        List<Object[]> result = orderDAO.getOrdersByCustomerPhone(phone);

        // Xóa dữ liệu cũ trong bảng và hiển thị kết quả mới
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Xóa bảng cũ

        for (Object[] row : result) {
            model.addRow(row);
        }

        if (result.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy đơn hàng nào với số điện thoại này!");
        }
    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
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
        int selectedRow = jTable1.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Bạn chưa chọn dòng để xóa!", "Thông báo", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int orderId = Integer.parseInt(jTable1.getValueAt(selectedRow, 0).toString());

    int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa đơn hàng này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
    if (confirm == JOptionPane.YES_OPTION) {
        try {
            OrderDAO orderDAO = new OrderDAO();
            
            // XÓA CHI TIẾT ĐƠN HÀNG TRƯỚC
            orderDAO.deleteOrderDetailsByOrderId(orderId);
            
            // RỒI XÓA ĐƠN HÀNG CHÍNH
            boolean success = orderDAO.delete(orderId);
            if (success) {
                JOptionPane.showMessageDialog(this, "Xóa đơn hàng thành công!");
                loadTableOrders(); // Cập nhật lại bảng
                clearFields();     // Xóa dữ liệu trên form

                btnThem.setEnabled(true);
                btnSua.setEnabled(false);
                btnXoa.setEnabled(false);
                btnLuu.setEnabled(false);
                btnBoQua.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this, "Xóa đơn hàng thất bại!");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Lỗi kết nối cơ sở dữ liệu: " + ex.getMessage());
        }
    }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnBoQuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBoQuaActionPerformed
        // TODO add your handling code here:
        clearFields();
        loadEmployeeNames();
        loadTableOrders();
        disableFields();
        btnXoa.setEnabled(false);
        btnBoQua.setEnabled(false);
        btnLuu.setEnabled(false);
        btnSua.setEnabled(false);
    }//GEN-LAST:event_btnBoQuaActionPerformed

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatActionPerformed
        // TODO add your handling code here:
        loadTableOrders();
    }//GEN-LAST:event_btnCapNhatActionPerformed

    private void btnXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatActionPerformed
        // TODO add your handling code here:
        exportOrdersToExcel();
    }//GEN-LAST:event_btnXuatActionPerformed
    private void loadTableOrders() {
   try {
        DefaultTableModel model = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Chỉ cho phép cột "Chi tiết" được click
            }
        };

        // Thiết lập tên cột chính xác
        model.setColumnIdentifiers(new String[] {
            "order_id", "Tên nhân viên", "Ngày tạo đơn", "Tổng tiền", "Số điện thoại khách hàng", "Chức năng"
        });

        // Lấy danh sách đơn hàng từ database
        OrderDAO dao = new OrderDAO();
        List<Object[]> list = dao.getAllOrdersWithNames();

        // Duyệt từng dòng dữ liệu và thêm vào bảng
        for (Object[] row : list) {
            Object[] newRow = Arrays.copyOf(row, row.length + 1);
            newRow[newRow.length - 1] = "Chi tiết"; // Thêm nút "Chi tiết"
            model.addRow(newRow);
        }

        // Gán model vào jTable1
        jTable1.setModel(model);

        // Ẩn cột order_id nhưng vẫn có thể lấy dữ liệu
        jTable1.getColumnModel().getColumn(0).setMinWidth(0);
        jTable1.getColumnModel().getColumn(0).setMaxWidth(0);
        jTable1.getColumnModel().getColumn(0).setWidth(0);
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(0);
        jTable1.getColumnModel().getColumn(0).setResizable(false); // Đảm bảo vẫn lấy giá trị

        // Thêm renderer và editor cho cột "Chi tiết"
        jTable1.getColumn("Chức năng").setCellRenderer(new ButtonRenderer());
        jTable1.getColumn("Chức năng").setCellEditor(new ButtonEditor(new JCheckBox(), jTable1));

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Lỗi khi tải dữ liệu đơn hàng!");
    }

}




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBoQua;
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnLuu;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXuat;
    private javax.swing.JComboBox<String> cboIDNV;
    private com.toedter.calendar.JDateChooser dtpNgayTao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtIDKH;
    private javax.swing.JTextField txtTK1;
    private javax.swing.JTextField txtTongGia;
    // End of variables declaration//GEN-END:variables
}
