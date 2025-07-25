/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.Supplier;

import view.NhapExcel;
import controller.SupplierController;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Admin
 */
public class SupplierForm extends javax.swing.JPanel {

    private ThemNCCForm themNCCForm;
    private SuaNCCForm suaNccForm;
    private NhapExcel nhapExcel;
    SupplierController spController;

    public ThemNCCForm getThemNCCForm() {
        return themNCCForm;
    }

    public SuaNCCForm getSuaNccForm() {
        return suaNccForm;
    }

    public NhapExcel getNhapExcel() {
        return nhapExcel;
    }

    public JTable getTblDanhSach() {
        return tblDanhSach;
    }

    public JTextPane getTxtTimKiem() {
        return txtTimKiem;
    }
    

    /**
     * Creates new form SupplierForm
     */
    public SupplierForm() {
        initComponents();
        // Chỉnh màu header bảng
        JTableHeader header = tblDanhSach.getTableHeader();
        header.setOpaque(false);
        header.setBackground(new java.awt.Color(46, 125, 50)); // Màu nền header
        header.setForeground(Color.WHITE);                   // Màu chữ header
        header.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 14));   // Font header

        txtTimKiem.setText("Tìm kiếm theo tên nhà cung cấp");
        txtTimKiem.setForeground(Color.GRAY);

        txtTimKiem.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (txtTimKiem.getText().equals("Tìm kiếm theo tên nhà cung cấp")) {
                    txtTimKiem.setText("");
                    txtTimKiem.setForeground(Color.BLACK);
                }
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (txtTimKiem.getText().trim().isEmpty()) {
                    txtTimKiem.setText("Tìm kiếm theo tên nhà cung cấp");
                    txtTimKiem.setForeground(Color.GRAY);
                }
            }
        });

        // Tùy chỉnh chọn dòng
        tblDanhSach.setSelectionBackground(new Color(0, 153, 0)); // Màu khi chọn dòng
        tblDanhSach.setSelectionForeground(Color.WHITE);          // Chữ trắng khi chọn
        tblDanhSach.setRowHeight(25);

        spController = new SupplierController(this);

        // Lấy Frame cha của JPanel hiện tại
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        // Khởi tạo các dialog với parent đúng kiểu
        themNCCForm = new ThemNCCForm(parent, true);
        suaNccForm = new SuaNCCForm(parent, true);
        nhapExcel = new NhapExcel(parent, true);

        //themNCCForm = new ThemNCCForm(this, true);
        btnThem.addActionListener(e -> spController.HienthiForm("Thêm"));
        btnSua.addActionListener(e -> spController.HienthiForm("Sửa"));
        btnXoa.addActionListener(e -> spController.HienthiForm("Xóa"));
        btnXuat.addActionListener(e -> spController.Xuatbaocao());
        btnNhap.addActionListener(e -> spController.HienthiForm("Nhập Excel"));
        btnTimKiem.addActionListener(e -> spController.timKiem());
        //  nhapExcel = new NhapExcel(this, true);
        nhapExcel.getBtnUpload().addActionListener(e -> spController.Upload());
        nhapExcel.getBtnSave().addActionListener(e -> spController.SaveDataFromExcel());
        themNCCForm.getBtnThemNcc().addActionListener(e -> spController.AddSupplier());
        themNCCForm.getBtnHuy().addActionListener(e -> spController.Huy());

        //   suaNccForm = new SuaNCCForm(this, true);
        suaNccForm.getBtnLuu().addActionListener(e -> spController.UpdateSupplier());
        suaNccForm.getBtnHuy().addActionListener(e -> spController.Huy());

    }
     public void autoResizeColumnWidth(JTable table) {
        final TableColumnModel columnModel = table.getColumnModel();
        for (int column = 0; column < table.getColumnCount(); column++) {
            int width = 15;
            TableColumn tableColumn = columnModel.getColumn(column);

            for (int row = 0; row < table.getRowCount(); row++) {
                TableCellRenderer renderer = table.getCellRenderer(row, column);
                Component comp = table.prepareRenderer(renderer, row, column);
                width = Math.max(comp.getPreferredSize().width + 1, width);
            }

            TableCellRenderer headerRenderer = table.getTableHeader().getDefaultRenderer();
            Component headerComp = headerRenderer.getTableCellRendererComponent(
                    table, tableColumn.getHeaderValue(), false, false, 0, column);
            width = Math.max(width, headerComp.getPreferredSize().width + 1);

            if (width > 500) {
                width = 500;
            }

            columnModel.getColumn(column).setPreferredWidth(width);
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

        jPanel1 = new javax.swing.JPanel();
        btnThem = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnNhap = new javax.swing.JButton();
        btnXuat = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtTimKiem = new javax.swing.JTextPane();
        btnTimKiem = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDanhSach = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));
        setPreferredSize(new java.awt.Dimension(885, 591));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chức năng\n", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(46, 125, 50))); // NOI18N

        btnThem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/add (1).png"))); // NOI18N
        btnThem.setText("Thêm");
        btnThem.setBorderPainted(false);
        btnThem.setContentAreaFilled(false);
        btnThem.setFocusPainted(false);
        btnThem.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnThem.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnThem.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnXoa.setBackground(new java.awt.Color(204, 255, 255));
        btnXoa.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/trash (1).png"))); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.setBorderPainted(false);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setFocusPainted(false);
        btnXoa.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnXoa.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnXoa.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnSua.setBackground(new java.awt.Color(204, 255, 255));
        btnSua.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/edit (1).png"))); // NOI18N
        btnSua.setText("Sửa");
        btnSua.setBorderPainted(false);
        btnSua.setContentAreaFilled(false);
        btnSua.setFocusPainted(false);
        btnSua.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSua.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSua.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnNhap.setBackground(new java.awt.Color(204, 255, 255));
        btnNhap.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/sheets.png"))); // NOI18N
        btnNhap.setText("Nhập Excel");
        btnNhap.setBorderPainted(false);
        btnNhap.setContentAreaFilled(false);
        btnNhap.setFocusPainted(false);
        btnNhap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNhap.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnNhap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnXuat.setBackground(new java.awt.Color(204, 255, 255));
        btnXuat.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/raven/icon/excel.png"))); // NOI18N
        btnXuat.setText("Xuất Excel");
        btnXuat.setBorderPainted(false);
        btnXuat.setContentAreaFilled(false);
        btnXuat.setFocusPainted(false);
        btnXuat.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnXuat.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnXuat.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(btnNhap)
                .addGap(53, 53, 53)
                .addComponent(btnXuat)
                .addGap(42, 42, 42))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(btnNhap, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnThem, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSua, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnXoa, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(btnXuat))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tìm kiếm\n", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(46, 125, 50))); // NOI18N

        jScrollPane1.setViewportView(txtTimKiem);

        btnTimKiem.setBackground(new java.awt.Color(46, 125, 50));
        btnTimKiem.setForeground(new java.awt.Color(255, 255, 255));
        btnTimKiem.setText("Tìm kiếm");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 299, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách nhà cung cấp\n\n", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 14), new java.awt.Color(46, 125, 50))); // NOI18N
        jPanel3.setDoubleBuffered(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(827, 583));

        tblDanhSach.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblDanhSach);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(46, 125, 50));

        jLabel1.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("QUẢN LÍ NHÀ CUNG CẤP");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(200, 230, 201));
        jLabel2.setText("Theo dõi danh sách nhà cung cấp");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 303, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(359, 359, 359))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(409, 409, 409))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 1040, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 437, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnThemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNhap;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXuat;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDanhSach;
    private javax.swing.JTextPane txtTimKiem;
    // End of variables declaration//GEN-END:variables


}
