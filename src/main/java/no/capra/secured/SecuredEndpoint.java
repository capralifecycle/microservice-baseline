package no.capra.secured;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import static no.capra.secured.SecuredEndpoint.SECURED_PATH;

@Path(SECURED_PATH)
public class SecuredEndpoint {
    static final String SECURED_PATH = "/secured";

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response secured() {
        return Response.ok("{ \"message\" : \"OK\" }").build();
    }
}
