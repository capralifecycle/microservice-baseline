package no.capraconsulting.config

import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.Properties

object PropertiesHelper {
    private val log = LoggerFactory.getLogger(PropertiesHelper::class.java)

    const val BASIC_AUTH_USERNAME = "basic.auth.username"
    const val BASIC_AUTH_PASSWORD = "basic.auth.password"
    const val SECURED_ENDPOINT_MESSAGE = "secured.endpoint.message"

    fun getProperties() =
        Properties().apply {
            putAll(getPropertiesFromClasspathFile("application.properties"))
            putAll(getPropertiesFromOptionalClasspathFile("application-test.properties"))
            putAll(getPropertiesFromFile(File("config_override/application.properties")))
        }

    @JvmStatic
    fun getPropertiesFromClasspathFile(filename: String): Properties {
        val properties = Properties()
        try {
            InputStreamReader(
                PropertiesHelper::class.java.classLoader.getResourceAsStream(filename)!!,
                StandardCharsets.UTF_8
            ).use(properties::load)
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
            InputStreamReader(
                PropertiesHelper::class.java.classLoader.getResourceAsStream(filename)!!,
                StandardCharsets.UTF_8
            ).use(properties::load)
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
                InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8).use(properties::load)
            } catch (e: Exception) {
                log.warn("Failed to load properties from {}", file, e)
            }
        }
        return properties
    }

    fun Properties.requireString(propertyKey: String) = string(propertyKey).require(propertyKey)
    fun Properties.requireInt(propertyKey: String) = int(propertyKey).require(propertyKey)
    fun Properties.requireLong(propertyKey: String) = long(propertyKey).require(propertyKey)
    fun Properties.requireBoolean(propertyKey: String) = boolean(propertyKey).require(propertyKey)

    fun Properties.string(propertyKey: String) =
        System.getProperty(propertyKey) ?: getProperty(propertyKey)

    fun Properties.int(propertyKey: String) = string(propertyKey)?.toInt()
    fun Properties.long(propertyKey: String) = string(propertyKey)?.toLong()
    fun Properties.boolean(propertyKey: String) = string(propertyKey)?.toBoolean()

    private fun <T> T?.require(propertyKey: String): T {
        check(this != null) {
            "Property '$propertyKey' not found."
        }
        return this
    }
}
