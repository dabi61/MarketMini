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
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.DBConnection;
import model.Suppliers;

/**
 *
 * @author Admin
 */
public class SupplierDAO {
        Connection con;
        public void supplier_insert(Suppliers supplier){
            try {
                // kết nối db
                con = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "Insert into suppliers Values(?,?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);  
                ps.setNull(1, java.sql.Types.INTEGER );
                ps.setString(2,supplier.getSupplier_name());
                ps.setString(3,supplier.getPhone());
                ps.setString(4,supplier.getAddress());
                ps.setString(5,supplier.getEmail());
                ps.executeUpdate();
                con.close();
            } catch (SQLException ex) {
                String em = ex.getMessage();
                System.out.println(em);
            }
            }
        
        
        public void supplier_update(Suppliers supplier){
            try {
                // kết nối db
                con = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "Update suppliers Set supplier_name=?, phone=?, address=?, email=? where supplier_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1,supplier.getSupplier_name());
                ps.setString(2,supplier.getPhone());
                ps.setString(3,supplier.getAddress());
                ps.setString(4,supplier.getEmail());
                ps.setInt(5,supplier.getSupplier_id());
                ps.executeUpdate();
                con.close();
            } catch (SQLException ex) {}
            }
        
        public void supplier_delete(Suppliers supplier){
            try {
                // kết nối db
                con = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "delete from suppliers where supplier_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1,supplier.getSupplier_id());
                ps.executeUpdate();
                con.close();
            } catch (SQLException ex) {}
            }   
        
        public void supplierfind(JTable tbBang,Suppliers supplier){
        try {
            // kết nối db
            con = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "select * from suppliers where supplier_name like ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1,'%'+supplier.getSupplier_name()+'%');
            ResultSet rs = ps.executeQuery();
            tbBang.removeAll();
            String[] head= {"Mã Nhà CC","Tên NCC","Điện thoại","Địa chỉ","Email"};
            DefaultTableModel tb = new DefaultTableModel(head,0);
            while(rs.next()){
                Vector vt = new Vector();
                vt.add(rs.getInt("supplier_id"));
                vt.add(rs.getString("supplier_name"));
                vt.add(rs.getString("phone"));
                vt.add(rs.getString("address"));
                vt.add(rs.getString("email"));
                tb.addRow(vt);
            }
            tbBang.setModel(tb);
            con.close();
        } catch (SQLException ex) {
            
        }
        }
        public ResultSet timKiem(String tieuChi, String txtTimKiem) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT *FROM suppliers";

            if (!tieuChi.equals("Tất cả") && txtTimKiem != null && !txtTimKiem.isEmpty()) {
                switch (tieuChi) {
                    case "Mã NCC":
                        sql += " WHERE supplier_id LIKE ?";
                        break;
                    case "Tên":
                        sql += " WHERE supplier_name LIKE ?";
                        break;
                    case "Email":
                        sql += " WHERE email LIKE ?";
                        break;
                     case "Địa Chỉ":
                        sql += " WHERE address LIKE ?";
                        break;
                    case "Số điện thoại":
                        sql += " WHERE phone LIKE ?";
                        break;
                }
            }
            PreparedStatement st = con.prepareStatement(sql);
            if (!tieuChi.equals("Tất cả") && txtTimKiem != null && !txtTimKiem.isEmpty()) {
                st.setString(1, "%" + txtTimKiem + "%");
            }
            return st.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        }
        
        public int supplierIdMax(){
            try {
                // kết nối db
                int maxId = -1;
                con = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "select max(supplier_id) from suppliers";
                PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    maxId = rs.getInt(1); // lấy cột đầu tiên của kết quả (MAX(id))
                }
                con.close();
                return maxId + 1;
            } catch (SQLException ex) {

            }
            return 0;
        }
       
        public ResultSet load_execel(Suppliers supplier) {
            ResultSet rs = null;
            try {
                con = DBConnection.getConnection();

                String sql;
                PreparedStatement st;

                if (supplier.getSupplier_name() == null || supplier.getSupplier_name().isEmpty()) {
                    sql = "SELECT * FROM suppliers"; // nếu không nhập tên thì lấy tất cả
                    st = con.prepareStatement(sql);
                } else {
                    sql = "SELECT * FROM suppliers WHERE supplier_name LIKE ? or phone LIKE ?";
                    st = con.prepareStatement(sql);
                    st.setString(1, "%" + supplier.getSupplier_name() + "%");
                    st.setString(2, "%" + supplier.getPhone() + "%");
                }

                rs = st.executeQuery();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return rs;
        }
 
    public boolean isSupplierExists(String name, String phone) {
        boolean exists = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM suppliers WHERE supplier_name = ? OR phone = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
            ps.setString(2, phone);
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

