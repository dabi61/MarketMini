-- ====================================================================
-- MARKETMINI COMPLETE DATABASE FROM SCRATCH
-- ====================================================================
-- Database thiết kế hoàn toàn từ đầu dựa trên phân tích Java code
-- Created: 2025-06-25
-- Version: 1.0 - Production Ready

-- Drop database if exists and create new
DROP DATABASE IF EXISTS `marketmini`;
CREATE DATABASE `marketmini` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `marketmini`;

-- ====================================================================
-- 1. EMPLOYEES TABLE - Quản lý nhân viên
-- ====================================================================
CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_name` varchar(50) NOT NULL UNIQUE COMMENT 'Username for login',
  `password` varchar(255) NOT NULL COMMENT 'Password for login',
  `full_name` varchar(100) NOT NULL COMMENT 'Full display name',
  `sex` enum('Nam','Nữ','Khác') DEFAULT 'Nam' COMMENT 'Gender',
  `role` tinyint(4) NOT NULL DEFAULT 2 COMMENT '1=Admin, 2=Staff',
  `phone` varchar(15) DEFAULT NULL COMMENT 'Phone number',
  `email` varchar(100) DEFAULT NULL COMMENT 'Email address',
  `date` date DEFAULT NULL COMMENT 'Hire date',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `uk_employee_name` (`employee_name`),
  KEY `idx_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Employee management table';

-- ====================================================================
-- 2. CATEGORY TABLE - Danh mục sản phẩm
-- ====================================================================
CREATE TABLE `category` (
  `category_id` int(11) NOT NULL AUTO_INCREMENT,
  `category_name` varchar(100) NOT NULL COMMENT 'Category name',
  `description` text DEFAULT NULL COMMENT 'Category description',
  `parent_category_id` int(11) DEFAULT NULL COMMENT 'Parent category for hierarchy',
  `category_code` varchar(20) NOT NULL UNIQUE COMMENT 'Auto-generated category code',
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'Active status',
  `display_order` int(11) DEFAULT 0 COMMENT 'Display order',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`category_id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_parent_category` (`parent_category_id`),
  KEY `idx_active_order` (`is_active`, `display_order`),
  CONSTRAINT `fk_category_parent` FOREIGN KEY (`parent_category_id`) REFERENCES `category` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product category management';

-- ====================================================================
-- 3. SUPPLIERS TABLE - Nhà cung cấp
-- ====================================================================
CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL AUTO_INCREMENT,
  `supplier_name` varchar(100) NOT NULL COMMENT 'Supplier company name',
  `phone` varchar(15) DEFAULT NULL COMMENT 'Contact phone',
  `address` text DEFAULT NULL COMMENT 'Supplier address',
  `email` varchar(100) DEFAULT NULL COMMENT 'Contact email',
  `contact_person` varchar(100) DEFAULT NULL COMMENT 'Contact person name',
  `tax_code` varchar(20) DEFAULT NULL COMMENT 'Tax identification number',
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'Active status',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplier_id`),
  KEY `idx_supplier_active` (`is_active`),
  KEY `idx_supplier_name` (`supplier_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Supplier management';

-- ====================================================================
-- 4. PRODUCTS TABLE - Sản phẩm
-- ====================================================================
CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_name` varchar(150) NOT NULL COMMENT 'Product name',
  `category_id` int(11) NOT NULL COMMENT 'Category reference',
  `price` decimal(15,2) NOT NULL DEFAULT 0 COMMENT 'Import/cost price',
  `selling_price` decimal(15,2) NOT NULL DEFAULT 0 COMMENT 'Selling price',
  `stock_quantity` int(11) NOT NULL DEFAULT 0 COMMENT 'Current stock quantity',
  `unit` varchar(20) NOT NULL DEFAULT 'cái' COMMENT 'Unit of measurement',
  `description` text DEFAULT NULL COMMENT 'Product description',
  `barcode` varchar(50) DEFAULT NULL COMMENT 'Product barcode',
  `min_stock_level` int(11) DEFAULT 10 COMMENT 'Minimum stock alert level',
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'Active status',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  KEY `idx_category` (`category_id`),
  KEY `idx_product_name` (`product_name`),
  KEY `idx_stock_level` (`stock_quantity`),
  KEY `idx_active` (`is_active`),
  CONSTRAINT `fk_product_category` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Product inventory management';

-- ====================================================================
-- 5. CUSTOMERS TABLE - Khách hàng
-- ====================================================================
CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL AUTO_INCREMENT,
  `full_name` varchar(100) NOT NULL COMMENT 'Customer full name',
  `phone_number` varchar(15) NOT NULL COMMENT 'Customer phone number',
  `email` varchar(100) DEFAULT NULL COMMENT 'Customer email',
  `address` text DEFAULT NULL COMMENT 'Customer address',
  `points` int(11) NOT NULL DEFAULT 0 COMMENT 'Loyalty points',
  `customer_type` enum('REGULAR','VIP','PREMIUM') DEFAULT 'REGULAR' COMMENT 'Customer tier',
  `date_of_birth` date DEFAULT NULL COMMENT 'Customer birthday',
  `gender` enum('Nam','Nữ','Khác') DEFAULT NULL COMMENT 'Customer gender',
  `is_active` tinyint(1) DEFAULT 1 COMMENT 'Active status',
  `created_at` timestamp DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  KEY `idx_phone` (`phone_number`),
  KEY `idx_customer_name` (`full_name`),
  KEY `idx_customer_type` (`customer_type`),
  KEY `idx_points` (`points`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Customer management with loyalty system';
