# 📂 Hệ Thống Quản Lý Category - MarketMini

## 🎯 Tổng Quan
Hệ thống quản lý danh mục sản phẩm hoàn chỉnh cho MarketMini với thiết kế theo pattern **InventoryDetailForm** - clean, modular và dễ bảo trì.

## 🏗️ Kiến Trúc Hệ Thống

### 📁 **Cấu trúc Files**
```
📦 Category System
├── 📄 model/Category.java                 # Enhanced model với full DB fields  
├── 📄 dao/CategoryDAO.java                # Complete CRUD operations
├── 📄 controller/CategoryController.java  # Business logic & event handling
├── 📄 view/CategoryForm.java              # Main management form (JFrame)
├── 📄 view/SuaCategoryForm.java          # Add/Edit dialog (JDialog)
└── 📄 CATEGORY_SAMPLE_DATA.sql           # Sample categories data
```

### 🔄 **Design Pattern**
Theo cùng pattern với `InventoryDetailForm.java`:

#### **CategoryForm (JFrame)**
```java
public class CategoryForm extends JFrame {
    // Constructor với error handling
    public CategoryForm(int userRole) {
        try {
            this.controller = new CategoryController(userRole);
            initComponents();     // Setup initial UI
            setupEventListeners(); // Wire events  
            loadData();           // Load & build full UI
        } catch (Exception ex) {
            // Handle errors gracefully
        }
    }
    
    // Core method - rebuilds entire UI
    private void loadData() {
        // Get fresh data
        List<Category> categories = controller.getAllCategories();
        
        // Create components
        createCategoryTable(categories);
        
        // Rebuild UI completely
        getContentPane().removeAll();
        add(createTitlePanel(), BorderLayout.NORTH);
        // ... add other panels
        revalidate();
        repaint();
    }
}
```

#### **Key Design Principles**
1. ✅ **JFrame-based** - Direct window, không cần wrapper
2. ✅ **loadData() rebuilds UI** - Fresh data mỗi lần refresh  
3. ✅ **Separate create methods** - Clean separation of concerns
4. ✅ **Exception handling** - Graceful error management
5. ✅ **Resource cleanup** - Proper dispose() implementation

## 🚀 **Tính Năng Chính**

### **CRUD Operations**
- ➕ **Thêm danh mục** - Comprehensive form validation
- ✏️ **Sửa danh mục** - In-place editing với pre-filled data  
- 🗑️ **Xóa danh mục** - Soft delete với dependency check
- 🔄 **Toggle trạng thái** - Active/Inactive switching

### **Advanced Features**  
- 🌳 **Parent-child hierarchy** - Nested categories support
- 🔍 **Real-time search** - Search by name/description
- 📊 **Smart sorting** - By display order & hierarchy
- 🛡️ **Role-based access** - Admin/Staff permissions
- ⌨️ **Keyboard shortcuts** - ESC, Enter, Double-click
- 🎨 **Professional UI** - Icons, colors, spacing

### **Data Integrity**
- ✅ **Validation** - Required fields, length limits
- 🔒 **Duplicate prevention** - Unique category codes  
- 🛡️ **Circular reference prevention** - Smart parent selection
- 📊 **Product count tracking** - Show usage statistics

## 📊 **Sample Data Structure**

```sql
📂 Thực phẩm tươi sống (FRESH)
   ├── 🥩 Thịt (MEAT)
   ├── 🐟 Hải sản (SEAFOOD)  
   ├── 🥬 Rau củ quả (VEGETABLE)
   ├── 🍎 Trái cây (FRUIT)
   └── 🥛 Trứng & sữa (DAIRY)

📂 Đồ uống (DRINK)
   ├── 🥤 Nước ngọt (SODA)
   ├── 💧 Nước suối (WATER)
   ├── 🍺 Bia rượu (ALCOHOL)  
   ├── ☕ Cà phê & trà (COFFEE)
   └── 🧃 Nước ép (JUICE)

📂 Bánh kẹo (SWEET)
   ├── 🍪 Bánh quy (BISCUIT)
   ├── 🍬 Kẹo (CANDY)
   ├── 🍿 Snack (SNACK)  
   └── 🍞 Bánh mì (BREAD)

+ 3 categories khác với 10+ subcategories
```

## 🔧 **Cách Sử Dụng**

### **1. Standalone Testing**
```bash
# Compile project
mvn compile

# Run CategoryForm directly  
mvn exec:java -Dexec.mainClass="TestCategoryForm"
```

### **2. Import Sample Data**
```bash
# Load sample categories vào database
mysql -u root -p marketmini < CATEGORY_SAMPLE_DATA.sql
```

### **3. Integration vào Main App**

#### **Option A: Direct JFrame**
```java
// Trong menu system
CategoryForm categoryForm = new CategoryForm(userRole);
categoryForm.setVisible(true);
```

#### **Option B: Embed in JTabbedPane** 
```java
// Convert sang JPanel nếu cần nhúng
public class CategoryPanel extends JPanel {
    private CategoryForm categoryForm;
    
    public CategoryPanel(int userRole) {
        setLayout(new BorderLayout());
        categoryForm = new CategoryForm(userRole);
        add(categoryForm.getContentPane(), BorderLayout.CENTER);
    }
}
```

## 🎯 **UI/UX Features**

### **Professional Design**
- 🎨 **Consistent colors** - Role-based theming
- 📐 **Responsive layout** - Proper column sizing  
- 🔤 **Typography hierarchy** - Clear information structure
- ⚡ **Performance** - Efficient data loading & rendering

### **User Experience**
- 🖱️ **Double-click to edit** - Quick access to edit mode
- ⌨️ **Keyboard navigation** - ESC to close, Enter to save
- 🔍 **Live search** - Instant filtering as you type
- 📊 **Visual feedback** - Loading states, success messages  
- 🛡️ **Error handling** - User-friendly error messages

### **Accessibility**  
- 🏷️ **Clear labels** - Descriptive field names
- 🎯 **Focus management** - Logical tab order  
- 🔤 **Font sizing** - Readable text sizes
- 🌈 **Color coding** - Meaningful visual cues

## 🛠️ **Technical Implementation**

### **Database Schema**
```sql
CREATE TABLE `category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `parent_category_id` int(11) DEFAULT NULL,
  `category_code` varchar(20) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `display_order` int(11) DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  KEY `category_parent_fk` (`parent_category_id`),
  CONSTRAINT `category_parent_fk` FOREIGN KEY (`parent_category_id`) 
    REFERENCES `category` (`category_id`) ON DELETE SET NULL
);
```

### **Key Methods**

#### **CategoryDAO.java**
```java
// Core CRUD
List<Category> getAllCategories()
List<Category> getActiveCategories()  
Category getCategoryById(int id)
boolean addCategory(Category category)
boolean updateCategory(Category category)
boolean deleteCategory(int id)

// Hierarchy support  
List<Category> getRootCategories()
List<Category> getSubCategories(int parentId)
String getParentCategoryName(Integer parentId)

// Business logic
List<Category> searchCategories(String keyword)
boolean isCategoryCodeExists(String code, int excludeId)
int getProductCountInCategory(int categoryId)
```

#### **CategoryController.java**
```java
// UI Event handling
void actionPerformed(ActionEvent e)  // Router cho all events
void openAddCategoryForm()           // Show add dialog
void openEditCategoryForm()          // Show edit dialog  
void deleteCategory()                // Confirm & delete
void searchCategories()              // Live search
void refreshCategoryTable()         // Reload data

// Data operations
boolean addCategory(Category category)
boolean updateCategory(Category category)  
void updateCategoryTable(List<Category> categories)
```

## 🔒 **Security & Validation**

### **Input Validation**
- ✅ Required field checking
- ✅ Length limit enforcement  
- ✅ Duplicate code prevention
- ✅ Circular reference detection
- ✅ SQL injection prevention

### **Role-Based Access**
```java
// Admin (role = 1)
- Full CRUD access
- Delete categories
- Toggle status
- Manage all categories

// Staff (role = 2)  
- View categories
- Add new categories
- Edit own categories
- No delete permissions
```

## 📈 **Performance Optimizations**

- 🚀 **Lazy loading** - Load data only when needed
- 💾 **Connection pooling** - Efficient DB connections
- 🎯 **Targeted updates** - Only refresh changed data  
- 📊 **Smart caching** - Cache frequently accessed data
- ⚡ **UI threading** - Non-blocking UI operations

## 🎉 **Result**

Hệ thống Category hoàn chỉnh với:
- ✅ **Professional UI/UX** theo pattern chuẩn
- ✅ **Complete CRUD operations** với validation
- ✅ **Hierarchical support** cho nested categories  
- ✅ **Role-based permissions** Admin/Staff
- ✅ **Search & filter** functionality
- ✅ **Error handling** comprehensive  
- ✅ **Database integration** với sample data
- ✅ **Clean architecture** dễ maintain & extend

**Ready to use & integrate! 🚀** 