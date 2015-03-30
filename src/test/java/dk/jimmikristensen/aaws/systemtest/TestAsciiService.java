package dk.jimmikristensen.aaws.systemtest;

import dk.jimmikristensen.aaws.webservice.config.ApplicationConfig;
import dk.jimmikristensen.aaws.webservice.service.AsciidocService;
import dk.jimmikristensen.aaws.webservice.service.AsciidocServiceImpl;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.media.multipart.file.FileDataBodyPart;
import org.glassfish.jersey.test.JerseyTest;
import static org.junit.Assert.*;
import org.junit.Test;

public class TestAsciiService extends JerseyTest {

    @Override
    protected Application configure() {
        return new ApplicationConfig();
    }

    @Override
    protected void configureClient(ClientConfig config) {
        config.register(MultiPartFeature.class);
    }

    @Test
    public void getAllDocs() {
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidocs").build().toString();
        Response response = target(asciidocServicePath).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    public void uploadAsciidocFile() throws IOException {
        String apikey = "123123";
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "uploadFile").build(apikey).toString();
        
        String contents = getTestCase1();
        FormDataMultiPart part = new FormDataMultiPart();
        FormDataContentDisposition dispo = FormDataContentDisposition
                .name("file")
                .fileName("asciidoc-testcase1.adoc")
                .size(contents.getBytes().length)
                .build();
        FormDataBodyPart bodyPart = new FormDataBodyPart(dispo, contents);
        part.bodyPart(bodyPart);
        Response response = target(asciidocServicePath).request().post(Entity.entity(part, MediaType.MULTIPART_FORM_DATA), Response.class);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
    
    private String getTestCase1() {
        InputStream is = getClass().getResourceAsStream("/asciidoc-testcase1.adoc");
        
        String contents = "";
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }

            contents = buf.toString();
        } catch (IOException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return contents;
    }
}
