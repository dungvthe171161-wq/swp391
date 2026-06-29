package com.hrm.model.dto;

public class StatusDistribution {
    private String status;
    private int count;

    public StatusDistribution() {
    }

    public StatusDistribution(String status, int count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
