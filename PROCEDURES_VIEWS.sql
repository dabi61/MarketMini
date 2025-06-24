-- ================================================
-- VIEWS, STORED PROCEDURES & FUNCTIONS
-- ================================================
-- Chạy file này sau khi đã import dữ liệu mẫu

USE `marketmini`;

-- ================================================
-- 1. VIEWS - CÁC VIEW THỐNG KÊ
-- ================================================

-- View thống kê ca làm nhân viên
CREATE OR REPLACE VIEW `v_employee_shift_summary` AS
SELECT 
    e.employee_id,
    e.full_name,
    e.role,
    DATE(ws.shift_date) as work_date,
    ws.shift_type,
    ws.status,
    ws.planned_hours,
    ws.actual_hours,
    ws.overtime_hours,
    (COALESCE(ws.actual_hours, ws.planned_hours) * s.hourly_wage) as daily_earnings,
    (COALESCE(ws.overtime_hours, 0) * s.hourly_wage * 1.5) as overtime_earnings
FROM employees e
LEFT JOIN workshifts ws ON e.employee_id = ws.employee_id
LEFT JOIN salary s ON e.employee_id = s.employee_id
WHERE ws.shift_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY)
ORDER BY e.employee_id, ws.shift_date DESC;

-- View thống kê doanh thu sản phẩm
CREATE OR REPLACE VIEW `v_product_sales_summary` AS
SELECT 
    p.product_id,
    p.product_name,
    c.category_name,
    SUM(od.quantity) as total_sold,
    SUM(od.quantity * od.unit_price) as total_revenue,
    COUNT(DISTINCT o.order_id) as order_count,
    AVG(od.unit_price) as avg_price,
    p.stock_quantity as current_stock
FROM products p
JOIN category c ON p.category_id = c.category_id
LEFT JOIN orderdetails od ON p.product_id = od.product_id
LEFT JOIN orders o ON od.order_id = o.order_id
GROUP BY p.product_id, p.product_name, c.category_name, p.stock_quantity
ORDER BY total_revenue DESC;

-- View thống kê nhân viên bán hàng
CREATE OR REPLACE VIEW `v_employee_sales_summary` AS
SELECT 
    e.employee_id,
    e.full_name,
    e.role,
    COUNT(o.order_id) as total_orders,
    SUM(o.final_amount) as total_sales,
    AVG(o.final_amount) as avg_order_value,
    MIN(o.order_date) as first_sale_date,
    MAX(o.order_date) as last_sale_date
FROM employees e
LEFT JOIN orders o ON e.employee_id = o.employee_id
GROUP BY e.employee_id, e.full_name, e.role
ORDER BY total_sales DESC;

-- View tình trạng tồn kho chi tiết
CREATE OR REPLACE VIEW `v_inventory_status` AS
SELECT 
    p.product_id,
    p.product_name,
    c.category_name,
    p.stock_quantity,
    p.price,
    (p.stock_quantity * p.price) as inventory_value,
    COALESCE(recent_imports.last_import_date, 'N/A') as last_import_date,
    COALESCE(recent_imports.last_import_price, 0) as last_import_price,
    CASE 
        WHEN p.stock_quantity = 0 THEN 'Hết hàng'
        WHEN p.stock_quantity <= 50 THEN 'Sắp hết'
        WHEN p.stock_quantity <= 200 THEN 'Ít'
        ELSE 'Đủ'
    END as stock_status
FROM products p
JOIN category c ON p.category_id = c.category_id
LEFT JOIN (
    SELECT 
        product_id,
        MAX(import_date) as last_import_date,
        import_price as last_import_price
    FROM imports i1
    WHERE import_date = (
        SELECT MAX(import_date) 
        FROM imports i2 
        WHERE i2.product_id = i1.product_id
    )
    GROUP BY product_id, import_price
) recent_imports ON p.product_id = recent_imports.product_id
ORDER BY p.stock_quantity ASC;

-- ================================================
-- 2. STORED PROCEDURES
-- ================================================

-- Procedure chốt ca làm việc
DELIMITER //
CREATE OR REPLACE PROCEDURE `CloseWorkShift`(
    IN p_employee_id INT,
    IN p_shift_date DATE,
    IN p_end_time TIME,
    IN p_notes TEXT
)
BEGIN
    DECLARE v_shift_id INT DEFAULT NULL;
    DECLARE v_start_time TIME;
    DECLARE v_planned_hours DECIMAL(4,2);
    DECLARE v_actual_hours DECIMAL(4,2);
    DECLARE v_overtime DECIMAL(4,2) DEFAULT 0.00;
    
    -- Lấy thông tin ca làm đang diễn ra
    SELECT shift_id, start_time, planned_hours 
    INTO v_shift_id, v_start_time, v_planned_hours
    FROM workshifts 
    WHERE employee_id = p_employee_id 
      AND shift_date = p_shift_date 
      AND status = 'IN_PROGRESS'
    LIMIT 1;
    
    IF v_shift_id IS NOT NULL THEN
        -- Tính số giờ làm thực tế
        SET v_actual_hours = TIME_TO_SEC(TIMEDIFF(p_end_time, v_start_time)) / 3600;
        
        -- Tính overtime (nếu làm vượt giờ dự kiến)
        IF v_actual_hours > v_planned_hours THEN
            SET v_overtime = v_actual_hours - v_planned_hours;
        END IF;
        
        -- Cập nhật ca làm
        UPDATE workshifts 
        SET 
            end_time = p_end_time,
            actual_hours = v_actual_hours,
            overtime_hours = v_overtime,
            status = 'COMPLETED',
            notes = COALESCE(p_notes, notes),
            updated_at = NOW()
        WHERE shift_id = v_shift_id;
        
        -- Cập nhật working session nếu có
        UPDATE workingsession 
        SET 
            logout_time = CONCAT(p_shift_date, ' ', p_end_time),
            working_hours = v_actual_hours,
            work_status = 'COMPLETED'
        WHERE employee_id = p_employee_id 
          AND DATE(login_time) = p_shift_date;
          
        SELECT 'SUCCESS' as result, v_shift_id as shift_id, v_actual_hours as actual_hours, v_overtime as overtime_hours;
    ELSE
        SELECT 'ERROR' as result, 'Không tìm thấy ca làm đang diễn ra' as message;
    END IF;
END //
DELIMITER ;

-- Procedure bắt đầu ca làm việc
DELIMITER //
CREATE OR REPLACE PROCEDURE `StartWorkShift`(
    IN p_employee_id INT,
    IN p_shift_date DATE
)
BEGIN
    DECLARE v_shift_count INT DEFAULT 0;
    
    -- Kiểm tra xem có ca làm được lên lịch cho ngày này không
    SELECT COUNT(*) INTO v_shift_count
    FROM workshifts 
    WHERE employee_id = p_employee_id 
      AND shift_date = p_shift_date 
      AND status = 'SCHEDULED';
    
    IF v_shift_count > 0 THEN
        -- Cập nhật trạng thái ca làm
        UPDATE workshifts 
        SET 
            status = 'IN_PROGRESS',
            start_time = CURRENT_TIME,
            updated_at = NOW()
        WHERE employee_id = p_employee_id 
          AND shift_date = p_shift_date 
          AND status = 'SCHEDULED'
        LIMIT 1;
        
        -- Tạo working session
        INSERT INTO workingsession (employee_id, login_time, date, work_status)
        VALUES (p_employee_id, NOW(), p_shift_date, 'IN_PROGRESS');
        
        SELECT 'SUCCESS' as result, 'Đã bắt đầu ca làm việc' as message;
    ELSE
        SELECT 'ERROR' as result, 'Không có ca làm được lên lịch cho hôm nay' as message;
    END IF;
END //
DELIMITER ;

-- Procedure tạo ca làm hàng loạt
DELIMITER //
CREATE OR REPLACE PROCEDURE `CreateBulkShifts`(
    IN p_start_date DATE,
    IN p_end_date DATE,
    IN p_employee_list TEXT -- Format: "1,2,3,4,5"
)
BEGIN
    DECLARE v_employee_id INT;
    DECLARE v_current_date DATE;
    DECLARE v_pos INT DEFAULT 1;
    DECLARE v_employee_ids TEXT DEFAULT p_employee_list;
    
    -- Loop qua từng ngày
    SET v_current_date = p_start_date;
    WHILE v_current_date <= p_end_date DO
        -- Chỉ tạo ca cho các ngày trong tuần (thứ 2-7)
        IF WEEKDAY(v_current_date) < 6 THEN
            -- Reset employee list cho mỗi ngày
            SET v_employee_ids = p_employee_list;
            SET v_pos = 1;
            
            -- Loop qua từng employee ID
            WHILE LENGTH(v_employee_ids) > 0 DO
                -- Lấy employee ID đầu tiên
                IF LOCATE(',', v_employee_ids) > 0 THEN
                    SET v_employee_id = CAST(SUBSTRING(v_employee_ids, 1, LOCATE(',', v_employee_ids) - 1) AS UNSIGNED);
                    SET v_employee_ids = SUBSTRING(v_employee_ids, LOCATE(',', v_employee_ids) + 1);
                ELSE
                    SET v_employee_id = CAST(v_employee_ids AS UNSIGNED);
                    SET v_employee_ids = '';
                END IF;
                
                -- Tạo ca làm (mặc định là ca FULL)
                INSERT INTO workshifts (employee_id, shift_date, shift_type, start_time, planned_hours, status)
                VALUES (v_employee_id, v_current_date, 'FULL', '08:00:00', 8.00, 'SCHEDULED')
                ON DUPLICATE KEY UPDATE shift_id = shift_id; -- Ignore if exists
                
            END WHILE;
        END IF;
        
        -- Chuyển sang ngày tiếp theo
        SET v_current_date = DATE_ADD(v_current_date, INTERVAL 1 DAY);
    END WHILE;
    
    SELECT 'SUCCESS' as result, CONCAT('Đã tạo ca làm từ ', p_start_date, ' đến ', p_end_date) as message;
END //
DELIMITER ;

-- ================================================
-- 3. FUNCTIONS
-- ================================================

-- Function tính lương theo ca làm
DELIMITER //
CREATE OR REPLACE FUNCTION `CalculateShiftEarnings`(
    p_employee_id INT,
    p_start_date DATE,
    p_end_date DATE
) RETURNS DECIMAL(10,2)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE total_earnings DECIMAL(10,2) DEFAULT 0;
    DECLARE v_hourly_wage INT DEFAULT 18000;
    
    -- Lấy mức lương theo giờ
    SELECT hourly_wage INTO v_hourly_wage
    FROM salary 
    WHERE employee_id = p_employee_id 
    LIMIT 1;
    
    -- Tính tổng lương
    SELECT 
        SUM(
            (COALESCE(ws.actual_hours, ws.planned_hours) * v_hourly_wage) +
            (COALESCE(ws.overtime_hours, 0) * v_hourly_wage * 1.5)
        )
    INTO total_earnings
    FROM workshifts ws
    WHERE ws.employee_id = p_employee_id
      AND ws.shift_date BETWEEN p_start_date AND p_end_date
      AND ws.status IN ('COMPLETED', 'IN_PROGRESS');
    
    RETURN COALESCE(total_earnings, 0);
END //
DELIMITER ;

-- Function tính tỷ lệ chuyên cần
DELIMITER //
CREATE OR REPLACE FUNCTION `CalculateAttendanceRate`(
    p_employee_id INT,
    p_month INT,
    p_year INT
) RETURNS DECIMAL(5,2)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE total_shifts INT DEFAULT 0;
    DECLARE completed_shifts INT DEFAULT 0;
    DECLARE attendance_rate DECIMAL(5,2) DEFAULT 0;
    
    -- Đếm tổng số ca và ca hoàn thành
    SELECT 
        COUNT(*),
        SUM(CASE WHEN status = 'COMPLETED' THEN 1 ELSE 0 END)
    INTO total_shifts, completed_shifts
    FROM workshifts
    WHERE employee_id = p_employee_id
      AND MONTH(shift_date) = p_month
      AND YEAR(shift_date) = p_year;
    
    -- Tính tỷ lệ
    IF total_shifts > 0 THEN
        SET attendance_rate = (completed_shifts * 100.0) / total_shifts;
    END IF;
    
    RETURN attendance_rate;
END //
DELIMITER ;

-- Function kiểm tra ca làm trùng lặp
DELIMITER //
CREATE OR REPLACE FUNCTION `CheckShiftConflict`(
    p_employee_id INT,
    p_shift_date DATE,
    p_start_time TIME,
    p_end_time TIME,
    p_exclude_shift_id INT
) RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE conflict_count INT DEFAULT 0;
    
    SELECT COUNT(*) INTO conflict_count
    FROM workshifts
    WHERE employee_id = p_employee_id
      AND shift_date = p_shift_date
      AND shift_id != COALESCE(p_exclude_shift_id, 0)
      AND status != 'ABSENT'
      AND (
          (start_time <= p_start_time AND COALESCE(end_time, '23:59:59') > p_start_time) OR
          (start_time < p_end_time AND COALESCE(end_time, '23:59:59') >= p_end_time) OR
          (start_time >= p_start_time AND COALESCE(end_time, '23:59:59') <= p_end_time)
      );
    
    RETURN conflict_count > 0;
END //
DELIMITER ;

-- ================================================
-- 4. TRIGGERS
-- ================================================

-- Trigger cập nhật stock khi có đơn hàng
DELIMITER //
CREATE OR REPLACE TRIGGER `update_stock_after_order`
AFTER INSERT ON `orderdetails`
FOR EACH ROW
BEGIN
    UPDATE products 
    SET stock_quantity = stock_quantity - NEW.quantity
    WHERE product_id = NEW.product_id;
END //
DELIMITER ;

-- Trigger cập nhật stock khi nhập hàng
DELIMITER //
CREATE OR REPLACE TRIGGER `update_stock_after_import`
AFTER INSERT ON `imports`
FOR EACH ROW
BEGIN
    UPDATE products 
    SET stock_quantity = stock_quantity + NEW.quantity
    WHERE product_id = NEW.product_id;
END //
DELIMITER ;

-- Trigger tự động tính điểm thưởng cho khách hàng
DELIMITER //
CREATE OR REPLACE TRIGGER `calculate_customer_points`
AFTER INSERT ON `orders`
FOR EACH ROW
BEGIN
    DECLARE points_earned INT DEFAULT 0;
    
    -- Tính điểm: 1 điểm cho mỗi 1000 VND
    SET points_earned = FLOOR(NEW.final_amount / 1000);
    
    -- Cập nhật điểm cho khách hàng
    UPDATE customers 
    SET points = points + points_earned
    WHERE customer_id = NEW.customer_id;
END //
DELIMITER ;

-- ================================================
-- 5. INDEXES FOR PERFORMANCE
-- ================================================

-- Indexes cho bảng orders
CREATE INDEX IF NOT EXISTS `idx_orders_date` ON `orders` (`order_date`);
CREATE INDEX IF NOT EXISTS `idx_orders_employee_date` ON `orders` (`employee_id`, `order_date`);

-- Indexes cho bảng orderdetails
CREATE INDEX IF NOT EXISTS `idx_orderdetails_product` ON `orderdetails` (`product_id`);

-- Indexes cho bảng workshifts
CREATE INDEX IF NOT EXISTS `idx_workshifts_date_status` ON `workshifts` (`shift_date`, `status`);
CREATE INDEX IF NOT EXISTS `idx_workshifts_employee_month` ON `workshifts` (`employee_id`, `shift_date`);

-- Indexes cho bảng products
CREATE INDEX IF NOT EXISTS `idx_products_category_active` ON `products` (`category_id`, `is_active`);
CREATE INDEX IF NOT EXISTS `idx_products_stock` ON `products` (`stock_quantity`);

-- ================================================
-- SAMPLE QUERIES FOR TESTING
-- ================================================

-- Test chốt ca
-- CALL CloseWorkShift(1, '2025-06-25', '17:30:00', 'Hoàn thành ca làm');

-- Test tính lương
-- SELECT CalculateShiftEarnings(1, '2025-06-01', '2025-06-30') as monthly_earnings;

-- Test tỷ lệ chuyên cần
-- SELECT CalculateAttendanceRate(1, 6, 2025) as attendance_rate;

-- Test view thống kê
-- SELECT * FROM v_employee_shift_summary WHERE employee_id = 1;
-- SELECT * FROM v_product_sales_summary LIMIT 10;
-- SELECT * FROM v_inventory_status WHERE stock_status = 'Sắp hết'; 