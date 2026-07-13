<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cau hinh GPS - BetterHR</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/Admin_home.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/unified-layout.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/user-menu.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/Admin/css/admin-shared-theme.css">
    <style>
        .gps-page { padding: 28px 34px; }
        .gps-card { background: var(--admin-card, #fff); border: 1px solid var(--admin-border, #e6e0d8); border-radius: 12px; box-shadow: var(--admin-shadow, 0 8px 24px rgba(0,0,0,.08)); padding: 22px; margin-bottom: 20px; overflow: hidden; }
        .gps-grid { display: grid; grid-template-columns: repeat(2, minmax(0, 1fr)); gap: 14px; align-items: end; }
        .gps-field { display: grid; gap: 7px; }
        .gps-field input, .gps-field select { width: 100%; min-height: 42px; border: 1px solid var(--admin-border, #e6e0d8); border-radius: 8px; padding: 0 12px; }
        .gps-btn { min-height: 40px; border: 0; border-radius: 8px; padding: 0 14px; background: var(--admin-primary, #00482f); color: #fff; font-weight: 800; cursor: pointer; display: inline-flex; align-items: center; justify-content: center; text-decoration: none; }
        .gps-btn.secondary { background: #f3f0ea; color: var(--admin-primary, #00482f); border: 1px solid var(--admin-border, #e6e0d8); }
        .gps-btn.danger { background: #b42318; }
        .gps-actions { display: flex; gap: 10px; flex-wrap: wrap; }
        .gps-map-frame { width: 100%; height: 360px; min-height: 320px; max-height: 400px; border: 0; border-radius: 12px; display: block; }
        .gps-preview { display: grid; gap: 12px; }
        .gps-preview-empty { min-height: 320px; height: 360px; border: 1px dashed var(--admin-border, #e6e0d8); border-radius: 12px; display: grid; place-items: center; color: #6f7772; text-align: center; padding: 18px; }
        .gps-card:first-of-type form[data-map-form] { display: grid; grid-template-columns: minmax(320px, 1fr) minmax(320px, 420px); gap: 22px; align-items: start; }
        .gps-card:first-of-type .gps-preview { grid-column: 1; grid-row: 1; }
        .gps-card:first-of-type .gps-grid { grid-column: 2; grid-row: 1; }
        .gps-table-wrapper { width: 100%; overflow-x: auto; }
        .gps-table { width: 100%; min-width: 1080px; border-collapse: collapse; }
        .gps-table th, .gps-table td { padding: 10px; border-bottom: 1px solid #eee; text-align: left; vertical-align: top; }
        .gps-table .gps-map-frame { width: 240px; height: 180px; min-height: 180px; max-height: 180px; border-radius: 8px; }
        @media (max-width: 900px) {
            .gps-card:first-of-type form[data-map-form], .gps-grid { grid-template-columns: 1fr; }
            .gps-card:first-of-type .gps-preview, .gps-card:first-of-type .gps-grid { grid-column: auto; grid-row: auto; }
            .gps-map-frame, .gps-preview-empty { height: 320px; min-height: 320px; }
        }
    </style>
</head>
<body class="admin-page">
<div class="dashboard-container">
    <aside class="sidebar">
        <div class="sidebar-header">
            <div class="logo">
                <img src="https://cdn-icons-png.flaticon.com/512/847/847969.png" alt="Logo" width="32">
                <span>Qu&#7843;n tr&#7883;</span>
            </div>
        </div>
        <div class="sidebar-nav">
            <a href="${pageContext.request.contextPath}/admin?action=dashboard"
               class="nav-item ${activePage == 'dashboard' ? 'active' : ''}">
                <span class="material-symbols-outlined">dashboard</span>
                <span>T&#7893;ng quan</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin?action=departments"
               class="nav-item ${activePage == 'departments' ? 'active' : ''}">
                <span class="material-symbols-outlined">domain</span>
                <span>Ph&#242;ng ban</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/users"
               class="nav-item ${activePage == 'users' ? 'active' : ''}">
                <span class="material-symbols-outlined">group</span>
                <span>Ng&#432;&#7901;i d&#249;ng</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin?action=role-permissions"
               class="nav-item ${activePage == 'role-permissions' ? 'active' : ''}">
                <span class="material-symbols-outlined">admin_panel_settings</span>
                <span>Ph&#226;n quy&#7873;n</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin/office-location"
               class="nav-item ${activePage == 'office-location' ? 'active' : ''}">
                <span class="material-symbols-outlined">location_on</span>
                <span>C&#7845;u h&#236;nh GPS</span>
            </a>
                    <a href="${pageContext.request.contextPath}/admin/chatbot-faqs"
                       class="nav-item ${activePage == 'chatbot-faqs' ? 'active' : ''}">
                        <span class="material-symbols-outlined">smart_toy</span>
                        <span>FAQ chatbot</span>
                    </a>
            <a href="${pageContext.request.contextPath}/admin?action=audit-log"
               class="nav-item ${activePage == 'audit-log' ? 'active' : ''}">
                <span class="material-symbols-outlined">history</span>
                <span>Nh&#7853;t k&#253; h&#7879; th&#7889;ng</span>
            </a>
            <a href="${pageContext.request.contextPath}/admin?action=profile"
               class="nav-item ${activePage == 'profile' ? 'active' : ''}">
                <span class="material-symbols-outlined">person</span>
                <span>H&#7891; s&#417;</span>
            </a>
        </div>
    </aside>
    <main class="main-content">
        <section class="gps-page">
            <h1>C&#7845;u h&#236;nh &#273;&#7883;a &#273;i&#7875;m ch&#7845;m c&#244;ng GPS</h1>
            <c:if test="${not empty sessionScope.officeLocationSuccess}"><div class="gps-card">${sessionScope.officeLocationSuccess}</div><c:remove var="officeLocationSuccess" scope="session"/></c:if>
            <c:if test="${not empty sessionScope.officeLocationError}"><div class="gps-card">${sessionScope.officeLocationError}</div><c:remove var="officeLocationError" scope="session"/></c:if>
            <section class="gps-card">
                <h2>Th&#234;m &#273;&#7883;a &#273;i&#7875;m m&#7899;i</h2>
                <form method="post" action="${pageContext.request.contextPath}/admin/office-location" data-map-form>
                    <input type="hidden" name="action" value="create">
                    <div class="gps-grid">
                        <div class="gps-field"><label>LocationCode</label><input type="text" name="locationCode" maxlength="50" placeholder="HN_OFFICE"></div>
                        <div class="gps-field"><label>LocationName *</label><input type="text" name="locationName" maxlength="150" required></div>
                        <div class="gps-field"><label>Address</label><input type="text" name="address" maxlength="255"></div>
                        <div class="gps-field"><label>RadiusMeters *</label><input type="number" name="radiusMeters" min="1" value="500" required></div>
                        <div class="gps-field"><label>Latitude *</label><input type="number" name="latitude" min="-90" max="90" step="0.0000001" required></div>
                        <div class="gps-field"><label>Longitude *</label><input type="number" name="longitude" min="-180" max="180" step="0.0000001" required></div>
                        <div class="gps-field"><label>IsActive</label><select name="isActive"><option value="1">Active</option><option value="0">Inactive</option></select></div>
                        <div class="gps-actions"><button class="gps-btn" type="submit">L&#432;u</button><button class="gps-btn secondary" type="reset">H&#7911;y</button></div>
                    </div>
                    <div class="gps-preview">
                        <div class="gps-preview-empty" data-map-empty>Nhap Latitude va Longitude de xem ban do.</div>
                        <iframe class="gps-map-frame" data-map-frame loading="lazy" referrerpolicy="no-referrer-when-downgrade" style="display:none;" title="Google Maps preview"></iframe>
                        <a class="gps-btn secondary" data-map-link target="_blank" rel="noopener" href="#" style="display:none;">M&#7903; tr&#234;n Google Maps</a>
                    </div>
                </form>
            </section>
            <section class="gps-card">
                <h2>Danh s&#225;ch &#273;&#7883;a &#273;i&#7875;m</h2>
                <div class="gps-table-wrapper">
                    <table class="gps-table">
                        <thead><tr><th>ID</th><th>M&#227;</th><th>T&#234;n</th><th>&#272;&#7883;a ch&#7881;</th><th>Latitude</th><th>Longitude</th><th>B&#225;n k&#237;nh</th><th>Tr&#7841;ng th&#225;i</th><th>H&#224;nh &#273;&#7897;ng</th></tr></thead>
                        <tbody>
                        <c:forEach var="location" items="${officeLocations}">
                            <tr>
                                <form method="post" action="${pageContext.request.contextPath}/admin/office-location" data-map-form>
                                    <input type="hidden" name="officeLocationId" value="${location.officeLocationId}">
                                    <td>${location.officeLocationId}</td>
                                    <td><input type="text" name="locationCode" value="${location.locationCode}" maxlength="50"></td>
                                    <td><input type="text" name="locationName" value="${location.locationName}" maxlength="150" required></td>
                                    <td><input type="text" name="address" value="${location.address}" maxlength="255"></td>
                                    <td><input type="number" name="latitude" value="${location.latitude}" min="-90" max="90" step="0.0000001" required></td>
                                    <td><input type="number" name="longitude" value="${location.longitude}" min="-180" max="180" step="0.0000001" required></td>
                                    <td><input type="number" name="radiusMeters" value="${location.radiusMeters}" min="1" required></td>
                                    <td><select name="isActive"><option value="1" ${location.active ? 'selected' : ''}>Active</option><option value="0" ${!location.active ? 'selected' : ''}>Inactive</option></select></td>
                                    <td><div class="gps-actions"><button class="gps-btn secondary" name="action" value="update">S&#7917;a</button><button class="gps-btn danger" name="action" value="deactivate">T&#7855;t</button><a class="gps-btn secondary" data-map-link target="_blank" rel="noopener" href="https://www.google.com/maps?q=${location.latitude},${location.longitude}">M&#7903; Maps</a><iframe class="gps-map-frame" data-map-frame loading="lazy" referrerpolicy="no-referrer-when-downgrade" src="https://www.google.com/maps?q=${location.latitude},${location.longitude}&amp;z=16&amp;output=embed"></iframe></div></td>
                                </form>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty officeLocations}"><tr><td colspan="9">Ch&#432;a c&#243; &#273;&#7883;a &#273;i&#7875;m GPS.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </section>
    </main>
</div>
<script>
function isValidCoordinate(lat, lng) { return Number.isFinite(lat) && Number.isFinite(lng) && lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180; }
function updateMapPreview(form) {
    const latInput = form.querySelector('input[name="latitude"]'), lngInput = form.querySelector('input[name="longitude"]');
    const frame = form.querySelector('[data-map-frame]'), link = form.querySelector('[data-map-link]'), empty = form.querySelector('[data-map-empty]');
    if (!latInput || !lngInput || !frame || !link) return;
    const lat = Number(latInput.value), lng = Number(lngInput.value);
    if (!isValidCoordinate(lat, lng)) { frame.style.display = 'none'; frame.removeAttribute('src'); link.style.display = 'none'; link.href = '#'; if (empty) empty.style.display = 'grid'; return; }
    const coordinate = lat + ',' + lng;
    frame.src = 'https://www.google.com/maps?q=' + coordinate + '&z=16&output=embed';
    frame.style.display = 'block';
    link.href = 'https://www.google.com/maps?q=' + coordinate;
    link.style.display = 'inline-flex';
    if (empty) empty.style.display = 'none';
}
document.querySelectorAll('form[data-map-form]').forEach((form) => {
    ['input', 'change'].forEach((eventName) => {
        form.querySelector('input[name="latitude"]')?.addEventListener(eventName, () => updateMapPreview(form));
        form.querySelector('input[name="longitude"]')?.addEventListener(eventName, () => updateMapPreview(form));
    });
    form.addEventListener('reset', () => setTimeout(() => updateMapPreview(form), 0));
    updateMapPreview(form);
});
</script>
</body>
</html>
