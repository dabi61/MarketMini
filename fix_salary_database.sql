-- Sửa lỗi database cho bảng salary
-- Chạy file này để cập nhật cấu trúc và dữ liệu

-- 1. Thêm cột overtime_pay nếu chưa có
ALTER TABLE `salary` 
ADD COLUMN IF NOT EXISTS `overtime_pay` DECIMAL(12,2) DEFAULT 0 AFTER `net_salary`;

-- 2. Thêm các cột khác nếu chưa có
ALTER TABLE `salary` 
ADD COLUMN IF NOT EXISTS `penalty_deduction` INT(11) DEFAULT 0 AFTER `bonus`,
ADD COLUMN IF NOT EXISTS `gross_salary` DECIMAL(12,2) DEFAULT NULL AFTER `penalty_deduction`,
ADD COLUMN IF NOT EXISTS `net_salary` DECIMAL(12,2) DEFAULT NULL AFTER `gross_salary`;

-- 3. Cập nhật dữ liệu hiện tại
UPDATE salary SET 
    penalty_deduction = COALESCE(penalty_deduction, 0),
    overtime_pay = COALESCE(overtime_pay, 0),
    gross_salary = (total_hours * hourly_wage) + bonus,
    net_salary = ((total_hours * hourly_wage) + bonus) - COALESCE(penalty_deduction, 0)
WHERE penalty_deduction IS NULL OR overtime_pay IS NULL OR gross_salary IS NULL OR net_salary IS NULL;

-- 4. Xóa các trigger cũ nếu có
DROP TRIGGER IF EXISTS `calculate_total_hours`;
DROP TRIGGER IF EXISTS `calculate_total_hours_update`;
DROP TRIGGER IF EXISTS `calculate_salary_amounts`;
DROP TRIGGER IF EXISTS `calculate_salary_amounts_update`;
DROP TRIGGER IF EXISTS `update_salary_on_workingsession_change`;
DROP TRIGGER IF EXISTS `update_salary_on_workingsession_update`;
DROP TRIGGER IF EXISTS `calculate_total_hours_insert`;
DROP TRIGGER IF EXISTS `calculate_total_hours_update`;
DROP TRIGGER IF EXISTS `update_salary_on_workingsession_insert`;
DROP TRIGGER IF EXISTS `update_salary_on_workingsession_update`;
DROP TRIGGER IF EXISTS `update_salary_on_workingsession_delete`;

-- 5. Tạo các trigger mới
DELIMITER $$

-- Trigger khi INSERT vào bảng salary
CREATE TRIGGER `calculate_total_hours_insert` 
BEFORE INSERT ON `salary`
FOR EACH ROW
BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    
    -- Tính tổng giờ làm từ bảng workingsession (chỉ tính các ca đã hoàn thành)
    SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
    FROM workingsession 
    WHERE employee_id = NEW.employee_id 
    AND work_status = 'COMPLETED';
    
    SET NEW.total_hours = total_working_hours;
    
    -- Tính gross_salary = total_hours * hourly_wage + bonus + overtime_pay
    SET NEW.gross_salary = (NEW.total_hours * NEW.hourly_wage) + NEW.bonus + COALESCE(NEW.overtime_pay, 0);
    
    -- Tính net_salary = gross_salary - penalty_deduction
    SET NEW.net_salary = NEW.gross_salary - NEW.penalty_deduction;
END$$

-- Trigger khi UPDATE bảng salary
CREATE TRIGGER `calculate_total_hours_update` 
BEFORE UPDATE ON `salary`
FOR EACH ROW
BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    
    -- Tính tổng giờ làm từ bảng workingsession (chỉ tính các ca đã hoàn thành)
    SELECT COALESCE(SUM(working_hours), 0) INTO total_working_hours
    FROM workingsession 
    WHERE employee_id = NEW.employee_id 
    AND work_status = 'COMPLETED';
    
    SET NEW.total_hours = total_working_hours;
    
    -- Tính gross_salary = total_hours * hourly_wage + bonus + overtime_pay
    SET NEW.gross_salary = (NEW.total_hours * NEW.hourly_wage) + NEW.bonus + COALESCE(NEW.overtime_pay, 0);
    
    -- Tính net_salary = gross_salary - penalty_deduction
    SET NEW.net_salary = NEW.gross_salary - NEW.penalty_deduction;
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
    
    -- Chỉ cập nhật khi work_status thay đổi thành COMPLETED hoặc từ COMPLETED thành khác
    IF (NEW.work_status = 'COMPLETED' AND OLD.work_status != 'COMPLETED') OR 
       (OLD.work_status = 'COMPLETED' AND NEW.work_status != 'COMPLETED') THEN
        
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

-- 6. Kiểm tra kết quả
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