package com.hrm.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Represents an employee's attendance record, including check-in/out times,
 * working hours, and overtime information.
 * 
 * @author admin
 */
public class Attendance implements Serializable {

    private static final long serialVersionUID = 1L;

    private int attendanceId;         // Primary key
    private Integer employeeId;       // Foreign key to Employee
    private LocalDate date;           // Attendance date
    private LocalTime checkIn;        // Check-in time
    private LocalTime checkOut;       // Check-out time
    private BigDecimal workingHours;  // Total working hours
    private BigDecimal overtimeHours; // Overtime hours if any
    private Integer scheduleId;
    private BigDecimal checkInLatitude;
    private BigDecimal checkInLongitude;
    private BigDecimal checkInDistanceMeters;
    private Integer checkInOfficeLocationId;
    private String checkInMethod;
    private BigDecimal checkOutLatitude;
    private BigDecimal checkOutLongitude;
    private BigDecimal checkOutDistanceMeters;
    private Integer checkOutOfficeLocationId;
    private String checkOutMethod;

    public Attendance() {
    }

    public Attendance(int attendanceId, Integer employeeId, LocalDate date, LocalTime checkIn,
                      LocalTime checkOut, BigDecimal workingHours, BigDecimal overtimeHours) {
        this.attendanceId = attendanceId;
        this.employeeId = employeeId;
        this.date = date;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.workingHours = workingHours;
        this.overtimeHours = overtimeHours;
    }

    // --- Getters & Setters ---
    public int getAttendanceId() {
        return attendanceId;
    }

    public void setAttendanceId(int attendanceId) {
        this.attendanceId = attendanceId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalTime checkIn) {
        this.checkIn = checkIn;
    }

    public LocalTime getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalTime checkOut) {
        this.checkOut = checkOut;
    }

    public BigDecimal getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(BigDecimal workingHours) {
        this.workingHours = workingHours;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public Integer getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(Integer scheduleId) {
        this.scheduleId = scheduleId;
    }

    public BigDecimal getCheckInLatitude() {
        return checkInLatitude;
    }

    public void setCheckInLatitude(BigDecimal checkInLatitude) {
        this.checkInLatitude = checkInLatitude;
    }

    public BigDecimal getCheckInLongitude() {
        return checkInLongitude;
    }

    public void setCheckInLongitude(BigDecimal checkInLongitude) {
        this.checkInLongitude = checkInLongitude;
    }

    public BigDecimal getCheckInDistanceMeters() {
        return checkInDistanceMeters;
    }

    public void setCheckInDistanceMeters(BigDecimal checkInDistanceMeters) {
        this.checkInDistanceMeters = checkInDistanceMeters;
    }

    public Integer getCheckInOfficeLocationId() {
        return checkInOfficeLocationId;
    }

    public void setCheckInOfficeLocationId(Integer checkInOfficeLocationId) {
        this.checkInOfficeLocationId = checkInOfficeLocationId;
    }

    public String getCheckInMethod() {
        return checkInMethod;
    }

    public void setCheckInMethod(String checkInMethod) {
        this.checkInMethod = checkInMethod;
    }

    public BigDecimal getCheckOutLatitude() {
        return checkOutLatitude;
    }

    public void setCheckOutLatitude(BigDecimal checkOutLatitude) {
        this.checkOutLatitude = checkOutLatitude;
    }

    public BigDecimal getCheckOutLongitude() {
        return checkOutLongitude;
    }

    public void setCheckOutLongitude(BigDecimal checkOutLongitude) {
        this.checkOutLongitude = checkOutLongitude;
    }

    public BigDecimal getCheckOutDistanceMeters() {
        return checkOutDistanceMeters;
    }

    public void setCheckOutDistanceMeters(BigDecimal checkOutDistanceMeters) {
        this.checkOutDistanceMeters = checkOutDistanceMeters;
    }

    public Integer getCheckOutOfficeLocationId() {
        return checkOutOfficeLocationId;
    }

    public void setCheckOutOfficeLocationId(Integer checkOutOfficeLocationId) {
        this.checkOutOfficeLocationId = checkOutOfficeLocationId;
    }

    public String getCheckOutMethod() {
        return checkOutMethod;
    }

    public void setCheckOutMethod(String checkOutMethod) {
        this.checkOutMethod = checkOutMethod;
    }

    @Override
    public String toString() {
        return "Attendance{" +
                "attendanceId=" + attendanceId +
                ", employeeId=" + employeeId +
                ", date=" + date +
                ", checkIn=" + checkIn +
                ", checkOut=" + checkOut +
                ", workingHours=" + workingHours +
                ", overtimeHours=" + overtimeHours +
                ", scheduleId=" + scheduleId +
                ", checkInDistanceMeters=" + checkInDistanceMeters +
                ", checkOutDistanceMeters=" + checkOutDistanceMeters +
                '}';
    }
}
