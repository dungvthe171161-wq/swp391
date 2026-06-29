package com.hrm.controller.guest;

import com.hrm.controller.EmailSender;
import com.hrm.controller.RecruitmentController.PendingCandidateProfile;
import com.hrm.dao.ApplicationDAO;
import com.hrm.dao.CandidateProfileDAO;
import com.hrm.dao.GuestDAO;
import com.hrm.dao.InterviewDAO;
import com.hrm.dao.NotificationDAO;
import com.hrm.dao.OfferDAO;
import com.hrm.dao.RecruitmentDAO;
import com.hrm.model.entity.CandidateProfile;
import com.hrm.model.entity.Guest;
import com.hrm.model.entity.SystemUser;
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
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@WebServlet(name = "GuestPortalController", urlPatterns = {
        "/guest",
        "/guest/dashboard",
        "/guest/applications",
        "/guest/profile",
        "/guest/notification/read",
        "/guest/offer/respond"
})
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 12
)
public class GuestPortalController extends HttpServlet {

    private static final int EMAIL_CODE_TTL_MINUTES = 10;
    private static final String SESSION_PROFILE_DRAFT = "guestCandidateProfileDraft";
    private static final String SESSION_PROFILE_CODE = "guestCandidateProfileVerifyCode";
    private static final String SESSION_PROFILE_EXPIRES = "guestCandidateProfileVerifyExpiresAt";

    private final transient GuestDAO guestDAO = new GuestDAO();
    private final transient CandidateProfileDAO candidateProfileDAO = new CandidateProfileDAO();
    private final transient ApplicationDAO applicationDAO = new ApplicationDAO();
    private final transient InterviewDAO interviewDAO = new InterviewDAO();
    private final transient OfferDAO offerDAO = new OfferDAO();
    private final transient NotificationDAO notificationDAO = new NotificationDAO();
    private final transient RecruitmentDAO recruitmentDAO = new RecruitmentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=login_required");
            return;
        }

        String path = request.getServletPath();
        if ("/guest".equals(path)) {
            response.sendRedirect(request.getContextPath() + "/guest/dashboard");
            return;
        }

        loadBaseData(request, currentUser);

        switch (path) {
            case "/guest/applications" ->
                    request.getRequestDispatcher("/Views/Guest/Applications.jsp").forward(request, response);
            case "/guest/profile" -> {
                setProfileMessages(request);
                request.getRequestDispatcher("/Views/Guest/Profile.jsp").forward(request, response);
            }
            case "/guest/dashboard" ->
                    request.getRequestDispatcher("/Views/Guest/Dashboard.jsp").forward(request, response);
            default -> response.sendRedirect(request.getContextPath() + "/guest/dashboard");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser currentUser = getCurrentUser(request);
        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login?error=login_required");
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String path = request.getServletPath();

        if ("/guest/notification/read".equals(path)) {
            handleNotificationRead(request, response, currentUser);
            return;
        }

        if ("/guest/offer/respond".equals(path)) {
            handleOfferResponse(request, response, currentUser);
            return;
        }

        if (!"/guest/profile".equals(path)) {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            return;
        }

        String action = request.getParameter("action");
        if ("saveCandidateProfile".equals(action)) {
            handleCandidateProfileSave(request, response, currentUser);
            return;
        }
        if ("verifyCandidateProfileEmail".equals(action)) {
            handleCandidateProfileEmailVerify(request, response, currentUser);
            return;
        }
        if ("resendCandidateProfileCode".equals(action)) {
            handleCandidateProfileCodeResend(request, response);
            return;
        }

        handleBasicProfileSave(request, response, currentUser);
    }

    private void handleBasicProfileSave(HttpServletRequest request, HttpServletResponse response,
                                        SystemUser currentUser)
            throws IOException, ServletException {
        Guest profile;
        try {
            profile = buildProfileFromRequest(request, currentUser);
        } catch (DateTimeParseException ex) {
            response.sendRedirect(request.getContextPath() + "/guest/profile?error=invalid_date");
            return;
        } catch (IllegalArgumentException ex) {
            response.sendRedirect(request.getContextPath() + "/guest/profile?error=avatar");
            return;
        }
        if (profile.getFullName() == null || profile.getFullName().isBlank()) {
            response.sendRedirect(request.getContextPath() + "/guest/profile?error=missing_name");
            return;
        }

        Guest existingProfile = guestDAO.findProfileByUserOrEmail(currentUser.getUserId(), currentUser.getEmail());
        if (existingProfile != null) {
            profile.setGuestId(existingProfile.getGuestId());
            if (profile.getCv() == null) {
                profile.setCv(existingProfile.getCv());
            }
            if (profile.getAvatar() == null) {
                profile.setAvatar(existingProfile.getAvatar());
            }
        } else if (profile.getAvatar() == null) {
            profile.setAvatar(currentUser.getAvatarUrl());
        }

        boolean saved = guestDAO.saveProfile(profile);
        response.sendRedirect(request.getContextPath() + "/guest/profile?" + (saved ? "saved=1" : "error=save"));
    }

    private void loadBaseData(HttpServletRequest request, SystemUser currentUser) {
        Guest profile = guestDAO.findProfileByUserOrEmail(currentUser.getUserId(), currentUser.getEmail());
        if (profile == null) {
            profile = defaultProfile(currentUser);
        }
        CandidateProfile candidateProfile = profile.getGuestId() > 0
                ? candidateProfileDAO.findByGuestId(profile.getGuestId())
                : null;

        List<ApplicationDAO.CandidateApplicationView> applications =
                applicationDAO.findByUserId(currentUser.getUserId());
        List<InterviewDAO.InterviewScheduleView> upcomingInterviews =
                interviewDAO.findUpcomingByUserId(currentUser.getUserId(), 3);
        List<OfferDAO.OfferView> pendingOffers =
                offerDAO.findPendingByUserId(currentUser.getUserId());

        request.setAttribute("currentUser", currentUser);
        request.setAttribute("guestProfile", profile);
        request.setAttribute("candidateProfile", candidateProfile);
        request.setAttribute("applications", applications);
        request.setAttribute("upcomingInterviews", upcomingInterviews);
        request.setAttribute("pendingOffers", pendingOffers);
        request.setAttribute("notifications", notificationDAO.findByUserId(currentUser.getUserId(), 5));
        request.setAttribute("recommendedRecruitments", recruitmentDAO.getLatestThree());
        request.setAttribute("unreadNotifications", notificationDAO.countUnreadByUserId(currentUser.getUserId()));
        request.setAttribute("totalApplications", applicationDAO.countByUserId(currentUser.getUserId()));
        request.setAttribute("processingApplications", applicationDAO.countActiveByUserId(currentUser.getUserId()));
        request.setAttribute("hiredApplications", applicationDAO.countByUserIdAndStatus(currentUser.getUserId(), "Hired"));
        request.setAttribute("rejectedApplications", applicationDAO.countByUserIdAndStatus(currentUser.getUserId(), "Rejected"));
        request.setAttribute("upcomingInterviewCount", upcomingInterviews.size());
        request.setAttribute("pendingOfferCount", pendingOffers.size());
    }

    private void setProfileMessages(HttpServletRequest request) {
        if ("1".equals(request.getParameter("saved"))) {
            request.setAttribute("success", "Cập nhật hồ sơ thành công.");
        } else if ("candidate_saved".equals(request.getParameter("saved"))) {
            request.setAttribute("success", "Cập nhật hồ sơ ứng tuyển thành công.");
        }
        String error = request.getParameter("error");
        if ("missing_name".equals(error)) {
            request.setAttribute("error", "Họ và tên là bắt buộc.");
        } else if ("invalid_date".equals(error)) {
            request.setAttribute("error", "Ngày sinh không hợp lệ.");
        } else if ("save".equals(error)) {
            request.setAttribute("error", "Không thể lưu hồ sơ. Vui lòng thử lại.");
        } else if ("avatar".equals(error)) {
            request.setAttribute("error", "Ảnh đại diện phải là PNG, JPG, JPEG, WEBP hoặc GIF và không quá 5MB.");
        }
    }

    private void handleCandidateProfileSave(HttpServletRequest request, HttpServletResponse response,
                                            SystemUser currentUser)
            throws ServletException, IOException {
        Guest guest = guestDAO.findOrCreateProfileForUser(currentUser);
        if (guest == null) {
            response.sendRedirect(request.getContextPath() + "/guest/profile?error=save");
            return;
        }

        CandidateProfile existingProfile = candidateProfileDAO.findByGuestId(guest.getGuestId());
        PendingCandidateProfile draft;
        try {
            draft = buildPendingCandidateProfile(request, guest, existingProfile);
        } catch (IllegalArgumentException ex) {
            loadBaseData(request, currentUser);
            request.setAttribute("error", ex.getMessage());
            request.getRequestDispatcher("/Views/Guest/Profile.jsp").forward(request, response);
            return;
        }

        if (existingProfile != null
                && existingProfile.isEmailVerified()
                && existingProfile.getEmail() != null
                && existingProfile.getEmail().equalsIgnoreCase(draft.getEmail())) {
            CandidateProfile profile = draft.toCandidateProfile();
            profile.setCandidateProfileId(existingProfile.getCandidateProfileId());
            profile.setEmailVerified(true);
            profile.setEmailVerifiedAt(existingProfile.getEmailVerifiedAt());
            int savedId = candidateProfileDAO.save(profile);
            if (savedId > 0) {
                syncGuestFromCandidateProfile(guest, profile);
                response.sendRedirect(request.getContextPath() + "/guest/profile?saved=candidate_saved");
            } else {
                response.sendRedirect(request.getContextPath() + "/guest/profile?error=save");
            }
            return;
        }

        String code = generateVerificationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EMAIL_CODE_TTL_MINUTES);
        try {
            sendCandidateProfileCode(draft.getEmail(), code);
        } catch (Exception ex) {
            loadBaseData(request, currentUser);
            request.setAttribute("error", "Không thể gửi mã xác nhận email. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/Views/Guest/Profile.jsp").forward(request, response);
            return;
        }

        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_PROFILE_DRAFT, draft);
        session.setAttribute(SESSION_PROFILE_CODE, code);
        session.setAttribute(SESSION_PROFILE_EXPIRES, expiresAt);

        request.setAttribute("profileDraft", draft);
        request.setAttribute("ttlMinutes", EMAIL_CODE_TTL_MINUTES);
        request.getRequestDispatcher("/Views/Guest/ProfileEmailVerify.jsp").forward(request, response);
    }

    private void handleCandidateProfileEmailVerify(HttpServletRequest request, HttpServletResponse response,
                                                   SystemUser currentUser)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        PendingCandidateProfile draft = session != null
                ? (PendingCandidateProfile) session.getAttribute(SESSION_PROFILE_DRAFT)
                : null;
        String code = session != null ? (String) session.getAttribute(SESSION_PROFILE_CODE) : null;
        LocalDateTime expiresAt = session != null
                ? (LocalDateTime) session.getAttribute(SESSION_PROFILE_EXPIRES)
                : null;
        String inputCode = trimToNull(request.getParameter("verificationCode"));

        if (draft == null || code == null || expiresAt == null) {
            response.sendRedirect(request.getContextPath() + "/guest/profile?error=save");
            return;
        }
        if (inputCode == null || !inputCode.equals(code) || expiresAt.isBefore(LocalDateTime.now())) {
            request.setAttribute("profileDraft", draft);
            request.setAttribute("ttlMinutes", EMAIL_CODE_TTL_MINUTES);
            request.setAttribute("error", "Mã xác nhận không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/Views/Guest/ProfileEmailVerify.jsp").forward(request, response);
            return;
        }

        CandidateProfile profile = draft.toCandidateProfile();
        profile.setEmailVerified(true);
        profile.setEmailVerifiedAt(LocalDateTime.now());
        int savedId = candidateProfileDAO.save(profile);
        if (savedId <= 0) {
            request.setAttribute("profileDraft", draft);
            request.setAttribute("ttlMinutes", EMAIL_CODE_TTL_MINUTES);
            request.setAttribute("error", "Không thể lưu hồ sơ ứng tuyển. Vui lòng thử lại.");
            request.getRequestDispatcher("/Views/Guest/ProfileEmailVerify.jsp").forward(request, response);
            return;
        }

        Guest guest = guestDAO.findProfileByUserOrEmail(currentUser.getUserId(), currentUser.getEmail());
        if (guest != null) {
            syncGuestFromCandidateProfile(guest, profile);
        }
        clearCandidateProfileVerifySession(session);
        response.sendRedirect(request.getContextPath() + "/guest/profile?saved=candidate_saved");
    }

    private void handleCandidateProfileCodeResend(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        PendingCandidateProfile draft = session != null
                ? (PendingCandidateProfile) session.getAttribute(SESSION_PROFILE_DRAFT)
                : null;
        if (draft == null) {
            response.sendRedirect(request.getContextPath() + "/guest/profile?error=save");
            return;
        }

        String code = generateVerificationCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EMAIL_CODE_TTL_MINUTES);
        try {
            sendCandidateProfileCode(draft.getEmail(), code);
        } catch (Exception ex) {
            request.setAttribute("profileDraft", draft);
            request.setAttribute("ttlMinutes", EMAIL_CODE_TTL_MINUTES);
            request.setAttribute("error", "Không thể gửi lại mã xác nhận. Vui lòng thử lại sau.");
            request.getRequestDispatcher("/Views/Guest/ProfileEmailVerify.jsp").forward(request, response);
            return;
        }

        session.setAttribute(SESSION_PROFILE_CODE, code);
        session.setAttribute(SESSION_PROFILE_EXPIRES, expiresAt);
        request.setAttribute("profileDraft", draft);
        request.setAttribute("ttlMinutes", EMAIL_CODE_TTL_MINUTES);
        request.setAttribute("success", "Mã xác nhận mới đã được gửi đến email của bạn.");
        request.getRequestDispatcher("/Views/Guest/ProfileEmailVerify.jsp").forward(request, response);
    }

    private PendingCandidateProfile buildPendingCandidateProfile(HttpServletRequest request, Guest guest,
                                                                 CandidateProfile existingProfile)
            throws IOException, ServletException {
        PendingCandidateProfile draft = new PendingCandidateProfile();
        draft.setGuestId(guest.getGuestId());
        draft.setFullName(requireText(request.getParameter("candidateFullName"), "Vui lòng nhập họ và tên ứng viên."));
        draft.setPhone(requirePhone(request.getParameter("candidatePhone")));
        draft.setEmail(requireEmail(request.getParameter("candidateEmail")));
        draft.setDateOfBirth(parseCandidateDate(trimToNull(request.getParameter("candidateDateOfBirth"))));
        draft.setAddress(trimToNull(request.getParameter("candidateAddress")));
        draft.setDesiredPosition(trimToNull(request.getParameter("desiredPosition")));
        draft.setExpectedSalary(parseCandidateSalary(trimToNull(request.getParameter("expectedSalary"))));
        draft.setWorkExperience(trimToNull(request.getParameter("workExperience")));
        draft.setCvFilePath(saveCandidateCvFile(request, existingProfile != null ? existingProfile.getCvFilePath() : null));
        if (draft.getCvFilePath() == null || draft.getCvFilePath().isBlank()) {
            throw new IllegalArgumentException("Vui lòng upload CV định dạng PDF, DOC hoặc DOCX, tối đa 10MB.");
        }
        return draft;
    }

    private Guest buildProfileFromRequest(HttpServletRequest request, SystemUser currentUser)
            throws IOException, ServletException {
        Guest profile = new Guest();
        profile.setUserId(currentUser.getUserId());
        profile.setFullName(trimToNull(request.getParameter("fullName")));
        profile.setEmail(currentUser.getEmail());
        profile.setPhone(trimToNull(request.getParameter("phone")));
        profile.setAvatar(saveAvatarFile(request));
        profile.setGender(trimToNull(request.getParameter("gender")));
        profile.setAddress(trimToNull(request.getParameter("address")));
        profile.setStatus("Processing");

        String dateOfBirth = trimToNull(request.getParameter("dateOfBirth"));
        if (dateOfBirth != null) {
            LocalDate parsedDate = LocalDate.parse(dateOfBirth);
            if (parsedDate.isAfter(LocalDate.now())) {
                throw new DateTimeParseException("future date", dateOfBirth, 0);
            }
            profile.setDateOfBirth(parsedDate);
        }
        return profile;
    }

    private String saveAvatarFile(HttpServletRequest request)
            throws IOException, ServletException {
        Part avatarFilePart = request.getPart("avatarFile");
        if (avatarFilePart == null || avatarFilePart.getSize() <= 0) {
            return null;
        }
        if (avatarFilePart.getSize() > 5L * 1024 * 1024) {
            throw new IllegalArgumentException("avatar too large");
        }
        String originalFileName = getFileName(avatarFilePart);
        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("missing avatar filename");
        }
        String extension = getFileExtension(originalFileName).toLowerCase();
        if (!extension.matches("\\.(png|jpg|jpeg|webp|gif)")) {
            throw new IllegalArgumentException("unsupported avatar type");
        }

        String uploadRelativePath = "/Upload/avatars";
        String uploadPath = getServletContext().getRealPath(uploadRelativePath);
        Path uploadDir = uploadPath != null
                ? Paths.get(uploadPath)
                : Paths.get(System.getProperty("user.home"), "hrms", "Upload", "avatars");
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String uniqueFileName = UUID.randomUUID() + extension;
        Path filePath = uploadDir.resolve(uniqueFileName);
        try (InputStream fileContent = avatarFilePart.getInputStream()) {
            Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return request.getContextPath() + uploadRelativePath + "/" + uniqueFileName;
    }

    private void syncGuestFromCandidateProfile(Guest guest, CandidateProfile profile) {
        guest.setFullName(profile.getFullName());
        guest.setEmail(profile.getEmail());
        guest.setPhone(profile.getPhone());
        guest.setDateOfBirth(profile.getDateOfBirth());
        guest.setAddress(profile.getAddress());
        guest.setStatus(firstNonBlank(guest.getStatus(), "Processing"));
        guestDAO.saveProfile(guest);
    }

    private String saveCandidateCvFile(HttpServletRequest request, String fallbackFileName)
            throws IOException, ServletException {
        Part cvFilePart = request.getPart("candidateCvFile");
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
        String extension = getFileExtension(originalFileName).toLowerCase();
        if (!extension.matches("\\.(pdf|doc|docx)")) {
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
        String uniqueFileName = UUID.randomUUID() + extension;
        Path filePath = uploadDir.resolve(uniqueFileName);
        try (InputStream fileContent = cvFilePart.getInputStream()) {
            Files.copy(fileContent, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        return uniqueFileName;
    }

    private Guest defaultProfile(SystemUser currentUser) {
        Guest profile = new Guest();
        profile.setUserId(currentUser.getUserId());
        profile.setFullName(firstNonBlank(currentUser.getUsername(), currentUser.getEmail(), "Ứng viên BetterHR"));
        profile.setEmail(currentUser.getEmail());
        profile.setAvatar(currentUser.getAvatarUrl());
        profile.setStatus("Processing");
        return profile;
    }

    private void handleNotificationRead(HttpServletRequest request, HttpServletResponse response, SystemUser currentUser)
            throws IOException {
        String notificationId = request.getParameter("notificationId");
        if (notificationId == null || notificationId.isBlank()) {
            notificationDAO.markAllRead(currentUser.getUserId());
        } else {
            try {
                notificationDAO.markRead(Integer.parseInt(notificationId), currentUser.getUserId());
            } catch (NumberFormatException ignored) {
                // Ignore invalid ids and return to dashboard.
            }
        }
        response.sendRedirect(request.getContextPath() + "/guest/dashboard");
    }

    private void handleOfferResponse(HttpServletRequest request, HttpServletResponse response, SystemUser currentUser)
            throws IOException {
        try {
            int offerId = Integer.parseInt(request.getParameter("offerId"));
            String status = request.getParameter("status");
            boolean updated = offerDAO.respondOffer(offerId, currentUser.getUserId(), status);
            response.sendRedirect(request.getContextPath() + "/guest/applications?"
                    + (updated ? "offerUpdated=1" : "error=offer"));
        } catch (NumberFormatException ex) {
            response.sendRedirect(request.getContextPath() + "/guest/applications?error=offer");
        }
    }

    private String requireText(String value, String message) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }

    private String requireEmail(String value) {
        String email = requireText(value, "Vui lòng nhập email ứng viên.");
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Email không đúng định dạng.");
        }
        return email;
    }

    private String requirePhone(String value) {
        String phone = requireText(value, "Vui lòng nhập số điện thoại.");
        if (!phone.matches("^\\d{9,11}$")) {
            throw new IllegalArgumentException("Số điện thoại phải có 9-11 chữ số.");
        }
        return phone;
    }

    private LocalDate parseCandidateDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            LocalDate date = LocalDate.parse(value);
            if (date.isAfter(LocalDate.now())) {
                throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
            }
            return date;
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Ngày sinh không hợp lệ.");
        }
    }

    private BigDecimal parseCandidateSalary(String value) {
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

    private void sendCandidateProfileCode(String email, String code) throws Exception {
        EmailSender.sendEmail(email, "Mã xác nhận hồ sơ ứng tuyển BetterHR",
                "Mã xác nhận hồ sơ ứng tuyển của bạn là " + code
                        + ". Mã này hết hạn sau " + EMAIL_CODE_TTL_MINUTES + " phút.");
    }

    private String generateVerificationCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    private void clearCandidateProfileVerifySession(HttpSession session) {
        if (session == null) {
            return;
        }
        session.removeAttribute(SESSION_PROFILE_DRAFT);
        session.removeAttribute(SESSION_PROFILE_CODE);
        session.removeAttribute(SESSION_PROFILE_EXPIRES);
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

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
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

    private SystemUser getCurrentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        Object user = session != null ? session.getAttribute("systemUser") : null;
        return user instanceof SystemUser ? (SystemUser) user : null;
    }
}
