package com.hrm.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class EmployeeWorkSchedule {
    private int assignmentId;
    private int employeeId;
    private String employeeName;
    private Integer departmentId;
    private String departmentName;
    private int scheduleId;
    private String scheduleCode;
    private String scheduleName;
    private LocalDate workDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String note;
    private Integer assignedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeWorkSchedule() {
    }

    public EmployeeWorkSchedule(int assignmentId, int employeeId, String employeeName, Integer departmentId,
                                String departmentName, int scheduleId, String scheduleCode, String scheduleName,
                                LocalDate workDate, LocalTime startTime, LocalTime endTime, String note,
                                Integer assignedBy, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.assignmentId = assignmentId;
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.scheduleId = scheduleId;
        this.scheduleCode = scheduleCode;
        this.scheduleName = scheduleName;
        this.workDate = workDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.note = note;
        this.assignedBy = assignedBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getAssignmentId() { return assignmentId; }
    public void setAssignmentId(int assignmentId) { this.assignmentId = assignmentId; }
    public int getEmployeeId() { return employeeId; }
    public void setEmployeeId(int employeeId) { this.employeeId = employeeId; }
    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
    public Integer getDepartmentId() { return departmentId; }
    public void setDepartmentId(Integer departmentId) { this.departmentId = departmentId; }
    public String getDepartmentName() { return departmentName; }
    public void setDepartmentName(String departmentName) { this.departmentName = departmentName; }
    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }
    public String getScheduleCode() { return scheduleCode; }
    public void setScheduleCode(String scheduleCode) { this.scheduleCode = scheduleCode; }
    public String getScheduleName() { return scheduleName; }
    public void setScheduleName(String scheduleName) { this.scheduleName = scheduleName; }
    public LocalDate getWorkDate() { return workDate; }
    public void setWorkDate(LocalDate workDate) { this.workDate = workDate; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public Integer getAssignedBy() { return assignedBy; }
    public void setAssignedBy(Integer assignedBy) { this.assignedBy = assignedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
