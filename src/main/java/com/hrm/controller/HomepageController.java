/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller;

import com.hrm.dao.DBConnection;
import com.hrm.dao.SystemUserDAO;
import com.hrm.dao.SystemLogDAO;
import com.hrm.dao.RoleDAO;
import com.hrm.model.entity.SystemUser;
import com.hrm.model.entity.SystemLog;
import com.hrm.model.entity.Role;
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
@WebServlet(name="HomepageController", urlPatterns={"/homepage"})
public class HomepageController extends HttpServlet {
    
    private static final Logger logger = Logger.getLogger(HomepageController.class.getName());
    private final SystemLogDAO systemLogDAO = new SystemLogDAO();
    private final SystemUserDAO systemUserDAO = new SystemUserDAO();
    private final RoleDAO roleDAO = new RoleDAO();
   
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
            // Get session
            HttpSession session = request.getSession();
            
            // Check if user is logged in
            SystemUser currentUser = (SystemUser) session.getAttribute("systemUser");
            
            // Get company statistics
            CompanyStats stats = getCompanyStats();
            request.setAttribute("companyStats", stats);
            
            // Get recent news/announcements
            List<NewsItem> recentNews = getRecentNews();
            request.setAttribute("recentNews", recentNews);
            
            String mode = request.getParameter("mode");
            boolean forcePublicView = mode != null && mode.equalsIgnoreCase("public");
            if (!forcePublicView) {
                String referer = request.getHeader("Referer");
                if (referer != null) {
                    String normalizedRef = referer.toLowerCase();
                    if (normalizedRef.contains("/admin")) {
                        forcePublicView = true;
                    }
                }
            }
            
            // Get user info if logged in
            if (currentUser != null) {
                request.setAttribute("currentUser", currentUser);
                request.setAttribute("isLoggedIn", true);
                
                // Get user role and permissions
                Role userRole = getUserRole(currentUser.getRoleId());
                request.setAttribute("userRole", userRole);
                DashboardAccess dashboardAccess = getDashboardAccess(userRole);
                request.setAttribute("dashboardAccess", dashboardAccess);
                request.setAttribute("isPublicView", forcePublicView);
                
            } else {
                request.setAttribute("isLoggedIn", false);
                request.setAttribute("dashboardAccess", getGuestDashboardAccess());
            }
            
            // Forward to homepage JSP
            request.getRequestDispatcher("/Views/Homepage.jsp").forward(request, response);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in HomepageController", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An error occurred while loading the homepage");
        }
    }
    
    /**
     * Get company statistics from database
     */
    private CompanyStats getCompanyStats() {
        CompanyStats stats = new CompanyStats();
        
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null) {
                // Get total employees
                String empQuery = "SELECT COUNT(*) as total FROM Employee WHERE Status = 'Active'";
                try (PreparedStatement stmt = conn.prepareStatement(empQuery);
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setTotalEmployees(rs.getInt("total"));
                    }
                }
                
                // Get total departments
                String deptQuery = "SELECT COUNT(*) as total FROM Department";
                try (PreparedStatement stmt = conn.prepareStatement(deptQuery);
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        stats.setTotalDepartments(rs.getInt("total"));
                    }
                }
                
                // Get total tasks
                String taskQuery = "SELECT COUNT(*) as total FROM Task WHERE Status != 'Completed'";
                try (PreparedStatement stmt = conn.prepareStatement(taskQuery);
                     ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
stats.setTotalProjects(rs.getInt("total"));
                    }
                }
                
                // Get years of experience (assuming company start date)
                stats.setYearsExperience(10); // Default value, can be calculated from company start date
                
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error getting company stats", e);
        }
        
        return stats;
    }
    
    /**
     * Get recent news/announcements
     */
    private List<NewsItem> getRecentNews() {
        List<NewsItem> newsList = new ArrayList<>();
        
        try {
            // Get recent system logs with user info
            List<SystemLog> systemLogs = systemLogDAO.getAllSystemLogsWithUserInfo();
            
            // Take only the first 3 logs
            int count = 0;
            for (SystemLog log : systemLogs) {
                if (count >= 3) break;
                
                NewsItem news = new NewsItem();
                news.setId(log.getLogId());
                news.setTitle(log.getAction() + " by " + (log.getNewValue() != null ? log.getNewValue() : "System"));
                news.setContent(log.getNewValue() != null ? log.getNewValue() : log.getAction());
                news.setCreatedDate(Timestamp.valueOf(log.getTimestamp()));
                news.setType(log.getObjectType());
                newsList.add(news);
                count++;
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting recent news", e);
            // Add default news if database fails
            newsList.add(new NewsItem(1, "Company Expansion", "We are excited to announce our expansion into new markets.", "expansion"));
            newsList.add(new NewsItem(2, "Industry Recognition", "Our company has been recognized as the best service provider.", "award"));
            newsList.add(new NewsItem(3, "Team Growth", "We welcome 20 new talented professionals to our team.", "team"));
        }
        
        return newsList;
    }
    
    /**
     * Get user role by role ID
     */
    private Role getUserRole(int roleId) {
        try {
            return roleDAO.getRoleById(roleId);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error getting user role", e);
            return null;
        }
    }
    
    /**
     * Get dashboard access permissions based on user role
     */
    private DashboardAccess getDashboardAccess(Role userRole) {
        DashboardAccess access = new DashboardAccess();
        
        if (userRole == null) {
            return getGuestDashboardAccess();
        }
        
        // Normalize DB role names (e.g., "HR Manager", "HR Staff") to internal groups
        String roleName = roleDAO.normalizeRoleName(userRole.getRoleName());
        String dbRoleName = userRole.getRoleName() != null ? userRole.getRoleName().trim().toLowerCase() : "";
        
        switch (roleName) {
            case "admin":
                access.setCanAccessAdmin(true);
                access.setCanAccessHR(true);
                access.setCanAccessDept(true);
                access.setCanAccessEmployee(true);
                access.setCanAccessGuest(true);
                access.setAdminUrl("/admin?action=dashboard");
                access.setHrUrl("/HrHomeController");
                access.setDeptUrl("/dept");
                access.setCanAccessHrStaff(true);
                access.setHrStaffUrl("/hrstaff");
                access.setEmployeeUrl("/employee");
                access.setGuestUrl("/guest/dashboard");
                break;
                
            case "hr":
                access.setCanAccessAdmin(false);
                // Distinguish HR Manager vs HR Staff by original DB role name
                boolean isHrStaff = dbRoleName.contains("staff");
                access.setCanAccessHR(!isHrStaff);
                access.setCanAccessHrStaff(isHrStaff);
                access.setCanAccessEmployee(true);
                access.setCanAccessGuest(true);
                access.setHrUrl("/HrHomeController");
                access.setHrStaffUrl("/hrstaff");
                access.setEmployeeUrl("/employee");
                access.setGuestUrl("/guest/dashboard");
                break;

            case "dept":
                access.setCanAccessAdmin(false);
                access.setCanAccessHR(false);
                access.setCanAccessHrStaff(false);
                access.setCanAccessDept(true);
                access.setCanAccessEmployee(false);
                access.setCanAccessGuest(true);
                access.setDeptUrl("/dept");
                access.setGuestUrl("/guest/dashboard");
                break;
                
            case "employee":
                access.setCanAccessAdmin(false);
                access.setCanAccessHR(false);
                access.setCanAccessDept(false);
                access.setCanAccessEmployee(true);
                access.setCanAccessGuest(true);
                access.setEmployeeUrl("/employee");
                access.setGuestUrl("/guest/dashboard");
                break;
                
            default:
                return getGuestDashboardAccess();
        }
        
        return access;
    }
    
    /**
     * Get guest dashboard access (limited permissions)
     */
    private DashboardAccess getGuestDashboardAccess() {
        DashboardAccess access = new DashboardAccess();
        access.setCanAccessAdmin(false);
        access.setCanAccessHR(false);
        access.setCanAccessDept(false);
        access.setCanAccessEmployee(false);
        access.setCanAccessGuest(true);
        access.setGuestUrl("/guest/dashboard");
        return access;
    }
    
    /**
     * Inner class for dashboard access permissions
     */
    public static class DashboardAccess {
        private boolean canAccessAdmin = false;
        private boolean canAccessHR = false;
        private boolean canAccessHrStaff = false;
        private boolean canAccessDept = false;
        private boolean canAccessEmployee = false;
        private boolean canAccessGuest = false;
        private String adminUrl;
        private String hrUrl;
        private String hrStaffUrl;
        private String deptUrl;
        private String employeeUrl;
        private String guestUrl;
        
        // Getters and setters
        public boolean isCanAccessAdmin() { return canAccessAdmin; }
        public void setCanAccessAdmin(boolean canAccessAdmin) { this.canAccessAdmin = canAccessAdmin; }
        
        public boolean isCanAccessHR() { return canAccessHR; }
        public void setCanAccessHR(boolean canAccessHR) { this.canAccessHR = canAccessHR; }
        
        public boolean isCanAccessHrStaff() { return canAccessHrStaff; }
        public void setCanAccessHrStaff(boolean canAccessHrStaff) { this.canAccessHrStaff = canAccessHrStaff; }

        public boolean isCanAccessDept() { return canAccessDept; }
        public void setCanAccessDept(boolean canAccessDept) { this.canAccessDept = canAccessDept; }

        public boolean isCanAccessEmployee() { return canAccessEmployee; }
        public void setCanAccessEmployee(boolean canAccessEmployee) { this.canAccessEmployee = canAccessEmployee; }
        
        public boolean isCanAccessGuest() { return canAccessGuest; }
public void setCanAccessGuest(boolean canAccessGuest) { this.canAccessGuest = canAccessGuest; }
        
        public String getAdminUrl() { return adminUrl; }
        public void setAdminUrl(String adminUrl) { this.adminUrl = adminUrl; }
        
        public String getHrUrl() { return hrUrl; }
        public void setHrUrl(String hrUrl) { this.hrUrl = hrUrl; }
        
        public String getHrStaffUrl() { return hrStaffUrl; }
        public void setHrStaffUrl(String hrStaffUrl) { this.hrStaffUrl = hrStaffUrl; }

        public String getDeptUrl() { return deptUrl; }
        public void setDeptUrl(String deptUrl) { this.deptUrl = deptUrl; }

        public String getEmployeeUrl() { return employeeUrl; }
        public void setEmployeeUrl(String employeeUrl) { this.employeeUrl = employeeUrl; }
        
        public String getGuestUrl() { return guestUrl; }
        public void setGuestUrl(String guestUrl) { this.guestUrl = guestUrl; }
    }
    
    /**
     * Inner class for company statistics
     */
    public static class CompanyStats {
        private int totalEmployees;
        private int totalDepartments;
        private int totalProjects;
        private int yearsExperience;
        
        // Getters and setters
        public int getTotalEmployees() { return totalEmployees; }
        public void setTotalEmployees(int totalEmployees) { this.totalEmployees = totalEmployees; }
        
        public int getTotalDepartments() { return totalDepartments; }
        public void setTotalDepartments(int totalDepartments) { this.totalDepartments = totalDepartments; }
        
        public int getTotalProjects() { return totalProjects; }
        public void setTotalProjects(int totalProjects) { this.totalProjects = totalProjects; }
        
        public int getYearsExperience() { return yearsExperience; }
        public void setYearsExperience(int yearsExperience) { this.yearsExperience = yearsExperience; }
    }
    
    /**
     * Inner class for news items
     */
    public static class NewsItem {
        private int id;
        private String title;
        private String content;
        private java.sql.Timestamp createdDate;
        private String type;
        
        public NewsItem() {}
        
        public NewsItem(int id, String title, String content, String type) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.type = type;
        }
        
        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public java.sql.Timestamp getCreatedDate() { return createdDate; }
        public void setCreatedDate(java.sql.Timestamp createdDate) { this.createdDate = createdDate; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
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
