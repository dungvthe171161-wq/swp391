package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class SystemUser implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userId;
    private String username;
    private String email;
    private String passwordHash;
    private String googleId;
    private String avatarUrl;
    private String loginProvider;
    private Integer roleId;
    private Integer employeeId;
    private int failedLoginAttempt;
    private LocalDateTime lockedUntil;
    private LocalDateTime lastLogin;
    private boolean isActive;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;

    private Department department;
    private Employee employee;
    private Role role;

    public SystemUser() {
    }

    public SystemUser(int userId, String username, String password, Integer roleId,
                      LocalDateTime lastLogin, boolean isActive, LocalDateTime createdDate, Integer employeeId) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = password;
        this.roleId = roleId;
        this.lastLogin = lastLogin;
        this.isActive = isActive;
        this.createdDate = createdDate;
        this.employeeId = employeeId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getPassword() {
        return passwordHash;
    }

    public void setPassword(String password) {
        this.passwordHash = password;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getLoginProvider() {
        return loginProvider;
    }

    public void setLoginProvider(String loginProvider) {
        this.loginProvider = loginProvider;
    }

    public int getRoleId() {
        return roleId != null ? roleId : 0;
    }

    public Integer getRoleIdObject() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public int getFailedLoginAttempt() {
        return failedLoginAttempt;
    }

    public void setFailedLoginAttempt(int failedLoginAttempt) {
        this.failedLoginAttempt = failedLoginAttempt;
    }

    public LocalDateTime getLockedUntil() {
        return lockedUntil;
    }

    public void setLockedUntil(LocalDateTime lockedUntil) {
        this.lockedUntil = lockedUntil;
    }

    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }

    public boolean isIsActive() {
        return isActive;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    @Override
    public String toString() {
        return "SystemUser{"
                + "userId=" + userId
                + ", username=" + username
                + ", email=" + email
                + ", roleId=" + roleId
                + ", employeeId=" + employeeId
                + ", loginProvider=" + loginProvider
                + ", failedLoginAttempt=" + failedLoginAttempt
                + ", lockedUntil=" + lockedUntil
                + ", lastLogin=" + lastLogin
                + ", isActive=" + isActive
                + ", createdDate=" + createdDate
                + ", updatedDate=" + updatedDate
                + '}';
    }
}
