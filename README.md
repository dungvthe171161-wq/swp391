# HRMS - Human Resource Management System

A Java web-based application for managing human resources such as employees, departments, attendance, payroll, and recruitment.

## Features
- Employee Management
- Department Management
- Attendance Tracking
- Payroll Management
- Recruitment
- Admin account creation for new employees
- Role-based authentication

## Tech Stack
- Backend: Java 17, Jakarta Servlet API 6.0
- Frontend: JSP, JSTL
- Database: MySQL 8.0
- Build Tool: Maven
- Server: Tomcat 10+
- Logging: SLF4J

## Requirements
- JDK 17 or higher
- Apache Maven 3.6+
- Apache Tomcat 10+
- MySQL 8.0+

## Setup Instructions
1. Clone the repository
- git clone https://github.com/yourusername/HRMS.git
- cd HRMS
2. Create a database named `hrm_db` in MySQL.
3. Create file `src/main/resources/db.properties` with your own credentials:
db.url=jdbc:mysql://localhost:3306/hrm_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=yourpassword
4. Build and deploy:
- mvn clean package
- Copy `target/HRMS.war` to `Tomcat/webapps/`.
5. Run Tomcat and open:
- http://localhost:8080/HRMS

## Folder Structure
src/
├── main/
│ ├── java/
│ │ ├── dao/
│ │ ├── model/
│ │ ├── controller/
│ │ └── service/
│ ├── resources/
│ │ └── db.properties (ignored by git)
│ └── webapp/
│ ├── WEB-INF/
│ └── views/
├── test/
pom.xml
README.md
.gitignore

## Contributors
- Member 1: 
- Member 2: 
- Member 3: Namhdhe187299
- Member 4: 
- Member 5: 

## License
MIT License