package dk.jimmikristensen.aaws.webservice.service;

import dk.jimmikristensen.aaws.domain.AsciidocHandler;
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter;
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter;
import dk.jimmikristensen.aaws.domain.encryption.SHA1;
import dk.jimmikristensen.aaws.domain.exception.MissingAsciidocPropertyException;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAOImpl;
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;
import dk.jimmikristensen.aaws.persistence.database.JndiDataSourceFactory;
import dk.jimmikristensen.aaws.webservice.error.ErrorCode;
import dk.jimmikristensen.aaws.webservice.exception.GeneralException;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class AsciidocServiceImpl implements AsciidocService {

    @Override
    public Response uploadFile(
            String apikey,
            InputStream stream,
            FormDataContentDisposition fileInfo) {

        try {
            String apikeyEnc = SHA1.encrypt(apikey);
            
            BufferedInputStream bis = new BufferedInputStream(stream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }

            DataSourceFactory dsFactory = new JndiDataSourceFactory();
            AsciidocDAO dao = new AsciidocDAOImpl(dsFactory);
            int apikeyId = dao.getApikeyId(apikeyEnc);
            
            if (apikeyId > 0) {
                AsciidocConverter converter = new HtmlAsciidocConverter();
                AsciidocHandler handler = new AsciidocHandler(converter, dao);
                handler.storeAsciidoc(apikeyId, buf.toString());
                
            } else {
                throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
            }
   
//            String asciidocServicePath = UriBuilder.fromMethod(AsciidocService.class, "uploadFile").build(apikey).toString();
            return Response.created(null).build();
            
        } catch (IOException | NamingException | NoSuchAlgorithmException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MissingAsciidocPropertyException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            throw new GeneralException("Missing Asciidoc property ("+ex.getMessage()+")", ErrorCode.MISSING_DOC_PROPERTY, Response.Status.BAD_REQUEST);
        } catch (SQLException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            
            // Unique index or primary key violation
            if (ex.getSQLState().equals("23505")) {
                throw new GeneralException("Document title already exists", ErrorCode.TITLE_ALREADY_EXISTS, Response.Status.BAD_REQUEST);
            }
        }
        
        throw new GeneralException("An unknown error occured", ErrorCode.UNKNOWN_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Response getAsciidocs() {
        return Response.ok().build();
    }

}
