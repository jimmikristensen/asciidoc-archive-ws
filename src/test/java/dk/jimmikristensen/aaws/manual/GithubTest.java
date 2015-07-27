package dk.jimmikristensen.aaws.manual;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.simple.parser.ParseException;

import dk.jimmikristensen.aaws.domain.github.GithubConnector;

public class GithubTest {

    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {
        fetchCommits();
    }
    
    public static void fetchCommits() throws IOException, ParseException, java.text.ParseException {
        GithubConnector gh = new GithubConnector();
        try {
        String string = "2015-07-21T16:38:34Z";
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = format.parse(string);
        gh.fetchCommits("jimmikristensen", "TDD", date);
        } catch (ClassCastException e) {
            
        }
    }
    
    public static void fetchAll() throws IOException, ParseException {
        GithubConnector gh = new GithubConnector();
        gh.fetchAll("jimmikristensen", "TDD");
    }
    
}
