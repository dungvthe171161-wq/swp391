package com.hrm.controller;

import com.hrm.util.UploadPathUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet(name = "CvFileServlet", urlPatterns = "/Upload/cvs/*")
public class CvFileServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        String fileName = pathInfo == null ? null : pathInfo.replaceFirst("^/+", "");
        Path file = UploadPathUtil.resolveCvFile(getServletContext(), fileName);
        if (file == null || !Files.isRegularFile(file)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        String contentType = getServletContext().getMimeType(file.getFileName().toString());
        response.setContentType(contentType != null ? contentType : "application/octet-stream");
        response.setContentLengthLong(Files.size(file));
        response.setHeader("Content-Disposition", "inline; filename=\"" + file.getFileName() + "\"");
        response.setHeader("X-Content-Type-Options", "nosniff");
        response.setHeader("Cache-Control", "private, max-age=3600");
        Files.copy(file, response.getOutputStream());
    }
}