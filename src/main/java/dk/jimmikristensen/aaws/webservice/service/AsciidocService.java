package dk.jimmikristensen.aaws.webservice.service;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
            @DefaultValue("") @QueryParam("apikey") String apikey,
            @DefaultValue("0") @QueryParam("offset") int offset,
            @DefaultValue("0") @QueryParam("limit") int limit,
            @QueryParam("catrgory") final List<String> categories);
    
//    @GET
//    @Path("/asciidoc/{title}/metadata")
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response getAsciidocsMetadata(
//            @DefaultValue("") @QueryParam("apikey") String apikey, 
//            @PathParam("title") String title);
    
//    @GET
//    @Path("/asciidoc/{title}")
//    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
//    public Response getAsciidoc(
//            @DefaultValue("false") @QueryParam("download") boolean download,
//            @DefaultValue("") @QueryParam("apikey") String apikey,
//            @PathParam("title") String title,
//            @HeaderParam("Accept") String header);
}
