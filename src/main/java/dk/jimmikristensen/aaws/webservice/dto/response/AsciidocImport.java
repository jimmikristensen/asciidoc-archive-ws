package dk.jimmikristensen.aaws.webservice.dto.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "asciidocImport")
@XmlAccessorType(XmlAccessType.FIELD)
public class AsciidocImport {

    private int deleted;
    private int inserted;
    
    public int getDeleted() {
        return deleted;
    }
    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }
    public int getInserted() {
        return inserted;
    }
    public void setInserted(int inserted) {
        this.inserted = inserted;
    }
    
    
}
