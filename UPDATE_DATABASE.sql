-- Script cập nhật database cho Penalty System
-- Chạy script này trước khi test nút Bắt đầu/Kết thúc ca

USE marketmini; -- Thay tên database của bạn

-- Kiểm tra và thêm các cột mới cho bảng workshifts
ALTER TABLE workshifts 
ADD COLUMN IF NOT EXISTS check_in_time TIME NULL COMMENT 'Thời gian thực tế bắt đầu ca',
ADD COLUMN IF NOT EXISTS check_out_time TIME NULL COMMENT 'Thời gian thực tế kết thúc ca',
ADD COLUMN IF NOT EXISTS late_minutes INT DEFAULT 0 COMMENT 'Số phút đến muộn',
ADD COLUMN IF NOT EXISTS early_leave_minutes INT DEFAULT 0 COMMENT 'Số phút về sớm',
ADD COLUMN IF NOT EXISTS is_scheduled BOOLEAN DEFAULT TRUE COMMENT 'Có trong lịch làm không',
ADD COLUMN IF NOT EXISTS penalty_amount DECIMAL(10,2) DEFAULT 0 COMMENT 'Số tiền phạt',
ADD COLUMN IF NOT EXISTS penalty_reason VARCHAR(500) NULL COMMENT 'Lý do phạt',
ADD COLUMN IF NOT EXISTS salary_adjustment_percent DECIMAL(5,2) DEFAULT 100.00 COMMENT 'Phần trăm lương (100% = bình thường)',
ADD COLUMN IF NOT EXISTS adjustment_reason VARCHAR(500) NULL COMMENT 'Lý do điều chỉnh lương',
ADD COLUMN IF NOT EXISTS auto_checkout_penalty BOOLEAN DEFAULT FALSE COMMENT 'Có bị phạt tự động chốt ca không';

-- Tạo bảng penalty_rules nếu chưa có
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

-- Insert các quy tắc phạt (chỉ insert nếu chưa có)
INSERT IGNORE INTO penalty_rules (rule_type, penalty_amount, penalty_percent, description) VALUES
('NO_CHECKIN', 200000, 0, 'Phạt không bắt đầu ca khi có lịch làm'),
('LATE_ARRIVAL', 0, 25, 'Giảm 25% lương mỗi giờ đến muộn'),
('UNSCHEDULED_WORK', 0, 75, 'Chỉ tính 75% lương khi làm ngoài lịch'),
('NO_CHECKOUT', 0, 50, 'Trừ 50% lương khi không chốt ca đúng giờ');

-- Tạo ca test cho staff (employee_id = 2)
INSERT IGNORE INTO workshifts (
    employee_id, shift_date, shift_type, start_time, planned_hours, 
    break_minutes, status, is_scheduled, created_at, updated_at
) VALUES (
    2, CURRENT_DATE, 'SANG', '08:00:00', 8.0, 
    60, 'SCHEDULED', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
);

-- Thông báo hoàn thành
SELECT 'Database schema updated successfully! Ready for penalty system testing.' as message; 