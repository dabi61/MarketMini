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

    public boolean insertImport(Imports importRecord) {
        String sql = "INSERT INTO imports (product_id, supplier_id, quantity, import_price, import_date, employee_id) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, importRecord.getProduct_id());
            stmt.setInt(2, importRecord.getSupplier_id());
            stmt.setInt(3, importRecord.getQuantity());
            stmt.setInt(4, importRecord.getImport_price());
            stmt.setDate(5, Date.valueOf(importRecord.getImport_date()));
            stmt.setInt(6, importRecord.getEmployee_id());
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all imports
    public List<Imports> getAllImports() {
        List<Imports> importsList = new ArrayList<>();
        String sql = "SELECT * FROM imports";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
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
}
