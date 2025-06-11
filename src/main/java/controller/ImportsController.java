/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ImportsDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.Map;
import javax.swing.JOptionPane;
import model.DBConnection;
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
}
