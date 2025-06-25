-- ====================================================================
-- MARKETMINI - COMPLETE DATABASE SYSTEM WITH ENHANCED FEATURES
-- ====================================================================
-- Dự án: Hệ thống quản lý siêu thị mini hoàn chỉnh
-- Version: 3.0 Enhanced - Penalty System + Category Hierarchy + Full Features
-- Author: AI Assistant based on codebase analysis
-- Date: 2025

-- ====================================================================
-- DATABASE SETUP
-- ====================================================================
DROP DATABASE IF EXISTS `marketmini`;
CREATE DATABASE `marketmini` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `marketmini`;

-- ====================================================================
-- 1. BẢNG DANH MỤC SẢN PHẨM (CATEGORY) - Enhanced với Hierarchy
-- ====================================================================
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
  CONSTRAINT `category_parent_fk` FOREIGN KEY (`parent_category_id`) 
    REFERENCES `category` (`category_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 2. BẢNG KHÁCH HÀNG (CUSTOMERS) - Enhanced với Points System
-- ====================================================================
CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL AUTO_INCREMENT,
  `phone_number` varchar(20) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `points` int(11) DEFAULT 0,
  `total_spent` bigint(20) DEFAULT 0,
  `member_since` date DEFAULT NULL,
  `last_purchase` date DEFAULT NULL,
  `customer_type` enum('REGULAR','VIP','PREMIUM') DEFAULT 'REGULAR',
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`customer_id`),
  UNIQUE KEY `phone_number` (`phone_number`),
  KEY `idx_customer_type` (`customer_type`),
  KEY `idx_points` (`points`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 3. BẢNG NHÂN VIÊN (EMPLOYEES) - Enhanced với More Fields
-- ====================================================================
CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL AUTO_INCREMENT,
  `employee_name` varchar(100) NOT NULL,
  `employee_code` varchar(20) DEFAULT NULL,
  `password` varchar(100) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `sex` enum('Nam','Nữ','Khác') DEFAULT 'Nam',
  `role` int(11) DEFAULT 2 COMMENT '1=Admin, 2=Staff, 3=Manager',
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `hire_date` date DEFAULT NULL,
  `birth_date` date DEFAULT NULL,
  `base_salary` decimal(10,2) DEFAULT 5000000.00,
  `hourly_wage` decimal(8,2) DEFAULT 25000.00,
  `is_active` tinyint(1) DEFAULT 1,
  `last_login` datetime DEFAULT NULL,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `employee_name` (`employee_name`),
  UNIQUE KEY `employee_code` (`employee_code`),
  UNIQUE KEY `phone` (`phone`),
  KEY `idx_employee_active` (`is_active`),
  KEY `idx_employee_role` (`role`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 4. BẢNG SẢN PHẨM (PRODUCTS) - Enhanced
-- ====================================================================
CREATE TABLE `products` (
  `product_id` int(11) NOT NULL AUTO_INCREMENT,
  `product_code` varchar(50) DEFAULT NULL,
  `product_name` varchar(255) NOT NULL,
  `category_id` int(11) NOT NULL,
  `price` int(11) NOT NULL,
  `cost_price` int(11) DEFAULT NULL,
  `stock_quantity` int(11) DEFAULT 0,
  `min_stock_level` int(11) DEFAULT 10,
  `max_stock_level` int(11) DEFAULT 1000,
  `unit` varchar(50) DEFAULT NULL,
  `brand` varchar(100) DEFAULT NULL,
  `barcode` varchar(100) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`product_id`),
  UNIQUE KEY `product_code` (`product_code`),
  UNIQUE KEY `barcode` (`barcode`),
  KEY `category_id` (`category_id`),
  KEY `idx_product_active` (`is_active`),
  KEY `idx_stock_level` (`stock_quantity`),
  CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- ====================================================================
-- 5. BẢNG NHÀ CUNG CẤP (SUPPLIERS) - Enhanced
-- ====================================================================
CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL AUTO_INCREMENT,
  `supplier_code` varchar(20) DEFAULT NULL,
  `supplier_name` varchar(255) NOT NULL,
  `contact_person` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `tax_code` varchar(50) DEFAULT NULL,
  `bank_account` varchar(100) DEFAULT NULL,
  `payment_terms` varchar(255) DEFAULT NULL,
  `rating` decimal(2,1) DEFAULT 5.0,
  `is_active` tinyint(1) DEFAULT 1,
  `created_at` datetime DEFAULT CURRENT_TIMESTAMP,
  `updated_at` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`supplier_id`),
  UNIQUE KEY `supplier_code` (`supplier_code`),
  KEY `idx_supplier_active` (`is_active`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci; 