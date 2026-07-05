-- BetterHR Notification System Phase 1
-- Extends the existing Notification table so all roles can share one
-- in-app notification store with entity routing and click targets.

USE hrm_db;

SET @add_actor_user_id = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'Notification'
              AND COLUMN_NAME = 'ActorUserID'
        ),
        'SELECT 1',
        'ALTER TABLE `Notification` ADD COLUMN ActorUserID INT NULL AFTER UserID'
    )
);
PREPARE stmt FROM @add_actor_user_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_entity_type = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'Notification'
              AND COLUMN_NAME = 'EntityType'
        ),
        'SELECT 1',
        'ALTER TABLE `Notification` ADD COLUMN EntityType VARCHAR(50) NULL AFTER ApplicationID'
    )
);
PREPARE stmt FROM @add_entity_type;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_entity_id = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'Notification'
              AND COLUMN_NAME = 'EntityID'
        ),
        'SELECT 1',
        'ALTER TABLE `Notification` ADD COLUMN EntityID INT NULL AFTER EntityType'
    )
);
PREPARE stmt FROM @add_entity_id;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_target_url = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'Notification'
              AND COLUMN_NAME = 'TargetUrl'
        ),
        'SELECT 1',
        'ALTER TABLE `Notification` ADD COLUMN TargetUrl VARCHAR(255) NULL AFTER Type'
    )
);
PREPARE stmt FROM @add_target_url;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_priority = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'Notification'
              AND COLUMN_NAME = 'Priority'
        ),
        'SELECT 1',
        'ALTER TABLE `Notification` ADD COLUMN Priority ENUM(''Low'',''Normal'',''High'') DEFAULT ''Normal'' AFTER TargetUrl'
    )
);
PREPARE stmt FROM @add_priority;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @add_expires_at = (
    SELECT IF(
        EXISTS (
            SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'Notification'
              AND COLUMN_NAME = 'ExpiresAt'
        ),
        'SELECT 1',
        'ALTER TABLE `Notification` ADD COLUMN ExpiresAt DATETIME NULL AFTER ReadDate'
    )
);
PREPARE stmt FROM @add_expires_at;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

ALTER TABLE `Notification`
    MODIFY Type ENUM(
        'Application',
        'Interview',
        'Offer',
        'Leave',
        'Payroll',
        'Task',
        'Contract',
        'Recruitment',
        'Permission',
        'User',
        'System'
    ) DEFAULT 'System';

SET @create_idx_notification_entity = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'Notification'
              AND INDEX_NAME = 'idx_notification_entity'
        ),
        'SELECT 1',
        'CREATE INDEX idx_notification_entity ON `Notification` (EntityType, EntityID)'
    )
);
PREPARE stmt FROM @create_idx_notification_entity;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @create_idx_notification_priority = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.STATISTICS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'Notification'
              AND INDEX_NAME = 'idx_notification_priority'
        ),
        'SELECT 1',
        'CREATE INDEX idx_notification_priority ON `Notification` (Priority, CreatedDate)'
    )
);
PREPARE stmt FROM @create_idx_notification_priority;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

SET @create_fk_notification_actor_user = (
    SELECT IF(
        EXISTS (
            SELECT 1
            FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS
            WHERE CONSTRAINT_SCHEMA = DATABASE()
              AND CONSTRAINT_NAME = 'fk_notification_actor_user'
        ),
        'SELECT 1',
        'ALTER TABLE `Notification` ADD CONSTRAINT fk_notification_actor_user FOREIGN KEY (ActorUserID) REFERENCES SystemUser(UserID) ON DELETE SET NULL'
    )
);
PREPARE stmt FROM @create_fk_notification_actor_user;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
