package dk.jimmikristensen.aaws.webservice.service;

import dk.jimmikristensen.aaws.domain.AsciidocHandler;
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocBackend;
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter;
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter;
import dk.jimmikristensen.aaws.domain.encryption.SHA1;
import dk.jimmikristensen.aaws.domain.exception.MissingAsciidocPropertyException;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAOImpl;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity;
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;
import dk.jimmikristensen.aaws.persistence.database.JndiDataSourceFactory;
import dk.jimmikristensen.aaws.webservice.dto.response.AsciidocList;
import dk.jimmikristensen.aaws.webservice.dto.response.AsciidocProperties;
import dk.jimmikristensen.aaws.webservice.error.ErrorCode;
import dk.jimmikristensen.aaws.webservice.exception.GeneralException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class AsciidocServiceImpl implements AsciidocService {
    
    private DataSourceFactory dsFactory;
    private AsciidocDAO dao;
    
    public AsciidocServiceImpl() {
        try {
            dsFactory = new JndiDataSourceFactory();
            dao = new AsciidocDAOImpl(dsFactory);
        } catch (NamingException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new GeneralException("Unable to connect to database", ErrorCode.PERSISTENCE_EXCEPTION, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    private int getAsciiKeyID(String apikey) {
        try {
            String apikeyEnc = SHA1.encrypt(apikey);
            return dao.getApikeyId(apikeyEnc);

        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            throw new GeneralException(e.getMessage(), ErrorCode.UNKNOWN_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    private String inputStreamToString(InputStream stream) {
        try {
            BufferedInputStream bis = new BufferedInputStream(stream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }
            return buf.toString();
            
        } catch (IOException e) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            throw new GeneralException(e.getMessage(), ErrorCode.UNKNOWN_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Override
    public Response uploadFile(
            String apikey,
            InputStream stream,
            FormDataContentDisposition fileInfo) {
        
        try {
            int apikeyId = getAsciiKeyID(apikey);
            
            if (apikeyId > 0) {
                AsciidocConverter converter = new HtmlAsciidocConverter();
                AsciidocHandler handler = new AsciidocHandler(converter, dao);
                
                String title = handler.storeAsciidoc(apikeyId, inputStreamToString(stream));
                if (title == null) {
                    throw new GeneralException("Failed to store asciidoc",
                            ErrorCode.UNABLE_TO_STORE_ASCIIDOC, Response.Status.INTERNAL_SERVER_ERROR);
                }

                URI asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc")
                        .queryParam("apikey", "ssdf")
                        .build(title);
                
                return Response.created(asciidocServicePath).build();

            } else {
                throw new GeneralException("Invalid api key",
                        ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
            }

        } catch (MissingAsciidocPropertyException e) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, e);
            throw new GeneralException("Missing Asciidoc property ("
                    + e.getMessage() + ")", ErrorCode.MISSING_DOC_PROPERTY,
                    Response.Status.BAD_REQUEST);

        } catch (SQLException e) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, e);

            // Unique index or primary key violation
            if (e.getSQLState().equals("23505")) {
                throw new GeneralException("Document title already exists",
                        ErrorCode.TITLE_ALREADY_EXISTS,
                        Response.Status.BAD_REQUEST);
            }
        }
        
        throw new GeneralException("An unknown error occured", ErrorCode.UNKNOWN_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
    }
    
    @Override
    public Response getAsciidoc(String apikey, String title, String acceptHeader) {
        if (getAsciiKeyID(apikey) > 0) {
            String doc = getAsciidocBasedOnMediaType(acceptHeader, title);
            if (doc != null) {
                return Response.ok(doc).build();
            } else {
                throw new GeneralException("Could not find document", ErrorCode.RESOURCE_NOT_FOUND, Response.Status.NOT_FOUND);
            }
        } else {
            throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
        }
    }

    private String getAsciidocBasedOnMediaType(String acceptHeader, String title) {
        if (acceptHeader.equals(MediaType.TEXT_HTML)) {
            TranslationEntity entity = dao.getTranslation(title, AsciidocBackend.HTML5.toString());
            if (entity != null) {
                return entity.getDoc();
            }
        }
        AsciidocEntity entity = dao.getDocumentByTitle(title);
        if (entity != null) {
            return entity.getDoc();
        }
        
        return null;
    }
    
    @Override
    public Response listAsciidocs(String apikey) {
        int apikeyId = getAsciiKeyID(apikey);
        
        if (apikeyId > 0) {
            List<AsciidocEntity> entities = dao.getDocumentList();
            AsciidocList list = new AsciidocList();
            
            for (AsciidocEntity entity : entities) {
                AsciidocProperties props = new AsciidocProperties();
                props.setId(entity.getId());
                props.setTitle(entity.getTitle());
                props.setOwner(entity.getOwner());
                props.setCreationDate(entity.getCreationDate());
                list.addProp(props);
            }

            return Response.ok(list).build();
            
        } else {
            throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
        }
    }

    @Override
    public Response updateFile(String apikey, InputStream stream,
            FormDataContentDisposition fileInfo) {
        // TODO Auto-generated method stub
        return null;
    }

}
