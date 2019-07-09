package no.capraconsulting.secured

import no.capraconsulting.secured.ProtectedEndpoint.Companion.SECURED_PATH
import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.core.MediaType
import javax.ws.rs.core.Response

@Path(SECURED_PATH)
class ProtectedEndpoint(private val message: String) {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    fun secured(): Response = Response.ok("""{"message": "$message"}""").build()

    companion object {
        internal const val SECURED_PATH = "/secured"
    }
}
