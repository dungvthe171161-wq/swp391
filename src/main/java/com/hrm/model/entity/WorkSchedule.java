package com.hrm.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class WorkSchedule {
    private int scheduleId;
    private String scheduleCode;
    private String scheduleName;
    private LocalTime startTime;
    private LocalTime endTime;
    private int breakMinutes;
    private BigDecimal workingHours;
    private int graceLateMinutes;
    private int graceEarlyLeaveMinutes;
    private boolean active;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public WorkSchedule() {
    }

    public WorkSchedule(int scheduleId, String scheduleCode, String scheduleName, LocalTime startTime,
                        LocalTime endTime, int breakMinutes, BigDecimal workingHours, int graceLateMinutes,
                        int graceEarlyLeaveMinutes, boolean active, Integer createdBy,
                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.scheduleId = scheduleId;
        this.scheduleCode = scheduleCode;
        this.scheduleName = scheduleName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.breakMinutes = breakMinutes;
        this.workingHours = workingHours;
        this.graceLateMinutes = graceLateMinutes;
        this.graceEarlyLeaveMinutes = graceEarlyLeaveMinutes;
        this.active = active;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getScheduleId() { return scheduleId; }
    public void setScheduleId(int scheduleId) { this.scheduleId = scheduleId; }
    public String getScheduleCode() { return scheduleCode; }
    public void setScheduleCode(String scheduleCode) { this.scheduleCode = scheduleCode; }
    public String getScheduleName() { return scheduleName; }
    public void setScheduleName(String scheduleName) { this.scheduleName = scheduleName; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public int getBreakMinutes() { return breakMinutes; }
    public void setBreakMinutes(int breakMinutes) { this.breakMinutes = breakMinutes; }
    public BigDecimal getWorkingHours() { return workingHours; }
    public void setWorkingHours(BigDecimal workingHours) { this.workingHours = workingHours; }
    public int getGraceLateMinutes() { return graceLateMinutes; }
    public void setGraceLateMinutes(int graceLateMinutes) { this.graceLateMinutes = graceLateMinutes; }
    public int getGraceEarlyLeaveMinutes() { return graceEarlyLeaveMinutes; }
    public void setGraceEarlyLeaveMinutes(int graceEarlyLeaveMinutes) { this.graceEarlyLeaveMinutes = graceEarlyLeaveMinutes; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public Integer getCreatedBy() { return createdBy; }
    public void setCreatedBy(Integer createdBy) { this.createdBy = createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
