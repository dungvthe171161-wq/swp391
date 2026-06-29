/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hrm.dao;

import com.hrm.model.entity.Attendance;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {

    public Attendance getTodayAttendance(int employeeId) {
        return getByDate(employeeId, LocalDate.now());
    }

    public Attendance getByDate(int employeeId, LocalDate date) {
        String sql = """
            SELECT AttendanceID, EmployeeID, Date, CheckIn, CheckOut, WorkingHours, OvertimeHours
            FROM Attendance
            WHERE EmployeeID = ? AND Date = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setDate(2, Date.valueOf(date));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAttendance(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean checkIn(int employeeId) {
        Attendance today = getTodayAttendance(employeeId);
        if (today != null && today.getCheckIn() != null) {
            return false;
        }

        String sql = """
            INSERT INTO Attendance (EmployeeID, Date, CheckIn, WorkingHours, OvertimeHours)
            VALUES (?, CURDATE(), CURTIME(), 0, 0)
            ON DUPLICATE KEY UPDATE CheckIn = COALESCE(CheckIn, VALUES(CheckIn))
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkOut(int employeeId) {
        Attendance today = getTodayAttendance(employeeId);
        if (today == null || today.getCheckIn() == null || today.getCheckOut() != null) {
            return false;
        }

        LocalTime checkOut = LocalTime.now();
        BigDecimal workingHours = calculateWorkingHours(today.getCheckIn(), checkOut);
        BigDecimal overtimeHours = workingHours.subtract(BigDecimal.valueOf(8));
        if (overtimeHours.compareTo(BigDecimal.ZERO) < 0) {
            overtimeHours = BigDecimal.ZERO;
        }

        String sql = """
            UPDATE Attendance
            SET CheckOut = ?, WorkingHours = ?, OvertimeHours = ?
            WHERE EmployeeID = ? AND Date = CURDATE() AND CheckIn IS NOT NULL AND CheckOut IS NULL
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTime(1, Time.valueOf(checkOut));
            ps.setBigDecimal(2, workingHours);
            ps.setBigDecimal(3, overtimeHours);
            ps.setInt(4, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Attendance> getRecentByEmployee(int employeeId, int limit) {
        List<Attendance> list = new ArrayList<>();
        String sql = """
            SELECT AttendanceID, EmployeeID, Date, CheckIn, CheckOut, WorkingHours, OvertimeHours
            FROM Attendance
            WHERE EmployeeID = ?
            ORDER BY Date DESC
            LIMIT ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapAttendance(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Object> getMonthlySummary(int employeeId, int year, int month) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalWorkingHours", BigDecimal.ZERO);
        summary.put("totalOvertimeHours", BigDecimal.ZERO);
        summary.put("lateCount", 0);
        summary.put("earlyLeaveCount", 0);
        summary.put("workedDays", 0);

        String sql = """
            SELECT
                COALESCE(SUM(WorkingHours), 0) AS TotalWorkingHours,
                COALESCE(SUM(OvertimeHours), 0) AS TotalOvertimeHours,
                SUM(CASE WHEN CheckIn IS NOT NULL AND CheckIn > '08:00:00' THEN 1 ELSE 0 END) AS LateCount,
                SUM(CASE WHEN CheckOut IS NOT NULL AND CheckOut < '17:00:00' THEN 1 ELSE 0 END) AS EarlyLeaveCount,
                COUNT(*) AS WorkedDays
            FROM Attendance
            WHERE EmployeeID = ?
              AND YEAR(Date) = ?
              AND MONTH(Date) = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    summary.put("totalWorkingHours", rs.getBigDecimal("TotalWorkingHours"));
                    summary.put("totalOvertimeHours", rs.getBigDecimal("TotalOvertimeHours"));
                    summary.put("lateCount", rs.getInt("LateCount"));
                    summary.put("earlyLeaveCount", rs.getInt("EarlyLeaveCount"));
                    summary.put("workedDays", rs.getInt("WorkedDays"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return summary;
    }

    public String getTodayStatus(int employeeId) {
        Attendance today = getTodayAttendance(employeeId);
        if (today == null || today.getCheckIn() == null) {
            return "ChuaVaoCa";
        }
        if (today.getCheckOut() == null) {
            return "DaVaoCa";
        }
        return "DaRaCa";
    }

    private BigDecimal calculateWorkingHours(LocalTime checkIn, LocalTime checkOut) {
        long minutes = Math.max(0, Duration.between(checkIn, checkOut).toMinutes());
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("AttendanceID"));
        attendance.setEmployeeId(rs.getInt("EmployeeID"));
        Date date = rs.getDate("Date");
        if (date != null) {
            attendance.setDate(date.toLocalDate());
        }
        Time checkIn = rs.getTime("CheckIn");
        if (checkIn != null) {
            attendance.setCheckIn(checkIn.toLocalTime());
        }
        Time checkOut = rs.getTime("CheckOut");
        if (checkOut != null) {
            attendance.setCheckOut(checkOut.toLocalTime());
        }
        attendance.setWorkingHours(rs.getBigDecimal("WorkingHours"));
        attendance.setOvertimeHours(rs.getBigDecimal("OvertimeHours"));
        return attendance;
    }
}
