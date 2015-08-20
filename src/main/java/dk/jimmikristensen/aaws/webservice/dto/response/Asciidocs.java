package dk.jimmikristensen.aaws.webservice.dto.response;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "asciidocs")
@XmlAccessorType(XmlAccessType.FIELD)
public class Asciidocs {
    
    private List<Asciidoc> asciidocs;

    public List<Asciidoc> getAsciidocs() {
        return asciidocs;
    }

    public void setAsciidocs(List<Asciidoc> asciidocs) {
        this.asciidocs = asciidocs;
    }
    
    
}
