package com.hrm.model.dto;

import java.io.Serializable;

/**
 * DTO nhẹ phục vụ hiển thị quyền trong trang phân quyền role
 */
public class PermissionSummary implements Serializable {
    private static final long serialVersionUID = 1L;

    private int permissionId;
    private String permissionCode;
    private String permissionName;
    private String description;
    private String category;

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
}



