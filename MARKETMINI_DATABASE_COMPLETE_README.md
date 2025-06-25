# 🏪 MARKETMINI - COMPLETE DATABASE SYSTEM

## 📋 Tổng Quan
Hệ thống database hoàn chỉnh cho ứng dụng quản lý siêu thị mini MarketMini, được thiết kế dựa trên phân tích toàn bộ codebase Java Swing.

### 🔧 Version: 3.0 Enhanced
- **Enhanced Penalty System** cho WorkShift management
- **Category Hierarchy** với parent-child relationships  
- **Customer Loyalty Program** với points và tiers
- **Comprehensive Product Management** 
- **Advanced Financial Tracking**

## 🗃️ Database Structure

### 📊 **17 Bảng Chính**

| STT | Bảng | Mô Tả | Records |
|-----|------|-------|---------|
| 1   | `category` | Danh mục sản phẩm (hierarchy) | 63 |
| 2   | `customers` | Khách hàng & loyalty points | 16 |
| 3   | `employees` | Nhân viên & roles | 8 |
| 4   | `products` | Sản phẩm & inventory | 30 |
| 5   | `suppliers` | Nhà cung cấp | 8 |
| 6   | `imports` | Nhập hàng & inventory tracking | 15 |
| 7   | `orders` | Đơn hàng & sales | 20 |
| 8   | `orderdetails` | Chi tiết đơn hàng | ~60 |
| 9   | `salary` | Lương nhân viên | 8 |
| 10  | `workshifts` | Ca làm việc & penalty system | 24 |
| 11  | `penalty_rules` | Quy tắc phạt | 6 |
| 12  | `workingsession` | Phiên làm việc | 17 |
| 13  | `promotion` | Khuyến mãi | - |
| 14  | `productdisplay` | Hiển thị sản phẩm | - |
| 15  | `monthly_operating_expenses` | Chi phí hoạt động | - |
| 16  | `invoices` | Hóa đơn | - |
| 17  | `payments` | Thanh toán | - |

## 🚀 Cách Cài Đặt

### **Bước 1: Tạo Database Structure**
```sql
-- Chạy lần lượt 3 files này:
source MARKETMINI_COMPLETE_DATABASE_ENHANCED.sql;
source MARKETMINI_DATABASE_PART2.sql;
source MARKETMINI_DATABASE_PART3.sql;
```

### **Bước 2: Import Sample Data**
```sql
-- Chạy 2 files data:
source MARKETMINI_COMPREHENSIVE_DATA.sql;
source MARKETMINI_SAMPLE_TRANSACTIONS.sql;
```

### **Bước 3: Verify Installation**
```sql
-- Kiểm tra số lượng records
SELECT 
    'Categories' as table_name, COUNT(*) as records FROM category
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Customers', COUNT(*) FROM customers
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'WorkShifts', COUNT(*) FROM workshifts;
```

## 🎯 Tính Năng Nổi Bật

### 🏆 **1. Enhanced WorkShift System**
```sql
-- Penalty System Fields
penalty_amount          -- Số tiền phạt
penalty_reason          -- Lý do phạt  
salary_adjustment_percent -- % lương (100% = bình thường)
late_minutes            -- Số phút đến muộn
early_leave_minutes     -- Số phút về sớm
is_scheduled            -- Có trong lịch không
auto_checkout_penalty   -- Phạt tự động checkout
```

**Các Loại Phạt:**
- **Đến muộn**: Giảm 25% lương/giờ
- **Về sớm**: Giảm 25% lương/giờ  
- **Không check-in**: Phạt 200,000 VNĐ
- **Không checkout**: Trừ 50% lương buổi
- **Làm ngoài lịch**: Chỉ tính 75% lương
- **Vắng mặt**: Phạt 300,000 VNĐ

### 📂 **2. Category Hierarchy System**
```sql
-- Parent Categories
Thực phẩm tươi sống
├── Rau củ quả
├── Thịt gia cầm  
├── Thịt heo
├── Hải sản
└── Trứng & sữa tươi

Thực phẩm khô
├── Gạo, ngũ cốc
├── Mì, bún, phở
├── Gia vị
└── Bánh kẹo

Vệ sinh cá nhân
├── Dầu gội, sữa tắm
├── Kem đánh răng
└── Giấy vệ sinh
```

### 👥 **3. Customer Loyalty Program**
```sql
-- Customer Types & Benefits
REGULAR  : 0-10M VNĐ     -> 1% points
VIP      : 10-20M VNĐ    -> 2% points  
PREMIUM  : 20M+ VNĐ      -> 3% points
```

### 💰 **4. Advanced Financial Tracking**
- **Real-time Salary Calculation** với penalty
- **Overtime & Bonus Management**
- **Monthly Operating Expenses**
- **Profit/Loss Tracking**
- **Tax & Insurance Calculation**

## 📊 Sample Data Highlights

### **🧑‍💼 Employees (8 nhân viên)**
- **1 Admin**: Nguyễn Ba Quốc Cường (50k/giờ)
- **1 Manager**: Trần Văn Minh (35k/giờ) 
- **6 Staff**: Lương 25k/giờ

### **👥 Customers (16 khách hàng)**
- **3 PREMIUM**: Chi tiêu 18-28M VNĐ
- **4 VIP**: Chi tiêu 9-15M VNĐ
- **9 REGULAR**: Chi tiêu dưới 10M VNĐ

### **📦 Products (30 sản phẩm)**
- **Vệ sinh cá nhân**: Head&Shoulders, Pantene, Sunsilk
- **Thực phẩm**: Gạo ST25, Coca Cola, Sữa Vinamilk
- **Gia dụng**: Sunlight, Ariel, Tepro

### **💼 Realistic Business Scenarios**
- **Ca làm với penalty**: Đến muộn, về sớm, vắng mặt
- **Đơn hàng đa dạng**: 20 orders từ 45k-856k VNĐ
- **Inventory management**: 15 lần nhập hàng  
- **Salary với penalty**: Thực tế từ 8.8M-19.6M VNĐ

## 🔧 Configuration

### **Database Connection (DBConnection.java)**
```java
String url = "jdbc:mysql://localhost:3307/marketmini";
String user = "root";  
String password = "";
```

### **User Roles**
- **Role 1**: Admin (Full access)
- **Role 2**: Staff (Limited access)
- **Role 3**: Manager (Management access)

## 📈 Business Intelligence

### **Báo Cáo Tự Động**
```sql
-- Top selling products
SELECT p.product_name, SUM(od.quantity) as total_sold
FROM products p 
JOIN orderdetails od ON p.product_id = od.product_id
GROUP BY p.product_id 
ORDER BY total_sold DESC;

-- Employee performance  
SELECT e.full_name, 
       SUM(ws.total_earnings) as earnings,
       SUM(ws.penalty_amount) as penalties
FROM employees e
JOIN workshifts ws ON e.employee_id = ws.employee_id
GROUP BY e.employee_id;

-- Customer loyalty analysis
SELECT customer_type, 
       COUNT(*) as customers,
       AVG(total_spent) as avg_spent,
       AVG(points) as avg_points
FROM customers 
GROUP BY customer_type;
```

## 🔒 Security Features

### **Data Integrity**
- Foreign key constraints
- Unique constraints cho codes
- Enum validation
- Default values

### **Audit Trail**
- `created_at` timestamps
- `updated_at` auto-update
- Employee tracking cho transactions

## 🎨 UI Integration

### **Compatible với existing codebase:**
- ✅ CategoryForm.java - Category management
- ✅ WorkShiftForm.java - Enhanced shift management
- ✅ SalaryForm.java - Salary với penalty calculation
- ✅ EmployeeForm.java - Employee management
- ✅ All DAO classes - Database operations

## 📞 Support & Maintenance

### **Database Optimization**
- Indexed columns cho performance
- Proper data types
- Normalized structure

### **Backup Strategy**
```bash
# Daily backup
mysqldump -u root -p marketmini > backup_$(date +%Y%m%d).sql

# Restore  
mysql -u root -p marketmini < backup_20250625.sql
```

---

## 🎉 **Kết Luận**

Database MarketMini v3.0 Enhanced cung cấp:
- ✅ **Complete business workflow** từ nhập hàng → bán hàng → báo cáo
- ✅ **Advanced HR management** với penalty system  
- ✅ **Customer relationship** với loyalty program
- ✅ **Financial tracking** đầy đủ và chính xác
- ✅ **Scalable architecture** cho tương lai

Perfect cho việc **production deployment** và **commercial use**! 🚀

---
*Generated by AI Assistant based on complete codebase analysis* 