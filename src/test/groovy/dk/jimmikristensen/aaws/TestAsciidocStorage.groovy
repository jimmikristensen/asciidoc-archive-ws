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
import dk.jimmikristensen.aaws.persistence.dao.impl.AsciidocDAO
import dk.jimmikristensen.aaws.persistence.dao.impl.AsciidocDAOImpl

class TestAsciidocStorage extends Specification {
    
    void ""() {
        setup:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory);
    }
    
}
