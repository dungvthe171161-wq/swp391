package com.hrm.controller.hrstaff;

import com.hrm.dao.*;
import com.hrm.model.entity.Contract;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.hrm.util.PermissionUtil;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import com.hrm.dao.DBConnection;

/**
 * Controller for Payroll Calculation (API)
 * Handles GET /hrstaff/payroll/calculate - Calculate payroll data (returns JSON)
 * Includes Attendance data for automatic calculation
 * @author admin
 */
@WebServlet(name = "PayrollCalculateController", urlPatterns = {"/hrstaff/payroll/calculate"})
public class PayrollCalculateController extends HttpServlet {

    private static final String REQUIRED_PERMISSION = "VIEW_PAYROLLS";

    private final EmployeeAllowanceDAO employeeAllowanceDAO = new EmployeeAllowanceDAO();
    private final InsuranceRateDAO insuranceRateDAO = new InsuranceRateDAO();
    private final TaxRateDAO taxRateDAO = new TaxRateDAO();
    private final DependentDAO dependentDAO = new DependentDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!PermissionUtil.ensureRolePermissionJson(
                request,
                response,
                PermissionUtil.ROLE_HR_STAFF,
                REQUIRED_PERMISSION,
                "Khu vực này chỉ dành cho nhân viên nhân sự.",
                "Bạn không có quyền tính lương.")) {
            return;
        }
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            String employeeIdStr = request.getParameter("employeeId");
            String month = request.getParameter("month");

            if (employeeIdStr == null || month == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                PrintWriter out = response.getWriter();
                out.print("{\"error\":\"Thiếu tham số bắt buộc\"}");
                return;
            }

            int employeeId = Integer.parseInt(employeeIdStr);
            
            String[] monthParts = month.split("-");
            int year = Integer.parseInt(monthParts[0]);
            int monthNum = Integer.parseInt(monthParts[1]);
            
            BigDecimal baseSalary = BigDecimal.ZERO;
            try {
                System.out.println("PayrollCalculateController: Getting contract for employee " + employeeId + 
                    ", period " + month + " (year=" + year + ", month=" + monthNum + ")");
                
                Contract contract = getContractForPayPeriod(employeeId, year, monthNum);
                if (contract != null && contract.getBaseSalary() != null && 
                    contract.getBaseSalary().compareTo(BigDecimal.ZERO) > 0) {
                    baseSalary = contract.getBaseSalary();
                    System.out.println("PayrollCalculateController: ✓ Found contract ID " + contract.getContractId() + 
                        " for employee " + employeeId + " for period " + month + 
                        ", BaseSalary = " + baseSalary + 
                        ", Status = " + contract.getStatus() +
                        ", StartDate = " + contract.getStartDate() + 
                        ", EndDate = " + contract.getEndDate());
                } else {
                    System.out.println("PayrollCalculateController: ✗ No valid contract found for employee " + employeeId + 
                        " for period " + month + ". Trying fallback...");
                    Contract fallbackContract = getAnyContractWithBaseSalary(employeeId);
                    if (fallbackContract != null && fallbackContract.getBaseSalary() != null && 
                        fallbackContract.getBaseSalary().compareTo(BigDecimal.ZERO) > 0) {
                        baseSalary = fallbackContract.getBaseSalary();
                        System.out.println("PayrollCalculateController: ✓ Using fallback contract ID " + 
                            fallbackContract.getContractId() + " for employee " + employeeId + 
                            ", BaseSalary = " + baseSalary + 
                            ", Status = " + fallbackContract.getStatus() +
                            ", StartDate = " + fallbackContract.getStartDate() + 
                            ", EndDate = " + fallbackContract.getEndDate());
                        System.out.println("PayrollCalculateController: ⚠️ WARNING - Using contract that may not be valid for period " + month);
                    } else {
                        System.err.println("PayrollCalculateController: ✗ ERROR - No contract with BaseSalary > 0 found for employee " + employeeId);
                        System.err.println("PayrollCalculateController: Cannot calculate payroll without BaseSalary.");
                        System.err.println("PayrollCalculateController: Please ensure employee has a Contract with BaseSalary > 0");
                    }
                }
            } catch (Exception e) {
                System.err.println("PayrollCalculateController: ✗ EXCEPTION - Error getting contract for employee " + employeeId);
                System.err.println("PayrollCalculateController: Exception message: " + e.getMessage());
                e.printStackTrace();
            }
            
            System.out.println("PayrollCalculateController: Final BaseSalary = " + baseSalary + " for employee " + employeeId);

            // Get total allowance
            BigDecimal totalAllowance = employeeAllowanceDAO.getTotalAllowance(employeeId, month);
            System.out.println("PayrollCalculateController: Total allowance for employee " + employeeId + 
                ", month " + month + " = " + totalAllowance);

            // Get Attendance statistics (including ActualWorkingDays, PaidLeaveDays, OvertimeHours)
            Map<String, Object> attendanceStats = getAttendanceStatistics(employeeId, year, monthNum, baseSalary);
            
            // Calculate ActualBaseSalary and OTSalary using stored procedure logic
            BigDecimal actualWorkingDays = (BigDecimal) attendanceStats.get("actualWorkingDays");
            BigDecimal paidLeaveDays = (BigDecimal) attendanceStats.get("paidLeaveDays");
            BigDecimal overtimeHours = (BigDecimal) attendanceStats.get("totalOvertimeHours");
            
            // Calculate ActualBaseSalary = (ActualWorkingDays + PaidLeaveDays) × (BaseSalary / 26)
            BigDecimal actualBaseSalary = BigDecimal.ZERO;
            if (baseSalary != null && baseSalary.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal dailyRate = baseSalary.divide(new BigDecimal(26), 2, java.math.RoundingMode.HALF_UP);
                BigDecimal totalDays = (actualWorkingDays != null ? actualWorkingDays : BigDecimal.ZERO)
                    .add(paidLeaveDays != null ? paidLeaveDays : BigDecimal.ZERO);
                actualBaseSalary = dailyRate.multiply(totalDays);
            }
            
            // Calculate OTSalary = OvertimeHours × (BaseSalary / 208) × 1.5
            BigDecimal otSalary = BigDecimal.ZERO;
            if (baseSalary != null && baseSalary.compareTo(BigDecimal.ZERO) > 0 && 
                overtimeHours != null && overtimeHours.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal hourlyRate = baseSalary.divide(new BigDecimal(208), 2, java.math.RoundingMode.HALF_UP);
                otSalary = overtimeHours.multiply(hourlyRate).multiply(new BigDecimal("1.5"));
            }

            // Calculate insurance and tax using stored procedure logic
            // TaxableIncome = ActualBaseSalary + OTSalary + Allowance - (BHXH + BHYT + BHTN)
            Map<String, Object> insuranceAndTax = calculateInsuranceAndTax(employeeId, baseSalary, actualBaseSalary, otSalary, totalAllowance);
            
            // Get existing deductions (excluding auto-calculated ones)
            BigDecimal existingDeduction = getExistingDeductionExcludingAuto(employeeId, month);
            
            // Total deduction = Insurance + Tax + Existing deductions
            BigDecimal insuranceTotal = (BigDecimal) insuranceAndTax.get("insuranceTotal");
            BigDecimal tax = (BigDecimal) insuranceAndTax.get("tax");
            BigDecimal totalDeduction = insuranceTotal.add(tax).add(existingDeduction);
            
            // Calculate NetSalary = ActualBaseSalary + OTSalary + Allowance - TotalDeduction
            BigDecimal netSalary = actualBaseSalary.add(otSalary).add(totalAllowance).subtract(totalDeduction);
            if (netSalary.compareTo(BigDecimal.ZERO) < 0) {
                netSalary = BigDecimal.ZERO;
            }

            // Build response
            Map<String, Object> result = new HashMap<>();
            result.put("baseSalary", baseSalary); // Base salary from contract
            result.put("actualBaseSalary", actualBaseSalary); // Actual base salary calculated
            result.put("otSalary", otSalary); // Overtime salary
            result.put("totalAllowance", totalAllowance);
            result.put("totalDeduction", totalDeduction);
            result.put("netSalary", netSalary);
            result.put("insurance", insuranceAndTax);
            result.put("attendance", attendanceStats);

            // Convert to JSON manually (simple approach)
            StringBuilder json = new StringBuilder();
            json.append("{");
            json.append("\"baseSalary\":").append(formatBigDecimal(result.get("baseSalary")));
            json.append(",\"actualBaseSalary\":").append(formatBigDecimal(result.get("actualBaseSalary")));
            json.append(",\"otSalary\":").append(formatBigDecimal(result.get("otSalary")));
            json.append(",\"totalAllowance\":").append(formatBigDecimal(result.get("totalAllowance")));
            json.append(",\"totalDeduction\":").append(formatBigDecimal(result.get("totalDeduction")));
            json.append(",\"netSalary\":").append(formatBigDecimal(result.get("netSalary")));
            
            // Add insurance and tax details
            json.append(",\"insurance\":{");
            json.append("\"bhxh\":").append(formatBigDecimal(insuranceAndTax.get("bhxh")));
            json.append(",\"bhyt\":").append(formatBigDecimal(insuranceAndTax.get("bhyt")));
            json.append(",\"bhtn\":").append(formatBigDecimal(insuranceAndTax.get("bhtn")));
            json.append(",\"insuranceTotal\":").append(formatBigDecimal(insuranceAndTax.get("insuranceTotal")));
            json.append(",\"tax\":").append(formatBigDecimal(insuranceAndTax.get("tax")));
            json.append(",\"taxableIncome\":").append(formatBigDecimal(insuranceAndTax.get("taxableIncome")));
            json.append(",\"dependents\":").append(insuranceAndTax.get("dependents"));
            json.append("}");
            
            // Add attendance
            json.append(",\"attendance\":{");
            json.append("\"actualWorkingDays\":").append(formatBigDecimal(attendanceStats.get("actualWorkingDays")));
            json.append(",\"paidLeaveDays\":").append(formatBigDecimal(attendanceStats.get("paidLeaveDays")));
            json.append(",\"unpaidLeaveDays\":").append(formatBigDecimal(attendanceStats.get("unpaidLeaveDays")));
            json.append(",\"workDays\":").append(attendanceStats.get("workDays"));
            json.append(",\"lateCount\":").append(attendanceStats.get("lateCount"));
            json.append(",\"earlyLeaveCount\":").append(attendanceStats.get("earlyLeaveCount"));
            json.append(",\"totalOvertimeHours\":").append(formatBigDecimal(attendanceStats.get("totalOvertimeHours")));
            json.append(",\"calculatedUnpaidLeaveAmount\":").append(formatBigDecimal(attendanceStats.get("calculatedUnpaidLeaveAmount")));
            json.append(",\"calculatedLatePenalty\":").append(formatBigDecimal(attendanceStats.get("calculatedLatePenalty")));
            json.append(",\"calculatedOvertimeAmount\":").append(formatBigDecimal(attendanceStats.get("calculatedOvertimeAmount")));
            json.append("}");
            
            json.append("}");

            PrintWriter out = response.getWriter();
            out.print(json.toString());
            out.flush();
            
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            PrintWriter out = response.getWriter();
            out.print("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
    
    private Contract getContractForPayPeriod(int employeeId, int year, int month) {
        // Build date string for the first day of the month (YYYY-MM-01)
        String monthStr = String.format("%d-%02d-01", year, month);
        
        // SQL matches stored procedure exactly:
        // c.StartDate <= LAST_DAY(STR_TO_DATE(CONCAT(p_pay_period, '-01'), '%Y-%m-%d'))
        // AND (c.EndDate IS NULL OR c.EndDate >= STR_TO_DATE(CONCAT(p_pay_period, '-01'), '%Y-%m-%d'))
        String sql = """
            SELECT ContractID, EmployeeID, StartDate, EndDate, 
                   BaseSalary, Allowance, ContractType, Status, Notes
            FROM Contract
            WHERE EmployeeID = ?
              AND StartDate <= LAST_DAY(STR_TO_DATE(?, '%Y-%m-%d'))
              AND (EndDate IS NULL OR EndDate >= STR_TO_DATE(?, '%Y-%m-%d'))
              AND BaseSalary IS NOT NULL
              AND BaseSalary > 0
            ORDER BY StartDate DESC
            LIMIT 1
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            ps.setString(2, monthStr); // For LAST_DAY calculation
            ps.setString(3, monthStr); // For EndDate comparison
            
            System.out.println("PayrollCalculateController: Executing SQL query for employee " + employeeId + 
                ", monthStr = " + monthStr);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = new Contract();
                    contract.setContractId(rs.getInt("ContractID"));
                    contract.setEmployeeId(rs.getInt("EmployeeID"));
                    contract.setStartDate(rs.getDate("StartDate") != null ? 
                        rs.getDate("StartDate").toLocalDate() : null);
                    contract.setEndDate(rs.getDate("EndDate") != null ? 
                        rs.getDate("EndDate").toLocalDate() : null);
                    contract.setBaseSalary(rs.getBigDecimal("BaseSalary"));
                    contract.setAllowance(rs.getBigDecimal("Allowance"));
                    contract.setContractType(rs.getString("ContractType"));
                    String status = rs.getString("Status");
                    contract.setStatus(status);
                    contract.setNote(rs.getString("Notes"));
                    
                    System.out.println("PayrollCalculateController: ✓ Query returned contract ID " + contract.getContractId() + 
                        ", BaseSalary = " + contract.getBaseSalary() + 
                        ", Status = " + status +
                        ", StartDate = " + contract.getStartDate() + 
                        ", EndDate = " + contract.getEndDate());
                    return contract;
                } else {
                    System.out.println("PayrollCalculateController: ✗ Query returned no results for employee " + employeeId + 
                        " valid for period " + year + "-" + String.format("%02d", month));
                    // Debug: Check if employee has any contracts at all
                    debugEmployeeContracts(employeeId);
                }
            }
        } catch (Exception e) {
            System.err.println("PayrollCalculateController: ✗ EXCEPTION getting contract for pay period: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Debug method to check all contracts for an employee
     */
    private void debugEmployeeContracts(int employeeId) {
        String sql = """
            SELECT ContractID, EmployeeID, StartDate, EndDate, 
                   BaseSalary, Allowance, ContractType, Status, Notes
            FROM Contract
            WHERE EmployeeID = ?
            ORDER BY StartDate DESC
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            try (ResultSet rs = ps.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    count++;
                    System.out.println("PayrollCalculateController: Contract #" + count + 
                        " - ID: " + rs.getInt("ContractID") + 
                        ", BaseSalary: " + rs.getBigDecimal("BaseSalary") + 
                        ", Status: " + rs.getString("Status") + 
                        ", StartDate: " + rs.getDate("StartDate") + 
                        ", EndDate: " + rs.getDate("EndDate"));
                }
                if (count == 0) {
                    System.out.println("PayrollCalculateController: ⚠️ Employee " + employeeId + " has NO contracts in database");
                } else {
                    System.out.println("PayrollCalculateController: Found " + count + " contract(s) for employee " + employeeId);
                }
            }
        } catch (Exception e) {
            System.err.println("PayrollCalculateController: Error debugging contracts: " + e.getMessage());
        }
    }
    
    /**
     * Get any contract with BaseSalary > 0 for an employee (fallback method)
     * This is used when no contract is valid for the pay period
     */
    private Contract getAnyContractWithBaseSalary(int employeeId) {
        String sql = """
            SELECT ContractID, EmployeeID, StartDate, EndDate, 
                   BaseSalary, Allowance, ContractType, Status, Notes
            FROM Contract
            WHERE EmployeeID = ?
              AND BaseSalary IS NOT NULL
              AND BaseSalary > 0
            ORDER BY StartDate DESC
            LIMIT 1
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Contract contract = new Contract();
                    contract.setContractId(rs.getInt("ContractID"));
                    contract.setEmployeeId(rs.getInt("EmployeeID"));
                    contract.setStartDate(rs.getDate("StartDate") != null ? 
                        rs.getDate("StartDate").toLocalDate() : null);
                    contract.setEndDate(rs.getDate("EndDate") != null ? 
                        rs.getDate("EndDate").toLocalDate() : null);
                    contract.setBaseSalary(rs.getBigDecimal("BaseSalary"));
                    contract.setAllowance(rs.getBigDecimal("Allowance"));
                    contract.setContractType(rs.getString("ContractType"));
                    contract.setStatus(rs.getString("Status"));
                    contract.setNote(rs.getString("Notes"));
                    return contract;
                }
            }
        } catch (Exception e) {
            System.err.println("PayrollCalculateController: Error getting any contract with BaseSalary: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Get attendance statistics for an employee in a specific month
     * Query directly from Attendance and MailRequest tables
     */
    private Map<String, Object> getAttendanceStatistics(int employeeId, int year, int month, BigDecimal baseSalary) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("workDays", 0);
        stats.put("actualWorkingDays", BigDecimal.ZERO);
        stats.put("paidLeaveDays", BigDecimal.ZERO);
        stats.put("unpaidLeaveDays", BigDecimal.ZERO);
        stats.put("lateCount", 0);
        stats.put("earlyLeaveCount", 0);
        stats.put("totalOvertimeHours", BigDecimal.ZERO);
        stats.put("calculatedUnpaidLeaveAmount", BigDecimal.ZERO);
        stats.put("calculatedLatePenalty", BigDecimal.ZERO);
        stats.put("calculatedOvertimeAmount", BigDecimal.ZERO);
        
        try (Connection con = DBConnection.getConnection()) {
            // Calculate ActualWorkingDays directly from Attendance table
            // Logic: SUM(WorkingHours / 8) where WorkingHours >= 8 counts as 1 day, otherwise WorkingHours/8
            String sqlActualWorkingDays = """
                SELECT COALESCE(
                    SUM(
                        CASE
                            WHEN WorkingHours >= 8 THEN 1
                            WHEN WorkingHours > 0 THEN WorkingHours / 8
                            ELSE 0
                        END
                    ), 0) as actualWorkingDays
                FROM Attendance
                WHERE EmployeeID = ?
                    AND YEAR(Date) = ?
                    AND MONTH(Date) = ?
            """;
            try (PreparedStatement ps = con.prepareStatement(sqlActualWorkingDays)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, year);
                ps.setInt(3, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal actualWorkingDays = rs.getBigDecimal("actualWorkingDays");
                        if (actualWorkingDays == null) actualWorkingDays = BigDecimal.ZERO;
                        stats.put("actualWorkingDays", actualWorkingDays);
                        System.out.println("PayrollCalculateController: ActualWorkingDays for employee " + employeeId + 
                            ", " + year + "-" + month + " = " + actualWorkingDays);
                    }
                }
            } catch (Exception e) {
                System.err.println("PayrollCalculateController: Error calculating ActualWorkingDays: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Calculate PaidLeaveDays directly from MailRequest table
            // Paid leave: Annual, Sick, Maternity with Status = 'Approved'
            String monthStartStr = String.format("%d-%02d-01", year, month);
            
            String sqlPaidLeaveDays = """
                SELECT COALESCE(
                    SUM(
                        DATEDIFF(
                            LEAST(LAST_DAY(STR_TO_DATE(?, '%Y-%m-%d')), EndDate),
                            GREATEST(STR_TO_DATE(?, '%Y-%m-%d'), StartDate)
                        ) + 1
                    ), 0) as paidLeaveDays
                FROM MailRequest
                WHERE EmployeeID = ?
                    AND Status = 'Approved'
                    AND LeaveType IN ('Annual', 'Sick', 'Maternity')
                    AND StartDate <= LAST_DAY(STR_TO_DATE(?, '%Y-%m-%d'))
                    AND EndDate >= STR_TO_DATE(?, '%Y-%m-%d')
            """;
            try (PreparedStatement ps = con.prepareStatement(sqlPaidLeaveDays)) {
                ps.setString(1, monthStartStr);
                ps.setString(2, monthStartStr);
                ps.setInt(3, employeeId);
                ps.setString(4, monthStartStr);
                ps.setString(5, monthStartStr);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal paidLeaveDays = rs.getBigDecimal("paidLeaveDays");
                        if (paidLeaveDays == null) paidLeaveDays = BigDecimal.ZERO;
                        stats.put("paidLeaveDays", paidLeaveDays);
                        System.out.println("PayrollCalculateController: PaidLeaveDays for employee " + employeeId + 
                            ", " + year + "-" + month + " = " + paidLeaveDays);
                    }
                }
            } catch (Exception e) {
                System.err.println("PayrollCalculateController: Error calculating PaidLeaveDays: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Calculate UnpaidLeaveDays directly from MailRequest table
            // Unpaid leave: LeaveType = 'Unpaid' with Status = 'Approved'
            String sqlUnpaidLeaveDays = """
                SELECT COALESCE(
                    SUM(
                        DATEDIFF(
                            LEAST(LAST_DAY(STR_TO_DATE(?, '%Y-%m-%d')), EndDate),
                            GREATEST(STR_TO_DATE(?, '%Y-%m-%d'), StartDate)
                        ) + 1
                    ), 0) as unpaidLeaveDays
                FROM MailRequest
                WHERE EmployeeID = ?
                    AND Status = 'Approved'
                    AND LeaveType = 'Unpaid'
                    AND StartDate <= LAST_DAY(STR_TO_DATE(?, '%Y-%m-%d'))
                    AND EndDate >= STR_TO_DATE(?, '%Y-%m-%d')
            """;
            try (PreparedStatement ps = con.prepareStatement(sqlUnpaidLeaveDays)) {
                ps.setString(1, monthStartStr);
                ps.setString(2, monthStartStr);
                ps.setInt(3, employeeId);
                ps.setString(4, monthStartStr);
                ps.setString(5, monthStartStr);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal unpaidLeaveDays = rs.getBigDecimal("unpaidLeaveDays");
                        if (unpaidLeaveDays == null) unpaidLeaveDays = BigDecimal.ZERO;
                        stats.put("unpaidLeaveDays", unpaidLeaveDays);
                        System.out.println("PayrollCalculateController: UnpaidLeaveDays for employee " + employeeId + 
                            ", " + year + "-" + month + " = " + unpaidLeaveDays);
                        // Calculate unpaid leave amount
                        if (unpaidLeaveDays.compareTo(BigDecimal.ZERO) > 0 && baseSalary != null && baseSalary.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal dailyRate = baseSalary.divide(new BigDecimal(26), 2, java.math.RoundingMode.HALF_UP);
                            BigDecimal unpaidLeaveAmount = unpaidLeaveDays.multiply(dailyRate);
                            stats.put("calculatedUnpaidLeaveAmount", unpaidLeaveAmount);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("PayrollCalculateController: Error calculating UnpaidLeaveDays: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Count work days (days with attendance records) for display
            String sqlWorkDays = """
                SELECT COUNT(DISTINCT Date) as workDays
                FROM Attendance
                WHERE EmployeeID = ? 
                    AND YEAR(Date) = ? 
                    AND MONTH(Date) = ?
            """;
            try (PreparedStatement ps = con.prepareStatement(sqlWorkDays)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, year);
                ps.setInt(3, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int workDays = rs.getInt("workDays");
                        stats.put("workDays", workDays);
                        System.out.println("PayrollCalculateController: WorkDays (count) for employee " + employeeId + 
                            ", " + year + "-" + month + " = " + workDays);
                    }
                }
            } catch (Exception e) {
                System.err.println("PayrollCalculateController: Error counting workDays: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Count late arrivals (CheckIn > 08:00:00)
            String sqlLate = """
                SELECT COUNT(*) as lateCount
                FROM Attendance
                WHERE EmployeeID = ? 
                    AND YEAR(Date) = ? 
                    AND MONTH(Date) = ?
                    AND CheckIn > '08:00:00'
            """;
            try (PreparedStatement ps = con.prepareStatement(sqlLate)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, year);
                ps.setInt(3, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int lateCount = rs.getInt("lateCount");
                        stats.put("lateCount", lateCount);
                        System.out.println("PayrollCalculateController: LateCount for employee " + employeeId + 
                            ", " + year + "-" + month + " = " + lateCount);
                        // Calculate late penalty (100,000 VNĐ per occurrence)
                        if (lateCount > 0) {
                            BigDecimal latePenalty = new BigDecimal(lateCount).multiply(new BigDecimal(100000));
                            stats.put("calculatedLatePenalty", latePenalty);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("PayrollCalculateController: Error counting late arrivals: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Count early leave (CheckOut < 17:00:00)
            String sqlEarly = """
                SELECT COUNT(*) as earlyCount
                FROM Attendance
                WHERE EmployeeID = ? 
                    AND YEAR(Date) = ? 
                    AND MONTH(Date) = ?
                    AND CheckOut < '17:00:00'
            """;
            try (PreparedStatement ps = con.prepareStatement(sqlEarly)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, year);
                ps.setInt(3, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int earlyCount = rs.getInt("earlyCount");
                        stats.put("earlyLeaveCount", earlyCount);
                        System.out.println("PayrollCalculateController: EarlyLeaveCount for employee " + employeeId + 
                            ", " + year + "-" + month + " = " + earlyCount);
                    }
                }
            } catch (Exception e) {
                System.err.println("PayrollCalculateController: Error counting early leave: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Sum overtime hours
            String sqlOvertime = """
                SELECT COALESCE(SUM(OvertimeHours), 0) as totalOvertime
                FROM Attendance
                WHERE EmployeeID = ? 
                    AND YEAR(Date) = ? 
                    AND MONTH(Date) = ?
            """;
            try (PreparedStatement ps = con.prepareStatement(sqlOvertime)) {
                ps.setInt(1, employeeId);
                ps.setInt(2, year);
                ps.setInt(3, month);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        BigDecimal totalOvertime = rs.getBigDecimal("totalOvertime");
                        if (totalOvertime == null) totalOvertime = BigDecimal.ZERO;
                        stats.put("totalOvertimeHours", totalOvertime);
                        System.out.println("PayrollCalculateController: TotalOvertimeHours for employee " + employeeId + 
                            ", " + year + "-" + month + " = " + totalOvertime);
                        // Calculate overtime amount: OvertimeHours × (BaseSalary / 208) × 1.5
                        if (totalOvertime.compareTo(BigDecimal.ZERO) > 0 && baseSalary != null && baseSalary.compareTo(BigDecimal.ZERO) > 0) {
                            BigDecimal hourlyRate = baseSalary.divide(new BigDecimal(208), 2, java.math.RoundingMode.HALF_UP);
                            BigDecimal overtimeAmount = totalOvertime.multiply(hourlyRate).multiply(new BigDecimal("1.5"));
                            stats.put("calculatedOvertimeAmount", overtimeAmount);
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("PayrollCalculateController: Error calculating overtime: " + e.getMessage());
                e.printStackTrace();
            }
            
        } catch (Exception e) {
            System.err.println("PayrollCalculateController: Error in getAttendanceStatistics: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("PayrollCalculateController: Final attendance stats for employee " + employeeId + 
            ", " + year + "-" + month + ": " + stats);
        return stats;
    }
    
    private Map<String, Object> calculateInsuranceAndTax(int employeeId, BigDecimal baseSalary, 
            BigDecimal actualBaseSalary, BigDecimal otSalary, BigDecimal allowance) {
        Map<String, Object> result = new HashMap<>();
        
        if (baseSalary == null || baseSalary.compareTo(BigDecimal.ZERO) <= 0) {
            result.put("bhxh", BigDecimal.ZERO);
            result.put("bhyt", BigDecimal.ZERO);
            result.put("bhtn", BigDecimal.ZERO);
            result.put("insuranceTotal", BigDecimal.ZERO);
            result.put("tax", BigDecimal.ZERO);
            result.put("taxableIncome", BigDecimal.ZERO);
            result.put("dependents", 0);
            return result;
        }
        
        // Get insurance rates (from InsuranceRate table)
        BigDecimal bhxhRate = insuranceRateDAO.getCurrentEmployeeRate("BHXH");
        BigDecimal bhytRate = insuranceRateDAO.getCurrentEmployeeRate("BHYT");
        BigDecimal bhtnRate = insuranceRateDAO.getCurrentEmployeeRate("BHTN");
        
        // If rates not found, use default values
        if (bhxhRate == null || bhxhRate.compareTo(BigDecimal.ZERO) == 0) {
            bhxhRate = new BigDecimal("8.00");
        }
        if (bhytRate == null || bhytRate.compareTo(BigDecimal.ZERO) == 0) {
            bhytRate = new BigDecimal("1.50");
        }
        if (bhtnRate == null || bhtnRate.compareTo(BigDecimal.ZERO) == 0) {
            bhtnRate = new BigDecimal("1.00");
        }
        
        // Calculate insurance amounts from BaseSalary (contract base salary)
        BigDecimal bhxhAmount = baseSalary.multiply(bhxhRate)
                .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal bhytAmount = baseSalary.multiply(bhytRate)
                .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal bhtnAmount = baseSalary.multiply(bhtnRate)
                .divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
        
        BigDecimal insuranceTotal = bhxhAmount.add(bhytAmount).add(bhtnAmount);
        
        // Count dependents (from Dependent table)
        int dependents = dependentDAO.countDependents(employeeId);
        
        // Calculate taxable income
        // TaxableIncome = ActualBaseSalary + OTSalary + Allowance - (BHXH + BHYT + BHTN)
        BigDecimal taxableIncome = (actualBaseSalary != null ? actualBaseSalary : BigDecimal.ZERO)
                .add(otSalary != null ? otSalary : BigDecimal.ZERO)
                .add(allowance != null ? allowance : BigDecimal.ZERO)
                .subtract(insuranceTotal);
        
        // Calculate tax base income after relief
        // TaxBaseIncome = TaxableIncome - (11000000 + dependents * 4400000)
        BigDecimal personalDeduction = new BigDecimal("11000000"); // Giảm trừ bản thân
        BigDecimal dependentDeduction = new BigDecimal(dependents).multiply(new BigDecimal("4400000")); // Giảm trừ người phụ thuộc
        BigDecimal totalRelief = personalDeduction.add(dependentDeduction);
        BigDecimal taxBaseIncome = taxableIncome.subtract(totalRelief);
        
        // Tax base income cannot be negative
        if (taxBaseIncome.compareTo(BigDecimal.ZERO) < 0) {
            taxBaseIncome = BigDecimal.ZERO;
        }
        
        // Calculate tax (from TaxRate table) based on taxBaseIncome
        BigDecimal tax = taxRateDAO.calculateTax(taxBaseIncome);
        
        result.put("bhxh", bhxhAmount);
        result.put("bhyt", bhytAmount);
        result.put("bhtn", bhtnAmount);
        result.put("insuranceTotal", insuranceTotal);
        result.put("tax", tax);
        result.put("taxableIncome", taxableIncome); // Taxable income before relief
        result.put("taxBaseIncome", taxBaseIncome); // Tax base income after relief
        result.put("dependents", dependents);
        
        return result;
    }
    
    /**
     * Get existing deductions excluding auto-calculated ones (insurance and tax)
     * These are manually entered deductions like unpaid leave, late penalty, etc.
     */
    private BigDecimal getExistingDeductionExcludingAuto(int employeeId, String month) {
        // Get deduction type IDs for auto-calculated deductions
        DeductionTypeDAO deductionTypeDAO = new DeductionTypeDAO();
        int socialInsuranceId = deductionTypeDAO.getIdByName("Social Insurance");
        int healthInsuranceId = deductionTypeDAO.getIdByName("Health Insurance");
        int unemploymentInsuranceId = deductionTypeDAO.getIdByName("Unemployment Insurance");
        int personalIncomeTaxId = deductionTypeDAO.getIdByName("Personal Income Tax");
        
        String sql = """
            SELECT COALESCE(SUM(Amount), 0) as total
            FROM EmployeeDeduction
            WHERE EmployeeID = ? 
                AND Month = ?
                AND DeductionTypeID NOT IN (?, ?, ?, ?)
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, employeeId);
            ps.setString(2, month);
            ps.setInt(3, socialInsuranceId > 0 ? socialInsuranceId : -1);
            ps.setInt(4, healthInsuranceId > 0 ? healthInsuranceId : -1);
            ps.setInt(5, unemploymentInsuranceId > 0 ? unemploymentInsuranceId : -1);
            ps.setInt(6, personalIncomeTaxId > 0 ? personalIncomeTaxId : -1);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Format BigDecimal for JSON output
     */
    private double formatBigDecimal(Object value) {
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).doubleValue();
        } else if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }
}

