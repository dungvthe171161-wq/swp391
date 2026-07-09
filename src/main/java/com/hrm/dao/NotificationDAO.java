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
            INSERT INTO `Notification`
                (UserID, ActorUserID, ApplicationID, EntityType, EntityID,
                 Title, Message, Type, TargetUrl, Priority, IsRead, ExpiresAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCreateParams(ps, notification);
            return executeCreate(ps);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int create(Connection con, Notification notification) throws SQLException {
        String sql = """
            INSERT INTO `Notification`
                (UserID, ActorUserID, ApplicationID, EntityType, EntityID,
                 Title, Message, Type, TargetUrl, Priority, IsRead, ExpiresAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            bindCreateParams(ps, notification);
            return executeCreate(ps);
        }
    }

    public int createForUsers(List<Integer> userIds, Notification template) {
        if (userIds == null || userIds.isEmpty() || template == null) {
            return 0;
        }
        int created = 0;
        for (Integer userId : userIds) {
            if (userId == null || userId <= 0) {
                continue;
            }
            Notification notification = copyForUser(template, userId);
            if (create(notification) > 0) {
                created++;
            }
        }
        return created;
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
        notification.setActorUserId(getIntegerIfPresent(rs, "ActorUserID"));
        notification.setApplicationId(rs.getObject("ApplicationID", Integer.class));
        notification.setEntityType(getStringIfPresent(rs, "EntityType"));
        notification.setEntityId(getIntegerIfPresent(rs, "EntityID"));
        notification.setTitle(rs.getString("Title"));
        notification.setMessage(rs.getString("Message"));
        notification.setType(rs.getString("Type"));
        notification.setTargetUrl(getStringIfPresent(rs, "TargetUrl"));
        notification.setPriority(getStringIfPresent(rs, "Priority"));
        notification.setRead(rs.getBoolean("IsRead"));
        notification.setCreatedDate(getLocalDateTime(rs, "CreatedDate"));
        notification.setReadDate(getLocalDateTime(rs, "ReadDate"));
        notification.setExpiresAt(getLocalDateTimeIfPresent(rs, "ExpiresAt"));
        return notification;
    }

    private void bindCreateParams(PreparedStatement ps, Notification notification) throws SQLException {
        ps.setInt(1, notification.getUserId());
        setNullableInt(ps, 2, notification.getActorUserId());
        setNullableInt(ps, 3, notification.getApplicationId());
        ps.setString(4, notification.getEntityType());
        setNullableInt(ps, 5, notification.getEntityId());
        ps.setString(6, notification.getTitle());
        ps.setString(7, notification.getMessage());
        ps.setString(8, notification.getType());
        ps.setString(9, notification.getTargetUrl());
        ps.setString(10, notification.getPriority() == null ? "Normal" : notification.getPriority());
        ps.setBoolean(11, notification.isRead());
        ps.setTimestamp(12, notification.getExpiresAt() != null
                ? Timestamp.valueOf(notification.getExpiresAt())
                : null);
    }

    private int executeCreate(PreparedStatement ps) throws SQLException {
        if (ps.executeUpdate() == 0) {
            return 0;
        }
        try (ResultSet keys = ps.getGeneratedKeys()) {
            if (keys.next()) {
                return keys.getInt(1);
            }
        }
        return 0;
    }

    private Notification copyForUser(Notification template, int userId) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setActorUserId(template.getActorUserId());
        notification.setApplicationId(template.getApplicationId());
        notification.setEntityType(template.getEntityType());
        notification.setEntityId(template.getEntityId());
        notification.setTitle(template.getTitle());
        notification.setMessage(template.getMessage());
        notification.setType(template.getType());
        notification.setTargetUrl(template.getTargetUrl());
        notification.setPriority(template.getPriority());
        notification.setRead(template.isRead());
        notification.setExpiresAt(template.getExpiresAt());
        return notification;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private String getStringIfPresent(ResultSet rs, String column) throws SQLException {
        return hasColumn(rs, column) ? rs.getString(column) : null;
    }

    private Integer getIntegerIfPresent(ResultSet rs, String column) throws SQLException {
        if (!hasColumn(rs, column)) {
            return null;
        }
        return rs.getObject(column, Integer.class);
    }

    private java.time.LocalDateTime getLocalDateTimeIfPresent(ResultSet rs, String column) throws SQLException {
        return hasColumn(rs, column) ? getLocalDateTime(rs, column) : null;
    }

    private java.time.LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value != null ? value.toLocalDateTime() : null;
    }

    private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
        java.sql.ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            if (columnName.equalsIgnoreCase(metaData.getColumnLabel(i))) {
                return true;
            }
        }
        return false;
    }
}
