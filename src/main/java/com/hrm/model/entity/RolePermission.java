package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Entity class for RolePermission
 * @author admin
 */
public class RolePermission implements Serializable {
    private static final long serialVersionUID = 1L;

    private int rolePermissionId;
    private int roleId;
    private int permissionId;
    private LocalDateTime createdDate;
    private Role role;
    private Permission permission;

    public RolePermission() {}

    public RolePermission(int rolePermissionId, int roleId, int permissionId, LocalDateTime createdDate) {
        this.rolePermissionId = rolePermissionId;
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.createdDate = createdDate;
    }

    public int getRolePermissionId() {
        return rolePermissionId;
    }

    public void setRolePermissionId(int rolePermissionId) {
        this.rolePermissionId = rolePermissionId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public int getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    @Override
    public String toString() {
        return "RolePermission{" + "rolePermissionId=" + rolePermissionId + 
               ", roleId=" + roleId + ", permissionId=" + permissionId + '}';
    }
}

