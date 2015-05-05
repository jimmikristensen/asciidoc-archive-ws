package dk.jimmikristensen.aaws.webservice.dto.response;

import javax.xml.bind.annotation.XmlElement;

public class AsciidocCatrgory {
    
    @XmlElement(name = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
}
