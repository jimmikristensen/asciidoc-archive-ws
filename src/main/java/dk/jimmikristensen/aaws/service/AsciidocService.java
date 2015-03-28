package dk.jimmikristensen.aaws.service;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public interface AsciidocService {
    
    @POST
    @Path("/asciidoc/{apikey}")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response getSubscription(@PathParam("apikey") String apikey);
}
