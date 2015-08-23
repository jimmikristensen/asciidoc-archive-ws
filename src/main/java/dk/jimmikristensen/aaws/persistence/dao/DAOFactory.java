package dk.jimmikristensen.aaws.persistence.dao;

import javax.naming.NamingException;

import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;
import dk.jimmikristensen.aaws.persistence.database.JndiDataSourceFactory;

public class DAOFactory implements DataAccessObjectFactory {
    
    private DataSourceFactory dsFactory;
    private AsciidocDAO asciidocDao;

    public DAOFactory() {
        dsFactory = new JndiDataSourceFactory();
    }
    
    public AsciidocDAO getAsciidocDao() throws NamingException {
        if (asciidocDao == null) {
            asciidocDao = new AsciidocDAOImpl(dsFactory);
        }
        return asciidocDao;
    }
    
}
