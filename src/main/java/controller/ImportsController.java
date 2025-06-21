/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.toedter.calendar.JDateChooser;
import dao.ImportsDAO;
import java.io.File;
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
            throw new IllegalArgumentException("Vui lòng chọn một nhà cung cấp hợp lệ!");
        }
        Integer supplierId = supplierMap.get(supplierName); // Lấy ID từ supplierMap

        if (categoryName == null || categoryName.equals("--Chọn loại hàng")) {
            throw new IllegalArgumentException("Vui lòng chọn một loại hàng hợp lệ!");
        }
        Integer categoryId = categoryMap.get(categoryName);

        if (fullname == null || fullname.equals("--Chọn nhân viên")) {
            throw new IllegalArgumentException("Vui lòng chọn một nhân viên hợp lệ!");
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
            int soLuong = Integer.parseInt(soLuongNhap);
            int gia = Integer.parseInt(giaNhap);
            java.sql.Date ngayNhap = new java.sql.Date(utilDate.getTime());

            // Gọi phương thức insertOrUpdateImport từ ImportsDAO
            boolean success = importsDAO.insertOrUpdateImport(tenSPNhap, categoryId, donViNhap, soLuong, gia,
                    ngayNhap, supplierId, employeeId);

            if (success) {
                JOptionPane.showMessageDialog(importsView, "Thêm nhập hàng thành công!");
                //showAllImports(); // Cập nhật danh sách sau khi thêm
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
                // importsView.loadImportsTable(); // hoặc gọi hàm để reload bảng nếu bạn có
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
}
