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
import model.Employees;

/**
 *
 * @author macbook
 */
public class EmployeeDAO {
    //Cac truy van toi database
    private Connection conn;
    
    public EmployeeDAO() throws SQLException{
        conn = DBConnection.getConnection();
    }
    
    // Trong EmployeeDAO
public int getEmployeeIdByName(String name) throws SQLException {
    String sql = "SELECT employee_id FROM employees WHERE full_name = ?";
    PreparedStatement ps = conn.prepareStatement(sql);
    ps.setString(1, name);
    ResultSet rs = ps.executeQuery();
    if (rs.next()) {
        return rs.getInt("employee_id");
    }
    return -1;
}

    
    public List<Employees> getAllEmployees() {
    List<Employees> list = new ArrayList<>();
    try {
        String sql = "SELECT * FROM employees";
        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Employees emp = new Employees();
            emp.setEmployee_id(rs.getInt("employee_id"));
            emp.setEmployee_name(rs.getString("employee_name"));
            emp.setPassword(rs.getString("password"));
            emp.setFull_name(rs.getString("full_name"));
            emp.setRole(rs.getInt("role"));
            list.add(emp);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

    public boolean register(Employees employees){
        try {
            String sql = "INSERT INTO `employees`(`employee_id`, `employee_name`, `password`, `full_name`, 'role') VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, employees.getEmployee_id());
            ps.setString(2, employees.getEmployee_name());
            ps.setString(3, employees.getPassword());
            ps.setString(4, employees.getFull_name());
            ps.setInt(5, employees.getRole());
            int result = ps.executeUpdate();
            return result>0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean login(Employees employees){
        try {
            String sql = "SELECT * FROM employees WHERE employee_name = ? AND password = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, employees.getEmployee_name());
            ps.setString(2, employees.getPassword());
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
