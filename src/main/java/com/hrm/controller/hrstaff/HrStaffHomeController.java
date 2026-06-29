package com.hrm.controller.hrstaff;

import com.hrm.dao.ContractDAO;
import com.hrm.dao.EmployeeDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@WebServlet(name = "HrStaffHomeController", urlPatterns = {"/hrstaff"})
public class HrStaffHomeController extends HttpServlet {

    private final ContractDAO contractDAO = new ContractDAO();
    private final EmployeeDAO employeeDAO = new EmployeeDAO();
    private static final String STATUS_PENDING_APPROVAL = "Pending_Approval";
    private static final String STATUS_ACTIVE = "Active";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int days = 30;
        try {
            String daysParam = request.getParameter("days");
            if (daysParam != null) {
                days = Math.max(1, Integer.parseInt(daysParam));
            }
        } catch (NumberFormatException ignored) {
        }

        // Get expiring contracts
        List<Map<String, Object>> expiringContracts = contractDAO.getExpiringWithinDays(days);
        request.setAttribute("expiringContracts", expiringContracts);
        request.setAttribute("expiringDays", days);

        // Get total employees count
        int totalEmployees = employeeDAO.getTotalEmployees();
        request.setAttribute("totalEmployees", totalEmployees);

        // Get pending contracts count (đang chờ duyệt)
        int pendingContractsCount = contractDAO.countContractsByStatus(STATUS_PENDING_APPROVAL);
        request.setAttribute("pendingContractsCount", pendingContractsCount);

        // Get approved contracts count (đã được duyệt) - Active status
        int approvedContractsCount = contractDAO.countContractsByStatus(STATUS_ACTIVE);
        request.setAttribute("approvedContractsCount", approvedContractsCount);

        request.getRequestDispatcher("/Views/HrStaff/HrStaffHome.jsp").forward(request, response);
    }
}


