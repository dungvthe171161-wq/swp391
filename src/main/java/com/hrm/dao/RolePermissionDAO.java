package com.hrm.dao;

import com.hrm.model.dto.PermissionSummary;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO hỗ trợ thao tác với quyền theo role
 */
public class RolePermissionDAO {
    private static final Logger logger = Logger.getLogger(RolePermissionDAO.class.getName());

    /**
     * Lấy danh sách permission ở mức summary để hiển thị theo nhóm
     */
    public List<PermissionSummary> getAllPermissionSummaries() throws SQLException {
        List<PermissionSummary> summaries = new ArrayList<>();
        String sql = "SELECT PermissionID, PermissionCode, PermissionName, Description, Category "
                   + "FROM Permission ORDER BY Category, PermissionName";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                PermissionSummary summary = new PermissionSummary();
                summary.setPermissionId(rs.getInt("PermissionID"));
                summary.setPermissionCode(rs.getString("PermissionCode"));
                summary.setPermissionName(rs.getString("PermissionName"));
                summary.setDescription(rs.getString("Description"));
                summary.setCategory(rs.getString("Category"));
                summaries.add(summary);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi truy vấn danh sách permission", e);
            throw e;
        }
        return summaries;
    }

    /**
     * Lấy tập PermissionID đã được gán cho một role
     */
    public Set<Integer> getPermissionIdSetByRole(int roleId) throws SQLException {
        Set<Integer> permissionIds = new HashSet<>();
        String sql = "SELECT PermissionID FROM RolePermission WHERE RoleID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissionIds.add(rs.getInt("PermissionID"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi lấy danh sách PermissionID của role: " + roleId, e);
            throw e;
        }
        return permissionIds;
    }

    /**
     * Cấp quyền cho role (nếu chưa tồn tại)
     */
    public boolean grantPermission(int roleId, int permissionId) throws SQLException {
        if (hasPermission(roleId, permissionId)) {
            return true;
        }

        String sql = "INSERT INTO RolePermission (RoleID, PermissionID, CreatedDate) "
                   + "VALUES (?, ?, CURRENT_TIMESTAMP)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            stmt.setInt(2, permissionId);
            int affected = stmt.executeUpdate();
            logger.log(Level.INFO, "Đã cấp quyền {0} cho role {1}", new Object[]{permissionId, roleId});
            return affected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi cấp quyền " + permissionId + " cho role " + roleId, e);
            throw e;
        }
    }

    /**
     * Thu hồi quyền khỏi role
     */
    public boolean revokePermission(int roleId, int permissionId) throws SQLException {
        String sql = "DELETE FROM RolePermission WHERE RoleID = ? AND PermissionID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            stmt.setInt(2, permissionId);
            int affected = stmt.executeUpdate();
            logger.log(Level.INFO, "Đã thu hồi quyền {0} khỏi role {1}", new Object[]{permissionId, roleId});
            return affected > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi thu hồi quyền " + permissionId + " khỏi role " + roleId, e);
            throw e;
        }
    }

    /**
     * Kiểm tra role đã có quyền hay chưa
     */
    public boolean hasPermission(int roleId, int permissionId) throws SQLException {
        String sql = "SELECT 1 FROM RolePermission WHERE RoleID = ? AND PermissionID = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            stmt.setInt(2, permissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi kiểm tra quyền " + permissionId + " của role " + roleId, e);
            throw e;
        }
    }

    /**
     * Check whether the given role currently owns the permission identified by its code.
     *
     * @param roleId         role identifier
     * @param permissionCode code of the permission (case-insensitive)
     * @return true if the role has the permission
     * @throws SQLException if the database query fails
     */
    public boolean hasPermissionCode(int roleId, String permissionCode) throws SQLException {
        if (permissionCode == null || permissionCode.isBlank()) {
            return false;
        }

        String sql = "SELECT 1 FROM RolePermission rp "
                + "JOIN Permission p ON rp.PermissionID = p.PermissionID "
                + "WHERE rp.RoleID = ? AND UPPER(p.PermissionCode) = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roleId);
            stmt.setString(2, permissionCode.trim().toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,
                    "Lỗi kiểm tra permissionCode " + permissionCode + " của role " + roleId, e);
            throw e;
        }
    }
}

