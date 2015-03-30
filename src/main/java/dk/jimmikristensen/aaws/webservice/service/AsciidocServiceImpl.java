package dk.jimmikristensen.aaws.webservice.service;

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
            System.out.println(buf.toString());
            
            System.out.println("END");
            
            
        } catch (IOException ex) {
            Logger.getLogger(AsciidocServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return Response.ok().build();
        
    }

    @Override
    public Response getAsciidocs() {
        return Response.ok().build();
    }

}
