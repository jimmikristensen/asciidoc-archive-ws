package dk.jimmikristensen.aaws.persistence.dao;

import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO;
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class AsciidocDAOImpl implements AsciidocDAO {

    private DataSource ds;

    public AsciidocDAOImpl(DataSourceFactory dsFactory) throws NamingException {
        ds = dsFactory.getDataSource("abonagent");
    }
    
    @Override
    public void saveAsciidoc() {
        
    }
    
}
