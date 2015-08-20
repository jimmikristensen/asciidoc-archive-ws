package dk.jimmikristensen.aaws.systemtest;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import dk.jimmikristensen.aaws.persistence.database.DataSources;
import dk.jimmikristensen.aaws.systemtest.doubles.FakeDataSourceMySql;
import dk.jimmikristensen.aaws.webservice.config.ApplicationConfig;
import dk.jimmikristensen.aaws.webservice.dto.response.Asciidoc;
import dk.jimmikristensen.aaws.webservice.dto.response.Asciidocs;
import dk.jimmikristensen.aaws.webservice.dto.response.adaptor.DateAdapter;
import dk.jimmikristensen.aaws.webservice.service.AsciidocService;

public class TestAsciiService extends JerseyTest {
    
    @Before
    public void setup() throws ClassNotFoundException {
        DataSources.put("asciidoc_service", new FakeDataSourceMySql());
    }

    @Override
    protected Application configure() {
        return new ApplicationConfig();
    }
    
    

//    @Override
//    protected void configureClient(ClientConfig config) {
//        config.register(MultiPartFeature.class);
//    }
//
//    @Test
//    public void uploadAsciidocFile() throws IOException {    
//        String apikey = "testkey";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "uploadFile").build().toString();
//
//        FormDataMultiPart part = getTestCase("asciidoc-testcase5.adoc");
//
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .post(Entity.entity(part, MediaType.MULTIPART_FORM_DATA), Response.class);
//        
//        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
//        assertEquals("http://localhost:9998/asciidoc/Another%20Introduction%20to%20AsciiDoc?apikey="+apikey, response.getHeaderString("Location"));
//    }
//    
//    @Test
//    public void updatingUnknownAsciidocFileShouldResultInNotFound() {
//        String apikey = "testkey";
//        String title = "Another Introduction to AsciiDoc";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "updateFile").build(title).toString();
//        
//        FormDataMultiPart part = getTestCase("asciidoc-testcase5.adoc");
//        
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .put(Entity.entity(part, MediaType.MULTIPART_FORM_DATA), Response.class);
//        
//        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
//    }
//    
//    @Test
//    public void UpdatingExistingAsciidocShouldSucceed() {
//        String apikey = "testkey";
//        String title = "Introduction to AsciiDoc"; // this title already exists
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "updateFile").build(title).toString();
//        
//        FormDataMultiPart part = getTestCase("asciidoc-testcase5.adoc");
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .put(Entity.entity(part, MediaType.MULTIPART_FORM_DATA), Response.class);
//        
//        assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
//        
//        // try to retrieve the updated doc
//        String newTitle = "Another Introduction to AsciiDoc";
//        String getAsciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(newTitle).toString();
//        Response docResponse = target(getAsciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .accept(MediaType.TEXT_PLAIN)
//                .get();
//        
//        assertEquals(Response.Status.OK.getStatusCode(), docResponse.getStatus());
//        
//        String docResp = docResponse.readEntity(String.class);
//        System.out.println(docResp);
//        assertTrue(docResp.startsWith("= Another Introduction to AsciiDoc"));
//        assertTrue(docResp.endsWith("puts \"Hello, World!\""));
//    }
//    
//    @Test
//    public void uploadAsciidocFileShouldFailWithForbiddenWhenSuppyingInvalidKey() {
//        String apikey = "invalidkey";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "uploadFile").build().toString();
//        
//        FormDataMultiPart part = getTestCase("asciidoc-testcase2.adoc");
//        
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .post(Entity.entity(part, MediaType.MULTIPART_FORM_DATA), Response.class);
//        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
//    } 
//   
//    @Test
//    public void uploadAsciidocFileShouldFailDocumentHasNoTitle() {
//        String apikey = "testkey";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "uploadFile").build().toString();
//        
//        FormDataMultiPart part = getTestCase("asciidoc-testcase4.adoc");
//        
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .post(Entity.entity(part, MediaType.MULTIPART_FORM_DATA), Response.class);
//        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
//    }
//    
//    @Test
//    public void uploadSameAsciidocFileShouldFailWithBadRequest() {
//        String apikey = "testkey";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "uploadFile").build().toString();
//        
//        FormDataMultiPart part = getTestCase("asciidoc-testcase2.adoc");
//        
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .post(Entity.entity(part, MediaType.MULTIPART_FORM_DATA), Response.class);
//        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
//        
//        response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .post(Entity.entity(part, MediaType.MULTIPART_FORM_DATA), Response.class);
//        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
//    }
//    
//    @Test
//    public void getAsciidocByTitle() {
//        String apikey = "testkey";
//        String docTitle = "Introduction to AsciiDoc";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docTitle).toString();
//
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .get();
//        
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//        
//        String docResp = response.readEntity(String.class);
//        assertTrue(docResp.startsWith("= Introduction to AsciiDoc"));
//        assertTrue(docResp.endsWith("puts \"Hello, World!\""));
//    }
//    
//    @Test
//    public void downloadAsciidocByTitle() {
//        String apikey = "testkey";
//        String docTitle = "Introduction to AsciiDoc";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docTitle).toString();
//
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .queryParam("download", true)
//                .request()
//                .get();
//        
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//        
//        String docResp = response.readEntity(String.class);
//        assertEquals("attachment; filename=\"Introduction to AsciiDoc.adoc\"", response.getHeaderString("Content-Disposition"));
//        assertTrue(docResp.startsWith("= Introduction to AsciiDoc"));
//        assertTrue(docResp.endsWith("puts \"Hello, World!\""));
//    }
//    
//    @Test
//    public void getAsciidocByUnknownTitle() {
//        String apikey = "testkey";
//        String docTitle = "Unknown title";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docTitle).toString();
//        Response response = target(asciidocServicePath).queryParam("apikey", apikey).request().get();
//        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
//    }
//    
    @Test
    public void getListOfDocuments() throws Exception {
        String apikey = "testkey";
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "listAsciidocs").build().toString();
        Response response = target(asciidocServicePath).queryParam("apikey", apikey).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
     
        Asciidocs adocs = response.readEntity(Asciidocs.class);
        DateAdapter dateAdaptor = new DateAdapter();

        assertEquals(3, adocs.getAsciidocs().size());
        
        Asciidoc doc1 = adocs.getAsciidocs().get(0);
        assertEquals(3, doc1.getId());
        assertEquals("Asciidoc Test 3", doc1.getTitle());
        assertEquals("2015-04-03T10:00:00Z", dateAdaptor.marshal(doc1.getDate()));
        
        Asciidoc doc3 = adocs.getAsciidocs().get(2);
        assertEquals(1, doc3.getId());
        assertEquals("Asciidoc Test 1", doc3.getTitle());
        assertEquals("2015-04-01T10:00:00Z", dateAdaptor.marshal(doc3.getDate()));
        
    }
//
//    @Test
//    public void getDocumentAsHtml() {
//        String apikey = "testkey";
//        String docTitle = "Introduction to AsciiDoc";
//        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docTitle).toString();
//        Response response = target(asciidocServicePath)
//                .queryParam("apikey", apikey)
//                .request()
//                .accept(MediaType.TEXT_HTML)
//                .get();
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//        
//        assertEquals("text/html", response.getHeaderString("Content-Type"));
//        
//        String docResp = response.readEntity(String.class);
//        assertTrue(docResp.startsWith("<div id=\"preamble\">"));
//        assertTrue(docResp.endsWith("</div>"));
//    }
//    
//    private FormDataMultiPart getTestCase(String fileName) {
//        InputStream is = getClass().getResourceAsStream("/"+fileName);
//
//        String contents = "";
//        try {
//            BufferedInputStream bis = new BufferedInputStream(is);
//            ByteArrayOutputStream buf = new ByteArrayOutputStream();
//            int result = bis.read();
//            while (result != -1) {
//                byte b = (byte) result;
//                buf.write(b);
//                result = bis.read();
//            }
//
//            contents = buf.toString();
//
//            FormDataMultiPart part = new FormDataMultiPart();
//            FormDataContentDisposition dispo = FormDataContentDisposition
//                    .name("file")
//                    .fileName(fileName)
//                    .size(contents.getBytes().length)
//                    .build();
//            FormDataBodyPart bodyPart = new FormDataBodyPart(dispo, contents);
//            part.bodyPart(bodyPart);
//            
//            return part;
//
//        } catch (IOException ex) {
//            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return null;
//    }
}
