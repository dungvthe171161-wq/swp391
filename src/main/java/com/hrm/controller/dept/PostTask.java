package com.hrm.controller.dept;

import com.hrm.controller.EmailSender;
import com.hrm.dao.DAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.NotificationDAO;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Notification;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.DeptManagerScope;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebServlet(name = "postTask", urlPatterns = {"/postTask"})
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 12 * 1024 * 1024)
public class PostTask extends HttpServlet {

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final NotificationDAO notificationDAO = new NotificationDAO();
    private static final DateTimeFormatter MAIL_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!scope.hasDepartment()) {
            response.sendRedirect(request.getContextPath() + "/dept");
            return;
        }

        List<Employee> employeeList = DAO.getInstance().loadEmpFollowDepartment(scope.getDepartmentId());
        request.setAttribute("employeeList", employeeList);
        request.setAttribute("activePage", "tasks");
        request.setAttribute("pageTitle", "Tạo công việc");
        request.setAttribute("pageSubtitle", "Giao công việc cho nhân viên trong phòng ban.");
        if (scope.getEmployee() != null) {
            request.setAttribute("userName", scope.getEmployee().getFullName());
            request.setAttribute("userPosition", scope.getEmployee().getPosition());
        }
        request.getRequestDispatcher("/Views/DeptManager/postTask.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        DeptManagerScope scope = DeptManagerScope.from(request, employeeDAO);
        if (scope == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }
        if (!scope.hasDepartment()) {
            response.sendRedirect(request.getContextPath() + "/dept");
            return;
        }

        String title = clean(request.getParameter("title"));
        String description = clean(request.getParameter("description"));
        String startDate = clean(request.getParameter("startDate"));
        String dueDate = clean(request.getParameter("dueDate"));
        String priority = clean(request.getParameter("priority"));

        String error = validate(title, description, startDate, dueDate, priority);
        if (error != null) {
            response.sendRedirect(request.getContextPath() + "/postTask?error="
                    + URLEncoder.encode(error, StandardCharsets.UTF_8));
            return;
        }

        String attachmentPath = saveAttachment(request);
        int taskId = DAO.getInstance().createTask(title, description, scope.getApproverEmployeeId(),
                startDate, dueDate, priority, attachmentPath);
        if (taskId <= 0) {
            response.sendRedirect(request.getContextPath() + "/postTask?error="
                    + URLEncoder.encode("Không thể tạo công việc", StandardCharsets.UTF_8));
            return;
        }

        List<Employee> assignedEmployees = assignEmployees(request, taskId, scope.getDepartmentId());
        sendTaskNotifications(assignedEmployees, title, formatDateTime(dueDate));

        response.sendRedirect(request.getContextPath() + "/taskManager?mess="
                + URLEncoder.encode("Đã tạo công việc thành công", StandardCharsets.UTF_8));
    }

    private List<Employee> assignEmployees(HttpServletRequest request, int taskId, int departmentId) {
        List<Employee> assignedEmployees = new ArrayList<>();
        String[] assignToIds = request.getParameterValues("assignTo");
        if (assignToIds == null) {
            return assignedEmployees;
        }
        for (String empIdStr : assignToIds) {
            int empId = parseInt(empIdStr, -1);
            Employee assignee = DAO.getInstance().getEmp(empId);
            if (assignee != null && assignee.getDepartmentId() == departmentId
                    && DAO.getInstance().assignTaskToEmployee(taskId, empId)) {
                assignedEmployees.add(assignee);
            }
        }
        return assignedEmployees;
    }

    private String validate(String title, String description, String startDate, String dueDate, String priority) {
        if (title == null || title.isBlank() || title.length() > 50) {
            return "Tên công việc là bắt buộc và tối đa 50 ký tự";
        }
        if (description != null && description.length() > 1000) {
            return "Mô tả tối đa 1000 ký tự";
        }
        if (startDate == null || startDate.isBlank() || dueDate == null || dueDate.isBlank()) {
            return "Thời gian bắt đầu và deadline là bắt buộc";
        }
        LocalDateTime start = parseDateTime(startDate);
        LocalDateTime due = parseDateTime(dueDate);
        if (start == null || due == null) {
            return "Thời gian bắt đầu hoặc deadline không hợp lệ";
        }
        if (start.isBefore(LocalDateTime.now().minusMinutes(1))) {
            return "Thời gian bắt đầu không được nằm trong quá khứ";
        }
        if (start.isAfter(due)) {
            return "Thời gian bắt đầu phải trước deadline";
        }
        if (!List.of("Low", "Normal", "High").contains(priority)) {
            return "Mức độ ưu tiên không hợp lệ";
        }
        return null;
    }

    private LocalDateTime parseDateTime(String value) {
        try {
            String normalized = value == null ? "" : value.trim();
            if (normalized.length() == 10) {
                return LocalDate.parse(normalized).atStartOfDay();
            }
            return LocalDateTime.parse(normalized.replace(' ', 'T'));
        } catch (DateTimeParseException ex) {
            return null;
        }
    }

    private String formatDateTime(String value) {
        LocalDateTime parsed = parseDateTime(value);
        return parsed == null ? value : parsed.format(MAIL_DATE_TIME_FORMAT);
    }

    private String saveAttachment(HttpServletRequest request) throws IOException, ServletException {
        Part part = request.getPart("attachment");
        if (part == null || part.getSize() == 0 || part.getSubmittedFileName() == null) {
            return null;
        }
        String originalName = Path.of(part.getSubmittedFileName()).getFileName().toString();
        String safeName = originalName.replaceAll("[^a-zA-Z0-9._-]", "_");
        String fileName = UUID.randomUUID() + "_" + safeName;
        Path uploadDir = Path.of(getServletContext().getRealPath("/Upload/tasks"));
        Files.createDirectories(uploadDir);
        part.write(uploadDir.resolve(fileName).toString());
        return "Upload/tasks/" + fileName;
    }

    private void sendTaskNotifications(List<Employee> employees, String title, String dueDate) {
        for (Employee employee : employees) {
            SystemUser user = DAO.getInstance().findSystemUserByEmpID(employee.getEmployeeId());
            if (user != null) {
                Notification notification = new Notification();
                notification.setUserId(user.getUserId());
                notification.setTitle("Công việc mới: " + title);
                notification.setMessage("Bạn vừa được giao công việc mới. Deadline: " + dueDate);
                notification.setType("Task");
                notification.setRead(false);
                notificationDAO.create(notification);
            }
            sendTaskEmail(employee, title, dueDate);
        }
    }

    private void sendTaskEmail(Employee employee, String title, String dueDate) {
        if (employee.getEmail() == null || employee.getEmail().isBlank()) {
            return;
        }
        try {
            EmailSender.sendEmail(employee.getEmail(),
                    "BetterHR - Công việc mới",
                    "Bạn vừa được giao công việc: " + title + "\nDeadline: " + dueDate
                            + "\nVui lòng đăng nhập BetterHR để cập nhật trạng thái hoặc nộp kết quả.");
        } catch (Exception ignored) {
            // Email is best-effort; the in-app notification is still stored.
        }
    }

    private String clean(String value) {
        return value == null ? null : value.trim();
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
