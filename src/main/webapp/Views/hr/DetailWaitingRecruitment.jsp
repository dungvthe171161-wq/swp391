<%-- 
    Document   : DetailRecruitment
    Created on : Oct 30, 2025, 11:54:21 AM
    Author     : DELL
--%>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>HRM - Waiting Recruitment Detail</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                font-family: "Inter", sans-serif;
                background-color: #f3f4f6;
                color: #1e293b;
                min-height: 100vh;
                margin: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 40px 16px;
            }

            .content-wrapper {
                width: 100%;
                max-width: 780px;
            }

            .card {
                border: none;
                border-radius: 20px;
                box-shadow: 0 20px 45px rgba(15, 23, 42, 0.12);
            }

            .card-header {
                border: none;
                background: linear-gradient(135deg, #2563eb, #1e40af);
                color: #ffffff;
                border-top-left-radius: 20px;
                border-top-right-radius: 20px;
                padding: 24px;
            }

            .card-header h1 {
                margin: 0;
                font-size: 1.75rem;
                font-weight: 600;
            }

            .card-body {
                padding: 32px;
                background-color: #ffffff;
                border-bottom-left-radius: 20px;
                border-bottom-right-radius: 20px;
            }

            .form-label {
                font-weight: 600;
                color: #0f172a;
                margin-bottom: 6px;
            }

            .form-control,
            .input-group-text {
                border-radius: 12px;
            }

            .form-control {
                border: 1px solid #cbd5f5;
                padding: 12px 14px;
                transition: border-color 0.2s ease, box-shadow 0.2s ease;
            }

            .form-control:focus {
                border-color: #2563eb;
                box-shadow: 0 0 0 0.2rem rgba(37, 99, 235, 0.15);
            }

            textarea.form-control {
                resize: vertical;
                min-height: 140px;
            }

            .back-link {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                color: #2563eb;
                text-decoration: none;
                font-weight: 600;
                transition: color 0.2s ease;
                margin-bottom: 24px;
            }

            .back-link:hover {
                color: #1e40af;
            }

            .alert-message {
                margin-top: 20px;
                font-weight: 600;
                color: #dc2626;
            }
        </style>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css">
    </head>
    <body>
        <div class="content-wrapper">
            <a href="${pageContext.request.contextPath}/viewRecruitment" class="back-link">
                <i class="fas fa-arrow-left"></i>
                Back to list
            </a>
            <div class="card">
                <div class="card-header d-flex align-items-center justify-content-between">
                    <h1>Waiting Recruitment Detail</h1>
                    <span class="badge bg-light text-dark px-3 py-2">ID: ${rec.recruitmentId}</span>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/detailWaitingRecruitment" method="post" class="row g-3">
                        <input type="hidden" name="id" value="${rec.recruitmentId}">

                        <div class="col-12">
                            <label for="titleInput" class="form-label">Title</label>
                            <input type="text" class="form-control" id="titleInput" name="Title" value="${rec.title}" placeholder="Enter recruitment title" required>
                        </div>

                        <div class="col-12">
                            <label for="descriptionInput" class="form-label">Description</label>
                            <textarea class="form-control" id="descriptionInput" name="Description" placeholder="Enter detailed description" rows="5" required>${rec.description}</textarea>
                        </div>

                        <div class="col-md-6">
                            <label for="requirementInput" class="form-label">Requirement</label>
                            <input type="text" class="form-control" id="requirementInput" name="Requirement" value="${rec.requirement}" placeholder="Enter key requirements" required>
                        </div>

                        <div class="col-md-6">
                            <label for="locationInput" class="form-label">Location</label>
                            <input type="text" class="form-control" id="locationInput" name="Location" value="${rec.location}" placeholder="Enter job location" required>
                        </div>

                        <div class="col-md-6">
                            <label for="applicantInput" class="form-label">Applicant</label>
                            <input type="number" class="form-control" id="applicantInput" name="Applicant" value="${rec.applicant}" placeholder="Enter applicant number" step="1" min="1" required>
                        </div>

                        <div class="col-md-6">
                            <label for="salaryInput" class="form-label">Salary</label>
                            <div class="input-group">
                                <input type="number" class="form-control" id="salaryInput" name="Salary" value="${rec.salary}" placeholder="Enter salary" min="0" required>
                                <span class="input-group-text">đ</span>
                            </div>
                        </div>

                        <c:if test="${not empty mess}">
                            <div class="col-12 alert-message">
                                ${mess}
                            </div>
                        </c:if>
                    </form>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
