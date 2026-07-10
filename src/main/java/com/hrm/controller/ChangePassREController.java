package com.hrm.controller;

import com.hrm.dao.DAO;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "ChangePassREController", urlPatterns = {"/changepassRE"})
public class ChangePassREController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        String recoveryEmail = session != null ? (String) session.getAttribute("recoveryEmail") : null;
        Boolean recoveryVerified = session != null ? (Boolean) session.getAttribute("recoveryVerified") : null;

        if (recoveryEmail == null || !Boolean.TRUE.equals(recoveryVerified)) {
            response.sendRedirect(request.getContextPath() + "/ForgotPassword");
            return;
        }
        request.getRequestDispatcher("/Views/ChangePasswordRE.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String newPass = request.getParameter("newPass");
        String confirmPass = request.getParameter("confirmPass");
        
        HttpSession session = request.getSession(false);
        String email = session != null ? (String) session.getAttribute("recoveryEmail") : null;
        Boolean recoveryVerified = session != null ? (Boolean) session.getAttribute("recoveryVerified") : null;

        if (email == null || !Boolean.TRUE.equals(recoveryVerified)) {
            response.sendRedirect(request.getContextPath() + "/ForgotPassword");
            return;
        }

        if (newPass == null || confirmPass == null || newPass.length() < 8 || newPass.length() > 16) {
            request.setAttribute("mess", "Mật khẩu mới phải có từ 8 đến 16 ký tự.");
            request.getRequestDispatcher("/Views/ChangePasswordRE.jsp").forward(request, response);
            return;
        }
        
        String allowPattern = "[a-zA-Z0-9]+";
        if(!newPass.matches(allowPattern)){
            request.setAttribute("mess", "Mật khẩu mới không được chứa ký tự đặc biệt.");
            request.getRequestDispatcher("/Views/ChangePasswordRE.jsp").forward(request, response);
            return;
        }
        
        if (!newPass.equals(confirmPass)) {
            request.setAttribute("mess", "Mật khẩu xác nhận không khớp với mật khẩu mới.");
            request.getRequestDispatcher("/Views/ChangePasswordRE.jsp").forward(request, response);
            return;
        }
        
        SystemUser user = DAO.getInstance().getAccountByEmail(email);

        if (user == null) {
            request.setAttribute("mess", "Đã xảy ra lỗi. Không tìm thấy tài khoản của bạn.");
            request.getRequestDispatcher("/Views/ChangePasswordRE.jsp").forward(request, response);
            return;
        }
        int result = DAO.getInstance().changePassword(user.getUsername(), newPass);

        if (result > 0) {
            session.removeAttribute("recoveryEmail");
            session.removeAttribute("pinCode");
            session.removeAttribute("recoveryVerified");
            response.sendRedirect(request.getContextPath() + "/login?success=password_changed");
        } else {
            request.setAttribute("mess", "Không thể đổi mật khẩu. Vui lòng thử lại.");
            request.getRequestDispatcher("/Views/ChangePasswordRE.jsp").forward(request, response);
        }
    }
}
