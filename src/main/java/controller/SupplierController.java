/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.SupplierDAO;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Suppliers;
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
import view.Supplier.SuaNCCForm;
import view.Supplier.SupplierForm;
import view.Supplier.ThemNCCForm;

/**
 *
 * @author Admin
 */
public class SupplierController {

    public SupplierForm supplierForm;
    public SupplierDAO supplierDao;
    public Suppliers supplier;
    public JTable tblDanhSach;
    public SuaNCCForm suaNccForm;
    public ThemNCCForm themNCCForm;
    public NhapExcel nhapExcel;

    public SupplierController(SupplierForm supplierForm) {
        this.supplierForm = supplierForm;
        supplierDao = new SupplierDAO();
        supplier = new Suppliers();
        tblDanhSach = this.supplierForm.getTblDanhSach();

        loadSupplier();

    }
    // tìm kiếm
//    private SupplierDAO dao = new SupplierDAO();
//
//    public ResultSet timKiem(String tieuChi, String tuKhoa) {
//        return dao.timKiem(tieuChi, tuKhoa);
//    } 

    public void HienthiForm(String action) {
        themNCCForm = this.supplierForm.getThemNCCForm();
        nhapExcel = this.supplierForm.getNhapExcel();
        suaNccForm = this.supplierForm.getSuaNccForm();
        if ("Thêm".equals(action)) {
            themNCCForm.setLocationRelativeTo(supplierForm);
            themNCCForm.setVisible(true);

        } else if ("Sửa".equals(action)) {
            tblDanhSach = this.supplierForm.getTblDanhSach();
            int selectedRow = tblDanhSach.getSelectedRow();
            if (selectedRow >= 0) {
                String maNcc = tblDanhSach.getValueAt(selectedRow, 0).toString();
                String tenNcc = tblDanhSach.getValueAt(selectedRow, 1).toString();
                String sdt = tblDanhSach.getValueAt(selectedRow, 2).toString();
                String diachi = tblDanhSach.getValueAt(selectedRow, 3).toString();
                String email = tblDanhSach.getValueAt(selectedRow, 4).toString();
                Suppliers sp = new Suppliers(Integer.parseInt(maNcc), tenNcc, sdt, diachi, email);
                suaNccForm.setValue(sp);
                suaNccForm.setLocationRelativeTo(supplierForm);
                suaNccForm.setVisible(true);

            }
        } else if ("Xóa".equals(action)) {
            int selectedRow = tblDanhSach.getSelectedRow();
            if (selectedRow >= 0) {
                String maNcc = tblDanhSach.getValueAt(selectedRow, 0).toString();
                String tenNcc = tblDanhSach.getValueAt(selectedRow, 1).toString();
                String sdt = tblDanhSach.getValueAt(selectedRow, 2).toString();
                String diachi = tblDanhSach.getValueAt(selectedRow, 3).toString();
                String email = tblDanhSach.getValueAt(selectedRow, 4).toString();
                Suppliers sp = new Suppliers(Integer.parseInt(maNcc), tenNcc, sdt, diachi, email);
                int choise = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn có muốn xóa dữ liệu?",
                        "Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choise == JOptionPane.YES_OPTION) {
                    supplierDao.supplier_delete(sp);
                    JOptionPane.showMessageDialog(null, "Đã xóa thành công.");
                    loadSupplier();
                }
            }
        } else if ("Nhập Excel".equals(action)) {
            nhapExcel.setLocationRelativeTo(supplierForm);
            nhapExcel.setVisible(true);
        }

    }

    public void AddSupplier() {
        try {
            //gọi hàm getmodel ở form themncc đẻ lấy thông tin vừa nhập
            var supplier = themNCCForm.getModel();
            // gọi hàm dao   
            supplierDao.supplier_insert(supplier);
            JOptionPane.showMessageDialog(supplierForm, "Thêm thành công");
            loadSupplier();
            themNCCForm.clear();
        } catch (Exception ex) {
            String messages = ex.getMessage();
            var mesErr = convertToStringList(messages);
            String mess = "";
            for (String m : mesErr) {
                mess += m + "\n";
            }
            JOptionPane.showMessageDialog(supplierForm, mess, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void UpdateSupplier() {

        try {
            var supplier = suaNccForm.getModel();
            // gọi hàm dao   
            supplierDao.supplier_update(supplier);
            JOptionPane.showMessageDialog(supplierForm, "Sửa thành công");
            suaNccForm.clear();
            loadSupplier();
        } catch (Exception e) {
            String messages = e.getMessage();
            var mesErr = convertToStringList(messages);
            String mess = "";
            for (String m : mesErr) {
                mess += m + "\n";
            }
            JOptionPane.showMessageDialog(supplierForm, mess, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void Huy() {
        themNCCForm.setVisible(false);
        suaNccForm.setVisible(false);
        themNCCForm.clear();
    }

    private void loadSupplier() {
        supplier = new Suppliers();
        supplier.setSupplier_name("");
        supplierDao.supplierfind(tblDanhSach, supplier);
        supplierForm.autoResizeColumnWidth(tblDanhSach);
        
    }

    public void timKiem() {
        String tuKhoa = supplierForm.getTxtTimKiem().getText().trim();

        // Nếu là placeholder hoặc người dùng xóa hết nội dung
        if (tuKhoa.equals("Tìm kiếm theo tên nhà cung cấp") || tuKhoa.isEmpty()) {
            loadSupplier(); // Gọi lại hàm load toàn bộ danh mục
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblDanhSach.getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        ResultSet rs = supplierDao.timKiem(tuKhoa);
//        String tieuChi = cboTieuChi.getSelectedItem().toString();
//          // Kiểm tra nếu chưa chọn tiêu chí tìm kiếm
//        if (tieuChi.equals("--Chọn--") || tieuChi.isEmpty()) {
//            JOptionPane.showMessageDialog(
//                this,
//                "Bạn cần chọn tiêu chí tìm kiếm!",
//                "Thông báo",
//                JOptionPane.WARNING_MESSAGE
//            );
//            return;
//        }
//        String tuKhoa = txtTimKiem.getText().trim();
//        DefaultTableModel model = (DefaultTableModel) tblDanhSach.getModel();
//        model.setRowCount(0);
//        ResultSet rs = supplierDao.timKiem(tieuChi, tuKhoa);
        try {
            while (rs != null && rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("supplier_id"),
                    rs.getString("supplier_name"),
                    rs.getString("phone"),
                    rs.getString("address"),
                    rs.getString("email")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private List<String> convertToStringList(String suppliersString) {
        // Remove square brackets and split by ", "
        String cleanedString = suppliersString.replaceAll("\\[|\\]", "");
        if (cleanedString.isEmpty()) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(cleanedString.split(", ")));
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

    public void Xuatbaocao() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Nhà Cung Cấp");

            // Tạo styles
            CellStyle styleTitle = workbook.createCellStyle();
            Font fontTitle = workbook.createFont();
            fontTitle.setFontHeightInPoints((short) 14);
            fontTitle.setBold(true);
            styleTitle.setFont(fontTitle);
            styleTitle.setAlignment(HorizontalAlignment.CENTER);

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

            CellStyle styleCell = workbook.createCellStyle();
            styleCell.setBorderTop(BorderStyle.THIN);
            styleCell.setBorderBottom(BorderStyle.THIN);
            styleCell.setBorderLeft(BorderStyle.THIN);
            styleCell.setBorderRight(BorderStyle.THIN);
            styleCell.setVerticalAlignment(VerticalAlignment.CENTER);
            styleCell.setWrapText(true);

            // Tạo tiêu đề
            Row titleRow = sheet.createRow(1); // Dòng 2 (index 1)
            Cell titleCell = titleRow.createCell(0); // Cột A (index 0)
            titleCell.setCellValue("DANH SÁCH NHÀ CUNG CẤP");
            titleCell.setCellStyle(styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 5)); // Merge A2:F2

            // Tạo dòng tiêu đề bảng
            Row headerRow = sheet.createRow(3);
            String[] headers = {"STT", "Mã NCC", "Tên NCC", "Điện Thoại", "Địa Chỉ", "Email"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleHeader);
            }

            // Lấy dữ liệu
            String tenNCC = supplierForm.getTxtTimKiem().getText().trim();
            if (tenNCC.equals("Tìm kiếm theo tên nhà cung cấp")) {
                tenNCC = "";
            }
            supplier.setSupplier_name(tenNCC);
            ResultSet rs = supplierDao.load_execel(supplier);

            if (rs == null) {
                JOptionPane.showMessageDialog(null, "Không có dữ liệu để xuất");
                return;
            }

            int rowIdx = 4;
            int stt = 1;
            while (rs.next()) {
                Row row = sheet.createRow(rowIdx++);

                // STT
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(stt++);
                cell0.setCellStyle(styleCell);

                // Mã NCC
                row.createCell(1).setCellValue(rs.getInt("supplier_id"));
                row.getCell(1).setCellStyle(styleCell);

                // Tên NCC
                row.createCell(2).setCellValue(rs.getString("supplier_name"));
                row.getCell(2).setCellStyle(styleCell);

                // Điện thoại
                row.createCell(3).setCellValue(rs.getString("phone"));
                row.getCell(3).setCellStyle(styleCell);

                // Địa chỉ
                row.createCell(4).setCellValue(rs.getString("address"));
                row.getCell(4).setCellStyle(styleCell);

                // Email
                row.createCell(5).setCellValue(rs.getString("email"));
                row.getCell(5).setCellStyle(styleCell);
            }

            // Auto resize cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi ra file
            File f = new File("D:\\caheo\\Danhsachnhacungcap.xlsx");
            FileOutputStream out = new FileOutputStream(f);
            workbook.write(out);
            out.close();
            workbook.close();

            JOptionPane.showMessageDialog(null, "Xuất báo cáo thành công!");

            //  Mở file Excel sau khi nhấn OK
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
            String[] head = {"STT", "Tên NCC", "Phone", "Address", "Email"};
            DefaultTableModel tb = new DefaultTableModel(head, 0);
            while (itr.hasNext()) {
                Row row = itr.next();
                try {
                    // Đọc và ép kiểu dữ liệu an toàn
                    int stt = (int) row.getCell(0).getNumericCellValue();
                    String name = getCellStringValue(row.getCell(1));
                    String phone = getCellStringValue(row.getCell(2));
                    String address = getCellStringValue(row.getCell(3));
                    String email = getCellStringValue(row.getCell(4));

                    // Gọi DAO hoặc Controller để thêm vào CSDL
                    Suppliers supplier = new Suppliers();
                    supplier.setSupplier_id(stt);
                    supplier.setSupplier_name(name);
                    supplier.setPhone(phone);
                    supplier.setAddress(address);
                    supplier.setEmail(email);

                    // <-- đảm bảo bạn đã viết hàm này
                    Vector vt = new Vector();
                    vt.add(stt);
                    vt.add(name);
                    vt.add(phone);
                    vt.add(address);
                    vt.add(email);
                    tb.addRow(vt);
                    tbBang.setModel(tb);
                } catch (Exception e) {
                    System.err.println("Lỗi dòng " + row.getRowNum() + ": " + e.getMessage());
                    // tiếp tục đọc các dòng sau
                }
            }

            wb.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(supplierForm, "Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public void SaveDataFromExcel() {
        DefaultTableModel model = (DefaultTableModel) nhapExcel.getTblNCC().getModel();
        int rowCount = model.getRowCount();
        int countInserted = 0, countSkipped = 0;

        for (int i = 0; i < rowCount; i++) {
            Suppliers supplier = new Suppliers();
            try {
                supplier.setSupplier_id(Integer.parseInt(model.getValueAt(i, 0).toString()));
                supplier.setSupplier_name(model.getValueAt(i, 1).toString());
                supplier.setPhone(model.getValueAt(i, 2).toString());
                supplier.setAddress(model.getValueAt(i, 3).toString());
                supplier.setEmail(model.getValueAt(i, 4).toString());
                // ❗ Kiểm tra trùng TÊN hoặc SỐ ĐIỆN THOẠI
                if (supplierDao.isSupplierExists(supplier.getSupplier_name(), supplier.getPhone())) {
                    countSkipped++;
                    continue;
                }
                // Insert vào DB
                supplierDao.supplier_insert(supplier);
                countInserted++;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(supplierForm, "Lỗi tại dòng " + (i + 1) + ": " + ex.getMessage());
            }
        }
        JOptionPane.showMessageDialog(supplierForm,
                "✔️ Đã thêm " + countInserted + " NCC mới\n❌ Bỏ qua " + countSkipped + " dòng do trùng tên hoặc số điện thoại");
        loadSupplier();
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
        int result = fc.showOpenDialog(supplierForm);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            nhapExcel.getTxtFile().setText(file.getPath());

            if (file.getName().toLowerCase().endsWith(".xlsx")) {
                ReadExcel(file.getPath(), nhapExcel.getTblNCC());
                JOptionPane.showMessageDialog(supplierForm, "Import thành công!");
                loadSupplier(); // Gọi lại để load dữ liệu lên bảng
            } else {
                JOptionPane.showMessageDialog(supplierForm, "Vui lòng chọn file Excel (.xlsx)");
            }
        }
    }
}
