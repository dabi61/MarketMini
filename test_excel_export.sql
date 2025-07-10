-- Test query để kiểm tra dữ liệu lương cho xuất Excel
SELECT 
    s.salary_id,
    e.full_name,
    s.total_hours,
    s.hourly_wage,
    s.bonus,
    s.overtime_pay,
    s.gross_salary,
    s.penalty_deduction,
    s.net_salary,
    s.payment_date,
    s.created_date
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
ORDER BY s.created_date DESC;

-- Thêm dữ liệu test cho xuất Excel
INSERT INTO salary (employee_id, total_hours, hourly_wage, bonus, payment_date, created_date, penalty_deduction, overtime_pay, gross_salary, net_salary) 
VALUES 
(10, 160.00, 50000, 200000, '2025-07-15', '2025-07-11', 0, 0.00, 8200000, 8200000),
(2, 140.00, 45000, 150000, '2025-07-15', '2025-07-11', 50000, 0.00, 6450000, 6400000),
(4, 180.00, 55000, 300000, '2025-07-15', '2025-07-11', 0, 0.00, 10200000, 10200000),
(5, 120.00, 40000, 100000, '2025-07-15', '2025-07-11', 25000, 0.00, 4900000, 4875000),
(8, 200.00, 60000, 500000, '2025-07-15', '2025-07-11', 0, 0.00, 12500000, 12500000);

-- Kiểm tra kết quả sau khi thêm
SELECT 
    s.salary_id,
    e.full_name,
    s.total_hours,
    s.hourly_wage,
    s.bonus,
    s.overtime_pay,
    s.gross_salary,
    s.penalty_deduction,
    s.net_salary,
    s.payment_date,
    s.created_date
FROM salary s
JOIN employees e ON s.employee_id = e.employee_id
ORDER BY s.created_date DESC; 