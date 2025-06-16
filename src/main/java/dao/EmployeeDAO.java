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
import model.Employees;

/**
 *
 * @author macbook
 */
public class EmployeeDAO {

    //Cac truy van toi database
    private Connection con;

    public EmployeeDAO() throws SQLException {
        con = DBConnection.getConnection();
    }

    public boolean register(Employees employees) {
        try {
            String sql = "INSERT INTO `employees`(`employee_id`, `employee_name`, `password`, `full_name`, 'role') VALUES (?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, employees.getEmployee_id());
            ps.setString(2, employees.getEmployee_name());
            ps.setString(3, employees.getPassword());
            ps.setString(4, employees.getFull_name());
            ps.setInt(5, employees.getRole());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

//    public boolean login(Employees employees){
//        try {
//            String sql = "SELECT * FROM employees WHERE employee_name = ? AND password = ?";
//            PreparedStatement ps = conn.prepareStatement(sql);
//            ps.setString(1, employees.getEmployee_name());
//            ps.setString(2, employees.getPassword());
//            ResultSet rs = ps.executeQuery();
//            return rs.next();
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
    public Employees login(Employees employee) {
        try {
            String sql = "SELECT * FROM employees WHERE employee_name = ? AND password = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, employee.getEmployee_name());
            ps.setString(2, employee.getPassword());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Employees loggedInEmployee = new Employees(
                        rs.getInt("employee_id"),
                        rs.getString("employee_name"),
                        rs.getString("password"),
                        rs.getString("full_name"),
                        rs.getInt("role"),
                        rs.getString("phone"),
                        rs.getString("email")
                );
                rs.close();
                ps.close();
                return loggedInEmployee;
            }
            rs.close();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
