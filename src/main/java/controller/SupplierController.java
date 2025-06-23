/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package controller;

import dao.SupplierDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import view.SupplierForm;

/**
 *
 * @author Admin
 */
public class SupplierController implements ActionListener{  
    public SupplierForm supplierForm;
    
    public SupplierController(SupplierForm supplierForm) {
        this.supplierForm = supplierForm;     
       
    } 
    // tìm kiếm
//    private SupplierDAO dao = new SupplierDAO();
//
//    public ResultSet timKiem(String tieuChi, String tuKhoa) {
//        return dao.timKiem(tieuChi, tuKhoa);
//    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String button=e.getActionCommand();
        supplierForm.HienthiForm(button);
        if("Thêm NCC".equals(button)){
            supplierForm.AddSupplier();
        }
        if("Hủy bỏ".equals(button)){
            supplierForm.Huy();
        }        
        //sửa
        if("Lưu thay đổi".equals(button)){
            supplierForm.UpdateSupplier();
        }
        if("Xuất Excel".equals(button)){
            supplierForm.Xuatbaocao();
        }
        if("Upload".equals(button)){
            supplierForm.Upload();
        }
        if("Save".equals(button)){
            supplierForm.SaveDataFromExcel();
        }
        if("Tìm kiếm".equals(button)){
            supplierForm.timKiem();
        }
    }
    
}
