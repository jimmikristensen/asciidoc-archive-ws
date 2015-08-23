package dk.jimmikristensen.aaws.persistence.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;


public interface AsciidocDAO {
    
    public List<AsciidocEntity> getDocumentsByTitle(String title, ContentType contentType);
    
    public AsciidocEntity getDocumentById(int id, ContentType contentType);
    
    public boolean save(List<AsciidocEntity> entity) throws SQLException;
    
    public boolean update(AsciidocEntity entity, String uniquePath) throws SQLException;
    
    public int getApikeyId(String apikey);

    public List<AsciidocEntity> getDocumentList(int offset, int limit, List<String> categories);

    public int deleteAsciidocs() throws SQLException;
}
