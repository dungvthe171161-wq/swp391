package com.hrm.dao;

import com.hrm.model.entity.Department;
import com.hrm.model.entity.Employee;
import com.hrm.model.entity.Role;
import com.hrm.model.entity.SystemUser;
import com.hrm.dao.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EmployeeDAO {

    public List<Employee> getAll() {
        List<Employee> list = new ArrayList<>();
        String sql = """
            SELECT e.*, d.DeptName, su.UserID, su.Username, su.LastLogin, r.RoleName
            FROM Employee e 
            LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
            LEFT JOIN SystemUser su ON e.EmployeeID = su.EmployeeID
            LEFT JOIN Role r ON su.RoleID = r.RoleID
        """;

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee e = new Employee();
                e.setEmployeeId(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));
                e.setGender(rs.getString("Gender"));
                e.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                e.setAddress(rs.getString("Address"));
                e.setPhone(rs.getString("Phone"));
                e.setEmail(rs.getString("Email"));
                e.setDepartmentId(rs.getInt("DepartmentID"));
                e.setDepartmentName(rs.getString("DeptName"));
                e.setPosition(rs.getString("Position"));
                e.setStatus(rs.getString("Status"));
                e.setEmploymentPeriod(rs.getString("EmploymentPeriod"));

                if (rs.getInt("UserID") != 0) {
                    SystemUser systemUser = new SystemUser();
                    systemUser.setUserId(rs.getInt("UserID"));
                    systemUser.setUsername(rs.getString("Username"));

                    Timestamp lastLoginTimestamp = rs.getTimestamp("LastLogin");
                    if (lastLoginTimestamp != null) {
                        systemUser.setLastLogin(lastLoginTimestamp.toLocalDateTime());
                    }

                    Role role = new Role();
                    role.setRoleName(rs.getString("RoleName"));
                    systemUser.setRole(role);

                    e.setSystemUser(systemUser);
                }

                list.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Employee getById(int id) {
        String sql = """
        SELECT e.*, d.DeptName
        FROM Employee e
        LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
        WHERE e.EmployeeID = ?
    """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            System.out.println("[v0] EmployeeDAO.getById() - Querying employee with ID: " + id);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("[v0] EmployeeDAO.getById() - Found employee: " + rs.getString("FullName"));

                Employee e = new Employee();
                e.setEmployeeId(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));
                e.setGender(rs.getString("Gender"));
                e.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                e.setAddress(rs.getString("Address"));
                e.setPhone(rs.getString("Phone"));
                e.setEmail(rs.getString("Email"));
                e.setDepartmentId(rs.getInt("DepartmentID"));
                e.setDepartmentName(rs.getString("DeptName"));
                e.setPosition(rs.getString("Position"));
                e.setEmploymentPeriod(rs.getString("EmploymentPeriod"));
                e.setStatus(rs.getString("Status"));

                String contractSql = """
                SELECT BaseSalary FROM Contract
                WHERE EmployeeID = ?
                ORDER BY StartDate DESC
                LIMIT 1
            """;
                try (PreparedStatement contractPs = con.prepareStatement(contractSql)) {
                    contractPs.setInt(1, id);
                    ResultSet contractRs = contractPs.executeQuery();
                    if (contractRs.next()) {
                        e.setSalary(contractRs.getDouble("BaseSalary"));
                    } else {
                        e.setSalary(0.0);
                    }
                }

                return e;
            } else {
                System.out.println("[v0] EmployeeDAO.getById() - No employee found with ID: " + id);
            }
        } catch (SQLException e) {
            System.err.println("[v0] EmployeeDAO.getById() - SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean insert(Employee e) {
        String sql = """
        INSERT INTO Employee (FullName, Gender, DOB, Address, Phone, Email, 
                              DepartmentID, Position, EmploymentPeriod, Status)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getFullName());
            ps.setString(2, e.getGender());
            ps.setDate(3, e.getDob() != null ? Date.valueOf(e.getDob()) : null);
            ps.setString(4, e.getAddress());
            ps.setString(5, e.getPhone());
            ps.setString(6, e.getEmail());
            ps.setObject(7, e.getDepartmentId(), Types.INTEGER);
            ps.setString(8, e.getPosition());
            ps.setString(9, e.getEmploymentPeriod());
            ps.setString(10, e.getStatus());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean update(Employee e) {
        String sql = """
        UPDATE Employee SET FullName=?, Gender=?, DOB=?, Address=?, Phone=?, Email=?, 
                        DepartmentID=?, Position=?, EmploymentPeriod=?, Status=? 
        WHERE EmployeeID=?
    """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getFullName());
            ps.setString(2, e.getGender());
            ps.setDate(3, e.getDob() != null ? Date.valueOf(e.getDob()) : null);
            ps.setString(4, e.getAddress());
            ps.setString(5, e.getPhone());
            ps.setString(6, e.getEmail());
            ps.setObject(7, e.getDepartmentId(), Types.INTEGER);
            ps.setString(8, e.getPosition());
            ps.setString(9, e.getEmploymentPeriod());
            ps.setString(10, e.getStatus());
            ps.setInt(11, e.getEmployeeId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Employee WHERE EmployeeID=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateEmployeeInfo(Employee e) {
        String sql = """
            UPDATE Employee SET FullName=?, Gender=?, DOB=?, Address=?, Phone=?, Email=?, 
                                DepartmentID=?, Position=?, EmploymentPeriod=?, Status=?
            WHERE EmployeeID=?
        """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, e.getFullName());
            ps.setString(2, e.getGender());
            ps.setDate(3, e.getDob() != null ? Date.valueOf(e.getDob()) : null);
            ps.setString(4, e.getAddress());
            ps.setString(5, e.getPhone());
            ps.setString(6, e.getEmail());
            ps.setObject(7, e.getDepartmentId(), Types.INTEGER);
            ps.setString(8, e.getPosition());
            ps.setString(9, e.getEmploymentPeriod());
            ps.setString(10, e.getStatus());
            ps.setInt(11, e.getEmployeeId());

            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean updateEmployeeStatus(int employeeId, String status) {
        String sql = "UPDATE Employee SET Status = ? WHERE EmployeeID = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setInt(2, employeeId);

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Employee getEmployeeBySystemUserId(int systemUserId) {
        String sql = "SELECT e.*, e.Status, e.EmploymentPeriod, d.DeptName, su.Username, su.LastLogin, r.RoleName "
                  + "FROM Employee e "
                  + "LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID "
                  + "JOIN SystemUser su ON e.EmployeeID = su.EmployeeID "
                  + "LEFT JOIN Role r ON su.RoleID = r.RoleID "
                  + "WHERE su.UserID = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, systemUserId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("EmployeeDAO: Found record for systemUserId: " + systemUserId);
                    Employee employee = new Employee();
                    employee.setEmployeeId(rs.getInt("EmployeeID"));
                    employee.setFullName(rs.getString("FullName"));
                    employee.setGender(rs.getString("Gender"));
                    employee.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                    employee.setAddress(rs.getString("Address"));
                    employee.setPhone(rs.getString("Phone"));
                    employee.setEmail(rs.getString("Email"));
                    employee.setPosition(rs.getString("Position"));
                    employee.setStatus(rs.getString("Status"));
                    employee.setEmploymentPeriod(rs.getString("EmploymentPeriod"));

                    if (rs.getInt("DepartmentID") != 0) {
                        Department department = new Department();
                        department.setDepartmentId(rs.getInt("DepartmentID"));
                        department.setDeptName(rs.getString("DeptName"));
                        employee.setDepartment(department);
                    }

                    SystemUser systemUser = new SystemUser();
                    systemUser.setUsername(rs.getString("Username"));
                    Timestamp lastLoginTimestamp = rs.getTimestamp("LastLogin");
                    if (lastLoginTimestamp != null) {
                        systemUser.setLastLogin(lastLoginTimestamp.toLocalDateTime());
                    }

                    Role role = new Role();
                    role.setRoleName(rs.getString("RoleName"));
                    systemUser.setRole(role);

                    employee.setSystemUser(systemUser);

                    return employee;
                }
            }
        } catch (SQLException e) {
            System.err.println("EmployeeDAO: SQL Exception for systemUserId: " + systemUserId);
            e.printStackTrace();
        }
        System.out.println("EmployeeDAO: No employee found for systemUserId: " + systemUserId);
        return null;
    }

    public List<Employee> getPaged(int offset, int limit) {
        List<Employee> list = new ArrayList<>();
        String sql = """
        SELECT e.*, d.DeptName, COALESCE(c.BaseSalary, 0) as BaseSalary
        FROM Employee e
        LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
        LEFT JOIN Contract c ON e.EmployeeID = c.EmployeeID AND c.ContractID = (
            SELECT ContractID FROM Contract
            WHERE EmployeeID = e.EmployeeID
            ORDER BY StartDate DESC
            LIMIT 1
        )
        ORDER BY e.EmployeeID
        LIMIT ?, ?
    """;

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, offset);
            ps.setInt(2, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee e = new Employee();
                    e.setEmployeeId(rs.getInt("EmployeeID"));
                    e.setFullName(rs.getString("FullName"));
                    e.setGender(rs.getString("Gender"));
                    e.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                    e.setAddress(rs.getString("Address"));
                    e.setPhone(rs.getString("Phone"));
                    e.setEmail(rs.getString("Email"));
                    e.setDepartmentId(rs.getInt("DepartmentID"));
                    e.setDepartmentName(rs.getString("DeptName"));
                    e.setPosition(rs.getString("Position"));
                    e.setEmploymentPeriod(rs.getString("EmploymentPeriod"));
                    e.setActive("Active".equalsIgnoreCase(rs.getString("Status")));
                    e.setSalary(rs.getDouble("BaseSalary"));
                    e.setStatus(rs.getString("Status"));
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getPaged: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalEmployeesCount() {
        String sql = "SELECT COUNT(*) as total FROM Employee";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Employee> searchEmployeesPaged(String keyword, int offset, int limit) {
        List<Employee> list = new ArrayList<>();
        String sql = """
            SELECT e.*, d.DeptName, COALESCE(c.BaseSalary, 0) as BaseSalary
            FROM Employee e
            LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
            LEFT JOIN Contract c ON e.EmployeeID = c.EmployeeID AND c.ContractID = (
                SELECT ContractID FROM Contract
                WHERE EmployeeID = e.EmployeeID
                ORDER BY StartDate DESC
                LIMIT 1
            )
            WHERE e.FullName LIKE ? OR e.Email LIKE ? OR e.Position LIKE ?
            ORDER BY e.EmployeeID
            LIMIT ?, ?
        """;

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setInt(4, offset);
            ps.setInt(5, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee e = new Employee();
                    e.setEmployeeId(rs.getInt("EmployeeID"));
                    e.setFullName(rs.getString("FullName"));
                    e.setGender(rs.getString("Gender"));
                    e.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                    e.setAddress(rs.getString("Address"));
                    e.setPhone(rs.getString("Phone"));
                    e.setEmail(rs.getString("Email"));
                    e.setDepartmentId(rs.getInt("DepartmentID"));
                    e.setDepartmentName(rs.getString("DeptName"));
                    e.setPosition(rs.getString("Position"));
                    e.setEmploymentPeriod(rs.getString("EmploymentPeriod"));
                    e.setActive("Active".equalsIgnoreCase(rs.getString("Status")));
                    e.setSalary(rs.getDouble("BaseSalary"));
                    e.setStatus(rs.getString("Status"));
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in searchEmployeesPaged: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public int searchEmployeesCount(String keyword) {
        String sql = "SELECT COUNT(*) as total FROM Employee WHERE FullName LIKE ? OR Email LIKE ? OR Position LIKE ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in searchEmployeesCount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public int getTotalEmployees() {
        String sql = "SELECT COUNT(*) as total FROM Employee";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getActiveEmployees() {
        String sql = "SELECT COUNT(*) as total FROM Employee WHERE Status = 'Active'";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Employee> getManagerList() {
        List<Employee> managers = new ArrayList<>();
        String sql = "SELECT DISTINCT e.EmployeeID, e.FullName, e.Email, e.DepartmentID "
                  + "FROM Employee e "
                  + "WHERE e.EmployeeID IN (SELECT DeptManagerID FROM Department WHERE DeptManagerID IS NOT NULL) "
                  + "ORDER BY e.FullName";

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getInt("EmployeeID"));
                emp.setFullName(rs.getString("FullName"));
                emp.setEmail(rs.getString("Email"));
                emp.setDepartmentId(rs.getInt("DepartmentID"));
                managers.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getManagerList: " + e.getMessage());
            e.printStackTrace();
        }
        return managers;
    }

    public int getEmployeeCountByDepartment(int departmentId) {
        String sql = "SELECT COUNT(*) as count FROM Employee WHERE DepartmentID = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, departmentId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("count");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public List<Employee> getByDepartmentId(int departmentId) {
        List<Employee> list = new ArrayList<>();
        String sql = """
            SELECT e.*, d.DeptName
            FROM Employee e
            LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
            WHERE e.DepartmentID = ?
            ORDER BY e.FullName
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee e = new Employee();
                    e.setEmployeeId(rs.getInt("EmployeeID"));
                    e.setFullName(rs.getString("FullName"));
                    e.setGender(rs.getString("Gender"));
                    e.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                    e.setAddress(rs.getString("Address"));
                    e.setPhone(rs.getString("Phone"));
                    e.setEmail(rs.getString("Email"));
                    e.setDepartmentId(rs.getInt("DepartmentID"));
                    e.setDepartmentName(rs.getString("DeptName"));
                    e.setPosition(rs.getString("Position"));
                    e.setEmploymentPeriod(rs.getString("EmploymentPeriod"));
                    e.setStatus(rs.getString("Status"));
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Employee> searchEmployees(String keyword) {
        List<Employee> list = new ArrayList<>();
        String sql = """
            SELECT e.*, d.DeptName, COALESCE(c.BaseSalary, 0) as BaseSalary
            FROM Employee e
            LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
            LEFT JOIN Contract c ON e.EmployeeID = c.EmployeeID AND c.ContractID = (
                SELECT ContractID FROM Contract
                WHERE EmployeeID = e.EmployeeID
                ORDER BY StartDate DESC
                LIMIT 1
            )
            WHERE e.FullName LIKE ? OR e.Email LIKE ? OR e.Position LIKE ? OR d.DeptName LIKE ?
            ORDER BY e.EmployeeID
        """;

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            ps.setString(3, searchPattern);
            ps.setString(4, searchPattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Employee e = new Employee();
                    e.setEmployeeId(rs.getInt("EmployeeID"));
                    e.setFullName(rs.getString("FullName"));
                    e.setGender(rs.getString("Gender"));
                    e.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                    e.setAddress(rs.getString("Address"));
                    e.setPhone(rs.getString("Phone"));
                    e.setEmail(rs.getString("Email"));
                    e.setDepartmentId(rs.getInt("DepartmentID"));
                    e.setDepartmentName(rs.getString("DeptName"));
                    e.setPosition(rs.getString("Position"));
                    e.setEmploymentPeriod(rs.getString("EmploymentPeriod"));
                    e.setActive("Active".equalsIgnoreCase(rs.getString("Status")));
                    e.setSalary(rs.getDouble("BaseSalary"));
                    e.setStatus(rs.getString("Status"));
                    list.add(e);
                }
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in searchEmployees: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Employee> getInternEmployees() {
        List<Employee> interns = new ArrayList<>();
        String sql = """
        SELECT e.EmployeeID, e.FullName, e.Email, e.Position, e.DepartmentID, d.DeptName
        FROM Employee e
        LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
        WHERE e.Status = 'Intern'
        ORDER BY e.FullName
    """;

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getInt("EmployeeID"));
                emp.setFullName(rs.getString("FullName"));
                emp.setEmail(rs.getString("Email"));
                emp.setPosition(rs.getString("Position"));
                emp.setDepartmentId(rs.getInt("DepartmentID"));
                emp.setDepartmentName(rs.getString("DeptName"));
                interns.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getInternEmployees: " + e.getMessage());
            e.printStackTrace();
        }
        return interns;
    }

    public List<Employee> getNonInternEmployees(int offset, int pageSize) {
        List<Employee> employees = new ArrayList<>();
        String sql = """
        SELECT e.*, d.DeptName, COALESCE(c.BaseSalary, 0) as BaseSalary
        FROM Employee e
        LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
        LEFT JOIN Contract c ON e.EmployeeID = c.EmployeeID AND c.ContractID = (
            SELECT ContractID FROM Contract
            WHERE EmployeeID = e.EmployeeID
            ORDER BY StartDate DESC
            LIMIT 1
        )
        WHERE e.Status != 'Intern'
        ORDER BY e.FullName
        LIMIT ? OFFSET ?
    """;

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pageSize);
            ps.setInt(2, offset);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Employee emp = new Employee();
                emp.setEmployeeId(rs.getInt("EmployeeID"));
                emp.setFullName(rs.getString("FullName"));
                emp.setGender(rs.getString("Gender"));
                emp.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                emp.setAddress(rs.getString("Address"));
                emp.setPhone(rs.getString("Phone"));
                emp.setEmail(rs.getString("Email"));
                emp.setDepartmentId(rs.getInt("DepartmentID"));
                emp.setDepartmentName(rs.getString("DeptName"));
                emp.setPosition(rs.getString("Position"));
                emp.setEmploymentPeriod(rs.getString("EmploymentPeriod"));
                emp.setStatus(rs.getString("Status"));
                emp.setSalary(rs.getDouble("BaseSalary"));
                employees.add(emp);
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getNonInternEmployees: " + e.getMessage());
            e.printStackTrace();
        }
        return employees;
    }

    public int getNonInternEmployeesCount() {
        String sql = "SELECT COUNT(*) as total FROM Employee WHERE Status != 'Intern'";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            System.err.println("[v0] Error in getNonInternEmployeesCount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public boolean isEmailExists(String email) {
        String sql = "SELECT COUNT(*) FROM Employee WHERE Email = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in isEmailExists: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public Employee getByEmail(String email) {
        String sql = """
        SELECT e.*, d.DeptName
        FROM Employee e
        LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
        WHERE e.Email = ?
    """;
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, email);
            System.out.println("[v0] EmployeeDAO.getByEmail() - Querying employee with email: " + email);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("[v0] EmployeeDAO.getByEmail() - Found employee: " + rs.getString("FullName"));

                Employee e = new Employee();
                e.setEmployeeId(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));
                e.setGender(rs.getString("Gender"));
                e.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                e.setAddress(rs.getString("Address"));
                e.setPhone(rs.getString("Phone"));
                e.setEmail(rs.getString("Email"));
                e.setDepartmentId(rs.getInt("DepartmentID"));
                e.setDepartmentName(rs.getString("DeptName"));
                e.setPosition(rs.getString("Position"));
                e.setEmploymentPeriod(rs.getString("EmploymentPeriod"));
                e.setStatus(rs.getString("Status"));

                return e;
            } else {
                System.out.println("[v0] EmployeeDAO.getByEmail() - No employee found with email: " + email);
            }
        } catch (SQLException e) {
            System.err.println("[v0] EmployeeDAO.getByEmail() - SQL Exception: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<Employee> getFilteredEmployees(String searchKeyword, Integer departmentId,
                                             String status, String gender, String position,
                                             int offset, int limit) {
        List<Employee> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("""
            SELECT e.*, d.DeptName, COALESCE(c.BaseSalary, 0) as BaseSalary
            FROM Employee e
            LEFT JOIN Department d ON e.DepartmentID = d.DepartmentID
            LEFT JOIN Contract c ON e.EmployeeID = c.EmployeeID AND c.ContractID = (
                SELECT ContractID FROM Contract
                WHERE EmployeeID = e.EmployeeID
                ORDER BY StartDate DESC
                LIMIT 1
            )
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (e.FullName LIKE ? OR e.Email LIKE ? OR e.Position LIKE ?)");
            String pattern = "%" + searchKeyword.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (departmentId != null) {
            sql.append(" AND e.DepartmentID = ?");
            params.add(departmentId);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND e.Status = ?");
            params.add(status.trim());
        }

        if (gender != null && !gender.trim().isEmpty()) {
            sql.append(" AND e.Gender = ?");
            params.add(gender.trim());
        }

        if (position != null && !position.trim().isEmpty()) {
            sql.append(" AND e.Position = ?");
            params.add(position.trim());
        }

        sql.append(" ORDER BY e.FullName LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Employee e = new Employee();
                e.setEmployeeId(rs.getInt("EmployeeID"));
                e.setFullName(rs.getString("FullName"));
                e.setGender(rs.getString("Gender"));
                e.setDob(rs.getDate("DOB") != null ? rs.getDate("DOB").toLocalDate() : null);
                e.setAddress(rs.getString("Address"));
                e.setPhone(rs.getString("Phone"));
                e.setEmail(rs.getString("Email"));
                e.setDepartmentId(rs.getInt("DepartmentID"));
                e.setDepartmentName(rs.getString("DeptName"));
                e.setPosition(rs.getString("Position"));
                e.setEmploymentPeriod(rs.getString("EmploymentPeriod"));
                e.setStatus(rs.getString("Status"));
                e.setSalary(rs.getDouble("BaseSalary"));
                list.add(e);
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getFilteredEmployees: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public int getFilteredEmployeesCount(String searchKeyword, Integer departmentId,
                                       String status, String gender, String position) {
        StringBuilder sql = new StringBuilder("""
            SELECT COUNT(*) as total
            FROM Employee e
            WHERE 1=1
        """);

        List<Object> params = new ArrayList<>();

        if (searchKeyword != null && !searchKeyword.trim().isEmpty()) {
            sql.append(" AND (e.FullName LIKE ? OR e.Email LIKE ? OR e.Position LIKE ?)");
            String pattern = "%" + searchKeyword.trim() + "%";
            params.add(pattern);
            params.add(pattern);
            params.add(pattern);
        }

        if (departmentId != null) {
            sql.append(" AND e.DepartmentID = ?");
            params.add(departmentId);
        }

        if (status != null && !status.trim().isEmpty()) {
            sql.append(" AND e.Status = ?");
            params.add(status.trim());
        }

        if (gender != null && !gender.trim().isEmpty()) {
            sql.append(" AND e.Gender = ?");
            params.add(gender.trim());
        }

        if (position != null && !position.trim().isEmpty()) {
            sql.append(" AND e.Position = ?");
            params.add(position.trim());
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getFilteredEmployeesCount: " + e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    public List<String> getDistinctPositions() {
        List<String> positions = new ArrayList<>();
        String sql = "SELECT DISTINCT Position FROM Employee WHERE Position IS NOT NULL ORDER BY Position";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String position = rs.getString("Position");
                if (position != null && !position.trim().isEmpty()) {
                    positions.add(position);
                }
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getDistinctPositions: " + e.getMessage());
            e.printStackTrace();
        }
        return positions;
    }

    public Map<String, Long> getStatusStats() {
        Map<String, Long> stats = new HashMap<>();
        String sql = "SELECT Status, COUNT(*) as count FROM Employee WHERE Status IS NOT NULL GROUP BY Status";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String status = rs.getString("Status");
                long count = rs.getLong("count");
                if (status != null && !status.trim().isEmpty()) {
                    stats.put(status, count);
                }
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getStatusStats: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }

    public Map<String, Long> getDepartmentStats() {
        Map<String, Long> stats = new HashMap<>();
        String sql = """
            SELECT d.DeptName, COUNT(e.EmployeeID) as count
            FROM Department d
            LEFT JOIN Employee e ON d.DepartmentID = e.DepartmentID
            GROUP BY d.DeptName
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                String deptName = rs.getString("DeptName");
                long count = rs.getLong("count");
                if (deptName != null && !deptName.trim().isEmpty()) {
                    stats.put(deptName, count);
                }
            }
        } catch (SQLException e) {
            System.err.println("[v0] Error in getDepartmentStats: " + e.getMessage());
            e.printStackTrace();
        }
        return stats;
    }
}
