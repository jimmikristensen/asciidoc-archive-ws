package dk.jimmikristensen.aaws.webservice.service;

import java.io.InputStream;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;

@Path("/")
public interface AsciidocService {
    
    @POST
    @Path("/asciidoc")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(
            @DefaultValue("") @QueryParam("apikey") String apikey,
            @FormDataParam("file") InputStream stream, 
            @FormDataParam("file") FormDataContentDisposition fileInfo);
    
    @GET
    @Path("/asciidoc/list")
    @Produces(MediaType.APPLICATION_JSON)
    public Response listAsciidocs(
            @DefaultValue("") @QueryParam("apikey") String apikey);
    
    @GET
    @Path("/asciidoc/{title}")
    @Produces({MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    public Response getAsciidoc(
            @DefaultValue("") @QueryParam("apikey") String apikey,
            @PathParam("title") String title);
}
