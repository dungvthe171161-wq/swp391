<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR | Đơn ứng tuyển</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/guest.css" rel="stylesheet">
</head>
<body>
<div class="candidate-shell">
    <aside class="candidate-sidebar">
        <a class="candidate-brand" href="${pageContext.request.contextPath}/homepage" aria-label="Về trang chủ BetterHR">
            <strong>Better Jobs</strong>
            <span>Cổng ứng viên BetterHR</span>
        </a>

        <nav class="candidate-nav">
            <a href="${pageContext.request.contextPath}/guest/dashboard"><i class="fa-solid fa-table-columns"></i> Bảng điều khiển</a>
            <a class="active" href="${pageContext.request.contextPath}/guest/applications"><i class="fa-solid fa-list-check"></i> Đơn ứng tuyển</a>
            <a href="${pageContext.request.contextPath}/guest/profile"><i class="fa-solid fa-user"></i> Hồ sơ của tôi</a>
            <a href="${pageContext.request.contextPath}/logout"><i class="fa-solid fa-right-from-bracket"></i> Đăng xuất</a>
        </nav>

        <div class="candidate-sidebar-footer">
            <div class="candidate-support-card">
                <p>Cần hỗ trợ hồ sơ?</p>
                <a href="${pageContext.request.contextPath}/homepage">Liên hệ HR</a>
            </div>
        </div>
    </aside>

    <main class="candidate-main">
        <header class="candidate-topbar">
            <div class="candidate-search">
                <i class="fa-solid fa-magnifying-glass"></i>
                <input type="text" placeholder="Tìm kiếm hồ sơ...">
            </div>
            <div class="candidate-topbar-user">
                <div class="candidate-greeting">
                    <strong>${guestProfile.fullName}</strong>
                    <span>${currentUser.email}</span>
                </div>
                <a class="candidate-avatar" href="${pageContext.request.contextPath}/guest/profile">
                    <c:choose>
                        <c:when test="${not empty guestProfile.avatar}">
                            <img src="${guestProfile.avatar}" alt="Ảnh đại diện">
                        </c:when>
                        <c:otherwise>B</c:otherwise>
                    </c:choose>
                </a>
            </div>
        </header>

        <section class="candidate-canvas">
            <section class="candidate-hero">
                <div class="candidate-hero-content">
                    <h1>Đơn ứng tuyển của tôi</h1>
                    <p>Xem lại các vị trí đã ứng tuyển, trạng thái xử lý và thông tin CV đã gửi cho BetterHR.</p>
                    <div class="candidate-actions">
                        <a class="candidate-btn" href="${pageContext.request.contextPath}/RecruitmentController">Ứng tuyển thêm</a>
                        <a class="candidate-btn secondary" href="${pageContext.request.contextPath}/guest/dashboard">Về bảng điều khiển</a>
                    </div>
                </div>
            </section>

            <section class="candidate-overview">
                <div class="candidate-stat">
                    <span class="candidate-stat-icon"><i class="fa-solid fa-file-lines"></i></span>
                    <div><p>Tổng hồ sơ</p><strong>${totalApplications}</strong></div>
                </div>
                <div class="candidate-stat">
                    <span class="candidate-stat-icon gold"><i class="fa-solid fa-hourglass-half"></i></span>
                    <div><p>Đang xử lý</p><strong>${processingApplications}</strong></div>
                </div>
                <div class="candidate-stat">
                    <span class="candidate-stat-icon"><i class="fa-solid fa-circle-check"></i></span>
                    <div><p>Đã duyệt</p><strong>${hiredApplications}</strong></div>
                </div>
                <div class="candidate-stat">
                    <span class="candidate-stat-icon red"><i class="fa-solid fa-circle-xmark"></i></span>
                    <div><p>Từ chối</p><strong>${rejectedApplications}</strong></div>
                </div>
            </section>

            <section class="candidate-card">
                <div class="candidate-card-header">
                    <div>
                        <h2>Danh sách hồ sơ</h2>
                        <p>Theo dõi toàn bộ hồ sơ bạn đã gửi cho BetterHR.</p>
                    </div>
                </div>
                <c:choose>
                    <c:when test="${empty applications}">
                        <div class="candidate-card-body">
                            <div class="candidate-empty">Bạn chưa có hồ sơ ứng tuyển nào. Hãy xem danh sách việc làm và nộp hồ sơ khi sẵn sàng.</div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="candidate-table-wrap">
                            <table class="candidate-table">
                                <thead>
                                <tr>
                                    <th>Vị trí</th>
                                    <th>Địa điểm</th>
                                    <th>Lương</th>
                                    <th>Ngày nộp</th>
                                    <th>Trạng thái</th>
                                    <th>CV</th>
                                </tr>
                                </thead>
                                <tbody>
                                <c:forEach var="app" items="${applications}">
                                    <tr>
                                        <td>
                                            <strong>${empty app.jobTitle ? 'Tin tuyển dụng' : app.jobTitle}</strong>
                                            <div class="candidate-muted candidate-requirement">${empty app.requirement ? 'Yêu cầu đang cập nhật' : app.requirement}</div>
                                        </td>
                                        <td class="candidate-muted">${empty app.location ? 'Đang cập nhật' : app.location}</td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${app.salary != null}">${app.salary} VNĐ</c:when>
                                                <c:otherwise>Thỏa thuận</c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="candidate-muted">${app.application.appliedDate}</td>
                                        <td>
                                            <span class="candidate-status ${app.application.statusCssClass}">
                                                ${app.application.statusLabel}
                                            </span>
                                        </td>
                                        <td>
                                            <c:choose>
                                                <c:when test="${not empty app.application.cv}">
                                                    <span class="candidate-status"><i class="fa-solid fa-file"></i>&nbsp;Đã nộp</span>
                                                </c:when>
                                                <c:otherwise>Chưa có</c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                                </tbody>
                            </table>
                        </div>
                    </c:otherwise>
                </c:choose>
            </section>
        </section>
    </main>
</div>
</body>
</html>
