/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
public class Session {

    private static Session instance;
    private Employees currentEmployee;

    private Session() {
        // Constructor riêng để ngăn khởi tạo trực tiếp
    }

    public static synchronized Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public void setCurrentEmployee(Employees employee) {
        this.currentEmployee = employee;
    }

    public Employees getCurrentEmployee() {
        return currentEmployee;
    }

    public String getEmployeeName() {
        return currentEmployee != null ? currentEmployee.getEmployee_name() : null;
    }

    public int getEmployeeId() {
        return currentEmployee != null ? currentEmployee.getEmployee_id() : null;
    }
    
    public int getRole(){
        return currentEmployee != null ? currentEmployee.getRole(): -1;
    }
    
    public String getFullName(){
        return currentEmployee != null ? currentEmployee.getFull_name(): null;
    }

    public void clearSession() {
        currentEmployee = null;
    }

    public boolean isLoggedIn() {
        return currentEmployee != null;
    }
    
    public void logout() {
        currentEmployee = null; // Xoá thông tin nhân viên đã đăng nhập
    }
}
