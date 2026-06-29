-- BetterHR Guest Candidate Profile apply flow
-- Adds reusable candidate profiles and links applications to the profile used.

USE hrm_db;

CREATE TABLE IF NOT EXISTS CandidateProfile (
    CandidateProfileID INT AUTO_INCREMENT PRIMARY KEY,
    GuestID INT NOT NULL,
    FullName VARCHAR(150) NOT NULL,
    Phone VARCHAR(50) NOT NULL,
    Email VARCHAR(150) NOT NULL,
    DateOfBirth DATE NULL,
    Address VARCHAR(255) NULL,
    DesiredPosition VARCHAR(150) NULL,
    ExpectedSalary DECIMAL(12,2) NULL,
    WorkExperience TEXT NULL,
    CVFilePath TEXT NOT NULL,
    EmailVerified BOOLEAN DEFAULT FALSE,
    EmailVerifiedAt DATETIME NULL,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedDate DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_candidate_profile_guest (GuestID),
    INDEX idx_candidate_profile_email (Email),
    CONSTRAINT fk_candidate_profile_guest FOREIGN KEY (GuestID)
        REFERENCES Guest(GuestID)
        ON DELETE CASCADE
);

DROP PROCEDURE IF EXISTS migrate_candidate_profile_apply_flow;

DELIMITER $$

CREATE PROCEDURE migrate_candidate_profile_apply_flow()
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Application'
          AND COLUMN_NAME = 'CandidateProfileID'
    ) THEN
        ALTER TABLE `Application`
            ADD COLUMN CandidateProfileID INT NULL AFTER RecruitmentID;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.COLUMNS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Application'
          AND COLUMN_NAME = 'Note'
    ) THEN
        ALTER TABLE `Application`
            ADD COLUMN Note TEXT NULL AFTER CoverLetter;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS
        WHERE TABLE_SCHEMA = DATABASE()
          AND TABLE_NAME = 'Application'
          AND CONSTRAINT_NAME = 'fk_application_candidate_profile'
          AND CONSTRAINT_TYPE = 'FOREIGN KEY'
    ) THEN
        ALTER TABLE `Application`
            ADD CONSTRAINT fk_application_candidate_profile
            FOREIGN KEY (CandidateProfileID)
            REFERENCES CandidateProfile(CandidateProfileID)
            ON DELETE SET NULL;
    END IF;
END$$

DELIMITER ;

CALL migrate_candidate_profile_apply_flow();

DROP PROCEDURE IF EXISTS migrate_candidate_profile_apply_flow;
