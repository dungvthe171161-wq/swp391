//package com.hrm.controller;
//
//import com.hrm.dao.*;
//import com.hrm.model.entity.*;
//import java.io.IOException;
//import java.util.*;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.annotation.WebServlet;
//import jakarta.servlet.http.HttpServlet;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//
//@WebServlet(name = "DepartmentController", urlPatterns = {"/departments"})
//public class DepartmentController extends HttpServlet {
//
//    private DepartmentDAO departmentDAO = new DepartmentDAO();
//    private EmployeeDAO employeeDAO = new EmployeeDAO();
//
//    @Override
//    protected void doGet(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String action = request.getParameter("action");
//        if (action == null) {
//            action = "list";
//        }
//
//        switch (action) {
//            case "departments":
//                listDepartments(request, response);
//                break;
//            case "edit":
//                editDepartment(request, response);
//                break;
//            case "permissions":
//                manageDepartmentPermissions(request, response);
//                break;
//            default:
//                listDepartments(request, response);
//        }
//    }
//
//    @Override
//    protected void doPost(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String action = request.getParameter("action");
//        if (action == null) {
//            action = "save";
//        }
//
//        switch (action) {
//            case "department-save":
//                saveDepartment(request, response);
//                break;
//            case "department-delete":
//                deleteDepartment(request, response);
//                break;
//            case "department-permissions-save":
//                savePermissions(request, response);
//                break;
//            default:
//                listDepartments(request, response);
//        }
//    }
//
//private void listDepartments(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        request.setAttribute("activePage", "departments");
//
//        jakarta.servlet.http.HttpSession session = request.getSession(false);
//        if (session != null) {
//            String successMessage = (String) session.getAttribute("successMessage");
//            String errorMessage = (String) session.getAttribute("errorMessage");
//            
//            if (successMessage != null) {
//                request.setAttribute("successMessage", successMessage);
//                session.removeAttribute("successMessage");
//            }
//            if (errorMessage != null) {
//                request.setAttribute("errorMessage", errorMessage);
//                session.removeAttribute("errorMessage");
//            }
//        }
//
//        String searchKeyword = request.getParameter("search");
//        List<Department> departmentList;
//
//        int page = 1;
//        int pageSize = 10;
//        try {
//            String pageStr = request.getParameter("page");
//            String sizeStr = request.getParameter("pageSize");
//            if (pageStr != null) page = Math.max(1, Integer.parseInt(pageStr));
//            if (sizeStr != null) pageSize = Math.max(1, Integer.parseInt(sizeStr));
//        } catch (NumberFormatException ignore) {}
//
//        try {
//            if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
//                int total = departmentDAO.searchDepartmentsCount(searchKeyword);
//                int totalPages = (int) Math.ceil(total / (double) pageSize);
//                if (totalPages == 0) totalPages = 1;
//                if (page > totalPages) page = totalPages;
//                int offset = (page - 1) * pageSize;
//                departmentList = departmentDAO.searchDepartmentsPaged(searchKeyword, offset, pageSize);
//                request.setAttribute("total", total);
//                request.setAttribute("totalPages", totalPages);
//            } else {
//                int total = departmentDAO.getTotalDepartmentsCount();
//                int totalPages = (int) Math.ceil(total / (double) pageSize);
//                if (page < 1) page = 1;
//                if (totalPages == 0) totalPages = 1;
//                if (page > totalPages) page = totalPages;
//                int offset = (page - 1) * pageSize;
//                departmentList = departmentDAO.getPaged(offset, pageSize);
//                request.setAttribute("total", total);
//                request.setAttribute("totalPages", totalPages);
//            }
//        } catch (Exception e) {
//            departmentList = departmentDAO.getAll();
//            request.setAttribute("errorMessage", "Search failed, showing all departments.");
//            e.printStackTrace();
//        }
//
//        for (Department dept : departmentList) {
//            int count = employeeDAO.getEmployeeCountByDepartment(dept.getDepartmentId());
//            dept.setEmployeeCount(count);
//        }
//
//        List<Employee> managers = employeeDAO.getManagerList();
//
//        request.setAttribute("departmentList", departmentList);
//        request.setAttribute("managers", managers);
//        request.setAttribute("searchKeyword", searchKeyword);
//        request.setAttribute("page", page);
//        request.setAttribute("pageSize", pageSize);
//
//        request.getRequestDispatcher("Admin/Departments.jsp").forward(request, response);
//    }
//
//
//    private void saveDepartment(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String deptId = request.getParameter("deptId");
//        String deptName = request.getParameter("deptName");
//        String deptManagerId = request.getParameter("deptManagerId");
//
//        if (deptName == null || deptName.trim().isEmpty()) {
//            jakarta.servlet.http.HttpSession session = request.getSession();
//            session.setAttribute("errorMessage", "Tên phòng ban là bắt buộc!");
//            response.sendRedirect(request.getContextPath() + "/department?action=departments");
//            return;
//        }
//
//        Department dept = new Department();
//        dept.setDeptName(deptName.trim());
//        dept.setDeptManagerId(deptManagerId != null && !deptManagerId.isEmpty() ? Integer.parseInt(deptManagerId) : null);
//
//        boolean success = false;
//        String message = "";
//
//        try {
//            if (deptId == null || deptId.isEmpty()) {
//                success = departmentDAO.insert(dept);
//                message = success ? "Phòng ban '" + deptName.trim() + "' đã được thêm thành công!" 
//                                  : "Không thể thêm phòng ban '" + deptName.trim() + "'. Vui lòng thử lại.";
//            } else {
//                dept.setDepartmentId(Integer.parseInt(deptId));
//                success = departmentDAO.update(dept);
//                message = success ? "Phòng ban '" + deptName.trim() + "' đã được cập nhật thành công!" 
//                                  : "Không thể cập nhật phòng ban '" + deptName.trim() + "'. Vui lòng thử lại.";
//            }
//        } catch (NumberFormatException e) {
//            message = "Lỗi: ID phòng ban không hợp lệ.";
//            e.printStackTrace();
//        } catch (Exception e) {
//            message = "Lỗi: " + e.getMessage();
//            e.printStackTrace();
//        }
//
//        jakarta.servlet.http.HttpSession session = request.getSession();
//        session.setAttribute(success ? "successMessage" : "errorMessage", message);
//        
//        response.sendRedirect(request.getContextPath() + "/department?action=departments");
//    }
//
//    private void deleteDepartment(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String deptId = request.getParameter("deptId");
//        String message = "";
//        boolean success = false;
//
//        try {
//            int id = Integer.parseInt(deptId);
//            
//            Department dept = departmentDAO.getById(id);
//            String deptName = dept != null ? dept.getDeptName() : "Phòng ban";
//            
//            int employeeCount = employeeDAO.getEmployeeCountByDepartment(id);
//            if (employeeCount > 0) {
//                message = "Không thể xóa phòng ban '" + deptName + "' vì nó có " + employeeCount + " nhân viên. Vui lòng chuyển nhân viên sang phòng ban khác trước.";
//            } else {
//                success = departmentDAO.delete(id);
//                message = success ? "Phòng ban '" + deptName + "' đã được xóa thành công!" 
//                                  : "Không thể xóa phòng ban '" + deptName + "'. Vui lòng thử lại.";
//            }
//        } catch (NumberFormatException e) {
//            message = "Lỗi: ID phòng ban không hợp lệ.";
//            e.printStackTrace();
//        } catch (Exception e) {
//            message = "Lỗi: " + e.getMessage();
//            e.printStackTrace();
//        }
//
//        jakarta.servlet.http.HttpSession session = request.getSession();
//        session.setAttribute(success ? "successMessage" : "errorMessage", message);
//        
//        response.sendRedirect(request.getContextPath() + "/department?action=departments");
//    }
//
//    private void editDepartment(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String deptId = request.getParameter("id");
//        try {
//            Department dept = departmentDAO.getById(Integer.parseInt(deptId));
//            request.setAttribute("department", dept);
//            request.getRequestDispatcher("Admin/DepartmentForm.jsp").forward(request, response);
//        } catch (Exception e) {
//            request.setAttribute("errorMessage", "Failed to load department.");
//            listDepartments(request, response);
//        }
//    }
//
//    private void manageDepartmentPermissions(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String deptId = request.getParameter("id");
//        try {
//            Department dept = departmentDAO.getById(Integer.parseInt(deptId));
//            request.setAttribute("department", dept);
//            request.getRequestDispatcher("Admin/DepartmentPermissions.jsp").forward(request, response);
//        } catch (Exception e) {
//            request.setAttribute("errorMessage", "Failed to load department permissions.");
//            listDepartments(request, response);
//        }
//    }
//
//    private void savePermissions(HttpServletRequest request, HttpServletResponse response)
//            throws ServletException, IOException {
//        String deptId = request.getParameter("deptId");
//        String[] permissions = request.getParameterValues("permissions");
//
//        try {
//            int id = Integer.parseInt(deptId);
//            String permissionList = permissions != null ? String.join(", ", permissions) : "None";
//            System.out.println("Permissions saved for department " + id + ": " + permissionList); 
//            String message = "Permissions updated successfully!";
//            request.setAttribute("successMessage", message);
//        } catch (NumberFormatException e) {
//            request.setAttribute("errorMessage", "Invalid department ID format.");
//            e.printStackTrace();
//        } catch (Exception e) {
//            request.setAttribute("errorMessage", "Error saving permissions: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        response.sendRedirect(request.getContextPath() + "/department?action=departments");
//    }
//
//    @Override
//    public String getServletInfo() {
//        return "Department Controller - Handles department management operations";
//    }
//}
