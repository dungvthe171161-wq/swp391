<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Ch&#7845;m c&#244;ng</title>
    <%@ include file="_EmployeeStyles.jspf" %>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <div class="content">
            <h1 class="page-title">Ch&#7845;m c&#244;ng</h1>
            <p class="page-note">V&#224;o ca / ra ca &#273;&#432;&#7907;c l&#432;u trong b&#7843;ng Attendance v&#224; d&#249;ng cho t&#237;nh l&#432;&#417;ng.</p>

            <c:if test="${not empty employeeSuccess}">
                <div class="alert success">${employeeSuccess}</div>
            </c:if>
            <c:if test="${not empty employeeError}">
                <div class="alert error">${employeeError}</div>
            </c:if>

            <div class="grid-2">
                <section class="panel">
                    <div class="panel-inner">
                        <h2 class="page-title" style="font-size:22px;">H&#244;m nay</h2>
                        <div class="stat-row">
                            <div class="stat"><span>Tr&#7841;ng th&#225;i</span><strong>
                                <c:choose>
                                    <c:when test="${todayAttendanceStatus eq 'ChuaVaoCa'}">Ch&#432;a v&#224;o ca</c:when>
                                    <c:when test="${todayAttendanceStatus eq 'DaVaoCa'}">&#272;&#227; v&#224;o ca, ch&#432;a ra ca</c:when>
                                    <c:when test="${todayAttendanceStatus eq 'DaRaCa'}">&#272;&#227; ra ca</c:when>
                                    <c:otherwise>${todayAttendanceStatus}</c:otherwise>
                                </c:choose>
                            </strong></div>
                            <div class="stat"><span>Ng&#224;y</span><strong>${todayAttendance.date}</strong></div>
                            <div class="stat"><span>V&#224;o ca</span><strong>${todayAttendance.checkIn}</strong></div>
                            <div class="stat"><span>Ra ca</span><strong>${todayAttendance.checkOut}</strong></div>
                            <div class="stat"><span>Gi&#7901; c&#244;ng</span><strong>${todayAttendance.workingHours}</strong></div>
                            <div class="stat"><span>T&#259;ng ca</span><strong>${todayAttendance.overtimeHours}</strong></div>
                        </div>
                        <div class="button-row" style="margin-top:18px;">
                            <form method="post" action="${pageContext.request.contextPath}/employee/attendance">
                                <input type="hidden" name="action" value="checkIn">
                                <c:choose>
                                    <c:when test="${todayAttendanceStatus eq 'ChuaVaoCa'}">
                                        <button class="primary-button" type="submit">
                                            <i class="fa-solid fa-right-to-bracket"></i> V&#224;o ca
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="primary-button" type="button" disabled>
                                            <i class="fa-solid fa-right-to-bracket"></i> V&#224;o ca
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/employee/attendance">
                                <input type="hidden" name="action" value="checkOut">
                                <c:choose>
                                    <c:when test="${todayAttendanceStatus eq 'DaVaoCa'}">
                                        <button class="secondary-button" type="submit">
                                            <i class="fa-solid fa-right-from-bracket"></i> Ra ca
                                        </button>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="secondary-button" type="button" disabled>
                                            <i class="fa-solid fa-right-from-bracket"></i> Ra ca
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </form>
                        </div>
                    </div>
                </section>

                <section class="panel">
                    <div class="panel-inner">
                        <h2 class="page-title" style="font-size:22px;">T&#7893;ng h&#7907;p th&#225;ng n&#224;y</h2>
                        <div class="stat-row">
                            <div class="stat"><span>Ng&#224;y c&#244;ng</span><strong>${attendanceSummary.workedDays}</strong></div>
                            <div class="stat"><span>T&#7893;ng gi&#7901;</span><strong>${attendanceSummary.totalWorkingHours}h</strong></div>
                            <div class="stat"><span>T&#259;ng ca</span><strong>${attendanceSummary.totalOvertimeHours}h</strong></div>
                            <div class="stat"><span>&#272;i mu&#7897;n</span><strong>${attendanceSummary.lateCount}</strong></div>
                            <div class="stat"><span>V&#7873; s&#7899;m</span><strong>${attendanceSummary.earlyLeaveCount}</strong></div>
                        </div>
                    </div>
                </section>
            </div>

            <section class="panel" style="margin-top:22px;">
                <div class="panel-inner">
                    <h2 class="page-title" style="font-size:22px;">L&#7883;ch s&#7917; v&#224;o ca / ra ca</h2>
                    <table class="table">
                        <thead>
                            <tr>
                                <th>Ng&#224;y</th>
                                <th>V&#224;o ca</th>
                                <th>Ra ca</th>
                                <th>Gi&#7901; c&#244;ng</th>
                                <th>T&#259;ng ca</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="item" items="${recentAttendances}">
                                <tr>
                                    <td>${item.date}</td>
                                    <td>${item.checkIn}</td>
                                    <td>${item.checkOut}</td>
                                    <td>${item.workingHours}</td>
                                    <td>${item.overtimeHours}</td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty recentAttendances}">
                                <tr><td colspan="5">Ch&#432;a c&#243; d&#7919; li&#7879;u ch&#7845;m c&#244;ng.</td></tr>
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
