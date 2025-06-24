-- Enhanced WorkShift Schema với Penalty System
-- Cập nhật bảng workshifts để hỗ trợ penalty và adjustment

ALTER TABLE workshifts 
ADD COLUMN check_in_time TIME NULL COMMENT 'Thời gian thực tế bắt đầu ca',
ADD COLUMN check_out_time TIME NULL COMMENT 'Thời gian thực tế kết thúc ca',
ADD COLUMN late_minutes INT DEFAULT 0 COMMENT 'Số phút đến muộn',
ADD COLUMN early_leave_minutes INT DEFAULT 0 COMMENT 'Số phút về sớm',
ADD COLUMN is_scheduled BOOLEAN DEFAULT TRUE COMMENT 'Có trong lịch làm không',
ADD COLUMN penalty_amount DECIMAL(10,2) DEFAULT 0 COMMENT 'Số tiền phạt',
ADD COLUMN penalty_reason VARCHAR(500) NULL COMMENT 'Lý do phạt',
ADD COLUMN salary_adjustment_percent DECIMAL(5,2) DEFAULT 100.00 COMMENT 'Phần trăm lương (100% = bình thường)',
ADD COLUMN adjustment_reason VARCHAR(500) NULL COMMENT 'Lý do điều chỉnh lương',
ADD COLUMN auto_checkout_penalty BOOLEAN DEFAULT FALSE COMMENT 'Có bị phạt tự động chốt ca không';

-- Thêm indexes để tối ưu query
CREATE INDEX idx_workshifts_employee_date ON workshifts(employee_id, shift_date);
CREATE INDEX idx_workshifts_status ON workshifts(status);
CREATE INDEX idx_workshifts_check_times ON workshifts(check_in_time, check_out_time);

-- Tạo bảng penalty_rules để quản lý quy tắc phạt
CREATE TABLE IF NOT EXISTS penalty_rules (
    rule_id INT AUTO_INCREMENT PRIMARY KEY,
    rule_type ENUM('NO_CHECKIN', 'LATE_ARRIVAL', 'UNSCHEDULED_WORK', 'NO_CHECKOUT') NOT NULL,
    penalty_amount DECIMAL(10,2) DEFAULT 0,
    penalty_percent DECIMAL(5,2) DEFAULT 0,
    description VARCHAR(500),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert các quy tắc phạt
INSERT INTO penalty_rules (rule_type, penalty_amount, penalty_percent, description) VALUES
('NO_CHECKIN', 200000, 0, 'Phạt không bắt đầu ca khi có lịch làm'),
('LATE_ARRIVAL', 0, 25, 'Giảm 25% lương mỗi giờ đến muộn'),
('UNSCHEDULED_WORK', 0, 75, 'Chỉ tính 75% lương khi làm ngoài lịch'),
('NO_CHECKOUT', 0, 50, 'Trừ 50% lương khi không chốt ca đúng giờ');

-- View để hiển thị thông tin ca làm với penalty
CREATE OR REPLACE VIEW v_workshift_details AS
SELECT 
    ws.*,
    e.full_name,
    e.employee_code,
    s.hourly_wage,
    s.base_salary,
    -- Tính lương cơ bản
    (COALESCE(ws.actual_hours, ws.planned_hours) * s.hourly_wage) as base_earnings,
    -- Tính lương sau điều chỉnh
    (COALESCE(ws.actual_hours, ws.planned_hours) * s.hourly_wage * ws.salary_adjustment_percent / 100) as adjusted_earnings,
    -- Tính lương cuối cùng (sau trừ phạt)
    ((COALESCE(ws.actual_hours, ws.planned_hours) * s.hourly_wage * ws.salary_adjustment_percent / 100) - ws.penalty_amount) as final_earnings,
    -- Overtime earnings
    (ws.overtime_hours * s.hourly_wage * 1.5) as overtime_earnings,
    CASE 
        WHEN ws.late_minutes > 0 THEN CONCAT('Muộn ', ws.late_minutes, ' phút')
        WHEN ws.early_leave_minutes > 0 THEN CONCAT('Về sớm ', ws.early_leave_minutes, ' phút')
        ELSE 'Đúng giờ'
    END as attendance_status
FROM workshifts ws
JOIN employees e ON ws.employee_id = e.employee_id
LEFT JOIN salary s ON ws.employee_id = s.employee_id;

-- Sample data cho test
-- Tạo ca làm theo lịch (scheduled)
INSERT INTO workshifts (
    employee_id, shift_date, shift_type, start_time, planned_hours, 
    break_minutes, status, is_scheduled, created_at, updated_at
) VALUES (
    2, CURRENT_DATE, 'SANG', '08:00:00', 8.0, 
    60, 'SCHEDULED', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Tạo ca làm ngoài lịch (unscheduled) - để test trường hợp 75% lương
INSERT INTO workshifts (
    employee_id, shift_date, shift_type, start_time, planned_hours, 
    break_minutes, status, is_scheduled, salary_adjustment_percent, adjustment_reason,
    created_at, updated_at
) VALUES (
    3, CURRENT_DATE, 'CHIEU', '14:00:00', 6.0, 
    30, 'SCHEDULED', FALSE, 75.00, 'Làm ngoài lịch - chỉ tính 75% lương',
    CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
); 