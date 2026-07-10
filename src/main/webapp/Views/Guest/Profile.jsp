<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR | Hồ sơ ứng viên</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css" rel="stylesheet">
    <link href="${pageContext.request.contextPath}/css/guest.css?v=guest-profile-merge" rel="stylesheet">
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
            <a href="${pageContext.request.contextPath}/guest/applications"><i class="fa-solid fa-list-check"></i> Đơn ứng tuyển</a>
            <a class="active" href="${pageContext.request.contextPath}/guest/profile"><i class="fa-solid fa-user"></i> Hồ sơ của tôi</a>
            <a href="#candidate-profile"><i class="fa-solid fa-id-card"></i> Hồ sơ ứng tuyển</a>
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
                    <h1>Hồ sơ của tôi</h1>
                    <p>Cập nhật thông tin cá nhân để HR có thể liên hệ và đánh giá hồ sơ của bạn thuận tiện hơn.</p>
                    <div class="candidate-actions">
                        <a class="candidate-btn secondary" href="${pageContext.request.contextPath}/guest/dashboard">Về bảng điều khiển</a>
                        <a class="candidate-btn" href="${pageContext.request.contextPath}/guest/applications">Xem đơn ứng tuyển</a>
                    </div>
                </div>
            </section>

            <div class="candidate-grid">
                <div class="candidate-left">
                <section class="candidate-card">
                    <div class="candidate-card-header">
                        <div>
                            <h2>Thông tin ứng viên</h2>
                            <p>Email đăng nhập được lấy từ tài khoản BetterHR.</p>
                        </div>
                    </div>
                    <div class="candidate-card-body">
                        <c:if test="${not empty success}">
                            <div class="candidate-alert">${success}</div>
                        </c:if>
                        <c:if test="${not empty error}">
                            <div class="candidate-alert error">${error}</div>
                        </c:if>

                        <div class="candidate-profile-compose">
                            <label class="candidate-avatar-edit candidate-avatar-edit-large" for="avatarFile" title="Chọn ảnh đại diện">
                                <span class="candidate-avatar">
                                    <c:choose>
                                        <c:when test="${not empty guestProfile.avatar}">
                                            <img id="avatarPreviewImage" src="${guestProfile.avatar}" alt="Ảnh đại diện">
                                        </c:when>
                                        <c:otherwise>
                                            <span id="avatarPreviewFallback">B</span>
                                            <img id="avatarPreviewImage" src="" alt="Ảnh đại diện mới" style="display:none;">
                                        </c:otherwise>
                                    </c:choose>
                                    <span class="candidate-avatar-edit-icon"><i class="fa-solid fa-camera"></i></span>
                                </span>
                            </label>
                            <input class="candidate-avatar-input" id="avatarFile" name="avatarFile" type="file"
                                   accept="image/png,image/jpeg,image/webp,image/gif" form="basicProfileForm">
                            <div class="candidate-profile-compose-text">
                                <span class="candidate-avatar-hint">Bấm vào ảnh để thay đổi</span>
                                <h3>${guestProfile.fullName}</h3>
                                <p>${currentUser.email}</p>
                                <span class="candidate-status candidate-profile-badge">Tài khoản ứng viên</span>
                            </div>
                        </div>

                        <form id="basicProfileForm" class="candidate-form" action="${pageContext.request.contextPath}/guest/profile" method="post" enctype="multipart/form-data">
                            <div class="candidate-form-grid">
                                <div class="candidate-field">
                                    <label for="fullName">Họ và tên</label>
                                    <input class="candidate-input" id="fullName" name="fullName" required value="${guestProfile.fullName}" placeholder="Nhập họ và tên">
                                </div>
                                <div class="candidate-field">
                                    <label for="email">Email</label>
                                    <input class="candidate-input" id="email" value="${currentUser.email}" readonly>
                                </div>
                                <div class="candidate-field">
                                    <label for="phone">Số điện thoại</label>
                                    <input class="candidate-input" id="phone" name="phone" value="${guestProfile.phone}" placeholder="Nhập số điện thoại">
                                </div>
                                <div class="candidate-field">
                                    <label for="gender">Giới tính</label>
                                    <select class="candidate-select" id="gender" name="gender">
                                        <option value="">Chọn giới tính</option>
                                        <option value="Nam" ${guestProfile.gender == 'Nam' ? 'selected' : ''}>Nam</option>
                                        <option value="Nữ" ${guestProfile.gender == 'Nữ' ? 'selected' : ''}>Nữ</option>
                                        <option value="Khác" ${guestProfile.gender == 'Khác' ? 'selected' : ''}>Khác</option>
                                    </select>
                                </div>
                                <div class="candidate-field">
                                    <label for="dateOfBirth">Ngày sinh</label>
                                    <input class="candidate-input" id="dateOfBirth" name="dateOfBirth" type="date" value="${guestProfile.dateOfBirth}">
                                </div>
                            </div>

                            <div class="candidate-field">
                                <label for="address">Địa chỉ</label>
                                <textarea class="candidate-textarea" id="address" name="address" placeholder="Nhập địa chỉ hiện tại">${guestProfile.address}</textarea>
                            </div>

                            <div class="candidate-actions">
                                <button class="candidate-btn" type="submit"><i class="fa-solid fa-floppy-disk"></i> Lưu hồ sơ</button>
                                <a class="candidate-btn outline" href="${pageContext.request.contextPath}/guest/applications">Xem đơn ứng tuyển</a>
                            </div>
                        </form>
                    </div>
                </section>

                <section class="candidate-card" id="candidate-profile">
                    <div class="candidate-card-header">
                        <div>
                            <h2>Hồ sơ ứng tuyển</h2>
                            <p>Thông tin này sẽ được dùng tự động cho các lần ứng tuyển tiếp theo.</p>
                        </div>
                    </div>
                    <div class="candidate-card-body">
                        <form class="candidate-form" action="${pageContext.request.contextPath}/guest/profile" method="post" enctype="multipart/form-data">
                            <input type="hidden" name="action" value="saveCandidateProfile">
                            <div class="candidate-form-grid">
                                <div class="candidate-field">
                                    <label for="candidateFullName">Họ và tên</label>
                                    <input class="candidate-input" id="candidateFullName" name="candidateFullName" required
                                           value="${not empty candidateProfile.fullName ? candidateProfile.fullName : guestProfile.fullName}">
                                </div>
                                <div class="candidate-field">
                                    <label for="candidateEmail">Email nhận xác nhận</label>
                                    <input class="candidate-input" id="candidateEmail" name="candidateEmail" type="email" required
                                           value="${not empty candidateProfile.email ? candidateProfile.email : currentUser.email}">
                                </div>
                                <div class="candidate-field">
                                    <label for="candidatePhone">Số điện thoại</label>
                                    <input class="candidate-input" id="candidatePhone" name="candidatePhone" required
                                           value="${not empty candidateProfile.phone ? candidateProfile.phone : guestProfile.phone}">
                                </div>
                                <div class="candidate-field">
                                    <label for="candidateDateOfBirth">Ngày sinh</label>
                                    <input class="candidate-input" id="candidateDateOfBirth" name="candidateDateOfBirth" type="date"
                                           value="${not empty candidateProfile.dateOfBirth ? candidateProfile.dateOfBirth : guestProfile.dateOfBirth}">
                                </div>
                                <div class="candidate-field">
                                    <label for="candidateAddress">Địa chỉ/Nơi ở hiện tại</label>
                                    <input class="candidate-input" id="candidateAddress" name="candidateAddress"
                                           value="${not empty candidateProfile.address ? candidateProfile.address : guestProfile.address}">
                                </div>
                                <div class="candidate-field">
                                    <label for="desiredPosition">Vị trí mong muốn</label>
                                    <input class="candidate-input" id="desiredPosition" name="desiredPosition"
                                           value="${candidateProfile.desiredPosition}">
                                </div>
                                <div class="candidate-field">
                                    <label for="expectedSalary">Mức lương mong muốn</label>
                                    <input class="candidate-input" id="expectedSalary" name="expectedSalary" type="number" min="0" step="100000"
                                           value="${candidateProfile.expectedSalary}">
                                </div>
                                <div class="candidate-field">
                                    <label for="candidateCvFile">CV</label>
                                    <input class="candidate-input" id="candidateCvFile" name="candidateCvFile" type="file" accept=".pdf,.doc,.docx" ${empty candidateProfile.cvFilePath ? 'required' : ''}>
                                    <c:if test="${not empty candidateProfile.cvFilePath}">
                                        <span class="candidate-file-note">CV hiện tại: ${candidateProfile.cvFilePath}</span>
                                    </c:if>
                                </div>
                            </div>

                            <div class="candidate-field">
                                <label for="workExperience">Kinh nghiệm làm việc</label>
                                <textarea class="candidate-textarea" id="workExperience" name="workExperience"
                                          placeholder="Công ty đã từng làm, thời gian làm việc, mô tả công việc, kỹ năng nổi bật...">${candidateProfile.workExperience}</textarea>
                            </div>

                            <c:choose>
                                <c:when test="${candidateProfile.emailVerified}">
                                    <div class="candidate-alert">Email hồ sơ ứng tuyển đã được xác nhận.</div>
                                </c:when>
                                <c:otherwise>
                                    <div class="candidate-alert error">Email hồ sơ ứng tuyển chưa được xác nhận. Khi lưu, hệ thống sẽ gửi mã xác nhận đến email bạn nhập.</div>
                                </c:otherwise>
                            </c:choose>

                            <div class="candidate-actions">
                                <button class="candidate-btn" type="submit"><i class="fa-solid fa-floppy-disk"></i> Lưu hồ sơ ứng tuyển</button>
                            </div>
                        </form>
                    </div>
                </section>

                </div>

                <aside class="candidate-right">
                    <section class="candidate-card">
                        <div class="candidate-card-body">
                            <h3 class="candidate-profile-section-title">Tiến độ hồ sơ</h3>
                            <div class="candidate-progress">
                                <div class="candidate-progress-ring"><span>85%</span></div>
                                <div>
                                    <strong>Sắp hoàn thành!</strong>
                                    <p class="candidate-muted candidate-tight-text">Bổ sung đầy đủ thông tin giúp HR xử lý nhanh hơn.</p>
                                </div>
                            </div>
                        </div>
                    </section>
                </aside>
            </div>
        </section>
    </main>
</div>
<script>
    const avatarInput = document.getElementById('avatarFile');
    const avatarPreviewImage = document.getElementById('avatarPreviewImage');
    const avatarPreviewFallback = document.getElementById('avatarPreviewFallback');

    if (avatarInput && avatarPreviewImage) {
        avatarInput.addEventListener('change', function () {
            const file = this.files && this.files[0];
            if (!file) {
                return;
            }
            const previewUrl = URL.createObjectURL(file);
            avatarPreviewImage.src = previewUrl;
            avatarPreviewImage.style.display = 'block';
            if (avatarPreviewFallback) {
                avatarPreviewFallback.style.display = 'none';
            }
        });
    }
</script>
</body>
</html>
