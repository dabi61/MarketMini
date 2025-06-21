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
import model.DBConnection;

/**
 *
 * @author THIS PC
 */
public class ProductDAO {
    private Connection conn;
    public ProductDAO() throws SQLException {
        conn = DBConnection.getConnection();
    }
   
public int getPriceByProductId(int productId) throws SQLException {
    String sql = "SELECT price FROM products WHERE product_id = ?";
    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, productId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) return rs.getInt("price");
    }
    throw new SQLException("Không tìm thấy giá cho sản phẩm ID: " + productId);
}
    public int getProductIdByName(String productName) {
    int productId = -1;
    String sql = "SELECT product_id FROM products WHERE product_name = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, productName);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            productId = rs.getInt("product_id");
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return productId;
}
    public List<Object[]> getAllProducts() {
    List<Object[]> productList = new ArrayList<>();
    String sql = "SELECT product_id, product_name FROM products ORDER BY product_name";

    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            productList.add(new Object[]{rs.getInt("product_id"), rs.getString("product_name")});
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return productList;
}
}
