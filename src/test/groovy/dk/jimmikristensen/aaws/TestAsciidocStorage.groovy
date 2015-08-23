package dk.jimmikristensen.aaws

import static org.junit.Assert.*

import java.util.List;

import spock.lang.Specification
import spock.lang.Unroll
import dk.jimmikristensen.aaws.domain.asciidoc.ContentType
import dk.jimmikristensen.aaws.domain.encryption.SHA1
import dk.jimmikristensen.aaws.doubles.FakeDataSourceFactory
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAOImpl
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory

class TestAsciidocStorage extends Specification {
    
    void "is able to convert plaintext to SHA1 ciphertext"() {
        given:
        String ciphertext = SHA1.encrypt("This is a test to check if SHA1 works");
        
        expect:
        ciphertext.length() == 40;
        ciphertext == "e39454df9247edb7e397a22a794b86a136a0ac8d";
    }
    
    @Unroll
    void "will successfully find one document by name"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO dao = new AsciidocDAOImpl(dsFactory);
        def docTitle = 'Asciidoc Test 2'
        
        when:
        ArrayList<AsciidocEntity> docs = dao.getDocumentsByTitle(docTitle, ContentType.HTML)
        
        then:
        docs != null
        docs.size() == 1
        docs.get(index).getTitle() == title
        docs.get(index).getFilename() == filename
        docs.get(index).getCategories().size() == numCategories
        docs.get(index).getContents().size() == numContents
        
        where:
        index   | title             | filename                   | numCategories    | numContents
        0       | 'Asciidoc Test 2' | 'asciidoc-testcase16.adoc' | 1                | 1
    }
    
    @Unroll
    void "will successfully find two documents with the same name"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO dao = new AsciidocDAOImpl(dsFactory);
        def docTitle = 'Asciidoc Test 1'
        
        when:
        ArrayList<AsciidocEntity> docs = dao.getDocumentsByTitle(docTitle, ContentType.HTML)
        
        then:
        docs != null
        docs.size() == 2
        docs.get(index).getTitle() == title
        docs.get(index).getFilename() == filename
        docs.get(index).getCategories().size() == numCategories
        docs.get(index).getContents().size() == numContents
        
        where:
        index   | title             | filename                   | numCategories    | numContents
        0       | 'Asciidoc Test 1' | 'asciidoc-testcase55.adoc' | 1                | 1
        1       | 'Asciidoc Test 1' | 'asciidoc-testcase98.adoc' | 1                | 1
    }
    
    void "will successfully find one document by id"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO dao = new AsciidocDAOImpl(dsFactory);
        def docId = 1
        
        when:
        AsciidocEntity entity = dao.getDocumentById(docId, ContentType.HTML);
        
        then:
        entity != null
        entity.getId() == docId
        entity.getTitle() == 'Asciidoc Test 1'
        entity.getCategories().size() == 1
        entity.getContents().size() == 1
        entity.getContents().get(0).getType() == ContentType.HTML
    }

    void "will truncate asciidoc database"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO dao = new AsciidocDAOImpl(dsFactory);
        
        when:
        int deleted = dao.deleteAsciidocs()
        
        then:
        deleted == 4
        List<AsciidocEntity> entities = dao.getDocumentList(0, 0, null)
        entities.size() == 0
        
    }
}
