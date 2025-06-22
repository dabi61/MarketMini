/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.DisplayDAO;
import java.sql.Connection;
import java.sql.SQLDataException;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import model.Products;
import view.DisplayForm;

/**
 *
 * @author Admin
 */
public class DisplayController {

    private DisplayDAO displayDAO;
    private DisplayForm displayView;
    private Connection connection;

    public DisplayController(DisplayDAO displayDAO, DisplayForm displayView) throws SQLDataException {
        this.displayDAO = displayDAO;
        this.displayView = displayView;
        this.connection = connection;
        displayDAO = new DisplayDAO(connection);
    }

    public void searchProduct() {
        try {
            // Lấy dữ liệu người dùng chọn trên giao diện
            String productName = displayView.getTxtTenSPTim().getText().trim();

            String categoryName = displayView.getCategorySearchComboBox();
            String supplierName = displayView.getSupplierSearchComboBox();

            Map<String, Integer> categoryMap = displayView.getCategoryMap();
            Map<String, Integer> supplierMap = displayView.getSupplierMap();

            Integer categoryId = null;
            Integer supplierId = null;

            if (categoryName != null && !categoryName.equals("--Chọn loại hàng")) {
                categoryId = categoryMap.get(categoryName);
            }

            if (supplierName != null && !supplierName.equals("--Chọn nhà cung cấp")) {
                supplierId = supplierMap.get(supplierName);
            }

            // Gọi DAO
            List<Products> results = displayDAO.searchProduct(categoryId, supplierId, productName);

            // Hiển thị kết quả tìm kiếm lên bảng
            displayView.loadDuLieuKho1(results);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(displayView, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    public void addDisplay() {
        String maSPStr = displayView.getTxtMaSP().getText().trim();
        int maSP = Integer.parseInt(maSPStr);
        String tenSP = displayView.getTxtTenSP().getText().trim();
        String day = displayView.getTxtDay().getText().trim();
        String tang = displayView.getTxtTang().getText().trim();
        java.util.Date fromDate = displayView.getjDateNgayBatDau().getDate();
        java.util.Date toDate = displayView.getjDateNgayKetThuc().getDate();

        // Kiểm tra dữ liệu đầu vào
        if (tenSP.isEmpty() || day.isEmpty() || tang.isEmpty()) {
            JOptionPane.showMessageDialog(displayView, "Vui lòng điền đầy đủ thông tin", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(displayView, "Vui lòng chọn ngày", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            java.sql.Date tuNgay = new java.sql.Date(fromDate.getTime());
            java.sql.Date denNgay = new java.sql.Date(toDate.getTime());
            // Gọi phương thức insertOrUpdateImport từ ImportsDAO
            boolean success = displayDAO.insertDisplay(maSP, day, tang, tuNgay, denNgay);

            if (success) {
                JOptionPane.showMessageDialog(displayView, "Thêm nhập hàng thành công!");
                displayView.setMacDinh();
            } else {
                JOptionPane.showMessageDialog(displayView, "Thêm nhập hàng thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateDisplay() {
        int selectedRow = displayView.getTblDisplayView().getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(displayView, "Vui lòng chọn một dòng để cập nhật.");
            return;
        }

        try {
            int productId = Integer.parseInt(displayView.getTblDisplayView().getValueAt(selectedRow, 1).toString());

            // Lấy dữ liệu từ form
            String day = displayView.getTxtDay().getText().trim();
            String tang = displayView.getTxtTang().getText().trim();
            java.util.Date fromDate = displayView.getjDateNgayBatDau().getDate();
            java.util.Date toDate = displayView.getjDateNgayKetThuc().getDate();
            
            // Kiểm tra rỗng
            if (day.isEmpty() || tang.isEmpty()) {
                JOptionPane.showMessageDialog(displayView, "Vui lòng điền đầy đủ thông tin cần cập nhật.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            if (fromDate == null || toDate == null) {
                JOptionPane.showMessageDialog(displayView, "Vui lòng điền đầy đủ ngày.", "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
                return;
            }

            java.sql.Date tuNgay = new java.sql.Date(fromDate.getTime());
            java.sql.Date denNgay = new java.sql.Date(toDate.getTime());

            // Gọi phương thức DAO để cập nhật
            boolean success = displayDAO.updateDisplay(
                    productId, day, tang, tuNgay, denNgay
            );

            if (success) {
                JOptionPane.showMessageDialog(displayView, "Cập nhật đơn nhập thành công!");
                displayView.setMacDinh();
            } else {
                JOptionPane.showMessageDialog(displayView, "Cập nhật thất bại. Kiểm tra lại dữ liệu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(displayView, "Lỗi khi cập nhật: " + e.getMessage());
        }
    }

    public void deleteDisplay() {
        int selectedRow = displayView.getTblDisplayView().getSelectedRow();

        if (selectedRow >= 0) {
            // Lấy product_id từ dòng được chọn
            int productId = Integer.parseInt(displayView.getTblDisplayView().getValueAt(selectedRow, 1).toString());

            // Xác nhận xóa
            int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa bản ghi này không?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = displayDAO.deleteDisplay(productId); // Gọi DAO để xóa

                if (success) {
                    JOptionPane.showMessageDialog(null, "Xóa thành công!");
                } else {
                    JOptionPane.showMessageDialog(null, "Không tìm thấy bản ghi để xóa hoặc xóa thất bại.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Vui lòng chọn một dòng để xóa.");
        }
    }
}
