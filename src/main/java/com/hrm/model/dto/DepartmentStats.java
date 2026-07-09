package com.hrm.model.dto;


public class DepartmentStats {
    private int departmentId;
    private String deptName;
    private int count;

    public DepartmentStats() {
    }

    public DepartmentStats(int departmentId, String departmentName, int count) {
        this.departmentId = departmentId;
        this.deptName = departmentName;
        this.count = count;
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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
