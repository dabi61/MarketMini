/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;

/**
 *
 * @author macbook
 */
public class Employees {
    // Day du contructor, get, set hoac co the toString luon cung duoc
    private int employee_id;
    private String employee_name;
    private String password;
    private String full_name;
    private String sex;
    private int role;
    private String phone;
    private String email;
    private Date date;    

    public Employees() {
    }

    public Employees(int employee_id, String employee_name, String password, String full_name, int role, String phone, String email) {
        this.employee_id = employee_id;
        this.employee_name = employee_name;
        this.password = password;
        this.full_name = full_name;
        this.role = role;
        this.phone = phone;
        this.email = email;
    }

    public Employees(int employee_id, String employee_name, String password, String full_name, String sex, int role, String phone, String email, Date date) {
        this.employee_id = employee_id;
        this.employee_name = employee_name;
        this.password = password;
        this.full_name = full_name;
        this.sex = sex;
        this.role = role;
        this.phone = phone;
        this.email = email;
        this.date = date;
    }
    
    public Employees(int employee_id, String employee_name, String password) {
        this.employee_id = employee_id;
        this.employee_name = employee_name;
        this.password = password;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public String getEmployee_name() {
        return employee_name;
    }

    public String getPassword() {
        return password;
    }

    public String getFull_name() {
        return full_name;
    }

    public int getRole() {
        return role;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }

    public void setEmployee_name(String employee_name) {
        this.employee_name = employee_name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    
}
