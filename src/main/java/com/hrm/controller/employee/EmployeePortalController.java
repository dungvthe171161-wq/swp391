package com.hrm.controller.employee;

import com.hrm.controller.EmailSender;
import com.hrm.controller.EmailTemplates;
import com.hrm.dao.AttendanceDAO;
import com.hrm.dao.ContractDAO;
import com.hrm.dao.ContractDocumentDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.MailRequestDAO;
import com.hrm.dao.OfficeLocationDAO;
import com.hrm.dao.PayrollDAO;
import com.hrm.dao.TaskDAO;
import com.hrm.dao.WorkScheduleDAO;
import com.hrm.model.entity.Contract;
import com.hrm.model.entity.ContractDocument;
import com.hrm.model.entity.EmployeeWorkSchedule;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.MailRequest;
import com.hrm.model.entity.OfficeLocation;
import com.hrm.model.entity.SystemUser;
import com.hrm.model.entity.Task;
import com.hrm.util.GeoUtil;
import com.hrm.service.NotificationRecipientService;
import com.hrm.service.NotificationService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Base64;
import java.util.List;
import java.util.Map;

@WebServlet(name = "EmployeePortalController", urlPatterns = {"/employee", "/employee/*"})
public class EmployeePortalController extends HttpServlet {

    private static final List<String> PAID_LEAVE_TYPES = List.of("Annual", "Sick", "Maternity");
    private static final List<String> VALID_LEAVE_TYPES = List.of("Annual", "Sick", "Maternity", "Unpaid", "Other");
    private static final List<String> VALID_LEAVE_DETAILS = List.of("FullDay", "Morning", "Afternoon");

    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private final AttendanceDAO attendanceDAO = new AttendanceDAO();
    private final OfficeLocationDAO officeLocationDAO = new OfficeLocationDAO();
    private final WorkScheduleDAO workScheduleDAO = new WorkScheduleDAO();
    private final ContractDAO contractDAO = new ContractDAO();
    private final ContractDocumentDAO contractDocumentDAO = new ContractDocumentDAO();
    private final PayrollDAO payrollDAO = new PayrollDAO();
    private final MailRequestDAO mailRequestDAO = new MailRequestDAO();
    private final TaskDAO taskDAO = new TaskDAO();
    private final NotificationService notificationService = new NotificationService();
    private final NotificationRecipientService notificationRecipientService = new NotificationRecipientService();

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
            case "/schedule" -> showSchedule(employee.getEmployeeId(), request, response);
            case "/leaves" -> showLeaves(employee.getEmployeeId(), request, response);
            case "/payroll" -> showPayroll(employee.getEmployeeId(), request, response);
            case "/contract" -> showContract(employee.getEmployeeId(), request, response);
            case "/contract/document" -> downloadContractDocument(employee.getEmployeeId(), request, response);
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
        if ("/contract".equals(section)) {
            handleContractSign(employee.getEmployeeId(), request, response);
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
        OfficeLocation officeLocation = officeLocationDAO.getActiveOfficeLocations().stream()
                .findFirst().orElse(null);
        request.setAttribute("activePage", "attendance");
        request.setAttribute("todayAttendance", attendanceDAO.getTodayAttendance(employeeId));
        request.setAttribute("todayAttendanceStatus", attendanceDAO.getTodayStatus(employeeId));
        request.setAttribute("attendanceSummary", attendanceDAO.getMonthlySummary(
                employeeId, LocalDate.now().getYear(), LocalDate.now().getMonthValue()));
        request.setAttribute("recentAttendances", attendanceDAO.getRecentByEmployee(employeeId, 31));
        request.setAttribute("officeLocation", officeLocation);
        if (officeLocation != null) {
            request.setAttribute("officeLatitude", officeLocation.getLatitude());
            request.setAttribute("officeLongitude", officeLocation.getLongitude());
            request.setAttribute("allowedRadiusMeters", officeLocation.getRadiusMeters());
        }
        request.setAttribute("todaySchedule", workScheduleDAO.getByEmployeeAndDate(employeeId, LocalDate.now()));
        request.setAttribute("gpsRequired", true);
        if (officeLocation == null) {
            request.setAttribute("gpsWarning", "Chưa cấu hình địa điểm văn phòng hợp lệ để chấm công GPS.");
        }
        request.getRequestDispatcher("/Views/Employee/Attendance.jsp").forward(request, response);
    }

    private void showSchedule(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        YearMonth target = parseYearMonth(request);
        request.setAttribute("activePage", "schedule");
        request.setAttribute("month", target.getMonthValue());
        request.setAttribute("year", target.getYear());
        request.setAttribute("todaySchedule", workScheduleDAO.getByEmployeeAndDate(employeeId, LocalDate.now()));
        request.setAttribute("monthlySchedules", workScheduleDAO.getByEmployeeMonth(
                employeeId, target.getYear(), target.getMonthValue()));
        request.getRequestDispatcher("/Views/Employee/Schedule.jsp").forward(request, response);
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
        Contract contract = contractDAO.getContractByEmployeeId(employeeId);
        request.setAttribute("activePage", "contract");
        request.setAttribute("contract", contract);
        if (contract != null) {
            request.setAttribute("contractDocument", contractDocumentDAO.getLatestByContractId(contract.getContractId()));
        }
        request.getRequestDispatcher("/Views/Employee/Contract.jsp").forward(request, response);
    }

    private void downloadContractDocument(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int contractId = parseInt(request.getParameter("contractId"), -1);
        Contract contract = contractId > 0 ? contractDAO.getContractById(contractId) : null;
        if (contract == null || contract.getEmployeeId() != employeeId) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        ContractDocument document = contractDocumentDAO.getLatestByContractId(contractId);
        if (document == null || document.getFileData() == null || document.getFileData().length == 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        response.setContentType(document.getContentType() != null ? document.getContentType() : "application/octet-stream");
        response.setHeader("Content-Disposition", "inline; filename=\"" + safeDownloadFileName(document.getFileName()) + "\"");
        response.setContentLengthLong(document.getFileData().length);
        response.getOutputStream().write(document.getFileData());
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
        if (!"checkIn".equals(action) && !"checkOut".equals(action)) {
            redirectAttendanceError(request, response, "Thao tác chấm công không hợp lệ.");
            return;
        }
        String latitudeValue = request.getParameter("latitude");
        String longitudeValue = request.getParameter("longitude");
        String accuracyValue = request.getParameter("accuracy");
        if (latitudeValue == null || latitudeValue.isBlank()
                || longitudeValue == null || longitudeValue.isBlank()
                || accuracyValue == null || accuracyValue.isBlank()) {
            redirectAttendanceError(request, response, "Vui lòng cấp quyền vị trí để chấm công.");
            return;
        }

        Double latitude = parseDoubleObject(latitudeValue);
        Double longitude = parseDoubleObject(longitudeValue);
        Double accuracy = parseDoubleObject(accuracyValue);
        if (!GeoUtil.isValidLatitude(latitude) || !GeoUtil.isValidLongitude(longitude)) {
            redirectAttendanceError(request, response, "Tọa độ chấm công không hợp lệ.");
            return;
        }
        if (accuracy == null || !Double.isFinite(accuracy) || accuracy < 0) {
            redirectAttendanceError(request, response, "Độ chính xác GPS không hợp lệ.");
            return;
        }

        OfficeLocation office = officeLocationDAO.getNearestActiveLocation(latitude, longitude);
        if (office == null || office.getLatitude() == null || office.getLongitude() == null) {
            redirectAttendanceError(request, response, "Chưa có địa điểm văn phòng hợp lệ để chấm công GPS.");
            return;
        }

        double distanceMeters = GeoUtil.distanceMeters(latitude, longitude,
                office.getLatitude().doubleValue(), office.getLongitude().doubleValue());
        if (!GeoUtil.isWithinRadius(distanceMeters, office.getRadiusMeters())) {
            redirectAttendanceError(request, response, String.format(
                    "Bạn đang cách địa điểm làm việc %.0f m. Khoảng cách cho phép là %d m.",
                    distanceMeters, office.getRadiusMeters()));
            return;
        }
        boolean success = false;
        if ("checkIn".equals(action)) {
            success = attendanceDAO.checkInWithGps(employeeId, latitude, longitude, accuracy);
            request.getSession().setAttribute(success ? "employeeSuccess" : "employeeError",
                    success ? "Đã ghi nhận vào ca." : "Không thể vào ca bằng GPS. Vui lòng kiểm tra vị trí.");
        } else if ("checkOut".equals(action)) {
            success = attendanceDAO.checkOutWithGps(employeeId, latitude, longitude, accuracy);
            request.getSession().setAttribute(success ? "employeeSuccess" : "employeeError",
                    success ? "Đã ghi nhận ra ca." : "Không thể ra ca bằng GPS. Vui lòng kiểm tra vị trí.");
        }

        if (success) {
            Employee employee = (Employee) request.getAttribute("currentEmployee");
            if (employee != null && employee.getEmail() != null && !employee.getEmail().isBlank()) {
                String timeString = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss dd/MM/yyyy"));
                String emailContent = EmailTemplates.attendanceLogged(employee.getFullName(), action, timeString);
                String emailSubject = "WorkMate - Ghi nhận chấm công thành công";
                try {
                    EmailSender.sendHtmlEmail(employee.getEmail(), emailSubject, emailContent);
                } catch (Exception ignored) {
                }
            }
        }

        response.sendRedirect(request.getContextPath() + "/employee/attendance");
    }

    private void redirectAttendanceError(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException {
        request.getSession().setAttribute("employeeError", message);
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
        int requestId = mailRequestDAO.insertAndReturnId(leave);
        boolean success = requestId > 0;

        if (success) {
            Employee currentEmployee = (Employee) request.getAttribute("currentEmployee");
            SystemUser currentUser = (SystemUser) request.getAttribute("currentUser");
            notifyDeptManagersAboutLeave(requestId, currentEmployee, currentUser, startDate, endDate);
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
        Task task = taskId > 0 ? taskDAO.getAssignedTaskById(taskId, employeeId) : null;
        String feedback = clean(request.getParameter("feedback"));
        boolean success = task != null
                && taskDAO.updateAssignedTaskProgress(taskId, employeeId, status, feedback);
        if (success) {
            Employee currentEmployee = (Employee) request.getAttribute("currentEmployee");
            SystemUser currentUser = (SystemUser) request.getAttribute("currentUser");
            List<Integer> managerUserIds = currentEmployee != null
                    ? notificationRecipientService.deptManagersByDepartment(currentEmployee.getDepartmentId())
                    : List.of();
            notificationService.notifyTaskStatusUpdatedForManagers(
                    managerUserIds,
                    currentUser != null ? currentUser.getUserId() : 0,
                    taskId,
                    task != null ? task.getTitle() : "Cong viec",
                    status,
                    currentEmployee != null ? currentEmployee.getFullName() : "Nhan vien"
            );
        }
        request.getSession().setAttribute(success ? "employeeSuccess" : "employeeError",
                success ? "Da cap nhat trang thai cong viec." : "Khong the cap nhat cong viec nay.");
        response.sendRedirect(request.getContextPath() + "/employee/tasks");
    }

    private void handleContractSign(int employeeId, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession();
        int contractId = parseInt(request.getParameter("contractId"), -1);
        SystemUser currentUser = (SystemUser) request.getAttribute("currentUser");
        Contract contract = contractId > 0 ? contractDAO.getContractById(contractId) : null;

        if (contract == null || contract.getEmployeeId() != employeeId || !isEmployeeSignable(contract.getStatus())) {
            session.setAttribute("employeeError", "Hop dong khong hop le hoac khong con cho ky.");
            response.sendRedirect(request.getContextPath() + "/employee/contract");
            return;
        }
        if (!"1".equals(request.getParameter("agreeDocument"))) {
            session.setAttribute("employeeError", "Ban can xac nhan da doc va dong y voi hop dong.");
            response.sendRedirect(request.getContextPath() + "/employee/contract");
            return;
        }

        ContractDocument document = contractDocumentDAO.getLatestByContractId(contractId);
        if (document == null || !hasReadableDocument(document)) {
            session.setAttribute("employeeError", "Hop dong chua co van ban de ky.");
            response.sendRedirect(request.getContextPath() + "/employee/contract");
            return;
        }

        byte[] signatureBytes;
        try {
            signatureBytes = decodeSignaturePng(request.getParameter("signatureData"));
        } catch (IllegalArgumentException ex) {
            session.setAttribute("employeeError", ex.getMessage());
            response.sendRedirect(request.getContextPath() + "/employee/contract");
            return;
        }

        String relativePath = "/Upload/signatures/contract_" + contractId
                + "_user_" + (currentUser != null ? currentUser.getUserId() : 0) + "_" + System.currentTimeMillis() + ".png";
        String absoluteSignaturePath = getServletContext().getRealPath(relativePath);
        if (absoluteSignaturePath == null || absoluteSignaturePath.isBlank()) {
            session.setAttribute("employeeError", "Khong xac dinh duoc thu muc luu chu ky tren server.");
            response.sendRedirect(request.getContextPath() + "/employee/contract");
            return;
        }
        Path signatureFile = Path.of(absoluteSignaturePath);
        Files.createDirectories(signatureFile.getParent());
        Files.write(signatureFile, signatureBytes);

        String signatureHash = sha256(signatureBytes);
        String contentHash = sha256(buildContractHashSource(contract, document).getBytes(StandardCharsets.UTF_8));
        boolean success = contractDAO.signContractByEmployee(
                contractId,
                employeeId,
                currentUser != null ? currentUser.getUserId() : 0,
                relativePath,
                signatureHash,
                clientIp(request),
                request.getHeader("User-Agent"),
                contentHash
        );

        session.setAttribute(success ? "employeeSuccess" : "employeeError",
                success ? "Da ky hop dong thanh cong. Hop dong da chuyen sang trang thai dang hieu luc."
                        : "Khong the ky hop dong nay. Vui long tai lai trang va thu lai.");
        response.sendRedirect(request.getContextPath() + "/employee/contract");
    }

    private boolean hasReadableDocument(ContractDocument document) {
        return document != null
                && ((document.getContent() != null && !document.getContent().trim().isEmpty())
                || (document.getFileData() != null && document.getFileData().length > 0));
    }

    private boolean isEmployeeSignable(String status) {
        return "Pending_Signature".equals(status) || "Approved".equals(status);
    }

    private byte[] decodeSignaturePng(String signatureData) {
        String prefix = "data:image/png;base64,";
        if (signatureData == null || !signatureData.startsWith(prefix)) {
            throw new IllegalArgumentException("Vui long ky ten trong khung chu ky.");
        }
        try {
            byte[] decoded = Base64.getDecoder().decode(signatureData.substring(prefix.length()));
            if (decoded.length < 300) {
                throw new IllegalArgumentException("Chu ky qua ngan. Vui long ky ro hon.");
            }
            if (decoded.length > 500_000) {
                throw new IllegalArgumentException("Anh chu ky qua lon. Vui long xoa va ky lai.");
            }
            return decoded;
        } catch (IllegalArgumentException ex) {
            if (ex.getMessage() != null && ex.getMessage().startsWith("Chu ky")) {
                throw ex;
            }
            if (ex.getMessage() != null && ex.getMessage().startsWith("Anh chu ky")) {
                throw ex;
            }
            throw new IllegalArgumentException("Du lieu chu ky khong hop le.");
        }
    }

    private String buildContractHashSource(Contract contract, ContractDocument document) {
        return contract.getContractId() + "|"
                + contract.getEmployeeId() + "|"
                + contract.getStartDate() + "|"
                + contract.getEndDate() + "|"
                + contract.getBaseSalary() + "|"
                + contract.getAllowance() + "|"
                + contract.getContractType() + "|"
                + contract.getNote() + "|"
                + document.getTitle() + "|"
                + document.getContent() + "|"
                + document.getFileName() + "|"
                + (document.getFileData() == null ? "" : sha256(document.getFileData()));
    }

    private String sha256(byte[] bytes) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(bytes);
            StringBuilder hex = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                hex.append(String.format("%02x", value));
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 is not available.", ex);
        }
    }

    private String clientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String safeDownloadFileName(String fileName) {
        String cleaned = fileName == null || fileName.isBlank() ? "contract-document" : fileName.trim();
        return cleaned.replace("\\", "_").replace("/", "_").replace("\"", "").replace("\r", "").replace("\n", "");
    }

    private void notifyDeptManagersAboutLeave(int requestId, Employee employee, SystemUser currentUser,
                                              LocalDate startDate, LocalDate endDate) {
        if (employee == null || employee.getDepartmentId() <= 0) {
            return;
        }
        List<Integer> managerUserIds = notificationRecipientService.deptManagersByDepartment(employee.getDepartmentId());
        notificationService.notifyNewLeaveRequestForDeptManagers(
                managerUserIds,
                currentUser != null ? currentUser.getUserId() : 0,
                requestId,
                employee.getFullName(),
                String.valueOf(startDate),
                String.valueOf(endDate)
        );
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
                    : currentUser != null ? currentUser.getUsername() : "Nhan vien WorkMate";
            String subject = "WorkMate - Thong tin ban giao cong viec khi nghi phep";
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

                WorkMate
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

    private YearMonth parseYearMonth(HttpServletRequest request) {
        try {
            int month = Integer.parseInt(request.getParameter("month"));
            int year = Integer.parseInt(request.getParameter("year"));
            return YearMonth.of(year, month);
        } catch (RuntimeException ex) {
            return YearMonth.now();
        }
    }

    private Double parseDoubleObject(String value) {
        try {
            return value == null || value.isBlank() ? null : Double.parseDouble(value);
        } catch (NumberFormatException ex) {
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
