/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.Employee.EmployeeForm;

/**
 *
 * @author Admin
 */
public class EmployeeController implements ActionListener{
    public EmployeeForm employeeForm;
    
    public EmployeeController(EmployeeForm employeeForm) {
        this.employeeForm = employeeForm;     
    } 
   

    @Override
    public void actionPerformed(ActionEvent e) {
       // throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        String button=e.getActionCommand();
        employeeForm.HienthiForm(button);
        if("Thêm Nhân Viên".equals(button)){
            employeeForm.AddEmployee();
        }
        if("Hủy bỏ".equals(button)){
            employeeForm.Huy();
        }        
        //sửa
        if("Lưu thay đổi".equals(button)){
            employeeForm.UpdateEmployee();
        }
        if("Xuất Excel".equals(button)){
            employeeForm.Xuatbaocao();
        }
        if("Upload".equals(button)){
            employeeForm.Upload();
        }
        if("Save".equals(button)){
            employeeForm.SaveDataFromExcel();
        }
        if("Tìm".equals(button)){
            employeeForm.timKiem();
        }
    }
    
}
