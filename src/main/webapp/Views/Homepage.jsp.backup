<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>BetterHR - Quản lý nhân sự chuyên nghiệp</title>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Material+Symbols+Outlined:wght,FILL@100..700,0..1&display=swap" rel="stylesheet">
    <style>
        :root {
            --text-primary: #1b1c19;
            --text-secondary: #5e665f;
            --background: #fbf9f4;
            --canvas-warm: #f2f0eb;
            --surface-low: #f5f3ee;
            --surface-white: #ffffff;
            --surface-container: #f0eee9;
            --surface-container-high: #eae8e3;
            --primary: #00482f;
            --primary-container: #006241;
            --secondary: #006c44;
            --secondary-container: #97f6c0;
            --secondary-fixed: #97f6c0;
            --on-primary: #ffffff;
            --outline: #6f7a72;
            --outline-variant: #bec9c0;
            --house-green: #1e3932;
            --accent-gold: #cba258;
            --error-red: #c82014;
            --radius-card: 8px;
            --radius-pill: 999px;
            --shadow-whisper: 0 0 0.5px rgba(0,0,0,0.14), 0 1px 1px rgba(0,0,0,0.22);
            --shadow-lift: 0 18px 44px rgba(30, 57, 50, 0.16);
            --gutter: 40px;
        }

        * {
            box-sizing: border-box;
        }

        html {
            scroll-behavior: smooth;
        }

        body {
            margin: 0;
            font-family: "Hanken Grotesk", "Segoe UI", Arial, sans-serif;
            color: var(--text-primary);
            background: var(--canvas-warm);
            -webkit-font-smoothing: antialiased;
            letter-spacing: 0;
        }

        body::before {
            content: "";
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            height: 6px;
            z-index: 80;
            background: #a8cfe0;
        }

        a {
            color: inherit;
            text-decoration: none;
        }

        button,
        input,
        select {
            font: inherit;
        }

        .material-symbols-outlined {
            font-variation-settings: "FILL" 0, "wght" 500, "GRAD" 0, "opsz" 24;
            line-height: 1;
        }

        .site-header {
            position: fixed;
            top: 0;
            left: 0;
            right: 0;
            z-index: 50;
            height: 72px;
            background: rgba(255,255,255,0.96);
            border-bottom: 1px solid rgba(0, 0, 0, 0.08);
            box-shadow: 0 1px 1px rgba(0,0,0,0.08);
            backdrop-filter: blur(14px);
        }

        .header-inner {
            width: min(1240px, 100%);
            height: 100%;
            margin: 0 auto;
            padding: 0 var(--gutter);
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 28px;
        }

        .brand-row,
        .nav-links,
        .header-actions {
            display: flex;
            align-items: center;
        }

        .brand-row {
            min-width: 0;
            gap: 42px;
        }

        .brand {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            color: var(--primary-container);
            font-size: 25px;
            line-height: 1;
            font-weight: 800;
            white-space: nowrap;
            letter-spacing: -0.02em;
        }

        .brand-logo {
            width: 36px;
            height: 36px;
            border-radius: 50%;
            object-fit: contain;
            background: #ffffff;
            box-shadow: 0 0 0 1px rgba(0, 98, 65, 0.12);
        }

        .nav-links {
            gap: 28px;
        }

        .nav-link {
            color: var(--text-secondary);
            font-size: 15px;
            font-weight: 700;
            transition: color 0.18s ease;
        }

        .nav-link:hover {
            color: var(--primary);
        }

        .header-actions {
            gap: 12px;
            position: relative;
        }

        .btn {
            min-height: 42px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 8px;
            border-radius: var(--radius-pill);
            border: 1px solid transparent;
            padding: 9px 20px;
            font-size: 14px;
            font-weight: 800;
            cursor: pointer;
            white-space: nowrap;
            transition: transform 0.18s ease, background 0.18s ease, border-color 0.18s ease, box-shadow 0.18s ease;
        }

        .btn:active,
        .active-scale:active {
            transform: scale(0.96);
        }

        .btn-primary {
            background: var(--primary);
            color: var(--on-primary);
            border-color: var(--primary);
        }

        .btn-primary:hover {
            background: var(--secondary);
            border-color: var(--secondary);
            box-shadow: 0 12px 28px rgba(0, 98, 65, 0.24);
        }

        .btn-outline {
            background: #fff;
            color: var(--primary);
            border-color: var(--outline-variant);
        }

        .btn-outline:hover {
            border-color: var(--primary);
            background: rgba(0, 98, 65, 0.06);
        }

        .dashboard-wrap,
        .profile-wrap {
            position: relative;
        }

        .dashboard-menu,
        .profile-menu {
            position: absolute;
            top: calc(100% + 12px);
            right: 0;
            width: 292px;
            display: none;
            flex-direction: column;
            gap: 8px;
            padding: 10px;
            border: 1px solid rgba(0, 0, 0, 0.12);
            border-radius: 14px;
            background: var(--surface-white);
            box-shadow: var(--shadow-lift);
        }

        .dashboard-menu.show,
        .profile-menu.show {
            display: flex;
        }

        .menu-item {
            display: flex;
            align-items: center;
            gap: 12px;
            padding: 12px;
            border-radius: 10px;
            color: var(--text-primary);
            border: 1px solid rgba(0, 0, 0, 0.08);
            background: #ffffff;
            transition: background 0.18s ease, color 0.18s ease, transform 0.18s ease;
        }

        .menu-item:hover {
            background: var(--surface-container);
            color: var(--primary-container);
            transform: translateY(-1px);
        }

        .menu-item .material-symbols-outlined {
            color: var(--primary);
        }

        .menu-item small {
            display: block;
            margin-top: 2px;
            color: var(--text-secondary);
            font-size: 12px;
            font-weight: 600;
        }

        .menu-item.admin { border-left: 4px solid var(--error-red); }
        .menu-item.hr { border-left: 4px solid var(--secondary); }
        .menu-item.dept { border-left: 4px solid var(--accent-gold); }
        .menu-item.employee { border-left: 4px solid var(--primary-container); }
        .menu-item.guest { border-left: 4px solid var(--outline); }

        .avatar {
            width: 42px;
            height: 42px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            border: 1px solid rgba(0, 0, 0, 0.10);
            border-radius: 50%;
            background: var(--secondary);
            color: #ffffff;
            cursor: pointer;
            box-shadow: var(--shadow-whisper);
        }

        main {
            padding-top: 72px;
        }

        .hero {
            position: relative;
            min-height: 640px;
            display: flex;
            align-items: center;
            overflow: hidden;
            color: var(--text-primary);
            background:
                radial-gradient(circle at 18% 28%, rgba(151, 246, 192, 0.23), transparent 30%),
                radial-gradient(circle at 82% 20%, rgba(151, 246, 192, 0.18), transparent 28%),
                linear-gradient(180deg, #fbfaf6 0%, #f6f3ec 100%);
        }

        .hero::before {
            content: "";
            position: absolute;
            inset: 0;
            background-image:
                linear-gradient(rgba(0, 108, 68, 0.045) 1px, transparent 1px),
                linear-gradient(90deg, rgba(0, 108, 68, 0.045) 1px, transparent 1px);
            background-size: 56px 56px;
            mask-image: radial-gradient(circle at center, #000 0%, transparent 72%);
            z-index: 1;
        }

        .hero-bg {
            position: absolute;
            inset: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
            opacity: 0.08;
            filter: saturate(0.65) blur(1px);
        }

        .hero-inner {
            position: relative;
            z-index: 2;
            width: min(1180px, 100%);
            margin: 0 auto;
            padding: 84px var(--gutter) 96px;
            display: flex;
            justify-content: center;
            text-align: center;
        }

        .hero-copy {
            max-width: 1040px;
            width: 100%;
        }

        .hero-bubble {
            position: absolute;
            border: 1px solid rgba(0, 108, 68, 0.16);
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.38);
            box-shadow: inset 0 0 20px rgba(151, 246, 192, 0.12);
        }

        .hero-bubble.one {
            width: 18px;
            height: 18px;
            left: 7%;
            top: 28%;
        }

        .hero-bubble.two {
            width: 118px;
            height: 118px;
            right: 17%;
            top: 18%;
        }

        .hero-bubble.three {
            width: 72px;
            height: 72px;
            left: 22%;
            bottom: 17%;
        }

        .eyebrow {
            display: inline-flex;
            align-items: center;
            justify-content: center;
            min-height: 26px;
            margin: 0 0 18px;
            padding: 0 13px;
            border-radius: var(--radius-pill);
            color: var(--secondary);
            background: rgba(0, 108, 68, 0.10);
            font-size: 12px;
            font-weight: 900;
            letter-spacing: 0;
            text-transform: none;
        }

        .hero h1 {
            margin: 0 0 22px;
            max-width: none;
            color: #242622;
            font-size: clamp(40px, 5vw, 64px);
            line-height: 1;
            font-weight: 500;
            letter-spacing: 0;
        }

        .hero-title-line {
            display: block;
            color: var(--secondary);
            font-size: clamp(30px, 3.8vw, 46px);
            line-height: 1.22;
            font-weight: 800;
        }

        .hero-title-main {
            white-space: nowrap;
        }

        .hero-title-line:last-child {
            color: var(--text-primary);
        }

        .hero-title-line strong {
            color: var(--text-primary);
        }

        .hero p {
            max-width: 720px;
            margin: 18px auto 0;
            color: var(--text-secondary);
            font-size: 15px;
            line-height: 1.65;
            font-weight: 600;
        }

        .hero-actions {
            display: flex;
            align-items: center;
            justify-content: center;
            gap: 14px;
            flex-wrap: wrap;
            margin-top: 30px;
        }

        .hero-actions .btn {
            min-height: 48px;
            min-width: 168px;
            padding: 12px 28px;
        }

        .hero-actions .btn-outline {
            background: #ffffff;
            color: var(--primary);
            border-color: rgba(0, 72, 47, 0.18);
            backdrop-filter: none;
        }

        .hero-visual {
            display: none;
        }

        .hero-visual img {
            width: 100%;
            display: block;
            border-radius: 12px;
            box-shadow: 0 22px 50px rgba(0,0,0,0.28);
        }

        .section {
            padding: 72px var(--gutter);
        }

        .section-inner {
            width: min(1240px, 100%);
            margin: 0 auto;
        }

        .service-card,
        .job-card,
        .faq-card {
            border: 1px solid var(--outline-variant);
            border-radius: var(--radius-card);
            background: var(--surface-white);
            box-shadow: var(--shadow-whisper);
        }

        .service-card:hover,
        .job-card:hover {
            transform: translateY(-4px);
            box-shadow: var(--shadow-lift);
        }

        .section-heading {
            max-width: 720px;
            margin: 0 auto 42px;
            text-align: center;
        }

        .section-heading.left {
            margin-left: 0;
            text-align: left;
        }

        .section-heading h2 {
            margin: 0;
            color: var(--primary);
            font-size: clamp(30px, 4vw, 42px);
            line-height: 1.15;
            font-weight: 800;
            letter-spacing: -0.03em;
        }

        .section-heading p {
            margin: 14px 0 0;
            color: var(--text-secondary);
            font-size: 17px;
            line-height: 1.65;
        }

        .heading-rule {
            width: 56px;
            height: 4px;
            margin: 18px auto 0;
            border-radius: 999px;
            background: var(--secondary);
        }

        .services-section,
        .jobs-section,
        .contact-section {
            background: var(--background);
        }

        .service-grid {
            display: grid;
            grid-template-columns: repeat(4, minmax(0, 1fr));
            gap: 22px;
        }

        .service-card {
            min-height: 260px;
            padding: 28px;
            display: flex;
            flex-direction: column;
            transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
        }

        .service-card:hover {
            border-color: rgba(0, 98, 65, 0.36);
        }

        .icon-box {
            width: 52px;
            height: 52px;
            border-radius: 12px;
            display: grid;
            place-items: center;
            margin-bottom: 24px;
            color: var(--primary);
            background: rgba(151, 246, 192, 0.34);
        }

        .icon-box .material-symbols-outlined {
            font-size: 29px;
        }

        .service-card h3 {
            margin: 0;
            color: var(--primary);
            font-size: 20px;
            font-weight: 800;
        }

        .service-card p {
            margin: 14px 0 24px;
            color: var(--text-secondary);
            line-height: 1.6;
        }

        .text-link {
            margin-top: auto;
            display: inline-flex;
            align-items: center;
            gap: 6px;
            color: var(--secondary);
            font-weight: 800;
        }

        .why-section,
        .faq-section {
            background: var(--canvas-warm);
        }

        .why-grid {
            display: grid;
            grid-template-columns: minmax(300px, 0.9fr) minmax(0, 1fr);
            gap: 72px;
            align-items: center;
        }

        .image-frame {
            position: relative;
        }

        .image-frame::after {
            content: "";
            position: absolute;
            right: -22px;
            bottom: -22px;
            width: 190px;
            height: 190px;
            border-radius: 14px;
            background: rgba(0, 72, 47, 0.08);
            z-index: 0;
        }

        .image-frame img {
            position: relative;
            z-index: 1;
            width: 100%;
            aspect-ratio: 1 / 1;
            object-fit: cover;
            display: block;
            border-radius: var(--radius-card);
            box-shadow: var(--shadow-lift);
        }

        .reason-grid {
            display: grid;
            grid-template-columns: repeat(2, minmax(0, 1fr));
            gap: 24px;
        }

        .reason-item {
            display: grid;
            grid-template-columns: auto minmax(0, 1fr);
            gap: 14px;
        }

        .reason-icon {
            width: 42px;
            height: 42px;
            border-radius: 50%;
            display: grid;
            place-items: center;
            background: rgba(0, 108, 68, 0.11);
            color: var(--secondary);
        }

        .reason-item h3 {
            margin: 0 0 4px;
            color: var(--primary);
            font-size: 17px;
        }

        .reason-item p {
            margin: 0;
            color: var(--text-secondary);
            line-height: 1.5;
            font-size: 14px;
        }

        .jobs-header {
            display: flex;
            align-items: flex-end;
            justify-content: space-between;
            gap: 24px;
            margin-bottom: 30px;
        }

        .job-tools {
            display: flex;
            gap: 12px;
            align-items: center;
        }

        .job-search,
        .job-select {
            min-height: 44px;
            border: 1px solid var(--outline-variant);
            border-radius: 12px;
            background: var(--surface-white);
            color: var(--text-primary);
            outline-color: var(--primary);
        }

        .job-search {
            width: 280px;
            padding: 0 14px 0 42px;
        }

        .search-wrap {
            position: relative;
        }

        .search-wrap .material-symbols-outlined {
            position: absolute;
            left: 13px;
            top: 50%;
            transform: translateY(-50%);
            color: var(--outline);
        }

        .job-select {
            min-width: 172px;
            padding: 0 12px;
        }

        .job-grid {
            display: grid;
            grid-template-columns: repeat(3, minmax(0, 1fr));
            gap: 24px;
        }

        .job-card {
            min-height: 300px;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            padding: 26px;
            transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
        }

        .job-card:hover {
            border-color: rgba(0, 98, 65, 0.42);
        }

        .job-top {
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            gap: 16px;
            margin-bottom: 22px;
        }

        .job-tag {
            display: inline-flex;
            align-items: center;
            min-height: 28px;
            padding: 0 10px;
            border-radius: var(--radius-pill);
            background: rgba(162, 243, 200, 0.5);
            color: #005235;
            font-size: 12px;
            font-weight: 900;
            letter-spacing: 0.04em;
            text-transform: uppercase;
        }

        .job-tag.gold {
            background: rgba(203, 162, 88, 0.18);
            color: #7a570f;
        }

        .job-card h3 {
            margin: 0 0 16px;
            color: var(--primary);
            font-size: 22px;
            line-height: 1.25;
            font-weight: 800;
        }

        .job-meta {
            display: grid;
            gap: 10px;
            color: var(--text-secondary);
            font-size: 14px;
            font-weight: 600;
        }

        .job-meta span {
            display: inline-flex;
            align-items: center;
            gap: 8px;
        }

        .job-footer {
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            padding-top: 22px;
            border-top: 1px solid var(--surface-container);
        }

        .salary {
            color: var(--primary);
            font-size: 16px;
            font-weight: 900;
        }

        .job-footer .btn {
            min-height: 38px;
            padding: 8px 18px;
        }

        .process-section {
            overflow: hidden;
            background: var(--primary);
            color: #fff;
        }

        .process-section .section-heading h2 {
            color: #fff;
        }

        .process-grid {
            position: relative;
            display: grid;
            grid-template-columns: repeat(5, minmax(0, 1fr));
            gap: 22px;
            margin-top: 54px;
        }

        .process-grid::before {
            content: "";
            position: absolute;
            left: 8%;
            right: 8%;
            top: 32px;
            height: 2px;
            background: rgba(255,255,255,0.12);
        }

        .process-item {
            position: relative;
            z-index: 1;
            text-align: center;
        }

        .step-number {
            width: 64px;
            height: 64px;
            margin: 0 auto 20px;
            display: grid;
            place-items: center;
            border-radius: 50%;
            background: var(--secondary-container);
            color: var(--primary);
            font-size: 20px;
            font-weight: 900;
            box-shadow: 0 0 0 8px var(--primary);
        }

        .process-item:last-child .step-number {
            background: var(--accent-gold);
        }

        .process-item h3 {
            margin: 0 0 8px;
            font-size: 16px;
            font-weight: 800;
        }

        .process-item p {
            margin: 0 auto;
            max-width: 180px;
            color: rgba(255,255,255,0.74);
            line-height: 1.5;
            font-size: 13px;
        }

        .culture-grid {
            display: grid;
            grid-template-columns: repeat(4, 1fr);
            grid-auto-rows: 180px;
            gap: 18px;
        }

        .culture-card {
            overflow: hidden;
            border-radius: var(--radius-card);
            box-shadow: var(--shadow-whisper);
            background: var(--surface-container);
        }

        .culture-card.large {
            grid-column: span 2;
            grid-row: span 2;
        }

        .culture-card.wide {
            grid-column: span 2;
        }

        .culture-card img {
            width: 100%;
            height: 100%;
            display: block;
            object-fit: cover;
            transition: transform 0.6s ease;
        }

        .culture-card:hover img {
            transform: scale(1.04);
        }

        .faq-list {
            max-width: 860px;
            margin: 0 auto;
            display: grid;
            gap: 14px;
        }

        .faq-card {
            overflow: hidden;
        }

        .faq-card button {
            width: 100%;
            min-height: 64px;
            border: 0;
            background: var(--surface-white);
            color: var(--primary);
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 16px;
            padding: 20px 24px;
            text-align: left;
            font-weight: 800;
            cursor: pointer;
        }

        .accordion-content {
            max-height: 0;
            overflow: hidden;
            color: var(--text-secondary);
            line-height: 1.65;
            transition: max-height 0.28s ease;
        }

        .accordion-open .accordion-content {
            max-height: 240px;
        }

        .accordion-open .accordion-icon {
            transform: rotate(180deg);
        }

        .accordion-body {
            padding: 0 24px 22px;
            border-top: 1px solid var(--surface-container);
        }

        .contact-grid {
            display: grid;
            grid-template-columns: minmax(0, 0.9fr) minmax(300px, 1.1fr);
            gap: 72px;
            align-items: start;
        }

        .contact-list {
            display: grid;
            gap: 22px;
            margin-top: 30px;
        }

        .contact-item {
            display: flex;
            align-items: center;
            gap: 16px;
            color: var(--text-secondary);
            font-size: 17px;
            font-weight: 700;
        }

        .contact-item .material-symbols-outlined {
            width: 52px;
            height: 52px;
            display: grid;
            place-items: center;
            border-radius: 50%;
            background: var(--surface-white);
            color: var(--secondary);
            box-shadow: var(--shadow-whisper);
        }

        .contact-panel {
            padding: 34px;
            border-radius: var(--radius-card);
            background: var(--surface-white);
            border: 1px solid var(--outline-variant);
            box-shadow: var(--shadow-whisper);
        }

        .contact-panel h3 {
            margin: 0 0 18px;
            color: var(--primary);
            font-size: 22px;
        }

        .contact-panel p {
            margin: 0 0 24px;
            color: var(--text-secondary);
            line-height: 1.6;
        }

        .site-footer {
            padding: 60px var(--gutter) 112px;
            background: var(--canvas-warm);
            border-top: 1px solid rgba(0,0,0,0.08);
        }

        .chatbot-bubble {
            position: fixed;
            right: 28px;
            bottom: 28px;
            z-index: 70;
            width: 74px;
            height: 74px;
            border: 0;
            border-radius: 50%;
            background: linear-gradient(145deg, #ffffff 0%, #eff8f1 100%);
            box-shadow: 0 18px 42px rgba(0, 72, 47, 0.24), 0 0 0 1px rgba(0, 108, 68, 0.16);
            display: grid;
            place-items: center;
            cursor: pointer;
            transition: transform 0.18s ease, box-shadow 0.18s ease;
        }

        .chatbot-bubble:hover {
            transform: translateY(-4px) scale(1.04);
            box-shadow: 0 24px 50px rgba(0, 72, 47, 0.28), 0 0 0 1px rgba(0, 108, 68, 0.20);
        }

        .chatbot-bubble::before {
            content: "";
            position: absolute;
            inset: -8px;
            border-radius: inherit;
            border: 1px solid rgba(0, 108, 68, 0.15);
            animation: chatbotPulse 2.4s ease-out infinite;
        }

        .chatbot-bubble img {
            width: 46px;
            height: 46px;
            object-fit: contain;
            border-radius: 50%;
        }

        .chatbot-badge {
            position: absolute;
            right: -2px;
            bottom: 2px;
            width: 25px;
            height: 25px;
            display: grid;
            place-items: center;
            border-radius: 50%;
            background: var(--secondary);
            color: #ffffff;
            border: 2px solid #ffffff;
        }

        .chatbot-badge .material-symbols-outlined {
            font-size: 15px;
            font-variation-settings: "FILL" 1, "wght" 600, "GRAD" 0, "opsz" 20;
        }

        @keyframes chatbotPulse {
            0% {
                opacity: 0.65;
                transform: scale(0.92);
            }
            100% {
                opacity: 0;
                transform: scale(1.28);
            }
        }

        .footer-inner {
            width: min(1240px, 100%);
            margin: 0 auto;
            display: grid;
            grid-template-columns: 1.2fr repeat(3, minmax(160px, 1fr));
            gap: 42px;
        }

        .footer-brand h2,
        .footer-col h3 {
            margin: 0 0 16px;
            color: var(--primary);
        }

        .footer-brand p,
        .footer-col a,
        .footer-bottom {
            color: var(--text-secondary);
            line-height: 1.6;
            font-weight: 600;
        }

        .footer-col {
            display: grid;
            align-content: start;
            gap: 10px;
        }

        .footer-col a:hover {
            color: var(--primary);
            text-decoration: underline;
        }

        .footer-bottom {
            width: min(1240px, 100%);
            margin: 36px auto 0;
            padding-top: 22px;
            border-top: 1px solid rgba(0,0,0,0.08);
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 18px;
            flex-wrap: wrap;
            font-size: 14px;
        }

        @media (max-width: 1080px) {
            .hero-inner,
            .why-grid,
            .contact-grid {
                grid-template-columns: 1fr;
            }

            .hero-title-main {
                white-space: normal;
            }

            .hero-visual {
                max-width: 620px;
            }

            .service-grid {
                grid-template-columns: repeat(2, minmax(0, 1fr));
            }

            .jobs-header {
                align-items: flex-start;
                flex-direction: column;
            }

            .job-grid,
            .process-grid {
                grid-template-columns: 1fr;
            }

            .process-grid::before {
                display: none;
            }
        }

        @media (max-width: 780px) {
            :root {
                --gutter: 20px;
            }

            .site-header {
                height: auto;
                min-height: 72px;
            }

            .header-inner {
                min-height: 72px;
                padding: 10px 16px;
                align-items: flex-start;
                flex-direction: column;
            }

            .brand-row {
                width: 100%;
                justify-content: space-between;
            }

            .nav-links {
                display: none;
            }

            .header-actions {
                width: 100%;
                justify-content: flex-start;
                flex-wrap: wrap;
            }

            main {
                padding-top: 112px;
            }

            .hero {
                min-height: auto;
            }

            .hero-inner {
                padding-top: 54px;
                padding-bottom: 54px;
            }

            .hero h1 {
                font-size: 42px;
            }

            .service-grid,
            .reason-grid,
            .job-tools,
            .footer-inner {
                grid-template-columns: 1fr;
            }

            .job-tools {
                width: 100%;
                display: grid;
            }

            .job-search,
            .job-select {
                width: 100%;
            }

            .culture-grid {
                grid-template-columns: 1fr;
                grid-auto-rows: 220px;
            }

            .culture-card.large,
            .culture-card.wide {
                grid-column: span 1;
                grid-row: span 1;
            }

            .dashboard-menu,
            .profile-menu {
                left: 0;
                right: auto;
                width: min(292px, calc(100vw - 32px));
            }
        }

        @media (max-width: 520px) {
            .header-actions .btn {
                padding-left: 14px;
                padding-right: 14px;
            }

            .dashboard-trigger span:last-child {
                display: none;
            }

            .hero-actions .btn,
            .contact-panel .btn {
                width: 100%;
            }

            .hero-visual {
                display: none;
            }

            .chatbot-bubble {
                right: 18px;
                bottom: 18px;
                width: 64px;
                height: 64px;
            }

            .chatbot-bubble img {
                width: 39px;
                height: 39px;
            }
        }
    </style>
</head>
<body>
    <header class="site-header">
        <div class="header-inner">
            <div class="brand-row">
                <a class="brand" href="${pageContext.request.contextPath}/homepage">
                    <img class="brand-logo" src="${pageContext.request.contextPath}/image/logo/Logo.png" alt="BetterHR">
                    <span>BetterHR</span>
                </a>
                <nav class="nav-links" aria-label="Điều hướng chính">
                    <a class="nav-link" href="#features">Về chúng tôi</a>
                    <a class="nav-link" href="#services">Dịch vụ</a>
                    <a class="nav-link" href="${pageContext.request.contextPath}/RecruitmentController">Việc làm</a>
                    <a class="nav-link" href="#contact">Liên hệ</a>
                </nav>
            </div>

            <div class="header-actions">
                <c:if test="${not empty sessionScope.systemUser}">
                    <div class="dashboard-wrap">
                        <button type="button" class="btn btn-outline dashboard-trigger" onclick="toggleDashboardDropdown()">
                            <span class="material-symbols-outlined">dashboard</span>
                            <span>Bảng điều khiển</span>
                        </button>
                        <div class="dashboard-menu" id="dashboardDropdown">
                            <c:if test="${dashboardAccess.canAccessAdmin}">
                                <a href="${pageContext.request.contextPath}${dashboardAccess.adminUrl}" class="menu-item admin">
                                    <span class="material-symbols-outlined">admin_panel_settings</span>
                                    <span>Quản trị<small>Quản trị hệ thống</small></span>
                                </a>
                            </c:if>
                            <c:if test="${dashboardAccess.canAccessHR}">
                                <a href="${pageContext.request.contextPath}${dashboardAccess.hrUrl}" class="menu-item hr">
                                    <span class="material-symbols-outlined">groups</span>
                                    <span>Quản lý HR<small>Quản lý nhân sự</small></span>
                                </a>
                            </c:if>
                            <c:if test="${dashboardAccess.canAccessHrStaff}">
                                <a href="${pageContext.request.contextPath}${dashboardAccess.hrStaffUrl}" class="menu-item hr">
                                    <span class="material-symbols-outlined">badge</span>
                                    <span>Nhân viên HR<small>Nghiệp vụ nhân sự</small></span>
                                </a>
                            </c:if>
                            <c:if test="${dashboardAccess.canAccessDept}">
                                <a href="${pageContext.request.contextPath}${dashboardAccess.deptUrl}" class="menu-item dept">
                                    <span class="material-symbols-outlined">supervisor_account</span>
                                    <span>Quản lý phòng ban<small>Không gian đội nhóm</small></span>
                                </a>
                            </c:if>
                            <c:if test="${dashboardAccess.canAccessEmployee}">
                                <a href="${pageContext.request.contextPath}${dashboardAccess.employeeUrl}" class="menu-item employee">
                                    <span class="material-symbols-outlined">person</span>
                                    <span>Nhân viên<small>Cổng nhân viên</small></span>
                                </a>
                            </c:if>
                            <c:if test="${dashboardAccess.canAccessGuest}">
                                <a href="${pageContext.request.contextPath}${dashboardAccess.guestUrl}" class="menu-item guest">
                                    <span class="material-symbols-outlined">home</span>
                                    <span>Khách<small>Trang công khai</small></span>
                                </a>
                            </c:if>
                        </div>
                    </div>
                </c:if>

                <c:if test="${empty sessionScope.systemUser}">
                    <a class="btn btn-outline" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/register">Đăng ký</a>
                </c:if>

                <c:if test="${not empty sessionScope.systemUser}">
                    <div class="profile-wrap">
                        <button type="button" class="avatar" onclick="toggleProfileDropdown()" title="Tài khoản">
                            <span class="material-symbols-outlined">person</span>
                        </button>
                        <div class="profile-menu" id="profileDropdown">
                            <a href="${pageContext.request.contextPath}/profilepage" class="menu-item">
                                <span class="material-symbols-outlined">account_circle</span>
                                <span>Hồ sơ<small>${currentUser.username}</small></span>
                            </a>
                            <a href="${pageContext.request.contextPath}/Views/ChangePassword.jsp" class="menu-item">
                                <span class="material-symbols-outlined">lock_reset</span>
                                <span>Đổi mật khẩu<small>Bảo mật tài khoản</small></span>
                            </a>
                            <a href="javascript:void(0)" onclick="handleLogout()" class="menu-item">
                                <span class="material-symbols-outlined">logout</span>
                                <span>Đăng xuất<small>Kết thúc phiên làm việc</small></span>
                            </a>
                        </div>
                    </div>
                </c:if>
            </div>
        </div>
    </header>

    <main>
        <section class="hero" id="home">
            <img class="hero-bg" alt="Không gian làm việc BetterHR" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCrjTJSlaNRyiOARfph393Ptjj_oBzG7H2PUtwslT80e_iYHoFpTA2Fz1Sx6CSkbQgdMCIWlLv7EGW6Ol5MiJnl4t_xxF4cO_zbatov4fT9P4O5SLzNisf9MswoW4C6KeUDbf2X8q1QCBoRmQYiGSBfYNVIZCJJ_v2Wpap5CYJMxsTOzsTFx2XWpWprcXpUEXXLRR6a-IYlhgMs2UMTI77SelH4gDjVw_-QSvDoMLSZgPsF6iaXe7YFMTqrAL17qcH9DDJJ6lAG_Vc">
            <span class="hero-bubble one" aria-hidden="true"></span>
            <span class="hero-bubble two" aria-hidden="true"></span>
            <span class="hero-bubble three" aria-hidden="true"></span>
            <div class="hero-inner">
                <div class="hero-copy">
                    <p class="eyebrow">Nền tảng HR số 1 Việt Nam</p>
                    <h1>
                        BetterHR
                        <span class="hero-title-line hero-title-main">Giải pháp Quản lý Nhân sự <strong>Toàn diện cho</strong></span>
                        <span class="hero-title-line">Doanh nghiệp</span>
                    </h1>
                    <p>Tự động hóa quy trình, tối ưu hiệu suất và kiến tạo trải nghiệm nhân viên tuyệt vời với nền tảng quản trị thông minh hàng đầu.</p>
                    <div class="hero-actions">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/RecruitmentController">Đăng ký ngay</a>
                        <a class="btn btn-outline" href="#features">Tìm hiểu thêm</a>
                    </div>
                </div>
                <div class="hero-visual" aria-hidden="true">
                    <img alt="Bảng điều khiển HR" src="https://lh3.googleusercontent.com/aida-public/AB6AXuASiVFllmfkuIid_f7xO4vEfmsiZmwTskjUC3URSVUV_Uw97V7W3c9iYRQegOKksrO1u0MeMSmQ-CnIOedJfoLqE9aj7uDf96HNTQ8L5o0WVY9M3Nf5S0xRO4I2GxyEkcSkq72zlZ4duC5kmThlbGOrWQxfZ0THvfWjXgk1kdQVUx26syGuUlBgTBOZr9CEeQWZbwwwNL5nlp_F6EC9gyaMQj1yy-XxgsjYV_J5_2dVsx69fk8QcZz8BDF1Jm_3EH2i6pwCtrOr1Mg">
                </div>
            </div>
        </section>

        <section class="section services-section" id="services">
            <div class="section-inner">
                <div class="section-heading">
                    <h2>Dịch vụ của chúng tôi</h2>
                    <div class="heading-rule"></div>
                </div>
                <div class="service-grid">
                    <article class="service-card">
                        <div class="icon-box"><span class="material-symbols-outlined">person_search</span></div>
                        <h3>Tuyển dụng</h3>
                        <p>Tìm kiếm và thu hút những nhân tài phù hợp nhất cho sự phát triển của doanh nghiệp.</p>
                        <a class="text-link" href="${pageContext.request.contextPath}/RecruitmentController">Tìm hiểu thêm <span class="material-symbols-outlined">arrow_forward</span></a>
                    </article>
                    <article class="service-card">
                        <div class="icon-box"><span class="material-symbols-outlined">groups</span></div>
                        <h3>Quản lý nhân sự</h3>
                        <p>Quản lý hồ sơ, phòng ban và dữ liệu nhân sự tập trung, rõ ràng và bảo mật.</p>
                        <a class="text-link" href="#features">Tìm hiểu thêm <span class="material-symbols-outlined">arrow_forward</span></a>
                    </article>
                    <article class="service-card">
                        <div class="icon-box"><span class="material-symbols-outlined">payments</span></div>
                        <h3>Tiền lương</h3>
                        <p>Tự động hóa quy trình tính lương, hợp đồng, phụ cấp và khấu trừ chính xác hơn.</p>
                        <a class="text-link" href="#features">Tìm hiểu thêm <span class="material-symbols-outlined">arrow_forward</span></a>
                    </article>
                    <article class="service-card">
                        <div class="icon-box"><span class="material-symbols-outlined">monitoring</span></div>
                        <h3>Đánh giá hiệu suất</h3>
                        <p>Theo dõi hiệu quả làm việc và xây dựng lộ trình phát triển minh bạch cho nhân viên.</p>
                        <a class="text-link" href="#features">Tìm hiểu thêm <span class="material-symbols-outlined">arrow_forward</span></a>
                    </article>
                </div>
            </div>
        </section>

        <section class="section why-section" id="features">
            <div class="section-inner why-grid">
                <div class="image-frame">
                    <img alt="Cộng tác trong doanh nghiệp" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAYq9CjcneChKeRCgByYk37akSWaHdty0R0OvtFrWTet2f2-Cd1iBPHvdlA4BGZnggICs6--QLJXPZBEA8q3HNIgQiWUK8b3FA1fO_nxUSATZm7lmut0S5wGC8EtYn_rVnWkPrclAf-m1WetaT9HqOedSqUaUcFdpk5Lkvt2jhm4fu7AApuNLugUQAPbyN6m_5taktcM8-Y0DqQviqdm0b4syzvM_yS996KxMBpbent2JCOWIfA7K1P5yMozBbaBA7hcM9pQlH-YcM">
                </div>
                <div>
                    <div class="section-heading left">
                        <h2>Tại sao chọn BetterHR?</h2>
                        <p>BetterHR mang đến trải nghiệm quản trị nhân sự hiện đại, tập trung vào con người và tăng trưởng bền vững.</p>
                    </div>
                    <div class="reason-grid">
                        <article class="reason-item">
                            <span class="reason-icon"><span class="material-symbols-outlined">check</span></span>
                            <div>
                                <h3>Tuyển dụng thông minh</h3>
                                <p>Lọc hồ sơ nhanh hơn và kết nối đúng ứng viên.</p>
                            </div>
                        </article>
                        <article class="reason-item">
                            <span class="reason-icon"><span class="material-symbols-outlined">check</span></span>
                            <div>
                                <h3>Bảo mật dữ liệu</h3>
                                <p>Thông tin nhân sự được quản lý chặt chẽ và có kiểm soát.</p>
                            </div>
                        </article>
                        <article class="reason-item">
                            <span class="reason-icon"><span class="material-symbols-outlined">check</span></span>
                            <div>
                                <h3>Lương tự động</h3>
                                <p>Giảm thao tác thủ công, tăng độ chính xác khi xử lý lương.</p>
                            </div>
                        </article>
                        <article class="reason-item">
                            <span class="reason-icon"><span class="material-symbols-outlined">check</span></span>
                            <div>
                                <h3>Dữ liệu tức thời</h3>
                                <p>Theo dõi nhân sự, hợp đồng và tuyển dụng ngay trên hệ thống.</p>
                            </div>
                        </article>
                    </div>
                </div>
            </div>
        </section>

        <section class="section jobs-section" id="jobs">
            <div class="section-inner">
                <div class="jobs-header">
                    <div class="section-heading left" style="margin-bottom: 0;">
                        <h2>Cơ hội nghề nghiệp mới nhất</h2>
                        <p>Khám phá các vị trí đang tuyển dụng tại BetterHR và các đối tác hàng đầu.</p>
                    </div>
                    <div class="job-tools">
                        <label class="search-wrap">
                            <span class="material-symbols-outlined">search</span>
                            <input class="job-search" type="text" placeholder="Tìm kiếm công việc..." aria-label="Tìm kiếm công việc">
                        </label>
                        <select class="job-select" aria-label="Lọc phòng ban">
                            <option>Tất cả phòng ban</option>
                            <option>Thiết kế</option>
                            <option>Kỹ thuật</option>
                            <option>Nhân sự</option>
                        </select>
                    </div>
                </div>

                <div class="job-grid">
                    <article class="job-card">
                        <div>
                            <div class="job-top">
                                <span class="job-tag">Toàn thời gian</span>
                                <span>Hạn: 30/12</span>
                            </div>
                            <h3>Senior Product Designer</h3>
                            <div class="job-meta">
                                <span><span class="material-symbols-outlined">domain</span>Thiết kế</span>
                                <span><span class="material-symbols-outlined">location_on</span>Quận 1, TP. HCM</span>
                            </div>
                        </div>
                        <div class="job-footer">
                            <span class="salary">2,000 - 3,500 USD</span>
                            <a class="btn btn-primary" href="${pageContext.request.contextPath}/RecruitmentController">Ứng tuyển</a>
                        </div>
                    </article>

                    <article class="job-card">
                        <div>
                            <div class="job-top">
                                <span class="job-tag gold">Từ xa</span>
                                <span>Hạn: 15/01</span>
                            </div>
                            <h3>Backend Developer</h3>
                            <div class="job-meta">
                                <span><span class="material-symbols-outlined">domain</span>Kỹ thuật</span>
                                <span><span class="material-symbols-outlined">location_on</span>Hà Nội linh hoạt</span>
                            </div>
                        </div>
                        <div class="job-footer">
                            <span class="salary">2,500 - 4,000 USD</span>
                            <a class="btn btn-primary" href="${pageContext.request.contextPath}/RecruitmentController">Ứng tuyển</a>
                        </div>
                    </article>

                    <article class="job-card">
                        <div>
                            <div class="job-top">
                                <span class="job-tag">Linh hoạt</span>
                                <span>Hạn: 20/12</span>
                            </div>
                            <h3>HR Business Partner</h3>
                            <div class="job-meta">
                                <span><span class="material-symbols-outlined">domain</span>Nhân sự</span>
                                <span><span class="material-symbols-outlined">location_on</span>TP. HCM</span>
                            </div>
                        </div>
                        <div class="job-footer">
                            <span class="salary">1,800 - 2,800 USD</span>
                            <a class="btn btn-primary" href="${pageContext.request.contextPath}/RecruitmentController">Ứng tuyển</a>
                        </div>
                    </article>
                </div>
            </div>
        </section>

        <section class="section process-section">
            <div class="section-inner">
                <div class="section-heading">
                    <h2>Quy trình tuyển dụng chuyên nghiệp</h2>
                </div>
                <div class="process-grid">
                    <article class="process-item">
                        <div class="step-number">01</div>
                        <h3>Ứng tuyển</h3>
                        <p>Đăng ký thông tin và gửi hồ sơ trực tuyến.</p>
                    </article>
                    <article class="process-item">
                        <div class="step-number">02</div>
                        <h3>Sàng lọc</h3>
                        <p>Đội ngũ nhân sự xem xét hồ sơ của bạn.</p>
                    </article>
                    <article class="process-item">
                        <div class="step-number">03</div>
                        <h3>Phỏng vấn</h3>
                        <p>Trao đổi về kỹ năng, kinh nghiệm và văn hóa.</p>
                    </article>
                    <article class="process-item">
                        <div class="step-number">04</div>
                        <h3>Đề nghị</h3>
                        <p>Nhận thư mời làm việc chính thức.</p>
                    </article>
                    <article class="process-item">
                        <div class="step-number">05</div>
                        <h3>Hội nhập</h3>
                        <p>Chào mừng bạn gia nhập BetterHR.</p>
                    </article>
                </div>
            </div>
        </section>

        <section class="section services-section">
            <div class="section-inner">
                <div class="section-heading left">
                    <h2>Văn hóa tại BetterHR</h2>
                    <p>Kiến tạo môi trường làm việc hạnh phúc, nơi mỗi cá nhân đều có cơ hội tỏa sáng.</p>
                </div>
                <div class="culture-grid">
                    <article class="culture-card large">
                        <img alt="Sự kiện đội nhóm BetterHR" src="https://lh3.googleusercontent.com/aida-public/AB6AXuBGDyGiuaRhRNfEloc-flWS9DiJXdSLrD4bJs8DdyVdvEjIn76ql0BLoriaC6sSLPBRdC-mqu6041tVNBm3maFwJy5HXEmrOF_eTZSDVhz6Xyuu-ps3MGdQINe2XMyAIgJc0DAzgXXOw5Fat8JLY_mjnOTaXI7j-uNBoeJQ8hTacSqRpdH51oI-6pR0T4UM5K2N85iTtXTk4R4ERQxBZHRCxdiQb1DTukojgk1wNFa1RQ-oMu2SR0cDiYTGKn-i79BNhKh3iw0Kx28">
                    </article>
                    <article class="culture-card wide">
                        <img alt="Workshop BetterHR" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAvuyBa875aKmhIjgoAomv_F2zbedqNBhpGW5xWUZznkYc1lCevunGKITSPQce-s06P4IQWiJCGpVbgUoEWvhfU24i6dz4yTVuQtkv00fXAsIBsd8UhOcSpkzyS5U-XL7FgdQ2jgWAVgUcOhod8ail1WpBI341xZbc80htuGUuncTTSxSitLnneSHHx5tKSozZ-FulPqzi_ataoS9iRp2T2THRJIIx1bL-PR3wxbsXwHDgz2FCMsL00R6l-s_kR5WV5wb2j-7t2bcc">
                    </article>
                    <article class="culture-card">
                        <img alt="Không gian văn phòng BetterHR" src="https://lh3.googleusercontent.com/aida-public/AB6AXuAp_VdNlfpgA_TBEVD9rwRiLlqiOFr9IZm0JtDXAKXorczrJduOiDjMnNT-sJK_c2bvAIR0oASezCgOXX-C2B6wJ1kaGgbTrNXMKosGjW5vfOyEIVjHg450VwmDNM7psf8L5bYGzprC0vHZxhXne-hRPiVaA9e-r_B-fC3xQAfoqFHVlsoXudna4kLgiO_4ut4Pq_x8LWStAEg9JfHbyEkyIzCFvrhY0B1N6MqY8XDrCm4Q6D8tEC3WpZ8rYumf8buOkmxvpRDekyQ">
                    </article>
                    <article class="culture-card">
                        <img alt="Hoạt động cộng đồng BetterHR" src="https://lh3.googleusercontent.com/aida-public/AB6AXuCG1FE16s1m75iE3BDH2zPiFlVvmQyqyY2N9bUMkoYS3n6uuAjCMd0_UxuLgo2yH8bnyJHFXH-9732PEYrA4HxYMxiwr8B1e4GFcgSF0IIMvFU1vqJl_SQp04TefX6JDhoqYkmgkwW3O6DhzP16aIFclVig5Jr3QcJ1iWB2dPT7kUkH2GbtT18YWvocfFndPDJp13l8MOG72-Pnbg4Kkvi8ui0cvKDrIw_XWL_v9cc7UMbx3cTNrf0K4s_jJT5crUDkW0FXMskH0j4">
                    </article>
                </div>
            </div>
        </section>

        <section class="section faq-section">
            <div class="section-inner">
                <div class="section-heading">
                    <h2>Câu hỏi thường gặp</h2>
                </div>
                <div class="faq-list">
                    <article class="faq-card">
                        <button type="button" onclick="this.parentElement.classList.toggle('accordion-open')">
                            <span>Quy trình ứng tuyển mất bao lâu?</span>
                            <span class="material-symbols-outlined accordion-icon">expand_more</span>
                        </button>
                        <div class="accordion-content">
                            <div class="accordion-body">Thông thường quy trình từ khi nộp hồ sơ đến khi nhận kết quả mất khoảng 2-3 tuần tùy vị trí và lịch phỏng vấn.</div>
                        </div>
                    </article>
                    <article class="faq-card">
                        <button type="button" onclick="this.parentElement.classList.toggle('accordion-open')">
                            <span>BetterHR có hỗ trợ làm việc linh hoạt không?</span>
                            <span class="material-symbols-outlined accordion-icon">expand_more</span>
                        </button>
                        <div class="accordion-content">
                            <div class="accordion-body">Có. Một số vị trí hỗ trợ mô hình làm việc linh hoạt tùy theo phòng ban, tính chất công việc và chính sách hiện hành.</div>
                        </div>
                    </article>
                </div>
            </div>
        </section>

        <section class="section contact-section" id="contact">
            <div class="section-inner contact-grid">
                <div>
                    <div class="section-heading left">
                        <h2>Kết nối với chúng tôi</h2>
                        <p>BetterHR luôn sẵn sàng đồng hành cùng ứng viên và doanh nghiệp trong hành trình phát triển nhân sự.</p>
                    </div>
                    <div class="contact-list">
                        <div class="contact-item"><span class="material-symbols-outlined">mail</span>contact@betterhr.com</div>
                        <div class="contact-item"><span class="material-symbols-outlined">call</span>1900 2026</div>
                        <div class="contact-item"><span class="material-symbols-outlined">location_on</span>TP. Hồ Chí Minh, Việt Nam</div>
                    </div>
                </div>
                <div class="contact-panel">
                    <h3>Sẵn sàng cho bước tiếp theo?</h3>
                    <p>Tìm kiếm cơ hội phù hợp, gửi hồ sơ và theo dõi quá trình ứng tuyển ngay trên BetterHR.</p>
                    <a class="btn btn-primary" href="${pageContext.request.contextPath}/RecruitmentController">Khám phá việc làm</a>
                </div>
            </div>
        </section>
    </main>

    <footer class="site-footer">
        <div class="footer-inner">
            <div class="footer-brand">
                <h2>BetterHR</h2>
                <p>Nền tảng quản trị nhân sự và tuyển dụng giúp doanh nghiệp vận hành rõ ràng, hiệu quả hơn.</p>
            </div>
            <div class="footer-col">
                <h3>Công ty</h3>
                <a href="#features">Về chúng tôi</a>
                <a href="${pageContext.request.contextPath}/RecruitmentController">Tuyển dụng</a>
                <a href="#services">Dịch vụ</a>
            </div>
            <div class="footer-col">
                <h3>Sản phẩm</h3>
                <a href="#services">Hệ thống quản lý</a>
                <a href="#services">Tính lương tự động</a>
                <a href="#services">Đánh giá hiệu suất</a>
            </div>
            <div class="footer-col">
                <h3>Hỗ trợ</h3>
                <a href="#">Điều khoản sử dụng</a>
                <a href="#">Chính sách bảo mật</a>
                <a href="#contact">Liên hệ</a>
            </div>
        </div>
        <div class="footer-bottom">
            <span>© 2026 BetterHR. Bảo lưu mọi quyền.</span>
            <span>Facebook · LinkedIn · YouTube</span>
        </div>
    </footer>

    <button class="chatbot-bubble" type="button" aria-label="Mở chatbot BetterHR">
        <img src="${pageContext.request.contextPath}/image/logo/Logo.png" alt="">
        <span class="chatbot-badge" aria-hidden="true">
            <span class="material-symbols-outlined">smart_toy</span>
        </span>
    </button>

    <script>
        function toggleDashboardDropdown() {
            const dropdown = document.getElementById('dashboardDropdown');
            if (dropdown) {
                dropdown.classList.toggle('show');
            }
        }

        function toggleProfileDropdown() {
            const dropdown = document.getElementById('profileDropdown');
            if (dropdown) {
                dropdown.classList.toggle('show');
            }
        }

        document.addEventListener('click', function(event) {
            const profileDropdown = document.getElementById('profileDropdown');
            const profileButton = document.querySelector('.avatar');
            const dashboardDropdown = document.getElementById('dashboardDropdown');
            const dashboardButton = document.querySelector('.dashboard-trigger');

            if (profileDropdown && profileButton && !profileButton.contains(event.target) && !profileDropdown.contains(event.target)) {
                profileDropdown.classList.remove('show');
            }

            if (dashboardDropdown && dashboardButton && !dashboardButton.contains(event.target) && !dashboardDropdown.contains(event.target)) {
                dashboardDropdown.classList.remove('show');
            }
        });

        function handleLogout() {
            if (confirm('Bạn có chắc chắn muốn đăng xuất?')) {
                window.location.href = '${pageContext.request.contextPath}/logout';
            }
        }

        const header = document.querySelector('.site-header');
        window.addEventListener('scroll', function() {
            if (window.scrollY > 50) {
                header.style.boxShadow = '0 10px 28px rgba(30,57,50,0.14)';
            } else {
                header.style.boxShadow = '0 1px 1px rgba(0,0,0,0.08)';
            }
        });
    </script>
</body>
</html>
