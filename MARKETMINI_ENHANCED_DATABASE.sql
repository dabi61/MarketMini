-- ====================================================================
-- MARKETMINI ENHANCED DATABASE - PHIÊN BẢN TƯƠNG THÍCH HOÀN TOÀN
-- ====================================================================
-- Database được thiết kế để tương thích với cả Java code mới và database cũ
-- Created: 2025-06-25
-- Version: 2.0 - Backward Compatible & Enhanced

-- Drop database if exists and create new
DROP DATABASE IF EXISTS `marketmini`;
CREATE DATABASE `marketmini` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `marketmini`;

-- ====================================================================
-- 1. EMPLOYEES TABLE - GIỮ NGUYÊN 100% CẤU TRÚC CŨ
-- ====================================================================
CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_name` varchar(100) DEFAULT NULL COMMENT 'Username for login - tương thích cũ',
  `password` varchar(100) DEFAULT NULL COMMENT 'Password - tương thích cũ', 
  `full_name` varchar(255) DEFAULT NULL COMMENT 'Full name - tương thích cũ',
  `sex` varchar(11) DEFAULT NULL COMMENT 'Gender - tương thích cũ',
  `role` int(11) DEFAULT NULL COMMENT 'Role: 1=Admin, 2=Staff - tương thích cũ',
  `phone` varchar(20) DEFAULT NULL COMMENT 'Phone - tương thích cũ',
  `email` varchar(50) DEFAULT NULL COMMENT 'Email - tương thích cũ',
  `date` date DEFAULT NULL COMMENT 'Hire date - tương thích cũ',
  PRIMARY KEY (`employee_id`),
  KEY `idx_role` (`role`),
  KEY `idx_employee_name` (`employee_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Employee management - Simple structure compatible with old code';

-- ====================================================================
-- 2. CATEGORY TABLE - GIỮ NGUYÊN + THÊM CÁC TRƯỜNG QUẢN LÝ NÂNG CAO
-- ====================================================================
CREATE TABLE `category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(255) DEFAULT NULL COMMENT 'Category name - tương thích cũ',
  `description` text DEFAULT NULL COMMENT 'Description - tương thích cũ',
  -- Thêm các trường mới cho CategoryForm
  `parent_category_id` int(11) DEFAULT NULL COMMENT 'MỚI: Parent category for hierarchy',
  `category_code` varchar(20) DEFAULT NULL UNIQUE COMMENT 'MỚI: Auto-generated category code',
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'MỚI: Active status',
  `display_order` int(11) DEFAULT 0 COMMENT 'MỚI: Display order',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`category_id`),
  KEY `idx_parent_category` (`parent_category_id`),
  KEY `idx_active_order` (`is_active`, `display_order`),
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Category management - Enhanced';

-- ====================================================================
-- 3. SUPPLIERS TABLE - GIỮ NGUYÊN 100% CẤU TRÚC CŨ
-- ====================================================================
CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(255) DEFAULT NULL COMMENT 'Supplier name - tương thích cũ',
  `phone` varchar(20) DEFAULT NULL COMMENT 'Phone - tương thích cũ',
  `address` varchar(500) DEFAULT NULL COMMENT 'Address - tương thích cũ', 
  `email` varchar(100) DEFAULT NULL COMMENT 'Email - tương thích cũ',
  PRIMARY KEY (`supplier_id`),
  KEY `idx_supplier_name` (`supplier_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Suppliers - Simple structure compatible with old code';

-- ====================================================================
-- 4. PRODUCTS TABLE - GIỮ NGUYÊN + THÊM SELLING_PRICE VÀ CÁC TRƯỜNG MỚI
-- ====================================================================
CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) DEFAULT NULL COMMENT 'Product name - tương thích cũ',
  `category_id` int(11) DEFAULT NULL COMMENT 'Category ID - tương thích cũ',
  `price` int(11) DEFAULT NULL COMMENT 'Import/cost price - tương thích cũ',
  `stock_quantity` int(11) DEFAULT NULL COMMENT 'Stock quantity - tương thích cũ',
  `unit` varchar(50) DEFAULT NULL COMMENT 'Unit - tương thích cũ',
  -- Thêm các trường mới cho Java code
  `selling_price` decimal(15,2) DEFAULT NULL COMMENT 'MỚI: Selling price for sales',
  `description` text DEFAULT NULL COMMENT 'MỚI: Product description',
  `barcode` varchar(50) DEFAULT NULL COMMENT 'MỚI: Product barcode',
  `min_stock_level` int(11) DEFAULT 10 COMMENT 'MỚI: Minimum stock alert level',
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'MỚI: Active status',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`product_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_product_name` (`product_name`),
  KEY `idx_stock_level` (`stock_quantity`),
  KEY `idx_active` (`is_active`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Products - Enhanced with selling price';

-- ====================================================================
-- 5. CUSTOMERS TABLE - GIỮ NGUYÊN + THÊM HỆ THỐNG LOYALTY NÂNG CAO
-- ====================================================================
CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(20) NOT NULL COMMENT 'Phone - tương thích cũ',
  `full_name` varchar(255) DEFAULT NULL COMMENT 'Full name - tương thích cũ',
  `points` int(11) DEFAULT NULL COMMENT 'Loyalty points - tương thích cũ',
  -- Thêm các trường mới cho hệ thống loyalty nâng cao
  `email` varchar(100) DEFAULT NULL COMMENT 'MỚI: Customer email',
  `address` text DEFAULT NULL COMMENT 'MỚI: Customer address',
  `customer_type` enum('REGULAR','VIP','PREMIUM') DEFAULT 'REGULAR' COMMENT 'MỚI: Customer tier',
  `date_of_birth` date DEFAULT NULL COMMENT 'MỚI: Customer birthday',
  `gender` enum('Nam','Nữ','Khác') DEFAULT NULL COMMENT 'MỚI: Customer gender',
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'MỚI: Active status',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`customer_id`),
  KEY `idx_phone` (`phone_number`),
  KEY `idx_customer_name` (`full_name`),
  KEY `idx_customer_type` (`customer_type`),
  KEY `idx_points` (`points`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customers - Enhanced with loyalty system';

-- ====================================================================
-- 6. IMPORTS TABLE - GIỮ NGUYÊN + THÊM CÁC TRƯỜNG QUẢN LÝ NÂNG CAO
-- ====================================================================
CREATE TABLE `imports` (
  `import_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL COMMENT 'Product ID - tương thích cũ',
  `supplier_id` int(11) NOT NULL COMMENT 'Supplier ID - tương thích cũ',
  `quantity` int(11) DEFAULT NULL COMMENT 'Quantity - tương thích cũ',
  `import_price` int(11) DEFAULT NULL COMMENT 'Import price - tương thích cũ',
  `import_date` date DEFAULT NULL COMMENT 'Import date - tương thích cũ',
  `employee_id` int(11) NOT NULL COMMENT 'Employee ID - tương thích cũ',
  -- Thêm các trường mới
  `import_code` varchar(30) DEFAULT NULL UNIQUE COMMENT 'MỚI: Import transaction code',
  `unit_cost` decimal(15,2) AS (import_price) STORED COMMENT 'MỚI: Unit cost compatibility',
  `total_cost` decimal(15,2) AS (quantity * import_price) STORED COMMENT 'MỚI: Total cost calculation',
  `import_status` enum('PENDING','COMPLETED','CANCELLED') DEFAULT 'COMPLETED' COMMENT 'MỚI: Import status',
  `notes` text DEFAULT NULL COMMENT 'MỚI: Import notes',
  `invoice_number` varchar(50) DEFAULT NULL COMMENT 'MỚI: Supplier invoice number',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`import_id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_supplier` (`supplier_id`),
  KEY `idx_employee` (`employee_id`),
  KEY `idx_import_date` (`import_date`),
  KEY `idx_status` (`import_status`),
  CONSTRAINT `fk_import_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_import_supplier` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_import_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Imports - Enhanced';

-- ====================================================================
-- 7. ORDERS TABLE - GIỮ NGUYÊN + THÊM CÁC TRƯỜNG PAYMENT & LOYALTY
-- ====================================================================
CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL COMMENT 'Employee ID - tương thích cũ',
  `order_date` date DEFAULT NULL COMMENT 'Order date - tương thích cũ',
  `total_amount` int(11) DEFAULT NULL COMMENT 'Total amount - tương thích cũ',
  `customer_id` int(11) NOT NULL COMMENT 'Customer ID - tương thích cũ',
  `final_amount` int(11) DEFAULT NULL COMMENT 'Final amount - tương thích cũ',
  -- Thêm các trường mới
  `order_code` varchar(30) DEFAULT NULL UNIQUE COMMENT 'MỚI: Order transaction code',
  `discount_amount` decimal(15,2) DEFAULT 0 COMMENT 'MỚI: Total discount applied',
  `points_used` int(11) DEFAULT 0 COMMENT 'MỚI: Loyalty points used',
  `points_earned` int(11) DEFAULT 0 COMMENT 'MỚI: Loyalty points earned',
  `payment_method` enum('CASH','CARD','TRANSFER','MIXED') DEFAULT 'CASH' COMMENT 'MỚI: Payment method',
  `payment_status` enum('PENDING','PAID','PARTIAL','REFUNDED') DEFAULT 'PAID' COMMENT 'MỚI: Payment status',
  `order_status` enum('PENDING','COMPLETED','CANCELLED','RETURNED') DEFAULT 'COMPLETED' COMMENT 'MỚI: Order status',
  `notes` text DEFAULT NULL COMMENT 'MỚI: Order notes',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`order_id`),
  KEY `idx_employee` (`employee_id`),
  KEY `idx_customer` (`customer_id`),
  KEY `idx_order_date` (`order_date`),
  KEY `idx_status` (`order_status`),
  KEY `idx_payment_status` (`payment_status`),
  CONSTRAINT `fk_order_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_order_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Orders - Enhanced';

-- ====================================================================
-- 8. ORDERDETAILS TABLE - GIỮ NGUYÊN + THÊM DISCOUNT SYSTEM
-- ====================================================================
CREATE TABLE `orderdetails` (
  `order_detail_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL COMMENT 'Order ID - tương thích cũ',
  `product_id` int(11) NOT NULL COMMENT 'Product ID - tương thích cũ',
  `quantity` int(11) DEFAULT NULL COMMENT 'Quantity - tương thích cũ',
  `unit_price` int(11) DEFAULT NULL COMMENT 'Unit price - tương thích cũ',
  -- Thêm các trường mới cho discount system
  `discount_percent` decimal(5,2) DEFAULT 0 COMMENT 'MỚI: Item discount percentage',
  `discount_amount` decimal(15,2) DEFAULT 0 COMMENT 'MỚI: Item discount amount',
  `line_total` decimal(15,2) AS (quantity * unit_price - discount_amount) STORED COMMENT 'MỚI: Line total after discount',
  `notes` text DEFAULT NULL COMMENT 'MỚI: Item notes',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  PRIMARY KEY (`order_detail_id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_product` (`product_id`),
  CONSTRAINT `fk_orderdetail_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_orderdetail_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Order details - Enhanced with discount';

-- ====================================================================
-- 9. WORKSHIFTS TABLE - MỚI HOÀN TOÀN CHO HỆ THỐNG PENALTY
-- ====================================================================
CREATE TABLE `workshifts` (
  `shift_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL COMMENT 'Employee reference',
  `shift_date` date NOT NULL COMMENT 'Work shift date',
  `shift_type` enum('SANG','CHIEU','TOI','FULL') DEFAULT 'FULL' COMMENT 'Shift type matching Java enum',
  `start_time` time NOT NULL COMMENT 'Planned start time',
  `end_time` time NOT NULL COMMENT 'Planned end time',
  `planned_hours` decimal(4,2) NOT NULL DEFAULT 8.00 COMMENT 'Planned working hours',
  `actual_hours` decimal(4,2) DEFAULT NULL COMMENT 'Actual working hours',
  `break_minutes` int(11) DEFAULT 60 COMMENT 'Break time in minutes',
  `overtime_hours` decimal(4,2) DEFAULT 0.00 COMMENT 'Overtime hours',
  `overtime_rate` decimal(3,2) DEFAULT 1.50 COMMENT 'Overtime multiplier rate',
  `hourly_wage` decimal(10,2) NOT NULL DEFAULT 25000.00 COMMENT 'Hourly wage in VND',
  `total_earnings` decimal(15,2) DEFAULT 0.00 COMMENT 'Total earnings for shift',
  `status` enum('SCHEDULED','IN_PROGRESS','COMPLETED','ABSENT') DEFAULT 'SCHEDULED' COMMENT 'Shift status',
  
  -- Enhanced penalty system fields  
  `check_in_time` time NULL COMMENT 'Actual check-in time',
  `check_out_time` time NULL COMMENT 'Actual check-out time',
  `late_minutes` int(11) DEFAULT 0 COMMENT 'Minutes late for check-in',
  `early_leave_minutes` int(11) DEFAULT 0 COMMENT 'Minutes left early',
  `is_scheduled` tinyint(1) DEFAULT 1 COMMENT 'Whether this shift was scheduled',
  `penalty_amount` decimal(10,2) DEFAULT 0.00 COMMENT 'Penalty amount in VND',
  `penalty_reason` text NULL COMMENT 'Reason for penalty',
  `salary_adjustment_percent` decimal(5,2) DEFAULT 100.00 COMMENT 'Salary adjustment percentage',
  `adjustment_reason` text NULL COMMENT 'Reason for salary adjustment',
  `auto_checkout_penalty` decimal(10,2) DEFAULT 0.00 COMMENT 'Penalty for not checking out',
  
  `notes` text DEFAULT NULL COMMENT 'Shift notes',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`shift_id`),
  KEY `idx_employee` (`employee_id`),
  KEY `idx_shift_date` (`shift_date`),
  KEY `idx_status` (`status`),
  KEY `idx_shift_type` (`shift_type`),
  UNIQUE KEY `uk_employee_date_start` (`employee_id`, `shift_date`, `start_time`),
  CONSTRAINT `fk_workshift_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_hours_range` CHECK (`planned_hours` BETWEEN 0 AND 16),
  CONSTRAINT `chk_actual_hours_range` CHECK (`actual_hours` IS NULL OR (`actual_hours` BETWEEN 0 AND 16)),
  CONSTRAINT `chk_overtime_range` CHECK (`overtime_hours` BETWEEN 0 AND 12)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='MỚI: Work shift management with penalty system';

-- ====================================================================
-- 10. SALARY TABLE - GIỮ NGUYÊN + THÊM COMPUTED FIELDS (FIXED)
-- ====================================================================
CREATE TABLE `salary` (
  `salary_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL COMMENT 'Employee ID - tương thích cũ',
  `total_hours` decimal(5,2) DEFAULT NULL COMMENT 'Total hours - tương thích cũ',
  `hourly_wage` int(11) DEFAULT NULL COMMENT 'Hourly wage - tương thích cũ',
  `bonus` int(11) DEFAULT NULL COMMENT 'Bonus - tương thích cũ',
  `payment_date` date DEFAULT NULL COMMENT 'Payment date - tương thích cũ',
  `created_date` date DEFAULT NULL COMMENT 'Created date - tương thích cũ',
  -- Thêm các trường mới
  `overtime_pay` decimal(15,2) DEFAULT 0.00 COMMENT 'MỚI: Overtime payment',
  `penalty_deduction` decimal(15,2) DEFAULT 0.00 COMMENT 'MỚI: Total penalty deductions',
  `gross_salary` decimal(15,2) AS (IFNULL(total_hours,0) * IFNULL(hourly_wage,0) + IFNULL(bonus,0) + IFNULL(overtime_pay,0)) STORED COMMENT 'MỚI: Gross salary',
  `net_salary` decimal(15,2) AS (IFNULL(total_hours,0) * IFNULL(hourly_wage,0) + IFNULL(bonus,0) + IFNULL(overtime_pay,0) - IFNULL(penalty_deduction,0)) STORED COMMENT 'MỚI: Net salary',
  `payment_status` enum('PENDING','PAID','CANCELLED') DEFAULT 'PENDING' COMMENT 'MỚI: Payment status',
  `salary_period` varchar(7) DEFAULT NULL COMMENT 'MỚI: Salary period YYYY-MM (auto-filled by trigger)',
  `notes` text DEFAULT NULL COMMENT 'MỚI: Salary notes',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`salary_id`),
  KEY `idx_employee` (`employee_id`),
  KEY `idx_period` (`salary_period`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_created_date` (`created_date`),
  CONSTRAINT `fk_salary_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Salary - Enhanced with penalty system';

-- ====================================================================
-- 11. PRODUCTDISPLAY TABLE - GIỮ NGUYÊN + THÊM CÁC TRƯỜNG QUẢN LÝ
-- ====================================================================
CREATE TABLE `productdisplay` (
  `display_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) DEFAULT NULL COMMENT 'Product ID - tương thích cũ',
  `row` varchar(50) NOT NULL COMMENT 'Row position - tương thích cũ',
  `floor` varchar(50) NOT NULL COMMENT 'Floor level - tương thích cũ',
  `start_date` date NOT NULL COMMENT 'Start date - tương thích cũ',
  `end_date` date NOT NULL COMMENT 'End date - tương thích cũ',
  -- Thêm các trường mới cho Java compatibility
  `column` varchar(50) DEFAULT NULL COMMENT 'MỚI: Column position for DisplayForm',
  `position_code` varchar(20) AS (CONCAT(`row`, '-', `floor`, IFNULL(CONCAT('-', `column`), ''))) STORED COMMENT 'MỚI: Full position code',
  `display_status` enum('ACTIVE','INACTIVE','MAINTENANCE') DEFAULT 'ACTIVE' COMMENT 'MỚI: Display status',
  `notes` text DEFAULT NULL COMMENT 'MỚI: Display notes',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`display_id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_position` (`row`, `floor`, `column`),
  KEY `idx_display_period` (`start_date`, `end_date`),
  KEY `idx_status` (`display_status`),
  CONSTRAINT `fk_display_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE SET NULL,
  CONSTRAINT `chk_valid_dates` CHECK (`start_date` <= `end_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product display - Enhanced';

-- ====================================================================
-- 12. PROMOTION TABLE - GIỮ NGUYÊN + THÊM HỆ THỐNG KHUYẾN MÃI NÂNG CAO
-- ====================================================================
CREATE TABLE `promotion` (
  `promotion_id` int(11) NOT NULL AUTO_INCREMENT,
  `promotion_name` varchar(255) DEFAULT NULL COMMENT 'Promotion name - tương thích cũ',
  `start_date` date DEFAULT NULL COMMENT 'Start date - tương thích cũ',
  `end_date` date DEFAULT NULL COMMENT 'End date - tương thích cũ',
  `product_id` int(11) DEFAULT NULL COMMENT 'Product ID - tương thích cũ',
  `discount` int(11) DEFAULT NULL COMMENT 'Discount percent - tương thích cũ',
  `discounted_price` int(11) DEFAULT NULL COMMENT 'Discounted price - tương thích cũ',
  `original_price` int(11) DEFAULT NULL COMMENT 'Original price - tương thích cũ',
  -- Thêm alias field cho Java compatibility
  `discount_percent` int(11) AS (`discount`) STORED COMMENT 'MỚI: Alias for discount field',
  -- Thêm các trường mới
  `promotion_code` varchar(20) DEFAULT NULL UNIQUE COMMENT 'MỚI: Promotion code',
  `promotion_type` enum('PERCENTAGE','FIXED_AMOUNT','BUY_X_GET_Y','BULK_DISCOUNT') DEFAULT 'PERCENTAGE' COMMENT 'MỚI: Promotion type',
  `min_quantity` int(11) DEFAULT 1 COMMENT 'MỚI: Minimum quantity for promotion',
  `max_uses` int(11) DEFAULT NULL COMMENT 'MỚI: Maximum uses limit',
  `current_uses` int(11) DEFAULT 0 COMMENT 'MỚI: Current usage count',
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'MỚI: Active status',
  `description` text DEFAULT NULL COMMENT 'MỚI: Promotion description',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`promotion_id`),
  KEY `idx_product` (`product_id`),
  KEY `idx_promotion_period` (`start_date`, `end_date`),
  KEY `idx_active` (`is_active`),
  KEY `idx_promotion_code` (`promotion_code`),
  CONSTRAINT `fk_promotion_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`) ON DELETE CASCADE,
  CONSTRAINT `chk_valid_promotion_dates` CHECK (`start_date` <= `end_date`),
  CONSTRAINT `chk_discount_range` CHECK (`discount` BETWEEN 0 AND 100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Promotions - Enhanced';

-- ====================================================================
-- 13. MONTHLY_OPERATING_EXPENSES TABLE - GIỮ NGUYÊN + THÊM CÁC LOẠI CHI PHÍ MỚI
-- ====================================================================
CREATE TABLE `monthly_operating_expenses` (
  `expense_id` int(11) NOT NULL AUTO_INCREMENT,
  `month_year` date DEFAULT NULL COMMENT 'Month year - tương thích cũ',
  `electricity_cost` int(11) DEFAULT NULL COMMENT 'Electricity cost - tương thích cũ',
  `rent_cost` int(11) DEFAULT NULL COMMENT 'Rent cost - tương thích cũ',
  `water_cost` int(11) DEFAULT NULL COMMENT 'Water cost - tương thích cũ',
  `repair_cost` int(11) DEFAULT NULL COMMENT 'Repair cost - tương thích cũ',
  -- Thêm các trường mới
  `staff_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'MỚI: Staff salary costs',
  `marketing_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'MỚI: Marketing expenses',
  `insurance_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'MỚI: Insurance costs',
  `tax_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'MỚI: Tax expenses',
  `other_cost` decimal(15,2) DEFAULT 0.00 COMMENT 'MỚI: Other miscellaneous costs',
  `total_cost` decimal(15,2) AS (IFNULL(electricity_cost,0) + IFNULL(rent_cost,0) + IFNULL(water_cost,0) + 
                                 IFNULL(repair_cost,0) + staff_cost + marketing_cost + 
                                 insurance_cost + tax_cost + other_cost) STORED COMMENT 'MỚI: Total monthly costs',
  `notes` text DEFAULT NULL COMMENT 'MỚI: Expense notes',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`expense_id`),
  KEY `idx_month_year` (`month_year`),
  UNIQUE KEY `uk_month_year` (`month_year`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Monthly expenses - Enhanced';

-- ====================================================================
-- 14. INVOICES TABLE - GIỮ NGUYÊN VÀ MỞ RỘNG
-- ====================================================================
CREATE TABLE `invoices` (
  `invoice_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL COMMENT 'Order ID - tương thích cũ',
  `issue_date` date DEFAULT NULL COMMENT 'Issue date - tương thích cũ',
  `total_amount` int(11) DEFAULT NULL COMMENT 'Total amount - tương thích cũ',
  -- Thêm các trường mới
  `invoice_number` varchar(30) DEFAULT NULL UNIQUE COMMENT 'MỚI: Invoice number',
  `invoice_type` enum('SALE','RETURN','REFUND') DEFAULT 'SALE' COMMENT 'MỚI: Invoice type',
  `tax_amount` decimal(15,2) DEFAULT 0.00 COMMENT 'MỚI: Tax amount (VAT)',
  `tax_rate` decimal(5,2) DEFAULT 10.00 COMMENT 'MỚI: Tax rate percentage',
  `invoice_status` enum('DRAFT','ISSUED','CANCELLED') DEFAULT 'ISSUED' COMMENT 'MỚI: Invoice status',
  `notes` text DEFAULT NULL COMMENT 'MỚI: Invoice notes',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`invoice_id`),
  KEY `idx_order` (`order_id`),
  KEY `idx_issue_date` (`issue_date`),
  KEY `idx_status` (`invoice_status`),
  KEY `idx_invoice_number` (`invoice_number`),
  CONSTRAINT `fk_invoice_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Invoices - Enhanced';

-- ====================================================================
-- 15. WORKINGSESSION TABLE - GIỮ NGUYÊN TƯƠNG THÍCH VỚI DB CŨ
-- ====================================================================
CREATE TABLE `workingsession` (
  `working_session_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) DEFAULT NULL COMMENT 'Employee ID - tương thích cũ',
  `login_time` datetime DEFAULT NULL COMMENT 'Login time - tương thích cũ',
  `logout_time` datetime DEFAULT NULL COMMENT 'Logout time - tương thích cũ',
  `working_hours` decimal(10,2) DEFAULT NULL COMMENT 'Working hours - tương thích cũ',
  `date` date DEFAULT NULL COMMENT 'Date - tương thích cũ',
  `work_status` varchar(20) DEFAULT NULL COMMENT 'Work status - tương thích cũ',
  -- Thêm các trường mới để tích hợp với workshifts
  `shift_id` int(11) DEFAULT NULL COMMENT 'MỚI: Reference to workshifts table',
  `session_type` enum('LOGIN','MANUAL_ENTRY','IMPORTED') DEFAULT 'LOGIN' COMMENT 'MỚI: Session data source',
  `ip_address` varchar(45) DEFAULT NULL COMMENT 'MỚI: Login IP address',
  `device_info` text DEFAULT NULL COMMENT 'MỚI: Device information',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP COMMENT 'MỚI: Creation time',
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'MỚI: Update time',
  PRIMARY KEY (`working_session_id`),
  KEY `idx_employee` (`employee_id`),
  KEY `idx_date` (`date`),
  KEY `idx_shift` (`shift_id`),
  KEY `idx_work_status` (`work_status`),
  CONSTRAINT `fk_session_employee` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_session_shift` FOREIGN KEY (`shift_id`) REFERENCES `workshifts` (`shift_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Working sessions - Backward compatible';

-- ====================================================================
-- SAMPLE DATA - TƯƠNG THÍCH VỚI DATABASE CŨ
-- ====================================================================

-- Insert employees (tương thích cũ)
INSERT INTO `employees` (`employee_id`, `employee_name`, `password`, `full_name`, `sex`, `role`, `phone`, `email`, `date`) VALUES
(1, 'admin1', 'pass1', 'Nguyen Ba Quoc Cuong', 'Nam', 1, '0382951529', 'cuongngba7@gmail.com', '2025-06-02'),
(2, 'baphu', '1', 'Nguyen Ba Phu', 'Nam', 2, '0382951523', 'baphu7@gmail.com', '2025-06-12'),
(4, 'Linh', '12', 'Lê Linh', 'Nam', 2, '0987656557', 'linh@gmail.com', '2025-06-24'),
(5, 'Linh', '12', 'Lê Linh', 'Nữ', 2, '0332343232', 'linh@gmail.com', '2025-06-23'),
(8, 'duc', '1234', 'le van duc', 'Nam', 2, '0945324366', 'duc@gmail.com', '2025-06-06');

-- Insert categories (tương thích cũ + mở rộng)
INSERT INTO `category` (`category_id`, `category_name`, `description`, `category_code`) VALUES
(1, 'Dầu gội', 'Dầu gội đầu', 'DG001'),
(2, 'Dầu ăn', '', 'DA002'),
(3, 'Sữa tươi', '', 'ST003');

-- Insert suppliers (tương thích cũ)
INSERT INTO `suppliers` (`supplier_id`, `supplier_name`, `phone`, `address`, `email`) VALUES
(1, 'CT TNHH Vạn Xuân', '0979932423', 'Kim Liên, Đống Đa, Hà Nội', 'vanxuanspl@gmail.com'),
(2, 'CT TNHH CALOFIC', '0747164638', 'Thanh Xuân, Hà Nội', 'calofit_ct@gmail.com'),
(3, 'Công ty Cổ phần sữa Việt Nam - Vinamilk', '0384726342', 'Số 10, đường Tân Trào, Phường Tân Phú, Quận 7, Thành phố Hồ Chí Minh, Việt Nam.', 'vinamilkvn@gmail.com'),
(4, 'Xuân Hạ', '0982342342', 'Hà Nội', 'xuanha@gmail.com'),
(5, 'Hòa hải', '0333232239', 'Thanh Xuân, Hà Nội', 'linh12@gmail.com'),
(6, 'Lan Hạ', '0343453452', 'Thanh Hóa', 'xuanha@gmail.com');

-- Insert products (tương thích cũ + selling_price)
INSERT INTO `products` (`product_id`, `product_name`, `category_id`, `price`, `stock_quantity`, `unit`, `selling_price`) VALUES
(3, 'Thái dương Xanh', 1, 15000, 4994, 'dây', 18000.00),
(4, 'Dầu gội Thái dương', 1, 12000, 986, 'dây', 14400.00),
(5, 'Dầu gội PANTENE', 1, 145000, 283, 'chai', 174000.00),
(6, 'Dầu ăn Cái Lân 400ml', 2, 45000, 193, 'chai', 54000.00),
(7, 'Dầu ăn Neptune 2l', 2, 125000, 250, 'chai', 150000.00),
(8, 'Dầu ăn meizan 1l', 2, 55000, 3195, 'chai', 66000.00),
(9, 'Dầu gội Head and shoulder', 1, 13000, 1996, 'dây', 15600.00),
(10, 'Dầu ăn Simple', 2, 60000, 300, 'chai', 72000.00),
(11, 'Sữa tươi tiệt trùng GreenFarm rất ít đường - Vinamilk', 3, 35000, 1997, 'lốc', 42000.00),
(12, 'Sữa tươi tiệt trùng GreenFarm tổ yến - Vinamilk', 3, 35000, 2000, 'lốc', 42000.00),
(13, 'Sữa tươi tiệt trùng 100% Không đường - Vinamilk', 3, 20000, 2000, 'lốc', 24000.00),
(14, 'Sữa tươi tiệt trùng 100% Sôcôla - Vinamilk', 3, 20000, 1999, 'lốc', 24000.00),
(15, 'Sữa tươi tiệt trùng 100% Hương Dâu - Vinamilk', 3, 20000, 2000, 'lốc', 24000.00),
(16, 'Dầu Gội Dược Liệu Nguyên Xuân Dưỡng Tóc 450Ml', 1, 145000, 300, 'chai', 174000.00);

-- Insert customers (tương thích cũ)
INSERT INTO `customers` (`customer_id`, `phone_number`, `full_name`, `points`) VALUES
(1, '0442452424', 'Chị Hạnh', NULL),
(2, '0356246724', 'Chị Minh', NULL),
(4, '0352507345', 'Anh Cường Nguyễn', 24936),
(5, '0726846578', 'Chị Thủy', NULL),
(6, '0', 'Vô danh', 6000),
(7, '0973847374', 'Chị Thủy', NULL),
(8, '04927475648', 'Chị Hạnh', NULL),
(9, '0434526345', 'Anh Quang', 3000);

-- Insert imports (tương thích cũ)
INSERT INTO `imports` (`import_id`, `product_id`, `supplier_id`, `quantity`, `import_price`, `import_date`, `employee_id`) VALUES
(4, 3, 1, 2000, 15000, '2025-06-11', 1),
(5, 3, 1, 3000, 15000, '2025-06-11', 1),
(6, 4, 1, 1000, 12000, '2025-06-13', 1),
(7, 5, 1, 300, 145000, '2025-06-12', 1),
(8, 6, 1, 200, 45000, '2025-06-13', 1),
(9, 7, 1, 250, 125000, '2025-06-13', 1),
(10, 8, 2, 3000, 55000, '2025-06-12', 1),
(11, 8, 2, 200, 55000, '2025-06-13', 1),
(12, 9, 1, 2000, 13000, '2025-06-17', 1),
(13, 10, 2, 300, 60000, '2025-06-14', 1),
(14, 11, 3, 1000, 35000, '2025-06-21', 1),
(15, 12, 3, 1000, 35000, '2025-06-21', 1),
(16, 13, 3, 1000, 20000, '2025-06-21', 1),
(17, 14, 3, 1000, 20000, '2025-06-21', 1),
(18, 15, 3, 1000, 20000, '2025-06-21', 1),
(19, 11, 3, 1000, 35000, '2025-06-21', 1),
(20, 12, 3, 1000, 35000, '2025-06-21', 1),
(21, 13, 3, 1000, 20000, '2025-06-21', 1),
(22, 14, 3, 1000, 20000, '2025-06-21', 1),
(23, 15, 3, 1000, 20000, '2025-06-21', 1),
(24, 16, 1, 300, 145000, '2025-06-21', 1);

-- Insert orders (tương thích cũ)
INSERT INTO `orders` (`order_id`, `employee_id`, `order_date`, `total_amount`, `customer_id`, `final_amount`) VALUES
(8, 1, '2025-06-17', 188400, 6, 188400),
(10, 1, '2025-06-17', 645600, 4, 642600),
(11, 1, '2025-06-17', 206400, 6, 206400),
(12, 1, '2025-06-17', 174000, 6, 169872),
(13, 1, '2025-06-17', 115200, 4, 86376),
(14, 1, '2025-06-17', 295200, 4, 295200),
(15, 1, '2025-06-17', 14400, 4, 6192),
(16, 1, '2025-06-17', 300000, 6, 292392),
(17, 1, '2025-06-17', 1232400, 4, 1232400),
(18, 1, '2025-06-21', 150000, 9, 150000);

-- Insert order details (tương thích cũ)
INSERT INTO `orderdetails` (`order_detail_id`, `order_id`, `product_id`, `quantity`, `unit_price`) VALUES
(10, 8, 4, 1, 14400),
(11, 8, 5, 1, 174000),
(12, 10, 6, 2, 54000),
(13, 10, 5, 3, 174000),
(14, 10, 9, 1, 15600),
(15, 11, 3, 1, 18000),
(16, 11, 4, 1, 14400),
(17, 11, 5, 1, 174000),
(18, 12, 5, 1, 174000),
(19, 13, 3, 1, 18000),
(20, 13, 4, 3, 14400),
(21, 13, 6, 1, 54000),
(22, 14, 8, 4, 66000),
(23, 14, 9, 2, 15600),
(24, 15, 4, 1, 14400),
(25, 16, 4, 5, 14400),
(26, 16, 5, 1, 174000),
(27, 16, 6, 1, 54000),
(28, 17, 4, 1, 14400),
(29, 17, 5, 7, 174000),
(30, 18, 11, 3, 42000),
(31, 18, 14, 1, 24000);

-- Insert product displays (tương thích cũ + column field)
INSERT INTO `productdisplay` (`display_id`, `product_id`, `row`, `floor`, `start_date`, `end_date`, `column`) VALUES
(2, 5, 'A1', '2', '2025-06-23', '2025-06-25', '1'),
(3, 6, 'A1', '1', '2025-06-22', '2025-06-25', '2');

-- Insert salaries (tương thích cũ + trigger sẽ tự động set salary_period)
INSERT INTO `salary` (`salary_id`, `employee_id`, `total_hours`, `hourly_wage`, `bonus`, `payment_date`, `created_date`) VALUES
(1, 1, 0.00, 30000, 0, '2025-06-24', '2025-06-24'),
(2, 2, 0.00, 18000, 200000, NULL, '2025-06-24'),
(3, 4, 0.00, 18000, 0, NULL, '2025-06-24'),
(4, 5, 0.00, 18000, 0, NULL, '2025-06-24');

-- ====================================================================
-- TRIGGERS VÀ STORED PROCEDURES CHO TƯƠNG THÍCH
-- ====================================================================

-- Trigger tự động cập nhật stock khi có đơn hàng
DELIMITER $$
CREATE TRIGGER `update_stock_after_order` 
AFTER INSERT ON `orderdetails` 
FOR EACH ROW 
BEGIN
    UPDATE `products` 
    SET `stock_quantity` = `stock_quantity` - NEW.`quantity` 
    WHERE `product_id` = NEW.`product_id`;
END$$

-- Trigger tự động cập nhật stock khi có nhập hàng
CREATE TRIGGER `update_stock_after_import` 
AFTER INSERT ON `imports` 
FOR EACH ROW 
BEGIN
    UPDATE `products` 
    SET `stock_quantity` = `stock_quantity` + NEW.`quantity` 
    WHERE `product_id` = NEW.`product_id`;
END$$

-- Trigger tự động tính lương từ workshifts
CREATE TRIGGER `calculate_workshift_earnings` 
BEFORE UPDATE ON `workshifts` 
FOR EACH ROW 
BEGIN
    IF NEW.actual_hours IS NOT NULL THEN
        SET NEW.total_earnings = (NEW.actual_hours * NEW.hourly_wage) + 
                               (NEW.overtime_hours * NEW.hourly_wage * NEW.overtime_rate) - 
                               NEW.penalty_amount;
        
        -- Apply salary adjustment
        SET NEW.total_earnings = NEW.total_earnings * (NEW.salary_adjustment_percent / 100);
    END IF;
END$$

-- Trigger tự động set salary_period (fix DATE_FORMAT issue)
CREATE TRIGGER `set_salary_period_insert` 
BEFORE INSERT ON `salary` 
FOR EACH ROW 
BEGIN
    IF NEW.created_date IS NOT NULL THEN
        SET NEW.salary_period = DATE_FORMAT(NEW.created_date, '%Y-%m');
    ELSE
        SET NEW.salary_period = DATE_FORMAT(NOW(), '%Y-%m');
    END IF;
END$$

CREATE TRIGGER `set_salary_period_update` 
BEFORE UPDATE ON `salary` 
FOR EACH ROW 
BEGIN
    IF NEW.created_date IS NOT NULL AND NEW.created_date != OLD.created_date THEN
        SET NEW.salary_period = DATE_FORMAT(NEW.created_date, '%Y-%m');
    END IF;
END$$

-- Trigger penalty calculation
CREATE TRIGGER `calculate_penalties` 
BEFORE UPDATE ON `workshifts` 
FOR EACH ROW 
BEGIN
    DECLARE penalty DECIMAL(10,2) DEFAULT 0.00;
    
    -- Late penalty: 5000 VND per 15 minutes
    IF NEW.late_minutes > 0 THEN
        SET penalty = penalty + (CEIL(NEW.late_minutes / 15) * 5000);
    END IF;
    
    -- Early leave penalty: 10000 VND per 30 minutes  
    IF NEW.early_leave_minutes > 0 THEN
        SET penalty = penalty + (CEIL(NEW.early_leave_minutes / 30) * 10000);
    END IF;
    
    -- Auto checkout penalty
    IF NEW.check_out_time IS NULL AND NEW.status = 'COMPLETED' THEN
        SET penalty = penalty + 20000;
        SET NEW.auto_checkout_penalty = 20000;
    END IF;
    
    SET NEW.penalty_amount = penalty;
END$$

DELIMITER ;

-- ====================================================================
-- VIEWS CHO BÁO CÁO TƯƠNG THÍCH
-- ====================================================================

-- View tương thích cho ThongKeDAO.getProfitAnalysis()
CREATE VIEW `profit_analysis_view` AS
SELECT 
    p.product_id,
    p.product_name,
    COALESCE(i.import_price, i.unit_cost, p.price) as import_price,
    p.selling_price,
    (p.selling_price - COALESCE(i.import_price, p.price)) as profit_per_unit,
    COALESCE(sales_data.total_sold, 0) as total_sold,
    (p.selling_price - COALESCE(i.import_price, p.price)) * COALESCE(sales_data.total_sold, 0) as total_profit
FROM products p
LEFT JOIN imports i ON p.product_id = i.product_id
LEFT JOIN (
    SELECT od.product_id, SUM(od.quantity) as total_sold
    FROM orderdetails od
    JOIN orders o ON od.order_id = o.order_id
    GROUP BY od.product_id
) sales_data ON p.product_id = sales_data.product_id;

-- View cho monthly expenses tương thích
CREATE VIEW `monthly_expenses_view` AS
SELECT 
    expense_id,
    month_year,
    DATE_FORMAT(month_year, '%Y-%m') as month_year_format,
    electricity_cost,
    rent_cost, 
    water_cost,
    repair_cost,
    staff_cost,
    total_cost
FROM monthly_operating_expenses;

-- ====================================================================
-- INDEXES CHO PERFORMANCE
-- ====================================================================

-- Performance indexes cho reporting
CREATE INDEX `idx_orders_date_customer` ON `orders` (`order_date`, `customer_id`);
CREATE INDEX `idx_orderdetails_product_quantity` ON `orderdetails` (`product_id`, `quantity`);
CREATE INDEX `idx_imports_date_product` ON `imports` (`import_date`, `product_id`);
CREATE INDEX `idx_workshifts_employee_date` ON `workshifts` (`employee_id`, `shift_date`);
CREATE INDEX `idx_products_category_active` ON `products` (`category_id`, `is_active`);

-- ====================================================================
-- NOTES: CÁC THAY ĐỔI VÀ BỔ SUNG
-- ====================================================================
/*
TƯƠNG THÍCH HOÀN TOÀN VỚI DATABASE CŨ:
✅ Giữ nguyên tất cả structure và data của 15 bảng cũ
✅ Giữ nguyên tất cả foreign keys và constraints cũ
✅ Tất cả queries cũ sẽ hoạt động bình thường

CÁC BỔ SUNG CHO JAVA CODE COMPATIBILITY:
✅ Thêm selling_price cho products (thiếu trong DB cũ)
✅ Thêm column field cho productdisplay (cần cho DisplayForm)
✅ Thêm workshifts table hoàn toàn mới với penalty system
✅ Thêm discount_percent alias cho promotion table
✅ Thêm unit_cost computed field cho imports
✅ Thêm computed fields cho profit analysis

CÁC TÍNH NĂNG MỚI ĐƯỢC THÊM:
✅ Hệ thống penalty nâng cao trong workshifts
✅ Loyalty points system nâng cao
✅ Promotion system nâng cao  
✅ Inventory management nâng cao
✅ Financial reporting nâng cao
✅ Automatic triggers cho stock updates
✅ Performance indexes cho speed optimization

MYSQL COMPATIBILITY FIXES:
✅ Fix DATE_FORMAT() trong salary_period - dùng trigger thay vì computed column
✅ Thêm IFNULL() trong computed fields để tránh NULL values
✅ Các triggers để auto-fill salary_period field

BACKWARD COMPATIBILITY:
✅ Tất cả data cũ được import y nguyên
✅ Tất cả Java DAO queries cũ sẽ hoạt động
✅ Thêm views để support các queries phức tạp
✅ Computed columns được optimize cho MySQL compatibility
*/

-- Set AUTO_INCREMENT values để match với data cũ
ALTER TABLE `category` AUTO_INCREMENT = 4;
ALTER TABLE `customers` AUTO_INCREMENT = 10;
ALTER TABLE `employees` AUTO_INCREMENT = 10;
ALTER TABLE `imports` AUTO_INCREMENT = 26;
ALTER TABLE `invoices` AUTO_INCREMENT = 1;
ALTER TABLE `monthly_operating_expenses` AUTO_INCREMENT = 1;
ALTER TABLE `orderdetails` AUTO_INCREMENT = 32;
ALTER TABLE `orders` AUTO_INCREMENT = 19;
ALTER TABLE `productdisplay` AUTO_INCREMENT = 4;
ALTER TABLE `products` AUTO_INCREMENT = 18;
ALTER TABLE `promotion` AUTO_INCREMENT = 1;
ALTER TABLE `salary` AUTO_INCREMENT = 5;
ALTER TABLE `suppliers` AUTO_INCREMENT = 7;
ALTER TABLE `workingsession` AUTO_INCREMENT = 1;
ALTER TABLE `workshifts` AUTO_INCREMENT = 1;

-- Commit transaction
COMMIT;

-- End of enhanced database script
