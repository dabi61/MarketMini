-- File SQL hoàn chỉnh để thêm trigger tính giờ làm
-- Chạy file này để cập nhật toàn bộ hệ thống trigger

-- 1. Xóa các trigger cũ nếu có
DROP TRIGGER IF EXISTS `calculate_working_hours_insert`;
DROP TRIGGER IF EXISTS `calculate_working_hours_update`;
DROP TRIGGER IF EXISTS `update_salary_on_working_hours_change`;
DROP TRIGGER IF EXISTS `update_salary_on_workingsession_insert`;
DROP TRIGGER IF EXISTS `update_salary_on_workingsession_update`;
DROP TRIGGER IF EXISTS `update_salary_on_workingsession_delete`;

-- 2. Tạo trigger tính working_hours tự động
DELIMITER $$

-- Trigger để tính working_hours khi INSERT
CREATE TRIGGER `calculate_working_hours_insert` 
BEFORE INSERT ON `workingsession`
FOR EACH ROW
BEGIN
    -- Tính working_hours nếu có cả login_time và logout_time
    IF NEW.login_time IS NOT NULL AND NEW.logout_time IS NOT NULL THEN
        -- Tính số giờ làm việc (chênh lệch giữa logout_time và login_time)
        SET NEW.working_hours = TIMESTAMPDIFF(MINUTE, NEW.login_time, NEW.logout_time) / 60.0;
        
        -- Nếu working_hours âm (logout trước login), đặt về 0
        IF NEW.working_hours < 0 THEN
            SET NEW.working_hours = 0;
        END IF;
        
        -- Cập nhật date nếu chưa có
        IF NEW.date IS NULL THEN
            SET NEW.date = DATE(NEW.login_time);
        END IF;
        
        -- Cập nhật work_status thành COMPLETED nếu chưa có
        IF NEW.work_status IS NULL THEN
            SET NEW.work_status = 'COMPLETED';
        END IF;
    ELSE
        -- Nếu chưa có logout_time, đặt working_hours = 0 và work_status = 'ACTIVE'
        SET NEW.working_hours = 0;
        IF NEW.work_status IS NULL THEN
            SET NEW.work_status = 'ACTIVE';
        END IF;
        
        -- Cập nhật date nếu chưa có
        IF NEW.date IS NULL AND NEW.login_time IS NOT NULL THEN
            SET NEW.date = DATE(NEW.login_time);
        END IF;
    END IF;
END$$

-- Trigger để tính working_hours khi UPDATE
CREATE TRIGGER `calculate_working_hours_update` 
BEFORE UPDATE ON `workingsession`
FOR EACH ROW
BEGIN
    -- Tính working_hours nếu có cả login_time và logout_time
    IF NEW.login_time IS NOT NULL AND NEW.logout_time IS NOT NULL THEN
        -- Tính số giờ làm việc (chênh lệch giữa logout_time và login_time)
        SET NEW.working_hours = TIMESTAMPDIFF(MINUTE, NEW.login_time, NEW.logout_time) / 60.0;
        
        -- Nếu working_hours âm (logout trước login), đặt về 0
        IF NEW.working_hours < 0 THEN
            SET NEW.working_hours = 0;
        END IF;
        
        -- Cập nhật date nếu chưa có
        IF NEW.date IS NULL THEN
            SET NEW.date = DATE(NEW.login_time);
        END IF;
        
        -- Cập nhật work_status thành COMPLETED
        SET NEW.work_status = 'COMPLETED';
    ELSE
        -- Nếu chưa có logout_time, đặt working_hours = 0 và work_status = 'ACTIVE'
        SET NEW.working_hours = 0;
        IF NEW.work_status IS NULL OR NEW.work_status = 'COMPLETED' THEN
            SET NEW.work_status = 'ACTIVE';
        END IF;
        
        -- Cập nhật date nếu chưa có
        IF NEW.date IS NULL AND NEW.login_time IS NOT NULL THEN
            SET NEW.date = DATE(NEW.login_time);
        END IF;
    END IF;
END$$

-- Trigger để cập nhật salary khi INSERT vào workingsession
CREATE TRIGGER `update_salary_on_workingsession_insert` 
AFTER INSERT ON `workingsession`
FOR EACH ROW
BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    DECLARE current_hourly_wage INT DEFAULT 0;
    DECLARE current_bonus INT DEFAULT 0;
    DECLARE current_penalty INT DEFAULT 0;
    DECLARE current_overtime DECIMAL(12,2) DEFAULT 0;
    
    -- Chỉ cập nhật khi work_status là COMPLETED
    IF NEW.work_status = 'COMPLETED' THEN
        -- Tính tổng giờ làm từ bảng workingsession
        SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
        FROM workingsession 
        WHERE employee_id = NEW.employee_id 
        AND work_status = 'COMPLETED';
        
        -- Lấy thông tin lương hiện tại
        SELECT hourly_wage, bonus, penalty_deduction, overtime_pay
        INTO current_hourly_wage, current_bonus, current_penalty, current_overtime
        FROM salary 
        WHERE employee_id = NEW.employee_id;
        
        -- Cập nhật salary nếu có record tồn tại
        IF current_hourly_wage IS NOT NULL THEN
            UPDATE salary 
            SET total_hours = total_working_hours,
                gross_salary = (total_working_hours * current_hourly_wage) + current_bonus + COALESCE(current_overtime, 0),
                net_salary = ((total_working_hours * current_hourly_wage) + current_bonus + COALESCE(current_overtime, 0)) - current_penalty
            WHERE employee_id = NEW.employee_id;
        END IF;
    END IF;
END$$

-- Trigger để cập nhật salary khi UPDATE workingsession
CREATE TRIGGER `update_salary_on_workingsession_update` 
AFTER UPDATE ON `workingsession`
FOR EACH ROW
BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    DECLARE current_hourly_wage INT DEFAULT 0;
    DECLARE current_bonus INT DEFAULT 0;
    DECLARE current_penalty INT DEFAULT 0;
    DECLARE current_overtime DECIMAL(12,2) DEFAULT 0;
    
    -- Chỉ cập nhật khi working_hours thay đổi hoặc work_status thay đổi
    IF (NEW.working_hours != OLD.working_hours OR NEW.work_status != OLD.work_status) 
       AND NEW.work_status = 'COMPLETED' THEN
        
        -- Tính tổng giờ làm từ bảng workingsession
        SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
        FROM workingsession 
        WHERE employee_id = NEW.employee_id 
        AND work_status = 'COMPLETED';
        
        -- Lấy thông tin lương hiện tại
        SELECT hourly_wage, bonus, penalty_deduction, overtime_pay
        INTO current_hourly_wage, current_bonus, current_penalty, current_overtime
        FROM salary 
        WHERE employee_id = NEW.employee_id;
        
        -- Cập nhật salary nếu có record tồn tại
        IF current_hourly_wage IS NOT NULL THEN
            UPDATE salary 
            SET total_hours = total_working_hours,
                gross_salary = (total_working_hours * current_hourly_wage) + current_bonus + COALESCE(current_overtime, 0),
                net_salary = ((total_working_hours * current_hourly_wage) + current_bonus + COALESCE(current_overtime, 0)) - current_penalty
            WHERE employee_id = NEW.employee_id;
        END IF;
    END IF;
END$$

-- Trigger để cập nhật salary khi DELETE workingsession
CREATE TRIGGER `update_salary_on_workingsession_delete` 
AFTER DELETE ON `workingsession`
FOR EACH ROW
BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    DECLARE current_hourly_wage INT DEFAULT 0;
    DECLARE current_bonus INT DEFAULT 0;
    DECLARE current_penalty INT DEFAULT 0;
    DECLARE current_overtime DECIMAL(12,2) DEFAULT 0;
    
    -- Chỉ cập nhật khi work_status của record bị xóa là COMPLETED
    IF OLD.work_status = 'COMPLETED' THEN
        -- Tính tổng giờ làm từ bảng workingsession
        SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
        FROM workingsession 
        WHERE employee_id = OLD.employee_id 
        AND work_status = 'COMPLETED';
        
        -- Lấy thông tin lương hiện tại
        SELECT hourly_wage, bonus, penalty_deduction, overtime_pay
        INTO current_hourly_wage, current_bonus, current_penalty, current_overtime
        FROM salary 
        WHERE employee_id = OLD.employee_id;
        
        -- Cập nhật salary nếu có record tồn tại
        IF current_hourly_wage IS NOT NULL THEN
            UPDATE salary 
            SET total_hours = total_working_hours,
                gross_salary = (total_working_hours * current_hourly_wage) + current_bonus + COALESCE(current_overtime, 0),
                net_salary = ((total_working_hours * current_hourly_wage) + current_bonus + COALESCE(current_overtime, 0)) - current_penalty
            WHERE employee_id = OLD.employee_id;
        END IF;
    END IF;
END$$

DELIMITER ;

-- 3. Cập nhật dữ liệu hiện tại
UPDATE workingsession 
SET working_hours = TIMESTAMPDIFF(MINUTE, login_time, logout_time) / 60.0
WHERE login_time IS NOT NULL AND logout_time IS NOT NULL;

-- Đặt working_hours = 0 cho các record chưa có logout_time
UPDATE workingsession 
SET working_hours = 0
WHERE logout_time IS NULL;

-- Cập nhật work_status cho các record chưa có
UPDATE workingsession 
SET work_status = 'COMPLETED'
WHERE login_time IS NOT NULL AND logout_time IS NOT NULL AND work_status IS NULL;

UPDATE workingsession 
SET work_status = 'ACTIVE'
WHERE logout_time IS NULL AND work_status IS NULL;

-- 4. Cập nhật salary với dữ liệu mới
UPDATE salary s
SET total_hours = (
    SELECT COALESCE(SUM(ws.working_hours), 0)
    FROM workingsession ws
    WHERE ws.employee_id = s.employee_id 
    AND ws.work_status = 'COMPLETED'
),
gross_salary = (
    SELECT COALESCE(SUM(ws.working_hours), 0) * s.hourly_wage + s.bonus + COALESCE(s.overtime_pay, 0)
    FROM workingsession ws
    WHERE ws.employee_id = s.employee_id 
    AND ws.work_status = 'COMPLETED'
),
net_salary = (
    SELECT COALESCE(SUM(ws.working_hours), 0) * s.hourly_wage + s.bonus + COALESCE(s.overtime_pay, 0) - s.penalty_deduction
    FROM workingsession ws
    WHERE ws.employee_id = s.employee_id 
    AND ws.work_status = 'COMPLETED'
);

-- 5. Kiểm tra kết quả
SELECT 
    '=== WORKINGSESSION DATA ===' as info;
    
SELECT 
    working_session_id,
    employee_id,
    login_time,
    logout_time,
    working_hours,
    date,
    work_status
FROM workingsession
ORDER BY working_session_id;

SELECT 
    '=== SALARY DATA ===' as info;
    
SELECT 
    s.salary_id,
    s.employee_id,
    e.full_name,
    s.total_hours,
    s.hourly_wage,
    s.bonus,
    s.penalty_deduction,
    s.overtime_pay,
    s.gross_salary,
    s.net_salary,
    (SELECT COALESCE(SUM(ws.working_hours), 0) 
     FROM workingsession ws 
     WHERE ws.employee_id = s.employee_id 
     AND ws.work_status = 'COMPLETED') as actual_working_hours
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
ORDER BY s.salary_id; 