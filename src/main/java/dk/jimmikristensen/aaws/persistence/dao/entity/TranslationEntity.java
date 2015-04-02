package dk.jimmikristensen.aaws.persistence.dao.entity;

import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocBackend;

public class TranslationEntity {
    
    private AsciidocBackend type;
    private int asciidocId;
    private String doc;

    /**
     * @return the type
     */
    public AsciidocBackend getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(AsciidocBackend type) {
        this.type = type;
    }

    /**
     * @return the asciidocId
     */
    public int getAsciidocId() {
        return asciidocId;
    }

    /**
     * @param asciidocId the asciidocId to set
     */
    public void setAsciidocId(int asciidocId) {
        this.asciidocId = asciidocId;
    }

    /**
     * @return the doc
     */
    public String getDoc() {
        return doc;
    }

    /**
     * @param doc the doc to set
     */
    public void setDoc(String doc) {
        this.doc = doc;
    }
    
}
