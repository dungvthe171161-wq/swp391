package com.hrm.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for EmployeeAllowance table
 * @author admin
 */
public class EmployeeAllowanceDAO {
    
    /**
     * Get all employee allowances with filters
     */
    public List<Map<String, Object>> getAll(Integer employeeId, String month) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT ea.ID, ea.EmployeeID, ea.AllowanceTypeID, ea.Amount, ea.Month,
                   e.FullName, at.AllowanceName
            FROM EmployeeAllowance ea
            JOIN Employee e ON ea.EmployeeID = e.EmployeeID
            JOIN AllowanceType at ON ea.AllowanceTypeID = at.AllowanceTypeID
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (employeeId != null) {
            sql.append(" AND ea.EmployeeID = ?");
            params.add(employeeId);
        }
        
        if (month != null && !month.trim().isEmpty()) {
            sql.append(" AND ea.Month = ?");
            params.add(month);
        }
        
        sql.append(" ORDER BY ea.Month DESC, e.FullName, at.AllowanceName");
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("ID"));
                    item.put("employeeId", rs.getInt("EmployeeID"));
                    item.put("employeeName", rs.getString("FullName"));
                    item.put("allowanceTypeId", rs.getInt("AllowanceTypeID"));
                    item.put("allowanceName", rs.getString("AllowanceName"));
                    item.put("amount", rs.getBigDecimal("Amount"));
                    item.put("month", rs.getString("Month"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get total allowance for an employee in a specific month
     */
    public BigDecimal getTotalAllowance(int employeeId, String month) {
        String sql = "SELECT SUM(Amount) as Total FROM EmployeeAllowance WHERE EmployeeID = ? AND Month = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            ps.setString(2, month);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("Total");
                    return total != null ? total : BigDecimal.ZERO;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Create new employee allowance
     */
    public boolean create(int employeeId, int allowanceTypeId, BigDecimal amount, String month) {
        String sql = "INSERT INTO EmployeeAllowance (EmployeeID, AllowanceTypeID, Amount, Month) VALUES (?, ?, ?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            ps.setInt(2, allowanceTypeId);
            ps.setBigDecimal(3, amount);
            ps.setString(4, month);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Update employee allowance
     */
    public boolean update(int id, int allowanceTypeId, BigDecimal amount, String month) {
        String sql = "UPDATE EmployeeAllowance SET AllowanceTypeID = ?, Amount = ?, Month = ? WHERE ID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, allowanceTypeId);
            ps.setBigDecimal(2, amount);
            ps.setString(3, month);
            ps.setInt(4, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Delete employee allowance
     */
    public boolean delete(int id) {
        String sql = "DELETE FROM EmployeeAllowance WHERE ID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get total count of allowances with filters
     */
    public int getTotalCount(Integer employeeId, String month) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) as Total
            FROM EmployeeAllowance ea
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (employeeId != null) {
            sql.append(" AND ea.EmployeeID = ?");
            params.add(employeeId);
        }
        
        if (month != null && !month.trim().isEmpty()) {
            sql.append(" AND ea.Month = ?");
            params.add(month);
        }
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("Total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * Get paged employee allowances with filters
     */
    public List<Map<String, Object>> getPaged(Integer employeeId, String month, int offset, int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT ea.ID, ea.EmployeeID, ea.AllowanceTypeID, ea.Amount, ea.Month,
                   e.FullName, at.AllowanceName
            FROM EmployeeAllowance ea
            JOIN Employee e ON ea.EmployeeID = e.EmployeeID
            JOIN AllowanceType at ON ea.AllowanceTypeID = at.AllowanceTypeID
            WHERE 1=1
        """);
        
        List<Object> params = new ArrayList<>();
        
        if (employeeId != null) {
            sql.append(" AND ea.EmployeeID = ?");
            params.add(employeeId);
        }
        
        if (month != null && !month.trim().isEmpty()) {
            sql.append(" AND ea.Month = ?");
            params.add(month);
        }
        
        sql.append(" ORDER BY ea.Month DESC, e.FullName, at.AllowanceName");
        sql.append(" LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("ID"));
                    item.put("employeeId", rs.getInt("EmployeeID"));
                    item.put("employeeName", rs.getString("FullName"));
                    item.put("allowanceTypeId", rs.getInt("AllowanceTypeID"));
                    item.put("allowanceName", rs.getString("AllowanceName"));
                    item.put("amount", rs.getBigDecimal("Amount"));
                    item.put("month", rs.getString("Month"));
                    list.add(item);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get employee allowance by ID
     */
    public Map<String, Object> getById(int id) {
        String sql = """
            SELECT ea.ID, ea.EmployeeID, ea.AllowanceTypeID, ea.Amount, ea.Month,
                   e.FullName, at.AllowanceName
            FROM EmployeeAllowance ea
            JOIN Employee e ON ea.EmployeeID = e.EmployeeID
            JOIN AllowanceType at ON ea.AllowanceTypeID = at.AllowanceTypeID
            WHERE ea.ID = ?
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("id", rs.getInt("ID"));
                    item.put("employeeId", rs.getInt("EmployeeID"));
                    item.put("employeeName", rs.getString("FullName"));
                    item.put("allowanceTypeId", rs.getInt("AllowanceTypeID"));
                    item.put("allowanceName", rs.getString("AllowanceName"));
                    item.put("amount", rs.getBigDecimal("Amount"));
                    item.put("month", rs.getString("Month"));
                    return item;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}


