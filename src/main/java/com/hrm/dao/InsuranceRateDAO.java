package com.hrm.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for InsuranceRate table
 * @author admin
 */
public class InsuranceRateDAO {
    
    /**
     * Get current employee rate for a specific insurance type
     * @param type Insurance type: 'BHXH', 'BHYT', 'BHTN'
     * @return Employee rate as BigDecimal (percentage)
     */
    public BigDecimal getCurrentEmployeeRate(String type) {
        String sql = """
            SELECT EmployeeRate 
            FROM InsuranceRate 
            WHERE Type = ? 
            ORDER BY EffectiveDate DESC 
            LIMIT 1
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, type);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("EmployeeRate");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Get current employer rate for a specific insurance type
     * @param type Insurance type: 'BHXH', 'BHYT', 'BHTN'
     * @return Employer rate as BigDecimal (percentage)
     */
    public BigDecimal getCurrentEmployerRate(String type) {
        String sql = """
            SELECT EmployerRate 
            FROM InsuranceRate 
            WHERE Type = ? 
            ORDER BY EffectiveDate DESC 
            LIMIT 1
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, type);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("EmployerRate");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
}

