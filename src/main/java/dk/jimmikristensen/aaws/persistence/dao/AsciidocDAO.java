package dk.jimmikristensen.aaws.persistence.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;


public interface AsciidocDAO {
    
    public List<AsciidocEntity> getDocumentsByTitle(String title, ContentType contentType);
    
    public boolean save(List<AsciidocEntity> entity) throws SQLException;
    
//    public boolean saveAsciidoc(AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException;
//    
//    public boolean saveAsciidoc(String title, AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException;
    
    public int getApikeyId(String apikey);
    
//    public TranslationEntity getTranslation(int id, String type);
//    
//    public TranslationEntity getTranslation(String title, String type);
//    
//    public AsciidocEntity getDocument(int id);
//    
//    public AsciidocEntity getDocumentByTitle(String title);
//    
//    public List<AsciidocEntity> getDocumentList(int offset, int limit, List<String> categories);
//    
//    public AsciidocEntity getMetadata(String docTitle);
}
