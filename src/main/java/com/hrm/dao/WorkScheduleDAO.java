package com.hrm.dao;

import com.hrm.model.entity.EmployeeWorkSchedule;
import com.hrm.model.entity.WorkSchedule;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class WorkScheduleDAO {

    public List<WorkSchedule> getAllActiveSchedules() {
        List<WorkSchedule> list = new ArrayList<>();
        String sql = "SELECT * FROM WorkSchedule WHERE IsActive = 1 ORDER BY StartTime, ScheduleName";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapSchedule(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public WorkSchedule getScheduleById(int scheduleId) {
        String sql = "SELECT * FROM WorkSchedule WHERE ScheduleID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSchedule(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean createSchedule(WorkSchedule schedule) {
        String sql = """
            INSERT INTO WorkSchedule (ScheduleCode, ScheduleName, StartTime, EndTime, BreakMinutes,
                                      WorkingHours, GraceLateMinutes, GraceEarlyLeaveMinutes, IsActive, CreatedBy)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setScheduleParams(ps, schedule);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSchedule(WorkSchedule schedule) {
        String sql = """
            UPDATE WorkSchedule
            SET ScheduleCode = ?, ScheduleName = ?, StartTime = ?, EndTime = ?, BreakMinutes = ?,
                WorkingHours = ?, GraceLateMinutes = ?, GraceEarlyLeaveMinutes = ?, IsActive = ?, CreatedBy = ?
            WHERE ScheduleID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setScheduleParams(ps, schedule);
            ps.setInt(11, schedule.getScheduleId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deactivateSchedule(int scheduleId) {
        String sql = "UPDATE WorkSchedule SET IsActive = 0 WHERE ScheduleID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public EmployeeWorkSchedule getByEmployeeAndDate(int employeeId, LocalDate date) {
        String sql = employeeScheduleSelect() + " WHERE ews.EmployeeID = ? AND ews.WorkDate = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapEmployeeSchedule(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<EmployeeWorkSchedule> getByEmployeeMonth(int employeeId, int year, int month) {
        String sql = employeeScheduleSelect()
                + " WHERE ews.EmployeeID = ? AND YEAR(ews.WorkDate) = ? AND MONTH(ews.WorkDate) = ? ORDER BY ews.WorkDate";
        return queryEmployeeSchedules(sql, employeeId, year, month);
    }

    public List<EmployeeWorkSchedule> getByDepartmentMonth(int departmentId, int year, int month) {
        String sql = employeeScheduleSelect()
                + " WHERE e.DepartmentID = ? AND YEAR(ews.WorkDate) = ? AND MONTH(ews.WorkDate) = ? ORDER BY ews.WorkDate, e.FullName";
        return queryEmployeeSchedules(sql, departmentId, year, month);
    }

    public boolean assignSchedule(int employeeId, int scheduleId, LocalDate workDate, String note, int assignedBy) {
        String sql = """
            INSERT INTO EmployeeWorkSchedule (EmployeeID, ScheduleID, WorkDate, Note, AssignedBy)
            VALUES (?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE ScheduleID = VALUES(ScheduleID), Note = VALUES(Note),
                                    AssignedBy = VALUES(AssignedBy), UpdatedAt = CURRENT_TIMESTAMP
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, scheduleId);
            ps.setDate(3, Date.valueOf(workDate));
            ps.setString(4, blankToNull(note));
            ps.setInt(5, assignedBy);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateAssignmentScoped(int assignmentId, int departmentId, int scheduleId, LocalDate workDate, String note) {
        String sql = """
            UPDATE EmployeeWorkSchedule ews
            JOIN Employee e ON ews.EmployeeID = e.EmployeeID
            SET ews.ScheduleID = ?, ews.WorkDate = ?, ews.Note = ?, ews.UpdatedAt = CURRENT_TIMESTAMP
            WHERE ews.AssignmentID = ? AND e.DepartmentID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, scheduleId);
            ps.setDate(2, Date.valueOf(workDate));
            ps.setString(3, blankToNull(note));
            ps.setInt(4, assignmentId);
            ps.setInt(5, departmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deleteAssignmentScoped(int assignmentId, int departmentId) {
        String sql = """
            DELETE ews FROM EmployeeWorkSchedule ews
            JOIN Employee e ON ews.EmployeeID = e.EmployeeID
            WHERE ews.AssignmentID = ? AND e.DepartmentID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, assignmentId);
            ps.setInt(2, departmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean employeeBelongsToDepartment(int employeeId, int departmentId) {
        String sql = "SELECT 1 FROM Employee WHERE EmployeeID = ? AND DepartmentID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<EmployeeWorkSchedule> queryEmployeeSchedules(String sql, int id, int year, int month) {
        List<EmployeeWorkSchedule> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapEmployeeSchedule(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private String employeeScheduleSelect() {
        return """
            SELECT ews.AssignmentID, ews.EmployeeID, e.FullName AS EmployeeName,
                   e.DepartmentID, d.DeptName AS DepartmentName,
                   ews.ScheduleID, ws.ScheduleCode, ws.ScheduleName,
                   ews.WorkDate, ws.StartTime, ws.EndTime, ews.Note,
                   ews.AssignedBy, ews.CreatedAt, ews.UpdatedAt
            FROM EmployeeWorkSchedule ews
            JOIN Employee e ON ews.EmployeeID = e.EmployeeID
            LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
            JOIN WorkSchedule ws ON ews.ScheduleID = ws.ScheduleID
        """;
    }

    private WorkSchedule mapSchedule(ResultSet rs) throws SQLException {
        WorkSchedule schedule = new WorkSchedule();
        schedule.setScheduleId(rs.getInt("ScheduleID"));
        schedule.setScheduleCode(rs.getString("ScheduleCode"));
        schedule.setScheduleName(rs.getString("ScheduleName"));
        Time start = rs.getTime("StartTime");
        Time end = rs.getTime("EndTime");
        schedule.setStartTime(start != null ? start.toLocalTime() : null);
        schedule.setEndTime(end != null ? end.toLocalTime() : null);
        schedule.setBreakMinutes(rs.getInt("BreakMinutes"));
        schedule.setWorkingHours(rs.getBigDecimal("WorkingHours"));
        schedule.setGraceLateMinutes(rs.getInt("GraceLateMinutes"));
        schedule.setGraceEarlyLeaveMinutes(rs.getInt("GraceEarlyLeaveMinutes"));
        schedule.setActive(rs.getBoolean("IsActive"));
        int createdBy = rs.getInt("CreatedBy");
        schedule.setCreatedBy(rs.wasNull() ? null : createdBy);
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        schedule.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        schedule.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        return schedule;
    }

    private EmployeeWorkSchedule mapEmployeeSchedule(ResultSet rs) throws SQLException {
        EmployeeWorkSchedule item = new EmployeeWorkSchedule();
        item.setAssignmentId(rs.getInt("AssignmentID"));
        item.setEmployeeId(rs.getInt("EmployeeID"));
        item.setEmployeeName(rs.getString("EmployeeName"));
        int departmentId = rs.getInt("DepartmentID");
        item.setDepartmentId(rs.wasNull() ? null : departmentId);
        item.setDepartmentName(rs.getString("DepartmentName"));
        item.setScheduleId(rs.getInt("ScheduleID"));
        item.setScheduleCode(rs.getString("ScheduleCode"));
        item.setScheduleName(rs.getString("ScheduleName"));
        Date workDate = rs.getDate("WorkDate");
        item.setWorkDate(workDate != null ? workDate.toLocalDate() : null);
        Time start = rs.getTime("StartTime");
        Time end = rs.getTime("EndTime");
        item.setStartTime(start != null ? start.toLocalTime() : null);
        item.setEndTime(end != null ? end.toLocalTime() : null);
        item.setNote(rs.getString("Note"));
        int assignedBy = rs.getInt("AssignedBy");
        item.setAssignedBy(rs.wasNull() ? null : assignedBy);
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        item.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        item.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        return item;
    }

    private void setScheduleParams(PreparedStatement ps, WorkSchedule schedule) throws SQLException {
        ps.setString(1, blankToNull(schedule.getScheduleCode()));
        ps.setString(2, schedule.getScheduleName());
        ps.setTime(3, Time.valueOf(schedule.getStartTime()));
        ps.setTime(4, Time.valueOf(schedule.getEndTime()));
        ps.setInt(5, schedule.getBreakMinutes());
        ps.setBigDecimal(6, schedule.getWorkingHours());
        ps.setInt(7, schedule.getGraceLateMinutes());
        ps.setInt(8, schedule.getGraceEarlyLeaveMinutes());
        ps.setBoolean(9, schedule.isActive());
        if (schedule.getCreatedBy() == null) {
            ps.setNull(10, java.sql.Types.INTEGER);
        } else {
            ps.setInt(10, schedule.getCreatedBy());
        }
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
