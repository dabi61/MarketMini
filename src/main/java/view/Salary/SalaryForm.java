/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.Salary;

import controller.SalaryController;
import dao.SalaryDAO;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import model.Salary;

/**
 *
 * @author Admin
 */
public class SalaryForm extends javax.swing.JPanel {
     private SalaryController salaryController;
    private SalaryDAO salaryDao;
    private Salary salary;
    private SuaLuongForm suaLuongForm;

    /**
     * Creates new form SalaryForm
     */
    public SalaryForm() {
        initComponents();initComponents();
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        salaryController = new SalaryController(this);
        salaryDao = new SalaryDAO();
        btnSua.addActionListener(salaryController);
        btnXoa.addActionListener(salaryController);
        suaLuongForm = new SuaLuongForm(parent, true);
        suaLuongForm.getBtnSua().addActionListener(salaryController);
        suaLuongForm.getBtnHuy().addActionListener(salaryController);
        LoadCbo();
        LoadDS();
        cboThang.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                LoadDS(); // Gọi lại để load danh sách (có thể thay bằng hàm khác)
            }
        });
        
    }
     public void LoadDS(){
        String selectedMonth = (String) cboThang.getSelectedItem();
        int monthNumber = Integer.parseInt(selectedMonth.replace("Tháng ", ""));
        salaryDao.getTotalWorkingHoursByEmployee(monthNumber);
        salaryDao.salaryfind(tblDanhsach, monthNumber);
       
    }
    public void HienthiForm(String action) {
       if("Sửa".equals(action)){
            int selectedRow = tblDanhsach.getSelectedRow();
            if(selectedRow >= 0){
               String maLuong = tblDanhsach.getValueAt(selectedRow, 0).toString();
               String name = tblDanhsach.getValueAt(selectedRow, 1).toString();
               String tongGioLam = tblDanhsach.getValueAt(selectedRow, 2).toString();
               String luongTheoH = tblDanhsach.getValueAt(selectedRow, 3).toString();
               String bonus = tblDanhsach.getValueAt(selectedRow, 4).toString();              
               Date ngayTra = (Date) tblDanhsach.getValueAt(selectedRow, 6);
               Salary sl = new Salary(Integer.parseInt(maLuong), 0 , new BigDecimal(tongGioLam), new BigDecimal(luongTheoH),
               new BigDecimal(bonus), ngayTra, null);
               suaLuongForm.setValue(sl, name);
               suaLuongForm.setLocationRelativeTo(this);
               suaLuongForm.setVisible(true);
               }           
        }else if("Xóa".equals(action)){
            int selectedRow = tblDanhsach.getSelectedRow();
            if(selectedRow >= 0){
               String maLuong = tblDanhsach.getValueAt(selectedRow, 0).toString();
               String name = tblDanhsach.getValueAt(selectedRow, 1).toString();
               String tongGioLam = tblDanhsach.getValueAt(selectedRow, 2).toString();
               String luongTheoH = tblDanhsach.getValueAt(selectedRow, 3).toString();
               String bonus = tblDanhsach.getValueAt(selectedRow, 4).toString();              
               Date ngayTra = (Date) tblDanhsach.getValueAt(selectedRow, 6);
               Salary sl = new Salary(Integer.parseInt(maLuong), 0 , new BigDecimal(tongGioLam), new BigDecimal(luongTheoH),
               new BigDecimal(bonus), ngayTra, null);
                int choise = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn có muốn xóa dữ liệu?", 
                        "Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if(choise == JOptionPane.YES_OPTION){
                    salaryDao.salary_delete(sl);
                    JOptionPane.showMessageDialog(null, "Đã xóa thành công.");
                    LoadDS();
                }              
                        
            }
        }
    }
    
    public void Huy(){
        suaLuongForm.setVisible(false);
    }
    public void SuaTTLuong(){
         try{
            //gọi hàm getmodel ở form themncc đẻ lấy thông tin vừa nhập
            var salary = suaLuongForm.getModel();
            // gọi hàm dao   
            salaryDao.salary_update(salary);
            LoadDS();    
            JOptionPane.showMessageDialog(this, "Sửa thành công");
                   
        }catch(Exception ex){
            String messages = ex.getMessage();
            var mesErr = convertToStringList(messages);
            String mess = "";
            for (String m : mesErr) {
                mess += m + "\n";
            }
            JOptionPane.showMessageDialog(this, mess, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }   
    }
    public void LoadCbo(){
        String[] months = new String[12];
            for (int i = 0; i < 12; i++) {
                months[i] = "Tháng " + (i + 1);
            }            
        cboThang.setModel(new DefaultComboBoxModel<>(months));
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        cboThang.setSelectedItem("Tháng " + currentMonth);
    }
     private List<String> convertToStringList(String suppliersString) {
        // Remove square brackets and split by ", "
        String cleanedString = suppliersString.replaceAll("\\[|\\]", "");
        if (cleanedString.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(cleanedString.split(", ")));
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
        jButton1 = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        jButton6 = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDanhsach = new javax.swing.JTable();
        cboThang = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();

        setBackground(new java.awt.Color(255, 255, 255));

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Chức năng\n"));

        jButton1.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\add (1).png")); // NOI18N
        jButton1.setText(" Thêm");
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setFocusPainted(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnXoa.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\trash (1).png")); // NOI18N
        btnXoa.setText("Xóa");
        btnXoa.setBorderPainted(false);
        btnXoa.setContentAreaFilled(false);
        btnXoa.setFocusPainted(false);
        btnXoa.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnXoa.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnXoa.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnSua.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\edit (1).png")); // NOI18N
        btnSua.setText("Sửa");
        btnSua.setBorderPainted(false);
        btnSua.setContentAreaFilled(false);
        btnSua.setFocusPainted(false);
        btnSua.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSua.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSua.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jButton4.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\excel.png")); // NOI18N
        jButton4.setText("Nhập Excel");
        jButton4.setBorderPainted(false);
        jButton4.setContentAreaFilled(false);
        jButton4.setFocusPainted(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jButton5.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\sheets.png")); // NOI18N
        jButton5.setBorderPainted(false);
        jButton5.setContentAreaFilled(false);
        jButton5.setFocusPainted(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setLabel("Xuất Execl");
        jButton5.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(jButton4)
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnXoa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSua, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm kiếm\n"));

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Tên" }));

        jScrollPane1.setViewportView(jTextPane1);

        jButton6.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\search.png")); // NOI18N
        jButton6.setText("Tìm");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton6)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Danh sách lương của nhân viên\n\n"));

        tblDanhsach.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblDanhsach);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 802, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setText("Lọc");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cboThang, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cboThang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(16, 16, 16))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnXoaActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboThang;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JTable tblDanhsach;
    // End of variables declaration//GEN-END:variables
}
