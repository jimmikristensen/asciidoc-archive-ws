package dk.jimmikristensen.aaws.webservice.service;

import dk.jimmikristensen.aaws.domain.AsciidocHandler;
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter;
import dk.jimmikristensen.aaws.domain.asciidoc.GeneralAsciidocConverter;
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class AsciidocServiceImpl implements AsciidocService {

    @Override
    public Response uploadFile(
            String apikey,
            InputStream stream,
            FormDataContentDisposition fileInfo) {

        try {
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
            int apikeyId = dao.getApikeyId(apikey);
            
            if (apikeyId > 0) {
                AsciidocConverter converter = new HtmlAsciidocConverter();
                AsciidocHandler handler = new AsciidocHandler(converter, dao);
                handler.storeAsciidoc(apikeyId, buf.toString());
                
            } else {
                throw new GeneralException("Invalid api key", ErrorCode.INVALID_API_KEY, Response.Status.FORBIDDEN);
            }
   
            return Response.ok().build();
            
        } catch (IOException | NamingException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        throw new GeneralException("An unknown error occured", ErrorCode.UNKNOWN_ERROR, Response.Status.INTERNAL_SERVER_ERROR);
    }

    @Override
    public Response getAsciidocs() {
        return Response.ok().build();
    }

}
