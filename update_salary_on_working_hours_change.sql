-- Trigger để cập nhật salary khi working_hours thay đổi
-- Chạy file này sau khi chạy calculate_working_hours_triggers.sql

-- Xóa trigger cũ nếu có
DROP TRIGGER IF EXISTS `update_salary_on_working_hours_change`;

DELIMITER $$

-- Trigger để cập nhật salary khi working_hours thay đổi
CREATE TRIGGER `update_salary_on_working_hours_change` 
AFTER UPDATE ON `workingsession`
FOR EACH ROW
BEGIN
    DECLARE total_working_hours DECIMAL(10,2) DEFAULT 0;
    DECLARE current_hourly_wage INT DEFAULT 0;
    DECLARE current_bonus INT DEFAULT 0;
    DECLARE current_penalty INT DEFAULT 0;
    DECLARE current_overtime DECIMAL(12,2) DEFAULT 0;
    
    -- Chỉ cập nhật khi working_hours thay đổi và work_status là COMPLETED
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

DELIMITER ;

-- Test trigger bằng cách cập nhật một record
-- UPDATE workingsession 
-- SET logout_time = NOW() 
-- WHERE working_session_id = 1;

-- Kiểm tra kết quả
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