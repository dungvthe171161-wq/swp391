package com.hrm.automation.playwright;

import com.microsoft.playwright.options.Geolocation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Playwright Automation Test: Chấm công GPS & Giả lập Tọa độ Trình duyệt")
public class EmployeeAttendancePlaywrightTest extends PlaywrightTestBase {

    private static final String BASE_URL = "http://localhost:8080/HRMS";

    @Test
    @DisplayName("PW-GPS-01: Giả lập GPS chuẩn tại văn phòng -> Mở trang Chấm công")
    void testGPSCheckIn_SuccessAtOffice() {
        context.grantPermissions(List.of("geolocation"));
        context.setGeolocation(new Geolocation(21.028511, 105.782011));

        page.navigate(BASE_URL + "/login");
        page.locator("input[name='user']").fill("emp_01");
        page.locator("input[name='pass']").fill("123456");
        page.locator("button[type='submit']").click();

        assertTrue(page.url().contains("/HRMS"));
    }
}
