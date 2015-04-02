package dk.jimmikristensen.aaws

import org.junit.After
import org.junit.AfterClass
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import spock.lang.Specification

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
        aEntity.setDoc("Test");
        TranslationEntity tEntity = new TranslationEntity();
        tEntity.setType(AsciidocBackend.HTML5);
        tEntity.setAsciidocId(1);     
        tEntity.setDoc("<p>Test</p>");
        
        when:
        boolean status = asdDAO.saveAsciidoc(aEntity, tEntity);
        
        then:
        status == false;
    }
    
    void "Insertion of asciidoc should fail if no asciidoc string is set"() {
        setup:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
        AsciidocEntity aEntity = new AsciidocEntity();
        aEntity.setId(1);
        TranslationEntity tEntity = new TranslationEntity();
        tEntity.setType(AsciidocBackend.HTML5);
        tEntity.setAsciidocId(1);     
        tEntity.setDoc("<p>Test</p>");
        
        when:
        boolean status = asdDAO.saveAsciidoc(aEntity, tEntity);
        
        then:
        status == false;
    }
    
    void "simulate convert and store asciidoc using mocks"() {
        setup:        
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocConverter converter = Mock(HtmlAsciidocConverter);
        AsciidocDAO asdDAO = Mock(AsciidocDAOImpl, constructorArgs: [dsFactory]);
        AsciidocHandler ah = new AsciidocHandler(converter, asdDAO);
        int apikeyId = 1;
        String asciiDoc = "Test";
        
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
        String asciiDoc = "Test";
        
        when:
        boolean status = ah.storeAsciidoc(apikeyId, asciiDoc);
        
        then:
        status == true;
    }
    
}
