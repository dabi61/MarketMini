package dao;

import model.Category;
import model.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.Suppliers;

/**
 * Data Access Object cho Category
 * @author Admin
 */
public class CategoryDAO {
        Connection con;
        public void category_insert(Category category){
            try {
                // kết nối db
                con = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "Insert into category Values(?,?,?,?)";
                PreparedStatement ps = con.prepareStatement(sql);  
                ps.setNull(1, java.sql.Types.INTEGER );
                ps.setString(2,category.getCategory_name());
                ps.setString(3,category.getDescription());
                ps.setString(4,null);
                ps.executeUpdate();
                con.close();
            } catch (SQLException ex) {
                String em = ex.getMessage();
                System.out.println(em);
            }
            }
        
        
        public void category_update(Category category){
            try {
                // kết nối db
                con = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "Update category Set category_name=?, description=? where category_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1,category.getCategory_name());
                ps.setString(2,category.getDescription());
                ps.setInt(3, category.getCategory_id());
                ps.executeUpdate();
                con.close();
            } catch (SQLException ex) {
                
            }
            }
        
        public void category_delete(Category category){
            try {
                // kết nối db
                con = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "delete from category where category_id=?";
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setInt(1,category.getCategory_id());
                ps.executeUpdate();
                con.close();
            } catch (SQLException ex) {}
            }   
        
        public void categoryfind(JTable tbBang,Category category){
        try {
            // kết nối db
            con = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "select * from category ";
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            tbBang.removeAll();
            String[] head= {"Mã Danh Mục","Tên Danh Mục","Mô Tả"};
            DefaultTableModel tb = new DefaultTableModel(head,0);
            while(rs.next()){
                Vector vt = new Vector();
                vt.add(rs.getInt("category_id"));
                vt.add(rs.getString("category_name"));
                vt.add(rs.getString("description"));
                tb.addRow(vt);
            }
            tbBang.setModel(tb);
            con.close();
        } catch (SQLException ex) {
            
        }
        }
        public ResultSet timKiem(String tuKhoa) {
        try {
            Connection con = DBConnection.getConnection();
            String sql = "SELECT * FROM category";
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                sql += " WHERE category_name LIKE ?";
            }
            PreparedStatement ps = con.prepareStatement(sql);
            if (tuKhoa != null && !tuKhoa.trim().isEmpty()) {
                ps.setString(1, "%" + tuKhoa + "%");
            }
            return ps.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
        
        public int categoryIdMax(){
            try {
                // kết nối db
                int maxId = -1;
                con = DBConnection.getConnection();
                // tạo đối tượng pre để thực hiện câu lệnh truy vấn
                String sql = "select max(category_id) from category";
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
       
        public ResultSet load_execel(Category category) {
            ResultSet rs = null;
            try {
                con = DBConnection.getConnection();
                String sql;
                PreparedStatement st;
                if (category.getCategory_name()== null || category.getCategory_name().isEmpty()) {
                    sql = "SELECT * FROM category"; // nếu không nhập tên thì lấy tất cả
                    st = con.prepareStatement(sql);
                } else {
                    sql = "SELECT * FROM category WHERE category_name LIKE ? ";
                    st = con.prepareStatement(sql);
                    st.setString(1, "%" + category.getCategory_name()+ "%");
                }

                rs = st.executeQuery();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return rs;
        }
// 
    public boolean isCategoryExists(String name) {
        boolean exists = false;
        try (Connection con = DBConnection.getConnection()) {
            String sql = "SELECT COUNT(*) FROM category WHERE category_name = ? ";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, name);
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