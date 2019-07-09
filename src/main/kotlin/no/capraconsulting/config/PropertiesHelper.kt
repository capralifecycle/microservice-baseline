package no.capraconsulting.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.io.*
import java.nio.charset.StandardCharsets
import java.util.Properties

object PropertiesHelper {

    private val log = LoggerFactory.getLogger(PropertiesHelper::class.java)

    val BASIC_AUTH_USERNAME = "basic.auth.username"
    val BASIC_AUTH_PASSWORD = "basic.auth.password"
    val SECURED_ENDPOINT_MESSAGE = "secured.endpoint.message"

    val properties: Properties
        get() {
            val properties = Properties()
            properties.putAll(getPropertiesFromClasspathFile("application.properties"))
            properties.putAll(getPropertiesFromOptionalClasspathFile("application-test.properties"))
            properties.putAll(getPropertiesFromFile(File("config_override/application.properties")))
            return properties
        }

    internal fun getPropertiesFromClasspathFile(filename: String): Properties {
        val properties = Properties()
        try {
            properties.load(
                InputStreamReader(
                    PropertiesHelper::class.java.classLoader.getResourceAsStream(filename)!!,
                    StandardCharsets.UTF_8
                )
            )
        } catch (e: NullPointerException) {
            throw RuntimeException("$filename not found on classpath.", e)
        } catch (e: IOException) {
            throw RuntimeException("IOException while reading $filename from classpath.", e)
        }

        return properties
    }

    private fun getPropertiesFromOptionalClasspathFile(filename: String): Properties {
        val properties = Properties()
        try {
            properties.load(
                InputStreamReader(
                    PropertiesHelper::class.java.classLoader.getResourceAsStream(filename)!!,
                    StandardCharsets.UTF_8
                )
            )
        } catch (e: NullPointerException) {
            return properties
        } catch (e: IOException) {
            throw RuntimeException("IOException while reading $filename from classpath.", e)
        }

        return properties
    }

    private fun getPropertiesFromFile(file: File): Properties {
        val properties = Properties()
        if (file.exists()) {
            try {
                InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8).use { input -> properties.load(input) }
            } catch (e: Exception) {
                log.warn("Failed to load properties from {}", file, e)
            }

        }
        return properties
    }

    @Throws(IllegalStateException::class)
    fun getRequiredStringProperty(properties: Properties, propertyKey: String): String {
        val result = getStringProperty(properties, propertyKey, null)
        return checkRequiredProperty(propertyKey, result)
    }

    @Throws(IllegalStateException::class)
    fun getRequiredIntProperty(properties: Properties, propertyKey: String): Int {
        val result = getIntProperty(properties, propertyKey, null)
        return checkRequiredProperty(propertyKey, result)
    }

    @Throws(IllegalStateException::class)
    fun getRequiredLongProperty(properties: Properties, propertyKey: String): Long {
        val result = getLongProperty(properties, propertyKey, null)
        return checkRequiredProperty(propertyKey, result)
    }

    @Throws(IllegalStateException::class)
    fun getRequiredBooleanProperty(properties: Properties, propertyKey: String): Boolean {
        val result = getBooleanProperty(properties, propertyKey, null)
        return checkRequiredProperty(propertyKey, result)
    }

    private fun <T> checkRequiredProperty(propertyKey: String, result: T?): T {
        if (result == null) {
            throw IllegalStateException(String.format("Property '%s' not found.", propertyKey))
        }
        return result
    }

    fun getStringProperty(properties: Properties, propertyKey: String, defaultValue: String?): String? {
        var property: String? = System.getProperty(propertyKey)
        if (property == null) {
            property = properties.getProperty(propertyKey, defaultValue)
        }
        return property
    }

    fun getIntProperty(properties: Properties, propertyKey: String, defaultValue: Int?): Int? {
        val property = getStringProperty(properties, propertyKey, null) ?: return defaultValue
        return Integer.valueOf(property)
    }

    fun getLongProperty(properties: Properties, propertyKey: String, defaultValue: Long?): Long? {
        val property = getStringProperty(properties, propertyKey, null) ?: return defaultValue
        return java.lang.Long.valueOf(property)
    }

    fun getBooleanProperty(properties: Properties, propertyKey: String, defaultValue: Boolean?): Boolean? {
        val property = getStringProperty(properties, propertyKey, null) ?: return defaultValue
        return java.lang.Boolean.valueOf(property)
    }
}
