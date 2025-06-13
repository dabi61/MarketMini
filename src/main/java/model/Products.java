/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author Admin
 */
public class Products {
    private int product_id;
    private String product_name;
    private int category_id;
    private int price;
    private int stock_quantity;
    private String unit;

    //thêm phục vụ tìm kiếm
    private String categoryName;
    public Products() {
    }

    public Products(int product_id, String product_name, int category_id, int price, int stock_quantity, String unit) {
        this.product_id = product_id;
        this.product_name = product_name;
        this.category_id = category_id;
        this.price = price;
        this.stock_quantity = stock_quantity;
        this.unit = unit;
    }

    public int getProduct_id() {
        return product_id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public int getCategory_id() {
        return category_id;
    }

    public int getPrice() {
        return price;
    }

    public int getStock_quantity() {
        return stock_quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setStock_quantity(int stock_quantity) {
        this.stock_quantity = stock_quantity;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
    
    
}
