package com.hrm.dao;

import com.hrm.model.entity.Guest;
import com.hrm.model.entity.SystemUser;
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
import java.util.ArrayList;
import java.util.List;

public class GuestDAO {

    public List<Guest> getAll() {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM Guest ORDER BY GuestID";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapGuest(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Guest getById(int guestId) {
        String sql = "SELECT * FROM Guest WHERE GuestID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapGuest(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guest getByEmail(String email) {
        String sql = "SELECT * FROM Guest WHERE Email = ? ORDER BY GuestID DESC LIMIT 1";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapGuest(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guest findByEmail(String email) {
        return getByEmail(email);
    }

    public Guest findByUserId(int userId) {
        String sql = """
            SELECT *
            FROM Guest
            WHERE UserID = ?
            ORDER BY CASE WHEN RecruitmentID IS NULL THEN 0 ELSE 1 END,
                     COALESCE(UpdatedDate, AppliedDate) DESC,
                     GuestID DESC
            LIMIT 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapGuest(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guest findProfileByUserOrEmail(int userId, String email) {
        String sql = """
            SELECT *
            FROM Guest
            WHERE UserID = ? OR (UserID IS NULL AND Email = ?)
            ORDER BY CASE WHEN RecruitmentID IS NULL THEN 0 ELSE 1 END,
                     COALESCE(UpdatedDate, AppliedDate) DESC,
                     GuestID DESC
            LIMIT 1
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapGuest(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Guest findOrCreateProfileForUser(SystemUser user) {
        if (user == null) {
            return null;
        }

        Guest profile = findProfileByUserOrEmail(user.getUserId(), user.getEmail());
        if (profile != null) {
            if (profile.getUserId() == null) {
                profile.setUserId(user.getUserId());
                if (profile.getEmail() == null || profile.getEmail().isBlank()) {
                    profile.setEmail(user.getEmail());
                }
                updateProfile(profile);
                profile = getById(profile.getGuestId());
            }
            return profile;
        }

        Guest newProfile = new Guest();
        newProfile.setUserId(user.getUserId());
        newProfile.setFullName(firstNonBlank(user.getUsername(), user.getEmail(), "Ứng viên BetterHR"));
        newProfile.setEmail(user.getEmail());
        newProfile.setAvatar(user.getAvatarUrl());
        newProfile.setStatus("Processing");
        if (insertProfile(newProfile)) {
            return findByUserId(user.getUserId());
        }
        return null;
    }

    public List<Guest> getByRecruitmentId(int recruitmentId) {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM Guest WHERE RecruitmentID = ? ORDER BY AppliedDate DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, recruitmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGuest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Guest> getByStatus(String status) {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM Guest WHERE Status = ? ORDER BY AppliedDate DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGuest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<GuestApplication> getApplicationsByUserOrEmail(int userId, String email) {
        List<GuestApplication> list = new ArrayList<>();
        String sql = """
            SELECT g.*,
                   r.JobTitle,
                   r.JobDescription,
                   r.Requirement,
                   r.Location,
                   r.Salary,
                   r.Status AS RecruitmentStatus,
                   r.PostedDate
            FROM Guest g
            LEFT JOIN Recruitment r ON g.RecruitmentID = r.RecruitmentID
            WHERE g.RecruitmentID IS NOT NULL
              AND (g.UserID = ? OR (g.UserID IS NULL AND g.Email = ?))
            ORDER BY g.AppliedDate DESC, g.GuestID DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, email);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGuestApplication(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int countApplicationsByUserOrEmail(int userId, String email) {
        String sql = """
            SELECT COUNT(*)
            FROM Guest
            WHERE RecruitmentID IS NOT NULL
              AND (UserID = ? OR (UserID IS NULL AND Email = ?))
        """;
        return countByUserQuery(sql, userId, email, null);
    }

    public int countApplicationsByUserOrEmailAndStatus(int userId, String email, String status) {
        String sql = """
            SELECT COUNT(*)
            FROM Guest
            WHERE RecruitmentID IS NOT NULL
              AND (UserID = ? OR (UserID IS NULL AND Email = ?))
              AND Status = ?
        """;
        return countByUserQuery(sql, userId, email, status);
    }

    public boolean insert(Guest guest) {
        String sql = """
            INSERT INTO Guest
                (UserID, FullName, Email, Phone, CV, Avatar, Gender, DateOfBirth, Address,
                 Status, RecruitmentID, AppliedDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableInt(ps, 1, guest.getUserId());
            ps.setString(2, guest.getFullName());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getPhone());
            ps.setString(5, guest.getCv());
            ps.setString(6, guest.getAvatar());
            ps.setString(7, guest.getGender());
            setNullableDate(ps, 8, guest.getDateOfBirth());
            ps.setString(9, guest.getAddress());
            ps.setString(10, guest.getStatus());
            setNullableInt(ps, 11, guest.getRecruitmentId());
            ps.setTimestamp(12, guest.getAppliedDate() != null
                    ? Timestamp.valueOf(guest.getAppliedDate())
                    : Timestamp.valueOf(LocalDateTime.now()));

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean insertProfile(Guest guest) {
        String sql = """
            INSERT INTO Guest
                (UserID, FullName, Email, Phone, Avatar, Gender, DateOfBirth, Address,
                 Status, RecruitmentID, AppliedDate)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, 'Processing', NULL, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableInt(ps, 1, guest.getUserId());
            ps.setString(2, guest.getFullName());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getPhone());
            ps.setString(5, guest.getAvatar());
            ps.setString(6, guest.getGender());
            setNullableDate(ps, 7, guest.getDateOfBirth());
            ps.setString(8, guest.getAddress());
            ps.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Guest guest) {
        String sql = """
            UPDATE Guest
            SET UserID=?, FullName=?, Email=?, Phone=?, CV=?, Avatar=?, Gender=?,
                DateOfBirth=?, Address=?, Status=?, RecruitmentID=?, AppliedDate=?
            WHERE GuestID=?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableInt(ps, 1, guest.getUserId());
            ps.setString(2, guest.getFullName());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getPhone());
            ps.setString(5, guest.getCv());
            ps.setString(6, guest.getAvatar());
            ps.setString(7, guest.getGender());
            setNullableDate(ps, 8, guest.getDateOfBirth());
            ps.setString(9, guest.getAddress());
            ps.setString(10, guest.getStatus());
            setNullableInt(ps, 11, guest.getRecruitmentId());
            ps.setTimestamp(12, guest.getAppliedDate() != null
                    ? Timestamp.valueOf(guest.getAppliedDate())
                    : null);
            ps.setInt(13, guest.getGuestId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateProfile(Guest guest) {
        String sql = """
            UPDATE Guest
            SET UserID=?, FullName=?, Email=?, Phone=?, Avatar=?, Gender=?,
                DateOfBirth=?, Address=?
            WHERE GuestID=?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setNullableInt(ps, 1, guest.getUserId());
            ps.setString(2, guest.getFullName());
            ps.setString(3, guest.getEmail());
            ps.setString(4, guest.getPhone());
            ps.setString(5, guest.getAvatar());
            ps.setString(6, guest.getGender());
            setNullableDate(ps, 7, guest.getDateOfBirth());
            ps.setString(8, guest.getAddress());
            ps.setInt(9, guest.getGuestId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean saveProfile(Guest guest) {
        if (guest.getGuestId() > 0) {
            return updateProfile(guest);
        }
        return insertProfile(guest);
    }

    public boolean delete(int guestId) {
        String sql = "DELETE FROM Guest WHERE GuestID=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, guestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateStatus(int guestId, String newStatus) {
        String sql = "UPDATE Guest SET Status = ? WHERE GuestID = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setInt(2, guestId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Guest WHERE Email=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isPhoneExists(String phone) {
        String sql = "SELECT COUNT(*) FROM Guest WHERE Phone=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, phone);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM Guest";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getCountByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM Guest WHERE Status = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
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

    public List<Guest> search(String keyword) {
        List<Guest> list = new ArrayList<>();
        String sql = """
            SELECT *
            FROM Guest
            WHERE FullName LIKE ? OR Email LIKE ?
            ORDER BY AppliedDate DESC
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGuest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Guest> getPaged(int offset, int limit) {
        List<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM Guest ORDER BY AppliedDate DESC LIMIT ?, ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapGuest(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private int countByUserQuery(String sql, int userId, String email, String status) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, email);
            if (status != null) {
                ps.setString(3, status);
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

    private Guest mapGuest(ResultSet rs) throws SQLException {
        Guest guest = new Guest();
        guest.setGuestId(rs.getInt("GuestID"));
        guest.setUserId(rs.getObject("UserID", Integer.class));
        guest.setFullName(rs.getString("FullName"));
        guest.setEmail(rs.getString("Email"));
        guest.setPhone(rs.getString("Phone"));
        guest.setCv(rs.getString("CV"));
        guest.setAvatar(rs.getString("Avatar"));
        guest.setGender(rs.getString("Gender"));
        guest.setDateOfBirth(getLocalDate(rs, "DateOfBirth"));
        guest.setAddress(rs.getString("Address"));
        guest.setStatus(rs.getString("Status"));
        guest.setRecruitmentId(rs.getObject("RecruitmentID", Integer.class));
        guest.setAppliedDate(getLocalDateTime(rs, "AppliedDate"));
        guest.setUpdatedDate(getLocalDateTime(rs, "UpdatedDate"));
        return guest;
    }

    private GuestApplication mapGuestApplication(ResultSet rs) throws SQLException {
        GuestApplication application = new GuestApplication();
        application.setGuest(mapGuest(rs));
        application.setJobTitle(rs.getString("JobTitle"));
        application.setJobDescription(rs.getString("JobDescription"));
        application.setRequirement(rs.getString("Requirement"));
        application.setLocation(rs.getString("Location"));
        application.setSalary(rs.getObject("Salary", Double.class));
        application.setRecruitmentStatus(rs.getString("RecruitmentStatus"));
        application.setPostedDate(getLocalDateTime(rs, "PostedDate"));
        return application;
    }

    private LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value != null ? value.toLocalDateTime() : null;
    }

    private LocalDate getLocalDate(ResultSet rs, String column) throws SQLException {
        Date value = rs.getDate(column);
        return value != null ? value.toLocalDate() : null;
    }

    private void setNullableInt(PreparedStatement ps, int index, Integer value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.INTEGER);
        } else {
            ps.setInt(index, value);
        }
    }

    private void setNullableDate(PreparedStatement ps, int index, LocalDate value) throws SQLException {
        if (value == null) {
            ps.setNull(index, Types.DATE);
        } else {
            ps.setDate(index, Date.valueOf(value));
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    public static class GuestApplication {
        private Guest guest;
        private String jobTitle;
        private String jobDescription;
        private String requirement;
        private String location;
        private Double salary;
        private String recruitmentStatus;
        private LocalDateTime postedDate;

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

        public LocalDateTime getPostedDate() {
            return postedDate;
        }

        public void setPostedDate(LocalDateTime postedDate) {
            this.postedDate = postedDate;
        }
    }
}
