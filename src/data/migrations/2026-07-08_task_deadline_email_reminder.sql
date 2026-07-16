-- Add one lightweight marker so each assignment receives the 12-hour deadline email once.
SET @column_exists := (
    SELECT COUNT(*)
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'assignList'
      AND COLUMN_NAME = 'DeadlineReminderSentAt'
);

SET @sql := IF(
    @column_exists = 0,
    'ALTER TABLE assignList ADD COLUMN DeadlineReminderSentAt DATETIME NULL AFTER Feedback',
    'SELECT ''assignList.DeadlineReminderSentAt already exists'' AS message'
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
