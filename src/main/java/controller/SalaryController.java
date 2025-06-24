/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.Salary.SalaryForm;

/**
 *
 * @author Admin
 */
public class SalaryController implements ActionListener {

    public SalaryController(SalaryForm salaryForm) {
        this.salaryForm = salaryForm;
    }

    public SalaryForm salaryForm;
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String button=e.getActionCommand();
        if("Sửa".equals(button) || "Xóa".equals(button)){
            salaryForm.HienthiForm(button);
        }
        if("Hủy bỏ".equals(button)){
            salaryForm.Huy();
        }
        if("Cập nhật".equals(button)){
            salaryForm.SuaTTLuong();
        }       
    }
    
}
