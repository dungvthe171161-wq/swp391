package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity class for UserPermission - Core permission matrix
 * @author admin
 */
public class UserPermission implements Serializable {
    private static final long serialVersionUID = 1L;

    private int userPermissionId;
    private int userId;
    private int permissionId;
    private boolean isGranted; // TRUE = Cấp quyền, FALSE = Thu hồi quyền
    private String scope; // ALL, DEPARTMENT, SELF
    private Integer scopeValue; // Giá trị phạm vi (ví dụ: DepartmentID)
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Integer createdBy;
    
    // Related entities
    private SystemUser user;
    private Permission permission;
    private SystemUser createdByUser;

    public UserPermission() {}

    public UserPermission(int userPermissionId, int userId, int permissionId, 
                         boolean isGranted, String scope, Integer scopeValue,
                         LocalDateTime createdDate, LocalDateTime updatedDate, Integer createdBy) {
        this.userPermissionId = userPermissionId;
        this.userId = userId;
        this.permissionId = permissionId;
        this.isGranted = isGranted;
        this.scope = scope;
        this.scopeValue = scopeValue;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.createdBy = createdBy;
    }

    public int getUserPermissionId() {
        return userPermissionId;
    }

    public void setUserPermissionId(int userPermissionId) {
        this.userPermissionId = userPermissionId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public boolean isIsGranted() {
        return isGranted;
    }

    public void setIsGranted(boolean isGranted) {
        this.isGranted = isGranted;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public Integer getScopeValue() {
        return scopeValue;
    }

    public void setScopeValue(Integer scopeValue) {
        this.scopeValue = scopeValue;
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public SystemUser getUser() {
        return user;
    }

    public void setUser(SystemUser user) {
        this.user = user;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public SystemUser getCreatedByUser() {
        return createdByUser;
    }

    public void setCreatedByUser(SystemUser createdByUser) {
        this.createdByUser = createdByUser;
    }

    @Override
    public String toString() {
        return "UserPermission{" + "userPermissionId=" + userPermissionId + 
               ", userId=" + userId + ", permissionId=" + permissionId + 
               ", isGranted=" + isGranted + ", scope=" + scope + 
               ", scopeValue=" + scopeValue + '}';
    }
}

