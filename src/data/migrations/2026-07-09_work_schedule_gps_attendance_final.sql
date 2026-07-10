-- Work Schedule + GPS Attendance migration
-- Idempotent for MySQL 8+.

CREATE TABLE IF NOT EXISTS OfficeLocation (
    OfficeLocationID INT AUTO_INCREMENT PRIMARY KEY,
    LocationCode VARCHAR(50) UNIQUE NULL,
    LocationName VARCHAR(150) NOT NULL,
    Address VARCHAR(255) NULL,
    Latitude DECIMAL(10,7) NOT NULL,
    Longitude DECIMAL(10,7) NOT NULL,
    RadiusMeters INT NOT NULL DEFAULT 500,
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS WorkSchedule (
    ScheduleID INT AUTO_INCREMENT PRIMARY KEY,
    ScheduleCode VARCHAR(50) UNIQUE NULL,
    ScheduleName VARCHAR(100) NOT NULL,
    StartTime TIME NOT NULL,
    EndTime TIME NOT NULL,
    BreakMinutes INT DEFAULT 0,
    WorkingHours DECIMAL(5,2) DEFAULT 8.00,
    GraceLateMinutes INT DEFAULT 0,
    GraceEarlyLeaveMinutes INT DEFAULT 0,
    IsActive BOOLEAN DEFAULT TRUE,
    CreatedBy INT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS EmployeeWorkSchedule (
    AssignmentID INT AUTO_INCREMENT PRIMARY KEY,
    EmployeeID INT NOT NULL,
    ScheduleID INT NOT NULL,
    WorkDate DATE NOT NULL,
    Note VARCHAR(500) NULL,
    AssignedBy INT NULL,
    CreatedAt DATETIME DEFAULT CURRENT_TIMESTAMP,
    UpdatedAt DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_employee_workdate (EmployeeID, WorkDate)
);

SET @db_name := DATABASE();

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='ScheduleID'),
    'ALTER TABLE Attendance ADD COLUMN ScheduleID INT NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckInLatitude'),
    'ALTER TABLE Attendance ADD COLUMN CheckInLatitude DECIMAL(10,7) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckInLongitude'),
    'ALTER TABLE Attendance ADD COLUMN CheckInLongitude DECIMAL(10,7) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckInDistanceMeters'),
    'ALTER TABLE Attendance ADD COLUMN CheckInDistanceMeters DECIMAL(10,2) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckInOfficeLocationID'),
    'ALTER TABLE Attendance ADD COLUMN CheckInOfficeLocationID INT NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckInMethod'),
    'ALTER TABLE Attendance ADD COLUMN CheckInMethod VARCHAR(30) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckOutLatitude'),
    'ALTER TABLE Attendance ADD COLUMN CheckOutLatitude DECIMAL(10,7) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckOutLongitude'),
    'ALTER TABLE Attendance ADD COLUMN CheckOutLongitude DECIMAL(10,7) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckOutDistanceMeters'),
    'ALTER TABLE Attendance ADD COLUMN CheckOutDistanceMeters DECIMAL(10,2) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckOutOfficeLocationID'),
    'ALTER TABLE Attendance ADD COLUMN CheckOutOfficeLocationID INT NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND COLUMN_NAME='CheckOutMethod'),
    'ALTER TABLE Attendance ADD COLUMN CheckOutMethod VARCHAR(30) NULL',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='EmployeeWorkSchedule' AND CONSTRAINT_NAME='fk_ews_employee'),
    'ALTER TABLE EmployeeWorkSchedule ADD CONSTRAINT fk_ews_employee FOREIGN KEY (EmployeeID) REFERENCES Employee(EmployeeID)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='EmployeeWorkSchedule' AND CONSTRAINT_NAME='fk_ews_schedule'),
    'ALTER TABLE EmployeeWorkSchedule ADD CONSTRAINT fk_ews_schedule FOREIGN KEY (ScheduleID) REFERENCES WorkSchedule(ScheduleID)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='EmployeeWorkSchedule' AND CONSTRAINT_NAME='fk_ews_assigned_by'),
    'ALTER TABLE EmployeeWorkSchedule ADD CONSTRAINT fk_ews_assigned_by FOREIGN KEY (AssignedBy) REFERENCES Employee(EmployeeID)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND CONSTRAINT_NAME='fk_attendance_schedule'),
    'ALTER TABLE Attendance ADD CONSTRAINT fk_attendance_schedule FOREIGN KEY (ScheduleID) REFERENCES WorkSchedule(ScheduleID)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND CONSTRAINT_NAME='fk_attendance_checkin_location'),
    'ALTER TABLE Attendance ADD CONSTRAINT fk_attendance_checkin_location FOREIGN KEY (CheckInOfficeLocationID) REFERENCES OfficeLocation(OfficeLocationID)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @sql := IF(
    NOT EXISTS (SELECT 1 FROM INFORMATION_SCHEMA.TABLE_CONSTRAINTS WHERE TABLE_SCHEMA=@db_name AND TABLE_NAME='Attendance' AND CONSTRAINT_NAME='fk_attendance_checkout_location'),
    'ALTER TABLE Attendance ADD CONSTRAINT fk_attendance_checkout_location FOREIGN KEY (CheckOutOfficeLocationID) REFERENCES OfficeLocation(OfficeLocationID)',
    'SELECT 1'
);
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

INSERT INTO OfficeLocation (LocationCode, LocationName, Address, Latitude, Longitude, RadiusMeters, IsActive)
SELECT 'HN_TEST', 'BetterHR Test Office', 'Hoa Lac', 21.0116940, 105.5177500, 500, 1
WHERE NOT EXISTS (SELECT 1 FROM OfficeLocation WHERE LocationCode = 'HN_TEST');

INSERT INTO WorkSchedule (ScheduleCode, ScheduleName, StartTime, EndTime, BreakMinutes, WorkingHours, IsActive)
SELECT 'OFFICE_DAY', 'Office Day', '08:00:00', '17:00:00', 60, 8.00, 1
WHERE NOT EXISTS (SELECT 1 FROM WorkSchedule WHERE ScheduleCode = 'OFFICE_DAY');

INSERT INTO WorkSchedule (ScheduleCode, ScheduleName, StartTime, EndTime, BreakMinutes, WorkingHours, IsActive)
SELECT 'MORNING', 'Morning', '08:00:00', '12:00:00', 0, 4.00, 1
WHERE NOT EXISTS (SELECT 1 FROM WorkSchedule WHERE ScheduleCode = 'MORNING');

INSERT INTO WorkSchedule (ScheduleCode, ScheduleName, StartTime, EndTime, BreakMinutes, WorkingHours, IsActive)
SELECT 'AFTERNOON', 'Afternoon', '13:00:00', '17:00:00', 0, 4.00, 1
WHERE NOT EXISTS (SELECT 1 FROM WorkSchedule WHERE ScheduleCode = 'AFTERNOON');

INSERT INTO Permission (PermissionCode, PermissionName, Description, Category)
SELECT code, name, name, 'Attendance'
FROM (
    SELECT 'VIEW_WORK_SCHEDULE' code, 'View Work Schedule' name UNION ALL
    SELECT 'MANAGE_WORK_SCHEDULE', 'Manage Work Schedule' UNION ALL
    SELECT 'VIEW_OWN_WORK_SCHEDULE', 'View Own Work Schedule' UNION ALL
    SELECT 'USE_GPS_ATTENDANCE', 'Use GPS Attendance' UNION ALL
    SELECT 'VIEW_OFFICE_LOCATION', 'View Office Location' UNION ALL
    SELECT 'MANAGE_OFFICE_LOCATION', 'Manage Office Location'
) p
WHERE NOT EXISTS (SELECT 1 FROM Permission x WHERE x.PermissionCode = p.code);

INSERT INTO RolePermission (RoleID, PermissionID)
SELECT r.RoleID, p.PermissionID
FROM Role r
JOIN Permission p ON p.PermissionCode IN (
    'VIEW_WORK_SCHEDULE', 'MANAGE_WORK_SCHEDULE', 'VIEW_OWN_WORK_SCHEDULE',
    'USE_GPS_ATTENDANCE', 'VIEW_OFFICE_LOCATION', 'MANAGE_OFFICE_LOCATION'
)
WHERE r.RoleID = 1
  AND NOT EXISTS (
      SELECT 1 FROM RolePermission rp WHERE rp.RoleID = r.RoleID AND rp.PermissionID = p.PermissionID
  );

INSERT INTO RolePermission (RoleID, PermissionID)
SELECT r.RoleID, p.PermissionID
FROM Role r
JOIN Permission p ON p.PermissionCode IN ('VIEW_OFFICE_LOCATION', 'MANAGE_OFFICE_LOCATION', 'VIEW_WORK_SCHEDULE')
WHERE r.RoleID IN (2, 4)
  AND NOT EXISTS (
      SELECT 1 FROM RolePermission rp WHERE rp.RoleID = r.RoleID AND rp.PermissionID = p.PermissionID
  );

INSERT INTO RolePermission (RoleID, PermissionID)
SELECT r.RoleID, p.PermissionID
FROM Role r
JOIN Permission p ON p.PermissionCode IN ('VIEW_WORK_SCHEDULE', 'MANAGE_WORK_SCHEDULE')
WHERE r.RoleID = 3
  AND NOT EXISTS (
      SELECT 1 FROM RolePermission rp WHERE rp.RoleID = r.RoleID AND rp.PermissionID = p.PermissionID
  );

INSERT INTO RolePermission (RoleID, PermissionID)
SELECT r.RoleID, p.PermissionID
FROM Role r
JOIN Permission p ON p.PermissionCode IN ('VIEW_OWN_WORK_SCHEDULE', 'USE_GPS_ATTENDANCE')
WHERE r.RoleID = 5
  AND NOT EXISTS (
      SELECT 1 FROM RolePermission rp WHERE rp.RoleID = r.RoleID AND rp.PermissionID = p.PermissionID
  );
