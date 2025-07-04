/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.SalesDAO;
import java.sql.Connection;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import model.Customers;
import model.Products;
import view.SalesForm;

public class SalesController {

    private SalesDAO salesDAO;
    private SalesForm salesView;
    private Connection connection;

    public SalesController() {
    }

    public SalesController(SalesDAO salesDAO, SalesForm salesView) {
        this.salesDAO = salesDAO;
        this.salesView = salesView;
        this.connection = connection;
        salesDAO = new SalesDAO(connection);
    }

    public void addCustomer() {
        String fullName = salesView.getTxtThemTenKhach().getText().trim();
        String phoneNumber = salesView.getTxtThemSdtKhach().getText().trim();

        if (fullName.isEmpty() || phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(salesView, "Vui lòng nhập đầy đủ thông tin.");
            return;
        }

        if (!phoneNumber.matches("\\d+")) {
            JOptionPane.showMessageDialog(salesView, "Số điện thoại chỉ được chứa chữ số.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customers customer = new Customers(phoneNumber, fullName);
        boolean success = salesDAO.addCustomer(customer);

        if (success) {
            JOptionPane.showMessageDialog(salesView, "Thêm khách hàng thành công!");
        } else {
            JOptionPane.showMessageDialog(salesView, "Thêm khách hàng thất bại.");
        }
    }

    public void searchCustomers(String searchText) {
        try {
            DefaultTableModel tblModel = (DefaultTableModel) salesView.getTblSuggestions().getModel();
            tblModel.setRowCount(0); // Xóa dữ liệu cũ

            if (searchText.trim().isEmpty()) {
                salesView.getPopupMenu().setVisible(false);
                return;
            }

            List<Customers> customers = salesDAO.searchCustomers(searchText);
            if (customers.isEmpty()) {
                salesView.getPopupMenu().setVisible(false);
            } else {
                for (Customers customer : customers) {
                    tblModel.addRow(new Object[]{
                        customer.getCustomer_id(),
                        customer.getFull_name(),
                        customer.getPhone_number()
                    });
                }
                salesView.getPopupMenu().show(salesView.getTxtTimKiemKhachHang(), 0, salesView.getTxtTimKiemKhachHang().getHeight());
            }
        } catch (Exception e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(salesView, "Lỗi tìm kiếm khách hàng: " + e.getMessage());
        }
    }

    public void searchProduct() {
        try {
            // Lấy dữ liệu người dùng chọn trên giao diện
            String productName = salesView.getTxtTimSP().getText().trim();

            // Gọi DAO
            List<Products> results = salesDAO.searchProduct(productName);

            // Hiển thị kết quả tìm kiếm lên bảng
            salesView.loadDuLieuProduct1(results);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(salesView, "Lỗi khi tìm kiếm: " + e.getMessage());
        }
    }

    // Phần add point khi mua hàng của khách chưa hoàn thiện
    // gợi ý: bảng order thêm 1 trường là tiền khách phải trả để lưu, tránh gây xung đột giữa tổng tiền hàng và tiền thực nhận
    public void insertOrder() {
        try {
            // Lấy dữ liệu từ form
            int employeeId = salesView.getEmployeeId();
            // Lấy ngày hiện tại theo múi giờ Việt Nam
            ZoneId zoneId = ZoneId.of("Asia/Ho_Chi_Minh");
            LocalDate localDate = LocalDate.now(zoneId);
            Date orderDate = Date.valueOf(localDate); // java.sql.Date
            String totalAmountStr = salesView.getTxtTongTienHang().getText().trim();
            int totalAmount = Integer.parseInt(totalAmountStr);
            String customerName = salesView.getTxtTimKiemKhachHang().getText().trim();
            int customerId = salesDAO.getCustomerIdByName(customerName);
            String pointStr = salesView.getTxtPoint().getText().trim();
            int point = Integer.parseInt(pointStr);

            if (customerId == -1) {
                JOptionPane.showMessageDialog(salesView, "Không tìm thấy khách hàng: " + customerName);
                return;
            }

            String finalAmountStr = salesView.getTxtKhachCanTra().getText().trim();
            int finalAmount = Integer.parseInt(finalAmountStr);

            // 1. Chèn đơn hàng vào DB
            int orderId = salesDAO.insertOrder(employeeId, orderDate, totalAmount, customerId, finalAmount);
            if (orderId == -1) {
                JOptionPane.showMessageDialog(salesView, "Không thể tạo đơn hàng!");
                return;
            }
            // 2. Tính điểm sau khi giảm giá(nếu có)
            if (salesView.getChkDungPoint().isSelected()) {
                int usedPoints = point;

                boolean updated = salesDAO.subtractCustomerPoints(customerId, usedPoints);
                if (!updated) {
                    JOptionPane.showMessageDialog(salesView, "Lỗi khi trừ điểm khách hàng.");
                    return;
                }
            }

            // 3. Lấy dữ liệu sản phẩm từ bảng JTable
            DefaultTableModel tableModel = (DefaultTableModel) salesView.getTblDonHangView().getModel();
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int productId = Integer.parseInt(tableModel.getValueAt(i, 0).toString()); // Cột product_id
                int quantity = Integer.parseInt(tableModel.getValueAt(i, 3).toString()); // Cột quantity
                int unitPrice = Integer.parseInt(tableModel.getValueAt(i, 2).toString()); // Cột unit price

                boolean success = salesDAO.insertOrderDetail(orderId, productId, quantity, unitPrice);
                if (!success) {
                    JOptionPane.showMessageDialog(salesView, "Lỗi khi lưu chi tiết sản phẩm.");
                    return;
                }
                // Cập nhật tồn kho - trigger đã trừ
                
//                boolean stockUpdated = salesDAO.updateProductStock(productId, quantity);
//                if (!stockUpdated) {
//                    JOptionPane.showMessageDialog(salesView, "Lỗi cập nhật tồn kho. Sản phẩm có thể không đủ số lượng.");
//                    return;
//                }
            }

            // 4. Tích điểm point cho khách hàng
            int earnedPoints = (int) (totalAmount * 0.02);
            int updatedPoints;
            if (salesView.getChkDungPoint().isSelected()) {
                updatedPoints = earnedPoints;
            } else {
                updatedPoints = point + earnedPoints;
            }
            salesDAO.updateCustomerPoint(customerId, updatedPoints);

            JOptionPane.showMessageDialog(salesView, "Thanh toán thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(salesView, "Lỗi tạo đơn hàng: " + e.getMessage());
        }

    }
}
