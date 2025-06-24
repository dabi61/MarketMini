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
    private BigDecimal hourly_wage;
    private BigDecimal bonus;
    private Date payment_date;
    private Date created_date;

    public Date getCreated_date() {
        return created_date;
    }

    public void setCreated_date(Date created_date) {
        this.created_date = created_date;
    }
    
    public Salary() {
    }

    public Salary(int salary_id, int employee_id, BigDecimal total_hours, BigDecimal hourly_wage, BigDecimal bonus,
            Date payment_date, Date created_date) {
        this.salary_id = salary_id;
        this.employee_id = employee_id;
        this.total_hours = total_hours;
        this.hourly_wage = hourly_wage;
        this.bonus = bonus;
        this.payment_date = payment_date;
        this.created_date = created_date;
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

    public BigDecimal getHourly_wage() {
        return hourly_wage;
    }

    public void setHourly_wage(BigDecimal hourly_wage) {
        this.hourly_wage = hourly_wage;
    }

    public BigDecimal getBonus() {
        return bonus;
    }

    public void setBonus(BigDecimal bonus) {
        this.bonus = bonus;
    }

    public Date getPayment_date() {
        return payment_date;
    }

    public void setPayment_date(Date payment_date) {
        this.payment_date = payment_date;
    }
    
    
}
