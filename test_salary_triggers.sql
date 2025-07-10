-- File test để kiểm tra trigger tính toán salary từ workingsession
-- Chạy file này sau khi đã cập nhật database

-- 1. Kiểm tra cấu trúc bảng salary
DESCRIBE salary;

-- 2. Kiểm tra dữ liệu hiện tại
SELECT 
    s.salary_id,
    s.employee_id,
    e.full_name,
    s.total_hours,
    s.hourly_wage,
    s.bonus,
    s.penalty_deduction,
    s.gross_salary,
    s.net_salary
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
ORDER BY s.salary_id;

-- 3. Kiểm tra dữ liệu workingsession hiện tại
SELECT 
    ws.working_session_id,
    ws.employee_id,
    e.full_name,
    ws.login_time,
    ws.logout_time,
    ws.working_hours,
    ws.date,
    ws.work_status
FROM workingsession ws
JOIN employees e ON ws.employee_id = e.employee_id
ORDER BY ws.working_session_id;

-- 4. Test: Thêm ca làm việc hoàn thành cho employee_id = 2
INSERT INTO workingsession (employee_id, login_time, logout_time, working_hours, date, work_status) 
VALUES (2, '2025-06-25 08:00:00', '2025-06-25 17:00:00', 8.5, '2025-06-25', 'COMPLETED');

-- 5. Kiểm tra kết quả sau khi thêm ca làm việc
SELECT 
    s.salary_id,
    s.employee_id,
    e.full_name,
    s.total_hours,
    s.hourly_wage,
    s.bonus,
    s.penalty_deduction,
    s.gross_salary,
    s.net_salary,
    (SELECT COALESCE(SUM(ws.working_hours), 0) 
     FROM workingsession ws 
     WHERE ws.employee_id = s.employee_id 
     AND ws.work_status = 'COMPLETED') as actual_working_hours
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
WHERE s.employee_id = 2;

-- 6. Test: Thêm ca làm việc thứ 2 cho employee_id = 2
INSERT INTO workingsession (employee_id, login_time, logout_time, working_hours, date, work_status) 
VALUES (2, '2025-06-26 08:00:00', '2025-06-26 18:00:00', 10.0, '2025-06-26', 'COMPLETED');

-- 7. Kiểm tra kết quả sau khi thêm ca làm việc thứ 2
SELECT 
    s.salary_id,
    s.employee_id,
    e.full_name,
    s.total_hours,
    s.hourly_wage,
    s.bonus,
    s.penalty_deduction,
    s.gross_salary,
    s.net_salary,
    (SELECT COALESCE(SUM(ws.working_hours), 0) 
     FROM workingsession ws 
     WHERE ws.employee_id = s.employee_id 
     AND ws.work_status = 'COMPLETED') as actual_working_hours
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
WHERE s.employee_id = 2;

-- 8. Test: Cập nhật ca làm việc từ IN_PROGRESS thành COMPLETED
-- Đầu tiên thêm một ca làm việc với status IN_PROGRESS
INSERT INTO workingsession (employee_id, login_time, logout_time, working_hours, date, work_status) 
VALUES (4, '2025-06-25 09:00:00', NULL, 0, '2025-06-25', 'IN_PROGRESS');

-- Sau đó cập nhật thành COMPLETED
UPDATE workingsession 
SET logout_time = '2025-06-25 17:30:00', 
    working_hours = 8.5, 
    work_status = 'COMPLETED' 
WHERE employee_id = 4 AND work_status = 'IN_PROGRESS';

-- 9. Kiểm tra kết quả sau khi cập nhật
SELECT 
    s.salary_id,
    s.employee_id,
    e.full_name,
    s.total_hours,
    s.hourly_wage,
    s.bonus,
    s.penalty_deduction,
    s.gross_salary,
    s.net_salary,
    (SELECT COALESCE(SUM(ws.working_hours), 0) 
     FROM workingsession ws 
     WHERE ws.employee_id = s.employee_id 
     AND ws.work_status = 'COMPLETED') as actual_working_hours
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
WHERE s.employee_id = 4;

-- 10. Test: Xóa ca làm việc và kiểm tra
-- Lưu ý: Chỉ xóa ca làm việc test, không xóa dữ liệu thật
DELETE FROM workingsession WHERE working_session_id > 10;

-- 11. Kiểm tra kết quả sau khi xóa
SELECT 
    s.salary_id,
    s.employee_id,
    e.full_name,
    s.total_hours,
    s.hourly_wage,
    s.bonus,
    s.penalty_deduction,
    s.gross_salary,
    s.net_salary,
    (SELECT COALESCE(SUM(ws.working_hours), 0) 
     FROM workingsession ws 
     WHERE ws.employee_id = s.employee_id 
     AND ws.work_status = 'COMPLETED') as actual_working_hours
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
ORDER BY s.salary_id;

-- 12. Hiển thị tất cả trigger hiện tại
SHOW TRIGGERS; 