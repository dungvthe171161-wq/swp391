package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO for Dependent table
 * @author admin
 */
public class DependentDAO {
    
    /**
     * Count number of dependents for an employee
     * @param employeeId Employee ID
     * @return Number of dependents
     */
    public int countDependents(int employeeId) {
        String sql = "SELECT COUNT(*) as count FROM Dependent WHERE EmployeeID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}

