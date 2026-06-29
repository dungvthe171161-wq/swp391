package com.hrm.dao;

import com.hrm.model.entity.Application;
import com.hrm.model.entity.Guest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDAO {

    public Application findById(int applicationId) {
        String sql = "SELECT * FROM `Application` WHERE ApplicationID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapApplication(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Application> findByGuestId(int guestId) {
        List<Application> applications = new ArrayList<>();
        String sql = """
            SELECT *
            FROM `Application`
            WHERE GuestID = ?
            ORDER BY AppliedDate DESC, ApplicationID DESC
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapApplication(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public List<CandidateApplicationView> findByUserId(int userId) {
        List<CandidateApplicationView> applications = new ArrayList<>();
        String sql = """
            SELECT a.*,
                   g.GuestID AS g_GuestID,
                   g.UserID AS g_UserID,
                   g.FullName AS g_FullName,
                   g.Email AS g_Email,
                   g.Phone AS g_Phone,
                   g.CV AS g_CV,
                   g.Avatar AS g_Avatar,
                   g.Gender AS g_Gender,
                   g.DateOfBirth AS g_DateOfBirth,
                   g.Address AS g_Address,
                   g.Status AS g_Status,
                   g.RecruitmentID AS g_RecruitmentID,
                   g.AppliedDate AS g_AppliedDate,
                   g.UpdatedDate AS g_UpdatedDate,
                   r.JobTitle,
                   r.JobDescription,
                   r.Requirement,
                   r.Location,
                   r.Salary,
                   r.Status AS RecruitmentStatus,
                   r.PostedDate
            FROM `Application` a
            JOIN Guest g ON a.GuestID = g.GuestID
            JOIN Recruitment r ON a.RecruitmentID = r.RecruitmentID
            JOIN SystemUser su ON su.UserID = ?
            WHERE (g.UserID = ? OR (g.UserID IS NULL AND g.Email = su.Email))
            ORDER BY a.AppliedDate DESC, a.ApplicationID DESC
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    applications.add(mapCandidateApplicationView(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return applications;
    }

    public boolean existsByGuestAndRecruitment(int guestId, int recruitmentId) {
        String sql = """
            SELECT COUNT(*)
            FROM `Application`
            WHERE GuestID = ? AND RecruitmentID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            ps.setInt(2, recruitmentId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean existsByUserOrEmailAndRecruitment(int userId, String email, int recruitmentId) {
        String sql = """
            SELECT COUNT(*)
            FROM `Application` a
            JOIN Guest g ON a.GuestID = g.GuestID
            WHERE a.RecruitmentID = ?
              AND (g.UserID = ? OR (g.UserID IS NULL AND g.Email = ?))
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, recruitmentId);
            ps.setInt(2, userId);
            ps.setString(3, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int create(Application application) {
        String sql = """
            INSERT INTO `Application`
                (GuestID, RecruitmentID, CandidateProfileID, AppliedDate, Status, CurrentStep, CV, CoverLetter, Note, Source)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, application.getGuestId());
            ps.setInt(2, application.getRecruitmentId());
            if (application.getCandidateProfileId() == null) {
                ps.setNull(3, Types.INTEGER);
            } else {
                ps.setInt(3, application.getCandidateProfileId());
            }
            ps.setTimestamp(4, application.getAppliedDate() != null
                    ? Timestamp.valueOf(application.getAppliedDate())
                    : null);
            ps.setString(5, application.getStatus());
            ps.setString(6, application.getCurrentStep());
            ps.setString(7, application.getCv());
            ps.setString(8, application.getCoverLetter());
            ps.setString(9, application.getNote());
            ps.setString(10, application.getSource());

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

    public boolean updateStatus(int applicationId, String status, String currentStep) {
        String sql = """
            UPDATE `Application`
            SET Status = ?, CurrentStep = ?
            WHERE ApplicationID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, currentStep);
            ps.setInt(3, applicationId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int countByUserId(int userId) {
        return countByUserIdAndStatuses(userId);
    }

    public int countActiveByUserId(int userId) {
        return countByUserIdAndStatuses(userId, "Applied", "Screening", "Interview", "Offered");
    }

    public int countByUserIdAndStatus(int userId, String status) {
        return countByUserIdAndStatuses(userId, status);
    }

    private int countByUserIdAndStatuses(int userId, String... statuses) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*)
            FROM `Application` a
            JOIN Guest g ON a.GuestID = g.GuestID
            JOIN SystemUser su ON su.UserID = ?
            WHERE (g.UserID = ? OR (g.UserID IS NULL AND g.Email = su.Email))
        """);
        if (statuses != null && statuses.length > 0) {
            sql.append(" AND a.Status IN (");
            for (int i = 0; i < statuses.length; i++) {
                sql.append(i == 0 ? "?" : ", ?");
            }
            sql.append(")");
        }
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            if (statuses != null) {
                for (int i = 0; i < statuses.length; i++) {
                    ps.setString(i + 3, statuses[i]);
                }
            }
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

    private Application mapApplication(ResultSet rs) throws SQLException {
        Application application = new Application();
        application.setApplicationId(rs.getInt("ApplicationID"));
        application.setGuestId(rs.getInt("GuestID"));
        application.setRecruitmentId(rs.getInt("RecruitmentID"));
        application.setCandidateProfileId(rs.getObject("CandidateProfileID", Integer.class));
        application.setAppliedDate(getLocalDateTime(rs, "AppliedDate"));
        application.setStatus(rs.getString("Status"));
        application.setCurrentStep(rs.getString("CurrentStep"));
        application.setCv(rs.getString("CV"));
        application.setCoverLetter(rs.getString("CoverLetter"));
        application.setNote(rs.getString("Note"));
        application.setSource(rs.getString("Source"));
        application.setCreatedDate(getLocalDateTime(rs, "CreatedDate"));
        application.setUpdatedDate(getLocalDateTime(rs, "UpdatedDate"));
        return application;
    }

    private CandidateApplicationView mapCandidateApplicationView(ResultSet rs) throws SQLException {
        CandidateApplicationView view = new CandidateApplicationView();
        view.setApplication(mapApplication(rs));
        view.setGuest(mapGuestWithPrefix(rs));
        view.setJobTitle(rs.getString("JobTitle"));
        view.setJobDescription(rs.getString("JobDescription"));
        view.setRequirement(rs.getString("Requirement"));
        view.setLocation(rs.getString("Location"));
        view.setSalary(rs.getObject("Salary", Double.class));
        view.setRecruitmentStatus(rs.getString("RecruitmentStatus"));
        view.setPostedDate(getLocalDateTime(rs, "PostedDate"));
        return view;
    }

    private Guest mapGuestWithPrefix(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setGuestId(rs.getInt("g_GuestID"));
        guest.setUserId(rs.getObject("g_UserID", Integer.class));
        guest.setFullName(rs.getString("g_FullName"));
        guest.setEmail(rs.getString("g_Email"));
        guest.setPhone(rs.getString("g_Phone"));
        guest.setCv(rs.getString("g_CV"));
        guest.setAvatar(rs.getString("g_Avatar"));
        guest.setGender(rs.getString("g_Gender"));
        java.sql.Date birthDate = rs.getDate("g_DateOfBirth");
        guest.setDateOfBirth(birthDate != null ? birthDate.toLocalDate() : null);
        guest.setAddress(rs.getString("g_Address"));
        guest.setStatus(rs.getString("g_Status"));
        guest.setRecruitmentId(rs.getObject("g_RecruitmentID", Integer.class));
        guest.setAppliedDate(getLocalDateTime(rs, "g_AppliedDate"));
        guest.setUpdatedDate(getLocalDateTime(rs, "g_UpdatedDate"));
        return guest;
    }

    private java.time.LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value != null ? value.toLocalDateTime() : null;
    }

    public static class CandidateApplicationView {
        private Application application;
        private Guest guest;
        private String jobTitle;
        private String jobDescription;
        private String requirement;
        private String location;
        private Double salary;
        private String recruitmentStatus;
        private java.time.LocalDateTime postedDate;

        public Application getApplication() {
            return application;
        }

        public void setApplication(Application application) {
            this.application = application;
        }

        public Guest getGuest() {
            return guest;
        }

        public void setGuest(Guest guest) {
            this.guest = guest;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getJobDescription() {
            return jobDescription;
        }

        public void setJobDescription(String jobDescription) {
            this.jobDescription = jobDescription;
        }

        public String getRequirement() {
            return requirement;
        }

        public void setRequirement(String requirement) {
            this.requirement = requirement;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public Double getSalary() {
            return salary;
        }

        public void setSalary(Double salary) {
            this.salary = salary;
        }

        public String getRecruitmentStatus() {
            return recruitmentStatus;
        }

        public void setRecruitmentStatus(String recruitmentStatus) {
            this.recruitmentStatus = recruitmentStatus;
        }

        public java.time.LocalDateTime getPostedDate() {
            return postedDate;
        }

        public void setPostedDate(java.time.LocalDateTime postedDate) {
            this.postedDate = postedDate;
        }
    }
}
