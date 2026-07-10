package com.hrm.controller.admin;

import com.google.gson.Gson;
import com.hrm.dao.SystemUserDAO;
import com.hrm.dao.DAO;
import com.hrm.dao.RoleDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.DepartmentDAO;
import com.hrm.model.entity.SystemUser;
import com.hrm.model.entity.Role;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Department;
import com.hrm.dao.DBConnection;
import com.hrm.util.PermissionUtil;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import java.time.format.DateTimeFormatter;
import java.security.SecureRandom;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.sql.SQLException;

@WebServlet("/admin/users")
@MultipartConfig
public class UserController extends HttpServlet {

    private static final int PAGE_SIZE = 10;
    private static final String TEMP_PASS_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()";
    private static final int TEMP_PASS_LENGTH = 10; 
    private static final SecureRandom random = new SecureRandom();

    private String generateTempPassword() {
        StringBuilder sb = new StringBuilder(TEMP_PASS_LENGTH);
        for (int i = 0; i < TEMP_PASS_LENGTH; i++) {
            int randomIndex = random.nextInt(TEMP_PASS_CHARACTERS.length());
            sb.append(TEMP_PASS_CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {
        String action = request.getParameter("action");

        try (Connection conn = DBConnection.getConnection()) {
            if ("getUser".equals(action)) {
                handleGetUser(request, response, conn);
            } else if ("checkUsername".equals(action)) {
                handleCheckUsername(request, response, conn);
            } else if ("toggleStatus".equals(action)) {
                handleToggleStatus(request, response, conn);
            } else if ("delete".equals(action)) {
                handleDeleteUser(request, response, conn);
            } else {
                handleListUsers(request, response, conn);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
              throws ServletException, IOException {
        String action = request.getParameter("action");

        try (Connection conn = DBConnection.getConnection()) {
            if ("save".equals(action)) {
                handleSaveUser(request, response, conn);
            } else if ("update".equals(action)) {
                handleUpdateUser(request, response, conn);
            } else if ("resetPassword".equals(action)) {
                handleResetPassword(request, response, conn);
            } else if ("toggleStatus".equals(action)) {
                handleToggleStatus(request, response, conn);
            } else if ("delete".equals(action)) {
                handleDeleteUser(request, response, conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private void handleListUsers(HttpServletRequest request, HttpServletResponse response, Connection conn)
              throws Exception {
        SystemUserDAO userDAO = new SystemUserDAO();
        RoleDAO roleDAO = new RoleDAO();
        EmployeeDAO employeeDAO = new EmployeeDAO();
        DepartmentDAO departmentDAO = new DepartmentDAO();

        int page = 1;
        int pageSize = PAGE_SIZE;
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignore) {
            }
        }
        String pageSizeParam = request.getParameter("pageSize");
        if (pageSizeParam != null && !pageSizeParam.isEmpty()) {
            try {
                pageSize = Integer.parseInt(pageSizeParam);
            } catch (NumberFormatException ignore) {
            }
        }

        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");
        if (sortField == null || sortField.isEmpty()) {
            sortField = "CreatedDate";
        }
        if (sortOrder == null || sortOrder.isEmpty()) {
            sortOrder = "DESC";
        }

        Integer roleFilter = null;
        Integer statusFilter = null;
        Integer departmentFilter = null;
        String usernameFilter = null;

        String roleParam = request.getParameter("roleFilter");
        if (roleParam != null && !roleParam.isEmpty()) {
            roleFilter = Integer.parseInt(roleParam);
        }

        String statusParam = request.getParameter("statusFilter");
        if (statusParam != null && !statusParam.isEmpty()) {
            statusFilter = Integer.parseInt(statusParam);
        }

        String deptParam = request.getParameter("departmentFilter");
        if (deptParam != null && !deptParam.isEmpty()) {
            departmentFilter = Integer.parseInt(deptParam);
        }

        usernameFilter = request.getParameter("usernameFilter");

        List<SystemUser> users;
        Integer totalCountObj;

        if (roleFilter != null || statusFilter != null || departmentFilter != null || usernameFilter != null) {
            users = userDAO.getUsersWithFilters(page, pageSize, roleFilter, statusFilter, departmentFilter, usernameFilter, sortField, sortOrder);
            totalCountObj = userDAO.getTotalUserCountWithFilters(roleFilter, statusFilter, departmentFilter, usernameFilter);
        } else {
            users = userDAO.getAllUsers(page, pageSize, sortField, sortOrder);
            totalCountObj = userDAO.getTotalUserCount();
        }

        int totalCount = totalCountObj != null ? totalCountObj : 0;

        for (SystemUser user : users) {
            if (user.getEmployeeId() != null) {
                Employee employee = employeeDAO.getById(user.getEmployeeId());
                if (employee != null && employee.getDepartmentId() > 0) {
                    Department dept = departmentDAO.getById(employee.getDepartmentId());
                    user.setDepartment(dept);
                    user.setEmployee(employee);
                }
            }
        }

        int totalPages = (int) Math.ceil(totalCount / (double) pageSize);
        List<Role> roles = roleDAO.getAllRoles();
        List<Employee> employees = employeeDAO.getAll();
        List<Department> departments = departmentDAO.getAll();
        request.setAttribute("users", users);
        request.setAttribute("roles", roles);
        request.setAttribute("employees", employees);
        request.setAttribute("departments", departments);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);
        request.setAttribute("total", totalCount);
        request.setAttribute("sortField", sortField);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("activePage", "users");

        request.getRequestDispatcher("/Admin/Users.jsp").forward(request, response);
    }

    private void handleGetUser(HttpServletRequest request, HttpServletResponse response, Connection conn)
              throws Exception {
        int userId = Integer.parseInt(request.getParameter("id"));
        SystemUserDAO userDAO = new SystemUserDAO();
        SystemUser user = userDAO.getUserById(userId);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Gson gson = new GsonBuilder()
                  .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (src, typeOfSrc, context)
                            -> src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))))
                  .registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context)
                            -> src == null ? null : new JsonPrimitive(src.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))))
                  .create();

        PrintWriter out = response.getWriter();
        out.print(gson.toJson(user));
        out.flush();
    }

    private void handleCheckUsername(HttpServletRequest request, HttpServletResponse response, Connection conn)
              throws Exception {
        String username = request.getParameter("username");
        String userIdParam = request.getParameter("userId");
        
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        
        if (username == null || username.trim().isEmpty()) {
            out.write("{\"exists\":false,\"valid\":false,\"message\":\"Username không được để trống.\"}");
            out.flush();
            return;
        }
        
        SystemUserDAO userDAO = new SystemUserDAO();
        boolean exists;
        
        if (userIdParam != null && !userIdParam.isEmpty()) {
            int userId = Integer.parseInt(userIdParam);
            exists = userDAO.usernameExistsForOtherUser(username, userId);
        } else {
            exists = userDAO.usernameExists(username);
        }
        
        if (exists) {
            out.write("{\"exists\":true,\"valid\":false,\"message\":\"Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.\"}");
        } else {
            out.write("{\"exists\":false,\"valid\":true,\"message\":\"Username có thể sử dụng.\"}");
        }
        out.flush();
    }

    private void handleSaveUser(HttpServletRequest request, HttpServletResponse response, Connection conn)
              throws Exception {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String roleIdStr = request.getParameter("roleId");
        String empParam = request.getParameter("employeeId");
        boolean isActive = request.getParameter("isActive") != null;

        if (username == null || username.trim().length() < 3 || username.trim().length() > 50) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            out.write("{\"success\":false,\"message\":\"Username phải có từ 3–50 ký tự.\"}");
            out.flush();
            return;
        }

        if (!username.matches("^[A-Za-z0-9._-]+$")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            out.write("{\"success\":false,\"message\":\"Username chỉ được chứa chữ, số, dấu '.', '-' hoặc '_'.\"}");
            out.flush();
            return;
        }

        SystemUserDAO userDAO = new SystemUserDAO();
        if (userDAO.usernameExists(username)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            out.write("{\"success\":false,\"message\":\"Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.\"}");
            out.flush();
            return;
        }

        if (password == null || password.length() < 8
                  || !password.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            out.write("{\"success\":false,\"message\":\"Mật khẩu phải có ít nhất 8 ký tự, gồm 1 chữ hoa, 1 chữ thường và 1 số.\"}");
            out.flush();
            return;
        }

        if (roleIdStr == null || roleIdStr.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            out.write("{\"success\":false,\"message\":\"Vui lòng chọn vai trò.\"}");
            out.flush();
            return;
        }

        Integer employeeId = null;
        if (empParam != null && !empParam.isEmpty()) {
            try {
                employeeId = Integer.parseInt(empParam);
            } catch (NumberFormatException ex) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.write("{\"success\":false,\"message\":\"ID nhân viên không hợp lệ.\"}");
                out.flush();
                return;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            out.write("{\"success\":false,\"message\":\"Vui lòng chọn nhân viên.\"}");
            out.flush();
            return;
        }

        int roleId = Integer.parseInt(roleIdStr);

        SystemUser existingAdmin = userDAO.getAdminUser();
        if (roleId == PermissionUtil.ROLE_ADMIN && existingAdmin != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            out.write("{\"success\":false,\"message\":\"Hệ thống chỉ cho phép duy nhất một tài khoản Admin.\"}");
            out.flush();
            return;
        }

        SystemUser existingByEmployee = userDAO.getByEmployeeId(employeeId);
        if (existingByEmployee != null) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            out.write("{\"success\":false,\"message\":\"Nhân viên này đã có tài khoản. Vui lòng chọn nhân viên khác.\"}");
            out.flush();
            return;
        }

        SystemUser user = new SystemUser();
        user.setUsername(username);
        user.setPassword("");
        user.setRoleId(roleId);
        user.setEmployeeId(employeeId);
        user.setIsActive(isActive);

        try {
            if (userDAO.createUser(user)) {
                SystemUser createdUser = userDAO.getByUsername(username);
                if (createdUser == null) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.write("{\"success\":false,\"message\":\"Không thể tải thông tin người dùng sau khi tạo.\"}");
                    out.flush();
                    return;
                }
                int changeResult = DAO.getInstance().changePassword(username, password);
                if (changeResult <= 0) {
                    userDAO.deleteUser(createdUser.getUserId());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
                    out.write("{\"success\":false,\"message\":\"Không thể thiết lập mật khẩu cho tài khoản mới.\"}");
                } else {
                    out.write("{\"success\":true,\"message\":\"Tạo người dùng thành công.\"}");
                }
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
                out.write("{\"success\":false,\"message\":\"Không thể tạo người dùng.\"}");
            }
        } catch (SQLException sqlEx) {
            sqlEx.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"success\":false,\"message\":\"Lỗi cơ sở dữ liệu: " + sqlEx.getMessage().replace("\"", "\\\"") + "\"}");
        } finally {
            out.flush();
        }
    }

    private void handleUpdateUser(HttpServletRequest request, HttpServletResponse response, Connection conn)
              throws Exception {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {

            String idParam = request.getParameter("id");
            String username = request.getParameter("username");
            String password = request.getParameter("password"); 
            String roleIdParam = request.getParameter("roleId");
            String empParam = request.getParameter("employeeId");
            boolean isActive = request.getParameter("isActive") != null;

            SystemUserDAO userDAO = new SystemUserDAO();

            int userId;
            try {
                userId = Integer.parseInt(idParam);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                out.write("{\"success\":false,\"message\":\"ID người dùng không hợp lệ.\"}");
                out.flush();
                return;
            }

            SystemUser existingUser = userDAO.getUserById(userId);
            if (existingUser == null) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.write("{\"success\":false,\"message\":\"Không tìm thấy người dùng để cập nhật.\"}");
                out.flush();
                return;
            }

            int roleId;
            try {
                roleId = Integer.parseInt(roleIdParam);
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                out.write("{\"success\":false,\"message\":\"Vui lòng chọn vai trò.\"}");
                out.flush();
                return;
            }

            Integer employeeId;
            try {
                if (empParam != null && !empParam.isEmpty()) {
                    employeeId = Integer.parseInt(empParam);
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                    out.write("{\"success\":false,\"message\":\"Vui lòng chọn nhân viên.\"}");
                    out.flush();
                    return;
                }
            } catch (NumberFormatException e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                out.write("{\"success\":false,\"message\":\"ID nhân viên không hợp lệ.\"}");
                out.flush();
                return;
            }

            if (username == null || username.trim().length() < 3 || username.trim().length() > 50) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                out.write("{\"success\":false,\"message\":\"Username phải có từ 3–50 ký tự.\"}");
                out.flush();
                return;
            }
            if (!username.matches("^[A-Za-z0-9._-]+$")) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                out.write("{\"success\":false,\"message\":\"Username chỉ được chứa chữ, số, dấu '.', '-' hoặc '_'.\"}");
                out.flush();
                return;
            }

            if (userDAO.usernameExistsForOtherUser(username, userId)) {
                response.setStatus(HttpServletResponse.SC_CONFLICT); 
                out.write("{\"success\":false,\"message\":\"Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.\"}");
                out.flush();
                return;
            }

            SystemUser existingByEmployee = userDAO.getByEmployeeId(employeeId);
            if (existingByEmployee != null && existingByEmployee.getUserId() != userId) {
                response.setStatus(HttpServletResponse.SC_CONFLICT); 
                out.write("{\"success\":false,\"message\":\"Nhân viên này đã có tài khoản. Vui lòng chọn nhân viên khác.\"}");
                out.flush();
                return;
            }

            SystemUser currentAdmin = userDAO.getAdminUser();
            if (roleId == PermissionUtil.ROLE_ADMIN) {
                if (currentAdmin != null && currentAdmin.getUserId() != userId) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.write("{\"success\":false,\"message\":\"Hệ thống chỉ cho phép duy nhất một tài khoản Admin.\"}");
                    out.flush();
                    return;
                }
            } else {
                if (currentAdmin != null && currentAdmin.getUserId() == userId) {
                    response.setStatus(HttpServletResponse.SC_CONFLICT);
                    out.write("{\"success\":false,\"message\":\"Không thể thay đổi vai trò của Admin duy nhất trong hệ thống.\"}");
                    out.flush();
                    return;
                }
            }

            SystemUser user = new SystemUser();
            user.setUserId(userId);
            user.setUsername(username);
            user.setRoleId(roleId);
            user.setEmployeeId(employeeId);
            user.setIsActive(isActive);

            boolean basicUpdateSuccess = userDAO.updateUser(user);

            if (!basicUpdateSuccess) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
                out.write("{\"success\":false,\"message\":\"Không thể cập nhật thông tin người dùng.\"}");
                out.flush();
                return;
            }

            if (password != null && !password.isEmpty()) {
                if (password.length() < 8 || !password.matches("(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*")) {
                    out.write("{\"success\":true,\"message\":\"Thông tin người dùng đã được cập nhật thành công, NHƯNG mật khẩu mới không hợp lệ (yêu cầu 8 ký tự, 1 hoa, 1 thường, 1 số) và KHÔNG được lưu.\"}");
                    out.flush();
                    return;
                }
                int passwordUpdated = DAO.getInstance().changePassword(user.getUsername(), password);
                if (passwordUpdated <= 0) {
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.write("{\"success\":false,\"message\":\"Không thể cập nhật mật khẩu người dùng.\"}");
                    out.flush();
                    return;
                }
            }

            out.write("{\"success\":true,\"message\":\"Cập nhật người dùng thành công.\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
            out.write("{\"success\":false,\"message\":\"Lỗi xử lý phía server: " + e.getMessage() + "\"}");
            out.flush();
        }
    }

    private void handleResetPassword(HttpServletRequest request, HttpServletResponse response, Connection conn)
              throws Exception {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            out.write("{\"success\":false,\"message\":\"Thiếu ID người dùng.\"}");
            out.flush();
            return;
        }

        int userId = Integer.parseInt(idParam);
        String tempPassword = generateTempPassword(); 

        SystemUserDAO userDAO = new SystemUserDAO();
        SystemUser user = userDAO.getById(userId);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("{\"success\":false,\"message\":\"Không tìm thấy người dùng.\"}");
        } else if (DAO.getInstance().changePassword(user.getUsername(), tempPassword) > 0) {
            out.write("{\"success\":true,\"message\":\"Đặt lại mật khẩu thành công. Mật khẩu tạm thời: " + tempPassword + "\"}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
            out.write("{\"success\":false,\"message\":\"Không thể đặt lại mật khẩu.\"}");
        }
        out.flush();
    }

    private void handleToggleStatus(HttpServletRequest request, HttpServletResponse response, Connection conn)
              throws Exception {
        int userId = Integer.parseInt(request.getParameter("id"));
        SystemUserDAO userDAO = new SystemUserDAO();

        if (userDAO.toggleUserStatus(userId)) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to toggle user status");
        }
    }

    private void handleDeleteUser(HttpServletRequest request, HttpServletResponse response, Connection conn)
              throws Exception {
        int userId = Integer.parseInt(request.getParameter("id"));
        SystemUserDAO userDAO = new SystemUserDAO();

        if (userDAO.deleteUser(userId)) {
            response.sendRedirect(request.getContextPath() + "/admin/users");
        } else {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete user");
        }
    }
}
