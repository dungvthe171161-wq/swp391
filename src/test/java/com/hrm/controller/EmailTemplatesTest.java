package com.hrm.controller;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class EmailTemplatesTest {

    @Test
    void taskEmailsRenderBrandedHtmlAndEscapeUserContent() {
        String assigned = EmailTemplates.taskAssigned(
                "Nguyễn Văn A", "Hoàn thiện <b>báo cáo</b>", "Dòng 1\nDòng 2",
                "16/07/2026 09:00", "17/07/2026 17:00", "High",
                "http://localhost:8080/employee/tasks");
        String reminder = EmailTemplates.taskDeadlineReminder(
                "Nguyễn Văn A", "Hoàn thiện báo cáo", "17/07/2026 17:00", 12,
                "http://localhost:8080/employee/tasks");

        assertTrue(assigned.contains("<!doctype html>"));
        assertTrue(assigned.contains("BetterHR"));
        assertTrue(assigned.contains("Mở công việc"));
        assertTrue(assigned.contains("&lt;b&gt;báo cáo&lt;/b&gt;"));
        assertTrue(assigned.contains("Dòng 1<br>Dòng 2"));
        assertTrue(reminder.contains("Công việc sắp đến hạn"));
        assertTrue(reminder.contains("Khoảng 12 giờ"));
    }
}