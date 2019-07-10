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

private val LOG = LoggerFactory.getLogger(HealthEndpoint::class.java)

@Path(HEALTH_PATH)
class HealthEndpoint {

    private val version: String = getVersion()
    private val runningSince: String = getRunningSince()
    private val mavenGroupId: String = "no.capraconsulting"
    private val mavenArtifactId: String = "microservice-baseline"

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun healthCheck(): Response = Response
        .ok("""{"service":"$mavenArtifactId","timestamp":"${Instant.now()}","runningSince":"$runningSince","version":"$version"}""")
        .build()

    private fun getRunningSince(): String {
        val uptimeInMillis = ManagementFactory.getRuntimeMXBean().uptime
        return Instant.now().minus(uptimeInMillis, ChronoUnit.MILLIS).toString()
    }

    private fun getVersion(): String {
        val mavenProperties = Properties()
        val resourcePath = "/META-INF/maven/$mavenGroupId/$mavenArtifactId/pom.properties"
        val mavenVersionResource = javaClass.getResource(resourcePath)
        if (mavenVersionResource != null) {
            try {
                mavenVersionResource.openStream().use(mavenProperties::load)
                return mavenProperties.getProperty("version", "missing version info in $resourcePath")
            } catch (var5: IOException) {
                LOG.warn("Problem reading version resource from classpath: ", var5)
            }
        }

        return "(DEV VERSION)"
    }

    companion object {
        const val HEALTH_PATH = "/health"
    }
}
