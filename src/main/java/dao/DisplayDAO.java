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
import java.util.ArrayList;
import java.util.List;
import model.Products;

/**
 *
 * @author Admin
 */
public class DisplayDAO {
    private Connection connection;

    public DisplayDAO() {
    }

    public DisplayDAO(Connection connection) {
        this.connection = connection;
    }
    
    public boolean insertDisplay(int product_id, String row, String floor, Date start_date, Date end_date){
        try {
            String insertDisplaySql = "INSERT INTO productdisplay(product_id, row, floor, start_date, end_date) VALUES (?,?,?,?,?)";
            PreparedStatement importPs = connection.prepareStatement(insertDisplaySql);
            importPs.setInt(1, product_id);
            importPs.setString(2, row);
            importPs.setString(3, floor);
            importPs.setDate(4, start_date);
            importPs.setDate(5, end_date);
            int affectedRows = importPs.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteDisplay(int product_id){
        try {
            String deleteDisplaySql = "DELETE FROM productdisplay WHERE product_id = ?";
            PreparedStatement importPs = connection.prepareStatement(deleteDisplaySql);
            importPs.setInt(1, product_id);
            int affectedRows = importPs.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateDisplay(int product_id, String row, String floor, Date start_date, Date end_date){
        try {
            String updateDisplaySql = "UPDATE productdisplay SET row = ?, floor = ?, start_date = ?, end_date = ? WHERE product_id = ?";
            PreparedStatement updateStoreProductPs = connection.prepareStatement(updateDisplaySql);
            updateStoreProductPs.setString(1, row);
            updateStoreProductPs.setString(2, floor);
            updateStoreProductPs.setDate(3, start_date);
            updateStoreProductPs.setDate(4, end_date);
            updateStoreProductPs.setInt(5, product_id);
            updateStoreProductPs.executeUpdate();

            int affectedRows = updateStoreProductPs.executeUpdate();
            return affectedRows > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Products> searchProduct(Integer categoryId, Integer supplierId, String productName) {
        List<Products> productList = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT DISTINCT p.product_id, p.product_name, c.category_name, p.price, p.stock_quantity, p.unit "
                + "FROM products p "
                + "JOIN imports i ON p.product_id = i.product_id "
                + "JOIN category c ON p.category_id = c.category_id "
                + "WHERE 1=1");

        // Thêm điều kiện lọc nếu có
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND p.category_id = ").append(categoryId);
        }
        if (supplierId != null && supplierId > 0) {
            sql.append(" AND i.supplier_id = ").append(supplierId);
        }
        if (productName != null && !productName.trim().isEmpty()) {
            sql.append(" AND p.product_name LIKE ?");
        }

        try {
            PreparedStatement stmt = connection.prepareStatement(sql.toString());

            if (productName != null && !productName.trim().isEmpty()) {
                stmt.setString(1, "%" + productName + "%");
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Products p = new Products();
                p.setProduct_id(rs.getInt("product_id"));
                p.setProduct_name(rs.getString("product_name"));
                productList.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productList;
    }
}
