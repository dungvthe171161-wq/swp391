package com.hrm.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatbotReviewDAO {

    public List<ReviewItem> findRecentNeedsReview(int limit) throws SQLException {
        String sql = """
                SELECT bot.MessageID,
                       bot.ConversationID,
                       bot.Intent,
                       bot.MessageText AS BotReply,
                       bot.IsFallback,
                       bot.CreatedAt,
                       conversation.RoleName,
                       conversation.PagePath,
                       (
                           SELECT userMessage.MessageText
                           FROM ChatMessage userMessage
                           WHERE userMessage.ConversationID = bot.ConversationID
                             AND userMessage.SenderType = 'User'
                             AND userMessage.MessageID < bot.MessageID
                           ORDER BY userMessage.MessageID DESC
                           LIMIT 1
                       ) AS UserQuestion,
                       (
                           SELECT feedback.Rating
                           FROM ChatbotFeedback feedback
                           WHERE feedback.MessageID = bot.MessageID
                           ORDER BY feedback.FeedbackID DESC
                           LIMIT 1
                       ) AS Rating
                FROM ChatMessage bot
                INNER JOIN ChatConversation conversation
                    ON conversation.ConversationID = bot.ConversationID
                WHERE bot.SenderType = 'Bot'
                  AND (
                      bot.IsFallback = TRUE
                      OR EXISTS (
                          SELECT 1
                          FROM ChatbotFeedback feedbackFilter
                          WHERE feedbackFilter.MessageID = bot.MessageID
                            AND feedbackFilter.Rating = 'NotUseful'
                      )
                  )
                ORDER BY bot.CreatedAt DESC, bot.MessageID DESC
                LIMIT ?
                """;

        List<ReviewItem> items = new ArrayList<>();
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, Math.max(1, Math.min(limit, 100)));
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    items.add(mapItem(rs));
                }
            }
        }
        return items;
    }

    private ReviewItem mapItem(ResultSet rs) throws SQLException {
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        return new ReviewItem(
                rs.getLong("MessageID"),
                rs.getLong("ConversationID"),
                rs.getString("Intent"),
                rs.getString("UserQuestion"),
                rs.getString("BotReply"),
                rs.getBoolean("IsFallback"),
                rs.getString("Rating"),
                rs.getString("RoleName"),
                rs.getString("PagePath"),
                createdAt != null ? createdAt.toLocalDateTime() : null
        );
    }

    private Connection requireConnection() throws SQLException {
        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            throw new SQLException("Cannot connect to the chatbot database.");
        }
        return connection;
    }

    public static class ReviewItem {
        private final long messageId;
        private final long conversationId;
        private final String intent;
        private final String userQuestion;
        private final String botReply;
        private final boolean fallback;
        private final String rating;
        private final String roleName;
        private final String pagePath;
        private final LocalDateTime createdAt;

        public ReviewItem(long messageId,
                          long conversationId,
                          String intent,
                          String userQuestion,
                          String botReply,
                          boolean fallback,
                          String rating,
                          String roleName,
                          String pagePath,
                          LocalDateTime createdAt) {
            this.messageId = messageId;
            this.conversationId = conversationId;
            this.intent = intent;
            this.userQuestion = userQuestion;
            this.botReply = botReply;
            this.fallback = fallback;
            this.rating = rating;
            this.roleName = roleName;
            this.pagePath = pagePath;
            this.createdAt = createdAt;
        }

        public long getMessageId() {
            return messageId;
        }

        public long getConversationId() {
            return conversationId;
        }

        public String getIntent() {
            return intent;
        }

        public String getUserQuestion() {
            return userQuestion;
        }

        public String getBotReply() {
            return botReply;
        }

        public boolean isFallback() {
            return fallback;
        }

        public String getRating() {
            return rating;
        }

        public String getRoleName() {
            return roleName;
        }

        public String getPagePath() {
            return pagePath;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}
