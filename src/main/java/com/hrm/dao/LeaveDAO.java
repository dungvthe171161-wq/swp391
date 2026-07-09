package com.hrm.dao;

import com.hrm.model.entity.Leave; // hoặc tên class model thực tế trong project (Leave/LeaveApplication)
import com.hrm.dao.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LeaveDAO {

    /**
     * Insert a leave record.
     * Use DB timestamp (NOW()) so we don't need to set createdAt in Java.
     */
    public boolean insertLeave(Leave leave) {
        String sql = "INSERT INTO leave_applications (employee_id, start_date, end_date, reason, status, created_at) "
                   + "VALUES (?, ?, ?, ?, ?, NOW())";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, leave.getEmployeeId());
            // Assume getStartDate/getEndDate return java.util.Date or java.sql.Date
            if (leave.getStartDate() != null) {
                ps.setDate(2, new java.sql.Date(leave.getStartDate().getTime()));
            } else {
                ps.setDate(2, null);
            }
            if (leave.getEndDate() != null) {
                ps.setDate(3, new java.sql.Date(leave.getEndDate().getTime()));
            } else {
                ps.setDate(3, null);
            }
            ps.setString(4, leave.getReason());
            ps.setString(5, leave.getStatus() == null ? "Pending" : leave.getStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get leaves for an employee.
     * Note: we intentionally do not call setCreatedAt because the model may not have that setter.
     */
    public List<Leave> getLeavesByEmployee(int employeeId) {
        List<Leave> list = new ArrayList<>();
        String sql = "SELECT id, employee_id, start_date, end_date, reason, status FROM leave_applications "
                   + "WHERE employee_id = ? ORDER BY created_at DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Leave leave = new Leave();
                    leave.setId(rs.getInt("id"));
                    leave.setEmployeeId(rs.getInt("employee_id"));
                    leave.setStartDate(rs.getDate("start_date"));
                    leave.setEndDate(rs.getDate("end_date"));
                    leave.setReason(rs.getString("reason"));
                    leave.setStatus(rs.getString("status"));
                    // DO NOT call setCreatedAt here to avoid mismatch with model
                    list.add(leave);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Update status of a leave (approve/reject).
     */
    public boolean updateLeaveStatus(int leaveId, String status) {
        String sql = "UPDATE leave_applications SET status = ? WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, leaveId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get single leave by id.
     * We only populate fields that are very likely to exist on the model.
     */
    public Leave getLeaveById(int id) {
        String sql = "SELECT id, employee_id, start_date, end_date, reason, status FROM leave_applications WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Leave leave = new Leave();
                    leave.setId(rs.getInt("id"));
                    leave.setEmployeeId(rs.getInt("employee_id"));
                    leave.setStartDate(rs.getDate("start_date"));
                    leave.setEndDate(rs.getDate("end_date"));
                    leave.setReason(rs.getString("reason"));
                    leave.setStatus(rs.getString("status"));
                    // intentionally not setting createdAt to avoid calling non-existing setter
                    return leave;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
