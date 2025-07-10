-- Cập nhật cấu trúc bảng salary để thêm các trường tính toán
ALTER TABLE `salary` 
ADD COLUMN `penalty_deduction` INT(11) DEFAULT 0,
ADD COLUMN `gross_salary` DECIMAL(12,2) DEFAULT NULL,
ADD COLUMN `net_salary` DECIMAL(12,2) DEFAULT NULL;

-- Trigger để tính toán total_hours từ bảng workingsession
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
    
    -- Tính gross_salary = total_hours * hourly_wage + bonus
    SET NEW.gross_salary = (NEW.total_hours * NEW.hourly_wage) + NEW.bonus;
    
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
    
    -- Tính gross_salary = total_hours * hourly_wage + bonus
    SET NEW.gross_salary = (NEW.total_hours * NEW.hourly_wage) + NEW.bonus;
    
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
        
        -- Cập nhật salary
        UPDATE salary 
        SET total_hours = total_working_hours,
            gross_salary = (total_working_hours * current_hourly_wage) + current_bonus,
            net_salary = ((total_working_hours * current_hourly_wage) + current_bonus) - current_penalty
        WHERE employee_id = NEW.employee_id;
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
        
        -- Cập nhật salary
        UPDATE salary 
        SET total_hours = total_working_hours,
            gross_salary = (total_working_hours * current_hourly_wage) + current_bonus,
            net_salary = ((total_working_hours * current_hourly_wage) + current_bonus) - current_penalty
        WHERE employee_id = NEW.employee_id;
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
        
        -- Cập nhật salary
        UPDATE salary 
        SET total_hours = total_working_hours,
            gross_salary = (total_working_hours * current_hourly_wage) + current_bonus,
            net_salary = ((total_working_hours * current_hourly_wage) + current_bonus) - current_penalty
        WHERE employee_id = OLD.employee_id;
    END IF;
END$$

DELIMITER ; 