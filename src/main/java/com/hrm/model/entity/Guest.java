
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 *
 * @author admin
 */
public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;

    private int guestId;
    private Integer userId;
    private String fullName;
    private String email;
    private String phone;
    private String cv;
    private String avatar;
    private String gender;
    private LocalDate dateOfBirth;
    private String address;
    private String status;
    private Integer recruitmentId;
    private LocalDateTime appliedDate;
    private LocalDateTime updatedDate;

    public Guest() {}

    public Guest(int guestId, String fullName, String email, String phone, String cv,
                 String status, Integer recruitmentId, LocalDateTime appliedDate) {
        this.guestId = guestId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.cv = cv;
        this.status = status;
        this.recruitmentId = recruitmentId;
        this.appliedDate = appliedDate;
    }

    public Guest(int guestId, Integer userId, String fullName, String email, String phone, String cv,
                 String avatar, String gender, LocalDate dateOfBirth, String address,
                 String status, Integer recruitmentId, LocalDateTime appliedDate,
                 LocalDateTime updatedDate) {
        this.guestId = guestId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.cv = cv;
        this.avatar = avatar;
        this.gender = gender;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.status = status;
        this.recruitmentId = recruitmentId;
        this.appliedDate = appliedDate;
        this.updatedDate = updatedDate;
    }

    public int getGuestId() {
        return guestId;
    }

    public void setGuestId(int guestId) {
        this.guestId = guestId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusLabel() {
        if (status == null || status.isBlank()) {
            return "Chưa xác định";
        }
        return switch (status) {
            case "Processing" -> "Đang xử lý";
            case "Hired" -> "Đã duyệt";
            case "Rejected" -> "Từ chối";
            default -> status;
        };
    }

    public Integer getRecruitmentId() {
        return recruitmentId;
    }

    public void setRecruitmentId(Integer recruitmentId) {
        this.recruitmentId = recruitmentId;
    }

    public LocalDateTime getAppliedDate() {
        return appliedDate;
    }

    public void setAppliedDate(LocalDateTime appliedDate) {
        this.appliedDate = appliedDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    @Override
    public String toString() {
        return "Guest{"
                + "guestId=" + guestId
                + ", userId=" + userId
                + ", fullName=" + fullName
                + ", email=" + email
                + ", phone=" + phone
                + ", cv=" + cv
                + ", avatar=" + avatar
                + ", gender=" + gender
                + ", dateOfBirth=" + dateOfBirth
                + ", address=" + address
                + ", status=" + status
                + ", recruitmentId=" + recruitmentId
                + ", appliedDate=" + appliedDate
                + ", updatedDate=" + updatedDate
                + '}';
    }
}
