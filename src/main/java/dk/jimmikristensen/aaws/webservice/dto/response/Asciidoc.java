package dk.jimmikristensen.aaws.webservice.dto.response;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;

@XmlRootElement(name = "asciidoc")
@XmlAccessorType(XmlAccessType.FIELD)
public class Asciidoc {
    
    private int id;
    private String title;
    private Date date;
    private ContentType contentType;
    private String content;
    private List<AsciidocCatrgory> categories;
    private String url;
    
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
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public ContentType getContentType() {
        return contentType;
    }
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public List<AsciidocCatrgory> getCategories() {
        return categories;
    }
    public void setCategories(List<AsciidocCatrgory> categories) {
        this.categories = categories;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    
}
