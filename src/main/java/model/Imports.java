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
public class Imports {
    private int import_id;
    private int product_id;
    private int supplier_id;
    private int quantity;
    private int import_price;
    private LocalDate import_date;
    private int employee_id;

    public Imports() {
    }

    public Imports(int import_id, int product_id, int supplier_id, int quantity, int import_price, LocalDate import_date, int employee_id) {
        this.import_id = import_id;
        this.product_id = product_id;
        this.supplier_id = supplier_id;
        this.quantity = quantity;
        this.import_price = import_price;
        this.import_date = import_date;
        this.employee_id = employee_id;
    }

    public int getImport_id() {
        return import_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getSupplier_id() {
        return supplier_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getImport_price() {
        return import_price;
    }

    public LocalDate getImport_date() {
        return import_date;
    }

    public int getEmployee_id() {
        return employee_id;
    }

    public void setImport_id(int import_id) {
        this.import_id = import_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImport_price(int import_price) {
        this.import_price = import_price;
    }

    public void setImport_date(LocalDate import_date) {
        this.import_date = import_date;
    }

    public void setEmployee_id(int employee_id) {
        this.employee_id = employee_id;
    }
    
    
}
