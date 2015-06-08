package dk.jimmikristensen.aaws.persistence.dao;

import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity;

import java.sql.SQLException;
import java.util.List;


public interface AsciidocDAO {
    
    public boolean saveAsciidoc(AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException;
    
    public boolean saveAsciidoc(String title, AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException;
    
    public int getApikeyId(String apikey);
    
    public TranslationEntity getTranslation(int id, String type);
    
    public TranslationEntity getTranslation(String title, String type);
    
    public AsciidocEntity getDocument(int id);
    
    public AsciidocEntity getDocumentByTitle(String title);
    
    public List<AsciidocEntity> getDocumentList(int offset, int limit, List<String> categories);
    
    public AsciidocEntity getMetadata(String docTitle);
}
