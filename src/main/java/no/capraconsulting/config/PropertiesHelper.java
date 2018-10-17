package no.capraconsulting.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesHelper {

    private static final Logger log = LoggerFactory.getLogger(PropertiesHelper.class);

    public static final String BASIC_AUTH_USERNAME = "basic.auth.username";
    public static final String BASIC_AUTH_PASSWORD = "basic.auth.password";
    public static final String SECURED_ENDPOINT_MESSAGE = "secured.endpoint.message";

    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(getPropertiesFromClasspath("application.properties"));
        properties.putAll(getPropertiesFromFile(new File("config_override/application.properties")));
        return properties;
    }

    private static Properties getPropertiesFromClasspath(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(PropertiesHelper.class.getClassLoader().getResourceAsStream(filename));
        } catch (NullPointerException | IOException e) {
            log.debug("{} not found on classpath.", filename);
        }
        return properties;
    }

    private static Properties getPropertiesFromFile(File file) {
        Properties properties = new Properties();
        if (file.exists()) {
            try (InputStream input = new FileInputStream(file)) {
                properties.load(input);
            } catch (Exception e) {
                log.warn("Failed to load properties from {}", file, e);
            }
        }
        return properties;
    }



    public static String getStringProperty(final Properties properties, String propertyKey, String defaultValue) {
        String property = System.getProperty(propertyKey);
        if (property == null) {
            property = properties.getProperty(propertyKey, defaultValue);
        }
        return property;
    }

    public static Integer getIntProperty(final Properties properties, String propertyKey, Integer defaultValue) {
        String property = getStringProperty(properties, propertyKey, null);
        if (property == null) {
            return defaultValue;
        }
        return Integer.valueOf(property);
    }

    public static Long getLongProperty(final Properties properties, String propertyKey, Long defaultValue) {
        String property = getStringProperty(properties, propertyKey, null);
        if (property == null) {
            return defaultValue;
        }
        return Long.valueOf(property);
    }

    public static void setLongProperty(Properties properties, String propertyKey, long value) {
        properties.setProperty(propertyKey, String.valueOf(value));
    }

    public static Boolean getBooleanProperty(final Properties properties, String propertyKey, Boolean defaultValue) {
        String property = getStringProperty(properties, propertyKey, null);
        if (property == null) {
            return defaultValue;
        }
        return Boolean.valueOf(property);
    }
}
