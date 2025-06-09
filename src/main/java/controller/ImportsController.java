/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.ImportsDAO;
import java.sql.Connection;
import java.sql.SQLDataException;
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
//    private Map<String, String> categoryMap;
//    private Map<String, String> supplierMap;
//    private Map<String, String> employeeMap;

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
        String employeeName = importsView.getEmployeeComboBoxSelection();
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
        
        if (employeeName == null || employeeName.equals("--Chọn nhân viên")) {
            throw new IllegalArgumentException("Vui lòng chọn một nhân viên hợp lệ!");
        }
        Integer employeeId = employeeMap.get(employeeName);
        
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
            //int nccId = Integer.parseInt(NCCNhap);
            //int loaiHangId = Integer.parseInt(LoaiHangNhap);
            //int nhanVienId = Integer.parseInt(NhanVienNhap);
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
//        } catch (NumberFormatException e) {
//            JOptionPane.showMessageDialog(importsView, "Số lượng, giá nhập, hoặc ID phải là số hợp lệ!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(importsView, "Lỗi khi xử lý: " + e.getMessage());
        }
    }
}
