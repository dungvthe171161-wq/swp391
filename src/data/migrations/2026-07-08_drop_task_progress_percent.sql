SET @sql = (
    SELECT IF(
        COUNT(*) > 0,
        'ALTER TABLE assignList DROP COLUMN ProgressPercent',
        'SELECT ''assignList.ProgressPercent does not exist; skipped'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'assignList'
      AND COLUMN_NAME = 'ProgressPercent'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
