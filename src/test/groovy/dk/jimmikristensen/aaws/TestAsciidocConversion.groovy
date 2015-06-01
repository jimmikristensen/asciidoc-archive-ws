

package dk.jimmikristensen.aaws

import static org.junit.Assert.*

import org.asciidoctor.ast.ContentPart

import spock.lang.Specification
import dk.jimmikristensen.aaws.domain.AsciidocHandler
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocBackend
import dk.jimmikristensen.aaws.domain.asciidoc.AsciidocConverter
import dk.jimmikristensen.aaws.domain.asciidoc.HtmlAsciidocConverter
import dk.jimmikristensen.aaws.doubles.FakeDataSourceFactory
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAO
import dk.jimmikristensen.aaws.persistence.dao.AsciidocDAOImpl
import dk.jimmikristensen.aaws.persistence.dao.entity.TranslationEntity
import dk.jimmikristensen.aaws.persistence.database.DataSourceFactory

class TestAsciidocConversion extends Specification {

    void "Asciidoc converted to html"() {
        given:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        
        when:
        converter.loadString(getTestCase("asciidoc-testcase1.adoc"));
        String html = converter.convert();
        
        then:
        html != null;
        html.startsWith("<div id=\"preamble\">") == true;
        html.contains("First Section") == true;
        html.contains("Introduction to AsciiDoc") == false;
        html.length() == 513;
    }
    
    void "it converts an asciidoc to HTML and returns it"() {
        given:
        DataSourceFactory dsFactory = new FakeDataSourceFactory()
        AsciidocDAO asdDAO = new AsciidocDAOImpl(dsFactory)
        AsciidocHandler handler = new AsciidocHandler(new HtmlAsciidocConverter(), asdDAO)
        def apiKeyId = 1
        
        when:
        def title = handler.storeAsciidoc(apiKeyId, getTestCase("asciidoc-testcase2.adoc"))
        
        then:
        title == 'Sample Document'
        
        when:
        def id = 1
        def type = AsciidocBackend.HTML5.toString()
        TranslationEntity entity = asdDAO.getTranslation(3, type)
        
        then:
        entity != null
        entity.getDoc() != ''
        entity.getDoc().startsWith('<div class="paragraph">')
        entity.getDoc().endsWith('</div>')
        
    }
    
    void "get meta data from document"() {
        given:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        
        when:
        converter.loadString(getTestCase("asciidoc-testcase2.adoc"));
        String html = converter.convert();
        
        then:
        html != null;
        "doc.writer@asciidoc.org" == converter.getAuthor().getEmail();
        "Doc Writer" == converter.getAuthor().getFullName();
        "First draft" == converter.getRevisionInfo().getRemark();
        "1.0" == converter.getRevisionInfo().getNumber();
        "2013-05-20" == converter.getRevisionInfo().getDate();
        "Sample Document" == converter.getDocHeader().getDocumentTitle().getMain();
        null == converter.getDocHeader().getDocumentTitle().getSubtitle();
        "Sample Document" == converter.getDocHeader().getDocumentTitle().getCombined();
    }
    
    void "convert badly formed document"() {
        given:
        AsciidocConverter converter = new HtmlAsciidocConverter();
        
        when:
        converter.loadString(getTestCase("asciidoc-testcase3.adoc"));
        String html = converter.convert();
        
        then:
        null != html;
        null != converter.getAuthor();
        null == converter.getAuthor().getEmail();
        null == converter.getAuthor().getFullName();
        null != converter.getRevisionInfo();
        null == converter.getRevisionInfo().getRemark();
        null == converter.getRevisionInfo().getNumber();
        null == converter.getRevisionInfo().getDate();
        null != converter.getDocHeader();
        null == converter.getDocHeader().getDocumentTitle();
    }
    
    void "sdfsf"() {
        given:
        AsciidocConverter converter = new HtmlAsciidocConverter()
        
        when:
        converter.loadString(getTestCase("asciidoc-testcase7.adoc"))
        def doc = converter.getDocument()
        def html = converter.convert()

        println html
        
        println '----------------'
        
        for (ContentPart part : doc.getParts()){
            println '########################'
            System.out.println(part.getTitle());
            System.out.println(part.getContent());

          }
        
//        String html = converter.convert()
        
        then:
        println ''
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
