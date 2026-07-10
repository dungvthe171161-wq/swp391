package com.hrm.dao;

import static com.hrm.dao.DBConnection.getConnection;
import com.hrm.model.entity.*;
import com.hrm.model.dto.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;

public class DashboardDAO {

    public List<DepartmentStats> getEmployeeByDepartment() {
        List<DepartmentStats> result = new ArrayList<>();
        String sql = "SELECT d.DepartmentID, d.DeptName, COUNT(e.EmployeeID) as count " +
                     "FROM department d " +
                     "LEFT JOIN employee e ON d.DepartmentID = e.DepartmentID " +
                     "WHERE e.Status != 'Intern' " +
                     "GROUP BY d.DepartmentID, d.DeptName " +
                     "ORDER BY count DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                DepartmentStats stats = new DepartmentStats(
                    rs.getInt("DepartmentID"),
                    rs.getString("DeptName"),
                    rs.getInt("count")
                );
                result.add(stats);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<StatusDistribution> getEmployeeStatusDistribution() {
        List<StatusDistribution> result = new ArrayList<>();
        String sql = "SELECT status, COUNT(*) as count FROM employee WHERE status != 'Intern' GROUP BY status";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                StatusDistribution dist = new StatusDistribution(
                    rs.getString("status"),
                    rs.getInt("count")
                );
                result.add(dist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<SystemLog> getRecentActivity(int limit) {
        List<SystemLog> result = new ArrayList<>();
        String sql = "SELECT LogID, UserID, Action, ObjectType, OldValue, NewValue, Timestamp " +
                     "FROM SystemLog " +
                     "ORDER BY Timestamp DESC LIMIT ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    SystemLog log = new SystemLog();
                    log.setLogId(rs.getInt("LogID"));
                    log.setUserId(rs.getInt("UserID"));
                    log.setAction(rs.getString("Action"));
                    log.setObjectType(rs.getString("ObjectType"));
                    log.setOldValue(rs.getString("OldValue"));
                    log.setNewValue(rs.getString("NewValue"));
                    Timestamp ts = rs.getTimestamp("Timestamp");
                    if (ts != null) {
                        log.setTimestamp(ts.toLocalDateTime());
                        log.setTimestampDate(java.util.Date.from(ts.toLocalDateTime().atZone(java.time.ZoneId.systemDefault()).toInstant()));
                    } else {
                        log.setTimestamp(null);
                        log.setTimestampDate(null);
                    }
                    result.add(log);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<ActivityStats> getActivityLast7Days() {
        return getActivityByDays(7);
    }

    /**
     * Get activity statistics for the last N days
     * @param days Number of days to retrieve (7, 14, 30, etc.)
     * @return List of ActivityStats with date and count
     */
    public List<ActivityStats> getActivityByDays(int days) {
        List<ActivityStats> result = new ArrayList<>();
        String sql = "SELECT DATE(Timestamp) as date, COUNT(*) as count " +
                     "FROM SystemLog " +
                     "WHERE Timestamp >= DATE_SUB(NOW(), INTERVAL ? DAY) " +
                     "GROUP BY DATE(Timestamp) " +
                     "ORDER BY date ASC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, days);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    LocalDate date = rs.getDate("date").toLocalDate();
                    int count = rs.getInt("count");
                    ActivityStats stats = new ActivityStats(date, count);
                    result.add(stats);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
