package dk.jimmikristensen.aaws.domain.github;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.simple.parser.ParseException;

import dk.jimmikristensen.aaws.domain.github.dto.CommitFile;
import dk.jimmikristensen.aaws.domain.github.dto.RepoFile;
import dk.jimmikristensen.aaws.domain.github.exception.GithubHttpErrorException;
import dk.jimmikristensen.aaws.domain.github.exception.GithubLimitReachedException;

public interface RepoScanner {

    public List<CommitFile> scanCommits(String owner, String repo, Date date) throws IOException, ParseException, java.text.ParseException, ClassCastException, GithubLimitReachedException, GithubHttpErrorException;
    
    public List<RepoFile> scanRepository(String owner, String repo) throws IOException, ParseException, ClassCastException, GithubLimitReachedException, GithubHttpErrorException;
    
    public String readResource(String url) throws IOException, GithubLimitReachedException, GithubHttpErrorException;
    
    public String downloadResource(String url) throws IOException, GithubLimitReachedException, GithubHttpErrorException;
}
