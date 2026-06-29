-- BetterHR Guest Phase 1 profile migration
-- Purpose: add nullable candidate profile columns without breaking existing apply flow.
-- This script is idempotent for MySQL 8.0.

USE hrm_db;

DROP PROCEDURE IF EXISTS migrate_guest_phase1_profile;

DELIMITER $$

CREATE PROCEDURE migrate_guest_phase1_profile()
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND COLUMN_NAME = 'UserID'
    ) THEN
        ALTER TABLE Guest ADD COLUMN UserID INT NULL AFTER GuestID;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND COLUMN_NAME = 'Avatar'
    ) THEN
        ALTER TABLE Guest ADD COLUMN Avatar VARCHAR(500) NULL AFTER CV;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND COLUMN_NAME = 'Gender'
    ) THEN
        ALTER TABLE Guest ADD COLUMN Gender VARCHAR(20) NULL AFTER Avatar;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND COLUMN_NAME = 'DateOfBirth'
    ) THEN
        ALTER TABLE Guest ADD COLUMN DateOfBirth DATE NULL AFTER Gender;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND COLUMN_NAME = 'Address'
    ) THEN
        ALTER TABLE Guest ADD COLUMN Address VARCHAR(255) NULL AFTER DateOfBirth;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND COLUMN_NAME = 'UpdatedDate'
    ) THEN
        ALTER TABLE Guest
            ADD COLUMN UpdatedDate DATETIME NULL
            DEFAULT CURRENT_TIMESTAMP
            ON UPDATE CURRENT_TIMESTAMP
            AFTER AppliedDate;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND CONSTRAINT_NAME = 'fk_guest_system_user'
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE Guest
            ADD CONSTRAINT fk_guest_system_user
            FOREIGN KEY (UserID) REFERENCES SystemUser(UserID)
            ON DELETE SET NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND INDEX_NAME = 'idx_guest_userid'
    ) THEN
        CREATE INDEX idx_guest_userid ON Guest(UserID);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND INDEX_NAME = 'idx_guest_email'
    ) THEN
        CREATE INDEX idx_guest_email ON Guest(Email);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM INFORMATION_SCHEMA.STATISTICS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Guest'
          AND INDEX_NAME = 'idx_guest_recruitment_status'
    ) THEN
        CREATE INDEX idx_guest_recruitment_status ON Guest(RecruitmentID, Status);
    END IF;
END$$

DELIMITER ;

CALL migrate_guest_phase1_profile();

DROP PROCEDURE IF EXISTS migrate_guest_phase1_profile;
