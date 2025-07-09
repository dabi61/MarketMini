package model;

import java.time.LocalDateTime;

/**
 * Model cho Category với đầy đủ các thuộc tính từ database
 * @author Admin
 */
public class Category {
    private int category_id;
    private String category_name;
    private String description;

    
    public Category() {
        
    }

    public Category(int category_id, String category_name, String description) {
        this();
        this.category_id = category_id;
        this.category_name = category_name;
        this.description = description;
    }

   

    // Getters
    public int getCategory_id() {
        return category_id;
    }

    public String getCategory_name() {
        return category_name;
    }

    public String getDescription() {
        return description;
    }

   

    // Setters
    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    
    @Override
    public String toString() {
        return category_name; // Hiển thị tên category trong combobox
    }
}
