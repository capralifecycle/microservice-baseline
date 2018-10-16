package no.capra.health;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Properties;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/health")
public final class HealthEndpoint {
    private static Logger LOG = LoggerFactory.getLogger(HealthEndpoint.class);
    private final String version;
    private final String runningSince;
    private final String mavenGroupId;
    private final String mavenArtifactId;

    public HealthEndpoint() {
        this.mavenGroupId = "no.capra";
        this.mavenArtifactId = "microservice-baseline";
        this.version = this.getVersion();
        this.runningSince = this.getRunningSince();
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response healthCheck() {
        String json = "{" +
                "\"service\":\"" + this.mavenArtifactId
                + "\",\"timestamp\":\"" + Instant.now().toString()
                + "\",\"runningSince\":\"" + this.runningSince
                + "\",\"version\":\"" + this.version
                + "\"}";
        return Response.ok(json).build();
    }

    private String getRunningSince() {
        long uptimeInMillis = ManagementFactory.getRuntimeMXBean().getUptime();
        return Instant.now().minus(uptimeInMillis, ChronoUnit.MILLIS).toString();
    }

    private String getVersion() {
        Properties mavenProperties = new Properties();
        String resourcePath = "/META-INF/maven/" + this.mavenGroupId + "/" + this.mavenArtifactId + "/pom.properties";
        URL mavenVersionResource = this.getClass().getResource(resourcePath);
        if (mavenVersionResource != null) {
            try {
                mavenProperties.load(mavenVersionResource.openStream());
                return mavenProperties.getProperty("version", "missing version info in " + resourcePath);
            } catch (IOException var5) {
                LOG.warn("Problem reading version resource from classpath: ", var5);
            }
        }

        return "(DEV VERSION)";
    }
}