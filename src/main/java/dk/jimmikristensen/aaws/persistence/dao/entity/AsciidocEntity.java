package dk.jimmikristensen.aaws.persistence.dao.entity;

public class AsciidocEntity {
    
    private int id;
    private int apikeyId;
    private String doc;
    private String owner;

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the apikeyId
     */
    public int getApikeyId() {
        return apikeyId;
    }

    /**
     * @param apikeyId the apikeyId to set
     */
    public void setApikeyId(int apikeyId) {
        this.apikeyId = apikeyId;
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

    /**
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner the owner to set
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
}
