/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change employeeForm license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit employeeForm template
 */
package controller;

import dao.EmployeeDAO;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableModel;
import model.Employees;
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
import view.Employee.EmployeeForm;
import view.Employee.SuaNVForm;
import view.Employee.ThemNVForm;
import view.NhapExcel;

/**
 *
 * @author Admin
 */
public class EmployeeController {

    public EmployeeForm employeeForm;
    EmployeeDAO employeeDao;
    Employees employee;
    private ThemNVForm themNVForm;
    private SuaNVForm suaNVForm;
    private NhapExcel nhapExcel;
    private JTable tblDanhSach;
    private Map<String, String> mapChucVu;
    private JTextPane txtTimKiem;

    public EmployeeController(EmployeeForm employeeForm) {
        mapChucVu = new HashMap();
        mapChucVu.put("Admin", "1");
        mapChucVu.put("Nhân viên", "2");
        this.employeeForm = employeeForm;
        try {
            employeeDao = new EmployeeDAO();
        } catch (SQLException ex) {
            Logger.getLogger(EmployeeForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        tblDanhSach = this.employeeForm.getTblDanhSach();

        employee = new Employees();
        loadEmployee();

    }

    public void HienthiForm(String action) {
        themNVForm = this.employeeForm.getThemNVForm();
        suaNVForm = this.employeeForm.getSuaNVForm();
        nhapExcel = this.employeeForm.getNhapExcel();
        tblDanhSach = this.employeeForm.getTblDanhSach();
        txtTimKiem = this.employeeForm.getTxtTimKiem();

        if ("Thêm".equals(action)) {
            themNVForm.setLocationRelativeTo(employeeForm);
            themNVForm.setVisible(true);
        } else if ("Sửa".equals(action)) {
            int selectedRow = tblDanhSach.getSelectedRow();
            if (selectedRow >= 0) {
                String maNV = tblDanhSach.getValueAt(selectedRow, 0).toString();
                String tenNV = tblDanhSach.getValueAt(selectedRow, 1).toString();
                String pass = tblDanhSach.getValueAt(selectedRow, 2).toString();
                String fullname = tblDanhSach.getValueAt(selectedRow, 3).toString();
                String gt = tblDanhSach.getValueAt(selectedRow, 4).toString();
                String role = tblDanhSach.getValueAt(selectedRow, 5).toString();
                String sdt = tblDanhSach.getValueAt(selectedRow, 6).toString();
                String email = tblDanhSach.getValueAt(selectedRow, 7).toString();
                Date ngayThem = (Date) tblDanhSach.getValueAt(selectedRow, 8);
                Employees sp = new Employees(Integer.parseInt(maNV), tenNV, pass, fullname, gt, Integer.parseInt(mapChucVu.get(role)), sdt, email, ngayThem);
                suaNVForm.setValue(sp);
                suaNVForm.setLocationRelativeTo(employeeForm);
                suaNVForm.setVisible(true);
            }
        } else if ("Xóa".equals(action)) {
            int selectedRow = tblDanhSach.getSelectedRow();
            if (selectedRow >= 0) {
                String maNV = tblDanhSach.getValueAt(selectedRow, 0).toString();
                String tenNV = tblDanhSach.getValueAt(selectedRow, 1).toString();
                String pass = tblDanhSach.getValueAt(selectedRow, 2).toString();
                String fullname = tblDanhSach.getValueAt(selectedRow, 3).toString();
                String gt = tblDanhSach.getValueAt(selectedRow, 4).toString();
                String role = tblDanhSach.getValueAt(selectedRow, 5).toString();
                String sdt = tblDanhSach.getValueAt(selectedRow, 6).toString();
                String email = tblDanhSach.getValueAt(selectedRow, 7).toString();
                Date ngayThem = (Date) tblDanhSach.getValueAt(selectedRow, 8);
                Employees sp = new Employees(Integer.parseInt(maNV), tenNV, pass, fullname, gt, Integer.parseInt(mapChucVu.get(role)), sdt, email, ngayThem);
                int choise = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn có muốn xóa dữ liệu?",
                        "Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choise == JOptionPane.YES_OPTION) {
                    employeeDao.employee_delete(sp);
                    JOptionPane.showMessageDialog(null, "Đã xóa thành công.");
                    loadEmployee();
                }

            }
        } else if ("Nhập Excel".equals(action)) {
            nhapExcel.setLocationRelativeTo(employeeForm);
            nhapExcel.setVisible(true);
        } else if ("Xem chi tiết".equals(action)) {
            int selectedRow = tblDanhSach.getSelectedRow();
            if (selectedRow >= 0) {
                String maNV = tblDanhSach.getValueAt(selectedRow, 0).toString();
                String tenNV = tblDanhSach.getValueAt(selectedRow, 1).toString();
                String pass = tblDanhSach.getValueAt(selectedRow, 2).toString();
                String fullname = tblDanhSach.getValueAt(selectedRow, 3).toString();
                String gt = tblDanhSach.getValueAt(selectedRow, 4).toString();
                String role = tblDanhSach.getValueAt(selectedRow, 5).toString();
                String sdt = tblDanhSach.getValueAt(selectedRow, 6).toString();
                String email = tblDanhSach.getValueAt(selectedRow, 7).toString();
                Date ngayThem = (Date) tblDanhSach.getValueAt(selectedRow, 8);

                // ✅ Hiển thị thông tin nhân viên
                String message = String.format(
                        "Mã NV: %s\nTên đăng nhập: %s\nMật khẩu: %s\nHọ tên: %s\nGiới tính: %s\nChức vụ: %s\nSĐT: %s\nEmail: %s\nNgày thêm: %s",
                        maNV, tenNV, pass, fullname, gt, role, sdt, email, new SimpleDateFormat("yyyy-MM-dd").format(ngayThem)
                );

                JOptionPane.showMessageDialog(null, message, "Chi tiết nhân viên", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xem chi tiết!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            }
        }else if ("Xóa".equals(action)) {
            JTable tblDanhSach = employeeForm.getTblDanhSach();
            int selectedRow = tblDanhSach.getSelectedRow();
            if (selectedRow >= 0) {
                String maNV = tblDanhSach.getValueAt(selectedRow, 0).toString();
                String tenNV = tblDanhSach.getValueAt(selectedRow, 1).toString();
                String pass = tblDanhSach.getValueAt(selectedRow, 2).toString();
                String fullname = tblDanhSach.getValueAt(selectedRow, 3).toString();
                String gt = tblDanhSach.getValueAt(selectedRow, 4).toString();
                String role = tblDanhSach.getValueAt(selectedRow, 5).toString();
                String sdt = tblDanhSach.getValueAt(selectedRow, 6).toString();
                String email = tblDanhSach.getValueAt(selectedRow, 7).toString();
                Date ngayThem = (Date) tblDanhSach.getValueAt(selectedRow, 8);
                Employees sp = new Employees(Integer.parseInt(maNV), tenNV, pass, fullname, gt, Integer.parseInt(mapChucVu.get(role)), sdt, email, ngayThem);
                int choise = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn có muốn xóa dữ liệu?",
                        "Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choise == JOptionPane.YES_OPTION) {
                    try {
                        employeeDao.employee_delete(sp);
                        JOptionPane.showMessageDialog(null, "Đã xóa thành công.");
                        loadEmployee();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, "Không thể xóa user ");
                    }
                  
                }
            }
        }
    }

    public void AddEmployee() {
        try {
            //gọi hàm getmodel ở form themncc đẻ lấy thông tin vừa nhập
            var employee = themNVForm.getModel();
            // gọi hàm dao   
            employeeDao.employee_insert(employee);
            JOptionPane.showMessageDialog(employeeForm, "Thêm thành công");
            themNVForm.clear();

            loadEmployee();
        } catch (Exception ex) {
            String messages = ex.getMessage();
            var mesErr = convertToStringList(messages);
            String mess = "";
            for (String m : mesErr) {
                mess += m + "\n";
            }
            JOptionPane.showMessageDialog(employeeForm, mess, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void Huy() {
        themNVForm.setVisible(false);
        suaNVForm.setVisible(false);
        themNVForm.clear();
        suaNVForm.clear();
    }

    public void UpdateEmployee() {
        try {
            //gọi hàm getmodel ở form themncc đẻ lấy thông tin vừa nhập
            var employee = suaNVForm.getModel();
            // gọi hàm dao   
            employeeDao.employee_update(employee);
            loadEmployee();
            JOptionPane.showMessageDialog(employeeForm, "Sửa thành công");
            suaNVForm.clear();
        } catch (Exception ex) {
            String messages = ex.getMessage();
            var mesErr = convertToStringList(messages);
            String mess = "";
            for (String m : mesErr) {
                mess += m + "\n";
            }
            JOptionPane.showMessageDialog(employeeForm, mess, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadEmployee() {
        employeeDao.employeefind(tblDanhSach);

    }

    private List<String> convertToStringList(String suppliersString) {
        // Remove square brackets and split by ", "
        String cleanedString = suppliersString.replaceAll("\\[|\\]", "");
        if (cleanedString.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(cleanedString.split(", ")));
    }

//    private static CellStyle DinhdangHeader(XSSFSheet sheet) {
//        // Create font
//        Font font = sheet.getWorkbook().createFont();
//        font.setFontName("Times New Roman");
//        font.setBold(true);
//        font.setFontHeightInPoints((short) 12); // font size
//        font.setColor(IndexedColors.WHITE.getIndex()); // text color
//
//        // Create CellStyle
//        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
//        cellStyle.setFont(font);
//        cellStyle.setAlignment(HorizontalAlignment.CENTER);
//        cellStyle.setVerticalAlignment(VerticalAlignment.TOP);
//        cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
//        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        cellStyle.setBorderBottom(BorderStyle.THIN);
//        cellStyle.setWrapText(true);
//        return cellStyle;
//    }
    private String mapRoleToName(int roleId) {
        return switch (roleId) {
            case 1 ->
                "Admin";
            case 2 ->
                "Nhân viên";
            default ->
                "Không rõ";
        };
    }

    public void Xuatbaocao() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Danh sách nhân viên");

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
            titleCell.setCellValue("DANH SÁCH NHÂN VIÊN");
            titleCell.setCellStyle(styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 9)); // Merge A2:J2

            // Dòng tiêu đề bảng
            Row headerRow = sheet.createRow(3);
            String[] headers = {"STT", "Mã NV", "Tên Đăng Nhập", "Mật Khẩu", "Họ Tên", "Giới Tính", "Chức vụ", "SĐT", "Email", "Ngày"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleHeader);
            }

            // Lấy dữ liệu
            String tenNV = txtTimKiem.getText().trim();
            employee = new Employees();
            employee.setEmployee_name(tenNV);
            ResultSet rs = employeeDao.load_execel(employee);

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

                // Mã NV
                cell = row.createCell(col++);
                cell.setCellValue(rs.getInt("employee_id"));
                cell.setCellStyle(styleCell);

                // Tên đăng nhập
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("employee_name"));
                cell.setCellStyle(styleCell);

                // Mật khẩu
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("password"));
                cell.setCellStyle(styleCell);

                // Họ tên
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("full_name"));
                cell.setCellStyle(styleCell);

                // Giới tính
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("sex"));
                cell.setCellStyle(styleCell);

                // Vai trò (chuyển từ số sang chuỗi)
                int roleId = rs.getInt("role");
                String roleName = mapRoleToName(roleId);
                cell = row.createCell(col++);
                cell.setCellValue(roleName);
                cell.setCellStyle(styleCell);

                // SĐT
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("phone"));
                cell.setCellStyle(styleCell);

                // Email
                cell = row.createCell(col++);
                cell.setCellValue(rs.getString("email"));
                cell.setCellStyle(styleCell);

                // Ngày
                String dateStr = rs.getDate("date") != null ? rs.getDate("date").toString() : "";
                cell = row.createCell(col++);
                cell.setCellValue(dateStr);
                cell.setCellStyle(styleCell);
            }

            // Tự động giãn cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Lưu file
            File f = new File("D:\\caheo\\Danhsachnhanvien.xlsx");
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

    private void ReadExcel(String filePath, JTable tbBang) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            Workbook wb = new XSSFWorkbook(fis);
            Sheet sheet = wb.getSheetAt(0);

            Iterator<Row> itr = sheet.iterator();
            if (itr.hasNext()) {
                itr.next(); // Bỏ qua dòng tiêu đề
            }
            tbBang.removeAll();

            String[] head = {"STT", "Tên đăng nhập", "Mật khẩu", "Họ tên", "Giới tính", "Chức vụ", "SĐT", "Email", "Ngày"};
            DefaultTableModel tb = new DefaultTableModel(head, 0);

            while (itr.hasNext()) {
                Row row = itr.next();
                try {
                    int stt = (int) row.getCell(0).getNumericCellValue();
                    String username = getCellStringValue(row.getCell(1));
                    String password = getCellStringValue(row.getCell(2));
                    String fullName = getCellStringValue(row.getCell(3));
                    String sex = getCellStringValue(row.getCell(4));
                    String roleStr = getCellStringValue(row.getCell(5));
                    int role = roleStr.equalsIgnoreCase("admin") ? 1 : 2; // 1: admin, 2: nhân viên
                    String phone = getCellStringValue(row.getCell(6));
                    String email = getCellStringValue(row.getCell(7));
                    String date = getCellStringValue(row.getCell(8)); // chuỗi ngày yyyy-MM-dd

                    Vector<Object> vt = new Vector<>();
                    vt.add(stt);
                    vt.add(username);
                    vt.add(password);
                    vt.add(fullName);
                    vt.add(sex);
                    vt.add(role == 1 ? "Admin" : "Nhân viên");
                    vt.add(phone);
                    vt.add(email);
                    vt.add(date);
                    tb.addRow(vt);
                } catch (Exception e) {
                    System.err.println("Lỗi dòng " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            tbBang.setModel(tb);
            wb.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(employeeForm, "Lỗi khi đọc file Excel: " + e.getMessage());
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
        int result = fc.showOpenDialog(employeeForm);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            nhapExcel.getTxtFile().setText(file.getPath());

            if (file.getName().toLowerCase().endsWith(".xlsx")) {
                ReadExcel(file.getPath(), nhapExcel.getTblNCC());
                JOptionPane.showMessageDialog(employeeForm, "Import thành công!");
                loadEmployee(); // Gọi lại để load dữ liệu lên bảng
            } else {
                JOptionPane.showMessageDialog(employeeForm, "Vui lòng chọn file Excel (.xlsx)");
            }
        }

    }

    public void SaveDataFromExcel() {
        DefaultTableModel model = (DefaultTableModel) nhapExcel.getTblNCC().getModel();
        int rowCount = model.getRowCount();
        int countInserted = 0, countSkipped = 0;

        for (int i = 0; i < rowCount; i++) {
            Employees emp = new Employees();
            try {
                emp.setEmployee_id(Integer.parseInt(model.getValueAt(i, 0).toString()));
                emp.setEmployee_name(model.getValueAt(i, 1).toString());
                emp.setPassword(model.getValueAt(i, 2).toString());
                emp.setFull_name(model.getValueAt(i, 3).toString());
                emp.setSex(model.getValueAt(i, 4).toString());

                String roleText = model.getValueAt(i, 5).toString().toLowerCase();
                emp.setRole(roleText.contains("admin") ? 1 : 2);

                emp.setPhone(model.getValueAt(i, 6).toString());
                emp.setEmail(model.getValueAt(i, 7).toString());
                emp.setDate(Date.valueOf(model.getValueAt(i, 8).toString())); // cần chuỗi yyyy-MM-dd

                // Kiểm tra trùng tên đăng nhập hoặc số điện thoại
                if (employeeDao.isEmployeeExists(emp.getPhone())) {
                    countSkipped++;
                    continue;
                }

                employeeDao.employee_insert(emp);
                countInserted++;

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(employeeForm, "Lỗi tại dòng " + (i + 1) + ": " + ex.getMessage());
            }
        }

        JOptionPane.showMessageDialog(employeeForm,
                "✔️ Đã thêm " + countInserted + " nhân viên mới\n❌ Bỏ qua " + countSkipped + " dòng do trùng SĐT");

        loadEmployee(); // Gọi lại bảng

    }

    public void timKiem() {
        String tieuChi = employeeForm.getCboTieuChi().getSelectedItem().toString();
        // Kiểm tra nếu chưa chọn tiêu chí tìm kiếm
        if (tieuChi.equals("--Chọn tìm kiếm--") || tieuChi.isEmpty()) {
            JOptionPane.showMessageDialog(
                    employeeForm,
                    "Bạn cần chọn tiêu chí tìm kiếm!",
                    "Thông báo",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }
        String tuKhoa = txtTimKiem.getText().trim();
        DefaultTableModel model = (DefaultTableModel) tblDanhSach.getModel();
        model.setRowCount(0);
        ResultSet rs = employeeDao.timKiem(tieuChi, tuKhoa);
        try {
            while (rs != null && rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("employee_id"),
                    rs.getString("employee_name"),
                    rs.getString("password"),
                    rs.getString("full_name"),
                    rs.getString("full_name"),
                    employeeDao.MapRole(rs.getInt("role")),
                    rs.getString("phone"),
                    rs.getString("email"),
                    rs.getDate("date")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
