package dk.jimmikristensen.aaws.persistence.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AsciidocEntity {
    
    private int id;
    private String title;
    private String filename;
    private String path;
    private String sha;
    private String url;
    private Date date;
    private List<ContentsEntity> contents = new ArrayList<>();
    private List<CategoryEntity> categories = new ArrayList<>();
    
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
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public String getSha() {
        return sha;
    }
    public void setSha(String sha) {
        this.sha = sha;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }
    public void addContent(ContentsEntity c) {
        contents.add(c);
    }
    public List<ContentsEntity> getContents() {
        return contents;
    }
    public void addcategory(CategoryEntity c) {
        categories.add(c);
    }
    public List<CategoryEntity> getCategories() {
        return categories;
    }
    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }
    
    @Override
    public String toString() {
        return "filename: "+filename+", path: "+path+", sha: "+sha+", url: "+url+", date: "+date;
    }
}
