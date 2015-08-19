package dk.jimmikristensen.aaws

import org.junit.After;

import spock.lang.Specification
import dk.jimmikristensen.aaws.domain.AsciidocImporter
import dk.jimmikristensen.aaws.domain.asciidoc.ContentType
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter
import dk.jimmikristensen.aaws.domain.github.CommitStatus;
import dk.jimmikristensen.aaws.domain.github.GithubScanner
import dk.jimmikristensen.aaws.domain.github.dto.CommitFile
import dk.jimmikristensen.aaws.domain.github.dto.RepoFile
import dk.jimmikristensen.aaws.doubles.FakeDataSourceFactory
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAOImpl
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity
import dk.jimmikristensen.aaws.persistence.dao.entity.CategoryEntity
import dk.jimmikristensen.aaws.persistence.dao.entity.ContentsEntity

class TestAsciidocImporter extends Specification {

    void "One new asciidoc file is successfully stored using mocks"() {
        given:
        def dao = Mock(AsciidocDAO)
        def scanner = Mock(GithubScanner)
        def converter = Mock(HtmlAsciidocConverter)
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        
        def now = new Date()
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/asciidoc-testcase1.adoc'
        def convertedDoc = '<h1>Introduction to AsciiDoc</h1>'
        def docTitle = 'Introduction to AsciiDoc'
        def asciidoc = '= Introduction to AsciiDoc'
        def filename = 'asciidoc-testcase1.adoc'
        def path = 'test cases/asciidoc-testcase1.adoc'
        def sha = '4e6e10426375e746ad5dff7d94e765af66a1b8a5'
        def fileType = '.adoc'
        
        when:
        ArrayList<AsciidocEntity> insertedEntities = importer.initialImport(owner, repo)
        
        then:
        1 * scanner.scanRepository(owner, repo) >> [
                RepoFile[
                    filename: filename,
                    path: path,
                    sha: sha,
                    url: docUrl,
                    type: fileType,
                    date: now
                ]
            ]
        
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        1 * dao.save(_ as ArrayList<AsciidocEntity>)

        and: 'check that all attributes has been set on the asciidoc entity'
        insertedEntities.size() == 1
        insertedEntities.get(0).getTitle() == docTitle
        insertedEntities.get(0).getDate() == now
        insertedEntities.get(0).getFilename() == filename
        insertedEntities.get(0).getPath() == path
        insertedEntities.get(0).getSha() == sha
        insertedEntities.get(0).getUrl() == docUrl
        insertedEntities.get(0).getContents().size() == 2
        insertedEntities.get(0).getCategories().size() == 1
        
        and: 'check that the asciidoc is part of contents'
        ContentsEntity adocContents = insertedEntities.get(0).getContents().get(0)
        adocContents.getDocument() == '= Introduction to AsciiDoc'
        adocContents.getType() == ContentType.ASCIIDOC
        
        and: 'check that the html is part of contents'
        ContentsEntity htmlContents = insertedEntities.get(0).getContents().get(1)
        htmlContents.getDocument() == '<h1>Introduction to AsciiDoc</h1>'
        htmlContents.getType() == ContentType.HTML
        
        and: 'check that categories has been set'
        CategoryEntity category = insertedEntities.get(0).getCategories().get(0)
        category.getName() == 'test cases'
    }
    
    void "One new asciidoc file is successfully stored using fake database"() {
        given:
        def dsFactory = new FakeDataSourceFactory()
        def dao = new AsciidocDAOImpl(dsFactory)
        def scanner = Mock(GithubScanner)
        def converter = Mock(HtmlAsciidocConverter)
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        
        def now = new Date()
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/asciidoc-testcase1.adoc'
        def convertedDoc = '<h1>Introduction to AsciiDoc</h1>'
        def docTitle = 'Introduction to AsciiDoc'
        def asciidoc = '= Introduction to AsciiDoc'
        def filename = 'asciidoc-testcase1.adoc'
        def path = 'test cases/asciidoc-testcase1.adoc'
        def sha = '4e6e10426375e746ad5dff7d94e765af66a1b8a5'
        def fileType = '.adoc'
        
        when:
        ArrayList<AsciidocEntity> insertedEntities = importer.initialImport(owner, repo)
        
        then:
        1 * scanner.scanRepository(owner, repo) >> [
            RepoFile[
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now
            ]
        ]
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        
        when:
        ArrayList<AsciidocEntity> docs = dao.getDocumentsByTitle(docTitle, ContentType.HTML)
        
        then:
        docs != null
        docs.size() == 1
        def docEntity = docs.get(0)
        docEntity.getId() > 0
        docEntity.getDate() == now
        docEntity.getTitle() == docTitle
        
        and:
        docEntity.getCategories().size() == 1
        def catEntity = docEntity.getCategories().get(0)
        catEntity.getName() == 'test cases'
        
        and:
        docEntity.getContents().size() == 1
        def contentEntity = docEntity.getContents().get(0)
        contentEntity.getDocument() == convertedDoc
        contentEntity.getType() == ContentType.HTML
    }
    
    void "One new asciidoc file is successfully stored using fake database and real converter"() {
        given:
        def dsFactory = new FakeDataSourceFactory()
        def dao = new AsciidocDAOImpl(dsFactory)
        def scanner = Mock(GithubScanner)
        def converter = new HtmlAsciidocConverter()
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        
        def now = new Date()
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/asciidoc-testcase1.adoc'
        def docTitle = 'Introduction to AsciiDoc'
        def filename = 'asciidoc-testcase1.adoc'
        
        when:
        ArrayList<AsciidocEntity> insertedEntities = importer.initialImport(owner, repo)
        
        then:
        1 * scanner.scanRepository(owner, repo) >> [
            RepoFile[
                filename: filename,
                path: 'test cases/asciidoc-testcase1.adoc',
                sha: '4e6e10426375e746ad5dff7d94e765af66a1b8a5',
                url: docUrl,
                type: '.adoc',
                date: now
            ]
        ]
        1 * scanner.readResource(docUrl) >> getTestCase(filename)
        
        when:
        ArrayList<AsciidocEntity> docs = dao.getDocumentsByTitle(docTitle, ContentType.HTML)
        
        then:
        docs != null
        docs.size() == 1
        def docEntity = docs.get(0)
        docEntity.getId() > 0
        docEntity.getDate() == now
        docEntity.getTitle() == docTitle
        
        and:
        docEntity.getCategories().size() == 1
        def catEntity = docEntity.getCategories().get(0)
        catEntity.getName() == 'test cases'
        
        and:
        docEntity.getContents().size() == 1
        def contentEntity = docEntity.getContents().get(0)
        contentEntity.getDocument().startsWith('<div')
        contentEntity.getType() == ContentType.HTML
    }
    
    void "Updating one commit file with status modified succeeds"() {
        given:
        def dao = Mock(AsciidocDAO)
        def scanner = Mock(GithubScanner)
        def converter = Mock(HtmlAsciidocConverter)
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        def now = new Date()
        
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/asciidoc-testcase1.adoc'
        def convertedDoc = '<h1>Introduction to AsciiDoc</h1>'
        def docTitle = 'Introduction to AsciiDoc'
        def asciidoc = '= Introduction to AsciiDoc'
        def filename = 'asciidoc-testcase1.adoc'
        def path = 'test cases/asciidoc-testcase2.adoc'
        def sha = '2e6e10426375e746ad5dff7d94e765af66a1b8a5'
        def fileType = '.adoc'
        
        when:
        ArrayList<AsciidocEntity> insertedEntities = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            CommitFile[
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.MODIFIED,
                previousPath: null
            ]
        ]
    
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        1 * dao.update(_ as AsciidocEntity, path)
        
        and:
        insertedEntities.size() == 1
    }
    
    void "Updating one commit file with status renamed succeeds"() {
        given:
        def dao = Mock(AsciidocDAO)
        def scanner = Mock(GithubScanner)
        def converter = Mock(HtmlAsciidocConverter)
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        def now = new Date()
        
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/asciidoc-testcase1.adoc'
        def convertedDoc = '<h1>Introduction to AsciiDoc</h1>'
        def docTitle = 'Introduction to AsciiDoc'
        def asciidoc = '= Introduction to AsciiDoc'
        def filename = 'asciidoc-testcase1.adoc'
        def path = 'test cases/somewhere else/asciidoc-testcase2.adoc'
        def previousPath = 'test cases/asciidoc-testcase2.adoc'
        def sha = '2e6e10426375e746ad5dff7d94e765af66a1b8a5'
        def fileType = '.adoc'
        
        when:
        ArrayList<AsciidocEntity> insertedEntities = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            CommitFile[
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.RENAMED,
                previousPath: previousPath
            ]
        ]
    
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        1 * dao.update(_ as AsciidocEntity, previousPath)
        
        and:
        insertedEntities.size() == 1
    }
    
    void "Updating one commit file with status added succeeds"() {
        given:
        def dao = Mock(AsciidocDAO)
        def scanner = Mock(GithubScanner)
        def converter = Mock(HtmlAsciidocConverter)
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        def now = new Date()
        
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/asciidoc-testcase1.adoc'
        def convertedDoc = '<h1>Introduction to AsciiDoc</h1>'
        def docTitle = 'Introduction to AsciiDoc'
        def asciidoc = '= Introduction to AsciiDoc'
        def filename = 'asciidoc-testcase1.adoc'
        def path = 'test cases/somewhere else/asciidoc-testcase2.adoc'
        def previousPath = 'test cases/asciidoc-testcase2.adoc'
        def sha = '2e6e10426375e746ad5dff7d94e765af66a1b8a5'
        def fileType = '.adoc'
        
        when:
        ArrayList<AsciidocEntity> insertedEntities = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            CommitFile[
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.ADDED,
                previousPath: null
            ]
        ]
    
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        1 * dao.save(_ as ArrayList<AsciidocEntity>)
        
        and:
        insertedEntities.size() == 1
    }
    
    void "Updating three commit file with status modified, renamed and added succeeds"() {
        given:
        def dao = Mock(AsciidocDAO)
        def scanner = Mock(GithubScanner)
        def converter = Mock(HtmlAsciidocConverter)
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        def now = new Date()
        
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/asciidoc-testcase1.adoc'
        def convertedDoc = '<h1>Introduction to AsciiDoc</h1>'
        def docTitle = 'Introduction to AsciiDoc'
        def asciidoc = '= Introduction to AsciiDoc'
        def filename = 'asciidoc-testcase1.adoc'
        def path = 'test cases/somewhere else/asciidoc-testcase2.adoc'
        def previousPath = 'test cases/asciidoc-testcase2.adoc'
        def sha = '2e6e10426375e746ad5dff7d94e765af66a1b8a5'
        def fileType = '.adoc'
        
        when:
        ArrayList<AsciidocEntity> insertedEntities = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            CommitFile[
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.MODIFIED,
                previousPath: previousPath
            ],
            CommitFile[
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.RENAMED,
                previousPath: previousPath
            ],
            CommitFile[
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.ADDED,
                previousPath: previousPath
            ]
        ]
    
        3 * scanner.readResource(docUrl) >> asciidoc
        3 * converter.loadString(asciidoc)
        3 * converter.convert() >> convertedDoc
        3 * converter.getMainTitle() >> docTitle
        1 * dao.update(_ as AsciidocEntity, path)
        1 * dao.update(_ as AsciidocEntity, previousPath)
        1 * dao.save(_ as ArrayList<AsciidocEntity>)
        
        and:
        insertedEntities.size() == 3
    }
    
    private String getTestCase(String fileName) {
        InputStream is = getClass().getResourceAsStream("/"+fileName);

        String contents = "";
        try {
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            int result = bis.read();
            while (result != -1) {
                byte b = (byte) result;
                buf.write(b);
                result = bis.read();
            }

            return buf.toString();

        } catch (IOException ex) {
        }

        return null;
    }
}