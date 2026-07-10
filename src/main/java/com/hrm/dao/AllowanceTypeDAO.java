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
 * DAO for AllowanceType table
 * @author admin
 */
public class AllowanceTypeDAO {
    
    /**
     * Get all allowance types
     */
    public List<Map<String, Object>> getAll() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT AllowanceTypeID, AllowanceName, Description FROM AllowanceType ORDER BY AllowanceName";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> item = new HashMap<>();
                item.put("allowanceTypeId", rs.getInt("AllowanceTypeID"));
                item.put("allowanceName", rs.getString("AllowanceName"));
                item.put("description", rs.getString("Description"));
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /**
     * Get allowance type by ID
     */
    public Map<String, Object> getById(int id) {
        String sql = "SELECT AllowanceTypeID, AllowanceName, Description FROM AllowanceType WHERE AllowanceTypeID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("allowanceTypeId", rs.getInt("AllowanceTypeID"));
                    item.put("allowanceName", rs.getString("AllowanceName"));
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
     * Create new allowance type
     */
    public boolean create(String allowanceName, String description) {
        String sql = "INSERT INTO AllowanceType (AllowanceName, Description) VALUES (?, ?)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, allowanceName);
            ps.setString(2, description);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}

