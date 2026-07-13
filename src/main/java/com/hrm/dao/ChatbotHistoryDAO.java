package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class ChatbotHistoryDAO {

    public RecordResult recordExchange(Long existingConversationId,
                                       Integer userId,
                                       String roleName,
                                       String pagePath,
                                       String userMessage,
                                       String intent,
                                       String botReply,
                                       boolean fallback) throws SQLException {
        try (Connection connection = requireConnection()) {
            connection.setAutoCommit(false);
            try {
                long conversationId = resolveConversation(
                        connection, existingConversationId, userId, roleName, pagePath);
                insertMessage(connection, conversationId, "User", null, userMessage, false);
                long botMessageId = insertMessage(
                        connection, conversationId, "Bot", intent, botReply, fallback);
                touchConversation(connection, conversationId, roleName, pagePath);
                connection.commit();
                return new RecordResult(conversationId, botMessageId);
            } catch (SQLException | RuntimeException ex) {
                rollbackQuietly(connection);
                throw ex;
            }
        }
    }

    private long resolveConversation(Connection connection,
                                     Long existingConversationId,
                                     Integer userId,
                                     String roleName,
                                     String pagePath) throws SQLException {
        if (existingConversationId != null
                && isReusable(connection, existingConversationId, userId)) {
            return existingConversationId;
        }
        return createConversation(connection, userId, roleName, pagePath);
    }

    private boolean isReusable(Connection connection, long conversationId, Integer userId)
            throws SQLException {
        String sql = """
                SELECT ConversationID
                FROM ChatConversation
                WHERE ConversationID = ?
                  AND Status = 'Open'
                  AND ((UserID IS NULL AND ? IS NULL) OR UserID = ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, conversationId);
            setNullableInt(statement, 2, userId);
            setNullableInt(statement, 3, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private long createConversation(Connection connection,
                                    Integer userId,
                                    String roleName,
                                    String pagePath) throws SQLException {
        String sql = """
                INSERT INTO ChatConversation (UserID, RoleName, PagePath, Channel)
                VALUES (?, ?, ?, 'web')
                """;
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            setNullableInt(statement, 1, userId);
            setNullableString(statement, 2, limit(roleName, 100));
            setNullableString(statement, 3, limit(pagePath, 255));
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Cannot create chatbot conversation.");
    }

    private long insertMessage(Connection connection,
                               long conversationId,
                               String senderType,
                               String intent,
                               String message,
                               boolean fallback) throws SQLException {
        String sql = """
                INSERT INTO ChatMessage
                    (ConversationID, SenderType, Intent, MessageText, IsFallback)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setLong(1, conversationId);
            statement.setString(2, senderType);
            setNullableString(statement, 3, intent);
            statement.setString(4, message);
            statement.setBoolean(5, fallback);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getLong(1);
                }
            }
        }
        throw new SQLException("Cannot create chatbot message.");
    }

    private void touchConversation(Connection connection,
                                   long conversationId,
                                   String roleName,
                                   String pagePath) throws SQLException {
        String sql = """
                UPDATE ChatConversation
                SET RoleName = ?, PagePath = ?, LastMessageAt = CURRENT_TIMESTAMP
                WHERE ConversationID = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setNullableString(statement, 1, limit(roleName, 100));
            setNullableString(statement, 2, limit(pagePath, 255));
            statement.setLong(3, conversationId);
            statement.executeUpdate();
        }
    }

    private void setNullableInt(PreparedStatement statement, int index, Integer value)
            throws SQLException {
        if (value == null || value <= 0) {
            statement.setNull(index, Types.INTEGER);
        } else {
            statement.setInt(index, value);
        }
    }

    private void setNullableString(PreparedStatement statement, int index, String value)
            throws SQLException {
        if (value == null || value.isBlank()) {
            statement.setNull(index, Types.VARCHAR);
        } else {
            statement.setString(index, value);
        }
    }

    private String limit(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.length() <= maxLength ? trimmed : trimmed.substring(0, maxLength);
    }

    private Connection requireConnection() throws SQLException {
        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            throw new SQLException("Cannot connect to the chatbot database.");
        }
        return connection;
    }

    private void rollbackQuietly(Connection connection) {
        try {
            connection.rollback();
        } catch (SQLException ignored) {
            // Preserve the original database exception.
        }
    }

    public static class RecordResult {
        private final long conversationId;
        private final long botMessageId;

        public RecordResult(long conversationId, long botMessageId) {
            this.conversationId = conversationId;
            this.botMessageId = botMessageId;
        }

        public long getConversationId() {
            return conversationId;
        }

        public long getBotMessageId() {
            return botMessageId;
        }
    }
}
