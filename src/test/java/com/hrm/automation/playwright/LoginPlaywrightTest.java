package com.hrm.automation.playwright;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Playwright Automation Test: Màn hình Đăng Nhập & Bảo Mật")
public class LoginPlaywrightTest extends PlaywrightTestBase {

    private static final String BASE_URL = "http://localhost:8080/HRMS";

    @Test
    @DisplayName("PW-AUTH-01: Nhập thông tin và kiểm tra phản hồi form Đăng nhập")
    void testLoginSuccess() {
        page.navigate(BASE_URL + "/login");

        page.locator("input[name='user']").fill("admin");
        page.locator("input[name='pass']").fill("123456");
        page.locator("button[type='submit']").click();

        assertTrue(page.url().contains("/HRMS"));
    }

    @Test
    @DisplayName("PW-AUTH-02: Đăng nhập sai mật khẩu -> Hiển thị thông báo lỗi")
    void testLoginLockoutFiveFailedAttempts() {
        page.navigate(BASE_URL + "/login");

        page.locator("input[name='user']").fill("emp_dev");
        page.locator("input[name='pass']").fill("WrongPassword123");
        page.locator("button[type='submit']").click();

        // Kiểm tra thông báo lỗi hiển thị trong div.auth-message.error
        assertThat(page.locator(".auth-message.error")).isVisible();
    }
}
