-- Payroll module improvements (run once on existing databases)
-- BẮT BUỘC chạy trước: 2026-07-07_payroll_deduplicate.sql

-- 1. Prevent duplicate payroll per employee/period (required for sp_GeneratePayrollImproved upsert)
ALTER TABLE Payroll
    ADD UNIQUE KEY uk_payroll_employee_period (EmployeeID, PayPeriod);

-- 2. Grant payroll permissions to HR Manager and HR Staff
INSERT IGNORE INTO RolePermission (RoleID, PermissionID)
SELECT 2, PermissionID FROM Permission WHERE PermissionCode = 'APPROVE_PAYROLL';

INSERT IGNORE INTO RolePermission (RoleID, PermissionID)
SELECT 4, PermissionID FROM Permission
WHERE PermissionCode IN ('VIEW_PAYROLLS', 'CREATE_PAYROLL', 'EDIT_PAYROLL');
