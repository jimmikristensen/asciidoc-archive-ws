package dk.jimmikristensen.aaws.persistence.dao;

import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity;
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class AsciidocDAOImpl implements AsciidocDAO {

    private DataSource ds;

    public AsciidocDAOImpl(DataSourceFactory dsFactory) throws NamingException {
        ds = dsFactory.getDataSource("asciidoc_service");
    }

    @Override
    public boolean saveAsciidoc(AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException {
        String asciidocQry = "INSERT INTO asciidoc (title, apikeys_id, doc) VALUES (?, ?, ?)";
        String translationQry = "INSERT INTO translation (type, asciidoc_id, doc) VALUES (?, ?, ?)";

        Connection conn = null;
        PreparedStatement asciidocStmt = null;
        PreparedStatement translationStmt = null;
        boolean status = false;

        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);

            asciidocStmt = conn.prepareStatement(asciidocQry);
            asciidocStmt.setString(1, aEntity.getTitle());
            asciidocStmt.setInt(2, aEntity.getApikeyId());
            asciidocStmt.setString(3, aEntity.getDoc());
            asciidocStmt.executeUpdate();
            ResultSet rs = asciidocStmt.getGeneratedKeys();
            
            if (rs.next()) {
                int insertId = rs.getInt(1);

                translationStmt = conn.prepareStatement(translationQry);
                translationStmt.setString(1, tEntity.getType().toString());
                translationStmt.setInt(2, insertId);
                translationStmt.setString(3, tEntity.getDoc());
                translationStmt.executeUpdate();
                conn.commit();

                status = true;
            } else {
                rollbackTransaction(conn);
            }

        } catch (SQLException ex) {
            rollbackTransaction(conn);
            throw ex;
            
        } finally {
            try {
                if (asciidocStmt != null) {
                    asciidocStmt.close();
                }
                if (translationStmt != null) {
                    translationStmt.close();
                }
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return status;
    }

    private void rollbackTransaction(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    @Override
    public int getApikeyId(String apikey) {
        try (Connection conn = ds.getConnection()) {
            String qry = "SELECT id FROM apikeys WHERE apikey = ?;";
            PreparedStatement statement = conn.prepareStatement(qry);
            statement.setString(1, apikey);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                return resultSet.getInt("id");
            }
        } catch (SQLException ex) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return 0;
    }

    @Override
    public AsciidocEntity getDocument(int id) {
        try (Connection conn = ds.getConnection()) {            
            String qry = "SELECT doc "
                       + "FROM asciidoc "
                       + "WHERE id = ?;";
            PreparedStatement statement = conn.prepareStatement(qry);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                AsciidocEntity entity = new AsciidocEntity();
                entity.setDoc(resultSet.getString("doc"));
                return entity;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public AsciidocEntity getDocumentByTitle(String title) {
        try (Connection conn = ds.getConnection()) {            
            String qry = "SELECT doc "
                       + "FROM asciidoc "
                       + "WHERE title = ?;";
            PreparedStatement statement = conn.prepareStatement(qry);
            statement.setString(1, title);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                AsciidocEntity entity = new AsciidocEntity();
                entity.setDoc(resultSet.getString("doc"));
                return entity;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
