package com.hrm.controller.admin;

import com.hrm.dao.OfficeLocationDAO;
import com.hrm.model.entity.OfficeLocation;
import com.hrm.model.entity.SystemUser;
import com.hrm.util.GeoUtil;
import com.hrm.util.PermissionUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;

@WebServlet(name = "OfficeLocationController", urlPatterns = {"/admin/office-location"})
public class OfficeLocationController extends HttpServlet {
    private final OfficeLocationDAO officeLocationDAO = new OfficeLocationDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser user = PermissionUtil.getCurrentUser(request);
        if (!isAdminOrHr(user)) {
            PermissionUtil.handleHtmlForbidden(request, response, "Ban khong co quyen cau hinh GPS.");
            return;
        }
        request.setAttribute("activePage", "office-location");
        request.setAttribute("officeLocations", officeLocationDAO.getAll());
        request.getRequestDispatcher("/Admin/office-location.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        SystemUser user = PermissionUtil.getCurrentUser(request);
        if (!isAdminOrHr(user)) {
            PermissionUtil.handleHtmlForbidden(request, response, "Ban khong co quyen cau hinh GPS.");
            return;
        }
        String action = request.getParameter("action");
        boolean success = false;
        String error = null;
        try {
            if ("deactivate".equals(action)) {
                success = officeLocationDAO.deactivate(parseInt(request.getParameter("officeLocationId")));
            } else if ("create".equals(action) || "update".equals(action)) {
                OfficeLocation location = readLocation(request);
                if ("update".equals(action)) {
                    location.setOfficeLocationId(parseInt(request.getParameter("officeLocationId")));
                    success = officeLocationDAO.update(location);
                } else {
                    success = officeLocationDAO.create(location);
                }
            } else {
                error = "Thao tac khong hop le.";
            }
        } catch (IllegalArgumentException ex) {
            error = ex.getMessage();
        }
        request.getSession().setAttribute(success ? "officeLocationSuccess" : "officeLocationError",
                success ? "Da cap nhat dia diem GPS." : (error != null ? error : "Khong the cap nhat dia diem GPS."));
        response.sendRedirect(request.getContextPath() + "/admin/office-location");
    }

    private OfficeLocation readLocation(HttpServletRequest request) {
        String locationName = clean(request.getParameter("locationName"));
        if (locationName.isBlank()) {
            throw new IllegalArgumentException("LocationName khong duoc rong.");
        }
        double lat = parseDouble(request.getParameter("latitude"), "Latitude khong hop le.");
        double lng = parseDouble(request.getParameter("longitude"), "Longitude khong hop le.");
        int radius = parseInt(request.getParameter("radiusMeters"));
        if (!GeoUtil.isValidLatitude(lat) || !GeoUtil.isValidLongitude(lng) || radius <= 0) {
            throw new IllegalArgumentException("Toa do hoac ban kinh khong hop le.");
        }
        OfficeLocation location = new OfficeLocation();
        location.setLocationCode(clean(request.getParameter("locationCode")));
        location.setLocationName(locationName);
        location.setAddress(clean(request.getParameter("address")));
        location.setLatitude(BigDecimal.valueOf(lat));
        location.setLongitude(BigDecimal.valueOf(lng));
        location.setRadiusMeters(radius);
        location.setActive("1".equals(request.getParameter("isActive")) || "true".equalsIgnoreCase(request.getParameter("isActive")));
        return location;
    }

    private boolean isAdminOrHr(SystemUser user) {
        if (user == null) {
            return false;
        }
        int roleId = user.getRoleId();
        return roleId == PermissionUtil.ROLE_ADMIN
                || roleId == PermissionUtil.ROLE_HR_MANAGER
                || roleId == PermissionUtil.ROLE_HR_STAFF;
    }

    private String clean(String value) {
        return value == null ? "" : value.trim();
    }

    private int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException("So nguyen khong hop le.");
        }
    }

    private double parseDouble(String value, String message) {
        try {
            return Double.parseDouble(value);
        } catch (RuntimeException ex) {
            throw new IllegalArgumentException(message);
        }
    }
}
