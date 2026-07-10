<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - &#272;&#417;n ngh&#7881; ph&#233;p</title>
    <%@ include file="_EmployeeStyles.jspf" %>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <div class="content">
            <h1 class="page-title">&#272;&#417;n ngh&#7881; ph&#233;p</h1>
            <p class="page-note">&#272;&#417;n &#273;&#432;&#7907;c l&#432;u v&#224;o b&#7843;ng MailRequest &#273;&#7875; tr&#432;&#7903;ng ph&#242;ng duy&#7879;t v&#224; payroll t&#237;nh ng&#224;y ngh&#7881;.</p>

            <c:if test="${not empty employeeSuccess}">
                <div class="alert success">${employeeSuccess}</div>
            </c:if>
            <c:if test="${not empty employeeError}">
                <div class="alert error">${employeeError}</div>
            </c:if>

            <div class="grid-2">
                <section class="panel">
                    <div class="panel-inner">
                        <h2 class="page-title" style="font-size:22px;">T&#7841;o &#273;&#417;n m&#7899;i</h2>
                        <p class="page-note">S&#7889; bu&#7893;i ph&#233;p c&#242;n l&#7841;i: ${leaveRemainingSessions}/${leaveTotalSessions}</p>
                        <form method="post" action="${pageContext.request.contextPath}/employee/leaves">
                            <div class="field">
                                <label>Lo&#7841;i ngh&#7881;</label>
                                <select name="leaveType" required>
                                    <option value="Annual">Ngh&#7881; ph&#233;p n&#259;m</option>
                                    <option value="Sick">Ngh&#7881; &#7889;m</option>
                                    <option value="Maternity">Ngh&#7881; thai s&#7843;n</option>
                                    <option value="Unpaid">Ngh&#7881; kh&#244;ng l&#432;&#417;ng</option>
                                    <option value="Other">Kh&#225;c</option>
                                </select>
                            </div>
                            <div class="field">
                                <label>Chi ti&#7871;t ngh&#7881;</label>
                                <select name="leaveDetail" required>
                                    <option value="FullDay">Ngh&#7881; c&#7843; ng&#224;y</option>
                                    <option value="Morning">Ngh&#7881; bu&#7893;i s&#225;ng</option>
                                    <option value="Afternoon">Ngh&#7881; bu&#7893;i chi&#7873;u</option>
                                </select>
                            </div>
                            <div class="field">
                                <label>T&#7915; ng&#224;y</label>
                                <input type="date" name="startDate" min="<%= java.time.LocalDate.now() %>" required>
                            </div>
                            <div class="field">
                                <label>&#272;&#7871;n ng&#224;y</label>
                                <input type="date" name="endDate" min="<%= java.time.LocalDate.now() %>" required>
                            </div>
                            <div class="field">
                                <label>B&#224;n giao cho (email)</label>
                                <input type="email" name="handoverTo" placeholder="Email ng&#432;&#7901;i nh&#7853;n b&#224;n giao" required>
                            </div>
                            <div class="field">
                                <label>N&#7897;i dung b&#224;n giao</label>
                                <textarea name="handoverWork" rows="3" placeholder="C&#244;ng vi&#7879;c, t&#224;i li&#7879;u, deadline c&#7847;n b&#224;n giao" required></textarea>
                            </div>
                            <div class="field">
                                <label>L&#253; do</label>
                                <textarea name="reason" rows="4" required></textarea>
                            </div>
                            <button class="primary-button" type="submit">G&#7917;i &#273;&#417;n</button>
                        </form>
                    </div>
                </section>

                <section class="panel">
                    <div class="panel-inner">
                        <h2 class="page-title" style="font-size:22px;">&#272;&#417;n c&#7911;a t&#244;i</h2>
                        <table class="table">
                            <thead>
                                <tr>
                                    <th>Lo&#7841;i</th>
                                    <th>T&#7915;</th>
                                    <th>&#272;&#7871;n</th>
                                    <th>Chi ti&#7871;t</th>
                                    <th>Tr&#7841;ng th&#225;i</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="leave" items="${leaveRequests}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${leave.leaveType eq 'Annual'}">Ngh&#7881; ph&#233;p n&#259;m</c:when>
                                                <c:when test="${leave.leaveType eq 'Sick'}">Ngh&#7881; &#7889;m</c:when>
                                                <c:when test="${leave.leaveType eq 'Maternity'}">Ngh&#7881; thai s&#7843;n</c:when>
                                                <c:when test="${leave.leaveType eq 'Unpaid'}">Ngh&#7881; kh&#244;ng l&#432;&#417;ng</c:when>
                                                <c:otherwise>Kh&#225;c</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td>${leave.startDate}</td>
                                        <td>${leave.endDate}</td>
                                        <td><pre style="white-space:pre-wrap;margin:0;font:inherit;">${leave.reason}</pre></td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${leave.status eq 'Pending'}">Ch&#7901; duy&#7879;t</c:when>
                                                <c:when test="${leave.status eq 'Approved'}">&#272;&#227; duy&#7879;t</c:when>
                                                <c:when test="${leave.status eq 'Rejected'}">T&#7915; ch&#7889;i</c:when>
                                                <c:otherwise>${leave.status}</c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                <c:if test="${empty leaveRequests}">
                                    <tr><td colspan="5">Ch&#432;a c&#243; &#273;&#417;n ngh&#7881; ph&#233;p.</td></tr>
                                </c:if>
                            </tbody>
                        </table>
                    </div>
                </section>
            </div>
        </div>
    </main>
</div>
<script>
    const startDateInput = document.querySelector('input[name="startDate"]');
    const endDateInput = document.querySelector('input[name="endDate"]');
    if (startDateInput && endDateInput) {
        startDateInput.addEventListener('change', () => {
            endDateInput.min = startDateInput.value || endDateInput.min;
            if (endDateInput.value && startDateInput.value && endDateInput.value < startDateInput.value) {
                endDateInput.value = startDateInput.value;
            }
        });
    }
</script>
</body>
</html>
