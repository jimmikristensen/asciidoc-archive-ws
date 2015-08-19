package dk.jimmikristensen.aaws.domain.github.dto;

import dk.jimmikristensen.aaws.domain.github.CommitStatus;

public class CommitFile extends RepoFile {

    private String committer;
    private CommitStatus status;
    private String previousPath;
    
    public String getCommitter() {
        return committer;
    }
    public void setCommitter(String committer) {
        this.committer = committer;
    }
    public CommitStatus getStatus() {
        return status;
    }
    public void setStatus(CommitStatus status) {
        this.status = status;
    }
    public String getPreviousPath() {
        return previousPath;
    }
    public void setPreviousPath(String previousFilename) {
        this.previousPath = previousFilename;
    }
    
    
}
