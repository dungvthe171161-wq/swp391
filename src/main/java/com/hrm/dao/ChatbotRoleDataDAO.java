package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class ChatbotRoleDataDAO implements ChatbotRoleDataRepository {

    @Override
    public GuestSnapshot loadGuestSnapshot(int userId) throws SQLException {
        try (Connection connection = requireConnection()) {
            int applicationCount = count(connection, """
                    SELECT COUNT(*)
                    FROM Application application
                    INNER JOIN Guest guest ON guest.GuestID = application.GuestID
                    WHERE guest.UserID = ?
                    """, userId);

            String latestStatus = null;
            String currentStep = null;
            String latestSql = """
                    SELECT application.Status, application.CurrentStep
                    FROM Application application
                    INNER JOIN Guest guest ON guest.GuestID = application.GuestID
                    WHERE guest.UserID = ?
                    ORDER BY application.AppliedDate DESC, application.ApplicationID DESC
                    LIMIT 1
                    """;
            try (PreparedStatement statement = connection.prepareStatement(latestSql)) {
                statement.setInt(1, userId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        latestStatus = rs.getString("Status");
                        currentStep = rs.getString("CurrentStep");
                    }
                }
            }

            LocalDateTime nextInterviewAt = null;
            String interviewStatus = null;
            String interviewSql = """
                    SELECT interview.ScheduledAt, interview.Status
                    FROM Interview interview
                    INNER JOIN Application application
                        ON application.ApplicationID = interview.ApplicationID
                    INNER JOIN Guest guest ON guest.GuestID = application.GuestID
                    WHERE guest.UserID = ?
                      AND interview.Status IN ('Scheduled', 'Rescheduled')
                      AND interview.ScheduledAt >= CURRENT_TIMESTAMP
                    ORDER BY interview.ScheduledAt ASC, interview.InterviewID ASC
                    LIMIT 1
                    """;
            try (PreparedStatement statement = connection.prepareStatement(interviewSql)) {
                statement.setInt(1, userId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        Timestamp scheduledAt = rs.getTimestamp("ScheduledAt");
                        nextInterviewAt = scheduledAt != null ? scheduledAt.toLocalDateTime() : null;
                        interviewStatus = rs.getString("Status");
                    }
                }
            }

            String offerStatus = null;
            LocalDate offerStartDate = null;
            String offerSql = """
                    SELECT offer.Status, offer.StartDate
                    FROM Offer offer
                    INNER JOIN Application application
                        ON application.ApplicationID = offer.ApplicationID
                    INNER JOIN Guest guest ON guest.GuestID = application.GuestID
                    WHERE guest.UserID = ?
                    ORDER BY offer.CreatedDate DESC, offer.OfferID DESC
                    LIMIT 1
                    """;
            try (PreparedStatement statement = connection.prepareStatement(offerSql)) {
                statement.setInt(1, userId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        offerStatus = rs.getString("Status");
                        offerStartDate = toLocalDate(rs, "StartDate");
                    }
                }
            }

            return new GuestSnapshot(
                    applicationCount,
                    latestStatus,
                    currentStep,
                    nextInterviewAt,
                    interviewStatus,
                    offerStatus,
                    offerStartDate);
        }
    }

    @Override
    public EmployeeSnapshot loadEmployeeSnapshot(int employeeId) throws SQLException {
        try (Connection connection = requireConnection()) {
            int pendingLeaveCount = count(connection, """
                    SELECT COUNT(*)
                    FROM MailRequest
                    WHERE EmployeeID = ?
                      AND RequestType = 'Leave'
                      AND Status = 'Pending'
                    """, employeeId);

            String latestLeaveStatus = queryString(connection, """
                    SELECT Status
                    FROM MailRequest
                    WHERE EmployeeID = ? AND RequestType = 'Leave'
                    ORDER BY RequestID DESC
                    LIMIT 1
                    """, employeeId, "Status");

            int waitingTasks = 0;
            int inProgressTasks = 0;
            int completedTasks = 0;
            String taskSql = """
                    SELECT
                        COUNT(DISTINCT CASE WHEN task.Status = 'Waiting' THEN task.TaskID END) AS WaitingCount,
                        COUNT(DISTINCT CASE WHEN task.Status = 'In Progress' THEN task.TaskID END) AS InProgressCount,
                        COUNT(DISTINCT CASE WHEN task.Status = 'Completed' THEN task.TaskID END) AS CompletedCount
                    FROM assignList assignment
                    INNER JOIN Task task ON task.TaskID = assignment.TaskId
                    WHERE assignment.EmpId = ?
                    """;
            try (PreparedStatement statement = connection.prepareStatement(taskSql)) {
                statement.setInt(1, employeeId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        waitingTasks = rs.getInt("WaitingCount");
                        inProgressTasks = rs.getInt("InProgressCount");
                        completedTasks = rs.getInt("CompletedCount");
                    }
                }
            }

            String contractStatus = null;
            String contractType = null;
            LocalDate contractEndDate = null;
            String contractSql = """
                    SELECT Status, ContractType, EndDate
                    FROM Contract
                    WHERE EmployeeID = ?
                    ORDER BY StartDate DESC, ContractID DESC
                    LIMIT 1
                    """;
            try (PreparedStatement statement = connection.prepareStatement(contractSql)) {
                statement.setInt(1, employeeId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        contractStatus = rs.getString("Status");
                        contractType = rs.getString("ContractType");
                        contractEndDate = toLocalDate(rs, "EndDate");
                    }
                }
            }

            String payrollPeriod = null;
            String payrollStatus = null;
            String payrollSql = """
                    SELECT PayPeriod, Status
                    FROM Payroll
                    WHERE EmployeeID = ?
                    ORDER BY PayPeriod DESC, PayrollID DESC
                    LIMIT 1
                    """;
            try (PreparedStatement statement = connection.prepareStatement(payrollSql)) {
                statement.setInt(1, employeeId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        payrollPeriod = rs.getString("PayPeriod");
                        payrollStatus = rs.getString("Status");
                    }
                }
            }

            return new EmployeeSnapshot(
                    pendingLeaveCount,
                    latestLeaveStatus,
                    waitingTasks,
                    inProgressTasks,
                    completedTasks,
                    contractStatus,
                    contractType,
                    contractEndDate,
                    payrollPeriod,
                    payrollStatus);
        }
    }

    @Override
    public ManagerSnapshot loadManagerSnapshot(int managerEmployeeId) throws SQLException {
        try (Connection connection = requireConnection()) {
            String departmentScope = queryString(connection, """
                    SELECT GROUP_CONCAT(DeptName ORDER BY DeptName SEPARATOR ', ') AS DepartmentScope
                    FROM Department
                    WHERE DeptManagerID = ?
                    """, managerEmployeeId, "DepartmentScope");
            if (departmentScope == null || departmentScope.isBlank()) {
                return new ManagerSnapshot(null, 0, 0, 0, 0);
            }

            int pendingLeaves = count(connection, """
                    SELECT COUNT(*)
                    FROM MailRequest request
                    INNER JOIN Employee employee ON employee.EmployeeID = request.EmployeeID
                    INNER JOIN Department department
                        ON department.DepartmentID = employee.DepartmentID
                    WHERE department.DeptManagerID = ?
                      AND request.RequestType = 'Leave'
                      AND request.Status = 'Pending'
                    """, managerEmployeeId);

            int waitingTasks = 0;
            int inProgressTasks = 0;
            int completedTasks = 0;
            String taskSql = """
                    SELECT
                        COUNT(DISTINCT CASE WHEN task.Status = 'Waiting' THEN task.TaskID END) AS WaitingCount,
                        COUNT(DISTINCT CASE WHEN task.Status = 'In Progress' THEN task.TaskID END) AS InProgressCount,
                        COUNT(DISTINCT CASE WHEN task.Status = 'Completed' THEN task.TaskID END) AS CompletedCount
                    FROM Task task
                    INNER JOIN assignList assignment ON assignment.TaskId = task.TaskID
                    INNER JOIN Employee employee ON employee.EmployeeID = assignment.EmpId
                    INNER JOIN Department department
                        ON department.DepartmentID = employee.DepartmentID
                    WHERE department.DeptManagerID = ?
                    """;
            try (PreparedStatement statement = connection.prepareStatement(taskSql)) {
                statement.setInt(1, managerEmployeeId);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        waitingTasks = rs.getInt("WaitingCount");
                        inProgressTasks = rs.getInt("InProgressCount");
                        completedTasks = rs.getInt("CompletedCount");
                    }
                }
            }

            return new ManagerSnapshot(
                    departmentScope,
                    pendingLeaves,
                    waitingTasks,
                    inProgressTasks,
                    completedTasks);
        }
    }
    @Override
    public HrSnapshot loadHrSnapshot() throws SQLException {
        try (Connection connection = requireConnection()) {
            int applicationCount = 0;
            int screeningCount = 0;
            int interviewCount = 0;
            int offeredCount = 0;
            String applicationSql = """
                    SELECT
                        COUNT(*) AS TotalCount,
                        COUNT(CASE WHEN Status = 'Screening' THEN 1 END) AS ScreeningCount,
                        COUNT(CASE WHEN Status = 'Interview' THEN 1 END) AS InterviewCount,
                        COUNT(CASE WHEN Status = 'Offered' THEN 1 END) AS OfferedCount
                    FROM Application
                    """;
            try (PreparedStatement statement = connection.prepareStatement(applicationSql);
                 ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    applicationCount = rs.getInt("TotalCount");
                    screeningCount = rs.getInt("ScreeningCount");
                    interviewCount = rs.getInt("InterviewCount");
                    offeredCount = rs.getInt("OfferedCount");
                }
            }

            int scheduledInterviews = count(connection, """
                    SELECT COUNT(*)
                    FROM Interview
                    WHERE Status IN ('Scheduled', 'Rescheduled')
                      AND ScheduledAt >= CURRENT_TIMESTAMP
                    """);
            int draftOffers = count(connection,
                    "SELECT COUNT(*) FROM Offer WHERE Status = 'Draft'");
            int sentOffers = count(connection,
                    "SELECT COUNT(*) FROM Offer WHERE Status = 'Sent'");
            int pendingLeaves = count(connection, """
                    SELECT COUNT(*) FROM MailRequest
                    WHERE RequestType = 'Leave' AND Status = 'Pending'
                    """);
            int pendingPayrolls = count(connection,
                    "SELECT COUNT(*) FROM Payroll WHERE Status = 'Pending'");
            int pendingContracts = count(connection, """
                    SELECT COUNT(*) FROM Contract
                    WHERE Status IN ('Pending_Approval', 'Pending_Signature')
                    """);

            return new HrSnapshot(
                    applicationCount,
                    screeningCount,
                    interviewCount,
                    offeredCount,
                    scheduledInterviews,
                    draftOffers,
                    sentOffers,
                    pendingLeaves,
                    pendingPayrolls,
                    pendingContracts);
        }
    }

    private int count(Connection connection, String sql, int... parameters) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < parameters.length; i++) {
                statement.setInt(i + 1, parameters[i]);
            }
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private String queryString(Connection connection,
                               String sql,
                               int parameter,
                               String column) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parameter);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getString(column) : null;
            }
        }
    }

    private LocalDate toLocalDate(ResultSet rs, String column) throws SQLException {
        java.sql.Date value = rs.getDate(column);
        return value != null ? value.toLocalDate() : null;
    }

    private Connection requireConnection() throws SQLException {
        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            throw new SQLException("Cannot connect to role-aware chatbot data.");
        }
        return connection;
    }
}
