package dk.jimmikristensen.aaws.persistence.database;

import java.util.Map;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * This manager is responsible for retrieving the data sources from a 
 * web server like tomcat. It uses the Context to lookup data sources and 
 * saves them locally in a map.
 */
public class JndiDataSourceFactory implements DataSourceFactory {

    /**
     * The prefix context name.
     */
    protected String CONTEXT_LOOKUP = "java:comp/env/jdbc/";

    @Override
    public DataSource getDataSource(String name) throws NamingException {
        DataSource ds = DataSources.get(name);
        
        if (ds == null) {
            Context ctx = new InitialContext();
            DataSources.put(name,(DataSource) ctx.lookup(CONTEXT_LOOKUP + name));
            return DataSources.get(name);
            
        } else {
            return ds;
        }
        
    }
    
    @Override
    public Map<String, DataSource> getActiveDataSources() {
        return DataSources.getAll();
    }
}

