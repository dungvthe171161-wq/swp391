package com.hrm.dao;

import com.hrm.model.entity.Interview;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class InterviewDAO {

    public List<Interview> findByApplicationId(int applicationId) {
        List<Interview> interviews = new ArrayList<>();
        String sql = """
            SELECT *
            FROM Interview
            WHERE ApplicationID = ?
            ORDER BY RoundNo ASC, ScheduledAt ASC
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    interviews.add(mapInterview(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interviews;
    }

    public List<InterviewScheduleView> findUpcomingByUserId(int userId, int limit) {
        List<InterviewScheduleView> interviews = new ArrayList<>();
        String sql = """
            SELECT i.*,
                   r.JobTitle,
                   r.Location AS JobLocation
            FROM Interview i
            JOIN `Application` a ON i.ApplicationID = a.ApplicationID
            JOIN Guest g ON a.GuestID = g.GuestID
            JOIN Recruitment r ON a.RecruitmentID = r.RecruitmentID
            JOIN SystemUser su ON su.UserID = ?
            WHERE i.Status IN ('Scheduled', 'Rescheduled')
              AND i.ScheduledAt >= NOW()
              AND (g.UserID = ? OR (g.UserID IS NULL AND g.Email = su.Email))
            ORDER BY i.ScheduledAt ASC
            LIMIT ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    InterviewScheduleView view = new InterviewScheduleView();
                    view.setInterview(mapInterview(rs));
                    view.setJobTitle(rs.getString("JobTitle"));
                    view.setJobLocation(rs.getString("JobLocation"));
                    interviews.add(view);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return interviews;
    }

    public int create(Interview interview) {
        String sql = """
            INSERT INTO Interview
                (ApplicationID, RoundNo, ScheduledAt, Location, MeetingLink,
                 InterviewerEmployeeID, Status, Result, Note)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            setInterviewParams(ps, interview);
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

    public boolean updateSchedule(Interview interview) {
        String sql = """
            UPDATE Interview
            SET ApplicationID = ?, RoundNo = ?, ScheduledAt = ?, Location = ?, MeetingLink = ?,
                InterviewerEmployeeID = ?, Status = ?, Result = ?, Note = ?
            WHERE InterviewID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setInterviewParams(ps, interview);
            ps.setInt(10, interview.getInterviewId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateResult(int interviewId, String status, String result, String note) {
        String sql = """
            UPDATE Interview
            SET Status = ?, Result = ?, Note = ?
            WHERE InterviewID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, result);
            ps.setString(3, note);
            ps.setInt(4, interviewId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void setInterviewParams(PreparedStatement ps, Interview interview) throws SQLException {
        ps.setInt(1, interview.getApplicationId());
        ps.setInt(2, interview.getRoundNo() > 0 ? interview.getRoundNo() : 1);
        ps.setTimestamp(3, interview.getScheduledAt() != null
                ? Timestamp.valueOf(interview.getScheduledAt())
                : null);
        ps.setString(4, interview.getLocation());
        ps.setString(5, interview.getMeetingLink());
        if (interview.getInterviewerEmployeeId() == null) {
            ps.setNull(6, Types.INTEGER);
        } else {
            ps.setInt(6, interview.getInterviewerEmployeeId());
        }
        ps.setString(7, interview.getStatus());
        ps.setString(8, interview.getResult());
        ps.setString(9, interview.getNote());
    }

    private Interview mapInterview(ResultSet rs) throws SQLException {
        Interview interview = new Interview();
        interview.setInterviewId(rs.getInt("InterviewID"));
        interview.setApplicationId(rs.getInt("ApplicationID"));
        interview.setRoundNo(rs.getInt("RoundNo"));
        interview.setScheduledAt(getLocalDateTime(rs, "ScheduledAt"));
        interview.setLocation(rs.getString("Location"));
        interview.setMeetingLink(rs.getString("MeetingLink"));
        interview.setInterviewerEmployeeId(rs.getObject("InterviewerEmployeeID", Integer.class));
        interview.setStatus(rs.getString("Status"));
        interview.setResult(rs.getString("Result"));
        interview.setNote(rs.getString("Note"));
        interview.setCreatedDate(getLocalDateTime(rs, "CreatedDate"));
        interview.setUpdatedDate(getLocalDateTime(rs, "UpdatedDate"));
        return interview;
    }

    private java.time.LocalDateTime getLocalDateTime(ResultSet rs, String column) throws SQLException {
        Timestamp value = rs.getTimestamp(column);
        return value != null ? value.toLocalDateTime() : null;
    }

    public static class InterviewScheduleView {
        private Interview interview;
        private String jobTitle;
        private String jobLocation;

        public Interview getInterview() {
            return interview;
        }

        public void setInterview(Interview interview) {
            this.interview = interview;
        }

        public String getJobTitle() {
            return jobTitle;
        }

        public void setJobTitle(String jobTitle) {
            this.jobTitle = jobTitle;
        }

        public String getJobLocation() {
            return jobLocation;
        }

        public void setJobLocation(String jobLocation) {
            this.jobLocation = jobLocation;
        }
    }
}
