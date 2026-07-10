package com.hrm.dao;

import com.hrm.model.entity.Offer;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OfferDAO {

    public Offer findByApplicationId(int applicationId) {
        String sql = "SELECT * FROM `Offer` WHERE ApplicationID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapOffer(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<OfferView> findPendingByUserId(int userId) {
        List<OfferView> offers = new ArrayList<>();
        String sql = """
            SELECT o.*,
                   r.JobTitle
            FROM `Offer` o
            JOIN `Application` a ON o.ApplicationID = a.ApplicationID
            JOIN Guest g ON a.GuestID = g.GuestID
            JOIN Recruitment r ON a.RecruitmentID = r.RecruitmentID
            JOIN SystemUser su ON su.UserID = ?
            WHERE o.Status = 'Sent'
              AND (o.ExpiredAt IS NULL OR o.ExpiredAt >= NOW())
              AND (g.UserID = ? OR (g.UserID IS NULL AND g.Email = su.Email))
            ORDER BY o.SentAt DESC, o.OfferID DESC
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OfferView view = new OfferView();
                    view.setOffer(mapOffer(rs));
                    view.setJobTitle(rs.getString("JobTitle"));
                    offers.add(view);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return offers;
    }

    public int create(Offer offer) {
        String sql = """
            INSERT INTO `Offer`
                (ApplicationID, Position, OfferedSalary, StartDate, ExpiredAt,
                 Status, SentAt, RespondedAt, Note)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setOfferParams(ps, offer);
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

    public boolean sendOffer(int offerId) {
        String sql = """
            UPDATE `Offer`
            SET Status = 'Sent', SentAt = COALESCE(SentAt, NOW())
            WHERE OfferID = ? AND Status = 'Draft'
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offerId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean respondOffer(int offerId, int userId, String status) {
        if (!"Accepted".equals(status) && !"Rejected".equals(status)) {
            return false;
        }

        String selectSql = """
            SELECT o.ApplicationID
            FROM `Offer` o
            JOIN `Application` a ON o.ApplicationID = a.ApplicationID
            JOIN Guest g ON a.GuestID = g.GuestID
            JOIN SystemUser su ON su.UserID = ?
            WHERE o.OfferID = ?
              AND o.Status = 'Sent'
              AND (o.ExpiredAt IS NULL OR o.ExpiredAt >= NOW())
              AND (g.UserID = ? OR (g.UserID IS NULL AND g.Email = su.Email))
        """;
        String updateOfferSql = """
            UPDATE `Offer`
            SET Status = ?, RespondedAt = NOW()
            WHERE OfferID = ?
        """;
        String updateApplicationSql = """
            UPDATE `Application`
            SET Status = ?, CurrentStep = ?
            WHERE ApplicationID = ?
        """;

        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int applicationId = 0;
                try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                    ps.setInt(1, userId);
                    ps.setInt(2, offerId);
                    ps.setInt(3, userId);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            applicationId = rs.getInt("ApplicationID");
                        }
                    }
                }

                if (applicationId == 0) {
                    con.rollback();
                    return false;
                }

                try (PreparedStatement ps = con.prepareStatement(updateOfferSql)) {
                    ps.setString(1, status);
                    ps.setInt(2, offerId);
                    ps.executeUpdate();
                }

                String applicationStatus = "Accepted".equals(status) ? "Hired" : "Rejected";
                String currentStep = "Accepted".equals(status) ? "Hired" : "Rejected";
                try (PreparedStatement ps = con.prepareStatement(updateApplicationSql)) {
                    ps.setString(1, applicationStatus);
                    ps.setString(2, currentStep);
                    ps.setInt(3, applicationId);
                    ps.executeUpdate();
                }

                con.commit();
                return true;
            } catch (SQLException ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int expireOverdueOffers() {
        String sql = """
            UPDATE `Offer`
            SET Status = 'Expired'
            WHERE Status = 'Sent'
              AND ExpiredAt IS NOT NULL
              AND ExpiredAt < NOW()
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void setOfferParams(PreparedStatement ps, Offer offer) throws SQLException {
        ps.setInt(1, offer.getApplicationId());
        ps.setString(2, offer.getPosition());
        ps.setBigDecimal(3, offer.getOfferedSalary());
        ps.setDate(4, offer.getStartDate() != null ? Date.valueOf(offer.getStartDate()) : null);
        ps.setTimestamp(5, offer.getExpiredAt() != null ? Timestamp.valueOf(offer.getExpiredAt()) : null);
        ps.setString(6, offer.getStatus());
        ps.setTimestamp(7, offer.getSentAt() != null ? Timestamp.valueOf(offer.getSentAt()) : null);
        ps.setTimestamp(8, offer.getRespondedAt() != null ? Timestamp.valueOf(offer.getRespondedAt()) : null);
        ps.setString(9, offer.getNote());
    }

    private Offer mapOffer(ResultSet rs) throws SQLException {
        Offer offer = new Offer();
        offer.setOfferId(rs.getInt("OfferID"));
        offer.setApplicationId(rs.getInt("ApplicationID"));
        offer.setPosition(rs.getString("Position"));
        offer.setOfferedSalary(rs.getBigDecimal("OfferedSalary"));
        Date startDate = rs.getDate("StartDate");
        offer.setStartDate(startDate != null ? startDate.toLocalDate() : null);
        offer.setExpiredAt(getLocalDateTime(rs, "ExpiredAt"));
        offer.setStatus(rs.getString("Status"));
        offer.setSentAt(getLocalDateTime(rs, "SentAt"));
        offer.setRespondedAt(getLocalDateTime(rs, "RespondedAt"));
        offer.setNote(rs.getString("Note"));
        offer.setCreatedDate(getLocalDateTime(rs, "CreatedDate"));
        offer.setUpdatedDate(getLocalDateTime(rs, "UpdatedDate"));
        return offer;
    }

    private java.time.LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value != null ? value.toLocalDateTime() : null;
    }

    public static class OfferView {
        private Offer offer;
        private String jobTitle;

        public Offer getOffer() {
            return offer;
        }

        public void setOffer(Offer offer) {
            this.offer = offer;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }
    }
}
