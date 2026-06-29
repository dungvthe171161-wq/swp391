package com.hrm.dao;

import com.hrm.model.entity.Permission;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * DAO for Permission entity
 * @author admin
 */
public class PermissionDAO {
    private static final Logger logger = Logger.getLogger(PermissionDAO.class.getName());

    /**
     * Get all permissions
     */
    public List<Permission> getAllPermissions() throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT PermissionID, PermissionCode, PermissionName, Description, Category, CreatedDate " +
                     "FROM Permission ORDER BY Category, PermissionName";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Permission permission = new Permission();
                permission.setPermissionId(rs.getInt("PermissionID"));
                permission.setPermissionCode(rs.getString("PermissionCode"));
                permission.setPermissionName(rs.getString("PermissionName"));
                permission.setDescription(rs.getString("Description"));
                permission.setCategory(rs.getString("Category"));
                if (rs.getTimestamp("CreatedDate") != null) {
                    permission.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                }
                permissions.add(permission);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting all permissions", e);
            throw e;
        }

        return permissions;
    }

    /**
     * Get permissions by category
     */
    public List<Permission> getPermissionsByCategory(String category) throws SQLException {
        List<Permission> permissions = new ArrayList<>();
        String sql = "SELECT PermissionID, PermissionCode, PermissionName, Description, Category, CreatedDate " +
                     "FROM Permission WHERE Category = ? ORDER BY PermissionName";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, category);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Permission permission = new Permission();
                    permission.setPermissionId(rs.getInt("PermissionID"));
                    permission.setPermissionCode(rs.getString("PermissionCode"));
                    permission.setPermissionName(rs.getString("PermissionName"));
                    permission.setDescription(rs.getString("Description"));
                    permission.setCategory(rs.getString("Category"));
                    if (rs.getTimestamp("CreatedDate") != null) {
                        permission.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    }
                    permissions.add(permission);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting permissions by category: " + category, e);
            throw e;
        }

        return permissions;
    }

    /**
     * Get permission by ID
     */
    public Permission getPermissionById(int permissionId) throws SQLException {
        String sql = "SELECT PermissionID, PermissionCode, PermissionName, Description, Category, CreatedDate " +
                     "FROM Permission WHERE PermissionID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, permissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Permission permission = new Permission();
                    permission.setPermissionId(rs.getInt("PermissionID"));
                    permission.setPermissionCode(rs.getString("PermissionCode"));
                    permission.setPermissionName(rs.getString("PermissionName"));
                    permission.setDescription(rs.getString("Description"));
                    permission.setCategory(rs.getString("Category"));
                    if (rs.getTimestamp("CreatedDate") != null) {
                        permission.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    }
                    return permission;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting permission by ID: " + permissionId, e);
            throw e;
        }

        return null;
    }

    /**
     * Get permission by code
     */
    public Permission getPermissionByCode(String permissionCode) throws SQLException {
        String sql = "SELECT PermissionID, PermissionCode, PermissionName, Description, Category, CreatedDate " +
                     "FROM Permission WHERE PermissionCode = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, permissionCode);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Permission permission = new Permission();
                    permission.setPermissionId(rs.getInt("PermissionID"));
                    permission.setPermissionCode(rs.getString("PermissionCode"));
                    permission.setPermissionName(rs.getString("PermissionName"));
                    permission.setDescription(rs.getString("Description"));
                    permission.setCategory(rs.getString("Category"));
                    if (rs.getTimestamp("CreatedDate") != null) {
                        permission.setCreatedDate(rs.getTimestamp("CreatedDate").toLocalDateTime());
                    }
                    return permission;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting permission by code: " + permissionCode, e);
            throw e;
        }

        return null;
    }

    /**
     * Get all unique categories
     */
    public List<String> getAllCategories() throws SQLException {
        List<String> categories = new ArrayList<>();
        String sql = "SELECT DISTINCT Category FROM Permission ORDER BY Category";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                categories.add(rs.getString("Category"));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting categories", e);
            throw e;
        }

        return categories;
    }
}

