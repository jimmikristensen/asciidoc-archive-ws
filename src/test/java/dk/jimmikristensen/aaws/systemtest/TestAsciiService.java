package dk.jimmikristensen.aaws.systemtest;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.text.ParseException;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.test.JerseyTest;
import org.junit.Before;
import org.junit.Test;

import dk.jimmikristensen.aaws.domain.asciidoc.DocType;
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
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "listAsciidocs").build().toString();
        Response response = target(asciidocServicePath).request().get();
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
    public void retrievingAnAsciidocByExistingIdWithoutDocTypeSucceeds() throws ParseException {
        int docId = 1;
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docId).toString();
        Response response = target(asciidocServicePath).request().get();
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        DateAdapter dateAdaptor = new DateAdapter();
        
        Asciidoc adoc = response.readEntity(Asciidoc.class);
        assertThat(adoc.getId(), is(docId));
        assertThat(adoc.getTitle(), is("Asciidoc Test 1"));
        assertThat(dateAdaptor.marshal(adoc.getDate()), is("2015-04-01T10:00:00Z"));
        assertThat(adoc.getCategories().size(), is(1));
        assertThat(adoc.getCategories().get(0).getName(), is("test1"));
        assertThat(adoc.getContentType(), is(DocType.HTML));
        assertThat(adoc.getContent(), is("<h1>Another Introduction to AsciiDocc</h1>"));
    }
    
    @Test
    public void retrievingAnAsciidocByExistingIdWithDocTypeAsciidocSucceeds() throws ParseException {
        int docId = 1;
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docId).toString();
        Response response = target(asciidocServicePath)
                .queryParam("doctype", DocType.ASCIIDOC.getType())
                .request().get();
        
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        
        DateAdapter dateAdaptor = new DateAdapter();
        
        Asciidoc adoc = response.readEntity(Asciidoc.class);
        assertEquals(docId, adoc.getId());
        assertEquals("Asciidoc Test 1", adoc.getTitle());
        assertEquals("2015-04-01T10:00:00Z", dateAdaptor.marshal(adoc.getDate()));
        assertEquals(1, adoc.getCategories().size());
        assertEquals("test1", adoc.getCategories().get(0).getName());
        assertEquals(DocType.ASCIIDOC, adoc.getContentType());
    }
    
    @Test
    public void retrievingAsciidocByIdWithInvalidDocTypeWillFail() {
        int docId = 1;
        
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docId).toString();
        Response response = target(asciidocServicePath)
                .queryParam("doctype", "not_valid_type")
                .request().get();
        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
    }
    
    @Test
    public void retrievingAsciidocWithUnknownIdWillReturnNotFound() {
        int docId = 10000;
        String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(docId).toString();
        Response response = target(asciidocServicePath)
                .request().get();
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

}
