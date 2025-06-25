-- ====================================================================
-- MARKETMINI DATABASE - PART 3: REMAINING TABLES & VIEWS
-- ====================================================================

-- ====================================================================
-- 12. BẢNG PHIÊN LÀM VIỆC (WORKINGSESSION) - Enhanced
-- ====================================================================
CREATE TABLE `workingsession` (
  `working_session_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_id` int(11) NOT NULL,
  `shift_id` int(11) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  `logout_time` datetime DEFAULT NULL,
  `working_hours` decimal(6,2) DEFAULT NULL,
  `break_start` datetime DEFAULT NULL,
  `break_end` datetime DEFAULT NULL,
  `total_break_minutes` int(11) DEFAULT 0,
  `overtime_start` datetime DEFAULT NULL,
  `overtime_end` datetime DEFAULT NULL,
  `session_date` date DEFAULT NULL,
  `work_status` enum('ACTIVE','BREAK','OVERTIME','COMPLETED','CANCELLED') DEFAULT 'ACTIVE',
  `ip_address` varchar(45) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `session_notes` text DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`working_session_id`),
  KEY `employee_id` (`employee_id`),
  KEY `shift_id` (`shift_id`),
  KEY `idx_session_date` (`session_date`),
  KEY `idx_work_status` (`work_status`),
  CONSTRAINT `workingsession_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`),
  CONSTRAINT `workingsession_shift_fk` FOREIGN KEY (`shift_id`) REFERENCES `workshifts` (`shift_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 13. BẢNG KHUYẾN MÃI (PROMOTION) - Enhanced
-- ====================================================================
CREATE TABLE `promotion` (
  `promotion_id` int(11) NOT NULL AUTO_INCREMENT,
  `promotion_code` varchar(20) DEFAULT NULL,
  `promotion_name` varchar(255) NOT NULL,
  `promotion_type` enum('PERCENTAGE','FIXED_AMOUNT','BUY_X_GET_Y','COMBO','POINTS_MULTIPLIER') DEFAULT 'PERCENTAGE',
  `start_date` datetime NOT NULL,
  `end_date` datetime NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `discount_percent` decimal(5,2) DEFAULT 0,
  `discount_amount` decimal(10,2) DEFAULT 0,
  `min_purchase_amount` decimal(10,2) DEFAULT 0,
  `max_discount_amount` decimal(10,2) DEFAULT NULL,
  `usage_limit` int(11) DEFAULT NULL,
  `usage_count` int(11) DEFAULT 0,
  `customer_type` enum('ALL','REGULAR','VIP','PREMIUM') DEFAULT 'ALL',
  `is_active` tinyint(1) DEFAULT 1,
  `description` text DEFAULT NULL,
  `terms_conditions` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`promotion_id`),
  UNIQUE KEY `promotion_code` (`promotion_code`),
  KEY `product_id` (`product_id`),
  KEY `category_id` (`category_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_promotion_active` (`is_active`),
  KEY `idx_promotion_dates` (`start_date`,`end_date`),
  CONSTRAINT `promotion_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `promotion_ibfk_2` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`),
  CONSTRAINT `promotion_ibfk_3` FOREIGN KEY (`created_by`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 14. BẢNG HIỂN THỊ SẢN PHẨM (PRODUCTDISPLAY) - Enhanced
-- ====================================================================
CREATE TABLE `productdisplay` (
  `display_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_id` int(11) NOT NULL,
  `display_location` varchar(100) NOT NULL,
  `row_position` varchar(50) NOT NULL,
  `floor_level` varchar(50) NOT NULL,
  `shelf_position` varchar(50) DEFAULT NULL,
  `start_date` datetime NOT NULL,
  `end_date` datetime DEFAULT NULL,
  `priority_level` int(11) DEFAULT 1,
  `display_type` enum('SHELF','ENDCAP','PROMOTION','SEASONAL','SPECIAL') DEFAULT 'SHELF',
  `display_notes` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_by` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`display_id`),
  KEY `product_id` (`product_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_display_active` (`is_active`),
  KEY `idx_display_dates` (`start_date`,`end_date`),
  CONSTRAINT `productdisplay_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  CONSTRAINT `productdisplay_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 15. BẢNG CHI PHÍ HOẠT ĐỘNG (MONTHLY_OPERATING_EXPENSES) - Enhanced
-- ====================================================================
CREATE TABLE `monthly_operating_expenses` (
  `expense_id` int(11) NOT NULL AUTO_INCREMENT,
  `expense_code` varchar(20) DEFAULT NULL,
  `period_month` int(11) NOT NULL,
  `period_year` int(11) NOT NULL,
  `electricity_cost` decimal(12,2) DEFAULT 0,
  `water_cost` decimal(12,2) DEFAULT 0,
  `rent_cost` decimal(12,2) DEFAULT 0,
  `internet_cost` decimal(12,2) DEFAULT 0,
  `phone_cost` decimal(12,2) DEFAULT 0,
  `repair_cost` decimal(12,2) DEFAULT 0,
  `maintenance_cost` decimal(12,2) DEFAULT 0,
  `cleaning_cost` decimal(12,2) DEFAULT 0,
  `security_cost` decimal(12,2) DEFAULT 0,
  `marketing_cost` decimal(12,2) DEFAULT 0,
  `other_cost` decimal(12,2) DEFAULT 0,
  `total_cost` decimal(12,2) DEFAULT 0,
  `notes` text DEFAULT NULL,
  `approved_by` int(11) DEFAULT NULL,
  `approval_date` datetime DEFAULT NULL,
  `expense_status` enum('DRAFT','PENDING','APPROVED','PAID','CANCELLED') DEFAULT 'DRAFT',
  `created_by` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`expense_id`),
  UNIQUE KEY `expense_code` (`expense_code`),
  UNIQUE KEY `unique_period` (`period_month`,`period_year`),
  KEY `approved_by` (`approved_by`),
  KEY `created_by` (`created_by`),
  KEY `idx_expense_period` (`period_year`,`period_month`),
  KEY `idx_expense_status` (`expense_status`),
  CONSTRAINT `monthly_expenses_approved_fk` FOREIGN KEY (`approved_by`) REFERENCES `employees` (`employee_id`),
  CONSTRAINT `monthly_expenses_created_fk` FOREIGN KEY (`created_by`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 16. BẢNG HÓA ĐƠN (INVOICES) - Enhanced
-- ====================================================================
CREATE TABLE `invoices` (
  `invoice_id` int(11) NOT NULL AUTO_INCREMENT,
  `invoice_code` varchar(20) DEFAULT NULL,
  `order_id` int(11) NOT NULL,
  `invoice_date` datetime NOT NULL,
  `due_date` datetime DEFAULT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `tax_amount` decimal(10,2) DEFAULT 0,
  `discount_amount` decimal(10,2) DEFAULT 0,
  `total_amount` decimal(12,2) NOT NULL,
  `paid_amount` decimal(12,2) DEFAULT 0,
  `remaining_amount` decimal(12,2) DEFAULT 0,
  `payment_status` enum('UNPAID','PARTIAL','PAID','OVERDUE','CANCELLED') DEFAULT 'UNPAID',
  `invoice_status` enum('DRAFT','SENT','VIEWED','PAID','CANCELLED') DEFAULT 'DRAFT',
  `customer_name` varchar(255) DEFAULT NULL,
  `customer_address` text DEFAULT NULL,
  `customer_tax_code` varchar(50) DEFAULT NULL,
  `notes` text DEFAULT NULL,
  `created_by` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`invoice_id`),
  UNIQUE KEY `invoice_code` (`invoice_code`),
  KEY `order_id` (`order_id`),
  KEY `created_by` (`created_by`),
  KEY `idx_invoice_date` (`invoice_date`),
  KEY `idx_payment_status` (`payment_status`),
  KEY `idx_invoice_status` (`invoice_status`),
  CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `invoices_ibfk_2` FOREIGN KEY (`created_by`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 17. BẢNG THANH TOÁN (PAYMENTS)
-- ====================================================================
CREATE TABLE `payments` (
  `payment_id` int(11) NOT NULL AUTO_INCREMENT,
  `payment_code` varchar(20) DEFAULT NULL,
  `order_id` int(11) DEFAULT NULL,
  `invoice_id` int(11) DEFAULT NULL,
  `payment_date` datetime NOT NULL,
  `payment_amount` decimal(12,2) NOT NULL,
  `payment_method` enum('CASH','CARD','TRANSFER','POINTS','INSTALLMENT') NOT NULL,
  `card_type` varchar(50) DEFAULT NULL,
  `transaction_id` varchar(100) DEFAULT NULL,
  `reference_number` varchar(100) DEFAULT NULL,
  `payment_status` enum('PENDING','COMPLETED','FAILED','CANCELLED','REFUNDED') DEFAULT 'PENDING',
  `exchange_rate` decimal(10,4) DEFAULT 1.0000,
  `currency` varchar(3) DEFAULT 'VND',
  `notes` text DEFAULT NULL,
  `processed_by` int(11) DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `payment_code` (`payment_code`),
  KEY `order_id` (`order_id`),
  KEY `invoice_id` (`invoice_id`),
  KEY `processed_by` (`processed_by`),
  KEY `idx_payment_date` (`payment_date`),
  KEY `idx_payment_status` (`payment_status`),
  CONSTRAINT `payments_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  CONSTRAINT `payments_ibfk_2` FOREIGN KEY (`invoice_id`) REFERENCES `invoices` (`invoice_id`),
  CONSTRAINT `payments_ibfk_3` FOREIGN KEY (`processed_by`) REFERENCES `employees` (`employee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; 