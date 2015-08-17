package dk.jimmikristensen.aaws.domain.github;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.squareup.okhttp.Headers;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.HttpUrl.Builder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import dk.jimmikristensen.aaws.config.Configuration;
import dk.jimmikristensen.aaws.domain.github.dto.CommitFile;
import dk.jimmikristensen.aaws.domain.github.dto.RepoFile;
import dk.jimmikristensen.aaws.domain.github.exception.GithubHttpErrorException;
import dk.jimmikristensen.aaws.domain.github.exception.GithubLimitReachedException;
import dk.jimmikristensen.aaws.webservice.dto.response.adaptor.DateAdapter;

public class GithubScanner implements RepoScanner {
    
    private final static Logger log = LoggerFactory.getLogger(GithubScanner.class);
    
    private final String USER_AGENT = "Asciidoc-ws-1.0";
    private OkHttpClient client;
    
    private final String GH_ACCESS_TOKEN = Configuration.getProperty("github.access_token", null);
    private final String GH_API_URL = Configuration.getCriticalProperty("github.apiurl");
    private final String[] GH_RESOURCE_FILES = Configuration.getProperty("github.resource_files", "").split(",");
    
    private List<RepoFile> fileResources;
    private List<CommitFile> commitResources;
    private DateAdapter dateAdapter;
    
    private int ghRateLimit;
    private int ghRateLimitRemaining;
    private String ghRateLimitReset;
    
    public GithubScanner() {
        client = new OkHttpClient();
        client.setConnectTimeout(5, TimeUnit.SECONDS);
        client.setWriteTimeout(5, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);

        fileResources = new ArrayList<>();
        commitResources = new ArrayList<>();
        dateAdapter = new DateAdapter();
    }

    public List<CommitFile> scanCommits(String owner, String repo, Date date) throws IOException, ParseException, java.text.ParseException, ClassCastException, GithubLimitReachedException, GithubHttpErrorException {
        String url = GH_API_URL + "/repos/"+owner+"/"+repo+"/commits";
        
        String stringDate = dateAdapter.marshal(date);
        Map<String, String> params = new HashMap<>();
        params.put("since", stringDate);
        
        Response resp = callGithub(url, params);
        String respBody = resp.body().string().trim();
        
        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(respBody);
        
        JSONArray tree = (JSONArray) obj;
        Iterator<JSONObject> itr = tree.iterator();
        
        while (itr.hasNext()) {
            JSONObject jsonObject = itr.next();
            String commitUrl = (String) jsonObject.get("url");
            scanCommit(commitUrl);
        }
        
        return commitResources;
    }
    
    private void scanCommit(String url) throws IOException, ParseException, ClassCastException, GithubLimitReachedException, GithubHttpErrorException {
        Response resp = callGithub(url, null);
        String respBody = resp.body().string().trim();

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(respBody);

        JSONObject rootObj = (JSONObject) obj;

        // find the committer name and commit date
        JSONObject commitObj = ((JSONObject) rootObj.get("commit"));
        JSONObject committerObj = (JSONObject) commitObj.get("committer");
        String committerName = (String) committerObj.get("name");
        String committerDate = (String) committerObj.get("date");
        
        Date commitDate = new Date();
        try {
            commitDate = dateAdapter.unmarshal(committerDate);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        // find the files that was committed
        JSONArray files = (JSONArray) rootObj.get("files");
        Iterator<JSONObject> filesItr = files.iterator();

        while (filesItr.hasNext()) {
            JSONObject file = filesItr.next();
            String sha = (String) file.get("sha");
            String path = (String) file.get("filename");
            String name = path.substring(path.lastIndexOf("/") + 1);
            String downloadUrl = (String) file.get("raw_url");
            
            String status = (String) file.get("status");
            String previousName = "";
            if (status.equals("renamed")) {
                previousName = (String) file.get("previous_filename");
                previousName = previousName.substring(previousName.lastIndexOf("/") + 1);
            }

            if (name != null) {
                for (String ext : GH_RESOURCE_FILES) {
                    if (name.endsWith("."+ext)) {
                        String extension = name.substring(name.lastIndexOf("."));
                        
                        CommitFile commit = new CommitFile();
                        commit.setFilename(name);
                        commit.setPath(path);
                        commit.setSha(sha);
                        commit.setUrl(downloadUrl);
                        commit.setType(extension);
                        commit.setDate(commitDate);
                        commit.setStatus(status);
                        commit.setCommitter(committerName);
                        commit.setPreviousFilename(previousName);
                        
                        commitResources.add(commit);
                    }
                }
            }
        }
    }
    
    public List<RepoFile> scanRepository(String owner, String repo) throws IOException, ParseException, ClassCastException, GithubLimitReachedException, GithubHttpErrorException {
        String url = GH_API_URL + "/repos/"+owner+"/"+repo+"/contents";
        scanRepo(url);
        return fileResources;
    }
    
    private void scanRepo(String url) throws IOException, ParseException, ClassCastException, GithubLimitReachedException, GithubHttpErrorException {
        Response resp = callGithub(url, null);
        String respBody = resp.body().string().trim();

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(respBody);
        
        JSONArray tree = (JSONArray) obj;
        Iterator<JSONObject> itr = tree.iterator();
        
        while (itr.hasNext()) {
            JSONObject jsonObject = itr.next();
            String name = (String) jsonObject.get("name");
            String type = (String) jsonObject.get("type");
            String downloadUrl = (String) jsonObject.get("download_url");
            String urlRef = (String) jsonObject.get("url");
            String sha = (String) jsonObject.get("sha");
            String path = (String) jsonObject.get("path");
            
            if (name != null && type != null) {
                if (type.equals("file")) {
                    for (String ext : GH_RESOURCE_FILES) {
                        if (name.endsWith("."+ext)) {
                            String extension = name.substring(name.lastIndexOf("."));
                            
                            RepoFile file = new RepoFile();
                            file.setFilename(name);
                            file.setPath(path);
                            file.setSha(sha);
                            file.setUrl(downloadUrl);
                            file.setType(extension);
                            file.setDate(new Date());

                            fileResources.add(file);
                        }
                    }
                    
                } else if (type.equals("dir") && urlRef != null) {
                    scanRepo(urlRef);
                }
            }
        }
    }
    
    public String readResource(String url) throws IOException, GithubLimitReachedException, GithubHttpErrorException {
        Response resp = callGithub(url, null);
        InputStream is = resp.body().byteStream();
        
        BufferedInputStream input = new BufferedInputStream(is);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        
        byte[] data = new byte[1024];
        long total = 0;
        int count = 0;
        while ((count = input.read(data)) != -1) {
            total += count;
            output.write(data, 0, count);
        }
       
        output.flush();
        output.close();
        input.close();
        
        return output.toString();
    }
    
    public String downloadResource(String url) throws IOException, GithubLimitReachedException, GithubHttpErrorException {
        String filename = "unknownfile_"+System.currentTimeMillis();
        if (!url.endsWith("/")) {
            filename = url.substring(url.lastIndexOf("/") + 1);
        }

        // url decode the filename in case of spaces and other encoded chars
        filename = URLDecoder.decode(filename, "UTF-8");
        
        String path = Configuration.getProperty("github.file_download_path", "");

        Response resp = callGithub(url, null);
        
        // fail if unable to create the directory
        File directory = new File(path);
        if (!path.equals("") && !directory.exists() && !directory.mkdirs()) {
            return null;
        }

        // download the file
        InputStream is = resp.body().byteStream();
        BufferedInputStream input = new BufferedInputStream(is);
        OutputStream output = new FileOutputStream(
                Configuration.getProperty("github.file_download_path", "")
                +filename);
        
        byte[] data = new byte[1024];
        long total = 0;
        int count = 0;
        while ((count = input.read(data)) != -1) {
            total += count;
            output.write(data, 0, count);
        }
        
        output.flush();
        output.close();
        input.close();

        return filename;
        
    }
    
    public int getGithubRateLimit() {
        return ghRateLimit;
    }
    
    public int getGithunRateRemaining() {
        return ghRateLimitRemaining;
    }
    
    public Date getGithubRateReset() throws NumberFormatException {
        long unixtime = Long.parseLong(ghRateLimitReset)*1000L;
        return new Date(unixtime);
    }

    private Response callGithub(String url, Map<String, String> params) throws IOException, GithubLimitReachedException, GithubHttpErrorException {
        Builder httpUrlBuilder = HttpUrl.parse(url).newBuilder();
        if (GH_ACCESS_TOKEN != null) {
            httpUrlBuilder.addEncodedQueryParameter("access_token", GH_ACCESS_TOKEN);
        }
        
        if (params != null) {
            for (Map.Entry<String, String> param : params.entrySet()) {
                httpUrlBuilder.addEncodedQueryParameter(param.getKey(), param.getValue());
            }
        }
        
        HttpUrl httpUrl = httpUrlBuilder.build();
        Request req = new Request
                .Builder()
                .addHeader("User-Agent", USER_AGENT)
                .url(httpUrl)
                .build();
        
        Response resp = client.newCall(req).execute();
        Headers headers = resp.headers();
        
        if (!resp.isSuccessful()) {
            log.warn("HTTP ERROR: Code("+resp.code()+"), URL("+httpUrl.toString()+")");
            throw new GithubHttpErrorException("Github returned HTTP error code ("+resp.code()+") when requesting URL ("+httpUrl.toString()+")");
        }
        
        try {
            if (headers.get("X-RateLimit-Limit") != null) {
                ghRateLimit = Integer.parseInt(headers.get("X-RateLimit-Limit"));
                ghRateLimitRemaining = Integer.parseInt(headers.get("X-RateLimit-Remaining"));
                ghRateLimitReset = headers.get("X-RateLimit-Reset");
                
                if (ghRateLimitRemaining <= 0) {
                    log.warn("Github rate limit reached: Limit is ("+ghRateLimit+") and will reset at "+ghRateLimitRemaining);
                    throw new GithubLimitReachedException("Github rate limit reached: Limit is ("+ghRateLimit+") and will reset at "+ghRateLimitRemaining);
                }
            }
        } catch (NumberFormatException e) {
            log.warn("Unable to parse string to int", e);
        }
        return resp;
    }
}


