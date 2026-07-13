-- Ensure payroll-generated deductions update one row per employee/type/month.
-- Required because sp_GeneratePayrollImproved uses ON DUPLICATE KEY UPDATE.

SET @db_name := DATABASE();

DELETE ed FROM EmployeeDeduction ed
JOIN (
    SELECT ID
    FROM (
        SELECT
            ID,
            ROW_NUMBER() OVER (
                PARTITION BY EmployeeID, DeductionTypeID, Month
                ORDER BY ID DESC
            ) AS rn
        FROM EmployeeDeduction
    ) ranked
    WHERE rn > 1
) duplicates ON duplicates.ID = ed.ID;

SET @sql := IF(
    NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = @db_name
          AND TABLE_NAME = 'EmployeeDeduction'
          AND INDEX_NAME = 'uk_employee_deduction_employee_type_month'
    ),
    'ALTER TABLE EmployeeDeduction ADD UNIQUE KEY uk_employee_deduction_employee_type_month (EmployeeID, DeductionTypeID, Month)',
    'SELECT 1'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
