package dk.jimmikristensen.aaws.persistence.database;

import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;

public class DataSources {
    
    private static final Map<String, DataSource> dataSources = new HashMap<>();
    
    public static void put(String handle, DataSource ds) {
        dataSources.put(handle, ds);
    }
    
    protected static DataSource get(String handle) {
        if (dataSources.containsKey(handle)) {
            return dataSources.get(handle);
        }
        return null;
    }
    
    protected static Map<String, DataSource> getAll() {
        return dataSources;
    }
}
