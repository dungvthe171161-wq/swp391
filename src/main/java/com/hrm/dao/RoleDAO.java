package com.hrm.dao;

import com.hrm.model.entity.Role;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

public class RoleDAO {
    private static final Logger logger = Logger.getLogger(RoleDAO.class.getName());
    private static final int MAX_ROLE_NAME_LENGTH = 100;
    private static final String ROLE_NAME_PATTERN = "^[a-zA-Z0-9\\s\\-_]+$";  

    private void validateRoleName(String roleName) throws IllegalArgumentException {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be empty");
        }
        
        String trimmed = roleName.trim();
        
        if (trimmed.length() > MAX_ROLE_NAME_LENGTH) {
            throw new IllegalArgumentException("Role name exceeds maximum length of " + MAX_ROLE_NAME_LENGTH + " characters");
        }
        
        if (!trimmed.matches(ROLE_NAME_PATTERN)) {
            throw new IllegalArgumentException("Role name contains invalid characters. Only alphanumeric, spaces, hyphens, and underscores are allowed");
        }
    }

    private boolean isRoleNameDuplicate(String roleName, int excludeRoleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Role WHERE LOWER(RoleName) = LOWER(?) AND RoleID != ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roleName.trim());
            stmt.setInt(2, excludeRoleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Role> getPagedRoles(int offset, int limit,
              String searchKeyword, String sortBy, String sortOrder) throws SQLException {

        List<Role> roles = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT RoleID, RoleName FROM Role WHERE 1=1");
        List<Object> params = new ArrayList<>();


        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (RoleName LIKE ? OR RoleID = ?)");
            params.add("%" + searchKeyword.trim() + "%");

            try {
                int roleId = Integer.parseInt(searchKeyword.trim());
                params.add(roleId);
            } catch (NumberFormatException e) {
                params.add(-1);
            }
        }

        String orderByClause = " ORDER BY ";

        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "RoleID";
        }

        switch (sortBy.toLowerCase()) {
            case "rolename":
                orderByClause += "RoleName ";
                break;
            case "roleid":
            default:
                orderByClause += "RoleID ";
                break;
        }

        if (sortOrder == null || sortOrder.trim().isEmpty() || (!sortOrder.equalsIgnoreCase("ASC") && !sortOrder.equalsIgnoreCase("DESC"))) {
            sortOrder = "ASC";
        }
        orderByClause += sortOrder;

        sql.append(orderByClause);
        sql.append(" LIMIT ? OFFSET ?");

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            int paramIndex = 1;
            for (Object param : params) {
                stmt.setObject(paramIndex++, param);
            }

            stmt.setInt(paramIndex++, limit);
            stmt.setInt(paramIndex++, offset);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Role role = new Role();
                    role.setRoleId(rs.getInt("RoleID"));
                    role.setRoleName(rs.getString("RoleName"));
                    roles.add(role);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching paged roles", e);
            throw e;
        }
        return roles;
    }

    public int getTotalRoleCount(String searchKeyword) throws SQLException {
        StringBuilder sql = new StringBuilder("SELECT COUNT(RoleID) FROM Role WHERE 1=1");
        List<Object> params = new ArrayList<>();


        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (RoleName LIKE ? OR RoleID = ?)");
            params.add("%" + searchKeyword.trim() + "%");

            try {
                int roleId = Integer.parseInt(searchKeyword.trim());
                params.add(roleId);
            } catch (NumberFormatException e) {
                params.add(-1);
            }
        }

        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error counting roles", e);
            throw e;
        }
        return 0;
    }

    public Role getRoleById(int roleId) throws SQLException {
        String sql = "SELECT RoleID, RoleName FROM Role WHERE RoleID = ?";
        try (Connection con = DBConnection.getConnection(); 
             PreparedStatement ps = con != null ? con.prepareStatement(sql) : null) {

            if (ps == null) {
                logger.log(Level.SEVERE, "Database connection failed");
                return null;
            }
            ps.setInt(1, roleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Role role = new Role();
                    role.setRoleId(rs.getInt("RoleID"));
                    role.setRoleName(rs.getString("RoleName"));
                    return role;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error fetching role by ID: " + roleId, e);
            throw e;
        }
        return null;
    }

    public boolean createRole(Role role) throws SQLException, IllegalArgumentException {
        validateRoleName(role.getRoleName());
        
        if (isRoleNameDuplicate(role.getRoleName(), -1)) {
            throw new IllegalArgumentException("A role with this name already exists");
        }

        String sql = "INSERT INTO Role (RoleName) VALUES (?)";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.getRoleName().trim());
            int result = stmt.executeUpdate();
            logger.log(Level.INFO, "Role created: " + role.getRoleName());
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating role", e);
            throw e;
        }
    }

    public boolean updateRole(Role role) throws SQLException, IllegalArgumentException {
        validateRoleName(role.getRoleName());
        
        if (isRoleNameDuplicate(role.getRoleName(), role.getRoleId())) {
            throw new IllegalArgumentException("A role with this name already exists");
        }

        String sql = "UPDATE Role SET RoleName = ? WHERE RoleID = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, role.getRoleName().trim());
            stmt.setInt(2, role.getRoleId());
            int result = stmt.executeUpdate();
            logger.log(Level.INFO, "Role updated: ID=" + role.getRoleId());
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating role", e);
            throw e;
        }
    }

    public boolean deleteRole(int roleId) throws SQLException {
        if (isRoleInUse(roleId)) {
            throw new IllegalArgumentException("Cannot delete role because it is assigned to one or more users");
        }

        String sql = "DELETE FROM Role WHERE RoleID = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roleId);
            int result = stmt.executeUpdate();
            logger.log(Level.INFO, "Role deleted: ID=" + roleId);
            return result > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting role", e);
            throw e;
        }
    }

    private boolean isRoleInUse(int roleId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM SystemUser WHERE RoleID = ?";
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Role> getAllRoles() {
        List<Role> roles = new ArrayList<>();
        String sql = "SELECT RoleID, RoleName FROM Role ORDER BY RoleID";

        try (Connection conn = DBConnection.getConnection(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Role role = new Role();
                role.setRoleId(rs.getInt("RoleID"));
                role.setRoleName(rs.getString("RoleName"));
                roles.add(role);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error fetching all roles", e);
        }
        return roles;
    }

    public String normalizeRoleName(String dbRoleName) {
        if (dbRoleName == null) {
            return "guest";
        }
        String name = dbRoleName.trim().toLowerCase();

        if (name.contains("admin")) {
            return "admin";
        }
        if (name.contains("hr")) {
            return "hr";
        }
        if (name.contains("dept") || name.contains("department")) {
            return "dept";
        }
        if (name.contains("employee")) {
            return "employee";
        }
        return "guest";
    }
}
