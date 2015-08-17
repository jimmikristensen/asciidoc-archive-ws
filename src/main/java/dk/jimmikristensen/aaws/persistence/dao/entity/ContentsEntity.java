package dk.jimmikristensen.aaws.persistence.dao.entity;

import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;

public class ContentsEntity {

    private int asciidocId;
    private ContentType type;
    private String document;
    
    public int getAsciidocId() {
        return asciidocId;
    }
    public void setAsciidocId(int asciidocId) {
        this.asciidocId = asciidocId;
    }
    public ContentType getType() {
        return type;
    }
    public void setType(ContentType type) {
        this.type = type;
    }
    public String getDocument() {
        return document;
    }
    public void setDocument(String document) {
        this.document = document;
    }
    
    
}
