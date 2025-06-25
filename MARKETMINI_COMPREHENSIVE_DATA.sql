-- ====================================================================
-- MARKETMINI - COMPREHENSIVE SAMPLE DATA
-- ====================================================================
-- Chạy sau khi đã tạo database structure
-- Version: 3.0 Enhanced with realistic business data

USE `marketmini`;

-- ====================================================================
-- 1. DỮ LIỆU DANH MỤC (CATEGORY) - Hierarchy Complete
-- ====================================================================
INSERT INTO `category` (`category_id`, `category_name`, `description`, `parent_category_id`, `category_code`, `is_active`, `display_order`) VALUES
-- Root Categories
(1, 'Thực phẩm tươi sống', 'Các loại thực phẩm tươi, sống', NULL, 'FRESH_FOOD', 1, 1),
(2, 'Thực phẩm khô', 'Các loại thực phẩm khô, chế biến', NULL, 'DRY_FOOD', 1, 2),
(3, 'Đồ uống', 'Các loại nước uống, giải khát', NULL, 'BEVERAGE', 1, 3),
(4, 'Vệ sinh cá nhân', 'Đồ dùng vệ sinh cá nhân', NULL, 'PERSONAL_CARE', 1, 4),
(5, 'Gia dụng', 'Đồ gia dụng, sinh hoạt', NULL, 'HOUSEHOLD', 1, 5),
(6, 'Đồ chơi trẻ em', 'Đồ chơi và đồ dùng trẻ em', NULL, 'TOYS', 1, 6),

-- Sub Categories - Thực phẩm tươi sống
(11, 'Rau củ quả', 'Rau, củ, quả tươi', 1, 'VEGETABLES', 1, 11),
(12, 'Thịt gia cầm', 'Thịt gà, vịt, ngan', 1, 'POULTRY', 1, 12),
(13, 'Thịt heo', 'Các loại thịt heo', 1, 'PORK', 1, 13),
(14, 'Thịt bò', 'Các loại thịt bò', 1, 'BEEF', 1, 14),
(15, 'Hải sản', 'Cá, tôm, cua, mực', 1, 'SEAFOOD', 1, 15),
(16, 'Trứng & sữa tươi', 'Trứng và các sản phẩm sữa tươi', 1, 'DAIRY_FRESH', 1, 16),

-- Sub Categories - Thực phẩm khô
(21, 'Gạo, ngũ cốc', 'Gạo và các loại ngũ cốc', 2, 'RICE_GRAIN', 1, 21),
(22, 'Mì, bún, phở', 'Các loại mì, bún, phở khô', 2, 'NOODLES', 1, 22),
(23, 'Đồ hộp', 'Thực phẩm đóng hộp', 2, 'CANNED_FOOD', 1, 23),
(24, 'Gia vị', 'Các loại gia vị, nước mắm', 2, 'CONDIMENTS', 1, 24),
(25, 'Bánh kẹo', 'Bánh ngọt, kẹo, snack', 2, 'SNACKS', 1, 25),

-- Sub Categories - Đồ uống
(31, 'Nước ngọt', 'Coca, pepsi, các loại nước ngọt', 3, 'SOFT_DRINKS', 1, 31),
(32, 'Bia rượu', 'Bia, rượu các loại', 3, 'ALCOHOL', 1, 32),
(33, 'Nước uống healthy', 'Nước lọc, nước khoáng, trà', 3, 'HEALTHY_DRINKS', 1, 33),
(34, 'Cà phê, trà', 'Cà phê, trà các loại', 3, 'COFFEE_TEA', 1, 34),

-- Sub Categories - Vệ sinh cá nhân
(41, 'Dầu gội, sữa tắm', 'Sản phẩm tắm gội', 4, 'BATH_SHOWER', 1, 41),
(42, 'Kem đánh răng', 'Kem đánh răng, bàn chải', 4, 'DENTAL_CARE', 1, 42),
(43, 'Mỹ phẩm', 'Kem dưỡng, trang điểm', 4, 'COSMETICS', 1, 43),
(44, 'Giấy vệ sinh', 'Giấy vệ sinh, khăn giấy', 4, 'TISSUE_PAPER', 1, 44),

-- Sub Categories - Gia dụng  
(51, 'Dụng cụ bếp', 'Chảo, nồi, dao, thớt', 5, 'KITCHEN_TOOLS', 1, 51),
(52, 'Chất tẩy rửa', 'Nước rửa chén, bột giặt', 5, 'CLEANING', 1, 52),
(53, 'Dụng cụ vệ sinh', 'Chổi, cây lau nhà', 5, 'CLEANING_TOOLS', 1, 53),
(54, 'Đồ điện gia dụng', 'Quạt, bàn ủi, nồi cơm điện', 5, 'ELECTRONICS', 1, 54),

-- Sub Categories - Đồ chơi trẻ em
(61, 'Đồ chơi 0-3 tuổi', 'Đồ chơi cho trẻ nhỏ', 6, 'TOYS_0_3', 1, 61),
(62, 'Đồ chơi 4-12 tuổi', 'Đồ chơi cho trẻ lớn', 6, 'TOYS_4_12', 1, 62),
(63, 'Đồ dùng học tập', 'Sách vở, bút viết', 6, 'SCHOOL_SUPPLIES', 1, 63);

-- ====================================================================
-- 2. DỮ LIỆU KHÁCH HÀNG (CUSTOMERS) - Enhanced với Customer Types
-- ====================================================================
INSERT INTO `customers` (`customer_id`, `phone_number`, `full_name`, `points`, `total_spent`, `member_since`, `last_purchase`, `customer_type`) VALUES
(1, '0987654321', 'Nguyễn Thị Hạnh', 15500, 12500000, '2024-01-15', '2025-06-24', 'VIP'),
(2, '0356246724', 'Trần Văn Minh', 8200, 6800000, '2024-02-20', '2025-06-23', 'REGULAR'),
(3, '0352507345', 'Lê Thị Cường', 35000, 28500000, '2023-12-10', '2025-06-24', 'PREMIUM'),
(4, '0726846578', 'Phạm Thị Thủy', 4500, 3200000, '2024-03-15', '2025-06-22', 'REGULAR'),
(5, '0973847374', 'Hoàng Thị Lan', 12800, 9800000, '2024-01-05', '2025-06-24', 'VIP'),
(6, '0927475648', 'Vũ Thị Thu', 6300, 4900000, '2024-04-10', '2025-06-21', 'REGULAR'),
(7, '0434526345', 'Đỗ Văn Quang', 18200, 14500000, '2023-11-20', '2025-06-24', 'VIP'),
(8, '0987123456', 'Bùi Minh Tuấn', 2100, 1800000, '2024-05-15', '2025-06-20', 'REGULAR'),
(9, '0123456789', 'Lý Thị Nga', 5600, 4200000, '2024-03-25', '2025-06-23', 'REGULAR'),
(10, '0369852147', 'Cao Văn Đức', 25800, 20100000, '2023-10-08', '2025-06-24', 'PREMIUM'),

-- Thêm khách hàng thường xuyên
(11, '0456789123', 'Ngô Thị Mai', 3200, 2500000, '2024-06-01', '2025-06-24', 'REGULAR'),
(12, '0654321987', 'Trịnh Văn Hùng', 8900, 7200000, '2024-02-14', '2025-06-23', 'REGULAR'),
(13, '0789123456', 'Đặng Thị Linh', 16200, 13800000, '2023-12-25', '2025-06-24', 'VIP'),
(14, '0321654987', 'Phan Văn Nam', 1800, 1200000, '2024-05-20', '2025-06-19', 'REGULAR'),
(15, '0147258369', 'Vương Thị Hoa', 22500, 18900000, '2023-09-15', '2025-06-24', 'PREMIUM'),

-- Khách vãng lai
(16, '0', 'Khách vãng lai', 0, 0, NULL, NULL, 'REGULAR');

-- ====================================================================  
-- 3. DỮ LIỆU NHÂN VIÊN (EMPLOYEES) - Complete với Employee Codes
-- ====================================================================
INSERT INTO `employees` (`employee_id`, `employee_name`, `employee_code`, `password`, `full_name`, `sex`, `role`, `phone`, `email`, `address`, `hire_date`, `birth_date`, `base_salary`, `hourly_wage`, `is_active`) VALUES
(1, 'admin1', 'EMP001', 'pass1', 'Nguyễn Ba Quốc Cường', 'Nam', 1, '0382951529', 'cuongngba7@gmail.com', 'Hoàng Mai, Hà Nội', '2023-01-15', '1995-03-20', 12000000.00, 50000.00, 1),
(2, 'manager1', 'EMP002', 'manager123', 'Trần Văn Minh', 'Nam', 3, '0382951523', 'minh.manager@gmail.com', 'Cầu Giấy, Hà Nội', '2023-02-01', '1990-07-15', 8000000.00, 35000.00, 1),
(3, 'linhstaff', 'EMP003', 'linh123', 'Lê Thị Linh', 'Nữ', 2, '0987656557', 'linh@gmail.com', 'Đống Đa, Hà Nội', '2023-03-10', '1998-12-08', 6000000.00, 25000.00, 1),
(4, 'ducstaff', 'EMP004', '1234', 'Lê Văn Đức', 'Nam', 2, '0945324366', 'duc@gmail.com', 'Ba Đình, Hà Nội', '2023-04-01', '1996-09-25', 6000000.00, 25000.00, 1),
(5, 'maistaff', 'EMP005', 'mai456', 'Nguyễn Thị Mai', 'Nữ', 2, '0332343232', 'mai@gmail.com', 'Hai Bà Trưng, Hà Nội', '2023-05-15', '1997-11-10', 6000000.00, 25000.00, 1),
(6, 'hungstaff', 'EMP006', 'hung789', 'Trần Văn Hùng', 'Nam', 2, '0912345678', 'hung@gmail.com', 'Thanh Xuân, Hà Nội', '2023-06-01', '1994-05-30', 6000000.00, 25000.00, 1),
(7, 'anstaff', 'EMP007', 'an999', 'Phạm Thị An', 'Nữ', 2, '0976543210', 'an@gmail.com', 'Long Biên, Hà Nội', '2024-01-15', '1999-02-14', 6000000.00, 25000.00, 1),
(8, 'binhstaff', 'EMP008', 'binh555', 'Hoàng Văn Bình', 'Nam', 2, '0845123789', 'binh@gmail.com', 'Tây Hồ, Hà Nội', '2024-02-01', '1995-08-22', 6000000.00, 25000.00, 1);

-- ====================================================================
-- 4. DỮ LIỆU NHÀ CUNG CẤP (SUPPLIERS) - Enhanced
-- ====================================================================
INSERT INTO `suppliers` (`supplier_id`, `supplier_code`, `supplier_name`, `contact_person`, `phone`, `email`, `address`, `tax_code`, `payment_terms`, `rating`, `is_active`) VALUES
(1, 'SUP001', 'Công ty TNHH Vạn Xuân', 'Nguyễn Văn A', '0979932423', 'vanxuanspl@gmail.com', 'Kim Liên, Đống Đa, Hà Nội', '0123456789', 'Thanh toán trong 30 ngày', 4.5, 1),
(2, 'SUP002', 'Công ty TNHH CALOFIC', 'Trần Thị B', '0747164638', 'calofit_ct@gmail.com', 'Thanh Xuân, Hà Nội', '0987654321', 'Thanh toán trong 15 ngày', 4.2, 1),
(3, 'SUP003', 'Công ty Cổ phần Sữa Việt Nam - Vinamilk', 'Lê Văn C', '0384726342', 'vinamilkvn@gmail.com', 'Số 10, đường Tân Trào, Phường Tân Phú, Quận 7, TP.HCM', '0369258147', 'Thanh toán trong 45 ngày', 4.8, 1),
(4, 'SUP004', 'Công ty Xuân Hạ', 'Phạm Văn D', '0982342342', 'xuanha@gmail.com', 'Hoàng Mai, Hà Nội', '0456789123', 'Thanh toán ngay', 4.0, 1),
(5, 'SUP005', 'Công ty Hòa Hải', 'Ngô Thị E', '0333232239', 'hoahai@gmail.com', 'Thanh Xuân, Hà Nội', '0741852963', 'Thanh toán trong 30 ngày', 4.3, 1),
(6, 'SUP006', 'Tập đoàn Masan', 'Vũ Văn F', '0912345678', 'masan@gmail.com', 'Quận 1, TP.HCM', '0159753486', 'Thanh toán trong 60 ngày', 4.6, 1),
(7, 'SUP007', 'Công ty Coca-Cola Việt Nam', 'Đỗ Thị G', '0987654321', 'cocacola@gmail.com', 'Quận 3, TP.HCM', '0852741963', 'Thanh toán trong 30 ngày', 4.7, 1),
(8, 'SUP008', 'Unilever Việt Nam', 'Bùi Văn H', '0456123789', 'unilever@gmail.com', 'Quận Bình Thạnh, TP.HCM', '0753159486', 'Thanh toán trong 45 ngày', 4.5, 1);

-- ====================================================================
-- 5. DỮ LIỆU SẢN PHẨM (PRODUCTS) - Comprehensive
-- ====================================================================
INSERT INTO `products` (`product_id`, `product_code`, `product_name`, `category_id`, `price`, `cost_price`, `stock_quantity`, `min_stock_level`, `max_stock_level`, `unit`, `brand`, `description`, `is_active`) VALUES
-- Vệ sinh cá nhân - Dầu gội
(1, 'PRD001', 'Dầu gội Head & Shoulders 400ml', 41, 85000, 65000, 150, 20, 500, 'chai', 'Head & Shoulders', 'Dầu gội trị gàu hiệu quả', 1),
(2, 'PRD002', 'Dầu gội Pantene 400ml', 41, 125000, 95000, 120, 15, 400, 'chai', 'Pantene', 'Dầu gội dưỡng tóc cao cấp', 1),
(3, 'PRD003', 'Dầu gội Sunsilk 350ml', 41, 75000, 55000, 200, 25, 600, 'chai', 'Sunsilk', 'Dầu gội mềm mượt tóc', 1),
(4, 'PRD004', 'Dầu gội Clear Men 400ml', 41, 95000, 72000, 100, 15, 300, 'chai', 'Clear', 'Dầu gội dành cho nam giới', 1),

-- Thực phẩm khô - Gạo
(5, 'PRD005', 'Gạo ST25 5kg', 21, 250000, 200000, 80, 10, 200, 'túi', 'ST25', 'Gạo thơm ngon số 1 thế giới', 1),
(6, 'PRD006', 'Gạo Jasmine 5kg', 21, 180000, 145000, 120, 15, 300, 'túi', 'Jasmine', 'Gạo thơm Jasmine cao cấp', 1),
(7, 'PRD007', 'Gạo tẻ thường 5kg', 21, 120000, 95000, 200, 20, 500, 'túi', 'Việt Nam', 'Gạo tẻ thường hàng ngày', 1),

-- Đồ uống - Nước ngọt
(8, 'PRD008', 'Coca Cola 330ml', 31, 12000, 8500, 500, 50, 1500, 'lon', 'Coca Cola', 'Nước ngọt có gas Coca Cola', 1),
(9, 'PRD009', 'Pepsi 330ml', 31, 12000, 8500, 450, 50, 1500, 'lon', 'Pepsi', 'Nước ngọt có gas Pepsi', 1),
(10, 'PRD010', 'Sprite 330ml', 31, 12000, 8500, 400, 50, 1500, 'lon', 'Sprite', 'Nước ngọt vị chanh Sprite', 1),

-- Sữa tươi
(11, 'PRD011', 'Sữa tươi Vinamilk 1L', 16, 35000, 28000, 100, 15, 300, 'hộp', 'Vinamilk', 'Sữa tươi tiệt trùng không đường', 1),
(12, 'PRD012', 'Sữa tươi TH True Milk 1L', 16, 38000, 30000, 80, 10, 250, 'hộp', 'TH True Milk', 'Sữa tươi organic cao cấp', 1),
(13, 'PRD013', 'Sữa tươi Dutch Lady 1L', 16, 42000, 33000, 60, 10, 200, 'hộp', 'Dutch Lady', 'Sữa tươi nhập khẩu Hà Lan', 1),

-- Thực phẩm tươi - Rau củ
(14, 'PRD014', 'Cà chua bi', 11, 25000, 18000, 50, 5, 100, 'kg', 'Đà Lạt', 'Cà chua bi tươi Đà Lạt', 1),
(15, 'PRD015', 'Rau muống', 11, 15000, 10000, 30, 5, 80, 'kg', 'Đà Lạt', 'Rau muống tươi sạch', 1),
(16, 'PRD016', 'Khoai tây', 11, 20000, 15000, 80, 10, 200, 'kg', 'Đà Lạt', 'Khoai tây Đà Lạt', 1),

-- Thịt gia cầm  
(17, 'PRD017', 'Thịt gà ta 1kg', 12, 180000, 150000, 25, 5, 50, 'kg', 'C.P', 'Thịt gà ta tươi sạch', 1),
(18, 'PRD018', 'Thịt gà công nghiệp 1kg', 12, 120000, 95000, 40, 8, 80, 'kg', 'C.P', 'Thịt gà công nghiệp tươi', 1),

-- Gia vị
(19, 'PRD019', 'Nước mắm Nam Ngư 500ml', 24, 45000, 35000, 80, 10, 200, 'chai', 'Nam Ngư', 'Nước mắm truyền thống', 1),
(20, 'PRD020', 'Dầu ăn Simply 1L', 24, 55000, 42000, 120, 15, 300, 'chai', 'Simply', 'Dầu ăn thực vật cao cấp', 1),

-- Bánh kẹo
(21, 'PRD021', 'Bánh Oreo 137g', 25, 35000, 26000, 150, 20, 400, 'gói', 'Oreo', 'Bánh quy kem Oreo', 1),
(22, 'PRD022', 'Kẹo Mentos 38g', 25, 15000, 11000, 200, 30, 500, 'gói', 'Mentos', 'Kẹo bạc hà Mentos', 1),

-- Chất tẩy rửa
(23, 'PRD023', 'Nước rửa chén Sunlight 800ml', 52, 45000, 32000, 100, 15, 250, 'chai', 'Sunlight', 'Nước rửa chén khử mùi tanh', 1),
(24, 'PRD024', 'Bột giặt Ariel 720g', 52, 85000, 65000, 80, 10, 200, 'túi', 'Ariel', 'Bột giặt loại bỏ vết bẩn cứng đầu', 1),

-- Đồ uống - Bia rượu
(25, 'PRD025', 'Bia Heineken 330ml', 32, 25000, 18000, 200, 30, 600, 'lon', 'Heineken', 'Bia Heineken nhập khẩu', 1),
(26, 'PRD026', 'Bia Saigon 330ml', 32, 18000, 13000, 300, 50, 800, 'lon', 'Saigon', 'Bia Saigon truyền thống', 1),

-- Giấy vệ sinh
(27, 'PRD027', 'Giấy vệ sinh Tepro 10 cuộn', 44, 65000, 48000, 120, 15, 300, 'lốc', 'Tepro', 'Giấy vệ sinh mềm mịn', 1),
(28, 'PRD028', 'Khăn giấy Kleenex 200 tờ', 44, 25000, 18000, 150, 20, 400, 'hộp', 'Kleenex', 'Khăn giấy mềm mịn', 1),

-- Đồ dùng học tập
(29, 'PRD029', 'Bút bi Thiên Long TL-079', 63, 5000, 3500, 500, 100, 1000, 'cây', 'Thiên Long', 'Bút bi màu xanh', 1),
(30, 'PRD030', 'Vở kẻ ngang 200 trang', 63, 12000, 8000, 300, 50, 800, 'quyển', 'Hồng Hà', 'Vở học sinh kẻ ngang', 1);

-- ====================================================================
-- 6. DỮ LIỆU QUY TẮC PHẠT (PENALTY_RULES)
-- ====================================================================
INSERT INTO `penalty_rules` (`rule_id`, `rule_type`, `rule_name`, `penalty_amount`, `penalty_percent`, `description`, `is_active`) VALUES
(1, 'NO_CHECKIN', 'Không bắt đầu ca khi có lịch', 200000.00, 0.00, 'Phạt 200,000 VNĐ khi có lịch làm nhưng không check-in', 1),
(2, 'LATE_ARRIVAL', 'Đến muộn', 0.00, 25.00, 'Giảm 25% lương mỗi giờ đến muộn', 1),
(3, 'UNSCHEDULED_WORK', 'Làm việc ngoài lịch', 0.00, 75.00, 'Chỉ tính 75% lương khi làm việc ngoài lịch', 1),
(4, 'NO_CHECKOUT', 'Không kết thúc ca đúng giờ', 0.00, 50.00, 'Trừ 50% lương buổi đó khi không checkout sau 30 phút', 1),
(5, 'EARLY_LEAVE', 'Về sớm', 0.00, 25.00, 'Giảm 25% lương mỗi giờ về sớm', 1),
(6, 'ABSENT', 'Vắng mặt không phép', 300000.00, 0.00, 'Phạt 300,000 VNĐ khi vắng mặt không có lý do chính đáng', 1); 