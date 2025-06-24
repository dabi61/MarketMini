package dao;

import model.Category;
import model.DBConnection;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object cho Category
 * @author Admin
 */
public class CategoryDAO {
    private Connection connection;

    public CategoryDAO() throws SQLException {
        this.connection = DBConnection.getConnection();
    }

    // Lấy tất cả categories
    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY created_at DESC, category_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Lấy categories đang hoạt động
    public List<Category> getActiveCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE is_active = 1 ORDER BY display_order, category_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Lấy category theo ID
    public Category getCategoryById(int categoryId) {
        String sql = "SELECT * FROM category WHERE category_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToCategory(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy categories gốc (không có parent)
    public List<Category> getRootCategories() {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE parent_category_id IS NULL AND is_active = 1 ORDER BY display_order, category_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Lấy categories con theo parent ID
    public List<Category> getSubCategories(int parentId) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE parent_category_id = ? AND is_active = 1 ORDER BY display_order, category_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, parentId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Thêm category mới
    public boolean addCategory(Category category) {
        String sql = "INSERT INTO category (category_name, description, parent_category_id, category_code, is_active, display_order) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, category.getCategory_name());
            stmt.setString(2, category.getDescription());
            
            if (category.getParent_category_id() != null) {
                stmt.setInt(3, category.getParent_category_id());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            // Auto-generate category code if null
            String categoryCode = category.getCategory_code();
            if (categoryCode == null || categoryCode.trim().isEmpty()) {
                categoryCode = generateCategoryCode();
            }
            stmt.setString(4, categoryCode);
            
            stmt.setBoolean(5, category.isIs_active());
            stmt.setInt(6, category.getDisplay_order());
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                // Get the generated ID and set the auto-generated code if needed
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int newId = generatedKeys.getInt(1);
                    category.setCategory_id(newId);
                    
                    // If we generated a code, update the category object
                    if (category.getCategory_code() == null || category.getCategory_code().trim().isEmpty()) {
                        category.setCategory_code("CAT" + String.format("%04d", newId));
                        // Update the database with the new code
                        updateCategoryCode(newId, category.getCategory_code());
                    }
                }
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Generate unique category code
    private String generateCategoryCode() {
        return "CAT" + System.currentTimeMillis(); // Temporary code, will be updated with ID
    }
    
    // Update category code after getting ID
    private void updateCategoryCode(int categoryId, String categoryCode) {
        String sql = "UPDATE category SET category_code = ? WHERE category_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoryCode);
            stmt.setInt(2, categoryId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Cập nhật category
    public boolean updateCategory(Category category) {
        String sql = "UPDATE category SET category_name = ?, description = ?, parent_category_id = ?, category_code = ?, is_active = ?, display_order = ? WHERE category_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, category.getCategory_name());
            stmt.setString(2, category.getDescription());
            
            if (category.getParent_category_id() != null) {
                stmt.setInt(3, category.getParent_category_id());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            
            stmt.setString(4, category.getCategory_code());
            stmt.setBoolean(5, category.isIs_active());
            stmt.setInt(6, category.getDisplay_order());
            stmt.setInt(7, category.getCategory_id());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Xóa category (soft delete - set is_active = false)
    public boolean deleteCategory(int categoryId) {
        // Kiểm tra xem có sản phẩm nào đang sử dụng category này không
        if (hasProductsInCategory(categoryId)) {
            return false; // Không thể xóa nếu còn sản phẩm
        }
        
        String sql = "UPDATE category SET is_active = 0 WHERE category_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Kiểm tra category có sản phẩm không
    private boolean hasProductsInCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ? AND is_active = 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tìm kiếm categories theo tên
    public List<Category> searchCategories(String keyword) {
        List<Category> categories = new ArrayList<>();
        String sql = "SELECT * FROM category WHERE category_name LIKE ? OR description LIKE ? ORDER BY category_name";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Category category = mapResultSetToCategory(rs);
                categories.add(category);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Kiểm tra category code đã tồn tại chưa
    public boolean isCategoryCodeExists(String categoryCode, int excludeId) {
        String sql = "SELECT COUNT(*) FROM category WHERE category_code = ? AND category_id != ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoryCode);
            stmt.setInt(2, excludeId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tên parent category
    public String getParentCategoryName(Integer parentId) {
        if (parentId == null) return "Danh mục gốc";
        
        String sql = "SELECT category_name FROM category WHERE category_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, parentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getString("category_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Không xác định";
    }

    // Đếm số sản phẩm trong category
    public int getProductCountInCategory(int categoryId) {
        String sql = "SELECT COUNT(*) FROM products WHERE category_id = ? AND is_active = 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Mapping ResultSet to Category object
    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategory_id(rs.getInt("category_id"));
        category.setCategory_name(rs.getString("category_name"));
        category.setDescription(rs.getString("description"));
        
        int parentId = rs.getInt("parent_category_id");
        if (!rs.wasNull()) {
            category.setParent_category_id(parentId);
        }
        
        category.setCategory_code(rs.getString("category_code"));
        category.setIs_active(rs.getBoolean("is_active"));
        category.setDisplay_order(rs.getInt("display_order"));
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            category.setCreated_at(createdAt.toLocalDateTime());
        }
        
        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            category.setUpdated_at(updatedAt.toLocalDateTime());
        }
        
        return category;
    }

    // Đóng kết nối
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 