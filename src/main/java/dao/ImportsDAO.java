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
import model.Imports;
import model.Products;

/**
 *
 * @author Admin
 */
public class ImportsDAO {

    private Connection connection;

    public ImportsDAO() {
    }

    public ImportsDAO(Connection connection) {
        this.connection = connection;
    }

    public boolean insertOrUpdateImport(String productName, int categoryId, String unit, int quantity, int importPrice,
            Date importDate, int supplierId, int employeeId) {
        try {
            // Kiểm tra sản phẩm đã tồn tại chưa
            String checkProductSql = "SELECT product_id, stock_quantity FROM products WHERE product_name = ?";
            PreparedStatement checkPs = connection.prepareStatement(checkProductSql);
            checkPs.setString(1, productName);
            ResultSet rs = checkPs.executeQuery();

            int productId;
            if (rs.next()) {
                // Sản phẩm đã tồn tại, cập nhật số lượng
                productId = rs.getInt("product_id");
                int currentStock = rs.getInt("stock_quantity");
                String updateProductSql = "UPDATE products SET stock_quantity = ? WHERE product_id = ?";
                PreparedStatement updatePs = connection.prepareStatement(updateProductSql);
                updatePs.setInt(1, currentStock + quantity);
                updatePs.setInt(2, productId);
                updatePs.executeUpdate();
            } else {
                // Sản phẩm chưa tồn tại, chèn mới
                String insertProductSql = "INSERT INTO products (product_name, category_id, unit, stock_quantity, price) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement insertPs = connection.prepareStatement(insertProductSql, PreparedStatement.RETURN_GENERATED_KEYS);
                insertPs.setString(1, productName);
                insertPs.setInt(2, categoryId);
                insertPs.setString(3, unit);
                insertPs.setInt(4, quantity);
                insertPs.setInt(5, importPrice);
                insertPs.executeUpdate();

                // Lấy product_id vừa chèn
                rs = insertPs.getGeneratedKeys();
                if (rs.next()) {
                    productId = rs.getInt(1);
                } else {
                    throw new SQLException("Không thể lấy product_id sau khi chèn.");
                }
            }

            // Chèn vào bảng imports
            String insertImportSql = "INSERT INTO imports (product_id, supplier_id, quantity, import_price, import_date, employee_id) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement importPs = connection.prepareStatement(insertImportSql);
            importPs.setInt(1, productId);
            importPs.setInt(2, supplierId);
            importPs.setInt(3, quantity);
            importPs.setInt(4, importPrice);
            importPs.setDate(5, importDate);
            importPs.setInt(6, employeeId);
            int affectedRows = importPs.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteImport(int import_id) {
        String deleteImportSql = "DELETE FROM imports WHERE import_id = ?";
        try {
            PreparedStatement importPs = connection.prepareStatement(deleteImportSql);
            importPs.setInt(1, import_id);

            int rowsAffected = importPs.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateImport(int importId, String productName, int categoryId, String unit, int newQuantity,
            int importPrice, Date importDate, int supplierId, int employeeId) {
        try {
            // 1. Lấy product_id từ tên sản phẩm, và lấy hàng tồn ban đầu
            String getProductSql = "SELECT product_id, stock_quantity FROM products WHERE product_name = ?";
            PreparedStatement getProductPs = connection.prepareStatement(getProductSql);
            getProductPs.setString(1, productName);
            ResultSet rs = getProductPs.executeQuery();

            int productId;
            int currentStock;
            if (rs.next()) {
                productId = rs.getInt("product_id");
                currentStock = rs.getInt("stock_quantity");
            } else {
                throw new SQLException("Không tìm thấy sản phẩm.");
            }

            // 2. Lấy quantity cũ từ bảng imports
            String getOldImportSql = "SELECT quantity FROM imports WHERE import_id = ?";
            PreparedStatement getOldImportPs = connection.prepareStatement(getOldImportSql);
            getOldImportPs.setInt(1, importId);
            ResultSet rsOldImport = getOldImportPs.executeQuery();

            int oldQuantity;
            if (rsOldImport.next()) {
                oldQuantity = rsOldImport.getInt("quantity");
            } else {
                throw new SQLException("Không tìm thấy bản ghi nhập hàng.");
            }

            // 3. Tính lại số lượng tồn kho
            int updatedStock = currentStock - oldQuantity + newQuantity;

            // 4. Cập nhật bảng products
            String updateProductSql = "UPDATE products SET category_id = ?, unit = ?, stock_quantity = ?, price = ? WHERE product_id = ?";
            PreparedStatement updateProductPs = connection.prepareStatement(updateProductSql);
            updateProductPs.setInt(1, categoryId);
            updateProductPs.setString(2, unit);
            updateProductPs.setInt(3, updatedStock);
            updateProductPs.setInt(4, importPrice);
            updateProductPs.setInt(5, productId);
            updateProductPs.executeUpdate();

            // 5. Cập nhật bảng imports
            String updateImportSql = "UPDATE imports SET supplier_id = ?, quantity = ?, import_price = ?, import_date = ?, employee_id = ? WHERE import_id = ?";
            PreparedStatement updateImportPs = connection.prepareStatement(updateImportSql);
            updateImportPs.setInt(1, supplierId);
            updateImportPs.setInt(2, newQuantity);
            updateImportPs.setInt(3, importPrice);
            updateImportPs.setDate(4, importDate);
            updateImportPs.setInt(5, employeeId);
            updateImportPs.setInt(6, importId);

            int affectedRows = updateImportPs.executeUpdate();

            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Phần quản lý kho
    public boolean deleteProduct(int product_id) {
        String deleteProductSql = "DELETE FROM products WHERE product_id = ?";
        try {
            PreparedStatement importPs = connection.prepareStatement(deleteProductSql);
            importPs.setInt(1, product_id);

            int rowsAffected = importPs.executeUpdate();
            return rowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateProduct(int product_id, String productName, int categoryId, int price, String unit) {
        try {
            String updateProductSql = "UPDATE products SET product_name = ?, category_id = ?, price = ?, unit = ? WHERE product_id = ?";
            PreparedStatement updateStoreProductPs = connection.prepareStatement(updateProductSql);
            updateStoreProductPs.setString(1, productName);
            updateStoreProductPs.setInt(2, categoryId);
            updateStoreProductPs.setInt(3, price);
            updateStoreProductPs.setString(4, unit);
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

}
