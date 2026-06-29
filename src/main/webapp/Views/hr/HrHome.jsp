<%-- 
    Document   : HrHome
    Created on : Oct 20, 2025, 2:11:22 PM
    Author     : admin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page import="java.util.*, java.sql.Date, java.text.SimpleDateFormat, java.math.BigDecimal, java.text.NumberFormat, java.util.Locale" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html lang="vi">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>HR Home - BetterHR</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-dashboard.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-dashboard-backup.css">
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
        
        <!-- Fallback CSS - Inline styles to ensure HR Dashboard always looks good -->
        <style>
            /* Critical CSS Fallback */
            .hr-dashboard-container {
                min-height: 100vh !important;
                background-color: #f8f9fa !important;
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif !important;
            }
            .hr-header {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                color: white !important;
                padding: 1rem 0 !important;
                box-shadow: 0 2px 10px rgba(0,0,0,0.1) !important;
            }
            .header-content {
                max-width: 1400px !important;
                margin: 0 auto !important;
                padding: 0 2rem !important;
                display: flex !important;
                justify-content: space-between !important;
                align-items: center !important;
            }
            .logo-section {
                display: flex !important;
                align-items: center !important;
                gap: 1rem !important;
            }
            .logo-section i {
                font-size: 2rem !important;
            }
            .logo-section h1 {
                font-size: 1.8rem !important;
                font-weight: 600 !important;
                margin: 0 !important;
            }
            .hr-main-content {
                display: flex !important;
                max-width: 1400px !important;
                margin: 0 auto !important;
                padding: 2rem !important;
                gap: 2rem !important;
            }
            .hr-sidebar {
                width: 280px !important;
                background: white !important;
                border-radius: 12px !important;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1) !important;
                padding: 1.5rem !important;
                height: fit-content !important;
            }
            .hr-content-area {
                flex: 1 !important;
                background: white !important;
                border-radius: 12px !important;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1) !important;
                padding: 2rem !important;
                min-height: 600px !important;
            }
            .content-section {
                display: none !important;
            }
            .content-section.active {
                display: block !important;
            }
            .nav-item {
                display: flex !important;
                align-items: center !important;
                gap: 0.75rem !important;
                padding: 0.75rem 1rem !important;
                border-radius: 8px !important;
                text-decoration: none !important;
                color: #64748b !important;
                transition: all 0.3s ease !important;
                font-weight: 500 !important;
                margin-bottom: 0.5rem !important;
            }
            .nav-item:hover {
                background: #f1f5f9 !important;
                color: #2563eb !important;
                transform: translateX(4px) !important;
            }
            .nav-item.active {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                color: white !important;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4) !important;
            }
            .stats-grid {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)) !important;
                gap: 1.5rem !important;
                margin-bottom: 2rem !important;
            }
            .stat-card {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                color: white !important;
                padding: 1.5rem !important;
                border-radius: 12px !important;
                display: flex !important;
                align-items: center !important;
                gap: 1rem !important;
                box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3) !important;
                transition: transform 0.3s ease !important;
            }
            .stat-card:hover {
                transform: translateY(-4px) !important;
            }
            
            /* HR Home Section Styles */
            .welcome-content {
                padding: 2rem 0 !important;
            }
            
            .welcome-card {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                color: white !important;
                padding: 2rem !important;
                border-radius: 12px !important;
                display: flex !important;
                align-items: center !important;
                gap: 2rem !important;
                margin-bottom: 2rem !important;
                box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3) !important;
            }
            
            .welcome-icon {
                width: 80px !important;
                height: 80px !important;
                background: rgba(255,255,255,0.2) !important;
                border-radius: 50% !important;
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                flex-shrink: 0 !important;
            }
            
            .welcome-icon i {
                font-size: 2.5rem !important;
            }
            
            .welcome-text h3 {
                font-size: 1.8rem !important;
                margin: 0 0 1rem 0 !important;
                font-weight: 700 !important;
            }
            
            .welcome-text p {
                font-size: 1.1rem !important;
                margin: 0 !important;
                opacity: 0.9 !important;
                line-height: 1.6 !important;
            }
            
            .quick-actions {
                margin-bottom: 2rem !important;
            }
            
            .quick-actions h3 {
                font-size: 1.5rem !important;
                color: #1e293b !important;
                margin-bottom: 1.5rem !important;
                font-weight: 600 !important;
            }
            
            .action-grid {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)) !important;
                gap: 1.5rem !important;
            }
            
            .action-card {
                background: white !important;
                border: 1px solid #e5e7eb !important;
                border-radius: 12px !important;
                padding: 1.5rem !important;
                text-decoration: none !important;
                color: inherit !important;
                transition: all 0.3s ease !important;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05) !important;
            }
            
            .action-card:hover {
                transform: translateY(-4px) !important;
                box-shadow: 0 8px 25px rgba(0,0,0,0.15) !important;
                border-color: #667eea !important;
            }
            
            .action-card i {
                font-size: 2rem !important;
                color: #667eea !important;
                margin-bottom: 1rem !important;
                display: block !important;
            }
            
            .action-card h4 {
                font-size: 1.2rem !important;
                margin: 0 0 0.5rem 0 !important;
                color: #1e293b !important;
                font-weight: 600 !important;
            }
            
            .action-card p {
                margin: 0 !important;
                color: #64748b !important;
                font-size: 0.9rem !important;
                line-height: 1.5 !important;
            }
            
            .system-info h3 {
                font-size: 1.5rem !important;
                color: #1e293b !important;
                margin-bottom: 1.5rem !important;
                font-weight: 600 !important;
            }
            
            .info-grid {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)) !important;
                gap: 1rem !important;
            }
            
            .info-card {
                background: #f8fafc !important;
                border: 1px solid #e2e8f0 !important;
                border-radius: 8px !important;
                padding: 1.5rem !important;
                text-align: center !important;
                transition: all 0.3s ease !important;
            }
            
            .info-card:hover {
                background: #f1f5f9 !important;
                border-color: #cbd5e1 !important;
            }
            
            .info-card i {
                font-size: 1.5rem !important;
                color: #667eea !important;
                margin-bottom: 0.5rem !important;
                display: block !important;
            }
            
            .info-card h4 {
                font-size: 1rem !important;
                margin: 0 0 0.5rem 0 !important;
                color: #1e293b !important;
                font-weight: 600 !important;
            }
            
            .info-card p {
                margin: 0 !important;
                color: #64748b !important;
                font-size: 0.9rem !important;
            }
            
            /* HR Functions Section */
            .hr-functions {
                margin-bottom: 2rem !important;
            }
            
            .hr-functions h3 {
                font-size: 1.5rem !important;
                color: #1e293b !important;
                margin-bottom: 1.5rem !important;
                font-weight: 600 !important;
            }
            
            .function-grid {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)) !important;
                gap: 1.5rem !important;
            }
            
            .function-card {
                background: white !important;
                border: 1px solid #e5e7eb !important;
                border-radius: 12px !important;
                padding: 1.5rem !important;
                transition: all 0.3s ease !important;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05) !important;
            }
            
            .function-card:hover {
                transform: translateY(-4px) !important;
                box-shadow: 0 8px 25px rgba(0,0,0,0.15) !important;
                border-color: #667eea !important;
            }
            
            .function-icon {
                width: 60px !important;
                height: 60px !important;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                border-radius: 12px !important;
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                margin-bottom: 1rem !important;
            }
            
            .function-icon i {
                font-size: 1.5rem !important;
                color: white !important;
            }
            
            .function-content h4 {
                font-size: 1.3rem !important;
                margin: 0 0 0.5rem 0 !important;
                color: #1e293b !important;
                font-weight: 600 !important;
            }
            
            .function-content p {
                margin: 0 0 1rem 0 !important;
                color: #64748b !important;
                font-size: 0.95rem !important;
                line-height: 1.6 !important;
            }
            
            .function-link {
                display: inline-flex !important;
                align-items: center !important;
                gap: 0.5rem !important;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                color: white !important;
                padding: 0.5rem 1rem !important;
                border-radius: 6px !important;
                text-decoration: none !important;
                font-size: 0.9rem !important;
                font-weight: 500 !important;
                transition: all 0.3s ease !important;
            }
            
            .function-link:hover {
                transform: translateX(4px) !important;
                box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3) !important;
            }
            
            /* System Overview Section */
            .system-overview h3 {
                font-size: 1.5rem !important;
                color: #1e293b !important;
                margin-bottom: 1.5rem !important;
                font-weight: 600 !important;
            }
            
            .overview-grid {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)) !important;
                gap: 1rem !important;
            }
            
            .overview-card {
                background: #f8fafc !important;
                border: 1px solid #e2e8f0 !important;
                border-radius: 12px !important;
                padding: 1.5rem !important;
                text-align: center !important;
                transition: all 0.3s ease !important;
            }
            
            .overview-card:hover {
                background: #f1f5f9 !important;
                border-color: #667eea !important;
                transform: translateY(-2px) !important;
            }
            
            .overview-icon {
                width: 50px !important;
                height: 50px !important;
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                border-radius: 50% !important;
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                margin: 0 auto 1rem auto !important;
            }
            
            .overview-icon i {
                font-size: 1.2rem !important;
                color: white !important;
            }
            
            .overview-info h4 {
                font-size: 1rem !important;
                margin: 0 0 0.5rem 0 !important;
                color: #1e293b !important;
                font-weight: 600 !important;
            }
            
            .overview-number {
                display: block !important;
                font-size: 2rem !important;
                font-weight: 700 !important;
                color: #667eea !important;
                margin-bottom: 0.5rem !important;
            }
            
            .overview-status {
                display: block !important;
                font-size: 1.2rem !important;
                font-weight: 600 !important;
                color: #10b981 !important;
                margin-bottom: 0.5rem !important;
            }
            
            .overview-info p {
                margin: 0 !important;
                color: #64748b !important;
                font-size: 0.85rem !important;
            }
            
            /* Comprehensive Overview Styles */
            .comprehensive-overview {
                padding: 2rem 0 !important;
            }
            
            .overview-welcome {
                background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
                color: white !important;
                padding: 2rem !important;
                border-radius: 12px !important;
                display: flex !important;
                align-items: center !important;
                gap: 2rem !important;
                margin-bottom: 2rem !important;
                box-shadow: 0 4px 20px rgba(102, 126, 234, 0.3) !important;
            }
            
            .overview-welcome .welcome-icon {
                width: 80px !important;
                height: 80px !important;
                background: rgba(255,255,255,0.2) !important;
                border-radius: 50% !important;
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                flex-shrink: 0 !important;
            }
            
            .overview-welcome .welcome-icon i {
                font-size: 2.5rem !important;
            }
            
            .overview-welcome .welcome-text h3 {
                font-size: 1.8rem !important;
                margin: 0 0 1rem 0 !important;
                font-weight: 700 !important;
            }
            
            .overview-welcome .welcome-text p {
                font-size: 1.1rem !important;
                margin: 0 !important;
                opacity: 0.9 !important;
                line-height: 1.6 !important;
            }
            
            /* Main Statistics Grid */
            .main-stats-grid {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)) !important;
                gap: 1.5rem !important;
                margin-bottom: 2rem !important;
            }
            
            .main-stats-grid .stat-card {
                background: white !important;
                border-radius: 12px !important;
                padding: 1.5rem !important;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05) !important;
                transition: all 0.3s ease !important;
                border-left: 4px solid !important;
            }
            
            .main-stats-grid .stat-card:hover {
                transform: translateY(-4px) !important;
                box-shadow: 0 8px 25px rgba(0,0,0,0.15) !important;
            }
            
            .main-stats-grid .stat-card.primary {
                border-left-color: #667eea !important;
            }
            
            .main-stats-grid .stat-card.success {
                border-left-color: #10b981 !important;
            }
            
            .main-stats-grid .stat-card.warning {
                border-left-color: #f59e0b !important;
            }
            
            .main-stats-grid .stat-card.info {
                border-left-color: #3b82f6 !important;
            }
            
            .main-stats-grid .stat-card .stat-icon {
                width: 50px !important;
                height: 50px !important;
                border-radius: 50% !important;
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                margin-bottom: 1rem !important;
            }
            
            .main-stats-grid .stat-card.primary .stat-icon {
                background: #667eea !important;
                color: white !important;
            }
            
            .main-stats-grid .stat-card.success .stat-icon {
                background: #10b981 !important;
                color: white !important;
            }
            
            .main-stats-grid .stat-card.warning .stat-icon {
                background: #f59e0b !important;
                color: white !important;
            }
            
            .main-stats-grid .stat-card.info .stat-icon {
                background: #3b82f6 !important;
                color: white !important;
            }
            
            .main-stats-grid .stat-card .stat-icon i {
                font-size: 1.2rem !important;
            }
            
            .main-stats-grid .stat-card h3 {
                font-size: 1rem !important;
                margin: 0 0 0.5rem 0 !important;
                color: #64748b !important;
                font-weight: 500 !important;
            }
            
            .main-stats-grid .stat-card .stat-number {
                display: block !important;
                font-size: 2.5rem !important;
                font-weight: 700 !important;
                color: #1e293b !important;
                margin-bottom: 0.5rem !important;
            }
            
            .main-stats-grid .stat-card p {
                margin: 0 !important;
                color: #94a3b8 !important;
                font-size: 0.9rem !important;
            }
            
            /* Quick Access */
            .quick-access {
                margin-bottom: 2rem !important;
            }
            
            .quick-access h3 {
                font-size: 1.5rem !important;
                color: #1e293b !important;
                margin-bottom: 1.5rem !important;
                font-weight: 600 !important;
            }
            
            .access-grid {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)) !important;
                gap: 1rem !important;
            }
            
            /* Responsive grid for Quick Access */
            @media (min-width: 1200px) {
                .access-grid {
                    grid-template-columns: repeat(3, 1fr) !important;
                }
            }
            
            @media (max-width: 1199px) and (min-width: 768px) {
                .access-grid {
                    grid-template-columns: repeat(2, 1fr) !important;
                }
            }
            
            .access-card {
                background: white !important;
                border: 1px solid #e5e7eb !important;
                border-radius: 8px !important;
                padding: 1.5rem !important;
                text-decoration: none !important;
                color: inherit !important;
                transition: all 0.3s ease !important;
                text-align: center !important;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05) !important;
            }
            
            .access-card:hover {
                transform: translateY(-2px) !important;
                box-shadow: 0 8px 25px rgba(0,0,0,0.15) !important;
                border-color: #667eea !important;
            }
            
            .access-card i {
                font-size: 2rem !important;
                color: #667eea !important;
                margin-bottom: 1rem !important;
                display: block !important;
            }
            
            .access-card h4 {
                font-size: 1.1rem !important;
                margin: 0 0 0.5rem 0 !important;
                color: #1e293b !important;
                font-weight: 600 !important;
            }
            
            .access-card p {
                margin: 0 !important;
                color: #64748b !important;
                font-size: 0.85rem !important;
            }
            
            /* System Status */
            .system-status {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)) !important;
                gap: 1rem !important;
            }
            
            .status-card {
                background: #f8fafc !important;
                border: 1px solid #e2e8f0 !important;
                border-radius: 8px !important;
                padding: 1.5rem !important;
                display: flex !important;
                align-items: center !important;
                gap: 1rem !important;
                transition: all 0.3s ease !important;
            }
            
            .status-card:hover {
                background: #f1f5f9 !important;
                border-color: #cbd5e1 !important;
            }
            
            .status-icon {
                width: 50px !important;
                height: 50px !important;
                border-radius: 50% !important;
                display: flex !important;
                align-items: center !important;
                justify-content: center !important;
                flex-shrink: 0 !important;
            }
            
            .status-icon.online {
                background: #10b981 !important;
                color: white !important;
            }
            
            .status-icon i {
                font-size: 1.2rem !important;
            }
            
            .status-info h4 {
                font-size: 1rem !important;
                margin: 0 0 0.5rem 0 !important;
                color: #1e293b !important;
                font-weight: 600 !important;
            }
            
            .status-text {
                display: block !important;
                font-size: 1.1rem !important;
                font-weight: 600 !important;
                color: #10b981 !important;
                margin-bottom: 0.25rem !important;
            }
            
            .status-info p {
                margin: 0 !important;
                color: #64748b !important;
                font-size: 0.85rem !important;
            }

            /* BetterHR visual refresh: layout/image-only styling, no route or logic changes */
            :root {
                --bh-primary: #00482f;
                --bh-primary-soft: #006241;
                --bh-secondary: #00754a;
                --bh-secondary-soft: #d8f2e5;
                --bh-gold: #cba258;
                --bh-canvas: #fbf9f4;
                --bh-ceramic: #edebe9;
                --bh-surface: #ffffff;
                --bh-surface-low: #f5f3ee;
                --bh-border: #d9d4ca;
                --bh-text: #1b1c19;
                --bh-muted: rgba(0, 0, 0, 0.58);
                --bh-danger: #c82014;
                --bh-shadow: 0 0 0.5px rgba(0, 0, 0, 0.14), 0 8px 24px rgba(0, 0, 0, 0.08);
            }

            * {
                box-sizing: border-box !important;
            }

            body,
            .hr-dashboard-container {
                background:
                    radial-gradient(circle at top right, rgba(0, 117, 74, 0.08), transparent 30%),
                    var(--bh-canvas) !important;
                color: var(--bh-text) !important;
                font-family: "Hanken Grotesk", "Segoe UI", Arial, sans-serif !important;
            }

            .hr-header {
                background: rgba(255, 255, 255, 0.94) !important;
                color: var(--bh-text) !important;
                border-bottom: 1px solid var(--bh-border) !important;
                box-shadow: 0 2px 18px rgba(0, 0, 0, 0.06) !important;
                backdrop-filter: blur(14px) !important;
                position: sticky !important;
                top: 0 !important;
                z-index: 20 !important;
            }

            .header-content {
                max-width: 1480px !important;
                min-height: 76px !important;
            }

            .logo-section i {
                width: 46px !important;
                height: 46px !important;
                border-radius: 12px !important;
                display: inline-flex !important;
                align-items: center !important;
                justify-content: center !important;
                background: var(--bh-primary-soft) !important;
                color: #fff !important;
                box-shadow: inset 0 -10px 20px rgba(0, 0, 0, 0.08) !important;
            }

            .logo-section h1 {
                color: var(--bh-primary) !important;
                font-weight: 800 !important;
                letter-spacing: 0 !important;
            }

            .search-box {
                background: var(--bh-surface-low) !important;
                border: 1px solid transparent !important;
                border-radius: 999px !important;
                box-shadow: inset 0 0 0 1px rgba(0, 0, 0, 0.04) !important;
            }

            .search-box:focus-within {
                border-color: rgba(0, 117, 74, 0.35) !important;
                box-shadow: 0 0 0 4px rgba(0, 117, 74, 0.12) !important;
            }

            .search-box input {
                background: transparent !important;
                color: var(--bh-text) !important;
            }

            .notification-bell,
            .user-profile,
            .btn-homepage {
                border: 1px solid var(--bh-border) !important;
                background: var(--bh-surface) !important;
                color: var(--bh-primary) !important;
                box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05) !important;
            }

            .notification-count {
                background: var(--bh-danger) !important;
                color: #fff !important;
                border: 2px solid #fff !important;
            }

            .user-profile img,
            .employee-info img,
            .employee-basic-info img,
            .candidate-info img {
                border: 2px solid #fff !important;
                box-shadow: 0 4px 16px rgba(0, 72, 47, 0.18) !important;
                object-fit: cover !important;
            }

            .hr-main-content {
                max-width: 1480px !important;
                align-items: flex-start !important;
            }

            .hr-sidebar {
                background: rgba(255, 255, 255, 0.96) !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 16px !important;
                box-shadow: var(--bh-shadow) !important;
                position: sticky !important;
                top: 96px !important;
            }

            .nav-section-title {
                color: var(--bh-muted) !important;
                font-size: 0.75rem !important;
                font-weight: 800 !important;
                letter-spacing: 0.06em !important;
                text-transform: uppercase !important;
            }

            .nav-item {
                border-radius: 12px !important;
                color: var(--bh-muted) !important;
                font-weight: 700 !important;
            }

            .nav-item:hover {
                background: var(--bh-surface-low) !important;
                color: var(--bh-primary) !important;
                transform: translateX(3px) !important;
            }

            .nav-item.active {
                background: var(--bh-secondary-soft) !important;
                color: var(--bh-primary) !important;
                box-shadow: inset 3px 0 0 var(--bh-secondary) !important;
            }

            .nav-item i {
                color: currentColor !important;
                width: 22px !important;
                text-align: center !important;
            }

            .hr-content-area {
                background: rgba(255, 255, 255, 0.92) !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 18px !important;
                box-shadow: var(--bh-shadow) !important;
            }

            .section-header {
                border-bottom: 1px solid var(--bh-border) !important;
                padding-bottom: 1.25rem !important;
                margin-bottom: 1.75rem !important;
            }

            .section-header h2,
            .quick-access h3,
            .hr-functions h3,
            .system-overview h3,
            .approval-header h3,
            .history-header h3 {
                color: var(--bh-primary) !important;
                font-weight: 800 !important;
                letter-spacing: 0 !important;
            }

            .section-header p,
            .welcome-text p,
            .stat-card p,
            .access-card p,
            .status-info p,
            .overview-info p,
            .function-content p,
            .approval-header p {
                color: var(--bh-muted) !important;
            }

            .welcome-card,
            .overview-welcome {
                background:
                    linear-gradient(135deg, rgba(0, 98, 65, 0.96), rgba(0, 72, 47, 0.92)),
                    var(--bh-primary) !important;
                border-radius: 18px !important;
                box-shadow: 0 18px 40px rgba(0, 72, 47, 0.18) !important;
                overflow: hidden !important;
                position: relative !important;
            }

            .welcome-card::after,
            .overview-welcome::after {
                content: "" !important;
                position: absolute !important;
                inset: auto -80px -110px auto !important;
                width: 260px !important;
                height: 260px !important;
                border-radius: 999px !important;
                background: rgba(216, 242, 229, 0.18) !important;
            }

            .welcome-icon,
            .overview-welcome .welcome-icon,
            .function-icon,
            .overview-icon,
            .main-stats-grid .stat-card .stat-icon {
                background: var(--bh-secondary-soft) !important;
                color: var(--bh-primary) !important;
                border-radius: 14px !important;
                box-shadow: inset 0 0 0 1px rgba(0, 117, 74, 0.12) !important;
            }

            .overview-welcome .welcome-icon,
            .welcome-card .welcome-icon {
                background: rgba(255, 255, 255, 0.16) !important;
                color: #fff !important;
                border: 1px solid rgba(255, 255, 255, 0.2) !important;
            }

            .main-stats-grid .stat-card,
            .access-card,
            .function-card,
            .overview-card,
            .request-card,
            .request-item,
            .status-card,
            .job-posting-card,
            .candidate-card,
            .chart-container,
            .payroll-card,
            .info-card {
                background: var(--bh-surface) !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 14px !important;
                box-shadow: 0 2px 14px rgba(0, 0, 0, 0.05) !important;
            }

            .main-stats-grid .stat-card:hover,
            .access-card:hover,
            .function-card:hover,
            .overview-card:hover,
            .request-card:hover,
            .status-card:hover,
            .job-posting-card:hover,
            .candidate-card:hover,
            .payroll-card:hover {
                border-color: rgba(0, 117, 74, 0.32) !important;
                box-shadow: 0 12px 30px rgba(0, 72, 47, 0.10) !important;
                transform: translateY(-3px) !important;
            }

            .main-stats-grid .stat-card {
                border-left-width: 4px !important;
            }

            .main-stats-grid .stat-card.primary,
            .main-stats-grid .stat-card.success {
                border-left-color: var(--bh-secondary) !important;
            }

            .main-stats-grid .stat-card.warning {
                border-left-color: var(--bh-gold) !important;
            }

            .main-stats-grid .stat-card.info {
                border-left-color: var(--bh-primary) !important;
            }

            .main-stats-grid .stat-card .stat-number,
            .overview-number,
            .status-text {
                color: var(--bh-primary) !important;
                font-weight: 800 !important;
            }

            .access-card i,
            .function-link i,
            .action-card i,
            .info-card i {
                color: var(--bh-secondary) !important;
            }

            .access-card h4,
            .function-content h4,
            .overview-info h4,
            .status-info h4,
            .request-info h4,
            .job-header h3,
            .candidate-details h4 {
                color: var(--bh-text) !important;
                font-weight: 800 !important;
            }

            .function-link,
            .btn-primary,
            .btn-filter,
            .btn-approve,
            .status-tab-btn.active {
                background: var(--bh-secondary) !important;
                border-color: var(--bh-secondary) !important;
                color: #fff !important;
                border-radius: 999px !important;
                box-shadow: 0 8px 18px rgba(0, 117, 74, 0.20) !important;
            }

            .btn-secondary,
            .btn-reset,
            .btn-view,
            .tab-btn,
            .filter-btn,
            .status-tab-btn {
                border-color: var(--bh-border) !important;
                color: var(--bh-primary) !important;
                background: var(--bh-surface-low) !important;
                border-radius: 999px !important;
            }

            .tab-btn.active,
            .filter-btn.active {
                background: var(--bh-secondary-soft) !important;
                color: var(--bh-primary) !important;
                border-color: rgba(0, 117, 74, 0.25) !important;
            }

            .btn-danger,
            .btn-reject {
                background: var(--bh-danger) !important;
                color: #fff !important;
                border-color: var(--bh-danger) !important;
                border-radius: 999px !important;
            }

            .form-select,
            .approval-filters select,
            .approval-filters input,
            .history-filters select,
            .history-filters input,
            .status-select {
                background: var(--bh-surface) !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 10px !important;
                color: var(--bh-text) !important;
                outline: none !important;
            }

            .form-select:focus,
            .approval-filters select:focus,
            .approval-filters input:focus,
            .history-filters select:focus,
            .history-filters input:focus,
            .status-select:focus {
                border-color: var(--bh-secondary) !important;
                box-shadow: 0 0 0 4px rgba(0, 117, 74, 0.12) !important;
            }

            .status-icon.online {
                background: var(--bh-secondary-soft) !important;
                color: var(--bh-secondary) !important;
            }

            .empty-state,
            .no-data,
            .loading-state {
                background: var(--bh-surface-low) !important;
                border: 1px dashed var(--bh-border) !important;
                border-radius: 14px !important;
                color: var(--bh-muted) !important;
            }

            @media (max-width: 900px) {
                .hr-main-content {
                    display: block !important;
                    padding: 1rem !important;
                }

                .hr-sidebar {
                    position: static !important;
                    width: 100% !important;
                    margin-bottom: 1rem !important;
                }

                .header-content {
                    padding: 0 1rem !important;
                    gap: 1rem !important;
                    flex-wrap: wrap !important;
                }

                .header-actions {
                    width: 100% !important;
                    justify-content: flex-start !important;
                    overflow-x: auto !important;
                    padding-bottom: 0.25rem !important;
                }

                .overview-welcome,
                .welcome-card {
                    align-items: flex-start !important;
                    flex-direction: column !important;
                }
            }
            .btn-homepage {
                background: rgba(255,255,255,0.2) !important;
                color: white !important;
                padding: 0.5rem 1rem !important;
                border-radius: 25px !important;
                text-decoration: none !important;
                display: flex !important;
                align-items: center !important;
                gap: 0.5rem !important;
                transition: all 0.3s ease !important;
                border: 1px solid rgba(255,255,255,0.3) !important;
            }
            .btn-homepage:hover {
                background: rgba(255,255,255,0.3) !important;
                transform: translateY(-2px) !important;
                box-shadow: 0 4px 15px rgba(0,0,0,0.2) !important;
            }
            .btn-homepage i {
                font-size: 1rem !important;
            }
            .btn-homepage span {
                font-weight: 500 !important;
            }
            
            /* Payroll Approval Styles */
            .payroll-approval-section {
                padding: 1.5rem 0;
            }
            
            .approval-header {
                margin-bottom: 2rem;
            }
            
            .approval-header h3 {
                color: #1e293b;
                font-size: 1.5rem;
                margin-bottom: 0.5rem;
            }
            
            .approval-header p {
                color: #64748b;
                margin: 0;
            }
            
            .status-tabs-container {
                display: flex;
                gap: 0.5rem;
                margin-bottom: 1.5rem;
                flex-wrap: wrap;
            }
            
            .status-tab-btn {
                padding: 0.75rem 1.5rem;
                border-radius: 8px;
                border: 2px solid #e5e7eb;
                background: white;
                color: #64748b;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
            }
            
            .status-tab-btn:hover {
                background: #f1f5f9;
                border-color: #667eea;
                color: #667eea;
            }
            
            .status-tab-btn.active {
                background: #667eea;
                color: white;
                border-color: #667eea;
            }
            
            .status-tab-btn .badge {
                background: rgba(255,255,255,0.2);
                padding: 0.25rem 0.5rem;
                border-radius: 12px;
                font-size: 0.75rem;
            }
            
            .status-tab-btn.active .badge {
                background: rgba(255,255,255,0.3);
            }
            
            .approval-filters {
                display: flex;
                gap: 1rem;
                margin-bottom: 1.5rem;
            }
            
            .approval-filters select,
            .approval-filters input {
                padding: 0.75rem;
                border: 1px solid #e5e7eb;
                border-radius: 8px;
                font-size: 1rem;
            }
            
            .approval-filters select:focus,
            .approval-filters input:focus {
                outline: none;
                border-color: #667eea;
                box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
            }
            
            .payrolls-list {
                display: flex;
                flex-direction: column;
                gap: 1rem;
            }
            
            .payroll-card-item {
                background: white;
                border-radius: 12px;
                padding: 1.5rem;
                box-shadow: 0 2px 10px rgba(0,0,0,0.05);
                border-left: 4px solid #f59e0b;
                transition: all 0.3s ease;
            }
            
            .payroll-card-item:hover {
                box-shadow: 0 8px 25px rgba(0,0,0,0.15);
                transform: translateY(-2px);
            }
            
            .payroll-card-item.status-approved {
                border-left-color: #10b981;
            }
            
            .payroll-card-item.status-rejected {
                border-left-color: #ef4444;
            }
            
            .payroll-card-item.status-paid {
                border-left-color: #3b82f6;
            }
            
            .payroll-card-header {
                display: flex;
                justify-content: space-between;
                align-items: flex-start;
                margin-bottom: 1rem;
                padding-bottom: 1rem;
                border-bottom: 1px solid #e5e7eb;
            }
            
            .payroll-card-title h4 {
                margin: 0 0 0.5rem 0;
                color: #667eea;
                font-size: 1.1rem;
            }
            
            .payroll-card-title p {
                margin: 0;
                color: #64748b;
                font-size: 0.9rem;
            }
            
            .status-badge-item {
                display: inline-block;
                padding: 0.5rem 1rem;
                border-radius: 20px;
                font-size: 0.85rem;
                font-weight: 600;
                text-transform: uppercase;
            }
            
            .status-badge-item.Pending {
                background: #fef3c7;
                color: #92400e;
            }
            
            .status-badge-item.Approved {
                background: #d1fae5;
                color: #065f46;
            }
            
            .status-badge-item.Rejected {
                background: #fee2e2;
                color: #991b1b;
            }
            
            .status-badge-item.Paid {
                background: #dbeafe;
                color: #1e40af;
            }
            
            .payroll-card-details {
                display: grid;
                grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
                gap: 1rem;
                margin-bottom: 1rem;
            }
            
            .detail-item-small {
                display: flex;
                flex-direction: column;
            }
            
            .detail-label-small {
                font-size: 0.85rem;
                color: #64748b;
                margin-bottom: 0.25rem;
            }
            
            .detail-value-small {
                font-size: 1rem;
                color: #1e293b;
                font-weight: 600;
            }
            
            .detail-value-small.amount {
                color: #10b981;
            }
            
            .payroll-card-actions {
                display: flex;
                gap: 0.75rem;
                justify-content: flex-end;
                padding-top: 1rem;
                border-top: 1px solid #e5e7eb;
            }
            
            .btn-small {
                padding: 0.5rem 1rem;
                border-radius: 6px;
                border: none;
                font-size: 0.9rem;
                font-weight: 600;
                cursor: pointer;
                transition: all 0.3s ease;
                display: inline-flex;
                align-items: center;
                gap: 0.5rem;
            }
            
            .btn-approve-small {
                background: #10b981;
                color: white;
            }
            
            .btn-approve-small:hover {
                background: #059669;
                transform: translateY(-2px);
            }
            
            .btn-reject-small {
                background: #ef4444;
                color: white;
            }
            
            .btn-reject-small:hover {
                background: #dc2626;
                transform: translateY(-2px);
            }
            
            .btn-view-small {
                background: #667eea;
                color: white;
            }
            
            .btn-view-small:hover {
                background: #5568d3;
                transform: translateY(-2px);
            }
            
            .loading-state {
                text-align: center;
                padding: 3rem;
                color: #64748b;
            }
            
            .loading-state i {
                font-size: 2rem;
                margin-bottom: 1rem;
            }
            
            .empty-state {
                text-align: center;
                padding: 3rem;
                color: #64748b;
            }
            
            .empty-state i {
                font-size: 3rem;
                margin-bottom: 1rem;
                opacity: 0.5;
            }
            .payroll-cards {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(320px, 1fr)) !important;
                gap: 1.5rem !important;
            }
            .payroll-card {
                background: #ffffff !important;
                border: 1px solid #e2e8f0 !important;
                border-radius: 12px !important;
                box-shadow: 0 8px 20px rgba(15, 23, 42, 0.08) !important;
                padding: 1.5rem !important;
                display: flex !important;
                flex-direction: column !important;
                gap: 1.25rem !important;
                transition: transform 0.2s ease, box-shadow 0.2s ease !important;
            }
            .payroll-card:hover {
                transform: translateY(-4px) !important;
                box-shadow: 0 12px 24px rgba(15, 23, 42, 0.12) !important;
            }
            .payroll-card .payroll-header {
                display: flex !important;
                justify-content: space-between !important;
                align-items: flex-start !important;
                gap: 1rem !important;
            }
            .payroll-card .payroll-title {
                display: flex !important;
                flex-direction: column !important;
                gap: 0.25rem !important;
            }
            .payroll-card .employee-name {
                font-size: 1.1rem !important;
                font-weight: 600 !important;
                color: #1e293b !important;
            }
            .payroll-card .payroll-id {
                font-size: 0.85rem !important;
                color: #64748b !important;
            }
            .payroll-card .status-badge {
                border-radius: 999px !important;
                padding: 0.35rem 0.85rem !important;
                font-size: 0.75rem !important;
                font-weight: 600 !important;
                text-transform: uppercase !important;
                letter-spacing: 0.03em !important;
            }
            .payroll-card .status-badge.Pending {
                background: #fff7ed !important;
                color: #c2410c !important;
                border: 1px solid rgba(234, 88, 12, 0.3) !important;
            }
            .payroll-card .status-badge.Approved {
                background: #ecfdf5 !important;
                color: #047857 !important;
                border: 1px solid rgba(16, 185, 129, 0.3) !important;
            }
            .payroll-card .status-badge.Rejected {
                background: #fef2f2 !important;
                color: #b91c1c !important;
                border: 1px solid rgba(248, 113, 113, 0.3) !important;
            }
            .payroll-card .status-badge.Paid {
                background: #eef2ff !important;
                color: #4338ca !important;
                border: 1px solid rgba(99, 102, 241, 0.3) !important;
            }
            .payroll-card .payroll-details {
                display: grid !important;
                grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)) !important;
                gap: 1rem !important;
                border: 1px dashed #e2e8f0 !important;
                border-radius: 10px !important;
                padding: 1rem !important;
                background: #f8fafc !important;
            }
            .payroll-card .detail-item {
                display: flex !important;
                flex-direction: column !important;
                gap: 0.35rem !important;
            }
            .payroll-card .detail-label {
                font-size: 0.75rem !important;
                font-weight: 600 !important;
                color: #64748b !important;
                text-transform: uppercase !important;
                letter-spacing: 0.08em !important;
            }
            .payroll-card .detail-value {
                font-size: 1rem !important;
                color: #0f172a !important;
                font-weight: 600 !important;
            }
            .payroll-card .detail-value.amount {
                font-feature-settings: "tnum" 1, "lnum" 1 !important;
            }
            .payroll-card .detail-value.deduction {
                color: #dc2626 !important;
            }
            .payroll-card .detail-value.net-amount {
                font-size: 1.25rem !important;
                font-weight: 700 !important;
                color: #2563eb !important;
            }
            .payroll-card .payroll-actions {
                display: flex !important;
                flex-wrap: wrap !important;
                gap: 0.75rem !important;
            }
            .payroll-card .payroll-actions .btn {
                flex: 1 !important;
                min-width: 140px !important;
            }
            .hr-main-content {
                display: flex !important;
                max-width: 1400px !important;
                margin: 0 auto !important;
                padding: 2rem !important;
                gap: 2rem !important;
            }
            .hr-sidebar {
                width: 280px !important;
                background: white !important;
                border-radius: 12px !important;
                box-shadow: 0 4px 20px rgba(0,0,0,0.1) !important;
                padding: 1.5rem !important;
                height: fit-content !important;
            }

            /* BetterHR final visual overrides - placed last to keep the UI consistent */
            .hr-dashboard-container {
                background:
                    radial-gradient(circle at top right, rgba(0, 117, 74, 0.08), transparent 30%),
                    var(--bh-canvas) !important;
            }

            .hr-header {
                background: rgba(255, 255, 255, 0.94) !important;
                color: var(--bh-text) !important;
                border-bottom: 1px solid var(--bh-border) !important;
                box-shadow: 0 2px 18px rgba(0, 0, 0, 0.06) !important;
            }

            .hr-main-content {
                max-width: 1480px !important;
                align-items: flex-start !important;
            }

            .hr-sidebar {
                width: 280px !important;
                background: rgba(255, 255, 255, 0.96) !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 16px !important;
                box-shadow: var(--bh-shadow) !important;
                position: sticky !important;
                top: 96px !important;
            }

            .hr-content-area {
                background: rgba(255, 255, 255, 0.92) !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 18px !important;
                box-shadow: var(--bh-shadow) !important;
            }

            .btn-homepage {
                background: var(--bh-surface) !important;
                color: var(--bh-primary) !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 999px !important;
                box-shadow: 0 2px 10px rgba(0, 72, 47, 0.08) !important;
            }

            .btn-homepage:hover {
                background: var(--bh-secondary-soft) !important;
                color: var(--bh-primary) !important;
                box-shadow: 0 8px 20px rgba(0, 72, 47, 0.12) !important;
            }

            .status-tab-btn:hover,
            .approval-filters select:focus,
            .approval-filters input:focus {
                border-color: var(--bh-secondary) !important;
                color: var(--bh-primary) !important;
                box-shadow: 0 0 0 4px rgba(0, 117, 74, 0.12) !important;
            }

            .status-tab-btn.active,
            .btn-primary,
            .btn-filter,
            .btn-approve {
                background: var(--bh-secondary) !important;
                border-color: var(--bh-secondary) !important;
                color: #fff !important;
            }

            .payroll-card-title h4,
            .detail-value-small,
            .payroll-title,
            .employee-name,
            .history-header h3,
            .approval-header h3 {
                color: var(--bh-primary) !important;
            }

            .detail-value-small.amount,
            .detail-value.amount,
            .detail-value.net-amount {
                color: var(--bh-secondary) !important;
            }

            .payroll-card-item.status-approved,
            .payroll-card.status-approved {
                border-left-color: var(--bh-secondary) !important;
            }

            .payroll-card-item.status-paid,
            .payroll-card.status-paid {
                border-left-color: var(--bh-primary) !important;
            }

            .payroll-card-item,
            .payroll-card {
                border-radius: 14px !important;
                border-color: var(--bh-border) !important;
                box-shadow: 0 2px 14px rgba(0, 0, 0, 0.05) !important;
            }
        </style>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css?v=hrhome-20260618-2">
        <style>
            :root {
                --bh-green-900: #00482f;
                --bh-green-700: #006241;
                --bh-green-600: #00754a;
                --bh-green-100: #dff4e8;
                --bh-green-50: #eef8f1;
                --bh-canvas: #edebe9;
                --bh-card: #ffffff;
                --bh-border: #d9d2c7;
                --bh-text: #1f1f1f;
                --bh-muted: #6f6b66;
                --bh-danger: #c82014;
                --bh-gold: #cba258;
                --bh-radius: 8px;
                --bh-shadow: 0 14px 34px rgba(31, 31, 31, 0.08);
            }

            body .hr-home-shell {
                min-height: 100vh !important;
                background: var(--bh-canvas) !important;
                color: var(--bh-text) !important;
                font-family: "Hanken Grotesk", Inter, Arial, sans-serif !important;
                letter-spacing: 0 !important;
            }

            body .hr-home-shell .hr-header {
                position: fixed !important;
                top: 0 !important;
                right: 0 !important;
                left: 260px !important;
                z-index: 50 !important;
                height: 72px !important;
                padding: 0 !important;
                background: rgba(251, 249, 244, 0.98) !important;
                color: var(--bh-text) !important;
                border-bottom: 1px solid var(--bh-border) !important;
                box-shadow: none !important;
            }

            body .hr-home-shell .header-content {
                max-width: none !important;
                min-height: 72px !important;
                padding: 0 32px !important;
                display: flex !important;
                align-items: center !important;
                justify-content: space-between !important;
                gap: 24px !important;
            }

            body .hr-home-shell .logo-section,
            body .hr-home-shell .header-actions,
            body .hr-home-shell .user-profile {
                color: var(--bh-text) !important;
            }

            body .hr-home-shell .topbar-title h1 {
                margin: 0 !important;
                color: var(--bh-green-900) !important;
                font-size: 1.85rem !important;
                font-weight: 800 !important;
            }

            body .hr-home-shell .logo-section i {
                width: 42px !important;
                height: 42px !important;
                display: inline-flex !important;
                align-items: center !important;
                justify-content: center !important;
                border-radius: var(--bh-radius) !important;
                background: var(--bh-green-100) !important;
                color: var(--bh-green-700) !important;
                font-size: 1.35rem !important;
            }

            body .hr-home-shell .search-box input {
                width: 300px !important;
                min-width: 300px !important;
                height: 44px !important;
                padding: 0 16px 0 44px !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 999px !important;
                background: #f7f3ed !important;
                color: var(--bh-text) !important;
                box-shadow: inset 0 1px 0 rgba(255,255,255,0.8) !important;
            }

            body .hr-home-shell .search-box input::placeholder {
                color: #817b73 !important;
                opacity: 1 !important;
            }

            body .hr-home-shell .notification-bell,
            body .hr-home-shell .user-profile,
            body .hr-home-shell .btn-homepage {
                min-height: 44px !important;
                display: inline-flex !important;
                align-items: center !important;
                gap: 10px !important;
                padding: 0 16px !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: 999px !important;
                background: #fffefa !important;
                color: var(--bh-text) !important;
                box-shadow: 0 1px 2px rgba(31, 31, 31, 0.08) !important;
                text-decoration: none !important;
            }

            body .hr-home-shell .notification-count {
                background: var(--bh-danger) !important;
                color: #fff !important;
            }

            body .hr-home-shell .hr-main-content {
                display: block !important;
                max-width: none !important;
                margin: 0 !important;
                padding: 104px 40px 44px 300px !important;
                background: transparent !important;
            }

            body .hr-home-shell .hr-sidebar {
                position: fixed !important;
                inset: 0 auto 0 0 !important;
                z-index: 60 !important;
                width: 260px !important;
                height: 100vh !important;
                padding: 24px 16px !important;
                overflow-y: auto !important;
                border: 0 !important;
                border-right: 1px solid var(--bh-border) !important;
                border-radius: 0 !important;
                background: #fffefa !important;
                box-shadow: none !important;
            }

            body .hr-home-shell .hr-sidebar-brand {
                display: grid !important;
                grid-template-columns: 40px 1fr !important;
                align-items: center !important;
                gap: 12px !important;
                margin-bottom: 30px !important;
            }

            body .hr-home-shell .brand-mark {
                width: 40px !important;
                height: 40px !important;
                display: inline-flex !important;
                align-items: center !important;
                justify-content: center !important;
                border-radius: var(--bh-radius) !important;
                background: var(--bh-green-100) !important;
                color: var(--bh-green-900) !important;
                font-weight: 900 !important;
            }

            body .hr-home-shell .hr-sidebar-brand strong,
            body .hr-home-shell .hr-sidebar-brand span {
                display: block !important;
                line-height: 1.25 !important;
            }

            body .hr-home-shell .hr-sidebar-brand strong {
                color: var(--bh-green-900) !important;
                font-size: 1rem !important;
                font-weight: 800 !important;
            }

            body .hr-home-shell .hr-sidebar-brand span {
                color: var(--bh-muted) !important;
                font-size: 0.78rem !important;
            }

            body .hr-home-shell .nav-section {
                margin: 0 0 10px !important;
                padding: 0 !important;
                border: 0 !important;
            }

            body .hr-home-shell .nav-section h3 {
                margin: 20px 8px 8px !important;
                padding-bottom: 8px !important;
                border-bottom: 1px solid #e7ded2 !important;
                color: #6b7890 !important;
                font-size: 0.78rem !important;
                font-weight: 800 !important;
                text-transform: uppercase !important;
            }

            body .hr-home-shell .nav-item {
                min-height: 44px !important;
                margin: 2px 0 !important;
                padding: 0 14px !important;
                display: flex !important;
                align-items: center !important;
                gap: 12px !important;
                border-radius: var(--bh-radius) !important;
                color: var(--bh-muted) !important;
                font-weight: 800 !important;
                text-decoration: none !important;
                transform: none !important;
                box-shadow: none !important;
            }

            body .hr-home-shell .nav-item.active,
            body .hr-home-shell .nav-item:hover {
                background: var(--bh-green-100) !important;
                color: var(--bh-green-900) !important;
            }

            body .hr-home-shell .nav-item i {
                width: 20px !important;
                color: var(--bh-green-600) !important;
                font-size: 1rem !important;
            }

            body .hr-home-shell .hr-content-area {
                max-width: 1180px !important;
                padding: 0 !important;
                border: 0 !important;
                border-radius: 0 !important;
                background: transparent !important;
                box-shadow: none !important;
            }

            body .hr-home-shell .content-section {
                display: none !important;
            }

            body .hr-home-shell .content-section.active {
                display: block !important;
            }

            body .hr-home-shell .comprehensive-overview {
                padding: 0 !important;
                background: transparent !important;
            }

            body .hr-home-shell .hr-home-hero {
                display: flex !important;
                align-items: center !important;
                gap: 18px !important;
                margin-bottom: 28px !important;
                padding: 32px 36px !important;
                border: 0 !important;
                border-radius: var(--bh-radius) !important;
                background: var(--bh-green-700) !important;
                color: #fff !important;
                box-shadow: var(--bh-shadow) !important;
            }

            body .hr-home-shell .hero-icon {
                width: 54px !important;
                height: 54px !important;
                display: inline-flex !important;
                align-items: center !important;
                justify-content: center !important;
                border-radius: var(--bh-radius) !important;
                background: rgba(255,255,255,0.14) !important;
                color: #fff !important;
                flex: 0 0 auto !important;
            }

            body .hr-home-shell .hero-icon i {
                color: #fff !important;
                font-size: 1.35rem !important;
                margin: 0 !important;
            }

            body .hr-home-shell .hr-home-hero h2 {
                margin: 0 0 8px !important;
                color: #fff !important;
                font-size: 2rem !important;
                line-height: 1.1 !important;
            }

            body .hr-home-shell .hr-home-hero p {
                margin: 0 !important;
                color: rgba(255,255,255,0.84) !important;
                font-size: 1rem !important;
                line-height: 1.55 !important;
            }

            body .hr-home-shell .quick-access-heading {
                display: flex !important;
                align-items: center !important;
                justify-content: space-between !important;
                margin: 0 0 14px !important;
            }

            body .hr-home-shell .quick-access-heading h3 {
                margin: 0 !important;
                color: var(--bh-green-900) !important;
                font-size: 1.12rem !important;
                font-weight: 800 !important;
            }

            body .hr-home-shell .quick-access-heading a {
                color: var(--bh-green-700) !important;
                font-weight: 800 !important;
                text-decoration: none !important;
            }

            body .hr-home-shell .access-grid {
                display: grid !important;
                grid-template-columns: repeat(3, minmax(0, 1fr)) !important;
                gap: 18px !important;
            }

            body .hr-home-shell .access-card {
                min-height: 178px !important;
                padding: 22px !important;
                display: flex !important;
                flex-direction: column !important;
                align-items: flex-start !important;
                gap: 10px !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: var(--bh-radius) !important;
                background: var(--bh-card) !important;
                color: var(--bh-text) !important;
                box-shadow: 0 1px 2px rgba(31,31,31,0.12) !important;
                text-align: left !important;
                text-decoration: none !important;
                transform: none !important;
            }

            body .hr-home-shell .access-card:hover {
                border-color: rgba(0,117,74,0.42) !important;
                box-shadow: var(--bh-shadow) !important;
                transform: translateY(-2px) !important;
            }

            body .hr-home-shell .access-icon {
                width: 34px !important;
                height: 34px !important;
                display: inline-flex !important;
                align-items: center !important;
                justify-content: center !important;
                border-radius: 999px !important;
                background: var(--bh-green-100) !important;
                color: var(--bh-green-700) !important;
            }

            body .hr-home-shell .access-icon i {
                color: inherit !important;
                display: inline-flex !important;
                margin: 0 !important;
                font-size: 0.95rem !important;
            }

            body .hr-home-shell .access-icon-warning {
                background: rgba(200,32,20,0.08) !important;
                color: var(--bh-danger) !important;
            }

            body .hr-home-shell .access-icon-gold {
                background: rgba(203,162,88,0.18) !important;
                color: #8a6400 !important;
            }

            body .hr-home-shell .access-card h4 {
                margin: 4px 0 0 !important;
                color: var(--bh-text) !important;
                font-size: 1.04rem !important;
                font-weight: 800 !important;
            }

            body .hr-home-shell .access-card p {
                flex: 1 !important;
                margin: 0 !important;
                color: var(--bh-muted) !important;
                font-size: 0.9rem !important;
                line-height: 1.45 !important;
            }

            body .hr-home-shell .access-card strong {
                color: var(--bh-green-700) !important;
                font-size: 0.86rem !important;
                line-height: 1 !important;
            }

            body .hr-home-shell .access-card strong i,
            body .hr-home-shell .quick-access-heading a i {
                display: inline-flex !important;
                margin: 0 0 0 4px !important;
                color: inherit !important;
                font-size: 0.82rem !important;
                transform: none !important;
            }

            body .hr-home-shell .system-status {
                display: grid !important;
                grid-template-columns: repeat(2, minmax(0, 1fr)) !important;
                gap: 14px !important;
                margin-top: 32px !important;
            }

            body .hr-home-shell .system-status .quick-access-heading {
                grid-column: 1 / -1 !important;
            }

            body .hr-home-shell .status-card {
                display: flex !important;
                align-items: center !important;
                gap: 14px !important;
                padding: 18px !important;
                border: 1px solid var(--bh-border) !important;
                border-radius: var(--bh-radius) !important;
                background: var(--bh-card) !important;
                box-shadow: 0 1px 2px rgba(31,31,31,0.12) !important;
            }

            @media (max-width: 1100px) {
                body .hr-home-shell .access-grid {
                    grid-template-columns: repeat(2, minmax(0, 1fr)) !important;
                }
            }
        </style>
    </head>
    <body>
        <div class="hr-dashboard-container hr-home-shell">
            <!-- Header -->
            <header class="hr-header">
                <div class="header-content">
                    <div class="logo-section topbar-title">
                        <i class="fas fa-chart-line"></i>
                        <h1>HR Home</h1>
                    </div>
                    <div class="header-actions">
                        <div class="search-box">
                            <i class="fas fa-search"></i>
                            <input type="text" placeholder="Tìm nhân viên, phòng ban...">
                        </div>
                        <div class="notification-bell">
                            <i class="fas fa-bell"></i>
                            <span class="notification-count">5</span>
                        </div>
                        <div class="user-profile">
                            <img src="https://i.pravatar.cc/40" alt="Người dùng HR">
                            <span>Quản lý HR</span>
                            <i class="fas fa-chevron-down"></i>
                        </div>
                        <a href="${pageContext.request.contextPath}/homepage" class="btn-homepage" title="Về trang chủ">
                            <i class="fas fa-home"></i>
                            <span>Trang chủ</span>
                        </a>
                    </div>
                </div>
            </header>

            <!-- Main Content -->
            <main class="hr-main-content">
                <c:set var="hrSidebarActive" value="${param.section eq 'requests-approval' ? 'requests' : param.section eq 'reports-analytics' ? 'reports' : 'overview'}" />
                <%@ include file="_HrManagerSidebar.jspf" %>

                <!-- Content Area -->
                <div class="hr-content-area">
                    <!-- HR Home Section -->
                    <section id="hr-home" class="content-section active">
                        <div class="comprehensive-overview">
                            <%
                                // Load pending contracts count
                                int pendingContractsCount = 0;
                                try {
                                    com.hrm.dao.ContractDAO contractDAO = new com.hrm.dao.ContractDAO();
                                    pendingContractsCount = contractDAO.countContractsByStatus("Pending_Approval");
                                } catch (Exception e) {
                                    // Ignore errors, just show 0
                                }
                                pageContext.setAttribute("pendingContractsCount", pendingContractsCount);
                            %>

                            <div class="hr-home-hero">
                                <div class="hero-icon">
                                    <i class="fas fa-house-user"></i>
                                </div>
                                <div>
                                    <h2>HR Home</h2>
                                    <p>Chào mừng bạn quay lại hệ thống quản trị nhân sự BetterHR. Hôm nay bạn muốn xử lý công việc nào?</p>
                                </div>
                            </div>

                            <!-- Quick Access Functions -->
                            <div class="quick-access">
                                <div class="quick-access-heading">
                                    <h3>Truy cập nhanh</h3>
                                    <a href="${pageContext.request.contextPath}/homepage">Về trang chủ <i class="fas fa-arrow-right"></i></a>
                                </div>
                                <div class="access-grid">
                                    
<!--                                    <a href="${pageContext.request.contextPath}/EmploymentStatusController" class="access-card">
                                        <i class="fas fa-user-check"></i>
                                        <h4>Employment Status</h4>
                                        <p>Update employment status</p>
                                    </a>-->
                                    <a href="${pageContext.request.contextPath}/viewRecruitment" class="access-card">
                                        <span class="access-icon"><i class="fas fa-bullhorn"></i></span>
                                        <h4>Duyệt tin tuyển dụng</h4>
                                        <p>Kiểm tra và duyệt các tin tuyển dụng do HR Staff gửi lên.</p>
                                        <strong>Xem tin chờ duyệt <i class="fas fa-arrow-right"></i></strong>
                                    </a>
                                    
                                 
                                    <a href="${pageContext.request.contextPath}/hr/create-employee" class="access-card">
                                        <span class="access-icon"><i class="fas fa-user-plus"></i></span>
                                        <h4>Tạo nhân viên</h4>
                                        <p>Chuyển ứng viên đã duyệt thành hồ sơ nhân viên.</p>
                                        <strong>Tạo mới nhân viên <i class="fas fa-arrow-right"></i></strong>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/hr/employee-list" class="access-card">
                                        <span class="access-icon"><i class="fas fa-list"></i></span>
                                        <h4>Danh sách nhân viên</h4>
                                        <p>Xem, lọc và quản lý toàn bộ hồ sơ nhân sự.</p>
                                        <strong>Danh sách nhân viên <i class="fas fa-arrow-right"></i></strong>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/hr/approve-reject-contracts" class="access-card">
                                        <span class="access-icon access-icon-warning"><i class="fas fa-file-contract"></i></span>
                                        <h4>Duyệt hợp đồng</h4>
                                        <p>Kiểm tra các hợp đồng đang chờ duyệt (${pendingContractsCount}).</p>
                                        <strong>Phê duyệt hợp đồng <i class="fas fa-arrow-right"></i></strong>
                                    </a>
                                    <a href="${pageContext.request.contextPath}/hr/payroll-approval?status=Pending" class="access-card">
                                        <span class="access-icon access-icon-gold"><i class="fas fa-money-bill-wave"></i></span>
                                        <h4>Duyệt bảng lương</h4>
                                        <p>Rà soát và phê duyệt bảng lương do HR Staff gửi lên.</p>
                                        <strong>Phê duyệt lương <i class="fas fa-arrow-right"></i></strong>
                                    </a>
                                </div>
                            </div>
                            
                            <!-- System Status -->
                            <div class="system-status">
                                <div class="quick-access-heading">
                                    <h3>Hoạt động hệ thống gần đây</h3>
                                </div>
                                <div class="status-card">
                                    <div class="status-icon online">
                                        <i class="fas fa-database"></i>
                                    </div>
                                    <div class="status-info">
                                        <h4>Hệ thống</h4>
                                        <span class="status-text">Đang hoạt động</span>
                                        <p>Các chức năng HR sẵn sàng sử dụng.</p>
                                    </div>
                                </div>
                                <div class="status-card">
                                    <div class="status-icon online">
                                        <i class="fas fa-clock"></i>
                                    </div>
                                    <div class="status-info">
                                        <h4>Cập nhật gần nhất</h4>
                                        <span class="status-text" id="lastUpdatedTime"></span>
                                        <p>Thời điểm làm mới dữ liệu trên trang.</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                    

                    <!-- Profile Management Section -->
                    <section id="ProfileManagementController" class="content-section">
                        <div class="section-header">
                            <h2>Quản lý hồ sơ</h2>
                            <p>Duyệt hoặc từ chối các yêu cầu cập nhật thông tin cá nhân của nhân viên</p>
                        </div>
                        
                        <div class="management-tabs">
                            <button class="tab-btn active" data-tab="pending-profiles">Chờ duyệt</button>
                            <button class="tab-btn" data-tab="approved-profiles">Đã duyệt</button>
                            <button class="tab-btn" data-tab="rejected-profiles">Từ chối</button>
                        </div>
                        
                        <div class="tab-content">
                            <div id="pending-profiles" class="tab-panel active">
                                <div class="profile-requests">
                                    <c:choose>
                                        <c:when test="${not empty employees}">
                                            <c:forEach var="employee" items="${employees}" begin="0" end="4">
                                    <div class="request-card">
                                        <div class="request-header">
                                            <div class="employee-info">
                                                            <img src="https://i.pravatar.cc/50?img=${employee.employeeId}" alt="Employee">
                                                <div>
                                                                <h4>${employee.fullName}</h4>
                                                                <p>${employee.position} - ${employee.departmentName}</p>
                                                </div>
                                            </div>
                                            <div class="request-date">
                                                <i class="fas fa-clock"></i>
                                                            <span>${employee.employmentPeriod}</span>
                                            </div>
                                        </div>
                                        <div class="request-details">
                                                        <h5>Thông tin nhân viên:</h5>
                                                        <ul>
                                                            <li><strong>Email:</strong> ${employee.email}</li>
                                                            <li><strong>Số điện thoại:</strong> ${employee.phone}</li>
                                                            <li><strong>Địa chỉ:</strong> ${employee.address}</li>
                                                            <li><strong>Trạng thái:</strong> ${employee.status}</li>
                                            </ul>
                                        </div>
                                        <div class="request-actions">
                                            <button class="btn-approve">
                                                <i class="fas fa-check"></i>
                                                            Duyệt
                                            </button>
                                            <button class="btn-reject">
                                                <i class="fas fa-times"></i>
                                                            Từ chối
                                            </button>
                                            <button class="btn-view-details">
                                                <i class="fas fa-eye"></i>
                                                            Xem chi tiết
                                            </button>
                                        </div>
                                    </div>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="no-data">
                                                <i class="fas fa-users-slash"></i>
                                                <h3>Chưa có hồ sơ nhân viên</h3>
                                                <p>Hiện chưa có hồ sơ nhân viên cần hiển thị.</p>
                                                </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </section>

<!--                     Employment Status Management Section 
                    <section id="employment-status" class="content-section">
                        <div class="section-header">
                            <h2>Employment Status Management</h2>
                            <p>Update employee employment status</p>
                        </div>
                        
                        <div class="status-management">
                            <div class="status-filters">
                                <button class="filter-btn active" data-status="all">All</button>
                                <button class="filter-btn" data-status="active">Active</button>
                                <button class="filter-btn" data-status="intern">Intern</button>
                                <button class="filter-btn" data-status="probation">Probation</button>
                            </div>
                            
                            <div class="employee-status-list">
                                <c:choose>
                                    <c:when test="${not empty employees}">
                                        <c:forEach var="employee" items="${employees}" begin="0" end="5">
                                <div class="status-card">
                                    <div class="employee-basic-info">
                                                    <img src="https://i.pravatar.cc/50?img=${employee.employeeId}" alt="Employee">
                                        <div>
                                                        <h4>${employee.fullName}</h4>
                                                        <p>${employee.position} - ${employee.departmentName}</p>
                                                        <span class="current-status ${employee.status.toLowerCase()}">${employee.status}</span>
                                        </div>
                                    </div>
                                    <div class="status-actions">
                                                    <form method="POST" action="/HRMS/EmploymentStatusController" style="display: flex; gap: 10px; align-items: center;" onsubmit="return confirmStatusUpdate(this)">
                                                        <input type="hidden" name="action" value="updateStatus">
                                                        <input type="hidden" name="employeeId" value="${employee.employeeId}">
                                                        <select name="status" class="status-select">
                                                            <option value="Active" ${employee.status == 'Active' ? 'selected' : ''}>Active</option>
                                                            <option value="Probation" ${employee.status == 'Probation' ? 'selected' : ''}>Probation</option>
                                                            <option value="Intern" ${employee.status == 'Intern' ? 'selected' : ''}>Intern</option>
                                        </select>
                                                        <button type="submit" class="btn-update-status">
                                            <i class="fas fa-save"></i>
                                                            Update
                                        </button>
                                                    </form>
                                    </div>
                                </div>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="no-data">
                                            <i class="fas fa-users-slash"></i>
                                            <h3>No employees found</h3>
                                            <p>There are no employees to display at the moment.</p>
                                        </div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </section>-->

                    <!-- Requests and Recommendations Section -->
                    <section id="requests-approval" class="content-section">
                        <div class="section-header">
                            <h2>Yêu cầu và đề xuất</h2>
                            <p>Rà soát, phê duyệt hoặc từ chối các yêu cầu đã gửi</p>
                        </div>
                        
                        <div class="requests-tabs">
                            <button class="tab-btn active" data-tab="pending-requests">Chờ duyệt</button>
                            <button class="tab-btn" data-tab="approved-requests">Đã duyệt</button>
                            <button class="tab-btn" data-tab="rejected-requests">Từ chối</button>
                        </div>
                        
                        <div class="tab-content">
                            <div id="pending-requests" class="tab-panel active">
                                <div class="request-list">
                                    <div class="request-item">
                                        <div class="request-info">
                                            <h4>Yêu cầu nghỉ phép</h4>
                                            <p><strong>Người gửi:</strong> Nguyen Van E - Phòng IT</p>
                                            <p><strong>Loại yêu cầu:</strong> Nghỉ việc cá nhân</p>
                                            <p><strong>Thời gian:</strong> 15/12/2024 - 20/12/2024</p>
                                            <p><strong>Lý do:</strong> Việc gia đình</p>
                                        </div>
                                        <div class="request-actions">
                                            <button class="btn-approve">
                                                <i class="fas fa-check"></i>
                                                Duyệt
                                            </button>
                                            <button class="btn-reject">
                                                <i class="fas fa-times"></i>
                                                Từ chối
                                            </button>
                                        </div>
                                    </div>
                                    
                                    <div class="request-item">
                                        <div class="request-info">
                                            <h4>Đề xuất tăng lương</h4>
                                            <p><strong>Người gửi:</strong> Tran Thi F - Phòng Marketing</p>
                                            <p><strong>Loại yêu cầu:</strong> Đề xuất điều chỉnh lương</p>
                                            <p><strong>Mức đề xuất:</strong> 15%</p>
                                            <p><strong>Lý do:</strong> Hoàn thành tốt dự án và có đóng góp tích cực</p>
                                        </div>
                                        <div class="request-actions">
                                            <button class="btn-approve">
                                                <i class="fas fa-check"></i>
                                                Duyệt
                                            </button>
                                            <button class="btn-reject">
                                                <i class="fas fa-times"></i>
                                                Từ chối
                                            </button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <!-- Payroll Management Section -->
                    <section id="payroll-management" class="content-section">
                        <div class="section-header">
                            <h2>Quản lý bảng lương</h2>
                            <p>Rà soát, phê duyệt bảng lương do HR Staff gửi lên và xem lịch sử xử lý</p>
                        </div>
                        
                        <div class="payroll-tabs">
                            <button class="tab-btn active" data-tab="payroll-approval">Duyệt bảng lương</button>
                            <button class="tab-btn" data-tab="payroll-history">Lịch sử bảng lương</button>
                        </div>
                        
                        <div class="tab-content">
                            <!-- Payroll Approval Tab -->
                            <div id="payroll-approval" class="tab-panel active">
                                <div class="payroll-approval-section">
                                    <div class="approval-header">
                                        <h3><i class="fas fa-clipboard-check"></i> Duyệt bảng lương</h3>
                                        <p>Kiểm tra, phê duyệt hoặc từ chối bảng lương do HR Staff gửi lên</p>
                                    </div>
                                    
                                    <!-- Status Tabs -->
                                    <div class="status-tabs-container">
                                        <a class="status-tab-btn ${payrollStatus == 'Pending' ? 'active' : ''}" data-status="Pending" href="${pageContext.request.contextPath}/HrHomeController?section=payroll-management&payrollStatus=Pending">
                                            <i class="fas fa-clock"></i> Chờ duyệt <span class="badge" id="pendingCount">${pendingCount != null ? pendingCount : 0}</span>
                                        </a>
                                        <a class="status-tab-btn ${payrollStatus == 'Approved' ? 'active' : ''}" data-status="Approved" href="${pageContext.request.contextPath}/HrHomeController?section=payroll-management&payrollStatus=Approved">
                                            <i class="fas fa-check-circle"></i> Đã duyệt <span class="badge" id="approvedCount">${approvedCount != null ? approvedCount : 0}</span>
                                        </a>
                                        <a class="status-tab-btn ${payrollStatus == 'Rejected' ? 'active' : ''}" data-status="Rejected" href="${pageContext.request.contextPath}/HrHomeController?section=payroll-management&payrollStatus=Rejected">
                                            <i class="fas fa-times-circle"></i> Từ chối <span class="badge" id="rejectedCount">${rejectedCount != null ? rejectedCount : 0}</span>
                                        </a>
                                        <a class="status-tab-btn ${payrollStatus == 'Paid' ? 'active' : ''}" data-status="Paid" href="${pageContext.request.contextPath}/HrHomeController?section=payroll-management&payrollStatus=Paid">
                                            <i class="fas fa-money-bill-wave"></i> Đã chi trả <span class="badge" id="paidCount">${paidCount != null ? paidCount : 0}</span>
                                        </a>
                                    </div>
                                    
                                    <!-- Filter Section -->
                                    <form class="approval-filters" method="get" action="${pageContext.request.contextPath}/HrHomeController">
                                        <input type="hidden" name="section" value="payroll-management"/>
                                        <input type="hidden" name="payrollStatus" value="${payrollStatus != null ? payrollStatus : 'Pending'}"/>
                                        <select id="employeeFilter" name="employeeFilter" onchange="this.form.submit()">
                                            <option value="">Tất cả nhân viên</option>
                                            <c:forEach var="employee" items="${employees}">
                                                <option value="${employee.employeeId}" ${payrollEmployeeFilter == employee.employeeId ? 'selected' : ''}>${employee.fullName} (ID: ${employee.employeeId})</option>
                                            </c:forEach>
                                        </select>
                                        <input type="month" id="monthFilter" name="monthFilter" value="${payrollMonthFilter}" onchange="this.form.submit()"/>
                                        <button type="submit" class="btn btn-filter">Áp dụng</button>
                                        <a class="btn btn-reset" href="${pageContext.request.contextPath}/HrHomeController?section=payroll-management">Đặt lại</a>
                                    </form>
                                    
                                    <!-- Payrolls List -->
                                    <div id="payrollsList" class="payrolls-list">
                                        <c:choose>
                                            <c:when test="${not empty payrolls && payrolls.size() > 0}">
                                                <c:forEach var="payroll" items="${payrolls}">
                                                    <%
                                                        Map<String, Object> payrollMap = (Map<String, Object>) pageContext.getAttribute("payroll");
                                                        BigDecimal baseSalary = (BigDecimal) payrollMap.get("baseSalary");
                                                        BigDecimal allowance = (BigDecimal) payrollMap.get("allowance");
                                                        BigDecimal bonus = (BigDecimal) payrollMap.get("bonus");
                                                        BigDecimal deduction = (BigDecimal) payrollMap.get("deduction");
                                                        BigDecimal netSalary = (BigDecimal) payrollMap.get("netSalary");
                                                        String status = (String) payrollMap.get("status");
                                                        
                                                        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                                                    %>
                                                    <div class="payroll-card status-${fn:toLowerCase(status)}" data-payroll-id="${payroll.payrollId}">
                                                        <div class="payroll-header">
                                                            <div class="payroll-title">
                                                                <div class="employee-name">
                                                                    <i class="fas fa-user"></i> ${payroll.employeeName}
                                                                </div>
                                                                <div class="payroll-id">Mã bảng lương: #${payroll.payrollId} | Kỳ lương: ${payroll.payPeriod}</div>
                                                            </div>
                                                            <span class="status-badge ${status}">${status}</span>
                                                        </div>

                                                        <div class="payroll-details">
                                                            <div class="detail-item">
                                                                <span class="detail-label">Lương cơ bản</span>
                                                                <span class="detail-value amount"><%= nf.format(baseSalary != null ? baseSalary : BigDecimal.ZERO) %> VNĐ</span>
                                                            </div>
                                                            <div class="detail-item">
                                                                <span class="detail-label">Lương OT</span>
                                                                <span class="detail-value amount"><%= nf.format(bonus != null ? bonus : BigDecimal.ZERO) %> VNĐ</span>
                                                            </div>
                                                            <div class="detail-item">
                                                                <span class="detail-label">Phụ cấp</span>
                                                                <span class="detail-value amount"><%= nf.format(allowance != null ? allowance : BigDecimal.ZERO) %> VNĐ</span>
                                                            </div>
                                                            <div class="detail-item">
                                                                <span class="detail-label">Khấu trừ</span>
                                                                <span class="detail-value deduction"><%= nf.format(deduction != null ? deduction : BigDecimal.ZERO) %> VNĐ</span>
                                                            </div>
                                                            <div class="detail-item">
                                                                <span class="detail-label">Lương thực nhận</span>
                                                                <span class="detail-value net-amount"><%= nf.format(netSalary != null ? netSalary : BigDecimal.ZERO) %> VNĐ</span>
                                                            </div>
                                                            <c:if test="${not empty payroll.approvedDate}">
                                                                <div class="detail-item">
                                                                    <span class="detail-label">Ngày duyệt</span>
                                                                    <span class="detail-value">${payroll.approvedDate}</span>
                                                                </div>
                                                            </c:if>
                                                        </div>

                                                        <div class="payroll-actions">
                                                            <button type="button" class="btn btn-view" data-payroll-id="${payroll.payrollId}">
                                                                <i class="fas fa-eye"></i> Xem chi tiết
                                                            </button>
                                                            <c:if test="${status == 'Pending'}">
                                                                <button type="button" class="btn btn-approve" data-payroll-id="${payroll.payrollId}" data-employee="${payroll.employeeName}" data-period="${payroll.payPeriod}">
                                                                    <i class="fas fa-check"></i> Duyệt
                                                                </button>
                                                                <button type="button" class="btn btn-reject" data-payroll-id="${payroll.payrollId}" data-employee="${payroll.employeeName}" data-period="${payroll.payPeriod}">
                                                                    <i class="fas fa-times"></i> Từ chối
                                                                </button>
                                                            </c:if>
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </c:when>
                                            <c:otherwise>
                                                <div class="empty-state">
                                                    <i class="fas fa-inbox"></i>
                                                    <h3>Không tìm thấy bảng lương</h3>
                                                    <p>Không có bảng lương phù hợp với trạng thái "${payrollStatus != null ? payrollStatus : 'Pending'}" và bộ lọc hiện tại.</p>
                                                </div>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </div>
                            </div>
                            
                            <!-- Payroll History Tab -->
                            <div id="payroll-history" class="tab-panel">
                                <div class="payroll-history-section">
                                    <div class="history-header">
                                        <h3><i class="fas fa-history"></i> Lịch sử bảng lương</h3>
                                        <p>Xem các bảng lương đã được duyệt hoặc đã chi trả</p>
                                    </div>
                                    
                                    <div class="history-filters">
                                        <select id="historyEmployeeFilter">
                                            <option value="">Tất cả nhân viên</option>
                                            <c:forEach var="employee" items="${employees}">
                                                <option value="${employee.employeeId}">${employee.fullName} (ID: ${employee.employeeId})</option>
                                            </c:forEach>
                                        </select>
                                        <input type="month" id="historyMonthFilter"/>
                                        <button class="btn-primary" onclick="loadPayrollHistory()">
                                            <i class="fas fa-search"></i> Tìm kiếm
                                        </button>
                                    </div>
                                    
                                    <div id="payrollHistoryList" class="payrolls-list">
                                        <div class="empty-state">
                                            <i class="fas fa-inbox"></i>
                                            <h3>Chưa có lịch sử bảng lương</h3>
                                            <p>Sử dụng bộ lọc phía trên để tra cứu lịch sử bảng lương.</p>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <!-- Recruitment System Section -->
                    <section id="recruitment-system" class="content-section">
                        <div class="section-header">
                            <h2>Tuyển dụng</h2>
                            <p>Tạo tin tuyển dụng, rà soát hồ sơ ứng viên và theo dõi lịch phỏng vấn</p>
                        </div>
                        
                        <div class="recruitment-tabs">
                            <button class="tab-btn active" data-tab="job-postings">Tin tuyển dụng</button>
                            <button class="tab-btn" data-tab="candidate-screening">Sàng lọc ứng viên</button>
                            <button class="tab-btn" data-tab="interview-scheduling">Lịch phỏng vấn</button>
                        </div>
                        
                        <div class="tab-content">
                            <div id="job-postings" class="tab-panel active">
                                <div class="job-posting-management">
                                    <div class="section-actions">
                                        <button class="btn-primary" onclick="showJobPostingForm()">
                                            <i class="fas fa-plus"></i>
                                            Tạo tin tuyển dụng mới
                                        </button>
                                    </div>
                                    
                                    <div class="job-postings-list">
                                        <div class="job-posting-card">
                                            <div class="job-header">
                                                <h4>Senior Java Developer</h4>
                                                <span class="job-status active">Đang tuyển</span>
                                            </div>
                                            <div class="job-details">
                                                <p><strong>Phòng ban:</strong> Phòng IT</p>
                                                <p><strong>Lương:</strong> 20,000,000 - 30,000,000 VNĐ</p>
                                                <p><strong>Kinh nghiệm:</strong> 3-5 năm</p>
                                                <p><strong>Ngày đăng:</strong> 10/12/2024</p>
                                                <p><strong>Ứng viên:</strong> 25</p>
                                            </div>
                                            <div class="job-actions">
                                                <button class="btn-secondary">
                                                    <i class="fas fa-edit"></i>
                                                    Sửa
                                                </button>
                                             
                                                <button class="btn-danger">
                                                    <i class="fas fa-pause"></i>
                                                    Tạm dừng
                                                </button>
                                            </div>
                                        </div>
                                        
                                        <div class="job-posting-card">
                                            <div class="job-header">
                                                <h4>Marketing Specialist</h4>
                                                <span class="job-status active">Đang tuyển</span>
                                            </div>
                                            <div class="job-details">
                                                <p><strong>Phòng ban:</strong> Phòng Marketing</p>
                                                <p><strong>Lương:</strong> 12,000,000 - 18,000,000 VNĐ</p>
                                                <p><strong>Kinh nghiệm:</strong> 2-3 năm</p>
                                                <p><strong>Ngày đăng:</strong> 08/12/2024</p>
                                                <p><strong>Ứng viên:</strong> 18</p>
                                            </div>
                                            <div class="job-actions">
                                                <button class="btn-secondary">
                                                    <i class="fas fa-edit"></i>
                                                    Sửa
                                                </button>
                                                <button class="btn-danger">
                                                    <i class="fas fa-pause"></i>
                                                    Tạm dừng
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            
                            <div id="candidate-screening" class="tab-panel">
                                <div class="candidate-screening">
                                    <div class="screening-filters">
                                        <select class="form-select">
                                            <option>Tất cả vị trí</option>
                                            <option>Senior Java Developer</option>
                                            <option>Marketing Specialist</option>
                                        </select>
                                        <select class="form-select">
                                            <option>Tất cả trạng thái</option>
                                            <option>Hồ sơ mới</option>
                                            <option>Đã sàng lọc</option>
                                            <option>Đã phỏng vấn</option>
                                            <option>Đã tuyển</option>
                                            <option>Từ chối</option>
                                        </select>
                                    </div>
                                    
                                    <div class="candidates-list">
                                        <div class="candidate-card">
                                            <div class="candidate-info">
                                                <img src="https://i.pravatar.cc/50" alt="Ứng viên">
                                                <div>
                                                    <h4>Pham Van G</h4>
                                                    <p>Senior Java Developer</p>
                                                    <p><strong>Kinh nghiệm:</strong> 4 năm</p>
                                                    <p><strong>Ngày ứng tuyển:</strong> 12/12/2024</p>
                                                </div>
                                            </div>
                                            <div class="candidate-status">
                                                <span class="status-badge new">Hồ sơ mới</span>
                                            </div>
                                            <div class="candidate-actions">
                                                <button class="btn-primary">
                                                    <i class="fas fa-eye"></i>
                                                    Xem CV
                                                </button>
                                                <button class="btn-success">
                                                    <i class="fas fa-check"></i>
                                                    Chọn phỏng vấn
                                                </button>
                                                <button class="btn-danger">
                                                    <i class="fas fa-times"></i>
                                                    Từ chối
                                                </button>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>

                    <!-- Reports and Analytics Section -->
                    <section id="reports-analytics" class="content-section">
                        <div class="section-header">
                            <h2>Báo cáo và phân tích</h2>
                            <p>Tổng hợp báo cáo nhân sự, phòng ban và hiệu suất vận hành hệ thống</p>
                        </div>
                        
                        <div class="reports-tabs">
                            <button class="tab-btn active" data-tab="hr-reports">Báo cáo HR</button>
                            <button class="tab-btn" data-tab="department-reports">Báo cáo phòng ban</button>
                            <button class="tab-btn" data-tab="system-analytics">Phân tích hệ thống</button>
                        </div>
                        
                        <div class="tab-content">
                            <div id="hr-reports" class="tab-panel active">
                                <div class="reports-dashboard">
                                    <div class="report-filters">
                                        <div class="filter-group">
                                            <label>Thời gian:</label>
                                            <select class="form-select">
                                                <option>Tháng này</option>
                                                <option>Quý này</option>
                                                <option>Năm nay</option>
                                                <option>Tùy chỉnh</option>
                                            </select>
                                        </div>
                                        <div class="filter-group">
                                            <label>Phòng ban:</label>
                                            <select class="form-select">
                                                <option>Tất cả phòng ban</option>
                                                <option>Phòng IT</option>
                                                <option>Phòng Marketing</option>
                                                <option>Phòng HR</option>
                                            </select>
                                        </div>
                                        <button class="btn-primary">
                                            <i class="fas fa-download"></i>
                                            Xuất báo cáo
                                        </button>
                                    </div>
                                    
                                    <div class="charts-grid">
                                        <div class="chart-container">
                                            <h4>Tổng quan nhân sự</h4>
                                            <canvas id="employeeOverviewChart"></canvas>
                                        </div>
                                        <div class="chart-container">
                                            <h4>Phân bổ theo phòng ban</h4>
                                            <canvas id="departmentDistributionChart"></canvas>
                                        </div>
                                        <div class="chart-container">
                                            <h4>Tuyển dụng theo tháng</h4>
                                            <canvas id="recruitmentChart"></canvas>
                                        </div>
                                        <div class="chart-container">
                                            <h4>Tỷ lệ biến động nhân sự</h4>
                                            <canvas id="turnoverChart"></canvas>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </section>
                </div>
            </main>
        </div>

        <script>
            // Navigation function - Show specific section
            function showSection(sectionId) {
                // Hide all sections
                const sections = document.querySelectorAll('.content-section');
                sections.forEach(section => {
                    section.classList.remove('active');
                });

                // Show selected section
                const targetSection = document.getElementById(sectionId);
                if (targetSection) {
                    targetSection.classList.add('active');
                }

                // Update navigation
                const navItems = document.querySelectorAll('.nav-item');
                navItems.forEach(item => {
                    item.classList.remove('active');
                    if (item.getAttribute('data-section') === sectionId) {
                        item.classList.add('active');
                    }
                });

                // Scroll to top of content
                const contentArea = document.querySelector('.hr-content-area');
                if (contentArea) {
                    contentArea.scrollTop = 0;
                }
            }
            
            // Update URL without reloading page
            function updateURL(params) {
                const url = new URL(window.location);
                Object.keys(params).forEach(key => {
                    if (params[key]) {
                        url.searchParams.set(key, params[key]);
                    } else {
                        url.searchParams.delete(key);
                    }
                });
                window.history.pushState({}, '', url);
            }
            
            // Initialize navigation on page load
            document.addEventListener('DOMContentLoaded', function() {
                const appContext = '${pageContext.request.contextPath}';
                // Sidebar navigation
                const menuItems = document.querySelectorAll('.nav-item[data-section]');

                // Handle navigation items with data-section
                menuItems.forEach(item => {
                    item.addEventListener('click', function(e) {
                        e.preventDefault();
                        const sectionId = this.getAttribute('data-section');
                        showSection(sectionId);
                    });
                });
                
                // Handle access cards with data-section
                const accessCards = document.querySelectorAll('.access-card[data-section]');
                accessCards.forEach(card => {
                    card.addEventListener('click', function(e) {
                        e.preventDefault();
                        const sectionId = this.getAttribute('data-section');
                        showSection(sectionId);
                    });
                });
                
                // Handle tab buttons
                const tabButtons = document.querySelectorAll('.tab-btn');
                tabButtons.forEach(button => {
                    button.addEventListener('click', function() {
                        const tabId = this.getAttribute('data-tab');
                        const tabContainer = this.closest('.content-section');
                        if (tabContainer) {
                            // Remove active from all tab buttons in this container
                            tabContainer.querySelectorAll('.tab-btn').forEach(btn => {
                                btn.classList.remove('active');
                            });
                            // Add active to clicked button
                            this.classList.add('active');
                            
                            // Hide all tab panels in this container
                            tabContainer.querySelectorAll('.tab-panel').forEach(panel => {
                                panel.classList.remove('active');
                            });
                            
                            // Show selected tab panel
                            const targetPanel = tabContainer.querySelector('#' + tabId);
                            if (targetPanel) {
                                targetPanel.classList.add('active');
                            }
                        }
                    });
                });
            });
            
            // Confirm status update
            function confirmStatusUpdate(form) {
                const employeeName = form.closest('.status-card').querySelector('h4').textContent;
                const newStatus = form.querySelector('select[name="status"]').value;
                return confirm(`Bạn có chắc muốn cập nhật trạng thái của ${employeeName} thành ${newStatus}?`);
            }
            
            // Handle form submissions with AJAX to avoid page reload
            document.addEventListener('DOMContentLoaded', function() {
                // Handle status update forms
                document.querySelectorAll('form[action*="EmploymentStatusController"]').forEach(form => {
                    form.addEventListener('submit', function(e) {
                        e.preventDefault();
                        const formData = new FormData(this);
                        
                        fetch('/HRMS/EmploymentStatusController', {
                            method: 'POST',
                            body: formData
                        })
                        .then(response => response.text())
                        .then(data => {
                            // Show success message
                            showMessage('Cập nhật trạng thái thành công!', 'success');
                            // Update the UI
                            const statusSpan = this.closest('.status-card').querySelector('.current-status');
                            const newStatus = formData.get('status');
                            statusSpan.textContent = newStatus;
                            statusSpan.className = `current-status ${newStatus.toLowerCase()}`;
                        })
                        .catch(error => {
                            showMessage('Lỗi khi cập nhật trạng thái: ' + error.message, 'error');
                        });
                    });
                });
            });
            
            // Show message function
            function showMessage(message, type) {
                const messageDiv = document.createElement('div');
                messageDiv.className = 'message ' + type;
                messageDiv.innerHTML = 
                    '<i class="fas fa-' + (type === 'success' ? 'check-circle' : 'exclamation-triangle') + '"></i>' +
                    message;
                
                // Add to the top of the content area
                const contentArea = document.querySelector('.hr-content-area');
                contentArea.insertBefore(messageDiv, contentArea.firstChild);
                
                // Auto remove after 5 seconds
                setTimeout(function() {
                    messageDiv.remove();
                }, 5000);
            }
            
            // Set last updated time
            if (document.getElementById('lastUpdatedTime')) {
                document.getElementById('lastUpdatedTime').textContent = new Date().toLocaleString();
            }
            
            // Payroll Approval Functions
            let currentStatus = 'Pending';
            
            function getCurrentStatus() {
                const activeTab = document.querySelector('.status-tab-btn.active');
                return activeTab ? activeTab.getAttribute('data-status') : 'Pending';
            }
            
            function loadPayrollsByStatus(status) {
                const params = new URLSearchParams(window.location.search);
                params.set('section', 'payroll-management');
                params.set('payrollStatus', status);
                const employeeSelect = document.getElementById('employeeFilter');
                const monthInput = document.getElementById('monthFilter');
                if (employeeSelect && employeeSelect.value) {
                    params.set('employeeFilter', employeeSelect.value);
                } else {
                    params.delete('employeeFilter');
                }
                if (monthInput && monthInput.value) {
                    params.set('monthFilter', monthInput.value);
                } else {
                    params.delete('monthFilter');
                }
                window.location.href = appContext + '/HrHomeController?' + params.toString();
            }
            
            function updateCountsFromPage(doc) {
                // Try to get counts from status tabs
                const pendingBadge = doc.querySelector('#pendingCount');
                const approvedBadge = doc.querySelector('#approvedCount');
                const rejectedBadge = doc.querySelector('#rejectedCount');
                const paidBadge = doc.querySelector('#paidCount');
                
                if (pendingBadge && document.getElementById('pendingCount')) {
                    document.getElementById('pendingCount').textContent = pendingBadge.textContent;
                }
                if (approvedBadge && document.getElementById('approvedCount')) {
                    document.getElementById('approvedCount').textContent = approvedBadge.textContent;
                }
                if (rejectedBadge && document.getElementById('rejectedCount')) {
                    document.getElementById('rejectedCount').textContent = rejectedBadge.textContent;
                }
                if (paidBadge && document.getElementById('paidCount')) {
                    document.getElementById('paidCount').textContent = paidBadge.textContent;
                }
            }
            
            function attachPayrollEventListeners() {
                // Approve buttons
                document.querySelectorAll('.btn-approve').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const payrollId = this.dataset.payrollId;
                        const employeeName = this.dataset.employee || this.closest('.payroll-card')?.querySelector('.employee-name')?.textContent?.trim() || 'Nhân viên';
                        const payPeriod = this.dataset.period || this.closest('.payroll-card')?.querySelector('.payroll-id')?.textContent?.match(/Kỳ lương: (.+)/)?.[1] || '';
                        if (payrollId) approvePayroll(payrollId, employeeName, payPeriod);
                    });
                });
                
                // Reject buttons
                document.querySelectorAll('.btn-reject').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const payrollId = this.dataset.payrollId;
                        const employeeName = this.dataset.employee || this.closest('.payroll-card')?.querySelector('.employee-name')?.textContent?.trim() || 'Nhân viên';
                        const payPeriod = this.dataset.period || this.closest('.payroll-card')?.querySelector('.payroll-id')?.textContent?.match(/Kỳ lương: (.+)/)?.[1] || '';
                        if (payrollId) rejectPayroll(payrollId, employeeName, payPeriod);
                    });
                });
                
                // View details buttons
                document.querySelectorAll('.btn-view').forEach(btn => {
                    btn.addEventListener('click', function() {
                        const payrollId = this.dataset.payrollId || this.closest('.payroll-card')?.dataset.payrollId;
                        if (payrollId) viewPayrollDetails(payrollId);
                    });
                });
            }
            
            function approvePayroll(payrollId, employeeName, payPeriod) {
                if (confirm('Bạn có chắc muốn duyệt bảng lương này?\n\nNhân viên: ' + employeeName + '\nKỳ lương: ' + payPeriod)) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/hr/payroll-approval/approve';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'approve';
                    form.appendChild(actionInput);
                    
                    const payrollIdInput = document.createElement('input');
                    payrollIdInput.type = 'hidden';
                    payrollIdInput.name = 'payrollId';
                    payrollIdInput.value = payrollId;
                    form.appendChild(payrollIdInput);
                    
                    document.body.appendChild(form);
                    form.submit();
                }
            }
            
            function rejectPayroll(payrollId, employeeName, payPeriod) {
                const reason = prompt('Nhập lý do từ chối (không bắt buộc):');
                if (reason !== null) {
                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '${pageContext.request.contextPath}/hr/payroll-approval/reject';
                    
                    const actionInput = document.createElement('input');
                    actionInput.type = 'hidden';
                    actionInput.name = 'action';
                    actionInput.value = 'reject';
                    form.appendChild(actionInput);
                    
                    const payrollIdInput = document.createElement('input');
                    payrollIdInput.type = 'hidden';
                    payrollIdInput.name = 'payrollId';
                    payrollIdInput.value = payrollId;
                    form.appendChild(payrollIdInput);
                    
                    if (reason) {
                        const reasonInput = document.createElement('input');
                        reasonInput.type = 'hidden';
                        reasonInput.name = 'rejectReason';
                        reasonInput.value = reason;
                        form.appendChild(reasonInput);
                    }
                    
                    document.body.appendChild(form);
                    form.submit();
                }
            }
            
            function viewPayrollDetails(payrollId) {
                window.open('${pageContext.request.contextPath}/hr/payroll-approval/details?payrollId=' + payrollId, '_blank');
            }
            
            function loadPayrollHistory() {
                const employeeId = document.getElementById('historyEmployeeFilter') ? document.getElementById('historyEmployeeFilter').value : '';
                const month = document.getElementById('historyMonthFilter') ? document.getElementById('historyMonthFilter').value : '';
                
                const historyList = document.getElementById('payrollHistoryList');
                if (!historyList) return;
                
                historyList.innerHTML = '<div class="loading-state"><i class="fas fa-spinner fa-spin"></i><p>Đang tải lịch sử bảng lương...</p></div>';
                
                // Build URL - show Approved and Paid payrolls
                let url = '${pageContext.request.contextPath}/hr/payroll-approval?status=Approved';
                if (employeeId) url += '&employeeFilter=' + employeeId;
                if (month) url += '&monthFilter=' + month;
                
                fetch(url)
                    .then(response => response.text())
                    .then(html => {
                        const parser = new DOMParser();
                        const doc = parser.parseFromString(html, 'text/html');
                        const payrollsSection = doc.querySelector('.payrolls-list') || doc.querySelector('#payrollsList');
                        
                        if (payrollsSection && payrollsSection.innerHTML.trim()) {
                            const cards = payrollsSection.querySelectorAll('.payroll-card');
                            if (cards.length > 0) {
                                let cardsHTML = '';
                                cards.forEach(card => {
                                    cardsHTML += card.outerHTML;
                                });
                                historyList.innerHTML = cardsHTML;
                                attachPayrollEventListeners();
                            } else {
                                historyList.innerHTML = '<div class="empty-state"><i class="fas fa-inbox"></i><h3>Không tìm thấy lịch sử bảng lương</h3><p>Không có bảng lương đã duyệt phù hợp với bộ lọc.</p></div>';
                            }
                        } else {
                            historyList.innerHTML = '<div class="empty-state"><i class="fas fa-inbox"></i><h3>Không tìm thấy lịch sử bảng lương</h3><p>Không có bảng lương đã duyệt phù hợp với bộ lọc.</p></div>';
                        }
                    })
                    .catch(err => {
                        console.error('Lỗi tải lịch sử bảng lương:', err);
                        historyList.innerHTML = '<div class="empty-state"><i class="fas fa-exclamation-circle"></i><h3>Lỗi tải lịch sử</h3><p>' + err.message + '</p></div>';
                    });
            }
            
            // Add URL restoration to existing DOMContentLoaded
            // This code will run after the first DOMContentLoaded
            if (document.readyState === 'loading') {
                document.addEventListener('DOMContentLoaded', function() {
                    initURLState();
                });
            } else {
                // DOM already loaded
                initURLState();
            }
            
            function initURLState() {
                // Check URL parameters to restore state
                const urlParams = new URLSearchParams(window.location.search);
                const section = urlParams.get('section');
                const payrollStatus = urlParams.get('payrollStatus');
                
                // If section is payroll-management, show it
                if (section === 'payroll-management') {
                    setTimeout(() => {
                        showSection('payroll-management');
                        // Data is already loaded from backend, no need to reload
                    }, 100);
                } else if (section) {
                    // Show other sections from URL
                    setTimeout(() => {
                        showSection(section);
                    }, 100);
                }
                
                // Load initial data when payroll-approval tab is clicked (if not already loaded from backend)
                const payrollApprovalTab = document.querySelector('[data-tab="payroll-approval"]');
                if (payrollApprovalTab) {
                    payrollApprovalTab.addEventListener('click', function() {
                        setTimeout(() => {
                            const tabPanel = document.getElementById('payroll-approval');
                            if (tabPanel && tabPanel.classList.contains('active')) {
                                // Check if we have URL params - if not, load initial data
                                const urlParams = new URLSearchParams(window.location.search);
                                if (!urlParams.get('section') || urlParams.get('section') !== 'payroll-management') {
                                    loadPayrollsByStatus('Pending');
                                }
                            }
                        }, 100);
                    });
                }
                
                // Also handle when clicking on Payroll section from sidebar
                const payrollNavItem = document.querySelector('.nav-item[data-section="payroll-management"]');
                if (payrollNavItem) {
                    payrollNavItem.addEventListener('click', function(e) {
                        // Redirect to payroll approval page
                        e.preventDefault();
                        window.location.href = '${pageContext.request.contextPath}/hr/payroll-approval?status=Pending';
                    });
                }
            }
            
            // Update URL without reloading page (for non-payroll sections)
            function updateURL(params) {
                const url = new URL(window.location);
                Object.keys(params).forEach(key => {
                    if (params[key]) {
                        url.searchParams.set(key, params[key]);
                    } else {
                        url.searchParams.delete(key);
                    }
                });
                window.history.pushState({}, '', url);
            }
        </script>
    </body>
</html>
