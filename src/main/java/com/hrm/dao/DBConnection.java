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

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/hrm_db?useSSL=false&serverTimezone=Asia/Ho_Chi_Minh&allowPublicKeyRetrieval=true";
    private static final String DEFAULT_USER = "root";
    private static final String DEFAULT_PASSWORD = "";

    private static final String URL = getConfig("DB_URL", "db.url", DEFAULT_URL);
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

    private static String getConfig(String envKey, String propertyKey, String defaultValue) {
        String value = System.getenv(envKey);
        if (value == null || value.isBlank()) {
            value = System.getProperty(envKey);
        }
        if (value == null || value.isBlank()) {
            value = getProperty(propertyKey);
        }
        return value == null || value.isBlank() ? defaultValue : value.trim();
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
