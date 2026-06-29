package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

public class Employee implements Serializable {
    private static final long serialVersionUID = 1L;

    private int employeeId;
    private String fullName;
    private String gender;
    private LocalDate dob;       
    private String address;
    private String phone;
    private String email;
    private String position;
    private int departmentId;
    private String departmentName; 
    private LocalDate hireDate;    
    private double salary;
    private boolean active;       
    private Department department;
    private String employmentPeriod;
    private String status;
    private SystemUser systemUser;
    public Employee() {}

    public Employee(int employeeId, String fullName, String gender, LocalDate dob, String address, String phone, String email, String position, int departmentId, String departmentName, LocalDate hireDate, double salary, boolean active, Department department, String employmentPeriod, String status, SystemUser systemUser) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.gender = gender;
        this.dob = dob;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.departmentId = departmentId;
        this.departmentName = departmentName;
        this.hireDate = hireDate;
        this.salary = salary;
        this.active = active;
        this.department = department;
        this.employmentPeriod = employmentPeriod;
        this.status = status;
        this.systemUser = systemUser;
    }

    public Employee(int employeeId, String fullName, String address, String phone, String email, String position, int departmentId) {
        this.employeeId = employeeId;
        this.fullName = fullName;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.position = position;
        this.departmentId = departmentId;
    }
    
    
    

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDate getDob() {
        return dob;
    }

    public void setDob(LocalDate dob) {
        this.dob = dob;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(int departmentId) {
        this.departmentId = departmentId;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public void setHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public String getEmploymentPeriod() {
        return employmentPeriod;
    }

    public void setEmploymentPeriod(String employmentPeriod) {
        this.employmentPeriod = employmentPeriod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SystemUser getSystemUser() {
        return systemUser;
    }

    public void setSystemUser(SystemUser systemUser) {
        this.systemUser = systemUser;
    }

    @Override
    public String toString() {
        return "Employee{" + "employeeId=" + employeeId + ", fullName=" + fullName + ", gender=" + gender + ", dob=" + dob + ", address=" + address + ", phone=" + phone + ", email=" + email + ", position=" + position + ", departmentId=" + departmentId + ", departmentName=" + departmentName + ", hireDate=" + hireDate + ", salary=" + salary + ", active=" + active + ", department=" + department + ", employmentPeriod=" + employmentPeriod + ", status=" + status + ", systemUser=" + systemUser + '}';
    }

}
