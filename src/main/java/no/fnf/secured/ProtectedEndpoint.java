package no.fnf.secured;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static no.fnf.secured.ProtectedEndpoint.SECURED_PATH;

@Path(SECURED_PATH)
public class ProtectedEndpoint {
    static final String SECURED_PATH = "/secured";
    private final String message;

    public ProtectedEndpoint(String message){
        this.message = message;
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public Response secured() {
        return Response.ok("{ \"message\" : \" " +message+ " \" }").build();
    }
}
