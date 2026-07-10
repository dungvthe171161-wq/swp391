package com.hrm.listener;

import com.hrm.controller.EmailSender;
import com.hrm.dao.TaskDAO;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class TaskDeadlineReminderListener implements ServletContextListener {

    private static final int REMINDER_HOURS = 12;
    private static final long INITIAL_DELAY_MINUTES = 1;
    private static final long CHECK_INTERVAL_MINUTES = 15;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        ServletContext context = event.getServletContext();
        TaskDAO taskDAO = new TaskDAO();
        taskDAO.ensureDeadlineReminderColumn();

        scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, "task-deadline-reminder");
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() -> sendDeadlineReminders(context, taskDAO),
                INITIAL_DELAY_MINUTES, CHECK_INTERVAL_MINUTES, TimeUnit.MINUTES);
        context.log("Task deadline email reminder started.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        if (scheduler != null) {
            scheduler.shutdownNow();
        }
    }

    private void sendDeadlineReminders(ServletContext context, TaskDAO taskDAO) {
        try {
            List<Map<String, Object>> candidates = taskDAO.findDeadlineReminderCandidates(REMINDER_HOURS);
            for (Map<String, Object> candidate : candidates) {
                sendReminder(candidate, taskDAO, context);
            }
        } catch (Exception ex) {
            context.log("Cannot scan task deadline reminders.", ex);
        }
    }

    private void sendReminder(Map<String, Object> candidate, TaskDAO taskDAO, ServletContext context) {
        String email = stringValue(candidate.get("email"));
        if (email.isBlank()) {
            return;
        }

        int taskId = intValue(candidate.get("taskId"));
        int employeeId = intValue(candidate.get("employeeId"));
        String title = stringValue(candidate.get("title"));
        String dueAt = formatDueAt(candidate.get("dueAt"));
        String subject = "BetterHR - Nhắc deadline công việc";
        String content = "Công việc \"" + title + "\" sẽ đến hạn trong " + REMINDER_HOURS + " giờ.\n"
                + "Deadline: " + dueAt + "\n"
                + "Vui lòng đăng nhập BetterHR để cập nhật tiến độ hoặc nộp kết quả.";

        try {
            EmailSender.sendEmail(email, subject, content);
            taskDAO.markDeadlineReminderSent(taskId, employeeId);
        } catch (Exception ex) {
            context.log("Cannot send deadline reminder for task " + taskId + " to employee " + employeeId, ex);
        }
    }

    private String formatDueAt(Object value) {
        if (value instanceof Timestamp timestamp) {
            return timestamp.toLocalDateTime().format(DATE_TIME_FORMAT);
        }
        return stringValue(value);
    }

    private String stringValue(Object value) {
        return value == null ? "" : String.valueOf(value).trim();
    }

    private int intValue(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }
        try {
            return Integer.parseInt(stringValue(value));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }
}
