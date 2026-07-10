package com.hrm.model.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OfficeLocation {
    private int officeLocationId;
    private String locationCode;
    private String locationName;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private int radiusMeters;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OfficeLocation() {
    }

    public OfficeLocation(int officeLocationId, String locationCode, String locationName, String address,
                          BigDecimal latitude, BigDecimal longitude, int radiusMeters, boolean active,
                          LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.officeLocationId = officeLocationId;
        this.locationCode = locationCode;
        this.locationName = locationName;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radiusMeters = radiusMeters;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getOfficeLocationId() { return officeLocationId; }
    public void setOfficeLocationId(int officeLocationId) { this.officeLocationId = officeLocationId; }
    public String getLocationCode() { return locationCode; }
    public void setLocationCode(String locationCode) { this.locationCode = locationCode; }
    public String getLocationName() { return locationName; }
    public void setLocationName(String locationName) { this.locationName = locationName; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public BigDecimal getLatitude() { return latitude; }
    public void setLatitude(BigDecimal latitude) { this.latitude = latitude; }
    public BigDecimal getLongitude() { return longitude; }
    public void setLongitude(BigDecimal longitude) { this.longitude = longitude; }
    public int getRadiusMeters() { return radiusMeters; }
    public void setRadiusMeters(int radiusMeters) { this.radiusMeters = radiusMeters; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
