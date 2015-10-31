package dk.jimmikristensen.aaws.webservice.service;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/")
public interface AsciidocService {
    
    @GET
    @Path("/asciidocs")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAsciidocs(
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("0") @QueryParam("limit") int limit,
            @QueryParam("catrgory") final List<String> categories,
            @DefaultValue("HTML") @QueryParam("doctype") String docType);
    
    @GET
    @Path("/asciidocs/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAsciidoc(
            @PathParam("id") int id, 
            @DefaultValue("HTML") @QueryParam("doctype") String docType);

}
