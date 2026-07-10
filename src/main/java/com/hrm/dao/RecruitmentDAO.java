/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.hrm.dao;

import com.hrm.model.entity.Recruitment;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 *

 */
public class RecruitmentDAO {

    // Lấy tất cả Recruitment
    public List<Recruitment> getAll() {
        List<Recruitment> list = new ArrayList<>();
        String sql = "SELECT * FROM Recruitment ORDER BY PostedDate DESC";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Recruitment recruitment = new Recruitment(
                    rs.getInt("RecruitmentID"),
                    rs.getString("JobTitle"),
                    rs.getString("JobDescription"),
                    rs.getString("Requirement"),
                    rs.getString("Location"),
                    rs.getDouble("Salary"),
                    rs.getString("Status"),
                    rs.getInt("Applicant"),
                    rs.getObject("PostedDate", LocalDateTime.class)
                );
                list.add(recruitment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy Recruitment theo ID
    public Recruitment getById(int recruitmentId) {
        String sql = "SELECT * FROM Recruitment WHERE RecruitmentID = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, recruitmentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Recruitment recruitment = new Recruitment(
                        rs.getInt("RecruitmentID"),
                        rs.getString("JobTitle"),
                        rs.getString("JobDescription"),
                        rs.getString("Requirement"),
                        rs.getString("Location"),
                        rs.getDouble("Salary"),
                        rs.getString("Status"),
                        rs.getInt("Applicant"),
                        rs.getObject("PostedDate", LocalDateTime.class)
                    );
                    return recruitment;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Recruitment> getLatestThree() {
        List<Recruitment> list = new ArrayList<>();
        String sql = "SELECT * FROM Recruitment WHERE Status = 'Applied' ORDER BY PostedDate DESC LIMIT 3";

        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                Recruitment recruitment = new Recruitment(
                    rs.getInt("RecruitmentID"),
                    rs.getString("JobTitle"),
                    rs.getString("JobDescription"),
                    rs.getString("Requirement"),
                    rs.getString("Location"),
                    rs.getDouble("Salary"),
                    rs.getString("Status"),
                    rs.getInt("Applicant"),
                    rs.getObject("PostedDate", LocalDateTime.class)
                );
                list.add(recruitment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Thêm Recruitment mới
    public boolean insert(Recruitment recruitment) {
        String sql = """
            INSERT INTO Recruitment (JobTitle, JobDescription, PostDate, Status, PostedBy, PostedDate)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
//            ps.setString(1, recruitment.getJobTitle());
//            ps.setString(2, recruitment.getJobDescription());
//            ps.setDate(3, recruitment.getPostDate() != null ? 
//                Date.valueOf(recruitment.getPostDate()) : Date.valueOf(LocalDate.now()));
//            ps.setString(4, recruitment.getStatus() != null ? recruitment.getStatus() : "Open");
//            ps.setObject(5, recruitment.getPostedBy(), Types.INTEGER);
//            ps.setTimestamp(6, recruitment.getPostedDate() != null ? 
//                Timestamp.valueOf(recruitment.getPostedDate()) : Timestamp.valueOf(LocalDateTime.now()));
//            
//            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật Recruitment
    public boolean update(Recruitment recruitment) {
        String sql = """
            UPDATE Recruitment SET JobTitle=?, JobDescription=?, PostDate=?, 
                           Status=?, PostedBy=?, PostedDate=? 
            WHERE RecruitmentID=?
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
//            ps.setString(1, recruitment.getJobTitle());
//            ps.setString(2, recruitment.getJobDescription());
//            ps.setDate(3, recruitment.getPostDate() != null ? 
//                Date.valueOf(recruitment.getPostDate()) : null);
//            ps.setString(4, recruitment.getStatus());
//            ps.setObject(5, recruitment.getPostedBy(), Types.INTEGER);
//            ps.setTimestamp(6, recruitment.getPostedDate() != null ? 
//                Timestamp.valueOf(recruitment.getPostedDate()) : null);
//            ps.setInt(7, recruitment.getRecruitmentId());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa Recruitment
    public boolean delete(int recruitmentId) {
        String sql = "DELETE FROM Recruitment WHERE RecruitmentID=?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, recruitmentId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đếm tổng số Recruitment
    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM Recruitment";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Tìm kiếm Recruitment theo title hoặc description
    public List<Recruitment> search(String keyword) {
        List<Recruitment> list = new ArrayList<>();
        String sql = """
            SELECT * FROM Recruitment 
            WHERE JobTitle LIKE ? OR JobDescription LIKE ?
            ORDER BY PostedDate DESC
        """;
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            ps.setString(1, searchPattern);
            ps.setString(2, searchPattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Recruitment recruitment = new Recruitment(
                        rs.getInt("RecruitmentID"),
                        rs.getString("JobTitle"),
                        rs.getString("JobDescription"),
                        rs.getString("Requirement"),
                        rs.getString("Location"),
                        rs.getDouble("Salary"),
                        rs.getString("Status"),
                        rs.getInt("Applicant"),
                        rs.getObject("PostedDate", LocalDateTime.class)
                    );
                    list.add(recruitment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
