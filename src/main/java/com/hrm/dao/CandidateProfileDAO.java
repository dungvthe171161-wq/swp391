package com.hrm.dao;

import com.hrm.model.entity.CandidateProfile;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CandidateProfileDAO {

    public CandidateProfile findById(int candidateProfileId) {
        String sql = "SELECT * FROM CandidateProfile WHERE CandidateProfileID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, candidateProfileId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCandidateProfile(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CandidateProfile findByGuestId(int guestId) {
        String sql = "SELECT * FROM CandidateProfile WHERE GuestID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCandidateProfile(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public CandidateProfile findByUserId(int userId) {
        String sql = """
            SELECT cp.*
            FROM CandidateProfile cp
            JOIN Guest g ON cp.GuestID = g.GuestID
            WHERE g.UserID = ?
            ORDER BY cp.UpdatedDate DESC, cp.CandidateProfileID DESC
            LIMIT 1
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCandidateProfile(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int save(CandidateProfile profile) {
        CandidateProfile existing = findByGuestId(profile.getGuestId());
        if (existing == null) {
            return insert(profile);
        }
        profile.setCandidateProfileId(existing.getCandidateProfileId());
        if (profile.getCvFilePath() == null || profile.getCvFilePath().isBlank()) {
            profile.setCvFilePath(existing.getCvFilePath());
        }
        if (!profile.isEmailVerified()
                && existing.isEmailVerified()
                && equalsIgnoreCase(existing.getEmail(), profile.getEmail())) {
            profile.setEmailVerified(true);
            profile.setEmailVerifiedAt(existing.getEmailVerifiedAt());
        }
        return update(profile) ? profile.getCandidateProfileId() : 0;
    }

    public int insert(CandidateProfile profile) {
        String sql = """
            INSERT INTO CandidateProfile
                (GuestID, FullName, Phone, Email, DateOfBirth, Address, DesiredPosition,
                 ExpectedSalary, WorkExperience, CVFilePath, EmailVerified, EmailVerifiedAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setProfileParameters(ps, profile);
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

    public boolean update(CandidateProfile profile) {
        String sql = """
            UPDATE CandidateProfile
            SET GuestID=?, FullName=?, Phone=?, Email=?, DateOfBirth=?, Address=?,
                DesiredPosition=?, ExpectedSalary=?, WorkExperience=?, CVFilePath=?,
                EmailVerified=?, EmailVerifiedAt=?
            WHERE CandidateProfileID=?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setProfileParameters(ps, profile);
            ps.setInt(13, profile.getCandidateProfileId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setProfileParameters(PreparedStatement ps, CandidateProfile profile) throws SQLException {
        ps.setInt(1, profile.getGuestId());
        ps.setString(2, profile.getFullName());
        ps.setString(3, profile.getPhone());
        ps.setString(4, profile.getEmail());
        setNullableDate(ps, 5, profile.getDateOfBirth());
        ps.setString(6, profile.getAddress());
        ps.setString(7, profile.getDesiredPosition());
        if (profile.getExpectedSalary() == null) {
            ps.setNull(8, Types.DECIMAL);
        } else {
            ps.setBigDecimal(8, profile.getExpectedSalary());
        }
        ps.setString(9, profile.getWorkExperience());
        ps.setString(10, profile.getCvFilePath());
        ps.setBoolean(11, profile.isEmailVerified());
        if (profile.getEmailVerifiedAt() == null) {
            ps.setNull(12, Types.TIMESTAMP);
        } else {
            ps.setTimestamp(12, Timestamp.valueOf(profile.getEmailVerifiedAt()));
        }
    }

    private CandidateProfile mapCandidateProfile(ResultSet rs) throws SQLException {
        CandidateProfile profile = new CandidateProfile();
        profile.setCandidateProfileId(rs.getInt("CandidateProfileID"));
        profile.setGuestId(rs.getInt("GuestID"));
        profile.setFullName(rs.getString("FullName"));
        profile.setPhone(rs.getString("Phone"));
        profile.setEmail(rs.getString("Email"));
        profile.setDateOfBirth(getLocalDate(rs, "DateOfBirth"));
        profile.setAddress(rs.getString("Address"));
        profile.setDesiredPosition(rs.getString("DesiredPosition"));
        profile.setExpectedSalary(rs.getBigDecimal("ExpectedSalary"));
        profile.setWorkExperience(rs.getString("WorkExperience"));
        profile.setCvFilePath(rs.getString("CVFilePath"));
        profile.setEmailVerified(rs.getBoolean("EmailVerified"));
        profile.setEmailVerifiedAt(getLocalDateTime(rs, "EmailVerifiedAt"));
        profile.setCreatedDate(getLocalDateTime(rs, "CreatedDate"));
        profile.setUpdatedDate(getLocalDateTime(rs, "UpdatedDate"));
        return profile;
    }

    private LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        Date value = rs.getDate(column);
        return value != null ? value.toLocalDate() : null;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value != null ? value.toLocalDateTime() : null;
    }

    private void setNullableDate(PreparedStatement ps, int index, LocalDate value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    private boolean equalsIgnoreCase(String left, String right) {
        if (left == null || right == null) {
            return left == right;
        }
        return left.equalsIgnoreCase(right);
    }
}
