-- Trigger để tự động tính working_hours khi INSERT hoặc UPDATE workingsession
-- Chạy file này để thêm trigger tính giờ làm tự động

-- Xóa trigger cũ nếu có
DROP TRIGGER IF EXISTS `calculate_working_hours_insert`;
DROP TRIGGER IF EXISTS `calculate_working_hours_update`;

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

DELIMITER ;

-- Cập nhật dữ liệu hiện tại
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

-- Kiểm tra kết quả
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