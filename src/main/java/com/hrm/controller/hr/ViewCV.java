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
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
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

        Application latestApplication = applicationDAO.findLatestByGuestId(g.getGuestId());
        Recruitment r = findRecruitment(g, latestApplication);
        if(r!= null){
            request.setAttribute("r", r);
        }
        request.setAttribute("application", latestApplication);
        request.setAttribute("reviewStatus", reviewStatus(g, latestApplication));
        request.setAttribute("canReviewCandidate", canReviewCandidate(g, latestApplication));
        request.setAttribute("canAcceptCandidate", r == null || r.getApplicant() > 0);

        request.getRequestDispatcher("/Views/hr/ViewCV.jsp").forward(request, response);
    } 
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        if (!ensureAccess(request, response)) {
            return;
        }
        String action = request.getParameter("action");
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
        Application latestApplication = applicationDAO.findLatestByGuestId(g.getGuestId());
        Recruitment recruitment = findRecruitment(g, latestApplication);

        if ("apply".equals(action)) {
            n = updateCandidateDecision(gID, latestApplication, "Hired", "Hired");
            if (n > 0) {
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
            n = updateCandidateDecision(gID, latestApplication, "Rejected", "Rejected");
            if (n > 0) {
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
        request.setAttribute("application", latestApplication);
        request.setAttribute("reviewStatus", reviewStatus(g, latestApplication));
        request.setAttribute("canReviewCandidate", canReviewCandidate(g, latestApplication));
        request.setAttribute("canAcceptCandidate", recruitment == null || recruitment.getApplicant() > 0);
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

    private void enrichCandidateFromApplicationProfile(Guest guest) {
        if (guest == null) {
            return;
        }

        Application latestApplication = applicationDAO.findLatestByGuestId(guest.getGuestId());
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
        if (latestApplication != null && latestApplication.getAppliedDate() != null) {
            guest.setAppliedDate(latestApplication.getAppliedDate());
        }
    }

    private Recruitment findRecruitment(Guest guest, Application application) {
        Integer recruitmentId = null;
        if (application != null) {
            recruitmentId = application.getRecruitmentId();
        } else if (guest != null) {
            recruitmentId = guest.getRecruitmentId();
        }
        if (recruitmentId == null || recruitmentId <= 0) {
            return null;
        }
        return DAO.getInstance().getRecruitmentById(recruitmentId);
    }

    private String recruitmentTitle(Recruitment recruitment) {
        if (recruitment == null || recruitment.getTitle() == null || recruitment.getTitle().isBlank()) {
            return "Vị trí ứng tuyển";
        }
        return recruitment.getTitle();
    }

    private int updateCandidateDecision(int guestId, Application application, String guestStatus, String applicationStatus) {
        int updated = DAO.getInstance().updateCandidateStatus(guestId, guestStatus);
        if (application != null) {
            boolean applicationUpdated = applicationDAO.updateStatus(
                    application.getApplicationId(), applicationStatus, applicationStatus);
            if (applicationUpdated) {
                application.setStatus(applicationStatus);
                application.setCurrentStep(applicationStatus);
            }
        }
        return updated;
    }

    private String reviewStatus(Guest guest, Application application) {
        if (application != null && application.getStatus() != null && !application.getStatus().isBlank()) {
            return application.getStatus();
        }
        return guest != null ? guest.getStatus() : null;
    }

    private boolean canReviewCandidate(Guest guest, Application application) {
        String status = reviewStatus(guest, application);
        return "Processing".equals(status) || "Applied".equals(status) || "Screening".equals(status);
    }
}
