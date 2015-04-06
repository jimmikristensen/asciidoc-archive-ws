package dk.jimmikristensen.aaws.domain;

import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter;
import dk.jimmikristensen.aaws.domain.exception.MissingAsciidocPropertyException;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity;

public class AsciidocHandler {
    
    private AsciidocConverter converter;
    private AsciidocDAO dao;
    
    public AsciidocHandler(AsciidocConverter converter, AsciidocDAO dao) {
        this.converter = converter;
        this.dao = dao;
    }

    public boolean storeAsciidoc(int apikeyId, String asciiDoc) throws MissingAsciidocPropertyException {
        converter.loadString(asciiDoc);
        String convertedDoc = converter.convert();
        
        String docTitle = converter.getMainTitle();
        if (docTitle == null) {
            throw new MissingAsciidocPropertyException("Main title not set");
        }

        AsciidocEntity aEntity = new AsciidocEntity();
        aEntity.setApikeyId(apikeyId);
        aEntity.setDoc(asciiDoc);
        
        TranslationEntity tEntity = new TranslationEntity();
        tEntity.setDoc(convertedDoc);
        tEntity.setType(converter.getBackend());
        
        return dao.saveAsciidoc(aEntity, tEntity);
    }

}
