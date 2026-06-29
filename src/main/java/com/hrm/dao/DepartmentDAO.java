
package com.hrm.dao;

import com.hrm.model.entity.Department;
import com.hrm.dao.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;



public class DepartmentDAO {

    public List<Department> getAll() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM Department";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new Department(
                    rs.getInt("DepartmentID"),
                    rs.getString("DeptName"),
                    rs.getInt("DeptManagerID")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Department getById(int id) {
        String sql = "SELECT * FROM Department WHERE DepartmentID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Department(
                    rs.getInt("DepartmentID"),
                    rs.getString("DeptName"),
                    rs.getInt("DeptManagerID")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public boolean insert(Department d) {
        String sql = "INSERT INTO Department (DeptName, DeptManagerID) VALUES (?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getDeptName());
            if (d.getDeptManagerId() != null)
                ps.setInt(2, d.getDeptManagerId());
            else ps.setNull(2, Types.INTEGER);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean update(Department d) {
        String sql = "UPDATE Department SET DeptName=?, DeptManagerID=? WHERE DepartmentID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, d.getDeptName());
            if (d.getDeptManagerId() != null)
                ps.setInt(2, d.getDeptManagerId());
            else ps.setNull(2, Types.INTEGER);
            ps.setInt(3, d.getDepartmentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM Department WHERE DepartmentID=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public List<Department> getPaged(int offset, int limit) {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM Department ORDER BY DeptName LIMIT ? OFFSET ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department(
                        rs.getInt("DepartmentID"),
                        rs.getString("DeptName"),
                        rs.getObject("DeptManagerID") != null ? rs.getInt("DeptManagerID") : null
                    );
                    list.add(dept);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalDepartmentsCount() {
        String sql = "SELECT COUNT(*) AS total FROM Department";
        try (Connection conn = DBConnection.getConnection(); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    
    public int getTotalDepartments() {
        String sql = "SELECT COUNT(*) as total FROM Department";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt("total");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

public List<Department> searchDepartments(String keyword) throws SQLException {
        List<Department> departments = new ArrayList<>();
        String query = "SELECT * FROM Department WHERE DeptName LIKE ? ORDER BY DeptName";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department(
                        rs.getInt("DepartmentID"),
                        rs.getString("DeptName"),
                        rs.getObject("DeptManagerID") != null ? rs.getInt("DeptManagerID") : null
                    );
                    departments.add(dept);
                }
            }
        }
        return departments;
    }

    public List<Department> searchDepartmentsPaged(String keyword, int offset, int limit) throws SQLException {
        List<Department> departments = new ArrayList<>();
        String query = "SELECT * FROM Department WHERE DeptName LIKE ? ORDER BY DeptName LIMIT ? OFFSET ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);
            pstmt.setInt(2, limit);
            pstmt.setInt(3, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department(
                        rs.getInt("DepartmentID"),
                        rs.getString("DeptName"),
                        rs.getObject("DeptManagerID") != null ? rs.getInt("DeptManagerID") : null
                    );
                    departments.add(dept);
                }
            }
        }
        return departments;
    }

    public int searchDepartmentsCount(String keyword) throws SQLException {
        String query = "SELECT COUNT(*) as total FROM Department WHERE DeptName LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            String searchPattern = "%" + keyword + "%";
            pstmt.setString(1, searchPattern);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        }
        return 0;
    }


    public boolean isDepartmentNameExists(String deptName, int excludeDeptId) throws SQLException {
        String query = "SELECT COUNT(*) as count FROM Department WHERE DeptName = ? AND DepartmentID != ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, deptName);
            pstmt.setInt(2, excludeDeptId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("count") > 0;
                }
            }
        }
        return false;
    }
    
    
    public List<Department> getPagedByDepartment(int departmentId, int offset, int limit) {
        List<Department> list = new ArrayList<>();
        String sql = departmentId > 0 
            ? "SELECT * FROM Department WHERE DepartmentID = ? ORDER BY DeptName LIMIT ? OFFSET ?"
            : "SELECT * FROM Department ORDER BY DeptName LIMIT ? OFFSET ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (departmentId > 0) {
                ps.setInt(1, departmentId);
                ps.setInt(2, limit);
                ps.setInt(3, offset);
            } else {
                ps.setInt(1, limit);
                ps.setInt(2, offset);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department(
                        rs.getInt("DepartmentID"),
                        rs.getString("DeptName"),
                        rs.getObject("DeptManagerID") != null ? rs.getInt("DeptManagerID") : null
                    );
                    list.add(dept);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public int getCountByDepartment(int departmentId) {
        String sql = departmentId > 0 
            ? "SELECT COUNT(*) as total FROM Department WHERE DepartmentID = ?"
            : "SELECT COUNT(*) as total FROM Department";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (departmentId > 0) {
                ps.setInt(1, departmentId);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<Department> getPagedByDepartmentAndStatus(int departmentId, String status, int offset, int limit) {
        List<Department> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM Department WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (departmentId > 0) {
            sql.append(" AND DepartmentID = ?");
            params.add(departmentId);
        }
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND LOWER(Status) = LOWER(?)");
            params.add(status);
        }
        sql.append(" ORDER BY DeptName LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department(
                        rs.getInt("DepartmentID"),
                        rs.getString("DeptName"),
                        rs.getObject("DeptManagerID") != null ? rs.getInt("DeptManagerID") : null
                    );
                    list.add(dept);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public int getCountByDepartmentAndStatus(int departmentId, String status) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as total FROM Department WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (departmentId > 0) {
            sql.append(" AND DepartmentID = ?");
            params.add(departmentId);
        }
        if (status != null && !status.trim().isEmpty() && !"all".equalsIgnoreCase(status)) {
            sql.append(" AND LOWER(Status) = LOWER(?)");
            params.add(status);
        }

        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public List<Department> getPagedByTimeRange(String timeRange, int offset, int limit) {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM Department WHERE 1=1";
        
        if (timeRange != null && !timeRange.isEmpty()) {
            switch (timeRange.toLowerCase()) {
                case "today":
                    sql += " AND DATE(CreatedAt) = CURDATE()";
                    break;
                case "week":
                    sql += " AND WEEK(CreatedAt) = WEEK(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
                    break;
                case "month":
                    sql += " AND MONTH(CreatedAt) = MONTH(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
                    break;
            }
        }
        sql += " ORDER BY DeptName LIMIT ? OFFSET ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department(
                        rs.getInt("DepartmentID"),
                        rs.getString("DeptName"),
                        rs.getObject("DeptManagerID") != null ? rs.getInt("DeptManagerID") : null
                    );
                    list.add(dept);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getCountByTimeRange(String timeRange) {
        String sql = "SELECT COUNT(*) as total FROM Department WHERE 1=1";
        
        if (timeRange != null && !timeRange.isEmpty()) {
            switch (timeRange.toLowerCase()) {
                case "today":
                    sql += " AND DATE(CreatedAt) = CURDATE()";
                    break;
                case "week":
                    sql += " AND WEEK(CreatedAt) = WEEK(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
                    break;
                case "month":
                    sql += " AND MONTH(CreatedAt) = MONTH(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
                    break;
            }
        }
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

   public List<Department> getPagedByDepartmentAndTimeRange(int departmentId, String timeRange, int offset, int limit) {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM Department WHERE 1=1";
        
        if (departmentId > 0) {
            sql += " AND DepartmentID = " + departmentId;
        }
        
        if (timeRange != null && !timeRange.isEmpty()) {
            switch (timeRange.toLowerCase()) {
                case "today":
                    sql += " AND DATE(CreatedAt) = CURDATE()";
                    break;
                case "week":
                    sql += " AND WEEK(CreatedAt) = WEEK(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
                    break;
                case "month":
                    sql += " AND MONTH(CreatedAt) = MONTH(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
                    break;
            }
        }
        sql += " ORDER BY DeptName LIMIT ? OFFSET ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Department dept = new Department(
                        rs.getInt("DepartmentID"),
                        rs.getString("DeptName"),
                        rs.getObject("DeptManagerID") != null ? rs.getInt("DeptManagerID") : null
                    );
                    list.add(dept);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getCountByDepartmentAndTimeRange(int departmentId, String timeRange) {
        String sql = "SELECT COUNT(*) as total FROM Department WHERE 1=1";
        
        if (departmentId > 0) {
            sql += " AND DepartmentID = " + departmentId;
        }
        
        if (timeRange != null && !timeRange.isEmpty()) {
            switch (timeRange.toLowerCase()) {
                case "today":
                    sql += " AND DATE(CreatedAt) = CURDATE()";
                    break;
                case "week":
                    sql += " AND WEEK(CreatedAt) = WEEK(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
                    break;
                case "month":
                    sql += " AND MONTH(CreatedAt) = MONTH(CURDATE()) AND YEAR(CreatedAt) = YEAR(CURDATE())";
                    break;
            }
        }
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

