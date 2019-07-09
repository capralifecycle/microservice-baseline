package no.capraconsulting

import no.capraconsulting.config.BasicAuthSecurityHandler
import no.capraconsulting.config.JerseyConfig
import no.capraconsulting.config.JsonJettyErrorHandler
import no.capraconsulting.config.PropertiesHelper
import no.capraconsulting.health.HealthEndpoint
import org.eclipse.jetty.security.SecurityHandler
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.glassfish.jersey.servlet.ServletContainer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.util.Properties

class AppMain internal constructor(internal val port: Int?, private val properties: Properties) {
    private var server: Server? = null

    private// Basic Authentication
    // Add Jersey servlet to the Jetty context
    // Error responses as application/json with proper charset
    val servletContextHandler: ServletContextHandler
        get() {
            val contextHandler = ServletContextHandler()
            contextHandler.contextPath = CONTEXT_PATH

            val username = PropertiesHelper.getStringProperty(properties, PropertiesHelper.BASIC_AUTH_USERNAME, null)
            val password = PropertiesHelper.getStringProperty(properties, PropertiesHelper.BASIC_AUTH_PASSWORD, null)
            val basicAuthSecurityHandler = BasicAuthSecurityHandler
                .getBasicAuthSecurityHandler(username, password, "realm")
            contextHandler.securityHandler = basicAuthSecurityHandler
            val jerseyServlet = ServletHolder(ServletContainer(JerseyConfig(properties)))
            contextHandler.addServlet(jerseyServlet, "/*")
            contextHandler.errorHandler = JsonJettyErrorHandler()

            return contextHandler
        }

    internal val isStarted: Boolean
        get() = server != null && server!!.isStarted

    @Throws(InterruptedException::class)
    internal fun start() {
        log.debug("Starting server at port {}", port)
        server = Server(port!!)
        server!!.handler = servletContextHandler
        server!!.stopAtShutdown = true

        try {
            server!!.start()
        } catch (e: Exception) {
            log.error("Error during Jetty startup. Exiting", e)
            System.exit(1)
        }

        val healthEndpoint = "http://localhost:" + port + HealthEndpoint.HEALTH_PATH
        log.info("Server started at port {}. Health endpoint at: {}", port, healthEndpoint)
        server!!.join()
    }

    //used by TestServer
    internal fun stop() {
        try {
            server!!.stop()
        } catch (e: Exception) {
            log.warn("Error while stopping Jetty server", e)
        }

    }

    companion object {
        private val log = LoggerFactory.getLogger(AppMain::class.java)

        internal val CONTEXT_PATH = System.getProperty("server.context.path", "/")

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val port = Integer.parseInt(System.getProperty("server.port", "8080"))
            val properties = PropertiesHelper.properties
            AppMain(port, properties).start()
            log.info("Server stopped")
        }
    }


}
