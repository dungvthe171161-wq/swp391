package com.hrm.controller.admin;

import com.hrm.dao.RoleDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.SystemUserDAO;
import com.hrm.model.entity.Role;
import com.hrm.model.entity.SystemUser;
import com.hrm.model.entity.Employee;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;

@WebServlet(name = "RoleServlet", urlPatterns = {"/admin/role/*"})
public class RoleServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RoleServlet.class.getName());
    private final RoleDAO roleDAO = new RoleDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final SystemUserDAO userDAO = new SystemUserDAO();
    private static final int DEFAULT_PAGE_SIZE = 10;


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        
        try {
            if (pathInfo == null || "/".equals(pathInfo) || "/list".equals(pathInfo)) {
                handleListRoles(request, response);
            } else if (pathInfo.matches("/\\d+")) {
                int roleId = Integer.parseInt(pathInfo.substring(1));
                handleGetRole(roleId, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo == null || "/".equals(pathInfo)) {
                handleCreateRole(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                int roleId = Integer.parseInt(pathInfo.substring(1));
                handleUpdateRole(roleId, request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            handleError(response, e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String pathInfo = request.getPathInfo();
            
            if (pathInfo != null && pathInfo.matches("/\\d+")) {
                int roleId = Integer.parseInt(pathInfo.substring(1));
                handleDeleteRole(roleId, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (SQLException e) {
            handleError(response, e);
        }
    }

    private void handleListRoles(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        
        // Set current user info for display
        setCurrentUserInfo(request);
        
        String pageStr = request.getParameter("page");
        String pageSizeStr = request.getParameter("pageSize");
        String searchQuery = request.getParameter("search");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        HttpSession session = request.getSession(false); 
        
        if (session != null) {
            String successMessage = (String) session.getAttribute("successMessage");
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
                session.removeAttribute("successMessage"); 
            }

            String errorMessage = (String) session.getAttribute("errorMessage");
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("errorMessage");
            }
        }
        
        int page = 1;
        int pageSize = DEFAULT_PAGE_SIZE; 
        
        try {
            page = (pageStr != null && pageStr.matches("\\d+")) ? Integer.parseInt(pageStr) : 1;
        } catch (NumberFormatException e) {
            page = 1;
        }
        try {
            pageSize = (pageSizeStr != null && pageSizeStr.matches("\\d+")) ? Integer.parseInt(pageSizeStr) : DEFAULT_PAGE_SIZE;
            if (pageSize > 100) pageSize = 100;
        } catch (NumberFormatException e) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "RoleID"; 
        }
        if (sortOrder == null || sortOrder.trim().isEmpty()) {
            sortOrder = "ASC"; 
        }
        
        int totalRoles = roleDAO.getTotalRoleCount(searchQuery);

        int totalPages = (int) Math.ceil((double) totalRoles / pageSize);
        if (page < 1) page = 1;
        if (page > totalPages && totalPages > 0) page = totalPages;

        int offset = (page - 1) * pageSize;
        
        List<Role> roles = roleDAO.getPagedRoles(
                offset, pageSize, searchQuery, sortBy, sortOrder);

        request.setAttribute("roles", roles);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("total", totalRoles);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("searchQuery", searchQuery != null ? searchQuery : "");
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        
        request.setAttribute("activePage", "roles");
        request.getRequestDispatcher("/Admin/Roles.jsp").forward(request, response);
    }

    private void handleGetRole(int roleId, HttpServletResponse response)
            throws SQLException, IOException {
        try {
            Role role = roleDAO.getRoleById(roleId);
            if (role != null) {
                JSONObject jsonResponse = new JSONObject();
                jsonResponse.put("roleId", role.getRoleId());
                jsonResponse.put("roleName", role.getRoleName());
                sendJsonResponse(response, jsonResponse.toString());
            } else {
                sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Role not found");
            }
        } catch (SQLException e) {
            handleError(response, e);
        }
    }

    private void handleCreateRole(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        try {
            JSONObject jsonRequest = readJsonRequest(request);
            
            if (!jsonRequest.has("roleName") || jsonRequest.getString("roleName").trim().isEmpty()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Role name is required");
                return;
            }

            String roleName = jsonRequest.getString("roleName").trim();

            Role role = new Role();
            role.setRoleName(roleName);

            if (roleDAO.createRole(role)) {
                request.getSession().setAttribute("successMessage", "Role created successfully");
                response.setStatus(HttpServletResponse.SC_CREATED);
                JSONObject successResponse = new JSONObject()
                    .put("message", "Role created successfully")
                    .put("status", "success");
                sendJsonResponse(response, successResponse.toString());
                logger.log(Level.INFO, "Role created: " + roleName);
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to create role");
                sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to create role");
            }
        } catch (IllegalArgumentException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            request.getSession().setAttribute("errorMessage", "Database error: " + e.getMessage());
            handleError(response, e);
        }
    }

    private void handleUpdateRole(int roleId, HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        try {
            JSONObject jsonRequest = readJsonRequest(request);
            
            if (!jsonRequest.has("roleName") || jsonRequest.getString("roleName").trim().isEmpty()) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Role name is required");
                return;
            }

            String roleName = jsonRequest.getString("roleName").trim();

            Role role = new Role();
            role.setRoleId(roleId);
            role.setRoleName(roleName);

            if (roleDAO.updateRole(role)) {
                JSONObject successResponse = new JSONObject()
                    .put("message", "Role updated successfully")
                    .put("status", "success");
                sendJsonResponse(response, successResponse.toString());
                logger.log(Level.INFO, "Role updated: ID=" + roleId);
            } else {
                sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Role not found");
            }
        } catch (IllegalArgumentException e) {
            sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (SQLException e) {
            handleError(response, e);
        }
    }

    private void handleDeleteRole(int roleId, HttpServletResponse response)
            throws SQLException, IOException {
        try {
            if (roleDAO.deleteRole(roleId)) {
                JSONObject successResponse = new JSONObject()
                    .put("message", "Role deleted successfully")
                    .put("status", "success");
                sendJsonResponse(response, successResponse.toString());
                logger.log(Level.INFO, "Role deleted: ID=" + roleId);
            } else {
                sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Role not found");
            }
        } catch (IllegalArgumentException e) {
            sendJsonError(response, HttpServletResponse.SC_CONFLICT, e.getMessage());
        } catch (SQLException e) {
            handleError(response, e);
        }
    }

    private JSONObject readJsonRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (var reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return new JSONObject(sb.toString());
    }

    private void sendJsonError(HttpServletResponse response, int statusCode, String errorMessage) throws IOException {
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject errorResponse = new JSONObject()
            .put("status", "error")
            .put("message", errorMessage);
        response.getWriter().write(errorResponse.toString());
    }

    private void sendJsonResponse(HttpServletResponse response, String json) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }

    private void handleError(HttpServletResponse response, Exception e) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        JSONObject errorResponse = new JSONObject()
                .put("status", "error")
                .put("message", "Internal server error")
                .put("error", e.getMessage());
        response.getWriter().write(errorResponse.toString());
        logger.log(Level.SEVERE, "Servlet error", e);
    }

    /**
     * Helper method to set current user info from session to request attribute
     * This allows all admin pages to access the current user's information
     */
    private void setCurrentUserInfo(HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            if (session != null) {
                SystemUser currentUser = (SystemUser) session.getAttribute("systemUser");
                if (currentUser != null) {
                    // Get full user info from database
                    SystemUser fullUser = userDAO.getUserById(currentUser.getUserId());
                    if (fullUser != null) {
                        // Get employee info if exists
                        if (fullUser.getEmployeeId() != null) {
                            Employee employee = employeeDAO.getById(fullUser.getEmployeeId());
                            if (employee != null) {
                                fullUser.setEmployee(employee);
                                // Set employee name for display
                                String employeeName = employee.getFullName();
                                if (employeeName != null && !employeeName.trim().isEmpty()) {
                                    request.setAttribute("currentUserName", employeeName.trim());
                                }
                            }
                        }
                        // If no employee, use username
                        if (request.getAttribute("currentUserName") == null) {
                            request.setAttribute("currentUserName", fullUser.getUsername());
                        }
                        request.setAttribute("currentUser", fullUser);
                    } else {
                        // Fallback to session user
                        request.setAttribute("currentUserName", currentUser.getUsername());
                        request.setAttribute("currentUser", currentUser);
                    }
                }
            }
        } catch (Exception e) {
            // Silently fail - user info is optional for display
            logger.log(Level.WARNING, "Failed to set current user info", e);
        }
    }
}
