package com.hrm.controller;

import com.hrm.dao.ApplicationDAO;
import com.hrm.dao.CandidateProfileDAO;
import com.hrm.dao.GuestDAO;
import com.hrm.dao.NotificationDAO;
import com.hrm.dao.RecruitmentDAO;
import com.hrm.model.entity.Application;
import com.hrm.model.entity.CandidateProfile;
import com.hrm.model.entity.Guest;
import com.hrm.model.entity.Notification;
import com.hrm.model.entity.Recruitment;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "RecruitmentController", urlPatterns = {"/RecruitmentController"})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 12
)
public class RecruitmentController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RecruitmentController.class.getName());
    private static final long serialVersionUID = 1L;
    private static final int EMAIL_CODE_TTL_MINUTES = 10;
    private static final String SESSION_PROFILE_DRAFT = "candidateProfileDraft";
    private static final String SESSION_PROFILE_CODE = "candidateProfileVerifyCode";
    private static final String SESSION_PROFILE_EXPIRES = "candidateProfileVerifyExpiresAt";
    private static final String SESSION_PROFILE_RECRUITMENT_ID = "candidateProfileVerifyRecruitmentId";
    private static final String DUPLICATE_MESSAGE = "Bạn đã ứng tuyển công việc này.";

    private final transient RecruitmentDAO recruitmentDAO = new RecruitmentDAO();
    private final transient GuestDAO guestDAO = new GuestDAO();
    private final transient CandidateProfileDAO candidateProfileDAO = new CandidateProfileDAO();
    private final transient ApplicationDAO applicationDAO = new ApplicationDAO();
    private final transient NotificationDAO notificationDAO = new NotificationDAO();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = trimToNull(request.getParameter("action"));

        if (action == null) {
            action = "list";
        }

        switch (action) {
            case "list" -> showRecruitmentList(request, response);
            case "apply" -> handleApplyEntry(request, response);
            case "saveCandidateProfile" -> saveCandidateProfileDraft(request, response);
            case "verifyCandidateProfileEmail" -> verifyCandidateProfileEmail(request, response);
            case "resendCandidateProfileCode" -> resendCandidateProfileCode(request, response);
            case "confirmApplication" -> confirmApplication(request, response);
            case "submitApplication" -> handleApplyEntry(request, response);
            case "view" -> viewRecruitment(request, response);
            default -> showRecruitmentList(request, response);
        }
    }

    private void showRecruitmentList(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            setRecruitmentListMessages(request);
            var recruitments = recruitmentDAO.getLatestThree();
            request.setAttribute("recruitments", recruitments);
            RequestDispatcher dispatcher = request.getRequestDispatcher("/Views/Recruitment.jsp");
            dispatcher.forward(request, response);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot load recruitments", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleApplyEntry(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = getCurrentUser(request);
        String recruitmentIdStr = trimToNull(request.getParameter("recruitmentId"));
        if (currentUser == null) {
            rememberApplyUrl(request, recruitmentIdStr);
            response.sendRedirect(request.getContextPath() + "/login?error=login_required");
            return;
        }

        int recruitmentId = parseRecruitmentId(request, response, recruitmentIdStr);
        if (recruitmentId <= 0) {
            return;
        }

        Recruitment recruitment = recruitmentDAO.getById(recruitmentId);
        if (!isOpenRecruitment(recruitment)) {
            redirectRecruitmentError(response, request, "Tin tuyển dụng này không còn nhận hồ sơ.");
            return;
        }

        Guest guest = guestDAO.findOrCreateProfileForUser(currentUser);
        if (guest == null) {
            redirectRecruitmentError(response, request, "Không thể tạo hồ sơ ứng viên. Vui lòng thử lại.");
            return;
        }

        if (hasAlreadyApplied(currentUser, guest, recruitmentId)) {
            redirectRecruitmentError(response, request, DUPLICATE_MESSAGE);
            return;
        }

        CandidateProfile profile = candidateProfileDAO.findByGuestId(guest.getGuestId());
        if (isReadyProfile(profile)) {
            forwardApplyConfirm(request, response, recruitment, profile);
            return;
        }

        request.setAttribute("recruitment", recruitment);
        request.setAttribute("candidateProfile", profile);
        request.setAttribute("currentUser", currentUser);
        request.getRequestDispatcher("/Views/ApplyForm.jsp").forward(request, response);
    }

    private void saveCandidateProfileDraft(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=login_required");
            return;
        }

        String recruitmentIdStr = trimToNull(request.getParameter("recruitmentId"));
        int recruitmentId = parseRecruitmentId(request, response, recruitmentIdStr);
        if (recruitmentId <= 0) {
            return;
        }

        Recruitment recruitment = recruitmentDAO.getById(recruitmentId);
        if (!isOpenRecruitment(recruitment)) {
            redirectRecruitmentError(response, request, "Tin tuyển dụng này không còn nhận hồ sơ.");
            return;
        }

        Guest guest = guestDAO.findOrCreateProfileForUser(currentUser);
        if (guest == null) {
            forwardApplyFormError(request, response, recruitment, null, currentUser,
                    "Không thể tạo hồ sơ ứng viên. Vui lòng thử lại.");
            return;
        }

        if (hasAlreadyApplied(currentUser, guest, recruitmentId)) {
            redirectRecruitmentError(response, request, DUPLICATE_MESSAGE);
            return;
        }

        CandidateProfile existingProfile = candidateProfileDAO.findByGuestId(guest.getGuestId());
        PendingCandidateProfile draft;
        try {
            draft = buildPendingProfile(request, guest, existingProfile);
        } catch (IllegalArgumentException ex) {
            forwardApplyFormError(request, response, recruitment, existingProfile, currentUser, ex.getMessage());
            return;
        }

        if (existingProfile != null
                && existingProfile.isEmailVerified()
                && existingProfile.getEmail() != null
                && existingProfile.getEmail().equalsIgnoreCase(draft.getEmail())) {
            CandidateProfile savedProfile = draft.toCandidateProfile();
            savedProfile.setCandidateProfileId(existingProfile.getCandidateProfileId());
            savedProfile.setEmailVerified(true);
            savedProfile.setEmailVerifiedAt(existingProfile.getEmailVerifiedAt());
            int savedId = candidateProfileDAO.save(savedProfile);
            if (savedId <= 0) {
                forwardApplyFormError(request, response, recruitment, existingProfile, currentUser,
                        "Không thể lưu hồ sơ ứng tuyển. Vui lòng thử lại.");
                return;
            }
            syncGuestProfile(guest, savedProfile);
            response.sendRedirect(request.getContextPath()
                    + "/RecruitmentController?action=apply&recruitmentId=" + recruitmentId);
            return;
        }

        String code = generateVerificationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EMAIL_CODE_TTL_MINUTES);
        try {
            sendCandidateProfileCode(draft.getEmail(), code);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Cannot send candidate profile verification code", ex);
            forwardApplyFormError(request, response, recruitment, existingProfile, currentUser,
                    "Không thể gửi mã xác nhận email. Vui lòng thử lại sau.");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_PROFILE_DRAFT, draft);
        session.setAttribute(SESSION_PROFILE_CODE, code);
        session.setAttribute(SESSION_PROFILE_EXPIRES, expiresAt);
        session.setAttribute(SESSION_PROFILE_RECRUITMENT_ID, recruitmentId);

        forwardVerifyEmail(request, response, recruitment, draft, null);
    }

    private void verifyCandidateProfileEmail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=login_required");
            return;
        }

        VerificationState state = getVerificationState(request);
        if (!state.isComplete()) {
            redirectRecruitmentError(response, request, "Phiên xác nhận email đã hết hạn. Vui lòng lưu hồ sơ lại.");
            return;
        }

        Recruitment recruitment = recruitmentDAO.getById(state.recruitmentId());
        String inputCode = trimToNull(request.getParameter("verificationCode"));
        if (inputCode == null || !inputCode.equals(state.code()) || state.expiresAt().isBefore(LocalDateTime.now())) {
            forwardVerifyEmail(request, response, recruitment, state.draft(),
                    "Mã xác nhận không hợp lệ hoặc đã hết hạn.");
            return;
        }

        CandidateProfile profile = state.draft().toCandidateProfile();
        profile.setEmailVerified(true);
        profile.setEmailVerifiedAt(LocalDateTime.now());
        int savedId = candidateProfileDAO.save(profile);
        if (savedId <= 0) {
            forwardVerifyEmail(request, response, recruitment, state.draft(),
                    "Không thể lưu hồ sơ ứng tuyển. Vui lòng thử lại.");
            return;
        }
        profile.setCandidateProfileId(savedId);

        Guest guest = guestDAO.getById(profile.getGuestId());
        if (guest != null) {
            syncGuestProfile(guest, profile);
        }
        clearVerificationState(request.getSession(false));

        response.sendRedirect(request.getContextPath()
                + "/RecruitmentController?action=apply&recruitmentId=" + state.recruitmentId());
    }

    private void resendCandidateProfileCode(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        VerificationState state = getVerificationState(request);
        if (!state.hasDraft()) {
            redirectRecruitmentError(response, request, "Phiên xác nhận email đã hết hạn. Vui lòng lưu hồ sơ lại.");
            return;
        }

        String code = generateVerificationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EMAIL_CODE_TTL_MINUTES);
        try {
            sendCandidateProfileCode(state.draft().getEmail(), code);
        } catch (Exception ex) {
            LOGGER.log(Level.WARNING, "Cannot resend candidate profile verification code", ex);
            Recruitment recruitment = recruitmentDAO.getById(state.recruitmentId());
            forwardVerifyEmail(request, response, recruitment, state.draft(),
                    "Không thể gửi lại mã xác nhận. Vui lòng thử lại sau.");
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_PROFILE_CODE, code);
        session.setAttribute(SESSION_PROFILE_EXPIRES, expiresAt);

        Recruitment recruitment = recruitmentDAO.getById(state.recruitmentId());
        request.setAttribute("success", "Mã xác nhận mới đã được gửi đến email của bạn.");
        forwardVerifyEmail(request, response, recruitment, state.draft(), null);
    }

    private void confirmApplication(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=login_required");
            return;
        }

        String recruitmentIdStr = trimToNull(request.getParameter("recruitmentId"));
        int recruitmentId = parseRecruitmentId(request, response, recruitmentIdStr);
        if (recruitmentId <= 0) {
            return;
        }

        Recruitment recruitment = recruitmentDAO.getById(recruitmentId);
        if (!isOpenRecruitment(recruitment)) {
            redirectRecruitmentError(response, request, "Tin tuyển dụng này không còn nhận hồ sơ.");
            return;
        }

        Guest guest = guestDAO.findOrCreateProfileForUser(currentUser);
        if (guest == null) {
            redirectRecruitmentError(response, request, "Không thể tải hồ sơ ứng viên. Vui lòng thử lại.");
            return;
        }

        if (hasAlreadyApplied(currentUser, guest, recruitmentId)) {
            redirectRecruitmentError(response, request, DUPLICATE_MESSAGE);
            return;
        }

        CandidateProfile profile = candidateProfileDAO.findByGuestId(guest.getGuestId());
        if (!isReadyProfile(profile)) {
            response.sendRedirect(request.getContextPath()
                    + "/RecruitmentController?action=apply&recruitmentId=" + recruitmentId);
            return;
        }

        Application application = new Application();
        application.setGuestId(guest.getGuestId());
        application.setRecruitmentId(recruitmentId);
        application.setCandidateProfileId(profile.getCandidateProfileId());
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus("Applied");
        application.setCurrentStep("Applied");
        application.setCv(profile.getCvFilePath());
        application.setNote(trimToNull(request.getParameter("note")));
        application.setSource("Portal");

        int applicationId = applicationDAO.create(application);
        if (applicationId <= 0) {
            forwardApplyConfirmError(request, response, recruitment, profile,
                    "Không thể gửi hồ sơ. Vui lòng thử lại.");
            return;
        }

        Notification notification = new Notification();
        notification.setUserId(currentUser.getUserId());
        notification.setApplicationId(applicationId);
        notification.setTitle("Đã nhận hồ sơ ứng tuyển");
        notification.setMessage("Hồ sơ của bạn cho vị trí " + recruitment.getTitle() + " đã được BetterHR ghi nhận.");
        notification.setType("Application");
        notification.setRead(false);
        notificationDAO.create(notification);

        response.sendRedirect(request.getContextPath() + "/Views/Success.jsp");
    }

    private PendingCandidateProfile buildPendingProfile(HttpServletRequest request, Guest guest,
                                                        CandidateProfile existingProfile)
            throws IOException, ServletException {
        String fullName = trimToNull(request.getParameter("fullName"));
        String phone = trimToNull(request.getParameter("phone"));
        String email = trimToNull(request.getParameter("email"));
        String address = trimToNull(request.getParameter("address"));
        String desiredPosition = trimToNull(request.getParameter("desiredPosition"));
        String workExperience = trimToNull(request.getParameter("workExperience"));
        LocalDate dateOfBirth = parseDate(trimToNull(request.getParameter("dateOfBirth")));
        BigDecimal expectedSalary = parseExpectedSalary(trimToNull(request.getParameter("expectedSalary")));

        if (fullName == null) {
            throw new IllegalArgumentException("Vui lòng nhập họ và tên.");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }
        if (!isValidPhone(phone)) {
            throw new IllegalArgumentException("Số điện thoại phải có 9-11 chữ số.");
        }
        if (dateOfBirth != null && dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
        }

        String cvFileName = saveCvFile(request, existingProfile != null ? existingProfile.getCvFilePath() : null);
        if (cvFileName == null || cvFileName.isBlank()) {
            throw new IllegalArgumentException("Vui lòng upload CV định dạng PDF, DOC hoặc DOCX, tối đa 10MB.");
        }

        PendingCandidateProfile draft = new PendingCandidateProfile();
        draft.setGuestId(guest.getGuestId());
        draft.setFullName(fullName);
        draft.setPhone(phone);
        draft.setEmail(email);
        draft.setDateOfBirth(dateOfBirth);
        draft.setAddress(address);
        draft.setDesiredPosition(desiredPosition);
        draft.setExpectedSalary(expectedSalary);
        draft.setWorkExperience(workExperience);
        draft.setCvFilePath(cvFileName);
        return draft;
    }

    private String saveCvFile(HttpServletRequest request, String fallbackFileName)
            throws IOException, ServletException {
        Part cvFilePart = request.getPart("cvFile");
        if (cvFilePart == null || cvFilePart.getSize() <= 0) {
            return fallbackFileName;
        }

        if (cvFilePart.getSize() > 10L * 1024 * 1024) {
            return null;
        }

        String originalFileName = getFileName(cvFilePart);
        if (originalFileName == null || originalFileName.isBlank()) {
            return null;
        }

        String fileExtension = getFileExtension(originalFileName).toLowerCase();
        if (!fileExtension.matches("\\.(pdf|doc|docx)")) {
            return null;
        }

        String uploadRelativePath = "/Upload/cvs";
        String uploadPath = getServletContext().getRealPath(uploadRelativePath);
        Path uploadDir = uploadPath != null
                ? Paths.get(uploadPath)
                : Paths.get(System.getProperty("user.home"), "hrms", "Upload", "cvs");

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String uniqueFileName = UUID.randomUUID() + fileExtension;
        Path filePath = uploadDir.resolve(uniqueFileName);
        try (InputStream fileContent = cvFilePart.getInputStream()) {
            Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return uniqueFileName;
    }

    private void forwardApplyFormError(HttpServletRequest request, HttpServletResponse response,
                                       Recruitment recruitment, CandidateProfile profile,
                                       SystemUser currentUser, String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        request.setAttribute("recruitment", recruitment);
        request.setAttribute("candidateProfile", profile);
        request.setAttribute("currentUser", currentUser);
        request.getRequestDispatcher("/Views/ApplyForm.jsp").forward(request, response);
    }

    private void forwardApplyConfirm(HttpServletRequest request, HttpServletResponse response,
                                     Recruitment recruitment, CandidateProfile profile)
            throws ServletException, IOException {
        request.setAttribute("recruitment", recruitment);
        request.setAttribute("candidateProfile", profile);
        request.getRequestDispatcher("/Views/ApplyConfirm.jsp").forward(request, response);
    }

    private void forwardApplyConfirmError(HttpServletRequest request, HttpServletResponse response,
                                          Recruitment recruitment, CandidateProfile profile,
                                          String message)
            throws ServletException, IOException {
        request.setAttribute("error", message);
        forwardApplyConfirm(request, response, recruitment, profile);
    }

    private void forwardVerifyEmail(HttpServletRequest request, HttpServletResponse response,
                                    Recruitment recruitment, PendingCandidateProfile draft,
                                    String error)
            throws ServletException, IOException {
        if (error != null) {
            request.setAttribute("error", error);
        }
        request.setAttribute("recruitment", recruitment);
        request.setAttribute("profileDraft", draft);
        request.setAttribute("ttlMinutes", EMAIL_CODE_TTL_MINUTES);
        request.getRequestDispatcher("/Views/ApplyVerifyEmail.jsp").forward(request, response);
    }

    private void viewRecruitment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            String recruitmentIdStr = request.getParameter("recruitmentId");
            if (recruitmentIdStr != null) {
                int recruitmentId = Integer.parseInt(recruitmentIdStr);
                Recruitment recruitment = recruitmentDAO.getById(recruitmentId);
                request.setAttribute("recruitment", recruitment);

                RequestDispatcher dispatcher = request.getRequestDispatcher("/Views/RecruitmentDetail.jsp");
                dispatcher.forward(request, response);
            } else {
                showRecruitmentList(request, response);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Cannot view recruitment detail", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private int parseRecruitmentId(HttpServletRequest request, HttpServletResponse response,
                                   String recruitmentIdStr)
            throws IOException {
        if (recruitmentIdStr == null) {
            redirectRecruitmentError(response, request, "Tin tuyển dụng không hợp lệ.");
            return 0;
        }
        try {
            return Integer.parseInt(recruitmentIdStr);
        } catch (NumberFormatException ex) {
            redirectRecruitmentError(response, request, "Tin tuyển dụng không hợp lệ.");
            return 0;
        }
    }

    private boolean hasAlreadyApplied(SystemUser currentUser, Guest guest, int recruitmentId) {
        String email = firstNonBlank(currentUser.getEmail(), guest.getEmail());
        return applicationDAO.existsByUserOrEmailAndRecruitment(currentUser.getUserId(), email, recruitmentId);
    }

    private void syncGuestProfile(Guest guest, CandidateProfile profile) {
        guest.setFullName(profile.getFullName());
        guest.setEmail(profile.getEmail());
        guest.setPhone(profile.getPhone());
        guest.setDateOfBirth(profile.getDateOfBirth());
        guest.setAddress(profile.getAddress());
        guest.setStatus(firstNonBlank(guest.getStatus(), "Processing"));
        guestDAO.saveProfile(guest);
    }

    private VerificationState getVerificationState(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return VerificationState.empty();
        }
        Object draft = session.getAttribute(SESSION_PROFILE_DRAFT);
        Object code = session.getAttribute(SESSION_PROFILE_CODE);
        Object expires = session.getAttribute(SESSION_PROFILE_EXPIRES);
        Object recruitmentId = session.getAttribute(SESSION_PROFILE_RECRUITMENT_ID);
        return new VerificationState(
                draft instanceof PendingCandidateProfile ? (PendingCandidateProfile) draft : null,
                code instanceof String ? (String) code : null,
                expires instanceof LocalDateTime ? (LocalDateTime) expires : null,
                recruitmentId instanceof Integer ? (Integer) recruitmentId : 0
        );
    }

    private void clearVerificationState(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(SESSION_PROFILE_DRAFT);
        session.removeAttribute(SESSION_PROFILE_CODE);
        session.removeAttribute(SESSION_PROFILE_EXPIRES);
        session.removeAttribute(SESSION_PROFILE_RECRUITMENT_ID);
    }

    private void rememberApplyUrl(HttpServletRequest request, String recruitmentIdStr) {
        if (recruitmentIdStr == null) {
            return;
        }
        String target = request.getContextPath()
                + "/RecruitmentController?action=apply&recruitmentId=" + recruitmentIdStr;
        request.getSession(true).setAttribute("redirectAfterLogin", target);
    }

    private void redirectRecruitmentError(HttpServletResponse response, HttpServletRequest request, String message)
            throws IOException {
        response.sendRedirect(request.getContextPath() + "/RecruitmentController?error="
                + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8));
    }

    private void setRecruitmentListMessages(HttpServletRequest request) {
        String error = trimToNull(request.getParameter("error"));
        String success = trimToNull(request.getParameter("success"));
        if (error != null) {
            request.setAttribute("error", error);
        }
        if ("applied".equals(success)) {
            request.setAttribute("success", "Ứng tuyển thành công.");
        }
    }

    private void sendCandidateProfileCode(String email, String code) throws Exception {
        String subject = "Mã xác nhận hồ sơ ứng tuyển BetterHR";
        String content = "Mã xác nhận hồ sơ ứng tuyển của bạn là " + code
                + ". Mã này hết hạn sau " + EMAIL_CODE_TTL_MINUTES + " phút.";
        EmailSender.sendEmail(email, subject, content);
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    private boolean isOpenRecruitment(Recruitment recruitment) {
        return recruitment != null && "Applied".equals(recruitment.getStatus());
    }

    private boolean isReadyProfile(CandidateProfile profile) {
        return profile != null
                && profile.isEmailVerified()
                && profile.getCvFilePath() != null
                && !profile.getCvFilePath().isBlank();
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }

    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\d{9,11}$");
    }

    private LocalDate parseDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
        }
    }

    private BigDecimal parseExpectedSalary(String value) {
        if (value == null) {
            return null;
        }
        try {
            BigDecimal salary = new BigDecimal(value);
            if (salary.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Mức lương mong muốn không hợp lệ.");
            }
            return salary;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Mức lương mong muốn không hợp lệ.");
        }
    }

    private SystemUser getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object user = session != null ? session.getAttribute("systemUser") : null;
        return user instanceof SystemUser ? (SystemUser) user : null;
    }

    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition != null) {
            String[] tokens = contentDisposition.split(";");
            for (String token : tokens) {
                if (token.trim().startsWith("filename")) {
                    return token.substring(token.indexOf("=") + 2, token.length() - 1);
                }
            }
        }
        return null;
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf("."));
        }
        return "";
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return "";
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Recruitment Controller";
    }

    public static class PendingCandidateProfile implements Serializable {
        private static final long serialVersionUID = 1L;

        private int guestId;
        private String fullName;
        private String phone;
        private String email;
        private LocalDate dateOfBirth;
        private String address;
        private String desiredPosition;
        private BigDecimal expectedSalary;
        private String workExperience;
        private String cvFilePath;

        public CandidateProfile toCandidateProfile() {
            CandidateProfile profile = new CandidateProfile();
            profile.setGuestId(guestId);
            profile.setFullName(fullName);
            profile.setPhone(phone);
            profile.setEmail(email);
            profile.setDateOfBirth(dateOfBirth);
            profile.setAddress(address);
            profile.setDesiredPosition(desiredPosition);
            profile.setExpectedSalary(expectedSalary);
            profile.setWorkExperience(workExperience);
            profile.setCvFilePath(cvFilePath);
            return profile;
        }

        public int getGuestId() {
            return guestId;
        }

        public void setGuestId(int guestId) {
            this.guestId = guestId;
        }

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public LocalDate getDateOfBirth() {
            return dateOfBirth;
        }

        public void setDateOfBirth(LocalDate dateOfBirth) {
            this.dateOfBirth = dateOfBirth;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getDesiredPosition() {
            return desiredPosition;
        }

        public void setDesiredPosition(String desiredPosition) {
            this.desiredPosition = desiredPosition;
        }

        public BigDecimal getExpectedSalary() {
            return expectedSalary;
        }

        public void setExpectedSalary(BigDecimal expectedSalary) {
            this.expectedSalary = expectedSalary;
        }

        public String getWorkExperience() {
            return workExperience;
        }

        public void setWorkExperience(String workExperience) {
            this.workExperience = workExperience;
        }

        public String getCvFilePath() {
            return cvFilePath;
        }

        public void setCvFilePath(String cvFilePath) {
            this.cvFilePath = cvFilePath;
        }
    }

    private record VerificationState(PendingCandidateProfile draft, String code,
                                     LocalDateTime expiresAt, int recruitmentId) {
        static VerificationState empty() {
            return new VerificationState(null, null, null, 0);
        }

        boolean hasDraft() {
            return draft != null && recruitmentId > 0;
        }

        boolean isComplete() {
            return draft != null && code != null && expiresAt != null && recruitmentId > 0;
        }
    }
}
