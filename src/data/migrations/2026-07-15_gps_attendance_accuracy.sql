-- Preserve browser-reported GPS accuracy (meters) for attendance audit.
-- Idempotent for MySQL 8+; does not modify existing rows.

SET @db_name := DATABASE();

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckInAccuracy'),
    'ALTER TABLE Attendance ADD COLUMN CheckInAccuracy DECIMAL(10,2) NULL AFTER CheckInDistanceMeters',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckOutAccuracy'),
    'ALTER TABLE Attendance ADD COLUMN CheckOutAccuracy DECIMAL(10,2) NULL AFTER CheckOutDistanceMeters',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
