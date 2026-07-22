import com.microsoft.playwright.*;
import com.microsoft.playwright.options.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.*;

public class Example {
  public static void main(String[] args) {
    try (Playwright playwright = Playwright.create()) {
      Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
        .setHeadless(false));
      BrowserContext context = browser.newContext();
      Page page = context.newPage();
      page.navigate("http://localhost:8080/HRMS/login");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email hoặc tên đăng nhập")).click();
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email hoặc tên đăng nhập")).click();
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email hoặc tên đăng nhập")).fill("admin");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Email hoặc tên đăng nhập")).press("Tab");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mật khẩu")).fill("12345678");
      page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Mật khẩu")).press("Enter");
      page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Đăng nhập")).click();
    }
  }
}