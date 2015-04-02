package dk.jimmikristensen.aaws.persistence.dao;

import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity;


public interface AsciidocDAO {
    
    public boolean saveAsciidoc(AsciidocEntity aEntity, TranslationEntity tEntity);
    
    public int getApikeyId(String apikey);
    
}
