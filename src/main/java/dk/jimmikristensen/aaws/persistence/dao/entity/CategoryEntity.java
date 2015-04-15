package dk.jimmikristensen.aaws.persistence.dao.entity;

public class CategoryEntity {

    private int asciidocId;
    private String name;
    
    public int getAsciidocId() {
        return asciidocId;
    }
    public void setAsciidocId(int asciidocId) {
        this.asciidocId = asciidocId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
}
