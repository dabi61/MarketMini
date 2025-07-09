/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDate;

/**
 *
 * @author Admin
 */
public class Orders {
    private int order_id;
    private int employee_id;
    private LocalDate order_date;
    private int total_amount;
    private int customer_id;
    
    private int finalAmount;
    private String customerName;
    private String phoneNumber;

    public Orders() {
    }

    public Orders(int order_id, int employee_id, LocalDate order_date, int total_amount, int customer_id) {
        this.order_id = order_id;
        this.employee_id = employee_id;
        this.order_date = order_date;
        this.total_amount = total_amount;
        this.customer_id = customer_id;
    }

    public int getOrder_id() {
        return order_id;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public LocalDate getOrder_date() {
        return order_date;
    }

    public int getTotal_amount() {
        return total_amount;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public void setOrder_date(LocalDate order_date) {
        this.order_date = order_date;
    }

    public void setTotal_amount(int total_amount) {
        this.total_amount = total_amount;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(int finalAmount) {
        this.finalAmount = finalAmount;
    }
}
