package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity class for Permission
 * @author admin
 */
public class Permission implements Serializable {
    private static final long serialVersionUID = 1L;

    private int permissionId;
    private String permissionCode;
    private String permissionName;
    private String description;
    private String category;
    private LocalDateTime createdDate;

    public Permission() {}

    public Permission(int permissionId, String permissionCode, String permissionName, 
                     String description, String category, LocalDateTime createdDate) {
        this.permissionId = permissionId;
        this.permissionCode = permissionCode;
        this.permissionName = permissionName;
        this.description = description;
        this.category = category;
        this.createdDate = createdDate;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public String getPermissionCode() {
        return permissionCode;
    }

    public void setPermissionCode(String permissionCode) {
        this.permissionCode = permissionCode;
    }

    public String getPermissionName() {
        return permissionName;
    }

    public void setPermissionName(String permissionName) {
        this.permissionName = permissionName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    @Override
    public String toString() {
        return "Permission{" + "permissionId=" + permissionId + ", permissionCode=" + permissionCode + 
               ", permissionName=" + permissionName + ", category=" + category + '}';
    }
}

