-- Department task assignment flow without creating new tables.
-- Safe to run again: column additions are guarded by information_schema checks.

ALTER TABLE Task
    MODIFY COLUMN Status ENUM('Waiting','In Progress','Submitted','Approved','Rejected','Overdue','Cancelled') DEFAULT 'Waiting';

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE Task ADD COLUMN Priority ENUM(''Low'',''Normal'',''High'') NOT NULL DEFAULT ''Normal'' AFTER Status',
        'SELECT ''Task.Priority already exists'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'Task'
      AND COLUMN_NAME = 'Priority'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE Task ADD COLUMN AttachmentPath VARCHAR(500) NULL AFTER Priority',
        'SELECT ''Task.AttachmentPath already exists'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'Task'
      AND COLUMN_NAME = 'AttachmentPath'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE assignList ADD COLUMN Status ENUM(''Waiting'',''In Progress'',''Submitted'',''Approved'',''Rejected'') NOT NULL DEFAULT ''Waiting'' AFTER EmpId',
        'SELECT ''assignList.Status already exists'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'assignList'
      AND COLUMN_NAME = 'Status'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE assignList ADD COLUMN SubmittedAt DATETIME NULL AFTER Status',
        'SELECT ''assignList.SubmittedAt already exists'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'assignList'
      AND COLUMN_NAME = 'SubmittedAt'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE assignList ADD COLUMN ApprovedAt DATETIME NULL AFTER SubmittedAt',
        'SELECT ''assignList.ApprovedAt already exists'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'assignList'
      AND COLUMN_NAME = 'ApprovedAt'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @sql = (
    SELECT IF(
        COUNT(*) = 0,
        'ALTER TABLE assignList ADD COLUMN Feedback TEXT NULL AFTER ApprovedAt',
        'SELECT ''assignList.Feedback already exists'' AS message'
    )
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'assignList'
      AND COLUMN_NAME = 'Feedback'
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

UPDATE assignList al
JOIN Task t ON t.TaskID = al.TaskId
SET al.Status = CASE
        WHEN t.Status = 'Approved' THEN 'Approved'
        WHEN t.Status = 'Rejected' THEN 'Rejected'
        WHEN t.Status = 'Submitted' THEN 'Submitted'
        ELSE al.Status
    END,
    al.SubmittedAt = CASE
        WHEN t.Status IN ('Submitted','Approved') AND al.SubmittedAt IS NULL THEN NOW()
        ELSE al.SubmittedAt
    END,
    al.ApprovedAt = CASE
        WHEN t.Status = 'Approved' AND al.ApprovedAt IS NULL THEN NOW()
        ELSE al.ApprovedAt
    END
WHERE al.Status = 'Waiting'
  AND t.Status IN ('Submitted','Approved','Rejected');

SET @notification_table = (
    SELECT TABLE_NAME
    FROM information_schema.TABLES
    WHERE TABLE_SCHEMA = DATABASE()
      AND LOWER(TABLE_NAME) = 'notification'
    LIMIT 1
);

SET @sql = IF(
    @notification_table IS NULL,
    'SELECT ''Notification table does not exist; skipped Type enum update'' AS message',
    CONCAT(
        'ALTER TABLE `', @notification_table,
        '` MODIFY COLUMN Type ENUM(''Application'',''Interview'',''Offer'',''Task'',''System'') DEFAULT ''System'''
    )
);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
