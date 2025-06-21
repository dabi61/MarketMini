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
import model.Expense;

/**
 *
 * @author THIS PC
 */
public class ExpenseDAO {
    
    private Connection conn;

    public ExpenseDAO() throws SQLException {
        conn = DBConnection.getConnection(); // dùng class DBConnection của bạn
    }
    
    public List<Object[]> searchByMonthOrYear(String keyword) throws SQLException {
    List<Object[]> list = new ArrayList<>();
    String sql = "SELECT expense_id, month_year, electricity_cost, rent_cost, water_cost, repair_cost " +
                 "FROM monthly_operating_expenses " +
                 "WHERE DATE_FORMAT(month_year, '%Y-%m') LIKE ? OR DATE_FORMAT(month_year, '%Y') LIKE ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, "%" + keyword + "%");
        ps.setString(2, "%" + keyword + "%");

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("expense_id");
                row[1] = rs.getDate("month_year");
                row[2] = rs.getInt("electricity_cost");
                row[3] = rs.getInt("rent_cost");
                row[4] = rs.getInt("water_cost");
                row[5] = rs.getInt("repair_cost");
                list.add(row);
            }
        }
    }

    return list;
}

    public boolean delete(int expenseId) {
    String sql = "DELETE FROM monthly_operating_expenses WHERE expense_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setInt(1, expenseId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    public boolean insert(Expense expense) {
    String sql = "INSERT INTO monthly_operating_expenses (month_year, electricity_cost, rent_cost, water_cost, repair_cost) " +
                 "VALUES (?, ?, ?, ?, ?)";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setDate(1, new java.sql.Date(expense.getMonthYear().getTime()));
        ps.setInt(2, expense.getElectricityCost());
        ps.setInt(3, expense.getRentCost());
        ps.setInt(4, expense.getWaterCost());
        ps.setInt(5, expense.getRepairCost());

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean update(Expense expense) {
    String sql = "UPDATE monthly_operating_expenses SET month_year = ?, electricity_cost = ?, rent_cost = ?, water_cost = ?, repair_cost = ? " +
                 "WHERE expense_id = ?";

    try (PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setDate(1, new java.sql.Date(expense.getMonthYear().getTime()));
        ps.setInt(2, expense.getElectricityCost());
        ps.setInt(3, expense.getRentCost());
        ps.setInt(4, expense.getWaterCost());
        ps.setInt(5, expense.getRepairCost());
        ps.setInt(6, expense.getExpenseId());

        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

    
    public List<Object[]> getAllExpenses() throws SQLException {
    List<Object[]> list = new ArrayList<>();
    String sql = "SELECT expense_id, month_year, electricity_cost, rent_cost, water_cost, repair_cost FROM monthly_operating_expenses";
    
    try (PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Object[] row = new Object[6];
            row[0] = rs.getInt("expense_id");
            row[1] = rs.getDate("month_year").toString(); // Chuyển ngày thành chuỗi
            row[2] = rs.getInt("electricity_cost");
            row[3] = rs.getInt("rent_cost");
            row[4] = rs.getInt("water_cost");
            row[5] = rs.getInt("repair_cost");

            list.add(row);
        }
    }
    return list;
}

}
