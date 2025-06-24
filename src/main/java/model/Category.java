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
    private Integer parent_category_id;
    private String category_code;
    private boolean is_active;
    private int display_order;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    
    public Category() {
        this.is_active = true;
        this.display_order = 0;
    }

    public Category(int category_id, String category_name, String description) {
        this();
        this.category_id = category_id;
        this.category_name = category_name;
        this.description = description;
    }

    public Category(String category_name, String description, Integer parent_category_id, String category_code) {
        this();
        this.category_name = category_name;
        this.description = description;
        this.parent_category_id = parent_category_id;
        this.category_code = category_code;
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

    public Integer getParent_category_id() {
        return parent_category_id;
    }

    public String getCategory_code() {
        return category_code;
    }

    public boolean isIs_active() {
        return is_active;
    }

    public int getDisplay_order() {
        return display_order;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public LocalDateTime getUpdated_at() {
        return updated_at;
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

    public void setParent_category_id(Integer parent_category_id) {
        this.parent_category_id = parent_category_id;
    }

    public void setCategory_code(String category_code) {
        this.category_code = category_code;
    }

    public void setIs_active(boolean is_active) {
        this.is_active = is_active;
    }

    public void setDisplay_order(int display_order) {
        this.display_order = display_order;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    public String toString() {
        return category_name; // Hiển thị tên category trong combobox
    }

    // Helper method để lấy tên parent category
    public String getParentCategoryName() {
        return parent_category_id != null ? "Có danh mục cha" : "Danh mục gốc";
    }

    // Helper method để hiển thị trạng thái
    public String getStatusText() {
        return is_active ? "Hoạt động" : "Ngừng hoạt động";
    }
}
