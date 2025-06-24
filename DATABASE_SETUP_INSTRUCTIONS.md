# HƯỚNG DẪN CÀI ĐẶT DATABASE MARKETMINI HOÀN CHỈNH

## Tổng quan
Database MarketMini phiên bản 2.0 với tính năng quản lý ca làm việc và thống kê phân quyền đã được thiết kế hoàn chỉnh từ đầu.

## Cấu trúc Files

```
MarketMini/
├── COMPLETE_DATABASE.sql         # Structure: Tables, constraints, indexes
├── SAMPLE_DATA.sql              # Sample data cho tất cả bảng
├── PROCEDURES_VIEWS.sql         # Views, stored procedures, functions
└── DATABASE_SETUP_INSTRUCTIONS.md
```

## Thứ tự Import Database

### Bước 1: Cài đặt cấu trúc cơ bản
```sql
-- Import structure và tạo tất cả bảng
mysql -u root -p < COMPLETE_DATABASE.sql
```

### Bước 2: Import dữ liệu mẫu  
```sql
-- Import dữ liệu mẫu cho tất cả bảng
mysql -u root -p < SAMPLE_DATA.sql
```

### Bước 3: Cài đặt Views, Procedures và Functions
```sql
-- Import stored procedures, functions và views
mysql -u root -p < PROCEDURES_VIEWS.sql
```

## Cấu trúc Database

### 🏗️ Bảng chính

1. **`employees`** - Nhân viên (6 records)
   - Admin: admin1/pass1 (role=1)
   - Staff: baphu/1, linhstaff/linh123, ducstaff/1234, maistaff/mai456, hungstaff/hung789 (role=2)

2. **`category`** - Danh mục sản phẩm (9 records với hierarchy)
   - Danh mục cha: Dầu gội, Dầu ăn, Sữa tươi
   - Danh mục con: Phân loại cao cấp/phổ thông

3. **`products`** - Sản phẩm (15 records)
   - Dầu gội các loại, Dầu ăn, Sữa tươi Vinamilk
   - Stock quantity đã được set realistic

4. **`customers`** - Khách hàng (10 records với points)

5. **`suppliers`** - Nhà cung cấp (5 records)

### 🆕 Tính năng mới - Quản lý Ca làm việc

6. **`workshifts`** - Ca làm việc (24 records)
   - Enum shift_type: SANG, CHIEU, TOI, FULL
   - Enum status: SCHEDULED, IN_PROGRESS, COMPLETED, ABSENT
   - Dữ liệu mẫu: 24-25/06/2025 với các trạng thái khác nhau

7. **`workingsession`** - Phiên làm việc (8 records)
   - Liên kết với workshifts
   - Track login/logout times

### 📊 Dữ liệu kinh doanh

8. **`orders`** - Đơn hàng (15 records từ 17-24/06/2025)
9. **`orderdetails`** - Chi tiết đơn hàng (32 records)
10. **`imports`** - Nhập hàng (15 records)
11. **`promotion`** - Khuyến mãi (3 active promotions)
12. **`productdisplay`** - Hiển thị sản phẩm (4 records)
13. **`monthly_operating_expenses`** - Chi phí vận hành (3 months)
14. **`invoices`** - Hóa đơn (10 records)

### 🔍 Views & Analytics

- **`v_employee_shift_summary`** - Thống kê ca làm nhân viên
- **`v_product_sales_summary`** - Thống kê doanh thu sản phẩm  
- **`v_employee_sales_summary`** - Thống kê bán hàng nhân viên
- **`v_inventory_status`** - Tình trạng tồn kho

### ⚙️ Stored Procedures

- **`CloseWorkShift(employee_id, shift_date, end_time, notes)`** - Chốt ca làm
- **`StartWorkShift(employee_id, shift_date)`** - Bắt đầu ca làm
- **`CreateBulkShifts(start_date, end_date, employee_list)`** - Tạo ca hàng loạt

### 🧮 Functions

- **`CalculateShiftEarnings(employee_id, start_date, end_date)`** - Tính lương theo ca
- **`CalculateAttendanceRate(employee_id, month, year)`** - Tỷ lệ chuyên cần
- **`CheckShiftConflict(...)`** - Kiểm tra ca trùng lặp

### 🤖 Triggers

- **`update_stock_after_order`** - Tự động trừ kho khi bán
- **`update_stock_after_import`** - Tự động cộng kho khi nhập
- **`calculate_customer_points`** - Tự động tính điểm thưởng

## Testing Database

### Kiểm tra import thành công
```sql
USE marketmini;

-- Kiểm tra số lượng records
SELECT 'Employees' as table_name, COUNT(*) as record_count FROM employees
UNION ALL SELECT 'Categories', COUNT(*) FROM category
UNION ALL SELECT 'Products', COUNT(*) FROM products
UNION ALL SELECT 'Work Shifts', COUNT(*) FROM workshifts;

-- Kiểm tra ca làm hiện tại
SELECT e.full_name, ws.shift_date, ws.shift_type, ws.status
FROM workshifts ws
JOIN employees e ON ws.employee_id = e.employee_id
WHERE ws.status = 'IN_PROGRESS';
```

### Test stored procedures
```sql
-- Test chốt ca
CALL CloseWorkShift(1, '2025-06-25', '17:30:00', 'Test chốt ca');

-- Test tính lương
SELECT CalculateShiftEarnings(1, '2025-06-01', '2025-06-30') as monthly_earnings;

-- Test view thống kê
SELECT * FROM v_employee_shift_summary WHERE employee_id = 1 LIMIT 5;
```

## Tài khoản Login

### Admin Account
- **Username:** admin1
- **Password:** pass1
- **Role:** 1 (Admin) - Full dashboard với quản lý ca làm

### Staff Accounts
- **Username:** baphu, **Password:** 1 (Role: 2)
- **Username:** linhstaff, **Password:** linh123 (Role: 2)
- **Username:** ducstaff, **Password:** 1234 (Role: 2)
- **Username:** maistaff, **Password:** mai456 (Role: 2)
- **Username:** hungstaff, **Password:** hung789 (Role: 2)

Staff accounts có dashboard riêng với tính năng chốt ca.

## Dữ liệu Test Scenarios

### Scenario 1: Ca làm hiện tại
- Admin (ID=1) và Staff baphu (ID=2) đang trong ca ngày 25/06/2025
- Status: IN_PROGRESS - có thể test chốt ca

### Scenario 2: Lịch sử ca làm
- Tất cả nhân viên có lịch sử ca từ 20-24/06/2025
- Bao gồm ca COMPLETED, ABSENT với overtime hours

### Scenario 3: Doanh thu
- 15 đơn hàng từ 17-24/06/2025
- Total revenue: ~4.2 triệu VNĐ
- Top products: Dầu gội PANTENE, Sữa Vinamilk

### Scenario 4: Tồn kho
- Sản phẩm sắp hết: stock <= 50
- Sản phẩm còn ít: stock <= 200  
- Warning alerts sẵn sàng

## Performance & Indexes

Database đã được tối ưu với indexes:
- `idx_orders_employee_date` cho queries thống kê
- `idx_workshifts_employee_month` cho ca làm việc
- `idx_products_stock` cho inventory management
- Foreign key constraints đầy đủ

## Backup & Maintenance

```sql
-- Backup toàn bộ database
mysqldump -u root -p marketmini > marketmini_backup.sql

-- Backup chỉ structure
mysqldump -u root -p --no-data marketmini > marketmini_structure.sql

-- Cleanup old data (older than 3 months)
DELETE FROM workshifts WHERE shift_date < DATE_SUB(CURRENT_DATE, INTERVAL 3 MONTH);
```

## Troubleshooting

### Lỗi thường gặp:

1. **Foreign key constraint fails**
   - Đảm bảo import đúng thứ tự: Structure → Data → Procedures

2. **Function/Procedure already exists**
   - Sử dụng `CREATE OR REPLACE` hoặc DROP trước khi CREATE

3. **Charset issues**
   - Database sử dụng utf8mb4_general_ci

4. **Permission denied**
   - Cần quyền CREATE, ALTER, EXECUTE cho user MySQL

### Support
Nếu gặp vấn đề, kiểm tra:
- MySQL version >= 5.7
- Quyền user đầy đủ
- Charset/Collation settings
- Import đúng thứ tự files

---

**📝 Lưu ý:** Database này được thiết kế cho môi trường development/testing. Trước khi deploy production, cần:
- Review security settings
- Optimize performance parameters  
- Setup proper backup schedule
- Configure user permissions chi tiết hơn 