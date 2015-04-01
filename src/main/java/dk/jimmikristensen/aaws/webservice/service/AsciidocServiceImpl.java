package dk.jimmikristensen.aaws.webservice.service;

import dk.jimmikristensen.aaws.domain.AsciidocConverter;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;

public class AsciidocServiceImpl implements AsciidocService {

    @Override
    public Response uploadFile(
            String apikey,
            InputStream stream,
            FormDataContentDisposition fileInfo) {

        System.out.println(fileInfo);
        
        try {
            BufferedInputStream bis = new BufferedInputStream(stream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }
            
            AsciidocConverter converter = new AsciidocConverter();
            converter.loadString(buf.toString());
            String html = converter.getHtml();
            
            return Response.ok(html).build();
            
        } catch (IOException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.serverError().build();
    }

    @Override
    public Response getAsciidocs() {
        return Response.ok().build();
    }

}
