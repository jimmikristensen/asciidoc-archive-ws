package dk.jimmikristensen.aaws

import spock.lang.Specification
import dk.jimmikristensen.aaws.domain.AsciidocImporter
import dk.jimmikristensen.aaws.domain.asciidoc.DocType
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter
import dk.jimmikristensen.aaws.domain.github.CommitStatus
import dk.jimmikristensen.aaws.domain.github.GithubScanner
import dk.jimmikristensen.aaws.domain.github.dto.CommitFile
import dk.jimmikristensen.aaws.domain.github.dto.RepoFile
import dk.jimmikristensen.aaws.doubles.FakeDataSourceFactory
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAOImpl
import dk.jimmikristensen.aaws.persistence.dao.entity.AsciidocEntity
import dk.jimmikristensen.aaws.persistence.dao.entity.CategoryEntity
import dk.jimmikristensen.aaws.persistence.dao.entity.ContentsEntity
import dk.jimmikristensen.aaws.persistence.dao.entity.ImportReportEntity
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory

class TestAsciidocImporter extends Specification {
    
    void "One new asciidoc file is successfully stored using mocks"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory();
        AsciidocDAO dao = new AsciidocDAOImpl(dsFactory);
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
        ImportReportEntity report = importer.initialImport(owner, repo)
        
        then:
        1 * scanner.scanRepository(owner, repo) >> [
                new RepoFile(
                    filename: filename,
                    path: path,
                    sha: sha,
                    url: docUrl,
                    type: fileType,
                    date: now
                )
            ]
        
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle

        and: 'check that all attributes has been set on the report'
        report.getInserted() == 1
        report.getReportDate() != null
        report.getResourcesDownloaded() == 0
        report.getUpdated() == 0
        
        and:
        List<AsciidocEntity> docList = dao.getDocumentsByTitle(docTitle, DocType.HTML)
        docList.size() == 1
        AsciidocEntity doc = docList.get(0)
        doc.getTitle() == docTitle
        doc.getDate() == now
        doc.getFilename() == filename
        doc.getPath() == path
        doc.getSha() == sha
        doc.getUrl() == docUrl
        doc.getContents().size() == 1
        doc.getCategories().size() == 1
                
        and: 'check that the html is part of contents'
        ContentsEntity htmlContents = doc.getContents().get(0)
        htmlContents.getDocument() == '<h1>Introduction to AsciiDoc</h1>'
        htmlContents.getType() == DocType.HTML
        
        and: 'check that categories has been set'
        CategoryEntity category = doc.getCategories().get(0)
        category.getName() == 'test cases'
    }
    
    void "Asciidoc without title will have the filename as title"() {
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
        def docTitle = null
        def asciidoc = '= Introduction to AsciiDoc'
        def filename = 'asciidoc-testcase1.adoc'
        def path = 'test cases/asciidoc-testcase1.adoc'
        def sha = '4e6e10426375e746ad5dff7d94e765af66a1b8a5'
        def fileType = '.adoc'
        
        when:
        ImportReportEntity report = importer.initialImport(owner, repo)
        
        then:
        1 * scanner.scanRepository(owner, repo) >> [
                new RepoFile(
                    filename: filename,
                    path: path,
                    sha: sha,
                    url: docUrl,
                    type: fileType,
                    date: now
                )
            ]
        
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        
        and:
        List<AsciidocEntity> docList = dao.getDocumentsByTitle(filename, DocType.HTML)
        docList.size() == 1
        AsciidocEntity doc = docList.get(0)
        doc.getTitle() == filename
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
        ImportReportEntity report = importer.initialImport(owner, repo)
        
        then:
        1 * scanner.scanRepository(owner, repo) >> [
            new RepoFile(
                filename: filename,
                path: 'test cases/asciidoc-testcase1.adoc',
                sha: '4e6e10426375e746ad5dff7d94e765af66a1b8a5',
                url: docUrl,
                type: '.adoc',
                date: now
            )
        ]
        1 * scanner.readResource(docUrl) >> getTestCase(filename)
        
        when:
        ArrayList<AsciidocEntity> docs = dao.getDocumentsByTitle(docTitle, DocType.HTML)
        
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
        contentEntity.getType() == DocType.HTML
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
        ImportReportEntity report = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            new CommitFile(
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.MODIFIED,
                previousPath: null
            )
        ]
    
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        1 * dao.update(_ as AsciidocEntity, path)
        
        and:
        report.getUpdated() == 1
        report.getInserted() == 0
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
        ImportReportEntity report = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            new CommitFile(
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.RENAMED,
                previousPath: previousPath
            )
        ]
    
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        1 * dao.update(_ as AsciidocEntity, previousPath)
        
        and:
        report.getUpdated() == 1
        report.getInserted() == 0
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
        ImportReportEntity report = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            new CommitFile(
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.ADDED,
                previousPath: null
            )
        ]
    
        1 * scanner.readResource(docUrl) >> asciidoc
        1 * converter.loadString(asciidoc)
        1 * converter.convert() >> convertedDoc
        1 * converter.getMainTitle() >> docTitle
        1 * dao.save(_ as ArrayList<AsciidocEntity>)
        
        and:
        report.getInserted() == 1
        report.getUpdated() == 0
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
        ImportReportEntity report = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            new CommitFile(
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.MODIFIED,
                previousPath: previousPath
            ),
            new CommitFile(
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.RENAMED,
                previousPath: previousPath
            ),
            new CommitFile(
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.ADDED,
                previousPath: previousPath
            )
        ]
    
        3 * scanner.readResource(docUrl) >> asciidoc
        3 * converter.loadString(asciidoc)
        3 * converter.convert() >> convertedDoc
        3 * converter.getMainTitle() >> docTitle
        1 * dao.update(_ as AsciidocEntity, path)
        1 * dao.update(_ as AsciidocEntity, previousPath)
        1 * dao.save(_ as ArrayList<AsciidocEntity>)
        
        and:
        report.getUpdated() == 2
        report.getInserted() == 1
    }
    
    void "png file is downloaded successfully when doing initial import"() {
        given:
        def dao = Mock(AsciidocDAO)
        def scanner = Mock(GithubScanner)
        def converter = Mock(HtmlAsciidocConverter)
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        def now = new Date()
        
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/test1.png'
        def filename = 'test1.png'
        def path = 'test1.png'
        def sha = '65ac7047eebf24051b6384e3279c0634d74d2aae'
        def fileType = '.png'
        
        when:
        ImportReportEntity report = importer.initialImport(owner, repo)
        
        then:
        1 * scanner.scanRepository(owner, repo) >> [
            new RepoFile(
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now
            )
        ]
        
        0 * scanner.readResource(_)
        0 * converter.loadString(_)
        0 * converter.convert()
        0 * converter.getMainTitle()
        1 * scanner.downloadResource(docUrl, '') >> filename
        0 * dao.save(_)
        
        and:
        report.getUpdated() == 0
        report.getResourcesDownloaded() == 1
        report.getInserted() == 0
    }
    
    void "png file is downloaded successfully when doing incremental import"() {
        given:
        def dao = Mock(AsciidocDAO)
        def scanner = Mock(GithubScanner)
        def converter = Mock(HtmlAsciidocConverter)
        def importer = new AsciidocImporter(scanner, dao, converter)
        
        def owner = 'johndoe'
        def repo = 'somerepo'
        def now = new Date()
        
        def docUrl = 'https://raw.githubusercontent.com/'+owner+'/'+repo+'/master/some dir/test1.png'
        def filename = 'test1.png'
        def path = 'some dir/test1.png'
        def sha = '65ac7047eebf24051b6384e3279c0634d74d2aae'
        def fileType = '.png'
        
        when:
        ImportReportEntity report = importer.incrementalImport(owner, repo, now)
        
        then:
        1 * scanner.scanCommits(owner, repo, now) >> [
            new CommitFile(
                filename: filename,
                path: path,
                sha: sha,
                url: docUrl,
                type: fileType,
                date: now,
                committer: 'johndoe',
                status: CommitStatus.ADDED,
                previousPath: null
            )
        ]
        
        0 * scanner.readResource(_)
        0 * converter.loadString(_)
        0 * converter.convert()
        0 * converter.getMainTitle()
        0 * dao.update(_, _)
        0 * dao.update(_, _)
        0 * dao.save(_)
        1 * scanner.downloadResource(docUrl, 'some dir/') >> filename
        
        and:
        report.getInserted() == 0
        report.getUpdated() == 0
        report.getResourcesDownloaded() == 1
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