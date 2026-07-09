<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Ch&#7845;m c&#244;ng</title>
    <%@ include file="_EmployeeStyles.jspf" %>
    <style>
        .gps-map-frame { width: 100%; height: 360px; min-height: 320px; max-height: 400px; border: 0; border-radius: 14px; display: block; }
        .gps-location-panel { grid-column: 1 / -1; overflow: hidden; }
        .gps-location-panel .panel-inner { overflow: hidden; }
        .gps-office-layout { display: grid; grid-template-columns: minmax(320px, 1fr) minmax(280px, 420px); gap: 24px; align-items: stretch; }
        .gps-office-map, .gps-office-info { min-width: 0; }
        .gps-info-list { display: grid; min-width: 0; }
        .gps-info-row { display: grid; grid-template-columns: 140px minmax(0, 1fr); gap: 12px; padding: 12px 0; border-bottom: 1px solid rgba(0,0,0,.08); }
        .gps-info-row span { color: #6f7772; font-weight: 700; }
        .gps-info-value { min-width: 0; color: var(--bh-primary, #00482f); font-weight: 800; text-align: right; word-break: break-word; overflow-wrap: anywhere; }
        .gps-link-row { display: flex; gap: 14px; flex-wrap: wrap; margin-top: 12px; }
        .gps-map-button { min-height: 38px; display: inline-flex; align-items: center; justify-content: center; padding: 0 14px; border-radius: 8px; background: var(--bh-primary, #00482f); color: #fff; font-weight: 700; text-decoration: none; max-width: 100%; text-align: center; white-space: normal; }
        .gps-map-button.secondary { background: #f3f0ea; color: var(--bh-primary, #00482f); border: 1px solid rgba(0,0,0,.12); }
        @media (max-width: 900px) {
            .gps-office-layout { grid-template-columns: 1fr; }
            .gps-map-frame { height: 320px; }
            .gps-info-row { grid-template-columns: 1fr; gap: 4px; }
            .gps-info-value { text-align: left; }
        }
    </style>
</head>
<body>
<div class="employee-shell">
    <%@ include file="_EmployeeSidebar.jspf" %>
    <main class="employee-main">
        <%@ include file="_EmployeeTopbar.jspf" %>
        <div class="content">
            <h1 class="page-title">Ch&#7845;m c&#244;ng</h1>
            <p class="page-note">V&#224;o ca / ra ca &#273;&#432;&#7907;c l&#432;u trong b&#7843;ng Attendance v&#224; d&#249;ng cho t&#237;nh l&#432;&#417;ng.</p>

            <c:if test="${not empty employeeSuccess}"><div class="alert success">${employeeSuccess}</div></c:if>
            <c:if test="${not empty employeeError}"><div class="alert error">${employeeError}</div></c:if>
            <c:if test="${not empty gpsWarning}"><div class="alert error">${gpsWarning}</div></c:if>
            <div id="gps-client-message" class="alert error" style="display:none;"></div>
            <div id="gps-client-success" class="alert success" style="display:none;"></div>

            <div class="grid-2">
                <section class="panel gps-location-panel">
                    <div class="panel-inner">
                        <h2 class="page-title" style="font-size:22px;">V&#7883; tr&#237; ch&#7845;m c&#244;ng</h2>
                        <c:choose>
                            <c:when test="${not empty officeLocation}">
                                <div id="office-location-data"
                                     data-latitude="${officeLocation.latitude}"
                                     data-longitude="${officeLocation.longitude}"
                                     data-radius="${officeLocation.radiusMeters}"></div>
                                <div class="gps-office-layout">
                                    <div class="gps-office-map">
                                        <iframe class="gps-map-frame" loading="lazy" referrerpolicy="no-referrer-when-downgrade"
                                                src="https://www.google.com/maps?q=${officeLocation.latitude},${officeLocation.longitude}&amp;z=16&amp;output=embed"
                                                title="Vi tri cong ty tren Google Maps"></iframe>
                                    </div>
                                    <div class="gps-office-info">
                                        <div class="gps-info-list">
                                            <div class="gps-info-row"><span>T&#234;n v&#259;n ph&#242;ng</span><strong class="gps-info-value">${officeLocation.locationName}</strong></div>
                                            <div class="gps-info-row"><span>&#272;&#7883;a ch&#7881;</span><strong class="gps-info-value">${officeLocation.address}</strong></div>
                                            <div class="gps-info-row"><span>Latitude</span><strong class="gps-info-value">${officeLocation.latitude}</strong></div>
                                            <div class="gps-info-row"><span>Longitude</span><strong class="gps-info-value">${officeLocation.longitude}</strong></div>
                                            <div class="gps-info-row"><span>RadiusMeters</span><strong class="gps-info-value">${officeLocation.radiusMeters}m</strong></div>
                                        </div>
                                        <div class="gps-link-row page-note">
                                            <a class="gps-map-button" target="_blank" rel="noopener"
                                               href="https://www.google.com/maps?q=${officeLocation.latitude},${officeLocation.longitude}">
                                                M&#7903; v&#7883; tr&#237; c&#244;ng ty tr&#234;n Google Maps
                                            </a>
                                        </div>
                                    </div>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="page-note">Ch&#432;a c&#7845;u h&#236;nh &#273;&#7883;a &#273;i&#7875;m v&#259;n ph&#242;ng h&#7907;p l&#7879; &#273;&#7875; ch&#7845;m c&#244;ng GPS.</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </section>

                <section class="panel">
                    <div class="panel-inner">
                        <h2 class="page-title" style="font-size:22px;">L&#7883;ch l&#224;m vi&#7879;c h&#244;m nay</h2>
                        <c:choose>
                            <c:when test="${not empty todaySchedule}">
                                <div class="stat-row">
                                    <div class="stat"><span>T&#234;n ca</span><strong>${todaySchedule.scheduleName}</strong></div>
                                    <div class="stat"><span>Gi&#7901; b&#7855;t &#273;&#7847;u</span><strong>${todaySchedule.startTime}</strong></div>
                                    <div class="stat"><span>Gi&#7901; k&#7871;t th&#250;c</span><strong>${todaySchedule.endTime}</strong></div>
                                </div>
                            </c:when>
                            <c:otherwise><p class="page-note">H&#244;m nay b&#7841;n ch&#432;a c&#243; l&#7883;ch l&#224;m vi&#7879;c &#273;&#432;&#7907;c ph&#226;n c&#244;ng.</p></c:otherwise>
                        </c:choose>
                    </div>
                </section>
            </div>

            <section class="panel" style="margin-top:22px;">
                <div class="panel-inner">
                    <h2 class="page-title" style="font-size:22px;">V&#7883; tr&#237; hi&#7879;n t&#7841;i c&#7911;a t&#244;i</h2>
                    <div class="stat-row">
                        <div class="stat"><span>Latitude</span><strong id="current-latitude">Ch&#432;a l&#7845;y v&#7883; tr&#237;</strong></div>
                        <div class="stat"><span>Longitude</span><strong id="current-longitude">Ch&#432;a l&#7845;y v&#7883; tr&#237;</strong></div>
                        <div class="stat"><span>Kho&#7843;ng c&#225;ch</span><strong id="current-distance">N/A</strong></div>
                    </div>
                    <div class="gps-link-row page-note">
                        <a id="my-location-link" class="gps-map-button secondary" href="#" target="_blank" rel="noopener" style="display:none;">M&#7903; v&#7883; tr&#237; c&#7911;a t&#244;i tr&#234;n Google Maps</a>
                        <a id="directions-link" class="gps-map-button" href="#" target="_blank" rel="noopener" style="display:none;">Ch&#7881; &#273;&#432;&#7901;ng &#273;&#7871;n v&#259;n ph&#242;ng</a>
                    </div>
                    <iframe id="my-location-map" class="gps-map-frame" loading="lazy" referrerpolicy="no-referrer-when-downgrade" style="display:none;" title="Vi tri cua toi tren Google Maps"></iframe>
                </div>
            </section>

            <div class="grid-2" style="margin-top:22px;">
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
                            <form method="post" action="${pageContext.request.contextPath}/employee/attendance" data-gps-form>
                                <input type="hidden" name="action" value="checkIn"><input type="hidden" name="latitude"><input type="hidden" name="longitude">
                                <button class="primary-button" type="${todayAttendanceStatus eq 'ChuaVaoCa' ? 'submit' : 'button'}" ${todayAttendanceStatus eq 'ChuaVaoCa' ? '' : 'disabled'}><i class="fa-solid fa-right-to-bracket"></i> V&#224;o ca</button>
                            </form>
                            <form method="post" action="${pageContext.request.contextPath}/employee/attendance" data-gps-form>
                                <input type="hidden" name="action" value="checkOut"><input type="hidden" name="latitude"><input type="hidden" name="longitude">
                                <button class="secondary-button" type="${todayAttendanceStatus eq 'DaVaoCa' ? 'submit' : 'button'}" ${todayAttendanceStatus eq 'DaVaoCa' ? '' : 'disabled'}><i class="fa-solid fa-right-from-bracket"></i> Ra ca</button>
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
                        <thead><tr><th>Ng&#224;y</th><th>V&#224;o ca</th><th>Ra ca</th><th>Kho&#7843;ng c&#225;ch v&#224;o ca</th><th>Kho&#7843;ng c&#225;ch ra ca</th><th>Gi&#7901; c&#244;ng</th><th>T&#259;ng ca</th></tr></thead>
                        <tbody>
                        <c:forEach var="item" items="${recentAttendances}">
                            <tr>
                                <td>${item.date}</td><td>${item.checkIn}</td><td>${item.checkOut}</td>
                                <td><c:choose><c:when test="${empty item.checkInDistanceMeters}">Ch&#432;a c&#243;</c:when><c:when test="${item.checkInDistanceMeters lt 1000}"><fmt:formatNumber value="${item.checkInDistanceMeters}" maxFractionDigits="0" /> m</c:when><c:otherwise><fmt:formatNumber value="${item.checkInDistanceMeters / 1000}" minFractionDigits="2" maxFractionDigits="2" /> km</c:otherwise></c:choose></td>
                                <td><c:choose><c:when test="${empty item.checkOutDistanceMeters}">Ch&#432;a c&#243;</c:when><c:when test="${item.checkOutDistanceMeters lt 1000}"><fmt:formatNumber value="${item.checkOutDistanceMeters}" maxFractionDigits="0" /> m</c:when><c:otherwise><fmt:formatNumber value="${item.checkOutDistanceMeters / 1000}" minFractionDigits="2" maxFractionDigits="2" /> km</c:otherwise></c:choose></td>
                                <td>${item.workingHours}</td><td>${item.overtimeHours}</td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty recentAttendances}"><tr><td colspan="7">Ch&#432;a c&#243; d&#7919; li&#7879;u ch&#7845;m c&#244;ng.</td></tr></c:if>
                        </tbody>
                    </table>
                </div>
            </section>
        </div>
    </main>
</div>
<script>
    const officeData = document.getElementById('office-location-data');
    const gpsForms = document.querySelectorAll('form[data-gps-form]');
    const gpsError = document.getElementById('gps-client-message');
    const gpsSuccess = document.getElementById('gps-client-success');
    const currentLatitude = document.getElementById('current-latitude');
    const currentLongitude = document.getElementById('current-longitude');
    const currentDistance = document.getElementById('current-distance');
    const myLocationLink = document.getElementById('my-location-link');
    const directionsLink = document.getElementById('directions-link');
    const myLocationMap = document.getElementById('my-location-map');
    function showGpsError(message) { gpsSuccess.style.display = 'none'; gpsError.textContent = message; gpsError.style.display = 'block'; }
    function showGpsSuccess(message) { gpsError.style.display = 'none'; gpsSuccess.textContent = message; gpsSuccess.style.display = 'block'; }
    function toRadians(value) { return value * Math.PI / 180; }
    function distanceMeters(lat1, lon1, lat2, lon2) {
        const earthRadiusMeters = 6371000;
        const dLat = toRadians(lat2 - lat1);
        const dLon = toRadians(lon2 - lon1);
        const a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
            + Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2))
            * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        return earthRadiusMeters * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
    gpsForms.forEach((form) => {
        form.addEventListener('submit', (event) => {
            event.preventDefault();
            const button = form.querySelector('button[type="submit"]');
            if (!navigator.geolocation || !officeData) { showGpsError('B&#7841;n c&#7847;n b&#7853;t &#273;&#7883;nh v&#7883; &#273;&#7875; ch&#7845;m c&#244;ng GPS.'); return; }
            const originalText = button.innerHTML;
            button.disabled = true; button.innerHTML = '&#272;ang l&#7845;y v&#7883; tr&#237;...';
            navigator.geolocation.getCurrentPosition((position) => {
                const lat = position.coords.latitude, lng = position.coords.longitude;
                const officeLat = Number(officeData.dataset.latitude), officeLng = Number(officeData.dataset.longitude);
                const radiusMeters = Number(officeData.dataset.radius);
                const distance = distanceMeters(lat, lng, officeLat, officeLng);
                form.querySelector('input[name="latitude"]').value = lat;
                form.querySelector('input[name="longitude"]').value = lng;
                currentLatitude.textContent = lat.toFixed(7);
                currentLongitude.textContent = lng.toFixed(7);
                currentDistance.textContent = distance.toFixed(2) + 'm';
                myLocationLink.href = 'https://www.google.com/maps?q=' + lat + ',' + lng;
                myLocationLink.style.display = 'inline-flex';
                directionsLink.href = 'https://www.google.com/maps/dir/?api=1&origin=' + lat + ',' + lng + '&destination=' + officeLat + ',' + officeLng;
                directionsLink.style.display = 'inline-flex';
                myLocationMap.src = 'https://www.google.com/maps?q=' + lat + ',' + lng + '&z=16&output=embed';
                myLocationMap.style.display = 'block';
                if (distance <= radiusMeters) showGpsSuccess('B&#7841;n &#273;ang trong ph&#7841;m vi ch&#7845;m c&#244;ng.'); else showGpsError('B&#7841;n &#273;ang ngo&#224;i ph&#7841;m vi ch&#7845;m c&#244;ng.');
                form.submit();
            }, () => {
                button.disabled = false; button.innerHTML = originalText;
                showGpsError('B&#7841;n c&#7847;n b&#7853;t &#273;&#7883;nh v&#7883; &#273;&#7875; ch&#7845;m c&#244;ng GPS.');
            }, { enableHighAccuracy: true, timeout: 15000, maximumAge: 0 });
        });
    });
</script>
</body>
</html>
