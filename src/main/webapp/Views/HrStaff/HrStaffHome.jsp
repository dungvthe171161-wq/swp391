<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*" %>
<%!
    private String homeContractTypeLabel(Object typeObj) {
        String type = typeObj != null ? String.valueOf(typeObj) : "";
        if ("Full-time".equals(type)) return "Toàn thời gian";
        if ("Part-time".equals(type)) return "Bán thời gian";
        if ("Probation".equals(type)) return "Thử việc";
        if ("Intern".equals(type)) return "Thực tập";
        if ("Contract".equals(type)) return "Theo hợp đồng";
        return type.isEmpty() ? "Không có" : type;
    }
%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Bảng điều khiển nhân sự</title>
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.2/css/all.min.css" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hr-staff-shell-20260627-1">
    <style>
        :root {
            --bh-primary: #00482f;
            --bh-primary-strong: #006241;
            --bh-secondary: #00754a;
            --bh-soft-green: #dff6ea;
            --bh-mint: #97f6c0;
            --bh-gold: #cba258;
            --bh-gold-soft: #f6ead1;
            --bh-error: #c82014;
            --bh-error-soft: #fdebea;
            --bh-canvas: #edebe9;
            --bh-surface: #fbf9f4;
            --bh-card: #ffffff;
            --bh-border: #d8d1c7;
            --bh-border-soft: #e7e1d8;
            --bh-text: #1b1c19;
            --bh-muted: #647068;
            --bh-shadow: 0 1px 2px rgba(0, 0, 0, 0.08), 0 14px 30px rgba(30, 57, 50, 0.08);
        }

        * {
            box-sizing: border-box;
        }

        html {
            scroll-behavior: smooth;
        }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: Inter, "Segoe UI", Arial, sans-serif;
            background: var(--bh-canvas);
            color: var(--bh-text);
        }

        a {
            color: inherit;
            text-decoration: none;
        }

        button,
        input {
            font-family: inherit;
        }

        .staff-shell {
            min-height: 100vh;
            display: grid;
            grid-template-columns: 260px minmax(0, 1fr);
        }

        .staff-sidebar {
            position: sticky;
            top: 0;
            height: 100vh;
            padding: 22px 16px;
            background: var(--bh-primary);
            color: #fff;
            display: flex;
            flex-direction: column;
            gap: 24px;
            box-shadow: 1px 0 0 rgba(0, 0, 0, 0.12);
            z-index: 10;
        }

        .brand {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 0 4px;
        }

        .brand-mark {
            width: 42px;
            height: 42px;
            border-radius: 12px;
            display: grid;
            place-items: center;
            background: var(--bh-mint);
            color: var(--bh-primary);
            font-weight: 800;
            box-shadow: inset 0 0 0 1px rgba(0, 72, 47, 0.1);
        }

        .brand strong {
            display: block;
            font-size: 20px;
            letter-spacing: -0.01em;
        }

        .brand span {
            display: block;
            margin-top: 2px;
            color: rgba(255, 255, 255, 0.72);
            font-size: 12px;
        }

        .sidebar-section {
            display: grid;
            gap: 8px;
        }

        .section-title {
            margin: 14px 12px 6px;
            color: rgba(255, 255, 255, 0.64);
            font-size: 12px;
            font-weight: 800;
            letter-spacing: 0.08em;
            text-transform: uppercase;
        }

        .nav-link {
            min-height: 46px;
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px 14px;
            border-radius: 10px;
            color: rgba(255, 255, 255, 0.82);
            font-weight: 700;
            transition: background 0.18s ease, color 0.18s ease, transform 0.18s ease;
        }

        .nav-link i {
            width: 18px;
            text-align: center;
            font-size: 15px;
        }

        .nav-link:hover {
            transform: translateX(2px);
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
        }

        .nav-link.active {
            background: var(--bh-mint);
            color: var(--bh-primary);
        }

        .sidebar-footer {
            margin-top: auto;
            padding: 14px;
            border-radius: 14px;
            background: rgba(255, 255, 255, 0.08);
            border: 1px solid rgba(255, 255, 255, 0.12);
        }

        .sidebar-footer strong {
            display: block;
            font-size: 14px;
        }

        .sidebar-footer span {
            display: block;
            margin-top: 4px;
            color: rgba(255, 255, 255, 0.68);
            font-size: 12px;
        }

        .help-button {
            margin-top: 12px;
            width: 100%;
            min-height: 40px;
            border: 0;
            border-radius: 999px;
            background: #fff;
            color: var(--bh-primary);
            font-weight: 800;
            cursor: pointer;
        }

        .staff-main {
            min-width: 0;
            display: flex;
            flex-direction: column;
        }

        .topbar {
            position: sticky;
            top: 0;
            min-height: 72px;
            padding: 14px 36px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 20px;
            background: rgba(251, 249, 244, 0.92);
            border-bottom: 1px solid var(--bh-border);
            backdrop-filter: blur(14px);
            z-index: 8;
        }

        .topbar-title {
            display: flex;
            align-items: center;
            gap: 12px;
            min-width: 220px;
        }

        .topbar-icon {
            width: 44px;
            height: 44px;
            border-radius: 12px;
            display: grid;
            place-items: center;
            background: var(--bh-soft-green);
            color: var(--bh-primary);
            font-size: 20px;
        }

        .topbar-title h1 {
            margin: 0;
            color: var(--bh-primary);
            font-size: 26px;
            line-height: 1.1;
            letter-spacing: -0.03em;
        }

        .topbar-actions {
            display: flex;
            align-items: center;
            justify-content: flex-end;
            gap: 12px;
            min-width: 0;
            flex: 1;
        }

        .search-box {
            width: min(360px, 100%);
            min-height: 44px;
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 0 14px;
            border: 1px solid var(--bh-border);
            border-radius: 999px;
            background: var(--bh-surface);
            color: var(--bh-muted);
            box-shadow: inset 0 1px 1px rgba(0, 0, 0, 0.04);
        }

        .search-box input {
            width: 100%;
            border: 0;
            outline: 0;
            background: transparent;
            color: var(--bh-text);
            font-weight: 600;
        }

        .icon-button,
        .home-button {
            min-height: 44px;
            border: 1px solid var(--bh-border);
            background: var(--bh-card);
            color: var(--bh-primary);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            font-weight: 800;
            box-shadow: 0 8px 18px rgba(30, 57, 50, 0.06);
        }

        .icon-button {
            width: 44px;
            border-radius: 999px;
            position: relative;
        }

        .home-button {
            padding: 0 18px;
            border-radius: 999px;
        }

        .notification-dot {
            position: absolute;
            top: -4px;
            right: -2px;
            min-width: 18px;
            height: 18px;
            padding: 0 5px;
            border-radius: 999px;
            display: grid;
            place-items: center;
            background: var(--bh-error);
            color: #fff;
            font-size: 11px;
            font-weight: 800;
        }

        .avatar-chip {
            min-height: 46px;
            display: inline-flex;
            align-items: center;
            gap: 12px;
            padding: 5px 14px 5px 5px;
            border: 1px solid var(--bh-border);
            border-radius: 999px;
            background: var(--bh-card);
            box-shadow: 0 8px 18px rgba(30, 57, 50, 0.06);
        }

        .avatar {
            width: 36px;
            height: 36px;
            border-radius: 999px;
            display: grid;
            place-items: center;
            background: var(--bh-soft-green);
            color: var(--bh-primary);
            font-weight: 900;
            box-shadow: inset 0 0 0 2px #fff;
        }

        .avatar-chip strong {
            display: block;
            font-size: 14px;
        }

        .avatar-chip span {
            display: block;
            margin-top: 2px;
            color: var(--bh-muted);
            font-size: 12px;
            font-weight: 700;
        }

        .page-content {
            width: min(1240px, calc(100% - 56px));
            margin: 0 auto;
            padding: 28px 0 40px;
        }

        .hero {
            overflow: hidden;
            position: relative;
            display: grid;
            grid-template-columns: minmax(0, 1fr) auto;
            gap: 24px;
            padding: 34px;
            border-radius: 8px;
            background: linear-gradient(135deg, var(--bh-primary) 0%, var(--bh-primary-strong) 55%, #0f7f55 100%);
            color: #fff;
            box-shadow: var(--bh-shadow);
        }

        .hero::after {
            content: "";
            position: absolute;
            width: 240px;
            height: 240px;
            right: -70px;
            bottom: -120px;
            border-radius: 999px;
            background: rgba(151, 246, 192, 0.18);
        }

        .hero-content,
        .hero-actions {
            position: relative;
            z-index: 1;
        }

        .eyebrow {
            margin: 0 0 8px;
            color: var(--bh-mint);
            font-size: 13px;
            font-weight: 900;
            letter-spacing: 0.08em;
            text-transform: uppercase;
        }

        .hero h2 {
            margin: 0;
            max-width: 720px;
            font-size: clamp(30px, 4vw, 46px);
            line-height: 1.05;
            letter-spacing: -0.04em;
        }

        .hero p {
            margin: 14px 0 0;
            max-width: 720px;
            color: rgba(255, 255, 255, 0.78);
            line-height: 1.7;
        }

        .hero-actions {
            align-self: end;
            display: flex;
            flex-wrap: wrap;
            gap: 10px;
            justify-content: flex-end;
        }

        .pill-button {
            min-height: 44px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            padding: 0 18px;
            border: 1px solid rgba(255, 255, 255, 0.18);
            border-radius: 999px;
            background: #fff;
            color: var(--bh-primary);
            font-weight: 900;
            box-shadow: 0 14px 30px rgba(0, 33, 19, 0.16);
        }

        .pill-button.secondary {
            background: rgba(255, 255, 255, 0.1);
            color: #fff;
            box-shadow: none;
        }

        .metrics {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 16px;
            margin-top: 18px;
        }

        .metric-card {
            display: grid;
            grid-template-columns: auto minmax(0, 1fr);
            gap: 14px;
            align-items: center;
            min-height: 118px;
            padding: 18px;
            border: 1px solid var(--bh-border);
            border-radius: 8px;
            background: var(--bh-card);
            box-shadow: var(--bh-shadow);
        }

        .metric-icon {
            width: 48px;
            height: 48px;
            border-radius: 12px;
            display: grid;
            place-items: center;
            background: var(--bh-soft-green);
            color: var(--bh-primary);
            font-size: 19px;
        }

        .metric-icon.gold {
            background: var(--bh-gold-soft);
            color: #865f13;
        }

        .metric-icon.red {
            background: var(--bh-error-soft);
            color: var(--bh-error);
        }

        .metric-card span {
            color: var(--bh-muted);
            font-size: 13px;
            font-weight: 700;
        }

        .metric-card strong {
            display: block;
            margin-top: 4px;
            color: var(--bh-primary);
            font-size: 28px;
            line-height: 1;
            letter-spacing: -0.04em;
        }

        .layout-grid {
            display: grid;
            grid-template-columns: minmax(0, 1fr) 340px;
            gap: 20px;
            margin-top: 20px;
        }

        .main-column,
        .side-column {
            min-width: 0;
            display: grid;
            gap: 20px;
            align-content: start;
        }

        .panel {
            border: 1px solid var(--bh-border);
            border-radius: 8px;
            background: var(--bh-card);
            box-shadow: var(--bh-shadow);
        }

        .panel-inner {
            padding: 22px;
        }

        .panel-header {
            display: flex;
            align-items: flex-start;
            justify-content: space-between;
            gap: 16px;
            margin-bottom: 18px;
        }

        .panel-title {
            margin: 0;
            color: var(--bh-primary);
            font-size: 20px;
            line-height: 1.2;
            letter-spacing: -0.02em;
        }

        .panel-subtitle {
            margin: 6px 0 0;
            color: var(--bh-muted);
            line-height: 1.5;
            font-size: 14px;
        }

        .panel-link {
            border: 0;
            background: transparent;
            color: var(--bh-primary);
            font-weight: 900;
            white-space: nowrap;
            cursor: pointer;
        }

        .quick-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 14px;
        }

        .quick-card {
            min-height: 174px;
            padding: 18px;
            border: 1px solid var(--bh-border);
            border-radius: 8px;
            background: var(--bh-surface);
            display: flex;
            flex-direction: column;
            gap: 12px;
            transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
        }

        .quick-card:hover {
            transform: translateY(-2px);
            border-color: rgba(0, 98, 65, 0.36);
            box-shadow: 0 18px 30px rgba(30, 57, 50, 0.12);
        }

        .quick-icon {
            width: 42px;
            height: 42px;
            border-radius: 999px;
            display: grid;
            place-items: center;
            background: var(--bh-soft-green);
            color: var(--bh-primary);
        }

        .quick-card strong {
            color: var(--bh-text);
            font-size: 17px;
        }

        .quick-card p {
            margin: 0;
            color: var(--bh-muted);
            line-height: 1.55;
            font-size: 14px;
        }

        .quick-card span {
            margin-top: auto;
            color: var(--bh-primary);
            font-weight: 900;
        }

        .table-tools {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 12px;
            flex-wrap: wrap;
        }

        .filter-form {
            display: flex;
            align-items: center;
            gap: 10px;
            flex-wrap: wrap;
        }

        .filter-form label,
        .filter-form span {
            color: var(--bh-muted);
            font-weight: 700;
            font-size: 14px;
        }

        .input {
            width: 88px;
            min-height: 40px;
            padding: 0 12px;
            border: 1px solid var(--bh-border);
            border-radius: 8px;
            background: var(--bh-surface);
            color: var(--bh-text);
            font-weight: 800;
            outline-color: var(--bh-primary-strong);
        }

        .small-button {
            min-height: 40px;
            padding: 0 14px;
            border: 0;
            border-radius: 999px;
            background: var(--bh-primary);
            color: #fff;
            font-weight: 900;
            cursor: pointer;
        }

        .contract-table {
            width: 100%;
            border-collapse: collapse;
            overflow: hidden;
            border-radius: 8px;
        }

        .contract-table th,
        .contract-table td {
            padding: 14px 16px;
            border-bottom: 1px solid var(--bh-border-soft);
            text-align: left;
            vertical-align: middle;
        }

        .contract-table th {
            background: var(--bh-surface);
            color: var(--bh-muted);
            font-size: 12px;
            font-weight: 900;
            letter-spacing: 0.04em;
            text-transform: uppercase;
        }

        .contract-table td {
            color: var(--bh-text);
            font-weight: 650;
        }

        .contract-table tr:last-child td {
            border-bottom: 0;
        }

        .employee-cell {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .mini-avatar {
            width: 34px;
            height: 34px;
            border-radius: 999px;
            display: grid;
            place-items: center;
            background: var(--bh-soft-green);
            color: var(--bh-primary);
        }

        .status-badge {
            display: inline-flex;
            align-items: center;
            gap: 7px;
            min-height: 30px;
            padding: 0 11px;
            border-radius: 999px;
            background: var(--bh-gold-soft);
            color: #745112;
            font-weight: 900;
            font-size: 12px;
        }

        .empty-state {
            display: grid;
            place-items: center;
            gap: 10px;
            min-height: 170px;
            padding: 28px;
            border: 1px dashed var(--bh-border);
            border-radius: 8px;
            background: var(--bh-surface);
            color: var(--bh-muted);
            text-align: center;
            font-weight: 700;
        }

        .empty-state i {
            width: 46px;
            height: 46px;
            border-radius: 999px;
            display: grid;
            place-items: center;
            background: var(--bh-soft-green);
            color: var(--bh-primary);
            font-size: 18px;
        }

        .progress-card {
            display: grid;
            gap: 16px;
        }

        .progress-circle {
            width: 150px;
            height: 150px;
            margin: 2px auto 0;
            border-radius: 999px;
            display: grid;
            place-items: center;
            background: conic-gradient(var(--bh-primary-strong) 0 85%, var(--bh-gold) 85% 100%);
        }

        .progress-center {
            width: 108px;
            height: 108px;
            border-radius: 999px;
            display: grid;
            place-items: center;
            background: var(--bh-card);
            text-align: center;
        }

        .progress-center strong {
            display: block;
            color: var(--bh-primary);
            font-size: 30px;
            line-height: 1;
        }

        .progress-center span {
            display: block;
            margin-top: 4px;
            color: var(--bh-muted);
            font-size: 12px;
            font-weight: 800;
        }

        .timeline,
        .profile-list,
        .job-list {
            display: grid;
            gap: 12px;
        }

        .timeline-item,
        .profile-item,
        .job-item {
            display: grid;
            grid-template-columns: auto minmax(0, 1fr);
            gap: 12px;
            align-items: start;
            padding: 14px;
            border: 1px solid var(--bh-border-soft);
            border-radius: 8px;
            background: var(--bh-surface);
        }

        .timeline-dot,
        .profile-icon,
        .job-icon {
            width: 34px;
            height: 34px;
            border-radius: 999px;
            display: grid;
            place-items: center;
            background: var(--bh-soft-green);
            color: var(--bh-primary);
        }

        .timeline-item strong,
        .profile-item strong,
        .job-item strong {
            display: block;
            color: var(--bh-text);
            font-size: 14px;
        }

        .timeline-item span,
        .profile-item span,
        .job-item span {
            display: block;
            margin-top: 4px;
            color: var(--bh-muted);
            font-size: 13px;
            line-height: 1.45;
        }

        .job-meta {
            margin-top: 10px;
            display: flex;
            flex-wrap: wrap;
            gap: 8px;
        }

        .tag {
            display: inline-flex;
            align-items: center;
            min-height: 26px;
            padding: 0 9px;
            border-radius: 999px;
            background: #fff;
            border: 1px solid var(--bh-border);
            color: var(--bh-primary);
            font-size: 12px;
            font-weight: 900;
        }

        .cta-card {
            padding: 20px;
            border-radius: 8px;
            background: var(--bh-primary);
            color: #fff;
            box-shadow: var(--bh-shadow);
        }

        .cta-card h3 {
            margin: 0;
            font-size: 20px;
            letter-spacing: -0.02em;
        }

        .cta-card p {
            margin: 10px 0 18px;
            color: rgba(255, 255, 255, 0.76);
            line-height: 1.6;
        }

        .cta-card a {
            min-height: 42px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 10px;
            padding: 0 16px;
            border-radius: 999px;
            background: #fff;
            color: var(--bh-primary);
            font-weight: 900;
        }

        @media (max-width: 1180px) {
            .staff-shell {
                grid-template-columns: 1fr;
            }

            .staff-sidebar {
                position: relative;
                height: auto;
                border-radius: 0 0 18px 18px;
            }

            .sidebar-section {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            .section-title,
            .sidebar-footer {
                grid-column: 1 / -1;
            }

            .layout-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 860px) {
            .topbar {
                position: relative;
                align-items: flex-start;
                flex-direction: column;
                padding: 18px;
            }

            .topbar-actions {
                width: 100%;
                flex-wrap: wrap;
                justify-content: flex-start;
            }

            .page-content {
                width: min(100% - 28px, 1240px);
                padding-top: 18px;
            }

            .hero {
                grid-template-columns: 1fr;
                padding: 24px;
            }

            .hero-actions {
                justify-content: flex-start;
            }

            .metrics,
            .quick-grid {
                grid-template-columns: 1fr;
            }

            .contract-table {
                min-width: 720px;
            }

            .table-wrap {
                overflow-x: auto;
            }
        }

        @media (max-width: 560px) {
            .sidebar-section {
                grid-template-columns: 1fr;
            }

            .avatar-chip span,
            .home-button span {
                display: none;
            }

            .hero h2 {
                font-size: 30px;
            }
        }
    </style>
</head>
<%
    request.setAttribute("hrStaffSidebarActive", "overview");
    request.setAttribute("hrStaffPageTitle", "Nhân viên nhân sự");
    request.setAttribute("hrStaffSearchPlaceholder", "Tìm kiếm công việc...");
    request.setAttribute("hrStaffProfileSubtitle", "Quản trị hồ sơ");
%>
<body class="hr-staff-page-shell">
    <div class="staff-shell">
        <%@ include file="_HrStaffSidebar.jspf" %>

        <main class="staff-main">
            <%@ include file="_HrStaffTopbar.jspf" %>

            <section class="page-content">
                <section class="hero">
                    <div class="hero-content">
                        <p class="eyebrow">Cổng nhân sự BetterHR</p>
                        <h2>Chào mừng bạn đến với hệ thống nhân sự BetterHR</h2>
                        <p>Theo dõi đơn ứng tuyển, chăm sóc hợp đồng và cập nhật hồ sơ nhân sự trong một không gian làm việc rõ ràng, gọn gàng.</p>
                    </div>
                    <div class="hero-actions">
                        <a class="pill-button" href="${pageContext.request.contextPath}/postRecruitments">
                            <i class="fa-solid fa-plus"></i>
                            Đăng tin tuyển dụng
                        </a>
                        <a class="pill-button secondary" href="${pageContext.request.contextPath}/candidates">
                            <i class="fa-solid fa-user-check"></i>
                            Cập nhật hồ sơ
                        </a>
                    </div>
                </section>

                <section class="metrics" aria-label="Chỉ số nhanh">
                    <article class="metric-card">
                        <div class="metric-icon"><i class="fa-solid fa-users"></i></div>
                        <div>
                            <span>Tổng nhân viên</span>
                            <strong><%= request.getAttribute("totalEmployees") != null ? request.getAttribute("totalEmployees") : 0 %></strong>
                        </div>
                    </article>
                    <article class="metric-card">
                        <div class="metric-icon gold"><i class="fa-solid fa-hourglass-half"></i></div>
                        <div>
                            <span>Hợp đồng chờ duyệt</span>
                            <strong><%= request.getAttribute("pendingContractsCount") != null ? request.getAttribute("pendingContractsCount") : 0 %></strong>
                        </div>
                    </article>
                    <article class="metric-card">
                        <div class="metric-icon"><i class="fa-solid fa-circle-check"></i></div>
                        <div>
                            <span>Hợp đồng hiệu lực</span>
                            <strong><%= request.getAttribute("approvedContractsCount") != null ? request.getAttribute("approvedContractsCount") : 0 %></strong>
                        </div>
                    </article>
                    <article class="metric-card">
                        <div class="metric-icon red"><i class="fa-solid fa-triangle-exclamation"></i></div>
                        <div>
                            <span>Sắp hết hạn</span>
                            <strong>
                                <%
                                    List expiringContractsForMetric = (List) request.getAttribute("expiringContracts");
                                    out.print(expiringContractsForMetric != null ? expiringContractsForMetric.size() : 0);
                                %>
                            </strong>
                        </div>
                    </article>
                </section>

                <div class="layout-grid">
                    <div class="main-column">
                        <section class="panel" aria-labelledby="quick-title">
                            <div class="panel-inner">
                                <div class="panel-header">
                                    <div>
                                        <h2 class="panel-title" id="quick-title">Khu vực thao tác nhanh</h2>
                                        <p class="panel-subtitle">Các nghiệp vụ nhân sự dùng thường xuyên.</p>
                                    </div>
                                </div>
                                <div class="quick-grid">
                                    <a class="quick-card" href="${pageContext.request.contextPath}/postRecruitments">
                                        <div class="quick-icon"><i class="fa-solid fa-bullhorn"></i></div>
                                        <strong>Đăng tin tuyển dụng</strong>
                                        <p>Tạo và quản lý vị trí đang tuyển dụng cho ứng viên.</p>
                                        <span>Mở đợt tuyển dụng <i class="fa-solid fa-arrow-right"></i></span>
                                    </a>
                                    <a class="quick-card" href="${pageContext.request.contextPath}/candidates">
                                        <div class="quick-icon"><i class="fa-solid fa-user-plus"></i></div>
                                        <strong>Cập nhật hồ sơ</strong>
                                        <p>Xem ứng viên, chuyển hồ sơ đủ điều kiện sang xử lý tiếp theo.</p>
                                        <span>Xem danh sách ứng viên <i class="fa-solid fa-arrow-right"></i></span>
                                    </a>
                                    <a class="quick-card" href="<%=request.getContextPath()%>/hrstaff/payroll">
                                        <div class="quick-icon"><i class="fa-solid fa-money-bill-trend-up"></i></div>
                                        <strong>Lương & phụ cấp</strong>
                                        <p>Quản lý phụ cấp, khấu trừ và lập bảng lương định kỳ.</p>
                                        <span>Mở bảng lương <i class="fa-solid fa-arrow-right"></i></span>
                                    </a>
                                </div>
                            </div>
                        </section>

                        <section class="panel" id="contracts">
                            <div class="panel-inner">
                                <div class="panel-header table-tools">
                                    <div>
                                        <h2 class="panel-title">Hợp đồng sắp hết hạn</h2>
                                        <p class="panel-subtitle">Theo dõi các hợp đồng cần gia hạn trong khoảng thời gian đã chọn.</p>
                                    </div>
                                    <form class="filter-form" method="get" action="<%=request.getContextPath()%>/hrstaff">
                                        <label for="days">Trong</label>
                                        <input class="input" type="number" id="days" name="days" value="<%= request.getAttribute("expiringDays") != null ? request.getAttribute("expiringDays") : 30 %>" min="1">
                                        <span>ngày</span>
                                        <button class="small-button" type="submit">Lọc</button>
                                    </form>
                                </div>

                                <%
                                    List expiringContracts = (List) request.getAttribute("expiringContracts");
                                    if (expiringContracts == null || expiringContracts.isEmpty()) {
                                %>
                                    <div class="empty-state">
                                        <i class="fa-solid fa-calendar-check"></i>
                                        <div>Hiện chưa có hợp đồng nào sắp hết hạn.</div>
                                    </div>
                                <%
                                    } else {
                                %>
                                    <div class="table-wrap">
                                        <table class="contract-table">
                                            <thead>
                                                <tr>
                                                    <th>Nhân viên</th>
                                                    <th>Loại hợp đồng</th>
                                                    <th>Ngày bắt đầu</th>
                                                    <th>Ngày kết thúc</th>
                                                    <th>Trạng thái</th>
                                                </tr>
                                            </thead>
                                            <tbody>
                                            <%
                                                for (Object rowObj : expiringContracts) {
                                                    Map row = (Map) rowObj;
                                            %>
                                                <tr>
                                                    <td>
                                                        <div class="employee-cell">
                                                            <div class="mini-avatar"><i class="fa-solid fa-user"></i></div>
                                                            <strong><%= row.get("employeeName") %></strong>
                                                        </div>
                                                    </td>
                                                    <td><%= homeContractTypeLabel(row.get("contractType")) %></td>
                                                    <td><%= row.get("startDate") %></td>
                                                    <td><%= row.get("endDate") %></td>
                                                    <td><span class="status-badge"><i class="fa-solid fa-clock"></i>Cần theo dõi</span></td>
                                                </tr>
                                            <%
                                                }
                                            %>
                                            </tbody>
                                        </table>
                                    </div>
                                <%
                                    }
                                %>
                            </div>
                        </section>

                        <section class="panel">
                            <div class="panel-inner">
                                <div class="panel-header">
                                    <div>
                                        <h2 class="panel-title">Đề xuất tuyển dụng</h2>
                                        <p class="panel-subtitle">Các vị trí nên được ưu tiên cập nhật hồ sơ trong hôm nay.</p>
                                    </div>
                                    <a class="panel-link" href="${pageContext.request.contextPath}/postRecruitments">Xem tất cả</a>
                                </div>
                                <div class="job-list">
                                    <article class="job-item">
                                        <div class="job-icon"><i class="fa-solid fa-pen-nib"></i></div>
                                        <div>
                                            <strong>Trưởng nhóm thiết kế UX</strong>
                                            <span>Phòng vận hành thiết kế cần hồ sơ phù hợp cho vòng phỏng vấn tiếp theo.</span>
                                            <div class="job-meta">
                                                <span class="tag">Toàn thời gian</span>
                                                <span class="tag">Ưu tiên cao</span>
                                            </div>
                                        </div>
                                    </article>
                                    <article class="job-item">
                                        <div class="job-icon"><i class="fa-solid fa-code"></i></div>
                                        <div>
                                            <strong>Lập trình viên Java cao cấp</strong>
                                            <span>Đội kỹ thuật đang cần bổ sung ứng viên có kinh nghiệm hệ thống.</span>
                                            <div class="job-meta">
                                                <span class="tag">Java</span>
                                                <span class="tag">Hệ thống</span>
                                            </div>
                                        </div>
                                    </article>
                                </div>
                            </div>
                        </section>
                    </div>

                    <aside class="side-column">
                        <section class="panel">
                            <div class="panel-inner progress-card">
                                <div>
                                    <h2 class="panel-title">Tiến độ hồ sơ</h2>
                                    <p class="panel-subtitle">Tỷ lệ hồ sơ đã kiểm tra trong kỳ làm việc hiện tại.</p>
                                </div>
                                <div class="progress-circle">
                                    <div class="progress-center">
                                        <div>
                                            <strong>85%</strong>
                                            <span>Hoàn thành</span>
                                        </div>
                                    </div>
                                </div>
                                <div class="profile-list">
                                    <article class="profile-item">
                                        <div class="profile-icon"><i class="fa-solid fa-check"></i></div>
                                        <div>
                                            <strong>Hồ sơ đã cập nhật</strong>
                                            <span>Đã rà soát thông tin cơ bản và trạng thái liên hệ.</span>
                                        </div>
                                    </article>
                                    <article class="profile-item">
                                        <div class="profile-icon"><i class="fa-solid fa-hourglass-half"></i></div>
                                        <div>
                                            <strong>Hồ sơ cần bổ sung</strong>
                                            <span>Ưu tiên các hồ sơ còn thiếu email hoặc số điện thoại.</span>
                                        </div>
                                    </article>
                                </div>
                            </div>
                        </section>

                        <section class="cta-card">
                            <h3>Duyệt phỏng vấn sắp tới</h3>
                            <p>Vị trí Quản lý sản phẩm đang có lịch phỏng vấn vòng 2. Hãy kiểm tra hồ sơ và cập nhật kết quả sau buổi phỏng vấn.</p>
                            <a href="${pageContext.request.contextPath}/candidates">
                                Tham gia họp
                                <i class="fa-solid fa-arrow-right"></i>
                            </a>
                        </section>

                        <section class="panel">
                            <div class="panel-inner">
                                <div class="panel-header">
                                    <div>
                                        <h2 class="panel-title">Thông báo</h2>
                                        <p class="panel-subtitle">Các nhắc việc quan trọng trong ngày.</p>
                                    </div>
                                </div>
                                <div class="timeline">
                                    <article class="timeline-item">
                                        <div class="timeline-dot"><i class="fa-solid fa-file-circle-check"></i></div>
                                        <div>
                                            <strong>Làm mới phụ lục hợp đồng</strong>
                                            <span>Kiểm tra danh sách hợp đồng sắp hết hạn trước cuối ngày.</span>
                                        </div>
                                    </article>
                                    <article class="timeline-item">
                                        <div class="timeline-dot"><i class="fa-solid fa-user-clock"></i></div>
                                        <div>
                                            <strong>Ứng viên chờ phản hồi</strong>
                                            <span>Cập nhật trạng thái ứng viên sau phỏng vấn để tránh trễ lịch.</span>
                                        </div>
                                    </article>
                                    <article class="timeline-item">
                                        <div class="timeline-dot"><i class="fa-solid fa-money-check"></i></div>
                                        <div>
                                            <strong>Đối chiếu bảng lương</strong>
                                            <span>Kiểm tra phụ cấp và khấu trừ trước khi gửi duyệt.</span>
                                        </div>
                                    </article>
                                </div>
                            </div>
                        </section>
                    </aside>
                </div>
            </section>
        </main>
    </div>
</body>
</html>
