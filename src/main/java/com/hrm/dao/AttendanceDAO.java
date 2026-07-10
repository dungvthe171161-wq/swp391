package com.hrm.dao;

import com.hrm.model.entity.Attendance;
import com.hrm.model.entity.EmployeeWorkSchedule;
import com.hrm.model.entity.OfficeLocation;
import com.hrm.util.GeoUtil;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Types;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AttendanceDAO {
    private static final String ATTENDANCE_COLUMNS = """
        AttendanceID, EmployeeID, Date, CheckIn, CheckOut, WorkingHours, OvertimeHours,
        ScheduleID, CheckInLatitude, CheckInLongitude, CheckInDistanceMeters,
        CheckInOfficeLocationID, CheckInMethod, CheckOutLatitude, CheckOutLongitude,
        CheckOutDistanceMeters, CheckOutOfficeLocationID, CheckOutMethod
    """;

    public Attendance getTodayAttendance(int employeeId) {
        return getByDate(employeeId, LocalDate.now());
    }

    public Attendance getByDate(int employeeId, LocalDate date) {
        String sql = "SELECT " + ATTENDANCE_COLUMNS + " FROM Attendance WHERE EmployeeID = ? AND Date = ?";
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

    public boolean checkInWithGps(int employeeId, double latitude, double longitude) {
        if (!GeoUtil.isValidLatitude(latitude) || !GeoUtil.isValidLongitude(longitude)) {
            return false;
        }
        Attendance today = getTodayAttendance(employeeId);
        if (today != null && today.getCheckIn() != null) {
            return false;
        }
        OfficeLocation office = new OfficeLocationDAO().getNearestActiveLocation(latitude, longitude);
        if (office == null) {
            return false;
        }
        double distance = GeoUtil.distanceMeters(latitude, longitude,
                office.getLatitude().doubleValue(), office.getLongitude().doubleValue());
        if (!GeoUtil.isWithinRadius(distance, office.getRadiusMeters())) {
            return false;
        }
        EmployeeWorkSchedule todaySchedule = new WorkScheduleDAO().getByEmployeeAndDate(employeeId, LocalDate.now());
        Integer scheduleId = todaySchedule != null ? todaySchedule.getScheduleId() : null;
        String sql = """
            INSERT INTO Attendance (
                EmployeeID, Date, CheckIn, WorkingHours, OvertimeHours, ScheduleID,
                CheckInLatitude, CheckInLongitude, CheckInDistanceMeters, CheckInOfficeLocationID, CheckInMethod
            )
            VALUES (?, CURDATE(), CURTIME(), 0, 0, ?, ?, ?, ?, ?, 'GPS')
            ON DUPLICATE KEY UPDATE
                ScheduleID = IF(CheckIn IS NULL, VALUES(ScheduleID), ScheduleID),
                CheckInLatitude = IF(CheckIn IS NULL, VALUES(CheckInLatitude), CheckInLatitude),
                CheckInLongitude = IF(CheckIn IS NULL, VALUES(CheckInLongitude), CheckInLongitude),
                CheckInDistanceMeters = IF(CheckIn IS NULL, VALUES(CheckInDistanceMeters), CheckInDistanceMeters),
                CheckInOfficeLocationID = IF(CheckIn IS NULL, VALUES(CheckInOfficeLocationID), CheckInOfficeLocationID),
                CheckInMethod = IF(CheckIn IS NULL, VALUES(CheckInMethod), CheckInMethod),
                CheckIn = COALESCE(CheckIn, VALUES(CheckIn))
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            setNullableInteger(ps, 2, scheduleId);
            ps.setBigDecimal(3, BigDecimal.valueOf(latitude));
            ps.setBigDecimal(4, BigDecimal.valueOf(longitude));
            ps.setBigDecimal(5, toDistanceDecimal(distance));
            ps.setInt(6, office.getOfficeLocationId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkOutWithGps(int employeeId, double latitude, double longitude) {
        if (!GeoUtil.isValidLatitude(latitude) || !GeoUtil.isValidLongitude(longitude)) {
            return false;
        }
        Attendance today = getTodayAttendance(employeeId);
        if (today == null || today.getCheckIn() == null || today.getCheckOut() != null) {
            return false;
        }
        OfficeLocation office = new OfficeLocationDAO().getNearestActiveLocation(latitude, longitude);
        if (office == null) {
            return false;
        }
        double distance = GeoUtil.distanceMeters(latitude, longitude,
                office.getLatitude().doubleValue(), office.getLongitude().doubleValue());
        if (!GeoUtil.isWithinRadius(distance, office.getRadiusMeters())) {
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
            SET CheckOut = ?, WorkingHours = ?, OvertimeHours = ?,
                CheckOutLatitude = ?, CheckOutLongitude = ?, CheckOutDistanceMeters = ?,
                CheckOutOfficeLocationID = ?, CheckOutMethod = 'GPS'
            WHERE EmployeeID = ? AND Date = CURDATE() AND CheckIn IS NOT NULL AND CheckOut IS NULL
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTime(1, Time.valueOf(checkOut));
            ps.setBigDecimal(2, workingHours);
            ps.setBigDecimal(3, overtimeHours);
            ps.setBigDecimal(4, BigDecimal.valueOf(latitude));
            ps.setBigDecimal(5, BigDecimal.valueOf(longitude));
            ps.setBigDecimal(6, toDistanceDecimal(distance));
            ps.setInt(7, office.getOfficeLocationId());
            ps.setInt(8, employeeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Attendance> getRecentByEmployee(int employeeId, int limit) {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT " + ATTENDANCE_COLUMNS + " FROM Attendance WHERE EmployeeID = ? ORDER BY Date DESC LIMIT ?";
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
            SELECT COALESCE(SUM(WorkingHours), 0) AS TotalWorkingHours,
                   COALESCE(SUM(OvertimeHours), 0) AS TotalOvertimeHours,
                   SUM(CASE WHEN CheckIn IS NOT NULL AND CheckIn > '08:00:00' THEN 1 ELSE 0 END) AS LateCount,
                   SUM(CASE WHEN CheckOut IS NOT NULL AND CheckOut < '17:00:00' THEN 1 ELSE 0 END) AS EarlyLeaveCount,
                   COUNT(*) AS WorkedDays
            FROM Attendance
            WHERE EmployeeID = ? AND YEAR(Date) = ? AND MONTH(Date) = ?
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
        return BigDecimal.valueOf(minutes).divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal toDistanceDecimal(double distance) {
        return BigDecimal.valueOf(distance).setScale(2, RoundingMode.HALF_UP);
    }

    private void setNullableInteger(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private Attendance mapAttendance(ResultSet rs) throws SQLException {
        Attendance attendance = new Attendance();
        attendance.setAttendanceId(rs.getInt("AttendanceID"));
        attendance.setEmployeeId(rs.getInt("EmployeeID"));
        Date date = rs.getDate("Date");
        if (date != null) attendance.setDate(date.toLocalDate());
        Time checkIn = rs.getTime("CheckIn");
        if (checkIn != null) attendance.setCheckIn(checkIn.toLocalTime());
        Time checkOut = rs.getTime("CheckOut");
        if (checkOut != null) attendance.setCheckOut(checkOut.toLocalTime());
        attendance.setWorkingHours(rs.getBigDecimal("WorkingHours"));
        attendance.setOvertimeHours(rs.getBigDecimal("OvertimeHours"));
        int scheduleId = rs.getInt("ScheduleID");
        attendance.setScheduleId(rs.wasNull() ? null : scheduleId);
        attendance.setCheckInLatitude(rs.getBigDecimal("CheckInLatitude"));
        attendance.setCheckInLongitude(rs.getBigDecimal("CheckInLongitude"));
        attendance.setCheckInDistanceMeters(rs.getBigDecimal("CheckInDistanceMeters"));
        int checkInOfficeLocationId = rs.getInt("CheckInOfficeLocationID");
        attendance.setCheckInOfficeLocationId(rs.wasNull() ? null : checkInOfficeLocationId);
        attendance.setCheckInMethod(rs.getString("CheckInMethod"));
        attendance.setCheckOutLatitude(rs.getBigDecimal("CheckOutLatitude"));
        attendance.setCheckOutLongitude(rs.getBigDecimal("CheckOutLongitude"));
        attendance.setCheckOutDistanceMeters(rs.getBigDecimal("CheckOutDistanceMeters"));
        int checkOutOfficeLocationId = rs.getInt("CheckOutOfficeLocationID");
        attendance.setCheckOutOfficeLocationId(rs.wasNull() ? null : checkOutOfficeLocationId);
        attendance.setCheckOutMethod(rs.getString("CheckOutMethod"));
        return attendance;
    }
}
