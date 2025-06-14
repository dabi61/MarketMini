/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.SalesDAO;
import java.sql.Connection;
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
}
