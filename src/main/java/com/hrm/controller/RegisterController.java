package com.hrm.controller;

import com.hrm.dao.DAO;
import com.hrm.model.entity.SystemUser;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

@WebServlet(name = "RegisterController", urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(RegisterController.class.getName());
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9._-]{3,100}$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Views/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        String username = trim(request.getParameter("username"));
        String email = trim(request.getParameter("email"));
        String password = trim(request.getParameter("password"));
        String confirmPassword = trim(request.getParameter("confirmPassword"));

        request.setAttribute("username", username);
        request.setAttribute("email", email);

        String validationError = validate(username, email, password, confirmPassword);
        if (validationError != null) {
            forwardRegister(request, response, validationError);
            return;
        }

        email = email.toLowerCase();
        DAO dao = DAO.getInstance();

        if (dao.isSystemUsernameExists(username)) {
            forwardRegister(request, response, "Tên đăng nhập đã tồn tại.");
            return;
        }
        if (dao.isSystemEmailExists(email)) {
            forwardRegister(request, response, "Email đã được sử dụng.");
            return;
        }

        int guestRoleId = dao.getOrCreateRoleIdByName("Guest");
        SystemUser createdUser = dao.createLocalUser(username, email, password, guestRoleId);
        if (createdUser == null) {
            forwardRegister(request, response, "Không thể tạo tài khoản. Vui lòng thử lại.");
            return;
        }

        boolean mailSent = sendRegistrationSuccessEmail(createdUser);
        response.sendRedirect(request.getContextPath() + "/login?success=" + (mailSent ? "registered" : "registered_mail_failed"));
    }

    private boolean sendRegistrationSuccessEmail(SystemUser user) {
        String subject = "Đăng ký thành công BetterHR";
        String content = "Xin chào " + user.getUsername() + ",\n\n"
                + "Bạn đã đăng ký tài khoản BetterHR thành công.\n"
                + "Bạn có thể đăng nhập bằng tên đăng nhập hoặc email đã đăng ký để sử dụng hệ thống.\n\n"
                + "Thông tin tài khoản:\n"
                + "- Tên đăng nhập: " + user.getUsername() + "\n"
                + "- Email: " + user.getEmail() + "\n\n"
                + "Trân trọng,\n"
                + "BetterHR";

        try {
            EmailSender.sendEmail(user.getEmail(), subject, content);
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not send registration success email to " + user.getEmail(), e);
            return false;
        }
    }

    private String validate(String username, String email, String password, String confirmPassword) {
        if (isBlank(username) || isBlank(email) || isBlank(password) || isBlank(confirmPassword)) {
            return "Vui lòng nhập đầy đủ thông tin.";
        }
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            return "Tên đăng nhập phải có 3-100 ký tự và chỉ gồm chữ, số, dấu chấm, gạch dưới hoặc gạch ngang.";
        }
        if (!EMAIL_PATTERN.matcher(email).matches() || email.length() > 150) {
            return "Email không đúng định dạng.";
        }
        if (password.length() < 6 || password.length() > 100) {
            return "Mật khẩu phải có từ 6 đến 100 ký tự.";
        }
        if (!password.equals(confirmPassword)) {
            return "Mật khẩu xác nhận không khớp.";
        }
        return null;
    }

    private void forwardRegister(HttpServletRequest request, HttpServletResponse response, String message)
            throws ServletException, IOException {
        request.setAttribute("mess", message);
        request.getRequestDispatcher("/Views/Register.jsp").forward(request, response);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
