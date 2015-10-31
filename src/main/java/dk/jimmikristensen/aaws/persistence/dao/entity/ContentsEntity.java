package dk.jimmikristensen.aaws.persistence.dao.entity;

import dk.jimmikristensen.aaws.domain.asciidoc.DocType;

public class ContentsEntity {

    private int asciidocId;
    private DocType type;
    private String document;
    
    public int getAsciidocId() {
        return asciidocId;
    }
    public void setAsciidocId(int asciidocId) {
        this.asciidocId = asciidocId;
    }
    public DocType getType() {
        return type;
    }
    public void setType(DocType type) {
        this.type = type;
    }
    public String getDocument() {
        return document;
    }
    public void setDocument(String document) {
        this.document = document;
    }
    
    
}
