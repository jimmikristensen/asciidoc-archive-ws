package dk.jimmikristensen.aaws.webservice.service;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jimmikristensen.aaws.domain.asciidoc.DocType;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.dao.DAOFactory;
import dk.jimmikristensen.aaws.persistence.dao.DataAccessObjectFactory;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.CategoryEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.ContentsEntity;
import dk.jimmikristensen.aaws.webservice.dto.response.Asciidoc;
import dk.jimmikristensen.aaws.webservice.dto.response.AsciidocCatrgory;
import dk.jimmikristensen.aaws.webservice.dto.response.Asciidocs;
import dk.jimmikristensen.aaws.webservice.error.ErrorCode;
import dk.jimmikristensen.aaws.webservice.exception.GeneralException;

public class AsciidocServiceImpl implements AsciidocService {
    
    @Context UriInfo uriInfo;
    
    private final static Logger log = LoggerFactory.getLogger(AsciidocServiceImpl.class);

    private AsciidocDAO dao;
    private DataAccessObjectFactory daoFactory;
    
    public AsciidocServiceImpl() {
        try {
            daoFactory = new DAOFactory();
            dao = daoFactory.getAsciidocDao();
        } catch (NamingException ex) {
            log.error("Unable to connect to database", ex);
            throw new GeneralException("Unable to connect to database", ErrorCode.PERSISTENCE_EXCEPTION, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public Response listAsciidocs(int offset, int limit, List<String> categories, String docType) {
        List<AsciidocEntity> entities = dao.getDocumentList(offset, limit, categories);
        List<Asciidoc> asciidocs = new ArrayList<Asciidoc>();

        DocType cType = DocType.fromString(docType);
//      
//      if (cType == ContentType.UNKNOWN) {
//          throw new GeneralException("Invalid document content type, use one of: "+ContentType.getValidTypes(), 
//                  ErrorCode.RESOURCE_NOT_FOUND, Response.Status.BAD_REQUEST);
//      }
        
        for (AsciidocEntity entity : entities) {
            Asciidoc doc = new Asciidoc();
            doc.setId(entity.getId());
            doc.setTitle(entity.getTitle());
            doc.setDate(entity.getDate());
            
            String asciidocServicePath = uriInfo.getBaseUriBuilder().path(AsciidocService.class, "getAsciidoc").build(entity.getId()).toString();
            doc.setUrl(asciidocServicePath);
            asciidocs.add(doc);
        }

        Asciidocs docs = new Asciidocs();
        docs.setAsciidocs(asciidocs);

        return Response.ok(docs).build();
    }

    @Override
    public Response getAsciidoc(int id, String docType) {
        DocType cType = DocType.fromString(docType);
        
        if (cType == DocType.UNKNOWN) {
            throw new GeneralException("Invalid document type, use one of: "+DocType.getValidTypes(), 
                    ErrorCode.RESOURCE_NOT_FOUND, Response.Status.BAD_REQUEST);
        }

        AsciidocEntity docEntity = dao.getDocumentById(id, cType);
        if (docEntity != null) {
            Asciidoc doc = createAsciidocRepresentation(docEntity, cType);
            return Response.ok(doc).build();
        } else {
            throw new GeneralException("Unable to find document with that id", ErrorCode.RESOURCE_NOT_FOUND, Response.Status.NOT_FOUND);
        }
    }
   
    private Asciidoc createAsciidocRepresentation(AsciidocEntity docEntity, DocType contentType) {
        Asciidoc doc = new Asciidoc();
        doc.setId(docEntity.getId());
        doc.setTitle(docEntity.getTitle());
        doc.setDate(docEntity.getDate());
        
        List<ContentsEntity> contentEntities = docEntity.getContents();
        for (ContentsEntity cEntity : contentEntities) {
            if (cEntity.getType() == contentType) {
                doc.setContentType(cEntity.getType());
                doc.setContent(cEntity.getDocument());
            }
        }

        List<AsciidocCatrgory> categories = new ArrayList<AsciidocCatrgory>();
        List<CategoryEntity> categoryEntities = docEntity.getCategories();
        for (CategoryEntity catEntity : categoryEntities) {
            AsciidocCatrgory cat = new AsciidocCatrgory();
            cat.setName(catEntity.getName());
            categories.add(cat);
        }
        doc.setCategories(categories);
        
        return doc;
    }

}
