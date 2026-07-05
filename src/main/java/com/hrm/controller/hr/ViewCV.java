/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller.hr;

import com.hrm.controller.EmailSender;
import com.hrm.controller.EmailTemplates;
import com.hrm.dao.ApplicationDAO;
import com.hrm.dao.CandidateProfileDAO;
import com.hrm.dao.DAO;
import com.hrm.model.entity.Application;
import com.hrm.model.entity.CandidateProfile;
import com.hrm.model.entity.Guest;
import com.hrm.model.entity.Recruitment;
import com.hrm.model.entity.SystemUser;
import com.hrm.service.NotificationService;
import com.hrm.util.PermissionUtil;
import jakarta.mail.MessagingException; // Import MessagingException
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author DELL
 */
@WebServlet(name="ViewCV", urlPatterns={"/viewCV"})
public class ViewCV extends HttpServlet {
    private static final String REQUIRED_PERMISSION = "VIEW_RECRUITMENT";
    private static final String REQUIRED_ROLE_MESSAGE = "This section is restricted to HR Staff.";
    private static final String PERMISSION_DENIED_MESSAGE = "You do not have permission to view candidate CVs.";
    private final transient ApplicationDAO applicationDAO = new ApplicationDAO();
    private final transient CandidateProfileDAO candidateProfileDAO = new CandidateProfileDAO();
    private final transient NotificationService notificationService = new NotificationService();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String applicationIdRaw = request.getParameter("applicationId");
        if (applicationIdRaw != null && !applicationIdRaw.isBlank()) {
            showApplicationCv(request, response, Integer.parseInt(applicationIdRaw));
            return;
        }
        int gId = Integer.parseInt(request.getParameter("guestId"));
        Guest g = DAO.getInstance().getCandidateById(gId);
        if (g == null) {
            request.setAttribute("mess", "Candidate not found!");
            request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
            return;
        }
        enrichCandidateFromApplicationProfile(g);
        request.setAttribute("g", g);
        
        Recruitment r = DAO.getInstance().getRecruitmentById(g.getRecruitmentId());
        if(r!= null){
            request.setAttribute("r", r);
        }
        
        request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
    } 
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String action = request.getParameter("action");
        String applicationIdRaw = request.getParameter("applicationId");
        if (applicationIdRaw != null && !applicationIdRaw.isBlank()) {
            handleApplicationAction(request, response, action, Integer.parseInt(applicationIdRaw));
            return;
        }
        String gIDRaw = request.getParameter("guestId");
        
        int gID = Integer.parseInt(gIDRaw);
        int n = 0;
        String messageToHR = ""; // Thông báo sẽ hiển thị cho HR

        // Lấy lại thông tin ứng viên để có email và để hiển thị lại trên JSP
        Guest g = DAO.getInstance().getCandidateById(gID);
        if (g == null) {
            request.setAttribute("mess", "Candidate not found!");
            request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
            return;
        }
        enrichCandidateFromApplicationProfile(g);
        Recruitment recruitment = findRecruitment(g);

        if ("apply".equals(action)) {
            n = DAO.getInstance().updateCandidateStatus(gID, "Hired");
            if (n > 0) {
                notifyCandidate(request, g, recruitment, "Hired");
                try {
                    String subject = "BetterHR - CV của bạn đã vượt qua vòng sàng lọc";
                    String body = EmailTemplates.cvScreeningPassed(g.getFullName(), recruitmentTitle(recruitment));
                    EmailSender.sendHtmlEmail(g.getEmail(), subject, body);
                    messageToHR = "Ứng viên đã được duyệt và email thông báo đã được gửi.";
                } catch (MessagingException e) {
                    // Log lỗi ra console để debug
                    e.printStackTrace();
                    messageToHR = "Đã cập nhật trạng thái ứng viên nhưng không gửi được email thông báo.";
                }
            } else {
                messageToHR = "Duyệt ứng viên thất bại. Vui lòng thử lại.";
            }
            
        } else if ("reject".equals(action)) {
            n = DAO.getInstance().updateCandidateStatus(gID, "Rejected");
            if (n > 0) {
                notifyCandidate(request, g, recruitment, "Rejected");
                try {
                    String subject = "BetterHR - Cập nhật hồ sơ ứng tuyển";
                    String body = EmailTemplates.cvRejected(g.getFullName(), recruitmentTitle(recruitment));
                    EmailSender.sendHtmlEmail(g.getEmail(), subject, body);
                    messageToHR = "Ứng viên đã bị từ chối và email thông báo đã được gửi.";
                } catch (MessagingException e) {
                    e.printStackTrace();
                    messageToHR = "Đã cập nhật trạng thái ứng viên nhưng không gửi được email thông báo.";
                }
            } else {
                messageToHR = "Từ chối ứng viên thất bại. Vui lòng thử lại.";
            }
        }
        
        // Cần set lại các attribute để JSP không bị lỗi khi render lại
        request.setAttribute("g", g);
        request.setAttribute("r", recruitment);
        request.setAttribute("mess", messageToHR);
        
        request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
    }
    
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private boolean ensureAccess(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        return PermissionUtil.ensureRolePermission(
                request,
                response,
                PermissionUtil.ROLE_HR_STAFF,
                REQUIRED_PERMISSION,
                REQUIRED_ROLE_MESSAGE,
                PERMISSION_DENIED_MESSAGE
        );
    }

    private void showApplicationCv(HttpServletRequest request, HttpServletResponse response, int applicationId)
            throws ServletException, IOException {
        ApplicationDAO.CandidateApplicationView view = applicationDAO.findCandidateApplicationById(applicationId);
        if (view == null || view.getApplication() == null || view.getGuest() == null) {
            request.setAttribute("mess", "Không tìm thấy hồ sơ ứng tuyển.");
            request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
            return;
        }

        Guest guest = view.getGuest();
        applyApplicationViewToGuest(guest, view);
        request.setAttribute("g", guest);
        request.setAttribute("application", view.getApplication());

        Recruitment recruitment = DAO.getInstance().getRecruitmentById(view.getApplication().getRecruitmentId());
        if (recruitment != null) {
            request.setAttribute("r", recruitment);
        }
        request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
    }

    private void handleApplicationAction(HttpServletRequest request, HttpServletResponse response,
            String action, int applicationId) throws ServletException, IOException {
        ApplicationDAO.CandidateApplicationView view = applicationDAO.findCandidateApplicationById(applicationId);
        if (view == null || view.getApplication() == null || view.getGuest() == null) {
            request.setAttribute("mess", "Không tìm thấy hồ sơ ứng tuyển.");
            request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
            return;
        }

        Application application = view.getApplication();
        Guest guest = view.getGuest();
        applyApplicationViewToGuest(guest, view);
        Recruitment recruitment = DAO.getInstance().getRecruitmentById(application.getRecruitmentId());
        String messageToHR;

        if (!"reject".equals(action)) {
            messageToHR = "Thao tác không hợp lệ.";
        } else if (!isCvScreeningStatus(application.getStatus())) {
            messageToHR = "Chỉ có thể loại CV khi hồ sơ đang ở trạng thái Đã nộp hoặc Sàng lọc CV.";
        } else if (applicationDAO.updateStatus(applicationId, "Rejected", "Rejected")) {
            application.setStatus("Rejected");
            application.setCurrentStep("Rejected");
            guest.setStatus("Rejected");
            notifyCandidate(request, guest, recruitment, "Rejected");
            try {
                String subject = "BetterHR - Cập nhật hồ sơ ứng tuyển";
                String body = EmailTemplates.cvRejected(guest.getFullName(), recruitmentTitle(recruitment));
                EmailSender.sendHtmlEmail(guest.getEmail(), subject, body);
                messageToHR = "Đã loại CV và email thông báo đã được gửi cho ứng viên.";
            } catch (MessagingException e) {
                e.printStackTrace();
                messageToHR = "Đã loại CV nhưng không gửi được email thông báo.";
            }
        } else {
            messageToHR = "Loại CV thất bại. Vui lòng thử lại.";
        }

        request.setAttribute("g", guest);
        request.setAttribute("application", application);
        if (recruitment != null) {
            request.setAttribute("r", recruitment);
        }
        request.setAttribute("mess", messageToHR);
        request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
    }

    private boolean isCvScreeningStatus(String status) {
        return "Applied".equals(status) || "Screening".equals(status);
    }
    private void applyApplicationViewToGuest(Guest guest, ApplicationDAO.CandidateApplicationView view) {
        if (guest == null || view == null || view.getApplication() == null) {
            return;
        }
        Application application = view.getApplication();
        CandidateProfile profile = view.getCandidateProfile();

        if (profile != null) {
            if (profile.getFullName() != null && !profile.getFullName().isBlank()) {
                guest.setFullName(profile.getFullName());
            }
            if (profile.getEmail() != null && !profile.getEmail().isBlank()) {
                guest.setEmail(profile.getEmail());
            }
            if (profile.getPhone() != null && !profile.getPhone().isBlank()) {
                guest.setPhone(profile.getPhone());
            }
            if (profile.getDateOfBirth() != null) {
                guest.setDateOfBirth(profile.getDateOfBirth());
            }
            if (profile.getAddress() != null && !profile.getAddress().isBlank()) {
                guest.setAddress(profile.getAddress());
            }
        }

        String cv = application.getCv();
        if ((cv == null || cv.isBlank()) && profile != null) {
            cv = profile.getCvFilePath();
        }
        if (cv != null && !cv.isBlank()) {
            guest.setCv(cv);
        }
        guest.setRecruitmentId(application.getRecruitmentId());
        guest.setAppliedDate(application.getAppliedDate());
        guest.setStatus(application.getStatus());
    }
    private void enrichCandidateFromApplicationProfile(Guest guest) {
        if (guest == null) {
            return;
        }

        List<Application> applications = applicationDAO.findByGuestId(guest.getGuestId());
        Application latestApplication = applications.isEmpty() ? null : applications.get(0);
        CandidateProfile profile = candidateProfileDAO.findByGuestId(guest.getGuestId());

        if ((guest.getCv() == null || guest.getCv().isBlank()) && latestApplication != null
                && latestApplication.getCv() != null && !latestApplication.getCv().isBlank()) {
            guest.setCv(latestApplication.getCv());
        }
        if ((guest.getCv() == null || guest.getCv().isBlank()) && profile != null
                && profile.getCvFilePath() != null && !profile.getCvFilePath().isBlank()) {
            guest.setCv(profile.getCvFilePath());
        }
        if (guest.getRecruitmentId() == null && latestApplication != null) {
            guest.setRecruitmentId(latestApplication.getRecruitmentId());
        }
    }

    private Recruitment findRecruitment(Guest guest) {
        if (guest == null || guest.getRecruitmentId() == null) {
            return null;
        }
        return DAO.getInstance().getRecruitmentById(guest.getRecruitmentId());
    }

    private void notifyCandidate(HttpServletRequest request, Guest guest, Recruitment recruitment, String status) {
        if (guest == null || guest.getUserId() == null || guest.getUserId() <= 0) {
            return;
        }
        SystemUser currentUser = PermissionUtil.getCurrentUser(request);
        notificationService.notifyApplicationStatusChangedForCandidate(
                guest.getUserId(),
                currentUser != null ? currentUser.getUserId() : 0,
                guest.getGuestId(),
                recruitmentTitle(recruitment),
                status
        );
    }

    private String recruitmentTitle(Recruitment recruitment) {
        if (recruitment == null || recruitment.getTitle() == null || recruitment.getTitle().isBlank()) {
            return "Vị trí ứng tuyển";
        }
        return recruitment.getTitle();
    }
}
