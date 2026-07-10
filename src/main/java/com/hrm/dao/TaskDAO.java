package com.hrm.dao;

import com.hrm.model.entity.Task;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDAO {

    private static final List<String> EMPLOYEE_STATUSES = List.of("Waiting", "In Progress", "Submitted");
    private static final List<String> REVIEW_STATUSES = List.of("Approved", "Rejected");
    private static final List<String> TASK_STATUSES = List.of(
            "Waiting", "In Progress", "Submitted", "Approved", "Rejected", "Overdue", "Cancelled");

    public List<Task> getTasksByEmployee(int employeeId) {
        List<Task> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT t.TaskID, t.Title, t.Description, t.AssignedBy,
                   t.StartDate, t.DueDate, t.Status, t.Priority, t.AttachmentPath,
                   al.Status AS AssignmentStatus, al.SubmittedAt,
                   al.ApprovedAt, al.Feedback,
                   CASE
                       WHEN t.DueDate IS NOT NULL
                            AND t.DueDate < NOW()
                            AND (CASE
                                WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                     AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                                THEN t.Status
                                ELSE COALESCE(al.Status, t.Status)
                            END) NOT IN ('Submitted','Approved','Cancelled')
                       THEN 'Overdue'
                       ELSE CASE
                           WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                           THEN t.Status
                           ELSE COALESCE(al.Status, t.Status)
                       END
                   END AS DisplayStatus,
                   CASE
                       WHEN (CASE
                           WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                           THEN t.Status
                           ELSE COALESCE(al.Status, t.Status)
                       END) IN ('Submitted','Approved','Cancelled') THEN NULL
                       WHEN TIMESTAMPDIFF(HOUR, NOW(), t.DueDate) BETWEEN 0 AND 1 THEN '1h'
                       WHEN TIMESTAMPDIFF(HOUR, NOW(), t.DueDate) BETWEEN 2 AND 6 THEN '6h'
                       WHEN TIMESTAMPDIFF(HOUR, NOW(), t.DueDate) BETWEEN 7 AND 24 THEN '24h'
                       ELSE NULL
                   END AS DueReminder
            FROM Task t
            JOIN assignList al ON al.TaskId = t.TaskID
            WHERE al.EmpId = ?
              AND COALESCE(t.Status, 'Waiting') <> 'Cancelled'
            ORDER BY t.DueDate IS NULL, t.DueDate ASC, t.TaskID DESC
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAssignedTask(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Task getAssignedTaskById(int taskId, int employeeId) {
        String sql = """
            SELECT t.TaskID, t.Title, t.Description, t.AssignedBy,
                   t.StartDate, t.DueDate, t.Status, t.Priority, t.AttachmentPath,
                   al.Status AS AssignmentStatus, al.SubmittedAt,
                   al.ApprovedAt, al.Feedback,
                   CASE
                       WHEN t.DueDate IS NOT NULL
                            AND t.DueDate < NOW()
                            AND (CASE
                                WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                     AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                                THEN t.Status
                                ELSE COALESCE(al.Status, t.Status)
                            END) NOT IN ('Submitted','Approved','Cancelled')
                       THEN 'Overdue'
                       ELSE CASE
                           WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                           THEN t.Status
                           ELSE COALESCE(al.Status, t.Status)
                       END
                   END AS DisplayStatus,
                   CASE
                       WHEN (CASE
                           WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                           THEN t.Status
                           ELSE COALESCE(al.Status, t.Status)
                       END) IN ('Submitted','Approved','Cancelled') THEN NULL
                       WHEN TIMESTAMPDIFF(HOUR, NOW(), t.DueDate) BETWEEN 0 AND 1 THEN '1h'
                       WHEN TIMESTAMPDIFF(HOUR, NOW(), t.DueDate) BETWEEN 2 AND 6 THEN '6h'
                       WHEN TIMESTAMPDIFF(HOUR, NOW(), t.DueDate) BETWEEN 7 AND 24 THEN '24h'
                       ELSE NULL
                   END AS DueReminder
            FROM Task t
            JOIN assignList al ON al.TaskId = t.TaskID
            WHERE t.TaskID = ? AND al.EmpId = ?
              AND COALESCE(t.Status, 'Waiting') <> 'Cancelled'
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAssignedTask(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAssignedTaskProgress(int taskId, int employeeId, String status, String feedback) {
        if (!EMPLOYEE_STATUSES.contains(status)) {
            return false;
        }

        String assignmentSql = """
            UPDATE assignList
            SET Status = ?,
                Feedback = ?,
                SubmittedAt = CASE WHEN ? = 'Submitted' THEN NOW() ELSE SubmittedAt END
            WHERE TaskId = ? AND EmpId = ?
        """;
        String taskSql = """
            UPDATE Task
            SET Status = CASE
                    WHEN EXISTS (
                        SELECT 1 FROM assignList
                        WHERE TaskId = ? AND Status = 'Submitted'
                    ) THEN 'Submitted'
                    WHEN EXISTS (
                        SELECT 1 FROM assignList
                        WHERE TaskId = ? AND Status = 'In Progress'
                    ) THEN 'In Progress'
                    ELSE Status
                END
            WHERE TaskID = ?
        """;
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(assignmentSql)) {
                ps.setString(1, status);
                ps.setString(2, cleanFeedback(feedback));
                ps.setString(3, status);
                ps.setInt(4, taskId);
                ps.setInt(5, employeeId);
                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }
            }
            try (PreparedStatement ps = con.prepareStatement(taskSql)) {
                ps.setInt(1, taskId);
                ps.setInt(2, taskId);
                ps.setInt(3, taskId);
                ps.executeUpdate();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean reviewAssignment(int taskId, int employeeId, int managerEmployeeId,
                                    String status, String feedback) {
        if (!REVIEW_STATUSES.contains(status)) {
            return false;
        }
        if ("Rejected".equals(status) && (feedback == null || feedback.isBlank())) {
            return false;
        }

        String assignmentSql = """
            UPDATE assignList al
            JOIN Task t ON t.TaskID = al.TaskId
            SET al.Status = ?,
                al.ApprovedAt = CASE WHEN ? = 'Approved' THEN NOW() ELSE NULL END,
                al.Feedback = ?
            WHERE al.TaskId = ? AND al.EmpId = ? AND t.AssignedBy = ?
        """;
        String taskSql = """
            UPDATE Task
            SET Status = CASE
                    WHEN EXISTS (
                        SELECT 1 FROM assignList
                        WHERE TaskId = ? AND Status = 'Rejected'
                    ) THEN 'Rejected'
                    WHEN NOT EXISTS (
                        SELECT 1 FROM assignList
                        WHERE TaskId = ? AND Status <> 'Approved'
                    ) THEN 'Approved'
                    WHEN EXISTS (
                        SELECT 1 FROM assignList
                        WHERE TaskId = ? AND Status = 'Submitted'
                    ) THEN 'Submitted'
                    ELSE 'In Progress'
                END
            WHERE TaskID = ? AND AssignedBy = ?
        """;
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(assignmentSql)) {
                ps.setString(1, status);
                ps.setString(2, status);
                ps.setString(3, cleanFeedback(feedback));
                ps.setInt(4, taskId);
                ps.setInt(5, employeeId);
                ps.setInt(6, managerEmployeeId);
                if (ps.executeUpdate() == 0) {
                    con.rollback();
                    return false;
                }
            }
            try (PreparedStatement ps = con.prepareStatement(taskSql)) {
                ps.setInt(1, taskId);
                ps.setInt(2, taskId);
                ps.setInt(3, taskId);
                ps.setInt(4, taskId);
                ps.setInt(5, managerEmployeeId);
                ps.executeUpdate();
            }
            con.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean cancelTask(int taskId, int managerEmployeeId) {
        String sql = "UPDATE Task SET Status = 'Cancelled' WHERE TaskID = ? AND AssignedBy = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, managerEmployeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteCancelledTask(int taskId, int managerEmployeeId) {
        String deleteAssignmentsSql = "DELETE FROM assignList WHERE TaskId = ?";
        String deleteTaskSql = """
            DELETE FROM Task
            WHERE TaskID = ? AND AssignedBy = ? AND Status = 'Cancelled'
        """;
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try (PreparedStatement ps = con.prepareStatement(deleteAssignmentsSql)) {
                ps.setInt(1, taskId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = con.prepareStatement(deleteTaskSql)) {
                ps.setInt(1, taskId);
                ps.setInt(2, managerEmployeeId);
                boolean deleted = ps.executeUpdate() > 0;
                if (deleted) {
                    con.commit();
                    return true;
                }
                con.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countOpenTasksByEmployee(int employeeId) {
        String sql = """
            SELECT COUNT(DISTINCT t.TaskID) AS Total
            FROM Task t
            JOIN assignList al ON al.TaskId = t.TaskID
            WHERE al.EmpId = ? AND (CASE
                WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                     AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                THEN t.Status
                ELSE COALESCE(al.Status, t.Status)
            END) IN ('Waiting', 'In Progress', 'Rejected')
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Map<String, Object>> getDepartmentWorkload(int managerEmployeeId, int departmentId) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = """
            SELECT e.EmployeeID, e.FullName, e.Position, t.TaskID, t.Title, t.DueDate,
                   t.Priority, t.Status AS TaskStatus, al.Status AS AssignmentStatus,
                   al.SubmittedAt, al.ApprovedAt, al.Feedback,
                   CASE
                       WHEN t.DueDate IS NOT NULL
                            AND t.DueDate < NOW()
                            AND (CASE
                                WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                     AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                                THEN t.Status
                                ELSE COALESCE(al.Status, t.Status)
                            END) NOT IN ('Submitted','Approved','Cancelled')
                       THEN 'Overdue'
                       ELSE CASE
                           WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                           THEN t.Status
                           ELSE COALESCE(al.Status, t.Status)
                       END
                   END AS DisplayStatus
            FROM Employee e
            LEFT JOIN assignList al ON al.EmpId = e.EmployeeID
            LEFT JOIN Task t ON t.TaskID = al.TaskId AND t.AssignedBy = ?
            WHERE e.DepartmentID = ?
            ORDER BY e.FullName ASC, t.DueDate IS NULL, t.DueDate ASC
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, managerEmployeeId);
            ps.setInt(2, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapWorkloadRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Map<String, Object>> getAssigneesByTask(int taskId, int managerEmployeeId) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = """
            SELECT e.EmployeeID, e.FullName, e.Position, e.Email, al.Status AS AssignmentStatus,
                   al.SubmittedAt, al.ApprovedAt, al.Feedback,
                   t.DueDate,
                   CASE
                       WHEN t.DueDate IS NOT NULL
                            AND t.DueDate < NOW()
                            AND (CASE
                                WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                     AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                                THEN t.Status
                                ELSE COALESCE(al.Status, t.Status)
                            END) NOT IN ('Submitted','Approved','Cancelled')
                       THEN 'Overdue'
                       ELSE CASE
                           WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                                AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                           THEN t.Status
                           ELSE COALESCE(al.Status, t.Status)
                       END
                   END AS DisplayStatus
            FROM assignList al
            JOIN Task t ON t.TaskID = al.TaskId
            JOIN Employee e ON e.EmployeeID = al.EmpId
            WHERE al.TaskId = ? AND t.AssignedBy = ?
            ORDER BY e.FullName ASC
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, managerEmployeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(mapAssigneeRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public List<Map<String, Object>> findDueSoonAssignments(int hoursBeforeDue) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = """
            SELECT e.EmployeeID, e.FullName, e.Email, su.UserID, t.TaskID, t.Title, t.DueDate,
                   al.Status AS AssignmentStatus
            FROM Task t
            JOIN assignList al ON al.TaskId = t.TaskID
            JOIN Employee e ON e.EmployeeID = al.EmpId
            LEFT JOIN SystemUser su ON su.EmployeeID = e.EmployeeID
            WHERE t.DueDate BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? HOUR)
              AND (CASE
                  WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                       AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                  THEN t.Status
                  ELSE COALESCE(al.Status, t.Status)
              END) NOT IN ('Submitted','Approved','Cancelled')
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hoursBeforeDue);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("employeeId", rs.getInt("EmployeeID"));
                    row.put("fullName", rs.getString("FullName"));
                    row.put("email", rs.getString("Email"));
                    row.put("userId", rs.getObject("UserID"));
                    row.put("taskId", rs.getInt("TaskID"));
                    row.put("title", rs.getString("Title"));
                    row.put("dueDate", rs.getTimestamp("DueDate"));
                    row.put("assignmentStatus", rs.getString("AssignmentStatus"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public void ensureDeadlineReminderColumn() {
        String checkSql = """
            SELECT COUNT(*) AS Total
            FROM information_schema.COLUMNS
            WHERE TABLE_SCHEMA = DATABASE()
              AND TABLE_NAME = 'assignList'
              AND COLUMN_NAME = 'DeadlineReminderSentAt'
        """;
        String alterSql = "ALTER TABLE assignList ADD COLUMN DeadlineReminderSentAt DATETIME NULL AFTER Feedback";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement check = con.prepareStatement(checkSql);
             ResultSet rs = check.executeQuery()) {
            if (rs.next() && rs.getInt("Total") == 0) {
                try (PreparedStatement alter = con.prepareStatement(alterSql)) {
                    alter.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Map<String, Object>> findDeadlineReminderCandidates(int hoursBeforeDue) {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = """
            SELECT e.EmployeeID, e.FullName, e.Email, t.TaskID, t.Title, t.DueDate,
                   t.DueDate AS DueAt,
                   al.Status AS AssignmentStatus
            FROM Task t
            JOIN assignList al ON al.TaskId = t.TaskID
            JOIN Employee e ON e.EmployeeID = al.EmpId
            WHERE t.DueDate IS NOT NULL
              AND al.DeadlineReminderSentAt IS NULL
              AND COALESCE(e.Email, '') <> ''
              AND t.DueDate BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL ? HOUR)
              AND (CASE
                  WHEN COALESCE(al.Status, 'Waiting') = 'Waiting'
                       AND t.Status IN ('Submitted','Approved','Rejected','Cancelled')
                  THEN t.Status
                  ELSE COALESCE(al.Status, t.Status)
              END) NOT IN ('Submitted','Approved','Cancelled')
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, hoursBeforeDue);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("employeeId", rs.getInt("EmployeeID"));
                    row.put("fullName", rs.getString("FullName"));
                    row.put("email", rs.getString("Email"));
                    row.put("taskId", rs.getInt("TaskID"));
                    row.put("title", rs.getString("Title"));
                    row.put("dueDate", rs.getTimestamp("DueDate"));
                    row.put("dueAt", rs.getTimestamp("DueAt"));
                    row.put("assignmentStatus", rs.getString("AssignmentStatus"));
                    rows.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }

    public boolean markDeadlineReminderSent(int taskId, int employeeId) {
        String sql = """
            UPDATE assignList
            SET DeadlineReminderSentAt = NOW()
            WHERE TaskId = ? AND EmpId = ? AND DeadlineReminderSentAt IS NULL
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Task mapAssignedTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setTaskId(rs.getInt("TaskID"));
        task.setTitle(rs.getString("Title"));
        task.setDescription(rs.getString("Description"));
        task.setAssignedBy(rs.getInt("AssignedBy"));
        Timestamp startDate = rs.getTimestamp("StartDate");
        if (startDate != null) {
            task.setStartDate(startDate.toLocalDateTime());
        }
        Timestamp dueDate = rs.getTimestamp("DueDate");
        if (dueDate != null) {
            task.setDueDate(dueDate.toLocalDateTime());
        }
        task.setStatus(rs.getString("DisplayStatus"));
        task.setPriority(rs.getString("Priority"));
        task.setAttachmentPath(rs.getString("AttachmentPath"));
        task.setAssignmentStatus(rs.getString("AssignmentStatus"));
        task.setFeedback(rs.getString("Feedback"));
        task.setDueReminder(rs.getString("DueReminder"));
        Timestamp submittedAt = rs.getTimestamp("SubmittedAt");
        if (submittedAt != null) {
            task.setSubmittedAt(submittedAt.toLocalDateTime());
        }
        Timestamp approvedAt = rs.getTimestamp("ApprovedAt");
        if (approvedAt != null) {
            task.setApprovedAt(approvedAt.toLocalDateTime());
        }
        return task;
    }

    private Map<String, Object> mapWorkloadRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        row.put("employeeId", rs.getInt("EmployeeID"));
        row.put("fullName", rs.getString("FullName"));
        row.put("position", rs.getString("Position"));
        row.put("taskId", rs.getObject("TaskID"));
        row.put("title", rs.getString("Title"));
        row.put("dueDate", rs.getTimestamp("DueDate"));
        row.put("priority", rs.getString("Priority"));
        row.put("taskStatus", rs.getString("TaskStatus"));
        row.put("assignmentStatus", rs.getString("AssignmentStatus"));
        row.put("displayStatus", rs.getString("DisplayStatus"));
        row.put("submittedAt", rs.getTimestamp("SubmittedAt"));
        row.put("approvedAt", rs.getTimestamp("ApprovedAt"));
        row.put("feedback", rs.getString("Feedback"));
        return row;
    }

    private Map<String, Object> mapAssigneeRow(ResultSet rs) throws SQLException {
        Map<String, Object> row = new HashMap<>();
        row.put("employeeId", rs.getInt("EmployeeID"));
        row.put("fullName", rs.getString("FullName"));
        row.put("position", rs.getString("Position"));
        row.put("email", rs.getString("Email"));
        row.put("assignmentStatus", rs.getString("AssignmentStatus"));
        row.put("displayStatus", rs.getString("DisplayStatus"));
        row.put("submittedAt", rs.getTimestamp("SubmittedAt"));
        row.put("approvedAt", rs.getTimestamp("ApprovedAt"));
        row.put("feedback", rs.getString("Feedback"));
        row.put("dueDate", rs.getTimestamp("DueDate"));
        return row;
    }

    private String cleanFeedback(String feedback) {
        if (feedback == null) {
            return null;
        }
        String cleaned = feedback.trim();
        return cleaned.length() > 1000 ? cleaned.substring(0, 1000) : cleaned;
    }

    public boolean isValidTaskStatus(String status) {
        return TASK_STATUSES.contains(status);
    }
}
