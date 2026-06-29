package com.hrm.controller.employee;

import com.hrm.controller.EmailSender;
import com.hrm.dao.AttendanceDAO;
import com.hrm.dao.ContractDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.MailRequestDAO;
import com.hrm.dao.PayrollDAO;
import com.hrm.dao.TaskDAO;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.MailRequest;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@WebServlet(name = "EmployeePortalController", urlPatterns = {"/employee", "/employee/*"})
public class EmployeePortalController extends HttpServlet {

    private static final List<String> PAID_LEAVE_TYPES = List.of("Annual", "Sick", "Maternity");
    private static final List<String> VALID_LEAVE_TYPES = List.of("Annual", "Sick", "Maternity", "Unpaid", "Other");
    private static final List<String> VALID_LEAVE_DETAILS = List.of("FullDay", "Morning", "Afternoon");

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final MailRequestDAO mailRequestDAO = new MailRequestDAO();
    private final TaskDAO taskDAO = new TaskDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Employee employee = prepareEmployeeContext(request, response);
        if (employee == null) {
            return;
        }

        String section = normalizePath(request.getPathInfo());
        switch (section) {
            case "/profile" -> showProfile(request, response);
            case "/attendance" -> showAttendance(employee.getEmployeeId(), request, response);
            case "/leaves" -> showLeaves(employee.getEmployeeId(), request, response);
            case "/payroll" -> showPayroll(employee.getEmployeeId(), request, response);
            case "/contract" -> showContract(employee.getEmployeeId(), request, response);
            case "/tasks" -> showTasks(employee.getEmployeeId(), request, response);
            default -> showDashboard(employee.getEmployeeId(), request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Employee employee = prepareEmployeeContext(request, response);
        if (employee == null) {
            return;
        }

        String section = normalizePath(request.getPathInfo());
        if ("/attendance".equals(section)) {
            handleAttendance(employee.getEmployeeId(), request, response);
            return;
        }
        if ("/leaves".equals(section)) {
            handleLeaveCreate(employee.getEmployeeId(), request, response);
            return;
        }
        if ("/tasks".equals(section)) {
            handleTaskUpdate(employee.getEmployeeId(), request, response);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/employee");
    }

    private void showDashboard(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LocalDate today = LocalDate.now();
        List<Map<String, Object>> payrolls = payrollDAO.getAll(employeeId, null);
        int usedLeaveDays = mailRequestDAO.getApprovedLeaveDays(employeeId, today.getYear());
        int remainingLeaveDays = Math.max(0, 12 - usedLeaveDays);

        request.setAttribute("activePage", "dashboard");
        request.setAttribute("todayAttendance", attendanceDAO.getTodayAttendance(employeeId));
        request.setAttribute("todayAttendanceStatus", attendanceDAO.getTodayStatus(employeeId));
        request.setAttribute("attendanceSummary", attendanceDAO.getMonthlySummary(employeeId, today.getYear(), today.getMonthValue()));
        request.setAttribute("recentAttendances", attendanceDAO.getRecentByEmployee(employeeId, 5));
        request.setAttribute("latestContract", contractDAO.getContractByEmployeeId(employeeId));
        request.setAttribute("payrolls", payrolls);
        request.setAttribute("latestPayroll", payrollDAO.getLatestByEmployee(employeeId));
        request.setAttribute("tasks", taskDAO.getTasksByEmployee(employeeId));
        request.setAttribute("openTaskCount", taskDAO.countOpenTasksByEmployee(employeeId));
        request.setAttribute("leaveRemaining", remainingLeaveDays);
        request.setAttribute("pendingLeaveCount", mailRequestDAO.countPendingLeavesByEmployee(employeeId));
        request.getRequestDispatcher("/Views/Employee/EmployeeHome.jsp").forward(request, response);
    }

    private void showProfile(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "profile");
        request.getRequestDispatcher("/Views/Employee/EmployeeProfile.jsp").forward(request, response);
    }

    private void showAttendance(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "attendance");
        request.setAttribute("todayAttendance", attendanceDAO.getTodayAttendance(employeeId));
        request.setAttribute("todayAttendanceStatus", attendanceDAO.getTodayStatus(employeeId));
        request.setAttribute("attendanceSummary", attendanceDAO.getMonthlySummary(
                employeeId, LocalDate.now().getYear(), LocalDate.now().getMonthValue()));
        request.setAttribute("recentAttendances", attendanceDAO.getRecentByEmployee(employeeId, 31));
        request.getRequestDispatcher("/Views/Employee/Attendance.jsp").forward(request, response);
    }

    private void showLeaves(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int totalSessions = mailRequestDAO.getDefaultPaidLeaveSessions();
        int bookedSessions = mailRequestDAO.getBookedPaidLeaveSessions(employeeId, LocalDate.now().getYear());
        request.setAttribute("activePage", "leaves");
        request.setAttribute("leaveRequests", mailRequestDAO.getLeavesByEmployee(employeeId));
        request.setAttribute("leaveTotalSessions", totalSessions);
        request.setAttribute("leaveRemainingSessions", Math.max(0, totalSessions - bookedSessions));
        request.getRequestDispatcher("/Views/Employee/Leaves.jsp").forward(request, response);
    }

    private void showPayroll(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "payroll");
        String payrollIdParam = request.getParameter("payrollId");
        if (payrollIdParam != null && !payrollIdParam.isBlank()) {
            int payrollId = parseInt(payrollIdParam, -1);
            Map<String, Object> details = payrollDAO.getDetailsById(payrollId);
            if (details != null && ((Integer) details.get("employeeId")) == employeeId) {
                request.setAttribute("payrollDetails", details);
            } else {
                request.setAttribute("employeeError", "Khong tim thay phieu luong cua ban.");
            }
        }
        request.setAttribute("payrolls", payrollDAO.getAll(employeeId, null));
        request.getRequestDispatcher("/Views/Employee/Payroll.jsp").forward(request, response);
    }

    private void showContract(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "contract");
        request.setAttribute("contract", contractDAO.getContractByEmployeeId(employeeId));
        request.getRequestDispatcher("/Views/Employee/Contract.jsp").forward(request, response);
    }

    private void showTasks(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("activePage", "tasks");
        request.setAttribute("tasks", taskDAO.getTasksByEmployee(employeeId));
        request.getRequestDispatcher("/Views/Employee/Tasks.jsp").forward(request, response);
    }

    private void handleAttendance(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String action = request.getParameter("action");
        boolean success = false;
        if ("checkIn".equals(action)) {
            success = attendanceDAO.checkIn(employeeId);
            request.getSession().setAttribute(success ? "employeeSuccess" : "employeeError",
                    success ? "Da ghi nhan vao ca." : "Hom nay ban da vao ca roi.");
        } else if ("checkOut".equals(action)) {
            success = attendanceDAO.checkOut(employeeId);
            request.getSession().setAttribute(success ? "employeeSuccess" : "employeeError",
                    success ? "Da ghi nhan ra ca." : "Ban can vao ca truoc hoac da ra ca roi.");
        }
        response.sendRedirect(request.getContextPath() + "/employee/attendance");
    }

    private void handleLeaveCreate(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String leaveType = clean(request.getParameter("leaveType"));
        String leaveDetail = clean(request.getParameter("leaveDetail"));
        LocalDate startDate = parseDate(request.getParameter("startDate"));
        LocalDate endDate = parseDate(request.getParameter("endDate"));
        String handoverEmail = clean(request.getParameter("handoverTo"));
        String handoverWork = clean(request.getParameter("handoverWork"));
        String reason = clean(request.getParameter("reason"));
        String validationError = validateLeaveRequest(employeeId, leaveType, leaveDetail, startDate, endDate,
                handoverEmail, handoverWork, reason);
        if (validationError != null) {
            request.getSession().setAttribute("employeeError", validationError);
            response.sendRedirect(request.getContextPath() + "/employee/leaves");
            return;
        }

        MailRequest leave = new MailRequest();
        leave.setEmployeeId(employeeId);
        leave.setRequestType("Leave");
        leave.setLeaveType(leaveType);
        leave.setStartDate(startDate);
        leave.setEndDate(endDate);
        leave.setReason(buildLeaveReason(request));
        boolean success = mailRequestDAO.insert(leave);

        if (success) {
            String mailWarning = sendHandoverEmail(request, handoverEmail, startDate, endDate, leaveDetail,
                    handoverWork, reason);
            request.getSession().setAttribute(mailWarning == null ? "employeeSuccess" : "employeeError",
                    mailWarning == null
                            ? "Da gui don nghi phep va email ban giao."
                            : "Da luu don nghi phep, nhung chua gui duoc email ban giao: " + mailWarning);
        } else {
            request.getSession().setAttribute("employeeError", "Khong the gui don. Vui long thu lai.");
        }
        response.sendRedirect(request.getContextPath() + "/employee/leaves");
    }

    private void handleTaskUpdate(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int taskId = parseInt(request.getParameter("taskId"), -1);
        String status = request.getParameter("status");
        boolean success = taskId > 0 && taskDAO.updateAssignedTaskStatus(taskId, employeeId, status);
        request.getSession().setAttribute(success ? "employeeSuccess" : "employeeError",
                success ? "Da cap nhat trang thai cong viec." : "Khong the cap nhat cong viec nay.");
        response.sendRedirect(request.getContextPath() + "/employee/tasks");
    }

    private String buildLeaveReason(HttpServletRequest request) {
        String detail = toVietnameseLeaveDetail(request.getParameter("leaveDetail"));
        String handoverTo = clean(request.getParameter("handoverTo"));
        String handoverWork = clean(request.getParameter("handoverWork"));
        String reason = clean(request.getParameter("reason"));

        StringBuilder builder = new StringBuilder();
        appendReasonLine(builder, "Chi tiet nghi", detail);
        appendReasonLine(builder, "Ban giao cho", handoverTo);
        appendReasonLine(builder, "Noi dung ban giao", handoverWork);
        appendReasonLine(builder, "Ly do", reason);
        return builder.toString();
    }

    private String validateLeaveRequest(int employeeId, String leaveType, String leaveDetail,
            LocalDate startDate, LocalDate endDate, String handoverEmail,
            String handoverWork, String reason) {
        if (!VALID_LEAVE_TYPES.contains(leaveType)) {
            return "Loai nghi khong hop le.";
        }
        if (!VALID_LEAVE_DETAILS.contains(leaveDetail)) {
            return "Chi tiet nghi khong hop le.";
        }
        if (startDate == null || endDate == null) {
            return "Vui long nhap day du tu ngay va den ngay.";
        }
        LocalDate today = LocalDate.now();
        if (startDate.isBefore(today) || endDate.isBefore(today)) {
            return "Ngay nghi khong duoc nam trong qua khu.";
        }
        if (endDate.isBefore(startDate)) {
            return "Den ngay khong duoc nho hon tu ngay.";
        }
        if (!isValidEmail(handoverEmail)) {
            return "Vui long nhap email nguoi nhan ban giao hop le.";
        }
        if (handoverWork.isBlank()) {
            return "Vui long nhap noi dung cong viec can ban giao.";
        }
        if (reason.isBlank()) {
            return "Vui long nhap ly do nghi.";
        }
        if (mailRequestDAO.hasOverlappingActiveLeave(employeeId, startDate, endDate)) {
            return "Khoang ngay nghi da co don cho duyet hoac da duyet.";
        }
        if (PAID_LEAVE_TYPES.contains(leaveType)) {
            int totalSessions = mailRequestDAO.getDefaultPaidLeaveSessions();
            int bookedSessions = mailRequestDAO.getBookedPaidLeaveSessions(employeeId, startDate.getYear());
            int requestedSessions = calculateRequestedLeaveSessions(startDate, endDate, leaveDetail);
            int remainingSessions = totalSessions - bookedSessions;
            if (requestedSessions > remainingSessions) {
                return "So buoi nghi vuot qua so phep con lai. Con lai: "
                        + Math.max(0, remainingSessions) + " buoi, dang xin: " + requestedSessions + " buoi.";
            }
        }
        return null;
    }

    private int calculateRequestedLeaveSessions(LocalDate startDate, LocalDate endDate, String leaveDetail) {
        int days = (int) java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        int sessionsPerDay = "Morning".equals(leaveDetail) || "Afternoon".equals(leaveDetail) ? 1 : 2;
        return days * sessionsPerDay;
    }

    private String sendHandoverEmail(HttpServletRequest request, String handoverEmail, LocalDate startDate,
            LocalDate endDate, String leaveDetail, String handoverWork, String reason) {
        try {
            SystemUser currentUser = (SystemUser) request.getAttribute("currentUser");
            Employee currentEmployee = (Employee) request.getAttribute("currentEmployee");
            String employeeName = currentEmployee != null ? currentEmployee.getFullName()
                    : currentUser != null ? currentUser.getUsername() : "Nhan vien BetterHR";
            String subject = "BetterHR - Thong tin ban giao cong viec khi nghi phep";
            String content = """
                Xin chao,

                %s da tao don nghi phep va ban giao cong viec cho ban.

                Thoi gian nghi: %s den %s
                Chi tiet nghi: %s

                Noi dung ban giao:
                %s

                Ly do nghi:
                %s

                Vui long kiem tra va phoi hop xu ly cong viec duoc ban giao.

                BetterHR
                """.formatted(employeeName, startDate, endDate, toVietnameseLeaveDetail(leaveDetail),
                    handoverWork, reason);
            EmailSender.sendEmail(handoverEmail, subject, content);
            return null;
        } catch (Exception ex) {
            return ex.getMessage() == null ? "Loi gui email." : ex.getMessage();
        }
    }

    private boolean isValidEmail(String value) {
        return value != null && value.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private void appendReasonLine(StringBuilder builder, String label, String value) {
        if (value == null || value.isBlank()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(System.lineSeparator());
        }
        builder.append(label).append(": ").append(value);
    }

    private String toVietnameseLeaveDetail(String value) {
        return switch (value == null ? "" : value) {
            case "Morning" -> "Nghi buoi sang";
            case "Afternoon" -> "Nghi buoi chieu";
            default -> "Nghi ca ngay";
        };
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private Employee prepareEmployeeContext(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        SystemUser currentUser = session != null ? (SystemUser) session.getAttribute("systemUser") : null;
        if (currentUser == null || currentUser.getEmployeeId() == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return null;
        }

        Employee employee = employeeDAO.getById(currentUser.getEmployeeId());
        if (employee == null) {
            response.sendRedirect(request.getContextPath() + "/homepage");
            return null;
        }

        currentUser.setEmployee(employee);
        request.setAttribute("currentUser", currentUser);
        request.setAttribute("currentEmployee", employee);
        pullFlashMessages(request);
        return employee;
    }

    private void pullFlashMessages(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }
        Object success = session.getAttribute("employeeSuccess");
        Object error = session.getAttribute("employeeError");
        if (success != null) {
            request.setAttribute("employeeSuccess", success);
            session.removeAttribute("employeeSuccess");
        }
        if (error != null) {
            request.setAttribute("employeeError", error);
            session.removeAttribute("employeeError");
        }
    }

    private LocalDate parseDate(String value) {
        try {
            return value == null || value.isBlank() ? null : LocalDate.parse(value);
        } catch (RuntimeException ex) {
            return null;
        }
    }

    private int parseInt(String value, int fallback) {
        try {
            return value == null || value.isBlank() ? fallback : Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private String normalizePath(String pathInfo) {
        return pathInfo == null || pathInfo.isBlank() ? "/" : pathInfo;
    }
}
