
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hrm.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author admin
 */
public class Contract implements Serializable {

    private static final long serialVersionUID = 1L;

    private int contractId;
    private Integer employeeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal baseSalary;
    private BigDecimal allowance;
    private String contractType;
    private String status;
    private String note;

    public Contract() {
    }

    public Contract(int contractId, Integer employeeId, LocalDate startDate, LocalDate endDate,
            BigDecimal baseSalary, BigDecimal allowance, String contractType) {
        this.contractId = contractId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.baseSalary = baseSalary;
        this.allowance = allowance;
        this.contractType = contractType;
    }

    public Contract(int contractId, Integer employeeId, LocalDate startDate, LocalDate endDate,
            BigDecimal baseSalary, BigDecimal allowance, String contractType, String status) {
        this.contractId = contractId;
        this.employeeId = employeeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.baseSalary = baseSalary;
        this.allowance = allowance;
        this.contractType = contractType;
        this.status = status;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getBaseSalary() {
        return baseSalary;
    }

    public void setBaseSalary(BigDecimal baseSalary) {
        this.baseSalary = baseSalary;
    }

    public BigDecimal getAllowance() {
        return allowance;
    }

    public void setAllowance(BigDecimal allowance) {
        this.allowance = allowance;
    }

    public String getContractType() {
        return contractType;
    }

    public void setContractType(String contractType) {
        this.contractType = contractType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        if (note != null && note.length() > 1000) {
            throw new IllegalArgumentException("Notes cannot exceed 1000 characters.");
        }
        this.note = note;
    }

    @Override
    public String toString() {
        return "Contract{" + "contractId=" + contractId + ", employeeId=" + employeeId + ", startDate=" + startDate + ", endDate=" + endDate + ", baseSalary=" + baseSalary + ", allowance=" + allowance + ", contractType=" + contractType + ", status=" + status + ", note=" + note + '}';
    }

}
