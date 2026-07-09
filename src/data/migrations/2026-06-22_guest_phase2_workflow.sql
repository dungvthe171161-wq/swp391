-- BetterHR Guest Phase 2 workflow migration
-- Scope: database only. This script prepares Application, Interview, Offer,
-- and Notification tables for the future Guest workflow.
-- It does not remove or deprecate Guest.RecruitmentID in Phase 2 preparation.

USE hrm_db;

CREATE TABLE IF NOT EXISTS `Application` (
    ApplicationID INT AUTO_INCREMENT PRIMARY KEY,
    GuestID INT NOT NULL,
    RecruitmentID INT NOT NULL,
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
    CONSTRAINT fk_application_recruitment FOREIGN KEY (RecruitmentID)
        REFERENCES Recruitment(RecruitmentID)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Interview (
    InterviewID INT AUTO_INCREMENT PRIMARY KEY,
    ApplicationID INT NOT NULL,
    RoundNo INT NOT NULL DEFAULT 1,
    ScheduledAt DATETIME NOT NULL,
    Location VARCHAR(255),
    MeetingLink VARCHAR(500),
    InterviewerEmployeeID INT NULL,
    Status ENUM(
        'Scheduled',
        'Completed',
        'Cancelled',
        'NoShow',
        'Rescheduled'
    ) DEFAULT 'Scheduled',
    Result ENUM(
        'Pending',
        'Passed',
        'Failed'
    ) DEFAULT 'Pending',
    Note TEXT,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedDate DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_interview_application (ApplicationID),
    INDEX idx_interview_schedule (ScheduledAt),
    CONSTRAINT fk_interview_application FOREIGN KEY (ApplicationID)
        REFERENCES `Application`(ApplicationID)
        ON DELETE CASCADE,
    CONSTRAINT fk_interview_employee FOREIGN KEY (InterviewerEmployeeID)
        REFERENCES Employee(EmployeeID)
        ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS `Offer` (
    OfferID INT AUTO_INCREMENT PRIMARY KEY,
    ApplicationID INT NOT NULL,
    Position VARCHAR(150),
    OfferedSalary DECIMAL(12,2),
    StartDate DATE,
    ExpiredAt DATETIME,
    Status ENUM(
        'Draft',
        'Sent',
        'Accepted',
        'Rejected',
        'Expired',
        'Cancelled'
    ) DEFAULT 'Draft',
    SentAt DATETIME NULL,
    RespondedAt DATETIME NULL,
    Note TEXT,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedDate DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_offer_application (ApplicationID),
    INDEX idx_offer_status (Status),
    CONSTRAINT fk_offer_application FOREIGN KEY (ApplicationID)
        REFERENCES `Application`(ApplicationID)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `Notification` (
    NotificationID INT AUTO_INCREMENT PRIMARY KEY,
    UserID INT NOT NULL,
    ApplicationID INT NULL,
    Title VARCHAR(200) NOT NULL,
    Message TEXT NOT NULL,
    Type ENUM(
        'Application',
        'Interview',
        'Offer',
        'System'
    ) DEFAULT 'System',
    IsRead BOOLEAN DEFAULT FALSE,
    CreatedDate DATETIME DEFAULT CURRENT_TIMESTAMP,
    ReadDate DATETIME NULL,
    INDEX idx_notification_user_read (UserID, IsRead, CreatedDate),
    INDEX idx_notification_application (ApplicationID),
    CONSTRAINT fk_notification_user FOREIGN KEY (UserID)
        REFERENCES SystemUser(UserID)
        ON DELETE CASCADE,
    CONSTRAINT fk_notification_application FOREIGN KEY (ApplicationID)
        REFERENCES `Application`(ApplicationID)
        ON DELETE SET NULL
);

-- Backfill Phase 1 application rows into Application.
-- Existing Guest rows that already point to a Recruitment become application rows.
-- Guest.RecruitmentID remains untouched until the Java code is migrated.
INSERT INTO `Application` (
    GuestID,
    RecruitmentID,
    AppliedDate,
    Status,
    CurrentStep,
    CV,
    CreatedDate,
    UpdatedDate
)
SELECT
    g.GuestID,
    g.RecruitmentID,
    COALESCE(g.AppliedDate, NOW()),
    CASE g.Status
        WHEN 'Hired' THEN 'Hired'
        WHEN 'Rejected' THEN 'Rejected'
        ELSE 'Applied'
    END,
    CASE g.Status
        WHEN 'Hired' THEN 'Hired'
        WHEN 'Rejected' THEN 'Rejected'
        ELSE 'Applied'
    END,
    g.CV,
    COALESCE(g.AppliedDate, NOW()),
    COALESCE(g.UpdatedDate, NOW())
FROM Guest g
WHERE g.RecruitmentID IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM `Application` a
      WHERE a.GuestID = g.GuestID
        AND a.RecruitmentID = g.RecruitmentID
  );
