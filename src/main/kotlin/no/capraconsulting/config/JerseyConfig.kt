package no.capraconsulting.config

import no.capraconsulting.config.PropertiesHelper.boolean
import no.capraconsulting.config.PropertiesHelper.requireString
import no.capraconsulting.config.PropertiesHelper.string
import no.capraconsulting.secured.ProtectedEndpoint
import org.glassfish.jersey.server.ResourceConfig
import java.util.Properties

class JerseyConfig(properties: Properties) : ResourceConfig() {

    init {
        this.register(createProtectedEndpoint(properties))

        if (properties.boolean("cors.filter.enabled") == true) {
            this.register(createCORSFilter(properties))
        }

        this.packages("no.capraconsulting")
    }

    private fun createProtectedEndpoint(properties: Properties) =
        ProtectedEndpoint(
            properties.string(PropertiesHelper.SECURED_ENDPOINT_MESSAGE) ?: "Secure endpoint"
        )

    private fun createCORSFilter(properties: Properties): CORSFilter {
        val allowOrigin = properties.requireString("cors.allow.origin")
        val allowHeaders = properties.string("cors.allow.headers")
        val allowMethods = properties.string("cors.allow.methods")
        val allowCredentials = properties.string("cors.allow.credentials")
        val maxAge = properties.string("cors.max.age")
        return CORSFilter(allowOrigin, allowHeaders, allowMethods, allowCredentials, maxAge)
    }
}
