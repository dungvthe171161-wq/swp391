package com.hrm.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "RecoveryController", urlPatterns = {"/Recovery"})
public class RecoveryController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/Views/Recovery.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String inputPin = request.getParameter("pin");
        HttpSession session = request.getSession(false);
        String sessionPin = session != null ? (String) session.getAttribute("pinCode") : null;

        if (sessionPin != null && inputPin.equals(sessionPin)) {
            session.setAttribute("recoveryVerified", Boolean.TRUE);
            response.sendRedirect(request.getContextPath() + "/changepassRE");
        } else {
            request.setAttribute("mess", "Mã PIN không hợp lệ hoặc đã hết hạn.");
            request.getRequestDispatcher("/Views/Recovery.jsp").forward(request, response);
        }
    }
}
