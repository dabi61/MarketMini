/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import view.Category.CategorysForm;

/**
 *
 * @author Admin
 */
public class CategoryController implements ActionListener{
    public CategorysForm categoryForm;
    
    public CategoryController(CategorysForm categoryForm) {
        this.categoryForm = categoryForm;     
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        String button=e.getActionCommand();
        categoryForm.HienthiForm(button);
        if("Thêm DM".equals(button)){
            categoryForm.AddSupplier();
        }
        if("Hủy bỏ".equals(button)){
            categoryForm.Huy();
        }        
        //sửa
        if("Lưu thay đổi".equals(button)){
            categoryForm.UpdateCategory();
        }
        if("Xuất Excel".equals(button)){
            categoryForm.Xuatbaocao();
        }
        if("Upload".equals(button)){
            categoryForm.Upload();
        }
        if("Save".equals(button)){
            categoryForm.SaveDataFromExcel();
        }
        if("Tìm kiếm".equals(button)){
            categoryForm.timKiem();
        }
        
    }
    
}
