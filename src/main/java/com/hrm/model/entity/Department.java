
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hrm.model.entity;

import java.io.Serializable;

/**
 *
 * @author admin
 */
public class Department implements Serializable {

    private static final long serialVersionUID = 1L;

    private int departmentId;
    private String deptName;
    private Integer deptManagerId;
    private Integer employeeCount;
    private String status;
    private java.sql.Timestamp createdAt;
    private String managerName;
    public Department() {
    }

    public Department(int departmentId, String deptName, Integer deptManagerId) {
        this(departmentId, deptName, deptManagerId, null, null);
    }

    public Department(int departmentId, String deptName, Integer deptManagerId, String status, java.sql.Timestamp createdAt) {
        this.departmentId = departmentId;
        this.deptName = deptName;
        this.deptManagerId = deptManagerId;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getDeptManagerId() {
        return deptManagerId;
    }

    public void setDeptManagerId(Integer deptManagerId) {
        this.deptManagerId = deptManagerId;
    }

    public Integer getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Integer employeeCount) {
        this.employeeCount = employeeCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public java.sql.Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.sql.Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    public String getManagerName() {
        return managerName;
    }

    public void setManagerName(String managerName) {
        this.managerName = managerName;
    }
    @Override
    public String toString() {
        return "Department{" + "departmentId=" + departmentId + ", deptName=" + deptName + ", deptManagerId=" + deptManagerId + '}';
    }
}
