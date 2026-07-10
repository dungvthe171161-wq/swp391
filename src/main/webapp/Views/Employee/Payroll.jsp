<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - B&#7843;ng l&#432;&#417;ng</title>
    <%@ include file="_EmployeeStyles.jspf" %>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <div class="content">
            <h1 class="page-title">B&#7843;ng l&#432;&#417;ng</h1>
            <p class="page-note">Nh&#226;n vi&#234;n ch&#7881; xem &#273;&#432;&#7907;c payroll thu&#7897;c EmployeeID c&#7911;a m&#236;nh.</p>

            <c:if test="${not empty employeeError}">
                <div class="alert error">${employeeError}</div>
            </c:if>

            <c:if test="${not empty payrollDetails}">
                <section class="panel" style="margin-bottom:22px;">
                    <div class="panel-inner">
                        <h2 class="page-title" style="font-size:22px;">Chi ti&#7871;t phi&#7871;u l&#432;&#417;ng ${payrollDetails.payPeriod}</h2>
                        <div class="grid-2">
                            <div class="stat-row">
                                <div class="stat"><span>L&#432;&#417;ng c&#417; b&#7843;n</span><strong>${payrollDetails.baseSalary}</strong></div>
                                <div class="stat"><span>Ph&#7909; c&#7845;p</span><strong>${payrollDetails.allowance}</strong></div>
                                <div class="stat"><span>Th&#432;&#7903;ng</span><strong>${payrollDetails.bonus}</strong></div>
                                <div class="stat"><span>Kh&#7845;u tr&#7915;</span><strong>${payrollDetails.deduction}</strong></div>
                                <div class="stat"><span>Th&#7921;c nh&#7853;n</span><strong>${payrollDetails.netSalary}</strong></div>
                            </div>
                            <div class="stat-row">
                                <div class="stat"><span>Ng&#224;y c&#244;ng th&#7921;c t&#7871;</span><strong>${payrollDetails.audit.actualWorkingDays}</strong></div>
                                <div class="stat"><span>Ngh&#7881; c&#243; l&#432;&#417;ng</span><strong>${payrollDetails.audit.paidLeaveDays}</strong></div>
                                <div class="stat"><span>Ngh&#7881; kh&#244;ng l&#432;&#417;ng</span><strong>${payrollDetails.audit.unpaidLeaveDays}</strong></div>
                                <div class="stat"><span>T&#259;ng ca</span><strong>${payrollDetails.audit.overtimeHours}</strong></div>
                                <div class="stat"><span>BHXH/BHYT/BHTN</span><strong>${payrollDetails.audit.bhxh} / ${payrollDetails.audit.bhyt} / ${payrollDetails.audit.bhtn}</strong></div>
                                <div class="stat"><span>Thu&#7871; TNCN</span><strong>${payrollDetails.audit.personalTax}</strong></div>
                            </div>
                        </div>
                    </div>
                </section>
            </c:if>

            <section class="panel">
                <div class="panel-inner">
                    <table class="table">
                        <thead>
                            <tr>
                                <th>K&#7923;</th>
                                <th>L&#432;&#417;ng c&#417; b&#7843;n</th>
                                <th>Ph&#7909; c&#7845;p</th>
                                <th>Th&#432;&#7903;ng</th>
                                <th>Kh&#7845;u tr&#7915;</th>
                                <th>Th&#7921;c nh&#7853;n</th>
                                <th>Tr&#7841;ng th&#225;i</th>
                                <th>Chi ti&#7871;t</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="payroll" items="${payrolls}">
                                <tr>
                                    <td>${payroll.payPeriod}</td>
                                    <td>${payroll.baseSalary}</td>
                                    <td>${payroll.allowance}</td>
                                    <td>${payroll.bonus}</td>
                                    <td>${payroll.deduction}</td>
                                    <td><strong>${payroll.netSalary}</strong></td>
                                    <td>${payroll.status}</td>
                                    <td><a href="${pageContext.request.contextPath}/employee/payroll?payrollId=${payroll.payrollId}">Xem</a></td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty payrolls}">
                                <tr><td colspan="8">Ch&#432;a c&#243; d&#7919; li&#7879;u b&#7843;ng l&#432;&#417;ng.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
</body>
</html>
