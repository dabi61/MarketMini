-- ================================================
-- SAMPLE DATA FOR MARKETMINI DATABASE
-- ================================================
-- Chạy file này sau khi đã tạo structure từ COMPLETE_DATABASE.sql

USE `marketmini`;

-- ================================================
-- 1. DỮ LIỆU DANH MỤC (CATEGORY)
-- ================================================
INSERT INTO `category` (`category_id`, `category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
(1, 'Dầu gội', 'Dầu gội đầu các loại', NULL, 'DAU_GOI', 1, 1),
(2, 'Dầu ăn', 'Dầu ăn các loại', NULL, 'DAU_AN', 1, 2),
(3, 'Sữa tươi', 'Sữa tươi các loại', NULL, 'SUA_TUOI', 1, 3);

-- Thêm danh mục con
INSERT INTO `category` (`category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
('Dầu gội cao cấp', 'Dầu gội thương hiệu cao cấp', 1, 'DAU_GOI_CC', 1, 11),
('Dầu gội phổ thông', 'Dầu gội giá rẻ, phổ thông', 1, 'DAU_GOI_PT', 1, 12),
('Dầu ăn cao cấp', 'Dầu ăn thương hiệu cao cấp', 2, 'DAU_AN_CC', 1, 21),
('Dầu ăn phổ thông', 'Dầu ăn giá rẻ', 2, 'DAU_AN_PT', 1, 22),
('Sữa tươi có đường', 'Sữa tươi các loại có đường', 3, 'SUA_CO_DUONG', 1, 31),
('Sữa tươi không đường', 'Sữa tươi không đường, ít đường', 3, 'SUA_KHONG_DUONG', 1, 32);

-- ================================================
-- 2. DỮ LIỆU KHÁCH HÀNG (CUSTOMERS)
-- ================================================
INSERT INTO `customers` (`customer_id`, `phone_number`, `full_name`, `points`) VALUES
(1, '0442452424', 'Chị Hạnh', 5000),
(2, '0356246724', 'Chị Minh', 2500),
(3, '0352507345', 'Anh Cường Nguyễn', 24936),
(4, '0726846578', 'Chị Thủy', 1200),
(5, '0', 'Vô danh', 6000),
(6, '0973847374', 'Chị Thủy Lan', 800),
(7, '04927475648', 'Chị Hạnh Thu', 3200),
(8, '0434526345', 'Anh Quang', 3000),
(9, '0987654321', 'Anh Minh Tuấn', 1500),
(10, '0123456789', 'Chị Nga', 2200);

-- ================================================
-- 3. DỮ LIỆU NHÂN VIÊN (EMPLOYEES)
-- ================================================
INSERT INTO `employees` (`employee_id`, `employee_name`, `password`, `full_name`, `sex`, `role`, `phone`, `email`, `date`, `is_active`) VALUES
(1, 'admin1', 'pass1', 'Nguyen Ba Quoc Cuong', 'Nam', 1, '0382951529', 'cuongngba7@gmail.com', '2025-06-02', 1),
(2, 'baphu', '1', 'Nguyen Ba Phu', 'Nam', 2, '0382951523', 'baphu7@gmail.com', '2025-06-12', 1),
(3, 'linhstaff', 'linh123', 'Lê Thị Linh', 'Nữ', 2, '0987656557', 'linh@gmail.com', '2025-06-24', 1),
(4, 'ducstaff', '1234', 'Lê Văn Đức', 'Nam', 2, '0945324366', 'duc@gmail.com', '2025-06-06', 1),
(5, 'maistaff', 'mai456', 'Nguyễn Thị Mai', 'Nữ', 2, '0332343232', 'mai@gmail.com', '2025-06-23', 1),
(6, 'hungstaff', 'hung789', 'Trần Văn Hùng', 'Nam', 2, '0912345678', 'hung@gmail.com', '2025-06-20', 1);

-- ================================================
-- 4. DỮ LIỆU NHÀ CUNG CẤP (SUPPLIERS)
-- ================================================
INSERT INTO `suppliers` (`supplier_id`, `supplier_name`, `phone`, `address`, `email`, `is_active`) VALUES
(1, 'CT TNHH Vạn Xuân', '0979932423', 'Kim Liên, Đống Đa, Hà Nội', 'vanxuanspl@gmail.com', 1),
(2, 'CT TNHH CALOFIC', '0747164638', 'Thanh Xuân, Hà Nội', 'calofit_ct@gmail.com', 1),
(3, 'Công ty Cổ phần sữa Việt Nam - Vinamilk', '0384726342', 'Số 10, đường Tân Trào, Phường Tân Phú, Quận 7, TP.HCM', 'vinamilkvn@gmail.com', 1),
(4, 'Công ty Xuân Hạ', '0982342342', 'Hà Nội', 'xuanha@gmail.com', 1),
(5, 'Công ty Hòa Hải', '0333232239', 'Thanh Xuân, Hà Nội', 'hoahai@gmail.com', 1);

-- ================================================
-- 5. DỮ LIỆU SẢN PHẨM (PRODUCTS)
-- ================================================
INSERT INTO `products` (`product_id`, `product_name`, `category_id`, `price`, `stock_quantity`, `unit`, `is_active`) VALUES
(1, 'Thái Dương Xanh', 1, 15000, 4994, 'dây', 1),
(2, 'Dầu gội Thái Dương', 1, 12000, 986, 'dây', 1),
(3, 'Dầu gội PANTENE', 1, 145000, 283, 'chai', 1),
(4, 'Dầu ăn Cái Lân 400ml', 2, 45000, 193, 'chai', 1),
(5, 'Dầu ăn Neptune 2l', 2, 125000, 250, 'chai', 1),
(6, 'Dầu ăn Meizan 1l', 2, 55000, 3195, 'chai', 1),
(7, 'Dầu gội Head and Shoulder', 1, 13000, 1996, 'dây', 1),
(8, 'Dầu ăn Simple', 2, 60000, 300, 'chai', 1),
(9, 'Sữa tươi tiệt trùng GreenFarm rất ít đường - Vinamilk', 3, 35000, 1997, 'lốc', 1),
(10, 'Sữa tươi tiệt trùng GreenFarm tổ yến - Vinamilk', 3, 35000, 2000, 'lốc', 1),
(11, 'Sữa tươi tiệt trùng 100% Không đường - Vinamilk', 3, 20000, 2000, 'lốc', 1),
(12, 'Sữa tươi tiệt trùng 100% Sôcôla - Vinamilk', 3, 20000, 1999, 'lốc', 1),
(13, 'Sữa tươi tiệt trùng 100% Hương Dâu - Vinamilk', 3, 20000, 2000, 'lốc', 1),
(14, 'Dầu Gội Dược Liệu Nguyên Xuân Dưỡng Tóc 450ml', 1, 145000, 300, 'chai', 1),
(15, 'Dầu ăn cao cấp Olivia', 2, 85000, 150, 'chai', 1);

-- ================================================
-- 6. DỮ LIỆU LƯƠNG (SALARY)
-- ================================================
INSERT INTO `salary` (`salary_id`, `employee_id`, `total_hours`, `hourly_wage`, `bonus`, `payment_date`, `created_date`, `period_month`, `period_year`) VALUES
(1, 1, 160.00, 30000, 500000, '2025-06-30', '2025-06-24', 6, 2025),
(2, 2, 160.00, 18000, 200000, NULL, '2025-06-24', 6, 2025),
(3, 3, 160.00, 18000, 0, NULL, '2025-06-24', 6, 2025),
(4, 4, 160.00, 18000, 0, NULL, '2025-06-24', 6, 2025),
(5, 5, 160.00, 18000, 150000, NULL, '2025-06-24', 6, 2025),
(6, 6, 160.00, 18000, 0, NULL, '2025-06-24', 6, 2025);

-- ================================================
-- 7. DỮ LIỆU CA LÀM VIỆC (WORKSHIFTS)
-- ================================================
INSERT INTO `workshifts` (`shift_id`, `employee_id`, `shift_date`, `shift_type`, `start_time`, `end_time`, `planned_hours`, `actual_hours`, `overtime_hours`, `status`, `notes`) VALUES
-- Ca làm tháng 6/2025
(1, 1, '2025-06-24', 'FULL', '08:00:00', '17:00:00', 8.00, 8.00, 0.00, 'COMPLETED', 'Ca làm bình thường'),
(2, 2, '2025-06-24', 'SANG', '08:00:00', '12:00:00', 4.00, 4.00, 0.00, 'COMPLETED', ''),
(3, 3, '2025-06-24', 'CHIEU', '13:00:00', '18:00:00', 5.00, 5.00, 0.00, 'COMPLETED', ''),
(4, 4, '2025-06-24', 'TOI', '18:00:00', '22:00:00', 4.00, 4.00, 0.00, 'COMPLETED', ''),
(5, 5, '2025-06-24', 'FULL', '08:00:00', '17:00:00', 8.00, 7.50, 0.00, 'COMPLETED', 'Về sớm 30 phút'),
(6, 6, '2025-06-24', 'CHIEU', '13:00:00', '18:00:00', 5.00, 5.50, 0.50, 'COMPLETED', 'Làm thêm 30 phút'),

-- Ca làm ngày 25/06/2025
(7, 1, '2025-06-25', 'FULL', '08:00:00', NULL, 8.00, NULL, 0.00, 'IN_PROGRESS', ''),
(8, 2, '2025-06-25', 'SANG', '08:00:00', NULL, 4.00, NULL, 0.00, 'IN_PROGRESS', ''),
(9, 3, '2025-06-25', 'CHIEU', '13:00:00', NULL, 5.00, NULL, 0.00, 'SCHEDULED', ''),
(10, 4, '2025-06-25', 'TOI', '18:00:00', NULL, 4.00, NULL, 0.00, 'SCHEDULED', ''),
(11, 5, '2025-06-25', 'FULL', '08:00:00', NULL, 8.00, NULL, 0.00, 'SCHEDULED', ''),
(12, 6, '2025-06-25', 'CHIEU', '13:00:00', NULL, 5.00, NULL, 0.00, 'SCHEDULED', ''),

-- Ca làm tuần trước (18-23/06/2025)
(13, 1, '2025-06-23', 'FULL', '08:00:00', '17:30:00', 8.00, 8.50, 0.50, 'COMPLETED', 'Overtime 30 phút'),
(14, 2, '2025-06-23', 'SANG', '08:00:00', '12:00:00', 4.00, 4.00, 0.00, 'COMPLETED', ''),
(15, 3, '2025-06-23', 'CHIEU', '13:00:00', '18:00:00', 5.00, 5.00, 0.00, 'COMPLETED', ''),
(16, 4, '2025-06-23', 'TOI', '18:00:00', '22:15:00', 4.00, 4.25, 0.25, 'COMPLETED', 'Làm thêm 15 phút'),
(17, 5, '2025-06-23', 'FULL', '08:00:00', '17:00:00', 8.00, 8.00, 0.00, 'COMPLETED', ''),

(18, 1, '2025-06-22', 'FULL', '08:00:00', '17:00:00', 8.00, 8.00, 0.00, 'COMPLETED', ''),
(19, 2, '2025-06-22', 'SANG', '08:00:00', '12:00:00', 4.00, 4.00, 0.00, 'COMPLETED', ''),
(20, 3, '2025-06-22', 'CHIEU', '13:00:00', '18:00:00', 5.00, 5.00, 0.00, 'COMPLETED', ''),
(21, 4, '2025-06-22', 'TOI', '18:00:00', '22:00:00', 4.00, 4.00, 0.00, 'COMPLETED', ''),
(22, 5, '2025-06-22', 'FULL', '08:00:00', '17:00:00', 8.00, 8.00, 0.00, 'COMPLETED', ''),

-- Một số ca vắng mặt
(23, 3, '2025-06-21', 'CHIEU', '13:00:00', NULL, 5.00, NULL, 0.00, 'ABSENT', 'Vắng mặt không phép'),
(24, 4, '2025-06-20', 'TOI', '18:00:00', NULL, 4.00, NULL, 0.00, 'ABSENT', 'Ốm');

-- ================================================
-- 8. DỮ LIỆU PHIÊN LÀM VIỆC (WORKINGSESSION)
-- ================================================
INSERT INTO `workingsession` (`working_session_id`, `employee_id`, `login_time`, `logout_time`, `working_hours`, `date`, `work_status`, `shift_id`) VALUES
(1, 1, '2025-06-24 08:00:00', '2025-06-24 17:00:00', 8.00, '2025-06-24', 'COMPLETED', 1),
(2, 2, '2025-06-24 08:00:00', '2025-06-24 12:00:00', 4.00, '2025-06-24', 'COMPLETED', 2),
(3, 3, '2025-06-24 13:00:00', '2025-06-24 18:00:00', 5.00, '2025-06-24', 'COMPLETED', 3),
(4, 4, '2025-06-24 18:00:00', '2025-06-24 22:00:00', 4.00, '2025-06-24', 'COMPLETED', 4),
(5, 5, '2025-06-24 08:00:00', '2025-06-24 16:30:00', 7.50, '2025-06-24', 'COMPLETED', 5),
(6, 6, '2025-06-24 13:00:00', '2025-06-24 18:30:00', 5.50, '2025-06-24', 'COMPLETED', 6),
(7, 1, '2025-06-25 08:00:00', NULL, NULL, '2025-06-25', 'IN_PROGRESS', 7),
(8, 2, '2025-06-25 08:00:00', NULL, NULL, '2025-06-25', 'IN_PROGRESS', 8);

-- ================================================
-- 9. DỮ LIỆU NHẬP HÀNG (IMPORTS)
-- ================================================
INSERT INTO `imports` (`import_id`, `product_id`, `supplier_id`, `quantity`, `import_price`, `import_date`, `employee_id`, `notes`) VALUES
(1, 1, 1, 2000, 12000, '2025-06-11', 1, 'Lô hàng tháng 6'),
(2, 2, 1, 3000, 10000, '2025-06-11', 1, ''),
(3, 3, 1, 1000, 120000, '2025-06-13', 1, 'Hàng cao cấp'),
(4, 4, 2, 300, 40000, '2025-06-12', 1, ''),
(5, 5, 2, 200, 110000, '2025-06-13', 1, ''),
(6, 6, 2, 250, 50000, '2025-06-13', 1, ''),
(7, 7, 1, 3000, 11000, '2025-06-12', 1, ''),
(8, 8, 2, 200, 55000, '2025-06-13', 1, ''),
(9, 9, 3, 2000, 30000, '2025-06-17', 1, 'Sữa Vinamilk'),
(10, 10, 3, 300, 30000, '2025-06-14', 1, ''),
(11, 11, 3, 1000, 18000, '2025-06-21', 1, ''),
(12, 12, 3, 1000, 18000, '2025-06-21', 1, ''),
(13, 13, 3, 1000, 18000, '2025-06-21', 1, ''),
(14, 14, 1, 300, 120000, '2025-06-21', 1, 'Dầu gội cao cấp'),
(15, 15, 4, 150, 70000, '2025-06-22', 1, 'Dầu ăn cao cấp');

-- ================================================
-- 10. DỮ LIỆU ĐƠN HÀNG (ORDERS)
-- ================================================
INSERT INTO `orders` (`order_id`, `employee_id`, `order_date`, `total_amount`, `customer_id`, `final_amount`, `discount_amount`, `points_used`) VALUES
(1, 1, '2025-06-17', 188400, 5, 188400, 0, 0),
(2, 2, '2025-06-17', 645600, 3, 642600, 3000, 0),
(3, 1, '2025-06-17', 206400, 5, 206400, 0, 0),
(4, 3, '2025-06-17', 174000, 5, 169872, 4128, 0),
(5, 1, '2025-06-17', 115200, 3, 86376, 28824, 0),
(6, 2, '2025-06-17', 295200, 3, 295200, 0, 0),
(7, 4, '2025-06-17', 14400, 3, 6192, 8208, 0),
(8, 1, '2025-06-17', 300000, 5, 292392, 7608, 0),
(9, 2, '2025-06-17', 1232400, 3, 1232400, 0, 0),
(10, 3, '2025-06-21', 150000, 8, 150000, 0, 0),
(11, 1, '2025-06-22', 89000, 1, 89000, 0, 0),
(12, 2, '2025-06-22', 125000, 4, 120000, 5000, 0),
(13, 4, '2025-06-23', 67000, 6, 67000, 0, 0),
(14, 3, '2025-06-23', 180000, 2, 175000, 5000, 0),
(15, 5, '2025-06-24', 95000, 9, 95000, 0, 0);

-- ================================================
-- 11. DỮ LIỆU CHI TIẾT ĐƠN HÀNG (ORDERDETAILS)
-- ================================================
INSERT INTO `orderdetails` (`order_detail_id`, `order_id`, `product_id`, `quantity`, `unit_price`) VALUES
-- Order 1
(1, 1, 2, 1, 12000),
(2, 1, 3, 1, 145000),
-- Order 2  
(3, 2, 4, 2, 45000),
(4, 2, 3, 3, 145000),
(5, 2, 7, 1, 13000),
-- Order 3
(6, 3, 1, 1, 15000),
(7, 3, 2, 1, 12000),
(8, 3, 3, 1, 145000),
-- Order 4
(9, 4, 3, 1, 145000),
-- Order 5
(10, 5, 1, 1, 15000),
(11, 5, 2, 3, 12000),
(12, 5, 4, 1, 45000),
-- Order 6
(13, 6, 6, 4, 55000),
(14, 6, 7, 2, 13000),
-- Order 7
(15, 7, 2, 1, 12000),
-- Order 8
(16, 8, 2, 5, 12000),
(17, 8, 3, 1, 145000),
(18, 8, 4, 1, 45000),
-- Order 9
(19, 9, 2, 1, 12000),
(20, 9, 3, 7, 145000),
-- Order 10
(21, 10, 9, 3, 35000),
(22, 10, 12, 1, 20000),
-- Order 11-15
(23, 11, 1, 2, 15000),
(24, 11, 7, 3, 13000),
(25, 12, 5, 1, 125000),
(26, 13, 6, 1, 55000),
(27, 13, 11, 1, 20000),
(28, 14, 6, 2, 55000),
(29, 14, 10, 2, 35000),
(30, 15, 1, 1, 15000),
(31, 15, 6, 1, 55000),
(32, 15, 11, 1, 20000);

-- ================================================
-- 12. DỮ LIỆU HIỂN THỊ SẢN PHẨM (PRODUCTDISPLAY)
-- ================================================
INSERT INTO `productdisplay` (`display_id`, `product_id`, `row`, `floor`, `start_date`, `end_date`) VALUES
(1, 3, 'A1', '2', '2025-06-23', '2025-06-30'),
(2, 4, 'A1', '1', '2025-06-22', '2025-06-29'),
(3, 6, 'B2', '1', '2025-06-20', '2025-06-27'),
(4, 9, 'C1', '2', '2025-06-21', '2025-06-28');

-- ================================================
-- 13. DỮ LIỆU CHI PHÍ HOẠT ĐỘNG (MONTHLY_OPERATING_EXPENSES)
-- ================================================
INSERT INTO `monthly_operating_expenses` (`expense_id`, `month_year`, `electricity_cost`, `rent_cost`, `water_cost`, `repair_cost`, `other_cost`, `notes`) VALUES
(1, '2025-06-01', 800000, 3000000, 150000, 200000, 100000, 'Chi phí tháng 6/2025'),
(2, '2025-05-01', 750000, 3000000, 120000, 0, 50000, 'Chi phí tháng 5/2025'),
(3, '2025-04-01', 720000, 3000000, 140000, 500000, 80000, 'Chi phí tháng 4/2025 (có sửa chữa)');

-- ================================================
-- 14. DỮ LIỆU KHUYẾN MÃI (PROMOTION) - Mẫu
-- ================================================
INSERT INTO `promotion` (`promotion_id`, `promotion_name`, `start_date`, `end_date`, `product_id`, `discount`, `discounted_price`, `original_price`, `is_active`) VALUES
(1, 'Khuyến mãi dầu gội mùa hè', '2025-06-20', '2025-06-30', 3, 10, 130500, 145000, 1),
(2, 'Giảm giá dầu ăn', '2025-06-15', '2025-06-25', 5, 15, 106250, 125000, 1),
(3, 'Combo sữa tươi', '2025-06-22', '2025-06-29', 9, 5, 33250, 35000, 1);

-- ================================================
-- 15. DỮ LIỆU HÓA ĐƠN (INVOICES) - Mẫu
-- ================================================
INSERT INTO `invoices` (`invoice_id`, `order_id`, `issue_date`, `total_amount`) VALUES
(1, 1, '2025-06-17', 188400),
(2, 2, '2025-06-17', 642600),
(3, 3, '2025-06-17', 206400),
(4, 4, '2025-06-17', 169872),
(5, 5, '2025-06-17', 86376),
(6, 6, '2025-06-17', 295200),
(7, 7, '2025-06-17', 6192),
(8, 8, '2025-06-17', 292392),
(9, 9, '2025-06-17', 1232400),
(10, 10, '2025-06-21', 150000);

-- ================================================
-- SUMMARY AND VERIFICATION
-- ================================================
-- Kiểm tra dữ liệu đã tạo
SELECT 'Employees' as table_name, COUNT(*) as record_count FROM employees
UNION ALL
SELECT 'Categories', COUNT(*) FROM category
UNION ALL
SELECT 'Products', COUNT(*) FROM products
UNION ALL
SELECT 'Customers', COUNT(*) FROM customers
UNION ALL
SELECT 'Suppliers', COUNT(*) FROM suppliers
UNION ALL
SELECT 'Work Shifts', COUNT(*) FROM workshifts
UNION ALL
SELECT 'Orders', COUNT(*) FROM orders
UNION ALL
SELECT 'Order Details', COUNT(*) FROM orderdetails
UNION ALL
SELECT 'Imports', COUNT(*) FROM imports
UNION ALL
SELECT 'Working Sessions', COUNT(*) FROM workingsession;

-- Kiểm tra ca làm hiện tại
SELECT 
    e.full_name,
    ws.shift_date,
    ws.shift_type,
    ws.start_time,
    ws.status,
    ws.notes
FROM workshifts ws
JOIN employees e ON ws.employee_id = e.employee_id
WHERE ws.status = 'IN_PROGRESS'
ORDER BY ws.shift_date, ws.start_time; 