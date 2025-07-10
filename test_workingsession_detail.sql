-- Test query để kiểm tra dữ liệu workingsession với thông tin chi tiết
SELECT 
    ws.working_session_id,
    ws.employee_id,
    e.full_name as employee_name,
    ws.login_time,
    ws.logout_time,
    ws.working_hours,
    ws.date,
    ws.work_status,
    s.hourly_wage,
    CASE 
        WHEN ws.working_hours IS NOT NULL AND s.hourly_wage IS NOT NULL 
        THEN ws.working_hours * s.hourly_wage 
        ELSE 0 
    END as earnings
FROM workingsession ws
JOIN employees e ON ws.employee_id = e.employee_id
LEFT JOIN salary s ON ws.employee_id = s.employee_id
ORDER BY ws.date DESC, ws.login_time DESC;

-- Test thêm dữ liệu mẫu
INSERT INTO workingsession (employee_id, login_time, logout_time, working_hours, date, work_status) 
VALUES 
(10, '2025-07-11 08:00:00', '2025-07-11 17:00:00', 9.00, '2025-07-11', 'COMPLETED'),
(2, '2025-07-11 08:30:00', '2025-07-11 16:30:00', 8.00, '2025-07-11', 'COMPLETED'),
(4, '2025-07-11 09:00:00', '2025-07-11 18:00:00', 9.00, '2025-07-11', 'COMPLETED'),
(5, '2025-07-11 08:00:00', NULL, 0.00, '2025-07-11', 'IN_PROGRESS'),
(8, '2025-07-12 08:00:00', NULL, 0.00, '2025-07-12', 'SCHEDULED');

-- Kiểm tra kết quả sau khi thêm
SELECT 
    ws.working_session_id,
    ws.employee_id,
    e.full_name as employee_name,
    ws.login_time,
    ws.logout_time,
    ws.working_hours,
    ws.date,
    ws.work_status,
    s.hourly_wage,
    CASE 
        WHEN ws.working_hours IS NOT NULL AND s.hourly_wage IS NOT NULL 
        THEN ws.working_hours * s.hourly_wage 
        ELSE 0 
    END as earnings
FROM workingsession ws
JOIN employees e ON ws.employee_id = e.employee_id
LEFT JOIN salary s ON ws.employee_id = s.employee_id
ORDER BY ws.date DESC, ws.login_time DESC; 