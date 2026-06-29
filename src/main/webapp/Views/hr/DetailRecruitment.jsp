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
        <title>HRM - Recruitment Detail</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
        <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
        <style>
            body {
                font-family: "Inter", sans-serif;
                background-color: #f1f5f9;
                color: #0f172a;
                min-height: 100vh;
                margin: 0;
                display: flex;
                align-items: center;
                justify-content: center;
                padding: 40px 16px;
            }

            .content-wrapper {
                width: 100%;
                max-width: 820px;
            }

            .card {
                border: none;
                border-radius: 22px;
                box-shadow: 0 18px 50px rgba(15, 23, 42, 0.15);
            }

            .card-header {
                border: none;
                background: linear-gradient(135deg, #3b82f6, #1d4ed8);
                color: #ffffff;
                border-top-left-radius: 22px;
                border-top-right-radius: 22px;
                padding: 28px;
            }

            .card-header h1 {
                margin: 0;
                font-size: 1.9rem;
                font-weight: 600;
            }

            .card-body {
                padding: 36px;
                background-color: #ffffff;
                border-bottom-left-radius: 22px;
                border-bottom-right-radius: 22px;
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
                border-color: #1d4ed8;
                box-shadow: 0 0 0 0.2rem rgba(59, 130, 246, 0.18);
            }

            textarea.form-control {
                resize: vertical;
                min-height: 150px;
            }

            .back-link {
                display: inline-flex;
                align-items: center;
                gap: 8px;
                color: #1d4ed8;
                text-decoration: none;
                font-weight: 600;
                transition: color 0.2s ease;
                margin-bottom: 26px;
            }

            .back-link:hover {
                color: #1e3a8a;
            }

            .alert-message {
                margin-top: 8px;
                font-weight: 600;
                color: #dc2626;
            }

            .success-message {
                color: #0f766e;
            }
        </style>
        <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css">
    </head>
    <body>
        <div class="content-wrapper">
            <a href="${pageContext.request.contextPath}/postRecruitments" class="back-link">
                <i class="fas fa-arrow-left"></i>
                Back to list
            </a>
            <div class="card">
                <div class="card-header d-flex align-items-center justify-content-between">
                    <h1>Recruitment Detail</h1>
                    <span class="badge bg-light text-dark px-3 py-2">Status: ${rec.status}</span>
                </div>
                <div class="card-body">
                    <form action="${pageContext.request.contextPath}/detailRecruitment" method="post" class="row g-3">
                        <input type="hidden" name="id" value="${rec.recruitmentId}">
                        <input type="hidden" name="status" value="${rec.status}">

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
                                <input type="number" class="form-control" id="salaryInput" name="Salary" value="${rec.salary}" placeholder="Enter salary" min="0" step = "10000" required>
                                <span class="input-group-text">đ</span>
                            </div>
                        </div>

                        <c:if test="${not empty mess}">
                            <div class="col-12 alert-message ${mess eq 'Save recruitment successfully!' ? 'success-message' : ''}">
                                ${mess}
                            </div>
                        </c:if>

                        <div class="col-12 d-flex justify-content-end">
                            <c:if test="${rec.status ne 'Deleted'}">
                                <button type="submit" class="btn btn-primary px-4">
                                    <i class="fas fa-save me-2"></i>Save Changes
                                </button>
                            </c:if>
                        </div>
                    </form>
                </div>
            </div>
        </div>
        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    </body>
</html>
