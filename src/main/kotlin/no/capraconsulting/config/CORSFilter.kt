package no.capraconsulting.config

import org.slf4j.LoggerFactory
import javax.ws.rs.container.ContainerRequestContext
import javax.ws.rs.container.ContainerResponseContext
import javax.ws.rs.container.ContainerResponseFilter

/**
 * This filter is optionally enabled and wired in [JerseyConfig]
 * See [JerseyConfig] for details about properties used to control allowed origin and headers
 */
class CORSFilter(
    private val allowOrigin: String,
    private val allowHeaders: String,
    private val allowMethods: String,
    private val allowCredentials: String,
    private val maxAge: String
) : ContainerResponseFilter {

    init {
        log.info(
            "Creating CORS filter with the following values: Allow-Origin: {}, Allow-Headers: {}, Allow-Methods: {}, Allow-Credentials: {}, Max-Age: {}",
            allowOrigin, allowHeaders, allowMethods, allowCredentials, maxAge
        )
    }

    override fun filter(
        requestContext: ContainerRequestContext,
        responseContext: ContainerResponseContext
    ) {
        responseContext.headers.apply {
            add("Access-Control-Allow-Origin", allowOrigin)
            add("Access-Control-Allow-Headers", allowHeaders)
            add("Access-Control-Allow-Methods", allowMethods)
            add("Access-Control-Allow-Credentials", allowCredentials)
            add("Access-Control-Max-Age", maxAge)
        }
    }

    companion object {
        private val log = LoggerFactory.getLogger(CORSFilter::class.java)
    }
}
