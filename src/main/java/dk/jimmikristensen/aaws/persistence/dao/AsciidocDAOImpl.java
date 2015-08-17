package dk.jimmikristensen.aaws.persistence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.NamingException;
import javax.sql.DataSource;

import dk.jimmikristensen.aaws.domain.asciidoc.ContentType;
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.CategoryEntity;
import dk.jimmikristensen.aaws.persistence.dao.entity.ContentsEntity;
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory;

public class AsciidocDAOImpl implements AsciidocDAO {

    private DataSource ds;

    public AsciidocDAOImpl(DataSourceFactory dsFactory) throws NamingException {
        ds = dsFactory.getDataSource("asciidoc_service");
    }
    
//    @Override
//    public boolean saveAsciidoc(String title, AsciidocEntity aEntity, TranslationEntity tEntity) throws SQLException {
//        String asciidocQry = "UPDATE asciidoc SET title=?, apikeys_id=?, doc=? WHERE title=?;";
//        String translationQry = "UPDATE translation SET type=?, doc=?;";
//        
//        Connection conn = null;
//        PreparedStatement asciidocStmt = null;
//        PreparedStatement translationStmt = null;
//        boolean status = false;
//        
//        try {
//            conn = ds.getConnection();
//            conn.setAutoCommit(false);
//            
//            asciidocStmt = conn.prepareStatement(asciidocQry);
//            asciidocStmt.setString(1, aEntity.getTitle());
//            asciidocStmt.setInt(2, aEntity.getApikeyId());
//            asciidocStmt.setString(3, aEntity.getDoc());
//            asciidocStmt.setString(4, title);
//            int rowsAffected = asciidocStmt.executeUpdate();
//            
//            if (rowsAffected > 0) {
//                translationStmt = conn.prepareStatement(translationQry);
//                translationStmt.setString(1, tEntity.getType().toString());
//                translationStmt.setString(2, tEntity.getDoc());
//                translationStmt.executeUpdate();
//                conn.commit();
//
//                status = true;
//            }
//
//        } catch (SQLException ex) {
//            rollbackTransaction(conn);
//            throw ex;
//        } finally {
//            try {
//                if (asciidocStmt != null) {
//                    asciidocStmt.close();
//                }
//                if (translationStmt != null) {
//                    translationStmt.close();
//                }
//                if (conn != null) {
//                    conn.setAutoCommit(true);
//                    conn.close();
//                }
//            } catch (SQLException ex) {
//                Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        return status;
//    }

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

//    @Override
//    public AsciidocEntity getDocument(int id) {
//        try (Connection conn = ds.getConnection()) {
//            String qry = "SELECT doc "
//                       + "FROM asciidoc "
//                       + "WHERE id = ?;";
//            PreparedStatement statement = conn.prepareStatement(qry);
//            statement.setInt(1, id);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                AsciidocEntity entity = new AsciidocEntity();
//                entity.setDoc(resultSet.getString("doc"));
//                return entity;
//            }
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }
    

//    @Override
//    public List<AsciidocEntity> getDocumentList(int offset, int limit, List<String> categories) {
//        List<AsciidocEntity> entities = new ArrayList<>();
//        int qryLimit = 100;
//        if (limit > 0) {
//            qryLimit = limit;
//        }
//        
//        String qryOffset = "";
//        if (offset > 0) {
//            qryOffset = "OFFSET "+offset;
//        }
//        
//        try (Connection conn = ds.getConnection()) {  
//            String qry = "SELECT ad.id, title, owner, creationDate "
//                       + "FROM apikeys AS ak, asciidoc AS ad "
//                       + "WHERE ak.id = ad.apikeys_id "
//                       + "ORDER BY creationDate DESC "
//                       + "LIMIT "+qryLimit+" "+qryOffset+";";
//            
//            PreparedStatement statement = conn.prepareStatement(qry);
//            ResultSet rs = statement.executeQuery();
//            while (rs.next()) {
//                AsciidocEntity entity = new AsciidocEntity();
//                entity.setId(rs.getInt("id"));
//                entity.setTitle(rs.getString("title"));
//                entity.setOwner(rs.getString("owner"));
//                entity.setCreationDate(rs.getTimestamp("creationDate"));
//                entity.setCategoryEntities(getCategories(rs.getInt("id")));
//                entities.add(entity);
//            }
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        return entities;
//    }

    private List<CategoryEntity> getCategories(int asciidocId) {
        List<CategoryEntity> cList = new ArrayList<>();
        try (Connection conn = ds.getConnection()) {
            String qry = "SELECT name FROM categories WHERE asciidocId = ?;";
            PreparedStatement statement = conn.prepareStatement(qry);
            statement.setInt(1, asciidocId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                CategoryEntity cEntity = new CategoryEntity();
                cEntity.setAsciidocId(asciidocId);
                cEntity.setName(rs.getString("name"));
                cList.add(cEntity);
            }
        } catch (SQLException e) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        return cList;
    }

//    @Override
//    public TranslationEntity getTranslation(String title, String type) {
//        try (Connection conn = ds.getConnection()) {            
//            String qry = "SELECT t.doc "
//                       + "FROM asciidoc AS ad, translation AS t "
//                       + "WHERE ad.id=t.asciidoc_id "
//                       + "AND ad.title = ? "
//                       + "AND t.type=?;";
//            PreparedStatement statement = conn.prepareStatement(qry);
//            statement.setString(1, title);
//            statement.setString(2, type);
//            ResultSet resultSet = statement.executeQuery();
//            if (resultSet.next()) {
//                TranslationEntity entity = new TranslationEntity();
//                entity.setDoc(resultSet.getString("doc"));
//                return entity;
//            }
//            
//        } catch (SQLException ex) {
//            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return null;
//    }

    @Override
    public boolean save(List<AsciidocEntity> entities) throws SQLException {
        String asciidocQry = "INSERT INTO asciidocs (title, filename, path, sha, created, url) VALUES (?, ?, ?, ?, ?, ?);";
        String contentsQry = "INSERT INTO contents (asciidocId, `type`, doc) VALUES (?, ?, ?);";
        String categoriesQry = "INSERT INTO categories (asciidocId, name) VALUES (?, ?);";

        Connection conn = null;
        PreparedStatement asciidocStmt = null;
        PreparedStatement categoryStmt = null;
        PreparedStatement contentsStmt = null;
        boolean status = false;
        int asciidocId = 0;
        try {
            conn = ds.getConnection();
            conn.setAutoCommit(false);
        
            for (AsciidocEntity entity : entities) {
                asciidocStmt = conn.prepareStatement(asciidocQry);
                asciidocStmt.setString(1, entity.getTitle());
                asciidocStmt.setString(2, entity.getFilename());
                asciidocStmt.setString(3, entity.getPath());
                asciidocStmt.setString(4, entity.getSha());
                
                long timestamp = new Date().getTime();
                if (entity.getDate() != null) {
                    timestamp = entity.getDate().getTime();
                }
                
                asciidocStmt.setTimestamp(5, new java.sql.Timestamp(timestamp));
                asciidocStmt.setString(6, entity.getUrl());
                
                asciidocStmt.executeUpdate();
                ResultSet rs = asciidocStmt.getGeneratedKeys();
                
                if (rs.next()) {
                    asciidocId = rs.getInt(1);
                    
                    for (ContentsEntity cEntity : entity.getContents()) {
                        contentsStmt = conn.prepareStatement(contentsQry);
                        contentsStmt.setInt(1, asciidocId);
                        contentsStmt.setString(2, cEntity.getType().name());
                        contentsStmt.setString(3, cEntity.getDocument());
                        contentsStmt.executeUpdate();
                    }
                    
                    for (CategoryEntity catEntity : entity.getCategories()) {
                        categoryStmt = conn.prepareStatement(categoriesQry);
                        categoryStmt.setInt(1, asciidocId);
                        categoryStmt.setString(2, catEntity.getName());
                        categoryStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            status = true;
            
        } catch (SQLException ex) {
            rollbackTransaction(conn);
            throw ex;
            
        } finally {
            try {
                if (asciidocStmt != null) {
                    asciidocStmt.close();
                }
                if (contentsStmt != null) {
                    contentsStmt.close();
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
    public List<AsciidocEntity> getDocumentsByTitle(String title, ContentType contentType) {
        ArrayList<AsciidocEntity> docs = new ArrayList<AsciidocEntity>();
        
        try (Connection conn = ds.getConnection()) {
            String qry = "SELECT a.id, a.title, a.filename, a.path, a.sha, a.created, a.url, c.type, c.doc "
                       + "FROM asciidocs AS a "
                       + "JOIN contents AS c "
                       + "ON a.id=c.asciidocId "
                       + "WHERE a.title=? "
                       + "AND c.type=?;";
            PreparedStatement stmt = conn.prepareStatement(qry);
            stmt.setString(1, title);
            stmt.setString(2, contentType.name());
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int asciidocId = rs.getInt("id");
                
                AsciidocEntity adoc = new AsciidocEntity();
                adoc.setId(asciidocId);
                adoc.setTitle(rs.getString("title"));
                adoc.setFilename(rs.getString("filename"));
                adoc.setPath(rs.getString("path"));
                adoc.setSha(rs.getString("sha"));
                adoc.setDate(rs.getTimestamp("created"));
                adoc.setUrl(rs.getString("url"));
                
                ContentsEntity contents = new ContentsEntity();
                contents.setAsciidocId(asciidocId);
                contents.setType(ContentType.fromString(rs.getString("type")));
                contents.setDocument(rs.getString("doc"));

                List<CategoryEntity> categories = new ArrayList<>();
                String catQry = "SELECT name FROM categories WHERE asciidocId = ?;";
                PreparedStatement statement = conn.prepareStatement(catQry);
                statement.setInt(1, asciidocId);
                ResultSet catRs = statement.executeQuery();
                while (catRs.next()) {
                    CategoryEntity category = new CategoryEntity();
                    category.setAsciidocId(asciidocId);
                    category.setName(catRs.getString("name"));
                    categories.add(category);
                }
                
                adoc.addContent(contents);
                adoc.setCategories(categories);
                
                docs.add(adoc);
            }
            
        } catch (SQLException e) {
            Logger.getLogger(AsciidocDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }
        
        return docs;
    }
}
