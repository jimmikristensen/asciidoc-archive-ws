package dk.jimmikristensen.aaws.persistence.dao;

import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity;
import java.sql.SQLException;


public interface AsciidocDAO {
    
    public boolean saveAsciidoc(AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException;
    
    public int getApikeyId(String apikey);
    
    public AsciidocEntity getDocument(int id);
    
    public AsciidocEntity getDocumentByTitle(String title);
}
