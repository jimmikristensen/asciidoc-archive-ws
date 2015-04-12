package dk.jimmikristensen.aaws.webservice.dto.response;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import dk.jimmikristensen.aaws.webservice.dto.response.adaptor.DateAdapter;

@XmlRootElement(name = "asciidoc")
@XmlAccessorType(XmlAccessType.FIELD)
public class AsciidocProperties {

    @XmlElement(name = "id")
    private int id;
    
    @XmlElement(name = "title")
    private String title;
    
    @XmlElement(name = "owner")
    private String owner;
    
    @XmlElement(name = "created")
    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date creationDate;
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
