package dk.jimmikristensen.aaws.webservice.service;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jimmikristensen.aaws.config.Configuration;
import dk.jimmikristensen.aaws.domain.AsciidocImporterFactory;
import dk.jimmikristensen.aaws.domain.ImporterFactory;
import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;
import dk.jimmikristensen.aaws.domain.encryption.SHA1;
import dk.jimmikristensen.aaws.domain.exception.ImporterException;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.dao.DAOFactory;
import dk.jimmikristensen.aaws.persistence.dao.DataAccessObjectFactory;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.CategoryEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.ContentsEntity;
import dk.jimmikristensen.aaws.webservice.dto.response.Asciidoc;
import dk.jimmikristensen.aaws.webservice.dto.response.AsciidocCatrgory;
import dk.jimmikristensen.aaws.webservice.dto.response.AsciidocImport;
import dk.jimmikristensen.aaws.webservice.dto.response.Asciidocs;
import dk.jimmikristensen.aaws.webservice.error.ErrorCode;
import dk.jimmikristensen.aaws.webservice.exception.GeneralException;

public class AsciidocServiceImpl implements AsciidocService {
    
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
    
    private int getAsciiKeyID(String apikey) {
        try {
            String apikeyEnc = SHA1.encrypt(apikey);
            return dao.getApikeyId(apikeyEnc);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new GeneralException(e.getMessage(), ErrorCode.UNKNOWN_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public Response listAsciidocs(String apikey, int offset, int limit, List<String> categories) {
        checkApiKey(apikey);
        List<AsciidocEntity> entities = dao.getDocumentList(offset, limit, categories);
        List<Asciidoc> asciidocs = new ArrayList<Asciidoc>();

        for (AsciidocEntity entity : entities) {
            Asciidoc doc = new Asciidoc();
            doc.setId(entity.getId());
            doc.setTitle(entity.getTitle());
            doc.setDate(entity.getDate());
            
            String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc").build(entity.getId()).toString();
            doc.setUrl(asciidocServicePath);
            asciidocs.add(doc);
        }

        Asciidocs docs = new Asciidocs();
        docs.setAsciidocs(asciidocs);

        return Response.ok(docs).build();
    }

    @Override
    public Response getAsciidoc(String apikey, int id, String contentType) {
        checkApiKey(apikey);
        ContentType cType = ContentType.fromString(contentType);
        
        if (cType == ContentType.UNKNOWN) {
            throw new GeneralException("Invalid document content type, use one of: "+ContentType.getValidTypes(), 
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
    
    @Override
    public Response deleteAndInitializeAsciidocDatabase(String apikey) {
        checkApiKey(apikey);
        try {
            int deleted = dao.deleteAsciidocs();
            
            ImporterFactory importerFactory = new AsciidocImporterFactory(daoFactory);
            List<AsciidocEntity> imported = importerFactory.getImporter().initialImport(
                    Configuration.getCriticalProperty("github.repo_owner"), 
                    Configuration.getCriticalProperty("github.repo_name"));
            
            int inserted = imported.size();
            
            AsciidocImport importResp = new AsciidocImport();
            importResp.setDeleted(deleted);
            importResp.setInserted(inserted);
            
            return Response.ok(importResp).build();
            
        } catch (NamingException e) {
            log.error("Unable to connect to database", e);
            throw new GeneralException("Unable to connect to database", ErrorCode.PERSISTENCE_EXCEPTION, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (ImporterException e) {
            log.error("Unable to import asciidoc from repository", e);
            throw new GeneralException("Unable to import asciidoc from repository", ErrorCode.UNABLE_TO_STORE_ASCIIDOC, Response.Status.INTERNAL_SERVER_ERROR);
        } catch (SQLException e) {
            log.error("Unable to delete current asciidocs", e);
            throw new GeneralException("Unable to delete current asciidocs", ErrorCode.PERSISTENCE_EXCEPTION, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    private Asciidoc createAsciidocRepresentation(AsciidocEntity docEntity, ContentType contentType) {
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

    private int checkApiKey(String apikey) {
        int apikeyId = getAsciiKeyID(apikey);
        if (apikeyId > 0) {
            return apikeyId;
        } else {
            throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
        }
    }

}
