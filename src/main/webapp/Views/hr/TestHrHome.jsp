<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Test HR Dashboard</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 20px;
            background-color: #f5f5f5;
        }
        .test-container {
            max-width: 800px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        .test-header {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }
        .test-section {
            margin: 20px 0;
            padding: 15px;
            border: 1px solid #ddd;
            border-radius: 5px;
        }
        .success {
            background: #d4edda;
            border-color: #c3e6cb;
            color: #155724;
        }
        .error {
            background: #f8d7da;
            border-color: #f5c6cb;
            color: #721c24;
        }
        .info {
            background: #d1ecf1;
            border-color: #bee5eb;
            color: #0c5460;
        }
        .employee-list {
            max-height: 300px;
            overflow-y: auto;
            border: 1px solid #ddd;
            padding: 10px;
            margin: 10px 0;
        }
        .employee-item {
            padding: 10px;
            border-bottom: 1px solid #eee;
            display: flex;
            justify-content: space-between;
        }
        .employee-item:last-child {
            border-bottom: none;
        }
    </style>
    <link href="https://fonts.googleapis.com/css2?family=Hanken+Grotesk:wght@400;500;600;700;800&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/hr-theme.css">
</head>
<body>
    <div class="test-container">
        <div class="test-header">
            <h1>🧪 Test HR Dashboard</h1>
            <p>Testing HrHomeController → HrHome.jsp</p>
        </div>

        <div class="test-section info">
            <h3>📋 Test Information</h3>
            <p><strong>URL:</strong> <span id="current-url"></span></p>
            <p><strong>Timestamp:</strong> <span id="timestamp"></span></p>
            <p><strong>JSP File:</strong> TestHrHome.jsp</p>
        </div>

        <div class="test-section">
            <h3>📊 Data Test</h3>
            <c:choose>
                <c:when test="${not empty employees}">
                    <div class="success">
                        <h4>✅ Employees Data Loaded Successfully!</h4>
                        <p><strong>Total Employees:</strong> ${employees.size()}</p>
                    </div>
                    
                    <h4>👥 Employee List (First 10):</h4>
                    <div class="employee-list">
                        <c:forEach var="employee" items="${employees}" begin="0" end="9">
                            <div class="employee-item">
                                <div>
                                    <strong>${employee.fullName}</strong><br>
                                    <small>${employee.position} - ${employee.departmentName}</small>
                                </div>
                                <div>
                                    <span style="color: ${employee.status == 'Active' ? 'green' : 'orange'}">
                                        ${employee.status}
                                    </span>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="error">
                        <h4>❌ No Employees Data Found!</h4>
                        <p>HrHomeController did not load employee data.</p>
                        <p><strong>Possible causes:</strong></p>
                        <ul>
                            <li>Database connection issue</li>
                            <li>EmployeeDAO.getAll() failed</li>
                            <li>No data in Employee table</li>
                            <li>Controller not setting employees attribute</li>
                        </ul>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="test-section">
            <h3>🏢 Departments Test</h3>
            <c:choose>
                <c:when test="${not empty departments}">
                    <div class="success">
                        <h4>✅ Departments Data Loaded Successfully!</h4>
                        <p><strong>Total Departments:</strong> ${departments.size()}</p>
                    </div>
                    
                    <h4>🏢 Department List:</h4>
                    <div class="employee-list">
                        <c:forEach var="department" items="${departments}">
                            <div class="employee-item">
                                <div>
                                    <strong>${department.departmentName}</strong><br>
                                    <small>ID: ${department.departmentId}</small>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="error">
                        <h4>❌ No Departments Data Found!</h4>
                        <p>HrHomeController did not load department data.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="test-section">
            <h3>🔧 Controller Test</h3>
            <div class="info">
                <h4>📝 Controller Information</h4>
                <p><strong>Controller:</strong> HrHomeController</p>
                <p><strong>Method:</strong> processRequest()</p>
                <p><strong>Forward Target:</strong> /Views/hr/HrHome.jsp</p>
                <p><strong>Expected Attributes:</strong></p>
                <ul>
                    <li>employees (List&lt;Employee&gt;)</li>
                    <li>departments (List&lt;Department&gt;)</li>
                </ul>
            </div>
        </div>

        <div class="test-section">
            <h3>🔗 Navigation Test</h3>
            <p>Test links to verify routing:</p>
            <a href="/HRMS/HrHomeController" style="background: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; margin: 5px;">Test HrHomeController</a>
            <a href="/HRMS/DBConnectionTest" style="background: #28a745; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; margin: 5px;">Test Database Connection</a>
            <a href="/HRMS/homepage" style="background: #ffc107; color: black; padding: 10px 20px; text-decoration: none; border-radius: 4px; margin: 5px;">Test Homepage</a>
        </div>

        <div class="test-section">
            <h3>🐛 Debug Information</h3>
            <div class="info">
                <h4>🔍 Debug Details</h4>
                <p><strong>Request Attributes:</strong></p>
                <ul>
                    <li>employees: ${not empty employees ? 'LOADED' : 'MISSING'}</li>
                    <li>departments: ${not empty departments ? 'LOADED' : 'MISSING'}</li>
                    <li>error: ${not empty error ? error : 'NONE'}</li>
                </ul>
                
                <p><strong>JSP Context:</strong></p>
                <ul>
                    <li>Page Context: ${pageContext.request.contextPath}</li>
                    <li>Request URI: ${pageContext.request.requestURI}</li>
                    <li>Servlet Path: ${pageContext.request.servletPath}</li>
                </ul>
            </div>
        </div>
    </div>

    <script>
        // Set current URL and timestamp
        document.getElementById('current-url').textContent = window.location.href;
        document.getElementById('timestamp').textContent = new Date().toLocaleString();
        
        // Auto refresh every 30 seconds for testing
        setTimeout(() => {
            if (confirm('Refresh page to test again?')) {
                window.location.reload();
            }
        }, 30000);
    </script>
</body>
</html>



