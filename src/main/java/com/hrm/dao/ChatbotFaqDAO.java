package com.hrm.dao;

import com.hrm.model.entity.ChatbotFaq;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ChatbotFaqDAO {

    private static final Logger LOGGER = Logger.getLogger(ChatbotFaqDAO.class.getName());

    public Optional<FaqAnswer> findActiveAnswer(String intent, String roleName) {
        if (intent == null || intent.isBlank()) {
            return Optional.empty();
        }

        String audience = audienceRole(roleName);
        String sql = """
                SELECT Answer, Suggestions
                FROM ChatbotFaq
                WHERE Intent = ?
                  AND IsActive = TRUE
                  AND AudienceRole IN (?, 'All')
                ORDER BY
                    CASE
                        WHEN AudienceRole = ? THEN 0
                        WHEN AudienceRole = 'All' THEN 1
                        ELSE 2
                    END,
                    SortOrder ASC,
                    FaqID ASC
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection()) {
            if (connection == null) {
                return Optional.empty();
            }
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, intent);
                statement.setString(2, audience);
                statement.setString(3, audience);
                try (ResultSet rs = statement.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(new FaqAnswer(
                                rs.getString("Answer"),
                                parseSuggestions(rs.getString("Suggestions"))
                        ));
                    }
                }
            }
        } catch (SQLException | RuntimeException ex) {
            LOGGER.log(Level.WARNING, "Cannot load chatbot FAQ from database; using static fallback.", ex);
        }
        return Optional.empty();
    }

    public List<ChatbotFaq> findAll(String search, String audienceRole, Boolean active)
            throws SQLException {
        boolean hasSearch = search != null && !search.isBlank();
        boolean hasAudience = audienceRole != null && !audienceRole.isBlank();

        StringBuilder sql = new StringBuilder("""
                SELECT FaqID, Intent, AudienceRole, Question, Answer, Suggestions,
                       SortOrder, IsActive, CreatedBy, CreatedAt, UpdatedBy, UpdatedAt
                FROM ChatbotFaq
                WHERE 1 = 1
                """);
        if (hasSearch) {
            sql.append(" AND (Intent LIKE ? OR Question LIKE ? OR Answer LIKE ?)");
        }
        if (hasAudience) {
            sql.append(" AND AudienceRole = ?");
        }
        if (active != null) {
            sql.append(" AND IsActive = ?");
        }
        sql.append(" ORDER BY IsActive DESC, SortOrder ASC, FaqID ASC");

        List<ChatbotFaq> faqs = new ArrayList<>();
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            int index = 1;
            if (hasSearch) {
                String keyword = "%" + search.trim() + "%";
                statement.setString(index++, keyword);
                statement.setString(index++, keyword);
                statement.setString(index++, keyword);
            }
            if (hasAudience) {
                statement.setString(index++, audienceRole.trim());
            }
            if (active != null) {
                statement.setBoolean(index, active);
            }
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    faqs.add(mapFaq(rs));
                }
            }
        }
        return faqs;
    }

    public Optional<ChatbotFaq> findById(int faqId) throws SQLException {
        String sql = """
                SELECT FaqID, Intent, AudienceRole, Question, Answer, Suggestions,
                       SortOrder, IsActive, CreatedBy, CreatedAt, UpdatedBy, UpdatedAt
                FROM ChatbotFaq
                WHERE FaqID = ?
                """;
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, faqId);
            try (ResultSet rs = statement.executeQuery()) {
                return rs.next() ? Optional.of(mapFaq(rs)) : Optional.empty();
            }
        }
    }

    public int create(ChatbotFaq faq, int userId) throws SQLException {
        String sql = """
                INSERT INTO ChatbotFaq
                    (Intent, AudienceRole, Question, Answer, Suggestions, SortOrder,
                     IsActive, CreatedBy, UpdatedBy)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
            setFaqValues(statement, faq);
            setNullableUserId(statement, 8, userId);
            setNullableUserId(statement, 9, userId);
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                return keys.next() ? keys.getInt(1) : 0;
            }
        }
    }

    public boolean update(ChatbotFaq faq, int userId) throws SQLException {
        String sql = """
                UPDATE ChatbotFaq
                SET Intent = ?, AudienceRole = ?, Question = ?, Answer = ?, Suggestions = ?,
                    SortOrder = ?, IsActive = ?, UpdatedBy = ?
                WHERE FaqID = ?
                """;
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setFaqValues(statement, faq);
            setNullableUserId(statement, 8, userId);
            statement.setInt(9, faq.getFaqId());
            return statement.executeUpdate() > 0;
        }
    }

    public boolean setActive(int faqId, boolean active, int userId) throws SQLException {
        String sql = """
                UPDATE ChatbotFaq
                SET IsActive = ?, UpdatedBy = ?
                WHERE FaqID = ?
                """;
        try (Connection connection = requireConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, active);
            setNullableUserId(statement, 2, userId);
            statement.setInt(3, faqId);
            return statement.executeUpdate() > 0;
        }
    }

    private void setFaqValues(PreparedStatement statement, ChatbotFaq faq) throws SQLException {
        statement.setString(1, faq.getIntent());
        statement.setString(2, faq.getAudienceRole());
        statement.setString(3, faq.getQuestion());
        statement.setString(4, faq.getAnswer());
        if (faq.getSuggestions() == null || faq.getSuggestions().isBlank()) {
            statement.setNull(5, Types.VARCHAR);
        } else {
            statement.setString(5, faq.getSuggestions().trim());
        }
        statement.setInt(6, faq.getSortOrder());
        statement.setBoolean(7, faq.isActive());
    }

    private void setNullableUserId(PreparedStatement statement, int index, int userId)
            throws SQLException {
        if (userId > 0) {
            statement.setInt(index, userId);
        } else {
            statement.setNull(index, Types.INTEGER);
        }
    }

    private ChatbotFaq mapFaq(ResultSet rs) throws SQLException {
        ChatbotFaq faq = new ChatbotFaq();
        faq.setFaqId(rs.getInt("FaqID"));
        faq.setIntent(rs.getString("Intent"));
        faq.setAudienceRole(rs.getString("AudienceRole"));
        faq.setQuestion(rs.getString("Question"));
        faq.setAnswer(rs.getString("Answer"));
        faq.setSuggestions(rs.getString("Suggestions"));
        faq.setSortOrder(rs.getInt("SortOrder"));
        faq.setActive(rs.getBoolean("IsActive"));
        faq.setCreatedBy(nullableInt(rs, "CreatedBy"));
        faq.setUpdatedBy(nullableInt(rs, "UpdatedBy"));
        if (rs.getTimestamp("CreatedAt") != null) {
            faq.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
        }
        if (rs.getTimestamp("UpdatedAt") != null) {
            faq.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
        }
        return faq;
    }

    private Integer nullableInt(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    private Connection requireConnection() throws SQLException {
        Connection connection = DBConnection.getConnection();
        if (connection == null) {
            throw new SQLException("Cannot connect to the chatbot database.");
        }
        return connection;
    }

    private List<String> parseSuggestions(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split("\\|"))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .collect(Collectors.toList());
    }

    private String audienceRole(String roleName) {
        if (roleName == null || roleName.isBlank()) {
            return "Public";
        }
        String normalized = roleName.trim().toLowerCase();
        if (normalized.contains("employee") || normalized.contains("nhan vien")) {
            return "Employee";
        }
        if (normalized.contains("dept") || normalized.contains("department") || normalized.contains("truong phong")) {
            return "Dept Manager";
        }
        if (normalized.contains("hr staff") || normalized.contains("hrstaff")) {
            return "HR Staff";
        }
        if (normalized.contains("hr manager") || normalized.contains("hrmanager")) {
            return "HR Manager";
        }
        if (normalized.contains("admin")) {
            return "Admin";
        }
        if (normalized.contains("guest") || normalized.contains("public") || normalized.contains("candidate")) {
            return "Public";
        }
        return roleName.trim();
    }

    public static class FaqAnswer {
        private final String answer;
        private final List<String> suggestions;

        public FaqAnswer(String answer, List<String> suggestions) {
            this.answer = answer;
            this.suggestions = suggestions;
        }

        public String getAnswer() {
            return answer;
        }

        public List<String> getSuggestions() {
            return suggestions;
        }
    }
}
