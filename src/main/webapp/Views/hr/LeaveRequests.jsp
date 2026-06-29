<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Leave Approval</title>
    <style>
        body { margin:0; font-family: Inter, "Segoe UI", Arial, sans-serif; background:#f7f4ee; color:#1b1c19; }
        .wrap { width:min(1180px, 100%); margin:0 auto; padding:32px; }
        .top { display:flex; justify-content:space-between; gap:16px; align-items:center; margin-bottom:20px; }
        h1 { margin:0; color:#00482f; }
        .panel { background:#fff; border:1px solid #d8d1c7; border-radius:16px; box-shadow:0 14px 32px rgba(30,57,50,.08); overflow:hidden; }
        .inner { padding:22px; }
        .table { width:100%; border-collapse:collapse; }
        .table th,.table td { padding:12px; border-bottom:1px solid #e7e1d8; text-align:left; vertical-align:top; }
        .table th { color:#00482f; font-size:12px; text-transform:uppercase; }
        .btn { min-height:38px; border:0; border-radius:10px; padding:0 14px; color:#fff; font-weight:800; cursor:pointer; }
        .approve { background:#00754a; }
        .reject { background:#c82014; }
        .link { color:#00754a; font-weight:800; text-decoration:none; margin-right:10px; }
        .alert { margin-bottom:16px; padding:12px 14px; border-radius:12px; font-weight:800; }
        .success { background:#e3f5eb; color:#00482f; }
        .error { background:#fdebea; color:#c82014; }
    </style>
</head>
<body>
<div class="wrap">
    <div class="top">
        <div>
            <h1>Duy&#7879;t &#273;&#417;n ngh&#7881; ph&#233;p</h1>
            <p>Duy&#7879;t/t&#7915; ch&#7889;i s&#7869; c&#7853;p nh&#7853;t MailRequest. B&#7843;ng l&#432;&#417;ng d&#249;ng &#273;&#417;n &#273;&#227; duy&#7879;t &#273;&#7875; t&#237;nh ng&#224;y ngh&#7881;.</p>
        </div>
        <div>
            <a class="link" href="${pageContext.request.contextPath}/hr/leaves?status=Pending">Ch&#7901; duy&#7879;t</a>
            <a class="link" href="${pageContext.request.contextPath}/hr/leaves?status=Approved">&#272;&#227; duy&#7879;t</a>
            <a class="link" href="${pageContext.request.contextPath}/hr/leaves?status=Rejected">T&#7915; ch&#7889;i</a>
            <a class="link" href="${pageContext.request.contextPath}/hr/leaves?status=All">T&#7845;t c&#7843;</a>
        </div>
    </div>

    <c:if test="${not empty hrLeaveSuccess}">
        <div class="alert success">${hrLeaveSuccess}</div>
    </c:if>
    <c:if test="${not empty hrLeaveError}">
        <div class="alert error">${hrLeaveError}</div>
    </c:if>

    <section class="panel">
        <div class="inner">
            <table class="table">
                <thead>
                    <tr>
                        <th>Nh&#226;n vi&#234;n</th>
                        <th>Lo&#7841;i</th>
                        <th>T&#7915; ng&#224;y</th>
                        <th>&#272;&#7871;n ng&#224;y</th>
                        <th>L&#253; do</th>
                        <th>Tr&#7841;ng th&#225;i</th>
                        <th>Thao t&#225;c</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${leaveRequests}">
                        <tr>
                            <td><strong>${item.employeeName}</strong><br>${item.employeeEmail}</td>
                            <td>${item.leaveType}</td>
                            <td>${item.startDate}</td>
                            <td>${item.endDate}</td>
                            <td>${item.reason}</td>
                            <td>${item.status}</td>
                            <td>
                                <c:if test="${item.status eq 'Pending'}">
                                    <form method="post" action="${pageContext.request.contextPath}/hr/leaves" style="display:inline-flex; gap:8px;">
                                        <input type="hidden" name="requestId" value="${item.requestId}">
                                        <button class="btn approve" type="submit" name="decision" value="Approved">Duy&#7879;t</button>
                                        <button class="btn reject" type="submit" name="decision" value="Rejected">T&#7915; ch&#7889;i</button>
                                    </form>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty leaveRequests}">
                        <tr><td colspan="7">Kh&#244;ng c&#243; &#273;&#417;n ngh&#7881; ph&#233;p.</td></tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </section>
</div>
</body>
</html>
