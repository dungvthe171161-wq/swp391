package com.hrm.controller.admin;

import com.hrm.dao.*;
import com.hrm.model.entity.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "DepartmentController", urlPatterns = {"/departments"})
public class DepartmentController extends HttpServlet {

    private DepartmentDAO departmentDAO = new DepartmentDAO();
    private EmployeeDAO employeeDAO = new EmployeeDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "departments":
                listDepartments(request, response);
                break;
            case "edit":
                editDepartment(request, response);
                break;
            case "permissions":
                manageDepartmentPermissions(request, response);
                break;
            default:
                listDepartments(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = "save";
        }

        switch (action) {
            case "department-save":
                saveDepartment(request, response);
                break;
            case "department-delete":
                deleteDepartment(request, response);
                break;
            case "department-permissions-save":
                savePermissions(request, response);
                break;
            default:
                listDepartments(request, response);
        }
    }

    private void listDepartments(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "departments");

        jakarta.servlet.http.HttpSession session = request.getSession(false);
        if (session != null) {
            String successMessage = (String) session.getAttribute("successMessage");
            String errorMessage = (String) session.getAttribute("errorMessage");
            
            if (successMessage != null) {
                request.setAttribute("successMessage", successMessage);
                session.removeAttribute("successMessage");
            }
            if (errorMessage != null) {
                request.setAttribute("errorMessage", errorMessage);
                session.removeAttribute("errorMessage");
            }
        }

        String searchKeyword = request.getParameter("search");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        
        if (sortOrder == null || (!sortOrder.equals("ASC") && !sortOrder.equals("DESC"))) {
            sortOrder = "ASC";
        }

        int page = 1;
        int pageSize = 10;
        try {
            String pageStr = request.getParameter("page");
            String sizeStr = request.getParameter("pageSize");
            if (pageStr != null) page = Math.max(1, Integer.parseInt(pageStr));
            if (sizeStr != null) {
                pageSize = Integer.parseInt(sizeStr);
                if (pageSize < 1) pageSize = 1;
                if (pageSize > 100) pageSize = 100;
            }
        } catch (NumberFormatException ignore) {}

        try {
            int total = 0;
            int offset = (page - 1) * pageSize;
            
            List<Department> departmentList = new ArrayList<>();
            
            StringBuilder sqlCount = new StringBuilder(
                "SELECT COUNT(*) as total FROM Department WHERE 1=1"
            );
            StringBuilder sql = new StringBuilder(
                "SELECT DepartmentID, DeptName, DeptManagerID FROM Department WHERE 1=1"
            );
            
            List<Object> params = new ArrayList<>();
            List<Object> countParams = new ArrayList<>();
            
            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
                String searchCondition = " AND (DeptName LIKE ? OR DepartmentID = ?)";
                sqlCount.append(searchCondition);
                sql.append(searchCondition);
                
                countParams.add("%" + searchKeyword.trim() + "%");
                countParams.add(searchKeyword.trim()); 
                
                params.add("%" + searchKeyword.trim() + "%");
                params.add(searchKeyword.trim());
            }
            
            sql.append(" ORDER BY ");
            String sortColumn = "DeptName"; 
            if (sortBy != null) {
                switch(sortBy.toLowerCase()) {
                    case "id":
                        sortColumn = "DepartmentID";
                        break;
                    case "name":
                        sortColumn = "DeptName";
                        break;
                    case "manager":
                        sortColumn = "DeptManagerID";
                        break;
                    default:
                        sortColumn = "DeptName";
                }
            }
            sql.append(sortColumn).append(" ").append(sortOrder);
            
            sql.append(" LIMIT ? OFFSET ?");
            params.add(pageSize);
            params.add(offset);
            
            Connection conn = null;
            try {
                conn = DBConnection.getConnection();
                if (conn == null) {
                    throw new SQLException("Cannot connect to database");
                }
                
                try (PreparedStatement psCount = conn.prepareStatement(sqlCount.toString())) {
                    for (int i = 0; i < countParams.size(); i++) {
                        psCount.setObject(i + 1, countParams.get(i));
                    }
                    try (ResultSet rsCount = psCount.executeQuery()) {
                        if (rsCount.next()) {
                            total = rsCount.getInt("total");
                        }
                    }
                }
                
                departmentList = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(sql.toString())) {
                    for (int i = 0; i < params.size(); i++) {
                        ps.setObject(i + 1, params.get(i));
                    }
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            Department dept = new Department(
                                rs.getInt("DepartmentID"),
                                rs.getString("DeptName"),
                                rs.getObject("DeptManagerID") != null ? rs.getInt("DeptManagerID") : null
                            );
                            
                            try {
                                int count = employeeDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
                                dept.setEmployeeCount(count);
                            } catch (Exception ignore) {}
                            
                            departmentList.add(dept);
                        }
                    }
                }
            } catch (SQLException e) {
                throw new ServletException("Database error: " + e.getMessage(), e);
            } finally {
                try {
                    if (conn != null) conn.close();
                } catch (SQLException e) {
                    Logger.getLogger(DepartmentController.class.getName())
                          .log(Level.SEVERE, "Error closing database connection", e);
                }
            }
            
            int totalPages = (int) Math.ceil(total / (double) pageSize);
            if (totalPages == 0) totalPages = 1;
            if (page > totalPages) page = totalPages;
            
            request.setAttribute("departmentList", departmentList);
            request.setAttribute("total", total);
            request.setAttribute("totalPages", totalPages);
            
        } catch (Exception e) {
            List<Department> departmentList = departmentDAO.getAll();
            request.setAttribute("departmentList", departmentList);
            request.setAttribute("errorMessage", "Search failed, showing all departments: " + e.getMessage());
            
            int totalFallback = departmentList != null ? departmentList.size() : 0;
            int totalPagesFallback = (int) Math.ceil(totalFallback / (double) pageSize);
            if (totalPagesFallback == 0) totalPagesFallback = 1;
            request.setAttribute("total", totalFallback);
            request.setAttribute("totalPages", totalPagesFallback);
        }

        List<Employee> managers = employeeDAO.getManagerList();
        List<Department> allDepartments = departmentDAO.getAll();

        request.setAttribute("allDepartments", allDepartments);
        request.setAttribute("managers", managers);
        request.setAttribute("searchKeyword", searchKeyword);
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);
        request.setAttribute("page", page);
        request.setAttribute("pageSize", pageSize);

        request.getRequestDispatcher("Admin/Departments.jsp").forward(request, response);
    }

    private void saveDepartment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String deptId = request.getParameter("deptId");
        String deptName = request.getParameter("deptName");
        String deptManagerId = request.getParameter("deptManagerId");

        if (deptName == null || deptName.trim().isEmpty()) {
            jakarta.servlet.http.HttpSession session = request.getSession();
            session.setAttribute("errorMessage", "Tên phòng ban là bắt buộc!");
            response.sendRedirect(request.getContextPath() + "/departments?action=departments");
            return;
        }

        Department dept = new Department();
        dept.setDeptName(deptName.trim());
        dept.setDeptManagerId(deptManagerId != null && !deptManagerId.isEmpty() ? Integer.parseInt(deptManagerId) : null);

        boolean success = false;
        String message = "";

        try {
            if (deptId == null || deptId.isEmpty()) {
                success = departmentDAO.insert(dept);
                message = success ? "Phòng ban '" + deptName.trim() + "' đã được thêm thành công!" 
                                  : "Không thể thêm phòng ban '" + deptName.trim() + "'. Vui lòng thử lại.";
            } else {
                dept.setDepartmentId(Integer.parseInt(deptId));
                success = departmentDAO.update(dept);
                message = success ? "Phòng ban '" + deptName.trim() + "' đã được cập nhật thành công!" 
                                  : "Không thể cập nhật phòng ban '" + deptName.trim() + "'. Vui lòng thử lại.";
            }
        } catch (NumberFormatException e) {
            message = "Lỗi: ID phòng ban không hợp lệ.";
            e.printStackTrace();
        } catch (Exception e) {
            message = "Lỗi: " + e.getMessage();
            e.printStackTrace();
        }

        jakarta.servlet.http.HttpSession session = request.getSession();
        session.setAttribute(success ? "successMessage" : "errorMessage", message);
        
        response.sendRedirect(request.getContextPath() + "/departments?action=departments");
    }

    private void deleteDepartment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String deptId = request.getParameter("deptId");
        String message = "";
        boolean success = false;

        try {
            int id = Integer.parseInt(deptId);
            
            Department dept = departmentDAO.getById(id);
            String deptName = dept != null ? dept.getDeptName() : "Phòng ban";
            
            int employeeCount = employeeDAO.getEmployeeCountByDepartment(id);
            if (employeeCount > 0) {
                message = "Không thể xóa phòng ban '" + deptName + "' vì nó có " + employeeCount + " nhân viên. Vui lòng chuyển nhân viên sang phòng ban khác trước.";
            } else {
                success = departmentDAO.delete(id);
                message = success ? "Phòng ban '" + deptName + "' đã được xóa thành công!" 
                                  : "Không thể xóa phòng ban '" + deptName + "'. Vui lòng thử lại.";
            }
        } catch (NumberFormatException e) {
            message = "Lỗi: ID phòng ban không hợp lệ.";
            e.printStackTrace();
        } catch (Exception e) {
            message = "Lỗi: " + e.getMessage();
            e.printStackTrace();
        }

        jakarta.servlet.http.HttpSession session = request.getSession();
        session.setAttribute(success ? "successMessage" : "errorMessage", message);
        
        response.sendRedirect(request.getContextPath() + "/departments?action=departments");
    }

    private void editDepartment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String deptId = request.getParameter("id");
        try {
            Department dept = departmentDAO.getById(Integer.parseInt(deptId));
            request.setAttribute("department", dept);
            request.getRequestDispatcher("Admin/DepartmentForm.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load department.");
            listDepartments(request, response);
        }
    }

    private void manageDepartmentPermissions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String deptId = request.getParameter("id");
        try {
            Department dept = departmentDAO.getById(Integer.parseInt(deptId));
            request.setAttribute("department", dept);
            request.getRequestDispatcher("Admin/DepartmentPermissions.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Failed to load department permissions.");
            listDepartments(request, response);
        }
    }

    private void savePermissions(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String deptId = request.getParameter("deptId");
        String[] permissions = request.getParameterValues("permissions");

        try {
            int id = Integer.parseInt(deptId);
            String permissionList = permissions != null ? String.join(", ", permissions) : "None";
            System.out.println("Permissions saved for department " + id + ": " + permissionList); 
            String message = "Permissions updated successfully!";
            request.setAttribute("successMessage", message);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid department ID format.");
            e.printStackTrace();
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Error saving permissions: " + e.getMessage());
            e.printStackTrace();
        }

        response.sendRedirect(request.getContextPath() + "/department?action=departments");
    }

    @Override
    public String getServletInfo() {
        return "Department Controller - Handles department management operations";
    }
}
