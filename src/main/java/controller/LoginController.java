/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import com.raven.main.Main;
import dao.EmployeeDAO;
import javax.swing.JOptionPane;
import model.Employees;
import view.LoginForm;
import view.MainForm;

/**
 *
 * @author Admin
 */
public class LoginController {

    private LoginForm loginForm;
    private EmployeeDAO employeeDAO;

    public LoginController() {
    }
    
    public LoginController(LoginForm loginForm, EmployeeDAO employeeDAO) {
        this.loginForm = loginForm;
        this.employeeDAO = employeeDAO;
    }
    
    public LoginController(LoginForm loginForm) {
        this.loginForm = loginForm;
    }

    public void login() {
        String username = loginForm.getTxtTaiKhoan().getText();
        String password = loginForm.getTxtPassWord().getText();

        Employees employee = new Employees();
        employee.setEmployee_name(username);
        employee.setPassword(password);

        boolean isAuthenticated = employeeDAO.login(employee);

        if (isAuthenticated) {
            JOptionPane.showMessageDialog(loginForm, "Đăng nhập thành công!");
           
            loginForm.dispose();
            
            Main main = new Main();
            main.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(loginForm, "Tên đăng nhập hoặc mật khẩu không đúng!");
        }
    }

}
