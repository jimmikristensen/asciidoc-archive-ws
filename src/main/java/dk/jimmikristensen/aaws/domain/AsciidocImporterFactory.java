package dk.jimmikristensen.aaws.domain;

import javax.naming.NamingException;

import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter;
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter;
import dk.jimmikristensen.aaws.domain.github.GithubScanner;
import dk.jimmikristensen.aaws.domain.github.RepoScanner;
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.dao.DataAccessObjectFactory;

public class AsciidocImporterFactory implements ImporterFactory {
    
    private AsciidocDAO dao;
    private RepoScanner scanner;
    private AsciidocConverter converter;
    private AsciidocImporter importer;
    
    public AsciidocImporterFactory(DataAccessObjectFactory daoFactory) throws NamingException {
        dao = daoFactory.getAsciidocDao();
        scanner = new GithubScanner();
        converter = new HtmlAsciidocConverter();
    }

    public AsciidocImporter getImporter() {
        if (importer == null) {
            importer = new AsciidocImporter(scanner, dao, converter);
        }
        
        return importer;
    }
    
}
