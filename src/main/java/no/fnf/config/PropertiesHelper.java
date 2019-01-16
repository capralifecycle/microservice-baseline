package no.fnf.config;

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
        properties.putAll(getPropertiesFromClasspathFile("application.properties"));
        properties.putAll(getPropertiesFromOptionalClasspathFile("application-test.properties"));
        properties.putAll(getPropertiesFromFile(new File("config_override/application.properties")));
        return properties;
    }

    static Properties getPropertiesFromClasspathFile(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(PropertiesHelper.class.getClassLoader().getResourceAsStream(filename));
        } catch (NullPointerException e) {
            throw new RuntimeException(filename + " not found on classpath.", e);
        } catch (IOException e) {
            throw new RuntimeException("IOException while reading " + filename + " from classpath.", e);
        }
        return properties;
    }

    private static Properties getPropertiesFromOptionalClasspathFile(String filename) {
        Properties properties = new Properties();
        try {
            properties.load(PropertiesHelper.class.getClassLoader().getResourceAsStream(filename));
        } catch (NullPointerException e) {
            return properties;
        } catch (IOException e) {
            throw new RuntimeException("IOException while reading " + filename + " from classpath.", e);
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

    public static String getRequiredStringProperty(final Properties properties, String propertyKey) throws IllegalStateException {
        String result = getStringProperty(properties, propertyKey, null);
        return checkRequiredProperty(propertyKey, result);
    }

    public static Integer getRequiredIntProperty(final Properties properties, String propertyKey) throws IllegalStateException {
        Integer result = getIntProperty(properties, propertyKey, null);
        return checkRequiredProperty(propertyKey, result);
    }

    public static Long getRequiredLongProperty(final Properties properties, String propertyKey) throws IllegalStateException {
        Long result = getLongProperty(properties, propertyKey, null);
        return checkRequiredProperty(propertyKey, result);
    }

    public static Boolean getRequiredBooleanProperty(final Properties properties, String propertyKey) throws IllegalStateException {
        Boolean result = getBooleanProperty(properties, propertyKey, null);
        return checkRequiredProperty(propertyKey, result);
    }

    private static <T> T checkRequiredProperty(String propertyKey, T result) {
        if (result == null) {
            throw new IllegalStateException(String.format("Property '%s' not found.", propertyKey));
        }
        return result;
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

    public static Boolean getBooleanProperty(final Properties properties, String propertyKey, Boolean defaultValue) {
        String property = getStringProperty(properties, propertyKey, null);
        if (property == null) {
            return defaultValue;
        }
        return Boolean.valueOf(property);
    }
}
