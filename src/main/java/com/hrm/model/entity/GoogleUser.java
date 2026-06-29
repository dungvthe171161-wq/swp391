package com.hrm.model.entity;

public class GoogleUser {

    private String googleId;
    private String email;
    private String fullName;
    private String avatarUrl;

    public GoogleUser() {
    }

    public GoogleUser(String googleId, String email, String fullName, String avatarUrl) {
        this.googleId = googleId;
        this.email = email;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }
}
