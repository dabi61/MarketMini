/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.DBConnection;

/**
 *
 * @author THIS PC
 */
public class CustomerDAO {
    private Connection conn;
    
    public CustomerDAO() throws SQLException{
        conn = DBConnection.getConnection();
    }
    
    // Trong CustomerDAO
public int getCustomerIdByName(String name) throws SQLException {
    String sql = "SELECT customer_id FROM customers WHERE full_name = ?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, name);
    ResultSet rs = ps.executeQuery();
    if (rs.next()) {
        return rs.getInt("customer_id");
    }
    return -1;
}
public int getCustomerIdByPhone(String phone) {
    try {
        String sql = "SELECT customer_id FROM customers WHERE phone_number = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, phone);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt("customer_id");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1; // không tìm thấy
}


}
