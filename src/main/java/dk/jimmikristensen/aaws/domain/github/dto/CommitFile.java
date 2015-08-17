package dk.jimmikristensen.aaws.domain.github.dto;

public class CommitFile extends RepoFile {

    private String committer;
    private String status;
    private String previousFilename;
    
    public String getCommitter() {
        return committer;
    }
    public void setCommitter(String committer) {
        this.committer = committer;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getPreviousFilename() {
        return previousFilename;
    }
    public void setPreviousFilename(String previousFilename) {
        this.previousFilename = previousFilename;
    }
    
    
}
