-- BetterHR Guest Phase 2 sample data
-- Scope: database seed only. Safe to run multiple times.

USE hrm_db;

-- Ensure Application has rows migrated from Phase 1 Guest applications.
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

-- Sample interview for a hired application.
INSERT INTO Interview (
    ApplicationID,
    RoundNo,
    ScheduledAt,
    Location,
    MeetingLink,
    InterviewerEmployeeID,
    Status,
    Result,
    Note
)
SELECT
    a.ApplicationID,
    1,
    DATE_SUB(NOW(), INTERVAL 5 DAY),
    'Phong hop BetterHR',
    NULL,
    1,
    'Completed',
    'Passed',
    'Ung vien phu hop voi vi tri.'
FROM `Application` a
WHERE a.Status = 'Hired'
  AND NOT EXISTS (
      SELECT 1
      FROM Interview i
      WHERE i.ApplicationID = a.ApplicationID
        AND i.RoundNo = 1
  )
ORDER BY a.ApplicationID
LIMIT 1;

-- Sample upcoming interview for an applied application.
INSERT INTO Interview (
    ApplicationID,
    RoundNo,
    ScheduledAt,
    Location,
    MeetingLink,
    InterviewerEmployeeID,
    Status,
    Result,
    Note
)
SELECT
    a.ApplicationID,
    1,
    DATE_ADD(NOW(), INTERVAL 3 DAY),
    'Online',
    'https://meet.google.com/betterhr-demo',
    2,
    'Scheduled',
    'Pending',
    'Phong van vong 1 voi HR.'
FROM `Application` a
WHERE a.Status = 'Applied'
  AND NOT EXISTS (
      SELECT 1
      FROM Interview i
      WHERE i.ApplicationID = a.ApplicationID
        AND i.RoundNo = 1
  )
ORDER BY a.ApplicationID
LIMIT 1;

-- Sample offer for a hired application.
INSERT INTO `Offer` (
    ApplicationID,
    Position,
    OfferedSalary,
    StartDate,
    ExpiredAt,
    Status,
    SentAt,
    Note
)
SELECT
    a.ApplicationID,
    COALESCE(r.JobTitle, 'Nhan vien BetterHR'),
    COALESCE(r.Salary, 15000000),
    DATE_ADD(CURDATE(), INTERVAL 14 DAY),
    DATE_ADD(NOW(), INTERVAL 7 DAY),
    'Sent',
    NOW(),
    'Thu moi nhan viec mau cho quy trinh Phase 2.'
FROM `Application` a
JOIN Recruitment r ON r.RecruitmentID = a.RecruitmentID
WHERE a.Status = 'Hired'
  AND NOT EXISTS (
      SELECT 1
      FROM `Offer` o
      WHERE o.ApplicationID = a.ApplicationID
  )
ORDER BY a.ApplicationID
LIMIT 1;

-- Sample notifications for Guest users that are linked to SystemUser.
INSERT INTO `Notification` (
    UserID,
    ApplicationID,
    Title,
    Message,
    Type,
    IsRead
)
SELECT
    g.UserID,
    a.ApplicationID,
    'Ho so ung tuyen da duoc ghi nhan',
    'BetterHR da ghi nhan ho so cua ban. Vui long theo doi trang thai tren cong ung vien.',
    'Application',
    FALSE
FROM `Application` a
JOIN Guest g ON g.GuestID = a.GuestID
WHERE g.UserID IS NOT NULL
  AND NOT EXISTS (
      SELECT 1
      FROM `Notification` n
      WHERE n.UserID = g.UserID
        AND n.ApplicationID = a.ApplicationID
        AND n.Type = 'Application'
  )
ORDER BY a.ApplicationID
LIMIT 5;

INSERT INTO `Notification` (
    UserID,
    ApplicationID,
    Title,
    Message,
    Type,
    IsRead
)
SELECT
    g.UserID,
    i.ApplicationID,
    'Lich phong van moi',
    'Ban co lich phong van moi tren BetterHR. Hay kiem tra thoi gian va hinh thuc phong van.',
    'Interview',
    FALSE
FROM Interview i
JOIN `Application` a ON a.ApplicationID = i.ApplicationID
JOIN Guest g ON g.GuestID = a.GuestID
WHERE g.UserID IS NOT NULL
  AND i.Status = 'Scheduled'
  AND NOT EXISTS (
      SELECT 1
      FROM `Notification` n
      WHERE n.UserID = g.UserID
        AND n.ApplicationID = i.ApplicationID
        AND n.Type = 'Interview'
  )
ORDER BY i.ScheduledAt
LIMIT 5;
