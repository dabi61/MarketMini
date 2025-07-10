-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Jul 10, 2025 at 11:28 PM
-- Server version: 10.4.28-MariaDB
-- PHP Version: 8.0.28

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `marketmini`
--

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `category_id` int(11) NOT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`category_id`, `category_name`, `description`) VALUES
(1, 'Dầu gội', 'Dầu gội đầu'),
(2, 'Dầu ăn', ''),
(3, 'Sữa tươi', '');

-- --------------------------------------------------------

--
-- Table structure for table `customers`
--

CREATE TABLE `customers` (
  `customer_id` int(11) NOT NULL,
  `phone_number` varchar(20) NOT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `points` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `customers`
--

INSERT INTO `customers` (`customer_id`, `phone_number`, `full_name`, `points`) VALUES
(1, '0442452424', 'Chị Hạnh', NULL),
(2, '0356246724', 'Chị Minh', NULL),
(4, '0352507345', 'Anh Cường Nguyễn', 24936),
(5, '0726846578', 'Chị Thủy', NULL),
(6, '0', 'Vô danh', 6000),
(7, '0973847374', 'Chị Thủy', NULL),
(8, '04927475648', 'Chị Hạnh', NULL),
(9, '0434526345', 'Anh Quang', 3000);

-- --------------------------------------------------------

--
-- Table structure for table `employees`
--

CREATE TABLE `employees` (
  `employee_id` int(11) NOT NULL,
  `employee_name` varchar(100) DEFAULT NULL,
  `password` varchar(100) DEFAULT NULL,
  `full_name` varchar(255) DEFAULT NULL,
  `sex` varchar(11) DEFAULT NULL,
  `role` int(11) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `email` varchar(50) DEFAULT NULL,
  `date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `employees`
--

INSERT INTO `employees` (`employee_id`, `employee_name`, `password`, `full_name`, `sex`, `role`, `phone`, `email`, `date`) VALUES
(1, 'admin1', 'pass1', 'Nguyen Ba Quoc Cuong', 'Nam', 1, '0382951529', 'cuongngba7@gmail.com', '2025-06-02'),
(2, 'baphu', '1', 'Nguyen Ba Phu', 'Nam', 2, '0382951523', 'baphu7@gmail.com', '2025-06-12'),
(4, 'Linh', '12', 'Lê Linh', 'Nam', 2, '0987656557', 'linh@gmail.com', '2025-06-24'),
(5, 'Linh', '12', 'Lê Linh', 'Nữ', 2, '0332343232', 'linh@gmail.com', '2025-06-23'),
(8, 'duc', '1234', 'le van duc', 'Nam', 2, '0945324366', 'duc@gmail.com', '2025-06-06'),
(10, 'binh', '1', 'binh', 'Nam', 2, '0382951529', 'binh@gmail.com', '2025-06-25');

-- --------------------------------------------------------

--
-- Table structure for table `imports`
--

CREATE TABLE `imports` (
  `import_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `supplier_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT NULL,
  `import_price` int(11) DEFAULT NULL,
  `import_date` date DEFAULT NULL,
  `employee_id` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `imports`
--

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
(24, 16, 1, 300, 145000, '2025-06-21', 1),
(26, 11, 3, 1000, 35000, '2025-06-21', 1),
(27, 12, 3, 1000, 35000, '2025-06-21', 1),
(28, 13, 3, 1000, 20000, '2025-06-21', 1),
(29, 14, 3, 1000, 20000, '2025-06-21', 1),
(30, 15, 3, 1000, 20000, '2025-06-21', 1);

-- --------------------------------------------------------

--
-- Table structure for table `invoices`
--

CREATE TABLE `invoices` (
  `invoice_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `issue_date` date DEFAULT NULL,
  `total_amount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `monthly_operating_expenses`
--

CREATE TABLE `monthly_operating_expenses` (
  `expense_id` int(11) NOT NULL,
  `month_year` date DEFAULT NULL,
  `electricity_cost` int(11) DEFAULT NULL,
  `rent_cost` int(11) DEFAULT NULL,
  `water_cost` int(11) DEFAULT NULL,
  `repair_cost` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orderdetails`
--

CREATE TABLE `orderdetails` (
  `order_detail_id` int(11) NOT NULL,
  `order_id` int(11) NOT NULL,
  `product_id` int(11) NOT NULL,
  `quantity` int(11) DEFAULT NULL,
  `unit_price` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orderdetails`
--

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

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `order_id` int(11) NOT NULL,
  `employee_id` int(11) NOT NULL,
  `order_date` date DEFAULT NULL,
  `total_amount` int(11) DEFAULT NULL,
  `customer_id` int(11) NOT NULL,
  `final_amount` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `orders`
--

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

-- --------------------------------------------------------

--
-- Table structure for table `productdisplay`
--

CREATE TABLE `productdisplay` (
  `display_id` int(11) NOT NULL,
  `product_id` int(11) DEFAULT NULL,
  `row` varchar(50) NOT NULL,
  `floor` varchar(50) NOT NULL,
  `start_date` date NOT NULL,
  `end_date` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `productdisplay`
--

INSERT INTO `productdisplay` (`display_id`, `product_id`, `row`, `floor`, `start_date`, `end_date`) VALUES
(2, 5, 'A1', '2', '2025-06-23', '2025-06-25'),
(3, 6, 'A1', '1', '2025-06-22', '2025-06-25');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `product_id` int(11) NOT NULL,
  `product_name` varchar(255) DEFAULT NULL,
  `category_id` int(11) DEFAULT NULL,
  `price` int(11) DEFAULT NULL,
  `stock_quantity` int(11) DEFAULT NULL,
  `unit` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`product_id`, `product_name`, `category_id`, `price`, `stock_quantity`, `unit`) VALUES
(3, 'Thái dương Xanh', 1, 15000, 4994, 'dây'),
(4, 'Dầu gội Thái dương', 1, 12000, 986, 'dây'),
(5, 'Dầu gội PANTENE', 1, 145000, 283, 'chai'),
(6, 'Dầu ăn Cái Lân 400ml', 2, 45000, 193, 'chai'),
(7, 'Dầu ăn Neptune 2l', 2, 125000, 250, 'chai'),
(8, 'Dầu ăn meizan 1l', 2, 55000, 3195, 'chai'),
(9, 'Dầu gội Head and shoulder', 1, 13000, 1996, 'dây'),
(10, 'Dầu ăn Simple', 2, 60000, 300, 'cahi'),
(11, 'Sữa tươi tiệt trùng GreenFarm rất ít đường - Vinamilk', 3, 35000, 2997, 'lốc'),
(12, 'Sữa tươi tiệt trùng GreenFarm tổ yến - Vinamilk', 3, 35000, 3000, 'lốc'),
(13, 'Sữa tươi tiệt trùng 100% Không đường - Vinamilk', 3, 20000, 3000, 'lốc'),
(14, 'Sữa tươi tiệt trùng 100% Sôcôla - Vinamilk', 3, 20000, 2999, 'lốc'),
(15, 'Sữa tươi tiệt trùng 100% Hương Dâu - Vinamilk', 3, 20000, 3000, 'lốc'),
(16, 'Dầu Gội Dược Liệu Nguyên Xuân Dưỡng Tóc 450Ml', 1, 145000, 300, 'chai');

-- --------------------------------------------------------

--
-- Table structure for table `promotion`
--

CREATE TABLE `promotion` (
  `promotion_id` int(11) NOT NULL,
  `promotion_name` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `product_id` int(11) DEFAULT NULL,
  `discount` int(11) DEFAULT NULL,
  `discounted_price` int(11) DEFAULT NULL,
  `original_price` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `salary`
--

CREATE TABLE `salary` (
  `salary_id` int(11) NOT NULL,
  `employee_id` int(11) DEFAULT NULL,
  `total_hours` decimal(10,2) DEFAULT NULL,
  `hourly_wage` int(11) DEFAULT NULL,
  `bonus` int(11) DEFAULT NULL,
  `penalty_deduction` int(11) DEFAULT 0,
  `gross_salary` decimal(12,2) DEFAULT NULL,
  `net_salary` decimal(12,2) DEFAULT NULL,
  `overtime_pay` decimal(12,2) DEFAULT 0.00,
  `payment_date` date DEFAULT NULL,
  `created_date` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `salary`
--

INSERT INTO `salary` (`salary_id`, `employee_id`, `total_hours`, `hourly_wage`, `bonus`, `penalty_deduction`, `gross_salary`, `net_salary`, `overtime_pay`, `payment_date`, `created_date`) VALUES
(1, 1, 0.00, 30000, 0, 0, 0.00, 0.00, 0.00, '2025-06-24', '2025-06-24'),
(2, 2, 0.00, 18000, 200000, 0, 200000.00, 200000.00, 0.00, NULL, '2025-06-24'),
(3, 4, 0.00, 18000, 0, 0, 0.00, 0.00, 0.00, NULL, '2025-06-24'),
(4, 5, 0.00, 18000, 0, 0, 0.00, 0.00, 0.00, NULL, '2025-06-24'),
(5, 8, 0.00, 18000, 0, 0, 0.00, 0.00, 0.00, '2025-07-11', '2025-06-24'),
(6, 10, 0.00, 333, 0, 0, 0.00, 0.00, 0.00, '2025-07-31', '2025-07-11');

--
-- Triggers `salary`
--
DELIMITER $$
CREATE TRIGGER `calculate_total_hours_insert` BEFORE INSERT ON `salary` FOR EACH ROW BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    
    -- Tính tổng giờ làm từ bảng workingsession (chỉ tính các ca đã hoàn thành)
    SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
    FROM workingsession 
    WHERE employee_id = NEW.employee_id 
    AND work_status = 'COMPLETED';
    
    SET NEW.total_hours = total_working_hours;
    
    -- Tính gross_salary = total_hours * hourly_wage + bonus
    SET NEW.gross_salary = (NEW.total_hours * NEW.hourly_wage) + NEW.bonus;
    
    -- Tính net_salary = gross_salary - penalty_deduction
    SET NEW.net_salary = NEW.gross_salary - NEW.penalty_deduction;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `calculate_total_hours_update` BEFORE UPDATE ON `salary` FOR EACH ROW BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    
    -- Tính tổng giờ làm từ bảng workingsession (chỉ tính các ca đã hoàn thành)
    SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
    FROM workingsession 
    WHERE employee_id = NEW.employee_id 
    AND work_status = 'COMPLETED';
    
    SET NEW.total_hours = total_working_hours;
    
    -- Tính gross_salary = total_hours * hourly_wage + bonus
    SET NEW.gross_salary = (NEW.total_hours * NEW.hourly_wage) + NEW.bonus;
    
    -- Tính net_salary = gross_salary - penalty_deduction
    SET NEW.net_salary = NEW.gross_salary - NEW.penalty_deduction;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `supplier_id` int(11) NOT NULL,
  `supplier_name` varchar(255) DEFAULT NULL,
  `phone` varchar(20) DEFAULT NULL,
  `address` varchar(500) DEFAULT NULL,
  `email` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`supplier_id`, `supplier_name`, `phone`, `address`, `email`) VALUES
(1, 'CT TNHH Vạn Xuân', '0979932423', 'Kim Liên, Đống Đa, Hà Nội', 'vanxuanspl@gmail.com'),
(2, 'CT TNHH CALOFIC', '0747164638', 'Thanh Xuân, Hà Nội', 'calofit_ct@gmail.com'),
(3, 'Công ty Cổ phần sữa Việt Nam - Vinamilk', '0384726342', 'Số 10, đường Tân Trào, Phường Tân Phú, Quận 7, Thành phố Hồ Chí Minh, Việt Nam.', 'vinamilkvn@gmail.com'),
(4, 'Xuân Hạ', '0982342342', 'Hà Nội', 'xuanha@gmail.com'),
(5, 'Hòa hải', '0333232239', 'Thanh Xuân, Hà Nội', 'linh12@gmail.com'),
(6, 'Lan Hạ', '0343453452', 'Thanh Hóa', 'xuanha@gmail.com'),
(7, 'CTTNHH Mộc Châu', '0382951529', 'Thanh Xuân Nam, Hà Nội', 'mocchau@gmail.com');

-- --------------------------------------------------------

--
-- Table structure for table `workingsession`
--

CREATE TABLE `workingsession` (
  `working_session_id` int(11) NOT NULL,
  `employee_id` int(11) DEFAULT NULL,
  `login_time` datetime DEFAULT NULL,
  `logout_time` datetime DEFAULT NULL,
  `working_hours` decimal(10,2) DEFAULT NULL,
  `date` date DEFAULT NULL,
  `work_status` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `workingsession`
--

INSERT INTO `workingsession` (`working_session_id`, `employee_id`, `login_time`, `logout_time`, `working_hours`, `date`, `work_status`) VALUES
(1, 10, '2025-07-11 04:20:27', '2025-07-11 09:20:29', 0.00, '2025-07-11', 'COMPLETED'),
(2, 10, '2025-07-11 04:20:32', '2025-07-11 04:20:34', 0.00, '2025-07-11', 'COMPLETED'),
(3, 10, '2025-07-11 04:22:30', '2025-07-11 04:22:34', 0.00, '2025-07-11', 'COMPLETED'),
(4, 10, '2025-07-11 04:25:45', '2025-07-11 04:25:50', 0.00, '2025-07-11', 'COMPLETED');

--
-- Triggers `workingsession`
--
DELIMITER $$
CREATE TRIGGER `update_salary_on_workingsession_delete` AFTER DELETE ON `workingsession` FOR EACH ROW BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    DECLARE current_hourly_wage INT DEFAULT 0;
    DECLARE current_bonus INT DEFAULT 0;
    DECLARE current_penalty INT DEFAULT 0;
    
    -- Chỉ cập nhật khi work_status của record bị xóa là COMPLETED
    IF OLD.work_status = 'COMPLETED' THEN
        -- Tính tổng giờ làm từ bảng workingsession
        SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
        FROM workingsession 
        WHERE employee_id = OLD.employee_id 
        AND work_status = 'COMPLETED';
        
        -- Lấy thông tin lương hiện tại
        SELECT hourly_wage, bonus, penalty_deduction 
        INTO current_hourly_wage, current_bonus, current_penalty
        FROM salary 
        WHERE employee_id = OLD.employee_id;
        
        -- Cập nhật salary nếu có record tồn tại
        IF current_hourly_wage IS NOT NULL THEN
            UPDATE salary 
            SET total_hours = total_working_hours,
                gross_salary = (total_working_hours * current_hourly_wage) + current_bonus,
                net_salary = ((total_working_hours * current_hourly_wage) + current_bonus) - current_penalty
            WHERE employee_id = OLD.employee_id;
        END IF;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `update_salary_on_workingsession_insert` AFTER INSERT ON `workingsession` FOR EACH ROW BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    DECLARE current_hourly_wage INT DEFAULT 0;
    DECLARE current_bonus INT DEFAULT 0;
    DECLARE current_penalty INT DEFAULT 0;
    
    -- Chỉ cập nhật khi work_status là COMPLETED
    IF NEW.work_status = 'COMPLETED' THEN
        -- Tính tổng giờ làm từ bảng workingsession
        SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
        FROM workingsession 
        WHERE employee_id = NEW.employee_id 
        AND work_status = 'COMPLETED';
        
        -- Lấy thông tin lương hiện tại
        SELECT hourly_wage, bonus, penalty_deduction 
        INTO current_hourly_wage, current_bonus, current_penalty
        FROM salary 
        WHERE employee_id = NEW.employee_id;
        
        -- Cập nhật salary nếu có record tồn tại
        IF current_hourly_wage IS NOT NULL THEN
            UPDATE salary 
            SET total_hours = total_working_hours,
                gross_salary = (total_working_hours * current_hourly_wage) + current_bonus,
                net_salary = ((total_working_hours * current_hourly_wage) + current_bonus) - current_penalty
            WHERE employee_id = NEW.employee_id;
        END IF;
    END IF;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `update_salary_on_workingsession_update` AFTER UPDATE ON `workingsession` FOR EACH ROW BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    DECLARE current_hourly_wage INT DEFAULT 0;
    DECLARE current_bonus INT DEFAULT 0;
    DECLARE current_penalty INT DEFAULT 0;
    
    -- Chỉ cập nhật khi work_status thay đổi thành COMPLETED hoặc từ COMPLETED thành khác
    IF (NEW.work_status = 'COMPLETED' AND OLD.work_status != 'COMPLETED') OR 
       (OLD.work_status = 'COMPLETED' AND NEW.work_status != 'COMPLETED') THEN
        
        -- Tính tổng giờ làm từ bảng workingsession
        SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
        FROM workingsession 
        WHERE employee_id = NEW.employee_id 
        AND work_status = 'COMPLETED';
        
        -- Lấy thông tin lương hiện tại
        SELECT hourly_wage, bonus, penalty_deduction 
        INTO current_hourly_wage, current_bonus, current_penalty
        FROM salary 
        WHERE employee_id = NEW.employee_id;
        
        -- Cập nhật salary nếu có record tồn tại
        IF current_hourly_wage IS NOT NULL THEN
            UPDATE salary 
            SET total_hours = total_working_hours,
                gross_salary = (total_working_hours * current_hourly_wage) + current_bonus,
                net_salary = ((total_working_hours * current_hourly_wage) + current_bonus) - current_penalty
            WHERE employee_id = NEW.employee_id;
        END IF;
    END IF;
END
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`category_id`);

--
-- Indexes for table `customers`
--
ALTER TABLE `customers`
  ADD PRIMARY KEY (`customer_id`);

--
-- Indexes for table `employees`
--
ALTER TABLE `employees`
  ADD PRIMARY KEY (`employee_id`);

--
-- Indexes for table `imports`
--
ALTER TABLE `imports`
  ADD PRIMARY KEY (`import_id`),
  ADD KEY `product_id` (`product_id`),
  ADD KEY `supplier_id` (`supplier_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`invoice_id`),
  ADD KEY `order_id` (`order_id`);

--
-- Indexes for table `monthly_operating_expenses`
--
ALTER TABLE `monthly_operating_expenses`
  ADD PRIMARY KEY (`expense_id`);

--
-- Indexes for table `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD PRIMARY KEY (`order_detail_id`),
  ADD KEY `order_id` (`order_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`order_id`),
  ADD KEY `employee_id` (`employee_id`),
  ADD KEY `customer_id` (`customer_id`);

--
-- Indexes for table `productdisplay`
--
ALTER TABLE `productdisplay`
  ADD PRIMARY KEY (`display_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`product_id`),
  ADD KEY `category_id` (`category_id`);

--
-- Indexes for table `promotion`
--
ALTER TABLE `promotion`
  ADD PRIMARY KEY (`promotion_id`),
  ADD KEY `product_id` (`product_id`);

--
-- Indexes for table `salary`
--
ALTER TABLE `salary`
  ADD PRIMARY KEY (`salary_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`supplier_id`);

--
-- Indexes for table `workingsession`
--
ALTER TABLE `workingsession`
  ADD PRIMARY KEY (`working_session_id`),
  ADD KEY `employee_id` (`employee_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `category_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `customers`
--
ALTER TABLE `customers`
  MODIFY `customer_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `employees`
--
ALTER TABLE `employees`
  MODIFY `employee_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `imports`
--
ALTER TABLE `imports`
  MODIFY `import_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT for table `invoices`
--
ALTER TABLE `invoices`
  MODIFY `invoice_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `monthly_operating_expenses`
--
ALTER TABLE `monthly_operating_expenses`
  MODIFY `expense_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `orderdetails`
--
ALTER TABLE `orderdetails`
  MODIFY `order_detail_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=32;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `order_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;

--
-- AUTO_INCREMENT for table `productdisplay`
--
ALTER TABLE `productdisplay`
  MODIFY `display_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `product_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;

--
-- AUTO_INCREMENT for table `promotion`
--
ALTER TABLE `promotion`
  MODIFY `promotion_id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `salary`
--
ALTER TABLE `salary`
  MODIFY `salary_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `supplier_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `workingsession`
--
ALTER TABLE `workingsession`
  MODIFY `working_session_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `imports`
--
ALTER TABLE `imports`
  ADD CONSTRAINT `imports_ibfk_1` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`supplier_id`),
  ADD CONSTRAINT `imports_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`),
  ADD CONSTRAINT `imports_ibfk_3` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`);

--
-- Constraints for table `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `invoices_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`);

--
-- Constraints for table `orderdetails`
--
ALTER TABLE `orderdetails`
  ADD CONSTRAINT `orderdetails_ibfk_1` FOREIGN KEY (`order_id`) REFERENCES `orders` (`order_id`),
  ADD CONSTRAINT `orderdetails_ibfk_2` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`),
  ADD CONSTRAINT `orders_ibfk_2` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`customer_id`);

--
-- Constraints for table `productdisplay`
--
ALTER TABLE `productdisplay`
  ADD CONSTRAINT `productdisplay_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`);

--
-- Constraints for table `promotion`
--
ALTER TABLE `promotion`
  ADD CONSTRAINT `promotion_ibfk_1` FOREIGN KEY (`product_id`) REFERENCES `products` (`product_id`);

--
-- Constraints for table `salary`
--
ALTER TABLE `salary`
  ADD CONSTRAINT `salary_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`);

--
-- Constraints for table `workingsession`
--
ALTER TABLE `workingsession`
  ADD CONSTRAINT `workingsession_ibfk_1` FOREIGN KEY (`employee_id`) REFERENCES `employees` (`employee_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
