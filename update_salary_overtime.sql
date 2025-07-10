-- Thêm cột overtime_pay vào bảng salary
ALTER TABLE `salary` 
ADD COLUMN `overtime_pay` DECIMAL(12,2) DEFAULT 0 AFTER `net_salary`;

-- Cập nhật dữ liệu hiện tại
UPDATE salary SET overtime_pay = 0 WHERE overtime_pay IS NULL;

-- Kiểm tra kết quả
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
    s.overtime_pay
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
ORDER BY s.salary_id; 