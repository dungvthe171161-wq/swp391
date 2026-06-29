/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hrm.dao;

import com.hrm.model.entity.Contract;
import java.math.BigDecimal;
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
 *
 * @author Hask
 */
public class ContractDAO {
    public List<Map<String, Object>> getExpiringWithinDays(int days) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = """
            SELECT c.ContractID, c.EmployeeID, e.FullName,
                   c.StartDate, c.EndDate, c.BaseSalary, c.Allowance, c.ContractType, c.Notes
            FROM Contract c
            JOIN Employee e ON c.EmployeeID = e.EmployeeID
            WHERE c.EndDate BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL ? DAY)
            ORDER BY c.EndDate ASC
        """;

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, days);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("contractId", rs.getInt("ContractID"));
                    row.put("employeeId", rs.getInt("EmployeeID"));
                    row.put("employeeName", rs.getString("FullName"));
                    row.put("startDate", rs.getDate("StartDate"));
                    row.put("endDate", rs.getDate("EndDate"));
                    row.put("baseSalary", rs.getBigDecimal("BaseSalary"));
                    row.put("allowance", rs.getBigDecimal("Allowance"));
                    row.put("contractType", rs.getString("ContractType"));
                    results.add(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    public boolean create(Contract contract) {
        // Try with Status and Notes columns first (if they exist in database)
        String sqlWithStatus = """
            INSERT INTO Contract (EmployeeID, StartDate, EndDate, BaseSalary, Allowance, ContractType, Status, Notes)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        // Fallback SQL without Status and Notes (if columns don't exist yet)
        String sqlWithoutStatus = """
            INSERT INTO Contract (EmployeeID, StartDate, EndDate, BaseSalary, Allowance, ContractType)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        // SQL with Status but without Notes
        String sqlWithStatusOnly = """
            INSERT INTO Contract (EmployeeID, StartDate, EndDate, BaseSalary, Allowance, ContractType, Status)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection con = DBConnection.getConnection()) {
            // Try with Status and Notes first
            try (PreparedStatement ps = con.prepareStatement(sqlWithStatus)) {
                ps.setInt(1, contract.getEmployeeId());
                ps.setDate(2, contract.getStartDate() != null ? Date.valueOf(contract.getStartDate()) : null);
                ps.setDate(3, contract.getEndDate() != null ? Date.valueOf(contract.getEndDate()) : null);
                ps.setBigDecimal(4, contract.getBaseSalary());
                ps.setBigDecimal(5, contract.getAllowance() != null ? contract.getAllowance() : BigDecimal.ZERO);
                ps.setString(6, contract.getContractType());
                ps.setString(7, contract.getStatus() != null ? contract.getStatus() : "Draft");
                ps.setString(8, contract.getNote());
                
                int result = ps.executeUpdate();
                return result > 0;
            } catch (SQLException e) {
                // If Notes column doesn't exist, try with Status only
                if (e.getMessage().contains("Unknown column 'Notes'") || 
                    e.getMessage().contains("Notes") && e.getMessage().contains("doesn't exist")) {
                    try (PreparedStatement ps = con.prepareStatement(sqlWithStatusOnly)) {
                        ps.setInt(1, contract.getEmployeeId());
                        ps.setDate(2, contract.getStartDate() != null ? Date.valueOf(contract.getStartDate()) : null);
                        ps.setDate(3, contract.getEndDate() != null ? Date.valueOf(contract.getEndDate()) : null);
                        ps.setBigDecimal(4, contract.getBaseSalary());
                        ps.setBigDecimal(5, contract.getAllowance() != null ? contract.getAllowance() : BigDecimal.ZERO);
                        ps.setString(6, contract.getContractType());
                        ps.setString(7, contract.getStatus() != null ? contract.getStatus() : "Draft");
                        
                        int result = ps.executeUpdate();
                        return result > 0;
                    } catch (SQLException e2) {
                        // If Status column doesn't exist either, try without both
                        if (e2.getMessage().contains("Unknown column 'Status'") || 
                            e2.getMessage().contains("Status") && e2.getMessage().contains("doesn't exist")) {
                            try (PreparedStatement ps = con.prepareStatement(sqlWithoutStatus)) {
                                ps.setInt(1, contract.getEmployeeId());
                                ps.setDate(2, contract.getStartDate() != null ? Date.valueOf(contract.getStartDate()) : null);
                                ps.setDate(3, contract.getEndDate() != null ? Date.valueOf(contract.getEndDate()) : null);
                                ps.setBigDecimal(4, contract.getBaseSalary());
                                ps.setBigDecimal(5, contract.getAllowance() != null ? contract.getAllowance() : BigDecimal.ZERO);
                                ps.setString(6, contract.getContractType());
                                
                                int result = ps.executeUpdate();
                                return result > 0;
                            }
                        } else {
                            throw e2;
                        }
                    }
                } else {
                    throw e;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Get all contracts with employee information
     */
    public List<Map<String, Object>> getAllContractsWithEmployee(String sortBy, int offset, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT c.ContractID, c.EmployeeID, e.FullName, e.Email,
                   c.StartDate, c.EndDate, c.BaseSalary, c.Allowance, 
                   c.ContractType, c.Status, c.Notes
            FROM Contract c
            LEFT JOIN Employee e ON c.EmployeeID = e.EmployeeID
        """);
        
        // Handle sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            if ("contractIdAsc".equals(sortBy)) {
                sql.append(" ORDER BY c.ContractID ASC");
            } else if ("contractIdDesc".equals(sortBy)) {
                sql.append(" ORDER BY c.ContractID DESC");
            } else {
                sql.append(" ORDER BY c.ContractID DESC");
            }
        } else {
            sql.append(" ORDER BY c.ContractID DESC");
        }
        
        // Add pagination
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("contractId", rs.getInt("ContractID"));
                    row.put("employeeId", rs.getInt("EmployeeID"));
                    row.put("employeeName", rs.getString("FullName"));
                    row.put("employeeEmail", rs.getString("Email"));
                    row.put("startDate", rs.getDate("StartDate"));
                    row.put("endDate", rs.getDate("EndDate"));
                    row.put("baseSalary", rs.getBigDecimal("BaseSalary"));
                    row.put("allowance", rs.getBigDecimal("Allowance"));
                    row.put("contractType", rs.getString("ContractType"));
                    row.put("status", rs.getString("Status"));
                    row.put("note", rs.getString("Notes"));
                    results.add(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }
    
    /**
     * Get all contracts with employee information (backward compatibility - no pagination)
     */
    public List<Map<String, Object>> getAllContractsWithEmployee(String sortBy) {
        return getAllContractsWithEmployee(sortBy, 0, Integer.MAX_VALUE);
    }

    /**
     * Search contracts with filters
     */
    public List<Map<String, Object>> searchContracts(String keyword, String status, String contractType, String sortBy, int offset, int limit) {
        List<Map<String, Object>> results = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT c.ContractID, c.EmployeeID, e.FullName, e.Email,
                   c.StartDate, c.EndDate, c.BaseSalary, c.Allowance, 
                   c.ContractType, c.Status, c.Notes
            FROM Contract c
            LEFT JOIN Employee e ON c.EmployeeID = e.EmployeeID
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Only search by FullName, not Email
            sql.append(" AND e.FullName LIKE ?");
            String pattern = "%" + keyword + "%";
            params.add(pattern);
        }
        
        if (status != null && !status.trim().isEmpty() && !status.equals("All")) {
            sql.append(" AND c.Status = ?");
            params.add(status);
        }
        
        if (contractType != null && !contractType.trim().isEmpty() && !contractType.equals("All")) {
            sql.append(" AND c.ContractType = ?");
            params.add(contractType);
        }
        
        // Handle sorting
        if (sortBy != null && !sortBy.trim().isEmpty()) {
            if ("contractIdAsc".equals(sortBy)) {
                sql.append(" ORDER BY c.ContractID ASC");
            } else if ("contractIdDesc".equals(sortBy)) {
                sql.append(" ORDER BY c.ContractID DESC");
            } else {
                sql.append(" ORDER BY c.ContractID DESC");
            }
        } else {
            sql.append(" ORDER BY c.ContractID DESC");
        }
        
        // Add pagination
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            for (Object param : params) {
                ps.setObject(paramIndex++, param);
            }
            ps.setInt(paramIndex++, limit);
            ps.setInt(paramIndex++, offset);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("contractId", rs.getInt("ContractID"));
                    row.put("employeeId", rs.getInt("EmployeeID"));
                    row.put("employeeName", rs.getString("FullName"));
                    row.put("employeeEmail", rs.getString("Email"));
                    row.put("startDate", rs.getDate("StartDate"));
                    row.put("endDate", rs.getDate("EndDate"));
                    row.put("baseSalary", rs.getBigDecimal("BaseSalary"));
                    row.put("allowance", rs.getBigDecimal("Allowance"));
                    row.put("contractType", rs.getString("ContractType"));
                    row.put("status", rs.getString("Status"));
                    row.put("note", rs.getString("Notes"));
                    results.add(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }
    
    /**
     * Search contracts with filters (backward compatibility - no pagination)
     */
    public List<Map<String, Object>> searchContracts(String keyword, String status, String contractType, String sortBy) {
        return searchContracts(keyword, status, contractType, sortBy, 0, Integer.MAX_VALUE);
    }
    
    /**
     * Count total contracts with filters
     */
    public int countContracts(String keyword, String status, String contractType) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) as total
            FROM Contract c
            LEFT JOIN Employee e ON c.EmployeeID = e.EmployeeID
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND e.FullName LIKE ?");
            String pattern = "%" + keyword + "%";
            params.add(pattern);
        }
        
        if (status != null && !status.trim().isEmpty() && !status.equals("All")) {
            sql.append(" AND c.Status = ?");
            params.add(status);
        }
        
        if (contractType != null && !contractType.trim().isEmpty() && !contractType.equals("All")) {
            sql.append(" AND c.ContractType = ?");
            params.add(contractType);
        }

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Count total contracts (no filters)
     */
    public int countAllContracts() {
        String sql = "SELECT COUNT(*) as total FROM Contract";
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get all contracts with employee information (backward compatibility)
     */
    public List<Map<String, Object>> getAllContractsWithEmployee() {
        return getAllContractsWithEmployee(null);
    }

    /**
     * Get contract by ID
     */
    public Contract getContractById(int contractId) {
        String sql = """
            SELECT ContractID, EmployeeID, StartDate, EndDate, 
                   BaseSalary, Allowance, ContractType, Status, Notes
            FROM Contract
            WHERE ContractID = ?
        """;

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = new Contract();
                    contract.setContractId(rs.getInt("ContractID"));
                    contract.setEmployeeId(rs.getInt("EmployeeID"));
                    contract.setStartDate(rs.getDate("StartDate") != null ? 
                        rs.getDate("StartDate").toLocalDate() : null);
                    contract.setEndDate(rs.getDate("EndDate") != null ? 
                        rs.getDate("EndDate").toLocalDate() : null);
                    contract.setBaseSalary(rs.getBigDecimal("BaseSalary"));
                    contract.setAllowance(rs.getBigDecimal("Allowance"));
                    contract.setContractType(rs.getString("ContractType"));
                    contract.setStatus(rs.getString("Status"));
                    contract.setNote(rs.getString("Notes"));
                    return contract;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Update contract
     */
    public boolean updateContract(Contract contract) {
        // Try with Status and Notes columns first
        String sqlWithStatus = """
            UPDATE Contract 
            SET EmployeeID = ?, StartDate = ?, EndDate = ?, 
                BaseSalary = ?, Allowance = ?, ContractType = ?, Status = ?, Notes = ?
            WHERE ContractID = ?
        """;
        
        // Fallback SQL without Status and Notes
        String sqlWithoutStatus = """
            UPDATE Contract 
            SET EmployeeID = ?, StartDate = ?, EndDate = ?, 
                BaseSalary = ?, Allowance = ?, ContractType = ?
            WHERE ContractID = ?
        """;
        
        // SQL with Status but without Notes
        String sqlWithStatusOnly = """
            UPDATE Contract 
            SET EmployeeID = ?, StartDate = ?, EndDate = ?, 
                BaseSalary = ?, Allowance = ?, ContractType = ?, Status = ?
            WHERE ContractID = ?
        """;
        
        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sqlWithStatus)) {
                ps.setInt(1, contract.getEmployeeId());
                ps.setDate(2, contract.getStartDate() != null ? Date.valueOf(contract.getStartDate()) : null);
                ps.setDate(3, contract.getEndDate() != null ? Date.valueOf(contract.getEndDate()) : null);
                ps.setBigDecimal(4, contract.getBaseSalary());
                ps.setBigDecimal(5, contract.getAllowance() != null ? contract.getAllowance() : BigDecimal.ZERO);
                ps.setString(6, contract.getContractType());
                ps.setString(7, contract.getStatus() != null ? contract.getStatus() : "Draft");
                ps.setString(8, contract.getNote());
                ps.setInt(9, contract.getContractId());
                
                int result = ps.executeUpdate();
                return result > 0;
            } catch (SQLException e) {
                // If Notes column doesn't exist, try with Status only
                if (e.getMessage().contains("Unknown column 'Notes'") || 
                    e.getMessage().contains("Notes") && e.getMessage().contains("doesn't exist")) {
                    try (PreparedStatement ps = con.prepareStatement(sqlWithStatusOnly)) {
                        ps.setInt(1, contract.getEmployeeId());
                        ps.setDate(2, contract.getStartDate() != null ? Date.valueOf(contract.getStartDate()) : null);
                        ps.setDate(3, contract.getEndDate() != null ? Date.valueOf(contract.getEndDate()) : null);
                        ps.setBigDecimal(4, contract.getBaseSalary());
                        ps.setBigDecimal(5, contract.getAllowance() != null ? contract.getAllowance() : BigDecimal.ZERO);
                        ps.setString(6, contract.getContractType());
                        ps.setString(7, contract.getStatus() != null ? contract.getStatus() : "Draft");
                        ps.setInt(8, contract.getContractId());
                        
                        int result = ps.executeUpdate();
                        return result > 0;
                    } catch (SQLException e2) {
                        // If Status column doesn't exist either, try without both
                        if (e2.getMessage().contains("Unknown column 'Status'") || 
                            e2.getMessage().contains("Status") && e2.getMessage().contains("doesn't exist")) {
                            try (PreparedStatement ps = con.prepareStatement(sqlWithoutStatus)) {
                                ps.setInt(1, contract.getEmployeeId());
                                ps.setDate(2, contract.getStartDate() != null ? Date.valueOf(contract.getStartDate()) : null);
                                ps.setDate(3, contract.getEndDate() != null ? Date.valueOf(contract.getEndDate()) : null);
                                ps.setBigDecimal(4, contract.getBaseSalary());
                                ps.setBigDecimal(5, contract.getAllowance() != null ? contract.getAllowance() : BigDecimal.ZERO);
                                ps.setString(6, contract.getContractType());
                                ps.setInt(7, contract.getContractId());
                                
                                int result = ps.executeUpdate();
                                return result > 0;
                            }
                        } else {
                            throw e2;
                        }
                    }
                } else {
                    throw e;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Get HR Manager email
     */
    public String getHrManagerEmail() {
        String sql = """
            SELECT e.Email
            FROM SystemUser su
            JOIN Employee e ON su.EmployeeID = e.EmployeeID
            JOIN Role r ON su.RoleID = r.RoleID
            WHERE r.RoleName LIKE '%HR Manager%' OR r.RoleName LIKE '%HR%Manager%'
            LIMIT 1
        """;

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("Email");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Get active contract for an employee (Active or Pending_Approval status, not expired)
     */
    public Contract getActiveContractByEmployeeId(int employeeId) {
        String sql = """
            SELECT ContractID, EmployeeID, StartDate, EndDate, 
                   BaseSalary, Allowance, ContractType, Status, Notes
            FROM Contract
            WHERE EmployeeID = ?
              AND (Status = 'Active' OR Status = 'Pending_Approval')
              AND (EndDate IS NULL OR EndDate >= CURDATE())
            ORDER BY StartDate DESC
            LIMIT 1
        """;

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = new Contract();
                    contract.setContractId(rs.getInt("ContractID"));
                    contract.setEmployeeId(rs.getInt("EmployeeID"));
                    contract.setStartDate(rs.getDate("StartDate") != null ? 
                        rs.getDate("StartDate").toLocalDate() : null);
                    contract.setEndDate(rs.getDate("EndDate") != null ? 
                        rs.getDate("EndDate").toLocalDate() : null);
                    contract.setBaseSalary(rs.getBigDecimal("BaseSalary"));
                    contract.setAllowance(rs.getBigDecimal("Allowance"));
                    contract.setContractType(rs.getString("ContractType"));
                    contract.setStatus(rs.getString("Status"));
                    contract.setNote(rs.getString("Notes"));
                    return contract;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get the most recent contract for an employee (any status, as fallback)
     */
    public Contract getContractByEmployeeId(int employeeId) {
        String sql = """
            SELECT ContractID, EmployeeID, StartDate, EndDate, 
                   BaseSalary, Allowance, ContractType, Status, Notes
            FROM Contract
            WHERE EmployeeID = ?
            ORDER BY StartDate DESC
            LIMIT 1
        """;

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = new Contract();
                    contract.setContractId(rs.getInt("ContractID"));
                    contract.setEmployeeId(rs.getInt("EmployeeID"));
                    contract.setStartDate(rs.getDate("StartDate") != null ? 
                        rs.getDate("StartDate").toLocalDate() : null);
                    contract.setEndDate(rs.getDate("EndDate") != null ? 
                        rs.getDate("EndDate").toLocalDate() : null);
                    contract.setBaseSalary(rs.getBigDecimal("BaseSalary"));
                    contract.setAllowance(rs.getBigDecimal("Allowance"));
                    contract.setContractType(rs.getString("ContractType"));
                    contract.setStatus(rs.getString("Status"));
                    contract.setNote(rs.getString("Notes"));
                    return contract;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Expire a contract by setting its status to 'Expired'
     */
    public boolean expireContract(int contractId) {
        String sql = """
            UPDATE Contract 
            SET Status = 'Expired'
            WHERE ContractID = ?
        """;

        try (Connection con = DBConnection.getConnection()) {
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, contractId);
                int result = ps.executeUpdate();
                return result > 0;
            } catch (SQLException e) {
                // If Status column doesn't exist, return false
                if (e.getMessage().contains("Unknown column 'Status'") || 
                    e.getMessage().contains("Status") && e.getMessage().contains("doesn't exist")) {
                    System.err.println("Status column does not exist in Contract table");
                    return false;
                } else {
                    throw e;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a contract by ID
     */
    public boolean deleteContract(int contractId) {
        String sql = """
            DELETE FROM Contract 
            WHERE ContractID = ?
        """;

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, contractId);
            int result = ps.executeUpdate();
            return result > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    /**
     * Get contracts by status with employee information
     */
    public List<Map<String, Object>> getContractsByStatus(String status) {
        List<Map<String, Object>> results = new ArrayList<>();
        String sql = """
            SELECT c.ContractID, c.EmployeeID, e.FullName, e.Email,
                   c.StartDate, c.EndDate, c.BaseSalary, c.Allowance, 
                   c.ContractType, c.Status, c.Notes
            FROM Contract c
            LEFT JOIN Employee e ON c.EmployeeID = e.EmployeeID
            WHERE c.Status = ?
            ORDER BY c.ContractID DESC
        """;

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("contractId", rs.getInt("ContractID"));
                    row.put("employeeId", rs.getInt("EmployeeID"));
                    row.put("employeeName", rs.getString("FullName"));
                    row.put("employeeEmail", rs.getString("Email"));
                    row.put("startDate", rs.getDate("StartDate"));
                    row.put("endDate", rs.getDate("EndDate"));
                    row.put("baseSalary", rs.getBigDecimal("BaseSalary"));
                    row.put("allowance", rs.getBigDecimal("Allowance"));
                    row.put("contractType", rs.getString("ContractType"));
                    row.put("status", rs.getString("Status"));
                    row.put("note", rs.getString("Notes"));
                    results.add(row);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return results;
    }

    /**
     * Get previous active contract for an employee (to compare changes)
     */
    public Contract getPreviousActiveContract(int employeeId, int currentContractId) {
        String sql = """
            SELECT ContractID, EmployeeID, StartDate, EndDate, 
                   BaseSalary, Allowance, ContractType, Status, Notes
            FROM Contract
            WHERE EmployeeID = ?
              AND ContractID != ?
              AND (Status = 'Active' OR Status = 'Expired')
            ORDER BY StartDate DESC
            LIMIT 1
        """;

        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, employeeId);
            ps.setInt(2, currentContractId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = new Contract();
                    contract.setContractId(rs.getInt("ContractID"));
                    contract.setEmployeeId(rs.getInt("EmployeeID"));
                    contract.setStartDate(rs.getDate("StartDate") != null ? 
                        rs.getDate("StartDate").toLocalDate() : null);
                    contract.setEndDate(rs.getDate("EndDate") != null ? 
                        rs.getDate("EndDate").toLocalDate() : null);
                    contract.setBaseSalary(rs.getBigDecimal("BaseSalary"));
                    contract.setAllowance(rs.getBigDecimal("Allowance"));
                    contract.setContractType(rs.getString("ContractType"));
                    contract.setStatus(rs.getString("Status"));
                    contract.setNote(rs.getString("Notes"));
                    return contract;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Count contracts by status
     */
    public int countContractsByStatus(String status) {
        String sql = "SELECT COUNT(*) as total FROM Contract WHERE Status = ?";
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
