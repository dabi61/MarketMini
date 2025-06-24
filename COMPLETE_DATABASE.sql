-- ================================================
-- DATABASE MARKETMINI - COMPLETE SETUP
-- ================================================
-- Tạo database hoàn chỉnh từ đầu với tính năng mới
-- Version: 2.0 - Enhanced with Work Shift Management

-- Drop database if exists and create new
DROP DATABASE IF EXISTS `marketmini`;
CREATE DATABASE `marketmini` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `marketmini`;

-- ================================================
-- 1. BẢNG DANH MỤC (CATEGORY) - Enhanced
-- ================================================
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
  KEY `idx_category_code` (`category_code`),
  KEY `idx_category_active` (`is_active`),
  KEY `category_parent_fk` (`parent_category_id`),
  CONSTRAINT `category_parent_fk` FOREIGN KEY (`parent_category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 2. BẢNG KHÁCH HÀNG (CUSTOMERS)
-- ================================================
CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(20) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `points` int(11) DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `phone_number` (`phone_number`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 3. BẢNG NHÂN VIÊN (EMPLOYEES)
-- ================================================
CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_name` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `sex` varchar(11) DEFAULT NULL,
  `role` int(11) DEFAULT 2,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `employee_name` (`employee_name`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 4. BẢNG SẢN PHẨM (PRODUCTS)
-- ================================================
CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(255) NOT NULL,
  `category_id` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `stock_quantity` int(11) DEFAULT 0,
  `unit` varchar(50) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  KEY `category_id` (`category_id`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 5. BẢNG NHÀ CUNG CẤP (SUPPLIERS)
-- ================================================
CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(255) NOT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 6. BẢNG NHẬP HÀNG (IMPORTS)
-- ================================================
CREATE TABLE `imports` (
  `import_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `import_price` int(11) NOT NULL,
  `import_date` date NOT NULL,
  `employee_id` int(11) NOT NULL,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`import_id`),
  KEY `product_id` (`product_id`),
  KEY `supplier_id` (`supplier_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `imports_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  CONSTRAINT `imports_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `imports_ibfk_3` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 7. BẢNG ĐƠN HÀNG (ORDERS)
-- ================================================
CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL,
  `order_date` date NOT NULL,
  `total_amount` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `final_amount` int(11) DEFAULT NULL,
  `discount_amount` int(11) DEFAULT 0,
  `points_used` int(11) DEFAULT 0,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  KEY `employee_id` (`employee_id`),
  KEY `customer_id` (`customer_id`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 8. BẢNG CHI TIẾT ĐƠN HÀNG (ORDERDETAILS)
-- ================================================
CREATE TABLE `orderdetails` (
  `order_detail_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` int(11) NOT NULL,
  PRIMARY KEY (`order_detail_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `orderdetails_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `orderdetails_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 9. BẢNG LƯƠNG (SALARY)
-- ================================================
CREATE TABLE `salary` (
  `salary_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL,
  `total_hours` decimal(5,2) DEFAULT 0.00,
  `hourly_wage` int(11) DEFAULT 18000,
  `bonus` int(11) DEFAULT 0,
  `payment_date` date DEFAULT NULL,
  `created_date` date DEFAULT NULL,
  `period_month` int(11) DEFAULT NULL,
  `period_year` int(11) DEFAULT NULL,
  PRIMARY KEY (`salary_id`),
  KEY `employee_id` (`employee_id`),
  CONSTRAINT `salary_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 10. BẢNG QUẢN LÝ CA LÀM VIỆC (WORKSHIFTS) - NEW
-- ================================================
CREATE TABLE `workshifts` (
  `shift_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL,
  `shift_date` date NOT NULL,
  `shift_type` enum('SANG','CHIEU','TOI','FULL') DEFAULT 'FULL',
  `start_time` time NOT NULL DEFAULT '08:00:00',
  `end_time` time DEFAULT NULL,
  `planned_hours` decimal(4,2) DEFAULT 8.00,
  `actual_hours` decimal(4,2) DEFAULT NULL,
  `break_minutes` int(11) DEFAULT 60,
  `overtime_hours` decimal(4,2) DEFAULT 0.00,
  `status` enum('SCHEDULED','IN_PROGRESS','COMPLETED','ABSENT') DEFAULT 'SCHEDULED',
  `notes` text DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`shift_id`),
  KEY `employee_id` (`employee_id`),
  KEY `shift_date` (`shift_date`),
  KEY `status` (`status`),
  KEY `idx_workshift_employee_date` (`employee_id`, `shift_date`),
  CONSTRAINT `workshifts_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 11. BẢNG PHIÊN LÀM VIỆC (WORKINGSESSION) - Enhanced
-- ================================================
CREATE TABLE `workingsession` (
  `working_session_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL,
  `login_time` datetime DEFAULT NULL,
  `logout_time` datetime DEFAULT NULL,
  `working_hours` decimal(10,2) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `work_status` varchar(20) DEFAULT NULL,
  `shift_id` int(11) DEFAULT NULL,
  `break_start` datetime DEFAULT NULL,
  `break_end` datetime DEFAULT NULL,
  `total_break_minutes` int(11) DEFAULT 0,
  `overtime_start` datetime DEFAULT NULL,
  `session_notes` text DEFAULT NULL,
  PRIMARY KEY (`working_session_id`),
  KEY `employee_id` (`employee_id`),
  KEY `workingsession_shift_fk` (`shift_id`),
  CONSTRAINT `workingsession_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`),
  CONSTRAINT `workingsession_shift_fk` FOREIGN KEY (`shift_id`) REFERENCES `workshifts` (`shift_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 12. BẢNG KHUYẾN MÃI (PROMOTION)
-- ================================================
CREATE TABLE `promotion` (
  `promotion_id` int(11) NOT NULL AUTO_INCREMENT,
  `promotion_name` varchar(255) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `discount` int(11) DEFAULT 0,
  `discounted_price` int(11) DEFAULT NULL,
  `original_price` int(11) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`promotion_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `promotion_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 13. BẢNG HIỂN THỊ SẢN PHẨM (PRODUCTDISPLAY)
-- ================================================
CREATE TABLE `productdisplay` (
  `display_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `row` varchar(50) NOT NULL,
  `floor` varchar(50) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL,
  PRIMARY KEY (`display_id`),
  KEY `product_id` (`product_id`),
  CONSTRAINT `productdisplay_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 14. BẢNG CHI PHÍ HOẠT ĐỘNG (MONTHLY_OPERATING_EXPENSES)
-- ================================================
CREATE TABLE `monthly_operating_expenses` (
  `expense_id` int(11) NOT NULL AUTO_INCREMENT,
  `month_year` date NOT NULL,
  `electricity_cost` int(11) DEFAULT 0,
  `rent_cost` int(11) DEFAULT 0,
  `water_cost` int(11) DEFAULT 0,
  `repair_cost` int(11) DEFAULT 0,
  `other_cost` int(11) DEFAULT 0,
  `notes` text DEFAULT NULL,
  PRIMARY KEY (`expense_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ================================================
-- 15. BẢNG HÓA ĐƠN (INVOICES)
-- ================================================
CREATE TABLE `invoices` (
  `invoice_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `issue_date` date NOT NULL,
  `total_amount` int(11) NOT NULL,
  PRIMARY KEY (`invoice_id`),
  KEY `order_id` (`order_id`),
  CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; 