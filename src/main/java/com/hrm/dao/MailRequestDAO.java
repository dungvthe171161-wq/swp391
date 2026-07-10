/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hrm.dao;

import com.hrm.model.entity.MailRequest;
import com.hrm.dao.DBConnection;
import java.sql.*;
import java.util.*;

public class MailRequestDAO {
    private static final int DEFAULT_PAID_LEAVE_SESSIONS = 24;

    public boolean insert(MailRequest r) {
        String sql = """
            INSERT INTO MailRequest (EmployeeID, RequestType, LeaveType, StartDate, EndDate, Reason, Status)
            VALUES (?, ?, ?, ?, ?, ?, 'Pending')
        """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, r.getEmployeeId());
            ps.setString(2, r.getRequestType());
            ps.setString(3, r.getLeaveType());
            ps.setObject(4, r.getStartDate());
            ps.setObject(5, r.getEndDate());
            ps.setString(6, r.getReason());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int requestId, String status, int approverId) {
        String sql = "UPDATE MailRequest SET Status=?, ApprovedBy=? WHERE RequestID=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, approverId);
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<MailRequest> getByEmployee(int empId) {
        List<MailRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM MailRequest WHERE EmployeeID=? ORDER BY RequestID DESC";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MailRequest r = new MailRequest();
                r.setRequestId(rs.getInt("RequestID"));
                r.setEmployeeId(empId);
                r.setRequestType(rs.getString("RequestType"));
                r.setLeaveType(rs.getString("LeaveType"));
                java.sql.Date startDate = rs.getDate("StartDate");
                java.sql.Date endDate = rs.getDate("EndDate");
                if (startDate != null) {
                    r.setStartDate(startDate.toLocalDate());
                }
                if (endDate != null) {
                    r.setEndDate(endDate.toLocalDate());
                }
                r.setReason(rs.getString("Reason"));
                r.setStatus(rs.getString("Status"));
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<MailRequest> getLeavesByEmployee(int empId) {
        List<MailRequest> list = new ArrayList<>();
        String sql = """
            SELECT * FROM MailRequest
            WHERE EmployeeID = ? AND RequestType = 'Leave'
            ORDER BY RequestID DESC
        """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    MailRequest r = new MailRequest();
                    r.setRequestId(rs.getInt("RequestID"));
                    r.setEmployeeId(empId);
                    r.setRequestType(rs.getString("RequestType"));
                    r.setLeaveType(rs.getString("LeaveType"));
                    java.sql.Date startDate = rs.getDate("StartDate");
                    java.sql.Date endDate = rs.getDate("EndDate");
                    if (startDate != null) {
                        r.setStartDate(startDate.toLocalDate());
                    }
                    if (endDate != null) {
                        r.setEndDate(endDate.toLocalDate());
                    }
                    r.setReason(rs.getString("Reason"));
                    r.setStatus(rs.getString("Status"));
                    Object approvedBy = rs.getObject("ApprovedBy");
                    if (approvedBy != null) {
                        r.setApprovedBy((Integer) approvedBy);
                    }
                    list.add(r);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getApprovedLeaveDays(int empId, int year) {
        String sql = """
            SELECT COALESCE(SUM(DATEDIFF(EndDate, StartDate) + 1), 0) AS UsedDays
            FROM MailRequest
            WHERE EmployeeID = ?
              AND RequestType = 'Leave'
              AND Status = 'Approved'
              AND LeaveType IN ('Annual', 'Sick', 'Maternity')
              AND YEAR(StartDate) = ?
        """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UsedDays");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getDefaultPaidLeaveSessions() {
        return DEFAULT_PAID_LEAVE_SESSIONS;
    }

    public int getBookedPaidLeaveSessions(int empId, int year) {
        String sql = """
            SELECT COALESCE(SUM(
                (DATEDIFF(EndDate, StartDate) + 1) *
                CASE
                    WHEN Reason LIKE '%Nghi buoi sang%' OR Reason LIKE '%Nghi buoi chieu%' THEN 1
                    ELSE 2
                END
            ), 0) AS UsedSessions
            FROM MailRequest
            WHERE EmployeeID = ?
              AND RequestType = 'Leave'
              AND Status IN ('Pending', 'Approved')
              AND LeaveType IN ('Annual', 'Sick', 'Maternity')
              AND YEAR(StartDate) = ?
        """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setInt(2, year);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("UsedSessions");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean hasOverlappingActiveLeave(int empId, java.time.LocalDate startDate, java.time.LocalDate endDate) {
        String sql = """
            SELECT COUNT(*) AS Total
            FROM MailRequest
            WHERE EmployeeID = ?
              AND RequestType = 'Leave'
              AND Status IN ('Pending', 'Approved')
              AND StartDate <= ?
              AND EndDate >= ?
        """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
            ps.setObject(2, endDate);
            ps.setObject(3, startDate);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt("Total") > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public int countPendingLeavesByEmployee(int empId) {
        String sql = """
            SELECT COUNT(*) AS Total
            FROM MailRequest
            WHERE EmployeeID = ? AND RequestType = 'Leave' AND Status = 'Pending'
        """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, empId);
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

    public List<Map<String, Object>> getAllLeaveRequests(String status) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT mr.RequestID, mr.EmployeeID, e.FullName, e.Email,
                   mr.LeaveType, mr.StartDate, mr.EndDate, mr.Reason,
                   mr.Status, mr.ApprovedBy
            FROM MailRequest mr
            JOIN Employee e ON e.EmployeeID = mr.EmployeeID
            WHERE mr.RequestType = 'Leave'
        """);
        List<Object> params = new ArrayList<>();
        if (status != null && !status.isBlank() && !"All".equals(status)) {
            sql.append(" AND mr.Status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY mr.RequestID DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("requestId", rs.getInt("RequestID"));
                    row.put("employeeId", rs.getInt("EmployeeID"));
                    row.put("employeeName", rs.getString("FullName"));
                    row.put("employeeEmail", rs.getString("Email"));
                    row.put("leaveType", rs.getString("LeaveType"));
                    row.put("startDate", rs.getDate("StartDate"));
                    row.put("endDate", rs.getDate("EndDate"));
                    row.put("reason", rs.getString("Reason"));
                    row.put("status", rs.getString("Status"));
                    row.put("approvedBy", rs.getObject("ApprovedBy"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateLeaveStatus(int requestId, String status, int approverId) {
        if (!List.of("Approved", "Rejected").contains(status)) {
            return false;
        }
        String sql = """
            UPDATE MailRequest
            SET Status = ?, ApprovedBy = ?
            WHERE RequestID = ? AND RequestType = 'Leave' AND Status = 'Pending'
        """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, approverId);
            ps.setInt(3, requestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Map<String, Object>> getLeaveRequestsByDepartment(int departmentId, String status) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT mr.RequestID, mr.EmployeeID, e.FullName, e.Email,
                   mr.LeaveType, mr.StartDate, mr.EndDate, mr.Reason,
                   mr.Status, mr.ApprovedBy
            FROM MailRequest mr
            JOIN Employee e ON e.EmployeeID = mr.EmployeeID
            WHERE mr.RequestType = 'Leave'
              AND e.DepartmentID = ?
        """);
        List<Object> params = new ArrayList<>();
        params.add(departmentId);
        if (status != null && !status.isBlank() && !"All".equalsIgnoreCase(status)) {
            sql.append(" AND mr.Status = ?");
            params.add(status);
        }
        sql.append(" ORDER BY mr.RequestID DESC");

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("requestId", rs.getInt("RequestID"));
                    row.put("employeeId", rs.getInt("EmployeeID"));
                    row.put("employeeName", rs.getString("FullName"));
                    row.put("employeeEmail", rs.getString("Email"));
                    row.put("leaveType", rs.getString("LeaveType"));
                    row.put("startDate", rs.getDate("StartDate"));
                    row.put("endDate", rs.getDate("EndDate"));
                    row.put("reason", rs.getString("Reason"));
                    row.put("status", rs.getString("Status"));
                    row.put("approvedBy", rs.getObject("ApprovedBy"));
                    list.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean updateLeaveStatusByDepartment(int requestId, int departmentId, String status, int approverId) {
        if (!List.of("Approved", "Rejected").contains(status)) {
            return false;
        }
        String sql = """
            UPDATE MailRequest mr
            JOIN Employee e ON e.EmployeeID = mr.EmployeeID
            SET mr.Status = ?, mr.ApprovedBy = ?
            WHERE mr.RequestID = ?
              AND mr.RequestType = 'Leave'
              AND mr.Status = 'Pending'
              AND e.DepartmentID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, approverId);
            ps.setInt(3, requestId);
            ps.setInt(4, departmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
