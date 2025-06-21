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
import model.Order;
/**
 *
 * @author THIS PC
 */

public class OrderDAO {
    private Connection conn;
    public OrderDAO() throws SQLException {
        conn = DBConnection.getConnection();
    }
    public boolean updateTotalAmount(int orderId) {
    String sql = "UPDATE orders SET total_amount = " +
                 "(SELECT SUM(quantity * unit_price) FROM orderdetails WHERE order_id = ?) " +
                 "WHERE order_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        ps.setInt(2, orderId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    public boolean updateOrderDetail(int orderId, int productId, int quantity, double price) {
    String sql = "UPDATE orderdetails SET quantity = ?, unit_price = ? WHERE order_id = ? AND product_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, quantity);
        ps.setDouble(2, price);
        ps.setInt(3, orderId);
        ps.setInt(4, productId); // Truyền productId thay vì productName

        return ps.executeUpdate() > 0; // Trả về true nếu cập nhật thành công
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean addOrderDetailItem(int orderId, String productName, int quantity, double price) {
    String sql = "INSERT INTO orderdetails (order_id, product_id, quantity, unit_price) " +
                 "VALUES (?, (SELECT product_id FROM products WHERE product_name = ?), ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        ps.setString(2, productName);
        ps.setInt(3, quantity);
        ps.setDouble(4, price);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    public boolean deleteOrderDetail(int orderId, int productId) {
    String sql = "DELETE FROM orderdetails WHERE order_id = ? AND product_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        ps.setInt(2, productId);

        return ps.executeUpdate() > 0; // Trả về true nếu xóa thành công
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    
    public List<Object[]> getOrderItemsByOrderId(int orderId) throws Exception {
    List<Object[]> list = new ArrayList<>();
    String sql = "SELECT od.product_id, p.product_name, od.quantity, od.unit_price " +
                 "FROM orderdetails od " +  // Đúng tên bảng của bạn
                 "JOIN products p ON od.product_id = p.product_id " + // Join để lấy tên sản phẩm
                 "WHERE od.order_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, orderId);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = new Object[] {
                rs.getInt("product_id"),    // Lấy ID sản phẩm
                rs.getString("product_name"), // Lấy tên sản phẩm từ bảng product
                rs.getInt("quantity"),     // Lấy số lượng sản phẩm
                rs.getInt("unit_price")    // Lấy đơn giá của sản phẩm
            };
            list.add(row);
        }
    }
    return list;
}
    

    public List<Object[]> getOrdersByCustomerPhone(String phone) {
    List<Object[]> list = new ArrayList<>();
    try {
        String sql = "SELECT o.order_id, e.full_name AS employee_name, o.order_date, o.total_amount, c.phone_number AS customer_phone " +
                     "FROM orders o " +
                     "JOIN employees e ON o.employee_id = e.employee_id " +
                     "JOIN customers c ON o.customer_id = c.customer_id " +
                     "WHERE c.phone_number LIKE ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setString(1, "%" + phone + "%");
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = new Object[5];
            row[0] = rs.getInt("order_id");
            row[1] = rs.getString("employee_name");
            row[2] = rs.getDate("order_date");
            row[3] = rs.getInt("total_amount");
            row[4] = rs.getString("customer_phone");
            list.add(row);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}

    public List<Object[]> getAllOrdersWithNames() {
    List<Object[]> list = new ArrayList<>();
    try {
        String sql = "SELECT o.order_id, e.full_name AS employee_name, o.order_date, o.total_amount, c.phone_number AS customer_phone " +
                     "FROM orders o " +
                     "JOIN employees e ON o.employee_id = e.employee_id " +
                     "JOIN customers c ON o.customer_id = c.customer_id";

        PreparedStatement ps = conn.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Object[] row = new Object[5];
            row[0] = rs.getInt("order_id");
            row[1] = rs.getString("employee_name");
            row[2] = rs.getDate("order_date");
            row[3] = rs.getInt("total_amount");
            row[4] = rs.getString("customer_phone"); // Đổi từ customer_name sang customer_phone
            list.add(row);
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return list;
}



    // Lấy tất cả đơn hàng
    public List<Order> getAllOrders() {
        List<Order> list = new ArrayList<>();
        try {
            String sql = "SELECT * FROM orders";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setEmployeeId(rs.getInt("employee_id"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setTotalAmount(rs.getInt("total_amount"));
                order.setCustomerId(rs.getInt("customer_id"));
                list.add(order);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy đơn hàng theo ID
    public Order getOrderById(int id) {
        try {
            String sql = "SELECT * FROM orders WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setEmployeeId(rs.getInt("employee_id"));
                order.setOrderDate(rs.getDate("order_date"));
                order.setTotalAmount(rs.getInt("total_amount"));
                order.setCustomerId(rs.getInt("customer_id"));
                return order;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm đơn hàng mới
    public boolean insert(Order order) {
        try {
            String sql = "INSERT INTO orders (employee_id, order_date, total_amount, customer_id) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, order.getEmployeeId());
            ps.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            ps.setInt(3, order.getTotalAmount());
            ps.setInt(4, order.getCustomerId());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Cập nhật đơn hàng
    public boolean update(Order order) {
        try {
            String sql = "UPDATE orders SET employee_id = ?, order_date = ?, total_amount = ?, customer_id = ? WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, order.getEmployeeId());
            ps.setDate(2, new java.sql.Date(order.getOrderDate().getTime()));
            ps.setInt(3, order.getTotalAmount());
            ps.setInt(4, order.getCustomerId());
            ps.setInt(5, order.getOrderId());
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean deleteOrderDetailsByOrderId(int orderId) {
    try {
        String sql = "DELETE FROM orderdetails WHERE order_id = ?";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, orderId);
        int result = ps.executeUpdate();
        return result >= 0;
    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}

    // Xóa đơn hàng theo ID
    public boolean delete(int id) {
        try {   
            String sql = "DELETE FROM orders WHERE order_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
