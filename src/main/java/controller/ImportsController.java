/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.toedter.calendar.JDateChooser;
import dao.ImportsDAO;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import model.DBConnection;
import model.ExcelImporter;
import model.Imports;
import model.Products;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import view.StoreForm;

/**
 *
 * @author Admin
 */
public class ImportsController {

    private ImportsDAO importsDAO;
    private StoreForm importsView;
    private Connection connection;

    public ImportsController(ImportsDAO importsDAO, StoreForm importsView) throws SQLDataException {
        this.importsDAO = importsDAO;
        this.importsView = importsView;
        this.connection = DBConnection.getConnection(); // Kết nối database
        importsDAO = new ImportsDAO(connection); // Khởi tạo DAO với connection
    }

    public void addImport() {
        Map<String, Integer> supplierMap = importsView.getSupplierMap(); // Lấy supplierMap từ StoreForm
        String supplierName = importsView.getSupplierComboBoxSelection();
        Map<String, Integer> categoryMap = importsView.getCategoryMap(); // Lấy categoryMap từ StoreForm
        String categoryName = importsView.getCategoryComboBoxSelection();
        Map<String, Integer> employeeMap = importsView.getEmployeeMap(); // Lấy employeeStoreForm
        String fullname = importsView.getEmployeeComboBoxSelection();
        // Lấy dữ liệu từ form
        String tenSPNhap = importsView.getTxtTenSPNhap().getText().trim();
        String soLuongNhap = importsView.getTxtSoLuongNhap().getText().trim();
        String giaNhap = importsView.getTxtGiaNhap().getText().trim();
        String donViNhap = importsView.getTxtDonViNhap().getText().trim();
        java.util.Date utilDate = importsView.getjDateNgayNhap().getDate();
        String NCCNhap = importsView.getCboNCCNhap().getItemAt(0);
        String LoaiHangNhap = importsView.getCboLoaiHangNhap().getItemAt(0);
        String NhanVienNhap = importsView.getCboNhanVienNhap().getItemAt(0);

        if (supplierName == null || supplierName.equals("--Chọn nhà cung cấp")) {
            JOptionPane.showMessageDialog(importsView, "Vui lòng chọn một nhà cung cấp hợp lệ!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer supplierId = supplierMap.get(supplierName); // Lấy ID từ supplierMap

        if (categoryName == null || categoryName.equals("--Chọn loại hàng")) {
            JOptionPane.showMessageDialog(importsView, "Vui lòng chọn một loại hàng hợp lệ!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer categoryId = categoryMap.get(categoryName);

        if (fullname == null || fullname.equals("--Chọn nhân viên")) {
            JOptionPane.showMessageDialog(importsView, "Vui lòng chọn một nhân viên hợp lệ!", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Integer employeeId = employeeMap.get(fullname);

        // Kiểm tra dữ liệu đầu vào
        if (tenSPNhap.isEmpty() || soLuongNhap.isEmpty() || giaNhap.isEmpty() || donViNhap.isEmpty()
                || NCCNhap.isEmpty() || LoaiHangNhap.isEmpty() || NhanVienNhap.isEmpty()) {
            JOptionPane.showMessageDialog(importsView, "Vui lòng điền đầy đủ thông tin", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (utilDate == null) {
            JOptionPane.showMessageDialog(importsView, "Vui lòng chọn ngày nhập", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            // Kiểm tra số lượng và giá nhập có phải là số hợp lệ
            if (!soLuongNhap.matches("\\d+")) {
                JOptionPane.showMessageDialog(importsView, "Số lượng nhập phải là số nguyên dương!", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!giaNhap.matches("\\d+")) {
                JOptionPane.showMessageDialog(importsView, "Giá nhập phải là số nguyên dương!", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int soLuong = Integer.parseInt(soLuongNhap);
            int gia = Integer.parseInt(giaNhap);
            java.sql.Date ngayNhap = new java.sql.Date(utilDate.getTime());

            // Gọi phương thức insertOrUpdateImport từ ImportsDAO
            boolean success = importsDAO.insertOrUpdateImport(tenSPNhap, categoryId, donViNhap, soLuong, gia,
                    ngayNhap, supplierId, employeeId);

            if (success) {
                JOptionPane.showMessageDialog(importsView, "Thêm nhập hàng thành công!");
                importsView.setMacDinh();
            } else {
                JOptionPane.showMessageDialog(importsView, "Thêm nhập hàng thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi xử lý: " + e.getMessage());
        }
    }

    public void importExcelToDatabase(File file) {
        try {
            List<String[]> dataList = ExcelImporter.readExcel(file);
            if (dataList.size() <= 1) {
                JOptionPane.showMessageDialog(importsView, "File Excel không có dữ liệu!");
                return;
            }

            // Lấy map từ View
            Map<String, Integer> supplierMap = importsView.getSupplierMap();
            Map<String, Integer> categoryMap = importsView.getCategoryMap();
            Map<String, Integer> employeeMap = importsView.getEmployeeMap();

            int successCount = 0; // Đếm số dòng nhập thành công

            // Bỏ dòng tiêu đề
            for (int i = 1; i < dataList.size(); i++) {
                String[] row = dataList.get(i);
                if (row.length < 8) {
                    continue; // Thiếu cột
                }
                String tenSP = row[0].trim();
                int soLuong = Integer.parseInt(row[1].trim());
                int giaNhap = Integer.parseInt(row[2].trim());
                String donVi = row[3].trim();

                java.util.Date utilDate = new SimpleDateFormat("yyyy-MM-dd").parse(row[4].trim());
                java.sql.Date ngayNhap = new java.sql.Date(utilDate.getTime());

                String supplierName = row[5].trim();
                String categoryName = row[6].trim();
                String employeeName = row[7].trim();

                Integer supplierId = supplierMap.get(supplierName);
                Integer categoryId = categoryMap.get(categoryName);
                Integer employeeId = employeeMap.get(employeeName);

                if (supplierId == null || categoryId == null || employeeId == null) {
                    System.err.println("Không tìm thấy ID tương ứng cho dòng " + (i + 1));
                    continue;
                }

                boolean success = importsDAO.insertOrUpdateImport(
                        tenSP, categoryId, donVi, soLuong, giaNhap,
                        ngayNhap, supplierId, employeeId
                );

                if (success) {
                    successCount++; //Nhập thành công
                } else {
                    System.err.println("Không thể thêm dòng Excel số " + (i + 1));
                }
            }

            JOptionPane.showMessageDialog(importsView,
                    "Nhập từ Excel hoàn tất!\nĐã nhập thành công: " + successCount + " sản phẩm.");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi nhập Excel: " + e.getMessage());
        }
    }

    public void deleteImport() {
        int selectedRow = importsView.getTblViewNhapHang().getSelectedRow();

        if (selectedRow >= 0) {
            // Lấy import_id từ dòng được chọn
            int importId = Integer.parseInt(importsView.getTblViewNhapHang().getValueAt(selectedRow, 0).toString());

            // Xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa bản ghi này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = importsDAO.deleteImport(importId); // Gọi DAO để xóa

                if (success) {
                    JOptionPane.showMessageDialog(null, "Xóa thành công!");
                    // Có thể gọi phương thức load lại bảng ở đây, ví dụ:
                    // importsView.loadImportsTable();
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy bản ghi để xóa hoặc xóa thất bại.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa.");
        }
    }

    public void updateImport() {
        int selectedRow = importsView.getTblViewNhapHang().getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(importsView, "Vui lòng chọn một dòng để cập nhật.");
            return;
        }

        try {
            int importId = Integer.parseInt(importsView.getTblViewNhapHang().getValueAt(selectedRow, 0).toString());

            // Lấy dữ liệu từ form
            String tenSPNhap = importsView.getTxtTenSPNhap().getText().trim();
            String soLuongNhap = importsView.getTxtSoLuongNhap().getText().trim();
            String giaNhap = importsView.getTxtGiaNhap().getText().trim();
            String donViNhap = importsView.getTxtDonViNhap().getText().trim();
            java.util.Date utilDate = importsView.getjDateNgayNhap().getDate();

            String supplierName = importsView.getSupplierComboBoxSelection();
            String categoryName = importsView.getCategoryComboBoxSelection();
            String fullname = importsView.getEmployeeComboBoxSelection();

            if (supplierName == null || supplierName.equals("--Chọn nhà cung cấp")
                    || categoryName == null || categoryName.equals("--Chọn loại hàng")
                    || fullname == null || fullname.equals("--Chọn nhân viên")) {
                JOptionPane.showMessageDialog(importsView, "Vui lòng chọn đầy đủ nhà cung cấp, loại hàng và nhân viên.");
                return;
            }

            // Ánh xạ ID từ tên
            Map<String, Integer> supplierMap = importsView.getSupplierMap();
            Map<String, Integer> categoryMap = importsView.getCategoryMap();
            Map<String, Integer> employeeMap = importsView.getEmployeeMap();

            int supplierId = supplierMap.get(supplierName);
            int categoryId = categoryMap.get(categoryName);
            int employeeId = employeeMap.get(fullname);

            // Kiểm tra rỗng
            if (tenSPNhap.isEmpty() || soLuongNhap.isEmpty() || giaNhap.isEmpty() || donViNhap.isEmpty() || utilDate == null) {
                JOptionPane.showMessageDialog(importsView, "Vui lòng điền đầy đủ thông tin cần cập nhật.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int soLuong = Integer.parseInt(soLuongNhap);
            int gia = Integer.parseInt(giaNhap);
            java.sql.Date ngayNhap = new java.sql.Date(utilDate.getTime());

            // Gọi phương thức DAO để cập nhật
            boolean success = importsDAO.updateImport(
                    importId, tenSPNhap, categoryId, donViNhap, soLuong, gia, ngayNhap, supplierId, employeeId
            );

            if (success) {
                JOptionPane.showMessageDialog(importsView, "Cập nhật đơn nhập thành công!");
                importsView.setMacDinh();
            } else {
                JOptionPane.showMessageDialog(importsView, "Cập nhật thất bại. Kiểm tra lại dữ liệu.");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(importsView, "Giá và số lượng phải là số!", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    public void searchImport() {
        try {
            // Lấy dữ liệu người dùng chọn trên giao diện
            JDateChooser dateTimTu = importsView.getjDateTimTu();
            JDateChooser dateTimDen = importsView.getjDateTimDen();
            // Lấy ngày từ JDateChooser
            java.util.Date tuNgay = dateTimTu.getDate();
            java.util.Date denNgay = dateTimDen.getDate();

            String categoryName = importsView.getCboLoaiHangNhapTim();
            String supplierName = importsView.getCboNCCNhapTim();

            Map<String, Integer> categoryMap = importsView.getCategoryMap();
            Map<String, Integer> supplierMap = importsView.getSupplierMap();

            Integer categoryId = null;
            Integer supplierId = null;

            if (categoryName != null && !categoryName.equals("--Chọn loại hàng")) {
                categoryId = categoryMap.get(categoryName);
            }

            if (supplierName != null && !supplierName.equals("--Chọn nhà cung cấp")) {
                supplierId = supplierMap.get(supplierName);
            }

            // Gọi DAO
            List<Imports> results = importsDAO.searchImport(tuNgay, denNgay, categoryId, supplierId);

            // Hiển thị kết quả tìm kiếm lên bảng
            importsView.loadDuLieuNhap1(results);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    public void deleteProduct() {
        int selectedRow = importsView.getTblViewKhoHang().getSelectedRow();

        if (selectedRow >= 0) {
            int productId = Integer.parseInt(importsView.getTblViewKhoHang().getValueAt(selectedRow, 0).toString());
            // Xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa sản phẩm này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = importsDAO.deleteProduct(productId); // Gọi DAO để xóa

                if (success) {
                    JOptionPane.showMessageDialog(null, "Xóa thành công!");
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy bản ghi để xóa hoặc xóa thất bại.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một sản phẩm để xóa.");
        }
    }

    public void updateProduct() {
        int selectedRow = importsView.getTblViewKhoHang().getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(importsView, "Vui lòng chọn một dòng để cập nhật.");
            return;
        }

        try {
            int productId = Integer.parseInt(importsView.getTblViewKhoHang().getValueAt(selectedRow, 0).toString());
            // Lấy dữ liệu từ form
            String tenSPKho = importsView.getTxtTenSPKho().getText().trim();
            String giaKho = importsView.getTxtGiaKho().getText().trim();
            String donViKho = importsView.getTxtDonViKho().getText().trim();
            String categoryName = importsView.getCategoryStoreComboBox();

            if (categoryName == null || categoryName.equals("--Chọn loại hàng")) {
                JOptionPane.showMessageDialog(importsView, "Vui lòng chọn đầy đủ loại hàng.");
                return;
            }

            Map<String, Integer> categoryMap = importsView.getCategoryMap();
            int categoryId = categoryMap.get(categoryName);

            // Kiểm tra rỗng
            if (tenSPKho.isEmpty() || giaKho.isEmpty() || donViKho.isEmpty() || categoryName.isEmpty()) {
                JOptionPane.showMessageDialog(importsView, "Vui lòng điền đầy đủ thông tin cần cập nhật.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int gia = Integer.parseInt(giaKho);

            // Gọi phương thức DAO để cập nhật
            boolean success = importsDAO.updateProduct(productId, tenSPKho, categoryId, gia, donViKho);

            if (success) {
                JOptionPane.showMessageDialog(importsView, "Cập nhật đơn nhập thành công!");
            } else {
                JOptionPane.showMessageDialog(importsView, "Cập nhật thất bại. Kiểm tra lại dữ liệu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    public void searchProduct() {
        try {
            // Lấy dữ liệu người dùng chọn trên giao diện
            String productName = importsView.getTxtTenSPTimKho().getText().trim();

            String categoryName = importsView.getCategorySearchComboBox();
            String supplierName = importsView.getSupplierSearchComboBox();

            Map<String, Integer> categoryMap = importsView.getCategoryMap();
            Map<String, Integer> supplierMap = importsView.getSupplierMap();

            Integer categoryId = null;
            Integer supplierId = null;

            if (categoryName != null && !categoryName.equals("--Chọn loại hàng")) {
                categoryId = categoryMap.get(categoryName);
            }

            if (supplierName != null && !supplierName.equals("--Chọn nhà cung cấp")) {
                supplierId = supplierMap.get(supplierName);
            }

            // Gọi DAO
            List<Products> results = importsDAO.searchProduct(categoryId, supplierId, productName);

            // Hiển thị kết quả tìm kiếm lên bảng
            importsView.loadDuLieuKho1(results);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    public void exportImportProductExcelByDateRange(File file) {
        try {
            java.util.Date fromDate = importsView.getjDateTimTu().getDate();
            java.util.Date toDate = importsView.getjDateTimDen().getDate();

            java.sql.Date sqlFromDate = (fromDate != null) ? new java.sql.Date(fromDate.getTime()) : null;
            java.sql.Date sqlToDate = (toDate != null) ? new java.sql.Date(toDate.getTime()) : null;

            List<Imports> data;
            if (sqlFromDate != null && sqlToDate != null) {
                data = importsDAO.getImportsByDateRange(sqlFromDate, sqlToDate);
            } else {
                data = importsDAO.searchImport(null, null, null, null);
            }

            if (data.isEmpty()) {
                JOptionPane.showMessageDialog(importsView, "Không có dữ liệu để xuất.");
                return;
            }

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Lịch sử nhập hàng - Green Buy");

            int rowIdx = 0;

            // --- STYLE ---
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dateStyle = workbook.createCellStyle();
            Font dateFont = workbook.createFont();
            dateFont.setItalic(true);
            dateStyle.setFont(dateFont);
            dateStyle.setAlignment(HorizontalAlignment.LEFT);

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // --- Hàng 1: Tiêu đề ---
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("LỊCH SỬ NHẬP HÀNG");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 8));

            // --- Hàng 2: Dòng trống ---
            rowIdx++;

            // --- Hàng 3: "Từ ngày đến ngày" ---
            Row dateRangeRow = sheet.createRow(rowIdx++);
            Cell dateCell = dateRangeRow.createCell(0);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            String fromStr = (fromDate != null) ? sdf.format(fromDate) : "";
            String toStr = (toDate != null) ? sdf.format(toDate) : "";
            dateCell.setCellValue("Từ ngày: " + fromStr + "    đến ngày: " + toStr);
            dateCell.setCellStyle(dateStyle);
            sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 8));

            // --- Hàng 4: Header ---
            Row headerRow = sheet.createRow(rowIdx++);
            String[] columns = {"Mã nhập", "Tên sản phẩm", "Số lượng", "Giá nhập", "Đơn vị", "Ngày nhập", "Nhà cung cấp", "Loại hàng", "Nhân viên"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Dữ liệu từ hàng 5 trở đi ---
            for (Imports i : data) {
                Row row = sheet.createRow(rowIdx++);
                Cell c0 = row.createCell(0);
                c0.setCellValue(i.getImport_id());
                c0.setCellStyle(dataStyle);
                Cell c1 = row.createCell(1);
                c1.setCellValue(i.getProduct_name());
                c1.setCellStyle(dataStyle);
                Cell c2 = row.createCell(2);
                c2.setCellValue(i.getQuantity());
                c2.setCellStyle(dataStyle);
                Cell c3 = row.createCell(3);
                c3.setCellValue(i.getImport_price());
                c3.setCellStyle(dataStyle);
                Cell c4 = row.createCell(4);
                c4.setCellValue(i.getUnit());
                c4.setCellStyle(dataStyle);
                Cell c5 = row.createCell(5);
                c5.setCellValue(i.getImport_date().toString());
                c5.setCellStyle(dataStyle);
                Cell c6 = row.createCell(6);
                c6.setCellValue(i.getSupplier_name());
                c6.setCellStyle(dataStyle);
                Cell c7 = row.createCell(7);
                c7.setCellValue(i.getCategory_name());
                c7.setCellStyle(dataStyle);
                Cell c8 = row.createCell(8);
                c8.setCellValue(i.getEmployee_name());
                c8.setCellStyle(dataStyle);
            }

            // --- Căn chỉnh độ rộng ---
            sheet.setColumnWidth(1, 10000); // Tên sản phẩm dài hơn
            for (int i = 0; i < columns.length; i++) {
                if (i != 1) {
                    sheet.autoSizeColumn(i);
                }
            }

            // --- Ghi file và mở ---
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.close();

            Desktop.getDesktop().open(file);

            JOptionPane.showMessageDialog(importsView, "Xuất Excel thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi xuất Excel: " + e.getMessage());
        }
    }

    public void exportStoreExcel(File file) {
        try {
            // --- Lấy giá trị lọc từ giao diện ---
            String categoryName = importsView.getCategorySearchComboBox(); // view.get selected loại hàng
            String supplierName = importsView.getSupplierSearchComboBox(); // view.get selected nhà cung cấp

            Map<String, Integer> categoryMap = importsView.getCategoryMap();
            Map<String, Integer> supplierMap = importsView.getSupplierMap();

            Integer categoryId = null;
            Integer supplierId = null;

            if (categoryName != null && !categoryName.equals("--Chọn loại hàng")) {
                categoryId = categoryMap.get(categoryName);
            }

            if (supplierName != null && !supplierName.equals("--Chọn nhà cung cấp")) {
                supplierId = supplierMap.get(supplierName);
            }

            // --- Gọi DAO đúng cách ---
            List<Products> productList = importsDAO.getAllProductsFromTable(categoryId, supplierId);

            if (productList.isEmpty()) {
                JOptionPane.showMessageDialog(importsView, "Không có dữ liệu để xuất!");
                return;
            }

            XSSFWorkbook workbook = new XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Tồn kho - Green Buy");
            int rowIdx = 0;

            // --- STYLE ---
            CellStyle titleStyle = workbook.createCellStyle();
            XSSFFont titleFont = workbook.createFont();
            titleFont.setFontHeightInPoints((short) 16);
            titleFont.setBold(true);
            titleStyle.setFont(titleFont);
            titleStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle headerStyle = workbook.createCellStyle();
            XSSFFont headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);

            // --- Hàng 1: Tiêu đề ---
            Row titleRow = sheet.createRow(rowIdx++);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("DANH SÁCH SẢN PHẨM TỒN KHO");
            titleCell.setCellStyle(titleStyle);
            sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

            // --- Hàng 2: Thông tin lọc ---
            Row filterRow = sheet.createRow(rowIdx++);
            filterRow.createCell(0).setCellValue("Loại hàng: " + (categoryName != null ? categoryName : ""));
            filterRow.createCell(3).setCellValue("Nhà cung cấp: " + (supplierName != null ? supplierName : ""));

            // --- Hàng 3: Dòng trống ---
            rowIdx++;

            // --- Hàng 4: Header ---
            String[] columns = {"Mã SP", "Tên sản phẩm", "Loại hàng", "Giá", "Số lượng tồn", "Đơn vị"};
            Row headerRow = sheet.createRow(rowIdx++);
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // --- Dữ liệu từ hàng 5 ---
            for (Products p : productList) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(p.getProduct_id());
                row.createCell(1).setCellValue(p.getProduct_name());
                row.createCell(2).setCellValue(p.getCategoryName());
                row.createCell(3).setCellValue(p.getPrice());
                row.createCell(4).setCellValue(p.getStock_quantity());
                row.createCell(5).setCellValue(p.getUnit());

                for (int i = 0; i <= 5; i++) {
                    row.getCell(i).setCellStyle(dataStyle);
                }
            }

            // --- Auto size ---
            sheet.setColumnWidth(1, 10000); // Tên sản phẩm
            for (int i = 0; i < columns.length; i++) {
                if (i != 1) {
                    sheet.autoSizeColumn(i);
                }
            }

            // --- Ghi ra file và mở ---
            try (FileOutputStream fos = new FileOutputStream(file)) {
                workbook.write(fos);
            }
            workbook.close();

            Desktop.getDesktop().open(file);
            JOptionPane.showMessageDialog(importsView, "Xuất Excel thành công!");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi xuất Excel: " + e.getMessage());
        }
    }

}
