/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.DBConnection;
import model.Employees;

/**
 *
 * @author macbook
 */
public class EmployeeDAO {

    //Cac truy van toi database
    private Connection conn;

    public EmployeeDAO() throws SQLException {
        conn = DBConnection.getConnection();
    }

    public boolean register(Employees employees) {
        try {
            String sql = "INSERT INTO `employees`(`employee_id`, `employee_name`, `password`, `full_name`, 'role') VALUES (?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
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
            PreparedStatement ps = conn.prepareStatement(sql);
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
    public void employee_insert(Employees employee){
            try {
                // kết nối db
                conn = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "Insert into employees Values(?,?,?,?,?,?,?,?,?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setNull(1, java.sql.Types.INTEGER);
                ps.setString(2,employee.getEmployee_name());
                ps.setString(3,employee.getPassword());
                ps.setString(4,employee.getFull_name());
                ps.setString(5,employee.getSex());
                ps.setInt(6,employee.getRole());
                ps.setString(7,employee.getPhone());
                ps.setString(8,employee.getEmail());
                java.sql.Date sqlDate = new java.sql.Date(employee.getDate().getTime());
                ps.setDate(9, sqlDate);
                ps.executeUpdate();
                conn.close();
            } catch (SQLException ex) {}
    }
     public void employee_update(Employees employee){
            try {
                // kết nối db
                conn = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "Update employees Set employee_name=?,password=?,full_name=?,sex=?,role=?, phone=? , email=?, date=? where employee_id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1,employee.getEmployee_name());
                ps.setString(2,employee.getPassword());
                ps.setString(3,employee.getFull_name());
                ps.setString(4,employee.getSex());
                ps.setInt(5,employee.getRole());
                ps.setString(6,employee.getPhone());
                ps.setString(7,employee.getEmail());
                java.sql.Date sqlDate = new java.sql.Date(employee.getDate().getTime());
                ps.setDate(8, sqlDate);
                ps.setInt(9,employee.getEmployee_id());
                ps.executeUpdate();
                conn.close();
            } catch (SQLException ex) {}
    }
    public void employee_delete(Employees employee){
        try {
            // kết nối db
            conn = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "delete from employees where employee_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1,employee.getEmployee_id());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {}
    } 
    public void employeefind(JTable tbBang){
        try {
            // kết nối db
            conn = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "select * from employees";
            PreparedStatement ps = conn.prepareStatement(sql);        
            ResultSet rs = ps.executeQuery();
            tbBang.removeAll();
            String[] head= {"Mã nhân viên","Tên nhân viên","Password","Họ và Tên","Giới Tính","Chức vụ","Phone","Email","Ngày thêm"};
            DefaultTableModel tb = new DefaultTableModel(head,0);
            while(rs.next()){
                Vector vt = new Vector();
                vt.add(rs.getInt("employee_id"));
                vt.add(rs.getString("employee_name"));
                vt.add(rs.getString("password"));
                vt.add(rs.getString("full_name"));
                vt.add(rs.getString("sex"));
                vt.add(MapRole(rs.getInt("role")));
                vt.add(rs.getString("phone"));
                vt.add(rs.getString("email"));
                vt.add(rs.getDate("date"));
                tb.addRow(vt);
            }
            tbBang.setModel(tb);
            conn.close();
        } catch (SQLException ex) {}
    }
     public ResultSet timKiem(String tieuChi, String txtTimKiem) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT *FROM employees";

            if (!tieuChi.equals("Tất cả") && txtTimKiem != null && !txtTimKiem.isEmpty()) {
                switch (tieuChi) {                    
                    case "Tên":
                        sql += " WHERE employee_name LIKE ?";
                        break;                   
                    case "Chức vụ":
                        sql += " WHERE role LIKE ?";
                        break;
                    case "Ngày thêm":
                        sql += " WHERE date =  ? ";
                        break;
                    case "Số điện thoại":
                        sql += " WHERE phone LIKE ?";
                        break;
                }
            }
            PreparedStatement st = con.prepareStatement(sql);
            if (!tieuChi.equals("Tất cả") && txtTimKiem != null && !txtTimKiem.isEmpty()) {
                if(tieuChi.equals("Chức vụ")){
                  st.setString(1, "%" + MapToRole(txtTimKiem) + "%");
                }else if(tieuChi.equals("Ngày thêm")){
                    st.setString(1, txtTimKiem);
                }else
                    st.setString(1, "%" + txtTimKiem + "%");
            }
            return st.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        }
        
    public int employeeIdMax(){
            try {
                // kết nối db
                int maxId = -1;
                conn = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "select max(employee_id) from employees";
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    maxId = rs.getInt(1); // lấy cột đầu tiên của kết quả (MAX(id))
                }
                conn.close();
                return maxId + 1;
            } catch (SQLException ex) {

            }
            return 0;
        }
    public String MapRole(int role){
        if(role == 1) return "Admin";
        else return "Nhân viên";
    }
    public int MapToRole(String name){
        if(name.equals("Admin")) return 1;
        else return 2;
    }
    public ResultSet load_execel(Employees employee) {
            ResultSet rs = null;
            try {
                conn = DBConnection.getConnection();

                String sql;
                PreparedStatement st;

                if (employee.getEmployee_name() == null || employee.getEmployee_name().isEmpty()) {
                    sql = "SELECT * FROM employees"; // nếu không nhập tên thì lấy tất cả
                    st = conn.prepareStatement(sql);
                } else {
                    sql = "SELECT * FROM employees WHERE employee_name LIKE ? ";
                    st = conn.prepareStatement(sql);
                    st.setString(1, "%" + employee.getEmployee_name() + "%");
                }

                rs = st.executeQuery();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return rs;
        }
     public boolean isEmployeeExists(String phone) {
        boolean exists = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM employees WHERE phone = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                exists = rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exists;
    }
}
