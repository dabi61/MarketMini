/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import model.DBConnection;
import model.Employees;
import model.Salary;

/**
 *
 * @author macbook
 */
public class SalaryDAO {

    //Cac truy van toi database
    private Connection conn;

    public SalaryDAO() {

    }

    public void salary_insert(Salary salary) {
        try {
            // kết nối db
            conn = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "Insert into salary Values(?,?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, salaryIdMax());
            ps.setInt(2, salary.getEmployee_id());
            ps.setBigDecimal(3, salary.getTotal_hours());
            ps.setBigDecimal(4, salary.getHourly_wage());
            ps.setBigDecimal(5, salary.getBonus());
            if (salary.getPayment_date() == null) {
                ps.setNull(6, java.sql.Types.DATE);
            } else {
                java.sql.Date sqlDate = new java.sql.Date(salary.getPayment_date().getTime());
                ps.setDate(6, sqlDate);
            }

            java.sql.Date created_date = new java.sql.Date(salary.getCreated_date().getTime());
            ps.setDate(7, created_date);
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
        }
    }

    public void salary_update(Salary salary) {
        try {
            // kết nối db
            conn = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "Update salary Set bonus=?,payment_date=? where salary_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, salary.getBonus());
            if (salary.getPayment_date() == null) {
                ps.setNull(2, java.sql.Types.DATE);
            } else {
                java.sql.Date sqlDate = new java.sql.Date(salary.getPayment_date().getTime());
                ps.setDate(2, sqlDate);
            }
            ps.setInt(3, salary.getSalary_id());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void salary_update_total_hours(Salary salary) {
        try {
            // kết nối db
            conn = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "Update salary Set total_hours=? where salary_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setBigDecimal(1, salary.getTotal_hours());           
            ps.setInt(2, salary.getSalary_id());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void salary_delete(Salary salary) {
        try {
            // kết nối db
            conn = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "delete from salary where salary_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, salary.getSalary_id());
            ps.executeUpdate();
            conn.close();
        } catch (SQLException ex) {
        }
    }

    public void salaryfind(JTable tbBang, int month) {
        try {
            // kết nối db
            conn = DBConnection.getConnection();
            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.getYear();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "SELECT distinct s.salary_id, e.employee_id, e.employee_name, total_hours, hourly_wage, bonus, payment_date FROM employees e "
                    + "JOIN salary s ON e.employee_id = s.employee_id "
                    + "WHERE MONTH(s.created_date)  = ? AND YEAR(s.created_date) = ? ";
            //"AND employee_name like ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, month);
            ps.setInt(2, currentDate.getYear());
            //ps.setString(1,'%'+salary.getEmployee_id()+'%');
            ResultSet rs = ps.executeQuery();
            tbBang.removeAll();
            String[] head = {"Mã lương", "Tên nhân viên", "Tổng giờ làm", "Lương theo h", "Tiền thưởng", "Tổng", "Ngày trả"};
            DefaultTableModel tb = new DefaultTableModel(head, 0);
            while (rs.next()) {
                Vector vt = new Vector();
                vt.add(rs.getInt("salary_id"));
                vt.add(rs.getString("employee_name"));
                vt.add(rs.getBigDecimal("total_hours"));
                vt.add(rs.getBigDecimal("hourly_wage"));
                vt.add(rs.getBigDecimal("bonus"));
                vt.add(calculateSalary(rs.getInt("employee_id"), month));
                vt.add(rs.getDate("payment_date"));
                tb.addRow(vt);
            }
            tbBang.setModel(tb);
            conn.close();
        } catch (SQLException ex) {
        }
    }

    public int salaryIdMax() {
        try {
            // kết nối db
            int maxId = 0;
            conn = DBConnection.getConnection();
            // tạo đối tượng pre để thực hiện câu lệnh truy vấn
            String sql = "select max(salary_id) from salary";
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

    public double calculateSalary(int employeeId, int month) {
        double totalSalary = 0.0;
        String sql = "SELECT s.total_hours, s.hourly_wage, s.bonus "
                + "FROM salary s "
                + "WHERE employee_id = ? and MONTH(created_date) = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.setInt(2, month);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                double totalHours = rs.getDouble("total_hours");
                double hourlyWage = rs.getDouble("hourly_wage");
                double bonus = rs.getDouble("bonus");
                totalSalary = (totalHours * hourlyWage) + bonus;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalSalary;
    }

//    public double calculateSalaryForAll() {
//        String sql = "SELECT s.employee_id, s.total_hours, s.hourly_wage, s.bonus "
//                + "FROM salary s "
//                + "JOIN employees e ON s.employee_id = e.employee_id "
//                + "JOIN workingsession w ON s.employee_id = w.employee_id "
//                + "WHERE MONTH(w.date) = ? AND YEAR(w.date) = ?";
//        LocalDate currentDate = LocalDate.now();
//        double totalSalary = 0.0;
//        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
//            pstmt.setInt(1, currentDate.getMonthValue());
//            pstmt.setInt(2, currentDate.getYear());
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                double totalHours = rs.getDouble("total_hours");
//                double hourlyWage = rs.getDouble("hourly_wage");
//                double bonus = rs.getDouble("bonus");
//                totalSalary += (totalHours * hourlyWage) + bonus;
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return totalSalary;
//    }
    public void getTotalWorkingHoursByEmployee(int month) {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLDataException ex) {
            Logger.getLogger(SalaryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        LocalDate currentDate = LocalDate.now();
        int currentYear = currentDate.getYear();
        String sql = "SELECT e.employee_id, e.employee_name, COALESCE(SUM(w.working_hours), 0) as total_hours "
                + "FROM employees e "
                + "LEFT JOIN workingsession w ON e.employee_id = w.employee_id "
                + "AND MONTH(w.date) = ? AND YEAR(w.date) = ? "
                + "GROUP BY e.employee_id, e.employee_name";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, month);
            pstmt.setInt(2, currentYear);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int employee_id = rs.getInt("employee_id");
                BigDecimal total_hour = rs.getBigDecimal("total_hours");
                BigDecimal hour_wage = calculateHourlyWage(employee_id);
                BigDecimal bonus = new BigDecimal("0");
                Salary sl = new Salary(0, employee_id, total_hour, hour_wage, bonus, null, Date.from(currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                int r = hasSalaryDataThisMonth(employee_id);
                if (r != -1) {
                    sl.setSalary_id(r);
                    salary_update_total_hours(sl);
                } else {
                    salary_insert(sl);
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public BigDecimal calculateHourlyWage(int employeeId) {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLDataException ex) {
            Logger.getLogger(SalaryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        String sql = "SELECT e.role, e.date FROM employees e WHERE e.employee_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int role = rs.getInt("role");
                LocalDate hireDate = rs.getDate("date").toLocalDate();
                LocalDate now = LocalDate.now();
                long monthsWorked = ChronoUnit.MONTHS.between(hireDate, now);

                if (role == 1) { // Giả sử role 1 là quản lý
                    return new BigDecimal("30000.0");
                } else if (monthsWorked < 6) {
                    return new BigDecimal("18000.0");
                } else {
                    return new BigDecimal("20000.0");
                }
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new BigDecimal("0.0");
    }

    public boolean isLastDayOfMonth() {
        LocalDate currentDate = LocalDate.now();
        LocalDate nextDate = currentDate.plusDays(1);
        return currentDate.getMonth() != nextDate.getMonth();
    }

    public int hasSalaryDataThisMonth(int employeeId) {
        try {
            conn = DBConnection.getConnection();
        } catch (SQLDataException ex) {
            Logger.getLogger(SalaryDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        LocalDate currentDate = LocalDate.now();
        String sql = "SELECT distinct salary_id FROM salary s "
                + "WHERE s.employee_id = ? AND MONTH(created_date) = ? AND YEAR(created_date) = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, employeeId);
            pstmt.setInt(2, currentDate.getMonthValue());
            pstmt.setInt(3, currentDate.getYear());
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Integer.valueOf(rs.getString("salary_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public ResultSet timKiem(String tieuChi, String txtTimKiem) {
        try {
            LocalDate currentDate = LocalDate.now();
            int currentYear = currentDate.getYear();
            Connection con = DBConnection.getConnection();
            String sql = "SELECT distinct s.salary_id, e.employee_id, e.employee_name, total_hours, hourly_wage, bonus, payment_date FROM employees e "
                    + "JOIN salary s ON e.employee_id = s.employee_id "
                    + "WHERE MONTH(s.created_date)  = ? AND YEAR(s.created_date) = ? ";

            if (!tieuChi.equals("Tất cả") && txtTimKiem != null && !txtTimKiem.isEmpty()) {
                switch (tieuChi) {
                    case "Tên":
                        sql += " AND e.employee_name LIKE ?";
                        break;
                }
            }
            PreparedStatement st = con.prepareStatement(sql);
            st.setInt(1, currentDate.getMonthValue());
            st.setInt(2, currentDate.getYear());
            if (!tieuChi.equals("Tất cả") && txtTimKiem != null && !txtTimKiem.isEmpty()) {
                st.setString(3, "%" + txtTimKiem + "%");
            }
            return st.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResultSet load_execel(String txtTimKiem) {
        ResultSet rs = null;
        try {
            conn = DBConnection.getConnection();
            rs = timKiem("Tên", txtTimKiem);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return rs;
    }
}
