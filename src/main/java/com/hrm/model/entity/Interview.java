package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Interview implements Serializable {
    private static final long serialVersionUID = 1L;

    private int interviewId;
    private int applicationId;
    private int roundNo;
    private LocalDateTime scheduledAt;
    private String location;
    private String meetingLink;
    private Integer interviewerEmployeeId;
    private String status;
    private String result;
    private String note;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public int getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(int interviewId) {
        this.interviewId = interviewId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getRoundNo() {
        return roundNo;
    }

    public void setRoundNo(int roundNo) {
        this.roundNo = roundNo;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public void setScheduledAt(LocalDateTime scheduledAt) {
        this.scheduledAt = scheduledAt;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public void setMeetingLink(String meetingLink) {
        this.meetingLink = meetingLink;
    }

    public Integer getInterviewerEmployeeId() {
        return interviewerEmployeeId;
    }

    public void setInterviewerEmployeeId(Integer interviewerEmployeeId) {
        this.interviewerEmployeeId = interviewerEmployeeId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getStatusLabel() {
        if (status == null || status.isBlank()) {
            return "Chưa xác định";
        }
        return switch (status) {
            case "Scheduled" -> "Đã lên lịch";
            case "Completed" -> "Đã hoàn thành";
            case "Cancelled" -> "Đã hủy";
            case "NoShow" -> "Không tham dự";
            case "Rescheduled" -> "Đã đổi lịch";
            default -> status;
        };
    }
}
