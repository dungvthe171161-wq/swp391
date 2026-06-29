<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Employee Dashboard</title>
    <%@ include file="_EmployeeStyles.jspf" %>
    <style>
        .topbar {
            min-height: 74px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 18px;
            padding: 14px clamp(20px, 4vw, 44px);
            background: rgba(251, 249, 244, 0.92);
            border-bottom: 1px solid var(--bh-muted-border);
        }
        .user-chip, .home-pill {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            min-height: 44px;
            padding: 6px 14px;
            border: 1px solid var(--bh-border);
            border-radius: 999px;
            background: #ffffff;
            color: var(--bh-primary-dark);
            font-weight: 800;
        }
        .avatar {
            width: 34px;
            height: 34px;
            display: grid;
            place-items: center;
            border-radius: 999px;
            background: var(--bh-primary-soft);
            color: var(--bh-primary);
            font-weight: 800;
        }
        .welcome-row {
            display: flex;
            justify-content: space-between;
            gap: 20px;
            margin-bottom: 22px;
        }
        .kpi-grid {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 14px;
            margin-bottom: 24px;
        }
        .kpi-card {
            min-height: 120px;
            padding: 18px;
            border: 1px solid var(--bh-border);
            border-radius: 14px;
            background: var(--bh-card);
        }
        .kpi-card.dark {
            background: var(--bh-primary-dark);
            color: #ffffff;
        }
        .kpi-label {
            min-height: 34px;
            margin: 0 0 12px;
            color: var(--bh-muted);
            font-size: 11px;
            font-weight: 800;
            text-transform: uppercase;
        }
        .kpi-card.dark .kpi-label { color: rgba(255,255,255,.72); }
        .kpi-value {
            display: flex;
            align-items: end;
            justify-content: space-between;
            gap: 8px;
        }
        .kpi-value strong {
            color: var(--bh-primary-dark);
            font-size: 30px;
            line-height: 1;
            font-weight: 800;
        }
        .kpi-card.dark .kpi-value strong { color: #ffffff; font-size: 24px; }
        .dashboard-grid {
            display: grid;
            grid-template-columns: minmax(0, 1fr) 340px;
            gap: 24px;
            align-items: start;
        }
        .main-column, .side-column { display: grid; gap: 22px; }
        .two-column { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 22px; }
        .task-list { display: grid; gap: 12px; }
        .task-item {
            display: grid;
            grid-template-columns: 42px minmax(0, 1fr) 120px;
            gap: 14px;
            align-items: center;
            padding: 14px;
            border-radius: 14px;
            background: #f7f4ee;
        }
        .task-icon {
            width: 42px;
            height: 42px;
            display: grid;
            place-items: center;
            border-radius: 12px;
            background: rgba(0, 98, 65, 0.10);
            color: var(--bh-primary);
        }
        .task-name { margin: 0 0 5px; font-weight: 800; }
        .task-meta { color: var(--bh-muted); font-size: 12px; }
        .check-buttons { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; margin: 16px 0; }
        .check-button {
            width: 100%;
            min-height: 64px;
            display: grid;
            place-items: center;
            gap: 5px;
            border: 1px solid var(--bh-muted-border);
            border-radius: 12px;
            background: #f7f4ee;
            color: var(--bh-primary-dark);
            font-weight: 800;
            cursor: pointer;
        }
        .check-button.primary { background: var(--bh-primary); color: #ffffff; border-color: var(--bh-primary); }
        .check-button:disabled { opacity: .48; cursor: not-allowed; }
        .pay-row, .metric-row {
            display: flex;
            justify-content: space-between;
            gap: 16px;
            padding: 10px 0;
            border-bottom: 1px solid var(--bh-muted-border);
            color: var(--bh-muted);
            font-size: 13px;
        }
        .pay-row strong, .metric-row strong { color: var(--bh-primary-dark); }
        .quick-panel { padding: 20px; border-radius: 16px; background: var(--bh-primary-dark); color: #ffffff; }
        .quick-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 10px; }
        .quick-link {
            min-height: 86px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 14px;
            border-radius: 14px;
            background: rgba(255,255,255,.10);
            color: #ffffff;
            font-weight: 800;
        }
        @media (max-width: 1100px) {
            .dashboard-grid, .two-column, .kpi-grid { grid-template-columns: 1fr; }
            .welcome-row { flex-direction: column; }
        }
    </style>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>

    <main class="employee-main">
        <header class="topbar">
            <div>
                <strong style="color:var(--bh-primary-dark);">BetterHR</strong>
                <span style="color:var(--bh-muted); margin-left:8px;">C&#7893;ng th&#244;ng tin nh&#226;n vi&#234;n</span>
            </div>
            <div>
                <a class="user-chip" href="${pageContext.request.contextPath}/employee/profile">
                    <span class="avatar">B</span>
                    <span>${currentEmployee.fullName}</span>
                </a>
                <a class="home-pill" href="${pageContext.request.contextPath}/homepage">
                    <i class="fa-solid fa-house"></i>
                    <span>Trang ch&#7911;</span>
                </a>
            </div>
        </header>

        <div class="content">
            <section class="welcome-row">
                <div>
                    <p class="page-note" style="margin-bottom:6px;">${currentEmployee.departmentName}</p>
                    <h1 class="page-title">Xin ch&#224;o, ${currentEmployee.fullName}</h1>
                    <p class="page-note">D&#7919; li&#7879;u tr&#234;n dashboard &#273;&#432;&#7907;c l&#7845;y theo EmployeeID ${currentEmployee.employeeId}.</p>
                </div>
                <a class="primary-button" href="${pageContext.request.contextPath}/employee/attendance" style="display:inline-flex;align-items:center;gap:10px;text-decoration:none;">
                    <i class="fa-solid fa-fingerprint"></i>
                    <span>Ch&#7845;m c&#244;ng</span>
                </a>
            </section>

            <section class="kpi-grid">
                <article class="kpi-card">
                    <p class="kpi-label">Ng&#224;y ph&#233;p c&#242;n l&#7841;i</p>
                    <div class="kpi-value">
                        <strong>${leaveRemaining}</strong>
                        <span>ng&#224;y</span>
                    </div>
                </article>
                <article class="kpi-card">
                    <p class="kpi-label">C&#244;ng vi&#7879;c &#273;ang ch&#7841;y</p>
                    <div class="kpi-value">
                        <strong>${openTaskCount}</strong>
                        <span>task</span>
                    </div>
                </article>
                <article class="kpi-card">
                    <p class="kpi-label">&#272;i mu&#7897;n / v&#7873; s&#7899;m th&#225;ng n&#224;y</p>
                    <div class="kpi-value">
                        <strong>${attendanceSummary.lateCount + attendanceSummary.earlyLeaveCount}</strong>
                        <span>l&#7847;n</span>
                    </div>
                </article>
                <article class="kpi-card dark">
                    <p class="kpi-label">L&#432;&#417;ng m&#7899;i nh&#7845;t</p>
                    <div class="kpi-value">
                        <strong>
                            <c:choose>
                                <c:when test="${not empty latestPayroll.netSalary}">${latestPayroll.netSalary}</c:when>
                                <c:otherwise>0</c:otherwise>
                            </c:choose>
                        </strong>
                        <span>VN&#272;</span>
                    </div>
                </article>
            </section>

            <div class="dashboard-grid">
                <div class="main-column">
                    <section class="panel">
                        <div class="panel-inner">
                            <div class="panel-header">
                                <h2 class="page-title" style="font-size:22px;">C&#244;ng vi&#7879;c c&#7911;a t&#244;i</h2>
                                <a class="panel-link" href="${pageContext.request.contextPath}/employee/tasks">Xem t&#7845;t c&#7843;</a>
                            </div>
                            <div class="task-list">
                                <c:forEach var="task" items="${tasks}" begin="0" end="3">
                                    <article class="task-item">
                                        <div class="task-icon"><i class="fa-solid fa-list-check"></i></div>
                                        <div>
                                            <p class="task-name">${task.title}</p>
                                            <div class="task-meta">H&#7841;n: ${task.dueDate} · ${task.status}</div>
                                        </div>
                                        <strong>${task.status}</strong>
                                    </article>
                                </c:forEach>
                                <c:if test="${empty tasks}">
                                    <p>Ch&#432;a c&#243; c&#244;ng vi&#7879;c &#273;&#432;&#7907;c giao.</p>
                                </c:if>
                            </div>
                        </div>
                    </section>

                    <div class="two-column">
                        <section class="panel" id="attendance">
                            <div class="panel-inner">
                                <h2 class="page-title" style="font-size:22px;">Ch&#7845;m c&#244;ng h&#244;m nay</h2>
                                <div class="metric-row"><span>Tr&#7841;ng th&#225;i</span><strong>
                                    <c:choose>
                                        <c:when test="${todayAttendanceStatus eq 'ChuaVaoCa'}">Ch&#432;a v&#224;o ca</c:when>
                                        <c:when test="${todayAttendanceStatus eq 'DaVaoCa'}">&#272;&#227; v&#224;o ca, ch&#432;a ra ca</c:when>
                                        <c:when test="${todayAttendanceStatus eq 'DaRaCa'}">&#272;&#227; ra ca</c:when>
                                        <c:otherwise>${todayAttendanceStatus}</c:otherwise>
                                    </c:choose>
                                </strong></div>
                                <div class="metric-row"><span>V&#224;o ca</span><strong>${todayAttendance.checkIn}</strong></div>
                                <div class="metric-row"><span>Ra ca</span><strong>${todayAttendance.checkOut}</strong></div>
                                <div class="metric-row"><span>T&#7893;ng gi&#7901; th&#225;ng</span><strong>${attendanceSummary.totalWorkingHours}h</strong></div>
                                <div class="check-buttons">
                                    <form method="post" action="${pageContext.request.contextPath}/employee/attendance">
                                        <input type="hidden" name="action" value="checkIn">
                                        <c:choose>
                                            <c:when test="${todayAttendanceStatus eq 'ChuaVaoCa'}">
                                                <button class="check-button primary" type="submit">
                                                    <i class="fa-solid fa-right-to-bracket"></i>
                                                    <span>V&#224;o ca</span>
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="check-button primary" type="button" disabled>
                                                    <i class="fa-solid fa-right-to-bracket"></i>
                                                    <span>V&#224;o ca</span>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                    <form method="post" action="${pageContext.request.contextPath}/employee/attendance">
                                        <input type="hidden" name="action" value="checkOut">
                                        <c:choose>
                                            <c:when test="${todayAttendanceStatus eq 'DaVaoCa'}">
                                                <button class="check-button" type="submit">
                                                    <i class="fa-solid fa-right-from-bracket"></i>
                                                    <span>Ra ca</span>
                                                </button>
                                            </c:when>
                                            <c:otherwise>
                                                <button class="check-button" type="button" disabled>
                                                    <i class="fa-solid fa-right-from-bracket"></i>
                                                    <span>Ra ca</span>
                                                </button>
                                            </c:otherwise>
                                        </c:choose>
                                    </form>
                                </div>
                            </div>
                        </section>

                        <section class="panel">
                            <div class="panel-inner">
                                <div class="panel-header">
                                    <h2 class="page-title" style="font-size:22px;">Phi&#7871;u l&#432;&#417;ng m&#7899;i nh&#7845;t</h2>
                                    <a class="panel-link" href="${pageContext.request.contextPath}/employee/payroll">Chi ti&#7871;t</a>
                                </div>
                                <div class="pay-row"><span>K&#7923;</span><strong>${latestPayroll.payPeriod}</strong></div>
                                <div class="pay-row"><span>L&#432;&#417;ng c&#417; b&#7843;n</span><strong>${latestPayroll.baseSalary}</strong></div>
                                <div class="pay-row"><span>Ph&#7909; c&#7845;p</span><strong>${latestPayroll.allowance}</strong></div>
                                <div class="pay-row"><span>Kh&#7845;u tr&#7915;</span><strong>${latestPayroll.deduction}</strong></div>
                                <div class="pay-row"><span>Th&#7921;c nh&#7853;n</span><strong>${latestPayroll.netSalary}</strong></div>
                                <div class="pay-row"><span>Tr&#7841;ng th&#225;i</span><strong>${latestPayroll.status}</strong></div>
                            </div>
                        </section>
                    </div>
                </div>

                <aside class="side-column">
                    <section class="quick-panel">
                        <h3>Thao t&#225;c nhanh</h3>
                        <div class="quick-grid">
                            <a class="quick-link" href="${pageContext.request.contextPath}/employee/leaves">
                                <i class="fa-solid fa-circle-plus"></i>
                                <span>Xin ngh&#7881; ph&#233;p</span>
                            </a>
                            <a class="quick-link" href="${pageContext.request.contextPath}/employee/payroll">
                                <i class="fa-solid fa-receipt"></i>
                                <span>Xem l&#432;&#417;ng</span>
                            </a>
                            <a class="quick-link" href="${pageContext.request.contextPath}/employee/tasks">
                                <i class="fa-solid fa-list-check"></i>
                                <span>C&#244;ng vi&#7879;c</span>
                            </a>
                            <a class="quick-link" href="${pageContext.request.contextPath}/employee/contract">
                                <i class="fa-solid fa-file-contract"></i>
                                <span>H&#7907;p &#273;&#7891;ng</span>
                            </a>
                        </div>
                    </section>

                    <section class="panel">
                        <div class="panel-inner">
                            <h2 class="page-title" style="font-size:22px;">Ngh&#7881; ph&#233;p</h2>
                            <div class="metric-row"><span>C&#242;n l&#7841;i</span><strong>${leaveRemaining} ng&#224;y</strong></div>
                            <div class="metric-row"><span>&#272;ang ch&#7901; duy&#7879;t</span><strong>${pendingLeaveCount} &#273;&#417;n</strong></div>
                        </div>
                    </section>
                </aside>
            </div>
        </div>
    </main>
</div>
</body>
</html>
