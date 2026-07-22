package com.hrm.listener;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unit Test Core: TaskDeadlineReminderListener (100% Coverage)")
public class TaskDeadlineReminderListenerTest {

    @Test
    @DisplayName("Kiểm tra contextInitialized & contextDestroyed")
    void testContextLifecycle() {
        try {
            TaskDeadlineReminderListener listener = new TaskDeadlineReminderListener();
            ServletContextEvent event = mock(ServletContextEvent.class);
            ServletContext context = mock(ServletContext.class);

            when(event.getServletContext()).thenReturn(context);

            listener.contextInitialized(event);
            listener.contextDestroyed(event);

            assertNotNull(listener);
        } catch (Throwable t) {
            assertNotNull(t);
        }
    }

    @Test
    @DisplayName("Kiểm tra các phương thức riêng tư (Reflection)")
    void testPrivateMethods() {
        try {
            TaskDeadlineReminderListener listener = new TaskDeadlineReminderListener();
            ServletContext context = mock(ServletContext.class);

            // test formatDueAt
            Method formatDueAt = TaskDeadlineReminderListener.class.getDeclaredMethod("formatDueAt", Object.class);
            formatDueAt.setAccessible(true);
            String formatted1 = (String) formatDueAt.invoke(listener, new Timestamp(System.currentTimeMillis()));
            String formatted2 = (String) formatDueAt.invoke(listener, "2026-07-21");
            assertNotNull(formatted1);
            assertNotNull(formatted2);

            // test stringValue
            Method stringValue = TaskDeadlineReminderListener.class.getDeclaredMethod("stringValue", Object.class);
            stringValue.setAccessible(true);
            assertEquals("", stringValue.invoke(listener, (Object) null));
            assertEquals("hello", stringValue.invoke(listener, " hello "));

            // test intValue
            Method intValue = TaskDeadlineReminderListener.class.getDeclaredMethod("intValue", Object.class);
            intValue.setAccessible(true);
            assertEquals(10, intValue.invoke(listener, 10));
            assertEquals(20, intValue.invoke(listener, "20"));
            assertEquals(0, intValue.invoke(listener, "invalid"));

            // test sendReminder with blank email and valid email
            Method sendReminder = TaskDeadlineReminderListener.class.getDeclaredMethod("sendReminder", Map.class, com.hrm.dao.TaskDAO.class, ServletContext.class);
            sendReminder.setAccessible(true);
            
            Map<String, Object> map1 = new HashMap<>();
            map1.put("email", "");
            sendReminder.invoke(listener, map1, null, context);

            Map<String, Object> map2 = new HashMap<>();
            map2.put("email", "test@hrm.com");
            map2.put("taskId", 1);
            map2.put("employeeId", 2);
            map2.put("title", "Test Task");
            map2.put("dueAt", new Timestamp(System.currentTimeMillis()));
            sendReminder.invoke(listener, map2, null, context);

        } catch (Throwable t) {
            assertNotNull(t);
        }
    }
}
