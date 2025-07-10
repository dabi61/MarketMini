/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.util.Date;

/**
 *
 * @author Admin
 */
public class Salary {
    private int salary_id;
    private int employee_id;
    private BigDecimal total_hours;
    private int hourly_wage;
    private int bonus;
    private Date payment_date;
    private Date created_date;
    private BigDecimal overtime_rate;
    private int gross_salary;
    private int penalty_deduction;
    private int net_salary;

    public Salary() {
    }

    public Salary(int salary_id, int employee_id, BigDecimal total_hours, int hourly_wage, int bonus,
            Date payment_date, Date created_date) {
        this.salary_id = salary_id;
        this.employee_id = employee_id;
        this.total_hours = total_hours;
        this.hourly_wage = hourly_wage;
        this.bonus = bonus;
        this.payment_date = payment_date;
        this.created_date = created_date;
        this.overtime_rate = new BigDecimal("1.2");
        this.penalty_deduction = 0;
    }

    public int getSalary_id() {
        return salary_id;
    }

    public void setSalary_id(int salary_id) {
        this.salary_id = salary_id;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public BigDecimal getTotal_hours() {
        return total_hours;
    }

    public void setTotal_hours(BigDecimal total_hours) {
        this.total_hours = total_hours;
    }

    public int getHourly_wage() {
        return hourly_wage;
    }

    public void setHourly_wage(int hourly_wage) {
        this.hourly_wage = hourly_wage;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    public Date getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(Date payment_date) {
        this.payment_date = payment_date;
    }

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }

    public BigDecimal getOvertime_rate() {
        return overtime_rate;
    }

    public void setOvertime_rate(BigDecimal overtime_rate) {
        this.overtime_rate = overtime_rate;
    }

    public int getGross_salary() {
        return gross_salary;
    }

    public void setGross_salary(int gross_salary) {
        this.gross_salary = gross_salary;
    }

    public int getPenalty_deduction() {
        return penalty_deduction;
    }

    public void setPenalty_deduction(int penalty_deduction) {
        this.penalty_deduction = penalty_deduction;
    }

    public int getNet_salary() {
        return net_salary;
    }

    public void setNet_salary(int net_salary) {
        this.net_salary = net_salary;
    }
    
    /**
     * Tính tổng lương cơ bản (giờ làm * lương theo giờ)
     * @return tổng lương cơ bản
     */
    public int getBasicSalary() {
        if (total_hours != null && hourly_wage > 0) {
            return total_hours.multiply(new BigDecimal(hourly_wage)).intValue();
        }
        return 0;
    }
    
    /**
     * Tính tổng lương (lương cơ bản + thưởng)
     * @return tổng lương
     */
    public int getTotalSalary() {
        return getBasicSalary() + bonus;
    }
    
    /**
     * Tính overtime rate (mặc định 1.2)
     * @return overtime rate
     */
    public BigDecimal getOvertimeRate() {
        return overtime_rate != null ? overtime_rate : new BigDecimal("1.2");
    }
    
    /**
     * Tính lương overtime
     * @return lương overtime
     */
    public int getOvertimeSalary() {
        if (total_hours != null && hourly_wage > 0) {
            BigDecimal overtimeWage = new BigDecimal(hourly_wage).multiply(getOvertimeRate());
            return total_hours.multiply(overtimeWage).intValue();
        }
        return 0;
    }
    
    @Override
    public String toString() {
        return "Salary{" + "salary_id=" + salary_id + ", employee_id=" + employee_id + 
               ", total_hours=" + total_hours + ", hourly_wage=" + hourly_wage + 
               ", bonus=" + bonus + ", payment_date=" + payment_date + 
               ", created_date=" + created_date + ", overtime_rate=" + overtime_rate +
               ", gross_salary=" + gross_salary + ", penalty_deduction=" + penalty_deduction +
               ", net_salary=" + net_salary + '}';
    }
}
