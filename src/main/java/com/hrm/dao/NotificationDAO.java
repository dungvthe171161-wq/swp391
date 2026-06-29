package com.hrm.dao;

import com.hrm.model.entity.Notification;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public List<Notification> findByUserId(int userId, int limit) {
        List<Notification> notifications = new ArrayList<>();
        String sql = """
            SELECT *
            FROM `Notification`
            WHERE UserID = ?
            ORDER BY IsRead ASC, CreatedDate DESC, NotificationID DESC
            LIMIT ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapNotification(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public int countUnreadByUserId(int userId) {
        String sql = "SELECT COUNT(*) FROM `Notification` WHERE UserID = ? AND IsRead = FALSE";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int create(Notification notification) {
        String sql = """
            INSERT INTO `Notification` (UserID, ApplicationID, Title, Message, Type, IsRead)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, notification.getUserId());
            if (notification.getApplicationId() == null) {
                ps.setNull(2, Types.INTEGER);
            } else {
                ps.setInt(2, notification.getApplicationId());
            }
            ps.setString(3, notification.getTitle());
            ps.setString(4, notification.getMessage());
            ps.setString(5, notification.getType());
            ps.setBoolean(6, notification.isRead());

            if (ps.executeUpdate() == 0) {
                return 0;
            }
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean markRead(int notificationId, int userId) {
        String sql = """
            UPDATE `Notification`
            SET IsRead = TRUE, ReadDate = NOW()
            WHERE NotificationID = ? AND UserID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, notificationId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean markAllRead(int userId) {
        String sql = """
            UPDATE `Notification`
            SET IsRead = TRUE, ReadDate = NOW()
            WHERE UserID = ? AND IsRead = FALSE
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            return ps.executeUpdate() >= 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Notification mapNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("NotificationID"));
        notification.setUserId(rs.getInt("UserID"));
        notification.setApplicationId(rs.getObject("ApplicationID", Integer.class));
        notification.setTitle(rs.getString("Title"));
        notification.setMessage(rs.getString("Message"));
        notification.setType(rs.getString("Type"));
        notification.setRead(rs.getBoolean("IsRead"));
        notification.setCreatedDate(getLocalDateTime(rs, "CreatedDate"));
        notification.setReadDate(getLocalDateTime(rs, "ReadDate"));
        return notification;
    }

    private java.time.LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value != null ? value.toLocalDateTime() : null;
    }
}
