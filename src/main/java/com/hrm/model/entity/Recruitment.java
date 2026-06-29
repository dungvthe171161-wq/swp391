package com.hrm.model.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Recruitment entity – stores job postings for the HRMS system.
 * @author admin
 */
public class Recruitment implements Serializable {
    private static final long serialVersionUID = 1L;

    private int recruitmentId;
    private String Title;
    private String Description;
    private String Requirement;
    private String Location;   
    private Double Salary;
    private String Status;
    private int Applicant;
    private LocalDateTime postedDate;

    public Recruitment() {}

    public Recruitment(int recruitmentId, String Title, String Description, String Requirement, String Location, Double Salary, String Status, int Applicant, LocalDateTime postedDate) {
        this.recruitmentId = recruitmentId;
        this.Title = Title;
        this.Description = Description;
        this.Requirement = Requirement;
        this.Location = Location;
        this.Salary = Salary;
        this.Status = Status;
        this.Applicant = Applicant;
        this.postedDate = postedDate;
    }

    public int getRecruitmentId() {
        return recruitmentId;
    }

    public void setRecruitmentId(int recruitmentId) {
        this.recruitmentId = recruitmentId;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getRequirement() {
        return Requirement;
    }

    public void setRequirement(String Requirement) {
        this.Requirement = Requirement;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String Location) {
        this.Location = Location;
    }

    public Double getSalary() {
        return Salary;
    }

    public void setSalary(Double Salary) {
        this.Salary = Salary;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public int getApplicant() {
        return Applicant;
    }

    public void setApplicant(int Applicant) {
        this.Applicant = Applicant;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    @Override
    public String toString() {
        return "Recruitment{" + "recruitmentId=" + recruitmentId + ", Title=" + Title + ", Description=" + Description + ", Requirement=" + Requirement + ", Location=" + Location + ", Salary=" + Salary + ", Status=" + Status + ", Applicant=" + Applicant + ", postedDate=" + postedDate + '}';
    }

    

    
    
    
    
}