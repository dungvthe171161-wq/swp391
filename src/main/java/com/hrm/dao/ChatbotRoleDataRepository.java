package com.hrm.dao;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface ChatbotRoleDataRepository {

    GuestSnapshot loadGuestSnapshot(int userId) throws SQLException;

    EmployeeSnapshot loadEmployeeSnapshot(int employeeId) throws SQLException;

    ManagerSnapshot loadManagerSnapshot(int managerEmployeeId) throws SQLException;

    HrSnapshot loadHrSnapshot() throws SQLException;

    record GuestSnapshot(
            int applicationCount,
            String latestApplicationStatus,
            String currentStep,
            LocalDateTime nextInterviewAt,
            String interviewStatus,
            String offerStatus,
            LocalDate offerStartDate) {
    }

    record EmployeeSnapshot(
            int pendingLeaveCount,
            String latestLeaveStatus,
            int waitingTaskCount,
            int inProgressTaskCount,
            int completedTaskCount,
            String latestContractStatus,
            String contractType,
            LocalDate contractEndDate,
            String latestPayrollPeriod,
            String latestPayrollStatus) {
    }

    record ManagerSnapshot(
            String departmentName,
            int pendingLeaveCount,
            int waitingTaskCount,
            int inProgressTaskCount,
            int completedTaskCount) {
    }

    record HrSnapshot(
            int applicationCount,
            int screeningApplicationCount,
            int interviewApplicationCount,
            int offeredApplicationCount,
            int scheduledInterviewCount,
            int draftOfferCount,
            int sentOfferCount,
            int pendingLeaveCount,
            int pendingPayrollCount,
            int pendingContractCount) {
    }
}
