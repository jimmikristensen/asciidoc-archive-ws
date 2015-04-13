package dk.jimmikristensen.aaws

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test

import spock.lang.Specification
import spock.lang.Unroll;
import static org.junit.Assert.*
import dk.jimmikristensen.aaws.doubles.FakeDataSourceFactory
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory
import dk.jimmikristensen.aaws.doubles.FakeDataSourceMySql
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAOImpl
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity
import dk.jimmikristensen.aaws.domain.encryption.SHA1
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocBackend
import dk.jimmikristensen.aaws.domain.AsciidocHandler
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter
import dk.jimmikristensen.aaws.domain.exception.MissingAsciidocPropertyException

import java.sql.SQLException

class TestAsciidocStorage extends Specification {
    
    void "is able to convert plaintext to SHA1 ciphertext"() {
        given:
        String ciphertext = SHA1.encrypt("This is a test to check if SHA1 works");
        
        expect:
        ciphertext.length() == 40;
        ciphertext == "e39454df9247edb7e397a22a794b86a136a0ac8d";
    }
    
    void "get id of existing api key"() {
        setup:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        
        when:
        String apiKey = SHA1.encrypt("testkey");
        int id = asdDAO.getApikeyId(apiKey)
        
        then:
        id == 1;
    }
    
    void "get the int 0 when requesting an apikey that does not exist"() {
        setup:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        
        when:
        String apiKey = SHA1.encrypt("keyNotExists");
        int id = asdDAO.getApikeyId(apiKey)
        
        then:
        id == 0;
    }
    
    void "Insertion of asciidoc should be successful"() {
        setup:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        AsciidocEntity aEntity = new AsciidocEntity();
        aEntity.setApikeyId(1);
        aEntity.setTitle("Some title");
        aEntity.setDoc("Test");
        TranslationEntity tEntity = new TranslationEntity();
        tEntity.setType(AsciidocBackend.HTML5);
        tEntity.setDoc("<p>Test</p>");
        
        when:
        boolean status = asdDAO.saveAsciidoc(aEntity, tEntity);
        
        then:
        status == true;
    }
    
    void "Insertion of asciidoc should fail if no asciidoc ID is set"() {
        setup:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        AsciidocEntity aEntity = new AsciidocEntity();
        aEntity.setTitle("Some Doc1");
        aEntity.setDoc("Test");
        TranslationEntity tEntity = new TranslationEntity();
        tEntity.setType(AsciidocBackend.HTML5);
        tEntity.setAsciidocId(1);     
        tEntity.setDoc("<p>Test</p>");
        
        when:
        asdDAO.saveAsciidoc(aEntity, tEntity);
        
        then:
        thrown(SQLException);
    }
    
    void "Insertion of asciidoc should fail if no asciidoc string is set"() {
        setup:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        AsciidocEntity aEntity = new AsciidocEntity();
        aEntity.setId(1);
        aEntity.setTitle("Some Doc2");
        TranslationEntity tEntity = new TranslationEntity();
        tEntity.setType(AsciidocBackend.HTML5);
        tEntity.setAsciidocId(1);     
        tEntity.setDoc("<p>Test</p>");
        
        when:
        asdDAO.saveAsciidoc(aEntity, tEntity);
        
        then:
        thrown(SQLException);
    }
    
    void "simulate convert and store asciidoc using mocks"() {
        setup:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocConverter converter = Mock(HtmlAsciidocConverter);
        converter.getMainTitle() >> "Test title";
        AsciidocDAO asdDAO = Mock(AsciidocDAOImpl, constructorArgs: [dsFactory]);
        AsciidocHandler ah = new AsciidocHandler(converter, asdDAO);
        int apikeyId = 1;
        String asciiDoc = "= Test\n:title: Sample Document";
        
        when:
        ah.storeAsciidoc(apikeyId, asciiDoc);
        
        then:
        1 * converter.loadString(asciiDoc);
        1 * converter.convert();
        1 * converter.getBackend();
        1 * asdDAO.saveAsciidoc(_ as AsciidocEntity, _ as TranslationEntity);
    }
    
    void "convert and store asciidoc"() {
        setup:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        AsciidocHandler ah = new AsciidocHandler(converter, asdDAO);
        int apikeyId = 1;
        String asciiDoc = "= Sample Document";
        
        when:
        boolean status = ah.storeAsciidoc(apikeyId, asciiDoc);
        
        then:
        status == true;
        converter.getMainTitle() == "Sample Document";
    }
    
    void "converting a document without a title results in an exception"() {
        setup:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        AsciidocHandler ah = new AsciidocHandler(converter, asdDAO);
        int apikeyId = 1;
        String asciiDoc = "Test\n";
        
        when:
        ah.storeAsciidoc(apikeyId, asciiDoc);
        
        then:
        def e = thrown(MissingAsciidocPropertyException);
        e.message == "Main title not set";
    }
    
    void "inserting two documents with the same title should not be allowed"() {
        setup:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        AsciidocHandler ah = new AsciidocHandler(converter, asdDAO);
        int apikeyId = 1;
        String asciiDoc = "= Test";
        
        when:
        boolean status = ah.storeAsciidoc(apikeyId, asciiDoc);
        
        then:
        status == true;
        converter.getMainTitle() == "Test";
        
        when:
        boolean status2 = ah.storeAsciidoc(apikeyId, asciiDoc);
        
        then:
        def e = thrown(SQLException);
        e.getSQLState() == "23505"; //duplicate entry
    }
    
    void "get asciidoc by id"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        
        when:
        AsciidocEntity docEntity = asdDAO.getDocument(1);
        
        then:
        docEntity != null;
        docEntity.getDoc() != "";
        docEntity.getDoc().startsWith("= Introduction to AsciiDoc") == true;
        docEntity.getDoc().endsWith("puts \"Hello, World!\"") == true;
    }
    
    void "get asciidoc by title"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        
        when:
        AsciidocEntity docEntity = asdDAO.getDocumentByTitle("Introduction to AsciiDoc");
        
        then:
        docEntity != null;
        docEntity.getDoc() != "";
        docEntity.getDoc().startsWith("= Introduction to AsciiDoc") == true;
        docEntity.getDoc().endsWith("puts \"Hello, World!\"") == true;
    }
    
    @Unroll
    void "get document with id #id and title #title from document list"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        
        when:
        List<AsciidocEntity> docList = asdDAO.getDocumentList();
        
        then:
        docList.size() == 2;
        
        when:
        AsciidocEntity entity = docList.get(id);

        then:
        title == entity.getTitle();
        
        where:
        id  | title                         | owner | creationDate
        0   | 'Introduction to AsciiDoc'    | 1     | '2015-03-31 20:59:59'
        1   | 'Example of AsciiDoc'         | 1     | '2015-03-30 20:25:01'
        
    }
    
    void "it returns a HTML formatted string"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory()
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory)
        def id = 1
        def type = AsciidocBackend.HTML5.toString()
        
        when:
        TranslationEntity entity = asdDAO.getTranslation(id, type)
        
        then:
        entity != null
        entity.getDoc() != ''
        entity.getDoc().startsWith('<div id="preamble">')
        entity.getDoc().endsWith('</div>')
    }
   
    void "it returns html when requesting document by title"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory()
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory)
        def title = 'Introduction to AsciiDoc'
        def type = AsciidocBackend.HTML5.toString()
        
        when:
        TranslationEntity entity = asdDAO.getTranslation(title, type)
        
        then:
        entity != null
        entity.getDoc() != ''
        entity.getDoc().startsWith('<div id="preamble">')
        entity.getDoc().endsWith('</div>')
    }
    
    void "it updates existing asciidoc with title"() {
        setup:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        AsciidocHandler ah = new AsciidocHandler(converter, asdDAO);
        int apikeyId = 1;
        String asciiDoc = "= Sample Document";
        
        when:
        boolean status = ah.storeAsciidoc(apikeyId, asciiDoc);
        
        then:
        status == true;
        converter.getMainTitle() == "Sample Document";
        
        when:
        def overwriteTitle = 'Sample Document'
        asciiDoc = "= New Sample Document";
        def docTitle = ah.storeAsciidoc(apikeyId, overwriteTitle, asciiDoc);
        
        then:
        docTitle == 'New Sample Document'
        
        when:
        AsciidocEntity docEntity = asdDAO.getDocumentByTitle("New Sample Document");
        
        then:
        docEntity != null;
        docEntity.getDoc() != "";
        docEntity.getDoc().startsWith("= New Sample Document") == true;
    }
    
}
