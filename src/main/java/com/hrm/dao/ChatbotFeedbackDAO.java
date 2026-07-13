package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class ChatbotFeedbackDAO {

    public boolean saveOrUpdate(long conversationId,
                                long messageId,
                                Integer userId,
                                String rating,
                                String comment) throws SQLException {
        try (Connection connection = requireConnection()) {
            connection.setAutoCommit(false);
            try {
                if (!isOwnedBotMessage(connection, conversationId, messageId, userId)) {
                    connection.rollback();
                    return false;
                }

                Long feedbackId = findFeedbackId(
                        connection, conversationId, messageId, userId);
                if (feedbackId == null) {
                    insertFeedback(connection, conversationId, messageId, userId, rating, comment);
                } else {
                    updateFeedback(connection, feedbackId, rating, comment);
                }
                connection.commit();
                return true;
            } catch (SQLException | RuntimeException ex) {
                rollbackQuietly(connection);
                throw ex;
            }
        }
    }

    private boolean isOwnedBotMessage(Connection connection,
                                      long conversationId,
                                      long messageId,
                                      Integer userId) throws SQLException {
        String sql = """
                SELECT message.MessageID
                FROM ChatMessage message
                INNER JOIN ChatConversation conversation
                    ON conversation.ConversationID = message.ConversationID
                WHERE message.MessageID = ?
                  AND message.ConversationID = ?
                  AND message.SenderType = 'Bot'
                  AND ((conversation.UserID IS NULL AND ? IS NULL)
                       OR conversation.UserID = ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, messageId);
            statement.setLong(2, conversationId);
            setNullableInt(statement, 3, userId);
            setNullableInt(statement, 4, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }
        }
    }

    private Long findFeedbackId(Connection connection,
                                long conversationId,
                                long messageId,
                                Integer userId) throws SQLException {
        String sql = """
                SELECT FeedbackID
                FROM ChatbotFeedback
                WHERE ConversationID = ?
                  AND MessageID = ?
                  AND ((UserID IS NULL AND ? IS NULL) OR UserID = ?)
                ORDER BY FeedbackID DESC
                LIMIT 1
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, conversationId);
            statement.setLong(2, messageId);
            setNullableInt(statement, 3, userId);
            setNullableInt(statement, 4, userId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? rs.getLong("FeedbackID") : null;
            }
        }
    }

    private void insertFeedback(Connection connection,
                                long conversationId,
                                long messageId,
                                Integer userId,
                                String rating,
                                String comment) throws SQLException {
        String sql = """
                INSERT INTO ChatbotFeedback
                    (ConversationID, MessageID, UserID, Rating, Comment)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, conversationId);
            statement.setLong(2, messageId);
            setNullableInt(statement, 3, userId);
            statement.setString(4, rating);
            setNullableString(statement, 5, comment);
            statement.executeUpdate();
        }
    }

    private void updateFeedback(Connection connection,
                                long feedbackId,
                                String rating,
                                String comment) throws SQLException {
        String sql = """
                UPDATE ChatbotFeedback
                SET Rating = ?, Comment = ?, CreatedAt = CURRENT_TIMESTAMP
                WHERE FeedbackID = ?
                """;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, rating);
            setNullableString(statement, 2, comment);
            statement.setLong(3, feedbackId);
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
            statement.setString(index, value.trim());
        }
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
}
