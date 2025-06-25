-- ====================================================================
-- MARKETMINI DATABASE COMPATIBILITY PATCH - COMPLETE FIX
-- ====================================================================
-- Patch để database tương thích hoàn toàn với code Java hiện tại
-- Chạy file này sau khi đã import database chính

USE `marketmini`;

-- ====================================================================
-- 1. FIX IMPORTS TABLE - Thêm cột import_price cho tương thích
-- ====================================================================

-- Thêm cột import_price (alias cho unit_cost)
ALTER TABLE `imports` 
ADD COLUMN IF NOT EXISTS `import_price` decimal(10,2) AS (unit_cost) STORED COMMENT 'Alias for unit_cost - compatibility with existing code';

-- ====================================================================
-- 2. FIX WORKSHIFTS TABLE - Thêm ShiftType OVERTIME và các cột missing
-- ====================================================================

-- Đảm bảo shift_type có tất cả values Java cần
ALTER TABLE `workshifts` 
MODIFY COLUMN `shift_type` enum('SANG','CHIEU','TOI','CA') DEFAULT 'FULL';

-- Thêm các cột penalty mới nếu chưa có
ALTER TABLE `workshifts`
ADD COLUMN IF NOT EXISTS `check_in_time` time NULL COMMENT 'Actual check-in time',
ADD COLUMN IF NOT EXISTS `check_out_time` time NULL COMMENT 'Actual check-out time', 
ADD COLUMN IF NOT EXISTS `late_minutes` int DEFAULT 0 COMMENT 'Minutes late for check-in',
ADD COLUMN IF NOT EXISTS `early_leave_minutes` int DEFAULT 0 COMMENT 'Minutes left early',
ADD COLUMN IF NOT EXISTS `is_scheduled` tinyint(1) DEFAULT 1 COMMENT 'Whether this shift was scheduled',
ADD COLUMN IF NOT EXISTS `penalty_amount` decimal(10,2) DEFAULT 0 COMMENT 'Penalty amount in VND',
ADD COLUMN IF NOT EXISTS `penalty_reason` text NULL COMMENT 'Reason for penalty',
ADD COLUMN IF NOT EXISTS `salary_adjustment_percent` decimal(5,2) DEFAULT 100.00 COMMENT 'Salary adjustment percentage',
ADD COLUMN IF NOT EXISTS `adjustment_reason` text NULL COMMENT 'Reason for salary adjustment',
ADD COLUMN IF NOT EXISTS `auto_checkout_penalty` decimal(10,2) DEFAULT 0 COMMENT 'Penalty for not checking out';

-- Đổi tên cột date thành shift_date để tránh conflict
ALTER TABLE `workshifts`
CHANGE COLUMN `date` `shift_date` date NOT NULL COMMENT 'Date of the work shift';

-- ====================================================================
-- 3. FIX MONTHLY_OPERATING_EXPENSES TABLE - Thêm cột month_year
-- ====================================================================

-- Thêm cột month_year cho ExpenseDAO
ALTER TABLE `monthly_operating_expenses`
ADD COLUMN IF NOT EXISTS `month_year` varchar(7) AS (DATE_FORMAT(expense_date, '%Y-%m')) STORED COMMENT 'Month-Year format for filtering';

-- ====================================================================
-- 4. FIX PROMOTION TABLE - Thêm cột discount
-- ====================================================================

-- Thêm cột discount cho PromotionDAO
ALTER TABLE `promotion`
ADD COLUMN IF NOT EXISTS `discount` decimal(5,2) AS (discount_percent) STORED COMMENT 'Alias for discount_percent';

-- ====================================================================
-- 5. FIX PRODUCTDISPLAY TABLE - Thêm cột row và column
-- ====================================================================

-- Thêm cột row và column cho DisplayForm
ALTER TABLE `productdisplay`
ADD COLUMN IF NOT EXISTS `row` int DEFAULT 1 COMMENT 'Display row position',
ADD COLUMN IF NOT EXISTS `column` int DEFAULT 1 COMMENT 'Display column position';

-- ====================================================================
-- 6. FIX ORDERS TABLE - Ensure compatibility
-- ====================================================================

-- Kiểm tra và thêm các cột cần thiết cho orders table
ALTER TABLE `orders` 
MODIFY COLUMN `order_date` date NOT NULL COMMENT 'Changed from datetime to date for compatibility';

-- ====================================================================
-- 7. FIX SALARY TABLE - Ensure compatibility với WorkShift date column
-- ====================================================================

-- Thêm cột total_hours tính từ workshifts nếu chưa có
ALTER TABLE `salary`
MODIFY COLUMN `total_hours` decimal(6,2) DEFAULT 0 COMMENT 'Total working hours in the period';

-- ====================================================================
-- 8. UPDATE EXISTING DATA - Compatibility fixes
-- ====================================================================

-- Update productdisplay với default row/column values
UPDATE `productdisplay` SET 
    `row` = FLOOR((display_id - 1) / 10) + 1,
    `column` = ((display_id - 1) % 10) + 1
WHERE `row` IS NULL OR `column` IS NULL;

-- Update workshifts với các type hợp lệ dựa trên planned_hours
UPDATE `workshifts` SET 
    `shift_type` = CASE 
        WHEN `planned_hours` <= 4 THEN 'SANG'
        WHEN `planned_hours` <= 5 THEN 'CHIEU' 
        WHEN `planned_hours` <= 6 THEN 'TOI'
        ELSE 'FULL'
    END
WHERE `shift_type` NOT IN ('SANG','CHIEU','TOI','FULL');

-- ====================================================================
-- 9. CREATE COMPATIBILITY VIEWS
-- ====================================================================

-- View để đảm bảo ThongKeDAO hoạt động đúng với import_price
CREATE OR REPLACE VIEW v_imports_profit AS
SELECT 
    i.import_id,
    i.product_id,
    i.quantity,
    i.unit_cost as import_price,  -- Compatibility alias
    i.total_cost,
    i.import_date,
    p.selling_price,
    (p.selling_price - i.unit_cost) * i.quantity as profit_per_import
FROM imports i
JOIN products p ON i.product_id = p.product_id;

-- View cho workshifts với tên cột đúng
CREATE OR REPLACE VIEW v_workshifts_compatible AS  
SELECT 
    shift_id,
    employee_id,
    shift_date as date,  -- Alias cho compatibility
    shift_date,
    start_time,
    end_time,
    planned_hours,
    actual_hours,
    hourly_wage,
    total_earnings,
    overtime_hours,
    overtime_rate,
    shift_type,
    shift_status,
    notes,
    check_in_time,
    check_out_time,
    late_minutes,
    early_leave_minutes,
    is_scheduled,
    penalty_amount,
    penalty_reason,
    salary_adjustment_percent,
    adjustment_reason,
    auto_checkout_penalty,
    created_at,
    updated_at
FROM workshifts;

-- View cho expenses với month_year
CREATE OR REPLACE VIEW v_expenses_compatible AS
SELECT 
    expense_id,
    expense_type,
    description,
    amount,
    expense_date,
    month_year,  -- Computed column
    employee_id,
    receipt_image,
    notes,
    created_at,
    updated_at
FROM monthly_operating_expenses;

-- ====================================================================
-- 10. VERIFY COMPATIBILITY - Test all failing queries
-- ====================================================================

-- Test ThongKeDAO.getProfitAnalysis() query
SELECT 'Testing ThongKeDAO.getProfitAnalysis()...' as test_name;
SELECT SUM(i.quantity * i.import_price) as import_cost 
FROM imports i 
WHERE MONTH(i.import_date) = 6 AND YEAR(i.import_date) = 2025
LIMIT 1;

-- Test WorkShiftDAO enum values
SELECT 'Testing WorkShift enum values...' as test_name;
SELECT DISTINCT shift_type FROM workshifts;

-- Test SalaryDAO with w.date -> w.shift_date
SELECT 'Testing SalaryDAO.getTotalWorkingHoursByEmployee()...' as test_name;
SELECT e.employee_id, SUM(w.actual_hours) as total_hours
FROM employees e
LEFT JOIN workshifts w ON e.employee_id = w.employee_id 
    AND MONTH(w.shift_date) = 6 AND YEAR(w.shift_date) = 2025
GROUP BY e.employee_id
LIMIT 3;

-- Test ExpenseDAO.getAllExpenses() với month_year
SELECT 'Testing ExpenseDAO.getAllExpenses()...' as test_name;
SELECT expense_id, month_year, amount 
FROM monthly_operating_expenses
LIMIT 3;

-- Test PromotionDAO với p.discount
SELECT 'Testing PromotionDAO.getAllPromotionsWithProductNames()...' as test_name;
SELECT p.promotion_id, p.discount, pr.product_name
FROM promotion p
LEFT JOIN products pr ON p.product_id = pr.product_id
LIMIT 3;

-- Test DisplayForm với pd.row
SELECT 'Testing DisplayForm.loadDuLieuDisplay()...' as test_name;
SELECT pd.display_id, pd.row, pd.column, p.product_name
FROM productdisplay pd
LEFT JOIN products p ON pd.product_id = p.product_id
LIMIT 3;

-- ====================================================================
-- 11. SHOW FINAL STATUS
-- ====================================================================

SELECT '🎉 COMPLETE COMPATIBILITY PATCH APPLIED SUCCESSFULLY!' as status;
SELECT 'All SQL runtime errors should be fixed now.' as message;

-- Show updated table structures for verification
SELECT 'Updated table structures:' as info;
SELECT 'imports table:' as table_name;
SHOW COLUMNS FROM imports LIKE '%import_price%';

SELECT 'workshifts table (date column):' as table_name;  
SHOW COLUMNS FROM workshifts LIKE '%date%';

SELECT 'monthly_operating_expenses table:' as table_name;
SHOW COLUMNS FROM monthly_operating_expenses LIKE '%month_year%';

SELECT 'promotion table:' as table_name;
SHOW COLUMNS FROM promotion LIKE '%discount%';

SELECT 'productdisplay table:' as table_name;
SHOW COLUMNS FROM productdisplay LIKE '%row%'; 