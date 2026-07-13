package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

public class ChatbotFaq implements Serializable {

    private static final long serialVersionUID = 1L;

    private int faqId;
    private String intent;
    private String audienceRole;
    private String question;
    private String answer;
    private String suggestions;
    private int sortOrder;
    private boolean active;
    private Integer createdBy;
    private LocalDateTime createdAt;
    private Integer updatedBy;
    private LocalDateTime updatedAt;

    public int getFaqId() {
        return faqId;
    }

    public void setFaqId(int faqId) {
        this.faqId = faqId;
    }

    public String getIntent() {
        return intent;
    }

    public void setIntent(String intent) {
        this.intent = intent;
    }

    public String getAudienceRole() {
        return audienceRole;
    }

    public void setAudienceRole(String audienceRole) {
        this.audienceRole = audienceRole;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Integer updatedBy) {
        this.updatedBy = updatedBy;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
