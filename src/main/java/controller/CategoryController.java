/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.CategoryDAO;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
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
import model.Category;
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
import view.Category.CategorysForm;

/**
 *
 * @author Admin
 */
public class CategoryController {

    public CategorysForm categoryForm;
    public CategoryDAO categoryDao;
    public Category category;

    public CategoryController(CategorysForm categoryForm) {
        this.categoryForm = categoryForm;
        categoryDao = new CategoryDAO();
        loadCategory();
    }

//    public void actionPerformed(ActionEvent e) {
//        String button = e.getActionCommand();
//        //categoryForm.HienthiForm(button);
//        if ("Thêm DM".equals(button)) {
//            categoryForm.AddSupplier();
//        }
//        if ("Hủy bỏ".equals(button)) {
//            categoryForm.Huy();
//        }
//        //sửa
//        if ("Lưu thay đổi".equals(button)) {
//            categoryForm.UpdateCategory();
//        }
//        if ("Xuất Excel".equals(button)) {
//            categoryForm.Xuatbaocao();
//        }
//        if ("Upload".equals(button)) {
//            categoryForm.Upload();
//        }
//        if ("Save".equals(button)) {
//            categoryForm.SaveDataFromExcel();
//        }
//        if ("Tìm kiếm".equals(button)) {
//            categoryForm.timKiem();
//        }
//
//    }
    public void HienthiForm(String action) {
        if ("Thêm".equals(action)) {
            categoryForm.getThemDMForm().setLocationRelativeTo(categoryForm);
            categoryForm.getThemDMForm().setVisible(true);

        } else if ("Sửa".equals(action)) {
            JTable tblDanhSach = categoryForm.getTblDanhSach();
            int selectedRow = tblDanhSach.getSelectedRow();
            if (selectedRow >= 0) {
                String maDM = tblDanhSach.getValueAt(selectedRow, 0).toString();
                String ten = tblDanhSach.getValueAt(selectedRow, 1).toString();
                String mota = tblDanhSach.getValueAt(selectedRow, 2).toString();
                Category sp = new Category(Integer.parseInt(maDM), ten, mota);
                categoryForm.getSuaDMForm().setValue(sp);
                categoryForm.getSuaDMForm().setLocationRelativeTo(categoryForm);
                categoryForm.getSuaDMForm().setVisible(true);

            }
        } else if ("Xóa".equals(action)) {
            JTable tblDanhSach = categoryForm.getTblDanhSach();
            int selectedRow = tblDanhSach.getSelectedRow();
            if (selectedRow >= 0) {
                String maDM = tblDanhSach.getValueAt(selectedRow, 0).toString();
                String ten = tblDanhSach.getValueAt(selectedRow, 1).toString();
                String mota = tblDanhSach.getValueAt(selectedRow, 2).toString();
                Category sp = new Category(Integer.parseInt(maDM), ten, mota);
                int choise = JOptionPane.showConfirmDialog(null, "Bạn chắc chắn có muốn xóa dữ liệu?",
                        "Xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (choise == JOptionPane.YES_OPTION) {
                    categoryDao.category_delete(sp);
                    JOptionPane.showMessageDialog(null, "Đã xóa thành công.");
                    loadCategory();
                }
            }
        } else if ("Nhập Excel".equals(action)) {
            categoryForm.getNhapExcel().setLocationRelativeTo(categoryForm);
            categoryForm.getNhapExcel().setVisible(true);
        }

    }

    public void AddSupplier() {
        try {
            //gọi hàm getmodel ở form themncc đẻ lấy thông tin vừa nhập
            var supplier = categoryForm.getThemDMForm().getModel();
            // gọi hàm dao   
            categoryDao.category_insert(supplier);
            JOptionPane.showMessageDialog(categoryForm, "Thêm thành công");
            loadCategory();
            //   themDMForm.clear();
        } catch (Exception ex) {
            String messages = ex.getMessage();
            var mesErr = convertToStringList(messages);
            String mess = "";
            for (String m : mesErr) {
                mess += m + "\n";
            }
            JOptionPane.showMessageDialog(categoryForm, mess, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void UpdateCategory() {
        try {
            var category = categoryForm.getSuaDMForm().getModel();
            // gọi hàm dao   
            categoryDao.category_update(category);
            JOptionPane.showMessageDialog(categoryForm, "Sửa thành công");
            categoryForm.getSuaDMForm().clear();
            loadCategory();

        } catch (Exception e) {
            String messages = e.getMessage();
            var mesErr = convertToStringList(messages);
            String mess = "";
            for (String m : mesErr) {
                mess += m + "\n";
            }
            JOptionPane.showMessageDialog(categoryForm, mess, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void Huy() {
        categoryForm.getThemDMForm().setVisible(false);
        categoryForm.getSuaDMForm().setVisible(false);
        categoryForm.getThemDMForm().clear();
    }

    public void Upload() {
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(categoryForm);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();

            categoryForm.getNhapExcel().getTxtFile().setText(file.getPath());

            if (file.getName().toLowerCase().endsWith(".xlsx")) {
                ReadExcel(file.getPath(), categoryForm.getNhapExcel().getTblNCC());
                JOptionPane.showMessageDialog(categoryForm, "Import thành công!");
                loadCategory(); // Gọi lại để load dữ liệu lên bảng
            } else {
                JOptionPane.showMessageDialog(categoryForm, "Vui lòng chọn file Excel (.xlsx)");
            }
        }

    }

    public void SaveDataFromExcel() {
        DefaultTableModel model = (DefaultTableModel) categoryForm.getNhapExcel().getTblNCC().getModel();
        int rowCount = model.getRowCount();
        int countInserted = 0, countSkipped = 0;

        for (int i = 0; i < rowCount; i++) {
            Category category = new Category();
            try {
                category.setCategory_id(Integer.parseInt(model.getValueAt(i, 0).toString()));
                category.setCategory_name(model.getValueAt(i, 1).toString());
                category.setDescription(model.getValueAt(i, 2).toString());

                // ❗ Kiểm tra trùng TÊN hoặc SỐ ĐIỆN THOẠI
                if (categoryDao.isCategoryExists(category.getCategory_name())) {
                    countSkipped++;
                    continue;
                }
                // Insert vào DB
                categoryDao.category_insert(category);
                countInserted++;
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(categoryForm, "Lỗi tại dòng " + (i + 1) + ": " + ex.getMessage());
            }
        }
        JOptionPane.showMessageDialog(categoryForm,
                "✔️ Đã thêm " + countInserted + " DM mới\n❌ Bỏ qua " + countSkipped + " dòng do trùng tên ");
        loadCategory();

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
            String[] head = {"STT", "Tên Danh Mục", "Mô Tả"};
            DefaultTableModel tb = new DefaultTableModel(head, 0);
            while (itr.hasNext()) {
                Row row = itr.next();
                try {
                    // Đọc và ép kiểu dữ liệu an toàn
                    int stt = (int) row.getCell(0).getNumericCellValue();
                    String name = getCellStringValue(row.getCell(1));
                    String mota = getCellStringValue(row.getCell(2));

                    // Gọi DAO hoặc Controller để thêm vào CSDL
                    Category category = new Category();
                    category.setCategory_id(stt);
                    category.setCategory_name(name);
                    category.setDescription(mota);

                    // <-- đảm bảo bạn đã viết hàm này
                    Vector vt = new Vector();
                    vt.add(stt);
                    vt.add(name);
                    vt.add(mota);

                    tb.addRow(vt);
                    tbBang.setModel(tb);
                } catch (Exception e) {
                    System.err.println("Lỗi dòng " + row.getRowNum() + ": " + e.getMessage());
                    // tiếp tục đọc các dòng sau
                }
            }

            wb.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(categoryForm, "Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }

    public void Xuatbaocao() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Danh Mục");

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
            titleCell.setCellValue("DANH SÁCH DANH MỤC SẢN PHẨM");
            titleCell.setCellStyle(styleTitle);
            sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 3)); // Merge A2:F2

            // Tạo dòng tiêu đề bảng
            Row headerRow = sheet.createRow(3);
            String[] headers = {"STT", "Mã Danh Mục", "Tên Danh Mục", "Mô Tả"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(styleHeader);
            }

            // Lấy dữ liệu
            String ten = categoryForm.getTxtTimKiem().getText().trim();
            if(ten.equals("Tìm kiếm theo tên danh mục")){
                ten = "";
            }
            category.setCategory_name(ten);
            ResultSet rs = categoryDao.load_execel(category);

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
                row.createCell(1).setCellValue(rs.getInt("category_id"));
                row.getCell(1).setCellStyle(styleCell);

                // Tên NCC
                row.createCell(2).setCellValue(rs.getString("category_name"));
                row.getCell(2).setCellStyle(styleCell);

                // Mô Tả
                row.createCell(3).setCellValue(rs.getString("description"));
                row.getCell(3).setCellStyle(styleCell);
            }

            // Auto resize cột
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Ghi ra file
            File f = new File("D:\\caheo\\Danhsachdanhmuc.xlsx");
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

    public void timKiem() {
        String tuKhoa = categoryForm.getTxtTimKiem().getText().trim();

        // Nếu là placeholder hoặc người dùng xóa hết nội dung
        if (tuKhoa.equals("Tìm kiếm theo tên danh mục") || tuKhoa.isEmpty()) {
            loadCategory(); // Gọi lại hàm load toàn bộ danh mục
            return;
        }

        DefaultTableModel model = (DefaultTableModel) categoryForm.getTblDanhSach().getModel();
        model.setRowCount(0); // Xóa dữ liệu cũ

        ResultSet rs = categoryDao.timKiem(tuKhoa);

        try {
            while (rs != null && rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("category_id"),
                    rs.getString("category_name"),
                    rs.getString("description")
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

    private void loadCategory() {
        category = new Category();
        category.setCategory_name("");
        categoryDao.categoryfind(categoryForm.getTblDanhSach(), category);
    }
}
