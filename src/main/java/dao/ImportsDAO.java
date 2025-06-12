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

    // Get all imports
    public List<Imports> getAllImports() {
        List<Imports> importsList = new ArrayList<>();
        String sql = "SELECT * FROM imports";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Imports imp = new Imports();
                imp.setImport_id(rs.getInt("import_id"));
                imp.setProduct_id(rs.getInt("product_id"));
                imp.setSupplier_id(rs.getInt("supplier_id"));
                imp.setQuantity(rs.getInt("quantity"));
                imp.setImport_price(rs.getInt("import_price"));
                imp.setImport_date(rs.getDate("import_date").toLocalDate());
                imp.setEmployee_id(rs.getInt("employee_id"));
                importsList.add(imp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return importsList;
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
}
