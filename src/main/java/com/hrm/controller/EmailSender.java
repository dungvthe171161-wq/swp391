package com.hrm.controller;

import java.io.InputStream;
import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class EmailSender {

    public static void sendEmail(String to, String subject, String content) throws MessagingException {
        send(to, subject, content, false);
    }

    public static void sendHtmlEmail(String to, String subject, String htmlContent) throws MessagingException {
        send(to, subject, htmlContent, true);
    }

    private static void send(String to, String subject, String content, boolean html) throws MessagingException {
        Properties config = loadMailConfig();
        final String host = getConfig(config, "MAIL_HOST", "mail.smtp.host", "smtp.gmail.com");
        final String port = getConfig(config, "MAIL_PORT", "mail.smtp.port", "587");
        final String username = requireConfig(config, "MAIL_USERNAME", "mail.username");
        final String password = requireConfig(config, "MAIL_PASSWORD", "mail.password");
        final String from = getConfig(config, "MAIL_FROM", "mail.from", username);
        final String fromName = getConfig(config, "MAIL_FROM_NAME", "mail.from.name", "BetterHR");

        Properties props = new Properties();
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", getConfig(config, "MAIL_SMTP_AUTH", "mail.smtp.auth", "true"));
        props.put("mail.smtp.starttls.enable", getConfig(config, "MAIL_SMTP_STARTTLS", "mail.smtp.starttls.enable", "true"));
        props.put("mail.smtp.ssl.trust", host);

        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };

        Session session = Session.getInstance(props, auth);

        MimeMessage msg = new MimeMessage(session);
        try {
            msg.setFrom(new InternetAddress(from, fromName, "UTF-8"));
        } catch (Exception e) {
            throw new MessagingException("Invalid sender email configuration", e);
        }
        msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, false));
        msg.setSubject(subject, "UTF-8");
        if (html) {
            msg.setContent(createAlternativeContent(content));
        } else {
            msg.setText(content, "UTF-8");
        }
        Transport.send(msg);
    }

    private static Multipart createAlternativeContent(String htmlContent) throws MessagingException {
        MimeMultipart multipart = new MimeMultipart("alternative");

        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(toPlainText(htmlContent), "UTF-8");
        multipart.addBodyPart(textPart);

        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=UTF-8");
        multipart.addBodyPart(htmlPart);

        return multipart;
    }

    private static String toPlainText(String htmlContent) {
        if (htmlContent == null) {
            return "";
        }
        return htmlContent
                .replaceAll("(?is)<style[^>]*>.*?</style>", " ")
                .replaceAll("(?is)<script[^>]*>.*?</script>", " ")
                .replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("(?i)</p>", "\n\n")
                .replaceAll("(?i)</div>", "\n")
                .replaceAll("<[^>]+>", " ")
                .replace("&nbsp;", " ")
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll("\\n\\s+\\n", "\n\n")
                .trim();
    }

    private static Properties loadMailConfig() {
        Properties properties = new Properties();
        String[] resources = {"META-INF/mail.properties", "META-INF/mail.local.properties"};
        ClassLoader loader = EmailSender.class.getClassLoader();
        for (String resource : resources) {
            try (InputStream input = loader.getResourceAsStream(resource)) {
                if (input != null) {
                    properties.load(input);
                }
            } catch (Exception ignored) {
                // Missing or malformed optional mail config is handled by requireConfig.
            }
        }
        return properties;
    }

    private static String requireConfig(Properties properties, String envKey, String propertyKey) throws MessagingException {
        String value = getConfig(properties, envKey, propertyKey, "");
        if (value == null || value.isBlank()) {
            throw new MessagingException("Missing mail configuration: " + propertyKey + " or " + envKey);
        }
        return value;
    }

    private static String getConfig(Properties properties, String envKey, String propertyKey, String defaultValue) {
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) {
            value = System.getProperty(envKey);
        }
        if (value == null || value.isBlank()) {
            value = System.getProperty(propertyKey);
        }
        if (value == null || value.isBlank()) {
            value = properties.getProperty(propertyKey);
        }
        if (value == null || value.isBlank()) {
            return defaultValue;
        }
        return value.trim();
    }
}
