package dk.jimmikristensen.aaws.persistence.database;

import java.util.Map;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Is responsible for managing the data sources available in the application. 
 */
public interface DataSourceFactory {
    
    /**
     * Returns a DataSource given by the name of the source.
     * 
     * @param name The name of the data source to retrieve.
     * @return The requested DataSource.
     * @throws NamingException Is thrown if unable to find a source with the given name.
     */
    public DataSource getDataSource(String name) throws NamingException;
    
    /**
     * Returns all DataSources that has been used throughout the process lifetime.
     * 
     * @return The HashMap containing the active DataSources
     */
    public Map<String, DataSource> getActiveDataSources();    
}
