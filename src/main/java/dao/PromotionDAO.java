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
import model.Promotion;

/**
 *
 * @author THIS PC
 */
public class PromotionDAO {
   private Connection conn;

    public PromotionDAO() throws SQLException {
        conn = DBConnection.getConnection(); // dùng class DBConnection của bạn
    }
    public boolean insert(Promotion promotion) {
    String sql = "INSERT INTO promotion (promotion_name, start_date, end_date, product_id, discount, discounted_price, original_price) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, promotion.getPromotionName());
        ps.setDate(2, new java.sql.Date(promotion.getStartDate().getTime()));
        ps.setDate(3, new java.sql.Date(promotion.getEndDate().getTime()));
        ps.setInt(4, promotion.getProductId());
        ps.setInt(5, promotion.getDiscount());
        ps.setInt(6, promotion.getDiscountedPrice());
        ps.setInt(7, promotion.getOriginalPrice());

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public List<Object[]> searchByPromotionName(String keyword) {
    List<Object[]> list = new ArrayList<>();
    String sql = "SELECT p.promotion_id, p.promotion_name, p.start_date, p.end_date, pr.product_name, " +
                 "p.discount, p.discounted_price, p.original_price " +
                 "FROM promotion p JOIN products pr ON p.product_id = pr.product_id " +
                 "WHERE p.promotion_name LIKE ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "%" + keyword + "%");
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Object[] row = new Object[]{
                rs.getInt("promotion_id"),
                rs.getString("promotion_name"),
                rs.getDate("start_date"),
                rs.getDate("end_date"),
                rs.getString("product_name"),
                rs.getInt("discount"),
                rs.getInt("discounted_price"),
                rs.getInt("original_price")
            };
            list.add(row);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return list;
}

    public boolean delete(int promotionId) {
    String sql = "DELETE FROM promotion WHERE promotion_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, promotionId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    
    public boolean update(Promotion promotion) {
    String sql = "UPDATE promotion SET promotion_name = ?, start_date = ?, end_date = ?, product_id = ?, discount = ?, discounted_price = ?, original_price = ? " +
                 "WHERE promotion_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, promotion.getPromotionName());
        ps.setDate(2, new java.sql.Date(promotion.getStartDate().getTime()));
        ps.setDate(3, new java.sql.Date(promotion.getEndDate().getTime()));
        ps.setInt(4, promotion.getProductId());
        ps.setInt(5, promotion.getDiscount());
        ps.setInt(6, promotion.getDiscountedPrice());
        ps.setInt(7, promotion.getOriginalPrice());
        ps.setInt(8, promotion.getPromotionId());

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public List<Object[]> getAllPromotionsWithProductNames() {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT p.promotion_id, p.promotion_name, p.start_date, p.end_date, " +
                     "pr.product_name, p.discount, p.original_price, p.discounted_price " +
                     "FROM promotion p " +
                     "JOIN products pr ON p.product_id = pr.product_id";

        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getInt("promotion_id");
                row[1] = rs.getString("promotion_name");
                row[2] = rs.getDate("start_date");
                row[3] = rs.getDate("end_date");
                row[4] = rs.getString("product_name");
                row[5] = rs.getInt("discount");
                row[6] = rs.getInt("original_price");
                row[7] = rs.getInt("discounted_price");
                list.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}
