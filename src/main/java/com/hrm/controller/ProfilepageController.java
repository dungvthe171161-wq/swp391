/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller;

import com.hrm.dao.DAO;
import com.hrm.dao.DBConnection;
import com.hrm.dao.SystemLogDAO;
import com.hrm.model.entity.SystemUser;
import com.hrm.model.entity.SystemLog;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author admin
 */
@WebServlet(name="ProfilepageController", urlPatterns={"/profilepage"})
public class ProfilepageController extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(ProfilepageController.class.getName());
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            HttpSession session = request.getSession();
            SystemUser currentUser = (SystemUser) session.getAttribute("systemUser");
            
            // Check if user is logged in
            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/Views/Login.jsp");
                return;
            }
            
            String action = request.getParameter("action");
            
            if ("update".equals(action)) {
                handleUpdateProfile(request, response, currentUser);
            } else if ("changePassword".equals(action)) {
                handleChangePassword(request, response, currentUser);
            } else {
                // Default: show profile page
                showProfilePage(request, response, currentUser);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in ProfilepageController", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Đã xảy ra lỗi khi xử lý yêu cầu");
        }
    }
    
    /**
     * Show profile page with user data
     */
    private void showProfilePage(HttpServletRequest request, HttpServletResponse response, SystemUser user)
            throws ServletException, IOException {
        
        try {
            // Get detailed user information
            UserProfile profile = getUserProfile(user.getUserId());
            request.setAttribute("userProfile", profile);
            
            // Get user activity log
            List<UserActivity> activities = getUserActivities(user.getUserId());
            request.setAttribute("userActivities", activities);
            
            // Get user statistics
            UserStats stats = getUserStats(user.getUserId());
            request.setAttribute("userStats", stats);
            
            // Forward to profile JSP
            request.getRequestDispatcher("/Views/Profilepage.jsp").forward(request, response);
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting user profile data", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Không thể tải dữ liệu hồ sơ");
        }
    }
    
    /**
     * Handle profile update
     */
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response, SystemUser user)
            throws ServletException, IOException {
        
        try {
            String fullName = request.getParameter("fullName");
            String email = request.getParameter("email");
            String phone = request.getParameter("phone");
            String department = request.getParameter("department");
            String bio = request.getParameter("bio");
            
            // Update user profile in database
            boolean success = updateUserProfile(user.getUserId(), fullName, email, phone, department, bio);
            
            if (success) {
                // Note: Profile data is stored in Employee table, not SystemUser
                // Session user (SystemUser) doesn't need to be updated
                request.setAttribute("successMessage", "Cập nhật hồ sơ thành công.");
            } else {
                request.setAttribute("errorMessage", "Không thể cập nhật hồ sơ. Vui lòng thử lại.");
            }
            
            // Reload profile page
            showProfilePage(request, response, user);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error updating profile", e);
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi cập nhật hồ sơ.");
            showProfilePage(request, response, user);
        }
    }
    
    /**
     * Handle password change
     */
    private void handleChangePassword(HttpServletRequest request, HttpServletResponse response, SystemUser user)
            throws ServletException, IOException {
        
        try {
            String currentPassword = request.getParameter("currentPassword");
            String newPassword = request.getParameter("newPassword");
            String confirmPassword = request.getParameter("confirmPassword");
            
            // Validate passwords
            if (!newPassword.equals(confirmPassword)) {
                request.setAttribute("errorMessage", "Mật khẩu xác nhận không khớp.");
                showProfilePage(request, response, user);
                return;
            }
            
            // Verify current password
            if (!verifyCurrentPassword(user.getUserId(), currentPassword)) {
                request.setAttribute("errorMessage", "Mật khẩu hiện tại không đúng.");
                showProfilePage(request, response, user);
                return;
            }
            
            // Update password
            boolean success = updatePassword(user.getUserId(), newPassword);
            
            if (success) {
                request.setAttribute("successMessage", "Đổi mật khẩu thành công.");
            } else {
                request.setAttribute("errorMessage", "Không thể đổi mật khẩu. Vui lòng thử lại.");
            }
            
            showProfilePage(request, response, user);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error changing password", e);
            request.setAttribute("errorMessage", "Đã xảy ra lỗi khi đổi mật khẩu.");
            showProfilePage(request, response, user);
        }
    }
    
    /**
     * Get detailed user profile information
     */
    private UserProfile getUserProfile(int userId) throws SQLException {
        UserProfile profile = new UserProfile();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT su.*, e.FullName, e.Email, e.Phone, d.DeptName, r.RoleName " +
                              "FROM SystemUser su " +
                              "LEFT JOIN Employee e ON su.EmployeeID = e.EmployeeID " +
                              "LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID " +
                              "LEFT JOIN Role r ON su.RoleID = r.RoleID " +
                              "WHERE su.UserID = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            profile.setUserId(rs.getInt("UserID"));
                            profile.setUsername(rs.getString("Username"));
                            profile.setFullName(rs.getString("FullName"));
                            profile.setEmail(rs.getString("Email"));
                            profile.setPhone(rs.getString("Phone"));
                            profile.setDepartment(rs.getString("DeptName"));
                            profile.setRole(rs.getString("RoleName"));
                            profile.setJoinDate(rs.getTimestamp("CreatedDate"));
                            profile.setLastLogin(rs.getTimestamp("LastLogin"));
                            profile.setStatus(rs.getBoolean("IsActive") ? "Active" : "Inactive");
                        }
                    }
                }
            }
        }
        
        return profile;
    }
    
    /**
     * Get user activities
     */
    private List<UserActivity> getUserActivities(int userId) {
        List<UserActivity> activities = new ArrayList<>();
        
        try {
            // Get system logs for the user with limit of 10
            List<SystemLog> systemLogs = systemLogDAO.getSystemLogsByUserID(userId, 10);
            
            for (SystemLog log : systemLogs) {
                UserActivity activity = new UserActivity();
                activity.setId(log.getLogId());
                activity.setActivity(log.getAction());
                activity.setDescription(log.getNewValue() != null ? log.getNewValue() : log.getAction());
                activity.setActivityDate(Timestamp.valueOf(log.getTimestamp()));
                activities.add(activity);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting user activities", e);
        }
        
        return activities;
    }
    
    /**
     * Get user statistics
     */
    private UserStats getUserStats(int userId) throws SQLException {
        UserStats stats = new UserStats();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Get login count using SystemLogDAO
                stats.setLoginCount(systemLogDAO.getSystemLogCountByAction("LOGIN"));
                
                // Get days since join
                String joinQuery = "SELECT DATEDIFF(NOW(), CreatedDate) as days FROM SystemUser WHERE UserID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(joinQuery)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            stats.setDaysSinceJoin(rs.getInt("days"));
                        }
                    }
                }
                
                // Get satisfaction rate (mock data for now)
                stats.setSatisfactionRate(98);
            }
        }
        
        return stats;
    }
    
    /**
     * Update user profile
     */
    private boolean updateUserProfile(int userId, String fullName, String email, String phone, String department, String bio) {
        // Note: department and bio parameters are not used in current implementation
        // They can be added to Employee table if needed in the future
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                conn.setAutoCommit(false); // Start transaction
                
                try {
                    // First, get the EmployeeID for this user
                    String getEmpQuery = "SELECT EmployeeID FROM SystemUser WHERE UserID = ?";
                    int employeeId = 0;
                    try (PreparedStatement stmt = conn.prepareStatement(getEmpQuery)) {
                        stmt.setInt(1, userId);
                        try (ResultSet rs = stmt.executeQuery()) {
                            if (rs.next()) {
                                employeeId = rs.getInt("EmployeeID");
                            }
                        }
                    }
                    
                    if (employeeId > 0) {
                        // Update Employee table
                        String empQuery = "UPDATE Employee SET FullName = ?, Email = ?, Phone = ? WHERE EmployeeID = ?";
                        try (PreparedStatement stmt = conn.prepareStatement(empQuery)) {
                            stmt.setString(1, fullName);
                            stmt.setString(2, email);
                            stmt.setString(3, phone);
                            stmt.setInt(4, employeeId);
                            stmt.executeUpdate();
                        }
                    }
                    
                    // Log activity
                    logUserActivity(userId, "PROFILE_UPDATE", "Updated profile information");
                    
                    conn.commit(); // Commit transaction
                    return true;
                    
                } catch (SQLException e) {
                    conn.rollback(); // Rollback on error
                    throw e;
                } finally {
                    conn.setAutoCommit(true); // Reset auto-commit
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating user profile", e);
        }
        
        return false;
    }
    
    /**
     * Verify current password
     */
    private boolean verifyCurrentPassword(int userId, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String query = "SELECT PasswordHash FROM SystemUser WHERE UserID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setInt(1, userId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String storedPassword = rs.getString("PasswordHash");
                            return DAO.getInstance().checkPassword(password, storedPassword);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error verifying password", e);
        }
        
        return false;
    }
    
    /**
     * Update password
     */
    private boolean updatePassword(int userId, String newPassword) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                String query = "UPDATE SystemUser SET PasswordHash = ?, UpdatedDate = NOW() WHERE UserID = ?";
                try (PreparedStatement stmt = conn.prepareStatement(query)) {
                    stmt.setString(1, newPassword);
                    stmt.setInt(2, userId);
                    
                    int rowsAffected = stmt.executeUpdate();
                    
                    // Log activity
                    logUserActivity(userId, "PASSWORD_CHANGE", "Changed password");
                    
                    return rowsAffected > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating password", e);
        }
        
        return false;
    }
    
    /**
     * Log user activity
     */
    private void logUserActivity(int userId, String activity, String description) {
        try {
            systemLogDAO.insertSystemLog(userId, activity, "UserProfile", null, description);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error logging user activity", e);
        }
    }
    
    /**
     * Inner class for user profile
     */
    public static class UserProfile {
        private int userId;
        private String username;
        private String fullName;
        private String email;
        private String phone;
        private String department;
        private String role;
        private Timestamp joinDate;
        private Timestamp lastLogin;
        private String status;
        
        // Getters and setters
        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
        
        public Timestamp getJoinDate() { return joinDate; }
        public void setJoinDate(Timestamp joinDate) { this.joinDate = joinDate; }
        
        public Timestamp getLastLogin() { return lastLogin; }
        public void setLastLogin(Timestamp lastLogin) { this.lastLogin = lastLogin; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
    
    /**
     * Inner class for user activity
     */
    public static class UserActivity {
        private int id;
        private String activity;
        private String description;
        private Timestamp activityDate;
        
        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getActivity() { return activity; }
        public void setActivity(String activity) { this.activity = activity; }
        
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        
        public Timestamp getActivityDate() { return activityDate; }
        public void setActivityDate(Timestamp activityDate) { this.activityDate = activityDate; }
    }
    
    /**
     * Inner class for user statistics
     */
    public static class UserStats {
        private int loginCount;
        private int daysSinceJoin;
        private int satisfactionRate;
        
        // Getters and setters
        public int getLoginCount() { return loginCount; }
        public void setLoginCount(int loginCount) { this.loginCount = loginCount; }
        
        public int getDaysSinceJoin() { return daysSinceJoin; }
        public void setDaysSinceJoin(int daysSinceJoin) { this.daysSinceJoin = daysSinceJoin; }
        
        public int getSatisfactionRate() { return satisfactionRate; }
        public void setSatisfactionRate(int satisfactionRate) { this.satisfactionRate = satisfactionRate; }
    } 

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
