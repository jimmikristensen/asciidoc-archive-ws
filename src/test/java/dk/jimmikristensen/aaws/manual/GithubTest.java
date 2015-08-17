package dk.jimmikristensen.aaws.manual;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.json.simple.parser.ParseException;

import dk.jimmikristensen.aaws.domain.github.GithubScanner;
import dk.jimmikristensen.aaws.domain.github.dto.CommitFile;
import dk.jimmikristensen.aaws.domain.github.dto.RepoFile;
import dk.jimmikristensen.aaws.domain.github.exception.GithubHttpErrorException;
import dk.jimmikristensen.aaws.domain.github.exception.GithubLimitReachedException;

public class GithubTest {
    
    private static String repoOwner = "jimmikristensen";
    private static String repoName = "TDD";

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException, GithubLimitReachedException, GithubHttpErrorException {
        fetchAll();
        fetchCommits();
        downloadResource();
        readFile();
    }
    
    public static void readFile() throws IOException, GithubLimitReachedException, GithubHttpErrorException {
        GithubScanner gh = new GithubScanner();
        String file = gh.readResource("https://raw.githubusercontent.com/jimmikristensen/TDD/c6942dc14649898daf107245dba595f131693f33/test1/asciidoc-testcase16.adoc");
    }
    
    public static void downloadResource() throws IOException, GithubLimitReachedException, GithubHttpErrorException {
        GithubScanner gh = new GithubScanner();
        gh.downloadResource("https://raw.githubusercontent.com/jimmikristensen/TDD/3459a51c4951ba3a55d7debf25488ad174b1aaeb/fakeobject%20two.png");
    }
    
    public static void fetchCommits() throws IOException, ParseException, java.text.ParseException, GithubLimitReachedException, GithubHttpErrorException {
        GithubScanner gh = new GithubScanner();
        try {
            long time = 1438253603*1000L;
            Date date = new Date();
            date.setTime(time);
            List<CommitFile> allFiles = gh.scanCommits(repoOwner, repoName, date);
            
            for (CommitFile commit : allFiles) {
                System.out.println("Filename: "+commit.getFilename());
                System.out.println("Path: "+commit.getPath());
                System.out.println("SHA: "+commit.getSha());
                System.out.println("URL: "+commit.getUrl());
                System.out.println("Type: "+commit.getType());
                System.out.println("Date: "+commit.getDate());
                System.out.println("Committer: "+commit.getCommitter());
                System.out.println("PreviousFilename: "+commit.getPreviousFilename());
                System.out.println("Status: "+commit.getStatus());
                System.out.println("-------------------------------------");
            }
            
        } catch (ClassCastException e) {
            
        }
        
        System.out.println(gh.getGithubRateLimit());
        System.out.println(gh.getGithunRateRemaining());
        System.out.println(gh.getGithubRateReset());
    }
    
    public static void fetchAll() throws IOException, ParseException, ClassCastException, GithubLimitReachedException, GithubHttpErrorException {
        GithubScanner gh = new GithubScanner();
        List<RepoFile> allFiles = gh.scanRepository(repoOwner, repoName);
        
        System.out.println("Found " + allFiles.size() + " files");
        System.out.println("-------------------------------------");
        
        for (RepoFile file : allFiles) {
            System.out.println("Name: "+file.getFilename());
            System.out.println("Path: "+file.getPath());
            System.out.println("SHA: "+file.getSha());
            System.out.println("URL: "+file.getUrl());
            System.out.println("Type: "+file.getType());
            System.out.println("Date: "+file.getDate());
            System.out.println("-------------------------------------");
        }
    }
    
}
