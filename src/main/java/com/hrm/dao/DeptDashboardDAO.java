package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DeptDashboardDAO {

    public Map<String, Integer> getDashboardCounts(int departmentId, int managerEmployeeId) {
        Map<String, Integer> counts = new HashMap<>();
        counts.put("totalEmployees", countEmployees(departmentId, null));
        counts.put("activeEmployees", countEmployees(departmentId, "Active"));
        counts.put("pendingLeaves", countPendingLeaves(departmentId));
        counts.put("totalTasks", countTasks(managerEmployeeId, null));
        counts.put("completedTasks", countTasks(managerEmployeeId, "Completed"));
        counts.put("waitingTasks", countTasks(managerEmployeeId, "Waiting"));
        counts.put("inProgressTasks", countTasks(managerEmployeeId, "In Progress"));
        counts.put("overdueTasks", countOverdueTasks(managerEmployeeId));
        return counts;
    }

    private int countEmployees(int departmentId, String status) {
        String sql = status == null
                ? "SELECT COUNT(*) FROM Employee WHERE DepartmentID = ?"
                : "SELECT COUNT(*) FROM Employee WHERE DepartmentID = ? AND Status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            if (status != null) {
                ps.setString(2, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countPendingLeaves(int departmentId) {
        String sql = """
            SELECT COUNT(*)
            FROM MailRequest mr
            JOIN Employee e ON e.EmployeeID = mr.EmployeeID
            WHERE mr.RequestType = 'Leave'
              AND mr.Status = 'Pending'
              AND e.DepartmentID = ?
        """;
        return countOneInt(sql, departmentId);
    }

    private int countTasks(int managerEmployeeId, String status) {
        String sql = status == null
                ? "SELECT COUNT(*) FROM Task WHERE AssignedBy = ?"
                : "SELECT COUNT(*) FROM Task WHERE AssignedBy = ? AND Status = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, managerEmployeeId);
            if (status != null) {
                ps.setString(2, status);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int countOverdueTasks(int managerEmployeeId) {
        String sql = """
            SELECT COUNT(*)
            FROM Task
            WHERE AssignedBy = ?
              AND DueDate < CURRENT_DATE()
              AND Status NOT IN ('Completed', 'Rejected')
        """;
        return countOneInt(sql, managerEmployeeId);
    }

    private int countOneInt(String sql, int value) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
