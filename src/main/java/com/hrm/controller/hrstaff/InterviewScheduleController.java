package com.hrm.controller.hrstaff;

import com.hrm.controller.EmailSender;
import com.hrm.dao.ApplicationDAO;
import com.hrm.dao.EmployeeDAO;
import com.hrm.dao.InterviewDAO;
import com.hrm.dao.OfferDAO;
import com.hrm.model.entity.CandidateProfile;
import com.hrm.model.entity.Guest;
import com.hrm.model.entity.Interview;
import com.hrm.model.entity.Notification;
import com.hrm.model.entity.Offer;
import com.hrm.model.entity.SystemUser;
import com.hrm.service.NotificationService;
import com.hrm.service.RecruitmentWorkflowRules;
import com.hrm.util.PermissionUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "InterviewScheduleController", urlPatterns = {"/hrstaff/interviews/schedule"})
public class InterviewScheduleController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(InterviewScheduleController.class.getName());
    private static final String REQUIRED_PERMISSION = "VIEW_RECRUITMENT";
    private static final String ACCESS_DENIED_MESSAGE = "Bạn không có quyền quản lý lịch phỏng vấn.";

    private final transient ApplicationDAO applicationDAO = new ApplicationDAO();
    private final transient InterviewDAO interviewDAO = new InterviewDAO();
    private final transient OfferDAO offerDAO = new OfferDAO();
    private final transient EmployeeDAO employeeDAO = new EmployeeDAO();
    private final transient NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        int applicationId = parsePositiveInt(request.getParameter("applicationId"));
        if (applicationId <= 0) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=invalid_application");
            return;
        }
        ApplicationDAO.CandidateApplicationView applicationView =
                applicationDAO.findCandidateApplicationById(applicationId);
        if (applicationView == null) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=invalid_application");
            return;
        }
        for (Interview interview : interviewDAO.findByApplicationId(applicationId)) {
            if (!isCompletedPassed(interview)) {
                continue;
            }
            if (RecruitmentWorkflowRules.canPrepareOffer(applicationView.getApplication())
                    || RecruitmentWorkflowRules.isOfferSent(applicationView.getApplication())) {
                response.sendRedirect(request.getContextPath()
                        + "/hrstaff/offers/manage?applicationId=" + applicationId);
                return;
            }
            if ("Interview".equals(applicationView.getApplication().getStatus())
                    && "Interview".equals(applicationView.getApplication().getCurrentStep())) {
                if (ensureOfferDraft(applicationView, interview)
                        && applicationDAO.updateStatus(applicationId, "Interview", "Offer")) {
                    response.sendRedirect(request.getContextPath()
                            + "/hrstaff/offers/manage?applicationId=" + applicationId + "&recovered=1");
                    return;
                }
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Không thể đồng bộ offer cho ứng viên đã pass.");
                return;
            }
        }
        Interview selectedInterview = null;
        int interviewId = parsePositiveInt(request.getParameter("interviewId"));
        if (interviewId > 0) {
            selectedInterview = interviewDAO.findById(interviewId);
            if (selectedInterview == null || selectedInterview.getApplicationId() != applicationId) {
                response.sendRedirect(request.getContextPath() + "/hrstaff/interviews/schedule?applicationId=" + applicationId);
                return;
            }
        }
        forwardForm(request, response, applicationView, selectedInterview, null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        if (!ensureAccess(request, response)) {
            return;
        }

        String action = firstNonBlank(request.getParameter("action"), "saveSchedule");
        if ("updateResult".equals(action)) {
            handleResultUpdate(request, response, currentUser);
            return;
        }
        if ("cancelInterview".equals(action)) {
            handleCancel(request, response, currentUser);
            return;
        }
        handleScheduleSave(request, response, currentUser);
    }

    private void handleScheduleSave(HttpServletRequest request, HttpServletResponse response, SystemUser currentUser)
            throws ServletException, IOException {
        int applicationId = parsePositiveInt(request.getParameter("applicationId"));
        ApplicationDAO.CandidateApplicationView applicationView = loadApplication(applicationId);
        if (applicationView == null) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=invalid_application");
            return;
        }
        if (!RecruitmentWorkflowRules.canScheduleInterview(applicationView.getApplication())) {
            response.sendError(HttpServletResponse.SC_CONFLICT,
                    "Hồ sơ không còn ở trạng thái cho phép đặt hoặc sửa lịch phỏng vấn.");
            return;
        }

        int interviewId = parsePositiveInt(request.getParameter("interviewId"));
        Interview existingInterview = interviewId > 0 ? interviewDAO.findById(interviewId) : null;
        if (interviewId > 0 && (existingInterview == null || existingInterview.getApplicationId() != applicationId)) {
            forwardForm(request, response, applicationView, null, "Không tìm thấy lịch phỏng vấn.");
            return;
        }
        if (existingInterview == null) {
            List<Interview> savedInterviews = interviewDAO.findByApplicationId(applicationId);
            if (!savedInterviews.isEmpty()) {
                forwardForm(request, response, applicationView, savedInterviews.get(0),
                        "Hồ sơ đã có lịch phỏng vấn. Vui lòng sửa lịch hiện tại.");
                return;
            }
        }

        Interview interview;
        try {
            interview = buildInterviewFromRequest(request, applicationId);
        } catch (IllegalArgumentException ex) {
            forwardForm(request, response, applicationView, existingInterview, ex.getMessage());
            return;
        }

        boolean saved;
        if (existingInterview == null) {
            interviewId = interviewDAO.create(interview);
            saved = interviewId > 0;
        } else {
            interview.setInterviewId(interviewId);
            interview.setStatus("Rescheduled");
            interview.setResult("Pending");
            saved = interviewDAO.updateSchedule(interview);
        }
        if (!saved) {
            forwardForm(request, response, applicationView, existingInterview,
                    "Không thể lưu lịch phỏng vấn. Vui lòng thử lại.");
            return;
        }

        applicationDAO.updateStatus(applicationId, "Interview", "Interview");
        boolean emailSent = sendInterviewScheduleEmail(applicationView, interview, existingInterview != null);
        notifyGuest(applicationView, currentUser, "Interview", interviewId,
                existingInterview == null ? "Bạn có lịch phỏng vấn mới" : "Lịch phỏng vấn đã được cập nhật",
                "Lịch phỏng vấn cho " + firstNonBlank(applicationView.getJobTitle(), "vị trí ứng tuyển")
                        + " vào " + formatInterviewTime(interview.getScheduledAt()) + ".",
                "High");

        redirectAfterInterviewAction(request, response, applicationId,
                existingInterview == null ? "interviewScheduled=1" : "interviewUpdated=1", emailSent);
    }

    private void handleResultUpdate(HttpServletRequest request, HttpServletResponse response, SystemUser currentUser)
            throws ServletException, IOException {
        int interviewId = parsePositiveInt(request.getParameter("interviewId"));
        Interview interview = interviewId > 0 ? interviewDAO.findById(interviewId) : null;
        if (interview == null) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=invalid_interview");
            return;
        }
        ApplicationDAO.CandidateApplicationView applicationView = loadApplication(interview.getApplicationId());
        if (applicationView == null) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=invalid_application");
            return;
        }
        if (isCompletedPassed(interview)) {
            if (RecruitmentWorkflowRules.canPrepareOffer(applicationView.getApplication())
                    || RecruitmentWorkflowRules.isOfferSent(applicationView.getApplication())) {
                response.sendRedirect(request.getContextPath()
                        + "/hrstaff/offers/manage?applicationId=" + interview.getApplicationId());
                return;
            }
            if (!"Interview".equals(applicationView.getApplication().getStatus())
                    || !"Interview".equals(applicationView.getApplication().getCurrentStep())
                    || !ensureOfferDraft(applicationView, interview)
                    || !applicationDAO.updateStatus(interview.getApplicationId(), "Interview", "Offer")) {
                response.sendError(HttpServletResponse.SC_CONFLICT,
                        "Kết quả đã được xử lý và không thể chuyển trạng thái hồ sơ hiện tại.");
                return;
            }
            response.sendRedirect(request.getContextPath()
                    + "/hrstaff/offers/manage?applicationId=" + interview.getApplicationId() + "&recovered=1");
            return;
        }

        String result = firstNonBlank(request.getParameter("result"), "Pending");
        if (!"Passed".equals(result) && !"Failed".equals(result)) {
            forwardForm(request, response, applicationView, interview, "Vui lòng chọn kết quả Pass hoặc Fail.");
            return;
        }
        String note = trimToNull(request.getParameter("resultNote"));
        if (!RecruitmentWorkflowRules.canRecordResult(interview, LocalDateTime.now())) {
            response.sendError(HttpServletResponse.SC_CONFLICT,
                    "Lịch phỏng vấn chưa đến hoặc đã được xử lý.");
            return;
        }

        boolean passed = "Passed".equals(result);
        if (passed && !ensureOfferDraft(applicationView, interview)) {
            forwardForm(request, response, applicationView, interview,
                    "Không thể tạo bản nháp offer cho ứng viên đã pass.");
            return;
        }
        if (!interviewDAO.updateResult(interviewId, "Completed", result, note)) {
            forwardForm(request, response, applicationView, interview, "Không thể cập nhật kết quả phỏng vấn.");
            return;
        }
        if (!applicationDAO.updateStatus(
                interview.getApplicationId(),
                passed ? "Interview" : "Rejected",
                passed ? "Offer" : "Rejected")) {
            forwardForm(request, response, applicationView, interview,
                    "Không thể cập nhật trạng thái hồ sơ ứng viên.");
            return;
        }

        boolean emailSent = sendInterviewResultEmail(applicationView, passed, note);
        notifyGuest(applicationView, currentUser, "Interview", interviewId,
                passed ? "Bạn đã vượt qua phỏng vấn" : "Cập nhật kết quả phỏng vấn",
                passed
                        ? "Bạn đã vượt qua phỏng vấn cho " + firstNonBlank(applicationView.getJobTitle(), "vị trí ứng tuyển") + ". BetterHR sẽ gửi offer trong bước tiếp theo."
                        : "Cảm ơn bạn đã tham gia phỏng vấn. Hồ sơ hiện chưa phù hợp với vị trí này.",
                "High");

        if (passed) {
            response.sendRedirect(request.getContextPath()
                    + "/hrstaff/offers/manage?applicationId=" + interview.getApplicationId() + "&interviewPassed=1");
            return;
        }
        redirectAfterInterviewAction(request, response, interview.getApplicationId(), "interviewResult=1", emailSent);
    }

    private boolean ensureOfferDraft(ApplicationDAO.CandidateApplicationView applicationView,
                                     Interview interview) {
        if (offerDAO.findByApplicationId(interview.getApplicationId()) != null) {
            return true;
        }
        Offer draft = new Offer();
        draft.setApplicationId(interview.getApplicationId());
        draft.setPosition(firstNonBlank(applicationView.getJobTitle(), "Vị trí ứng tuyển"));
        draft.setStatus("Draft");
        return offerDAO.saveDraft(draft) > 0;
    }

    private boolean isCompletedPassed(Interview interview) {
        return interview != null
                && "Completed".equals(interview.getStatus())
                && "Passed".equals(interview.getResult());
    }
    private void handleCancel(HttpServletRequest request, HttpServletResponse response, SystemUser currentUser)
            throws IOException {
        int interviewId = parsePositiveInt(request.getParameter("interviewId"));
        Interview interview = interviewId > 0 ? interviewDAO.findById(interviewId) : null;
        if (interview == null) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=invalid_interview");
            return;
        }
        ApplicationDAO.CandidateApplicationView applicationView = loadApplication(interview.getApplicationId());
        if (applicationView == null) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=invalid_application");
            return;
        }
        String note = trimToNull(request.getParameter("cancelNote"));
        boolean updated = interviewDAO.updateResult(interviewId, "Cancelled", "Pending", note);
        boolean emailSent = false;
        if (updated) {
            emailSent = sendInterviewCancelledEmail(applicationView, interview, note);
            notifyGuest(applicationView, currentUser, "Interview", interviewId,
                    "Lịch phỏng vấn đã bị hủy",
                    "Lịch phỏng vấn cho " + firstNonBlank(applicationView.getJobTitle(), "vị trí ứng tuyển")
                            + " đã bị hủy. HR sẽ liên hệ nếu có lịch mới.",
                    "High");
        }
        redirectAfterInterviewAction(request, response, interview.getApplicationId(),
                updated ? "interviewCancelled=1" : "error=interview", emailSent);
    }

    private void forwardForm(HttpServletRequest request,
                             HttpServletResponse response,
                             ApplicationDAO.CandidateApplicationView applicationView,
                             Interview selectedInterview,
                             String error)
            throws ServletException, IOException {
        request.setAttribute("applicationView", applicationView);
        request.setAttribute("employees", employeeDAO.getAll());
        List<Interview> interviews = interviewDAO.findByApplicationId(applicationView.getApplication().getApplicationId());
        if (selectedInterview == null && !interviews.isEmpty()) {
            selectedInterview = interviews.get(0);
        }
        request.setAttribute("interviews", interviews);
        request.setAttribute("selectedInterview", selectedInterview);
        if (error != null) {
            request.setAttribute("error", error);
        }
        request.getRequestDispatcher("/Views/HrStaff/ScheduleInterview.jsp").forward(request, response);
    }

    private Interview buildInterviewFromRequest(HttpServletRequest request, int applicationId) {

        String scheduledAtRaw = trimToNull(request.getParameter("scheduledAt"));
        LocalDateTime scheduledAt;
        try {
            scheduledAt = scheduledAtRaw == null ? null : LocalDateTime.parse(scheduledAtRaw);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Thời gian phỏng vấn không hợp lệ.");
        }
        if (scheduledAt == null || scheduledAt.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời gian phỏng vấn phải ở tương lai.");
        }

        String location = trimToNull(request.getParameter("location"));
        String meetingLink = trimToNull(request.getParameter("meetingLink"));
        if (location == null && meetingLink == null) {
            throw new IllegalArgumentException("Vui lòng nhập địa điểm hoặc link phỏng vấn.");
        }

        int interviewerEmployeeId = parsePositiveInt(request.getParameter("interviewerEmployeeId"));
        String note = trimToNull(request.getParameter("note"));

        Interview interview = new Interview();
        interview.setApplicationId(applicationId);
        interview.setRoundNo(1);
        interview.setScheduledAt(scheduledAt);
        interview.setLocation(location);
        interview.setMeetingLink(meetingLink);
        interview.setInterviewerEmployeeId(interviewerEmployeeId > 0 ? interviewerEmployeeId : null);
        interview.setStatus("Scheduled");
        interview.setResult("Pending");
        interview.setNote(note);
        return interview;
    }

    private boolean sendInterviewScheduleEmail(ApplicationDAO.CandidateApplicationView applicationView,
                                               Interview interview,
                                               boolean rescheduled) {
        String email = candidateEmail(applicationView);
        if (email == null) {
            return false;
        }
        String jobTitle = firstNonBlank(applicationView.getJobTitle(), "vị trí ứng tuyển");
        String subject = (rescheduled ? "Cập nhật lịch phỏng vấn vị trí " : "Lịch phỏng vấn vị trí ") + jobTitle + " - BetterHR";
        String content = """
                Xin chào %s,

                BetterHR %s lịch phỏng vấn cho vị trí %s.

                Thời gian: %s
                Địa điểm: %s
                Link meeting: %s
                Ghi chú: %s

                Vui lòng có mặt đúng giờ và chuẩn bị CV/tài liệu liên quan nếu cần.

                Trân trọng,
                BetterHR
                """.formatted(
                candidateName(applicationView),
                rescheduled ? "cập nhật" : "gửi bạn",
                jobTitle,
                formatInterviewTime(interview.getScheduledAt()),
                firstNonBlank(interview.getLocation(), "Sẽ cập nhật"),
                firstNonBlank(interview.getMeetingLink(), "Không có"),
                firstNonBlank(interview.getNote(), "Không có")
        );
        return sendEmail(email, subject, content);
    }

    private boolean sendInterviewResultEmail(ApplicationDAO.CandidateApplicationView applicationView,
                                             boolean passed,
                                             String note) {
        String email = candidateEmail(applicationView);
        if (email == null) {
            return false;
        }
        String jobTitle = firstNonBlank(applicationView.getJobTitle(), "vị trí ứng tuyển");
        String subject = "Kết quả phỏng vấn vị trí " + jobTitle + " - BetterHR";
        String content = passed
                ? """
                Xin chào %s,

                Chúc mừng bạn đã vượt qua vòng phỏng vấn cho vị trí %s.
                BetterHR sẽ tiếp tục gửi thông tin offer trong bước tiếp theo.

                Ghi chú: %s

                Trân trọng,
                BetterHR
                """.formatted(candidateName(applicationView), jobTitle, firstNonBlank(note, "Không có"))
                : """
                Xin chào %s,

                Cảm ơn bạn đã tham gia phỏng vấn cho vị trí %s.
                Sau khi xem xét, hồ sơ hiện chưa phù hợp với vị trí này.

                Ghi chú: %s

                Trân trọng,
                BetterHR
                """.formatted(candidateName(applicationView), jobTitle, firstNonBlank(note, "Không có"));
        return sendEmail(email, subject, content);
    }

    private boolean sendInterviewCancelledEmail(ApplicationDAO.CandidateApplicationView applicationView,
                                                Interview interview,
                                                String note) {
        String email = candidateEmail(applicationView);
        if (email == null) {
            return false;
        }
        String jobTitle = firstNonBlank(applicationView.getJobTitle(), "vị trí ứng tuyển");
        String subject = "Hủy lịch phỏng vấn vị trí " + jobTitle + " - BetterHR";
        String content = """
                Xin chào %s,

                Lịch phỏng vấn cho vị trí %s vào %s đã bị hủy.
                HR sẽ liên hệ lại nếu có lịch mới.

                Ghi chú: %s

                Trân trọng,
                BetterHR
                """.formatted(candidateName(applicationView), jobTitle,
                formatInterviewTime(interview.getScheduledAt()), firstNonBlank(note, "Không có"));
        return sendEmail(email, subject, content);
    }

    private boolean sendEmail(String email, String subject, String content) {
        try {
            EmailSender.sendEmail(email, subject, content);
            return true;
        } catch (MessagingException ex) {
            LOGGER.log(Level.WARNING, "Cannot send interview email to " + email, ex);
            return false;
        }
    }

    private void notifyGuest(ApplicationDAO.CandidateApplicationView applicationView,
                             SystemUser currentUser,
                             String entityType,
                             int entityId,
                             String title,
                             String message,
                             String priority) {
        Guest guest = applicationView.getGuest();
        if (guest == null || guest.getUserId() == null || guest.getUserId() <= 0) {
            return;
        }
        Notification notification = notificationService.buildNotification(
                guest.getUserId(),
                currentUser != null ? currentUser.getUserId() : null,
                entityType,
                entityId,
                "Interview",
                title,
                message,
                "/guest/dashboard",
                priority
        );
        notification.setApplicationId(applicationView.getApplication().getApplicationId());
        notificationService.notifyUser(notification);
    }

    private ApplicationDAO.CandidateApplicationView loadApplication(int applicationId) {
        return applicationId > 0 ? applicationDAO.findCandidateApplicationById(applicationId) : null;
    }

    private void redirectAfterInterviewAction(HttpServletRequest request,
                                              HttpServletResponse response,
                                              int applicationId,
                                              String messageQuery,
                                              boolean emailSent)
            throws IOException {
        String redirect = request.getContextPath()
                + "/hrstaff/interviews/schedule?applicationId=" + applicationId + "&" + messageQuery;
        if (!emailSent) {
            redirect += "&mail=failed";
        }
        response.sendRedirect(redirect);
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return PermissionUtil.ensurePermission(request, response, REQUIRED_PERMISSION, ACCESS_DENIED_MESSAGE);
    }

    private String candidateEmail(ApplicationDAO.CandidateApplicationView applicationView) {
        CandidateProfile profile = applicationView.getCandidateProfile();
        if (profile != null && profile.getEmail() != null && !profile.getEmail().isBlank()) {
            return profile.getEmail().trim();
        }
        Guest guest = applicationView.getGuest();
        return guest != null && guest.getEmail() != null && !guest.getEmail().isBlank()
                ? guest.getEmail().trim()
                : null;
    }

    private String candidateName(ApplicationDAO.CandidateApplicationView applicationView) {
        CandidateProfile profile = applicationView.getCandidateProfile();
        if (profile != null && profile.getFullName() != null && !profile.getFullName().isBlank()) {
            return profile.getFullName().trim();
        }
        Guest guest = applicationView.getGuest();
        return guest != null ? firstNonBlank(guest.getFullName(), "ứng viên") : "ứng viên";
    }

    private String formatInterviewTime(LocalDateTime scheduledAt) {
        return scheduledAt == null
                ? "Sẽ cập nhật"
                : scheduledAt.format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : 0;
        } catch (Exception ex) {
            return 0;
        }
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String firstNonBlank(String... values) {
        if (values == null) {
            return "";
        }
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value.trim();
            }
        }
        return "";
    }
}
