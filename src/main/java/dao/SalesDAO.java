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

}
