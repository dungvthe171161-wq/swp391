/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package com.hrm.controller;

import com.hrm.dao.DBConnection;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author admin
 */
@WebServlet(name="DBConnectionTest", urlPatterns={"/DBConnectionTest"})
public class DBConnectionTest extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter()) {
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Database Connection Test</title>");
            out.println("<style>");
            out.println("body { font-family: Arial, sans-serif; margin: 50px; }");
            out.println(".success { background: #d4edda; color: #155724; padding: 15px; border-radius: 5px; margin: 10px 0; }");
            out.println(".error { background: #f8d7da; color: #721c24; padding: 15px; border-radius: 5px; margin: 10px 0; }");
            out.println(".info { background: #d1ecf1; color: #0c5460; padding: 15px; border-radius: 5px; margin: 10px 0; }");
            out.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
            out.println("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<h1>Database Connection Test</h1>");
            
            try {
                // Test database connection
                Connection conn = DBConnection.getConnection();
                
                if (conn != null) {
                    out.println("<div class='success'>");
                    out.println("<h3>✅ Database Connection Successful!</h3>");
                    out.println("<p>Connected to: " + conn.getMetaData().getDatabaseProductName() + "</p>");
                    out.println("<p>URL: " + conn.getMetaData().getURL() + "</p>");
                    out.println("</div>");
                    
                    // Test query
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as total FROM Employee");
if (rs.next()) {
                        int employeeCount = rs.getInt("total");
                        out.println("<div class='info'>");
                        out.println("<h3>📊 Database Data Check</h3>");
                        out.println("<p>Total Employees in database: <strong>" + employeeCount + "</strong></p>");
                        out.println("</div>");
                    }
                    
                    // Show sample employee data
                    rs = stmt.executeQuery("SELECT EmployeeID, FullName, Status, Position FROM Employee LIMIT 5");
                    out.println("<h3>Sample Employee Data:</h3>");
                    out.println("<table>");
                    out.println("<tr><th>ID</th><th>Name</th><th>Status</th><th>Position</th></tr>");
                    
                    while (rs.next()) {
                        out.println("<tr>");
                        out.println("<td>" + rs.getInt("EmployeeID") + "</td>");
                        out.println("<td>" + rs.getString("FullName") + "</td>");
                        out.println("<td>" + rs.getString("Status") + "</td>");
                        out.println("<td>" + rs.getString("Position") + "</td>");
                        out.println("</tr>");
                    }
                    out.println("</table>");
                    
                    rs.close();
                    stmt.close();
                    conn.close();
                    
                    out.println("<div class='success'>");
                    out.println("<h3>🎉 All Tests Passed!</h3>");
                    out.println("<p>The database connection is working properly and contains employee data.</p>");
                    out.println("<p>You can now access the HR management pages:</p>");
                    out.println("<ul>");
                    out.println("<li><a href='/HRMS/HrHomeController'>Profile Management</a></li>");
                    out.println("<li><a href='/HRMS/EmploymentStatusController'>Employment Status</a></li>");
                    out.println("<li><a href='/HRMS/TaskManagementController'>Task Management</a></li>");
                    out.println("</ul>");
                    out.println("</div>");
                    
                } else {
                    out.println("<div class='error'>");
                    out.println("<h3>❌ Database Connection Failed!</h3>");
                    out.println("<p>Unable to connect to the database. Please check:</p>");
                    out.println("<ul>");
                    out.println("<li>Database server is running</li>");
                    out.println("<li>Database credentials in DBConnection.java are correct</li>");
                    out.println("<li>Database 'hrm_db' exists</li>");
                    out.println("<li>MySQL JDBC driver is in classpath</li>");
                    out.println("</ul>");
                    out.println("</div>");
                }
} catch (Exception e) {
                out.println("<div class='error'>");
                out.println("<h3>❌ Database Error!</h3>");
                out.println("<p>Error: " + e.getMessage() + "</p>");
                out.println("<p>Stack trace:</p>");
                out.println("<pre>" + e.toString() + "</pre>");
                out.println("</div>");
            }
            
            out.println("</body>");
            out.println("</html>");
        }
    }
}
