package dk.jimmikristensen.aaws.doubles;

import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class FakeDataSourceFactory implements DataSourceFactory {

    /**
     * Map containing all data sources found.
     */
    protected Map<String, DataSource> dataSources = new HashMap<>();

    @Override
    public DataSource getDataSource(String name) throws NamingException {
        try {
            dataSources.put("asciidoc_service", new FakeDataSourceMySql());
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(FakeDataSourceFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (name.equals("asciidoc_service")) {
            return dataSources.get(name);
        }
        return null;
    }

    @Override
    public Map<String, DataSource> getActiveDataSources() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
