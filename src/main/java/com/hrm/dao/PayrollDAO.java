package com.hrm.dao;

import com.hrm.model.entity.Payroll;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for Payroll table
 * @author admin
 */
public class PayrollDAO {
    
    /**
     * Create new payroll and return the generated ID
     */
    public int create(Payroll payroll) {
        String sql = """
            INSERT INTO Payroll (EmployeeID, PayPeriod, BaseSalary, Allowance, Bonus, Deduction, NetSalary, Status)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            ps.setInt(1, payroll.getEmployeeId());
            ps.setString(2, payroll.getPayPeriod());
            ps.setBigDecimal(3, payroll.getBaseSalary());
            ps.setBigDecimal(4, payroll.getAllowance());
            ps.setBigDecimal(5, payroll.getBonus());
            ps.setBigDecimal(6, payroll.getDeduction());
            ps.setBigDecimal(7, payroll.getNetSalary());
            ps.setString(8, "Draft");
            
            int result = ps.executeUpdate();
            if (result > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    /**
     * Get payroll by ID
     */
    public Payroll getById(int payrollId) {
        String sql = """
            SELECT p.*, e.FullName
            FROM Payroll p
            JOIN Employee e ON p.EmployeeID = e.EmployeeID
            WHERE p.PayrollID = ?
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, payrollId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPayroll(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get all payrolls with filters
     */
    public List<Map<String, Object>> getAll(Integer employeeId, String status) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, e.FullName
            FROM Payroll p
            JOIN Employee e ON p.EmployeeID = e.EmployeeID
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (employeeId != null) {
            sql.append(" AND p.EmployeeID = ?");
            params.add(employeeId);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND p.Status = ?");
            params.add(status);
        }
        
        sql.append(" ORDER BY p.PayPeriod DESC, e.FullName");
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("payrollId", rs.getInt("PayrollID"));
                    item.put("employeeId", rs.getInt("EmployeeID"));
                    item.put("employeeName", rs.getString("FullName"));
                    item.put("payPeriod", rs.getString("PayPeriod"));
                    item.put("baseSalary", rs.getBigDecimal("BaseSalary"));
                    item.put("allowance", rs.getBigDecimal("Allowance"));
                    item.put("bonus", rs.getBigDecimal("Bonus"));
                    item.put("deduction", rs.getBigDecimal("Deduction"));
                    item.put("netSalary", rs.getBigDecimal("NetSalary"));
                    item.put("status", rs.getString("Status"));
                    item.put("approvedBy", rs.getObject("ApprovedBy"));
                    item.put("approvedDate", rs.getDate("ApprovedDate"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, Object> getLatestByEmployee(int employeeId) {
        String sql = """
            SELECT p.*, e.FullName
            FROM Payroll p
            JOIN Employee e ON p.EmployeeID = e.EmployeeID
            WHERE p.EmployeeID = ?
            ORDER BY p.PayPeriod DESC, p.PayrollID DESC
            LIMIT 1
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("payrollId", rs.getInt("PayrollID"));
                    item.put("employeeId", rs.getInt("EmployeeID"));
                    item.put("employeeName", rs.getString("FullName"));
                    item.put("payPeriod", rs.getString("PayPeriod"));
                    item.put("baseSalary", rs.getBigDecimal("BaseSalary"));
                    item.put("allowance", rs.getBigDecimal("Allowance"));
                    item.put("bonus", rs.getBigDecimal("Bonus"));
                    item.put("deduction", rs.getBigDecimal("Deduction"));
                    item.put("netSalary", rs.getBigDecimal("NetSalary"));
                    item.put("status", rs.getString("Status"));
                    item.put("approvedBy", rs.getObject("ApprovedBy"));
                    item.put("approvedDate", rs.getDate("ApprovedDate"));
                    return item;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }
    
    /**
     * Get total count of payrolls with filters (for pagination)
     */
    public int getTotalPayrollCount(Integer employeeId, String status, String payPeriod) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM Payroll p
            JOIN Employee e ON p.EmployeeID = e.EmployeeID
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (employeeId != null) {
            sql.append(" AND p.EmployeeID = ?");
            params.add(employeeId);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND p.Status = ?");
            params.add(status);
        }
        
        if (payPeriod != null && !payPeriod.trim().isEmpty()) {
            sql.append(" AND p.PayPeriod = ?");
            params.add(payPeriod);
        }
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    public int getTotalPayrollCount(Integer employeeId, String status) {
        return getTotalPayrollCount(employeeId, status, null);
    }
    
    /**
     * Get paged payrolls with filters and sorting (for pagination)
     */
    public List<Map<String, Object>> getPagedPayrolls(int offset, int pageSize,
            Integer employeeId, String status, String sortBy, String sortOrder, String payPeriod) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT p.*, e.FullName
            FROM Payroll p
            JOIN Employee e ON p.EmployeeID = e.EmployeeID
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (employeeId != null) {
            sql.append(" AND p.EmployeeID = ?");
            params.add(employeeId);
        }
        
        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND p.Status = ?");
            params.add(status);
        }
        
        if (payPeriod != null && !payPeriod.trim().isEmpty()) {
            sql.append(" AND p.PayPeriod = ?");
            params.add(payPeriod);
        }
        
        // Validate and set sortBy and sortOrder
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "PayPeriod";
        }
        // Validate sortBy column to prevent SQL injection
        String[] allowedColumns = {"PayrollID", "EmployeeID", "FullName", "PayPeriod", 
            "BaseSalary", "Allowance", "Bonus", "Deduction", "NetSalary", "Status", 
            "ApprovedDate"};
        boolean isValidColumn = false;
        for (String col : allowedColumns) {
            if (col.equals(sortBy)) {
                isValidColumn = true;
                break;
            }
        }
        if (!isValidColumn) {
            sortBy = "PayPeriod";
        }
        
        // Map sortBy to actual column names
        if ("FullName".equals(sortBy)) {
            sql.append(" ORDER BY e.FullName");
        } else {
            sql.append(" ORDER BY p.").append(sortBy);
        }
        
        if (sortOrder == null || sortOrder.trim().isEmpty() || 
            (!"ASC".equalsIgnoreCase(sortOrder) && !"DESC".equalsIgnoreCase(sortOrder))) {
            sortOrder = "DESC";
        }
        sql.append(" ").append(sortOrder);
        
        // Add secondary sort
        if (!"FullName".equals(sortBy)) {
            sql.append(", e.FullName ASC");
        }
        
        // Add pagination
        sql.append(" LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add(offset);
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("payrollId", rs.getInt("PayrollID"));
                    item.put("employeeId", rs.getInt("EmployeeID"));
                    item.put("employeeName", rs.getString("FullName"));
                    item.put("payPeriod", rs.getString("PayPeriod"));
                    item.put("baseSalary", rs.getBigDecimal("BaseSalary"));
                    item.put("allowance", rs.getBigDecimal("Allowance"));
                    item.put("bonus", rs.getBigDecimal("Bonus"));
                    item.put("deduction", rs.getBigDecimal("Deduction"));
                    item.put("netSalary", rs.getBigDecimal("NetSalary"));
                    item.put("status", rs.getString("Status"));
                    item.put("approvedBy", rs.getObject("ApprovedBy"));
                    item.put("approvedDate", rs.getDate("ApprovedDate"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<Map<String, Object>> getPagedPayrolls(int offset, int pageSize,
            Integer employeeId, String status, String sortBy, String sortOrder) {
        return getPagedPayrolls(offset, pageSize, employeeId, status, sortBy, sortOrder, null);
    }
    
    /**
     * Update payroll
     * Allows updating payrolls with status 'Draft' or 'Rejected'
     */
    public boolean update(Payroll payroll) {
        String sql = """
            UPDATE Payroll 
            SET BaseSalary = ?, Allowance = ?, Bonus = ?, Deduction = ?, NetSalary = ?
            WHERE PayrollID = ? AND (Status = 'Draft' OR Status = 'Rejected')
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setBigDecimal(1, payroll.getBaseSalary());
            ps.setBigDecimal(2, payroll.getAllowance());
            ps.setBigDecimal(3, payroll.getBonus());
            ps.setBigDecimal(4, payroll.getDeduction());
            ps.setBigDecimal(5, payroll.getNetSalary());
            ps.setInt(6, payroll.getPayrollId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update payroll status (submit for approval)
     */
    public boolean updateStatus(int payrollId, String status) {
        String sql = "UPDATE Payroll SET Status = ? WHERE PayrollID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, status);
            ps.setInt(2, payrollId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Approve payroll - set status to Approved and record approver info
     * Also updates PayrollAudit status to Approved
     */
    public boolean approvePayroll(int payrollId, Integer approvedBy, java.time.LocalDate approvedDate) {
        // First get payroll info to get employeeId and payPeriod
        Payroll payroll = getById(payrollId);
        if (payroll == null) {
            return false;
        }
        
        try (Connection con = DBConnection.getConnection()) {
            // Update Payroll table
            String payrollSql = "UPDATE Payroll SET Status = 'Approved', ApprovedBy = ?, ApprovedDate = ? WHERE PayrollID = ?";
            try (PreparedStatement ps = con.prepareStatement(payrollSql)) {
                ps.setObject(1, approvedBy);
                if (approvedDate != null) {
                    ps.setDate(2, java.sql.Date.valueOf(approvedDate));
                } else {
                    ps.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                }
                ps.setInt(3, payrollId);
                
                int payrollUpdated = ps.executeUpdate();
                if (payrollUpdated <= 0) {
                    return false;
                }
            }
            
            // Update PayrollAudit table with same status
            String auditSql = "UPDATE PayrollAudit SET Status = 'Approved' WHERE EmployeeID = ? AND PayPeriod = ?";
            try (PreparedStatement auditPs = con.prepareStatement(auditSql)) {
                auditPs.setInt(1, payroll.getEmployeeId());
                auditPs.setString(2, payroll.getPayPeriod());
                auditPs.executeUpdate(); // Don't check result, audit may not exist for all payrolls
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Reject payroll - set status to Rejected and record approver info
     * Also updates PayrollAudit status to Rejected and saves rejection note
     */
    public boolean rejectPayroll(int payrollId, Integer approvedBy, java.time.LocalDate approvedDate, String rejectNote) {
        // First get payroll info to get employeeId and payPeriod
        Payroll payroll = getById(payrollId);
        if (payroll == null) {
            return false;
        }
        
        try (Connection con = DBConnection.getConnection()) {
            // Update Payroll table
            String payrollSql = "UPDATE Payroll SET Status = 'Rejected', ApprovedBy = ?, ApprovedDate = ? WHERE PayrollID = ?";
            try (PreparedStatement ps = con.prepareStatement(payrollSql)) {
                ps.setObject(1, approvedBy);
                if (approvedDate != null) {
                    ps.setDate(2, java.sql.Date.valueOf(approvedDate));
                } else {
                    ps.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                }
                ps.setInt(3, payrollId);
                
                int payrollUpdated = ps.executeUpdate();
                if (payrollUpdated <= 0) {
                    return false;
                }
            }
            
            // Update PayrollAudit table with same status and rejection note
            String auditSql;
            if (rejectNote != null && !rejectNote.trim().isEmpty()) {
                // Build note with rejection reason and timestamp
                String fullNote = "[REJECTED] " + java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                    "Reason: " + rejectNote.trim();
                
                // Check if there's an existing note
                String checkNoteSql = "SELECT Notes FROM PayrollAudit WHERE EmployeeID = ? AND PayPeriod = ?";
                String existingNote = null;
                try (PreparedStatement checkPs = con.prepareStatement(checkNoteSql)) {
                    checkPs.setInt(1, payroll.getEmployeeId());
                    checkPs.setString(2, payroll.getPayPeriod());
                    try (ResultSet rs = checkPs.executeQuery()) {
                        if (rs.next()) {
                            existingNote = rs.getString("Notes");
                        }
                    }
                }
                
                // Combine existing note with rejection note if exists
                String finalNote = fullNote;
                if (existingNote != null && !existingNote.trim().isEmpty()) {
                    finalNote = existingNote + "\n\n" + fullNote;
                }
                
                auditSql = "UPDATE PayrollAudit SET Status = 'Rejected', Notes = ? WHERE EmployeeID = ? AND PayPeriod = ?";
                try (PreparedStatement auditPs = con.prepareStatement(auditSql)) {
                    auditPs.setString(1, finalNote);
                    auditPs.setInt(2, payroll.getEmployeeId());
                    auditPs.setString(3, payroll.getPayPeriod());
                    auditPs.executeUpdate(); // Don't check result, audit may not exist for all payrolls
                }
            } else {
                // No note provided, just update status
                auditSql = "UPDATE PayrollAudit SET Status = 'Rejected' WHERE EmployeeID = ? AND PayPeriod = ?";
                try (PreparedStatement auditPs = con.prepareStatement(auditSql)) {
                    auditPs.setInt(1, payroll.getEmployeeId());
                    auditPs.setString(2, payroll.getPayPeriod());
                    auditPs.executeUpdate(); // Don't check result, audit may not exist for all payrolls
                }
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Reject payroll without note (backward compatibility)
     */
    public boolean rejectPayroll(int payrollId, Integer approvedBy, java.time.LocalDate approvedDate) {
        return rejectPayroll(payrollId, approvedBy, approvedDate, null);
    }
    
    /**
     * Update PayrollAudit notes (append new note to existing notes)
     */
    public boolean updatePayrollAuditNotes(int employeeId, String payPeriod, String note) {
        if (note == null || note.trim().isEmpty()) {
            return false;
        }
        
        String checkSql = "SELECT Notes FROM PayrollAudit WHERE EmployeeID = ? AND PayPeriod = ?";
        String updateSql = "UPDATE PayrollAudit SET Notes = ? WHERE EmployeeID = ? AND PayPeriod = ?";
        
        try (Connection con = DBConnection.getConnection()) {
            // Check if PayrollAudit exists
            String existingNote = null;
            try (PreparedStatement checkPs = con.prepareStatement(checkSql)) {
                checkPs.setInt(1, employeeId);
                checkPs.setString(2, payPeriod);
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        existingNote = rs.getString("Notes");
                    }
                }
            }
            
            // Combine notes
            String finalNote = note.trim();
            if (existingNote != null && !existingNote.trim().isEmpty()) {
                finalNote = existingNote + "\n\n" + note.trim();
            }
            
            // Update
            try (PreparedStatement updatePs = con.prepareStatement(updateSql)) {
                updatePs.setString(1, finalNote);
                updatePs.setInt(2, employeeId);
                updatePs.setString(3, payPeriod);
                updatePs.executeUpdate(); // Don't check result, audit may not exist for all payrolls
            }
            
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete payroll (only if Draft)
     */
    public boolean delete(int payrollId) {
        String sql = "DELETE FROM Payroll WHERE PayrollID = ? AND Status = 'Draft'";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, payrollId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Check if payroll exists for employee and month
     */
    public boolean exists(int employeeId, String payPeriod) {
        String sql = "SELECT COUNT(*) FROM Payroll WHERE EmployeeID = ? AND PayPeriod = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            ps.setString(2, payPeriod);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get payroll status for employee and pay period
     * Returns null if payroll doesn't exist
     */
    public String getPayrollStatusByEmployeeAndPeriod(int employeeId, String payPeriod) {
        String sql = "SELECT Status FROM Payroll WHERE EmployeeID = ? AND PayPeriod = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            ps.setString(2, payPeriod);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Status");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No payroll exists
    }
    
    /**
     * Get payroll details with PayrollAudit information
     */
    public Map<String, Object> getDetailsById(int payrollId) {
        Map<String, Object> result = new HashMap<>();
        
        // Get payroll basic info
        Payroll payroll = getById(payrollId);
        if (payroll == null) {
            return null;
        }
        
        // Convert payroll to map
        result.put("payrollId", payroll.getPayrollId());
        result.put("employeeId", payroll.getEmployeeId());
        result.put("payPeriod", payroll.getPayPeriod());
        result.put("baseSalary", payroll.getBaseSalary());
        result.put("allowance", payroll.getAllowance());
        result.put("bonus", payroll.getBonus());
        result.put("deduction", payroll.getDeduction());
        result.put("netSalary", payroll.getNetSalary());
        result.put("status", payroll.getStatus());
        result.put("approvedBy", payroll.getApprovedBy());
        result.put("approvedDate", payroll.getApprovedDate() != null ? payroll.getApprovedDate().toString() : null);
        
        // Get PayrollAudit details if exists
        String auditSql = """
            SELECT pa.*, e.FullName AS EmployeeName, su.Username AS CalculatedByUsername
            FROM PayrollAudit pa
            JOIN Employee e ON pa.EmployeeID = e.EmployeeID
            LEFT JOIN SystemUser su ON pa.CalculatedBy = su.UserID
            WHERE pa.EmployeeID = ? AND pa.PayPeriod = ?
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(auditSql)) {
            
            ps.setInt(1, payroll.getEmployeeId());
            ps.setString(2, payroll.getPayPeriod());
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> audit = new HashMap<>();
                    audit.put("auditId", rs.getInt("AuditID"));
                    audit.put("baseSalary", rs.getBigDecimal("BaseSalary"));
                    audit.put("allowance", rs.getBigDecimal("Allowance"));
                    audit.put("netSalary", rs.getBigDecimal("NetSalary"));
                    audit.put("status", rs.getString("Status"));
                    audit.put("actualWorkingDays", rs.getBigDecimal("ActualWorkingDays"));
                    audit.put("paidLeaveDays", rs.getBigDecimal("PaidLeaveDays"));
                    audit.put("unpaidLeaveDays", rs.getBigDecimal("UnpaidLeaveDays"));
                    audit.put("actualBaseSalary", rs.getBigDecimal("ActualBaseSalary"));
                    audit.put("overtimeHours", rs.getBigDecimal("OvertimeHours"));
                    audit.put("otSalary", rs.getBigDecimal("OTSalary"));
                    audit.put("bhxh", rs.getBigDecimal("BHXH"));
                    audit.put("bhyt", rs.getBigDecimal("BHYT"));
                    audit.put("bhtn", rs.getBigDecimal("BHTN"));
                    audit.put("taxableIncome", rs.getBigDecimal("TaxableIncome"));
                    audit.put("personalTax", rs.getBigDecimal("PersonalTax"));
                    audit.put("absentPenalty", rs.getBigDecimal("AbsentPenalty"));
                    audit.put("otherDeduction", rs.getBigDecimal("OtherDeduction"));
                    audit.put("totalDeduction", rs.getBigDecimal("TotalDeduction"));
                    java.sql.Timestamp calculatedAt = rs.getTimestamp("CalculatedAt");
                    audit.put("calculatedAt", calculatedAt != null ? calculatedAt.toInstant().toString() : null);
                    audit.put("calculatedBy", rs.getObject("CalculatedBy"));
                    audit.put("calculatedByUsername", rs.getString("CalculatedByUsername"));
                    audit.put("notes", rs.getString("Notes"));
                    result.put("audit", audit);
                    result.put("employeeName", rs.getString("EmployeeName"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Get employee name
        if (!result.containsKey("employeeName")) {
            String employeeSql = "SELECT FullName FROM Employee WHERE EmployeeID = ?";
            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(employeeSql)) {

                ps.setInt(1, payroll.getEmployeeId());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        result.put("employeeName", rs.getString("FullName"));
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return result;
    }
    
    /**
     * Map ResultSet to Payroll object
     */
    private Payroll mapResultSetToPayroll(ResultSet rs) throws SQLException {
        Payroll payroll = new Payroll();
        payroll.setPayrollId(rs.getInt("PayrollID"));
        payroll.setEmployeeId(rs.getInt("EmployeeID"));
        payroll.setPayPeriod(rs.getString("PayPeriod"));
        payroll.setBaseSalary(rs.getBigDecimal("BaseSalary"));
        payroll.setAllowance(rs.getBigDecimal("Allowance"));
        payroll.setBonus(rs.getBigDecimal("Bonus"));
        payroll.setDeduction(rs.getBigDecimal("Deduction"));
        payroll.setNetSalary(rs.getBigDecimal("NetSalary"));
        payroll.setStatus(rs.getString("Status"));
        
        Object approvedByObj = rs.getObject("ApprovedBy");
        if (approvedByObj != null) {
            payroll.setApprovedBy((Integer) approvedByObj);
        }
        
        Date approvedDate = rs.getDate("ApprovedDate");
        if (approvedDate != null) {
            payroll.setApprovedDate(approvedDate.toLocalDate());
        }
        
        return payroll;
    }
}
