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
                "Tin vui từ BetterHR",
                "CV của bạn đã vượt qua vòng sàng lọc",
                "Hồ sơ của bạn phù hợp với tiêu chí tuyển dụng ban đầu. Đội ngũ BetterHR sẽ gửi lịch phỏng vấn trong thời gian sớm nhất.",
                "Vượt qua sàng lọc",
                GREEN_ACCENT,
                "Bước tiếp theo",
                "Vui lòng theo dõi email và điện thoại để nhận lịch phỏng vấn. Bạn có thể chuẩn bị trước thông tin kinh nghiệm, dự án nổi bật và các câu hỏi muốn trao đổi với nhà tuyển dụng.",
                candidateName,
                recruitmentTitle
        );
    }

    public static String cvRejected(String candidateName, String recruitmentTitle) {
        return applicationStatusEmail(
                "Cập nhật hồ sơ ứng tuyển",
                "Cảm ơn bạn đã quan tâm đến BetterHR",
                "Sau khi xem xét, hồ sơ hiện tại của bạn chưa thật sự phù hợp với yêu cầu của vị trí này. BetterHR rất trân trọng thời gian và sự quan tâm của bạn.",
                "Chưa phù hợp",
                RED,
                "Lời nhắn từ BetterHR",
                "Bạn vẫn có thể tiếp tục theo dõi các vị trí tuyển dụng khác trên BetterHR. Chúng tôi hy vọng sẽ có cơ hội đồng hành cùng bạn trong những đợt tuyển dụng tiếp theo.",
                candidateName,
                recruitmentTitle
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
        String safeName = escape(firstNonBlank(candidateName, "Ứng viên"));
        String safeTitle = escape(firstNonBlank(recruitmentTitle, "Vị trí ứng tuyển"));

        return """
                <!doctype html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>BetterHR</title>
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
                                                        <div style="display:inline-block; width:44px; height:44px; border-radius:50%%; background:%s; color:%s; text-align:center; line-height:44px; font-weight:700; font-size:20px; margin-right:12px;">B</div>
                                                        <span style="color:%s; font-size:22px; font-weight:700; vertical-align:middle;">BetterHR</span>
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
                                            <p style="margin:0 0 18px; font-size:16px; line-height:1.7; color:%s;">Xin chào <strong>%s</strong>,</p>
                                            <p style="margin:0 0 24px; font-size:16px; line-height:1.7; color:%s;">%s</p>
                                            <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" style="background:%s; border-radius:14px; border:1px solid %s; margin:0 0 24px;">
                                                <tr>
                                                    <td style="padding:18px 20px; border-bottom:1px solid %s;">
                                                        <div style="font-size:12px; color:%s; text-transform:uppercase; letter-spacing:0.08em; font-weight:700;">Vị trí</div>
                                                        <div style="margin-top:6px; font-size:16px; color:%s; font-weight:700;">%s</div>
                                                    </td>
                                                </tr>
                                                <tr>
                                                    <td style="padding:18px 20px;">
                                                        <div style="font-size:12px; color:%s; text-transform:uppercase; letter-spacing:0.08em; font-weight:700;">Trạng thái hồ sơ</div>
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
                                            <span style="display:inline-block; background:%s; color:%s; text-decoration:none; font-size:15px; font-weight:700; padding:13px 22px; border-radius:999px;">BetterHR đồng hành cùng bạn</span>
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="background:%s; padding:22px 32px; color:%s; font-size:13px; line-height:1.6;">
                                            Email này được gửi tự động từ hệ thống BetterHR. Vui lòng không trả lời trực tiếp email này.
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
