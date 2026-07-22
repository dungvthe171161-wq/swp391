package com.hrm.automation.playwright;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import java.nio.file.Paths;

public abstract class PlaywrightTestBase {

    protected static Playwright playwright;
    protected static Browser browser;
    protected BrowserContext context;
    protected Page page;

    @BeforeAll
    static void launchBrowser() {
        Assumptions.assumeTrue(isServerRunning(), 
            "Local server is not running on port 8080. Skipping Playwright automation tests.");

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(true) // Set false nếu muốn xem trình duyệt bật lên trực quan
                .setSlowMo(100));  // Làm chậm 100ms để dễ quan sát thao tác
    }

    private static boolean isServerRunning() {
        try (java.net.Socket socket = new java.net.Socket()) {
            socket.connect(new java.net.InetSocketAddress("localhost", 8080), 1000);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions()
                .setViewportSize(1280, 720));
        
        // Bật Tracing ghi lại hình ảnh & video màn hình test
        context.tracing().start(new Tracing.StartOptions()
                .setScreenshots(true)
                .setSnapshots(true)
                .setSources(true));

        page = context.newPage();
    }

    @AfterEach
    void closeContext(TestInfo testInfo) {
        // Lưu Trace File để xem lại video thao tác nếu test bị lỗi
        String traceName = "target/playwright-traces/" + testInfo.getDisplayName().replaceAll("[^a-zA-Z0-9_-]", "_") + ".zip";
        context.tracing().stop(new Tracing.StopOptions().setPath(Paths.get(traceName)));
        if (context != null) context.close();
    }
}