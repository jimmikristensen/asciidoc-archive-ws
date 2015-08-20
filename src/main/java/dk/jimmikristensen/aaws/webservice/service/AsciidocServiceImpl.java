package dk.jimmikristensen.aaws.webservice.service;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.ws.rs.core.Response;

import dk.jimmikristensen.aaws.domain.encryption.SHA1;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAOImpl;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;
import dk.jimmikristensen.aaws.persistence.database.JndiDataSourceFactory;
import dk.jimmikristensen.aaws.webservice.dto.response.Asciidoc;
import dk.jimmikristensen.aaws.webservice.dto.response.Asciidocs;
import dk.jimmikristensen.aaws.webservice.error.ErrorCode;
import dk.jimmikristensen.aaws.webservice.exception.GeneralException;

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
    
//    @Override
//    public Response updateFile(
//            String apikey, 
//            List<String> categories, 
//            String title,
//            InputStream stream, 
//            FormDataContentDisposition fileInfo) {
//
//        try {
//            int apikeyId = getAsciiKeyID(apikey);
//            if (apikeyId > 0) {
//                AsciidocConverter converter = new HtmlAsciidocConverter();
//                AsciidocHandler handler = new AsciidocHandler(converter, dao);
//
//                String docTitle = handler.storeAsciidoc(apikeyId, title, inputStreamToString(stream));
//                if (docTitle == null) {
//                    throw new GeneralException("Failed to store asciidoc",
//                            ErrorCode.UNABLE_TO_STORE_ASCIIDOC, Response.Status.NOT_FOUND);
//                }
//                
//                return Response.noContent().build();
//            }
//
//        } catch (MissingAsciidocPropertyException e) {
//            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, e);
//            throw new GeneralException("Missing Asciidoc property ("
//                    + e.getMessage() + ")", ErrorCode.MISSING_DOC_PROPERTY,
//                    Response.Status.BAD_REQUEST);
//            
//        } catch (SQLException e) {
//            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, e);
//        }
//
//        throw new GeneralException("An unknown error occured", ErrorCode.UNKNOWN_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
//    }
    
//    @Override
//    public Response uploadFile(
//            String apikey,
//            List<String> categories,
//            InputStream stream,
//            FormDataContentDisposition fileInfo) {
//        
//        try {
//            int apikeyId = getAsciiKeyID(apikey);
//            
//            if (apikeyId > 0) {
//                AsciidocConverter converter = new HtmlAsciidocConverter();
//                AsciidocHandler handler = new AsciidocHandler(converter, dao);
//                
//                String title = handler.storeAsciidoc(apikeyId, inputStreamToString(stream));
//                if (title == null) {
//                    throw new GeneralException("Failed to store asciidoc",
//                            ErrorCode.UNABLE_TO_STORE_ASCIIDOC, Response.Status.INTERNAL_SERVER_ERROR);
//                }
//
//                URI asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "getAsciidoc")
//                        .queryParam("apikey", apikey)
//                        .build(title);
//                
//                return Response.created(asciidocServicePath).build();
//
//            } else {
//                throw new GeneralException("Invalid api key",
//                        ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
//            }
//
//        } catch (MissingAsciidocPropertyException e) {
//            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, e);
//            throw new GeneralException("Missing Asciidoc property ("
//                    + e.getMessage() + ")", ErrorCode.MISSING_DOC_PROPERTY,
//                    Response.Status.BAD_REQUEST);
//
//        } catch (SQLException e) {
//            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, e);
//
//            // Unique index or primary key violation
//            if (e.getSQLState().equals("23505")) {
//                throw new GeneralException("Document title already exists",
//                        ErrorCode.TITLE_ALREADY_EXISTS,
//                        Response.Status.BAD_REQUEST);
//            }
//        }
//        
//        throw new GeneralException("An unknown error occured", ErrorCode.UNKNOWN_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
//    }
    
//    @Override
//    public Response getAsciidoc(boolean download, String apikey, String title, String acceptHeader) {
//        if (getAsciiKeyID(apikey) > 0) {
//            String mediaType = MediaType.TEXT_PLAIN;
//            String doc = null;
//            if (acceptHeader.equals(MediaType.TEXT_HTML)) {
//                mediaType = MediaType.TEXT_HTML;
//                TranslationEntity entity = dao.getTranslation(title, AsciidocBackend.HTML5.toString());
//                if (entity != null) {
//                    doc = entity.getDoc();
//                }
//            } else {
//                AsciidocEntity entity = dao.getDocumentByTitle(title);
//                if (entity != null) {
//                    doc = entity.getDoc();
//                }
//            }
//            
//            if (doc != null) {
//                if (download) {
//                    return Response.ok(doc)
//                            .header("Content-Disposition", "attachment; filename=\""+title+".adoc\"")
//                            .type(mediaType).build();
//                } else {
//                    return Response.ok(doc).type(mediaType).build();
//                }
//            } else {
//                throw new GeneralException("Could not find document", ErrorCode.RESOURCE_NOT_FOUND, Response.Status.NOT_FOUND);
//            }
//        } else {
//            throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
//        }
//    }
    
    @Override
    public Response listAsciidocs(String apikey, int offset, int limit, List<String> categories) {
        int apikeyId = getAsciiKeyID(apikey);
        
        if (apikeyId > 0) {
            List<AsciidocEntity> entities = dao.getDocumentList(offset, limit, categories);
            List<Asciidoc> asciidocs = new ArrayList<Asciidoc>();
                
            for (AsciidocEntity entity : entities) {
                Asciidoc doc = new Asciidoc();
                doc.setId(entity.getId());
                doc.setTitle(entity.getTitle());
                doc.setDate(entity.getDate());
                asciidocs.add(doc);
            }
                
            Asciidocs docs = new Asciidocs();
            docs.setAsciidocs(asciidocs);
                
            return Response.ok(docs).build();
        } else {
            throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
        }
        
        
//        if (apikeyId > 0) {
//            List<AsciidocEntity> entities = dao.getDocumentList(offset, limit, categories);
//            AsciidocList list = new AsciidocList();
//            
//            for (AsciidocEntity entity : entities) {
//                AsciidocMetadata props = new AsciidocMetadata();
//                props.setId(entity.getId());
//                props.setTitle(entity.getTitle());
//                props.setOwner(entity.getOwner());
//                props.setCreationDate(entity.getCreationDate());
//                
//                List<AsciidocCatrgory> categoryList = new ArrayList<>();
//                if (entity.getCategoryEntities() != null) {
//                    for (CategoryEntity catEntity : entity.getCategoryEntities()) {
//                        AsciidocCatrgory asciidocCat = new AsciidocCatrgory();
//                        asciidocCat.setName(catEntity.getName());
//                        categoryList.add(asciidocCat);
//                    }
//                }
//                
//                props.setCategories(categoryList);
//                list.addProp(props);
//            }
//
//            return Response.ok(list).build();
//            
//        } else {
//            throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
//        }
    }
    
//    @Override
//    public Response getAsciidocsMetadata(String apikey, String docTitle) {
//        int apikeyId = getAsciiKeyID(apikey);
//        if (apikeyId > 0) {
//            AsciidocEntity entity = dao.getMetadata(docTitle);
//            AsciidocMetadata response = new AsciidocMetadata();
//            response.setCreationDate(entity.getCreationDate());
//            response.setId(entity.getId());
//            response.setOwner(entity.getOwner());
//            response.setTitle(entity.getTitle());
//            
//            List<AsciidocCatrgory> categories = new ArrayList<>();
//            if (entity.getCategoryEntities() != null && entity.getCategoryEntities().size() > 0) {
//                for (CategoryEntity cat : entity.getCategoryEntities()) {
//                    AsciidocCatrgory catResp = new AsciidocCatrgory();
//                    catResp.setName(cat.getName());
//                    categories.add(catResp);
//                }
//            }
//            response.setCategories(categories);
//            return Response.ok(response).build();
//            
//        } else {
//            throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
//        }
//    }

}
