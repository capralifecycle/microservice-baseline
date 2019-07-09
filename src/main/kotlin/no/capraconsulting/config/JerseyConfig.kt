package no.capraconsulting.config

import no.capraconsulting.secured.ProtectedEndpoint
import org.glassfish.jersey.server.ResourceConfig

import java.util.Properties

class JerseyConfig(properties: Properties) : ResourceConfig() {

    init {
        this.register(createProtectedEndpoint(properties))

        if (PropertiesHelper.getBooleanProperty(properties, "cors.filter.enabled", false)!!) {
            this.register(createCORSFilter(properties))
        }

        this.packages("no.capraconsulting")
    }

    private fun createProtectedEndpoint(properties: Properties): ProtectedEndpoint {
        val message = PropertiesHelper.getStringProperty(
            properties, PropertiesHelper.SECURED_ENDPOINT_MESSAGE, null
        )
        return ProtectedEndpoint(message)
    }

    private fun createCORSFilter(properties: Properties): CORSFilter {
        val allowOrigin = PropertiesHelper.getRequiredStringProperty(properties, "cors.allow.origin")
        val allowHeaders = PropertiesHelper.getRequiredStringProperty(properties, "cors.allow.headers")
        val allowMethods = PropertiesHelper.getRequiredStringProperty(properties, "cors.allow.methods")
        val allowCredentials = PropertiesHelper.getRequiredStringProperty(properties, "cors.allow.credentials")
        val maxAge = PropertiesHelper.getRequiredStringProperty(properties, "cors.max.age")
        return CORSFilter(allowOrigin, allowHeaders, allowMethods, allowCredentials, maxAge)
    }
}
