package no.capraconsulting.health

import no.capraconsulting.health.HealthEndpoint.Companion.HEALTH_PATH
import org.slf4j.LoggerFactory
import java.io.IOException
import java.lang.management.ManagementFactory
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path(HEALTH_PATH)
class HealthEndpoint {

    private val version: String
    private val runningSince: String
    private val mavenGroupId: String
    private val mavenArtifactId: String

    init {
        this.mavenGroupId = "no.capraconsulting"
        this.mavenArtifactId = "microservice-baseline"
        this.version = this.getVersion()
        this.runningSince = this.getRunningSince()
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun healthCheck(): Response {
        val json = ("{" +
                "\"service\":\"" + this.mavenArtifactId
                + "\",\"timestamp\":\"" + Instant.now().toString()
                + "\",\"runningSince\":\"" + this.runningSince
                + "\",\"version\":\"" + this.version
                + "\"}")
        return Response.ok(json).build()
    }

    private fun getRunningSince(): String {
        val uptimeInMillis = ManagementFactory.getRuntimeMXBean().uptime
        return Instant.now().minus(uptimeInMillis, ChronoUnit.MILLIS).toString()
    }

    private fun getVersion(): String {
        val mavenProperties = Properties()
        val resourcePath = "/META-INF/maven/" + this.mavenGroupId + "/" + this.mavenArtifactId + "/pom.properties"
        val mavenVersionResource = this.javaClass.getResource(resourcePath)
        if (mavenVersionResource != null) {
            try {
                mavenProperties.load(mavenVersionResource.openStream())
                return mavenProperties.getProperty("version", "missing version info in $resourcePath")
            } catch (var5: IOException) {
                LOG.warn("Problem reading version resource from classpath: ", var5)
            }

        }

        return "(DEV VERSION)"
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(HealthEndpoint::class.java)
        const val HEALTH_PATH = "/health"
    }

}
