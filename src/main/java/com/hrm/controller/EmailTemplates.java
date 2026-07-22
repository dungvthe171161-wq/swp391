package com.hrm.controller;

public final class EmailTemplates {

    // Palette mirrored from src/color/Stabuck.md.
    private static final String STARBUCKS_GREEN = "#006241";
    private static final String GREEN_ACCENT = "#00754A";
    private static final String HOUSE_GREEN = "#1E3932";
    private static final String GREEN_LIGHT = "#d4e9e2";
    private static final String NEUTRAL_WARM = "#f2f0eb";
    private static final String CERAMIC = "#edebe9";
    private static final String WHITE = "#ffffff";
    private static final String TEXT_BLACK = "rgba(0,0,0,0.87)";
    private static final String TEXT_SOFT = "rgba(0,0,0,0.58)";
    private static final String RED = "#c82014";

    private EmailTemplates() {
    }

    public static String cvScreeningPassed(String candidateName, String recruitmentTitle) {
        return applicationStatusEmail(
                "Tin vui tá»« WorkMate",
                "CV cá»§a báº¡n Ä‘Ã£ vÆ°á»£t qua vÃ²ng sÃ ng lá»c",
                "Há»“ sÆ¡ cá»§a báº¡n phÃ¹ há»£p vá»›i tiÃªu chÃ­ tuyá»ƒn dá»¥ng ban Ä‘áº§u. Äá»™i ngÅ© WorkMate sáº½ gá»­i lá»‹ch phá»ng váº¥n trong thá»i gian sá»›m nháº¥t.",
                "VÆ°á»£t qua sÃ ng lá»c",
                GREEN_ACCENT,
                "BÆ°á»›c tiáº¿p theo",
                "Vui lÃ²ng theo dÃµi email vÃ  Ä‘iá»‡n thoáº¡i Ä‘á»ƒ nháº­n lá»‹ch phá»ng váº¥n. Báº¡n cÃ³ thá»ƒ chuáº©n bá»‹ trÆ°á»›c thÃ´ng tin kinh nghiá»‡m, dá»± Ã¡n ná»•i báº­t vÃ  cÃ¡c cÃ¢u há»i muá»‘n trao Ä‘á»•i vá»›i nhÃ  tuyá»ƒn dá»¥ng.",
                candidateName,
                recruitmentTitle
        );
    }

    public static String cvRejected(String candidateName, String recruitmentTitle) {
        return applicationStatusEmail(
                "Cáº­p nháº­t há»“ sÆ¡ á»©ng tuyá»ƒn",
                "Cáº£m Æ¡n báº¡n Ä‘Ã£ quan tÃ¢m Ä‘áº¿n WorkMate",
                "Sau khi xem xÃ©t, há»“ sÆ¡ hiá»‡n táº¡i cá»§a báº¡n chÆ°a tháº­t sá»± phÃ¹ há»£p vá»›i yÃªu cáº§u cá»§a vá»‹ trÃ­ nÃ y. WorkMate ráº¥t trÃ¢n trá»ng thá»i gian vÃ  sá»± quan tÃ¢m cá»§a báº¡n.",
                "ChÆ°a phÃ¹ há»£p",
                RED,
                "Lá»i nháº¯n tá»« WorkMate",
                "Báº¡n váº«n cÃ³ thá»ƒ tiáº¿p tá»¥c theo dÃµi cÃ¡c vá»‹ trÃ­ tuyá»ƒn dá»¥ng khÃ¡c trÃªn WorkMate. ChÃºng tÃ´i hy vá»ng sáº½ cÃ³ cÆ¡ há»™i Ä‘á»“ng hÃ nh cÃ¹ng báº¡n trong nhá»¯ng Ä‘á»£t tuyá»ƒn dá»¥ng tiáº¿p theo.",
                candidateName,
                recruitmentTitle
        );
    }

    public static String taskAssigned(String employeeName, String taskTitle, String description,
                                      String startDate, String dueDate, String priority, String taskUrl) {
        String priorityLabel = switch (firstNonBlank(priority, "Normal")) {
            case "High" -> "Cao";
            case "Low" -> "Tháº¥p";
            default -> "BÃ¬nh thÆ°á»ng";
        };
        String priorityColor = "High".equals(priority) ? RED
                : "Low".equals(priority) ? GREEN_ACCENT : "#9a6700";
        return taskEmail(
                "CÃ´ng viá»‡c má»›i",
                "Báº¡n vá»«a Ä‘Æ°á»£c giao má»™t cÃ´ng viá»‡c má»›i",
                "Quáº£n lÃ½ Ä‘Ã£ giao cÃ´ng viá»‡c má»›i cho báº¡n trÃªn WorkMate. HÃ£y xem thÃ´ng tin vÃ  chá»§ Ä‘á»™ng cáº­p nháº­t tiáº¿n Ä‘á»™ Ä‘Ãºng háº¡n.",
                employeeName, taskTitle, startDate, dueDate,
                "Má»©c Ä‘á»™ Æ°u tiÃªn", priorityLabel, priorityColor,
                "Ná»™i dung cÃ´ng viá»‡c", description, "Má»Ÿ cÃ´ng viá»‡c", taskUrl
        );
    }

    public static String taskDeadlineReminder(String employeeName, String taskTitle,
                                               String dueDate, int hoursRemaining, String taskUrl) {
        return taskEmail(
                "Nháº¯c deadline",
                "CÃ´ng viá»‡c sáº¯p Ä‘áº¿n háº¡n",
                "CÃ´ng viá»‡c dÆ°á»›i Ä‘Ã¢y sáº¯p Ä‘áº¿n deadline. Vui lÃ²ng kiá»ƒm tra tiáº¿n Ä‘á»™ vÃ  ná»™p káº¿t quáº£ trÆ°á»›c thá»i gian quy Ä‘á»‹nh.",
                employeeName, taskTitle, null, dueDate,
                "Thá»i gian cÃ²n láº¡i", "Khoáº£ng " + hoursRemaining + " giá»", "#b45309",
                "Viá»‡c cáº§n lÃ m", "ÄÄƒng nháº­p WorkMate Ä‘á»ƒ cáº­p nháº­t tráº¡ng thÃ¡i hoáº·c ná»™p káº¿t quáº£ cÃ´ng viá»‡c.",
                "Kiá»ƒm tra cÃ´ng viá»‡c", taskUrl
        );
    }

    public static String attendanceLogged(String employeeName, String actionType, String timeString) {
        String actionLabel = "checkIn".equals(actionType) ? "VÃ o ca (Check-in)" : "Ra ca (Check-out)";
        String statusColor = "checkIn".equals(actionType) ? GREEN_ACCENT : "#b45309";
        
        return applicationStatusEmail(
                "ThÃ´ng bÃ¡o cháº¥m cÃ´ng",
                "Cháº¥m cÃ´ng thÃ nh cÃ´ng",
                "Há»‡ thá»‘ng Ä‘Ã£ ghi nháº­n dá»¯ liá»‡u cháº¥m cÃ´ng cá»§a báº¡n trÃªn WorkMate.",
                actionLabel,
                statusColor,
                "Chi tiáº¿t ghi nháº­n",
                "Thá»i gian: " + timeString + "<br>PhÆ°Æ¡ng thá»©c: Äá»‹nh vá»‹ GPS",
                employeeName,
                "Báº£ng cháº¥m cÃ´ng hÃ ng ngÃ y"
        );
    }

    private static String taskEmail(String eyebrow, String headline, String lead,
                                    String employeeName, String taskTitle, String startDate, String dueDate,
                                    String badgeLabel, String badgeValue, String badgeColor,
                                    String noteTitle, String noteBody, String actionText, String taskUrl) {
        String safeName = escape(firstNonBlank(employeeName, "NhÃ¢n viÃªn WorkMate"));
        String safeTaskTitle = escape(firstNonBlank(taskTitle, "CÃ´ng viá»‡c"));
        String safeStartDate = escape(firstNonBlank(startDate, "Theo káº¿ hoáº¡ch cá»§a quáº£n lÃ½"));
        String safeDueDate = escape(firstNonBlank(dueDate, "ChÆ°a xÃ¡c Ä‘á»‹nh"));
        String safeNote = escape(firstNonBlank(noteBody, "Xem chi tiáº¿t cÃ´ng viá»‡c trÃªn WorkMate"))
                .replace("\r\n", "<br>").replace("\n", "<br>");
        String safeUrl = escape(firstNonBlank(taskUrl, "#"));

        return """
                <!doctype html>
                <html lang="vi">
                <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"><title>WorkMate</title></head>
                <body style="margin:0;padding:0;background:%s;font-family:Arial,Helvetica,sans-serif;color:%s;">
                    <div style="display:none;max-height:0;overflow:hidden;opacity:0;">%s - %s</div>
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:%s;padding:32px 12px;">
                        <tr><td align="center">
                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:640px;background:%s;border-radius:16px;overflow:hidden;box-shadow:0 8px 24px rgba(0,0,0,0.10);">
                                <tr><td style="background:%s;padding:28px 32px;">
                                    <div style="color:%s;font-size:22px;font-weight:700;"><span style="display:inline-block;width:42px;height:42px;line-height:42px;text-align:center;border-radius:50%%;background:%s;margin-right:10px;">W</span>WorkMate</div>
                                    <div style="margin-top:22px;color:rgba(255,255,255,0.72);font-size:12px;font-weight:700;text-transform:uppercase;letter-spacing:0.08em;">%s</div>
                                    <h1 style="margin:8px 0 0;color:%s;font-size:29px;line-height:1.25;">%s</h1>
                                </td></tr>
                                <tr><td style="padding:32px;">
                                    <p style="margin:0 0 16px;font-size:16px;line-height:1.7;">Xin chÃ o <strong>%s</strong>,</p>
                                    <p style="margin:0 0 24px;font-size:16px;line-height:1.7;color:%s;">%s</p>
                                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:%s;border:1px solid %s;border-radius:12px;overflow:hidden;">
                                        <tr><td colspan="2" style="padding:20px;border-bottom:1px solid %s;">
                                            <div style="font-size:12px;color:%s;text-transform:uppercase;font-weight:700;">CÃ´ng viá»‡c</div>
                                            <div style="margin-top:7px;font-size:19px;line-height:1.4;color:%s;font-weight:700;">%s</div>
                                        </td></tr>
                                        <tr>
                                            <td width="50%%" style="padding:18px 20px;border-right:1px solid %s;"><div style="font-size:12px;color:%s;text-transform:uppercase;font-weight:700;">Báº¯t Ä‘áº§u</div><div style="margin-top:6px;font-weight:700;">%s</div></td>
                                            <td width="50%%" style="padding:18px 20px;"><div style="font-size:12px;color:%s;text-transform:uppercase;font-weight:700;">Deadline</div><div style="margin-top:6px;font-weight:700;color:%s;">%s</div></td>
                                        </tr>
                                    </table>
                                    <div style="margin:18px 0 24px;"><span style="font-size:12px;color:%s;text-transform:uppercase;font-weight:700;">%s</span><br><span style="display:inline-block;margin-top:8px;padding:8px 13px;border-radius:999px;background:%s;color:%s;font-size:14px;font-weight:700;">%s</span></div>
                                    <div style="border-left:4px solid %s;padding:3px 0 3px 16px;margin-bottom:26px;"><h2 style="margin:0 0 8px;font-size:17px;color:%s;">%s</h2><p style="margin:0;font-size:15px;line-height:1.7;color:%s;">%s</p></div>
                                    <a href="%s" style="display:inline-block;background:%s;color:%s;text-decoration:none;font-size:15px;font-weight:700;padding:13px 22px;border-radius:999px;">%s</a>
                                </td></tr>
                                <tr><td style="background:%s;padding:20px 32px;color:rgba(255,255,255,0.70);font-size:13px;line-height:1.6;">Email nÃ y Ä‘Æ°á»£c gá»­i tá»± Ä‘á»™ng tá»« há»‡ thá»‘ng WorkMate. Vui lÃ²ng khÃ´ng tráº£ lá»i trá»±c tiáº¿p email nÃ y.</td></tr>
                            </table>
                        </td></tr>
                    </table>
                </body></html>
                """.formatted(
                NEUTRAL_WARM, TEXT_BLACK, escape(eyebrow), safeTaskTitle,
                NEUTRAL_WARM, WHITE, HOUSE_GREEN, WHITE, GREEN_ACCENT,
                escape(eyebrow), WHITE, escape(headline), safeName, TEXT_SOFT, escape(lead),
                NEUTRAL_WARM, CERAMIC, CERAMIC, TEXT_SOFT, STARBUCKS_GREEN, safeTaskTitle,
                CERAMIC, TEXT_SOFT, safeStartDate, TEXT_SOFT, badgeColor, safeDueDate,
                TEXT_SOFT, escape(badgeLabel), GREEN_LIGHT, badgeColor, escape(badgeValue),
                badgeColor, STARBUCKS_GREEN, escape(noteTitle), TEXT_SOFT, safeNote,
                safeUrl, GREEN_ACCENT, WHITE, escape(actionText), HOUSE_GREEN
        );
    }
    private static String applicationStatusEmail(String eyebrow,
                                                 String headline,
                                                 String lead,
                                                 String status,
                                                 String statusColor,
                                                 String sectionTitle,
                                                 String sectionBody,
                                                 String candidateName,
                                                 String recruitmentTitle) {
        String safeName = escape(firstNonBlank(candidateName, "á»¨ng viÃªn"));
        String safeTitle = escape(firstNonBlank(recruitmentTitle, "Vá»‹ trÃ­ á»©ng tuyá»ƒn"));

        return """
                <!doctype html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>WorkMate</title>
                </head>
                <body style="margin:0; padding:0; background:%s; font-family:Arial, Helvetica, sans-serif; color:%s;">
                    <div style="display:none; max-height:0; overflow:hidden; opacity:0;">%s - %s</div>
                    <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:%s; margin:0; padding:32px 12px;">
                        <tr>
                            <td align="center">
                                <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="max-width:640px; background:%s; border-radius:18px; overflow:hidden; box-shadow:0 8px 24px rgba(0,0,0,0.10);">
                                    <tr>
                                        <td style="background:%s; padding:28px 32px;">
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0">
                                                <tr>
                                                    <td style="vertical-align:middle;">
                                                        <div style="display:inline-block; width:44px; height:44px; border-radius:50%%; background:%s; color:%s; text-align:center; line-height:44px; font-weight:700; font-size:20px; margin-right:12px;">W</div>
                                                        <span style="color:%s; font-size:22px; font-weight:700; vertical-align:middle;">WorkMate</span>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding-top:20px;">
                                                        <div style="color:rgba(255,255,255,0.70); font-size:13px; font-weight:700; letter-spacing:0.08em; text-transform:uppercase;">%s</div>
                                                        <h1 style="margin:8px 0 0; color:%s; font-size:30px; line-height:1.25; font-weight:700;">%s</h1>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding:32px;">
                                            <p style="margin:0 0 18px; font-size:16px; line-height:1.7; color:%s;">Xin chÃ o <strong>%s</strong>,</p>
                                            <p style="margin:0 0 24px; font-size:16px; line-height:1.7; color:%s;">%s</p>
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:%s; border-radius:14px; border:1px solid %s; margin:0 0 24px;">
                                                <tr>
                                                    <td style="padding:18px 20px; border-bottom:1px solid %s;">
                                                        <div style="font-size:12px; color:%s; text-transform:uppercase; letter-spacing:0.08em; font-weight:700;">Vá»‹ trÃ­</div>
                                                        <div style="margin-top:6px; font-size:16px; color:%s; font-weight:700;">%s</div>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:18px 20px;">
                                                        <div style="font-size:12px; color:%s; text-transform:uppercase; letter-spacing:0.08em; font-weight:700;">Tráº¡ng thÃ¡i há»“ sÆ¡</div>
                                                        <div style="margin-top:8px;">
                                                            <span style="display:inline-block; padding:8px 14px; border-radius:999px; background:%s; color:%s; font-size:14px; font-weight:700;">%s</span>
                                                        </div>
                                                    </td>
                                                </tr>
                                            </table>
                                            <div style="border-left:4px solid %s; padding:4px 0 4px 16px; margin-bottom:26px;">
                                                <h2 style="margin:0 0 8px; font-size:18px; line-height:1.4; color:%s;">%s</h2>
                                                <p style="margin:0; font-size:15px; line-height:1.7; color:%s;">%s</p>
                                            </div>
                                            <span style="display:inline-block; background:%s; color:%s; text-decoration:none; font-size:15px; font-weight:700; padding:13px 22px; border-radius:999px;">WorkMate Ä‘á»“ng hÃ nh cÃ¹ng báº¡n</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background:%s; padding:22px 32px; color:%s; font-size:13px; line-height:1.6;">
                                            Email nÃ y Ä‘Æ°á»£c gá»­i tá»± Ä‘á»™ng tá»« há»‡ thá»‘ng WorkMate. Vui lÃ²ng khÃ´ng tráº£ lá»i trá»±c tiáº¿p email nÃ y.
                                        </td>
                                    </tr>
                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """.formatted(
                NEUTRAL_WARM,
                TEXT_BLACK,
                escape(eyebrow),
                escape(headline),
                NEUTRAL_WARM,
                WHITE,
                HOUSE_GREEN,
                GREEN_ACCENT,
                WHITE,
                WHITE,
                escape(eyebrow),
                WHITE,
                escape(headline),
                TEXT_BLACK,
                safeName,
                TEXT_BLACK,
                escape(lead),
                NEUTRAL_WARM,
                CERAMIC,
                CERAMIC,
                TEXT_SOFT,
                STARBUCKS_GREEN,
                safeTitle,
                TEXT_SOFT,
                GREEN_LIGHT,
                statusColor,
                escape(status),
                statusColor,
                STARBUCKS_GREEN,
                escape(sectionTitle),
                TEXT_SOFT,
                escape(sectionBody),
                GREEN_ACCENT,
                WHITE,
                HOUSE_GREEN,
                "rgba(255,255,255,0.70)"
        );
    }

    private static String firstNonBlank(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
