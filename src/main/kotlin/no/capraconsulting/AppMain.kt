package no.capraconsulting

import no.capraconsulting.config.BasicAuthSecurityHandler
import no.capraconsulting.config.JerseyConfig
import no.capraconsulting.config.JsonJettyErrorHandler
import no.capraconsulting.config.PropertiesHelper
import no.capraconsulting.config.PropertiesHelper.requireString
import no.capraconsulting.health.HealthEndpoint
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.servlet.ServletContainer
import org.slf4j.LoggerFactory
import java.util.Properties
import kotlin.system.exitProcess

private val log = LoggerFactory.getLogger(AppMain::class.java)

fun main() {
    val port = Integer.parseInt(System.getProperty("server.port", "8080"))
    AppMain(port, PropertiesHelper.getProperties()).start()
    log.info("Server stopped")
}

class AppMain(val port: Int, private val properties: Properties) {
    private var server: Server? = null

    @Throws(InterruptedException::class)
    fun start() {
        log.debug("Starting server at port {}", port)

        val server = Server(port)
        this.server = server

        server.handler = getServletContextHandler()
        server.stopAtShutdown = true

        try {
            server.start()
        } catch (e: Exception) {
            log.error("Error during Jetty startup. Exiting", e)
            exitProcess(1)
        }

        val healthEndpoint = "http://localhost:" + port + HealthEndpoint.HEALTH_PATH
        log.info("Server started at port {}. Health endpoint at: {}", port, healthEndpoint)
        server.join()
    }

    private fun getServletContextHandler(): ServletContextHandler {
        val contextHandler = ServletContextHandler()
        contextHandler.contextPath = CONTEXT_PATH

        val username = properties.requireString(PropertiesHelper.BASIC_AUTH_USERNAME)
        val password = properties.requireString(PropertiesHelper.BASIC_AUTH_PASSWORD)

        // Basic Authentication
        contextHandler.securityHandler = BasicAuthSecurityHandler
            .getBasicAuthSecurityHandler(username, password, "realm")

        // Add Jersey servlet to the Jetty context
        val jerseyServlet = ServletHolder(ServletContainer(JerseyConfig(properties)))
        contextHandler.addServlet(jerseyServlet, "/*")

        // Error responses as application/json with proper charset
        contextHandler.errorHandler = JsonJettyErrorHandler()

        return contextHandler
    }

    // used by TestServer
    fun stop() {
        try {
            server?.stop()
        } catch (e: Exception) {
            log.warn("Error while stopping Jetty server", e)
        }
    }

    fun isStarted(): Boolean {
        return server?.isStarted == true
    }

    companion object {
        @JvmField
        val CONTEXT_PATH: String = System.getProperty("server.context.path", "/")
    }
}
