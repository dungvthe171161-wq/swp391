<%-- 
    Document   : ViewTask
    Created on : Nov 14, 2025, 6:55:14 AM
    Author     : DELL
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>View Task Details</title>
        <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-9ndCyUaIbzAi2FUVXJi0CjmCapSmO7SnpJef0486qhLnuZ2cdeRhO02iuK6FUUVM" crossorigin="anonymous">
        <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css" integrity="sha512-iecdLmaskl7CVkqkXNQ/ZH/XLlvWZOJyj7Yy7tcenmpD1ypASozpmT/E0iPtmFIB46ZmdtAc9eNBvH0H/ZpiBw==" crossorigin="anonymous" referrerpolicy="no-referrer" />
        <style>
            .task-detail-card {
                box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                border-radius: 8px;
            }
            .info-label {
                font-weight: 600;
                color: #495057;
            }
            .info-value {
                color: #212529;
            }
            .status-badge {
                font-size: 0.9rem;
                padding: 0.5rem 1rem;
            }
            .employee-list {
                max-height: 300px;
                overflow-y: auto;
            }
            .employee-item {
                padding: 0.75rem;
                border-bottom: 1px solid #dee2e6;
            }
            .employee-item:last-child {
                border-bottom: none;
            }
            .employee-item:hover {
                background-color: #f8f9fa;
            }
        </style>
    </head>
    <body>
        <div class="container py-4">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h1 class="h3">
                    <i class="fas fa-tasks me-2"></i>Task Details
                </h1>
                <a href="${pageContext.request.contextPath}/taskManager" class="btn btn-secondary">
                    <i class="fas fa-arrow-left me-1"></i> Back to Tasks
                </a>
            </div>

            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show" role="alert">
                    <i class="fas fa-exclamation-circle me-2"></i>
                    ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                </div>
            </c:if>

            <c:if test="${not empty task}">
                <div class="card task-detail-card mb-4">
                    <div class="card-header bg-primary text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-info-circle me-2"></i>Task Information
                        </h5>
                    </div>
                    <div class="card-body">
                        <table class="table table-bordered">
                            <tbody>
                                <tr>
                                    <td class="info-label" style="width: 30%;">
                                        <i class="fas fa-hashtag me-2"></i>Task ID
                                    </td>
                                    <td class="info-value">${task.taskId}</td>
                                </tr>
                                <tr>
                                    <td class="info-label">
                                        <i class="fas fa-heading me-2"></i>Title
                                    </td>
                                    <td class="info-value">${task.title}</td>
                                </tr>
                                <tr>
                                    <td class="info-label">
                                        <i class="fas fa-align-left me-2"></i>Description
                                    </td>
                                    <td class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty task.description}">
                                                ${fn:replace(fn:replace(task.description, '
', '<br>'), '
', '<br>')}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">No description</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">
                                        <i class="fas fa-user-tie me-2"></i>Assigned By
                                    </td>
                                    <td class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty assignedByEmployee}">
                                                <i class="fas fa-user me-2"></i>${assignedByEmployee.fullName}
                                                <c:if test="${not empty assignedByEmployee.position}">
                                                    <span class="text-muted">(${assignedByEmployee.position})</span>
                                                </c:if>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Unknown</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">
                                        <i class="fas fa-calendar-alt me-2"></i>Start Date
                                    </td>
                                    <td class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty task.startDate}">
                                                ${task.startDate}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not set</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">
                                        <i class="fas fa-calendar-check me-2"></i>Due Date
                                    </td>
                                    <td class="info-value">
                                        <c:choose>
                                            <c:when test="${not empty task.dueDate}">
                                                ${task.dueDate}
                                            </c:when>
                                            <c:otherwise>
                                                <span class="text-muted">Not set</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                                <tr>
                                    <td class="info-label">
                                        <i class="fas fa-info-circle me-2"></i>Status
                                    </td>
                                    <td class="info-value">
                                        <c:choose>
                                            <c:when test="${task.status eq 'Completed'}">
                                                <span class="badge bg-success status-badge">
                                                    <i class="fas fa-check-circle me-1"></i>${task.status}
                                                </span>
                                            </c:when>
                                            <c:when test="${task.status eq 'In Progress'}">
                                                <span class="badge bg-warning text-dark status-badge">
                                                    <i class="fas fa-spinner me-1"></i>${task.status}
                                                </span>
                                            </c:when>
                                            <c:when test="${task.status eq 'Waiting'}">
                                                <span class="badge bg-info status-badge">
                                                    <i class="fas fa-clock me-1"></i>${task.status}
                                                </span>
                                            </c:when>
                                            <c:when test="${task.status eq 'Rejected'}">
                                                <span class="badge bg-danger status-badge">
                                                    <i class="fas fa-times-circle me-1"></i>${task.status}
                                                </span>
                                            </c:when>
                                            <c:otherwise>
                                                <span class="badge bg-secondary status-badge">${task.status}</span>
                                            </c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="card task-detail-card">
                    <div class="card-header bg-info text-white">
                        <h5 class="mb-0">
                            <i class="fas fa-users me-2"></i>Assigned Employees
                        </h5>
                    </div>
                    <div class="card-body">
                        <c:choose>
                            <c:when test="${not empty assignedEmployees}">
                                <div class="employee-list">
                                    <table class="table table-hover">
                                        <thead>
                                            <tr>
                                                <th style="width: 5%;">#</th>
                                                <th style="width: 30%;">Full Name</th>
                                                <th style="width: 25%;">Position</th>
                                                <th style="width: 20%;">Email</th>
                                                <th style="width: 20%;">Phone</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="emp" items="${assignedEmployees}" varStatus="loop">
                                                <tr class="employee-item">
                                                    <td>${loop.index + 1}</td>
                                                    <td>
                                                        <i class="fas fa-user me-2"></i>${emp.fullName}
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty emp.position}">
                                                                ${emp.position}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">-</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty emp.email}">
                                                                <i class="fas fa-envelope me-1"></i>${emp.email}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">-</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                    <td>
                                                        <c:choose>
                                                            <c:when test="${not empty emp.phone}">
                                                                <i class="fas fa-phone me-1"></i>${emp.phone}
                                                            </c:when>
                                                            <c:otherwise>
                                                                <span class="text-muted">-</span>
                                                            </c:otherwise>
                                                        </c:choose>
                                                    </td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <div class="alert alert-info mb-0">
                                    <i class="fas fa-info-circle me-2"></i>No employees assigned to this task yet.
                                </div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </c:if>

            <c:if test="${empty task}">
                <div class="alert alert-warning">
                    <i class="fas fa-exclamation-triangle me-2"></i>No task information available.
                </div>
            </c:if>
        </div>

        <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js" integrity="sha384-geWF76RCwLtnZ8qwWowPQNguL3RmwHVBC9FhGdlKrxdiJJigb/j/68SIy3Te4Bkz" crossorigin="anonymous"></script>
    </body>
</html>
