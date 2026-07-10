package com.hrm.controller.admin;

import com.hrm.dao.RolePermissionDAO;
import com.hrm.model.dto.PermissionSummary;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * API phục vụ quản lý phân quyền theo role
 */
@WebServlet(name = "RolePermissionServlet", urlPatterns = {"/admin/role-permissions/api"})
public class RolePermissionServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(RolePermissionServlet.class.getName());
    private final RolePermissionDAO rolePermissionDAO = new RolePermissionDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String roleIdParam = request.getParameter("roleId");
        try {
            int roleId = (roleIdParam == null || roleIdParam.isBlank())
                    ? -1
                    : Integer.parseInt(roleIdParam);

            List<PermissionSummary> permissions = rolePermissionDAO.getAllPermissionSummaries();
            Set<Integer> assigned = roleId > 0
                    ? rolePermissionDAO.getPermissionIdSetByRole(roleId)
                    : java.util.Collections.emptySet();
            JSONArray permissionArray = buildPermissionArray(permissions, assigned);

            JSONObject result = new JSONObject()
                    .put("permissions", permissionArray);
            if (roleId > 0) {
                result.put("roleId", roleId);
            }
            response.getWriter().write(result.toString());
        } catch (NumberFormatException e) {
            logger.log(Level.WARNING, "roleId không hợp lệ: " + roleIdParam, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject()
                    .put("status", "error")
                    .put("message", "roleId không hợp lệ").toString());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi truy vấn phân quyền role", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new JSONObject()
                    .put("status", "error")
                    .put("message", "Không thể tải dữ liệu quyền").toString());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            JSONObject jsonRequest = readJsonRequest(request);
            int roleId = jsonRequest.optInt("roleId", -1);
            boolean granted = jsonRequest.optBoolean("granted", true);
            JSONArray permissionIdArray = jsonRequest.optJSONArray("permissionIds");
            int permissionId = jsonRequest.optInt("permissionId", -1);
            List<Integer> permissionIds = new ArrayList<>();

            if (permissionIdArray != null) {
                for (int i = 0; i < permissionIdArray.length(); i++) {
                    permissionIds.add(permissionIdArray.optInt(i, -1));
                }
            } else if (permissionId > 0) {
                permissionIds.add(permissionId);
            }

            if (roleId <= 0 || permissionIds.isEmpty()) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(new JSONObject()
                        .put("status", "error")
                        .put("message", "roleId hoặc danh sách permission không hợp lệ").toString());
                return;
            }

            boolean allSucceeded = !permissionIds.isEmpty();
            for (Integer id : permissionIds) {
                if (id == null || id <= 0) {
                    continue;
                }
                boolean result = granted
                        ? rolePermissionDAO.grantPermission(roleId, id)
                        : rolePermissionDAO.revokePermission(roleId, id);
                if (!result && granted) {
                    allSucceeded = false;
                }
            }

            if (allSucceeded) {
                response.getWriter().write(new JSONObject()
                        .put("status", "success")
                        .put("message", granted ? "Đã cấp quyền cho role" : "Đã thu hồi quyền khỏi role")
                        .toString());
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.getWriter().write(new JSONObject()
                        .put("status", "error")
                        .put("message", "Không có thay đổi nào được áp dụng").toString());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Lỗi cập nhật phân quyền role", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write(new JSONObject()
                    .put("status", "error")
                    .put("message", "Không thể cập nhật quyền cho role").toString());
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Lỗi đọc dữ liệu JSON", e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(new JSONObject()
                    .put("status", "error")
                    .put("message", "Payload không hợp lệ").toString());
        }
    }

    private JSONObject readJsonRequest(HttpServletRequest request) throws IOException {
        StringBuilder sb = new StringBuilder();
        String line;
        try (var reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        if (sb.length() == 0) {
            return new JSONObject();
        }
        return new JSONObject(sb.toString());
    }

    private JSONArray buildPermissionArray(List<PermissionSummary> permissions, Set<Integer> assigned) {
        Map<String, List<PermissionSummary>> grouped = permissions.stream()
                .collect(Collectors.groupingBy(
                        p -> {
                            String category = p.getCategory();
                            return (category != null && !category.isBlank()) ? category : "Khác";
                        },
                        java.util.TreeMap::new,
                        Collectors.toList()));

        JSONArray categories = new JSONArray();
        grouped.entrySet().stream()
                .sorted(Map.Entry.comparingByKey(String.CASE_INSENSITIVE_ORDER))
                .forEach(entry -> {
                    String category = entry.getKey();
                    List<PermissionSummary> list = entry.getValue();
                    JSONArray ids = new JSONArray();
                    boolean grantedAll = true;

                    for (PermissionSummary ps : list) {
                        ids.put(ps.getPermissionId());
                        if (!assigned.contains(ps.getPermissionId())) {
                            grantedAll = false;
                        }
                    }

                    String description = list.stream()
                            .map(PermissionSummary::getDescription)
                            .filter(desc -> desc != null && !desc.isBlank())
                            .findFirst()
                            .orElse("Chức năng " + category);

                    categories.put(new JSONObject()
                            .put("category", category)
                            .put("permissionIds", ids)
                            .put("description", description)
                            .put("totalPermissions", list.size())
                            .put("granted", grantedAll));
                });
        return categories;
    }
}

