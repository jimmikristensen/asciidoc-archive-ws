package dk.jimmikristensen.aaws.persistence.dao;

import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.CategoryEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity;
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
    public boolean saveAsciidoc(String title, AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException {
        String asciidocQry = "UPDATE asciidoc SET title=?, apikeys_id=?, doc=? WHERE title=?;";
        String translationQry = "UPDATE translation SET type=?, doc=?;";
        
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
            asciidocStmt.setString(4, title);
            int rowsAffected = asciidocStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                translationStmt = conn.prepareStatement(translationQry);
                translationStmt.setString(1, tEntity.getType().toString());
                translationStmt.setString(2, tEntity.getDoc());
                translationStmt.executeUpdate();
                conn.commit();

                status = true;
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

    @Override
    public boolean saveAsciidoc(AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException {
        String asciidocQry = "INSERT INTO asciidoc (title, apikeys_id, doc) VALUES (?, ?, ?);";
        String translationQry = "INSERT INTO translation (type, asciidoc_id, doc) VALUES (?, ?, ?);";

        Connection conn = null;
        PreparedStatement asciidocStmt = null;
        PreparedStatement categoryStmt = null;
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
                int asciidocId = rs.getInt(1);

                if (aEntity.getCategoryEntities() != null) {
                    StringBuilder valuesStmtBuilder = new StringBuilder();
                    for (int i = 0; i < aEntity.getCategoryEntities().size(); i++) {
                        valuesStmtBuilder.append("(?, ?),");
                    }
                    String valuesStmt = valuesStmtBuilder.substring(0, valuesStmtBuilder.toString().length()-1);
                    String categoryQry = "INSERT INTO category (name, asciidoc_id) VALUES "+valuesStmt+";";
                    
                    categoryStmt = conn.prepareStatement(categoryQry);
                    for (int i = 0; i < aEntity.getCategoryEntities().size(); i++) {
                        String catName = aEntity.getCategoryEntities().get(i).getName();
                        categoryStmt.setString(i+1, catName);
                        categoryStmt.setInt(i+2, asciidocId);
                    }
                    categoryStmt.executeUpdate();
                }
                
                translationStmt = conn.prepareStatement(translationQry);
                translationStmt.setString(1, tEntity.getType().toString());
                translationStmt.setInt(2, asciidocId);
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

    @Override
    public List<AsciidocEntity> getDocumentList(int offset, int limit, List<String> categories) {
        List<AsciidocEntity> entities = new ArrayList<>();
        int qryLimit = 100;
        if (limit > 0) {
            qryLimit = limit;
        }
        
        String qryOffset = "";
        if (offset > 0) {
            qryOffset = "OFFSET "+offset;
        }
        
        try (Connection conn = ds.getConnection()) {  
            String qry = "SELECT ad.id, title, owner, creationDate "
                       + "FROM apikeys AS ak, asciidoc AS ad "
                       + "WHERE ak.id = ad.apikeys_id "
                       + "ORDER BY creationDate DESC "
                       + "LIMIT "+qryLimit+" "+qryOffset+";";
            
            PreparedStatement statement = conn.prepareStatement(qry);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                AsciidocEntity entity = new AsciidocEntity();
                entity.setId(rs.getInt("id"));
                entity.setTitle(rs.getString("title"));
                entity.setOwner(rs.getString("owner"));
                entity.setCreationDate(rs.getTimestamp("creationDate"));
                entity.setCategoryEntities(getCategories(rs.getInt("id")));
                entities.add(entity);
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return entities;
    }

    private List<CategoryEntity> getCategories(int asciidocId) {
        try (Connection conn = ds.getConnection()) {  
            List<CategoryEntity> cList = new ArrayList<>();
            String qry = "SELECT name FROM category WHERE asciidoc_id = ?;";
            PreparedStatement statement = conn.prepareStatement(qry);
            statement.setInt(1, asciidocId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                CategoryEntity cEntity = new CategoryEntity();
                cEntity.setAsciidocId(asciidocId);
                cEntity.setName(rs.getString("name"));
                cList.add(cEntity);
            }
            return cList;
        } catch (SQLException ex) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    @Override
    public TranslationEntity getTranslation(int id, String type) {
        try (Connection conn = ds.getConnection()) {            
            String qry = "SELECT doc "
                       + "FROM translation "
                       + "WHERE type=? AND asciidoc_id = ?;";
            PreparedStatement statement = conn.prepareStatement(qry);
            statement.setString(1, type);
            statement.setInt(2, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                TranslationEntity entity = new TranslationEntity();
                entity.setDoc(resultSet.getString("doc"));
                return entity;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public TranslationEntity getTranslation(String title, String type) {
        try (Connection conn = ds.getConnection()) {            
            String qry = "SELECT t.doc "
                       + "FROM asciidoc AS ad, translation AS t "
                       + "WHERE ad.id=t.asciidoc_id "
                       + "AND ad.title = ? "
                       + "AND t.type=?;";
            PreparedStatement statement = conn.prepareStatement(qry);
            statement.setString(1, title);
            statement.setString(2, type);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                TranslationEntity entity = new TranslationEntity();
                entity.setDoc(resultSet.getString("doc"));
                return entity;
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
