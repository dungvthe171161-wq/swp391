<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.hrm.model.entity.Recruitment" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR | Cổng ứng viên</title>
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
            <a class="active" href="${pageContext.request.contextPath}/guest/dashboard"><i class="fa-solid fa-table-columns"></i> Bảng điều khiển</a>
            <a href="${pageContext.request.contextPath}/guest/applications"><i class="fa-solid fa-list-check"></i> Đơn ứng tuyển</a>
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
                <input type="text" placeholder="Tìm kiếm công việc...">
            </div>

            <div class="candidate-topbar-user">
                <div class="candidate-greeting">
                    <strong>Chào bạn, ${guestProfile.fullName}</strong>
                    <span>Mã tài khoản: #${currentUser.userId}</span>
                </div>
                <button class="candidate-icon-btn" type="button" aria-label="Thông báo"><i class="fa-regular fa-bell"></i></button>
                <button class="candidate-icon-btn" type="button" aria-label="Cài đặt"><i class="fa-solid fa-gear"></i></button>
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
                    <h1>Chào mừng bạn đến với Cổng sự nghiệp BetterHR</h1>
                    <p>Theo dõi đơn ứng tuyển, khám phá cơ hội nghề nghiệp mới và quản lý hồ sơ của bạn. Bước tiến sự nghiệp tiếp theo của bạn bắt đầu từ đây.</p>
                    <div class="candidate-actions">
                        <a class="candidate-btn" href="${pageContext.request.contextPath}/RecruitmentController">Khám phá việc làm</a>
                        <a class="candidate-btn secondary" href="${pageContext.request.contextPath}/guest/profile">Cập nhật hồ sơ</a>
                    </div>
                </div>
            </section>

            <section class="candidate-overview">
                <div class="candidate-stat">
                    <span class="candidate-stat-icon"><i class="fa-solid fa-paper-plane"></i></span>
                    <div>
                        <p>Việc làm đã ứng tuyển</p>
                        <strong>${totalApplications}</strong>
                    </div>
                </div>
                <div class="candidate-stat">
                    <span class="candidate-stat-icon gold"><i class="fa-regular fa-calendar"></i></span>
                    <div>
                        <p>Lịch phỏng vấn</p>
                        <strong>${upcomingInterviewCount}</strong>
                    </div>
                </div>
                <div class="candidate-stat">
                    <span class="candidate-stat-icon"><i class="fa-solid fa-circle-check"></i></span>
                    <div>
                        <p>Hồ sơ đã duyệt</p>
                        <strong>${hiredApplications}</strong>
                    </div>
                </div>
                <div class="candidate-stat">
                    <span class="candidate-stat-icon red"><i class="fa-solid fa-bookmark"></i></span>
                    <div>
                        <p>Đang xử lý</p>
                        <strong>${processingApplications}</strong>
                    </div>
                </div>
            </section>

            <div class="candidate-grid">
                <div class="candidate-left">
                    <section class="candidate-card">
                        <div class="candidate-card-header">
                            <div>
                                <h2>Đơn ứng tuyển của tôi</h2>
                                <p>Theo dõi trạng thái các hồ sơ đã gửi.</p>
                            </div>
                            <a class="candidate-link" href="${pageContext.request.contextPath}/guest/applications">Xem tất cả</a>
                        </div>
                        <div class="candidate-table-wrap">
                            <c:choose>
                                <c:when test="${empty applications}">
                                    <div class="candidate-card-body">
                                        <div class="candidate-empty">Bạn chưa nộp hồ sơ nào. Hãy khám phá các vị trí đang tuyển để bắt đầu.</div>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <table class="candidate-table">
                                        <thead>
                                        <tr>
                                            <th>Vị trí</th>
                                            <th>Phòng ban</th>
                                            <th>Ngày</th>
                                            <th>Trạng thái</th>
                                            <th>Thao tác</th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="app" items="${applications}" end="3">
                                            <tr>
                                                <td><strong>${empty app.jobTitle ? 'Tin tuyển dụng' : app.jobTitle}</strong></td>
                                                <td class="candidate-muted">${empty app.location ? 'Đang cập nhật' : app.location}</td>
                                                <td class="candidate-muted">${app.application.appliedDate}</td>
                                                <td>
                                                    <span class="candidate-status ${app.application.statusCssClass}">
                                                        ${app.application.statusLabel}
                                                    </span>
                                                </td>
                                                <td><a class="candidate-link" href="${pageContext.request.contextPath}/guest/applications">Chi tiết</a></td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </section>

                    <section>
                        <div class="candidate-card-header candidate-card-header-clean">
                            <div>
                                <h2>Đề xuất cho bạn</h2>
                                <p>Các vị trí phù hợp đang mở tuyển.</p>
                            </div>
                            <a class="candidate-link" href="${pageContext.request.contextPath}/RecruitmentController">Xem việc làm</a>
                        </div>

                        <%
                            DateTimeFormatter jobDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                            List<Recruitment> recommendedRecruitments =
                                    (List<Recruitment>) request.getAttribute("recommendedRecruitments");
                            if (recommendedRecruitments != null && !recommendedRecruitments.isEmpty()) {
                        %>
                        <div class="candidate-job-grid">
                            <% for (Recruitment job : recommendedRecruitments) { %>
                            <article class="candidate-job-card">
                                <div>
                                    <span class="candidate-job-icon"><i class="fa-solid fa-briefcase"></i></span>
                                    <h3><%= job.getTitle() %></h3>
                                    <div class="candidate-job-meta vertical">
                                        <span><i class="fa-solid fa-location-dot"></i> <%= job.getLocation() == null || job.getLocation().isBlank() ? "Đang cập nhật" : job.getLocation() %></span>
                                        <% if (job.getSalary() != null) { %>
                                        <span><i class="fa-solid fa-money-bill-wave"></i> <%= String.format("%,.0f", job.getSalary()) %> VNĐ</span>
                                        <% } else { %>
                                        <span><i class="fa-solid fa-money-bill-wave"></i> Thỏa thuận</span>
                                        <% } %>
                                        <span><i class="fa-solid fa-users"></i> Số lượng tuyển: <%= job.getApplicant() %> người</span>
                                    </div>
                                    <% if ("Applied".equals(job.getStatus())) { %>
                                    <span class="candidate-status"><i class="fa-solid fa-circle-info"></i> Đang tuyển</span>
                                    <% } %>
                                    <p><%= job.getDescription() == null || job.getDescription().isBlank() ? "Mô tả công việc đang được cập nhật." : job.getDescription() %></p>
                                    <% if (job.getRequirement() != null && !job.getRequirement().trim().isEmpty()) { %>
                                    <div class="candidate-job-requirements">
                                        <strong>Yêu cầu:</strong>
                                        <ul>
                                            <%
                                                String[] requirements = job.getRequirement().split("\\n");
                                                for (String req : requirements) {
                                                    if (!req.trim().isEmpty()) {
                                            %>
                                            <li><%= req.trim() %></li>
                                            <%
                                                    }
                                                }
                                            %>
                                        </ul>
                                    </div>
                                    <% } %>
                                </div>
                                <div class="candidate-job-footer">
                                    <span class="candidate-job-date">
                                        <i class="fa-solid fa-calendar"></i>
                                        <%= job.getPostedDate() != null ? job.getPostedDate().format(jobDateFormatter) : "Đang cập nhật" %>
                                    </span>
                                    <% if ("Applied".equals(job.getStatus())) { %>
                                    <a class="candidate-btn" href="${pageContext.request.contextPath}/RecruitmentController?action=apply&recruitmentId=<%= job.getRecruitmentId() %>">
                                        <i class="fa-solid fa-paper-plane"></i> Ứng tuyển ngay
                                    </a>
                                    <% } else { %>
                                    <span class="candidate-btn disabled"><i class="fa-solid fa-lock"></i> Đã đóng</span>
                                    <% } %>
                                </div>
                            </article>
                            <% } %>
                        </div>
                        <% } else { %>
                        <div class="candidate-card">
                            <div class="candidate-card-body">
                                <div class="candidate-empty">Hiện chưa có vị trí tuyển dụng phù hợp. Bạn có thể quay lại sau để xem cơ hội mới.</div>
                            </div>
                        </div>
                        <% } %>
                    </section>
                </div>

                <aside class="candidate-right">
                    <section class="candidate-card">
                        <div class="candidate-card-body">
                            <div class="candidate-card-header candidate-card-header-compact">
                                <div>
                                    <h3>Tiến độ hồ sơ</h3>
                                    <p>Hoàn thiện để HR liên hệ dễ hơn.</p>
                                </div>
                            </div>
                            <div class="candidate-progress">
                                <div class="candidate-progress-ring"><span>85%</span></div>
                                <div>
                                    <strong>Sắp hoàn thành!</strong>
                                    <p class="candidate-muted candidate-tight-text">Cập nhật hồ sơ giúp tăng khả năng được chú ý.</p>
                                </div>
                            </div>
                            <ul class="candidate-check-list">
                                <li><i class="fa-solid fa-circle-check"></i> Thông tin liên hệ</li>
                                <li><i class="fa-solid fa-circle-check"></i> Hồ sơ ứng tuyển</li>
                                <li><i class="fa-regular fa-circle"></i> Bổ sung ảnh đại diện</li>
                            </ul>
                            <a class="candidate-btn outline candidate-full-btn" href="${pageContext.request.contextPath}/guest/profile">Cập nhật hồ sơ ứng viên</a>
                        </div>
                    </section>

                    <section class="candidate-card candidate-interview">
                        <div class="candidate-card-body">
                            <div class="candidate-card-header candidate-card-header-compact">
                                <div>
                                    <h3>Buổi phỏng vấn tiếp theo</h3>
                                    <p>Chưa có lịch mới</p>
                                </div>
                            </div>
                            <div class="candidate-mini-event">
                                <time>--<small>--</small></time>
                                <div>
                                    <strong>Đang chờ HR sắp lịch</strong>
                                    <span>Khi có lịch phỏng vấn, thông tin sẽ hiển thị tại đây.</span>
                                </div>
                            </div>
                        </div>
                    </section>

                    <section class="candidate-card">
                        <div class="candidate-card-body">
                            <div class="candidate-card-header candidate-card-header-compact">
                                <h3>Thông báo</h3>
                            </div>
                            <div class="candidate-side-list">
                                <c:forEach var="notice" items="${notifications}">
                                    <div class="candidate-notice">
                                        <i class="fa-solid fa-envelope candidate-icon-secondary"></i>
                                        <div>
                                            <strong>${notice.title}</strong>
                                            <span>${notice.message}</span>
                                        </div>
                                    </div>
                                </c:forEach>
                                <c:if test="${empty notifications}">
                                <div class="candidate-notice">
                                    <i class="fa-solid fa-envelope candidate-icon-secondary"></i>
                                    <div>
                                        <strong>Cập nhật trạng thái</strong>
                                        <span>Theo dõi hồ sơ đã nộp tại mục Đơn ứng tuyển.</span>
                                    </div>
                                </div>
                                <div class="candidate-notice">
                                    <i class="fa-solid fa-user-pen candidate-icon-gold"></i>
                                    <div>
                                        <strong>Hoàn thiện hồ sơ</strong>
                                        <span>Cập nhật số điện thoại, địa chỉ và ảnh đại diện.</span>
                                    </div>
                                </div>
                                </c:if>
                            </div>
                        </div>
                    </section>
                </aside>
            </div>
        </section>
    </main>
</div>

<button class="candidate-chat" type="button" aria-label="Hỗ trợ">
    <i class="fa-regular fa-comment-dots"></i>
</button>
</body>
</html>
