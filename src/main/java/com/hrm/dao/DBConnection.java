package com.hrm.dao;

import jakarta.servlet.annotation.WebListener;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class DBConnection {

    private static final String DEFAULT_HOST = "localhost";
    private static final String DEFAULT_PORT = "3306";
    private static final String DEFAULT_NAME = "hrm_db";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String URL = resolveUrl();
    private static final String USER = getConfig("DB_USER", "db.user", DEFAULT_USER);
    private static final String PASSWORD = getConfig("DB_PASSWORD", "db.password", DEFAULT_PASSWORD);


    /**
     * Phương thức chính để lấy kết nối JDBC.
     */
    public static Connection getJDBCConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, "Không tìm thấy Driver JDBC!", ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, "Lỗi kết nối database!", ex);
        }
        return null;
    }

    private static String resolveUrl() {
        String explicitUrl = getEnvironmentOrSystemProperty("DB_URL");
        if (explicitUrl != null) {
            return explicitUrl;
        }

        if (hasComponentConfiguration()) {
            return buildUrl(
                    getConfig("DB_HOST", "db.host", DEFAULT_HOST),
                    getConfig("DB_PORT", "db.port", DEFAULT_PORT),
                    getConfig("DB_NAME", "db.name", DEFAULT_NAME));
        }

        String propertyUrl = getProperty("db.url");
        return propertyUrl == null || propertyUrl.isBlank()
                ? buildUrl(DEFAULT_HOST, DEFAULT_PORT, DEFAULT_NAME)
                : propertyUrl.trim();
    }

    private static boolean hasComponentConfiguration() {
        return getEnvironmentOrSystemProperty("DB_HOST") != null
                || getEnvironmentOrSystemProperty("DB_PORT") != null
                || getEnvironmentOrSystemProperty("DB_NAME") != null
                || !getProperty("db.host").isBlank()
                || !getProperty("db.port").isBlank()
                || !getProperty("db.name").isBlank();
    }

    private static String buildUrl(String host, String port, String databaseName) {
        return "jdbc:mysql://" + host + ":" + port + "/" + databaseName
                + "?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true";
    }

    private static String getConfig(String envKey, String propertyKey, String defaultValue) {
        String value = getEnvironmentOrSystemProperty(envKey);
        if (value == null || value.isBlank()) {
            value = getProperty(propertyKey);
        }
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    private static String getEnvironmentOrSystemProperty(String key) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        return value == null || value.isBlank() ? null : value.trim();
    }

    private static String getProperty(String key) {
        Properties properties = new Properties();
        String[] resources = {"META-INF/db.local.properties", "META-INF/db.properties"};
        for (String resource : resources) {
            try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream(resource)) {
                if (input == null) {
                    continue;
                }
                properties.clear();
                properties.load(input);
                String value = properties.getProperty(key, "");
                if (value != null && !value.isBlank()) {
                    return value;
                }
            } catch (IOException ex) {
                Logger.getLogger(DBConnection.class.getName()).log(Level.WARNING, "Không đọc được cấu hình database", ex);
            }
        }
        return "";
    }

    /**
     * Phương thức tiện ích được DAO gọi (giống như alias).
     */
    public static Connection getConnection() {
        return getJDBCConnection();
    }

    public static boolean canConnect() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, "Không kiểm tra được kết nối database!", ex);
            return false;
        }
    }

    /**
     * Dùng để test riêng file kết nối này.
     */
    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null) {
                System.out.println("Check: Connection created successfully.");
            } else {
                System.out.println("Check: Connection not created.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
