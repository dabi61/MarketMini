/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Customers;
import model.DBConnection;
import model.Orders;
import model.Products;

/**
 *
 * @author Admin
 */
public class SalesDAO {

    private Connection con;

    public SalesDAO() {
    }

    public SalesDAO(Connection con) {
        this.con = con;
    }

    public boolean addCustomer(Customers customers) {
        String sql = "INSERT INTO customers (full_name, phone_number) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, customers.getFull_name());
            stmt.setString(2, customers.getPhone_number());

            int rowsInserted = stmt.executeUpdate();
            return rowsInserted > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Customers> searchCustomers(String searchText) throws SQLException {
        List<Customers> customers = new ArrayList<>();
        String sql = "SELECT customer_id, full_name, phone_number FROM customers WHERE full_name LIKE ? OR phone_number LIKE ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, "%" + searchText + "%");
        ps.setString(2, "%" + searchText + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            customers.add(new Customers(
                    rs.getInt("customer_id"),
                    rs.getString("full_name"),
                    rs.getString("phone_number")
            ));
        }

        rs.close();
        ps.close();
        return customers;
    }

    public List<Products> searchProduct(String productName) {
        List<Products> productList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT p.product_id, p.product_name, c.category_name, p.price, p.stock_quantity, p.unit "
                + "FROM products p "
                + "JOIN imports i ON p.product_id = i.product_id "
                + "JOIN category c ON p.category_id = c.category_id "
                + "WHERE 1=1");

        // Thêm điều kiện lọc nếu có
        if (productName != null && !productName.trim().isEmpty()) {
            sql.append(" AND p.product_name LIKE ?");
        }

        try {
            PreparedStatement stmt = con.prepareStatement(sql.toString());

            if (productName != null && !productName.trim().isEmpty()) {
                stmt.setString(1, "%" + productName + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Products p = new Products();
                p.setProduct_id(rs.getInt("product_id"));
                p.setProduct_name(rs.getString("product_name"));
                p.setCategoryName(rs.getString("category_name"));
                p.setPrice(rs.getInt("price"));
                p.setUnit(rs.getString("unit"));
                p.setStock_quantity(rs.getInt("stock_quantity"));
                productList.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }

    public int insertOrder(int employeeId, Date orderDate, int totalAmount, int customerId, int finalAmount) {
        try {
            String insertOrderSql = "INSERT INTO orders (employee_id, order_date, total_amount, customer_id, final_amount) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement orderPs = con.prepareStatement(insertOrderSql, Statement.RETURN_GENERATED_KEYS);
            orderPs.setInt(1, employeeId);
            orderPs.setDate(2, orderDate);
            orderPs.setInt(3, totalAmount);
            orderPs.setInt(4, customerId);
            orderPs.setInt(5, finalAmount);

            int result = orderPs.executeUpdate();
            if (result > 0) {
                ResultSet rs = orderPs.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1); // Trả về order_id
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Lỗi
    }

    public boolean insertOrderDetail(int orderId, int productId, int quantity, int unitPrice) {
        try {
            String insertDetailSql = "INSERT INTO orderdetails (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(insertDetailSql);
            ps.setInt(1, orderId);
            ps.setInt(2, productId);
            ps.setInt(3, quantity);
            ps.setInt(4, unitPrice);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProductStock(int productId, int quantitySold) {
        String sql = "UPDATE products SET stock_quantity = stock_quantity - ? WHERE product_id = ? AND stock_quantity >= ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, quantitySold);
            ps.setInt(2, productId);
            ps.setInt(3, quantitySold); // tránh trừ khi không đủ hàng

            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getCustomerIdByName(String customerName) {
        String sql = "SELECT customer_id FROM customers WHERE full_name = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, customerName);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt("customer_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Trả về -1 nếu không tìm thấy hoặc có lỗi
    }

    public boolean updateCustomerPoint(int customerId, int newPoint) {
        String sql = "UPDATE customers SET points = ? WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newPoint);
            stmt.setInt(2, customerId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean subtractCustomerPoints(int customerId, int pointsUsed) {
        String sql = "UPDATE customers SET points = points - ? WHERE customer_id = ?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, pointsUsed);
            stmt.setInt(2, customerId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
