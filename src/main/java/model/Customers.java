/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
public class Customers {
    private int customer_id;
    private String phone_number;
    private String full_name;
    private int point;

    public Customers() {
    }

    public Customers(String phone_number, String full_name) {
        this.phone_number = phone_number;
        this.full_name = full_name;
    }
    
    public Customers(int customer_id, String full_name, String phone_number) {
        this.customer_id = customer_id;
        this.full_name = full_name;
        this.phone_number = phone_number;
    }

    public Customers(int customer_id, String phone_number, String full_name, int point) {
        this.customer_id = customer_id;
        this.phone_number = phone_number;
        this.full_name = full_name;
        this.point = point;
    }
    
    
    
    public int getCustomer_id() {
        return customer_id;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getFull_name() {
        return full_name;
    }

    public int getPoint() {
        return point;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setPoint(int point) {
        this.point = point;
    }
}
