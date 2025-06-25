-- ====================================================================
-- MARKETMINI DATABASE - PART 2: TRANSACTIONS & WORKFLOW
-- ====================================================================

-- ====================================================================
-- 6. BẢNG NHẬP HÀNG (IMPORTS) - Enhanced
-- ====================================================================
CREATE TABLE `imports` (
  `import_id` int(11) NOT NULL AUTO_INCREMENT,
  `import_code` varchar(20) DEFAULT NULL,
  `product_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_cost` decimal(10,2) NOT NULL,
  `total_cost` decimal(12,2) NOT NULL,
  `import_date` date NOT NULL,
  `expiry_date` date DEFAULT NULL,
  `employee_id` int(11) NOT NULL,
  `batch_number` varchar(100) DEFAULT NULL,
  `warehouse_location` varchar(100) DEFAULT NULL,
  `import_status` enum('PENDING','RECEIVED','QUALITY_CHECK','COMPLETED','CANCELLED') DEFAULT 'PENDING',
  `notes` text DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`import_id`),
  UNIQUE KEY `import_code` (`import_code`),
  KEY `product_id` (`product_id`),
  KEY `supplier_id` (`supplier_id`),
  KEY `employee_id` (`employee_id`),
  KEY `idx_import_date` (`import_date`),
  KEY `idx_import_status` (`import_status`),
  CONSTRAINT `imports_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  CONSTRAINT `imports_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `imports_ibfk_3` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 7. BẢNG ĐƠN HÀNG (ORDERS) - Enhanced
-- ====================================================================
CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_code` varchar(20) DEFAULT NULL,
  `employee_id` int(11) NOT NULL,
  `customer_id` int(11) NOT NULL,
  `order_date` datetime NOT NULL,
  `total_amount` decimal(12,2) NOT NULL,
  `discount_amount` decimal(10,2) DEFAULT 0,
  `tax_amount` decimal(10,2) DEFAULT 0,
  `final_amount` decimal(12,2) NOT NULL,
  `points_used` int(11) DEFAULT 0,
  `points_earned` int(11) DEFAULT 0,
  `payment_method` enum('CASH','CARD','TRANSFER','POINTS') DEFAULT 'CASH',
  `payment_status` enum('PENDING','PAID','PARTIAL','CANCELLED') DEFAULT 'PENDING',
  `order_status` enum('PENDING','PROCESSING','COMPLETED','CANCELLED','REFUNDED') DEFAULT 'PENDING',
  `notes` text DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `order_code` (`order_code`),
  KEY `employee_id` (`employee_id`),
  KEY `customer_id` (`customer_id`),
  KEY `idx_order_date` (`order_date`),
  KEY `idx_order_status` (`order_status`),
  KEY `idx_payment_status` (`payment_status`),
  CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`),
  CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 8. BẢNG CHI TIẾT ĐƠN HÀNG (ORDERDETAILS) - Enhanced
-- ====================================================================
CREATE TABLE `orderdetails` (
  `order_detail_id` int(11) NOT NULL AUTO_INCREMENT,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unit_price` decimal(10,2) NOT NULL,
  `discount_percent` decimal(5,2) DEFAULT 0,
  `discount_amount` decimal(10,2) DEFAULT 0,
  `final_price` decimal(10,2) NOT NULL,
  `line_total` decimal(12,2) NOT NULL,
  `promotion_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`order_detail_id`),
  KEY `order_id` (`order_id`),
  KEY `product_id` (`product_id`),
  KEY `promotion_id` (`promotion_id`),
  CONSTRAINT `orderdetails_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`) ON DELETE CASCADE,
  CONSTRAINT `orderdetails_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 9. BẢNG LƯƠNG (SALARY) - Enhanced
-- ====================================================================
CREATE TABLE `salary` (
  `salary_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL,
  `period_month` int(11) NOT NULL,
  `period_year` int(11) NOT NULL,
  `base_salary` decimal(12,2) DEFAULT 0,
  `hourly_wage` decimal(8,2) DEFAULT 25000,
  `total_hours` decimal(6,2) DEFAULT 0,
  `regular_hours` decimal(6,2) DEFAULT 0,
  `overtime_hours` decimal(6,2) DEFAULT 0,
  `bonus` decimal(10,2) DEFAULT 0,
  `allowances` decimal(10,2) DEFAULT 0,
  `deductions` decimal(10,2) DEFAULT 0,
  `penalty_amount` decimal(10,2) DEFAULT 0,
  `gross_salary` decimal(12,2) DEFAULT 0,
  `tax_amount` decimal(10,2) DEFAULT 0,
  `insurance_amount` decimal(10,2) DEFAULT 0,
  `net_salary` decimal(12,2) DEFAULT 0,
  `payment_date` date DEFAULT NULL,
  `payment_status` enum('PENDING','PAID','CANCELLED') DEFAULT 'PENDING',
  `created_date` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`salary_id`),
  UNIQUE KEY `unique_employee_period` (`employee_id`,`period_month`,`period_year`),
  KEY `idx_salary_period` (`period_year`,`period_month`),
  KEY `idx_payment_status` (`payment_status`),
  CONSTRAINT `salary_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 10. BẢNG CA LÀM VIỆC (WORKSHIFTS) - Enhanced với Penalty System
-- ====================================================================
CREATE TABLE `workshifts` (
  `shift_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL,
  `shift_date` date NOT NULL,
  `shift_type` enum('SANG','CHIEU','TOI','FULL','OVERTIME') DEFAULT 'FULL',
  `start_time` time NOT NULL DEFAULT '08:00:00',
  `end_time` time DEFAULT NULL,
  `planned_hours` decimal(4,2) DEFAULT 8.00,
  `actual_hours` decimal(4,2) DEFAULT NULL,
  `break_minutes` int(11) DEFAULT 60,
  `overtime_hours` decimal(4,2) DEFAULT 0.00,
  `status` enum('SCHEDULED','IN_PROGRESS','COMPLETED','ABSENT','CANCELLED') DEFAULT 'SCHEDULED',
  -- Enhanced Penalty System Fields
  `check_in_time` time DEFAULT NULL,
  `check_out_time` time DEFAULT NULL,
  `late_minutes` int(11) DEFAULT 0,
  `early_leave_minutes` int(11) DEFAULT 0,
  `is_scheduled` tinyint(1) DEFAULT 1,
  `penalty_amount` decimal(10,2) DEFAULT 0,
  `penalty_reason` text DEFAULT NULL,
  `salary_adjustment_percent` decimal(5,2) DEFAULT 100.00,
  `adjustment_reason` text DEFAULT NULL,
  `auto_checkout_penalty` tinyint(1) DEFAULT 0,
  `hourly_wage` decimal(8,2) DEFAULT NULL,
  `total_earnings` decimal(10,2) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`shift_id`),
  UNIQUE KEY `unique_employee_shift_date` (`employee_id`,`shift_date`,`shift_type`),
  KEY `idx_shift_date` (`shift_date`),
  KEY `idx_shift_status` (`status`),
  KEY `idx_employee_date` (`employee_id`,`shift_date`),
  CONSTRAINT `workshifts_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 11. BẢNG QUY TẮC PHẠT (PENALTY_RULES)
-- ====================================================================
CREATE TABLE `penalty_rules` (
  `rule_id` int(11) NOT NULL AUTO_INCREMENT,
  `rule_type` enum('NO_CHECKIN','LATE_ARRIVAL','UNSCHEDULED_WORK','NO_CHECKOUT','EARLY_LEAVE','ABSENT') NOT NULL,
  `rule_name` varchar(255) NOT NULL,
  `penalty_amount` decimal(10,2) DEFAULT 0,
  `penalty_percent` decimal(5,2) DEFAULT 0,
  `description` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`rule_id`),
  KEY `idx_rule_type` (`rule_type`),
  KEY `idx_rule_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; 