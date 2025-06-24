/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package view.Salary;

import controller.SalaryController;
import dao.SalaryDAO;
import java.awt.Desktop;
import java.awt.Frame;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Employees;
import model.Salary;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import static org.apache.poi.ss.usermodel.CellType.BOOLEAN;
import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import view.NhapExcel;

/**
 *
 * @author Admin
 */
public class SalaryForm extends javax.swing.JPanel {

    private SalaryController salaryController;
    private SalaryDAO salaryDao;
    private Salary salary;
    private SuaLuongForm suaLuongForm;
    private NhapExcel nhapExcel;

    /**
     * Creates new form SalaryForm
     */
    public SalaryForm() {
        initComponents();
        initComponents();
        Frame parent = (Frame) SwingUtilities.getWindowAncestor(this);
        salaryController = new SalaryController(this);
        salaryDao = new SalaryDAO();
        nhapExcel = new NhapExcel(parent, true);
        nhapExcel.getBtnSave().addActionListener(salaryController);
        nhapExcel.getBtnUpload().addActionListener(salaryController);
        btnSua.addActionListener(salaryController);
        btnXoa.addActionListener(salaryController);
        btnTimKiem.addActionListener(salaryController);
        btnNhap.addActionListener(salaryController);
        btnXuat.addActionListener(salaryController);
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

    public void LoadDS() {
        String selectedMonth = (String) cboThang.getSelectedItem();
        int monthNumber = Integer.parseInt(selectedMonth.replace("Tháng ", ""));
        salaryDao.getTotalWorkingHoursByEmployee(monthNumber);
        salaryDao.salaryfind(tblDanhsach, monthNumber);

    }

    public void HienthiForm(String action) {
        if ("Sửa".equals(action)) {
            int selectedRow = tblDanhsach.getSelectedRow();
            if (selectedRow >= 0) {
                String maLuong = tblDanhsach.getValueAt(selectedRow, 0).toString();
                String name = tblDanhsach.getValueAt(selectedRow, 1).toString();
                String tongGioLam = tblDanhsach.getValueAt(selectedRow, 2).toString();
                String luongTheoH = tblDanhsach.getValueAt(selectedRow, 3).toString();
                String bonus = tblDanhsach.getValueAt(selectedRow, 4).toString();
                Date ngayTra = (Date) tblDanhsach.getValueAt(selectedRow, 6);
                Salary sl = new Salary(Integer.parseInt(maLuong), 0, new BigDecimal(tongGioLam), new BigDecimal(luongTheoH),
                        new BigDecimal(bonus), ngayTra, null);
                suaLuongForm.setValue(sl, name);
                suaLuongForm.setLocationRelativeTo(this);
                suaLuongForm.setVisible(true);
            }
        } else if ("Xóa".equals(action)) {
            int selectedRow = tblDanhsach.getSelectedRow();
            if (selectedRow >= 0) {
                String maLuong = tblDanhsach.getValueAt(selectedRow, 0).toString();
                String name = tblDanhsach.getValueAt(selectedRow, 1).toString();
                String tongGioLam = tblDanhsach.getValueAt(selectedRow, 2).toString();
                String luongTheoH = tblDanhsach.getValueAt(selectedRow, 3).toString();
                String bonus = tblDanhsach.getValueAt(selectedRow, 4).toString();
                Date ngayTra = (Date) tblDanhsach.getValueAt(selectedRow, 6);
                Salary sl = new Salary(Integer.parseInt(maLuong), 0, new BigDecimal(tongGioLam), new BigDecimal(luongTheoH),
                        new BigDecimal(bonus), ngayTra, null);
                int choise = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn có muốn xóa dữ liệu?",
                        "Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choise == JOptionPane.YES_OPTION) {
                    salaryDao.salary_delete(sl);
                    JOptionPane.showMessageDialog(null, "Đã xóa thành công.");
                    LoadDS();
                }

            }
        } else if ("Nhập Excel".equals(action)) {
            nhapExcel.setLocationRelativeTo(this);
            nhapExcel.setVisible(true);
        }
    }

    public void Huy() {
        suaLuongForm.setVisible(false);
    }

    public void SuaTTLuong() {
        try {
            //gọi hàm getmodel ở form themncc đẻ lấy thông tin vừa nhập
            var salary = suaLuongForm.getModel();
            // gọi hàm dao   
            salaryDao.salary_update(salary);
            LoadDS();
            JOptionPane.showMessageDialog(this, "Sửa thành công");

        } catch (Exception ex) {
            String messages = ex.getMessage();
            var mesErr = convertToStringList(messages);
            String mess = "";
            for (String m : mesErr) {
                mess += m + "\n";
            }
            JOptionPane.showMessageDialog(this, mess, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void LoadCbo() {
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
        btnNhap = new javax.swing.JButton();
        btnXuat = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        cboTieuChi = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtTimKiem = new javax.swing.JTextPane();
        btnTimKiem = new javax.swing.JButton();
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

        btnSua.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\edit (1).png")); // NOI18N
        btnSua.setText("Sửa");
        btnSua.setBorderPainted(false);
        btnSua.setContentAreaFilled(false);
        btnSua.setFocusPainted(false);
        btnSua.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSua.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnSua.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnNhap.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\excel.png")); // NOI18N
        btnNhap.setText("Nhập Excel");
        btnNhap.setBorderPainted(false);
        btnNhap.setContentAreaFilled(false);
        btnNhap.setFocusPainted(false);
        btnNhap.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnNhap.setMargin(new java.awt.Insets(0, 0, 0, 0));
        btnNhap.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        btnXuat.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\sheets.png")); // NOI18N
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
                .addContainerGap()
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 51, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(btnNhap)
                .addGap(18, 18, 18)
                .addComponent(btnXuat)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnXuat, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNhap, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnSua, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));
        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Tìm kiếm\n"));

        cboTieuChi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tên" }));

        jScrollPane1.setViewportView(txtTimKiem);

        btnTimKiem.setIcon(new javax.swing.ImageIcon("D:\\iconJV\\search.png")); // NOI18N
        btnTimKiem.setText("Tìm");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cboTieuChi, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(19, 19, 19)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(btnTimKiem)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(cboTieuChi, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnTimKiem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
    public void timKiem() {
        String tieuChi = cboTieuChi.getSelectedItem().toString();
        // Kiểm tra nếu chưa chọn tiêu chí tìm kiếm
        if (tieuChi.equals("--Chọn tìm kiếm--") || tieuChi.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Bạn cần chọn tiêu chí tìm kiếm!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String tuKhoa = txtTimKiem.getText().trim();
        DefaultTableModel model = (DefaultTableModel) tblDanhsach.getModel();
        model.setRowCount(0);
        ResultSet rs = salaryDao.timKiem(tieuChi, tuKhoa);
        String selectedMonth = (String) cboThang.getSelectedItem();
        int monthNumber = Integer.parseInt(selectedMonth.replace("Tháng ", ""));
        try {
            while (rs != null && rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("salary_id"),
                    rs.getString("employee_name"),
                    rs.getBigDecimal("total_hours"),
                    rs.getBigDecimal("hourly_wage"),
                    rs.getBigDecimal("bonus"),
                    salaryDao.calculateSalary(rs.getInt("employee_id"), monthNumber),
                    rs.getDate("payment_date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static CellStyle DinhdangHeader(XSSFSheet sheet) {
        // Create font
        Font font = sheet.getWorkbook().createFont();
        font.setFontName("Times New Roman");
        font.setBold(true);
        font.setFontHeightInPoints((short) 12); // font size
        font.setColor(IndexedColors.WHITE.getIndex()); // text color

        // Create CellStyle
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
        cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setWrapText(true);
        return cellStyle;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnNhap;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JButton btnXuat;
    private javax.swing.JComboBox<String> cboThang;
    private javax.swing.JComboBox<String> cboTieuChi;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable tblDanhsach;
    private javax.swing.JTextPane txtTimKiem;
    // End of variables declaration//GEN-END:variables

    public void Xuatbaocao() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Danh sách Lương");

            // Style tiêu đề
            CellStyle styleTitle = workbook.createCellStyle();
            Font fontTitle = workbook.createFont();
            fontTitle.setFontHeightInPoints((short) 14);
            fontTitle.setBold(true);
            styleTitle.setFont(fontTitle);
            styleTitle.setAlignment(HorizontalAlignment.CENTER);

            // Style tiêu đề cột
            CellStyle styleHeader = workbook.createCellStyle();
            Font fontHeader = workbook.createFont();
            fontHeader.setBold(true);
            fontHeader.setColor(IndexedColors.WHITE.getIndex());
            styleHeader.setFont(fontHeader);
            styleHeader.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            styleHeader.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            styleHeader.setBorderTop(BorderStyle.THIN);
            styleHeader.setBorderBottom(BorderStyle.THIN);
            styleHeader.setBorderLeft(BorderStyle.THIN);
            styleHeader.setBorderRight(BorderStyle.THIN);
            styleHeader.setAlignment(HorizontalAlignment.CENTER);

            // Style cho dữ liệu
            CellStyle styleCell = workbook.createCellStyle();
            styleCell.setBorderTop(BorderStyle.THIN);
            styleCell.setBorderBottom(BorderStyle.THIN);
            styleCell.setBorderLeft(BorderStyle.THIN);
            styleCell.setBorderRight(BorderStyle.THIN);
            styleCell.setVerticalAlignment(VerticalAlignment.CENTER);
            styleCell.setWrapText(true);

            // Dòng tiêu đề chính
            Row titleRow = sheet.createRow(1);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DANH SÁCH LƯƠNG");
            titleCell.setCellStyle(styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9)); // Merge A2:J2

            // Dòng tiêu đề bảng
            Row headerRow = sheet.createRow(3);
            String[] headers = {"STT", "Mã lương", "Mã NV", "Tên nhân viên", "Tổng giờ làm", "Lương theo h", "Tiền thưởng", "Tổng", "Ngày trả"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleHeader);
            }

            // Lấy dữ liệu
            String tenNV = txtTimKiem.getText().trim();
            ResultSet rs = salaryDao.load_execel(tenNV);

            if (rs == null) {
                JOptionPane.showMessageDialog(null, "Không có dữ liệu để xuất");
                return;
            }

            int rowIdx = 4;
            int stt = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowIdx++);

                int col = 0;

                // STT
                Cell cell = row.createCell(col++);
                cell.setCellValue(stt++);
                cell.setCellStyle(styleCell);

                // Mã luong
                cell = row.createCell(col++);
                cell.setCellValue(rs.getInt("salary_id"));
                cell.setCellStyle(styleCell);
                // Mã NV
                cell = row.createCell(col++);
                cell.setCellValue(rs.getInt("employee_id"));
                cell.setCellStyle(styleCell);

                // Tên nhân viên
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("employee_name"));
                cell.setCellStyle(styleCell);

                // Tổng số giờ làm
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("total_hours"));
                cell.setCellStyle(styleCell);

                // Tiền 1h làm
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("hourly_wage"));
                cell.setCellStyle(styleCell);

                // Tiền thêm
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("bonus"));
                cell.setCellStyle(styleCell);

                String selectedMonth = (String) cboThang.getSelectedItem();
                int monthNumber = Integer.parseInt(selectedMonth.replace("Tháng ", ""));
                // Tổng tiền
                cell = row.createCell(col++);
                cell.setCellValue(salaryDao.calculateSalary(rs.getInt("employee_id"), monthNumber));
                cell.setCellStyle(styleCell);

                // Ngày thanh toán
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("payment_date"));
                cell.setCellStyle(styleCell);

            }

            // Tự động giãn cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Lưu file
            File f = new File("D:\\caheo\\Danhsachluongnhanvien.xlsx");
            try (FileOutputStream out = new FileOutputStream(f)) {
                workbook.write(out);
            }
            workbook.close();

            JOptionPane.showMessageDialog(null, "Xuất báo cáo thành công!");

            // Mở file
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(f);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi xuất báo cáo: " + e.getMessage());
        }
    }
// nhập excel

    private void ReadExcel(String filePath, JTable tbBang) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);

            Iterator<Row> itr = sheet.iterator();
            if (itr.hasNext()) {
                itr.next(); // Bỏ qua dòng tiêu đề
            }
            tbBang.removeAll();

            String[] head = {"STT", "Mã NV", "Tên nhân viên", "Tiền thưởng", "Ngày trả"};
            DefaultTableModel tb = new DefaultTableModel(head, 0);

            while (itr.hasNext()) {
                Row row = itr.next();
                try {
                    int stt = (int) row.getCell(0).getNumericCellValue();
                    String manv = getCellStringValue(row.getCell(1));
                    String tennv = getCellStringValue(row.getCell(2));
//                    String tongio = getCellStringValue(row.getCell(4));
//                    String luongh = getCellStringValue(row.getCell(5));
                    String tienthuong = getCellStringValue(row.getCell(3));
                    String ngaytra = getCellStringValue(row.getCell(4));

                    Vector<Object> vt = new Vector<>();
                    vt.add(stt);
                    vt.add(manv);
                    vt.add(tennv);                   
                    vt.add(tienthuong);
                    vt.add(ngaytra);
                    tb.addRow(vt);
                } catch (Exception e) {
                    System.err.println("Lỗi dòng " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            tbBang.setModel(tb);
            wb.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    public void Upload() {
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            nhapExcel.getTxtFile().setText(file.getPath());

            if (file.getName().toLowerCase().endsWith(".xlsx")) {
                ReadExcel(file.getPath(), nhapExcel.getTblNCC());
                JOptionPane.showMessageDialog(this, "Import thành công!");
                LoadDS(); // Gọi lại để load dữ liệu lên bảng
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn file Excel (.xlsx)");
            }
        }

    }

    public void SaveDataFromExcel() {
        DefaultTableModel model = (DefaultTableModel) nhapExcel.getTblNCC().getModel();
        int rowCount = model.getRowCount();
        int countInserted = 0, countSkipped = 0;

        for (int i = 0; i < rowCount; i++) {
            Salary sp = new Salary();
            try {
                int employee_id = Integer.parseInt(model.getValueAt(i, 1).toString());
                sp.setEmployee_id(employee_id);
//                sp.setTotal_hours(new BigDecimal(model.getValueAt(i, 3).toString()));
//                sp.setHourly_wage(new BigDecimal(model.getValueAt(i, 4).toString()));
                sp.setBonus(new BigDecimal(model.getValueAt(i, 3).toString()));
                String date = model.getValueAt(i, 4).toString();
                if (date.isEmpty() || date.isBlank()) {
                    sp.setPayment_date(null);

                } else {
                    sp.setPayment_date(java.sql.Date.valueOf(date));
                }

                sp.setCreated_date(Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant()));

                // Kiểm tra trùng tên đăng nhập hoặc số điện thoại
//                if (employeeDao.isEmployeeExists(emp.getPhone())) {
//                    countSkipped++;
//                    continue;
//                }
                int r = salaryDao.hasSalaryDataThisMonth(employee_id);
                if ( r != -1 ) {
                    sp.setSalary_id(r);
                    salaryDao.salary_update(sp);
                } else {
                    salaryDao.salary_insert(sp);
                }
                countInserted++;

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Lỗi tại dòng " + (i + 1) + ": " + ex.getMessage());
            }
        }

        JOptionPane.showMessageDialog(this,
                "✔️ Đã thêm " + countInserted + " nhân viên mới\n❌ Bỏ qua " + countSkipped + " dòng do trùng SĐT");

        LoadDS(); // Gọi lại bảng

    }
}
