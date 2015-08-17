package dk.jimmikristensen.aaws.domain.github.dto;

import java.util.Date;

public class RepoFile {
    
    private String filename;
    private String path;
    private String sha;
    private String url;
    private String type;
    private Date date;
    
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
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public Date getDate() {
        return date;
    }
    public void setDate(Date date) {
        this.date = date;
    }

    
}
