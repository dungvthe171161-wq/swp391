package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for DeductionType table
 * @author admin
 */
public class DeductionTypeDAO {
    
    /**
     * Get all deduction types
     */
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT DeductionTypeID, DeductionName, Description FROM DeductionType ORDER BY DeductionName";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("deductionTypeId", rs.getInt("DeductionTypeID"));
                item.put("deductionName", rs.getString("DeductionName"));
                item.put("description", rs.getString("Description"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get deduction type by ID
     */
    public Map<String, Object> getById(int id) {
        String sql = "SELECT DeductionTypeID, DeductionName, Description FROM DeductionType WHERE DeductionTypeID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("deductionTypeId", rs.getInt("DeductionTypeID"));
                    item.put("deductionName", rs.getString("DeductionName"));
                    item.put("description", rs.getString("Description"));
                    return item;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Create new deduction type
     */
    public boolean create(String deductionName, String description) {
        String sql = "INSERT INTO DeductionType (DeductionName, Description) VALUES (?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, deductionName);
            ps.setString(2, description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Get deduction type ID by name
     * @param deductionName Name of deduction type (e.g., "Social Insurance", "Personal Income Tax")
     * @return DeductionTypeID or -1 if not found
     */
    public int getIdByName(String deductionName) {
        String sql = "SELECT DeductionTypeID FROM DeductionType WHERE DeductionName = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, deductionName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("DeductionTypeID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
}


