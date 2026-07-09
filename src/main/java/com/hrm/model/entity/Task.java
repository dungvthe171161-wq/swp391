package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDate;

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
    private LocalDate startDate;
    private LocalDate dueDate;
    private String status;

    public Task() {
    }

    public Task(int taskId, String title, String description, Integer assignedBy,
             LocalDate startDate, LocalDate dueDate, String status) {
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

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" + "taskId=" + taskId + ", title=" + title + ", description=" + description + ", assignedBy=" + assignedBy + ", startDate=" + startDate + ", dueDate=" + dueDate + ", status=" + status + '}';
    }
}
