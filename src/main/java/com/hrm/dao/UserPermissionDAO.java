package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * DAO xử lý quyền phân bổ trực tiếp cho user (UserPermission table).
 */
public class UserPermissionDAO {
    private static final Logger logger = Logger.getLogger(UserPermissionDAO.class.getName());

    /**
     * Trả về kết quả quyền gần nhất của user đối với permission code.
     * @param userId ID người dùng
     * @param permissionCode mã quyền (case-insensitive)
     * @return TRUE nếu được cấp, FALSE nếu bị thu hồi, NULL nếu không có bản ghi user-level
     * @throws SQLException lỗi truy vấn database
     */
    public Optional<Boolean> getPermissionOverride(int userId, String permissionCode) throws SQLException {
        if (userId <= 0 || permissionCode == null || permissionCode.isBlank()) {
            return Optional.empty();
        }

        String sql = "SELECT up.IsGranted "
                + "FROM UserPermission up "
                + "JOIN Permission p ON up.PermissionID = p.PermissionID "
                + "WHERE up.UserID = ? AND UPPER(p.PermissionCode) = ? "
                + "ORDER BY up.UpdatedDate DESC, up.UserPermissionID DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, permissionCode.trim().toUpperCase());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(rs.getBoolean("IsGranted"));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE,
                    "Lỗi kiểm tra quyền user {0} với permission {1}",
                    new Object[]{userId, permissionCode});
            throw e;
        }
        return Optional.empty();
    }

    /**
     * Kiểm tra xem user có quyền (theo user-level) hay không.
     * @param userId ID người dùng
     * @param permissionCode mã quyền
     * @return true nếu user được cấp quyền trực tiếp
     * @throws SQLException lỗi truy vấn database
     */
    public boolean hasPermissionCode(int userId, String permissionCode) throws SQLException {
        return getPermissionOverride(userId, permissionCode).orElse(Boolean.FALSE);
    }
}

