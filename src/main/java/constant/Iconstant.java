/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package constant;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Iconstant {

    public static final String GOOGLE_CLIENT_ID = getConfig("GOOGLE_CLIENT_ID");

    public static final String GOOGLE_CLIENT_SECRET = getConfig("GOOGLE_CLIENT_SECRET");

    public static final String GOOGLE_REDIRECT_URI = getConfig("GOOGLE_REDIRECT_URI");

    public static final String GOOGLE_GRANT_TYPE = "authorization_code";

    public static final String GOOGLE_LINK_GET_TOKEN = "https://oauth2.googleapis.com/token";

    public static final String GOOGLE_LINK_GET_USER_INFO = "https://openidconnect.googleapis.com/v1/userinfo?access_token=";

    public static final String GOOGLE_LINK_TOKEN_INFO = "https://oauth2.googleapis.com/tokeninfo?id_token=";

    private static String getConfig(String key) {
        return getConfig(key, "");
    }

    private static String getConfig(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.isBlank()) {
            value = System.getProperty(key);
        }
        if (value == null || value.isBlank()) {
            value = getProperty(key);
        }
        if (value == null || value.isBlank()) {
            value = getProperty(toPropertyKey(key));
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }

    private static String getProperty(String key) {
        Properties properties = new Properties();
        try (InputStream input = Iconstant.class.getClassLoader().getResourceAsStream("META-INF/google.properties")) {
            if (input == null) {
                return "";
            }
            properties.load(input);
            return properties.getProperty(key, "");
        } catch (IOException e) {
            return "";
        }
    }

    private static String toPropertyKey(String key) {
        return key.toLowerCase().replace('_', '.');
    }
}
