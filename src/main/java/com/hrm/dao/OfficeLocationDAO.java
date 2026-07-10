package com.hrm.dao;

import com.hrm.model.entity.OfficeLocation;
import com.hrm.util.GeoUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class OfficeLocationDAO {

    public List<OfficeLocation> getAll() {
        String sql = "SELECT * FROM OfficeLocation ORDER BY IsActive DESC, LocationName";
        return queryLocations(sql);
    }

    public List<OfficeLocation> getActiveOfficeLocations() {
        String sql = "SELECT * FROM OfficeLocation WHERE IsActive = 1 ORDER BY LocationName";
        return queryLocations(sql);
    }

    public OfficeLocation getById(int id) {
        String sql = "SELECT * FROM OfficeLocation WHERE OfficeLocationID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapLocation(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public OfficeLocation getNearestActiveLocation(double lat, double lng) {
        List<OfficeLocation> locations = getActiveOfficeLocations();
        OfficeLocation nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        for (OfficeLocation location : locations) {
            if (location.getLatitude() == null || location.getLongitude() == null) {
                continue;
            }
            double distance = GeoUtil.distanceMeters(
                    lat, lng, location.getLatitude().doubleValue(), location.getLongitude().doubleValue());
            if (distance < nearestDistance) {
                nearestDistance = distance;
                nearest = location;
            }
        }
        return nearest;
    }

    public boolean create(OfficeLocation location) {
        String sql = """
            INSERT INTO OfficeLocation (LocationCode, LocationName, Address, Latitude, Longitude, RadiusMeters, IsActive)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setLocationParams(ps, location, false);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(OfficeLocation location) {
        String sql = """
            UPDATE OfficeLocation
            SET LocationCode = ?, LocationName = ?, Address = ?, Latitude = ?, Longitude = ?, RadiusMeters = ?, IsActive = ?
            WHERE OfficeLocationID = ?
        """;
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            setLocationParams(ps, location, true);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean deactivate(int id) {
        String sql = "UPDATE OfficeLocation SET IsActive = 0 WHERE OfficeLocationID = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<OfficeLocation> queryLocations(String sql) {
        List<OfficeLocation> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapLocation(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void setLocationParams(PreparedStatement ps, OfficeLocation location, boolean includeId) throws SQLException {
        ps.setString(1, blankToNull(location.getLocationCode()));
        ps.setString(2, location.getLocationName());
        ps.setString(3, blankToNull(location.getAddress()));
        ps.setBigDecimal(4, location.getLatitude());
        ps.setBigDecimal(5, location.getLongitude());
        ps.setInt(6, location.getRadiusMeters());
        ps.setBoolean(7, location.isActive());
        if (includeId) {
            ps.setInt(8, location.getOfficeLocationId());
        }
    }

    private OfficeLocation mapLocation(ResultSet rs) throws SQLException {
        OfficeLocation location = new OfficeLocation();
        location.setOfficeLocationId(rs.getInt("OfficeLocationID"));
        location.setLocationCode(rs.getString("LocationCode"));
        location.setLocationName(rs.getString("LocationName"));
        location.setAddress(rs.getString("Address"));
        location.setLatitude(rs.getBigDecimal("Latitude"));
        location.setLongitude(rs.getBigDecimal("Longitude"));
        location.setRadiusMeters(rs.getInt("RadiusMeters"));
        location.setActive(rs.getBoolean("IsActive"));
        Timestamp createdAt = rs.getTimestamp("CreatedAt");
        Timestamp updatedAt = rs.getTimestamp("UpdatedAt");
        location.setCreatedAt(createdAt != null ? createdAt.toLocalDateTime() : null);
        location.setUpdatedAt(updatedAt != null ? updatedAt.toLocalDateTime() : null);
        return location;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
