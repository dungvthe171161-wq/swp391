package com.hrm.dao;

import com.hrm.model.entity.Task;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TaskDAO {

    public List<Task> getTasksByEmployee(int employeeId) {
        List<Task> list = new ArrayList<>();
        String sql = """
            SELECT DISTINCT t.TaskID, t.Title, t.Description, t.AssignedBy,
                   t.StartDate, t.DueDate, t.Status
            FROM Task t
            JOIN assignList al ON al.TaskId = t.TaskID
            WHERE al.EmpId = ?
            ORDER BY t.DueDate IS NULL, t.DueDate ASC, t.TaskID DESC
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTask(rs));
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
                   t.StartDate, t.DueDate, t.Status
            FROM Task t
            JOIN assignList al ON al.TaskId = t.TaskID
            WHERE t.TaskID = ? AND al.EmpId = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, taskId);
            ps.setInt(2, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTask(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateAssignedTaskStatus(int taskId, int employeeId, String status) {
        if (!List.of("Waiting", "In Progress", "Completed", "Rejected").contains(status)) {
            return false;
        }
        String sql = """
            UPDATE Task t
            JOIN assignList al ON al.TaskId = t.TaskID
            SET t.Status = ?
            WHERE t.TaskID = ? AND al.EmpId = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, taskId);
            ps.setInt(3, employeeId);
            return ps.executeUpdate() > 0;
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
            WHERE al.EmpId = ? AND t.Status IN ('Waiting', 'In Progress')
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

    private Task mapTask(ResultSet rs) throws SQLException {
        Task task = new Task();
        task.setTaskId(rs.getInt("TaskID"));
        task.setTitle(rs.getString("Title"));
        task.setDescription(rs.getString("Description"));
        task.setAssignedBy(rs.getInt("AssignedBy"));
        Date startDate = rs.getDate("StartDate");
        if (startDate != null) {
            task.setStartDate(startDate.toLocalDate());
        }
        Date dueDate = rs.getDate("DueDate");
        if (dueDate != null) {
            task.setDueDate(dueDate.toLocalDate());
        }
        task.setStatus(rs.getString("Status"));
        return task;
    }
}
