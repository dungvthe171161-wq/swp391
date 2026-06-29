package com.hrm.dao;

import com.hrm.model.entity.Guest;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Recruitment;
import com.hrm.model.entity.Task;
import com.hrm.model.entity.SystemUser;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    private static DAO instance;
    private Connection con;

    private DAO() {
        con = DBConnection.getConnection();
    }

    public static synchronized DAO getInstance() {
        if (instance == null) {
            instance = new DAO();
        }
        return instance;
    }

    public SystemUser getAccountByUsername(String username) {
        String sql = "SELECT * FROM SystemUser WHERE LOWER(Username) = LOWER(?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSystemUser(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting systemUser by username: " + e.getMessage());
        }
        return null;
    }

    public SystemUser getAccountByUsernameOrEmail(String login) {
        String sql = "SELECT * FROM SystemUser WHERE LOWER(Username) = LOWER(?) OR LOWER(Email) = LOWER(?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, login);
            ps.setString(2, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSystemUser(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting systemUser by username/email: " + e.getMessage());
        }
        return null;
    }

    public SystemUser getAccountByEmail(String email) {
        String sql = "SELECT * FROM SystemUser WHERE LOWER(Email) = LOWER(?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSystemUser(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting systemUser by email: " + e.getMessage());
        }
        return null;
    }

    public SystemUser getAccountByGoogleId(String googleId) {
        String sql = "SELECT * FROM SystemUser WHERE GoogleID = ?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, googleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapSystemUser(rs);
                }
            }
        } catch (Exception e) {
            System.err.println("Error getting systemUser by GoogleID: " + e.getMessage());
        }
        return null;
    }

    public int changePassword(String username, String newPass) {
        String sql = "UPDATE SystemUser SET PasswordHash=?, UpdatedDate=NOW() WHERE Username=?";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPass);
            ps.setString(2, username);
            return ps.executeUpdate();
        } catch (Exception e) {
            System.err.println("Error updating password for username " + username + ": " + e.getMessage());
            return 0;
        }
    }

    public boolean checkEmailExist(String email) {
        String sql = "SELECT COUNT(*) FROM SystemUser WHERE LOWER(Email) = LOWER(?)";
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                return rs.getInt(1) > 0; // Nếu có ít nhất 1 dòng, email đã tồn tại
            }
            }
        } catch (SQLException e) {
            System.err.println("Error checking email existence: " + e.getMessage());
        }
        return false;
    }

    public int updateCandidateStatus(int gID, String newStatus) {
        String sql = "UPDATE Guest SET status = ? WHERE guestId = ?";
        int result = 0;

        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, newStatus);
            ps.setInt(2, gID);
            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Guest> getAllCandidates(int page, int size) {
        List<Guest> gList = new ArrayList<Guest>();
        int offset = (page - 1) * size;
        String sql = "SELECT * from Guest ORDER BY guestId LIMIT ? OFFSET ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, size);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Guest guest = new Guest(rs.getInt("guestId"), rs.getString("fullName"), rs.getString("email"),
                        rs.getString("phone"), rs.getString("cv"), rs.getString("status"), rs.getObject("recruitmentId", Integer.class),
                        rs.getObject("appliedDate", java.time.LocalDateTime.class));
                gList.add(guest);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return gList;
    }

    public int getCountCandidate() {
        String sql = "SELECT COUNT(*) FROM Guest";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    public Guest getCandidateById(int id) {
        String sql = "SELECT * FROM Guest WHERE guestId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Guest g = new Guest(
                        rs.getInt("guestId"),
                        rs.getString("fullName"),
                        rs.getString("email"),
                        rs.getString("phone"),
                        rs.getString("cv"),
                        rs.getString("status"),
                        rs.getInt("recruitmentId"),
                        rs.getObject("appliedDate", LocalDateTime.class)
                );
                return g;
            }
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy candidate theo ID: " + e.getMessage());
            e.printStackTrace(); // In ra chi tiết lỗi để dễ dàng gỡ lỗi
        }
        return null;
    }

    public List<Guest> searchCandidates(String fullName, String status, String startDate, String endDate, int page, int size) {
        List<Guest> gList = new ArrayList<>();
        int offset = (page - 1) * size;
        StringBuilder sql = new StringBuilder("SELECT * FROM Guest WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (fullName != null && !fullName.trim().isEmpty()) {
            sql.append(" AND fullName LIKE ?");
            params.add("%" + fullName + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND appliedDate >= ?");
            params.add(startDate);
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND appliedDate <= ?");
            params.add(endDate);
        }

        sql.append(" ORDER BY guestId LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try {
            PreparedStatement ps = con.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Guest guest = new Guest(rs.getInt("guestId"), rs.getString("fullName"), rs.getString("email"),
                        rs.getString("phone"), rs.getString("cv"), rs.getString("status"), rs.getObject("recruitmentId", Integer.class),
                        rs.getObject("appliedDate", java.time.LocalDateTime.class));
                gList.add(guest);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return gList;
    }

    public int searchCountCandidates(String fullName, String status, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Guest WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (fullName != null && !fullName.trim().isEmpty()) {
            sql.append(" AND fullName LIKE ?");
            params.add("%" + fullName + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND appliedDate >= ?");
            params.add(startDate);
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND appliedDate <= ?");
            params.add(endDate);
        }

        try {
            PreparedStatement ps = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        return 0;
    }

    public Employee findEmployeeByEmail(String email) {
        String sql = "SELECT * FROM employee WHERE Email = ? ";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getInt("EmployeeID"));
                emp.setFullName(rs.getString("FullName"));
                emp.setEmail(rs.getString("Email"));
                return emp;
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public SystemUser findSystemUserByEmpID(int employeeID) {
        String sql = "SELECT * FROM SystemUser WHERE EmployeeID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, employeeID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapSystemUser(rs);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return null;
    }

    public List<Guest> getAllCandidates() {
        List<Guest> gList = new ArrayList<Guest>();
        String sql = "SELECT * from Guest ORDER BY GuestID";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Guest guest = new Guest(rs.getInt("GuestID"), rs.getString("FullName"), rs.getString("Email"),
                        rs.getString("Phone"), rs.getString("CV"), rs.getString("Status"), rs.getObject("RecruitmentID", Integer.class),
                        rs.getObject("AppliedDate", java.time.LocalDateTime.class));
                gList.add(guest);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return gList;
    }

    public List<Recruitment> searchRecruitment(String title, String status, String startDate, String endDate, int page, int size) {
        List<Recruitment> rList = new ArrayList<>();
        int offset = (page - 1) * size;
        StringBuilder sql = new StringBuilder("SELECT * FROM Recruitment WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            sql.append(" AND JobTitle LIKE ?");
            params.add("%" + title + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND Status = ?");
            params.add(status);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND PostedDate >= ?");
            params.add(startDate);
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND PostedDate <= ?");
            params.add(endDate);
        }
        sql.append(" ORDER BY RecruitmentID LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try {
            PreparedStatement ps = con.prepareStatement(sql.toString());

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recruitment rec = new Recruitment(rs.getInt("RecruitmentID"), rs.getString("JobTitle"),
                        rs.getString("JobDescription"), rs.getString("Requirement"), rs.getString("Location"), rs.getDouble("Salary"),
                        rs.getString("Status"), rs.getInt("Applicant"), rs.getObject("PostedDate", java.time.LocalDateTime.class));
                rList.add(rec);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi trong searchRecruitment: " + e.getMessage());
        }

        return rList;
    }

    public int searchCountRecruitment(String title, String status, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Recruitment WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            sql.append(" AND JobTitle LIKE ?");
            params.add("%" + title + "%");
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND Status = ?");
            params.add(status);
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND PostedDate >= ?");
            params.add(startDate);
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND PostedDate <= ?");
            params.add(endDate);
        }

        try {
            PreparedStatement ps = con.prepareStatement(sql.toString());
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi trong searchCountRecruitment: " + e.getMessage()); // Thêm thông báo lỗi
        }

        return 0;
    }

    public List<Recruitment> getAllRecruitment(int page, int size) {
        List<Recruitment> rList = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT * from Recruitment ORDER BY RecruitmentID LIMIT ? OFFSET ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, size);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recruitment rec = new Recruitment(rs.getInt("RecruitmentID"), rs.getString("JobTitle"),
                        rs.getString("JobDescription"), rs.getString("Requirement"), rs.getString("Location"), rs.getDouble("Salary"),
                        rs.getString("Status"), rs.getInt("Applicant"), rs.getObject("PostedDate", java.time.LocalDateTime.class));
                rList.add(rec);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return rList;
    }

    public int getCountRecruitment() {
        String sql = "SELECT COUNT(*) FROM Recruitment";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    public Recruitment getRecruitmentById(int id) {
        String sql = "SELECT * FROM Recruitment WHERE recruitmentId = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Recruitment rec = new Recruitment(rs.getInt("RecruitmentID"), rs.getString("JobTitle"),
                        rs.getString("JobDescription"), rs.getString("Requirement"), rs.getString("Location"), rs.getDouble("Salary"),
                        rs.getString("Status"), rs.getInt("Applicant"), rs.getObject("PostedDate", java.time.LocalDateTime.class));
                return rec;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int setRecruitmentById(String title, String description, String requirement, String location, Double salary, int applicant, int id) {
        String sql = "UPDATE Recruitment SET JobTitle = ?, JobDescription = ?, Requirement = ?, Location = ?, Salary = ?, Applicant=? WHERE RecruitmentID = ?";
        int result = 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, requirement);
            ps.setString(4, location);
            ps.setDouble(5, salary);
            ps.setInt(6, applicant);
            ps.setInt(7, id);

            result = ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int deleteRecruitmentById(int id) {
        String sql = "DELETE FROM Recruitment WHERE RecruitmentID = ?";
        int result = 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int createRecruitment(String title, String description, String requirement,
            String location, double salary,
            LocalDateTime postedDate, int applicant) {
        String sql = "INSERT INTO Recruitment (JobTitle, JobDescription, Requirement, Location, Salary, Status, PostedDate, Applicant) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        int result = 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setString(3, requirement);
            ps.setString(4, location);
            ps.setDouble(5, salary);
            ps.setString(6, "New");
            ps.setTimestamp(7, java.sql.Timestamp.valueOf(postedDate));
            ps.setInt(8, applicant);

            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public int updateRecruitmentStatus(int id, String status) {
        String sql = "UPDATE Recruitment SET Status = ? WHERE RecruitmentID = ?";
        int result = 0;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);

            result = ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating recruitment status: " + e.getMessage());
        }

        return result;
    }

    public List<Recruitment> searchRecruitmentWaiting(String title, String startDate, String endDate, int page, int size) {
        List<Recruitment> rList = new ArrayList<>();
        int offset = (page - 1) * size;
        StringBuilder sql = new StringBuilder("SELECT * FROM Recruitment WHERE Status IN ('Waiting', 'Applied')");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            sql.append(" AND JobTitle LIKE ?");
            params.add("%" + title + "%");
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND PostedDate >= ?");
            params.add(startDate);
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND PostedDate <= ?");
            params.add(endDate);
        }
        sql.append(" ORDER BY RecruitmentID LIMIT ? OFFSET ?");
        params.add(size);
        params.add(offset);

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recruitment rec = new Recruitment(rs.getInt("RecruitmentID"), rs.getString("JobTitle"),
                        rs.getString("JobDescription"), rs.getString("Requirement"), rs.getString("Location"), rs.getDouble("Salary"),
                        rs.getString("Status"), rs.getInt("Applicant"), rs.getObject("PostedDate", java.time.LocalDateTime.class));
                rList.add(rec);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi trong searchRecruitmentWaiting: " + e.getMessage());
        }

        return rList;
    }

    public int searchCountRecruitmentWaiting(String title, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT * FROM Recruitment WHERE Status IN ('Waiting', 'Applied')");
        List<Object> params = new ArrayList<>();

        if (title != null && !title.trim().isEmpty()) {
            sql.append(" AND JobTitle LIKE ?");
            params.add("%" + title + "%");
        }

        if (startDate != null && !startDate.trim().isEmpty()) {
            sql.append(" AND PostedDate >= ?");
            params.add(startDate);
        }

        if (endDate != null && !endDate.trim().isEmpty()) {
            sql.append(" AND PostedDate <= ?");
            params.add(endDate);
        }

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Lỗi trong countRecruitmentWaiting: " + e.getMessage());
        }

        return 0;
    }

    public int getCountRecruitmentWaiting() {
        String sql = "SELECT COUNT(*) FROM Recruitment WHERE Status IN ('Waiting', 'Applied')";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return 0;
    }

    public List<Recruitment> getAllRecruitmentWaiting(int page, int size) {
        List<Recruitment> rList = new ArrayList<>();
        int offset = (page - 1) * size;
        String sql = "SELECT * from Recruitment WHERE Status IN ('Waiting', 'Applied') ORDER BY RecruitmentID LIMIT ? OFFSET ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, size);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Recruitment rec = new Recruitment(rs.getInt("RecruitmentID"), rs.getString("JobTitle"),
                        rs.getString("JobDescription"), rs.getString("Requirement"), rs.getString("Location"), rs.getDouble("Salary"),
                        rs.getString("Status"), rs.getInt("Applicant"), rs.getObject("PostedDate", java.time.LocalDateTime.class));
                rList.add(rec);
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return rList;
    }

    // ==========================
    // Task Management (for Dept)
    // ==========================
    public List<Task> getAllTasks(int assigneeId, int page, int size) {
        List<Task> list = new ArrayList<>();
    int offset = (page - 1) * size;
    String sql = "SELECT * FROM Task WHERE AssignedBy = ? ORDER BY TaskID LIMIT ? OFFSET ?";
    try {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, assigneeId);
        ps.setInt(2, size);
        ps.setInt(3, offset);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Task t = new Task();
            t.setTaskId(rs.getInt("TaskID"));
            t.setTitle(rs.getString("Title"));
            t.setDescription(rs.getString("Description"));
            t.setAssignedBy(rs.getInt("AssignedBy"));
            if (rs.getDate("StartDate") != null) {
                t.setStartDate(rs.getDate("StartDate").toLocalDate());
            }
            if (rs.getDate("DueDate") != null) {
                t.setDueDate(rs.getDate("DueDate").toLocalDate());
            }
            t.setStatus(rs.getString("Status"));
            list.add(t);
        }
    } catch (SQLException e) {
        System.err.println("Error getting tasks by assignee: " + e.getMessage());
    }
    return list;
    }

    public int getCountTasks(int assigneeId) {
        String sql = "SELECT COUNT(*) FROM Task WHERE AssignedBy = ?";
    try {
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, assigneeId);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        System.err.println("Error counting tasks by assignee: " + e.getMessage());
    }
    return 0;
    }

    public List<Task> searchTasks(int assigneeId, String title, String status, String startDate, String endDate, int page, int size) {
        List<Task> list = new ArrayList<>();
    int offset = (page - 1) * size;
    StringBuilder sql = new StringBuilder("SELECT * FROM Task WHERE AssignedBy = ?");
    List<Object> params = new ArrayList<>();
    params.add(assigneeId);

    if (title != null && !title.trim().isEmpty()) {
        sql.append(" AND Title LIKE ?");
        params.add("%" + title.trim() + "%");
    }

    if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
        sql.append(" AND Status = ?");
        params.add(status.trim());
    }

    if (startDate != null && !startDate.trim().isEmpty()) {
        sql.append(" AND (DueDate IS NOT NULL AND DueDate >= ?)");
        params.add(startDate.trim());
    }

    if (endDate != null && !endDate.trim().isEmpty()) {
        sql.append(" AND (DueDate IS NOT NULL AND DueDate <= ?)");
        params.add(endDate.trim());
    }

    sql.append(" ORDER BY TaskID LIMIT ? OFFSET ?");
    params.add(size);
    params.add(offset);

    try {
        PreparedStatement ps = con.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Task t = new Task();
            t.setTaskId(rs.getInt("TaskID"));
            t.setTitle(rs.getString("Title"));
            t.setDescription(rs.getString("Description"));
            t.setAssignedBy(rs.getInt("AssignedBy"));
            if (rs.getDate("StartDate") != null) {
                t.setStartDate(rs.getDate("StartDate").toLocalDate());
            }
            if (rs.getDate("DueDate") != null) {
                t.setDueDate(rs.getDate("DueDate").toLocalDate());
            }
            t.setStatus(rs.getString("Status"));
            list.add(t);
        }
    } catch (SQLException e) {
        System.err.println("Error searching tasks by assignee: " + e.getMessage());
    }
    return list;
    }

    public int searchCountTasks(int assigneeId,String title, String status, String startDate, String endDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Task WHERE AssignedBy = ?");
    List<Object> params = new ArrayList<>();
    params.add(assigneeId);

    if (title != null && !title.trim().isEmpty()) {
        sql.append(" AND Title LIKE ?");
        params.add("%" + title.trim() + "%");
    }

    if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
        sql.append(" AND Status = ?");
        params.add(status.trim());
    }

    if (startDate != null && !startDate.trim().isEmpty()) {
        sql.append(" AND (DueDate IS NOT NULL AND DueDate >= ?)");
        params.add(startDate.trim());
    }

    if (endDate != null && !endDate.trim().isEmpty()) {
        sql.append(" AND (DueDate IS NOT NULL AND DueDate <= ?)");
        params.add(endDate.trim());
    }

    try {
        PreparedStatement ps = con.prepareStatement(sql.toString());
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
    } catch (SQLException e) {
        System.err.println("Error counting search tasks by assignee: " + e.getMessage());
    }
    return 0;
    }

    public int updateTaskStatus(int id, String status) {
        String sql = "UPDATE Task SET Status = ? WHERE TaskID = ?";
        int result = 0;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public int deleteTaskById(int id) {
        String sql = "DELETE FROM Task WHERE TaskID = ?";
        int result = 0;
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            result = ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Task getTaskById(int id) {
        String sql = "SELECT * FROM Task WHERE TaskID = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Task t = new Task();
                t.setTaskId(rs.getInt("TaskID"));
                t.setTitle(rs.getString("Title"));
                t.setDescription(rs.getString("Description"));
                t.setAssignedBy(rs.getInt("AssignedBy"));
                if (rs.getDate("StartDate") != null) {
                    t.setStartDate(rs.getDate("StartDate").toLocalDate());
                }
                if (rs.getDate("DueDate") != null) {
                    t.setDueDate(rs.getDate("DueDate").toLocalDate());
                }
                t.setStatus(rs.getString("Status"));
                return t;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createTask(String title, String description, int assignedBy, String startDate, String dueDate) {
        String sql = "INSERT INTO Task (Title, Description, AssignedBy, StartDate, DueDate, Status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, title);
            ps.setString(2, description);
            ps.setInt(3, assignedBy);
            if (startDate != null && !startDate.trim().isEmpty()) {
                ps.setDate(4, java.sql.Date.valueOf(startDate));
            } else {
                ps.setDate(4, new java.sql.Date(System.currentTimeMillis()));
            }
            if (dueDate != null && !dueDate.trim().isEmpty()) {
                ps.setDate(5, java.sql.Date.valueOf(dueDate));
            } else {
                ps.setNull(5, java.sql.Types.DATE);
            }
            ps.setString(6, "In Progress");
            if (ps.executeUpdate() == 0) {
                return 0;
            }
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Employee> loadEmpFollowDepartment(int deptID) {
        List<Employee> eList = new ArrayList<>();
        String sql = "SELECT * from Employee WHERE DepartmentID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, deptID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getInt("EmployeeID"),
                        rs.getString("FullName"),
                        rs.getString("Address"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        rs.getString("Position"),
                        rs.getInt("DepartmentID")
                );
                eList.add(emp);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return eList;
    }

    public Employee getEmp(int sysID) {
        String sql = "SELECT * from Employee WHERE EmployeeID = ?";
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, sysID);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getInt("EmployeeID"),
                        rs.getString("FullName"),
                        rs.getString("Address"),
                        rs.getString("Phone"),
                        rs.getString("Email"),
                        rs.getString("Position"),
                        rs.getInt("DepartmentID")
                );
                return emp;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String hashPassword(String plainTextPassword) {
    return plainTextPassword;
}

public boolean checkPassword(String plainTextPassword, String storedPassword) {
    return plainTextPassword != null && storedPassword != null && plainTextPassword.equals(storedPassword);
}

public boolean isSystemUsernameExists(String username) {
    return getAccountByUsername(username) != null;
}

public boolean isSystemEmailExists(String email) {
    return getAccountByEmail(email) != null;
}

public SystemUser createLocalUser(String username, String email, String password, int roleId) {
    String sql = """
        INSERT INTO SystemUser
            (Username, Email, PasswordHash, GoogleID, AvatarUrl, LoginProvider, RoleID, EmployeeID,
             FailedLoginAttempt, LockedUntil, LastLogin, IsActive, CreatedDate, UpdatedDate)
        VALUES (?, ?, ?, NULL, NULL, 'LOCAL', ?, NULL, 0, NULL, NULL, TRUE, NOW(), NOW())
    """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, username);
        ps.setString(2, email);
        ps.setString(3, password);
        ps.setInt(4, roleId);
        if (ps.executeUpdate() == 0) {
            return null;
        }
        return getAccountByUsername(username);
    } catch (SQLException e) {
        System.err.println("Error creating local user: " + e.getMessage());
        return null;
    }
}

public List<SystemUser> getAllSystemUsers() {
    List<SystemUser> users = new ArrayList<>();
    String sql = "SELECT * FROM SystemUser";
    
    try (PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        
        while (rs.next()) {
            users.add(mapSystemUser(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return users;
}

public boolean updateUserPassword(int userId, String hashedPassword) {
    String sql = "UPDATE SystemUser SET PasswordHash = ?, UpdatedDate=NOW() WHERE UserID = ?";
    
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        
        ps.setString(1, hashedPassword); 
        ps.setInt(2, userId);
        
        int result = ps.executeUpdate();
        return result > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}

public boolean updateGoogleAccount(int userId, String googleId, String avatarUrl) {
    String sql = """
        UPDATE SystemUser
        SET GoogleID = ?, AvatarUrl = ?,
            LastLogin = NOW(), FailedLoginAttempt = 0, LockedUntil = NULL, UpdatedDate = NOW()
        WHERE UserID = ?
    """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, googleId);
        ps.setString(2, avatarUrl);
        ps.setInt(3, userId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error linking Google account: " + e.getMessage());
        return false;
    }
}

public boolean promoteGuestToEmployee(int userId, int employeeId, String username, String password, int employeeRoleId) {
    String sql = """
        UPDATE SystemUser
        SET Username = ?, PasswordHash = ?, RoleID = ?, EmployeeID = ?,
            LoginProvider = 'LOCAL', IsActive = TRUE, UpdatedDate = NOW()
        WHERE UserID = ? AND EmployeeID IS NULL
    """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setInt(3, employeeRoleId);
        ps.setInt(4, employeeId);
        ps.setInt(5, userId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error promoting guest user to employee: " + e.getMessage());
        return false;
    }
}

public boolean createEmployeeUser(int employeeId, String username, String password, int employeeRoleId) {
    String sql = """
        INSERT INTO SystemUser
            (Username, Email, PasswordHash, RoleID, EmployeeID, IsActive, CreatedDate, UpdatedDate, LoginProvider)
        SELECT ?, e.Email, ?, ?, ?, TRUE, NOW(), NOW(), 'LOCAL'
        FROM Employee e
        WHERE e.EmployeeID = ?
    """;

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.setString(2, password);
        ps.setInt(3, employeeRoleId);
        ps.setInt(4, employeeId);
        ps.setInt(5, employeeId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        System.err.println("Error creating employee user: " + e.getMessage());
        return false;
    }
}

public SystemUser createGoogleUser(String googleId, String email, String name, String avatarUrl, int roleId) {
    String sql = """
        INSERT INTO SystemUser
            (Username, Email, PasswordHash, GoogleID, AvatarUrl, LoginProvider, RoleID, EmployeeID,
             FailedLoginAttempt, LockedUntil, LastLogin, IsActive, CreatedDate)
        VALUES (?, ?, NULL, ?, ?, 'GOOGLE', ?, NULL, 0, NULL, NOW(), TRUE, NOW())
    """;

    String username = buildGoogleUsername(email, name);
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, username);
        ps.setString(2, email);
        ps.setString(3, googleId);
        ps.setString(4, avatarUrl);
        ps.setInt(5, roleId);
        if (ps.executeUpdate() == 0) {
            return null;
        }
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return getAccountByEmail(email);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error creating Google user: " + e.getMessage());
    }
    return null;
}

public int getRoleIdByName(String roleName) {
    Integer roleId = findRoleIdByName(roleName);
    if (roleId == null) {
        throw new IllegalStateException("Role not found: " + roleName);
    }
    return roleId;
}

private Integer findRoleIdByName(String roleName) {
    String sql = "SELECT RoleID FROM Role WHERE RoleName = ? LIMIT 1";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, roleName);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("RoleID");
            }
        }
    } catch (SQLException e) {
        System.err.println("Error getting role by name: " + e.getMessage());
    }
    return null;
}

public int getOrCreateRoleIdByName(String roleName) {
    Integer existingRoleId = findRoleIdByName(roleName);
    if (existingRoleId != null) {
        return existingRoleId;
    }
    if ("Employee".equalsIgnoreCase(roleName)) {
        return 5;
    }

    String insertSql = "INSERT INTO Role (RoleName) VALUES (?)";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
        ps.setString(1, roleName);
        ps.executeUpdate();
        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        System.err.println("Error creating role " + roleName + ": " + e.getMessage());
    }
    Integer roleId = findRoleIdByName(roleName);
    if (roleId != null) {
        return roleId;
    }
    throw new IllegalStateException("Role not found or created: " + roleName);
}

public void recordSuccessfulLogin(int userId) {
    String sql = "UPDATE SystemUser SET LastLogin=NOW(), FailedLoginAttempt=0, LockedUntil=NULL, UpdatedDate=NOW() WHERE UserID=?";
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setInt(1, userId);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error recording successful login: " + e.getMessage());
    }
}

public void recordFailedLogin(String username) {
    String sql = """
        UPDATE SystemUser
        SET FailedLoginAttempt = FailedLoginAttempt + 1,
            LockedUntil = CASE WHEN FailedLoginAttempt + 1 >= 5 THEN DATE_ADD(NOW(), INTERVAL 15 MINUTE) ELSE LockedUntil END,
            UpdatedDate = NOW()
        WHERE Username = ?
    """;
    try (Connection connection = DBConnection.getConnection();
         PreparedStatement ps = connection.prepareStatement(sql)) {
        ps.setString(1, username);
        ps.executeUpdate();
    } catch (SQLException e) {
        System.err.println("Error recording failed login: " + e.getMessage());
    }
}

private String buildGoogleUsername(String email, String name) {
    String base = email != null && email.contains("@") ? email.substring(0, email.indexOf('@')) : name;
    if (base == null || base.trim().isEmpty()) {
        base = "google_user";
    }
    base = base.replaceAll("[^a-zA-Z0-9]", "");
    if (base.isEmpty()) {
        base = "googleuser";
    }

    String candidate = base;
    int suffix = 1;
    while (getAccountByUsername(candidate) != null) {
        candidate = base + suffix++;
    }
    return candidate;
}

private SystemUser mapSystemUser(ResultSet rs) throws SQLException {
    SystemUser user = new SystemUser();
    user.setUserId(rs.getInt("UserID"));
    user.setUsername(rs.getString("Username"));
    user.setEmail(rs.getString("Email"));
    user.setPasswordHash(rs.getString("PasswordHash"));
    user.setGoogleId(rs.getString("GoogleID"));
    user.setAvatarUrl(rs.getString("AvatarUrl"));
    user.setLoginProvider(rs.getString("LoginProvider"));
    user.setRoleId(rs.getObject("RoleID", Integer.class));
    user.setEmployeeId(rs.getObject("EmployeeID", Integer.class));
    user.setFailedLoginAttempt(rs.getInt("FailedLoginAttempt"));
    Timestamp lockedUntil = rs.getTimestamp("LockedUntil");
    user.setLockedUntil(lockedUntil != null ? lockedUntil.toLocalDateTime() : null);
    Timestamp lastLogin = rs.getTimestamp("LastLogin");
    user.setLastLogin(lastLogin != null ? lastLogin.toLocalDateTime() : null);
    user.setIsActive(rs.getBoolean("IsActive"));
    Timestamp createdDate = rs.getTimestamp("CreatedDate");
    user.setCreatedDate(createdDate != null ? createdDate.toLocalDateTime() : null);
    Timestamp updatedDate = rs.getTimestamp("UpdatedDate");
    user.setUpdatedDate(updatedDate != null ? updatedDate.toLocalDateTime() : null);
    return user;
}
public List<Integer> getEmployeeIdsByTaskId(int taskId) {
    List<Integer> empIds = new ArrayList<>();
    String sql = "SELECT empId FROM assignlist WHERE taskId = ?";
    
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, taskId);
        ResultSet rs = ps.executeQuery();
        
        while (rs.next()) {
            empIds.add(rs.getInt("empId"));
        }
    } catch (SQLException e) {
        System.err.println("Error getting employee IDs by task ID: " + e.getMessage());
    }
    
    return empIds;
}
    public boolean updateTask(int taskId, String title, String description, String startDate, String dueDate) {
    String sql = "UPDATE Task SET Title = ?, Description = ?, StartDate = ?, DueDate = ? WHERE TaskID = ?";
    
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, title);
        ps.setString(2, description);
        if (startDate != null && !startDate.isEmpty()) {
            ps.setDate(3, java.sql.Date.valueOf(startDate));
        } else {
            ps.setNull(3, java.sql.Types.DATE);
        }
        if (dueDate != null && !dueDate.isEmpty()) {
            ps.setDate(4, java.sql.Date.valueOf(dueDate));
        } else {
            ps.setNull(4, java.sql.Types.DATE);
        }
        ps.setInt(5, taskId);
        
        int result = ps.executeUpdate();
        return result > 0;
    } catch (SQLException e) {
        System.err.println("Error updating task: " + e.getMessage());
        return false;
    }
}
    
    public boolean deleteTaskAssignments(int taskId) {
    String sql = "DELETE FROM assignlist WHERE taskId = ?";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, taskId);
        int result = ps.executeUpdate();
        return result > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    public boolean assignTaskToEmployee(int taskId, int empId) {
    String sql = "INSERT INTO assignlist (taskId, empId) VALUES (?, ?)";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, taskId);
        ps.setInt(2, empId);
        int result = ps.executeUpdate();
        return result > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}
