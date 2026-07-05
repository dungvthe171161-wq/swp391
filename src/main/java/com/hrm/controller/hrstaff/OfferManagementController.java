package com.hrm.controller.hrstaff;

import com.hrm.controller.EmailSender;
import com.hrm.dao.ApplicationDAO;
import com.hrm.dao.OfferDAO;
import com.hrm.model.entity.CandidateProfile;
import com.hrm.model.entity.Guest;
import com.hrm.model.entity.Notification;
import com.hrm.model.entity.Offer;
import com.hrm.model.entity.SystemUser;
import com.hrm.service.NotificationService;
import com.hrm.util.PermissionUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "OfferManagementController", urlPatterns = {"/hrstaff/offers/manage"})
public class OfferManagementController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(OfferManagementController.class.getName());
    private static final String REQUIRED_PERMISSION = "VIEW_RECRUITMENT";
    private static final String ACCESS_DENIED_MESSAGE = "Ban khong co quyen quan ly offer tuyen dung.";

    private final transient ApplicationDAO applicationDAO = new ApplicationDAO();
    private final transient OfferDAO offerDAO = new OfferDAO();
    private final transient NotificationService notificationService = new NotificationService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        int applicationId = parsePositiveInt(request.getParameter("applicationId"));
        ApplicationDAO.CandidateApplicationView applicationView =
                applicationDAO.findCandidateApplicationById(applicationId);
        if (applicationView == null) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=application");
            return;
        }
        forwardForm(request, response, applicationView, offerDAO.findByApplicationId(applicationId), null);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!ensureAccess(request, response)) {
            return;
        }

        int applicationId = parsePositiveInt(request.getParameter("applicationId"));
        ApplicationDAO.CandidateApplicationView applicationView =
                applicationDAO.findCandidateApplicationById(applicationId);
        if (applicationView == null) {
            response.sendRedirect(request.getContextPath() + "/candidates?error=application");
            return;
        }

        String action = trimToNull(request.getParameter("action"));
        if (action == null) {
            action = "saveDraft";
        }

        Offer offer;
        try {
            offer = buildOfferFromRequest(request, applicationId);
        } catch (IllegalArgumentException ex) {
            forwardForm(request, response, applicationView, offerDAO.findByApplicationId(applicationId), ex.getMessage());
            return;
        }

        int offerId = offerDAO.saveDraft(offer);
        if (offerId <= 0) {
            forwardForm(request, response, applicationView, offerDAO.findByApplicationId(applicationId),
                    "Khong the luu offer. Offer da gui/da phan hoi thi khong the sua.");
            return;
        }

        if (!"sendOffer".equals(action)) {
            response.sendRedirect(request.getContextPath()
                    + "/hrstaff/offers/manage?applicationId=" + applicationId + "&saved=1");
            return;
        }

        boolean sent = offerDAO.sendOffer(offerId);
        if (!sent) {
            forwardForm(request, response, applicationView, offerDAO.findByApplicationId(applicationId),
                    "Khong the gui offer. Vui long kiem tra trang thai offer.");
            return;
        }

        applicationDAO.updateStatus(applicationId, "Offered", "Offered");
        Offer sentOffer = offerDAO.findById(offerId);
        boolean mailSent = sendOfferEmail(applicationView, sentOffer);
        notifyGuest(applicationView, sentOffer, PermissionUtil.getCurrentUser(request));

        response.sendRedirect(request.getContextPath()
                + "/hrstaff/offers/manage?applicationId=" + applicationId + "&sent=1"
                + (mailSent ? "" : "&mail=failed"));
    }

    private void forwardForm(HttpServletRequest request,
                             HttpServletResponse response,
                             ApplicationDAO.CandidateApplicationView applicationView,
                             Offer offer,
                             String error)
            throws ServletException, IOException {
        request.setAttribute("applicationView", applicationView);
        request.setAttribute("offer", offer);
        request.setAttribute("error", error);
        request.getRequestDispatcher("/Views/HrStaff/ManageOffer.jsp").forward(request, response);
    }

    private Offer buildOfferFromRequest(HttpServletRequest request, int applicationId) {
        Offer offer = new Offer();
        offer.setApplicationId(applicationId);
        offer.setPosition(requireText(request.getParameter("position"), "Vui long nhap vi tri offer."));
        offer.setOfferedSalary(parseSalary(trimToNull(request.getParameter("offeredSalary"))));
        offer.setStartDate(parseStartDate(trimToNull(request.getParameter("startDate"))));
        offer.setExpiredAt(parseExpiredAt(trimToNull(request.getParameter("expiredAt"))));
        offer.setNote(trimToNull(request.getParameter("note")));
        offer.setStatus("Draft");
        return offer;
    }

    private BigDecimal parseSalary(String value) {
        if (value == null) {
            return null;
        }
        try {
            BigDecimal salary = new BigDecimal(value);
            if (salary.compareTo(BigDecimal.ZERO) < 0) {
                throw new IllegalArgumentException("Luong offer khong hop le.");
            }
            return salary;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Luong offer khong hop le.");
        }
    }

    private LocalDate parseStartDate(String value) {
        if (value == null) {
            return null;
        }
        try {
            return LocalDate.parse(value);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Ngay bat dau khong hop le.");
        }
    }

    private LocalDateTime parseExpiredAt(String value) {
        if (value == null) {
            return null;
        }
        try {
            LocalDateTime expiredAt = LocalDateTime.parse(value);
            if (expiredAt.isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("Han phan hoi offer phai lon hon hien tai.");
            }
            return expiredAt;
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Han phan hoi offer khong hop le.");
        }
    }

    private boolean sendOfferEmail(ApplicationDAO.CandidateApplicationView applicationView, Offer offer) {
        String email = candidateEmail(applicationView);
        if (email == null || offer == null) {
            return false;
        }
        String candidateName = candidateName(applicationView);
        String subject = "Thu moi nhan viec vi tri " + offer.getPosition() + " - BetterHR";
        StringBuilder content = new StringBuilder();
        content.append("Xin chao ").append(candidateName).append(",\n\n");
        content.append("BetterHR gui ban offer cho vi tri ").append(offer.getPosition()).append(".\n");
        if (offer.getOfferedSalary() != null) {
            content.append("Muc luong de xuat: ").append(offer.getOfferedSalary()).append(" VND.\n");
        }
        if (offer.getStartDate() != null) {
            content.append("Ngay bat dau du kien: ").append(offer.getStartDate()).append(".\n");
        }
        if (offer.getExpiredAt() != null) {
            content.append("Han phan hoi: ").append(offer.getExpiredAt()).append(".\n");
        }
        if (offer.getNote() != null && !offer.getNote().isBlank()) {
            content.append("\nGhi chu:\n").append(offer.getNote()).append("\n");
        }
        content.append("\nVui long dang nhap cong ung vien BetterHR de chap nhan hoac tu choi offer.\n");
        content.append("Tran trong,\nBetterHR");

        try {
            EmailSender.sendEmail(email, subject, content.toString());
            return true;
        } catch (MessagingException ex) {
            LOGGER.log(Level.WARNING, "Failed to send offer email", ex);
            return false;
        }
    }

    private void notifyGuest(ApplicationDAO.CandidateApplicationView applicationView,
                             Offer offer,
                             SystemUser actor) {
        Guest guest = applicationView.getGuest();
        if (guest == null || guest.getUserId() == null || guest.getUserId() <= 0 || offer == null) {
            return;
        }
        Notification notification = notificationService.buildNotification(
                guest.getUserId(),
                actor != null && actor.getUserId() > 0 ? actor.getUserId() : null,
                "Offer",
                offer.getOfferId(),
                "Offer",
                "Ban co offer moi",
                "BetterHR da gui offer cho vi tri " + firstNonBlank(offer.getPosition(), applicationView.getJobTitle(), "dang tuyen") + ".",
                "/guest/applications",
                "High"
        );
        notification.setApplicationId(applicationView.getApplication().getApplicationId());
        notificationService.notifyUser(notification);
    }

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensurePermission(request, response, REQUIRED_PERMISSION, ACCESS_DENIED_MESSAGE);
    }

    private String candidateEmail(ApplicationDAO.CandidateApplicationView applicationView) {
        CandidateProfile profile = applicationView.getCandidateProfile();
        Guest guest = applicationView.getGuest();
        return firstNonBlank(
                profile != null ? profile.getEmail() : null,
                guest != null ? guest.getEmail() : null
        );
    }

    private String candidateName(ApplicationDAO.CandidateApplicationView applicationView) {
        CandidateProfile profile = applicationView.getCandidateProfile();
        Guest guest = applicationView.getGuest();
        return firstNonBlank(
                profile != null ? profile.getFullName() : null,
                guest != null ? guest.getFullName() : null,
                "ung vien"
        );
    }

    private String requireText(String value, String message) {
        String trimmed = trimToNull(value);
        if (trimmed == null) {
            throw new IllegalArgumentException(message);
        }
        return trimmed;
    }

    private int parsePositiveInt(String value) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : 0;
        } catch (NumberFormatException ex) {
            return 0;
        }
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
}
