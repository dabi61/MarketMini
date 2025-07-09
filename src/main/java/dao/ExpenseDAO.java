/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
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
    public boolean isMonthYearExists(Date monthYear) {
    String sql = "SELECT COUNT(*) FROM monthly_operating_expenses WHERE month_year = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setDate(1, new java.sql.Date(monthYear.getTime()));
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

    public List<Object[]> searchByMonthAndYear(int month, int year) throws SQLException {
    List<Object[]> list = new ArrayList<>();
    String sql = "SELECT expense_id, month_year, electricity_cost, rent_cost, water_cost, repair_cost " +
                 "FROM monthly_operating_expenses " +
                 "WHERE MONTH(month_year) = ? AND YEAR(month_year) = ?";

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, month);
        ps.setInt(2, year);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Object[] row = new Object[] {
                rs.getInt("expense_id"),
                rs.getDate("month_year").toLocalDate().format(DateTimeFormatter.ofPattern("MM/yyyy")),
                rs.getInt("electricity_cost"),
                rs.getInt("rent_cost"),
                rs.getInt("water_cost"),
                rs.getInt("repair_cost")
            };
            list.add(row);
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
            row[1] = rs.getDate("month_year").toLocalDate().format(DateTimeFormatter.ofPattern("MM/yyyy"));

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
