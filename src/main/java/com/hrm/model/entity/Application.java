package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Application implements Serializable {
    private static final long serialVersionUID = 1L;

    private int applicationId;
    private int guestId;
    private int recruitmentId;
    private Integer candidateProfileId;
    private LocalDateTime appliedDate;
    private String status;
    private String currentStep;
    private String cv;
    private String coverLetter;
    private String note;
    private String source;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public int getRecruitmentId() {
        return recruitmentId;
    }

    public void setRecruitmentId(int recruitmentId) {
        this.recruitmentId = recruitmentId;
    }

    public Integer getCandidateProfileId() {
        return candidateProfileId;
    }

    public void setCandidateProfileId(Integer candidateProfileId) {
        this.candidateProfileId = candidateProfileId;
    }

    public LocalDateTime getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDateTime appliedDate) {
        this.appliedDate = appliedDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCurrentStep() {
        return currentStep;
    }

    public void setCurrentStep(String currentStep) {
        this.currentStep = currentStep;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
            case "Applied" -> "Đã nộp hồ sơ";
            case "Screening" -> "Đang sàng lọc";
            case "Interview" -> "Phỏng vấn";
            case "Offered" -> "Đã gửi offer";
            case "Rejected" -> "Từ chối";
            case "Withdrawn" -> "Đã rút hồ sơ";
            case "Hired" -> "Đã tuyển";
            default -> status;
        };
    }

    public String getStatusCssClass() {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case "Rejected", "Withdrawn" -> "rejected";
            case "Hired", "Offered" -> "hired";
            default -> "";
        };
    }
}
