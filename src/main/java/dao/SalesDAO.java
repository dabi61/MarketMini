/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.Customers;
import model.DBConnection;
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

//    public List<Products> addProductToCart(String productName){
//        List<Products> productList = new ArrayList<>();
//        try {
//            
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return productList;
//    }
}
