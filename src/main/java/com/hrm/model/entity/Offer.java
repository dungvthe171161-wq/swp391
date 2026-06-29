package com.hrm.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Offer implements Serializable {
    private static final long serialVersionUID = 1L;

    private int offerId;
    private int applicationId;
    private String position;
    private BigDecimal offeredSalary;
    private LocalDate startDate;
    private LocalDateTime expiredAt;
    private String status;
    private LocalDateTime sentAt;
    private LocalDateTime respondedAt;
    private String note;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    public int getOfferId() {
        return offerId;
    }

    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getOfferedSalary() {
        return offeredSalary;
    }

    public void setOfferedSalary(BigDecimal offeredSalary) {
        this.offeredSalary = offeredSalary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(LocalDateTime expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }

    public LocalDateTime getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(LocalDateTime respondedAt) {
        this.respondedAt = respondedAt;
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
            case "Draft" -> "Bản nháp";
            case "Sent" -> "Đã gửi";
            case "Accepted" -> "Đã chấp nhận";
            case "Rejected" -> "Đã từ chối";
            case "Expired" -> "Đã hết hạn";
            case "Cancelled" -> "Đã hủy";
            default -> status;
        };
    }
}
