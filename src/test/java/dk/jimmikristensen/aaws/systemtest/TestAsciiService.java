package dk.jimmikristensen.aaws.systemtest;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;
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
    
    @Test
    public void getListOfDocumentsSuccceeds() throws Exception {
        String apikey = "testkey";
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "listAsciidocs").build().toString();
        Response response = target(asciidocServicePath).queryParam("apikey", apikey).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
     
        Asciidocs adocs = response.readEntity(Asciidocs.class);
        DateAdapter dateAdaptor = new DateAdapter();

        assertEquals(4, adocs.getAsciidocs().size());
        
        Asciidoc doc1 = adocs.getAsciidocs().get(0);
        assertEquals(4, doc1.getId());
        assertEquals("Asciidoc Test 1", doc1.getTitle());
        assertEquals("2015-06-01T11:00:00Z", dateAdaptor.marshal(doc1.getDate()));
        
        Asciidoc doc3 = adocs.getAsciidocs().get(2);
        assertEquals(2, doc3.getId());
        assertEquals("Asciidoc Test 2", doc3.getTitle());
        assertEquals("2015-04-02T10:00:00Z", dateAdaptor.marshal(doc3.getDate()));
    }
    
    @Test
    public void getListOfDocumentsWithIncorrectApikeyWillFail() {
        String apikey = "invalid_key";
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "listAsciidocs").build().toString();
        Response response = target(asciidocServicePath).queryParam("apikey", apikey).request().get();
        assertEquals(Response.Status.FORBIDDEN.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void retrievingAnAsciidocByExistingIdWithoutContentTypeSucceeds() throws ParseException {
        String apikey = "testkey";
        int docId = 1;
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docId).toString();
        Response response = target(asciidocServicePath).queryParam("apikey", apikey).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        DateAdapter dateAdaptor = new DateAdapter();
        
        Asciidoc adoc = response.readEntity(Asciidoc.class);
        assertEquals(docId, adoc.getId());
        assertEquals("Asciidoc Test 1", adoc.getTitle());
        assertEquals("2015-04-01T10:00:00Z", dateAdaptor.marshal(adoc.getDate()));
        assertEquals(1, adoc.getCategories().size());
        assertEquals("test1", adoc.getCategories().get(0).getName());
        assertEquals(ContentType.HTML, adoc.getContentType());
    }
    
    @Test
    public void retrievingAnAsciidocByExistingIdWithContentTypeAsciidocSucceeds() throws ParseException {
        String apikey = "testkey";
        int docId = 1;
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docId).toString();
        Response response = target(asciidocServicePath)
                .queryParam("apikey", apikey)
                .queryParam("contenttype", ContentType.ASCIIDOC.getType())
                .request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        DateAdapter dateAdaptor = new DateAdapter();
        
        Asciidoc adoc = response.readEntity(Asciidoc.class);
        assertEquals(docId, adoc.getId());
        assertEquals("Asciidoc Test 1", adoc.getTitle());
        assertEquals("2015-04-01T10:00:00Z", dateAdaptor.marshal(adoc.getDate()));
        assertEquals(1, adoc.getCategories().size());
        assertEquals("test1", adoc.getCategories().get(0).getName());
        assertEquals(ContentType.ASCIIDOC, adoc.getContentType());
    }
    
    @Test
    public void retrievingAsciidocByIdWithInvalidContentTypeWillFail() {
        String apikey = "testkey";
        int docId = 1;
        String invalidContentType = "invalid_type";
        
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docId).toString();
        Response response = target(asciidocServicePath)
                .queryParam("apikey", apikey)
                .queryParam("contenttype", invalidContentType)
                .request().get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void retrievingAsciidocWithUnknownIdWillReturnNotFound() {
        String apikey = "testkey";
        int docId = 10000;
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docId).toString();
        Response response = target(asciidocServicePath)
                .queryParam("apikey", apikey)
                .queryParam("contenttype", ContentType.HTML)
                .request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
