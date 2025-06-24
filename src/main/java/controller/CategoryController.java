package controller;

import dao.CategoryDAO;
import model.Category;
import view.CategoryForm;
import view.SuaCategoryForm;

import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller cho Category - xử lý logic nghiệp vụ
 * @author Admin
 */
public class CategoryController implements ActionListener {
    
    private CategoryDAO categoryDAO;
    private CategoryForm categoryForm;
    private int userRole;
    
    public CategoryController(int userRole) {
        this.userRole = userRole;
        try {
            this.categoryDAO = new CategoryDAO();
        } catch (SQLException e) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, e);
            JOptionPane.showMessageDialog(null, "Lỗi kết nối database: " + e.getMessage());
        }
    }
    
    public void setCategoryForm(CategoryForm categoryForm) {
        this.categoryForm = categoryForm;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (categoryDAO == null) return;
        
        String command = e.getActionCommand();
        
        try {
            switch (command) {
                case "add":
                    openAddCategoryForm();
                    break;
                case "edit":
                    openEditCategoryForm();
                    break;
                case "delete":
                    deleteCategory();
                    break;
                case "search":
                    searchCategories();
                    break;
                case "refresh":
                    refreshCategoryTable();
                    break;

                default:
                    break;
            }
        } catch (Exception ex) {
            Logger.getLogger(CategoryController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(categoryForm, "Có lỗi xảy ra: " + ex.getMessage());
        }
    }
    
    // Mở form thêm category
    private void openAddCategoryForm() {
        JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(categoryForm);
        SuaCategoryForm addForm = new SuaCategoryForm(parentFrame, true, null, userRole);
        addForm.setController(this);
        addForm.setVisible(true);
    }
    
    // Mở form sửa category
    private void openEditCategoryForm() {
        int selectedRow = categoryForm.getCategoryTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(categoryForm, "Vui lòng chọn danh mục cần sửa!");
            return;
        }
        
        // Lấy category ID từ table
        int categoryId = (Integer) categoryForm.getCategoryTable().getValueAt(selectedRow, 0);
        Category category = categoryDAO.getCategoryById(categoryId);
        
        if (category != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(categoryForm);
            SuaCategoryForm editForm = new SuaCategoryForm(parentFrame, true, category, userRole);
            editForm.setController(this);
            editForm.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(categoryForm, "Không thể tải thông tin danh mục!");
        }
    }
    
    // Xóa category
    private void deleteCategory() {
        int selectedRow = categoryForm.getCategoryTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(categoryForm, "Vui lòng chọn danh mục cần xóa!");
            return;
        }
        
        int categoryId = (Integer) categoryForm.getCategoryTable().getValueAt(selectedRow, 0);
        String categoryName = (String) categoryForm.getCategoryTable().getValueAt(selectedRow, 2);
        
        int confirm = JOptionPane.showConfirmDialog(
            categoryForm,
            "Bạn có chắc chắn muốn xóa danh mục '" + categoryName + "'?\n" +
            "Lưu ý: Không thể xóa nếu còn sản phẩm trong danh mục này.",
            "Xác nhận xóa",
            JOptionPane.YES_NO_OPTION
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = categoryDAO.deleteCategory(categoryId);
            
            if (success) {
                JOptionPane.showMessageDialog(categoryForm, "Xóa danh mục thành công!");
                refreshCategoryTable();
            } else {
                JOptionPane.showMessageDialog(categoryForm, 
                    "Không thể xóa danh mục này!\n" +
                    "Có thể vì còn sản phẩm đang sử dụng danh mục này."
                );
            }
        }
    }
    
    // Tìm kiếm categories
    private void searchCategories() {
        String keyword = categoryForm.getSearchField().getText().trim();
        
        if (keyword.isEmpty()) {
            refreshCategoryTable();
            return;
        }
        
        List<Category> searchResults = categoryDAO.searchCategories(keyword);
        updateCategoryTable(searchResults);
    }
    
    // Refresh bảng categories
    public void refreshCategoryTable() {
        List<Category> categories = categoryDAO.getAllCategories();
        updateCategoryTable(categories);
    }
    

    
    // Cập nhật bảng categories
    public void updateCategoryTable(List<Category> categories) {
        DefaultTableModel model = (DefaultTableModel) categoryForm.getCategoryTable().getModel();
        model.setRowCount(0); // Clear existing data
        
        for (Category category : categories) {
            model.addRow(new Object[]{
                category.getCategory_id(),                                           // ID
                category.getCategory_code() != null ? category.getCategory_code() : "AUTO-" + category.getCategory_id(), // Mã DM
                category.getCategory_name(),                                        // Tên Danh Mục
                category.getDescription() != null ? category.getDescription() : "", // Mô Tả
                category.getCreated_at() != null ? category.getCreated_at().toLocalDate() : "" // Ngày Tạo
            });
        }
    }
    
    // Thêm category mới
    public boolean addCategory(Category category) {
        // Kiểm tra dữ liệu
        if (!validateCategoryData(category)) {
            return false;
        }
        
        // Kiểm tra category code trùng lặp
        if (category.getCategory_code() != null && !category.getCategory_code().trim().isEmpty()) {
            if (categoryDAO.isCategoryCodeExists(category.getCategory_code(), 0)) {
                JOptionPane.showMessageDialog(null, "Mã danh mục đã tồn tại!");
                return false;
            }
        }
        
        boolean success = categoryDAO.addCategory(category);
        
        if (success) {
            JOptionPane.showMessageDialog(null, "Thêm danh mục thành công!");
            refreshCategoryTable();
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Thêm danh mục thất bại!");
            return false;
        }
    }
    
    // Cập nhật category
    public boolean updateCategory(Category category) {
        // Kiểm tra dữ liệu
        if (!validateCategoryData(category)) {
            return false;
        }
        
        // Kiểm tra category code trùng lặp (loại trừ chính nó)
        if (category.getCategory_code() != null && !category.getCategory_code().trim().isEmpty()) {
            if (categoryDAO.isCategoryCodeExists(category.getCategory_code(), category.getCategory_id())) {
                JOptionPane.showMessageDialog(null, "Mã danh mục đã tồn tại!");
                return false;
            }
        }
        
        boolean success = categoryDAO.updateCategory(category);
        
        if (success) {
            JOptionPane.showMessageDialog(null, "Cập nhật danh mục thành công!");
            refreshCategoryTable();
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "Cập nhật danh mục thất bại!");
            return false;
        }
    }
    
    // Validate dữ liệu category
    private boolean validateCategoryData(Category category) {
        if (category.getCategory_name() == null || category.getCategory_name().trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Tên danh mục không được để trống!");
            return false;
        }
        
        if (category.getCategory_name().length() > 255) {
            JOptionPane.showMessageDialog(null, "Tên danh mục không được vượt quá 255 ký tự!");
            return false;
        }
        
        if (category.getCategory_code() != null && category.getCategory_code().length() > 20) {
            JOptionPane.showMessageDialog(null, "Mã danh mục không được vượt quá 20 ký tự!");
            return false;
        }
        
        return true;
    }
    
    // Lấy tất cả categories
    public List<Category> getAllCategories() {
        return categoryDAO.getAllCategories();
    }
    
    // Lấy danh sách categories cho combobox
    public List<Category> getActiveCategories() {
        return categoryDAO.getActiveCategories();
    }
    
    // Lấy danh sách root categories
    public List<Category> getRootCategories() {
        return categoryDAO.getRootCategories();
    }
    
    // Lấy category theo ID
    public Category getCategoryById(int categoryId) {
        return categoryDAO.getCategoryById(categoryId);
    }
    
    // Đóng kết nối
    public void closeConnection() {
        if (categoryDAO != null) {
            categoryDAO.closeConnection();
        }
    }
} 