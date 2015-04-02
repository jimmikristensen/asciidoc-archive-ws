package dk.jimmikristensen.aaws.doubles;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcConnectionPool;

public class FakeDataSourceMySql implements DataSource {
    
    private final JdbcConnectionPool connectionPool;
    
    /**
     * The constructor creates an in-memory database using H2
     * @throws ClassNotFoundException
     * @see <a href="http://h2database.com/html/grammar.html">http://h2database.com/html/grammar.html</a>
     */
    public FakeDataSourceMySql() throws ClassNotFoundException {
        Class.forName("org.h2.Driver");
        //database runs sql/create.sql script first to create db structure, and then runs sql/populate.sql to insert test data
        connectionPool = JdbcConnectionPool.create("jdbc:h2:mem:test;INIT=CREATE SCHEMA IF NOT EXISTS ASCIIDOC_SERVICE\\;runscript from 'classpath:scripts/create_mysql.sql'\\;runscript from 'classpath:scripts/populate_mysql.sql';ALIAS_COLUMN_NAME=TRUE;MODE=MySQL","testuser","");
}

    @Override
    public Connection getConnection() throws SQLException {
    
        return connectionPool.getConnection();
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return connectionPool.getConnection();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        return connectionPool.getLogWriter();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        connectionPool.setLogWriter(out);
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        connectionPool.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return connectionPool.getLoginTimeout();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return connectionPool.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return connectionPool.isWrapperFor(iface);
    }
    
}
