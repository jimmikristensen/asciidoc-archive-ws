package dk.jimmikristensen.aaws.domain;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter;
import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;
import dk.jimmikristensen.aaws.domain.exception.ImporterException;
import dk.jimmikristensen.aaws.domain.github.CommitStatus;
import dk.jimmikristensen.aaws.domain.github.RepoScanner;
import dk.jimmikristensen.aaws.domain.github.dto.CommitFile;
import dk.jimmikristensen.aaws.domain.github.dto.RepoFile;
import dk.jimmikristensen.aaws.domain.github.exception.GithubHttpErrorException;
import dk.jimmikristensen.aaws.domain.github.exception.GithubLimitReachedException;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.CategoryEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.ContentsEntity;

public class AsciidocImporter {
    
    private final static Logger log = LoggerFactory.getLogger(AsciidocImporter.class);
    
    private RepoScanner scanner;
    private AsciidocDAO dao;
    private AsciidocConverter converter;

    public AsciidocImporter(RepoScanner scanner, AsciidocDAO dao, AsciidocConverter converter) {
        this.scanner = scanner;
        this.dao = dao;
        this.converter = converter;
    }
    
    public List<AsciidocEntity> incrementalImport(String owner, String repo, Date fromDate) throws ImporterException {
        // list of all committed asciidocs to return
        List<AsciidocEntity> adocEntities = new ArrayList<AsciidocEntity>();
        
        // list if asciidocs with status ADDED indicating they are new asciidoc files
        List<AsciidocEntity> adocEntitySaveList = new ArrayList<AsciidocEntity>();
        
        try {
            List<CommitFile> commitList = scanner.scanCommits(owner, repo, fromDate);
            for (CommitFile file : commitList) {
                
                if (file.getType().equals(".adoc")) {
                    AsciidocEntity entity = populateAsciidocEntity(file);
                    adocEntities.add(entity);
                                    
                    if (file.getStatus() == CommitStatus.MODIFIED) {
                        dao.update(entity, file.getPath());
                    } else if (file.getStatus() == CommitStatus.RENAMED) {
                        dao.update(entity, file.getPreviousPath());
                    } else if (file.getStatus() == CommitStatus.ADDED) {
                        adocEntitySaveList.add(entity);
                    }
                    
                } else {
                    initiateDownload(file);
                }
            }
            
            if (adocEntitySaveList.size() > 0) {
                dao.save(adocEntitySaveList);
            }

        } catch (ClassCastException | ParseException e) {
            log.error("Unable to parse JSON response", e);
            throw new ImporterException("Unable to import due to JSON parse error on external resource");
        } catch (IOException e) {
            log.error("Unable to connect to repository", e);
            throw new ImporterException("Unable to import due to connection error on external resource");
        } catch (GithubLimitReachedException e) {
            log.error("API limit reached", e);
            throw new ImporterException("Unable to import due to limiting constraints on external resource");
        } catch (GithubHttpErrorException e) {
            log.error("HTTP error response from repository", e);
            throw new ImporterException("Unable to import due to unexpected response from external resource");
        } catch (SQLException e) {
            log.error("Persistance error", e);
            throw new ImporterException("Unable to import due to database error ("+e.getErrorCode()+": "+e.getMessage()+")");
        } catch (java.text.ParseException e) {
            log.error("Date parse error", e);
            throw new ImporterException("Unable to import due to date parse error");
        }
        
        return adocEntities;
    }
        
    public List<AsciidocEntity> initialImport(String owner, String repo) throws ImporterException {
        List<AsciidocEntity> adocEntities = new ArrayList<AsciidocEntity>();
        try {
            List<RepoFile> fileList = scanner.scanRepository(owner, repo);
            for (RepoFile file : fileList) {
                if (file.getType().equals(".adoc")) {
                    adocEntities.add(populateAsciidocEntity(file));
                } else {
                    initiateDownload(file);
                }
            }

            if (adocEntities.size() > 0) {
                dao.save(adocEntities);
            }

        } catch (ClassCastException | ParseException e) {
            log.error("Unable to parse JSON response", e);
            throw new ImporterException("Unable to import due to JSON parse error on external resource");
        } catch (IOException e) {
            log.error("Unable to connect to repository", e);
            throw new ImporterException("Unable to import due to connection error on external resource");
        } catch (GithubLimitReachedException e) {
            log.error("API limit reached", e);
            throw new ImporterException("Unable to import due to limiting constraints on external resource");
        } catch (GithubHttpErrorException e) {
            log.error("HTTP error response from repository", e);
            throw new ImporterException("Unable to import due to unexpected response from external resource");
        } catch (SQLException e) {
            log.error("Persistance error", e);
            throw new ImporterException("Unable to import due to database error ("+e.getErrorCode()+": "+e.getMessage()+")");
        }
        
        return adocEntities;
    }
    
    private String initiateDownload(RepoFile file) throws IOException, GithubLimitReachedException, GithubHttpErrorException {
        String downloadPath = "";
        int slashIndex = file.getPath().lastIndexOf("/");
        if (slashIndex > 0) {
            downloadPath = file.getPath().substring(0, slashIndex+1);
        }
        
        log.debug("Downloading file: "+file.getUrl()+" to path "+downloadPath);
        return scanner.downloadResource(file.getUrl(), downloadPath);
    }
    
    private AsciidocEntity populateAsciidocEntity(RepoFile file) throws IOException, GithubLimitReachedException, GithubHttpErrorException {
        String adocText = scanner.readResource(file.getUrl());
        converter.loadString(adocText);
        String html = converter.convert();
        
        // add asciidoc data
        AsciidocEntity entity = new AsciidocEntity();
        entity.setTitle(converter.getMainTitle());
        entity.setFilename(file.getFilename());
        entity.setPath(file.getPath());
        entity.setSha(file.getSha());
        entity.setUrl(file.getUrl());
        entity.setDate(file.getDate());
        
        // add the asciidoc contents
        ContentsEntity adocContentsEntity = new ContentsEntity();
        adocContentsEntity.setType(ContentType.ASCIIDOC);
        adocContentsEntity.setDocument(adocText);
        entity.addContent(adocContentsEntity);
        
        // ass the html contents
        ContentsEntity htmlContentsEntity = new ContentsEntity();
        htmlContentsEntity.setType(ContentType.HTML);
        htmlContentsEntity.setDocument(html);
        entity.addContent(htmlContentsEntity);
        
        // add the categories
        entity.setCategories(parseCategories(file.getPath()));
        
        return entity;
    }
    
    /**
     * Parses the asciidoc path from github and uses them to generate categories.
     * 
     * @param path
     * @return
     */
    private ArrayList<CategoryEntity> parseCategories(String path) {
        ArrayList<CategoryEntity> categories = new ArrayList<>();
        String[] tokens = path.split("/");
        
        // if there are 2 or more tokens, it means the document is in a directory tree
        if (tokens.length >= 2) {
            for (int i = 0; i < tokens.length - 1; i++) {
                CategoryEntity e = new CategoryEntity();
                e.setName(tokens[i]);
                categories.add(e);
            }
        }

        return categories;
    }
}
