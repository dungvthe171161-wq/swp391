package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 *
 * @author admin
 */
public class Task implements Serializable {

    private static final long serialVersionUID = 1L;

    private int taskId;
    private String title;
    private String description;
    private Integer assignedBy;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private String status;
    private String priority;
    private String attachmentPath;
    private String assignmentStatus;
    private String feedback;
    private LocalDateTime submittedAt;
    private LocalDateTime approvedAt;
    private String dueReminder;

    public Task() {
    }

    public Task(int taskId, String title, String description, Integer assignedBy,
             LocalDateTime startDate, LocalDateTime dueDate, String status) {
        this.taskId = taskId;
        this.title = title;
        this.description = description;
        this.assignedBy = assignedBy;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.status = status;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(int assignedBy) {
        this.assignedBy = assignedBy;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getAssignmentStatus() {
        return assignmentStatus;
    }

    public void setAssignmentStatus(String assignmentStatus) {
        this.assignmentStatus = assignmentStatus;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getApprovedAt() {
        return approvedAt;
    }

    public void setApprovedAt(LocalDateTime approvedAt) {
        this.approvedAt = approvedAt;
    }

    public String getDueReminder() {
        return dueReminder;
    }

    public void setDueReminder(String dueReminder) {
        this.dueReminder = dueReminder;
    }

    @Override
    public String toString() {
        return "Task{" + "taskId=" + taskId + ", title=" + title + ", description=" + description + ", assignedBy=" + assignedBy + ", startDate=" + startDate + ", dueDate=" + dueDate + ", status=" + status + '}';
    }
}
