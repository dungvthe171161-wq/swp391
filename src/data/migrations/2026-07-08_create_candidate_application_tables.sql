-- Minimal application workflow tables required by the candidate apply flow.
-- Safe to run multiple times.

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

CREATE TABLE IF NOT EXISTS `Application` (
    ApplicationID INT AUTO_INCREMENT PRIMARY KEY,
    GuestID INT NOT NULL,
    RecruitmentID INT NOT NULL,
    CandidateProfileID INT NULL,
    AppliedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    Status ENUM(
        'Applied',
        'Screening',
        'Interview',
        'Offered',
        'Rejected',
        'Withdrawn',
        'Hired'
    ) DEFAULT 'Applied',
    CurrentStep ENUM(
        'Applied',
        'Screening',
        'Interview',
        'Offer',
        'Hired',
        'Rejected',
        'Withdrawn'
    ) DEFAULT 'Applied',
    CV TEXT,
    CoverLetter TEXT,
    Note TEXT,
    Source VARCHAR(50) DEFAULT 'BetterHR',
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedDate DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_application_guest_recruitment (GuestID, RecruitmentID),
    INDEX idx_application_guest (GuestID),
    INDEX idx_application_recruitment (RecruitmentID),
    INDEX idx_application_status (Status),
    CONSTRAINT fk_application_guest FOREIGN KEY (GuestID)
        REFERENCES Guest(GuestID)
        ON DELETE CASCADE,
    CONSTRAINT fk_application_candidate_profile FOREIGN KEY (CandidateProfileID)
        REFERENCES CandidateProfile(CandidateProfileID)
        ON DELETE SET NULL,
    CONSTRAINT fk_application_recruitment FOREIGN KEY (RecruitmentID)
        REFERENCES Recruitment(RecruitmentID)
        ON DELETE CASCADE
);
