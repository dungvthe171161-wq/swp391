-- =============================================================================
-- Dọn bản ghi trùng Payroll / PayrollAudit theo (EmployeeID, PayPeriod)
-- Chạy TRƯỚC migration 2026-07-06_payroll_improvements.sql (thêm UNIQUE KEY)
-- MySQL 8.0+ (dùng ROW_NUMBER)
-- =============================================================================

-- Bước 0: Xem trước các nhóm trùng (chạy riêng nếu cần kiểm tra)
-- SELECT EmployeeID, PayPeriod, COUNT(*) AS cnt,
--        GROUP_CONCAT(PayrollID ORDER BY PayrollID) AS payroll_ids,
--        GROUP_CONCAT(Status ORDER BY PayrollID) AS statuses
-- FROM Payroll
-- GROUP BY EmployeeID, PayPeriod
-- HAVING COUNT(*) > 1;

START TRANSACTION;

-- -----------------------------------------------------------------------------
-- 1. Payroll: giữ 1 bản / (EmployeeID, PayPeriod)
--    Ưu tiên: Paid > Approved > Pending > Rejected > Draft, rồi PayrollID lớn nhất
-- -----------------------------------------------------------------------------
DELETE FROM Payroll
WHERE PayrollID IN (
    SELECT PayrollID FROM (
        SELECT PayrollID,
            ROW_NUMBER() OVER (
                PARTITION BY EmployeeID, PayPeriod
                ORDER BY
                    FIELD(Status, 'Paid', 'Approved', 'Pending', 'Rejected', 'Draft') DESC,
                    PayrollID DESC
            ) AS rn
        FROM Payroll
    ) ranked
    WHERE rn > 1
);

-- -----------------------------------------------------------------------------
-- 2. PayrollAudit (phòng trường hợp DB cũ chưa có UNIQUE hoặc đã bị bỏ constraint)
-- -----------------------------------------------------------------------------
DELETE FROM PayrollAudit
WHERE AuditID IN (
    SELECT AuditID FROM (
        SELECT AuditID,
            ROW_NUMBER() OVER (
                PARTITION BY EmployeeID, PayPeriod
                ORDER BY
                    FIELD(Status, 'Paid', 'Approved', 'Pending', 'Rejected', 'Draft') DESC,
                    AuditID DESC
            ) AS rn
        FROM PayrollAudit
    ) ranked
    WHERE rn > 1
);

COMMIT;

-- Bước 3: Xác nhận không còn trùng (kết quả phải rỗng)
-- SELECT 'Payroll' AS tbl, EmployeeID, PayPeriod, COUNT(*) AS cnt
-- FROM Payroll GROUP BY EmployeeID, PayPeriod HAVING COUNT(*) > 1
-- UNION ALL
-- SELECT 'PayrollAudit', EmployeeID, PayPeriod, COUNT(*)
-- FROM PayrollAudit GROUP BY EmployeeID, PayPeriod HAVING COUNT(*) > 1;
