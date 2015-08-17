package dk.jimmikristensen.aaws.domain;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter;
import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;
import dk.jimmikristensen.aaws.domain.exception.ImporterException;
import dk.jimmikristensen.aaws.domain.github.RepoScanner;
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
    
    public List<AsciidocEntity> initialImport(String owner, String repo) throws ImporterException {
        List<AsciidocEntity> adocEntities = new ArrayList<AsciidocEntity>();
        try {
            List<RepoFile> fileList = scanner.scanRepository(owner, repo);
            for (RepoFile file : fileList) {
                String adocText = scanner.readResource(file.getUrl());
                converter.loadString(adocText);
                String html = converter.convert();
                System.out.println(adocText);
                System.out.println(html);
                
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
                
                adocEntities.add(entity);
                
            }

            dao.save(adocEntities);

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
